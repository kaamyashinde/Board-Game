package edu.ntnu.iir.bidata.model.tile.actions.monopoly;

import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;

/**
 * A tile action that collects money from the player.
 * 
 * @author kaamyashinde
 * @version 1.0
 */
public class CollectMoneyAction implements TileAction {
    /**
     * Collects money when passing the Go tile.
     * 
     * @param player The player that collects the money.
     * @param currentTile The current tile.
     */
    @Override
    public void executeAction(Player player, Tile currentTile) {
        if (player instanceof SimpleMonopolyPlayer){
            ((SimpleMonopolyPlayer) player).collectMoney(200);
        }
    }

    /**
     * Gets the description of the action.
     */
    @Override
    public String getDescription() {
        return "Collect 200 money";
    }
}
