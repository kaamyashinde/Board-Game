package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.tile.Tile;
import edu.ntnu.iir.bidata.model.tile.TileAction;
import edu.ntnu.iir.bidata.utils.ParameterValidation;
import lombok.Getter;

import java.util.HashMap;


/**
 * This class contains the actual board where a game can be played.
 *
 * @author kaamyashinde
 * @version 0.0.2
 */
@Getter
public class Board {
  private final HashMap<Integer, Tile> tiles;

  /**
   * Constructor for the Board class.
   * @param sizeOfBoard the size of the board
   */
  public Board(int sizeOfBoard){
    ParameterValidation.validateNonZeroPositiveInteger(sizeOfBoard, "size of board");
    this.tiles = new HashMap<>(sizeOfBoard);
  }

  /**
   * The method that allows the addition of a tile to the board.
   */

public boolean addTile(int id, TileAction action){
  ParameterValidation.validateTileId(id);
  if(tiles.containsKey(id)){
    return false;
  }
  tiles.put(id, new Tile(id, action));
  return true;
}

  /**
   * Access a specific position on the board.
   *
   * @param id the id to be used to access a specific spot on the board.
   */
  public Tile getPositionOnBoard(int id){
    return tiles.get(id);
  }

  /**
   * Returns the size of the board.
   * @return the size of the board
   */
  public int getSizeOfBoard(){
    return tiles.size();
  }

  /**
   * Returns the tiles of the board.
   * @return the tiles of the board
   */
  public HashMap<Integer, Tile> getTiles(){
    return tiles;
  }

  /**
   * Returns the tile at a specific position on the board.
   * @param id the id of the tile
   * @return the tile at the specific position
   */
  public Tile getTile(int id){
    return tiles.get(id);
  }
}