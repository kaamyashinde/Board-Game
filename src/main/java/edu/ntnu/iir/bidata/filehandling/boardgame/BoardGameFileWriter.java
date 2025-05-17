package edu.ntnu.iir.bidata.filehandling.boardgame;

import edu.ntnu.iir.bidata.model.BoardGame;
import java.nio.file.Path;
import java.io.IOException;

public interface BoardGameFileWriter {
    void writeBoardGame(BoardGame boardGame, Path path) throws IOException;
} 