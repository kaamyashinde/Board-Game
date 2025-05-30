package edu.ntnu.iir.bidata.model.tile.actions.movement;

import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;

/**
 * Represents an action where a player hops forward by five steps on the game board. This action is
 * executed when a player lands on a tile associated with this action.
 */
public class HopFiveStepsAction implements TileAction {

  /** The method that executes the tile action of skipping 5 steps. */
  @Override
  public void executeAction(Player player, Tile currentTile) {
    player.move(5);
  }

  /** The method that returns the description of the tile action. */
  @Override
  public String getDescription() {
    return "Skipping ahead 5 steps";
  }
}
