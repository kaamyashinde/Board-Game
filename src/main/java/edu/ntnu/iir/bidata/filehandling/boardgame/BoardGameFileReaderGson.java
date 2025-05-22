package edu.ntnu.iir.bidata.filehandling.boardgame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.ntnu.iir.bidata.Inject;
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
import java.util.logging.Logger;

/**
 * Class to read a board game from a JSON file using Gson. This class implements the
 * BoardGameFileReader interface. It uses Gson to parse the JSON file and create a BoardGame object.
 * It also handles the deserialization of tile connections.
 */
public class BoardGameFileReaderGson implements BoardGameFileReader {
  private static final Logger LOGGER = Logger.getLogger(BoardGameFileReaderGson.class.getName());
  private final Gson gson;

  /**
   * Constructs a new instance of BoardGameFileReaderGson with custom serialization and
   * deserialization configurations using the Gson library.
   *
   * <p>This constructor initializes a Gson instance with the following features:
   * <li>Pretty printing for readable JSON output.
   * <li>A custom serializer for the Tile class.
   * <li>A custom deserializer for the Board class.
   * <li>Type adapter factories for Player and TileAction objects to extend handling for these
   *     types.
   *
   *     <p>The constructor is annotated with {@code @Inject}, indicating it supports dependency
   *     injection, allowing the class to be managed and instantiated by a dependency injection
   *     framework.
   */
  @Inject
  public BoardGameFileReaderGson() {
    this.gson =
        new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Tile.class, new TileSerializer())
            .registerTypeAdapter(
                Board.class, new BoardDeserializer()) // Register custom deserializer
            .registerTypeAdapterFactory(
                new PlayerTypeAdapterFactory()) // Register custom player deserializer
            .registerTypeAdapterFactory(
                new TileActionTypeAdapterFactory()) // Register TileAction type adapter
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
    LOGGER.info("Board size after deserialization: " + boardGame.getBoard().getSizeOfBoard());

    // Set the level if present
    if (jsonObject.has("level")) {
      boardGame.setLevel(jsonObject.get("level").getAsString());
    } else {
      boardGame.setLevel("medium");
    }

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
      setPlayerCurrentTileToCorrectTileInstance(jsonObject, boardGame);
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
    tilesObject
        .keySet()
        .forEach(
            key -> {
              JsonObject tileJson = tilesObject.getAsJsonObject(key);
              int id = tileJson.get("id").getAsInt();
              Tile tile = boardGame.getBoard().getTile(id);
              if (tile != null) {
                tileMap.put(id, tile);
              }
            });
  }

  /**
   * Connects tiles based on the nextTileId field in the JSON file.
   *
   * @param tilesObject the JSON object containing tile information
   * @param tileMap a map of tile IDs to Tile objects
   */
  private static void connectTilesFromBoardGameJsonFile(
      JsonObject tilesObject, Map<Integer, Tile> tileMap) {
    tilesObject
        .keySet()
        .forEach(
            key -> {
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
            });
  }

  /**
   * Updates the current tile of each player in the board game to the correct tile instance based on
   * the JSON object provided. Additionally, sets the player's token image if present in the JSON
   * object.
   *
   * @param jsonObject the JSON object containing player and tile information
   * @param boardGame the BoardGame instance to be updated with the correct tile instances
   */
  private static void setPlayerCurrentTileToCorrectTileInstance(
      JsonObject jsonObject, BoardGame boardGame) {
    if (jsonObject.has("players")) {
      jsonObject
          .getAsJsonArray("players")
          .forEach(
              playerElement -> {
                JsonObject playerObj = playerElement.getAsJsonObject();
                if (playerObj.has("currentTile")) {
                  JsonObject currentTileObj = playerObj.getAsJsonObject("currentTile");
                  int tileId = currentTileObj.get("id").getAsInt();
                  // Find the player in the boardGame's player list by name
                  String playerName = playerObj.get("name").getAsString();
                  boardGame.getPlayers().stream()
                      .filter(p -> p.getName().equals(playerName))
                      .findFirst()
                      .ifPresent(
                          p -> {
                            p.setCurrentTile(boardGame.getBoard().getTile(tileId));
                            if (playerObj.has("tokenImage")) {
                              p.setTokenImage(playerObj.get("tokenImage").getAsString());
                            }
                          });
                }
              });
    }
  }
}
