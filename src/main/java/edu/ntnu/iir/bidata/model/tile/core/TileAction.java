package edu.ntnu.iir.bidata.model.tile.core;

import edu.ntnu.iir.bidata.model.player.Player;

/**
 * Represents an action associated with a tile on a game board. Implementations of this interface
 * define specific behaviors or events that occur when a player interacts with the tile.
 */
public interface TileAction {

  /**
   * Executes a specific action when a player interacts with a tile on the game board.
   *
   * @param player the player triggering the action
   * @param currentTile the tile on which the action is executed
   */
  void executeAction(Player player, Tile currentTile);

  /**
   * Retrieves a textual description of the tile action.
   *
   * @return a string describing the action associated with the tile
   */
  String getDescription();
}
