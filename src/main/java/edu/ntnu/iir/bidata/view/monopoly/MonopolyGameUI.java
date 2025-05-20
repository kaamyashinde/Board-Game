package edu.ntnu.iir.bidata.view.monopoly;

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
import edu.ntnu.iir.bidata.view.common.CommonButtons;
import edu.ntnu.iir.bidata.view.common.JavaFXGameUI;
import edu.ntnu.iir.bidata.view.common.DiceView;
import java.util.*;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import lombok.Setter;

/** JavaFX UI implementation for the Monopoly game. */
public class MonopolyGameUI extends JavaFXGameUI {
  private static final Logger LOGGER = Logger.getLogger(MonopolyGameUI.class.getName());
  private static final int GRID_DIM = 6; // 6x6 grid for 20-tile Monopoly
  private static final int TILE_SIZE = 60;
  private final Map<Integer, StackPane> tilePanes = new HashMap<>();
  private final Map<Player, Circle> playerTokens = new HashMap<>();
  private final Button rollDiceButton = new Button("Roll Dice");
  private final Button buyButton = new Button("Buy");
  private final Button skipButton = new Button("Skip");
  private final Button payRentButton = new Button("Pay Rent");
  private final Button jailRollButton = new Button("Roll Dice (Jail)");
  private final Button jailPayButton = new Button("Pay $50");
  private final Label actionLabel = new Label("");
  private final Color BLANK_COLOR = Color.LIGHTGRAY;
  private final Color[] GROUP_COLORS = {
    Color.SADDLEBROWN, Color.LIGHTBLUE, Color.HOTPINK, Color.ORANGE
  };
  private final Color GO_COLOR = Color.LIMEGREEN;
  private final Color JAIL_COLOR = Color.DARKGRAY;
  private final Color FREE_PARKING_COLOR = Color.GOLD;
  private final Color GO_TO_JAIL_COLOR = Color.ORANGERED;
  private final BorderPane mainLayout;
  private final GridPane boardPane;
  private final VBox playerInfoPanel;
  private final HBox gameControls;
  private final DiceView diceView = new DiceView();
  private final Stage primaryStage;
  protected BoardGame boardGame;

  @Setter private MonopolyController controller;
  private BorderPane root;

  public MonopolyGameUI(BoardGame boardGame, Stage primaryStage) {
    super(boardGame);
    this.boardGame = boardGame;
    this.primaryStage = primaryStage;
    this.controller = new MonopolyController(boardGame);
    this.mainLayout = new BorderPane();
    this.boardPane = new GridPane();
    this.playerInfoPanel = new VBox(10);
    this.gameControls = new HBox(10);

    // Set player names in controller to avoid NullPointerException
    List<String> playerNames = boardGame.getPlayers().stream().map(Player::getName).toList();
    controller.setPlayerNames(playerNames);
    setupUI();
  }

  private void setupUI() {
    Button backButton;
    Button saveButton;
    root = new BorderPane();
    root.setPadding(new Insets(25));
    root.setPrefWidth(1100);
    root.setPrefHeight(750);

    // Configure board pane
    boardPane.setHgap(5);
    boardPane.setVgap(5);
    boardPane.setPadding(new Insets(15));
    boardPane.getStyleClass().add("monopoly-board-pane");

    // Configure player info panel
    playerInfoPanel.setPadding(new Insets(15));
    playerInfoPanel.getStyleClass().add("monopoly-player-info-panel");
    playerInfoPanel.setPrefWidth(220);

    // Configure game controls
    gameControls.setPadding(new Insets(15));
    gameControls.setAlignment(Pos.CENTER);
    gameControls.getStyleClass().add("monopoly-game-controls");

    // Add components to main layout
    mainLayout.setCenter(boardPane);
    mainLayout.setRight(playerInfoPanel);
    mainLayout.setBottom(gameControls);

    // Set the root of the existing scene
    getScene().setRoot(mainLayout);

    primaryStage.setWidth(1300);
    primaryStage.setHeight(850);
    primaryStage.setMinWidth(1300);
    primaryStage.setMinHeight(850);

    // Initialize the board
    initializeBoard();

    // Add dice label and roll button to game controls
    diceView.getStyleClass().add("monopoly-dice-view");

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

    backButton = CommonButtons.backToMainMenu(primaryStage, true, controller);
    backButton.getStyleClass().add("monopoly-back-button");

    actionLabel.getStyleClass().add("monopoly-action-label");

    saveButton = CommonButtons.saveGameBtn(true, controller, actionLabel);
    saveButton.getStyleClass().add("game-control-button");

    gameControls.getChildren().add(backButton);
    gameControls
        .getChildren()
        .addAll(
            rollDiceButton,
            buyButton,
            skipButton,
            payRentButton,
            jailRollButton,
            jailPayButton,
            saveButton,
            diceView);

    // Add stylesheets
    getScene().getStylesheets().add(getClass().getResource("/monopoly.css").toExternalForm());
    getScene().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

    // Ensure buttons have consistent styling
    rollDiceButton.setStyle("-fx-border-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.5, 0, 0);");
    buyButton.setStyle("-fx-border-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.5, 0, 0);");
    skipButton.setStyle("-fx-border-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.5, 0, 0);");
    payRentButton.setStyle("-fx-border-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.5, 0, 0);");
    jailRollButton.setStyle("-fx-border-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.5, 0, 0);");
    jailPayButton.setStyle("-fx-border-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.5, 0, 0);");
    backButton.setStyle("-fx-border-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.5, 0, 0);");

    // Ensure actionLabel has consistent styling
    actionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
  }

  private void handleRollDice() {
    controller.handlePlayerMove();
    // Show dice value
    int[] diceValues = controller.getLastDiceRolls();
    if (diceValues != null && diceValues.length == 2) {
      diceView.setValues(diceValues[0], diceValues[1]);
    } else if (diceValues != null && diceValues.length == 1) {
      diceView.setValues(diceValues[0], diceValues[0]);
    } else {
      diceView.setValues(1, 1);
    }
    // Optionally, display the sum somewhere if needed
  }

  private void handleBuyProperty() {
    controller.buyPropertyForCurrentPlayer();
  }

  private void handleSkipAction() {
    controller.skipActionForCurrentPlayer();
  }

  private void handlePayRent() {
    controller.payRentForCurrentPlayer();
  }

  private void handleJailRoll() {
    controller.handleJailRollDice();
  }

  private void handleJailPay() {
    controller.handleJailPay();
  }

  @Override
  public void update() {
    updatePlayerInfoPanel();
    updatePlayerTokens();
    updateRollDiceButtonState();
  }

  private void updatePlayerInfoPanel() {
    LOGGER.info("Clearing playerInfoPanel and updating player info...");
    playerInfoPanel.getChildren().clear();
    int playerCount = 0;
    for (Player player : boardGame.getPlayers()) {
      LOGGER.info("Processing player: " + player.getName() + " of class: " + player.getClass().getName());
      if (player instanceof SimpleMonopolyPlayer) {
        SimpleMonopolyPlayer monopolyPlayer = (SimpleMonopolyPlayer) player;
        VBox playerBox = new VBox(5);
        playerBox.setPadding(new Insets(20));
        playerBox.getStyleClass().add("monopoly-player-box");

        Label nameLabel = new Label(monopolyPlayer.getName());
        nameLabel.getStyleClass().add("monopoly-player-name");
        Label moneyLabel = new Label("Money: $" + monopolyPlayer.getMoney());
        Label positionLabel =
            new Label("Position: Tile #" + monopolyPlayer.getCurrentTile().getId());
        Label propertiesLabel =
            new Label("Properties: " + monopolyPlayer.getOwnedProperties().size());

        playerBox.getChildren().addAll(nameLabel, moneyLabel, positionLabel, propertiesLabel);
        playerInfoPanel.getChildren().add(playerBox);
        LOGGER.info(String.format("Added player to panel: name=%s, money=%d, position=%d, properties=%d", 
          monopolyPlayer.getName(), monopolyPlayer.getMoney(), monopolyPlayer.getCurrentTile().getId(), monopolyPlayer.getOwnedProperties().size()));
        playerCount++;
      }
    }
    LOGGER.info("Total players added to playerInfoPanel: " + playerCount);
  }

  private void updatePlayerTokens() {
    // Remove all tokens
    for (StackPane pane : tilePanes.values()) {
      pane.getChildren().removeIf(n -> n instanceof Circle);
    }
    // Add tokens for each player
    int colorIdx = 0;
    Color[] tokenColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE};
    for (Player player : getBoardGame().getPlayers()) {
      int pos = player.getCurrentTile() != null ? player.getCurrentTile().getId() : 0;
      StackPane tilePane = tilePanes.get(pos);
      Circle token = new Circle(12, tokenColors[colorIdx % tokenColors.length]);
      token.setStroke(Color.BLACK);
      tilePane.getChildren().add(token);
      colorIdx++;
    }
  }

  private void updateRollDiceButtonState() {
    Player current = getBoardGame().getCurrentPlayer();
    boolean isGameOver = getBoardGame().isGameOver();
    boolean awaitingBuy = controller.isAwaitingPlayerAction();
    boolean awaitingRent = controller.isAwaitingRentAction();
    boolean inJail = controller.isCurrentPlayerInJail();
    boolean rollDiceActive = current != null && !isGameOver && !awaitingBuy && !awaitingRent && !inJail;

    rollDiceButton.setDisable(!rollDiceActive);
    buyButton.setDisable(rollDiceActive || isGameOver || awaitingRent || inJail);
    skipButton.setDisable(rollDiceActive || isGameOver || awaitingRent || inJail);
    payRentButton.setDisable(rollDiceActive || isGameOver || awaitingBuy || inJail);
    jailRollButton.setDisable(isGameOver || !inJail);
    jailPayButton.setDisable(isGameOver || !inJail);
  }

  @Override
  public Scene getScene() {
    return super.getScene();
  }

  public void setBoardGame(BoardGame boardGame) {
    // Remove this UI as observer from the old boardGame if needed
    if (this.boardGame != null) {
      this.boardGame.removeObserver(this);
    }
    this.boardGame = boardGame;
    // Register as observer to the new boardGame
    this.boardGame.addObserver(this);
    initializeBoard();
    update();
  }

  private void initializeBoard() {
    boardPane.getChildren().clear();
    tilePanes.clear();
    int tileIndex = 0;
    // Top row (left to right)
    for (int col = 0; col < GRID_DIM; col++) {
      if (tileIndex >= 20) break;
      Tile tile = getBoardGame().getBoard().getTile(tileIndex);
      StackPane tilePane = createTilePane(tile);
      boardPane.add(tilePane, col, 0);
      tilePanes.put(tileIndex, tilePane);
      tileIndex++;
    }
    // Right column (top to bottom, excluding top)
    for (int row = 1; row < GRID_DIM - 1; row++) {
      if (tileIndex >= 20) break;
      Tile tile = getBoardGame().getBoard().getTile(tileIndex);
      StackPane tilePane = createTilePane(tile);
      boardPane.add(tilePane, GRID_DIM - 1, row);
      tilePanes.put(tileIndex, tilePane);
      tileIndex++;
    }
    // Bottom row (right to left)
    for (int col = GRID_DIM - 1; col >= 0; col--) {
      if (tileIndex >= 20) break;
      Tile tile = getBoardGame().getBoard().getTile(tileIndex);
      StackPane tilePane = createTilePane(tile);
      boardPane.add(tilePane, col, GRID_DIM - 1);
      tilePanes.put(tileIndex, tilePane);
      tileIndex++;
    }
    // Left column (bottom to top, excluding top and bottom)
    for (int row = GRID_DIM - 2; row > 0; row--) {
      if (tileIndex >= 20) break;
      Tile tile = getBoardGame().getBoard().getTile(tileIndex);
      StackPane tilePane = createTilePane(tile);
      boardPane.add(tilePane, 0, row);
      tilePanes.put(tileIndex, tilePane);
      tileIndex++;
    }
    // Fill the center with blank tiles
    for (int row = 1; row < GRID_DIM - 1; row++) {
      for (int col = 1; col < GRID_DIM - 1; col++) {
        Rectangle blank = new Rectangle(70, 70, BLANK_COLOR);
        StackPane blankPane = new StackPane(blank);
        boardPane.add(blankPane, col, row);
      }
    }
    updatePlayerInfoPanel();
    updatePlayerTokens();
    updateRollDiceButtonState();
  }

  private StackPane createTilePane(Tile tile) {
    Rectangle rect = new Rectangle(70, 70);
    rect.getStyleClass().add("monopoly-tile");
    Label label = new Label();
    label.getStyleClass().add("monopoly-tile-label");

    if (tile instanceof PropertyTile) {
      PropertyTile pt = (PropertyTile) tile;
      rect.setFill(GROUP_COLORS[pt.getGroup() % GROUP_COLORS.length]);
      label.setText("Property\n$" + pt.getPrice());
    } else if (tile instanceof GoTile) {
      rect.setFill(GO_COLOR);
      label.setText("GO");
    } else if (tile instanceof JailTile) {
      rect.setFill(JAIL_COLOR);
      label.setText("JAIL");
    } else if (tile instanceof FreeParkingTile) {
      rect.setFill(FREE_PARKING_COLOR);
      label.setText("FREE\nPARKING");
    } else if (tile.getAction() instanceof GoToJailAction) {
      rect.setFill(GO_TO_JAIL_COLOR);
      label.setText("GO TO\nJAIL");
    } else {
      rect.setFill(BLANK_COLOR);
      label.setText("");
    }
    StackPane pane = new StackPane(rect, label);
    pane.setPrefSize(70, 70);
    return pane;
  }

  public BorderPane getRoot() {
    return mainLayout;
  }
}
