package edu.ntnu.iir.bidata.model.tile.core.monopoly;

import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.actions.monopoly.CollectMoneyAction;
/**
 * A tile that represents the Go tile in the Monopoly game.
 * 
 * @author kaamyashinde
 * @version 1.0
 */
public class GoTile extends Tile {

    public GoTile(int id) {
        super(id, new CollectMoneyAction());
    }
}
