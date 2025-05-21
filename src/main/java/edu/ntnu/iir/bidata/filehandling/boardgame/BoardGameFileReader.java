package edu.ntnu.iir.bidata.filehandling.boardgame;

import edu.ntnu.iir.bidata.model.BoardGame;
import java.nio.file.Path;
import java.io.IOException;

public interface BoardGameFileReader {
    BoardGame readBoardGame(Path path) throws IOException;
} 