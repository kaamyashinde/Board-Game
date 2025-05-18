package edu.ntnu.iir.bidata.filehandling.boardgame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.iir.bidata.model.BoardGame;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class BoardGameFileReaderGson implements BoardGameFileReader {
    private final Gson gson;

    public BoardGameFileReaderGson() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    }

    @Override
    public BoardGame readBoardGame(Path path) throws IOException {
        String json = Files.readString(path);
        return gson.fromJson(json, BoardGame.class);
    }
} 