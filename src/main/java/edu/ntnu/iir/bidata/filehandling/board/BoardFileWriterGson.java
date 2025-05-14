package edu.ntnu.iir.bidata.filehandling.board;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.tile.Tile;
import edu.ntnu.iir.bidata.model.tile.TileAction;
import edu.ntnu.iir.bidata.model.tile.GoToTileAction;
import edu.ntnu.iir.bidata.model.tile.snakeandladder.LadderAction;
import edu.ntnu.iir.bidata.model.tile.snakeandladder.SnakeAction;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class BoardFileWriterGson implements BoardFileWriter {

  @Override
  public void writeBoard(Board board, Path path) {
    JsonObject boardJson = new JsonObject();
    boardJson.addProperty("boardSize", board.getSizeOfBoard());

    JsonArray tilesArray = new JsonArray();
    for (Map.Entry<Integer, Tile> entry : board.getTiles().entrySet()) {
      Tile tile = entry.getValue();
      JsonObject tileJson = new JsonObject();
      tileJson.addProperty("id", tile.getId());

      // Serialize the TileAction if present
      tileJson.add("action",
          (tile.getAction() != null) ? serializeTileAction(tile.getAction()) : null);
      tilesArray.add(tileJson);
    }
    boardJson.add("tiles", tilesArray);

    try (FileWriter writer = new FileWriter(path.toFile())) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(boardJson, writer);
    } catch (IOException e) {
        throw new RuntimeException("Failed to write board to file", e);
    }
  }

  private JsonObject serializeTileAction(TileAction action) {
    JsonObject actionJson = new JsonObject();
    actionJson.addProperty("type", action.getClass().getSimpleName());
    if (action instanceof GoToTileAction) {
      actionJson.addProperty("targetTileId", ((GoToTileAction) action).getTargetTileId());
    } else if (action instanceof LadderAction) {
      actionJson.addProperty("topTileId", ((LadderAction) action).getTopTileId());
    } else if (action instanceof SnakeAction) {
      actionJson.addProperty("tailTileId", ((SnakeAction) action).getTailTileId());
    }
    // No extra fields for HopFiveStepsAction, LoseTurnAction, SwitchPositionAction
    return actionJson;
  }
}
