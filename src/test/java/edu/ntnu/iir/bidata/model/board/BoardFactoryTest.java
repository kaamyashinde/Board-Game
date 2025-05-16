package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.exception.GameException;
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
        players.add(mockPlayer1);
        players.add(mockPlayer2);
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

    @Test
    void createLudoBoard_WithTwoPlayers_ShouldCreateBoard() {
        Board board = BoardFactory.createLudoBoard(players);
        
        assertNotNull(board);
        assertEquals(52, board.getSizeOfBoard()); // Standard Ludo board size
        
        // Verify safe spots (every 13th tile)
        for (int i = 0; i < 52; i += 13) {
            Tile tile = board.getTile(i);
            assertNotNull(tile);
            assertNotNull(tile.getAction());
        }
        
        // Verify entry points
        assertNotNull(board.getTile(0).getAction()); // Red player entry
        assertNotNull(board.getTile(13).getAction()); // Green player entry
    }

    @Test
    void createLudoBoard_WithThreePlayers_ShouldCreateBoard() {
        players.add(mockPlayer3);
        Board board = BoardFactory.createLudoBoard(players);
        
        assertNotNull(board);
        assertNotNull(board.getTile(26).getAction()); // Yellow player entry
    }

    @Test
    void createLudoBoard_WithFourPlayers_ShouldCreateBoard() {
        players.add(mockPlayer3);
        players.add(mockPlayer4);
        Board board = BoardFactory.createLudoBoard(players);
        
        assertNotNull(board);
        assertNotNull(board.getTile(39).getAction()); // Blue player entry
    }

    @Test
    void createLudoBoard_WithInvalidPlayerCount_ShouldThrowException() {
        // Test with 1 player
        List<Player> onePlayer = new ArrayList<>();
        onePlayer.add(new Player("P1"));
        assertThrows(GameException.class, () -> BoardFactory.createLudoBoard(onePlayer));
        
        // Test with 5 players
        List<Player> fivePlayers = new ArrayList<>();
        fivePlayers.add(new Player("P1"));
        fivePlayers.add(new Player("P2"));
        fivePlayers.add(new Player("P3"));
        fivePlayers.add(new Player("P4"));
        fivePlayers.add(new Player("P5"));
        assertThrows(GameException.class, () -> BoardFactory.createLudoBoard(fivePlayers));
    }

    @Test
    void createLudoBoard_ShouldHaveCircularTrack() {
        Board board = BoardFactory.createLudoBoard(players);
        
        // Verify last tile connects to first tile
        assertTrue(board.isValidTileConnection(51, 0));
    }
} 