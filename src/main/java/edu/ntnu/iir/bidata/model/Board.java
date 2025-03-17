package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.tile.Tile;
import edu.ntnu.iir.bidata.utils.ParameterValidation;
import lombok.Getter;

import java.util.HashMap;

/**
 * This class contains the actual board where a game can be played.
 *
 * @author kaamyashinde
 * @version 0.0.1
 */
@Getter
public class Board {
  private final HashMap<Integer, Tile> tiles;

  /**
   * The constructor where the board is initialised, where all the tiles have no actions.
   *
   * @param numberOfTiles The number of tiles the board would contain.
   */
  public Board(int numberOfTiles) {
    ParameterValidation.validateNonZeroPositiveInteger(numberOfTiles, "number of tiles to create");
    tiles = new HashMap<>();
    for (int i = 0; i < numberOfTiles; i++) {
      tiles.put(i, new Tile(i, null));
    }

    // Link the tiles: set each tile's nextTile except for the final tile.
    for (int i = 0; i < numberOfTiles - 1; i++) {
      tiles.get(i).setNextTile(tiles.get(i + 1));
    }
  }


  /**
   * Access a specific position on the board.
   *
   * @param id the id to be used to access a specific spot on the board.
   */
  public Tile getPositionOnBoard(int id){
    return tiles.get(id);
  }
}