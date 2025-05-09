package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.TileAction;

/**
 * Interface for game UI implementations.
 */
public interface GameUI {
    /**
     * Displays a welcome message at the start of the game.
     */
    void displayWelcomeMessage();

    /**
     * Gets the number of players from the user.
     * @return The number of players
     */
    int getNumberOfPlayers();

    /**
     * Gets a player's name from the user.
     * @param playerNumber The player number (1-based)
     * @return The player's name
     */
    String getPlayerName(int playerNumber);

    /**
     * Displays the start of a player's turn.
     * @param player The current player
     */
    void displayPlayerTurn(Player player);

    /**
     * Displays the result of a dice roll.
     * @param player The player who rolled
     * @param rollResult The result of the dice roll
     */
    void displayDiceRoll(Player player, int rollResult);

    /**
     * Displays the current state of the game board.
     */
    void displayBoard();

    /**
     * Displays the game winner.
     * @param winner The winning player
     */
    void displayWinner(Player winner);

    /**
     * Displays a separator line for visual organization.
     */
    void displaySeparator();

    /**
     * Displays information about a tile action that was triggered.
     * @param player The player who triggered the action
     * @param action The action that was triggered
     */
    void displayTileAction(Player player, TileAction action);
} 