package edu.ntnu.iir.bidata.model.tile.core.monopoly;

import edu.ntnu.iir.bidata.model.tile.core.Tile;

/**
 * A tile that represents the Jail tile in the Monopoly game.
 *
 * @author kaamyashinde
 * @version 1.0
 */
public class JailTile extends Tile {

  /**
   * Constructs a new JailTile with the specified ID. This tile represents the "Jail" in the
   * Monopoly game, where players are "Just Visiting" unless specified otherwise.
   *
   * @param id the unique identifier for the tile
   */
  public JailTile(int id) {
    super(id, null); // No action, just visiting
  }
}
