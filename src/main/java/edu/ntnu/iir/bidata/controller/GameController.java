package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.NewBoardGame;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.TileAction;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

/**
 * Controls the flow of the game and coordinates between the model and view.
 */
public class GameController {
    private final NewBoardGame boardGame;
    private boolean gameStarted = false;

    // For Snakes and Ladders specific logic
    private Map<String, Integer> playerPositions = new HashMap<>();
    @Setter
    @Getter
    private int currentPlayerIndex = 0;
    private List<String> playerNames;

    // Snakes and Ladders specific data
    private final int[][] snakes = {
        {99, 41}, {95, 75}, {89, 86}, {78, 15}, {38, 2}, {29, 11}
    };

    private final int[][] ladders = {
        {3, 36}, {8, 12}, {14, 26}, {31, 73}, {59, 80}, {83, 97}, {90, 92}
    };

    // For Ludo specific logic
    private int diceValue = 1;
    @Setter
    @Getter
    private boolean diceRolled = false;
    @Getter
    @Setter
    private boolean movingPiece = false;

    public GameController(NewBoardGame boardGame) {
        this.boardGame = boardGame;
    }

    /**
     * Sets the player names for games that manage their own player list
     */
    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
        // Initialize positions for all players
        for (String playerName : playerNames) {
            playerPositions.put(playerName, 0);
        }
    }

    public void startGame() {
        if (!gameStarted) {
            // Start the first turn
            startNextTurn();
            gameStarted = true;
        }
    }

    private void startNextTurn() {
        if (!boardGame.isGameOver()) {
            Player currentPlayer = boardGame.getCurrentPlayer();
            // Handle turn logic here
        } else if (boardGame instanceof NewBoardGame) {
            // For Snakes and Ladders specific turn management
            String currentPlayer = playerNames.get(currentPlayerIndex);
            // Handle turn logic here
        }
    }

    /**
     * Handles the dice roll and player movement for standard board games
     */
    public void handleNextTurn() {
        if (!boardGame.isGameOver()) {
            Player currentPlayer = boardGame.getCurrentPlayer();

            // Roll dice
            int diceRoll = rollDiceForSnakesAndLadders();

            // Move player
            boolean hasWon = updateSnakesAndLaddersPosition(currentPlayer.getName(), diceRoll);

            // Check if game is over
            if (hasWon) {
                // Handle win condition
            } else {
                // Move to next player
                nextSnakesAndLaddersPlayer();
                startNextTurn();
            }
        }
    }

    /**
     * Rolls dice for Snakes and Ladders game
     * @return the dice roll value
     */
    public int rollDiceForSnakesAndLadders() {
        return 1 + (int)(Math.random() * 6);
    }

    /**
     * Updates player position for Snakes and Ladders game
     * @param playerName the player's name
     * @param diceRoll the dice roll value
     * @return true if the player won, false otherwise
     */
    public boolean updateSnakesAndLaddersPosition(String playerName, int diceRoll) {
        // Get current position
        int currentPosition = playerPositions.get(playerName);
        int newPosition = currentPosition + diceRoll;

        // Ensure we don't go past 100
        if (newPosition > 100) {
            // Bounce back from 100
            newPosition = 100 - (newPosition - 100);
        }

        // Update position
        playerPositions.put(playerName, newPosition);

        // Check for snakes and ladders
        int finalPosition = checkSnakesAndLadders(playerName, newPosition);
        if (finalPosition != newPosition) {
            playerPositions.put(playerName, finalPosition);
        }

        // Check for win condition
        return playerPositions.get(playerName) == 100;
    }

    /**
     * Checks if a position has a snake or ladder
     * @param playerName the player's name
     * @param position the current position
     * @return the new position after snake or ladder
     */
    private int checkSnakesAndLadders(String playerName, int position) {
        int newPosition = position;

        // Check for snakes
        for (int[] snake : snakes) {
            if (snake[0] == position) {
                newPosition = snake[1];
                break;
            }
        }

        // Check for ladders
        for (int[] ladder : ladders) {
            if (ladder[0] == position) {
                newPosition = ladder[1];
                break;
            }
        }

        return newPosition;
    }

    /**
     * Moves to the next player in Snakes and Ladders
     */
    public void nextSnakesAndLaddersPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playerNames.size();
    }

    /**
     * Gets the current player's name for Snakes and Ladders
     * @return the current player's name
     */
    public String getCurrentSnakesAndLaddersPlayerName() {
        return playerNames.get(currentPlayerIndex);
    }

    /**
     * Gets the position for a player in Snakes and Ladders
     * @param playerName the player's name
     * @return the player's position
     */
    public int getPlayerPosition(String playerName) {
        return playerPositions.get(playerName);
    }

    /* Ludo Game Specific Methods */

    /**
     * Rolls dice for Ludo game
     * @return the dice roll value
     */
    public int rollDiceForLudo() {
        diceRolled = true;
        return 1 + (int)(Math.random() * 6);
    }

    /**
     * Checks if a token can be moved in Ludo
     * @param currentPosition the token's current position
     * @param diceValue the dice roll value
     * @return true if the token can be moved, false otherwise
     */
    public boolean canMoveLudoToken(int currentPosition, int diceValue) {
        // If in home and rolled a 6, can move out
        if (currentPosition == -1 && diceValue == 6) {
            return true;
        }

        // If already on the board, can move
        return currentPosition >= 0;
    }
}