package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReader;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriter;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.exception.LowMoneyException;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import edu.ntnu.iir.bidata.model.utils.GameMediator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MonopolyControllerTest {

  @Mock
  private BoardGame mockBoardGame;

  @Mock
  private BoardGameFileWriter mockBoardGameWriter;

  @Mock
  private BoardGameFileReader mockBoardGameReader;

  @Mock
  private GameMediator mockMediator;

  @Mock
  private SimpleMonopolyPlayer mockCurrentPlayer;

  @Mock
  private SimpleMonopolyPlayer mockOtherPlayer;

  @Mock
  private PropertyTile mockPropertyTile;

  @Mock
  private Tile mockTile;

  @Mock
  private TileAction mockTileAction;

  @Mock
  private Dice mockDice;

  private MonopolyController controller;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    controller = new MonopolyController(mockBoardGame, mockBoardGameWriter, mockBoardGameReader, mockMediator);

    // Common setup
    when(mockBoardGame.getCurrentPlayer()).thenReturn(mockCurrentPlayer);
    when(mockBoardGame.getDice()).thenReturn(mockDice);
    when(mockCurrentPlayer.getName()).thenReturn("TestPlayer");
  }

  @Test
  void testConstructor() {
    // Arrange & Act
    MonopolyController newController = new MonopolyController(mockBoardGame, mockBoardGameWriter, mockBoardGameReader, mockMediator);

    // Assert
    assertNotNull(newController);
  }

  @Test
  void testSetPlayerNames() {
    // Arrange
    List<String> playerNames = Arrays.asList("Player1", "Player2", "Player3");

    // Act
    controller.setPlayerNames(playerNames);

    // Assert
    // Since it calls super.setPlayerNames(), we can't directly verify the internal state
    // but we can verify it doesn't throw an exception
    assertDoesNotThrow(() -> controller.setPlayerNames(playerNames));
  }

  @Test
  void testHandlePlayerMove_WhenPlayerInJail() {
    // Arrange
    when(mockCurrentPlayer.isInJail()).thenReturn(true);

    // Act
    controller.handlePlayerMove();

    // Assert
    assertTrue(controller.isCurrentPlayerInJail());
    verify(mockBoardGame).notifyObservers();
    verify(mockBoardGame, never()).getDice();
  }

  @Test
  void testHandlePlayerMove_FirstTurn() {
    // Arrange
    when(mockCurrentPlayer.isInJail()).thenReturn(false);
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{3, 4});
    when(mockCurrentPlayer.getCurrentTile()).thenReturn(mockTile);
    when(mockTile.getAction()).thenReturn(null);

    // Act
    controller.handlePlayerMove();

    // Assert
    verify(mockDice).rollAllDice();
    verify(mockCurrentPlayer).move(7);
    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testHandlePlayerMove_WhenAwaitingPlayerAction() {
    // Arrange
    when(mockCurrentPlayer.isInJail()).thenReturn(false);
    controller.setAwaitingPlayerAction(true);

    // Act
    controller.handlePlayerMove();

    // Assert
    verify(mockBoardGame, never()).getDice();
    verify(mockCurrentPlayer, never()).move(anyInt());
  }

  @Test
  void testHandlePlayerMove_LandOnUnownedProperty() {
    // Arrange
    when(mockCurrentPlayer.isInJail()).thenReturn(false);
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{2, 3});
    when(mockCurrentPlayer.getCurrentTile()).thenReturn(mockPropertyTile);
    when(mockPropertyTile.getOwner()).thenReturn(null);
    when(mockPropertyTile.getId()).thenReturn(5);

    // Act
    controller.handlePlayerMove();

    // Assert
    verify(mockDice).rollAllDice();
    verify(mockCurrentPlayer).move(5);
    assertTrue(controller.isAwaitingPlayerAction());
    verify(mockBoardGame).notifyObservers();
  }

  @Test
  void testHandlePlayerMove_LandOnOwnedProperty() {
    // Arrange
    when(mockCurrentPlayer.isInJail()).thenReturn(false);
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{1, 2});
    when(mockCurrentPlayer.getCurrentTile()).thenReturn(mockPropertyTile);
    when(mockPropertyTile.getOwner()).thenReturn(mockOtherPlayer);
    when(mockPropertyTile.getId()).thenReturn(8);

    // Act
    controller.handlePlayerMove();

    // Assert
    verify(mockDice).rollAllDice();
    verify(mockCurrentPlayer).move(3);
    assertTrue(controller.isAwaitingRentAction());
    verify(mockBoardGame).notifyObservers();
  }

  @Test
  void testHandlePlayerMove_LandOnTileWithAction() {
    // Arrange
    when(mockCurrentPlayer.isInJail()).thenReturn(false);
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{4, 2});
    when(mockCurrentPlayer.getCurrentTile()).thenReturn(mockTile);
    when(mockTile.getAction()).thenReturn(mockTileAction);

    // Act
    controller.handlePlayerMove();

    // Assert
    verify(mockDice).rollAllDice();
    verify(mockCurrentPlayer).move(6);
    verify(mockTileAction).executeAction(mockCurrentPlayer, mockTile);
    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testHandlePlayerMove_PlayerSentToJailByAction() {
    // Arrange
    when(mockCurrentPlayer.isInJail()).thenReturn(false).thenReturn(true);
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{5, 1});
    when(mockCurrentPlayer.getCurrentTile()).thenReturn(mockTile);
    when(mockTile.getAction()).thenReturn(mockTileAction);

    // Act
    controller.handlePlayerMove();

    // Assert
    verify(mockTileAction).executeAction(mockCurrentPlayer, mockTile);
    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testHandlePlayerMove_InvalidDiceValues() {
    // Arrange
    when(mockCurrentPlayer.isInJail()).thenReturn(false);
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(null);
    when(mockCurrentPlayer.getCurrentTile()).thenReturn(mockTile);
    when(mockTile.getAction()).thenReturn(null);

    // Act
    controller.handlePlayerMove();

    // Assert
    verify(mockCurrentPlayer).move(2); // Default values [1,1]
    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testIsCurrentPlayerInJail_True() {
    // Arrange
    when(mockCurrentPlayer.isInJail()).thenReturn(true);

    // Act
    boolean result = controller.isCurrentPlayerInJail();

    // Assert
    assertTrue(result);
  }

  @Test
  void testIsCurrentPlayerInJail_False() {
    // Arrange
    when(mockCurrentPlayer.isInJail()).thenReturn(false);

    // Act
    boolean result = controller.isCurrentPlayerInJail();

    // Assert
    assertFalse(result);
  }

  @Test
  void testHandleJailRollDice_RolledSix() {
    // Arrange
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{6, 3});

    // Act
    controller.handleJailRollDice();

    // Assert
    verify(mockDice).rollAllDice();
    verify(mockCurrentPlayer).setInJail(false);
    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testHandleJailRollDice_DidNotRollSix() {
    // Arrange
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{3, 4});

    // Act
    controller.handleJailRollDice();

    // Assert
    verify(mockDice).rollAllDice();
    verify(mockCurrentPlayer, never()).setInJail(false);
    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testHandleJailRollDice_InvalidDiceValues() {
    // Arrange
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(null);

    // Act
    controller.handleJailRollDice();

    // Assert
    verify(mockCurrentPlayer, never()).setInJail(false); // Default [1,1] no six
    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testHandleJailPay_SuccessfulPayment() throws LowMoneyException {
    // Arrange
    doNothing().when(mockCurrentPlayer).payRent(50);

    // Act
    controller.handleJailPay();

    // Assert
    verify(mockCurrentPlayer).payRent(50);
    verify(mockCurrentPlayer).setInJail(false);
    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testHandleJailPay_InsufficientFunds() throws LowMoneyException {
    // Arrange
    doThrow(new LowMoneyException()).when(mockCurrentPlayer).payRent(50);

    // Act
    controller.handleJailPay();

    // Assert
    verify(mockCurrentPlayer).payRent(50);
    verify(mockCurrentPlayer, never()).setInJail(false);
    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testBuyPropertyForCurrentPlayer_Success() {
    // Arrange
    List<String> playerNames = Arrays.asList("Player1", "Player2");
    controller.setPlayerNames(playerNames); // Initialize playerNames to avoid NullPointerException
    controller.setAwaitingPlayerAction(true);
    controller.setPendingPropertyTile(mockPropertyTile);
    when(mockPropertyTile.getId()).thenReturn(10);

    // Act
    controller.buyPropertyForCurrentPlayer();

    // Assert
    assertFalse(controller.isAwaitingPlayerAction());
    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testBuyPropertyForCurrentPlayer_NotAwaitingAction() {
    // Arrange
    controller.setAwaitingPlayerAction(false);

    // Act
    controller.buyPropertyForCurrentPlayer();

    // Assert
    verify(mockMediator, never()).notify(any(), any());
  }

  @Test
  void testBuyPropertyForCurrentPlayer_NoPendingProperty() {
    // Arrange
    controller.setAwaitingPlayerAction(true);
    controller.setPendingPropertyTile(null);

    // Act
    controller.buyPropertyForCurrentPlayer();

    // Assert
    verify(mockMediator, never()).notify(any(), any());
  }

  @Test
  void testBuyProperty_Success() throws LowMoneyException {
    // Arrange
    doNothing().when(mockCurrentPlayer).buyProperty(mockPropertyTile);
    when(mockPropertyTile.getId()).thenReturn(5);

    // Act
    controller.buyProperty(mockCurrentPlayer, mockPropertyTile);

    // Assert
    verify(mockCurrentPlayer).buyProperty(mockPropertyTile);
  }

  @Test
  void testBuyProperty_InsufficientFunds() throws LowMoneyException {
    // Arrange
    doThrow(new LowMoneyException()).when(mockCurrentPlayer).buyProperty(mockPropertyTile);
    when(mockPropertyTile.getId()).thenReturn(5);

    // Act
    controller.buyProperty(mockCurrentPlayer, mockPropertyTile);

    // Assert
    verify(mockCurrentPlayer).buyProperty(mockPropertyTile);
    // Should not throw exception, just log warning
  }

  @Test
  void testSkipActionForCurrentPlayer_Success() {
    // Arrange
    List<String> playerNames = Arrays.asList("Player1", "Player2");
    controller.setPlayerNames(playerNames); // Initialize playerNames to avoid NullPointerException
    controller.setAwaitingPlayerAction(true);

    // Act
    controller.skipActionForCurrentPlayer();

    // Assert
    assertFalse(controller.isAwaitingPlayerAction());
    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testSkipActionForCurrentPlayer_NotAwaitingAction() {
    // Arrange
    controller.setAwaitingPlayerAction(false);

    // Act
    controller.skipActionForCurrentPlayer();

    // Assert
    verify(mockMediator, never()).notify(any(), any());
  }

  @Test
  void testPayRentForCurrentPlayer_Success() {
    // Arrange
    List<String> playerNames = Arrays.asList("Player1", "Player2");
    controller.setPlayerNames(playerNames); // Initialize playerNames to avoid NullPointerException
    controller.setAwaitingRentAction(true);
    controller.setPendingRentPropertyTile(mockPropertyTile);

    // Act
    controller.payRentForCurrentPlayer();

    // Assert
    assertFalse(controller.isAwaitingRentAction());
    verify(mockMediator).notify(controller, "nextPlayer");
  }

  @Test
  void testPayRentForCurrentPlayer_NotAwaitingRentAction() {
    // Arrange
    controller.setAwaitingRentAction(false);

    // Act
    controller.payRentForCurrentPlayer();

    // Assert
    verify(mockMediator, never()).notify(any(), any());
  }

  @Test
  void testPayRentForCurrentPlayer_NoPendingRentProperty() {
    // Arrange
    controller.setAwaitingRentAction(true);
    controller.setPendingRentPropertyTile(null);

    // Act
    controller.payRentForCurrentPlayer();

    // Assert
    verify(mockMediator, never()).notify(any(), any());
  }

  @Test
  void testPayRent_Success() throws LowMoneyException {
    // Arrange
    when(mockPropertyTile.getRent()).thenReturn(100);
    when(mockPropertyTile.getId()).thenReturn(7);
    doNothing().when(mockCurrentPlayer).payRent(100);

    // Act
    controller.payRent(mockCurrentPlayer, mockPropertyTile);

    // Assert
    verify(mockCurrentPlayer).payRent(100);
  }

  @Test
  void testPayRent_InsufficientFunds() throws LowMoneyException {
    // Arrange
    when(mockPropertyTile.getRent()).thenReturn(100);
    when(mockPropertyTile.getId()).thenReturn(7);
    doThrow(new LowMoneyException()).when(mockCurrentPlayer).payRent(100);

    // Act
    controller.payRent(mockCurrentPlayer, mockPropertyTile);

    // Assert
    verify(mockCurrentPlayer).payRent(100);
    // Should not throw exception, just log warning
  }

  @Test
  void testGetLastDiceRolls() {
    // Arrange
    int[] expectedValues = {4, 5};
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(expectedValues);

    // Act
    int[] result = controller.getLastDiceRolls();

    // Assert
    assertArrayEquals(expectedValues, result);
  }

  @Test
  void testRollDice() {
    // Arrange
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{2, 6});

    // Act
    controller.rollDice();

    // Assert
    verify(mockDice).rollAllDice();
  }

  @Test
  void testGetLastDiceSum() {
    // Arrange
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(new int[]{3, 5});

    // Act
    int result = controller.getLastDiceSum();

    // Assert
    assertEquals(8, result);
  }

  @Test
  void testGetLastDiceSum_NullValues() {
    // Arrange
    when(mockBoardGame.getCurrentDiceValues()).thenReturn(null);

    // Act
    int result = controller.getLastDiceSum();

    // Assert
    assertEquals(0, result);
  }

  @Test
  void testIsAwaitingPlayerAction() {
    // Arrange
    controller.setAwaitingPlayerAction(true);

    // Act & Assert
    assertTrue(controller.isAwaitingPlayerAction());

    // Arrange
    controller.setAwaitingPlayerAction(false);

    // Act & Assert
    assertFalse(controller.isAwaitingPlayerAction());
  }

  @Test
  void testIsAwaitingRentAction() {
    // Arrange
    controller.setAwaitingRentAction(true);

    // Act & Assert
    assertTrue(controller.isAwaitingRentAction());

    // Arrange
    controller.setAwaitingRentAction(false);

    // Act & Assert
    assertFalse(controller.isAwaitingRentAction());
  }

  @Test
  void testSettersAndGetters() {
    // Test setAwaitingPlayerAction
    controller.setAwaitingPlayerAction(true);
    assertTrue(controller.isAwaitingPlayerAction());

    // Test setPendingPropertyTile
    controller.setPendingPropertyTile(mockPropertyTile);
    // No getter available, but we can test through buyPropertyForCurrentPlayer

    // Test setAwaitingRentAction
    controller.setAwaitingRentAction(true);
    assertTrue(controller.isAwaitingRentAction());

    // Test setPendingRentPropertyTile
    controller.setPendingRentPropertyTile(mockPropertyTile);
    // No getter available, but we can test through payRentForCurrentPlayer
  }
}