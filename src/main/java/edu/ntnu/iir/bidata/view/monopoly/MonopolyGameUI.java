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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  @Setter private MonopolyController controller;
  private BorderPane root;

  /**
   * Constructs the MonopolyGameUI, setting up the primary user interface components for the
   * Monopoly game. Initializes various UI elements, registers event listeners, and connects
   * necessary components such as the game mediator and the controller.
   *
   * @param boardGame the BoardGame instance representing the state and rules of the game
   * @param primaryStage the primary Stage for displaying the game's user interface
   * @param controller the MonopolyController responsible for managing user interactions and game
   *     logic
   * @param mediator the GameMediator facilitating communication between game components
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

  /**
   * Sets up the user interface for the Monopoly game. This method initializes and configures the
   * layout of the main game components, including the top bar, board, player info panel, game
   * control buttons, and action label. It also applies necessary styles and adds event listeners to
   * various UI elements.
   *
   * <p>The main layout structure includes: - A top bar with 'Back to Main Menu' and 'Save Game'
   * buttons. - A central game board displayed in a grid pane. - A right panel showing player
   * information. - A bottom bar with game control buttons (e.g., roll dice, buy property, skip
   * turn, etc.).
   *
   * <p>Additional configurations include defining minimum window sizes, adding stylesheets, and
   * initializing the game board.
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

  /**
   * Updates the user interface components of the Monopoly game. This method refreshes the display
   * of player information, updates the player tokens on the game board, and adjusts the state of
   * the "Roll Dice" button along with other relevant UI controls.
   *
   * <p>The method performs the following: - Invokes `updatePlayerInfoPanel()` to update the player
   * information displayed in the right panel. - Invokes `updatePlayerTokens()`
   */
  @Override
  public void update() {
    updatePlayerInfoPanel();
    updatePlayerTokens();
    updateRollDiceButtonState();
  }

  /**
   * Retrieves the current {@link Scene} associated with the primary stage of the application.
   *
   * @return the current Scene object displayed on the primary Stage
   */
  @Override
  public Scene getScene() {
    return primaryStage.getScene();
  }

  /**
   * Updates the player information panel to reflect the current state of all players in the game.
   * This method clears the existing panel and iterates through the list of players to display each
   * player's relevant details in the user interface.
   *
   * <p>For players of type {@code SimpleMonopolyPlayer}, the following information is displayed: -
   * Name of the player - Amount of money they possess - Current position on the Monopoly board
   * (tile ID) - Number of properties owned
   *
   * <p>Each player's details are added as a styled VBox to the player information panel. The method
   * also logs relevant information, such as the player's name, class, and updated UI information.
   */
  private void updatePlayerInfoPanel() {
    LOGGER.info("Clearing playerInfoPanel and updating player info...");
    playerInfoPanel.getChildren().clear();
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

                playerBox
                    .getChildren()
                    .addAll(nameLabel, moneyLabel, positionLabel, propertiesLabel);
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
   * Updates the positions of player tokens on the Monopoly game board.
   *
   * <p>This method removes all existing tokens from the game board's tile panes and updates the
   * tokens to reflect the current positions of all players. The following steps are performed:
   *
   * <p>1. Clears all tokens from the tile panes, ensuring they are reset before updating. 2. For
   * each player in the game: - Determines the player's current tile based on their position on the
   * board. - Retrieves or creates a graphical token (ImageView) for the player. - If the player
   * already has a token, it is reused. - If the player does not have a token, a new one is created
   * based on the player's associated token image or a default size is applied. - Adds the token to
   * the appropriate tile pane corresponding to the player's current position.
   *
   * <p>The graphical token is represented as an {@link ImageView}, and its size is standardized to
   * fit properly within the board's tiles.
   */
  private void updatePlayerTokens() {
    // Remove all tokens
    tilePanes.values().forEach(pane -> pane.getChildren().removeIf(n -> n instanceof ImageView));
    getBoardGame()
        .getPlayers()
        .forEach(
            player -> {
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
            });
  }

  /**
   * Updates the state of the "Roll Dice" button and other related UI controls based on the current
   * game state and player status.
   *
   * <p>This method performs the following: - Determines the current player and ensures the game is
   * not in a "game over" state. - Evaluates whether the "Roll Dice" action is available by checking
   * if the player is not awaiting actions like buying properties, paying rent, or currently in
   * jail. - Enables or disables the "Roll Dice" button, "Buy" button, "Skip" button, "Pay Rent"
   * button, "Jail Roll" button, and "Jail Pay" button accordingly to ensure proper gameplay flow
   * and prevent invalid actions.
   *
   * <p>Conditions used to update the state of each button: - The "Roll Dice" button is enabled only
   * when it is the current player's turn, the game is not over, and the player is not awaiting
   * other actions or in jail
   */
  private void updateRollDiceButtonState() {
    Player current = getBoardGame().getCurrentPlayer();
    boolean isGameOver = getBoardGame().isGameOver();
    boolean awaitingBuy = controller.isAwaitingPlayerAction();
    boolean awaitingRent = controller.isAwaitingRentAction();
    boolean inJail = controller.isCurrentPlayerInJail();
    boolean rollDiceActive =
        current != null && !isGameOver && !awaitingBuy && !awaitingRent && !inJail;

    rollDiceButton.setDisable(!rollDiceActive);
    buyButton.setDisable(rollDiceActive || isGameOver || awaitingRent || inJail);
    skipButton.setDisable(rollDiceActive || isGameOver || awaitingRent || inJail);
    payRentButton.setDisable(rollDiceActive || isGameOver || awaitingBuy || inJail);
    jailRollButton.setDisable(isGameOver || !inJail);
    jailPayButton.setDisable(isGameOver || !inJail);
  }

  /**
   * Handles the logic for the "Roll Dice" action in the Monopoly game user interface.
   *
   * <p>This method integrates with the game controller to process the player's dice roll, updates
   * the dice display in the UI, and ensures that correct dice values are shown based on the result
   * of the roll.
   *
   * <p>The following operations are performed: 1. Invokes the game controller to manage player
   * movement logic. 2. Retrieves the last dice roll values from the game controller. 3. Updates the
   * dice view to display the retrieved dice values. If the retrieved values contain: - Two dice
   * values, they are displayed directly. - A single dice value, it is duplicated to represent both
   * dice. - Null or invalid values, defaults both dice to a value of 1.
   */
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

  /**
   * Handles the buy property action within the Monopoly game user interface.
   *
   * <p>This method delegates the responsibility of managing the purchase of the property that the
   * current player has landed on to the game controller. If the action is valid based on the game
   * state, the controller ensures the following:
   *
   * <p>- The current player successfully acquires the property. - The game state is updated to
   * reflect the purchase. - Turn progression and transition to the next player are handled
   * appropriately.
   *
   * <p>This method is triggered when the "Buy" button is interacted with, and it facilitates the
   * smooth integration between the user interface and the game's core logic defined in the
   * controller.
   */
  private void handleBuyProperty() {
    controller.buyPropertyForCurrentPlayer();
  }

  /**
   * Handles the "Skip Turn" action in the Monopoly game user interface.
   *
   * <p>This method delegates the responsibility of skipping the current player's action to the game
   * controller. It ensures that the game progresses smoothly by performing the following
   * operations:
   *
   * <p>- Skips the current player's turn by invoking the corresponding method in the controller. -
   * Advances the game state to transition to the next player's turn. - Allows the game logic to
   * resolve any pending actions or state updates associated with skipping the current turn.
   *
   * <p>This method is typically invoked when the "Skip" button is clicked in the user interface,
   * signaling that the current player wishes to forgo their action or is unable to perform an
   * action.
   */
  private void handleSkipAction() {
    controller.skipActionForCurrentPlayer();
  }

  /**
   * Handles the action to process the current player's obligation to pay rent in the Monopoly game.
   *
   * <p>This method delegates the responsibility to the game controller to manage and execute the
   * rent payment logic for the current player. It ensures that the game state is updated
   * accordingly and validates that the necessary deductions or transactions occur based on the game
   * rules.
   *
   * <p>Operations performed by this method include: - Invoking the controller's logic to deduct
   * rent from the current player's balance. - Updating the game state to reflect the rent payment.
   * - Facilitating smooth gameplay progression post rent payment.
   *
   * <p>This method is typically triggered when the "Pay Rent" button is interacted with in the user
   * interface.
   */
  private void handlePayRent() {
    controller.payRentForCurrentPlayer();
  }

  /**
   * Handles the action of rolling the dice when a player is in jail. This method delegates the
   * handling of the dice roll to the controller's {@code handleJailRollDice} method, which manages
   * the logic for determining the player's outcome based on the roll while in jail.
   */
  private void handleJailRoll() {
    controller.handleJailRollDice();
  }

  /**
   * Handles the action for processing jail pay. This method delegates the operation to the
   * controller's handleJailPay method.
   */
  private void handleJailPay() {
    controller.handleJailPay();
  }

  /**
   * Initializes the game board by creating and placing the tile panes in a circular formation (top
   * row, right column, bottom row, and left column). For larger boards, adds a center area with
   * decorative "MONOPOLY" text.
   *
   * <ul>
   *   <li>Clears the existing board and tile panes.
   *   <li>Determines the size and grid dimensions of the board based on the game's configuration.
   *   <li>Populates the board in the following order:
   *       <ul>
   *         <li>Top row: Places tiles from left to right.
   *         <li>Right column: Places tiles from top to bottom (excluding the corners).
   *         <li>Bottom row: Places tiles from right to left.
   *         <li>Left column: Places tiles from bottom to top (excluding the corners).
   *       </ul>
   *   <li>Adds a decorative center area for larger boards, with "MONOPOLY" text.
   *   <li>Updates the player information panel, player tokens, and the state of the roll dice
   *       button.
   * </ul>
   *
   * <p>This method ensures the board is correctly initialized and displayed according to the game's
   * current configuration.
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
   * Calculates the smallest grid dimension n such that the perimeter of a grid with side length
   * (n-1) is greater than or equal to the provided board size.
   *
   * @param boardSize the total size of the board, used to determine the required grid dimension
   * @return the smallest integer n such that 4 * (n - 1) >= boardSize
   */
  private int getGridDimForBoardSize(int boardSize) {
    // Find the smallest n such that 4*(n-1) >= boardSize
    int n = 3;
    while (4 * (n - 1) < boardSize) {
      n++;
    }
    return n;
  }

  /**
   * Creates a StackPane representation of a tile for display in a game board UI. The appearance of
   * the tile varies based on its type. Property tiles include a color bar and display their price,
   * while other tiles have unique styling and labels based on their specific type (e.g., GoTile,
   * JailTile).
   *
   * @param tile the tile to be represented as a graphical StackPane
   * @return the StackPane representation of the given tile, styled appropriately
   */
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

  /**
   * Sets the board game instance for this UI and updates its state accordingly. It unregisters this
   * UI as an observer from the previous board game (if any) and registers it as an observer for the
   * new board game. Finally, it initializes and updates the board based on the new board game.
   *
   * @param boardGame the new instance of the BoardGame to be set
   */
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

  /**
   * Retrieves the root layout of the application.
   *
   * @return the main layout represented by a BorderPane object.
   */
  public BorderPane getRoot() {
    return mainLayout;
  }
}
