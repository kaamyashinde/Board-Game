package edu.ntnu.iir.bidata.model.dice;

import edu.ntnu.iir.bidata.utils.ParameterValidation;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class handling multiple instances of the class {@link Die}.
 *
 * @author kamyashinde
 * @version 1.0.1
 */

public class Dice {
  /**
   * The ArrayList that will contain the different dice that are created.
   */
  private final ArrayList<Die> dice;

  /**
   * The constructor that takes in the number of dice as a parameter to
   * create a certain number of Dice in the list.
   * <p>
   * The use of Collections.nCopies was suggested by CoPilot when asked
   * for a replacement for the use of a for loop.
   * </p>
   *
   * @param numberOfDice the number of dice to be stored in the list
   */
  public Dice(int numberOfDice) throws IllegalArgumentException {
    ParameterValidation.validateNonZeroPositiveInteger(numberOfDice, "number of dice to create the list");
    dice = new ArrayList<>(Collections.nCopies(numberOfDice, new Die()));
  }

  /**
   * The method responsible to roll all of the dice and update their corresponding last rolled value.
   */
  public void rollAllDice() {
    dice.forEach(Die::roll);
  }

  /**
   * Return the last rolled value by a specific die from the list of dice.
   *
   * @param dieNumber the index of the die to be rolled from the list.
   * @return The number rolled on the selected die.
   */
  public int getDie(int dieNumber) {
    ParameterValidation.validateZeroPositiveInteger(dieNumber, "index of the dice");
    return dice.get(dieNumber).getLastRolledValue();
  }

  /**
   * Sum of all the rolled values stored in the dice list.
   *
   * @return the sum of the values rolled by all the dice.
   */
  public int sumOfRolledValues() {
    return dice.stream().mapToInt(Die::getLastRolledValue).sum();
  }

}