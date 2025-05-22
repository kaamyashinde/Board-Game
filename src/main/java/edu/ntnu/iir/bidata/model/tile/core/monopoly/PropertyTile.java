package edu.ntnu.iir.bidata.model.tile.core.monopoly;

import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import lombok.Getter;
import lombok.Setter;

/**
 * A class that represents a property tile in the monopoly game.
 *
 * @author Kaamya Shinde
 * @version 1.0
 */
public class PropertyTile extends Tile {
  @Getter @Setter private int price;
  @Getter @Setter private int rent;
  @Getter @Setter private int group;
  @Getter @Setter private SimpleMonopolyPlayer owner;

  /**
   * Constructor for the PropertyTile class.
   *
   * @param id The id of the tile.
   * @param price The price of the property.
   * @param rent The rent of the property.
   * @param group The group of the property.
   */
  public PropertyTile(int id, int price, int rent, int group) {
    super(id);
    this.price = price;
    this.rent = rent;
    this.group = group;
    this.owner = null;
  }

  /**
   * Constructor for the PropertyTile class.
   *
   * @param id The id of the tile.
   * @param price The price of the property.
   * @param rent The rent of the property.
   * @param group The group of the property.
   * @param action The action associated with the property.
   */
  public PropertyTile(int id, int price, int rent, int group, TileAction action) {
    super(id, action);
    this.price = price;
    this.rent = rent;
    this.group = group;
    this.owner = null;
  }

  /**
   * Checks if the property is owned.
   *
   * @return True if the property is owned, false otherwise.
   */
  public boolean isOwned() {
    return owner != null;
  }
}
