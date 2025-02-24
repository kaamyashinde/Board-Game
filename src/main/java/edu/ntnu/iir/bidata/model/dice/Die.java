package edu.ntnu.iir.bidata.model.dice;

import lombok.Getter;

import java.util.Random;


/**
 * Die class containing a simple random number generator.
 *
 * @author kaamyashinde
 * @version 1.0.1
 */
@Getter
public class Die {
  private final Random rand;
  private int lastRolledValue;

  /**
   * The constructor that initialises the variable storing the random num generation options.
   */

  public Die() {
    rand = new Random();
  }

  /**
   * Generation of a random number between 1-6 and storing it to the lastRolledValue.
   */
  public void roll() {
    lastRolledValue = rand.nextInt(6) + 1;
  }

}