package edu.ntnu.iir.bidata.filehandling.boardgame;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;

/**
 * A custom implementation of the TypeAdapterFactory designed to handle serialization and
 * deserialization of Player objects or subclasses of Player. This factory creates a TypeAdapter
 * that adapts the JSON representation based on the specific type of Player being processed.
 *
 * <p>The factory supports distinguishing between different subclasses of Player, such as a
 * SimpleMonopolyPlayer, based on the "playerType" field in the JSON data. If "playerType" is set to
 * "MONOPOLY", the deserialization process uses the SimpleMonopolyPlayer type; otherwise, it
 * defaults to the general Player type.
 */
public class PlayerTypeAdapterFactory implements TypeAdapterFactory {

  /**
   * Creates a {@link TypeAdapter} for the specified type if it matches the {@link Player} class or
   * its subclasses. Customizes the serialization and deserialization process for Player objects
   * based on their JSON structure and type information.
   *
   * @param <T> the type of the object for which the {@link TypeAdapter} is being created
   * @param gson the Gson instance used for serialization and deserialization
   * @param type the {@link TypeToken} representing the type for which the adapter is being created
   * @return a {@link TypeAdapter} for handling the given type if it is assignable to {@link
   *     Player}, otherwise returns null
   */
  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    if (!Player.class.isAssignableFrom(type.getRawType())) {
      return null;
    }
    return (TypeAdapter<T>)
        new TypeAdapter<Player>() {
          @Override
          @SuppressWarnings("unchecked")
          public void write(JsonWriter out, Player value) throws java.io.IOException {
            TypeAdapter<Player> delegate =
                (TypeAdapter<Player>)
                    gson.getDelegateAdapter(
                        PlayerTypeAdapterFactory.this, TypeToken.get(value.getClass()));
            delegate.write(out, value);
          }

          @Override
          public Player read(JsonReader in) throws java.io.IOException {
            JsonObject obj = JsonParser.parseReader(in).getAsJsonObject();
            String playerType = obj.has("playerType") ? obj.get("playerType").getAsString() : "";
            TypeAdapter<? extends Player> delegate;
            if ("MONOPOLY".equalsIgnoreCase(playerType)) {
              delegate =
                  gson.getDelegateAdapter(
                      PlayerTypeAdapterFactory.this, TypeToken.get(SimpleMonopolyPlayer.class));
            } else {
              delegate =
                  gson.getDelegateAdapter(
                      PlayerTypeAdapterFactory.this, TypeToken.get(Player.class));
            }
            return delegate.fromJsonTree(obj);
          }
        };
  }
}
