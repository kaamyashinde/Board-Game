package edu.ntnu.iir.bidata.filehandling.player;

import edu.ntnu.iir.bidata.model.player.Player;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import  java.util.ArrayList;
import  java.util.List;

/**
 * A concrete implementation of the PlayerFileReader interface for reading player information from
 * CSV files. This class provides functionality to read a single player or multiple players from a
 * CSV file and stream.
 *
 * <p>Each line in the CSV file is expected to contain the player's name followed by an optional
 * token image, separated by a comma.
 */
public class PlayerFileReaderCSV implements PlayerFileReader {

  /**
   * Reads a player's information from the specified CSV file. Each line in the file should contain
   * the player's name and optionally the token image, separated by a comma. If the first line of
   * the file is empty or null, the method returns null.
   *
   * @param filePath the path to the CSV file containing player data
   * @return a Player object containing the extracted name and token image, or null if the file is
   *     empty or the first line is invalid
   * @throws RuntimeException if an I/O error occurs while reading the file
   */
  @Override
  public Player readPlayer(Path filePath) {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
      String line = reader.readLine();
      if (line != null && !line.isEmpty()) {
        String[] parts = line.split(",", 2);
        String name = parts[0].trim();
        String tokenImage = parts.length > 1 ? parts[1].trim() : null;
        return new Player(name, tokenImage);
      }
      return null;
    } catch (IOException e) {
      throw new RuntimeException("Failed to read player from CSV file", e);
    }
  }

  /**
   * Reads a list of players from a specified CSV file. Each line in the file is expected to contain
   * the player's name and optionally a token image, separated by a comma. Empty lines in the file
   * are ignored.
   *
   * @param filePath the path to the CSV file containing player data
   * @return a list of Player objects representing the players read from the file
   * @throws RuntimeException if an I/O error occurs while reading the file
   */
  @Override
  public List<Player> readPlayers(Path filePath) {
    List<Player> players = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (!line.trim().isEmpty()) {
          String[] parts = line.split(",", 2);
          String name = parts[0].trim();
          String tokenImage = parts.length > 1 ? parts[1].trim() : null;
          players.add(new Player(name, tokenImage));
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to read players from CSV file", e);
    }
    return players;
  }

  /**
   * Reads all players from a CSV input stream (one name per line). This method is useful for
   * reading from resources or other input sources.
   *
   * @param inputStream the input stream containing CSV data
   * @return a list of Player objects
   */
  public  List<Player> readPlayersFromInputStream(InputStream inputStream) {
     List<Player> players = new  ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (!line.trim().isEmpty()) {
          String[] parts = line.split(",", 2);
          String name = parts[0].trim();
          String tokenImage = parts.length > 1 ? parts[1].trim() : null;
          players.add(new Player(name, tokenImage));
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to read players from input stream", e);
    }
    return players;
  }
}
