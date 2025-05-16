package edu.ntnu.iir.bidata.filehandling.game;

import edu.ntnu.iir.bidata.model.game.GameState;
import java.nio.file.Path;

public interface GameStateFileWriter {
    void writeGameState(GameState gameState, Path path);
} 