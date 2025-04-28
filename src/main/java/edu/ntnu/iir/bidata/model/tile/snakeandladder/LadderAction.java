package edu.ntnu.iir.bidata.model.tile.snakeandladder;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.TileAction;

/**
 * A tile action that represents a ladder in a snake and ladder game.
 * When a player lands on the bottom of a ladder, they climb up to the top.
 */
public class LadderAction implements TileAction {
    private final int topTileId;

    /**
     * Creates a new LadderAction that will move the player to the top of the ladder.
     * @param topTileId The ID of the tile at the top of the ladder (must be higher than current tile)
     */
    public LadderAction(int topTileId) {
        if (topTileId <= 0) {
            throw new IllegalArgumentException("Top tile ID must be positive");
        }
        this.topTileId = topTileId;
    }

    @Override
    public void performAction(Player player) {
        System.out.println(player.getName() + " found a ladder! Climbing up to tile " + topTileId + "!");
    }

    @Override
    public String getDescription() {
        return "Climb up to tile " + topTileId;
    }

    public int getTopTileId() {
        return topTileId;
    }
} 