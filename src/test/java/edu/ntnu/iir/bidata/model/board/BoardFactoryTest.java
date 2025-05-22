package edu.ntnu.iir.bidata.model.board;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.config.TileConfiguration;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
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
    @Mock
    private TileConfiguration mockTileConfiguration;

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
    void testCreateStandardBoard_WithValidParameters_ShouldCreateBoard() {
        Board board = BoardFactory.createStandardBoard(STANDARD_BOARD_SIZE, players);

        assertNotNull(board);
        assertEquals(STANDARD_BOARD_SIZE, board.getSizeOfBoard());
        assertNotNull(board.getStartingTile());
        assertNotNull(board.getEndingTile());

        assertNotNull(board.getTile(3).getAction());
        assertNotNull(board.getTile(7).getAction());
        assertNotNull(board.getTile(5).getAction());
        assertNotNull(board.getTile(15).getAction());

        assertNull(board.getTile(0).getAction());
        assertNull(board.getTile(1).getAction());
        assertNull(board.getTile(2).getAction());
    }

    @Test
    void testCreateStandardBoard_WithMinimumSize_ShouldCreateBoard() {
        Board board = BoardFactory.createStandardBoard(16, players);

        assertNotNull(board);
        assertEquals(16, board.getSizeOfBoard());

        assertNotNull(board.getTile(3).getAction());
        assertNotNull(board.getTile(7).getAction());
        assertNotNull(board.getTile(5).getAction());
        assertNotNull(board.getTile(15).getAction());
    }

    @Test
    void testCreateStandardBoard_WithEmptyPlayerList_ShouldCreateBoard() {
        List<Player> emptyPlayers = new ArrayList<>();

        Board board = BoardFactory.createStandardBoard(STANDARD_BOARD_SIZE, emptyPlayers);

        assertNotNull(board);
        assertEquals(STANDARD_BOARD_SIZE, board.getSizeOfBoard());
    }

    @Test
    void testCreateStandardBoard_WithSinglePlayer_ShouldCreateBoard() {
        List<Player> singlePlayer = Arrays.asList(new Player("Solo"));

        Board board = BoardFactory.createStandardBoard(STANDARD_BOARD_SIZE, singlePlayer);

        assertNotNull(board);
        assertEquals(STANDARD_BOARD_SIZE, board.getSizeOfBoard());
    }

    @Test
    void testCreateSnakesAndLaddersBoard_WithValidParameters_ShouldCreateBoard() {
        Board board = BoardFactory.createSnakesAndLaddersBoard(SNAKES_AND_LADDERS_BOARD_SIZE, players);

        assertNotNull(board);
        assertEquals(SNAKES_AND_LADDERS_BOARD_SIZE, board.getSizeOfBoard());
        assertNotNull(board.getStartingTile());
        assertNotNull(board.getEndingTile());

        for (int i = 0; i < SNAKES_AND_LADDERS_BOARD_SIZE - 1; i++) {
            assertTrue(board.isValidTileConnection(i, i + 1));
        }
    }

    @Test
    void testCreateSnakesAndLaddersBoard_WithCustomConfiguration_ShouldCreateBoard() {
        Board board = BoardFactory.createSnakesAndLaddersBoard(SNAKES_AND_LADDERS_BOARD_SIZE, players, mockTileConfiguration);

        assertNotNull(board);
        assertEquals(SNAKES_AND_LADDERS_BOARD_SIZE, board.getSizeOfBoard());
        assertNotNull(board.getStartingTile());
        assertNotNull(board.getEndingTile());
    }

    @Test
    void testCreateSnakesAndLaddersBoard_WithDefaultConfiguration_ShouldCreateBoard() {
        Board board = BoardFactory.createSnakesAndLaddersBoard(50, players);

        assertNotNull(board);
        assertEquals(50, board.getSizeOfBoard());
    }

    @Test
    void testCreateLudoBoard_WithTwoPlayers_ShouldCreateBoard() {
        List<Player> twoPlayers = Arrays.asList(new Player("Player1"), new Player("Player2"));

        Board board = BoardFactory.createLudoBoard(twoPlayers);

        assertNotNull(board);
        assertEquals(52, board.getSizeOfBoard());

        assertNotNull(board.getTile(0).getAction());
        assertNotNull(board.getTile(13).getAction());

        assertTrue(board.isValidTileConnection(51, 0));
    }

    @Test
    void testCreateLudoBoard_WithThreePlayers_ShouldCreateBoard() {
        List<Player> threePlayers = Arrays.asList(
            new Player("Player1"),
            new Player("Player2"),
            new Player("Player3")
        );

        Board board = BoardFactory.createLudoBoard(threePlayers);

        assertNotNull(board);
        assertEquals(52, board.getSizeOfBoard());

        assertNotNull(board.getTile(0).getAction());
        assertNotNull(board.getTile(13).getAction());
        assertNotNull(board.getTile(26).getAction());
    }

    @Test
    void testCreateLudoBoard_WithFourPlayers_ShouldCreateBoard() {
        List<Player> fourPlayers = Arrays.asList(
            new Player("Player1"),
            new Player("Player2"),
            new Player("Player3"),
            new Player("Player4")
        );

        Board board = BoardFactory.createLudoBoard(fourPlayers);

        assertNotNull(board);
        assertEquals(52, board.getSizeOfBoard());

        assertNotNull(board.getTile(0).getAction());
        assertNotNull(board.getTile(13).getAction());
        assertNotNull(board.getTile(26).getAction());
        assertNotNull(board.getTile(39).getAction());
    }

    @Test
    void testCreateLudoBoard_WithOnePlayer_ShouldThrowException() {
        List<Player> onePlayer = Arrays.asList(new Player("Solo"));

        GameException exception = assertThrows(GameException.class,
            () -> BoardFactory.createLudoBoard(onePlayer));
        assertEquals("Ludo requires 2-4 players", exception.getMessage());
    }

    @Test
    void testCreateLudoBoard_WithFivePlayers_ShouldThrowException() {
        List<Player> fivePlayers = Arrays.asList(
            new Player("Player1"),
            new Player("Player2"),
            new Player("Player3"),
            new Player("Player4"),
            new Player("Player5")
        );

        GameException exception = assertThrows(GameException.class,
            () -> BoardFactory.createLudoBoard(fivePlayers));
        assertEquals("Ludo requires 2-4 players", exception.getMessage());
    }

    @Test
    void testCreateLudoBoard_WithEmptyPlayerList_ShouldThrowException() {
        List<Player> emptyPlayers = new ArrayList<>();

        GameException exception = assertThrows(GameException.class,
            () -> BoardFactory.createLudoBoard(emptyPlayers));
        assertEquals("Ludo requires 2-4 players", exception.getMessage());
    }

    @Test
    void testCreateLudoBoard_SafeSpots_ShouldBeCorrectlyPlaced() {
        List<Player> players = Arrays.asList(new Player("Player1"), new Player("Player2"));

        Board board = BoardFactory.createLudoBoard(players);

        assertNotNull(board.getTile(0).getAction());
        assertNotNull(board.getTile(13).getAction());
        assertNotNull(board.getTile(26).getAction());
        assertNotNull(board.getTile(39).getAction());
    }

    @Test
    void testCreateLudoBoard_AllTilesConnected_ShouldBeValid() {
        List<Player> players = Arrays.asList(new Player("Player1"), new Player("Player2"));

        Board board = BoardFactory.createLudoBoard(players);

        for (int i = 0; i < 52; i++) {
            int nextTile = (i + 1) % 52;
            assertTrue(board.isValidTileConnection(i, nextTile),
                "Connection between tile " + i + " and " + nextTile + " should be valid");
        }
    }

    @Test
    void testBoardFactory_StaticMethodsWork() {
        List<Player> testPlayers = Arrays.asList(new Player("Test1"), new Player("Test2"));

        assertDoesNotThrow(() -> {
            Board standardBoard = BoardFactory.createStandardBoard(10, testPlayers);
            assertNotNull(standardBoard);

            Board snakesBoard = BoardFactory.createSnakesAndLaddersBoard(10, testPlayers);
            assertNotNull(snakesBoard);

            Board ludoBoard = BoardFactory.createLudoBoard(testPlayers);
            assertNotNull(ludoBoard);
        });
    }

    @Test
    void testCreateStandardBoard_TileConnections_ShouldBeValid() {
        Board board = BoardFactory.createStandardBoard(STANDARD_BOARD_SIZE, players);

        for (int i = 0; i < STANDARD_BOARD_SIZE - 1; i++) {
            assertTrue(board.isValidTileConnection(i, i + 1),
                "Connection between tile " + i + " and " + (i + 1) + " should be valid");
        }
    }

    @Test
    void testCreateSnakesAndLaddersBoard_TileConnections_ShouldBeValid() {
        Board board = BoardFactory.createSnakesAndLaddersBoard(50, players);

        for (int i = 0; i < 49; i++) {
            assertTrue(board.isValidTileConnection(i, i + 1),
                "Connection between tile " + i + " and " + (i + 1) + " should be valid");
        }
    }
}