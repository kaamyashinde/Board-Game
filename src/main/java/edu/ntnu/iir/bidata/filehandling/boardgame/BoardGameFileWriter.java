package edu.ntnu.iir.bidata.filehandling.boardgame;

import edu.ntnu.iir.bidata.model.BoardGame;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface representing a writer for board game data to files. Implementations of this interface
 * are responsible for serializing board game instances and writing them to a specified file path.
 */
public interface BoardGameFileWriter {

  /**
   * Writes the given board game data to the specified file path.
   *
   * @param boardGame the instance of the board game to be written
   * @param path the file path where the board game data should be saved
   * @param isMonopoly a flag indicating if the board game is Monopoly
   * @throws IOException if an I/O error occurs while writing to the file
   */
  void writeBoardGame(BoardGame boardGame, Path path, boolean isMonopoly) throws IOException;
}
