package edu.ntnu.iir.bidata.filehandling.boardgame;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import edu.ntnu.iir.bidata.model.tile.actions.base.GoToTileAction;
import edu.ntnu.iir.bidata.model.tile.actions.base.SafeSpotAction;
import edu.ntnu.iir.bidata.model.tile.actions.game.LoseTurnAction;
import edu.ntnu.iir.bidata.model.tile.actions.game.SwitchPositionAction;
import edu.ntnu.iir.bidata.model.tile.actions.movement.EntryPointAction;
import edu.ntnu.iir.bidata.model.tile.actions.movement.HopFiveStepsAction;
import edu.ntnu.iir.bidata.model.tile.actions.snakeandladder.LadderAction;
import edu.ntnu.iir.bidata.model.tile.actions.snakeandladder.SnakeAction;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class TileActionTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!TileAction.class.isAssignableFrom(type.getRawType())) {
            return null;
        }

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", value.getClass().getSimpleName());

                if (value instanceof GoToTileAction) {
                    jsonObject.addProperty("targetTileId", ((GoToTileAction) value).getTargetTileId());
                } else if (value instanceof LadderAction) {
                    jsonObject.addProperty("topTileId", ((LadderAction) value).getTopTileId());
                } else if (value instanceof SnakeAction) {
                    jsonObject.addProperty("tailTileId", ((SnakeAction) value).getTailTileId());
                } else if (value instanceof EntryPointAction) {
                    // For EntryPointAction, we need to handle the Player reference
                    // This assumes Player has proper serialization
                    jsonObject.add("owner", gson.toJsonTree(((EntryPointAction) value).getOwner()));
                } else if (value instanceof SwitchPositionAction) {
                    // For SwitchPositionAction, we need to handle the List<Player> reference
                    // This assumes Player has proper serialization
                    jsonObject.add("allPlayers", gson.toJsonTree(((SwitchPositionAction) value).getAllPlayers()));
                }
                // SafeSpotAction, LoseTurnAction, and HopFiveStepsAction don't need extra fields

                gson.toJson(jsonObject, out);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                JsonObject jsonObject = gson.fromJson(in, JsonObject.class);
                if (jsonObject == null) {
                    return null;
                }

                String type = jsonObject.get("type").getAsString();
                switch (type) {
                    case "GoToTileAction":
                        return (T) new GoToTileAction(jsonObject.get("targetTileId").getAsInt());
                    case "LadderAction":
                        return (T) new LadderAction(jsonObject.get("topTileId").getAsInt());
                    case "SnakeAction":
                        return (T) new SnakeAction(jsonObject.get("tailTileId").getAsInt());
                    case "EntryPointAction":
                        return (T) new EntryPointAction(gson.fromJson(jsonObject.get("owner"), edu.ntnu.iir.bidata.model.Player.class));
                    case "SwitchPositionAction":
                        Type listType = new TypeToken<List<edu.ntnu.iir.bidata.model.Player>>(){}.getType();
                        return (T) new SwitchPositionAction(gson.fromJson(jsonObject.get("allPlayers"), listType));
                    case "SafeSpotAction":
                        return (T) new SafeSpotAction();
                    case "LoseTurnAction":
                        return (T) new LoseTurnAction();
                    case "HopFiveStepsAction":
                        return (T) new HopFiveStepsAction();
                    default:
                        throw new JsonParseException("Unknown TileAction type: " + type);
                }
            }
        };
    }
} 