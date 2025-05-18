package edu.ntnu.iir.bidata.filehandling.board;

import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.board.MonopolyBoardFactory;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.GoTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.JailTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.FreeParkingTile;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
} 