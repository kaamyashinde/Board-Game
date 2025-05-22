package edu.ntnu.iir.bidata.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReader;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriter;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.config.TileConfiguration;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.utils.GameMediator;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SnakesAndLaddersControllerTest {

  @Mock
  private BoardGame mockBoardGame;
  @Mock
  private Board mockBoard;
  @Mock
  private Dice mockDice;
  @Mock
  private BoardGameFileWriter mockFileWriter;
  @Mock
  private BoardGameFileReader mockFileReader;
  @Mock
  private GameMediator mockMediator;
  @Mock
  private TileConfiguration mockTileConfig;
  @Mock
  private Player mockPlayer1;
  @Mock
  private Player mockPlayer2;
  @Mock
  private Tile mockTile;

  private SnakesAndLaddersController controller;
  private List<String> playerNames;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    playerNames = Arrays.asList("Player1", "Player2");

    // Minimal mocking to prevent NullPointerExceptions
    when(mockBoardGame.getBoard()).thenReturn(mockBoard);
    when(mockBoardGame.getDice()).thenReturn(mockDice);
    when(mockBoardGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));
    when(mockBoardGame.getCurrentPlayer()).thenReturn(mockPlayer1);
    when(mockBoardGame.getCurrentPlayerIndex()).thenReturn(0);
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{4});

    when(mockBoard.getTile(0)).thenReturn(mockTile);
    when(mockPlayer1.getName()).thenReturn("Player1");
    when(mockPlayer1.getCurrentPosition()).thenReturn(5);
    when(mockPlayer2.getName()).thenReturn("Player2");
    when(mockPlayer2.getCurrentPosition()).thenReturn(8);

    controller = new SnakesAndLaddersController(
        mockBoardGame, mockFileWriter, mockFileReader, mockMediator, mockTileConfig);
  }

  @Test
  void testConstructor() {
    SnakesAndLaddersController newController = new SnakesAndLaddersController(
        mockBoardGame, mockFileWriter, mockFileReader, mockMediator, mockTileConfig);

    assertNotNull(newController);
  }

  @Test
  void testSetPlayerNames() {
    controller.setPlayerNames(playerNames);

    verify(mockPlayer1).setCurrentTile(mockTile);
    verify(mockPlayer2).setCurrentTile(mockTile);
  }

  @Test
  void testSetPlayerNames_EmptyList() {
    List<String> emptyNames = Arrays.asList();

    controller.setPlayerNames(emptyNames);

    verify(mockPlayer1, never()).setCurrentTile(any());
    verify(mockPlayer2, never()).setCurrentTile(any());
  }

  @Test
  void testStartGame() {
    controller.setPlayerNames(playerNames);

    controller.startGame();

    verify(mockBoardGame).startGame();
  }

  @Test
  void testHandlePlayerMove_GameNotStarted() {
    controller.handlePlayerMove();

    verify(mockMediator, never()).notify(any(), any());
  }

  @Test
  void testHandlePlayerMove_NormalMove() {
    controller.setPlayerNames(playerNames);
    controller.startGame();
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{3});
    when(mockPlayer1.getCurrentPosition()).thenReturn(5).thenReturn(8);
    when(mockTileConfig.isSnakeHead(8)).thenReturn(false);
    when(mockTileConfig.isLadderStart(8)).thenReturn(false);

    controller.handlePlayerMove();

    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testHandlePlayerMove_PlayerWins() {
    controller.setPlayerNames(playerNames);
    controller.startGame();
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{3});
    when(mockPlayer1.getCurrentPosition()).thenReturn(97).thenReturn(100);
    when(mockTileConfig.isSnakeHead(100)).thenReturn(false);
    when(mockTileConfig.isLadderStart(100)).thenReturn(false);

    controller.handlePlayerMove();

    verify(mockMediator, never()).notify(controller, "nextPlayer");
  }

  @Test
  void testGetCurrentSnakesAndLaddersPlayerName() {
    String playerName = controller.getCurrentSnakesAndLaddersPlayerName();

    assertEquals("Player1", playerName);
  }

  @Test
  void testGetLastDiceRoll() {
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{5, 3});

    int roll = controller.getLastDiceRoll();

    assertEquals(5, roll);
  }

  @Test
  void testGetLastDiceRoll_NullValues() {
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(null);

    int roll = controller.getLastDiceRoll();

    assertEquals(0, roll);
  }

  @Test
  void testGetLastDiceRoll_EmptyArray() {
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{});

    int roll = controller.getLastDiceRoll();

    assertEquals(0, roll);
  }

  @Test
  void testMovePlayer_NormalMove() {
    when(mockPlayer1.getCurrentPosition()).thenReturn(10).thenReturn(15);
    when(mockTileConfig.isSnakeHead(15)).thenReturn(false);
    when(mockTileConfig.isLadderStart(15)).thenReturn(false);

    SnakesAndLaddersController.MoveResult result = controller.movePlayer("Player1", 5);

    assertEquals(10, result.start);
    assertEquals(15, result.end);
    assertEquals("normal", result.type);
    verify(mockPlayer1).move(5);
  }

  @Test
  void testMovePlayer_HitSnake() {
    when(mockPlayer1.getCurrentPosition()).thenReturn(10).thenReturn(25);
    when(mockTileConfig.isSnakeHead(25)).thenReturn(true);
    when(mockTileConfig.getSnakeTail(25)).thenReturn(5);

    SnakesAndLaddersController.MoveResult result = controller.movePlayer("Player1", 15);

    assertEquals(10, result.start);
    assertEquals(5, result.end);
    assertEquals("snake", result.type);
    verify(mockPlayer1).move(15);
    verify(mockPlayer1).move(-20);
  }

  @Test
  void testMovePlayer_ClimbLadder() {
    when(mockPlayer1.getCurrentPosition()).thenReturn(10).thenReturn(20);
    when(mockTileConfig.isSnakeHead(20)).thenReturn(false);
    when(mockTileConfig.isLadderStart(20)).thenReturn(true);
    when(mockTileConfig.getLadderEnd(20)).thenReturn(35);

    SnakesAndLaddersController.MoveResult result = controller.movePlayer("Player1", 10);

    assertEquals(10, result.start);
    assertEquals(35, result.end);
    assertEquals("ladder", result.type);
    verify(mockPlayer1).move(10);
    verify(mockPlayer1).move(15);
  }

  @Test
  void testMovePlayer_ExceedBoard() {
    when(mockPlayer1.getCurrentPosition()).thenReturn(95).thenReturn(100);
    when(mockTileConfig.isSnakeHead(100)).thenReturn(false);
    when(mockTileConfig.isLadderStart(100)).thenReturn(false);

    SnakesAndLaddersController.MoveResult result = controller.movePlayer("Player1", 10);

    assertEquals(95, result.start);
    assertEquals(100, result.end);
    assertEquals("normal", result.type);
    verify(mockPlayer1).move(5);
  }

  @Test
  void testMovePlayer_PlayerNotFound() {
    SnakesAndLaddersController.MoveResult result = controller.movePlayer("NonExistentPlayer", 5);

    assertEquals(0, result.start);
    assertEquals(0, result.end);
    assertEquals("normal", result.type);
  }

  @Test
  void testGetLastDiceRolls() {
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{3, 4, 2});

    int[] rolls = controller.getLastDiceRolls();

    assertArrayEquals(new int[]{3, 4, 2}, rolls);
  }

  @Test
  void testGetLastDiceSum() {
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{3, 4, 2});

    int sum = controller.getLastDiceSum();

    assertEquals(9, sum);
  }

  @Test
  void testGetLastDiceSum_NullValues() {
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(null);

    int sum = controller.getLastDiceSum();

    assertEquals(0, sum);
  }

  @Test
  void testRollDice() {
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{6});

    controller.rollDice();

    verify(mockDice).rollAllDice();
    assertTrue(controller.isDiceRolled());
  }

  @Test
  void testNextSnakesAndLaddersPlayer() {
    when(mockBoardGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));
    when(mockBoardGame.getCurrentPlayerIndex()).thenReturn(0);
    when(mockBoardGame.getCurrentPlayer()).thenReturn(mockPlayer2);
    when(mockPlayer2.getName()).thenReturn("Player2");

    controller.nextSnakesAndLaddersPlayer();

    verify(mockBoardGame).setCurrentPlayerIndex(1);
  }

  @Test
  void testNextSnakesAndLaddersPlayer_WrapAround() {
    when(mockBoardGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));
    when(mockBoardGame.getCurrentPlayerIndex()).thenReturn(1);
    when(mockBoardGame.getCurrentPlayer()).thenReturn(mockPlayer1);
    when(mockPlayer1.getName()).thenReturn("Player1");

    controller.nextSnakesAndLaddersPlayer();

    verify(mockBoardGame).setCurrentPlayerIndex(0);
  }

  @Test
  void testGetPlayerPosition() {
    when(mockPlayer1.getCurrentPosition()).thenReturn(25);

    int position = controller.getPlayerPosition("Player1");

    assertEquals(25, position);
  }

  @Test
  void testGetPlayerPosition_PlayerNotFound() {
    int position = controller.getPlayerPosition("NonExistentPlayer");

    assertEquals(0, position);
  }

  @Test
  void testLoadSnakesAndLadderGame() {
    // Test that the method doesn't throw exceptions
    // The actual file loading will fail in test environment, which is expected
    assertDoesNotThrow(() -> controller.loadSnakesAndLadderGame("testSave"));
  }

  @Test
  void testUpdateSnakesAndLaddersPosition() {
    when(mockPlayer1.getCurrentPosition()).thenReturn(10);

    controller.updateSnakesAndLaddersPosition("Player1", 20);

    verify(mockPlayer1).move(10);
  }

  @Test
  void testUpdateSnakesAndLaddersPosition_PlayerNotFound() {
    controller.updateSnakesAndLaddersPosition("NonExistentPlayer", 20);

    verify(mockPlayer1, never()).move(anyInt());
    verify(mockPlayer2, never()).move(anyInt());
  }

  @Test
  void testMoveResult_Constructor() {
    SnakesAndLaddersController.MoveResult result =
        new SnakesAndLaddersController.MoveResult(5, 10, "ladder");

    assertEquals(5, result.start);
    assertEquals(10, result.end);
    assertEquals("ladder", result.type);
  }

  @Test
  void testMovePlayer_ComplexScenario() {
    when(mockPlayer1.getCurrentPosition()).thenReturn(90).thenReturn(95);
    when(mockTileConfig.isSnakeHead(95)).thenReturn(true);
    when(mockTileConfig.getSnakeTail(95)).thenReturn(75);

    SnakesAndLaddersController.MoveResult result = controller.movePlayer("Player1", 5);

    assertEquals(90, result.start);
    assertEquals(75, result.end);
    assertEquals("snake", result.type);
  }

  @Test
  void testGetLastDiceRoll_SingleDie() {
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{6});

    int roll = controller.getLastDiceRoll();

    assertEquals(6, roll);
  }

  @Test
  void testHandlePlayerMove_Integration() {
    controller.setPlayerNames(playerNames);
    controller.startGame();
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{4});
    when(mockPlayer1.getCurrentPosition()).thenReturn(10).thenReturn(14);
    when(mockTileConfig.isSnakeHead(14)).thenReturn(false);
    when(mockTileConfig.isLadderStart(14)).thenReturn(false);

    controller.handlePlayerMove();

    verify(mockPlayer1).move(4);
    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testUpdateSnakesAndLaddersPosition_NegativeMovement() {
    when(mockPlayer1.getCurrentPosition()).thenReturn(20);

    controller.updateSnakesAndLaddersPosition("Player1", 10);

    verify(mockPlayer1).move(-10);
  }

  @Test
  void testUpdateSnakesAndLaddersPosition_SamePosition() {
    when(mockPlayer1.getCurrentPosition()).thenReturn(15);

    controller.updateSnakesAndLaddersPosition("Player1", 15);

    verify(mockPlayer1).move(0);
  }
}