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
        SimpleMonopolyPlayer newPlayer = new SimpleMonopolyPlayer(PLAYER_NAME);

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
        SimpleMonopolyPlayer newPlayer = new SimpleMonopolyPlayer(PLAYER_NAME, TOKEN_IMAGE);

        assertEquals(PLAYER_NAME, newPlayer.getName());
        assertEquals(TOKEN_IMAGE, newPlayer.getTokenImage());
        assertEquals(INITIAL_MONEY, newPlayer.getMoney());
        assertNotNull(newPlayer.getOwnedProperties());
        assertTrue(newPlayer.getOwnedProperties().isEmpty());
        assertFalse(newPlayer.isInJail());
    }

    @Test
    void testConstructor_NoArguments() {
        SimpleMonopolyPlayer newPlayer = new SimpleMonopolyPlayer();

        assertEquals("", newPlayer.getName());
        assertEquals(INITIAL_MONEY, newPlayer.getMoney());
        assertNotNull(newPlayer.getOwnedProperties());
        assertTrue(newPlayer.getOwnedProperties().isEmpty());
        assertFalse(newPlayer.isInJail());
    }

    @Test
    void testGetMoney() {
        assertEquals(INITIAL_MONEY, player.getMoney());
    }

    @Test
    void testSetMoney() {
        int newMoney = 2000;

        player.setMoney(newMoney);

        assertEquals(newMoney, player.getMoney());
    }

    @Test
    void testGetOwnedProperties() {
        List<PropertyTile> properties = player.getOwnedProperties();

        assertNotNull(properties);
        assertTrue(properties.isEmpty());
    }

    @Test
    void testSetOwnedProperties() {
        List<PropertyTile> newProperties = java.util.Arrays.asList(mockProperty1, mockProperty2);

        player.setOwnedProperties(newProperties);

        assertEquals(newProperties, player.getOwnedProperties());
        assertEquals(2, player.getOwnedProperties().size());
    }

    @Test
    void testBuyProperty_Successfully() throws LowMoneyException {
        PropertyTile property = new PropertyTile(1, 200, 20, 1);
        int expectedMoney = INITIAL_MONEY - property.getPrice();

        player.buyProperty(property);

        assertEquals(expectedMoney, player.getMoney());
        assertTrue(player.getOwnedProperties().contains(property));
        assertEquals(player, property.getOwner());
    }

    @Test
    void testBuyProperty_WithInsufficientFunds() {
        int propertyPrice = INITIAL_MONEY + 100;
        PropertyTile property = new PropertyTile(1, propertyPrice, 20, 1);

        assertThrows(LowMoneyException.class, () -> player.buyProperty(property));

        assertEquals(INITIAL_MONEY, player.getMoney());
        assertFalse(player.getOwnedProperties().contains(property));
    }

    @Test
    void testBuyProperty_WithExactMoney() throws LowMoneyException {
        PropertyTile property = new PropertyTile(1, INITIAL_MONEY, 20, 1);

        player.buyProperty(property);

        assertEquals(0, player.getMoney());
        assertTrue(player.getOwnedProperties().contains(property));
        assertEquals(player, property.getOwner());
    }

    @Test
    void testBuyMultipleProperties() throws LowMoneyException {
        PropertyTile property1 = new PropertyTile(1, 200, 20, 1);
        PropertyTile property2 = new PropertyTile(2, 300, 30, 1);
        int expectedMoney = INITIAL_MONEY - property1.getPrice() - property2.getPrice();

        player.buyProperty(property1);
        player.buyProperty(property2);

        assertEquals(expectedMoney, player.getMoney());
        assertEquals(2, player.getOwnedProperties().size());
        assertTrue(player.getOwnedProperties().contains(property1));
        assertTrue(player.getOwnedProperties().contains(property2));
    }

    @Test
    void testPayRent_Successfully() throws LowMoneyException {
        int rentAmount = 100;
        int expectedMoney = INITIAL_MONEY - rentAmount;

        player.payRent(rentAmount);

        assertEquals(expectedMoney, player.getMoney());
    }

    @Test
    void testPayRent_WithInsufficientFunds() {
        int rentAmount = INITIAL_MONEY + 100;

        assertThrows(LowMoneyException.class, () -> player.payRent(rentAmount));

        assertEquals(INITIAL_MONEY, player.getMoney());
    }

    @Test
    void testPayRent_WithExactMoney() throws LowMoneyException {
        int rentAmount = INITIAL_MONEY;

        player.payRent(rentAmount);

        assertEquals(0, player.getMoney());
    }

    @Test
    void testPayRent_ZeroAmount() throws LowMoneyException {
        player.payRent(0);

        assertEquals(INITIAL_MONEY, player.getMoney());
    }

    @Test
    void testCollectMoney() {
        int collectAmount = 200;
        int expectedMoney = INITIAL_MONEY + collectAmount;

        player.collectMoney(collectAmount);

        assertEquals(expectedMoney, player.getMoney());
    }

    @Test
    void testCollectMoney_ZeroAmount() {
        player.collectMoney(0);

        assertEquals(INITIAL_MONEY, player.getMoney());
    }

    @Test
    void testCollectMoney_NegativeAmount() {
        int negativeAmount = -100;
        int expectedMoney = INITIAL_MONEY + negativeAmount;

        player.collectMoney(negativeAmount);

        assertEquals(expectedMoney, player.getMoney());
    }

    @Test
    void testGoToJail() {
        player.setPaidToLeaveJail(true);
        player.setCanLeaveJailNextTurn(true);

        player.goToJail();

        assertTrue(player.isInJail());
        assertFalse(player.isPaidToLeaveJail());
        assertFalse(player.isCanLeaveJailNextTurn());
    }

    @Test
    void testLeaveJail() {
        player.setInJail(true);
        player.setPaidToLeaveJail(true);
        player.setCanLeaveJailNextTurn(true);

        player.leaveJail();

        assertFalse(player.isInJail());
        assertFalse(player.isPaidToLeaveJail());
        assertFalse(player.isCanLeaveJailNextTurn());
    }

    @Test
    void testIsInJail_Initially() {
        assertFalse(player.isInJail());
    }

    @Test
    void testIsInJail_AfterGoingToJail() {
        player.goToJail();

        assertTrue(player.isInJail());
    }

    @Test
    void testSetInJail() {
        player.setInJail(true);

        assertTrue(player.isInJail());

        player.setInJail(false);

        assertFalse(player.isInJail());
    }

    @Test
    void testGetSetPaidToLeaveJail() {
        assertFalse(player.isPaidToLeaveJail());

        player.setPaidToLeaveJail(true);

        assertTrue(player.isPaidToLeaveJail());
    }

    @Test
    void testGetSetCanLeaveJailNextTurn() {
        assertFalse(player.isCanLeaveJailNextTurn());

        player.setCanLeaveJailNextTurn(true);

        assertTrue(player.isCanLeaveJailNextTurn());
    }

    @Test
    void testMultipleTransactions() throws LowMoneyException {
        PropertyTile property = new PropertyTile(1, 200, 20, 1);
        int rentAmount = 50;
        int collectAmount = 100;

        player.buyProperty(property);
        player.payRent(rentAmount);
        player.collectMoney(collectAmount);

        int expectedMoney = INITIAL_MONEY - property.getPrice() - rentAmount + collectAmount;
        assertEquals(expectedMoney, player.getMoney());
        assertTrue(player.getOwnedProperties().contains(property));
    }

    @Test
    void testComplexScenario_JailAndProperties() throws LowMoneyException {
        PropertyTile property1 = new PropertyTile(1, 300, 30, 1);
        PropertyTile property2 = new PropertyTile(2, 250, 25, 1);

        player.buyProperty(property1);
        player.goToJail();
        player.collectMoney(200);
        player.leaveJail();
        player.buyProperty(property2);

        int expectedMoney = INITIAL_MONEY - property1.getPrice() + 200 - property2.getPrice();
        assertEquals(expectedMoney, player.getMoney());
        assertEquals(2, player.getOwnedProperties().size());
        assertFalse(player.isInJail());
    }

    @Test
    void testDeductMoney_EdgeCase_ExactlyEnoughMoney() throws LowMoneyException {
        player.setMoney(100);

        player.payRent(100);

        assertEquals(0, player.getMoney());
    }

    @Test
    void testDeductMoney_EdgeCase_InsufficientByOne() {
        player.setMoney(99);

        assertThrows(LowMoneyException.class, () -> player.payRent(100));
        assertEquals(99, player.getMoney());
    }
}