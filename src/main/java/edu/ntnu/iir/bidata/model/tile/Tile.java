package edu.ntnu.iir.bidata.model.tile;
import edu.ntnu.iir.bidata.model.Player;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a tile on the game board.
 * @author Durva
 * @version 0.0.1
 */
@Getter
@Setter
public class Tile {
  private int id;
  private TileAction action;

  /**
   * -- SETTER --
   *  Sets the next tile in the sequence.
   *
   * @param nextTile The next tile to set.
   */
  @Setter
  private Tile nextTile;


  /**
   * Two-argument constructor that allows setting the id and action.
   *
   * @param id The tile's identifier.
   * @param action The action associated with the tile.
   */
  public Tile(int id, TileAction action) {
    this.id = id;
    this.action = action;
  }

  /**
   * Single-argument constructor (defaults the action to null).
   *
   * @param id The tile's identifier.
   */
  public Tile(int id) {
    // Call the two-argument constructor with a default action of null
    this(id, null);
  }


  /**
   * Executes the action associated with this tile.
   *
   * @param player The player who landed on this tile.
   */
  public void performAction(Player player) {
    if (action != null) {
      action.performAction(player);
    }
  }

  /**
   * Places the player on this tile.
   *
   * @param player The player to place on this tile.
   */
  public void landPlayer(Player player) {
    player.placeOnTile(this);
    performAction(player);
  }

  /**
   * Removes the player from this tile.
   *
   * @param player The player to remove from this tile.
   */
  public void leavePlayer(Player player) {
    // need more?
  }
}
