package edu.ntnu.iir.bidata.filehandling.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.iir.bidata.model.game.GameState;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class GameStateFileWriterGson implements GameStateFileWriter {
    @Override
    public void writeGameState(GameState gameState, Path path) {
        try (FileWriter writer = new FileWriter(path.toFile())) {
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
            gson.toJson(gameState, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write game state to file", e);
        }
    }
} 