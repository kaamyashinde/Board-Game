package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a tile on the game board.
 *
 * @author Durva
 * @version 0.0.1
 */
@Getter
@Setter
public class Tile {
  /**
   * The id of the tile.
   */
  private int id;
  /**
   * The action connected to the tile.
   */
  private TileAction action;
  /**
   * -- SETTER --
   * Sets the next tile in the sequence.
   *
   * @param nextTile The next tile to set.
   */
  @Setter
  private Tile nextTile;

  /**
   * Constructor that creates an instance of the tile with the desired id and tileAction.
   * @param id the id of the tile.
   * @param action the action associated with the tile.
   * @author Kaamya
   */
  public Tile(int id, TileAction action){
    this.id = id;
    this.action = action;
  }

  /**
   * Executes the action associated with this tile
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
