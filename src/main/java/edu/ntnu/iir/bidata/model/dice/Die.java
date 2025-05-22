package edu.ntnu.iir.bidata.model.dice;

import java.util.Random;
import lombok.Getter;

/**
 * Die class containing a simple random number generator.
 *
 * @author kaamyashinde
 * @version 1.0.1
 */
@Getter
public class Die {
  private static final Random RAND = new Random();
  private int lastRolledValue;

  /** The constructor that initialises the variable storing the random num generation options. */
  public Die() {
    // No need to create a new Random instance for each die
  }

  /** Generation of a random number between 1-6 and storing it to the lastRolledValue. */
  public void roll() {
    lastRolledValue = RAND.nextInt(6) + 1;
  }
}
