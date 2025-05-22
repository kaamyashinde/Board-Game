package edu.ntnu.iir.bidata.filehandling.boardgame.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import java.lang.reflect.Type;

/**
 * A custom deserializer for converting JSON data into a {@link Board} object. This class implements
 * the {@link JsonDeserializer} interface and is utilized by the Gson library during deserialization
 * of JSON into a Board instance.
 *
 * <p>The deserialization process includes extracting the board size and tiles from the given JSON
 * and populating the {@link Board} object accordingly.
 */
public class BoardDeserializer implements JsonDeserializer<Board> {

  /**
   * Deserializes a JSON element into a Board object.
   *
   * <p>This method reads the board size and tiles from the given JSON element and constructs a
   * corresponding Board instance. The tiles are deserialized as individual Tile objects and added
   * to the board.
   *
   * @param json The JSON data being deserialized
   * @param typeOfT The type of the Object to deserialize to
   * @param context Context for deserialization, used to deserialize custom objects
   * @return A Board object populated with the deserialized data
   * @throws JsonParseException If the JSON is not in the expected format or cannot be deserialized
   */
  @Override
  public Board deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {

    JsonObject boardObject = json.getAsJsonObject();
    int boardSize = boardObject.has("boardSize") ? boardObject.get("boardSize").getAsInt() : 0;

    Board board = new Board(boardSize);

    if (boardObject.has("tiles")) {
      JsonObject tilesObject = boardObject.getAsJsonObject("tiles");
      tilesObject
          .keySet()
          .forEach(
              key -> {
                JsonObject tileJson = tilesObject.getAsJsonObject(key);
                Tile tile = context.deserialize(tileJson, Tile.class);
                board.addTile(tile);
              });
    }

    return board;
  }
}
