package edu.ntnu.iir.bidata.ui;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.TileAction;

/**
 * Interface for game UI implementations.
 */
public interface GameUI {
    /**
     * Displays the start of a player's turn.
     *
     * @param player The current player
     * @param currentPosition The player's current position
     */
    void displayTurnStart(Player player, int currentPosition);

    /**
     * Displays the result of a dice roll.
     *
     * @param rollResult The result of the dice roll
     */
    void displayDiceRoll(int rollResult);

    /**
     * Displays the player's new position after moving.
     *
     * @param newPosition The player's new position
     */
    void displayNewPosition(int newPosition);

    /**
     * Displays the game winner.
     *
     * @param winner The winning player
     */
    void displayWinner(Player winner);

    /**
     * Displays a separator line for visual organization.
     */
    void displaySeparator();

    /**
     * Displays information about a tile action that was triggered.
     *
     * @param player The player who triggered the action
     * @param action The action that was triggered
     */
    void displayTileAction(Player player, TileAction action);
} 