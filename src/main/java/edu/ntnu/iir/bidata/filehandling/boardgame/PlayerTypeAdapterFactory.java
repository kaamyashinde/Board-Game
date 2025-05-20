package edu.ntnu.iir.bidata.filehandling.boardgame;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import java.lang.reflect.Type;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class PlayerTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!Player.class.isAssignableFrom(type.getRawType())) {
            return null;
        }
        return (TypeAdapter<T>) new TypeAdapter<Player>() {
            @Override
            @SuppressWarnings("unchecked")
            public void write(JsonWriter out, Player value) throws java.io.IOException {
                TypeAdapter<Player> delegate = (TypeAdapter<Player>) gson.getDelegateAdapter(PlayerTypeAdapterFactory.this, TypeToken.get(value.getClass()));
                delegate.write(out, value);
            }

            @Override
            public Player read(JsonReader in) throws java.io.IOException {
                JsonObject obj = JsonParser.parseReader(in).getAsJsonObject();
                String playerType = obj.has("playerType") ? obj.get("playerType").getAsString() : "";
                TypeAdapter<? extends Player> delegate;
                if ("MONOPOLY".equalsIgnoreCase(playerType)) {
                    delegate = gson.getDelegateAdapter(PlayerTypeAdapterFactory.this, TypeToken.get(SimpleMonopolyPlayer.class));
                } else {
                    delegate = gson.getDelegateAdapter(PlayerTypeAdapterFactory.this, TypeToken.get(Player.class));
                }
                return delegate.fromJsonTree(obj);
            }
        };
    }
} 