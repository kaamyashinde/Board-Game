package edu.ntnu.iir.bidata.model.tile.actions.base;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;

/**
 * A tile action that moves a player to a specific tile on the board. This action can be used to
 * move players forward or backward by specifying the target tile's ID. If the target tile doesn't
 * exist, a GameException is thrown.
 *
 * @author kaamyashinde
 * @version 1.0
 */
public class GoToTileAction implements TileAction {

  private final int targetTileId;

  /**
   * Constructs a new GoToTileAction with the target tile ID.
   *
   * @param targetTileId The ID of the tile to move to
   * @throws IllegalArgumentException if targetTileId is negative
   */
  public GoToTileAction(int targetTileId) {
    if (targetTileId < 0) {
      throw new IllegalArgumentException("Target tile ID cannot be negative");
    }
    this.targetTileId = targetTileId;
  }

  /**
   * Executes the action by moving the player to the target tile. The player will move forward
   * through the tiles until reaching the target.
   *
   * @param player The player who landed on the tile
   * @param currentTile The tile the player landed on
   * @throws GameException if the target tile cannot be reached
   */
  @Override
  public void executeAction(Player player, Tile currentTile) {
    Tile targetTile = currentTile;
    while (targetTile != null && targetTile.getId() != targetTileId) {
      targetTile = targetTile.getNextTile();
    }

    if (targetTile == null) {
      throw new GameException("Cannot reach target tile " + targetTileId);
    }

    player.setCurrentTile(targetTile);
  }

  /**
   * Returns a description of the action that will be performed.
   *
   * @return A string describing the action
   */
  @Override
  public String getDescription() {
    return "Go to tile " + targetTileId;
  }

  public int getTargetTileId() {
    return targetTileId;
  }
}
