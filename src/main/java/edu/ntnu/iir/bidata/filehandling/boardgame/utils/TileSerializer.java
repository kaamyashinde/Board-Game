package edu.ntnu.iir.bidata.filehandling.boardgame.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.FreeParkingTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.GoTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.JailTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import java.lang.reflect.Type;

/**
 * A serializer and deserializer for the Tile class and its subclasses using Gson.
 *
 * <p>This class implements the JsonSerializer and JsonDeserializer interfaces to provide custom
 * behavior for serializing and deserializing Tile objects into and from JSON format.
 */
public class TileSerializer implements JsonSerializer<Tile>, JsonDeserializer<Tile> {

  /**
   * Serializes a Tile object into its JSON representation.
   *
   * @param src The Tile object to serialize.
   * @param typeOfSrc The type of the source object to be serialized.
   * @param context The JSON serialization context for custom serialization.
   * @return A JsonElement representing the serialized form of the Tile.
   */
  @Override
  public JsonElement serialize(Tile src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("id", src.getId());
    jsonObject.addProperty("type", src.getClass().getSimpleName());
    if (src.getNextTile() != null) {
      jsonObject.addProperty("nextTileId", src.getNextTile().getId());
    }
    if (src instanceof PropertyTile propertyTile) {
      jsonObject.addProperty("price", propertyTile.getPrice());
      jsonObject.addProperty("rent", propertyTile.getRent());
      jsonObject.addProperty("group", propertyTile.getGroup());
      if (propertyTile.getOwner() != null) {
        jsonObject.addProperty("owner", propertyTile.getOwner().getName());
      }
    }
    // Add other properties as needed
    if (src.getAction() != null) {
      jsonObject.add("action", context.serialize(src.getAction()));
    }
    return jsonObject;
  }

  /**
   * Deserializes a JSON element into a Tile object, selecting the appropriate subclass based on the
   * "type" property in the JSON data. If the "type" is not specified, a default Tile object is
   * created. Certain subclasses may require additional properties to be present in the JSON data
   * (e.g., "price" and "rent" for PropertyTile).
   *
   * @param json The JSON element to deserialize.
   * @param typeOfT The type of the object to deserialize to.
   * @param context The deserialization context, which can be used to deserialize nested objects.
   * @return A Tile or a subclass of Tile, instantiated based on the JSON data.
   * @throws JsonParseException If the JSON data is malformed or required fields are missing.
   */
  @Override
  public Tile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    int id = jsonObject.get("id").getAsInt();
    String type = jsonObject.has("type") ? jsonObject.get("type").getAsString() : "Tile";
    TileAction action = null;
    if (jsonObject.has("action")) {
      action = context.deserialize(jsonObject.get("action"), TileAction.class);
    }
    Tile tile;
    switch (type) {
      case "PropertyTile":
        int price = jsonObject.get("price").getAsInt();
        int rent = jsonObject.get("rent").getAsInt();
        int group = jsonObject.get("group").getAsInt();
        tile = new PropertyTile(id, price, rent, group, action);
        // Owner will be set after all players are loaded, if needed
        break;
      case "GoTile":
        tile = new GoTile(id); // GoTile sets its own action
        break;
      case "JailTile":
        tile = new JailTile(id); // JailTile sets its own action (null)
        break;
      case "FreeParkingTile":
        tile = new FreeParkingTile(id); // FreeParkingTile sets its own action (none)
        break;
      default:
        tile = (action != null) ? new Tile(id, action) : new Tile(id);
    }
    return tile;
  }
}
