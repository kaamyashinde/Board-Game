package edu.ntnu.iir.bidata.model.tile.core.monopoly;

import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.actions.monopoly.GoToJailAction;
/**
 * A tile that represents the Jail tile in the Monopoly game.
 * 
 * @author kaamyashinde
 * @version 1.0
 */
public class JailTile extends Tile {
    public JailTile(int id) {
        super(id, new GoToJailAction());
    }
}