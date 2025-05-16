package edu.ntnu.iir.bidata.filehandling.boardgame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.iir.bidata.model.BoardGame;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class BoardGameFileWriterGson implements BoardGameFileWriter {
    @Override
    public void writeBoardGame(BoardGame boardGame, Path path) {
        try (FileWriter writer = new FileWriter(path.toFile())) {
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapterFactory(new TileActionTypeAdapterFactory())
                .create();
            gson.toJson(boardGame, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write board game to file", e);
        }
    }
} 