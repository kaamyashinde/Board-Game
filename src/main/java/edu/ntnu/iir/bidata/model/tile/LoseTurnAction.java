package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;

public class LoseTurnAction implements TileAction {
    
    @Override
    public String getDescription() {
        return "Skip your next turn";
    }

    @Override
    public void executeAction(Player player, Tile currentTile) {
        player.setSkipNextTurn(true);
    }
} 