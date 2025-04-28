package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;

/**
 * A tile action that teleports a player to a specific tile.
 */
public class TeleportAction implements TileAction {
    private final int targetTileId;

    /**
     * Creates a new TeleportAction that teleports the player to the specified tile.
     * @param targetTileId The ID of the tile to teleport to
     */
    public TeleportAction(int targetTileId) {
        if (targetTileId < 0) {
            throw new IllegalArgumentException("Target tile ID must be non-negative");
        }
        this.targetTileId = targetTileId;
    }

    @Override
    public void performAction(Player player) {
        // The actual teleportation will be handled by the game logic
        // This action just marks that the player should be teleported
        System.out.println(player.getName() + " is being teleported to tile " + targetTileId + "!");
    }

    @Override
    public String getDescription() {
        return "Teleport to tile " + targetTileId;
    }

    public int getTargetTileId() {
        return targetTileId;
    }
} 