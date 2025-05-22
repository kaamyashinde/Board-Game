package edu.ntnu.iir.bidata.model.tile.actions.snakeandladder;

import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;

/**
 * A tile action that represents a ladder in a snake and ladder game. When a player lands on the
 * bottom of a ladder, they climb up to the top.
 */
public class LadderAction implements TileAction {

  private final int topTileId;

  /**
   * Creates a new LadderAction that will move the player to the top of the ladder.
   *
   * @param topTileId The ID of the tile at the top of the ladder (must be higher than current tile)
   */
  public LadderAction(int topTileId) {
    if (topTileId <= 0) {
      throw new IllegalArgumentException("Top tile ID must be positive");
    }
    this.topTileId = topTileId;
  }

  @Override
  public void executeAction(Player player, Tile currentTile) {
    // Find the target tile
    Tile targetTile = currentTile;

    // Move forward until we reach the target tile
    while (targetTile != null && targetTile.getId() < topTileId) {
      targetTile = targetTile.getNextTile();
    }

    if (targetTile != null && targetTile.getId() == topTileId) {
      player.setCurrentTile(targetTile);
    }
  }

  @Override
  public String getDescription() {
    return "Climb up to tile " + topTileId;
  }

  public int getTopTileId() {
    return topTileId;
  }
}
