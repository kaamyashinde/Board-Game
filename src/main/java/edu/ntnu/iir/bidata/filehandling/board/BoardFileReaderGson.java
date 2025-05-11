package edu.ntnu.iir.bidata.filehandling.board;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.tile.GoToTileAction;
import edu.ntnu.iir.bidata.model.tile.HopFiveStepsAction;
import edu.ntnu.iir.bidata.model.tile.LoseTurnAction;
import edu.ntnu.iir.bidata.model.tile.SwitchPositionAction;
import edu.ntnu.iir.bidata.model.tile.TileAction;
import edu.ntnu.iir.bidata.model.tile.snakeandladder.LadderAction;
import edu.ntnu.iir.bidata.model.tile.snakeandladder.SnakeAction;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class BoardFileReaderGson implements BoardFileReader {

  @Override
  public Board readBoard(Path path) {
    try (FileReader reader = new FileReader(path.toFile())) {
      JsonObject boardJson = JsonParser.parseReader(reader).getAsJsonObject();
      int boardSize = boardJson.get("boardSize").getAsInt();
      Board board = new Board(boardSize);

      JsonArray tilesArray = boardJson.getAsJsonArray("tiles");
      for (int i = 0; i < tilesArray.size(); i++) {
        JsonObject tileJson = tilesArray.get(i).getAsJsonObject();
        int id = tileJson.get("id").getAsInt();

        TileAction action = null;
        if (tileJson.has("action") && !tileJson.get("action").isJsonNull()) {
          action = deserializeTileAction(tileJson.getAsJsonObject("action"));
        }
        board.addTile(id, action);
      }
      // Optionally, connect tiles if needed (not handled here)
      return board;
    } catch (IOException e) {
      throw new RuntimeException("Failed to read board from file: " + path, e);
    }
  }

  private TileAction deserializeTileAction(JsonObject actionJson) {
    String type = actionJson.get("type").getAsString();
    // Add more logic if your TileAction subclasses have extra fields
    switch (type) {
      case "GoToTileAction":
        return new GoToTileAction(actionJson.get("targetTileId").getAsInt());
      case "HopFiveStepsAction":
        return new HopFiveStepsAction();
      case "LoseTurnAction":
        return new LoseTurnAction();
      case "SwitchPositionAction":
        return new SwitchPositionAction(null); // Pass actual player list if needed
      case "LadderAction":
        return new LadderAction(actionJson.get("topTileId").getAsInt());
      case "SnakeAction":
        return new SnakeAction(actionJson.get("tailTileId").getAsInt());
      default:
        return null;
    }
  }
}
