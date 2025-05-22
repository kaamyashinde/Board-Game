package edu.ntnu.iir.bidata.filehandling.boardgame;

import edu.ntnu.iir.bidata.model.BoardGame;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Defines the contract for reading a BoardGame object from a file. Implementations of this
 * interface are responsible for handling specific file formats and parsing logic.
 */
public interface BoardGameFileReader {

  /**
   * Reads and parses a BoardGame object from a specified file. The method processes the file
   * located at the provided path and converts its content into a valid BoardGame object. The format
   * and parsing logic depend on the implementation.
   *
   * @param path the path to the file containing the board game data
   * @return a BoardGame object parsed from the file
   * @throws IOException if an I/O error occurs while reading the file
   */
  BoardGame readBoardGame(Path path) throws IOException;
}
