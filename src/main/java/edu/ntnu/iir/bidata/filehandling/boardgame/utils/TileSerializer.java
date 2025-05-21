package edu.ntnu.iir.bidata.filehandling.boardgame.utils;

import com.google.gson.*;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.GoTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.JailTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.FreeParkingTile;
import edu.ntnu.iir.bidata.model.tile.actions.monopoly.GoToJailAction;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import java.lang.reflect.Type;

public class TileSerializer implements JsonSerializer<Tile>, JsonDeserializer<Tile> {
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

  @Override
  public Tile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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