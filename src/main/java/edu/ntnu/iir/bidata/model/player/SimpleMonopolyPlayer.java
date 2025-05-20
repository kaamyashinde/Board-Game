package edu.ntnu.iir.bidata.model.player;

import edu.ntnu.iir.bidata.model.exception.LowMoneyException;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
/**
 * A class that represents a player in the monopoly game.
 * 
 * @author Kaamya Shinde
 * @version 1.0
 */

public class SimpleMonopolyPlayer extends Player {
    @Getter
    @Setter
    private int money;
    @Getter
    @Setter
    private List<PropertyTile> ownedProperties;
    @Getter
    @Setter
    private boolean inJail = false;
    @Getter
    @Setter
    private boolean paidToLeaveJail = false;
    @Getter
    @Setter
    private boolean canLeaveJailNextTurn = false;

    /**
     * Constructor for the SimpleMonopolyPlayer class.
     * 
     * @param name The name of the player.
     */
    public SimpleMonopolyPlayer(String name) {
        super(name);
        this.money = 1500;
        this.ownedProperties = new ArrayList<>();
    }

    /**
     * No-argument constructor for Gson deserialization.
     */
    public SimpleMonopolyPlayer() {
        super("");
        this.money = 1500;
        this.ownedProperties = new ArrayList<>();
    }

    /**
     * Buys a property.
     * 
     * @param propertyTile The property to buy.
     */
    public void buyProperty(PropertyTile propertyTile) throws LowMoneyException {
        deductMoney(propertyTile.getPrice());
        ownedProperties.add(propertyTile);
        propertyTile.setOwner(this);
    }

    /**
     * Pays rent.
     * 
     * @param amount The amount to pay.
     */
    public void payRent(int amount) throws LowMoneyException {
        deductMoney(amount);
    }

    /**
     * Deducts money from the player's balance.
     * 
     * @param amount The amount to deduct.
     * @throws LowMoneyException If the player does not have enough money.
     */

    private void deductMoney(int amount) throws LowMoneyException {
        if (money >= amount) {
            money -= amount;
        } else {
            throw new LowMoneyException();
        }
    }

    /**
     * Collects money.
     */

    public void collectMoney(int amount) {
        money += amount;
    }

    public void goToJail() {
        this.inJail = true;
        this.paidToLeaveJail = false;
        this.canLeaveJailNextTurn = false;
    }

    public void leaveJail() {
        this.inJail = false;
        this.paidToLeaveJail = false;
        this.canLeaveJailNextTurn = false;
    }

    public boolean isInJail() {
        return inJail;
    }
}
