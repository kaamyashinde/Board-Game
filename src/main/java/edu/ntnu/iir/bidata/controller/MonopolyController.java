package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.exception.LowMoneyException;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import edu.ntnu.iir.bidata.filehandling.game.GameStateFileWriter;
import edu.ntnu.iir.bidata.filehandling.game.GameStateFileReader;
import edu.ntnu.iir.bidata.filehandling.game.GameStateFileWriterGson;
import edu.ntnu.iir.bidata.filehandling.game.GameStateFileReaderGson;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller class specifically for Monopoly game logic.
 */
public class MonopolyController extends BaseGameController {
    private static final Logger LOGGER = Logger.getLogger(MonopolyController.class.getName());
    private final GameStateFileWriter gameStateWriter;
    private final GameStateFileReader gameStateReader;
    private boolean gameStarted = false;
    private boolean awaitingPlayerAction = false;
    private PropertyTile pendingPropertyTile = null;
    private boolean awaitingRentAction = false;
    private PropertyTile pendingRentPropertyTile = null;

    public MonopolyController(BoardGame boardGame) {
        super(boardGame);
        this.gameStateWriter = new GameStateFileWriterGson();
        this.gameStateReader = new GameStateFileReaderGson();
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

    @Override
    public void handlePlayerMove() {
        if (!gameStarted) {
            gameStarted = true;
            LOGGER.info("First turn started");
        }
        if (awaitingPlayerAction || awaitingRentAction) {
            LOGGER.warning("Still awaiting player action. Turn cannot proceed.");
            return;
        }
        // Roll the dice before moving
        boardGame.getDice().rollAllDice();
        SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) boardGame.getCurrentPlayer();
        int[] diceValues = boardGame.getCurrentDiceValues();
        int steps = 0;
        for (int value : diceValues) {
            steps += value;
        }
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
                return;
            } else if (propertyTile.getOwner() != currentPlayer) {
                // Player needs to pay rent
                LOGGER.info(currentPlayer.getName() + " must pay rent for property at position " + propertyTile.getId());
                awaitingRentAction = true;
                pendingRentPropertyTile = propertyTile;
                return;
            }
        }
        // No action needed, move to next player
        nextPlayer();
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
    }

    public void skipActionForCurrentPlayer() {
        if (!awaitingPlayerAction) return;
        awaitingPlayerAction = false;
        pendingPropertyTile = null;
        nextPlayer();
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
    }
} 
