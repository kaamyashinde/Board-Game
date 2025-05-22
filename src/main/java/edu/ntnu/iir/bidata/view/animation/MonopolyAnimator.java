package edu.ntnu.iir.bidata.view.animation;

import edu.ntnu.iir.bidata.view.monopoly.MonopolyGameUI;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Animation class for Monopoly game.
 * Handles token movement animations around the board following the proper path.
 */
public class MonopolyAnimator {

  private static final Logger LOGGER = Logger.getLogger(MonopolyAnimator.class.getName());
  private static final double PAUSE_BETWEEN_TILES_MS = 400; // Pause between tile movements (slower)
  private static final double JAIL_MOVEMENT_DURATION_MS = 800; // Duration for "Go to Jail" movement

  private final MonopolyGameUI gameUI;
  private final Map<Integer, StackPane> tilePanes;
  private final Map<String, ImageView> playerTokens;
  private boolean animationInProgress = false;

  /**
   * Constructor for MonopolyAnimator
   *
   * @param gameUI The MonopolyGameUI instance
   * @param tilePanes Map of tile positions to their StackPane containers
   * @param playerTokens Map of player names to their token ImageViews
   */
  public MonopolyAnimator(MonopolyGameUI gameUI, Map<Integer, StackPane> tilePanes, Map<String, ImageView> playerTokens) {
    this.gameUI = gameUI;
    this.tilePanes = tilePanes;
    this.playerTokens = playerTokens;
  }

  /**
   * Animates a player token moving around the board from current position to target position
   *
   * @param playerName The name of the player
   * @param fromPosition Starting position
   * @param toPosition Target position
   * @param boardSize Total number of tiles on the board
   * @param onComplete Callback to execute when animation completes
   */
  public void animateMovement(String playerName, int fromPosition, int toPosition, int boardSize, Runnable onComplete) {
    if (animationInProgress) {
      LOGGER.warning("Animation already in progress, skipping new animation request");
      return;
    }

    ImageView token = playerTokens.get(playerName);
    if (token == null) {
      LOGGER.warning("No token found for player: " + playerName);
      if (onComplete != null) onComplete.run();
      return;
    }

    animationInProgress = true;
    LOGGER.info("Starting movement animation for " + playerName + " from " + fromPosition + " to " + toPosition);

    SequentialTransition movementSequence = new SequentialTransition();

    // Calculate path around the board
    int currentPos = fromPosition;
    int steps = calculateSteps(fromPosition, toPosition, boardSize);

    for (int i = 0; i < steps; i++) {
      // Move to next position (wrapping around the board)
      currentPos = (currentPos + 1) % boardSize;
      final int targetPos = currentPos;

      // Create pause transition that moves the token to the tile
      PauseTransition moveToTile = new PauseTransition(Duration.millis(PAUSE_BETWEEN_TILES_MS));
      moveToTile.setOnFinished(e -> moveTokenToTile(token, targetPos));
      movementSequence.getChildren().add(moveToTile);
    }

    // Set completion handler
    movementSequence.setOnFinished(e -> {
      animationInProgress = false;
      LOGGER.info("Movement animation completed for " + playerName);

      // Ensure token is properly placed in the final tile pane
      moveTokenToTile(token, toPosition);

      if (onComplete != null) {
        onComplete.run();
      }
    });

    movementSequence.play();
  }

  /**
   * Animates direct movement to jail (no path following)
   *
   * @param playerName The name of the player
   * @param jailPosition The jail tile position
   * @param onComplete Callback to execute when animation completes
   */
  public void animateGoToJail(String playerName, int jailPosition, Runnable onComplete) {
    if (animationInProgress) {
      LOGGER.warning("Animation already in progress, skipping go to jail animation request");
      return;
    }

    ImageView token = playerTokens.get(playerName);
    if (token == null) {
      LOGGER.warning("No token found for player: " + playerName);
      if (onComplete != null) onComplete.run();
      return;
    }

    animationInProgress = true;
    LOGGER.info("Starting 'Go to Jail' animation for " + playerName);

    // Create direct movement to jail with a pause
    PauseTransition jailTransition = new PauseTransition(Duration.millis(JAIL_MOVEMENT_DURATION_MS));
    jailTransition.setOnFinished(e -> {
      moveTokenToTile(token, jailPosition);
      animationInProgress = false;
      LOGGER.info("'Go to Jail' animation completed for " + playerName);

      if (onComplete != null) {
        onComplete.run();
      }
    });

    jailTransition.play();
  }

  /**
   * Immediately moves a token to a tile without animation
   *
   * @param token The token to move
   * @param tilePosition The target tile position
   */
  public void moveTokenToTile(ImageView token, int tilePosition) {
    // Remove token from current tile
    removeTokenFromAllTiles(token);

    // Add token to target tile
    StackPane targetTile = tilePanes.get(tilePosition);
    if (targetTile != null) {
      // Clear any existing translate values that might interfere
      token.setTranslateX(0);
      token.setTranslateY(0);

      // Add token to the tile pane - it will be automatically centered
      targetTile.getChildren().add(token);

      // If there are multiple tokens on the same tile, offset them slightly
      offsetMultipleTokens(targetTile);
    } else {
      LOGGER.warning("No tile pane found for position: " + tilePosition);
    }
  }

  /**
   * Offsets multiple tokens on the same tile to prevent complete overlap
   */
  private void offsetMultipleTokens(StackPane tilePane) {
    int tokenCount = 0;
    for (javafx.scene.Node node : tilePane.getChildren()) {
      if (node instanceof ImageView) {
        ImageView tokenView = (ImageView) node;
        // Apply small offset based on token count
        double offsetX = (tokenCount % 2) * 10 - 5; // -5 or +5
        double offsetY = (tokenCount / 2) * 8 - 4; // -4, +4, etc.

        tokenView.setTranslateX(offsetX);
        tokenView.setTranslateY(offsetY);
        tokenCount++;
      }
    }
  }

  /**
   * Removes a token from all tile panes
   *
   * @param token The token to remove
   */
  private void removeTokenFromAllTiles(ImageView token) {
    for (StackPane pane : tilePanes.values()) {
      pane.getChildren().remove(token);
    }
  }

  /**
   * Immediately places a token at a position without animation (for initialization)
   *
   * @param playerName The name of the player
   * @param position The target position
   */
  public void setTokenPosition(String playerName, int position) {
    ImageView token = playerTokens.get(playerName);
    if (token == null) {
      LOGGER.warning("No token found for player: " + playerName);
      return;
    }

    moveTokenToTile(token, position);
  }

  /**
   * Checks if an animation is currently in progress
   *
   * @return true if animation is running, false otherwise
   */
  public boolean isAnimationInProgress() {
    return animationInProgress;
  }

  /**
   * Calculates the number of steps to move around the board
   *
   * @param fromPosition Starting position
   * @param toPosition Target position
   * @param boardSize Total board size
   * @return Number of steps to take
   */
  public int calculateSteps(int fromPosition, int toPosition, int boardSize) {
    if (toPosition >= fromPosition) {
      return toPosition - fromPosition;
    } else {
      // Wrapped around the board
      return (boardSize - fromPosition) + toPosition;
    }
  }

  /**
   * Gets the position after moving a certain number of steps
   *
   * @param currentPosition Current position
   * @param steps Number of steps to move
   * @param boardSize Total board size
   * @return New position after movement
   */
  public int getPositionAfterSteps(int currentPosition, int steps, int boardSize) {
    return (currentPosition + steps) % boardSize;
  }
}