package edu.ntnu.iir.bidata.model.tile.core.monopoly;

import edu.ntnu.iir.bidata.model.tile.core.Tile;
/**
 * A tile that represents the Jail tile in the Monopoly game.
 * 
 * @author kaamyashinde
 * @version 1.0
 */
public class JailTile extends Tile {
    public JailTile(int id) {
        super(id, null); // No action, just visiting
    }
}