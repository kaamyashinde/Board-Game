package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;

/**
 * A tile action that makes the current player switch places with another player.
 */
public class SwitchPlacesAction implements TileAction {
    private final Player otherPlayer;

    /**
     * Creates a new SwitchPlacesAction that will switch the current player with the specified player.
     * @param otherPlayer The player to switch places with
     */
    public SwitchPlacesAction(Player otherPlayer) {
        if (otherPlayer == null) {
            throw new IllegalArgumentException("Other player cannot be null");
        }
        this.otherPlayer = otherPlayer;
    }

    @Override
    public void performAction(Player currentPlayer) {
        if (currentPlayer.equals(otherPlayer)) {
            System.out.println("Cannot switch places with yourself!");
            return;
        }

        Tile currentPlayerTile = currentPlayer.getCurrentTile();
        Tile otherPlayerTile = otherPlayer.getCurrentTile();

        currentPlayer.placeOnTile(otherPlayerTile);
        otherPlayer.placeOnTile(currentPlayerTile);

        System.out.println(currentPlayer.getName() + " and " + otherPlayer.getName() + " have switched places!");
    }

    @Override
    public String getDescription() {
        return "Switch places with " + otherPlayer.getName();
    }
} 