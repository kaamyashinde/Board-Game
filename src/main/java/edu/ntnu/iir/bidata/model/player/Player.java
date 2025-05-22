package edu.ntnu.iir.bidata.model.player;

import edu.ntnu.iir.bidata.model.tile.core.Tile;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Class that handles the movements of a specific player.
 *
 * @author kaamyashinde
 * @version 0.0.2
 */
@Getter
@Setter
public class Player {

  private String name;
  private Tile currentTile;
  private boolean skipNextTurn;
  private String tokenImage; // Path or name of the token image

  /**
   * Constructs a Player instance with the specified name. The token image is set to null.
   *
   * @param inputName the name of the player
   */
  public Player(String inputName) {
    this(inputName, null);
  }

  /**
   * The constructor that initialises the players with their name and the board game they are
   * connected to.
   *
   * @param inputName the name of the player
   * @param tokenImage the image path or name for the player's token (optional)
   */
  public Player(String inputName, String tokenImage) {
    setName(inputName);
    setTokenImage(tokenImage);
  }

  /**
   * The method that allows the player to move on the board.
   *
   * @param steps the number of steps the player will move
   */
  public void move(int steps) {
    currentTile = currentTile.getNextTile(steps);
  }

  /**
   * The method that checks if the player is on the first tile.
   *
   * @return true if the player is on the first tile, false otherwise
   */
  public boolean isOnFirstTile() {
    return currentTile.isFirstTile();
  }

  /**
   * The method that checks if the player is on the last tile.
   *
   * @return true if the player is on the last tile, false otherwise
   */
  public boolean isOnLastTile() {
    return currentTile.isLastTile();
  }

  /**
   * Checks the current position of the player.
   *
   * @return the current position of the player
   */
  public int getCurrentPosition() {
    return currentTile != null ? currentTile.getId() : -1;
  }

  /**
   * Computes the hash code for the Player object based on its attributes.
   *
   * @return the hash code value calculated using the name, currentTile, skipNextTurn, and
   *     tokenImage fields
   */
  @Override
  public int hashCode() {
    return Objects.hash(name, currentTile, skipNextTurn, tokenImage);
  }

  /**
   * Compares this Player object with another object to determine equality. Two Player objects are
   * considered equal if they have the same name, currentTile, skipNextTurn value, and tokenImage.
   *
   * @param obj the object to be compared with this Player
   * @return true if the specified object is equal to this Player, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Player other = (Player) obj;
    return Objects.equals(name, other.name)
        && Objects.equals(currentTile, other.currentTile)
        && skipNextTurn == other.skipNextTurn
        && Objects.equals(tokenImage, other.tokenImage);
  }
}
