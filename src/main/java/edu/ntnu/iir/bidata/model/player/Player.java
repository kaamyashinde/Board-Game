package edu.ntnu.iir.bidata.model.player;

import edu.ntnu.iir.bidata.model.tile.core.Tile;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

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

  public Player(String inputName) {
    this(inputName, null);
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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Player other = (Player) obj;
    return Objects.equals(name, other.name) &&
           Objects.equals(currentTile, other.currentTile) &&
           skipNextTurn == other.skipNextTurn &&
           Objects.equals(tokenImage, other.tokenImage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, currentTile, skipNextTurn, tokenImage);
  }

}