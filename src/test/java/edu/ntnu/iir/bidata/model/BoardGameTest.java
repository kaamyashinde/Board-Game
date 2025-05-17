package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.dice.Dice;
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

class BoardGameTest {

    @Mock
    private Board mockBoard;
    @Mock
    private Dice mockDice;
    @Mock
    private Tile mockTile;
    @Mock
    private Tile mockStartingTile;
    @Mock
    private TileAction mockAction;
    @Mock
    private Observer mockObserver;

    private BoardGame boardGame;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        boardGame = new BoardGame(mockBoard, mockDice);
        players = new ArrayList<>();
        when(mockBoard.getStartingTile()).thenReturn(mockStartingTile);
    }

    @Test
    void constructor_WithValidParameters_ShouldInitializeGame() {
        assertNotNull(boardGame);
        assertFalse(boardGame.isGameOver());
        assertFalse(boardGame.isGameInitialized());
        assertEquals(1, boardGame.getRoundNumber());
    }

    @Test
    void addPlayer_WithValidName_ShouldAddPlayer() {
        assertTrue(boardGame.addPlayer("Player1"));
        assertEquals(1, boardGame.getPlayers().size());
        assertEquals("Player1", boardGame.getPlayers().get(0).getName());
    }

    @Test
    void addPlayer_WithNullName_ShouldNotAddPlayer() {
        assertFalse(boardGame.addPlayer(null));
        assertTrue(boardGame.getPlayers().isEmpty());
    }

    @Test
    void addPlayer_WithEmptyName_ShouldNotAddPlayer() {
        assertFalse(boardGame.addPlayer(""));
        assertTrue(boardGame.getPlayers().isEmpty());
    }

    @Test
    void startGame_WithNoPlayers_ShouldThrowException() {
        GameException exception = assertThrows(
            GameException.class,
            () -> boardGame.startGame()
        );
        assertEquals("Cannot start game with no players", exception.getMessage());
    }

    @Test
    void startGame_WithNullStartingTile_ShouldThrowException() {
        when(mockBoard.getStartingTile()).thenReturn(null);
        boardGame.addPlayer("Player1");
        
        GameException exception = assertThrows(
            GameException.class,
            () -> boardGame.startGame()
        );
        assertEquals("Board is not properly initialized - starting tile is null", exception.getMessage());
    }

    @Test
    void startGame_WithValidSetup_ShouldInitializeGame() {
        boardGame.addPlayer("Player1");
        boardGame.addPlayer("Player2");
        
        boardGame.startGame();
        
        assertTrue(boardGame.isGameInitialized());
        assertFalse(boardGame.isGameOver());
        assertEquals(0, boardGame.getCurrentPlayerIndex());
    }

    @Test
    void makeMoveWithResult_WhenGameNotInitialized_ShouldThrowException() {
        GameException exception = assertThrows(
            GameException.class,
            () -> boardGame.makeMoveWithResult()
        );
        assertEquals("Game has not been started. Call startGame() first.", exception.getMessage());
    }

    @Test
    void makeMoveWithResult_WhenGameOver_ShouldReturnNull() {
        // Use a real board with 2 tiles
        Board realBoard = new Board(2);
        realBoard.addTile(0, null);
        realBoard.addTile(1, null);
        realBoard.connectTiles(0, realBoard.getTile(1));
        Dice realDice = mock(Dice.class);
        BoardGame realGame = new BoardGame(realBoard, realDice);
        realGame.addPlayer("Player1");
        realGame.startGame();
        Player player = realGame.getCurrentPlayer();
        player.setCurrentTile(realBoard.getTile(0));
        when(realDice.getLastRolledValues()).thenReturn(new int[]{1});
        when(realDice.sumOfRolledValues()).thenReturn(1);
        realGame.makeMoveWithResult(); // Move to last tile (game over)
        BoardGame.MoveResult result = realGame.makeMoveWithResult();
        assertNull(result);
    }

    @Test
    void makeMoveWithResult_WithSkipTurn_ShouldReturnSkipTurnResult() {
        boardGame.addPlayer("Player1");
        boardGame.addPlayer("Player2");
        boardGame.startGame();
        
        Player currentPlayer = boardGame.getCurrentPlayer();
        currentPlayer.setSkipNextTurn(true);
        
        BoardGame.MoveResult result = boardGame.makeMoveWithResult();
        
        assertNotNull(result);
        assertEquals("Skip Turn", result.actionDesc);
        assertEquals(result.prevPos, result.posAfterMove);
        assertEquals(result.prevPos, result.posAfterAction);
    }

    @Test
    void makeMoveWithResult_WithNormalMove_ShouldReturnCorrectResult() {
        // Use a real board with 2 tiles
        Board realBoard = new Board(2);
        realBoard.addTile(0, null);
        realBoard.addTile(1, null);
        realBoard.connectTiles(0, realBoard.getTile(1));
        Dice realDice = mock(Dice.class);
        BoardGame realGame = new BoardGame(realBoard, realDice);
        realGame.addPlayer("Player1");
        realGame.startGame();
        Player currentPlayer = realGame.getCurrentPlayer();
        currentPlayer.setCurrentTile(realBoard.getTile(0));
        when(realDice.getLastRolledValues()).thenReturn(new int[]{1});
        when(realDice.sumOfRolledValues()).thenReturn(1);
        BoardGame.MoveResult result = realGame.makeMoveWithResult();
        assertNotNull(result);
        assertEquals("Player1", result.playerName);
        assertArrayEquals(new int[]{1}, result.diceValues);
    }

    @Test
    void makeMoveWithResult_WithTileAction_ShouldExecuteAction() {
        // Use a real board with 2 tiles, second tile has an action
        Board realBoard = new Board(2);
        realBoard.addTile(0, null);
        TileAction action = mock(TileAction.class);
        Tile tile1 = new Tile(1, action);
        realBoard.getTiles().put(1, tile1);
        realBoard.connectTiles(0, tile1);
        Dice realDice = mock(Dice.class);
        BoardGame realGame = new BoardGame(realBoard, realDice);
        realGame.addPlayer("Player1");
        realGame.startGame();
        Player currentPlayer = realGame.getCurrentPlayer();
        currentPlayer.setCurrentTile(realBoard.getTile(0));
        when(realDice.getLastRolledValues()).thenReturn(new int[]{1});
        when(realDice.sumOfRolledValues()).thenReturn(1);
        when(action.getDescription()).thenReturn("Test Action");
        BoardGame.MoveResult result = realGame.makeMoveWithResult();
        assertNotNull(result);
        assertEquals("Test Action", result.actionDesc);
        verify(action).executeAction(currentPlayer, tile1);
    }

    @Test
    void getCurrentPlayer_ShouldReturnCorrectPlayer() {
        boardGame.addPlayer("Player1");
        boardGame.addPlayer("Player2");
        boardGame.startGame();
        
        assertEquals("Player1", boardGame.getCurrentPlayer().getName());
    }

    @Test
    void getWinner_WhenGameNotOver_ShouldReturnNull() {
        boardGame.addPlayer("Player1");
        boardGame.startGame();
        
        assertNull(boardGame.getWinner());
    }

    @Test
    void getWinner_WhenGameOver_ShouldReturnCurrentPlayer() {
        // Use a real board with 2 tiles
        Board realBoard = new Board(2);
        realBoard.addTile(0, null);
        realBoard.addTile(1, null);
        realBoard.connectTiles(0, realBoard.getTile(1));
        Dice realDice = mock(Dice.class);
        BoardGame realGame = new BoardGame(realBoard, realDice);
        realGame.addPlayer("Player1");
        realGame.startGame();
        Player player = realGame.getCurrentPlayer();
        player.setCurrentTile(realBoard.getTile(0));
        when(realDice.getLastRolledValues()).thenReturn(new int[]{1});
        when(realDice.sumOfRolledValues()).thenReturn(1);
        realGame.makeMoveWithResult(); // Move to last tile
        assertNotNull(realGame.getWinner());
        assertEquals("Player1", realGame.getWinner().getName());
    }

    @Test
    void observerPattern_ShouldWorkCorrectly() {
        boardGame.addObserver(mockObserver);
        boardGame.addPlayer("Player1");
        
        verify(mockObserver).update();
        
        boardGame.removeObserver(mockObserver);
        boardGame.addPlayer("Player2");
        
        verify(mockObserver, times(1)).update();
    }
} 