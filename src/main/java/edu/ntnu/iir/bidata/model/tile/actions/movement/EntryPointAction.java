package edu.ntnu.iir.bidata.model.tile.actions.movement;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;

/**
 * Represents an entry point in Ludo where players can enter their pieces from their home area.
 */
public class EntryPointAction implements TileAction {

  private final Player owner;

  public EntryPointAction(Player owner) {
    this.owner = owner;
  }

  @Override
  public void executeAction(Player player, Tile currentTile) {
    // Entry points don't have any special action, they just mark where players can enter
    // The actual entry logic is handled by the game rules
  }

  @Override
  public String getDescription() {
    return "Entry point for " + owner.getName() + "'s pieces";
  }

  public Player getOwner() {
    return owner;
  }
} 