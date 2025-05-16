package edu.ntnu.iir.bidata.model.tile.actions.snakeandladder;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;

/**
 * A tile action that represents a snake in a snake and ladder game. When a player lands on the head
 * of a snake, they slide down to its tail.
 */
public class SnakeAction implements TileAction {

  private final int tailTileId;

  /**
   * Creates a new SnakeAction that will move the player to the tail of the snake.
   *
   * @param tailTileId The ID of the tile at the tail of the snake (must be lower than current
   *                   tile)
   */
  public SnakeAction(int tailTileId) {
    if (tailTileId <= 0) {
      throw new IllegalArgumentException("Tail tile ID must be positive");
    }
    this.tailTileId = tailTileId;
  }

  @Override
  public void executeAction(Player player, Tile currentTile) {
    // Find the target tile
    Tile targetTile = currentTile;

    // Move backward until we reach the target tile
    while (targetTile != null && targetTile.getId() > tailTileId) {
      targetTile = targetTile.getNextTile();
    }

    if (targetTile != null && targetTile.getId() == tailTileId) {
      player.setCurrentTile(targetTile);
      System.out.println(
          player.getName() + " encountered a snake! Slid down to tile " + tailTileId + "!");
    }
  }

  @Override
  public String getDescription() {
    return "Slide down to tile " + tailTileId;
  }

  public int getTailTileId() {
    return tailTileId;
  }
} 