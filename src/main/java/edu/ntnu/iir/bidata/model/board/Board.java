package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import edu.ntnu.iir.bidata.model.utils.ParameterValidation;
import java.util.HashMap;
import java.util.Map;
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

  /** The method that allows the addition of a tile to the board. */
  public boolean addTile(int id, TileAction action) {
    ParameterValidation.validateTileId(id);
    if (tiles.containsKey(id)) {
      return false;
    }
    tiles.put(id, new Tile(id, action));
    return true;
  }

  /**
   * Adds a tile to the board.
   *
   * @param tile the tile to be added
   * @throws IllegalArgumentException if the tile ID is invalid
   * @throws GameException if a tile with the same ID already exists on the board
   */
  public void addTile(Tile tile) {
    ParameterValidation.validateTileId(tile.getId());
    if (tiles.containsKey(tile.getId())) {
      throw new GameException("Tile already exists");
    }
    tiles.put(tile.getId(), tile);
  }

  /**
   * Connects a tile on the board to its next tile.
   *
   * @param id the identifier of the current tile to connect
   * @param nextTile the tile to set as the next tile for the current tile
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
   * Determines if a valid connection exists between two tiles on the board.
   *
   * <p>A connection is considered valid if the tile with identifier {@code fromId} correctly points
   * to the tile with identifier {@code toId}.
   *
   * @param fromId the identifier of the starting tile
   * @param toId the identifier of the target tile to check for connection
   * @return true if a valid connection exists between the tiles, false otherwise
   */
  public boolean isValidTileConnection(int fromId, int toId) {
    Tile fromTile = tiles.get(fromId);
    Tile toTile = tiles.get(toId);

    // Add null check for fromTile
    if (fromTile == null) {
      return false;
    }

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
  public Map<Integer, Tile> getTiles() {
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

  /**
   * Generates a hash code for the Board instance based on its state.
   *
   * @return an integer hash code derived from the boardSize and tiles fields
   */
  @Override
  public int hashCode() {
    return Objects.hash(boardSize, tiles);
  }

  /**
   * Compares this Board instance with another object to determine equality. Two Board instances are
   * considered equal if they have the same board size and their tiles are equal.
   *
   * @param obj the object to compare with this Board instance
   * @return true if the specified object is equal to this Board instance, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Board other = (Board) obj;
    return boardSize == other.boardSize && Objects.equals(tiles, other.tiles);
  }
}
