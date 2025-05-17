package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.game.GameState;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.actions.snakeandladder.SnakeAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SnakesAndLaddersControllerTest {
    private SnakesAndLaddersController controller;
    private BoardGame boardGame;
    private List<String> playerNames;
    private Board board;
    private Dice dice;

    private void initializeBoard(Board board) {
        // Add 100 tiles
        for (int i = 0; i < 100; i++) {
            if (i == 95) {
                // Create tile 95 with snake action
                board.addTile(i, new SnakeAction(75));
            } else {
                board.addTile(i, null);
            }
        }
        // Connect each tile to the next
        for (int i = 0; i < 99; i++) {
            board.connectTiles(i, board.getTile(i + 1));
        }
    }

    @BeforeEach
    void setUp() {
        board = new Board(100); // 100 tiles for Snakes and Ladders
        initializeBoard(board);
        dice = new Dice(1); // One die for Snakes and Ladders
        boardGame = new BoardGame(board, dice);
        controller = new SnakesAndLaddersController(boardGame);
        playerNames = Arrays.asList("Player1", "Player2");
        
        // Add players to the game
        for (String name : playerNames) {
            boardGame.addPlayer(name);
        }
        
        controller.setPlayerNames(playerNames);
    }

    @Test
    void testInitialization() {
        assertNotNull(controller);
        assertEquals("Player1", controller.getCurrentSnakesAndLaddersPlayerName());
        assertEquals(0, controller.getPlayerPosition("Player1"));
        assertEquals(0, controller.getPlayerPosition("Player2"));
    }

    @Test
    void testRollDice() {
        controller.startGame();
        controller.rollDiceForSnakesAndLadders();
        int roll = controller.getLastDiceRoll();
        assertTrue(roll >= 1 && roll <= 6);
    }

    @Test
    void testNormalMove() {
        controller.startGame();
        // Force a specific dice roll value of 2 to avoid landing on ladder at position 3
        controller.rollDiceForSnakesAndLadders();
        int roll = 2;  // Use a fixed value instead of the random roll
        SnakesAndLaddersController.MoveResult result = controller.movePlayer("Player1", roll);
        
        assertEquals(0, result.start);
        assertEquals(roll, result.end);
        assertEquals("normal", result.type);
        assertEquals(roll, controller.getPlayerPosition("Player1"));
    }


    @Test
    void testLadderMove() {
        controller.startGame();
        // Move player to position 3 (ladder bottom)
        controller.updateSnakesAndLaddersPosition("Player1", 3);
        SnakesAndLaddersController.MoveResult result = controller.movePlayer("Player1", 0);
        
        assertEquals(3, result.start);
        assertEquals(36, result.end); // Ladder top
        assertEquals("ladder", result.type);
        assertEquals(36, controller.getPlayerPosition("Player1"));
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
    void testHandlePlayerMove() {
        controller.startGame();
        controller.rollDiceForSnakesAndLadders();
        controller.handlePlayerMove();
        // Player should have moved and turn should have passed to next player
        assertNotEquals(0, controller.getPlayerPosition("Player1"));
        assertEquals("Player2", controller.getCurrentSnakesAndLaddersPlayerName());
    }

    @Test
    void testSaveAndLoadGame(@TempDir Path tempDir) {
        controller.startGame();
        controller.rollDiceForSnakesAndLadders();
        controller.handlePlayerMove();
        
        // Save game to temporary directory
        String gameName = "test_game";
        Path savePath = tempDir.resolve(gameName + ".json");
        controller.saveGame(savePath.toString());
        
        // Create new controller and load game
        Board newBoard = new Board(100);
        initializeBoard(newBoard);
        Dice newDice = new Dice(1);
        BoardGame newBoardGame = new BoardGame(newBoard, newDice);
        
        // Add players to the new game
        for (String name : playerNames) {
            newBoardGame.addPlayer(name);
        }
        
        SnakesAndLaddersController newController = new SnakesAndLaddersController(newBoardGame);
        newController.loadGame(savePath.toString(), null);
        
        // Verify game state was restored
        assertEquals(controller.getPlayerPosition("Player1"), 
                    newController.getPlayerPosition("Player1"));
        assertEquals(controller.getCurrentSnakesAndLaddersPlayerName(), 
                    newController.getCurrentSnakesAndLaddersPlayerName());
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
