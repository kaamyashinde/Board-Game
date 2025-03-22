package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.tile.Tile;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A class for managing the board game.
 *
 * @author kaamyashinde
 * @version 1.0.0
 */
@Getter
@Setter
public class BoardGame {
    private static final int MIN_PLAYERS = 2;
    private static final int MIN_BOARD_SIZE = 10;
    private static final int MIN_DICE = 1;

    private final Board board;
    private final Dice dice;
    private final Map<Player, Integer> players;
    private Player currentPlayer;
    private boolean playing;

    /**
     * Constructor that initializes the board game with the specified parameters.
     *
     * @param numOfDices   number of dice to use in the game
     * @param numOfPlayers number of players in the game
     * @param sizeOfBoard  size of the game board
     * @throws IllegalArgumentException if parameters are invalid
     */
    public BoardGame(int numOfDices, int numOfPlayers, int sizeOfBoard) {
        validateGameParameters(numOfDices, numOfPlayers, sizeOfBoard);
        
        this.dice = new Dice(numOfDices);
        this.players = new HashMap<>(numOfPlayers);
        this.board = new Board(sizeOfBoard);
        this.playing = false;
    }

    /**
     * Validates the game parameters.
     *
     * @param numOfDices   number of dice
     * @param numOfPlayers number of players
     * @param sizeOfBoard  size of the board
     * @throws IllegalArgumentException if any parameter is invalid
     */
    private void validateGameParameters(int numOfDices, int numOfPlayers, int sizeOfBoard) {
        if (numOfDices < MIN_DICE) {
            throw new IllegalArgumentException("Number of dice must be at least " + MIN_DICE);
        }
        if (numOfPlayers < MIN_PLAYERS) {
            throw new IllegalArgumentException("Number of players must be at least " + MIN_PLAYERS);
        }
        if (sizeOfBoard < MIN_BOARD_SIZE) {
            throw new IllegalArgumentException("Board size must be at least " + MIN_BOARD_SIZE);
        }
    }

    /**
     * Adds a player to the game.
     *
     * @param player the player to add
     * @throws IllegalStateException if the game has already started
     * @throws IllegalArgumentException if player is null
     */
    public void addPlayer(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        if (playing) {
            throw new IllegalStateException("Cannot add players after game has started");
        }
        
        player.setCurrentTile(board.getTiles().get(0));
        this.players.put(player, 0);
    }

    /**
     * Initializes the game and sets the first player.
     *
     * @throws IllegalStateException if no players are added or game is already playing
     */
    public void initialiseGame() {
        if (players.isEmpty()) {
            throw new IllegalStateException("No players have been added to the game");
        }
        if (playing) {
            throw new IllegalStateException("Game is already in progress");
        }
        
        currentPlayer = players.keySet().iterator().next();
        playing = true;
        System.out.println("First player is: " + currentPlayer.getName());
    }

    /**
     * Executes the current player's turn.
     */
    public void playCurrentPlayer() {
        if (!playing || currentPlayer == null) {
            throw new IllegalStateException("Game is not in progress or no current player");
        }

        System.out.println("-----------");
        System.out.println("Current player " + currentPlayer.getName());
        System.out.println("Current position: " + currentPlayer.getCurrentTile().getId());

        dice.rollAllDice();
        int steps = dice.sumOfRolledValues();
        System.out.println("Rolled: " + steps);

        movePlayer(steps);
    }

    /**
     * Moves the current player the specified number of steps.
     *
     * @param steps number of steps to move
     */
    private void movePlayer(int steps) {
        Tile currentTile = currentPlayer.getCurrentTile();
        for (int i = 0; i < steps; i++) {
            if (currentTile.getNextTile() == null) {
                handleGameWin();
                return;
            }
            currentTile = currentTile.getNextTile();
        }
        
        currentPlayer.setCurrentTile(currentTile);
        players.put(currentPlayer, currentTile.getId());
        System.out.println("New position: " + currentTile.getId());
    }

    /**
     * Handles the win condition when a player reaches the final tile.
     */
    private void handleGameWin() {
        System.out.println("Player " + currentPlayer.getName() + " has reached the final tile and wins the game!");
        playing = false;
    }

    /**
     * Starts and runs the game until a winner is determined.
     */
    public void playGame() {
        if (!playing) {
            throw new IllegalStateException("Game has not been initialized");
        }

        while (playing) {
            for (Map.Entry<Player, Integer> playerEntry : players.entrySet()) {
                currentPlayer = playerEntry.getKey();
                playCurrentPlayer();
                if (!playing) {
                    break;
                }
            }
        }
    }
}
