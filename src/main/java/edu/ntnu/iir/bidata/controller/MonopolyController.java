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
import edu.ntnu.iir.bidata.model.gamestate.MonopolyGameState;
import java.util.List;
import java.util.logging.Logger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.File;

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

    public MonopolyController(BoardGame boardGame) {
        super(boardGame);
        this.boardGameWriter = new BoardGameFileWriterGson();
        this.boardGameReader = new BoardGameFileReaderGson();
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
        boolean rolledSix = false;
        for (int value : diceValues) {
            if (value == 6) {
                rolledSix = true;
                break;
            }
        }
        if (rolledSix) {
            currentPlayer.setInJail(false);
        }
        awaitingJailAction = false;
        nextPlayer();
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
        nextPlayer();
    }

    @Override
    public void handlePlayerMove() {
        SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) boardGame.getCurrentPlayer();
        if (currentPlayer.isInJail()) {
            awaitingJailAction = true;
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
                nextPlayer();
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

    public void saveGame(String gameName) {
        if (!gameStarted) {
            LOGGER.warning("Cannot save game: Game has not started");
            return;
        }
        try {
            // Ensure the saved_games/monopoly directory exists
            File savedGamesDir = new File("src/main/resources/saved_games/monopoly");
            if (!savedGamesDir.exists()) {
                savedGamesDir.mkdirs();
            }
            
            MonopolyGameState gameState = MonopolyGameState.fromBoardGame(boardGame);
            Path savePath = Paths.get("src/main/resources/saved_games/monopoly", gameName + ".json");
            // Serialize MonopolyGameState directly
            if (boardGameWriter instanceof edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriterGson) {
                ((edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriterGson) boardGameWriter).writeMonopolyGameState(gameState, savePath);
            } else {
                throw new IOException("BoardGameWriter does not support MonopolyGameState");
            }
            LOGGER.info("Game saved to: " + savePath);
        } catch (IOException e) {
            LOGGER.severe("Failed to save game: " + e.getMessage());
        }
    }

    public void loadGame(String gameName, MonopolyGameUI ui) {
        try {
            Path savePath = Paths.get("src/main/resources/saved_games/monopoly", gameName + ".json");
            MonopolyGameState gameState;
            if (boardGameReader instanceof edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson) {
                gameState = ((edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson) boardGameReader).readMonopolyGameState(savePath);
                // Verify this is a Monopoly game
                if (!"MONOPOLY".equals(gameState.getGameType())) {
                    LOGGER.severe("Cannot load game: Not a Monopoly save file");
                    return;
                }
            } else {
                throw new IOException("BoardGameReader does not support MonopolyGameState");
            }
            this.boardGame = gameState.toBoardGame();
            this.gameStarted = true;
            if (ui != null) {
                ui.refreshUIFromBoardGame();
                ui.updateUI();  // Add explicit UI update
            }
            LOGGER.info("Game loaded: " + gameName);
        } catch (IOException e) {
            LOGGER.severe("Failed to load game: " + e.getMessage());
        }
    }
} 
