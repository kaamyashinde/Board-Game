package edu.ntnu.iir.bidata.model.tile.actions.base;

import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;

/**
 * Represents a safe spot in Ludo where pieces cannot be captured.
 */
public class SafeSpotAction implements TileAction {

  @Override
  public void executeAction(Player player, Tile currentTile) {
    // Safe spots don't have any special action, they just prevent pieces from being captured
  }

  @Override
  public String getDescription() {
    return "Safe spot - pieces cannot be captured here";
  }
} 