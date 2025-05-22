package edu.ntnu.iir.bidata.model.tile.actions.movement;

import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;

/** Represents an entry point in Ludo where players can enter their pieces from their home area. */
public class EntryPointAction implements TileAction {

  private final Player owner;

  /**
   * Constructs an {@code EntryPointAction} associated with a specific player.
   *
   * @param owner the player who owns this entry point action
   */
  public EntryPointAction(Player owner) {
    this.owner = owner;
  }

  /**
   * Executes the action associated with the entry point tile.
   * This method performs no specific action, as entry points solely mark where players can
   * enter the game. The actual logic for player entry is handled by the game rules.
   *
   * @param player the player interacting with the current tile
   * @param currentTile the tile on which the action is being executed
   */
  @Override
  public void executeAction(Player player, Tile currentTile) {
    // Entry points don't have any special action, they just mark where players can enter
    // The actual entry logic is handled by the game rules
  }

  /**
   * Retrieves a description of the entry point action.
   *
   * @return a string representing the entry point for the owner's pieces
   */
  @Override
  public String getDescription() {
    return "Entry point for " + owner.getName() + "'s pieces";
  }

  /**
   * Retrieves the owner of the current entry point action.
   *
   * @return the Player object representing the owner of this entry point action
   */
  public Player getOwner() {
    return owner;
  }
}
