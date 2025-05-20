package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import edu.ntnu.iir.bidata.model.utils.ParameterValidation;
import java.util.HashMap;
import java.util.Objects;
import lombok.Getter;

/**
 * This class contains the actual board where a game can be played.
 *
 * @author kaamyashinde
 * @version 0.0.2
 */
@Getter
public class Board {

  private final HashMap<Integer, Tile> tiles;
  private final int boardSize;

  /**
   * Constructor for the Board class.
   *
   * @param sizeOfBoard the size of the board
   */
  public Board(int sizeOfBoard) {
    ParameterValidation.validateNonZeroPositiveInteger(sizeOfBoard, "size of board");
    this.tiles = new HashMap<>(sizeOfBoard);
    this.boardSize = sizeOfBoard;
  }

  /**
   * The method that allows the addition of a tile to the board.
   */
  public boolean addTile(int id, TileAction action) {
    ParameterValidation.validateTileId(id);
    if (tiles.containsKey(id)) {
      return false;
    }
    tiles.put(id, new Tile(id, action));
    return true;
  }
  public void addTile(Tile tile) {
    ParameterValidation.validateTileId(tile.getId());
    if (tiles.containsKey(tile.getId())) {
      throw new GameException("Tile already exists");
    }
    tiles.put(tile.getId(), tile);
  }
  /**
   * A method that connects the tiles in the board.
   *
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
   *
   * @return the starting tile of the board
   */
  public Tile getStartingTile() {
    return tiles.get(0);
  }

  /**
   * The method that returns the ending tile of the board.
   *
   * @return the ending tile of the board
   */
  public Tile getEndingTile() {
    return tiles.get(boardSize - 1);
  }

  /**
   * The method that checks if a tile connection is valid.
   *
   * @param fromId
   * @param toId
   * @return true if the connection is valid, false otherwise
   */
  public boolean isValidTileConnection(int fromId, int toId) {
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
  public Tile getPositionOnBoard(int id) {
    return tiles.get(id);
  }

  /**
   * Returns the size of the board.
   *
   * @return the size of the board
   */
  public int getSizeOfBoard() {
    return boardSize;
  }

  /**
   * Returns the tiles of the board.
   *
   * @return the tiles of the board
   */
  public HashMap<Integer, Tile> getTiles() {
    return tiles;
  }

  /**
   * Returns the tile at a specific position on the board.
   *
   * @param id the id of the tile
   * @return the tile at the specific position
   */
  public Tile getTile(int id) {
    return tiles.get(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Board other = (Board) obj;
    return boardSize == other.boardSize &&
           Objects.equals(tiles, other.tiles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(boardSize, tiles);
  }
}