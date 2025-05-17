package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameControllerTest {
    @Mock
    private BoardGame mockBoardGame;
    @Mock
    private Player mockPlayer;
    @Mock
    private TileAction mockTileAction;

    private GameController controller;
    private List<String> playerNames;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new GameController(mockBoardGame);
        playerNames = Arrays.asList("Alice", "Bob");
        controller.setPlayerNames(playerNames);
    }

    @Test
    void testSetPlayerNamesAndGetCurrentPlayer() {
        assertEquals("Alice", controller.getCurrentSnakesAndLaddersPlayerName());
        assertEquals(0, controller.getPlayerPosition("Alice"));
        assertEquals(0, controller.getPlayerPosition("Bob"));
    }

    @Test
    void testStartGame() {
        controller.startGame();
        // No exception means pass; check log output if needed
    }

    @Test
    void testRollDiceForSnakesAndLadders() {
        int roll = controller.rollDiceForSnakesAndLadders();
        assertTrue(roll >= 1 && roll <= 6);
    }

    @Test
    void testUpdateSnakesAndLaddersPositionNormalMove() {
        boolean hasWon = controller.updateSnakesAndLaddersPosition("Alice", 4);
        assertFalse(hasWon);
        assertEquals(4, controller.getPlayerPosition("Alice"));
    }

    @Test
    void testUpdateSnakesAndLaddersPositionBounceBack() {
        controller.updateSnakesAndLaddersPosition("Alice", 98); // Move to 98
        boolean hasWon = controller.updateSnakesAndLaddersPosition("Alice", 5); // Should bounce back to 97
        assertFalse(hasWon);
        assertEquals(97, controller.getPlayerPosition("Alice"));
    }

    @Test
    void testUpdateSnakesAndLaddersPositionSnake() {
        controller.updateSnakesAndLaddersPosition("Alice", 99); // Move to 99 (snake to 41)
        assertEquals(41, controller.getPlayerPosition("Alice"));
    }

    @Test
    void testUpdateSnakesAndLaddersPositionLadder() {
        controller.updateSnakesAndLaddersPosition("Alice", 3); // Move to 3 (ladder to 36)
        assertEquals(36, controller.getPlayerPosition("Alice"));
    }

    @Test
    void testNextSnakesAndLaddersPlayer() {
        assertEquals("Alice", controller.getCurrentSnakesAndLaddersPlayerName());
        controller.nextSnakesAndLaddersPlayer();
        assertEquals("Bob", controller.getCurrentSnakesAndLaddersPlayerName());
        controller.nextSnakesAndLaddersPlayer();
        assertEquals("Alice", controller.getCurrentSnakesAndLaddersPlayerName());
    }

    @Test
    void testIsGameOver() {
        when(mockBoardGame.isGameOver()).thenReturn(true);
        when(mockBoardGame.getWinner()).thenReturn(mockPlayer);
        when(mockPlayer.getName()).thenReturn("Alice");
        assertTrue(controller.isGameOver());
    }

    @Test
    void testGetCurrentPlayer() {
        when(mockBoardGame.getCurrentPlayer()).thenReturn(mockPlayer);
        when(mockPlayer.getName()).thenReturn("Alice");
        Player player = controller.getCurrentPlayer();
        assertEquals("Alice", player.getName());
    }

    @Test
    void testMakeMove() {
        BoardGame.MoveResult realResult = new BoardGame.MoveResult(
            "Alice", 1, 2, 2, new int[]{4}, "Test Action");
        when(mockBoardGame.makeMoveWithResult()).thenReturn(realResult);
        BoardGame.MoveResult result = controller.makeMove();
        assertNotNull(result);
        assertEquals("Alice", result.playerName);
        assertEquals(1, result.prevPos);
        assertEquals(2, result.posAfterMove);
        assertEquals(2, result.posAfterAction);
        assertArrayEquals(new int[]{4}, result.diceValues);
        assertEquals("Test Action", result.actionDesc);
    }

    @Test
    void testHandleTileAction() {
        // Just ensure no exception is thrown
        controller.handleTileAction(mockTileAction);
    }

    @Test
    void testRollDiceForLudo() {
        int roll = controller.rollDiceForLudo();
        assertTrue(roll >= 1 && roll <= 6);
        assertTrue(controller.isDiceRolled());
    }

    @Test
    void testCanMoveLudoToken() {
        assertTrue(controller.canMoveLudoToken(-1, 6)); // In home, rolled 6
        assertFalse(controller.canMoveLudoToken(-1, 3)); // In home, not 6
        assertTrue(controller.canMoveLudoToken(0, 3)); // On board
    }
} 