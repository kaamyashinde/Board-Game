package edu.ntnu.iir.bidata.filehandling.boardgame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.iir.bidata.model.BoardGame;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class BoardGameFileReaderGson implements BoardGameFileReader {
    @Override
    public BoardGame readBoardGame(Path path) {
        try (FileReader reader = new FileReader(path.toFile())) {
            Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new TileActionTypeAdapterFactory())
                .create();
            return gson.fromJson(reader, BoardGame.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read board game from file", e);
        }
    }
} 