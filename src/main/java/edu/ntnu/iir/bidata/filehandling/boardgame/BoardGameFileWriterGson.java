package edu.ntnu.iir.bidata.filehandling.boardgame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.iir.bidata.filehandling.boardgame.utils.TileSerializer;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class BoardGameFileWriterGson implements BoardGameFileWriter {
    private final Gson gson;

    public BoardGameFileWriterGson() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Tile.class, new TileSerializer())
            .create();
    }

    @Override
    public void writeBoardGame(BoardGame boardGame, Path path) throws IOException {
        String json = gson.toJson(boardGame);
        Files.writeString(path, json);
    }

} 