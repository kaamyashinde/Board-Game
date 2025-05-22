package edu.ntnu.iir.bidata.view.common;

import edu.ntnu.iir.bidata.controller.GameController;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Observer;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.File;
import java.util.Optional;

public abstract class JavaFXGameUI implements Observer {

  protected final BoardGame boardGame;
  protected final Stage primaryStage;
  protected final Map<Player, Circle> playerTokens;
  protected final Map<Integer, StackPane> tilePanes;
  protected final GridPane boardPane;
  protected final VBox playerInfoPane;
  protected final Label currentPlayerLabel;
  protected final Label diceRollLabel;
  protected final Label statusLabel;
  protected final Button nextTurnButton;
  protected final DiceView diceView;
  protected final VBox gameInfoPane;
  protected final Button pauseButton;
  protected final Button saveButton;
  protected boolean isPaused = false;
  protected GameController controller;

  public JavaFXGameUI(BoardGame boardGame, Stage primaryStage) {
    this.boardGame = boardGame;
    this.primaryStage = primaryStage;
    this.playerTokens = new HashMap<>();
    this.tilePanes = new HashMap<>();
    this.boardPane = new GridPane();
    this.playerInfoPane = new VBox(10);
    this.gameInfoPane = new VBox(10);
    this.nextTurnButton = new Button("Roll Dice");
    this.currentPlayerLabel = new Label();
    this.diceRollLabel = new Label();
    this.statusLabel = new Label();
    this.diceView = new DiceView();
    this.pauseButton = new Button("Pause");
    this.saveButton = new Button("Save Game");
    boardGame.addObserver(this);
    // Subclasses must call setupUI() in their constructor
  }

  /**
   * Template method for setting up the UI. Subclasses should call this in their constructor.
   */
  protected void setupUI() {
    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20));
    setupBoardPane();
    ScrollPane scrollPane = new ScrollPane(boardPane);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    root.setCenter(scrollPane);
    setupPlayerInfoPanel();
    root.setRight(playerInfoPane);
    setupGameInfoPanel();
    root.setTop(gameInfoPane);
    HBox controls = new HBox(20);
    controls.setPadding(new Insets(20));
    controls.setAlignment(Pos.CENTER);
    HBox gameControls = new HBox(10);
    gameControls.setAlignment(Pos.CENTER);
    gameControls.getChildren().addAll(pauseButton, saveButton);
    controls.getChildren().addAll(diceView, nextTurnButton, gameControls);
    root.setBottom(controls);
    Scene scene = new Scene(root, 800, 600);
    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    primaryStage.setScene(scene);
  }

  /**
   * Protected method for setting up the board pane. Subclasses can override for custom boards.
   */
  protected void setupBoardPane() {
    boardPane.setPadding(new Insets(20));
    boardPane.setHgap(2);
    boardPane.setVgap(2);
    boardPane.setAlignment(Pos.CENTER);
    // Default: no tiles. Subclasses should implement their own tile setup.
  }

  /**
   * Protected method for setting up the player info panel.
   */
  protected void setupPlayerInfoPanel() {
    playerInfoPane.setPadding(new Insets(20));
    playerInfoPane.getStyleClass().add("player-info-panel");
    Label title = new Label("Player Information");
    title.getStyleClass().add("title-label");
    currentPlayerLabel.getStyleClass().add("status-label");
    diceRollLabel.getStyleClass().add("label");
    playerInfoPane.getChildren().addAll(title, currentPlayerLabel, diceRollLabel);
  }

  /**
   * Protected method for setting up the game info panel.
   */
  protected void setupGameInfoPanel() {
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
      tilePanes.values().forEach(pane -> pane.getChildren().remove(token));
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

  public void setPauseAction(Runnable action) {
    pauseButton.setOnAction(e -> {
      isPaused = !isPaused;
      pauseButton.setText(isPaused ? "Resume" : "Pause");
      action.run();
    });
  }

  public void setSaveAction(Runnable action) {
    saveButton.setOnAction(e -> {
      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle("Save Game");
      dialog.setHeaderText("Enter a name for your saved game");
      dialog.setContentText("Game name:");

      Optional<String> result = dialog.showAndWait();
      result.ifPresent(gameName -> {
        action.run();
        statusLabel.setText("Game saved as: " + gameName);
      });
    });
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
      playerTokens.keySet().forEach(this::updatePlayerPosition);
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

  public void setController(GameController controller) {
    this.controller = controller;
    setupGameControls();
  }

  private void setupGameControls() {
    if (controller == null) return;

    pauseButton.setOnAction(e -> {
      if (isPaused) {
        controller.resumeGame();
        pauseButton.setText("Pause");
        statusLabel.setText("Game resumed");
      } else {
        controller.pauseGame();
        pauseButton.setText("Resume");
        statusLabel.setText("Game paused");
      }
      isPaused = !isPaused;
    });

    saveButton.setOnAction(e -> {
      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle("Save Game");
      dialog.setHeaderText("Enter a name for your saved game");
      dialog.setContentText("Game name:");

      Optional<String> result = dialog.showAndWait();
      result.ifPresent(gameName -> {
        try {
          //controller.saveGame(gameName);
          statusLabel.setText("Game saved as: " + gameName);
        } catch (Exception ex) {
          statusLabel.setText("Error saving game: " + ex.getMessage());
        }
      });
    });
  }

  /**
   * Abstract method for refreshing the UI from the board game state.
   */
  public abstract void refreshUIFromBoardGame();

  /**
   * Abstract method to get the main scene for this UI.
   */
  public abstract Scene getScene();

  protected VBox getPlayerInfoPane() {
    return playerInfoPane;
  }

  protected HBox getGameControls() {
    HBox gameControls = new HBox(10);
    gameControls.setAlignment(Pos.CENTER);
    gameControls.getChildren().addAll(pauseButton, saveButton);
    return gameControls;
  }

  protected BoardGame getBoardGame() {
    return boardGame;
  }

  protected Map<Integer, StackPane> getTilePanes() {
    return tilePanes;
  }
} 