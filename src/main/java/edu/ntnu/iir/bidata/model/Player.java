package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.tile.Tile;
import lombok.Getter;
import lombok.Setter;
/**
 * Class that handles the movements of a specific player.
 * @author kaamyashinde
 * @version 0.0.1
 */
@Getter
@Setter

public class Player {
  private String name;
  private Tile currentTile;

  /**
   * The constructor that initialises the players with their name and the board game they are connected to.
   * @param inputName the name of the player
   * @param game the instance of Board Game they are connected to todo
   */
  public Player(String inputName) {
    setName(inputName);
  }
}