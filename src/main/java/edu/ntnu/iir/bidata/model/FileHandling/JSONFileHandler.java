package edu.ntnu.iir.bidata.model.FileHandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.ui.GameUI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * file handler class for saving a hard-coded board game to
 * and loading a board game from JSON files.
 *
 * @author Durva
 * @version 1.0.0
 */

public class JSONFileHandler {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    //whenever the mapper writes JSON, it will format the output with indentation and line breaks,
    // making the JSON easier to read for humans.
  }

  /**
   * Inner class representing the game state for JSON serialization.
   */
  public static class GameState {
    private List<PlayerData> players;
    private int boardSize;
    private int currentPlayerIndex;
    private boolean playing;

    // Getters and setters
    public List<PlayerData> getPlayers() { return players; }
    public void setPlayers(List<PlayerData> players) { this.players = players; }
    public int getBoardSize() { return boardSize; }
    public void setBoardSize(int boardSize) { this.boardSize = boardSize; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public void setCurrentPlayerIndex(int currentPlayerIndex) { this.currentPlayerIndex = currentPlayerIndex; }
    public boolean isPlaying() { return playing; }
    public void setPlaying(boolean playing) { this.playing = playing; }
  }

  /**
   * Inner class representing player data for JSON serialization.
   */
  public static class PlayerData {
    private String name;
    private int position;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
  }

  /**
   * Saves the entire game state to a JSON file.
   *
   * @param game     The BoardGame instance to save
   * @param filePath Path to the JSON file
   * @throws GameException if file operations fail
   */
  public static void saveGameToJson(BoardGame game, String filePath) {
    Objects.requireNonNull(game, "Game cannot be null");
    Objects.requireNonNull(filePath, "File path cannot be null");

    try {
      GameState gameState = new GameState();
      gameState.setBoardSize(game.getBoard().getTiles().size());
      gameState.setPlaying(game.isPlaying());

      List<PlayerData> playerDataList = new ArrayList<>();
      int currentPlayerIndex = 0;
      int index = 0;
      for (Map.Entry<Player, Integer> entry : game.getPlayers().entrySet()) {
        Player player = entry.getKey();
        PlayerData playerData = new PlayerData();
        playerData.setName(player.getName());
        playerData.setPosition(player.getCurrentTile().getId());
        playerDataList.add(playerData);

        if (player.equals(game.getCurrentPlayer())) {
          currentPlayerIndex = index;
        }
        index++;
      }
      gameState.setPlayers(playerDataList);
      gameState.setCurrentPlayerIndex(currentPlayerIndex);

      objectMapper.writeValue(new File(filePath), gameState);
    } catch (IOException e) {
      throw new GameException("Failed to save game to JSON: " + e.getMessage(), e);
    }
  }

  /**
   * Loads a game state from a JSON file and returns a new BoardGame instance with that state.
   *
   * @param filePath Path to the JSON file
   * @param ui       GameUI implementation to use for the new game
   * @return The loaded BoardGame instance
   * @throws GameException if file operations fail
   */
  public static BoardGame loadGameFromJson(String filePath, GameUI ui) {
    Objects.requireNonNull(filePath, "File path cannot be null");
    Objects.requireNonNull(ui, "UI cannot be null");

    try {
      GameState gameState = objectMapper.readValue(new File(filePath), GameState.class);

      // Create a new BoardGame using the saved board size and number of players
      BoardGame game = new BoardGame(1, gameState.getPlayers().size(), gameState.getBoardSize(), ui);

      // Add players from saved data
      for (PlayerData playerData : gameState.getPlayers()) {
        Player player = new Player(playerData.getName());
        game.addPlayer(player);
        player.placeOnTile(game.getBoard().getPositionOnBoard(playerData.getPosition()));
      }

      // Initialize the game state
      game.initialiseGame();
      game.setPlaying(gameState.isPlaying());

      // Set the current player based on saved index
      if (gameState.getPlayers().size() > 0) {
        int currentPlayerIndex = gameState.getCurrentPlayerIndex();
        if (currentPlayerIndex >= 0 && currentPlayerIndex < gameState.getPlayers().size()) {
          String currentPlayerName = gameState.getPlayers().get(currentPlayerIndex).getName();
          for (Player player : game.getPlayers().keySet()) {
            if (player.getName().equals(currentPlayerName)) {
              game.setCurrentPlayer(player);
              break;
            }
          }
        }
      }
      return game;
    } catch (IOException e) {
      throw new GameException("Failed to load game from JSON: " + e.getMessage(), e);
    }
  }




}
