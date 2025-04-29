package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.view.GameUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardGameTest {
    private BoardGame boardGame;
    private Player player1;
    private Player player2;
    
    @Mock
    private GameUI mockUI;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        boardGame = new BoardGame(2, 10);
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");
    }
    
    @Test
    void testAddPlayer() {
        boardGame.addPlayer(player1);
        assertTrue(boardGame.getPlayers().containsKey(player1));
        assertEquals(0, boardGame.getPlayers().get(player1));
    }
    
    @Test
    void testAddPlayerWhenGameStarted() {
        boardGame.addPlayer(player1);
        boardGame.initialiseGame();
        assertThrows(GameException.class, () -> boardGame.addPlayer(player2));
    }
    
    @Test
    void testInitialiseGame() {
        boardGame.addPlayer(player1);
        boardGame.addPlayer(player2);
        boardGame.initialiseGame();
        assertTrue(boardGame.isPlaying());
        assertEquals(player1, boardGame.getCurrentPlayer());
    }
    
    @Test
    void testInitialiseGameWithoutPlayers() {
        assertThrows(GameException.class, () -> boardGame.initialiseGame());
    }
    
    @Test
    void testPlayCurrentPlayer() {
        boardGame.addPlayer(player1);
        boardGame.initialiseGame();
        int steps = boardGame.rollDice();
        boardGame.movePlayer(player1, steps);
        verify(mockUI, atLeastOnce()).displayPlayerTurn(player1);
        verify(mockUI, atLeastOnce()).displayDiceRoll(player1, steps);
    }
    
    @Test
    void testObserverNotifications() {
        BoardGameObserver mockObserver = mock(BoardGameObserver.class);
        boardGame.addObserver(mockObserver);
        boardGame.addPlayer(player1);
        boardGame.initialiseGame();
        
        verify(mockObserver, times(1)).onTurnChanged(player1);
    }
    
    @Test
    void testGameWin() {
        boardGame.addPlayer(player1);
        boardGame.initialiseGame();
        
        // Force a win by moving player to last tile
        boardGame.getBoard().getTiles().get(9).setNextTile(null);
        player1.setCurrentTile(boardGame.getBoard().getTiles().get(8));
        boardGame.getPlayers().put(player1, 8);
        
        int steps = boardGame.rollDice();
        boardGame.movePlayer(player1, steps);
        assertFalse(boardGame.isPlaying());
        verify(mockUI).displayWinner(player1);
    }
} 