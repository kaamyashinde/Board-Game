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
import javafx.scene.control.ScrollPane;
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
import lombok.Getter;
import lombok.Setter;

/** JavaFX UI implementation for the Monopoly game with scalable board layout and responsive buttons. */
public class MonopolyGameUI extends JavaFXGameUI {
  private static final Logger LOGGER = Logger.getLogger(MonopolyGameUI.class.getName());
  // Getter methods for animator access
  @Getter
  private final Map<Integer, StackPane> tilePanes = new HashMap<>();
  private final Map<Player, ImageView> playerTokens = new HashMap<>();
  @Getter
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
   * Helper class to store button size calculations
   */
  private static class ButtonSizes {
    final int rollDiceWidth;
    final int buyWidth;
    final int skipWidth;
    final int payRentWidth;
    final int jailRollWidth;
    final int jailPayWidth;
    final int height;
    final int spacing;
    final int fontSize;

    ButtonSizes(int rollDiceWidth, int buyWidth, int skipWidth, int payRentWidth,
        int jailRollWidth, int jailPayWidth, int height, int spacing, int fontSize) {
      this.rollDiceWidth = rollDiceWidth;
      this.buyWidth = buyWidth;
      this.skipWidth = skipWidth;
      this.payRentWidth = payRentWidth;
      this.jailRollWidth = jailRollWidth;
      this.jailPayWidth = jailPayWidth;
      this.height = height;
      this.spacing = spacing;
      this.fontSize = fontSize;
    }
  }

  /**
   * Constructs the MonopolyGameUI with scalable board and responsive button support.
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

    // Set player names in controller
    List<String> playerNames = boardGame.getPlayers().stream().map(Player::getName).toList();
    controller.setPlayerNames(playerNames);
    setupUI();

    // Register mediator listener
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

  /**
   * Sets up the scalable UI layout with responsive button sizing.
   */
  public void setupUI() {
    root = new BorderPane();
    root.setPadding(new Insets(20));
    root.setPrefWidth(1200);
    root.setPrefHeight(800);
    root.getStyleClass().add("monopoly-main-layout");

    // --- Top bar: Back and Save buttons (Fixed Height) ---
    HBox topBar = new HBox(10);
    topBar.setPadding(new Insets(10));
    topBar.setAlignment(Pos.CENTER_LEFT);
    topBar.setPrefHeight(60);
    topBar.setMaxHeight(60);
    Button backButton = CommonButtons.backToMainMenu(primaryStage, true, controller);
    backButton.getStyleClass().add("monopoly-back-button");
    Button saveButton = CommonButtons.saveGameBtn(true, controller, actionLabel);
    saveButton.getStyleClass().add("game-control-button");
    topBar.getChildren().addAll(backButton, saveButton);
    root.setTop(topBar);

    // --- Center: Scrollable Board Container ---
    ScrollPane boardScrollPane = new ScrollPane();
    boardScrollPane.setFitToWidth(true);
    boardScrollPane.setFitToHeight(true);
    boardScrollPane.setPannable(true);
    boardScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    boardScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    boardScrollPane.getStyleClass().add("monopoly-board-scroll");

    // Board container with dynamic sizing
    StackPane boardContainer = new StackPane();
    boardContainer.setAlignment(Pos.CENTER);

    boardPane.setHgap(3);
    boardPane.setVgap(3);
    boardPane.setPadding(new Insets(20));
    boardPane.getStyleClass().add("monopoly-board-pane");
    boardPane.setAlignment(Pos.CENTER);

    boardContainer.getChildren().add(boardPane);
    boardScrollPane.setContent(boardContainer);
    root.setCenter(boardScrollPane);

    // --- Right: Player info panel (Fixed Width, Scrollable Content) ---
    playerInfoPanel.setPadding(new Insets(15));
    playerInfoPanel.getStyleClass().add("monopoly-player-info-panel");
    playerInfoPanel.setPrefWidth(250);
    playerInfoPanel.setMaxWidth(250);
    playerInfoPanel.setMinWidth(200);

    ScrollPane playerScrollPane = new ScrollPane(playerInfoPanel);
    playerScrollPane.setFitToWidth(true);
    playerScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    playerScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    playerScrollPane.setPrefWidth(250);
    playerScrollPane.getStyleClass().add("monopoly-player-scroll");
    root.setRight(playerScrollPane);

    // --- Bottom bar: Game controls with responsive sizing ---
    VBox bottomSection = new VBox(10);
    bottomSection.setAlignment(Pos.CENTER);
    bottomSection.setPadding(new Insets(15));
    bottomSection.setPrefHeight(170); // Increased height for larger buttons
    bottomSection.setMaxHeight(170);

    // Action label for game feedback
    actionLabel.getStyleClass().add("monopoly-action-label");
    actionLabel.setText("Ready to play! Roll the dice to start.");
    actionLabel.setWrapText(true);
    actionLabel.setPrefWidth(800);
    actionLabel.setMaxHeight(50); // Increased height for action label

    // Calculate responsive button sizes based on available space
    int boardSize = getBoardGame() != null ? getBoardGame().getBoard().getSizeOfBoard() : 20;
    ButtonSizes buttonSizes = calculateButtonSizes(boardSize);

    // Game controls with responsive sizing
    HBox controls = new HBox(buttonSizes.spacing);
    controls.setPadding(new Insets(10));
    controls.setAlignment(Pos.CENTER);
    controls.getStyleClass().add("monopoly-game-controls");

    // Create buttons with calculated sizes and CSS classes
    rollDiceButton.getStyleClass().add("monopoly-roll-dice-button");
    rollDiceButton.setOnAction(e -> handleRollDice());
    rollDiceButton.setPrefSize(buttonSizes.rollDiceWidth, buttonSizes.height);
    rollDiceButton.setMinWidth(buttonSizes.rollDiceWidth);

    buyButton.getStyleClass().add("monopoly-buy-button");
    buyButton.setOnAction(e -> handleBuyProperty());
    buyButton.setPrefSize(buttonSizes.buyWidth, buttonSizes.height);
    buyButton.setMinWidth(buttonSizes.buyWidth);

    skipButton.getStyleClass().add("monopoly-skip-button");
    skipButton.setOnAction(e -> handleSkipAction());
    skipButton.setPrefSize(buttonSizes.skipWidth, buttonSizes.height);
    skipButton.setMinWidth(buttonSizes.skipWidth);

    payRentButton.getStyleClass().add("monopoly-pay-rent-button");
    payRentButton.setOnAction(e -> handlePayRent());
    payRentButton.setPrefSize(buttonSizes.payRentWidth, buttonSizes.height);
    payRentButton.setMinWidth(buttonSizes.payRentWidth);

    jailRollButton.getStyleClass().add("monopoly-jail-roll-button");
    jailRollButton.setOnAction(e -> handleJailRoll());
    jailRollButton.setPrefSize(buttonSizes.jailRollWidth, buttonSizes.height);
    jailRollButton.setMinWidth(buttonSizes.jailRollWidth);

    jailPayButton.getStyleClass().add("monopoly-jail-pay-button");
    jailPayButton.setOnAction(e -> handleJailPay());
    jailPayButton.setPrefSize(buttonSizes.jailPayWidth, buttonSizes.height);
    jailPayButton.setMinWidth(buttonSizes.jailPayWidth);

    diceView.getStyleClass().add("monopoly-dice-view");

    // Set font size for all buttons using inline style (only for dynamic sizing)
    String buttonFontStyle = "-fx-font-size: " + buttonSizes.fontSize + "px;";
    rollDiceButton.setStyle(buttonFontStyle);
    buyButton.setStyle(buttonFontStyle);
    skipButton.setStyle(buttonFontStyle);
    payRentButton.setStyle(buttonFontStyle);
    jailRollButton.setStyle(buttonFontStyle);
    jailPayButton.setStyle(buttonFontStyle);

    controls.getChildren().addAll(
        diceView, rollDiceButton, buyButton, skipButton,
        payRentButton, jailRollButton, jailPayButton);

    bottomSection.getChildren().addAll(actionLabel, controls);
    root.setBottom(bottomSection);

    // Set the root of the existing scene
    getScene().setRoot(root);

    primaryStage.setMinWidth(1000);
    primaryStage.setMinHeight(700);

    // Initialize the board
    initializeBoard();

    // Initialize animator after board is set up
    animator = new MonopolyAnimator(this, tilePanes, playerTokensByName);

    // Add stylesheets
    getScene().getStylesheets().add(getClass().getResource("/style/monopoly.css").toExternalForm());
    getScene().getStylesheets().add(getClass().getResource("/style/styles.css").toExternalForm());
  }

  /**
   * Calculates optimal button sizes based on board size and available space
   */
  private ButtonSizes calculateButtonSizes(int boardSize) {
    if (boardSize <= 20) {
      // Small board - larger buttons with more space
      return new ButtonSizes(
          120, // Roll Dice
          100, // Buy
          100, // Skip
          110, // Pay Rent
          140, // Roll Dice (Jail)
          100, // Pay $50
          40,  // Height
          12,  // Spacing
          12   // Font size
      );
    } else if (boardSize <= 28) {
      // Medium board - medium buttons
      return new ButtonSizes(
          110, // Roll Dice
          90,  // Buy
          90,  // Skip
          100, // Pay Rent
          130, // Roll Dice (Jail)
          90,  // Pay $50
          38,  // Height
          10,  // Spacing
          11   // Font size
      );
    } else {
      // Large board - compact but readable buttons
      return new ButtonSizes(
          100, // Roll Dice
          80,  // Buy
          80,  // Skip
          90,  // Pay Rent
          120, // Roll Dice (Jail)
          80,  // Pay $50
          36,  // Height
          8,   // Spacing
          10   // Font size
      );
    }
  }

  /**
   * Updates button sizes based on board size
   */
  private void updateButtonSizes(int boardSize) {
    ButtonSizes sizes = calculateButtonSizes(boardSize);

    // Update button dimensions
    rollDiceButton.setPrefSize(sizes.rollDiceWidth, sizes.height);
    rollDiceButton.setMinWidth(sizes.rollDiceWidth);

    buyButton.setPrefSize(sizes.buyWidth, sizes.height);
    buyButton.setMinWidth(sizes.buyWidth);

    skipButton.setPrefSize(sizes.skipWidth, sizes.height);
    skipButton.setMinWidth(sizes.skipWidth);

    payRentButton.setPrefSize(sizes.payRentWidth, sizes.height);
    payRentButton.setMinWidth(sizes.payRentWidth);

    jailRollButton.setPrefSize(sizes.jailRollWidth, sizes.height);
    jailRollButton.setMinWidth(sizes.jailRollWidth);

    jailPayButton.setPrefSize(sizes.jailPayWidth, sizes.height);
    jailPayButton.setMinWidth(sizes.jailPayWidth);

    // Update font sizes (only dynamic property that needs inline style)
    String fontStyle = "-fx-font-size: " + sizes.fontSize + "px;";
    rollDiceButton.setStyle(fontStyle);
    buyButton.setStyle(fontStyle);
    skipButton.setStyle(fontStyle);
    payRentButton.setStyle(fontStyle);
    jailRollButton.setStyle(fontStyle);
    jailPayButton.setStyle(fontStyle);

    // Update spacing in the controls container
    if (root.getBottom() instanceof VBox bottomSection) {
      bottomSection.getChildren().stream()
          .filter(node -> node instanceof HBox)
          .map(node -> (HBox) node)
          .findFirst()
          .ifPresent(controls -> controls.setSpacing(sizes.spacing));
    }
  }

  /**
   * Handles dice rolling with animation - dice values shown immediately, animation first, logic after.
   */
  private void handleRollDice() {
    if (animator != null && animator.isAnimationInProgress()) {
      LOGGER.info("Animation in progress, ignoring dice roll");
      return;
    }

    Player currentPlayer = getBoardGame().getCurrentPlayer();
    int originalPos = currentPlayer.getCurrentPosition();
    String playerName = currentPlayer.getName();

    // Roll dice ONCE for both display and calculation
    boardGame.getDice().rollAllDice();
    int[] diceValues = boardGame.getCurrentDiceValues();

    if (diceValues == null || diceValues.length != 2) {
      LOGGER.warning("Invalid dice values, defaulting to [1,1]");
      diceValues = new int[]{1, 1};
    }

    int diceSum = diceValues[0] + diceValues[1];

    // Show dice values and feedback immediately
    diceView.setValues(diceValues[0], diceValues[1]);
    actionLabel.setText(playerName + " rolled " + diceValues[0] + " and " + diceValues[1] + " (Total: " + diceSum + ")");

    // Calculate final position
    int boardSize = getBoardGame().getBoard().getSizeOfBoard();
    int calculatedFinalPos = (originalPos + diceSum) % boardSize;

    // Check for "Go to Jail"
    Tile targetTile = getBoardGame().getBoard().getTile(calculatedFinalPos);
    boolean isGoToJailTile = targetTile.getAction() instanceof GoToJailAction;

    if (isGoToJailTile) {
      int jailPos = findJailPosition();
      animator.animateMovement(playerName, originalPos, calculatedFinalPos, boardSize, () -> {
        actionLabel.setText(playerName + " landed on 'Go to Jail'! Moving to jail...");
        animator.animateGoToJail(playerName, jailPos, () -> {
          movePlayerToPosition(currentPlayer, calculatedFinalPos);
          handleTileActionAfterMove(currentPlayer, calculatedFinalPos);
          update();
        });
      });
    } else {
      animator.animateMovement(playerName, originalPos, calculatedFinalPos, boardSize, () -> {
        movePlayerToPosition(currentPlayer, calculatedFinalPos);
        handleTileActionAfterMove(currentPlayer, calculatedFinalPos);
        updateActionLabelAfterMove(playerName, calculatedFinalPos);
        update();
      });
    }
  }

  private void movePlayerToPosition(Player player, int position) {
    Tile targetTile = getBoardGame().getBoard().getTile(position);
    player.setCurrentTile(targetTile);
  }

  private void handleTileActionAfterMove(Player currentPlayer, int position) {
    Tile currentTile = getBoardGame().getBoard().getTile(position);

    if (currentTile instanceof PropertyTile propertyTile) {
      if (propertyTile.getOwner() == null) {
        LOGGER.info("Property at position " + propertyTile.getId() + " is available for purchase");
        controller.setAwaitingPlayerAction(true);
        controller.setPendingPropertyTile(propertyTile);
        boardGame.notifyObservers();
        return;
      } else if (propertyTile.getOwner() != currentPlayer) {
        LOGGER.info(currentPlayer.getName() + " must pay rent for property at position " + propertyTile.getId());
        controller.setAwaitingRentAction(true);
        controller.setPendingRentPropertyTile(propertyTile);
        boardGame.notifyObservers();
        return;
      }
    } else if (currentTile.getAction() != null) {
      currentTile.getAction().executeAction(currentPlayer, currentTile);
      if (currentPlayer instanceof SimpleMonopolyPlayer && ((SimpleMonopolyPlayer) currentPlayer).isInJail()) {
        mediator.notify(this, "nextPlayer");
        return;
      }
    }

    mediator.notify(this, "nextPlayer");
  }

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

  private int findJailPosition() {
    for (int i = 0; i < getBoardGame().getBoard().getSizeOfBoard(); i++) {
      Tile tile = getBoardGame().getBoard().getTile(i);
      if (tile instanceof JailTile) {
        return i;
      }
    }
    return 15;
  }

  @Override
  public void update() {
    updatePlayerInfoPanel();
    updatePlayerTokens();
    updateRollDiceButtonState();
  }

  @Override
  public Scene getScene() {
    return primaryStage.getScene();
  }

  /**
   * Updates player information panel with current turn indicator and enhanced styling.
   */
  private void updatePlayerInfoPanel() {
    LOGGER.info("Updating player info panel...");
    playerInfoPanel.getChildren().clear();

    Player currentPlayer = boardGame.getCurrentPlayer();
    if (currentPlayer != null) {
      Label currentPlayerLabel = new Label("🎯 " + currentPlayer.getName() + "'s Turn");
      currentPlayerLabel.getStyleClass().add("monopoly-current-player-label");
      playerInfoPanel.getChildren().add(currentPlayerLabel);

      Label separator = new Label("─────────────────");
      separator.getStyleClass().add("monopoly-separator");
      playerInfoPanel.getChildren().add(separator);
    }

    boardGame.getPlayers().forEach(player -> {
      if (player instanceof SimpleMonopolyPlayer monopolyPlayer) {
        VBox playerBox = new VBox(5);
        playerBox.setPadding(new Insets(12));
        playerBox.getStyleClass().add("monopoly-player-box");

        if (player == currentPlayer) {
          playerBox.getStyleClass().add("current-player");
        }

        Label nameLabel = new Label("👤 " + monopolyPlayer.getName());
        nameLabel.getStyleClass().add("monopoly-player-name");

        Label moneyLabel = new Label("💰 $" + monopolyPlayer.getMoney());
        moneyLabel.getStyleClass().add("monopoly-player-money");

        Label positionLabel = new Label("📍 Tile #" + monopolyPlayer.getCurrentTile().getId());
        positionLabel.getStyleClass().add("monopoly-player-position");

        Label propertiesLabel = new Label("🏠 Properties: " + monopolyPlayer.getOwnedProperties().size());
        propertiesLabel.getStyleClass().add("monopoly-player-properties");

        if (monopolyPlayer.isInJail()) {
          Label jailLabel = new Label("🔒 IN JAIL");
          jailLabel.getStyleClass().add("monopoly-player-jail");
          playerBox.getChildren().addAll(nameLabel, moneyLabel, positionLabel, propertiesLabel, jailLabel);
        } else {
          playerBox.getChildren().addAll(nameLabel, moneyLabel, positionLabel, propertiesLabel);
        }

        playerInfoPanel.getChildren().add(playerBox);
      }
    });
  }

  /**
   * Updates player token positions (only when animation is not running).
   */
  private void updatePlayerTokens() {
    if (animator != null && animator.isAnimationInProgress()) {
      return;
    }

    tilePanes.values().forEach(pane -> pane.getChildren().removeIf(n -> n instanceof ImageView));

    getBoardGame().getPlayers().forEach(player -> {
      int pos = player.getCurrentTile() != null ? player.getCurrentTile().getId() : 0;
      ImageView token = getOrCreatePlayerToken(player);

      if (animator != null) {
        animator.moveTokenToTile(token, pos);
      } else {
        StackPane tilePane = tilePanes.get(pos);
        if (tilePane != null) {
          tilePane.getChildren().add(token);
        }
      }
    });
  }

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

      token.setFitWidth(20);
      token.setFitHeight(30);
      token.setPreserveRatio(true);
      token.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 2, 0, 1, 1);");
      token.toFront();

      playerTokens.put(player, token);
      playerTokensByName.put(player.getName(), token);
    }
    return token;
  }

  /**
   * Updates button text and sizes when button states change
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

    // Update button text based on state (ensuring text fits)
    if (awaitingBuy) {
      int boardSize = getBoardGame().getBoard().getSizeOfBoard();
      if (boardSize > 28) {
        buyButton.setText("Buy");
        skipButton.setText("Skip");
      } else {
        buyButton.setText("Buy Property");
        skipButton.setText("Skip Purchase");
      }
    } else if (awaitingRent) {
      payRentButton.setText("Pay Rent");
    } else {
      // Reset to default text
      buyButton.setText("Buy");
      skipButton.setText("Skip");
      payRentButton.setText("Pay Rent");
    }
  }

  private void handleBuyProperty() { controller.buyPropertyForCurrentPlayer(); }
  private void handleSkipAction() { controller.skipActionForCurrentPlayer(); }
  private void handlePayRent() { controller.payRentForCurrentPlayer(); }
  private void handleJailRoll() { controller.handleJailRollDice(); }
  private void handleJailPay() { controller.handleJailPay(); }

  /**
   * Initializes board with dynamic sizing and recalculates button sizes.
   */
  private void initializeBoard() {
    boardPane.getChildren().clear();
    tilePanes.clear();
    int boardSize = getBoardGame().getBoard().getSizeOfBoard();
    int gridDim = getGridDimForBoardSize(boardSize);
    int tileSize = calculateOptimalTileSize(boardSize);

    // Recalculate button sizes for the new board size
    updateButtonSizes(boardSize);

    int[] tileIndex = {0};

    // Create board layout (top row, right column, bottom row, left column)
    // Top row
    java.util.stream.IntStream.range(0, gridDim)
        .filter(col -> tileIndex[0] < boardSize)
        .forEach(col -> {
          Tile tile = getBoardGame().getBoard().getTile(tileIndex[0]);
          StackPane tilePane = createTilePane(tile, tileSize);
          boardPane.add(tilePane, col, 0);
          tilePanes.put(tileIndex[0], tilePane);
          tileIndex[0]++;
        });

    // Right column
    java.util.stream.IntStream.range(1, gridDim - 1)
        .filter(row -> tileIndex[0] < boardSize)
        .forEach(row -> {
          Tile tile = getBoardGame().getBoard().getTile(tileIndex[0]);
          StackPane tilePane = createTilePane(tile, tileSize);
          boardPane.add(tilePane, gridDim - 1, row);
          tilePanes.put(tileIndex[0], tilePane);
          tileIndex[0]++;
        });

    // Bottom row
    java.util.stream.IntStream.iterate(gridDim - 1, col -> col >= 0, col -> col - 1)
        .filter(col -> tileIndex[0] < boardSize)
        .forEach(col -> {
          Tile tile = getBoardGame().getBoard().getTile(tileIndex[0]);
          StackPane tilePane = createTilePane(tile, tileSize);
          boardPane.add(tilePane, col, gridDim - 1);
          tilePanes.put(tileIndex[0], tilePane);
          tileIndex[0]++;
        });

    // Left column
    java.util.stream.IntStream.iterate(gridDim - 2, row -> row > 0, row -> row - 1)
        .filter(row -> tileIndex[0] < boardSize)
        .forEach(row -> {
          Tile tile = getBoardGame().getBoard().getTile(tileIndex[0]);
          StackPane tilePane = createTilePane(tile, tileSize);
          boardPane.add(tilePane, 0, row);
          tilePanes.put(tileIndex[0], tilePane);
          tileIndex[0]++;
        });

    // Center area with MONOPOLY text
    if (gridDim > 3) {
      StackPane centerArea = new StackPane();
      double centerSize = (gridDim - 2) * tileSize + (gridDim - 3) * 3;

      Rectangle centerRect = new Rectangle(centerSize, centerSize);
      centerRect.getStyleClass().add("monopoly-center-area");
      centerRect.setFill(Color.web("#f0f8ff"));
      centerRect.setStroke(Color.web("#4169e1"));
      centerRect.setStrokeWidth(2);

      javafx.scene.text.Text monopolyText = new javafx.scene.text.Text("MONOPOLY");
      monopolyText.getStyleClass().add("monopoly-center-text");
      monopolyText.setStyle("-fx-font-size: " + Math.max(16, centerSize / 8) + "px;");

      centerArea.getChildren().addAll(centerRect, monopolyText);
      boardPane.add(centerArea, 1, 1, gridDim - 2, gridDim - 2);
      GridPane.setHalignment(centerArea, javafx.geometry.HPos.CENTER);
      GridPane.setValignment(centerArea, javafx.geometry.VPos.CENTER);
    }

    updatePlayerInfoPanel();
    updatePlayerTokens();
    updateRollDiceButtonState();
  }

  private int calculateOptimalTileSize(int boardSize) {
    if (boardSize <= 20) return 70;      // Small board
    else if (boardSize <= 28) return 60; // Medium board
    else return 50;                      // Large board
  }

  private int getGridDimForBoardSize(int boardSize) {
    int n = 3;
    while (4 * (n - 1) < boardSize) n++;
    return n;
  }

  /**
   * Creates a tile pane with dynamic sizing and responsive fonts.
   */
  private StackPane createTilePane(Tile tile, int tileSize) {
    StackPane pane = new StackPane();
    pane.setPrefSize(tileSize, tileSize);
    pane.setMaxSize(tileSize, tileSize);
    pane.setMinSize(tileSize, tileSize);

    if (tile instanceof PropertyTile pt) {
      VBox propertyContainer = new VBox();
      propertyContainer.getStyleClass().add("property-tile-container");

      int colorBarHeight = Math.max(8, tileSize / 6);
      Rectangle colorBar = new Rectangle(tileSize, colorBarHeight);
      colorBar.getStyleClass().addAll("property-color-bar", "property-group-" + pt.getGroup());

      // Set property group colors
      Color groupColor = getPropertyGroupColor(pt.getGroup());
      colorBar.setFill(groupColor);

      Rectangle mainRect = new Rectangle(tileSize, tileSize - colorBarHeight);
      mainRect.getStyleClass().add("monopoly-tile");
      mainRect.setFill(Color.WHITE);
      mainRect.setStroke(Color.BLACK);
      mainRect.setStrokeWidth(1);

      Label label = new Label("$" + pt.getPrice());
      label.getStyleClass().add("monopoly-tile-label");
      label.setStyle("-fx-font-size: " + Math.max(8, tileSize / 8) + "px;");

      StackPane mainArea = new StackPane(mainRect, label);
      propertyContainer.getChildren().addAll(colorBar, mainArea);
      pane.getChildren().add(propertyContainer);
    } else {
      Rectangle rect = new Rectangle(tileSize, tileSize);
      rect.getStyleClass().add("monopoly-tile");
      rect.setStroke(Color.BLACK);
      rect.setStrokeWidth(1);

      Label label = new Label();
      label.getStyleClass().add("monopoly-tile-label");
      label.setStyle("-fx-font-size: " + Math.max(8, tileSize / 10) + "px;");

      if (tile instanceof GoTile) {
        rect.getStyleClass().add("go-tile-color");
        rect.setFill(Color.web("#90EE90"));
        label.setText("GO");
      } else if (tile instanceof JailTile) {
        rect.getStyleClass().add("jail-tile-color");
        rect.setFill(Color.web("#FFB6C1"));
        label.setText("JAIL");
      } else if (tile instanceof FreeParkingTile) {
        rect.getStyleClass().add("free-parking-tile-color");
        rect.setFill(Color.web("#F0E68C"));
        label.setText("FREE\nPARKING");
      } else if (tile.getAction() instanceof GoToJailAction) {
        rect.getStyleClass().add("go-to-jail-tile-color");
        rect.setFill(Color.web("#FFA07A"));
        label.setText("GO TO\nJAIL");
      } else {
        rect.getStyleClass().add("default-tile-color");
        rect.setFill(Color.web("#F5F5F5"));
        label.setText("");
      }

      pane.getChildren().addAll(rect, label);
    }

    return pane;
  }

  /**
   * Gets the color for a property group.
   */
  private Color getPropertyGroupColor(int group) {
    Color[] groupColors = {
        Color.web("#8B4513"), // Brown
        Color.web("#87CEEB"), // Light Blue
        Color.web("#FF69B4"), // Pink
        Color.web("#FFA500"), // Orange
        Color.web("#FF0000"), // Red
        Color.web("#FFFF00"), // Yellow
        Color.web("#00FF00"), // Green
        Color.web("#0000FF")  // Blue
    };
    return groupColors[group % groupColors.length];
  }

  public void setBoardGame(BoardGame boardGame) {
    if (this.boardGame != null) {
      this.boardGame.removeObserver(this);
    }
    this.boardGame = boardGame;
    this.boardGame.addObserver(this);
    initializeBoard();
    update();
  }

  public BorderPane getRoot() {
    return mainLayout;
  }

}