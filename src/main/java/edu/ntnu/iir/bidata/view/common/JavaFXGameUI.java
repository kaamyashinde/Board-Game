package edu.ntnu.iir.bidata.view.common;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Observer;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class JavaFXGameUI implements Observer {

  private final BoardGame boardGame;
  private final Stage primaryStage;
  private final Map<Player, Circle> playerTokens;
  private final Map<Integer, StackPane> tilePanes;
  private final GridPane boardPane;
  private final VBox playerInfoPane;
  private final Label currentPlayerLabel;
  private final Label diceRollLabel;
  private final Label statusLabel;
  private final Button nextTurnButton;
  private final DiceView diceView;
  private final VBox gameInfoPane;

  public JavaFXGameUI(BoardGame boardGame) {
    this.boardGame = boardGame;
    this.playerTokens = new HashMap<>();
    this.tilePanes = new HashMap<>();
    this.primaryStage = new Stage();
    this.boardPane = new GridPane();
    this.playerInfoPane = new VBox(10);
    this.gameInfoPane = new VBox(10);
    this.nextTurnButton = new Button("Roll Dice");
    this.currentPlayerLabel = new Label();
    this.diceRollLabel = new Label();
    this.statusLabel = new Label();
    this.diceView = new DiceView();

    boardGame.addObserver(this);
    initializeUI();
  }

  private void initializeUI() {
    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20));

    // Setup board
    setupBoard();
    ScrollPane scrollPane = new ScrollPane(boardPane);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    root.setCenter(scrollPane);

    // Setup player info panel
    setupPlayerInfoPanel();
    root.setRight(playerInfoPane);

    // Setup game info panel
    setupGameInfoPanel();
    root.setTop(gameInfoPane);

    // Setup controls with dice
    HBox controls = new HBox(20);
    controls.setPadding(new Insets(20));
    controls.setAlignment(Pos.CENTER);
    controls.getChildren().addAll(diceView, nextTurnButton);
    root.setBottom(controls);

    Scene scene = new Scene(root, 800, 600);
    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

    primaryStage.setTitle("Snakes and Ladders");
    primaryStage.setScene(scene);
  }

  private void setupBoard() {
    boardPane.setPadding(new Insets(20));
    boardPane.setHgap(2);
    boardPane.setVgap(2);
    boardPane.setAlignment(Pos.CENTER);

    int boardSize = boardGame.getBoard().getSizeOfBoard();
    int gridSize = (int) Math.ceil(Math.sqrt(boardSize));

    // Create a background for the board
    Rectangle boardBackground = new Rectangle(
        (gridSize * 70) + (gridSize * 2), // width
        (gridSize * 70) + (gridSize * 2)  // height
    );
    boardBackground.getStyleClass().add("board-background");
    boardPane.getChildren().add(boardBackground);

    // Create tiles in a snake pattern (alternating rows)
    for (int i = 0; i < boardSize; i++) {
      int row = i / gridSize;
      int col = (row % 2 == 0) ? i % gridSize : gridSize - 1 - (i % gridSize);
      int actualRow = gridSize - 1 - row; // Start from bottom

      StackPane tilePane = new StackPane();
      tilePane.setPrefSize(70, 70);

      Rectangle tile = new Rectangle(70, 70);
      tile.getStyleClass().add("board-tile");

      Tile boardTile = boardGame.getBoard().getPositionOnBoard(i);

      // Style the tile based on its type
      if (i == 0) {
        tile.getStyleClass().add("board-tile-start");
      } else if (boardTile.getAction() != null) {
        tile.getStyleClass().add("board-tile-special");
      } else {
        tile.getStyleClass().add("board-tile-regular");
      }

      // Create content box for tile information
      VBox contentBox = new VBox(2);
      contentBox.setAlignment(Pos.CENTER);
      contentBox.setMaxWidth(65);

      // Add tile number
      Text number = new Text(String.valueOf(i));
      number.getStyleClass().add("tile-number");

      // Add action text if the tile has an action
      if (boardTile.getAction() != null) {
        Text actionText = new Text(boardTile.getAction().getDescription());
        actionText.getStyleClass().add("tile-action-text");
        contentBox.getChildren().add(actionText);
      }

      contentBox.getChildren().add(number);
      tilePane.getChildren().addAll(tile, contentBox);
      tilePanes.put(i, tilePane);

      // Add the tile to the board
      boardPane.add(tilePane, col, actualRow);
    }
  }

  private void setupPlayerInfoPanel() {
    playerInfoPane.setPadding(new Insets(20));
    playerInfoPane.getStyleClass().add("player-info-panel");

    Label title = new Label("Player Information");
    title.getStyleClass().add("title-label");

    currentPlayerLabel.getStyleClass().add("status-label");
    diceRollLabel.getStyleClass().add("label");

    playerInfoPane.getChildren().addAll(title, currentPlayerLabel, diceRollLabel);
  }

  private void setupGameInfoPanel() {
    gameInfoPane.setPadding(new Insets(20));
    gameInfoPane.getStyleClass().add("control-panel");

    Label title = new Label("Game Status");
    title.getStyleClass().add("title-label");

    statusLabel.getStyleClass().add("status-label");

    gameInfoPane.getChildren().addAll(title, statusLabel);
  }

  public void showWelcomeMessage() {
    Platform.runLater(() -> {
      statusLabel.setText("Welcome to Snakes and Ladders!");
      primaryStage.show();
    });
  }

  public void showPlayerTurn(Player player) {
    Platform.runLater(() -> {
      currentPlayerLabel.setText("Current Player: " + player.getName());
      statusLabel.setText(player.getName() + "'s turn to roll the dice!");

      if (!playerTokens.containsKey(player)) {
        Circle token = createPlayerToken(player, playerTokens.size() + 1);
        playerTokens.put(player, token);
        updatePlayerPosition(player);
      }
    });
  }

  private Circle createPlayerToken(Player player, int playerNumber) {
    Circle token = new Circle(20);
    token.getStyleClass().add("player-token");
    token.getStyleClass().add("player-token-" + playerNumber);
    return token;
  }

  private void updatePlayerPosition(Player player) {
    Circle token = playerTokens.get(player);
    if (token != null) {
      int position = player.getCurrentTile().getId();
      StackPane tilePane = tilePanes.get(position);

      for (StackPane pane : tilePanes.values()) {
        pane.getChildren().remove(token);
      }

      tilePane.getChildren().add(token);
    }
  }

  public void showDiceRoll(Player player, int rollResult) {
    Platform.runLater(() -> {
      diceRollLabel.setText(player.getName() + " rolled: " + rollResult);
      diceView.setValue(rollResult);
      statusLabel.setText(player.getName() + " moves " + rollResult + " spaces!");
    });
  }

  public void showTileAction(Player player, TileAction action) {
    Platform.runLater(() -> {
      diceRollLabel.setText(player.getName() + " triggered: " + action.getDescription());
      statusLabel.setText(player.getName() + " " + action.getDescription() + "!");
    });
  }

  public void setNextTurnAction(Runnable action) {
    nextTurnButton.setOnAction(e -> action.run());
  }

  @Override
  public void update() {
    Platform.runLater(() -> {
      updateBoard();
      Player currentPlayer = boardGame.getCurrentPlayer();
      if (currentPlayer != null) {
        currentPlayerLabel.setText("Current Player: " + currentPlayer.getName());
        statusLabel.setText(currentPlayer.getName() + "'s turn to roll the dice!");
      }
      if (boardGame.isGameOver()) {
        Player winner = boardGame.getWinner();
        if (winner != null) {
          showWinner(winner);
        }
      }
    });
  }

  public void updateBoard() {
    Platform.runLater(() -> {
      for (Player player : playerTokens.keySet()) {
        updatePlayerPosition(player);
      }
    });
  }

  public void showWinner(Player winner) {
    Platform.runLater(() -> {
      currentPlayerLabel.setText("Winner: " + winner.getName() + "!");
      statusLabel.setText("🎉 " + winner.getName() + " has won the game! 🎉");
      statusLabel.getStyleClass().add("success-label");
      nextTurnButton.setDisable(true);
    });
  }
} 