package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.exception.GameException;
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
   * A method that connects the tiles in the board.
   * @param id
   * @param nextTile
   */
  public void connectTiles(int id, Tile nextTile) {
    Tile fromTile = tiles.get(id);
    if (fromTile != null && nextTile != null) {
        fromTile.setNextTile(nextTile);
    }
  }
  /**
   * The method that returns the starting tile of the board.
   * @return the starting tile of the board
   */
  public Tile getStartingTile(){
    return tiles.get(0);
  }
  /**
   * The method that returns the ending tile of the board.
   * @return the ending tile of the board
   */
  public Tile getEndingTile(){
    return tiles.get(tiles.size() - 1);
  }
  /**
   * The method that checks if a tile connection is valid.
   * @param fromId
   * @param toId
   * @return true if the connection is valid, false otherwise 
   */
  public boolean isValidTileConnection(int fromId, int toId){
    Tile fromTile = tiles.get(fromId);
    Tile toTile = tiles.get(toId);
    try {
        return fromTile.getNextTile(1) == toTile;
    } catch (GameException e) {
        return false;
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