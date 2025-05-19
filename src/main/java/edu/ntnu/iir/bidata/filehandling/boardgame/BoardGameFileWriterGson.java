package edu.ntnu.iir.bidata.filehandling.boardgame;
 
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.iir.bidata.Main;
import edu.ntnu.iir.bidata.filehandling.boardgame.utils.TileSerializer;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
 
public class BoardGameFileWriterGson implements BoardGameFileWriter {
  private final Gson gson;
  private static final Logger LOGGER = Logger.getLogger(BoardGameFileWriterGson.class.getName());
 
 
  public BoardGameFileWriterGson() {
    this.gson =
        new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Tile.class, new TileSerializer())
            .create();
  }
 
  @Override
  public void writeBoardGame(BoardGame boardGame, Path path, boolean isMonopoly) throws IOException {
    if (isMonopoly){
        writeMonopolyGameToJson(boardGame, path);
    } else {
        String json = gson.toJson(boardGame);
        Files.writeString(path, json);
    }
    /*LOGGER.info("board game is of " + boardGame.getCurrentPlayer().getClass() + " class");
   if (boardGame.getCurrentPlayer().getClass().isInstance(SimpleMonopolyPlayer.class)) {
     LOGGER.info("Writing " + boardGame.getCurrentPlayer().getClass() + " game to JSON file: " + path.toAbsolutePath());
     writeMonopolyGameToJson(boardGame, path);
   } else {
     String json = gson.toJson(boardGame);
     Files.writeString(path, json);
   }*/
  }
 
  private void writeMonopolyGameToJson(BoardGame boardGame, Path path) throws IOException {
    // Create a simplified version of the board game for serialization
    Map<String, Object> simplifiedGame = new HashMap<>();
 
    // Serialize essential tile data
    List<Map<String, Object>> tilesData = new ArrayList<>();
    for (Map.Entry<Integer, Tile> entry : boardGame.getBoard().getTiles().entrySet()) {
      Tile tile = entry.getValue();
      Map<String, Object> tileData = new HashMap<>();
      tileData.put("id", tile.getId());
      tileData.put("type", tile.getClass().getSimpleName());
      if (tile instanceof PropertyTile) {
        PropertyTile propertyTile = (PropertyTile) tile;
        tileData.put("price", propertyTile.getPrice());
        tileData.put("rent", propertyTile.getRent());
        tileData.put("group", propertyTile.getGroup());
        tileData.put(
            "owner", propertyTile.getOwner() != null ? propertyTile.getOwner().getName() : null);
      }
      tilesData.add(tileData);
    }
    simplifiedGame.put("tiles", tilesData);
 
    // Serialize player data
    List<Map<String, Object>> playersData = new ArrayList<>();
    for (Player player : boardGame.getPlayers()) {
      Map<String, Object> playerData = new HashMap<>();
      playerData.put("name", player.getName());
      playerData.put("money", ((SimpleMonopolyPlayer) player).getMoney());
      playerData.put("position", player.getCurrentTile().getId());
      playersData.add(playerData);
    }
    simplifiedGame.put("players", playersData);
 
    // Serialize current player index
    simplifiedGame.put("currentPlayerIndex", boardGame.getCurrentPlayerIndex());
 
    // Convert to JSON and write to file
    String json = gson.toJson(simplifiedGame);
    Files.writeString(path, json);
  }
}