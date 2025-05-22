package edu.ntnu.iir.bidata.view.common;

import java.util.List;
import java.util.Map;

/**
 * Represents the result of a player selection process, containing the names of selected players and
 * their associated tokens.
 *
 * <p>This class is designed to encapsulate the outcome of a player selection, where a list of
 * player names and a corresponding map of player tokens are provided. Each player's name is stored
 * as a string in the list, and their tokens are represented as key-value pairs in a map where the
 * key is the player's name and the value is the player's token.
 *
 * <p>The class provides immutable access to the player names and tokens, ensuring the integrity of
 * this data once the object is created.
 */
public class PlayerSelectionResult {
  public final List<String> playerNames;
  public final Map<String, String> playerTokens;

  /**
   * Constructs a new PlayerSelectionResult instance containing a list of player names and a map of
   * player tokens. Each player's name is associated with a corresponding token in the map.
   *
   * @param playerNames the list of player names participating in the selection process
   * @param playerTokens a map where each key is a player's name and the corresponding value is the
   *     player's token
   */
  public PlayerSelectionResult(List<String> playerNames, Map<String, String> playerTokens) {
    this.playerNames = playerNames;
    this.playerTokens = playerTokens;
  }
}
