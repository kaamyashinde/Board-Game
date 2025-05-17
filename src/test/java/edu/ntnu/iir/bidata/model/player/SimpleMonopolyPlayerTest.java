package edu.ntnu.iir.bidata.model.player;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import java.util.List;
import edu.ntnu.iir.bidata.model.exception.LowMoneyException;

public class SimpleMonopolyPlayerTest {
    private SimpleMonopolyPlayer player;
    private static final String PLAYER_NAME = "Test Player";
    private static final int INITIAL_MONEY = 1500;
    
    @BeforeEach
    void setUp() {
        player = new SimpleMonopolyPlayer(PLAYER_NAME);
    }
    
    @Test
    void testConstructor() {
        assertEquals(PLAYER_NAME, player.getName());
        assertEquals(INITIAL_MONEY, player.getMoney());
        assertNotNull(player.getOwnedProperties());
        assertTrue(player.getOwnedProperties().isEmpty());
    }
    
    @Test
    void testGetMoney() {
        assertEquals(INITIAL_MONEY, player.getMoney());
    }
    
    @Test
    void testGetOwnedProperties() {
        List<PropertyTile> properties = player.getOwnedProperties();
        assertNotNull(properties);
        assertTrue(properties.isEmpty());
    }

    @Test
    void testBuyPropertySuccessfully() throws LowMoneyException {
        PropertyTile property = new PropertyTile(1, 200, 20, 1);
        int expectedMoney = INITIAL_MONEY - property.getPrice();
        
        player.buyProperty(property);
        
        assertEquals(expectedMoney, player.getMoney());
    }
    
    @Test
    void testBuyPropertyWithInsufficientFunds() {
        int propertyPrice = INITIAL_MONEY + 100;
        PropertyTile property = new PropertyTile(1, propertyPrice, 20, 1);
        assertThrows(LowMoneyException.class, () -> {
            player.buyProperty(property);
        });
        
        // Verify money hasn't changed
        assertEquals(INITIAL_MONEY, player.getMoney());
    }
    
    @Test
    void testPayRentSuccessfully() throws LowMoneyException {
        int rentAmount = 100;
        int expectedMoney = INITIAL_MONEY - rentAmount;
        
        player.payRent(rentAmount);
        
        assertEquals(expectedMoney, player.getMoney());
    }
    
    @Test
    void testPayRentWithInsufficientFunds() {
        int rentAmount = INITIAL_MONEY + 100;
        
        assertThrows(LowMoneyException.class, () -> {
            player.payRent(rentAmount);
        });
        
        // Verify money hasn't changed
        assertEquals(INITIAL_MONEY, player.getMoney());
    }
    
    @Test
    void testMultipleTransactions() throws LowMoneyException {
        // Buy a property
        PropertyTile property = new PropertyTile(1, 200, 20, 1);
        player.buyProperty(property);
        assertEquals(INITIAL_MONEY - property.getPrice(), player.getMoney());
        
        // Pay rent
        int rentAmount = 50;
        player.payRent(rentAmount);
        assertEquals(INITIAL_MONEY - property.getPrice() - rentAmount, player.getMoney());
    }
}