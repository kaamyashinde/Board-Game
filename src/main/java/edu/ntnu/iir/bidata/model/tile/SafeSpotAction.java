package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;

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