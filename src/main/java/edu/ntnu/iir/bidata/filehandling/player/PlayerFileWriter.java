package edu.ntnu.iir.bidata.filehandling.player;

import edu.ntnu.iir.bidata.model.player.Player;
import java.nio.file.Path;

/**
 * Interface for writing player information to files or other output sources. This interface
 * provides methods for writing single player data to a file.
 */
public interface PlayerFileWriter {

  /**
   * Writes the data of a single player to the specified file.
   *
   * @param player the Player object containing the player's data to be written
   * @param filePath the path to the file where the player's data will be written
   */
  void writePlayer(Player player, Path filePath);
}
