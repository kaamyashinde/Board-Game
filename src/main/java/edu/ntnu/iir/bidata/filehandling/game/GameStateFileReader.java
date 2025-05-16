package edu.ntnu.iir.bidata.filehandling.game;

import edu.ntnu.iir.bidata.model.game.GameState;
import java.nio.file.Path;

public interface GameStateFileReader {
    GameState readGameState(Path path);
} 