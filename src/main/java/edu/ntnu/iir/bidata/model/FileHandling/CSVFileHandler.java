package edu.ntnu.iir.bidata.model.FileHandling;

import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.exception.GameException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * file handler class for saving players to and loading players from CSV files.
 *
 * @author Durva
 * @version 1.0.0
 */

public class CSVFileHandler {
  private static final String CSV_DELIMITER = ",";
  private static final String NEWLINE = "\n";
  //so that if you ever need to change the delimiter or the newline character
  //if you're working with a different file format or operating system
  // you only need to update the constant in one place

  /**
   * Saves the list of players to a CSV file.
   * The first line is a header ("PlayerName,CurrentPosition").
   * Each subsequent line contains a player's name and current tile ID.
   *
   * @param players  List of players to save
   * @param filePath Path to the CSV file
   * @throws GameException if file operations fail
   */
  public static void savePlayersToCSV(List<Player> players, String filePath) {
    Objects.requireNonNull(players, "Players list cannot be null");
    Objects.requireNonNull(filePath, "File path cannot be null");

    try (FileWriter writer = new FileWriter(filePath)) {
      // Write header
      writer.append("PlayerName,CurrentPosition").append(NEWLINE);

      // Write each player's data
      for (Player player : players) {
        String name = player.getName();
        String tileId = "";
        if (player.getCurrentTile() != null) {
          tileId = String.valueOf(player.getCurrentTile().getId());
        }
        writer.append(name)
            .append(CSV_DELIMITER)
            .append(tileId)
            .append(NEWLINE);
      }
      writer.flush();
    } catch (IOException e) {
      throw new GameException("Failed to save players to CSV: " + e.getMessage(), e);
    }
  }

  /**
   * Loads player data from a CSV file.
   * The method expects the file to have a header line.
   * For each subsequent line, it creates a Player and uses the provided Board to set the current tile.
   *
   * @param filePath Path to the CSV file
   * @param board    Board instance for looking up tiles
   * @return List of loaded players
   * @throws GameException if file operations fail
   */
  public static List<Player> loadPlayersFromCSV(String filePath, Board board) {
    Objects.requireNonNull(filePath, "File path cannot be null");
    Objects.requireNonNull(board, "Board cannot be null");

    List<Player> players = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line = reader.readLine(); // Skip header

      while ((line = reader.readLine()) != null) {
        String[] data = line.split(CSV_DELIMITER);
        if (data.length >= 2) {
          String playerName = data[0].trim();
          int position = Integer.parseInt(data[1].trim());

          Player player = new Player(playerName);
          player.placeOnTile(board.getPositionOnBoard(position));
          players.add(player);
        }
      }
      return players;
    } catch (IOException | NumberFormatException e) {
      throw new GameException("Failed to load players from CSV: " + e.getMessage(), e);
    }
  }

}
