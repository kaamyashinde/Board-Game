package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;

/**
 * A tile action that makes a player skip their next turn.
 */
public class SkipTurnAction implements TileAction {
    @Override
    public void performAction(Player player) {
        // In a real implementation, you would need to mark the player to skip their next turn
        // This would require additional game state management
        System.out.println(player.getName() + " must skip their next turn!");
    }

    @Override
    public String getDescription() {
        return "Skip your next turn";
    }
} 