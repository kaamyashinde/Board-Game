package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.dice.Dice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SnakesAndLaddersControllerTest {

    @Mock
    private BoardGame mockBoardGame;
    
    @Mock
    private Dice mockDice;

    private SnakesAndLaddersController controller;
    private List<String> playerNames;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new SnakesAndLaddersController(mockBoardGame);
        playerNames = Arrays.asList("Player1", "Player2");
        controller.setPlayerNames(playerNames);
        when(mockBoardGame.getDice()).thenReturn(mockDice);
    }

    @Test
    void testInitialization() {
        assertNotNull(controller);
        assertEquals(0, controller.getPlayerPosition("Player1"));
        assertEquals(0, controller.getPlayerPosition("Player2"));
    }

    @Test
    void testSetPlayerNames() {
        List<String> names = Arrays.asList("Player1", "Player2");
        controller.setPlayerNames(names);
        assertEquals("Player1", controller.getCurrentPlayerName());
        assertEquals(0, controller.getPlayerPosition("Player1"));
    }

    @Test
    void testHandlePlayerMoveWithoutDiceRoll() {
        controller.handlePlayerMove();
        verify(mockBoardGame, never()).getCurrentDiceValues();
    }

    @Test
    void testHandlePlayerMoveWithDiceRoll() {
        when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{3, 4});
        controller.setDiceRolled(true);
        
        controller.handlePlayerMove();
        assertEquals(7, controller.getPlayerPosition("Player1"));
    }

    @Test
    void testSnakeEncounter() {
        // Position 99 has a snake to 41
        controller.updateSnakesAndLaddersPosition("Player1", 98);
        when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{1});
        controller.setDiceRolled(true);
        
        controller.handlePlayerMove();
        assertEquals(41, controller.getPlayerPosition("Player1"));
    }

    @Test
    void testLadderEncounter() {
        // Position 3 has a ladder to 36
        controller.updateSnakesAndLaddersPosition("Player1", 2);
        when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{1});
        controller.setDiceRolled(true);
        
        controller.handlePlayerMove();
        assertEquals(36, controller.getPlayerPosition("Player1"));
    }

    @Test
    void testWinCondition() {
        controller.updateSnakesAndLaddersPosition("Player1", 98);
        when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{2});
        controller.setDiceRolled(true);
        
        controller.handlePlayerMove();
        assertEquals(100, controller.getPlayerPosition("Player1"));
    }

    @Test
    void testMovePlayer() {
        // Test normal move
        SnakesAndLaddersController.MoveResult result = controller.movePlayer("Player1", 5);
        assertEquals(0, result.start);
        assertEquals(5, result.end);
        assertEquals("normal", result.type);
        
        // Test snake encounter
        controller.updateSnakesAndLaddersPosition("Player1", 94);
        result = controller.movePlayer("Player1", 5);
        assertEquals(94, result.start);
        assertEquals(41, result.end); // Snake at 99 goes to 41
        assertEquals("snake", result.type);
        
        // Test ladder encounter
        controller.updateSnakesAndLaddersPosition("Player1", 2);
        result = controller.movePlayer("Player1", 1);
        assertEquals(2, result.start);
        assertEquals(36, result.end);
        assertEquals("ladder", result.type);
    }

    @Test
    void testRollDiceForSnakesAndLadders() {
        doNothing().when(mockDice).rollAllDice();
        when(mockDice.getLastRolledValues()).thenReturn(new int[]{3, 4});
        when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{3, 4});
        
        controller.rollDiceForSnakesAndLadders();
        assertTrue(controller.isDiceRolled());
        assertEquals(3, controller.getLastDiceRoll());
    }

    @Test
    void testNextPlayer() {
        assertEquals("Player1", controller.getCurrentPlayerName());
        controller.nextSnakesAndLaddersPlayer();
        assertEquals("Player2", controller.getCurrentPlayerName());
    }
} 