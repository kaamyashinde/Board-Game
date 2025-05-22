package edu.ntnu.iir.bidata.model.tile.core.monopoly;

import edu.ntnu.iir.bidata.model.tile.actions.monopoly.CollectMoneyAction;
import edu.ntnu.iir.bidata.model.tile.core.Tile;

/**
 * A tile that represents the Go tile in the Monopoly game.
 *
 * @author kaamyashinde
 * @version 1.0
 */
public class GoTile extends Tile {

  /**
   * Constructs a new GoTile with the specified ID. Represents the "Go" tile in the Monopoly game,
   * where players collect money when they pass or land on this tile.
   *
   * @param id the unique identifier for the tile
   */
  public GoTile(int id) {
    super(id, new CollectMoneyAction());
  }
}
