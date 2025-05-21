package edu.ntnu.iir.bidata.filehandling.board;

import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.board.MonopolyBoardFactory;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.GoTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.JailTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.FreeParkingTile;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriterGson;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonopolyBoardFactoryTest {
    
  
    @Test
    void testPropertyGroups() {
        Board board = MonopolyBoardFactory.createBoard();
        
        // Test property groups
        for (int i = 1; i <= 8; i++) {
            assertNotNull(board.getTile(i));
        }
        
        for (int i = 11; i <= 19; i++) {
            assertNotNull(board.getTile(i));
        }
    }

    @Test
    public void testWriteMonopolyBoardGame() throws Exception {
        // Create a Monopoly board game
        BoardGame boardGame = new BoardGame(MonopolyBoardFactory.createBoard(), 1);
        boardGame.setPlayers(List.of(new SimpleMonopolyPlayer("Player 1"), new SimpleMonopolyPlayer("Player 2")));

        // Create a temporary file path
        Path tempFilePath = Files.createTempFile("monopoly_board_game", ".json");

        // Write the board game to the file
        BoardGameFileWriterGson writer = new BoardGameFileWriterGson();
        writer.writeBoardGame(boardGame, tempFilePath, true);

        // Assert that the file was created and is not empty
        assertTrue(Files.exists(tempFilePath));
        assertTrue(Files.size(tempFilePath) > 0);

        // Clean up the temporary file
        Files.deleteIfExists(tempFilePath);
    }
} 