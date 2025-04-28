package edu.ntnu.iir.bidata.model.tile.snakeandladder;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.TileAction;

/**
 * A tile action that represents a snake in a snake and ladder game.
 * When a player lands on the head of a snake, they slide down to its tail.
 */
public class SnakeAction implements TileAction {
    private final int tailTileId;

    /**
     * Creates a new SnakeAction that will move the player to the tail of the snake.
     * @param tailTileId The ID of the tile at the tail of the snake (must be lower than current tile)
     */
    public SnakeAction(int tailTileId) {
        if (tailTileId <= 0) {
            throw new IllegalArgumentException("Tail tile ID must be positive");
        }
        this.tailTileId = tailTileId;
    }

    @Override
    public void performAction(Player player) {
        System.out.println(player.getName() + " encountered a snake! Sliding down to tile " + tailTileId + "!");
    }

    @Override
    public String getDescription() {
        return "Slide down to tile " + tailTileId;
    }

    public int getTailTileId() {
        return tailTileId;
    }
} 