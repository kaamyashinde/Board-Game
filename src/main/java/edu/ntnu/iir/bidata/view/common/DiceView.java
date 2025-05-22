package edu.ntnu.iir.bidata.view.common;

import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * A graphical view representing a pair of dice using JavaFX. Each die is displayed as a square with
 * dots depicting the dice value. The class provides methods to set individual or simultaneous
 * values for the dice.
 */
public class DiceView extends HBox {
  private static final int SIZE = 50;
  private static final int DOT_SIZE = 6;
  private static final int PADDING = 10;
  private final SingleDieView die1;
  private final SingleDieView die2;

  /**
   * Constructs a new DiceView instance, initializing two dice represented as graphical components
   * and arranging them horizontally with specified spacing. Each die is initialized with a default
   * value of 1, and the initial layout of the two dice is configured within the container.
   */
  public DiceView() {
    setSpacing(16);
    die1 = new SingleDieView();
    die2 = new SingleDieView();
    getChildren().addAll(die1, die2);
    setValues(1, 1);
  }

  /**
   * Sets the values of the two dice represented in the DiceView. Updates the graphical
   * representation of each die to reflect the specified values.
   *
   * @param value1 the value to set for the first die (should be in the valid range for a die)
   * @param value2 the value to set for the second die (should be in the valid range for a die)
   */
  public void setValues(int value1, int value2) {
    die1.setValue(value1);
    die2.setValue(value2);
  }

  /**
   * Sets the same value to both dice represented in the DiceView. This method is provided for
   * backward compatibility and updates the graphical representation of both dice to reflect the
   * specified value.
   *
   * @param value the value to set for both dice (should be in the valid range for a die)
   */
  // For backward compatibility: sets both dice to the same value
  public void setValue(int value) {
    setValues(value, value);
  }

  /**
   * Represents a graphical view of a single die, providing a visual representation of a die face.
   * Each die face is customizable to show a value between 1 and 6 through graphical dots. The die
   * is represented as a square with rounded corners and black border, containing dots placed to
   * reflect the displayed value.
   */
  // Inner class for a single die
  private static class SingleDieView extends StackPane {
    private final Rectangle diceFace;
    private final Group dots;

    /**
     * Constructs a new SingleDieView instance that represents a graphical view of a single die
     * face. The die face is a square with rounded corners, a white fill, and a black border. The
     * initial die value is set to one, displaying a single dot in the center. A group of dots is
     * used to graphically represent the current die value.
     */
    public SingleDieView() {
      diceFace = new Rectangle(SIZE, SIZE);
      diceFace.setFill(Color.WHITE);
      diceFace.setStroke(Color.BLACK);
      diceFace.setStrokeWidth(2);
      diceFace.setArcWidth(10);
      diceFace.setArcHeight(10);
      dots = new Group();
      getChildren().addAll(diceFace, dots);
      setValue(1);
    }

    /**
     * Sets the value of the die and updates the graphical representation to display the
     * corresponding number of dots.
     *
     * @param value the number to be displayed on the die face; must be an integer between 1 and 6,
     *     inclusive. If the value is outside this range, no dots will be displayed.
     */
    public void setValue(int value) {
      dots.getChildren().clear();
      switch (value) {
        case 1:
          createDot(SIZE / 2, SIZE / 2);
          break;
        case 2:
          createDot(PADDING, PADDING);
          createDot(SIZE - PADDING, SIZE - PADDING);
          break;
        case 3:
          createDot(PADDING, PADDING);
          createDot(SIZE / 2, SIZE / 2);
          createDot(SIZE - PADDING, SIZE - PADDING);
          break;
        case 4:
          createDot(PADDING, PADDING);
          createDot(SIZE - PADDING, PADDING);
          createDot(PADDING, SIZE - PADDING);
          createDot(SIZE - PADDING, SIZE - PADDING);
          break;
        case 5:
          createDot(PADDING, PADDING);
          createDot(SIZE - PADDING, PADDING);
          createDot(SIZE / 2, SIZE / 2);
          createDot(PADDING, SIZE - PADDING);
          createDot(SIZE - PADDING, SIZE - PADDING);
          break;
        case 6:
          createDot(PADDING, PADDING);
          createDot(SIZE - PADDING, PADDING);
          createDot(PADDING, SIZE / 2);
          createDot(SIZE - PADDING, SIZE / 2);
          createDot(PADDING, SIZE - PADDING);
          createDot(SIZE - PADDING, SIZE - PADDING);
          break;
        default:
          break;
      }
    }

    /**
     * Creates a dot at the specified coordinates on the die face.
     *
     * @param x the x-coordinate of the dot's center
     * @param y the y-coordinate of the dot's center
     */
    private void createDot(double x, double y) {
      Circle dot = new Circle(x, y, DOT_SIZE);
      dot.setFill(Color.BLACK);
      dots.getChildren().add(dot);
    }
  }
}
