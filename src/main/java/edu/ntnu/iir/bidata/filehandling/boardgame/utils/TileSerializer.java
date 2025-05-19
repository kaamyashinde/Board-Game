package edu.ntnu.iir.bidata.filehandling.boardgame.utils;

import com.google.gson.*;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import java.lang.reflect.Type;

public class TileSerializer implements JsonSerializer<Tile>, JsonDeserializer<Tile> {
  @Override
  public JsonElement serialize(Tile src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("id", src.getId());
    // Only serialize the next tile's ID instead of the whole object
    if (src.getNextTile() != null) {
      jsonObject.addProperty("nextTileId", src.getNextTile().getId());
    }
    // Add other properties as needed
    if (src.getAction() != null) {
      jsonObject.add("action", context.serialize(src.getAction()));
    }
    return jsonObject;
  }

  @Override
  public Tile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    int id = jsonObject.get("id").getAsInt();
    return new Tile(id);
  }
}