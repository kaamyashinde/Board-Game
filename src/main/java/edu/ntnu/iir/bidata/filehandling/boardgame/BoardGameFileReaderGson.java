package edu.ntnu.iir.bidata.filehandling.boardgame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.ntnu.iir.bidata.filehandling.boardgame.utils.BoardDeserializer;
import edu.ntnu.iir.bidata.filehandling.boardgame.utils.TileSerializer;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import edu.ntnu.iir.bidata.filehandling.boardgame.TileActionTypeAdapterFactory;
import edu.ntnu.iir.bidata.Inject;

/**
 * Class to read a board game from a JSON file using Gson.
 * This class implements the BoardGameFileReader interface.
 * It uses Gson to parse the JSON file and create a BoardGame object.
 * It also handles the deserialization of tile connections.
 */
public class BoardGameFileReaderGson implements BoardGameFileReader {
  private final Gson gson;

  @Inject
  public BoardGameFileReaderGson() {
    this.gson = new GsonBuilder()
    .setPrettyPrinting()
    .registerTypeAdapter(Tile.class, new TileSerializer())
    .registerTypeAdapter(Board.class, new BoardDeserializer()) // Register custom deserializer
    .registerTypeAdapterFactory(new PlayerTypeAdapterFactory()) // Register custom player deserializer
    .registerTypeAdapterFactory(new TileActionTypeAdapterFactory()) // Register TileAction type adapter
    .create();
  }


  /**
   * Reads a board game from a JSON file.
   *
   * @param path the path to the JSON file
   * @return the BoardGame object
   * @throws IOException if an I/O error occurs
   */
  @Override
  public BoardGame readBoardGame(Path path) throws IOException {
    String json = Files.readString(path);
    JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

    // First, deserialize the board game without tile connections
    BoardGame boardGame = gson.fromJson(jsonObject, BoardGame.class);
    System.out.println("Board size after deserialization: " + boardGame.getBoard().getSizeOfBoard());


    String memberNameBoard = "board";
    // Then, reconstruct the tile connections
    if (jsonObject.has(memberNameBoard)
        && jsonObject.getAsJsonObject(memberNameBoard).has("tiles")) {
      JsonObject tilesObject = jsonObject.getAsJsonObject(memberNameBoard).getAsJsonObject("tiles");
      Map<Integer, Tile> tileMap = new HashMap<>();

      // First pass: collect all tiles
      collectTilesFromBoardGameJsonFile(tilesObject, boardGame, tileMap);

      // Second pass: establish connections
      connectTilesFromBoardGameJsonFile(tilesObject, tileMap);

      // Third pass: set each player's currentTile to the correct Tile instance from the board
      if (jsonObject.has("players")) {
        for (var playerElement : jsonObject.getAsJsonArray("players")) {
          JsonObject playerObj = playerElement.getAsJsonObject();
          if (playerObj.has("currentTile")) {
            JsonObject currentTileObj = playerObj.getAsJsonObject("currentTile");
            int tileId = currentTileObj.get("id").getAsInt();
            // Find the player in the boardGame's player list by name
            String playerName = playerObj.get("name").getAsString();
            boardGame.getPlayers().stream()
                .filter(p -> p.getName().equals(playerName))
                .findFirst()
                .ifPresent(p -> {
                  p.setCurrentTile(boardGame.getBoard().getTile(tileId));
                  if (playerObj.has("tokenImage")) {
                    p.setTokenImage(playerObj.get("tokenImage").getAsString());
                  }
                });
          }
        }
      }
    }

    return boardGame;
  }

  /**
   * Collects tiles from the JSON file and adds them to the board game.
   *
   * @param tilesObject the JSON object containing tile information
   * @param boardGame the BoardGame object
   * @param tileMap a map of tile IDs to Tile objects
   */
  private static void collectTilesFromBoardGameJsonFile(
      JsonObject tilesObject, BoardGame boardGame, Map<Integer, Tile> tileMap) {
    for (String key : tilesObject.keySet()) {
      JsonObject tileJson = tilesObject.getAsJsonObject(key);
      int id = tileJson.get("id").getAsInt();
      Tile tile = boardGame.getBoard().getTile(id);
      if (tile != null) {
        tileMap.put(id, tile);
      }
    }
  }

  /**
   * Connects tiles based on the nextTileId field in the JSON file.
   *
   * @param tilesObject the JSON object containing tile information
   * @param tileMap a map of tile IDs to Tile objects
   */
  private static void connectTilesFromBoardGameJsonFile(
      JsonObject tilesObject, Map<Integer, Tile> tileMap) {
    for (String key : tilesObject.keySet()) {
      JsonObject tileJson = tilesObject.getAsJsonObject(key);
      int id = tileJson.get("id").getAsInt();
      if (tileJson.has("nextTileId")) {
        int nextTileId = tileJson.get("nextTileId").getAsInt();
        Tile tile = tileMap.get(id);
        Tile nextTile = tileMap.get(nextTileId);
        if (tile != null && nextTile != null) {
          tile.setNextTile(nextTile);
        }
      }
    }
  }
}
