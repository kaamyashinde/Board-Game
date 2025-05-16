package edu.ntnu.iir.bidata.filehandling.game;

import com.google.gson.Gson;
import edu.ntnu.iir.bidata.model.game.GameState;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class GameStateFileReaderGson implements GameStateFileReader {
    @Override
    public GameState readGameState(Path path) {
        try (FileReader reader = new FileReader(path.toFile())) {
            Gson gson = new Gson();
            return gson.fromJson(reader, GameState.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read game state from file", e);
        }
    }
} 