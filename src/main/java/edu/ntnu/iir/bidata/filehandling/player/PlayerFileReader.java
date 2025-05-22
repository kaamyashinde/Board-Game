package edu.ntnu.iir.bidata.filehandling.player;

import edu.ntnu.iir.bidata.model.player.Player;
import java.nio.file.Path;
import java.util.List;

/**
 * Interface for reading player information from files or other input sources. This interface
 * provides methods for reading single player data or a list of players from a file.
 */
public interface PlayerFileReader {

  /**
   * Reads a player's information from a file specified by the given path.
   *
   * @param filePath the path to the file containing player data
   * @return a Player object representing the player data read from the file, or null if the file is
   *     empty
   * @throws RuntimeException if an error occurs while reading the file
   */
  Player readPlayer(Path filePath);

  /**
   * Reads all players from a CSV file (one name per line).
   *
   * @param filePath the path to the CSV file
   * @return a list of Player objects
   */
  List<Player> readPlayers(Path filePath);
}
