package edu.ntnu.iir.bidata.filehandling.boardgame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.iir.bidata.model.BoardGame;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class BoardGameFileWriterGson implements BoardGameFileWriter {
    private final Gson gson;

    public BoardGameFileWriterGson() {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    }

    @Override
    public void writeBoardGame(BoardGame boardGame, Path path) throws IOException {
        String json = gson.toJson(boardGame);
        Files.writeString(path, json);
    }

} 