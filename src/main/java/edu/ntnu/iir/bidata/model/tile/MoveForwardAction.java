package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;

/**
 * A tile action that moves a player forward a specified number of spaces.
 */
public class MoveForwardAction implements TileAction {
    private final int spacesToMoveForward;

    /**
     * Creates a new MoveForwardAction that moves the player forward the specified number of spaces.
     * @param spacesToMoveForward The number of spaces to move forward (must be positive)
     */
    public MoveForwardAction(int spacesToMoveForward) {
        if (spacesToMoveForward <= 0) {
            throw new IllegalArgumentException("Spaces to move forward must be positive");
        }
        this.spacesToMoveForward = spacesToMoveForward;
    }

    @Override
    public void performAction(Player player) {
        Tile currentTile = player.getCurrentTile();
        for (int i = 0; i < spacesToMoveForward; i++) {
            if (currentTile.getNextTile() != null) {
                currentTile = currentTile.getNextTile();
            } else {
                break; // Stop if we reach the end of the board
            }
        }
        player.placeOnTile(currentTile);
    }

    @Override
    public String getDescription() {
        return "Move forward " + spacesToMoveForward + " spaces";
    }
} 