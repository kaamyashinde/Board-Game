package edu.ntnu.iir.bidata.model.utils;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.Tile;

import java.util.Objects;

/**
 * Utility class for parameter validation.
 * Contains both generic validation methods and game-specific validation logic.
 *
 * @author kaamyashinde
 * @version 1.0.0
 */
public class ParameterValidation {
    // Game-specific constants
    private static final int MIN_PLAYERS = 2;
    private static final int MIN_BOARD_SIZE = 10;
    private static final int MIN_DICE = 1;

    // Generic validation methods
    /**
     * Ensuring that the input is a positive integer.
     *
     * @param numberToValidate The value that is to be validated.
     * @param object The name of the object being validated (for error message).
     * @throws IllegalArgumentException Exception thrown if the integer is negative.
     */
    public static void validateZeroPositiveInteger(int numberToValidate, String object) throws IllegalArgumentException {
        if (numberToValidate < 0) {
            throw new IllegalArgumentException("The " + object + " must be a positive integer.");
        }
    }

    /**
     * Ensuring that the input is a non-zero positive integer.
     *
     * @param numberToValidate The value that is to be validated.
     * @param object The name of the object being validated (for error message).
     * @throws IllegalArgumentException Exception thrown if the integer is zero or lower.
     */
    public static void validateNonZeroPositiveInteger(int numberToValidate, String object) throws IllegalArgumentException {
        if (numberToValidate <= 0) {
            throw new IllegalArgumentException("The " + object + " needs to be a non-zero positive integer.");
        }
    }

    // Game-specific validation methods
    /**
     * Validates game parameters for board size, number of players, and number of dice.
     *
     * @param numOfDices   number of dice
     * @param numOfPlayers number of players
     * @param sizeOfBoard  size of the board
     * @throws GameException if any parameter is invalid
     */
    public static void validateGameParameters(int numOfDices, int numOfPlayers, int sizeOfBoard) {
        if (numOfDices < MIN_DICE) {
            throw new GameException("Number of dice must be at least " + MIN_DICE);
        }
        if (numOfPlayers < MIN_PLAYERS) {
            throw new GameException("Number of players must be at least " + MIN_PLAYERS);
        }
        if (sizeOfBoard < MIN_BOARD_SIZE) {
            throw new GameException("Board size must be at least " + MIN_BOARD_SIZE);
        }
    }

    /**
     * Validates that a player object is not null.
     *
     * @param player the player to validate
     * @throws IllegalArgumentException if player is null
     */
    public static void validatePlayer(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
    }

    /**
     * Validates that a tile object is not null.
     *
     * @param tile the tile to validate
     * @throws IllegalArgumentException if tile is null
     */
    public static void validateTile(Tile tile) {
        Objects.requireNonNull(tile, "Tile cannot be null");
    }

    /**
     * Validates that a tile ID is not negative.
     *
     * @param id the tile ID to validate
     * @throws IllegalArgumentException if id is negative
     */
    public static void validateTileId(int id) {
        validateZeroPositiveInteger(id, "Tile ID");
    }

    /**
     * Validates that the game is in progress and has a current player.
     *
     * @param playing      whether the game is currently playing
     * @param currentPlayer the current player
     * @throws GameException if game is not in progress or no current player
     */
    public static void validateGameState(boolean playing, Player currentPlayer) {
        if (!playing || currentPlayer == null) {
            throw new GameException("Game is not in progress or no current player");
        }
    }

    /**
     * Validates that the game has players before initialization.
     *
     * @param players the map of players
     * @throws GameException if no players are added
     */
    public static void validatePlayersExist(java.util.Map<?, ?> players) {
        if (players.isEmpty()) {
            throw new GameException("No players have been added to the game");
        }
    }

    /**
     * Validates that the game is not already in progress.
     *
     * @param playing whether the game is currently playing
     * @throws GameException if game is already in progress
     */
    public static void validateGameNotStarted(boolean playing) {
        if (playing) {
            throw new GameException("Game is already in progress");
        }
    }
}