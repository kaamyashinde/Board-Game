package edu.ntnu.iir.bidata.view.common;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class providing reusable animation functions for JavaFX board games
 */
public class AnimationUtils {

  /**
   * Creates a smooth movement animation for a node
   * @param node The node to animate
   * @param toX Target X position
   * @param toY Target Y position
   * @param durationMillis Animation duration in milliseconds
   * @param onFinished Action to run when animation completes (can be null)
   * @return The created transition
   */
  public static TranslateTransition createMoveAnimation(Node node, double toX, double toY,
      int durationMillis, Runnable onFinished) {
    TranslateTransition transition = new TranslateTransition(Duration.millis(durationMillis), node);
    transition.setToX(toX);
    transition.setToY(toY);
    if (onFinished != null) {
      transition.setOnFinished(e -> onFinished.run());
    }
    return transition;
  }

  /**
   * Creates a bounce effect animation for a node
   * @param node The node to animate
   * @param durationMillis Animation duration in milliseconds
   * @return The created transition
   */
  public static SequentialTransition createBounceAnimation(Node node, int durationMillis) {
    ScaleTransition scaleUp = new ScaleTransition(Duration.millis(durationMillis / 2), node);
    scaleUp.setToX(1.2);
    scaleUp.setToY(1.2);

    ScaleTransition scaleDown = new ScaleTransition(Duration.millis(durationMillis / 2), node);
    scaleDown.setToX(1.0);
    scaleDown.setToY(1.0);

    SequentialTransition bounceAnimation = new SequentialTransition(scaleUp, scaleDown);
    return bounceAnimation;
  }

  /**
   * Creates a dice roll animation
   * @param diceView The dice view to animate
   * @param value1 Final value for first die
   * @param value2 Final value for second die
   * @param durationMillis Animation duration in milliseconds
   * @param onFinished Action to run when animation completes (can be null)
   */
  public static void animateDiceRoll(DiceView diceView, int value1, int value2,
      int durationMillis, Runnable onFinished) {
    Timeline timeline = new Timeline();
    int frames = 8; // Number of "rolls" before settling

    for (int i = 0; i < frames; i++) {
      final int frameIndex = i;
      KeyFrame keyFrame = new KeyFrame(
          Duration.millis(durationMillis * i / frames),
          e -> {
            // Generate random values for the animation frames
            int frameVal1 = 1 + (int)(Math.random() * 6);
            int frameVal2 = 1 + (int)(Math.random() * 6);

            // Make dice values gradually converge to final values
            if (frameIndex > frames / 2) {
              if (Math.random() < (double)frameIndex / frames) {
                frameVal1 = value1;
              }
              if (Math.random() < (double)frameIndex / frames) {
                frameVal2 = value2;
              }
            }

            diceView.setValues(frameVal1, frameVal2);
          }
      );
      timeline.getKeyFrames().add(keyFrame);
    }

    // Final frame shows the actual result
    KeyFrame finalFrame = new KeyFrame(
        Duration.millis(durationMillis),
        e -> {
          diceView.setValues(value1, value2);
          if (onFinished != null) {
            onFinished.run();
          }
        }
    );
    timeline.getKeyFrames().add(finalFrame);

    timeline.play();
  }

  /**
   * Creates a celebration animation for winning
   * @param node The node to animate
   * @param durationMillis Animation duration in milliseconds
   * @return The created transition
   */
  public static ParallelTransition createWinAnimation(Node node, int durationMillis) {
    RotateTransition rotate = new RotateTransition(Duration.millis(durationMillis), node);
    rotate.setByAngle(360);
    rotate.setCycleCount(2);

    ScaleTransition scale = new ScaleTransition(Duration.millis(durationMillis / 2), node);
    scale.setToX(1.5);
    scale.setToY(1.5);
    scale.setCycleCount(2);
    scale.setAutoReverse(true);

    ParallelTransition winAnimation = new ParallelTransition(rotate, scale);
    return winAnimation;
  }

  /**
   * Creates a snake or ladder animation (for path following)
   * @param node The node to animate
   * @param path The path to follow
   * @param durationMillis Animation duration in milliseconds
   * @param onFinished Action to run when animation completes (can be null)
   * @return The created transition
   */
  public static PathTransition createPathAnimation(Node node, Path path,
      int durationMillis, Runnable onFinished) {
    PathTransition pathTransition = new PathTransition();
    pathTransition.setDuration(Duration.millis(durationMillis));
    pathTransition.setNode(node);
    pathTransition.setPath(path);
    pathTransition.setCycleCount(1);

    if (onFinished != null) {
      pathTransition.setOnFinished(e -> onFinished.run());
    }

    return pathTransition;
  }

  /**
   * Creates a curved path animation for snake or ladder movement with rotation effect
   * @param node The node to animate (player token)
   * @param startX Starting X position
   * @param startY Starting Y position
   * @param endX Ending X position
   * @param endY Ending Y position
   * @param isSnake Whether this is a snake (downward) or ladder (upward) animation
   * @param durationMillis Animation duration in milliseconds
   * @param onFinished Action to run when animation completes (can be null)
   * @return The created transition
   */
  public static ParallelTransition createSnakeOrLadderAnimation(Node node,
      double startX, double startY, double endX, double endY,
      boolean isSnake, int durationMillis, Runnable onFinished) {

    Path path = new Path();
    path.getElements().add(new MoveTo(startX, startY));

    // Calculate control points for curved path
    double midX = (startX + endX) / 2;
    double controlY;

    if (isSnake) {
      // For snake, curve downward (gravity effect)
      controlY = startY + Math.abs(endY - startY) * 0.3;
    } else {
      // For ladder, curve upward (climbing effect)
      controlY = startY - Math.abs(endY - startY) * 0.3;
    }

    // Add curve using quadratic curve
    path.getElements().add(new QuadCurveTo(
        midX, controlY, endX, endY));

    PathTransition pathTransition = new PathTransition();
    pathTransition.setDuration(Duration.millis(durationMillis));
    pathTransition.setNode(node);
    pathTransition.setPath(path);
    pathTransition.setCycleCount(1);

    // Different animation treatment for snake vs ladder
    if (isSnake) {
      // For snake, add spinning effect as the player slides down
      RotateTransition rotateTransition = new RotateTransition(Duration.millis(durationMillis), node);
      rotateTransition.setByAngle(360); // Full rotation while sliding

      // Combine path and rotation for snake animation
      ParallelTransition snakeAnimation = new ParallelTransition(pathTransition, rotateTransition);

      if (onFinished != null) {
        snakeAnimation.setOnFinished(e -> onFinished.run());
      }

      return snakeAnimation;
    } else {
      // For ladder, create a climbing motion effect
      Timeline climbingMotion = new Timeline();
      int steps = 6;
      for (int i = 0; i < steps; i++) {
        KeyFrame keyFrame = new KeyFrame(
            Duration.millis(durationMillis * i / steps),
            new KeyValue(node.rotateProperty(), (i % 2 == 0) ? 15 : -15)
        );
        climbingMotion.getKeyFrames().add(keyFrame);
      }

      // End with no rotation
      KeyFrame finalFrame = new KeyFrame(
          Duration.millis(durationMillis),
          new KeyValue(node.rotateProperty(), 0)
      );
      climbingMotion.getKeyFrames().add(finalFrame);

      // Combine path and climbing animations for ladder
      ParallelTransition ladderAnimation = new ParallelTransition(pathTransition, climbingMotion);

      if (onFinished != null) {
        ladderAnimation.setOnFinished(e -> onFinished.run());
      }

      return ladderAnimation;
    }
  }

  /**
   * Creates a fade-in animation
   * @param node The node to animate
   * @param durationMillis Animation duration in milliseconds
   * @return The created transition
   */
  public static FadeTransition createFadeInAnimation(Node node, int durationMillis) {
    FadeTransition fadeIn = new FadeTransition(Duration.millis(durationMillis), node);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);
    return fadeIn;
  }

  /**
   * Creates a fade-out animation
   * @param node The node to animate
   * @param durationMillis Animation duration in milliseconds
   * @param removeAfter Whether to remove the node after animation
   * @return The created transition
   */
  public static FadeTransition createFadeOutAnimation(Node node, int durationMillis, boolean removeAfter) {
    FadeTransition fadeOut = new FadeTransition(Duration.millis(durationMillis), node);
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);

    if (removeAfter) {
      fadeOut.setOnFinished(e -> {
        if (node.getParent() != null) {
          node.getParent().getChildrenUnmodifiable().remove(node);
        }
      });
    }

    return fadeOut;
  }

  /**
   * Creates a pulsating effect animation
   * @param node The node to animate
   * @param durationMillis Animation duration for one pulse in milliseconds
   * @param cycles Number of pulse cycles (use Timeline.INDEFINITE for infinite)
   * @return The created transition
   */
  public static Timeline createPulseAnimation(Node node, int durationMillis, int cycles) {
    Timeline timeline = new Timeline(
        new KeyFrame(Duration.ZERO,
            new KeyValue(node.scaleXProperty(), 1.0),
            new KeyValue(node.scaleYProperty(), 1.0)),
        new KeyFrame(Duration.millis(durationMillis / 2),
            new KeyValue(node.scaleXProperty(), 1.1),
            new KeyValue(node.scaleYProperty(), 1.1)),
        new KeyFrame(Duration.millis(durationMillis),
            new KeyValue(node.scaleXProperty(), 1.0),
            new KeyValue(node.scaleYProperty(), 1.0))
    );

    timeline.setCycleCount(cycles);
    return timeline;
  }

  /**
   * Creates a button hover effect
   * @param button The button to add the effect to
   */
  public static void addButtonHoverEffect(javafx.scene.control.Button button) {
    button.setOnMouseEntered(e -> {
      ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
      st.setToX(1.05);
      st.setToY(1.05);
      st.play();
    });

    button.setOnMouseExited(e -> {
      ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
      st.setToX(1.0);
      st.setToY(1.0);
      st.play();
    });

    button.setOnMousePressed(e -> {
      ScaleTransition st = new ScaleTransition(Duration.millis(50), button);
      st.setToX(0.98);
      st.setToY(0.98);
      st.play();
    });

    button.setOnMouseReleased(e -> {
      ScaleTransition st = new ScaleTransition(Duration.millis(50), button);
      st.setToX(1.0);
      st.setToY(1.0);
      st.play();
    });
  }

  /**
   * Creates an animation for a player token moving across multiple tiles one by one
   * @param token The token node to animate
   * @param tilePanes Map of tile IDs to tile pane nodes
   * @param startTileId Starting tile ID
   * @param endTileId Ending tile ID
   * @param speedMillisPerTile Animation speed in milliseconds per tile
   * @param onFinished Action to run when animation completes (can be null)
   * @return The created transition
   */
  public static SequentialTransition createTileByTileMovementAnimation(
      Node token,
      Map<Integer, StackPane> tilePanes,
      int startTileId,
      int endTileId,
      int speedMillisPerTile,
      Runnable onFinished) {

    SequentialTransition sequentialTransition = new SequentialTransition();

    // Get the maximum tile ID to handle wrapping around the board
    int maxTileId = tilePanes.keySet().stream().max(Integer::compareTo).orElse(0);

    // Calculate the path (list of tile IDs to visit)
    List<Integer> pathTileIds = new ArrayList<>();

    // Check if moving forward or backward
    boolean isMovingForward = endTileId > startTileId || (endTileId < startTileId && endTileId <= maxTileId/4);

    if (isMovingForward) {
      // Moving forward (normal roll)
      if (endTileId > startTileId) {
        // Simple forward movement
        for (int id = startTileId + 1; id <= endTileId; id++) {
          pathTileIds.add(id);
        }
      } else {
        // Wrapping around the board (e.g., passing GO)
        for (int id = startTileId + 1; id <= maxTileId; id++) {
          pathTileIds.add(id);
        }
        for (int id = 0; id <= endTileId; id++) {
          pathTileIds.add(id);
        }
      }
    } else {
      // Moving backward (e.g., from a card or special action)
      if (startTileId > endTileId) {
        // Simple backward movement
        for (int id = startTileId - 1; id >= endTileId; id--) {
          pathTileIds.add(id);
        }
      } else {
        // Wrapping backward around the board
        for (int id = startTileId - 1; id >= 0; id--) {
          pathTileIds.add(id);
        }
        for (int id = maxTileId; id >= endTileId; id--) {
          pathTileIds.add(id);
        }
      }
    }

    // Create an animation for each tile in the path
    for (int tileId : pathTileIds) {
      StackPane targetPane = tilePanes.get(tileId);
      if (targetPane != null) {
        // Calculate the center position of the target tile
        double toX = targetPane.getBoundsInParent().getCenterX() - token.getBoundsInParent().getWidth() / 2;
        double toY = targetPane.getBoundsInParent().getCenterY() - token.getBoundsInParent().getHeight() / 2;

        // Create a transition to move to this tile
        TranslateTransition moveToTile = createMoveAnimation(
            token,
            toX,
            toY,
            speedMillisPerTile,
            null); // No callback for intermediate steps

        // Add a small bounce effect to make it more visually appealing
        ScaleTransition bounceUp = new ScaleTransition(Duration.millis(speedMillisPerTile / 4), token);
        bounceUp.setToX(1.2);
        bounceUp.setToY(1.2);

        ScaleTransition bounceDown = new ScaleTransition(Duration.millis(speedMillisPerTile / 4), token);
        bounceDown.setToX(1.0);
        bounceDown.setToY(1.0);

        ParallelTransition moveWithBounce = new ParallelTransition(
            moveToTile,
            new SequentialTransition(bounceUp, bounceDown)
        );

        sequentialTransition.getChildren().add(moveWithBounce);
      }
    }

    // Set the onFinished handler for the overall animation
    if (onFinished != null) {
      sequentialTransition.setOnFinished(e -> onFinished.run());
    }

    return sequentialTransition;
  }
}