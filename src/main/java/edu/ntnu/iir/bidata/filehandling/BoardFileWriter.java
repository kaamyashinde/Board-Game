package edu.ntnu.iir.bidata.filehandling;

import java.nio.file.Path;

import edu.ntnu.iir.bidata.model.board.Board;

public interface BoardFileWriter {
    public void writeBoard(Board board, Path filePath);
}
