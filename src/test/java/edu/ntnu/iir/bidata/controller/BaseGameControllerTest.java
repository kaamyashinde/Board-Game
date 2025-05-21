package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseGameControllerTest {

    @Mock
    private BoardGame mockBoardGame;

    private BaseGameController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new BaseGameController(mockBoardGame) {
            @Override
            public void handlePlayerMove() {
                // Mock implementation for testing
            }
        };
    }

    @Test
    void testSetPlayerNames() {
        List<String> playerNames = Arrays.asList("Player1", "Player2");
        controller.setPlayerNames(playerNames);
        assertEquals("Player1", controller.getCurrentPlayerName());
    }

    @Test
    void testStartGame() {
        controller.startGame();
        verify(mockBoardGame).startGame();
    }

    @Test
    void testGetCurrentPlayerName() {
        List<String> playerNames = Arrays.asList("Player1", "Player2");
        controller.setPlayerNames(playerNames);
        assertEquals("Player1", controller.getCurrentPlayerName());
    }

    @Test
    void testNextPlayer() {
        List<String> playerNames = Arrays.asList("Player1", "Player2", "Player3");
        controller.setPlayerNames(playerNames);
        
        // Test first player
        assertEquals("Player1", controller.getCurrentPlayerName());
        
        // Test next player
        controller.setCurrentPlayerIndex(1);
        assertEquals("Player2", controller.getCurrentPlayerName());
        
        // Test wrap around
        controller.setCurrentPlayerIndex(2);
        assertEquals("Player3", controller.getCurrentPlayerName());
    }

    @Test
    void testDiceRolled() {
        assertFalse(controller.isDiceRolled());
        controller.setDiceRolled(true);
        assertTrue(controller.isDiceRolled());
    }
} 