package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.Tile;
import edu.ntnu.iir.bidata.model.tile.HopFiveStepsAction;
import edu.ntnu.iir.bidata.model.tile.GoToTileAction;
import edu.ntnu.iir.bidata.model.tile.LoseTurnAction;
import edu.ntnu.iir.bidata.model.tile.SwitchPositionAction;
import edu.ntnu.iir.bidata.model.tile.TileAction;
import edu.ntnu.iir.bidata.utils.ParameterValidation;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * A facade class that handles the main game logic and coordinates between different components.
 * This class manages the game state, players, dice, and board interactions.
 *
 * @author kaamyashinde
 * @version 1.0.0
 */
@Getter
public class NewBoardGame {
    private final Board board;
    private final List<Player> players;
    private final Dice dice;
    private int currentPlayerIndex;
    private boolean gameOver;
    private boolean gameInitialized;

    /**
     * Constructor for the NewBoardGame class.
     *
     * @param boardSize The size of the game board
     * @param numberOfDice The number of dice to use in the game
     * @throws IllegalArgumentException if boardSize or numberOfDice is invalid
     */
    public NewBoardGame(int boardSize, int numberOfDice) {
        ParameterValidation.validateNonZeroPositiveInteger(boardSize, "board size");
        ParameterValidation.validateNonZeroPositiveInteger(numberOfDice, "number of dice");
        
        this.board = new Board(boardSize);
        this.players = new ArrayList<>();
        this.dice = new Dice(numberOfDice);
        this.currentPlayerIndex = 0;
        this.gameOver = false;
        this.gameInitialized = false;
        
        // Initialize the board with connected tiles
        initializeBoard();
    }

    /**
     * Initializes the board by creating and connecting all tiles.
     */
    private void initializeBoard() {
        // Create all tiles first
        for (int i = 0; i < board.getSizeOfBoard(); i++) {
            TileAction action = null;
            // Add different tile actions at specific positions
            if (i == 5) {
                action = new HopFiveStepsAction();
            } else if (i == 10) {
                action = new GoToTileAction(15); // Go to tile 15
            } else if (i == 15) {
                action = new LoseTurnAction();
            } else if (i == 20) {
                action = new SwitchPositionAction(players);
            }
            if (!board.addTile(i, action)) {
                throw new GameException("Failed to add tile at position " + i);
            }
        }
        
        // Connect all tiles
        for (int i = 0; i < board.getSizeOfBoard() - 1; i++) {
            Tile currentTile = board.getTile(i);
            Tile nextTile = board.getTile(i + 1);
            if (currentTile == null || nextTile == null) {
                throw new GameException("Failed to get tiles for connection at position " + i);
            }
            board.connectTiles(i, nextTile);
        }
    }

    /**
     * Adds a player to the game.
     *
     * @param playerName The name of the player to add
     * @return true if the player was added successfully, false otherwise
     */
    public boolean addPlayer(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return false;
        }
        Player player = new Player(playerName);
        return players.add(player);
    }

    /**
     * Starts the game by ensuring all players are at the starting position.
     * @throws GameException if the game cannot be started
     */
    public void startGame() {
        if (players.isEmpty()) {
            throw new GameException("Cannot start game with no players");
        }
        
        // Ensure board is properly initialized
        Tile startingTile = board.getStartingTile();
        if (startingTile == null) {
            throw new GameException("Board is not properly initialized - starting tile is null");
        }
        
        // Set all players to starting position
        for (Player player : players) {
            player.setCurrentTile(startingTile);
        }
        
        currentPlayerIndex = 0;
        gameOver = false;
        gameInitialized = true;
    }

    /**
     * Makes a move for the current player.
     * This includes rolling the dice and moving the player accordingly.
     *
     * @return true if the game continues, false if the game is over
     * @throws GameException if the game is not properly initialized
     */
    public boolean makeMove() {
        if (!gameInitialized) {
            throw new GameException("Game has not been started. Call startGame() first.");
        }
        
        if (gameOver) {
            return false;
        }

        Player currentPlayer = players.get(currentPlayerIndex);
        if (currentPlayer.getCurrentTile() == null) {
            throw new GameException("Current player's position is not set");
        }

        System.out.println("\n" + currentPlayer.getName() + "'s turn");
        System.out.println("Current position: " + currentPlayer.getCurrentPosition());
        
        dice.rollAllDice();
        int steps = dice.sumOfRolledValues();
        System.out.println("Rolled: " + steps + " steps");
        
        try {
            currentPlayer.move(steps);
            Tile landedTile = currentPlayer.getCurrentTile();
            System.out.println("Landed on tile: " + landedTile.getId());
            
            if (landedTile != null && landedTile.getAction() != null) {
                System.out.println("Tile Action: " + landedTile.getAction().getDescription());
                landedTile.getAction().executeAction(currentPlayer, landedTile);
                System.out.println("New position after action: " + currentPlayer.getCurrentPosition());
            }
            
            // Check if the player has won
            if (currentPlayer.isOnLastTile()) {
                gameOver = true;
                return false;
            }
            
            // Move to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            return true;
        } catch (GameException e) {
            // If player can't move (e.g., reached end of board), game is over
            gameOver = true;
            return false;
        }
    }

    /**
     * Gets the current player.
     *
     * @return The current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Gets the winner of the game.
     *
     * @return The winning player, or null if the game is not over
     */
    public Player getWinner() {
        if (!gameOver) {
            return null;
        }
        return players.get(currentPlayerIndex);
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Gets the current dice values.
     *
     * @return An array of the current dice values
     */
    public int[] getCurrentDiceValues() {
        return dice.getLastRolledValues();
    }
}