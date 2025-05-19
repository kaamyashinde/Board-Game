package edu.ntnu.iir.bidata.filehandling.boardgame.utils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import java.lang.reflect.Type;

public class BoardDeserializer implements JsonDeserializer<Board> {
    @Override
    public Board deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {

        JsonObject boardObject = json.getAsJsonObject();
        int boardSize = boardObject.has("sizeOfBoard") ? boardObject.get("sizeOfBoard").getAsInt() : 0;

        Board board = new Board(boardSize);

        if (boardObject.has("tiles")) {
            JsonObject tilesObject = boardObject.getAsJsonObject("tiles");
            for (String key : tilesObject.keySet()) {
                JsonObject tileJson = tilesObject.getAsJsonObject(key);
                Tile tile = context.deserialize(tileJson, Tile.class);
                board.addTile(tile);
            }
        }

        return board;
    }
}

