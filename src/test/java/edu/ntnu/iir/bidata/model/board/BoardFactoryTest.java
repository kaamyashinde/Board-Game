package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardFactoryTest {

    @Mock
    private Player mockPlayer1;
    @Mock
    private Player mockPlayer2;
    @Mock
    private Player mockPlayer3;
    @Mock
    private Player mockPlayer4;

    private List<Player> players;
    private static final int STANDARD_BOARD_SIZE = 20;
    private static final int SNAKES_AND_LADDERS_BOARD_SIZE = 100;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        players = new ArrayList<>();
        players.add(new Player("Player1"));
        players.add(new Player("Player2"));
    }

    @Test
    void createStandardBoard_WithValidParameters_ShouldCreateBoard() {
        Board board = BoardFactory.createStandardBoard(STANDARD_BOARD_SIZE, players);
        
        assertNotNull(board);
        assertEquals(STANDARD_BOARD_SIZE, board.getSizeOfBoard());
        assertNotNull(board.getStartingTile());
        assertNotNull(board.getEndingTile());
        
        // Verify special tiles
        assertNotNull(board.getTile(3)); // HopFiveStepsAction
        assertNotNull(board.getTile(7)); // GoToTileAction
        assertNotNull(board.getTile(5)); // LoseTurnAction
        assertNotNull(board.getTile(15)); // SwitchPositionAction
    }

    @Test
    void createStandardBoard_WithInvalidSize_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
            () -> BoardFactory.createStandardBoard(0, players));
    }

    @Test
    void createSnakesAndLaddersBoard_WithValidParameters_ShouldCreateBoard() {
        Board board = BoardFactory.createSnakesAndLaddersBoard(SNAKES_AND_LADDERS_BOARD_SIZE, players);
        
        assertNotNull(board);
        assertEquals(SNAKES_AND_LADDERS_BOARD_SIZE, board.getSizeOfBoard());
        assertNotNull(board.getStartingTile());
        assertNotNull(board.getEndingTile());
        
        // Verify all tiles are connected
        for (int i = 0; i < SNAKES_AND_LADDERS_BOARD_SIZE - 1; i++) {
            assertTrue(board.isValidTileConnection(i, i + 1));
        }
    }

    @Test
    void createSnakesAndLaddersBoard_WithInvalidSize_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
            () -> BoardFactory.createSnakesAndLaddersBoard(0, players));
    }

} 