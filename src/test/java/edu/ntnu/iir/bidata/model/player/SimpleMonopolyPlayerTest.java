package edu.ntnu.iir.bidata.model.player;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import java.util.List;
import edu.ntnu.iir.bidata.model.exception.LowMoneyException;

class SimpleMonopolyPlayerTest {

    @Mock
    private PropertyTile mockProperty1;
    @Mock
    private PropertyTile mockProperty2;

    private SimpleMonopolyPlayer player;
    private static final String PLAYER_NAME = "Test Player";
    private static final String TOKEN_IMAGE = "token.png";
    private static final int INITIAL_MONEY = 1500;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        player = new SimpleMonopolyPlayer(PLAYER_NAME);
    }

    @Test
    void testConstructor_WithName() {
        // Arrange & Act
        SimpleMonopolyPlayer newPlayer = new SimpleMonopolyPlayer(PLAYER_NAME);

        // Assert
        assertEquals(PLAYER_NAME, newPlayer.getName());
        assertEquals(INITIAL_MONEY, newPlayer.getMoney());
        assertNotNull(newPlayer.getOwnedProperties());
        assertTrue(newPlayer.getOwnedProperties().isEmpty());
        assertFalse(newPlayer.isInJail());
        assertFalse(newPlayer.isPaidToLeaveJail());
        assertFalse(newPlayer.isCanLeaveJailNextTurn());
    }

    @Test
    void testConstructor_WithNameAndTokenImage() {
        // Arrange & Act
        SimpleMonopolyPlayer newPlayer = new SimpleMonopolyPlayer(PLAYER_NAME, TOKEN_IMAGE);

        // Assert
        assertEquals(PLAYER_NAME, newPlayer.getName());
        assertEquals(TOKEN_IMAGE, newPlayer.getTokenImage());
        assertEquals(INITIAL_MONEY, newPlayer.getMoney());
        assertNotNull(newPlayer.getOwnedProperties());
        assertTrue(newPlayer.getOwnedProperties().isEmpty());
        assertFalse(newPlayer.isInJail());
    }

    @Test
    void testConstructor_NoArguments() {
        // Arrange & Act
        SimpleMonopolyPlayer newPlayer = new SimpleMonopolyPlayer();

        // Assert
        assertEquals("", newPlayer.getName());
        assertEquals(INITIAL_MONEY, newPlayer.getMoney());
        assertNotNull(newPlayer.getOwnedProperties());
        assertTrue(newPlayer.getOwnedProperties().isEmpty());
        assertFalse(newPlayer.isInJail());
    }

    @Test
    void testGetMoney() {
        // Arrange & Act & Assert
        assertEquals(INITIAL_MONEY, player.getMoney());
    }

    @Test
    void testSetMoney() {
        // Arrange
        int newMoney = 2000;

        // Act
        player.setMoney(newMoney);

        // Assert
        assertEquals(newMoney, player.getMoney());
    }

    @Test
    void testGetOwnedProperties() {
        // Arrange & Act
        List<PropertyTile> properties = player.getOwnedProperties();

        // Assert
        assertNotNull(properties);
        assertTrue(properties.isEmpty());
    }

    @Test
    void testSetOwnedProperties() {
        // Arrange
        List<PropertyTile> newProperties = java.util.Arrays.asList(mockProperty1, mockProperty2);

        // Act
        player.setOwnedProperties(newProperties);

        // Assert
        assertEquals(newProperties, player.getOwnedProperties());
        assertEquals(2, player.getOwnedProperties().size());
    }

    @Test
    void testBuyProperty_Successfully() throws LowMoneyException {
        // Arrange
        PropertyTile property = new PropertyTile(1, 200, 20, 1);
        int expectedMoney = INITIAL_MONEY - property.getPrice();

        // Act
        player.buyProperty(property);

        // Assert
        assertEquals(expectedMoney, player.getMoney());
        assertTrue(player.getOwnedProperties().contains(property));
        assertEquals(player, property.getOwner());
    }

    @Test
    void testBuyProperty_WithInsufficientFunds() {
        // Arrange
        int propertyPrice = INITIAL_MONEY + 100;
        PropertyTile property = new PropertyTile(1, propertyPrice, 20, 1);

        // Act & Assert
        assertThrows(LowMoneyException.class, () -> player.buyProperty(property));

        // Verify money and properties haven't changed
        assertEquals(INITIAL_MONEY, player.getMoney());
        assertFalse(player.getOwnedProperties().contains(property));
    }

    @Test
    void testBuyProperty_WithExactMoney() throws LowMoneyException {
        // Arrange
        PropertyTile property = new PropertyTile(1, INITIAL_MONEY, 20, 1);

        // Act
        player.buyProperty(property);

        // Assert
        assertEquals(0, player.getMoney());
        assertTrue(player.getOwnedProperties().contains(property));
        assertEquals(player, property.getOwner());
    }

    @Test
    void testBuyMultipleProperties() throws LowMoneyException {
        // Arrange
        PropertyTile property1 = new PropertyTile(1, 200, 20, 1);
        PropertyTile property2 = new PropertyTile(2, 300, 30, 1);
        int expectedMoney = INITIAL_MONEY - property1.getPrice() - property2.getPrice();

        // Act
        player.buyProperty(property1);
        player.buyProperty(property2);

        // Assert
        assertEquals(expectedMoney, player.getMoney());
        assertEquals(2, player.getOwnedProperties().size());
        assertTrue(player.getOwnedProperties().contains(property1));
        assertTrue(player.getOwnedProperties().contains(property2));
    }

    @Test
    void testPayRent_Successfully() throws LowMoneyException {
        // Arrange
        int rentAmount = 100;
        int expectedMoney = INITIAL_MONEY - rentAmount;

        // Act
        player.payRent(rentAmount);

        // Assert
        assertEquals(expectedMoney, player.getMoney());
    }

    @Test
    void testPayRent_WithInsufficientFunds() {
        // Arrange
        int rentAmount = INITIAL_MONEY + 100;

        // Act & Assert
        assertThrows(LowMoneyException.class, () -> player.payRent(rentAmount));

        // Verify money hasn't changed
        assertEquals(INITIAL_MONEY, player.getMoney());
    }

    @Test
    void testPayRent_WithExactMoney() throws LowMoneyException {
        // Arrange
        int rentAmount = INITIAL_MONEY;

        // Act
        player.payRent(rentAmount);

        // Assert
        assertEquals(0, player.getMoney());
    }

    @Test
    void testPayRent_ZeroAmount() throws LowMoneyException {
        // Arrange & Act
        player.payRent(0);

        // Assert
        assertEquals(INITIAL_MONEY, player.getMoney());
    }

    @Test
    void testCollectMoney() {
        // Arrange
        int collectAmount = 200;
        int expectedMoney = INITIAL_MONEY + collectAmount;

        // Act
        player.collectMoney(collectAmount);

        // Assert
        assertEquals(expectedMoney, player.getMoney());
    }

    @Test
    void testCollectMoney_ZeroAmount() {
        // Arrange & Act
        player.collectMoney(0);

        // Assert
        assertEquals(INITIAL_MONEY, player.getMoney());
    }

    @Test
    void testCollectMoney_NegativeAmount() {
        // Arrange
        int negativeAmount = -100;
        int expectedMoney = INITIAL_MONEY + negativeAmount;

        // Act
        player.collectMoney(negativeAmount);

        // Assert
        assertEquals(expectedMoney, player.getMoney());
    }

    @Test
    void testGoToJail() {
        // Arrange
        player.setPaidToLeaveJail(true);
        player.setCanLeaveJailNextTurn(true);

        // Act
        player.goToJail();

        // Assert
        assertTrue(player.isInJail());
        assertFalse(player.isPaidToLeaveJail());
        assertFalse(player.isCanLeaveJailNextTurn());
    }

    @Test
    void testLeaveJail() {
        // Arrange
        player.setInJail(true);
        player.setPaidToLeaveJail(true);
        player.setCanLeaveJailNextTurn(true);

        // Act
        player.leaveJail();

        // Assert
        assertFalse(player.isInJail());
        assertFalse(player.isPaidToLeaveJail());
        assertFalse(player.isCanLeaveJailNextTurn());
    }

    @Test
    void testIsInJail_Initially() {
        // Arrange & Act & Assert
        assertFalse(player.isInJail());
    }

    @Test
    void testIsInJail_AfterGoingToJail() {
        // Arrange & Act
        player.goToJail();

        // Assert
        assertTrue(player.isInJail());
    }

    @Test
    void testSetInJail() {
        // Arrange & Act
        player.setInJail(true);

        // Assert
        assertTrue(player.isInJail());

        // Act
        player.setInJail(false);

        // Assert
        assertFalse(player.isInJail());
    }

    @Test
    void testGetSetPaidToLeaveJail() {
        // Arrange & Act & Assert
        assertFalse(player.isPaidToLeaveJail());

        // Act
        player.setPaidToLeaveJail(true);

        // Assert
        assertTrue(player.isPaidToLeaveJail());
    }

    @Test
    void testGetSetCanLeaveJailNextTurn() {
        // Arrange & Act & Assert
        assertFalse(player.isCanLeaveJailNextTurn());

        // Act
        player.setCanLeaveJailNextTurn(true);

        // Assert
        assertTrue(player.isCanLeaveJailNextTurn());
    }

    @Test
    void testMultipleTransactions() throws LowMoneyException {
        // Arrange
        PropertyTile property = new PropertyTile(1, 200, 20, 1);
        int rentAmount = 50;
        int collectAmount = 100;

        // Act
        player.buyProperty(property);
        player.payRent(rentAmount);
        player.collectMoney(collectAmount);

        // Assert
        int expectedMoney = INITIAL_MONEY - property.getPrice() - rentAmount + collectAmount;
        assertEquals(expectedMoney, player.getMoney());
        assertTrue(player.getOwnedProperties().contains(property));
    }

    @Test
    void testComplexScenario_JailAndProperties() throws LowMoneyException {
        // Arrange
        PropertyTile property1 = new PropertyTile(1, 300, 30, 1);
        PropertyTile property2 = new PropertyTile(2, 250, 25, 1);

        // Act
        player.buyProperty(property1);
        player.goToJail();
        player.collectMoney(200); // Collect money while in jail
        player.leaveJail();
        player.buyProperty(property2);

        // Assert
        int expectedMoney = INITIAL_MONEY - property1.getPrice() + 200 - property2.getPrice();
        assertEquals(expectedMoney, player.getMoney());
        assertEquals(2, player.getOwnedProperties().size());
        assertFalse(player.isInJail());
    }

    @Test
    void testDeductMoney_EdgeCase_ExactlyEnoughMoney() throws LowMoneyException {
        // Arrange
        player.setMoney(100);

        // Act
        player.payRent(100);

        // Assert
        assertEquals(0, player.getMoney());
    }

    @Test
    void testDeductMoney_EdgeCase_InsufficientByOne() {
        // Arrange
        player.setMoney(99);

        // Act & Assert
        assertThrows(LowMoneyException.class, () -> player.payRent(100));
        assertEquals(99, player.getMoney()); // Money should remain unchanged
    }
}