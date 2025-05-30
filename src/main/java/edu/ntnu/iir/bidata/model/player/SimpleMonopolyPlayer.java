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
  @Getter @Setter private int money;
  @Getter @Setter private List<PropertyTile> ownedProperties;
  @Getter @Setter private boolean inJail = false;
  @Getter @Setter private boolean paidToLeaveJail = false;
  @Getter @Setter private boolean canLeaveJailNextTurn = false;

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
   * Constructor for the SimpleMonopolyPlayer class with name and token image.
   *
   * @param name The name of the player.
   * @param tokenImage The image path or name for the player's token.
   */
  public SimpleMonopolyPlayer(String name, String tokenImage) {
    super(name, tokenImage);
    this.money = 1500;
    this.ownedProperties = new ArrayList<>();
  }

  /** No-argument constructor for Gson deserialization. */
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
   * Pays rent.
   *
   * @param amount The amount to pay.
   */
  public void payRent(int amount) throws LowMoneyException {
    deductMoney(amount);
  }

  /** Collects money. */
  public void collectMoney(int amount) {
    money += amount;
  }

  /**
   * Sends the player to jail by updating their status. This method sets the {@code inJail} flag to
   * {@code true}, indicating the player is in jail. It also resets the {@code paidToLeaveJail} flag
   * and {@code canLeaveJailNextTurn} flag to {@code false}, ensuring the player cannot leave jail
   * immediately or has not yet paid to leave.
   */
  public void goToJail() {
    this.inJail = true;
    this.paidToLeaveJail = false;
    this.canLeaveJailNextTurn = false;
  }

  /**
   * Allows the player to leave jail by updating their status. This method sets the {@code inJail}
   * flag to {@code false}, indicating the player is no longer in jail. It also resets the {@code
   * paidToLeaveJail} and {@code canLeaveJailNextTurn} flags to {@code false}, ensuring the player
   * has not paid to leave jail and cannot leave jail under special conditions on the next turn.
   */
  public void leaveJail() {
    this.inJail = false;
    this.paidToLeaveJail = false;
    this.canLeaveJailNextTurn = false;
  }

  public boolean isInJail() {
    return inJail;
  }
}
