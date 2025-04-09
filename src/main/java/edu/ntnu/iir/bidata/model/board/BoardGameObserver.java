package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.Player;

/**
 * Interface for objects that want to observe changes in the BoardGame state.
 * This follows the Observer design pattern to notify observers of game state changes.
 *
 * @author kaamyashinde
 * @version 1.0.0
 */
public interface BoardGameObserver {
    /**
     * Called when a player has moved to a new position.
     *
     * @param player the player who moved
     * @param newPosition the new position (tile ID) of the player
     */
    void onPlayerMoved(Player player, int newPosition);

    /**
     * Called when a player has won the game.
     *
     * @param winner the player who won the game
     */
    void onGameWon(Player winner);

    /**
     * Called when it becomes a new player's turn.
     *
     * @param currentPlayer the player whose turn it is now
     */
    void onTurnChanged(Player currentPlayer);
}