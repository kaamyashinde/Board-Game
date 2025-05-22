package edu.ntnu.iir.bidata.model.tile.actions.monopoly;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.ntnu.iir.bidata.model.exception.LowMoneyException;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;

import java.util.logging.Level;
import java.util.logging.Logger;

class PayRentActionTest {

    @Mock
    private Player mockPlayer;
    @Mock
    private SimpleMonopolyPlayer mockMonopolyPlayer;
    @Mock
    private PropertyTile mockPropertyTile;
    @Mock
    private Tile mockTile;
    @Mock
    private Logger mockLogger;

    private PayRentAction action;
    private SimpleMonopolyPlayer realPlayer;
    private PropertyTile realProperty;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        action = new PayRentAction();
        realPlayer = new SimpleMonopolyPlayer("TestPlayer");
        realProperty = new PropertyTile(1, 200, 50, 1);
    }

    private void setPlayerMoney(SimpleMonopolyPlayer player, int amount) {
        try {
            java.lang.reflect.Field moneyField = SimpleMonopolyPlayer.class.getDeclaredField("money");
            moneyField.setAccessible(true);
            moneyField.setInt(player, amount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testExecuteAction_SimpleMonopolyPlayer_Success() throws LowMoneyException {
        setPlayerMoney(realPlayer, 100);

        action.executeAction(realPlayer, realProperty);

        assertEquals(50, realPlayer.getMoney());
    }

    @Test
    void testExecuteAction_SimpleMonopolyPlayer_InsufficientFunds() {
        setPlayerMoney(realPlayer, 20);

        action.executeAction(realPlayer, realProperty);

        assertEquals(20, realPlayer.getMoney());
    }

    @Test
    void testExecuteAction_SimpleMonopolyPlayer_ExactAmount() throws LowMoneyException {
        setPlayerMoney(realPlayer, 50);

        action.executeAction(realPlayer, realProperty);

        assertEquals(0, realPlayer.getMoney());
    }

    @Test
    void testExecuteAction_SimpleMonopolyPlayer_ZeroRent() {
        PropertyTile freeProperty = new PropertyTile(2, 100, 0, 1);
        setPlayerMoney(realPlayer, 100);

        action.executeAction(realPlayer, freeProperty);

        assertEquals(100, realPlayer.getMoney());
    }

    @Test
    void testExecuteAction_SimpleMonopolyPlayer_HighRent() {
        PropertyTile expensiveProperty = new PropertyTile(3, 500, 300, 1);
        setPlayerMoney(realPlayer, 1000);

        action.executeAction(realPlayer, expensiveProperty);

        assertEquals(700, realPlayer.getMoney());
    }

    @Test
    void testExecuteAction_RegularPlayer_NotSimpleMonopolyPlayer() {
        action.executeAction(mockPlayer, mockPropertyTile);

        verifyNoInteractions(mockPropertyTile);
    }

    @Test
    void testExecuteAction_NullPlayer() {
        assertDoesNotThrow(() -> action.executeAction(null, mockPropertyTile));
    }

    @Test
    void testExecuteAction_WithMockedPlayer_Success() throws LowMoneyException {
        when(mockPropertyTile.getRent()).thenReturn(75);
        doNothing().when(mockMonopolyPlayer).payRent(75);

        action.executeAction(mockMonopolyPlayer, mockPropertyTile);

        verify(mockMonopolyPlayer).payRent(75);
        verify(mockPropertyTile).getRent();
    }

    @Test
    void testExecuteAction_WithMockedPlayer_LowMoneyException() throws LowMoneyException {
        when(mockPropertyTile.getRent()).thenReturn(100);
        doThrow(new LowMoneyException()).when(mockMonopolyPlayer).payRent(100);

        assertDoesNotThrow(() -> action.executeAction(mockMonopolyPlayer, mockPropertyTile));

        verify(mockMonopolyPlayer).payRent(100);
        verify(mockPropertyTile).getRent();
    }

    @Test
    void testExecuteAction_NonPropertyTile() {
        setPlayerMoney(realPlayer, 100);

        assertThrows(ClassCastException.class, () -> {
            action.executeAction(realPlayer, mockTile);
        });
    }

    @Test
    void testGetDescription() {
        String description = action.getDescription();

        assertEquals("Pay Rent", description);
        assertNotNull(description);
        assertFalse(description.isEmpty());
    }

    @Test
    void testExecuteAction_MultipleRentPayments() {
        PropertyTile property1 = new PropertyTile(1, 200, 30, 1);
        PropertyTile property2 = new PropertyTile(2, 150, 25, 1);
        setPlayerMoney(realPlayer, 200);

        action.executeAction(realPlayer, property1);
        assertEquals(170, realPlayer.getMoney());

        action.executeAction(realPlayer, property2);
        assertEquals(145, realPlayer.getMoney());
    }

    @Test
    void testExecuteAction_PlayerWithZeroMoney() {
        setPlayerMoney(realPlayer, 0);

        action.executeAction(realPlayer, realProperty);

        assertEquals(0, realPlayer.getMoney());
    }

    @Test
    void testExecuteAction_PlayerWithNegativeMoney() {
        setPlayerMoney(realPlayer, -50);

        action.executeAction(realPlayer, realProperty);

        assertEquals(-50, realPlayer.getMoney());
    }

    @Test
    void testExecuteAction_DifferentPlayerTypes() {
        Player regularPlayer = new Player("RegularPlayer");
        SimpleMonopolyPlayer monopolyPlayer = new SimpleMonopolyPlayer("MonopolyPlayer");
        setPlayerMoney(monopolyPlayer, 100);

        action.executeAction(regularPlayer, realProperty);
        action.executeAction(monopolyPlayer, realProperty);

        assertEquals(50, monopolyPlayer.getMoney());
    }

    @Test
    void testExecuteAction_PropertyTileWithDifferentRents() {
        PropertyTile lowRentProperty = new PropertyTile(1, 100, 10, 1);
        PropertyTile mediumRentProperty = new PropertyTile(2, 200, 50, 1);
        PropertyTile highRentProperty = new PropertyTile(3, 500, 100, 1);

        setPlayerMoney(realPlayer, 200);

        action.executeAction(realPlayer, lowRentProperty);
        assertEquals(190, realPlayer.getMoney());

        action.executeAction(realPlayer, mediumRentProperty);
        assertEquals(140, realPlayer.getMoney());

        action.executeAction(realPlayer, highRentProperty);
        assertEquals(40, realPlayer.getMoney());
    }

    @Test
    void testExecuteAction_EdgeCase_MaxIntegerRent() {
        PropertyTile maxRentProperty = new PropertyTile(1, Integer.MAX_VALUE, Integer.MAX_VALUE, 1);
        setPlayerMoney(realPlayer, Integer.MAX_VALUE);

        action.executeAction(realPlayer, maxRentProperty);

        assertEquals(0, realPlayer.getMoney());
    }

    @Test
    void testExecuteAction_ConsistentBehavior() {
        setPlayerMoney(realPlayer, 100);

        action.executeAction(realPlayer, realProperty);
        int moneyAfterFirst = realPlayer.getMoney();

        setPlayerMoney(realPlayer, 100);
        action.executeAction(realPlayer, realProperty);
        int moneyAfterSecond = realPlayer.getMoney();

        assertEquals(moneyAfterFirst, moneyAfterSecond);
    }

    @Test
    void testExecuteAction_WithInheritedPlayer() {
        class ExtendedMonopolyPlayer extends SimpleMonopolyPlayer {
            public ExtendedMonopolyPlayer(String name) {
                super(name);
            }
        }

        ExtendedMonopolyPlayer extendedPlayer = new ExtendedMonopolyPlayer("Extended");
        setPlayerMoney(extendedPlayer, 100);

        action.executeAction(extendedPlayer, realProperty);

        assertEquals(50, extendedPlayer.getMoney());
    }

    @Test
    void testActionIntegrity() {
        PayRentAction action1 = new PayRentAction();
        PayRentAction action2 = new PayRentAction();

        assertEquals(action1.getDescription(), action2.getDescription());

        setPlayerMoney(realPlayer, 100);
        action1.executeAction(realPlayer, realProperty);
        int moneyAfterAction1 = realPlayer.getMoney();

        setPlayerMoney(realPlayer, 100);
        action2.executeAction(realPlayer, realProperty);
        int moneyAfterAction2 = realPlayer.getMoney();

        assertEquals(moneyAfterAction1, moneyAfterAction2);
    }

    @Test
    void testExecuteAction_BoundaryConditions() {
        PropertyTile minRentProperty = new PropertyTile(1, 1, 1, 1);
        setPlayerMoney(realPlayer, 1);

        action.executeAction(realPlayer, minRentProperty);
        assertEquals(0, realPlayer.getMoney());
    }
}