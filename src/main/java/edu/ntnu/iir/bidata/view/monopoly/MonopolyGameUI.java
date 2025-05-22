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
import edu.ntnu.iir.bidata.model.utils.DefaultGameMediator;
import edu.ntnu.iir.bidata.model.utils.GameMediator;
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
import edu.ntnu.iir.bidata.Inject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/** JavaFX UI implementation for the Monopoly game. */
public class MonopolyGameUI extends JavaFXGameUI {
  private static final Logger LOGGER = Logger.getLogger(MonopolyGameUI.class.getName());
  private static final int GRID_DIM = 6; // 6x6 grid for 20-tile Monopoly
  private static final int TILE_SIZE = 60;
  private final Map<Integer, StackPane> tilePanes = new HashMap<>();
  private final Map<Player, ImageView> playerTokens = new HashMap<>();
  private final Button rollDiceButton = new Button("Roll Dice");
  private final Button buyButton = new Button("Buy");
  private final Button skipButton = new Button("Skip");
  private final Button payRentButton = new Button("Pay Rent");
  private final Button jailRollButton = new Button("Roll Dice (Jail)");
  private final Button jailPayButton = new Button("Pay $50");
  private final Label actionLabel = new Label("");
  private final Color[] GROUP_COLORS = {
      Color.BROWN, Color.LIGHTBLUE, Color.PINK, Color.ORANGE,
      Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE
  };
  private final BorderPane mainLayout;
  private final GridPane boardPane;
  private final VBox playerInfoPanel;
  private final HBox gameControls;
  private final DiceView diceView = new DiceView();
  private final Stage primaryStage;
  protected BoardGame boardGame;

  @Setter private MonopolyController controller;
  private final GameMediator mediator;
  private BorderPane root;

  @Inject
  public MonopolyGameUI(BoardGame boardGame, Stage primaryStage, MonopolyController controller, GameMediator mediator) {
    super(boardGame, primaryStage);
    this.boardGame = boardGame;
    this.primaryStage = primaryStage;
    this.controller = controller;
    this.mediator = mediator;
    this.mainLayout = new BorderPane();
    this.boardPane = new GridPane();
    this.playerInfoPanel = new VBox(10);
    this.gameControls = new HBox(10);

    // Set player names in controller to avoid NullPointerException
    List<String> playerNames = boardGame.getPlayers().stream().map(Player::getName).toList();
    controller.setPlayerNames(playerNames);
    setupUI();

    // Register mediator listener to update UI on nextPlayer event
    if (mediator instanceof DefaultGameMediator m) {
      m.register((sender, event) -> {
        if ("nextPlayer".equals(event)) {
          javafx.application.Platform.runLater(() -> {
            updatePlayerInfoPanel();
            updatePlayerTokens();
            updateRollDiceButtonState();
          });
        }
      });
    }
  }

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

    // --- Bottom bar: Game controls ---
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

    controls.getChildren().addAll(diceView, rollDiceButton, buyButton, skipButton, payRentButton, jailRollButton, jailPayButton);
    root.setBottom(controls);

    // Add actionLabel with proper styling
    actionLabel.getStyleClass().add("monopoly-action-label");

    // Set the root of the existing scene
    getScene().setRoot(root);

    primaryStage.setMinWidth(900);
    primaryStage.setMinHeight(600);

    // Initialize the board
    initializeBoard();

    // Add stylesheets
    getScene().getStylesheets().add(getClass().getResource("/monopoly.css").toExternalForm());
    getScene().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
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
      pane.getChildren().removeIf(n -> n instanceof ImageView);
    }
    // Add tokens for each player
    for (Player player : getBoardGame().getPlayers()) {
      int pos = player.getCurrentTile() != null ? player.getCurrentTile().getId() : 0;
      StackPane tilePane = tilePanes.get(pos);
      ImageView token = playerTokens.get(player);
      if (token == null) {
        String tokenImage = player.getTokenImage();
        if (tokenImage != null && !tokenImage.isEmpty()) {
          Image img = new Image(getClass().getResourceAsStream("/tokens/" + tokenImage));
          token = new ImageView(img);
          token.setFitWidth(32);
          token.setFitHeight(48);
        } else {
          token = new ImageView();
          token.setFitWidth(32);
          token.setFitHeight(48);
        }
        playerTokens.put(player, token);
      }
      tilePane.getChildren().add(token);
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
    return primaryStage.getScene();
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

  private int getGridDimForBoardSize(int boardSize) {
    // Find the smallest n such that 4*(n-1) >= boardSize
    int n = 3;
    while (4 * (n - 1) < boardSize) {
      n++;
    }
    return n;
  }

  private void initializeBoard() {
    boardPane.getChildren().clear();
    tilePanes.clear();
    int boardSize = getBoardGame().getBoard().getSizeOfBoard();
    int gridDim = getGridDimForBoardSize(boardSize);
    int tileIndex = 0;
    // Top row (left to right)
    for (int col = 0; col < gridDim && tileIndex < boardSize; col++) {
      Tile tile = getBoardGame().getBoard().getTile(tileIndex);
      StackPane tilePane = createTilePane(tile);
      boardPane.add(tilePane, col, 0);
      tilePanes.put(tileIndex, tilePane);
      tileIndex++;
    }
    // Right column (top to bottom, excluding top)
    for (int row = 1; row < gridDim - 1 && tileIndex < boardSize; row++) {
      Tile tile = getBoardGame().getBoard().getTile(tileIndex);
      StackPane tilePane = createTilePane(tile);
      boardPane.add(tilePane, gridDim - 1, row);
      tilePanes.put(tileIndex, tilePane);
      tileIndex++;
    }
    // Bottom row (right to left)
    for (int col = gridDim - 1; col >= 0 && tileIndex < boardSize; col--) {
      Tile tile = getBoardGame().getBoard().getTile(tileIndex);
      StackPane tilePane = createTilePane(tile);
      boardPane.add(tilePane, col, gridDim - 1);
      tilePanes.put(tileIndex, tilePane);
      tileIndex++;
    }
    // Left column (bottom to top, excluding top and bottom)
    for (int row = gridDim - 2; row > 0 && tileIndex < boardSize; row--) {
      Tile tile = getBoardGame().getBoard().getTile(tileIndex);
      StackPane tilePane = createTilePane(tile);
      boardPane.add(tilePane, 0, row);
      tilePanes.put(tileIndex, tilePane);
      tileIndex++;
    }

    // Create a center area with MONOPOLY text
    if (gridDim > 3) {
      // Create a pane spanning the entire center area
      StackPane centerArea = new StackPane();

      // Create background rectangle
      Rectangle centerRect = new Rectangle(
          (gridDim - 2) * 70 + (gridDim - 3) * 5, // Width accounting for tile sizes and gaps
          (gridDim - 2) * 70 + (gridDim - 3) * 5  // Height accounting for tile sizes and gaps
      );
      centerRect.getStyleClass().add("monopoly-center-area");

      // Create MONOPOLY text
      javafx.scene.text.Text monopolyText = new javafx.scene.text.Text("MONOPOLY");
      monopolyText.getStyleClass().add("monopoly-center-text");

      centerArea.getChildren().addAll(centerRect, monopolyText);

      // Add to center of grid, spanning the appropriate number of cells
      boardPane.add(centerArea, 1, 1, gridDim - 2, gridDim - 2);
      GridPane.setHalignment(centerArea, javafx.geometry.HPos.CENTER);
      GridPane.setValignment(centerArea, javafx.geometry.VPos.CENTER);
    }

    updatePlayerInfoPanel();
    updatePlayerTokens();
    updateRollDiceButtonState();
  }

  private StackPane createTilePane(Tile tile) {
    StackPane pane = new StackPane();
    pane.setPrefSize(70, 70);

    if (tile instanceof PropertyTile) {
      PropertyTile pt = (PropertyTile) tile;
      // Create a more authentic property tile with a color bar at the top
      VBox propertyContainer = new VBox();
      propertyContainer.getStyleClass().add("property-tile-container");

      // Create the color bar at the top
      Rectangle colorBar = new Rectangle(70, 15);
      colorBar.getStyleClass().addAll("property-color-bar", "property-group-" + pt.getGroup());

      // Create the main part of the tile
      Rectangle mainRect = new Rectangle(70, 55);
      mainRect.getStyleClass().add("monopoly-tile");
      mainRect.setFill(Color.WHITE);

      // Create the property information
      Label label = new Label("$" + pt.getPrice());
      label.getStyleClass().add("monopoly-tile-label");

      // Add to container
      StackPane mainArea = new StackPane(mainRect, label);
      propertyContainer.getChildren().addAll(colorBar, mainArea);

      pane.getChildren().add(propertyContainer);
    } else {
      // For non-property tiles, create a simple rectangular tile
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

  public BorderPane getRoot() {
    return mainLayout;
  }

  @Override
  public void refreshUIFromBoardGame() {
    updatePlayerInfoPanel();
    updatePlayerTokens();
    updateRollDiceButtonState();
  }
}