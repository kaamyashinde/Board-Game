package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.Observable;
import edu.ntnu.iir.bidata.model.game.GameState;
import edu.ntnu.iir.bidata.filehandling.game.GameStateFileWriter;
import edu.ntnu.iir.bidata.filehandling.game.GameStateFileReader;
import edu.ntnu.iir.bidata.filehandling.game.GameStateFileWriterGson;
import edu.ntnu.iir.bidata.filehandling.game.GameStateFileReaderGson;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import lombok.Getter;

/**
 * Controller class specifically for Snakes and Ladders game logic.
 */
public class SnakesAndLaddersController extends BaseGameController {
    private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersController.class.getName());
    
    // Snakes and Ladders specific data
    private final int[][] snakes = {
        {99, 41}, {95, 75}, {89, 86}, {78, 15}, {38, 2}, {29, 11}
    };
    private final int[][] ladders = {
        {3, 36}, {8, 12}, {14, 26}, {31, 73}, {59, 80}, {83, 97}, {90, 92}
    };
    
    @Getter
    private final Map<String, Integer> playerPositions = new HashMap<>();
    private final GameStateFileWriter gameStateWriter;
    private final GameStateFileReader gameStateReader;
    private boolean gameStarted = false;

    public SnakesAndLaddersController(BoardGame boardGame) {
        super(boardGame);
        this.gameStateWriter = new GameStateFileWriterGson();
        this.gameStateReader = new GameStateFileReaderGson();
        LOGGER.info("SnakesAndLaddersController initialized");
    }

    @Override
    public void setPlayerNames(List<String> playerNames) {
        super.setPlayerNames(playerNames);
        // Initialize positions for all players
        for (String playerName : playerNames) {
            playerPositions.put(playerName, 0);
        }
    }

    @Override
    public void handlePlayerMove() {
        if (!diceRolled) {
            LOGGER.warning("Dice must be rolled before moving");
            return;
        }

        String currentPlayer = getCurrentPlayerName();
        int currentPosition = playerPositions.get(currentPlayer);
        int[] diceValues = boardGame.getCurrentDiceValues();
        int steps = 0;
        for (int value : diceValues) {
            steps += value;
        }

        // Calculate new position
        int newPosition = currentPosition + steps;
        
        // Check if player won
        if (newPosition >= 100) {
            newPosition = 100;
            LOGGER.info(currentPlayer + " has won the game!");
        } else {
            // Check for snakes
            for (int[] snake : snakes) {
                if (newPosition == snake[0]) {
                    newPosition = snake[1];
                    LOGGER.info(currentPlayer + " landed on a snake! Moving down to " + newPosition);
                    break;
                }
            }
            
            // Check for ladders
            for (int[] ladder : ladders) {
                if (newPosition == ladder[0]) {
                    newPosition = ladder[1];
                    LOGGER.info(currentPlayer + " climbed a ladder! Moving up to " + newPosition);
                    break;
                }
            }
        }

        // Update player position
        playerPositions.put(currentPlayer, newPosition);
        nextPlayer();
    }

    /**
     * Gets the current player's position
     */
    public int getPlayerPosition(String playerName) {
        return playerPositions.getOrDefault(playerName, 0);
    }

    /**
     * Gets the current player's name for Snakes and Ladders
     */
    public String getCurrentSnakesAndLaddersPlayerName() {
        return getCurrentPlayerName();
    }

    /**
     * Rolls the dice for the current player
     */
    public void rollDiceForSnakesAndLadders() {
        boardGame.getDice().rollAllDice();
        diceRolled = true;
        LOGGER.info("Dice rolled: " + boardGame.getCurrentDiceValues()[0]);
    }

    /**
     * Updates a player's position
     */
    public void updateSnakesAndLaddersPosition(String playerName, int position) {
        playerPositions.put(playerName, position);
        LOGGER.info(playerName + " moved to position " + position);
    }

    /**
     * Moves to the next player
     */
    public void nextSnakesAndLaddersPlayer() {
        nextPlayer();
        LOGGER.info("Next player: " + getCurrentPlayerName());
    }

    /**
     * Gets the last dice roll value
     */
    public int getLastDiceRoll() {
        int[] values = boardGame.getCurrentDiceValues();
        return (values != null && values.length > 0) ? values[0] : 0;
    }

    public static class MoveResult {
        public final int start;
        public final int end;
        public final String type; // "normal", "snake", "ladder"
        public MoveResult(int start, int end, String type) {
            this.start = start;
            this.end = end;
            this.type = type;
        }
    }

    /**
     * Moves the player and returns the result (final position and move type)
     */
    public MoveResult movePlayer(String playerName, int roll) {
        int start = playerPositions.getOrDefault(playerName, 0);
        int end = start + roll;
        String type = "normal";
        if (end >= 100) end = 100;
        // Check for snakes
        for (int[] snake : snakes) {
            if (end == snake[0]) {
                end = snake[1];
                type = "snake";
                break;
            }
        }
        // Check for ladders
        if (type.equals("normal")) {
            for (int[] ladder : ladders) {
                if (end == ladder[0]) {
                    end = ladder[1];
                    type = "ladder";
                    break;
                }
            }
        }
        playerPositions.put(playerName, end);
        return new MoveResult(start, end, type);
    }

    /**
     * Saves the current game state
     * @param gameName The name to save the game as
     */
    public void saveGame(String gameName) {
        if (!gameStarted) {
            LOGGER.warning("Cannot save game: Game has not started");
            return;
        }
        
        // Create a GameState object with current game state
        GameState gameState = new GameState(
            boardGame.getBoard(),
            boardGame.getPlayers(),
            currentPlayerIndex,
            gameName
        );
        
        // Save the game state
        Path savePath = Paths.get("src/main/resources/saved_games", gameName + ".json");
        gameStateWriter.writeGameState(gameState, savePath);
        LOGGER.info("Game saved: " + gameName);
    }

    /**
     * Loads a saved game state
     * @param gameName The name of the saved game to load
     */
    public void loadGame(String gameName) {
        Path savePath = Paths.get("src/main/resources/saved_games", gameName + ".json");
        GameState gameState = gameStateReader.readGameState(savePath);
        
        // Create a new BoardGame instance with the loaded board
        BoardGame newBoardGame = new BoardGame(gameState.getBoard(), boardGame.getDice().getLastRolledValues().length);
        
        // Add all players from the saved state
        for (Player player : gameState.getPlayers()) {
            newBoardGame.addPlayer(player.getName());
        }
        
        // Set the current player index
        currentPlayerIndex = gameState.getCurrentPlayerIndex();
        gameStarted = true;
        
        // Initialize the new game
        newBoardGame.startGame();
        
        // Update the game state in the UI
        if (boardGame instanceof Observable) {
            ((Observable) boardGame).notifyObservers();
        }
        
        LOGGER.info("Game loaded: " + gameName);
    }

    @Override
    public void startGame() {
        super.startGame();
        gameStarted = true;
        // Initialize player positions
        for (String playerName : playerNames) {
            playerPositions.put(playerName, 0);
        }
        LOGGER.info("Snakes and Ladders game started with players: " + playerNames);
    }
} 