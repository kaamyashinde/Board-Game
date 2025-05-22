package edu.ntnu.iir.bidata.view.monopoly;

import edu.ntnu.iir.bidata.Inject;
import edu.ntnu.iir.bidata.controller.MonopolyController;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.actions.monopoly.GoToJailAction;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.FreeParkingTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.GoTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.JailTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import edu.ntnu.iir.bidata.model.utils.DefaultGameMediator;
import edu.ntnu.iir.bidata.model.utils.GameMediator;
import edu.ntnu.iir.bidata.view.common.CommonButtons;
import edu.ntnu.iir.bidata.view.common.DiceView;
import edu.ntnu.iir.bidata.view.common.JavaFXGameUI;
import edu.ntnu.iir.bidata.view.animation.MonopolyAnimator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import lombok.Setter;

/** JavaFX UI implementation for the Monopoly game. */
public class MonopolyGameUI extends JavaFXGameUI {
  private static final Logger LOGGER = Logger.getLogger(MonopolyGameUI.class.getName());
  private static final int GRID_DIM = 6; // 6x6 grid for 20-tile Monopoly
  private static final int TILE_SIZE = 60;
  private final Map<Integer, StackPane> tilePanes = new HashMap<>();
  private final Map<Player, ImageView> playerTokens = new HashMap<>();
  private final Map<String, ImageView> playerTokensByName = new HashMap<>();
  private final Button rollDiceButton = new Button("Roll Dice");
  private final Button buyButton = new Button("Buy");
  private final Button skipButton = new Button("Skip");
  private final Button payRentButton = new Button("Pay Rent");
  private final Button jailRollButton = new Button("Roll Dice (Jail)");
  private final Button jailPayButton = new Button("Pay $50");
  private final Label actionLabel = new Label("");

  private final BorderPane mainLayout;
  private final GridPane boardPane;
  private final VBox playerInfoPanel;
  private final DiceView diceView = new DiceView();
  private final Stage primaryStage;
  private final GameMediator mediator;
  protected BoardGame boardGame;
  private MonopolyAnimator animator;
  @Setter private MonopolyController controller;
  private BorderPane root;

  /**
   * Constructs the MonopolyGameUI, setting up the primary user interface components for the
   * Monopoly game. Initializes various UI elements, registers event listeners, and connects
   * necessary components such as the game mediator and the controller.
   */
  @Inject
  public MonopolyGameUI(
      BoardGame boardGame,
      Stage primaryStage,
      MonopolyController controller,
      GameMediator mediator) {
    super(boardGame, primaryStage);
    this.boardGame = boardGame;
    this.primaryStage = primaryStage;
    this.controller = controller;
    this.mediator = mediator;
    this.mainLayout = new BorderPane();
    this.boardPane = new GridPane();
    this.playerInfoPanel = new VBox(10);

    // Set player names in controller to avoid NullPointerException
    List<String> playerNames = boardGame.getPlayers().stream().map(Player::getName).toList();
    controller.setPlayerNames(playerNames);
    setupUI();

    // Register mediator listener to update UI on nextPlayer event
    if (mediator instanceof DefaultGameMediator m) {
      m.register(
          (sender, event) -> {
            if ("nextPlayer".equals(event)) {
              javafx.application.Platform.runLater(
                  () -> {
                    updatePlayerInfoPanel();
                    updatePlayerTokens();
                    updateRollDiceButtonState();
                  });
            }
          });
    }
  }

  // Getter methods for animator access
  public Map<Integer, StackPane> getTilePanes() {
    return tilePanes;
  }

  public Map<String, ImageView> getPlayerTokensByName() {
    return playerTokensByName;
  }

  /**
   * Sets up the user interface for the Monopoly game with enhanced layout including action label.
   */
  public void setupUI() {
    root = new BorderPane();
    root.setPadding(new Insets(25));
    root.setPrefWidth(1100);
    root.setPrefHeight(750);
    root.getStyleClass().add("monopoly-main-layout");

    // --- Top bar: Back and Save buttons ---
    HBox topBar = new HBox(10);
    topBar.setPadding(new Insets(10));
    topBar.setAlignment(Pos.CENTER_LEFT);
    Button backButton = CommonButtons.backToMainMenu(primaryStage, true, controller);
    backButton.getStyleClass().add("monopoly-back-button");
    Button saveButton = CommonButtons.saveGameBtn(true, controller, actionLabel);
    saveButton.getStyleClass().add("game-control-button");
    topBar.getChildren().addAll(backButton, saveButton);
    root.setTop(topBar);

    // --- Center: Board ---
    boardPane.setHgap(5);
    boardPane.setVgap(5);
    boardPane.setPadding(new Insets(15));
    boardPane.getStyleClass().add("monopoly-board-pane");
    root.setCenter(boardPane);

    // --- Right: Player info panel ---
    playerInfoPanel.setPadding(new Insets(15));
    playerInfoPanel.getStyleClass().add("monopoly-player-info-panel");
    playerInfoPanel.setPrefWidth(220);
    root.setRight(playerInfoPanel);

    // --- Bottom bar: Game controls with action label ---
    VBox bottomSection = new VBox(10);
    bottomSection.setAlignment(Pos.CENTER);
    bottomSection.setPadding(new Insets(15));

    // Action label for game feedback
    actionLabel.getStyleClass().add("monopoly-action-label");
    actionLabel.setText("Ready to play! Roll the dice to start.");
    actionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-text-alignment: center;");
    actionLabel.setWrapText(true);
    actionLabel.setPrefWidth(800);

    // Game controls
    HBox controls = new HBox(10);
    controls.setPadding(new Insets(15));
    controls.setAlignment(Pos.CENTER);
    controls.getStyleClass().add("monopoly-game-controls");

    rollDiceButton.getStyleClass().add("monopoly-roll-dice-button");
    rollDiceButton.setOnAction(e -> handleRollDice());

    buyButton.getStyleClass().add("monopoly-buy-button");
    buyButton.setOnAction(e -> handleBuyProperty());

    skipButton.getStyleClass().add("monopoly-skip-button");
    skipButton.setOnAction(e -> handleSkipAction());

    payRentButton.getStyleClass().add("monopoly-pay-rent-button");
    payRentButton.setOnAction(e -> handlePayRent());

    jailRollButton.getStyleClass().add("monopoly-jail-roll-button");
    jailRollButton.setOnAction(e -> handleJailRoll());

    jailPayButton.getStyleClass().add("monopoly-jail-pay-button");
    jailPayButton.setOnAction(e -> handleJailPay());

    diceView.getStyleClass().add("monopoly-dice-view");

    controls
        .getChildren()
        .addAll(
            diceView,
            rollDiceButton,
            buyButton,
            skipButton,
            payRentButton,
            jailRollButton,
            jailPayButton);

    bottomSection.getChildren().addAll(actionLabel, controls);
    root.setBottom(bottomSection);

    // Set the root of the existing scene
    getScene().setRoot(root);

    primaryStage.setMinWidth(900);
    primaryStage.setMinHeight(600);

    // Initialize the board
    initializeBoard();

    // Initialize animator after board is set up
    animator = new MonopolyAnimator(this, tilePanes, playerTokensByName);

    // Add stylesheets
    getScene().getStylesheets().add(getClass().getResource("/monopoly.css").toExternalForm());
    getScene().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
  }

  /**
   * Handles the logic for the "Roll Dice" action with proper animation flow.
   * Animation happens first, then game logic is processed.
   */
  private void handleRollDice() {
    // Check if animation is in progress
    if (animator != null && animator.isAnimationInProgress()) {
      LOGGER.info("Animation in progress, ignoring dice roll");
      return;
    }

    // Store current state before any changes
    Player currentPlayer = getBoardGame().getCurrentPlayer();
    int originalPos = currentPlayer.getCurrentPosition();
    String playerName = currentPlayer.getName();

    // Roll dice first to get the values for display and calculation
    boardGame.getDice().rollAllDice();
    int[] diceValues = boardGame.getCurrentDiceValues();
    int diceSum = java.util.Arrays.stream(diceValues).sum();

    // Update dice view immediately when dice are rolled
    if (diceValues != null && diceValues.length >= 2) {
      diceView.setValues(diceValues[0], diceValues[1]);
      actionLabel.setText(playerName + " rolled " + diceValues[0] + " and " + diceValues[1] + " (Total: " + diceSum + ")");
    } else if (diceValues != null && diceValues.length == 1) {
      diceView.setValues(diceValues[0], diceValues[0]);
      actionLabel.setText(playerName + " rolled " + diceValues[0]);
    } else {
      diceView.setValues(1, 1);
      actionLabel.setText(playerName + " rolled 1 and 1 (Total: 2)");
      diceSum = 2;
    }

    // Calculate final position without actually moving the player yet
    int boardSize = getBoardGame().getBoard().getSizeOfBoard();
    int calculatedFinalPos = (originalPos + diceSum) % boardSize;

    // Check if this would be a "Go to Jail" situation by examining the tile
    Tile targetTile = getBoardGame().getBoard().getTile(calculatedFinalPos);
    boolean isGoToJailTile = targetTile.getAction() instanceof GoToJailAction;

    if (isGoToJailTile) {
      // Find the jail position
      int jailPos = findJailPosition();

      // Animate movement to the "Go to Jail" tile first, then to jail
      animator.animateMovement(playerName, originalPos, calculatedFinalPos, boardSize, () -> {
        actionLabel.setText(playerName + " landed on 'Go to Jail'! Moving to jail...");

        // After reaching "Go to Jail" tile, animate to jail
        animator.animateGoToJail(playerName, jailPos, () -> {
          // Now let controller handle the actual game logic
          controller.handlePlayerMove();
          update();
        });
      });
    } else {
      // Normal movement
      animator.animateMovement(playerName, originalPos, calculatedFinalPos, boardSize, () -> {
        // After animation completes, let controller handle the game logic
        controller.handlePlayerMove();

        // Update action label based on what happened
        updateActionLabelAfterMove(playerName, calculatedFinalPos);
        update();
      });
    }
  }

  /**
   * Updates the action label based on what the player landed on
   */
  private void updateActionLabelAfterMove(String playerName, int position) {
    Tile landedTile = getBoardGame().getBoard().getTile(position);
    Player currentPlayer = getBoardGame().getCurrentPlayer();

    if (landedTile instanceof PropertyTile) {
      PropertyTile property = (PropertyTile) landedTile;
      if (property.getOwner() == null) {
        actionLabel.setText(playerName + " can buy this property for $" + property.getPrice());
      } else if (property.getOwner() != currentPlayer) {
        actionLabel.setText(playerName + " must pay rent of $" + property.getRent());
      } else {
        actionLabel.setText(playerName + " landed on their own property");
      }
    } else if (landedTile instanceof GoTile) {
      actionLabel.setText(playerName + " passed GO! Collect $200");
    } else if (landedTile instanceof JailTile) {
      actionLabel.setText(playerName + " is just visiting jail");
    } else if (landedTile instanceof FreeParkingTile) {
      actionLabel.setText(playerName + " landed on Free Parking");
    } else {
      actionLabel.setText(playerName + " landed on " + landedTile.getClass().getSimpleName().replace("Tile", ""));
    }
  }

  /**
   * Finds the jail position on the board
   */
  private int findJailPosition() {
    for (int i = 0; i < getBoardGame().getBoard().getSizeOfBoard(); i++) {
      Tile tile = getBoardGame().getBoard().getTile(i);
      if (tile instanceof JailTile) {
        return i;
      }
    }
    return 15; // Default jail position if not found
  }

  /**
   * Updates the user interface components of the Monopoly game.
   */
  @Override
  public void update() {
    updatePlayerInfoPanel();
    updatePlayerTokens();
    updateRollDiceButtonState();
  }

  /**
   * Retrieves the current Scene associated with the primary stage.
   */
  @Override
  public Scene getScene() {
    return primaryStage.getScene();
  }

  /**
   * Updates the player information panel with enhanced current player indicator.
   */
  private void updatePlayerInfoPanel() {
    LOGGER.info("Clearing playerInfoPanel and updating player info...");
    playerInfoPanel.getChildren().clear();

    // Add current player indicator at the top
    Player currentPlayer = boardGame.getCurrentPlayer();
    if (currentPlayer != null) {
      Label currentPlayerLabel = new Label("Current Turn: " + currentPlayer.getName());
      currentPlayerLabel.getStyleClass().add("monopoly-current-player-label");
      currentPlayerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c5aa0; -fx-padding: 10px;");
      playerInfoPanel.getChildren().add(currentPlayerLabel);

      // Add separator
      Label separator = new Label("─────────────────");
      separator.getStyleClass().add("monopoly-separator");
      playerInfoPanel.getChildren().add(separator);
    }

    int[] playerCount = {0};
    boardGame
        .getPlayers()
        .forEach(
            player -> {
              LOGGER.info(
                  "Processing player: "
                      + player.getName()
                      + " of class: "
                      + player.getClass().getName());
              if (player instanceof SimpleMonopolyPlayer monopolyPlayer) {
                VBox playerBox = new VBox(5);
                playerBox.setPadding(new Insets(15));
                playerBox.getStyleClass().add("monopoly-player-box");

                // Highlight current player
                if (player == currentPlayer) {
                  playerBox.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #2196f3; -fx-border-width: 2px; -fx-border-radius: 5px;");
                }

                Label nameLabel = new Label(monopolyPlayer.getName());
                nameLabel.getStyleClass().add("monopoly-player-name");
                nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                Label moneyLabel = new Label("Money: $" + monopolyPlayer.getMoney());
                moneyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4caf50;");

                Label positionLabel =
                    new Label("Position: Tile #" + monopolyPlayer.getCurrentTile().getId());
                positionLabel.setStyle("-fx-font-size: 12px;");

                Label propertiesLabel =
                    new Label("Properties: " + monopolyPlayer.getOwnedProperties().size());
                propertiesLabel.setStyle("-fx-font-size: 12px;");

                // Add jail status if applicable
                if (monopolyPlayer.isInJail()) {
                  Label jailLabel = new Label("🔒 IN JAIL");
                  jailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #f44336; -fx-font-weight: bold;");
                  playerBox.getChildren().addAll(nameLabel, moneyLabel, positionLabel, propertiesLabel, jailLabel);
                } else {
                  playerBox.getChildren().addAll(nameLabel, moneyLabel, positionLabel, propertiesLabel);
                }

                playerInfoPanel.getChildren().add(playerBox);
                LOGGER.info(
                    String.format(
                        "Added player to panel: name=%s, money=%d, position=%d, properties=%d",
                        monopolyPlayer.getName(),
                        monopolyPlayer.getMoney(),
                        monopolyPlayer.getCurrentTile().getId(),
                        monopolyPlayer.getOwnedProperties().size()));
                playerCount[0]++;
              }
            });
    LOGGER.info("Total players added to playerInfoPanel: " + playerCount[0]);
  }

  /**
   * Updates the positions of player tokens on the board (only when animation is not in progress).
   */
  private void updatePlayerTokens() {
    // Only update if animation is not in progress
    if (animator != null && animator.isAnimationInProgress()) {
      return;
    }

    // Remove all tokens from tile panes
    tilePanes.values().forEach(pane -> pane.getChildren().removeIf(n -> n instanceof ImageView));

    // Add tokens for each player
    getBoardGame()
        .getPlayers()
        .forEach(
            player -> {
              int pos = player.getCurrentTile() != null ? player.getCurrentTile().getId() : 0;
              ImageView token = getOrCreatePlayerToken(player);

              // Use animator's method to properly place the token
              if (animator != null) {
                animator.moveTokenToTile(token, pos);
              } else {
                // Fallback: place directly in tile pane
                StackPane tilePane = tilePanes.get(pos);
                if (tilePane != null) {
                  tilePane.getChildren().add(token);
                }
              }
            });
  }

  /**
   * Creates or retrieves a player token
   */
  private ImageView getOrCreatePlayerToken(Player player) {
    ImageView token = playerTokens.get(player);
    if (token == null) {
      String tokenImage = player.getTokenImage();
      if (tokenImage != null && !tokenImage.isEmpty()) {
        try {
          Image img = new Image(Objects.requireNonNull(
              getClass().getResourceAsStream("/tokens/" + tokenImage)));
          token = new ImageView(img);
        } catch (Exception e) {
          LOGGER.warning("Could not load token image: " + tokenImage + ", using default");
          token = new ImageView();
        }
      } else {
        token = new ImageView();
      }

      token.setFitWidth(24);
      token.setFitHeight(36);
      token.setPreserveRatio(true);
      token.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 2, 0, 1, 1);");
      token.toFront();

      playerTokens.put(player, token);
      playerTokensByName.put(player.getName(), token);
    }
    return token;
  }

  /**
   * Updates button states based on game state and animation status.
   */
  private void updateRollDiceButtonState() {
    Player current = getBoardGame().getCurrentPlayer();
    boolean isGameOver = getBoardGame().isGameOver();
    boolean awaitingBuy = controller.isAwaitingPlayerAction();
    boolean awaitingRent = controller.isAwaitingRentAction();
    boolean inJail = controller.isCurrentPlayerInJail();
    boolean animationInProgress = animator != null && animator.isAnimationInProgress();
    boolean rollDiceActive = current != null && !isGameOver && !awaitingBuy && !awaitingRent && !inJail && !animationInProgress;

    rollDiceButton.setDisable(!rollDiceActive);
    buyButton.setDisable(rollDiceActive || isGameOver || awaitingRent || inJail || animationInProgress);
    skipButton.setDisable(rollDiceActive || isGameOver || awaitingRent || inJail || animationInProgress);
    payRentButton.setDisable(rollDiceActive || isGameOver || awaitingBuy || inJail || animationInProgress);
    jailRollButton.setDisable(isGameOver || !inJail || animationInProgress);
    jailPayButton.setDisable(isGameOver || !inJail || animationInProgress);

    // Update button text based on state
    if (awaitingBuy) {
      buyButton.setText("Buy Property");
      skipButton.setText("Skip Purchase");
    } else if (awaitingRent) {
      payRentButton.setText("Pay Rent");
    }
  }

  /**
   * Handles the buy property action.
   */
  private void handleBuyProperty() {
    controller.buyPropertyForCurrentPlayer();
  }

  /**
   * Handles the skip action.
   */
  private void handleSkipAction() {
    controller.skipActionForCurrentPlayer();
  }

  /**
   * Handles the pay rent action.
   */
  private void handlePayRent() {
    controller.payRentForCurrentPlayer();
  }

  /**
   * Handles jail roll action.
   */
  private void handleJailRoll() {
    controller.handleJailRollDice();
  }

  /**
   * Handles jail pay action.
   */
  private void handleJailPay() {
    controller.handleJailPay();
  }

  /**
   * Initializes the game board layout.
   */
  private void initializeBoard() {
    boardPane.getChildren().clear();
    tilePanes.clear();
    int boardSize = getBoardGame().getBoard().getSizeOfBoard();
    int gridDim = getGridDimForBoardSize(boardSize);
    int[] tileIndex = {0};

    // Top row (left to right)
    java.util.stream.IntStream.range(0, gridDim)
        .filter(col -> tileIndex[0] < boardSize)
        .forEach(
            col -> {
              Tile tile = getBoardGame().getBoard().getTile(tileIndex[0]);
              StackPane tilePane = createTilePane(tile);
              boardPane.add(tilePane, col, 0);
              tilePanes.put(tileIndex[0], tilePane);
              tileIndex[0]++;
            });

    // Right column (top to bottom, excluding top)
    java.util.stream.IntStream.range(1, gridDim - 1)
        .filter(row -> tileIndex[0] < boardSize)
        .forEach(
            row -> {
              Tile tile = getBoardGame().getBoard().getTile(tileIndex[0]);
              StackPane tilePane = createTilePane(tile);
              boardPane.add(tilePane, gridDim - 1, row);
              tilePanes.put(tileIndex[0], tilePane);
              tileIndex[0]++;
            });

    // Bottom row (right to left)
    java.util.stream.IntStream.iterate(gridDim - 1, col -> col >= 0, col -> col - 1)
        .filter(col -> tileIndex[0] < boardSize)
        .forEach(
            col -> {
              Tile tile = getBoardGame().getBoard().getTile(tileIndex[0]);
              StackPane tilePane = createTilePane(tile);
              boardPane.add(tilePane, col, gridDim - 1);
              tilePanes.put(tileIndex[0], tilePane);
              tileIndex[0]++;
            });

    // Left column (bottom to top, excluding top and bottom)
    java.util.stream.IntStream.iterate(gridDim - 2, row -> row > 0, row -> row - 1)
        .filter(row -> tileIndex[0] < boardSize)
        .forEach(
            row -> {
              Tile tile = getBoardGame().getBoard().getTile(tileIndex[0]);
              StackPane tilePane = createTilePane(tile);
              boardPane.add(tilePane, 0, row);
              tilePanes.put(tileIndex[0], tilePane);
              tileIndex[0]++;
            });

    // Create a center area with MONOPOLY text
    if (gridDim > 3) {
      StackPane centerArea = new StackPane();
      Rectangle centerRect =
          new Rectangle(
              (gridDim - 2) * 70 + (gridDim - 3) * 5, (gridDim - 2) * 70 + (gridDim - 3) * 5);
      centerRect.getStyleClass().add("monopoly-center-area");

      javafx.scene.text.Text monopolyText = new javafx.scene.text.Text("MONOPOLY");
      monopolyText.getStyleClass().add("monopoly-center-text");

      centerArea.getChildren().addAll(centerRect, monopolyText);
      boardPane.add(centerArea, 1, 1, gridDim - 2, gridDim - 2);
      GridPane.setHalignment(centerArea, javafx.geometry.HPos.CENTER);
      GridPane.setValignment(centerArea, javafx.geometry.VPos.CENTER);
    }

    updatePlayerInfoPanel();
    updatePlayerTokens();
    updateRollDiceButtonState();
  }

  /**
   * Calculates the grid dimension for a given board size.
   */
  private int getGridDimForBoardSize(int boardSize) {
    int n = 3;
    while (4 * (n - 1) < boardSize) {
      n++;
    }
    return n;
  }

  /**
   * Creates a tile pane for the board.
   */
  private StackPane createTilePane(Tile tile) {
    StackPane pane = new StackPane();
    pane.setPrefSize(70, 70);

    if (tile instanceof PropertyTile pt) {
      VBox propertyContainer = new VBox();
      propertyContainer.getStyleClass().add("property-tile-container");

      Rectangle colorBar = new Rectangle(70, 15);
      colorBar.getStyleClass().addAll("property-color-bar", "property-group-" + pt.getGroup());

      Rectangle mainRect = new Rectangle(70, 55);
      mainRect.getStyleClass().add("monopoly-tile");
      mainRect.setFill(Color.WHITE);

      Label label = new Label("$" + pt.getPrice());
      label.getStyleClass().add("monopoly-tile-label");

      StackPane mainArea = new StackPane(mainRect, label);
      propertyContainer.getChildren().addAll(colorBar, mainArea);
      pane.getChildren().add(propertyContainer);
    } else {
      Rectangle rect = new Rectangle(70, 70);
      rect.getStyleClass().add("monopoly-tile");
      Label label = new Label();
      label.getStyleClass().add("monopoly-tile-label");

      if (tile instanceof GoTile) {
        rect.getStyleClass().add("go-tile");
        label.setText("GO");
      } else if (tile instanceof JailTile) {
        rect.getStyleClass().add("jail-tile");
        label.setText("JAIL");
      } else if (tile instanceof FreeParkingTile) {
        rect.getStyleClass().add("free-parking-tile");
        label.setText("FREE\nPARKING");
      } else if (tile.getAction() instanceof GoToJailAction) {
        rect.getStyleClass().add("go-to-jail-tile");
        label.setText("GO TO\nJAIL");
      } else {
        rect.getStyleClass().add("blank-tile");
        label.setText("");
      }

      pane.getChildren().addAll(rect, label);
    }

    return pane;
  }

  /**
   * Sets the board game and updates the UI.
   */
  public void setBoardGame(BoardGame boardGame) {
    if (this.boardGame != null) {
      this.boardGame.removeObserver(this);
    }
    this.boardGame = boardGame;
    this.boardGame.addObserver(this);
    initializeBoard();
    update();
  }

  /**
   * Gets the root layout.
   */
  public BorderPane getRoot() {
    return mainLayout;
  }

}