package edu.ntnu.iir.bidata.filehandling.boardgame;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.actions.base.GoToTileAction;
import edu.ntnu.iir.bidata.model.tile.actions.base.SafeSpotAction;
import edu.ntnu.iir.bidata.model.tile.actions.game.LoseTurnAction;
import edu.ntnu.iir.bidata.model.tile.actions.game.SwitchPositionAction;
import edu.ntnu.iir.bidata.model.tile.actions.monopoly.CollectMoneyAction;
import edu.ntnu.iir.bidata.model.tile.actions.monopoly.GoToJailAction;
import edu.ntnu.iir.bidata.model.tile.actions.movement.EntryPointAction;
import edu.ntnu.iir.bidata.model.tile.actions.movement.HopFiveStepsAction;
import edu.ntnu.iir.bidata.model.tile.actions.snakeandladder.LadderAction;
import edu.ntnu.iir.bidata.model.tile.actions.snakeandladder.SnakeAction;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A factory class for creating custom Gson TypeAdapters for serializing and deserializing
 * implementations of the {@code TileAction} interface. This factory ensures that the proper
 * concrete class adapter is utilized based on the runtime type of the {@code TileAction} instance.
 *
 * <p>The primary role of this class is to handle polymorphic serialization and deserialization by
 * inspecting the type field within the JSON representation of a {@code TileAction} object. Specific
 * subclasses of {@code TileAction} (e.g., {@code GoToTileAction}, {@code GoToJailAction}, etc.) are
 * handled through conditional logic during serialization and deserialization.
 *
 * <p>During serialization, this factory embeds a "type" field in the JSON representation,
 * indicating the specific subclass type of {@code TileAction}. Additional fields specific to the
 * subclass may also be added.
 *
 * <p>During deserialization, this factory reads the "type" field from the JSON and instantiates the
 * corresponding concrete class of {@code TileAction} by mapping the type string to its respective
 * implementation and initializing it with fields found within the JSON object.
 *
 * <p>Unsupported or unknown {@code TileAction} types will throw a {@code JsonParseException} during
 * deserialization.
 *
 * <p>This factory supports the following {@code TileAction} subclasses:
 *
 * <ul>
 *   <li>GoToTileAction
 *   <li>GoToJailAction
 *   <li>LadderAction
 *   <li>SnakeAction
 *   <li>EntryPointAction
 *   <li>SwitchPositionAction
 *   <li>SafeSpotAction
 *   <li>LoseTurnAction
 *   <li>HopFiveStepsAction
 *   <li>CollectMoneyAction
 * </ul>
 *
 * <p>It assumes proper serialization and deserialization of related objects like {@code Player} and
 * other dependent classes used within the {@code TileAction} implementations.
 *
 * <p>Implements the {@code TypeAdapterFactory} interface from Gson to register a custom adapter.
 */
public class TileActionTypeAdapterFactory implements TypeAdapterFactory {

  /**
   * Creates a custom {@link TypeAdapter} for serializing and deserializing objects of type {@link
   * TileAction}. This method generates a specialized adapter for handling various implementations
   * of the {@link TileAction} interface during JSON conversion.
   *
   * @param <T> the type of the object for which the {@link TypeAdapter} is being created
   * @param gson the {@link Gson} instance used for serialization and deserialization
   * @param type the {@link TypeToken} defining the type of the object to be handled
   * @return a {@link TypeAdapter} for the given type if it is assignable from {@link TileAction},
   *     or null otherwise
   */
  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    if (!TileAction.class.isAssignableFrom(type.getRawType())) {
      return null;
    }

    return new TypeAdapter<T>() {
      /**
       * Serializes the given value of type {@code T} into JSON using the provided {@link
       * JsonWriter}. This method converts the object into a {@link JsonObject} by adding
       * type-specific data based on the runtime class of the value.
       *
       * @param out the {@link JsonWriter} used to write the JSON representation of the value
       * @param value the object of type {@code T} to be serialized into JSON
       * @throws IOException if an I/O error occurs during writing to the {@link JsonWriter}
       */
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
        } else if (value instanceof GoToJailAction) {
          jsonObject.addProperty("jailTileId", ((GoToJailAction) value).getJailTileId());
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
          jsonObject.add(
              "allPlayers", gson.toJsonTree(((SwitchPositionAction) value).getAllPlayers()));
        } else if (value instanceof CollectMoneyAction) {
          // No extra fields needed
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
          case "GoToJailAction":
            int jailTileId =
                jsonObject.has("jailTileId") ? jsonObject.get("jailTileId").getAsInt() : 10;
            return (T) new GoToJailAction(jailTileId);
          case "LadderAction":
            return (T) new LadderAction(jsonObject.get("topTileId").getAsInt());
          case "SnakeAction":
            return (T) new SnakeAction(jsonObject.get("tailTileId").getAsInt());
          case "EntryPointAction":
            return (T) new EntryPointAction(gson.fromJson(jsonObject.get("owner"), Player.class));
          case "SwitchPositionAction":
            Type listType = new TypeToken<List<Player>>() {}.getType();
            return (T)
                new SwitchPositionAction(gson.fromJson(jsonObject.get("allPlayers"), listType));
          case "SafeSpotAction":
            return (T) new SafeSpotAction();
          case "LoseTurnAction":
            return (T) new LoseTurnAction();
          case "HopFiveStepsAction":
            return (T) new HopFiveStepsAction();
          case "CollectMoneyAction":
            return (T) new CollectMoneyAction();
          default:
            throw new JsonParseException("Unknown TileAction type: " + type);
        }
      }
    };
  }
}
