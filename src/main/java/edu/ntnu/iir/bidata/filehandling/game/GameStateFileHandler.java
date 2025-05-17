package edu.ntnu.iir.bidata.filehandling.game;

import java.nio.file.Path;

public interface GameStateFileHandler<T> {
    void writeGameState(T gameState, Path path);
    T readGameState(Path path);
} 