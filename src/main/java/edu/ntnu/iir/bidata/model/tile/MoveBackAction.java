package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;

/**
 * A tile action that moves a player back a specified number of spaces.
 */
public class MoveBackAction implements TileAction {
    private final int spacesToMoveBack;

    /**
     * Creates a new MoveBackAction that moves the player back the specified number of spaces.
     * @param spacesToMoveBack The number of spaces to move back (must be positive)
     */
    public MoveBackAction(int spacesToMoveBack) {
        if (spacesToMoveBack <= 0) {
            throw new IllegalArgumentException("Spaces to move back must be positive");
        }
        this.spacesToMoveBack = spacesToMoveBack;
    }

    @Override
    public void performAction(Player player) {
        Tile currentTile = player.getCurrentTile();
        for (int i = 0; i < spacesToMoveBack; i++) {
            if (currentTile.getPreviousTile() != null) {
                currentTile = currentTile.getPreviousTile();
            } else {
                break; // Stop if we reach the start of the board
            }
        }
        player.placeOnTile(currentTile);
    }

    @Override
    public String getDescription() {
        return "Move back " + spacesToMoveBack + " spaces";
    }
} 