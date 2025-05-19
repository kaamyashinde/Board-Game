package edu.ntnu.iir.bidata.filehandling.boardgame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.iir.bidata.filehandling.boardgame.utils.TileSerializer;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Implementation of the BoardGameFileWriter interface using Gson for JSON serialization.
 * This class provides the functionality to write board game data to a JSON file.
 */
public class BoardGameFileWriterGson implements BoardGameFileWriter {
  private final Gson gson;

  public BoardGameFileWriterGson() {
    this.gson =
        new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Tile.class, new TileSerializer())
            .create();
  }

  /**
   * Writes a board game to a JSON file. Depending on the value of {@code isMonopoly},
   * the method either serializes the board game with custom logic for Monopoly or writes
   * a standard JSON representation of the board game.
   *
   * @param boardGame the board game instance to write to a JSON file
   * @param path the path to the file where the JSON output will be saved
   * @param isMonopoly a flag indicating whether the board game is a Monopoly game,
   *        requiring custom serialization
   * @throws IOException if writing to the file fails
   */
  @Override
  public void writeBoardGame(BoardGame boardGame, Path path, boolean isMonopoly)
      throws IOException {
    if (isMonopoly) {
      writeMonopolyGameToJson(boardGame, path);
    } else {
      String json = gson.toJson(boardGame);
      Files.writeString(path, json);
    }
  }

  /**
   * Serializes a Monopoly board game to JSON format and writes it to the specified file path.
   * The method simplifies the board game structure for serialization, including details about tiles,
   * players, and the current game state.
   *
   * @param boardGame the Monopoly board game instance to be serialized
   * @param path the file path where the JSON representation of the game will be saved
   * @throws IOException if an I/O error occurs while writing to the file
   */
  private void writeMonopolyGameToJson(BoardGame boardGame, Path path) throws IOException {
    // Create a simplified version of the board game for serialization
    Map<String, Object> simplifiedGame = new HashMap<>();
    simplifiedGame.put("dice", boardGame.getDice());


    // Serialize essential tile data
    Map<String, Map<String, Object>> tilesData = new HashMap<>();
    for (Map.Entry<Integer, Tile> entry : boardGame.getBoard().getTiles().entrySet()) {
      Tile tile = entry.getValue();
      Map<String, Object> tileData = new HashMap<>();
      tileData.put("id", tile.getId());
      tileData.put("type", tile.getClass().getSimpleName());
      if (tile instanceof PropertyTile) {
        PropertyTile propertyTile = (PropertyTile) tile;
        tileData.put("price", propertyTile.getPrice());
        tileData.put("rent", propertyTile.getRent());
        tileData.put("group", propertyTile.getGroup());
        tileData.put(
            "owner", propertyTile.getOwner() != null ? propertyTile.getOwner().getName() : null);
      }
      tilesData.put(String.valueOf(tile.getId()), tileData);
    }
    Map<String, Object> boardData = new HashMap<>();
    boardData.put("tiles", tilesData);
    boardData.put("sizeOfBoard", boardGame.getBoard().getSizeOfBoard());

    simplifiedGame.put("board", boardData);
    
    // Serialize player data
    List<Map<String, Object>> playersData = new ArrayList<>();
    for (Player player : boardGame.getPlayers()) {
      Map<String, Object> playerData = new HashMap<>();
      playerData.put("name", player.getName());
      playerData.put("money", ((SimpleMonopolyPlayer) player).getMoney());
      playerData.put("position", player.getCurrentTile().getId());
      playersData.add(playerData);
    }
    simplifiedGame.put("players", playersData);

    // Serialize current player index
    simplifiedGame.put("currentPlayerIndex", boardGame.getCurrentPlayerIndex());

    // Convert to JSON and write to file
    String json = gson.toJson(simplifiedGame);
    Files.writeString(path, json);
  }
}
