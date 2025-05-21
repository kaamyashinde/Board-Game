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
    private final int jailTileId;

    public GoToJailAction() {
        this(10); // Default jail tile ID
    }

    public GoToJailAction(int jailTileId) {
        this.jailTileId = jailTileId;
    }

    @Override
    public void executeAction(Player player, Tile tile) {
        if (player instanceof SimpleMonopolyPlayer) {
            ((SimpleMonopolyPlayer) player).goToJail();
            // Move player to the jail tile
            Tile current = tile;
            while (current != null && current.getId() != jailTileId) {
                current = current.getNextTile();
            }
            if (current != null) {
                player.setCurrentTile(current);
            }
        }
    }

    @Override
    public String getDescription() {
        return "Go to Jail";
    }

    public int getJailTileId() {
        return jailTileId;
    }
}
