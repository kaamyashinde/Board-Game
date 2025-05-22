package edu.ntnu.iir.bidata.view.cli;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandLineInterfaceTest {

  @Mock
  private BoardGame mockGame;

  @Mock
  private Player mockPlayer1;

  @Mock
  private Player mockPlayer2;

  @Mock
  private Player mockPlayer3;

  private CommandLineInterface cli;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Capture System.out for testing console output
    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outputStream));

    // Common mock setup
    when(mockPlayer1.getName()).thenReturn("Player1");
    when(mockPlayer1.getCurrentPosition()).thenReturn(5);
    when(mockPlayer2.getName()).thenReturn("Player2");
    when(mockPlayer2.getCurrentPosition()).thenReturn(8);
    when(mockPlayer3.getName()).thenReturn("Player3");
    when(mockPlayer3.getCurrentPosition()).thenReturn(2);
  }

  @Test
  void testConstructor() {
    // Arrange & Act
    cli = new CommandLineInterface(mockGame);

    // Assert
    assertNotNull(cli);
  }

  @Test
  void testStart_SimpleGameFlow() {
    // Arrange
    String input = "2\nAlice\nBob\n\n\n"; // 2 players, names, then enter presses
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    // Mock game flow
    when(mockGame.isGameOver()).thenReturn(false).thenReturn(false).thenReturn(true);
    when(mockGame.getCurrentPlayerIndex()).thenReturn(0).thenReturn(1);
    when(mockGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));

    // Mock move results
    BoardGame.MoveResult moveResult1 = new BoardGame.MoveResult(
        "Player1", 0, 7, 7, new int[]{3, 4}, ""
    );

    BoardGame.MoveResult moveResult2 = new BoardGame.MoveResult(
        "Player2", 0, 7, 7, new int[]{2, 5}, "Moved forward"
    );

    when(mockGame.makeMoveWithResult()).thenReturn(moveResult1).thenReturn(moveResult2);
    when(mockGame.getWinner()).thenReturn(mockPlayer1);

    // Act
    cli.start();

    // Assert
    verify(mockGame).startGame();
    verify(mockGame, atLeast(2)).makeMoveWithResult();
    verify(mockGame).getWinner();

    String output = outputStream.toString();
    assertTrue(output.contains("Welcome to the Board Game!"));
    assertTrue(output.contains("Rules:"));
    assertTrue(output.contains("Player1 wins!"));
    assertTrue(output.contains("Game Over!"));
  }

  @Test
  void testStart_WithTileAction() {
    // Arrange
    String input = "2\nAlice\nBob\n\n\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(false).thenReturn(true);
    when(mockGame.getCurrentPlayerIndex()).thenReturn(0);
    when(mockGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));

    BoardGame.MoveResult moveResult = new BoardGame.MoveResult(
        "Player1", 5, 12, 15, new int[]{6, 1}, "Go forward 3 spaces"
    );

    when(mockGame.makeMoveWithResult()).thenReturn(moveResult);
    when(mockGame.getWinner()).thenReturn(mockPlayer1);

    // Act
    cli.start();

    // Assert
    String output = outputStream.toString();
    assertTrue(output.contains("Rolled: 6 + 1 = 7"));
    assertTrue(output.contains("Tile Action: Go forward 3 spaces"));
    assertTrue(output.contains("New position after move: 12"));
    assertTrue(output.contains("New position after tile action: 15"));
  }

  @Test
  void testStart_NullDiceValues() {
    // Arrange
    String input = "2\nAlice\nBob\n\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(false).thenReturn(true);
    when(mockGame.getCurrentPlayerIndex()).thenReturn(0);
    when(mockGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));

    BoardGame.MoveResult moveResult = new BoardGame.MoveResult(
        "Player1", 0, 0, 0, null, "" // Null dice values
    );

    when(mockGame.makeMoveWithResult()).thenReturn(moveResult);
    when(mockGame.getWinner()).thenReturn(mockPlayer1);

    // Act
    cli.start();

    // Assert
    String output = outputStream.toString();
    assertFalse(output.contains("Rolled:")); // Should not show dice if null
  }

  @Test
  void testStart_EmptyDiceValues() {
    // Arrange
    String input = "2\nAlice\nBob\n\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(false).thenReturn(true);
    when(mockGame.getCurrentPlayerIndex()).thenReturn(0);
    when(mockGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));

    BoardGame.MoveResult moveResult = new BoardGame.MoveResult(
        "Player1", 0, 0, 0, new int[]{}, "" // Empty dice values
    );

    when(mockGame.makeMoveWithResult()).thenReturn(moveResult);
    when(mockGame.getWinner()).thenReturn(mockPlayer1);

    // Act
    cli.start();

    // Assert
    String output = outputStream.toString();
    assertFalse(output.contains("Rolled:")); // Should not show dice if empty
  }

  @Test
  void testStart_MultipleRounds() {
    // Arrange
    String input = "2\nAlice\nBob\n\n\n\n\n"; // Multiple enter presses for turns
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    // Game continues for multiple rounds - need to ensure we cycle back to player 0
    when(mockGame.isGameOver()).thenReturn(false, false, false, false, true);
    // Simulate cycling: P1(0) -> P2(1) -> P1(0) -> P2(1) - this should trigger round 2
    when(mockGame.getCurrentPlayerIndex()).thenReturn(0, 1, 0, 1);
    when(mockGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));

    BoardGame.MoveResult moveResult1 = new BoardGame.MoveResult(
        "Player1", 0, 6, 6, new int[]{3, 3}, ""
    );

    BoardGame.MoveResult moveResult2 = new BoardGame.MoveResult(
        "Player2", 0, 4, 4, new int[]{2, 2}, ""
    );

    when(mockGame.makeMoveWithResult())
        .thenReturn(moveResult1)  // Round 1 - Player1
        .thenReturn(moveResult2)  // Round 1 - Player2
        .thenReturn(moveResult1)  // Round 2 - Player1
        .thenReturn(moveResult2); // Round 2 - Player2

    when(mockGame.getWinner()).thenReturn(mockPlayer1);

    // Act
    cli.start();

    // Assert
    String output = outputStream.toString();
    // The round header appears when currentPlayerIndex == 0 and turnResults is not empty
    // So we should see the results table for round 1, then round 2 header
    assertTrue(output.contains("Results for Round"));
  }

  @Test
  void testSetupPlayers_ValidInput() {
    // Arrange
    String input = "3\nAlice\nBob\nCharlie\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(true); // End game immediately after setup
    when(mockGame.getWinner()).thenReturn(null);

    // Act
    cli.start();

    // Assert
    verify(mockGame, times(3)).addPlayer(anyString());
    verify(mockGame).addPlayer("Alice");
    verify(mockGame).addPlayer("Bob");
    verify(mockGame).addPlayer("Charlie");

    String output = outputStream.toString();
    assertTrue(output.contains("Enter number of players (2-4):"));
    assertTrue(output.contains("Enter name for Player 1:"));
    assertTrue(output.contains("Enter name for Player 2:"));
    assertTrue(output.contains("Enter name for Player 3:"));
  }

  @Test
  void testSetupPlayers_InvalidNumberThenValid() {
    // Arrange
    String input = "1\n5\n0\n3\nAlice\nBob\nCharlie\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(true); // End game immediately
    when(mockGame.getWinner()).thenReturn(null);

    // Act
    cli.start();

    // Assert
    verify(mockGame, times(3)).addPlayer(anyString());

    String output = outputStream.toString();
    assertTrue(output.contains("Please enter a number between 2 and 4."));
  }

  @Test
  void testSetupPlayers_InvalidNumberFormat() {
    // Arrange
    String input = "abc\ntwo\n2\nAlice\nBob\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(true);
    when(mockGame.getWinner()).thenReturn(null);

    // Act
    cli.start();

    // Assert
    verify(mockGame, times(2)).addPlayer(anyString());

    String output = outputStream.toString();
    assertTrue(output.contains("Please enter a valid number."));
  }

  @Test
  void testSetupPlayers_EmptyPlayerName() {
    // Arrange
    String input = "2\n\n   \nAlice\nBob\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(true);
    when(mockGame.getWinner()).thenReturn(null);

    // Act
    cli.start();

    // Assert
    verify(mockGame, times(2)).addPlayer(anyString());
    verify(mockGame).addPlayer("Alice");
    verify(mockGame).addPlayer("Bob");

    String output = outputStream.toString();
    assertTrue(output.contains("Name cannot be empty."));
  }

  @Test
  void testPrintGameState() {
    // Arrange
    String input = "2\nAlice\nBob\n\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(false).thenReturn(true);
    when(mockGame.getCurrentPlayerIndex()).thenReturn(0);
    when(mockGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));

    BoardGame.MoveResult moveResult = new BoardGame.MoveResult(
        "Player1", 0, 6, 6, new int[]{4, 2}, ""
    );

    when(mockGame.makeMoveWithResult()).thenReturn(moveResult);
    when(mockGame.getWinner()).thenReturn(mockPlayer1);

    // Act
    cli.start();

    // Assert
    String output = outputStream.toString();
    assertTrue(output.contains("Current Game State:"));
    assertTrue(output.contains("Player1 is at position 5"));
    assertTrue(output.contains("Player2 is at position 8"));
  }

  @Test
  void testTurnResultsTable() {
    // Arrange
    String input = "2\nAlice\nBob\n\n\n\n\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    // Setup for multiple rounds to trigger table printing
    when(mockGame.isGameOver()).thenReturn(false, false, false, false, true);
    when(mockGame.getCurrentPlayerIndex()).thenReturn(0, 1, 0, 1);
    when(mockGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));

    BoardGame.MoveResult moveResult1 = new BoardGame.MoveResult(
        "Player1", 0, 5, 8, new int[]{3, 2}, "Ladder"
    );

    BoardGame.MoveResult moveResult2 = new BoardGame.MoveResult(
        "Player2", 2, 9, 9, new int[]{1, 6}, ""
    );

    when(mockGame.makeMoveWithResult()).thenReturn(moveResult1, moveResult2, moveResult1, moveResult2);
    when(mockGame.getWinner()).thenReturn(mockPlayer1);

    // Act
    cli.start();

    // Assert
    String output = outputStream.toString();
    assertTrue(output.contains("Results for Round 1:"));
    assertTrue(output.contains("Round"));
    assertTrue(output.contains("Player"));
    assertTrue(output.contains("Dice Rolled"));
    assertTrue(output.contains("Prev Pos"));
    assertTrue(output.contains("After Move"));
    assertTrue(output.contains("After Tile Action"));
    assertTrue(output.contains("Tile Action"));
  }

  @Test
  void testGameWithNoWinner() {
    // Arrange
    String input = "2\nAlice\nBob\n\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(false).thenReturn(true);
    when(mockGame.getCurrentPlayerIndex()).thenReturn(0);
    when(mockGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));

    BoardGame.MoveResult moveResult = new BoardGame.MoveResult(
        "Player1", 0, 2, 2, new int[]{1, 1}, ""
    );

    when(mockGame.makeMoveWithResult()).thenReturn(moveResult);
    when(mockGame.getWinner()).thenReturn(null); // No winner

    // Act
    cli.start();

    // Assert
    String output = outputStream.toString();
    assertFalse(output.contains("Player1 wins!")); // Should not show specific winner message
    assertFalse(output.contains("Player2 wins!")); // Should not show specific winner message
    assertTrue(output.contains("Game Over!"));
  }

  @Test
  void testStart_SingleDie() {
    // Arrange
    String input = "2\nAlice\nBob\n\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(false).thenReturn(true);
    when(mockGame.getCurrentPlayerIndex()).thenReturn(0);
    when(mockGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));

    BoardGame.MoveResult moveResult = new BoardGame.MoveResult(
        "Player1", 0, 4, 4, new int[]{4}, "" // Single die
    );

    when(mockGame.makeMoveWithResult()).thenReturn(moveResult);
    when(mockGame.getWinner()).thenReturn(mockPlayer1);

    // Act
    cli.start();

    // Assert
    String output = outputStream.toString();
    assertTrue(output.contains("Rolled: 4 = 4"));
  }

  @Test
  void testStart_MultipleDice() {
    // Arrange
    String input = "2\nAlice\nBob\n\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(false).thenReturn(true);
    when(mockGame.getCurrentPlayerIndex()).thenReturn(0);
    when(mockGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));

    BoardGame.MoveResult moveResult = new BoardGame.MoveResult(
        "Player1", 0, 12, 12, new int[]{2, 4, 6}, "" // Three dice
    );

    when(mockGame.makeMoveWithResult()).thenReturn(moveResult);
    when(mockGame.getWinner()).thenReturn(mockPlayer1);

    // Act
    cli.start();

    // Assert
    String output = outputStream.toString();
    assertTrue(output.contains("Rolled: 2 + 4 + 6 = 12"));
  }

  @Test
  void testStart_EmptyActionDescription() {
    // Arrange
    String input = "2\nAlice\nBob\n\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(false).thenReturn(true);
    when(mockGame.getCurrentPlayerIndex()).thenReturn(0);
    when(mockGame.getPlayers()).thenReturn(Arrays.asList(mockPlayer1, mockPlayer2));

    BoardGame.MoveResult moveResult = new BoardGame.MoveResult(
        "Player1", 0, 5, 5, new int[]{3, 2}, "" // Empty action description
    );

    when(mockGame.makeMoveWithResult()).thenReturn(moveResult);
    when(mockGame.getWinner()).thenReturn(mockPlayer1);

    // Act
    cli.start();

    // Assert
    String output = outputStream.toString();
    assertFalse(output.contains("Tile Action:")); // Should not show empty action
  }

  @Test
  void testStart_MaximumPlayers() {
    // Arrange
    String input = "4\nAlice\nBob\nCharlie\nDave\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(true);
    when(mockGame.getWinner()).thenReturn(null);

    // Act
    cli.start();

    // Assert
    verify(mockGame, times(4)).addPlayer(anyString());
    verify(mockGame).addPlayer("Alice");
    verify(mockGame).addPlayer("Bob");
    verify(mockGame).addPlayer("Charlie");
    verify(mockGame).addPlayer("Dave");
  }

  @Test
  void testStart_MinimumPlayers() {
    // Arrange
    String input = "2\nAlice\nBob\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    cli = new CommandLineInterface(mockGame);

    when(mockGame.isGameOver()).thenReturn(true);
    when(mockGame.getWinner()).thenReturn(null);

    // Act
    cli.start();

    // Assert
    verify(mockGame, times(2)).addPlayer(anyString());
    verify(mockGame).addPlayer("Alice");
    verify(mockGame).addPlayer("Bob");
  }

  // Clean up after each test
  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    System.setOut(originalOut);
    System.setIn(System.in);
  }
}