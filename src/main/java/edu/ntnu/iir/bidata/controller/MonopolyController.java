package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.exception.LowMoneyException;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriter;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReader;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriterGson;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson;
import edu.ntnu.iir.bidata.view.monopoly.MonopolyGameUI;
import java.util.List;
import java.util.logging.Logger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.File;
import edu.ntnu.iir.bidata.model.utils.GameMediator;
import edu.ntnu.iir.bidata.Inject;

/**
 * Controller class specifically for Monopoly game logic.
 */
public class MonopolyController extends BaseGameController {
    private static final Logger LOGGER = Logger.getLogger(MonopolyController.class.getName());
    private final BoardGameFileWriter boardGameWriter;
    private final BoardGameFileReader boardGameReader;
    private boolean gameStarted = false;
    private boolean awaitingPlayerAction = false;
    private PropertyTile pendingPropertyTile = null;
    private boolean awaitingRentAction = false;
    private PropertyTile pendingRentPropertyTile = null;
    private boolean awaitingJailAction = false;
    private boolean jailRolledSix = false;
    private boolean diceRolled = false;
    private final GameMediator mediator;

    @Inject
    public MonopolyController(BoardGame boardGame, BoardGameFileWriter boardGameWriter, BoardGameFileReader boardGameReader, GameMediator mediator) {
        super(boardGame);
        this.boardGameWriter = boardGameWriter;
        this.boardGameReader = boardGameReader;
        this.mediator = mediator;
        LOGGER.info("MonopolyController initialized");
    }

    @Override
    public void setPlayerNames(List<String> playerNames) {
        super.setPlayerNames(playerNames);
        // Do not add players here; they are already added in the launcher
        LOGGER.info("Setting player names: " + playerNames);
    }

    public void buyProperty(SimpleMonopolyPlayer player, PropertyTile property) {
        try {
            player.buyProperty(property);
            LOGGER.info(player.getName() + " bought property at position " + property.getId());
        } catch (LowMoneyException e) {
            LOGGER.warning(player.getName() + " cannot afford property at position " + property.getId());
            // Handle insufficient funds
        }
    }

    public void buildHouse(SimpleMonopolyPlayer player, PropertyTile property) {
        // Implement house building logic
        LOGGER.info(player.getName() + " built a house on property at position " + property.getId());
    }

    public void mortgageProperty(SimpleMonopolyPlayer player, PropertyTile property) {
        // Implement property mortgaging logic
        LOGGER.info(player.getName() + " mortgaged property at position " + property.getId());
    }

    public void payRent(SimpleMonopolyPlayer player, PropertyTile property) {
        try {
            player.payRent(property.getRent());
            LOGGER.info(player.getName() + " paid rent for property at position " + property.getId());
        } catch (LowMoneyException e) {
            LOGGER.warning(player.getName() + " cannot afford rent for property at position " + property.getId());
            // Handle insufficient funds
        }
    }

    public void collectRent(SimpleMonopolyPlayer player, int amount) {
        player.collectMoney(amount);
        LOGGER.info(player.getName() + " collected $" + amount + " in rent");
    }

    public boolean isCurrentPlayerInJail() {
        return boardGame.getCurrentPlayer() instanceof SimpleMonopolyPlayer &&
               ((SimpleMonopolyPlayer) boardGame.getCurrentPlayer()).isInJail();
    }

    public boolean isCurrentPlayerCanLeaveJail() {
        return ((SimpleMonopolyPlayer) boardGame.getCurrentPlayer()).isCanLeaveJailNextTurn();
    }

    public void handleJailRollDice() {
        SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) boardGame.getCurrentPlayer();
        boardGame.getDice().rollAllDice();
        int[] diceValues = boardGame.getCurrentDiceValues();
        boolean rolledSix = java.util.Arrays.stream(diceValues).anyMatch(value -> value == 6);
        if (rolledSix) {
            currentPlayer.setInJail(false);
        }
        awaitingJailAction = false;
        mediator.notify(this, "nextPlayer");
    }

    public void handleJailPay() {
        SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) boardGame.getCurrentPlayer();
        try {
            currentPlayer.payRent(50);
            currentPlayer.setInJail(false);
        } catch (Exception e) {
            // Not enough money, do nothing
        }
        awaitingJailAction = false;
        mediator.notify(this, "nextPlayer");
    }

    @Override
    public void handlePlayerMove() {
        SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) boardGame.getCurrentPlayer();
        if (currentPlayer.isInJail()) {
            awaitingJailAction = true;
            boardGame.notifyObservers();
            return;
        }
        if (!gameStarted) {
            gameStarted = true;
            LOGGER.info("First turn started");
        }
        if (awaitingPlayerAction || awaitingRentAction || awaitingJailAction) {
            LOGGER.warning("Still awaiting player action. Turn cannot proceed.");
            return;
        }
        // Roll the dice before moving
        boardGame.getDice().rollAllDice();
        int[] diceValues = boardGame.getCurrentDiceValues();
        int steps = java.util.Arrays.stream(diceValues).sum();
        // Move the player
        currentPlayer.move(steps);
        LOGGER.info(currentPlayer.getName() + " moved " + steps + " steps");
        // Handle the tile the player landed on
        Tile currentTile = currentPlayer.getCurrentTile();
        if (currentTile instanceof PropertyTile) {
            PropertyTile propertyTile = (PropertyTile) currentTile;
            if (propertyTile.getOwner() == null) {
                // Property is available for purchase
                LOGGER.info("Property at position " + propertyTile.getId() + " is available for purchase");
                awaitingPlayerAction = true;
                pendingPropertyTile = propertyTile;
                boardGame.notifyObservers();
                return;
            } else if (propertyTile.getOwner() != currentPlayer) {
                // Player needs to pay rent
                LOGGER.info(currentPlayer.getName() + " must pay rent for property at position " + propertyTile.getId());
                awaitingRentAction = true;
                pendingRentPropertyTile = propertyTile;
                boardGame.notifyObservers();
                return;
            }
        } else if (currentTile.getAction() != null) {
            currentTile.getAction().executeAction(currentPlayer, currentTile);
            // If the player is now in jail, end their turn immediately
            if (currentPlayer.isInJail()) {
                mediator.notify(this, "nextPlayer");
                return;
            }
        }
        // No action needed, move to next player
        mediator.notify(this, "nextPlayer");
    }

    public boolean isAwaitingPlayerAction() {
        return awaitingPlayerAction;
    }

    public PropertyTile getPendingPropertyTile() {
        return pendingPropertyTile;
    }

    public void buyPropertyForCurrentPlayer() {
        if (!awaitingPlayerAction || pendingPropertyTile == null) return;
        SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) boardGame.getCurrentPlayer();
        buyProperty(currentPlayer, pendingPropertyTile);
        awaitingPlayerAction = false;
        pendingPropertyTile = null;
        nextPlayer();
        mediator.notify(this, "nextPlayer");
    }

    public void skipActionForCurrentPlayer() {
        if (!awaitingPlayerAction) return;
        awaitingPlayerAction = false;
        pendingPropertyTile = null;
        nextPlayer();
        mediator.notify(this, "nextPlayer");
    }

    public boolean isAwaitingRentAction() {
        return awaitingRentAction;
    }

    public PropertyTile getPendingRentPropertyTile() {
        return pendingRentPropertyTile;
    }

    public void payRentForCurrentPlayer() {
        if (!awaitingRentAction || pendingRentPropertyTile == null) return;
        SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) boardGame.getCurrentPlayer();
        payRent(currentPlayer, pendingRentPropertyTile);
        awaitingRentAction = false;
        pendingRentPropertyTile = null;
        nextPlayer();
        mediator.notify(this, "nextPlayer");
    }

    public int[] getLastDiceRolls() {
        return boardGame.getCurrentDiceValues();
    }

    public int getLastDiceSum() {
        int[] values = boardGame.getCurrentDiceValues();
        int sum = 0;
        if (values != null) {
            sum = java.util.Arrays.stream(values).sum();
        }
        return sum;
    }

    public void rollDice() {
        boardGame.getDice().rollAllDice();
        diceRolled = true;
        LOGGER.info("Dice rolled: " + java.util.Arrays.toString(boardGame.getCurrentDiceValues()));
    }
} 
