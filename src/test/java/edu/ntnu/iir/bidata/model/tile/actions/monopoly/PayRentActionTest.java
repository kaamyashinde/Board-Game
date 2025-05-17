package edu.ntnu.iir.bidata.model.tile.actions.monopoly;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;

public class PayRentActionTest {
    private PayRentAction action;
    private SimpleMonopolyPlayer player;
    private PropertyTile property;

    // Helper to set money using reflection (since there's no setter)
    private void setPlayerMoney(SimpleMonopolyPlayer player, int amount) {
        try {
            java.lang.reflect.Field moneyField = SimpleMonopolyPlayer.class.getDeclaredField("money");
            moneyField.setAccessible(true);
            moneyField.setInt(player, amount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        action = new PayRentAction();
        player = new SimpleMonopolyPlayer("Alice");
        property = new PropertyTile(1, 200, 50, 1);
    }

    @Test
    void testPayRentSuccessfully() {
        setPlayerMoney(player, 100);
        action.executeAction(player, property);
        assertEquals(50, player.getMoney()); // 100 - 50 rent
    }

    @Test
    void testPayRentWithInsufficientFunds() {
        setPlayerMoney(player, 20); // Less than rent
        action.executeAction(player, property);
        // Money should remain unchanged since rent can't be paid
        assertEquals(20, player.getMoney());
    }
}
