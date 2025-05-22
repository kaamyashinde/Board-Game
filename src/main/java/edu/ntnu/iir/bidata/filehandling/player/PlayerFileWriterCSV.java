package edu.ntnu.iir.bidata.filehandling.player;

import edu.ntnu.iir.bidata.model.player.Player;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Implementation of the PlayerFileWriter interface for writing player data to a CSV file. This
 * class provides methods for saving a single player's data or a list of players to a specified
 * file.
 */
public class PlayerFileWriterCSV implements PlayerFileWriter {
  @Override
  public void writePlayer(Player player, Path filePath) {
    try (FileWriter writer = new FileWriter(filePath.toFile())) {
      writer.write(
          player.getName()
              + ","
              + (player.getTokenImage() != null ? player.getTokenImage() : "")
              + "\n");
    } catch (IOException e) {
      throw new RuntimeException("Failed to write player to CSV file", e);
    }
  }
}
