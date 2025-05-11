package edu.ntnu.iir.bidata.filehandling.board;

import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.tile.GoToTileAction;
import edu.ntnu.iir.bidata.model.tile.HopFiveStepsAction;
import edu.ntnu.iir.bidata.model.tile.LoseTurnAction;
import edu.ntnu.iir.bidata.model.tile.TileAction;
import edu.ntnu.iir.bidata.model.tile.snakeandladder.LadderAction;
import edu.ntnu.iir.bidata.model.tile.snakeandladder.SnakeAction;
import edu.ntnu.iir.bidata.model.tile.SwitchPositionAction;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BoardFileGsonTest {

    @Test
    void testWriteAndReadBoard() throws Exception {
        // Create a board with various actions
        Board board = new Board(8);
        board.addTile(0, null);
        board.addTile(1, new HopFiveStepsAction());
        board.addTile(2, new GoToTileAction(4));
        board.addTile(3, new LoseTurnAction());
        board.addTile(4, new LadderAction(7));
        board.addTile(5, new SnakeAction(2));
        board.addTile(6, new SwitchPositionAction(Collections.emptyList()));
        board.addTile(7, null);

        // Write to file
        Path tempFile = Files.createTempFile("test-board", ".json");
        new BoardFileWriterGson().writeBoard(board, tempFile);

        // Read from file
        Board loaded = new BoardFileReaderGson().readBoard(tempFile);

        // Check board size
        assertEquals(board.getSizeOfBoard(), loaded.getSizeOfBoard());

        // Check tile actions
        for (int i = 0; i < board.getSizeOfBoard(); i++) {
            TileAction expected = board.getTile(i).getAction();
            TileAction actual = loaded.getTile(i).getAction();
            if (expected == null) {
                assertNull(actual);
            } else {
                assertEquals(expected.getClass(), actual.getClass());
                if (expected instanceof GoToTileAction) {
                    assertEquals(
                        ((GoToTileAction) expected).getTargetTileId(),
                        ((GoToTileAction) actual).getTargetTileId()
                    );
                } else if (expected instanceof LadderAction) {
                    assertEquals(
                        ((LadderAction) expected).getTopTileId(),
                        ((LadderAction) actual).getTopTileId()
                    );
                } else if (expected instanceof SnakeAction) {
                    assertEquals(
                        ((SnakeAction) expected).getTailTileId(),
                        ((SnakeAction) actual).getTailTileId()
                    );
                }
            }
        }
        Files.deleteIfExists(tempFile);
    }
} 