package edu.ntnu.iir.bidata.model.tile.actions.monopoly;

import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
/**
 * A tile action that moves a player to the Jail tile.
 * 
 * @author kaamyashinde
 * @version 1.0
 */
public class GoToJailAction implements TileAction {
    @Override
    public void executeAction(Player player, Tile tile) {
        if (player instanceof SimpleMonopolyPlayer) {
           //TODO: ((SimpleMonopolyPlayer) player).goToJail();
        }
    }

    @Override
    public String getDescription() {
        return "Go to Jail";
    }
}
