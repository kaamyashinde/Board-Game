package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.tile.actions.base.SafeSpotAction;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.board.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LudoControllerTest {

    @Mock
    private BoardGame mockBoardGame;
    
    @Mock
    private Dice mockDice;
    
    @Mock
    private Tile mockTile;
    
    @Mock
    private Board mockBoard;

    private LudoController controller;
    private List<String> playerNames;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new LudoController(mockBoardGame);
        playerNames = Arrays.asList("Player1", "Player2");
        controller.setPlayerNames(playerNames);
        when(mockBoardGame.getDice()).thenReturn(mockDice);
        when(mockBoardGame.getBoard()).thenReturn(mockBoard);
    }

    @Test
    void testInitialization() {
        assertNotNull(controller);
        assertFalse(controller.isGameOver());
        assertFalse(controller.isMovingPiece());
    }

    @Test
    void testSetPlayerNames() {
        List<String> names = Arrays.asList("Player1", "Player2");
        controller.setPlayerNames(names);
        
        // Verify token positions are initialized
        List<Integer> positions = controller.getPlayerTokenPositions("Player1");
        assertEquals(4, positions.size());
        assertTrue(positions.stream().allMatch(pos -> pos == -1));
        
        // Verify home status
        List<Boolean> home = controller.getPlayerTokenHome("Player1");
        assertEquals(4, home.size());
        assertTrue(home.stream().allMatch(h -> h));
        
        // Verify finished status
        List<Boolean> finished = controller.getPlayerTokenFinished("Player1");
        assertEquals(4, finished.size());
        assertTrue(finished.stream().noneMatch(f -> f));
    }

    @Test
    void testHandlePlayerMoveWithoutDiceRoll() {
        controller.handlePlayerMove();
        verify(mockBoardGame, never()).getCurrentDiceValues();
    }

    @Test
    void testHandlePlayerMoveWithDiceRoll() {
        when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{6});
        controller.setDiceRolled(true);
        
        controller.handlePlayerMove();
        assertTrue(controller.isMovingPiece());
    }

    @Test
    void testGetLegalMoves() {
        // Test getting legal moves for a player with tokens in home
        List<Integer> legalMoves = controller.getLegalMoves("Player1", 6);
        assertEquals(4, legalMoves.size()); // All tokens should be legal to move out
        
        // Test getting legal moves for a player with tokens on board
        List<Integer> positions = controller.getPlayerTokenPositions("Player1");
        List<Boolean> home = controller.getPlayerTokenHome("Player1");
        positions.set(0, 0); // Move first token out
        home.set(0, false);
        
        legalMoves = controller.getLegalMoves("Player1", 3);
        assertEquals(1, legalMoves.size()); // Only the token on board should be movable
    }

    @Test
    void testMoveToken() {
        when(mockBoard.getTile(anyInt())).thenReturn(mockTile);
        when(mockTile.getAction()).thenReturn(new SafeSpotAction());
        
        // Test moving token out of home
        LudoController.MoveResult result = controller.moveToken("Player1", 0, 6);
        assertEquals(0, result.tokenIndex);
        assertEquals(-1, result.start);
        assertEquals(0, result.end);
        assertEquals("home", result.type);
        
        // Test normal move
        result = controller.moveToken("Player1", 0, 3);
        assertEquals(0, result.tokenIndex);
        assertEquals(0, result.start);
        assertEquals(3, result.end);
        assertEquals("normal", result.type);
    }

    @Test
    void testSafeZone() {
        when(mockBoard.getTile(anyInt())).thenReturn(mockTile);
        when(mockTile.getAction()).thenReturn(new SafeSpotAction());
        
        assertTrue(controller.getBoardGame().getBoard().getTile(0).getAction() instanceof SafeSpotAction);
    }

    @Test
    void testGameOver() {
    // Set all but one token to finished for Player1
    List<Boolean> finished = controller.getPlayerTokenFinished("Player1");
    for (int i = 1; i < finished.size(); i++) {
        finished.set(i, true);
    }
    // Set up the last token so it will finish with the next move
    List<Integer> positions = controller.getPlayerTokenPositions("Player1");
    List<Boolean> home = controller.getPlayerTokenHome("Player1");
    positions.set(0, 49); // Assume finish position is 50 for this player
    home.set(0, false);
    controller.setDiceRolled(true);
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{1}); // 49 + 1 = 50 (finish)
    when(mockBoard.getTile(anyInt())).thenReturn(mockTile);
    when(mockTile.getAction()).thenReturn(new SafeSpotAction());

    // This should trigger the finish and set gameOver to true
    controller.handlePlayerMove();
    assertTrue(controller.isGameOver());
}
    @Test
    void testRollDice() {
        doNothing().when(mockDice).rollAllDice();
        when(mockDice.getLastRolledValues()).thenReturn(new int[]{6});
        when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{6});
        
        int result = controller.rollDice();
        assertEquals(6, result);
        assertTrue(controller.isDiceRolled());
    }
} 