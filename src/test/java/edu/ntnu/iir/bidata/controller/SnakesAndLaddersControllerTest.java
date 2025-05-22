package edu.ntnu.iir.bidata.controller;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriterGson;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.actions.snakeandladder.SnakeAction;
import edu.ntnu.iir.bidata.model.tile.config.TileConfiguration;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SnakesAndLaddersControllerTest {
  private SnakesAndLaddersController controller;
  private BoardGame boardGame;
  private List<String> playerNames;
  private Board board;
  private Dice dice;

  @BeforeEach
  void setUp() {
    board = new Board(100); // 100 tiles for Snakes and Ladders
    initializeBoard(board);
    dice = new Dice(1); // One die for Snakes and Ladders
    boardGame = new BoardGame(board, dice);
    // Use a mediator that advances the player
    TileConfiguration config = new TileConfiguration();
    controller =
        new SnakesAndLaddersController(
            boardGame,
            new BoardGameFileWriterGson(),
            new BoardGameFileReaderGson(),
            (sender, event) -> {
              if ("nextPlayer".equals(event)) {
                ((SnakesAndLaddersController) sender).nextSnakesAndLaddersPlayer();
              }
            },
            config);
    playerNames = Arrays.asList("Player1", "Player2");

    // Add players to the game
    playerNames.forEach(name -> boardGame.addPlayer(name));

    controller.setPlayerNames(playerNames);
  }

  private void initializeBoard(Board board) {
    // Add 100 tiles
    java.util.stream.IntStream.range(0, 100)
        .forEach(
            i -> {
              if (i == 95) {
                // Create tile 95 with snake action
                board.addTile(i, new SnakeAction(75));
              } else {
                board.addTile(i, null);
              }
            });
    // Connect each tile to the next
    java.util.stream.IntStream.range(0, 99)
        .forEach(i -> board.connectTiles(i, board.getTile(i + 1)));
  }

  @Test
  void testInitialization() {
    assertNotNull(controller);
    assertEquals("Player1", controller.getCurrentSnakesAndLaddersPlayerName());
    assertEquals(0, controller.getPlayerPosition("Player1"));
    assertEquals(0, controller.getPlayerPosition("Player2"));
  }

  @Test
  void testNormalMove() {
    controller.startGame();
    // Force a specific dice roll value of 2 to avoid landing on ladder at position 3
    int roll = 2; // Use a fixed value instead of the random roll
    SnakesAndLaddersController.MoveResult result = controller.movePlayer("Player1", roll);

    assertEquals(0, result.start);
    assertEquals(roll, result.end);
    assertEquals("normal", result.type);
    assertEquals(roll, controller.getPlayerPosition("Player1"));
  }

  @Test
  void testNextPlayer() {
    controller.startGame();
    assertEquals("Player1", controller.getCurrentSnakesAndLaddersPlayerName());
    controller.nextSnakesAndLaddersPlayer();
    assertEquals("Player2", controller.getCurrentSnakesAndLaddersPlayerName());
    controller.nextSnakesAndLaddersPlayer();
    assertEquals("Player1", controller.getCurrentSnakesAndLaddersPlayerName());
  }

  @Test
  void testWinCondition() {
    controller.startGame();
    // Move player to position 98
    controller.updateSnakesAndLaddersPosition("Player1", 98);
    // Moving to position 100 should throw GameException
    assertThrows(GameException.class, () -> controller.movePlayer("Player1", 2));
  }

  @Test
  void testInvalidMoves() {
    // Test move before game start
    controller.handlePlayerMove();
    assertEquals(0, controller.getPlayerPosition("Player1"));

    // Test move beyond board
    controller.startGame();
    controller.updateSnakesAndLaddersPosition("Player1", 98);
    // This should throw GameException because it would exceed board limit
    assertThrows(GameException.class, () -> controller.movePlayer("Player1", 5));
  }
}
