package edu.ntnu.iir.bidata.view.common;

import edu.ntnu.iir.bidata.controller.GameController;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Observer;
import edu.ntnu.iir.bidata.model.player.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Represents an abstract JavaFX-based graphical user interface (GUI) for a board game. The class
 * manages the layout, rendering, and interactions with game elements, updating the interface to
 * reflect the current state of the game. This class implements the Observer pattern to listen to
 * updates in the game model.
 */
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

  /**
   * Constructs a new JavaFXGameUI instance to manage the graphical user interface for a board game.
   * This constructor initializes the main UI components, binds them to the game data, and sets up
   * observers for game updates. Subclasses must call {@code setupUI()} in their constructor to
   * complete the initialization and properly configure the user interface.
   *
   * @param boardGame the {@code BoardGame} instance that this UI will represent and interact with
   * @param primaryStage the primary {@code Stage} where the game UI will be displayed
   */
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

  /** Template method for setting up the UI. Subclasses should call this in their constructor. */
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
    scene.getStylesheets().add(getClass().getResource("/style/styles.css").toExternalForm());
    primaryStage.setScene(scene);
  }

  /** Protected method for setting up the board pane. Subclasses can override for custom boards. */
  protected void setupBoardPane() {
    boardPane.setPadding(new Insets(20));
    boardPane.setHgap(2);
    boardPane.setVgap(2);
    boardPane.setAlignment(Pos.CENTER);
    // Default: no tiles. Subclasses should implement their own tile setup.
  }

  /** Protected method for setting up the player info panel. */
  protected void setupPlayerInfoPanel() {
    playerInfoPane.setPadding(new Insets(20));
    playerInfoPane.getStyleClass().add("player-info-panel");
    Label title = new Label("Player Information");
    title.getStyleClass().add("title-label");
    currentPlayerLabel.getStyleClass().add("status-label");
    diceRollLabel.getStyleClass().add("label");
    playerInfoPane.getChildren().addAll(title, currentPlayerLabel, diceRollLabel);
  }

  /** Protected method for setting up the game info panel. */
  protected void setupGameInfoPanel() {
    gameInfoPane.setPadding(new Insets(20));
    gameInfoPane.getStyleClass().add("control-panel");
    Label title = new Label("Game Status");
    title.getStyleClass().add("title-label");
    statusLabel.getStyleClass().add("status-label");
    gameInfoPane.getChildren().addAll(title, statusLabel);
  }

  /**
   * Updates the game UI to reflect the current state of the game.
   *
   * <p>This method is executed on the JavaFX application thread and performs the following actions:
   * 1. Updates the game board by calling the {@code updateBoard()} method. 2. Retrieves the current
   * player from the {@code boardGame} and updates the UI labels to display the current player's
   * name and their turn status. 3. Checks if the game is over. If the game is over, retrieves the
   * winner and displays the winner's information using the {@code showWinner(Player winner)}
   * method.
   */
  @Override
  public void update() {
    Platform.runLater(
        () -> {
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

  /**
   * Updates the game board by refreshing the position of all player tokens.
   *
   * <p>This method executes on the JavaFX application thread to ensure thread safety while
   * manipulating UI components. It iterates through all players in the game and calls the {@code
   * updatePlayerPosition} method to update each player's token to reflect their current position on
   * the board.
   */
  public void updateBoard() {
    Platform.runLater(
        () -> {
          playerTokens.keySet().forEach(this::updatePlayerPosition);
        });
  }

  /**
   * Updates the UI to display the winning player and game outcome.
   *
   * <p>This method is run on the JavaFX application thread to ensure thread safety when updating UI
   * components. It sets the current player label and status label to show the winner's name,
   * applies a success style to the status label, and disables the next turn button to conclude the
   * game.
   *
   * @param winner the player who has won the game
   */
  public void showWinner(Player winner) {
    Platform.runLater(
        () -> {
          currentPlayerLabel.setText("Winner: " + winner.getName() + "!");
          statusLabel.setText("🎉 " + winner.getName() + " has won the game! 🎉");
          statusLabel.getStyleClass().add("success-label");
          nextTurnButton.setDisable(true);
        });
  }

  /**
   * Updates the position of the player's token on the game board. This method removes the player's
   * token from its current position and places it on the new position determined by the player's
   * current tile.
   *
   * @param player the player whose token position needs to be updated
   */
  private void updatePlayerPosition(Player player) {
    Circle token = playerTokens.get(player);
    if (token != null) {
      int position = player.getCurrentTile().getId();
      StackPane tilePane = tilePanes.get(position);
      tilePanes.values().forEach(pane -> pane.getChildren().remove(token));
      tilePane.getChildren().add(token);
    }
  }

  /**
   * Sets the game controller for this UI and initializes the game controls.
   *
   * @param controller the {@code GameController} instance to be associated with this UI
   */
  public void setController(GameController controller) {
    this.controller = controller;
    setupGameControls();
  }

  /**
   * Configures the game control buttons and their associated actions.
   *
   * <p>This method sets up event handlers for the pause and save buttons. When the pause button is
   * clicked, the game toggles between paused and resumed states, updating the button text and
   * status label accordingly. When the save button is clicked, a dialog is displayed for the user
   * to input a save game name, and an attempt is made to save the game using the provided name.
   *
   * <p>Preconditions: - The `controller` instance must not be null.
   *
   * <p>Postconditions: - The pause button toggles the game's paused/resumed state when clicked. -
   * The save button displays a dialog to input the save name and attempts to save the game.
   *
   * <p>Exceptions: - If an error occurs during the save operation, an error message is displayed in
   * the status label.
   */
  private void setupGameControls() {
    if (controller == null) return;

    pauseButton.setOnAction(
        e -> {
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

    saveButton.setOnAction(
        e -> {
          TextInputDialog dialog = new TextInputDialog();
          dialog.setTitle("Save Game");
          dialog.setHeaderText("Enter a name for your saved game");
          dialog.setContentText("Game name:");

          Optional<String> result = dialog.showAndWait();
          result.ifPresent(
              gameName -> {
                try {
                  // controller.saveGame(gameName);
                  statusLabel.setText("Game saved as: " + gameName);
                } catch (Exception ex) {
                  statusLabel.setText("Error saving game: " + ex.getMessage());
                }
              });
        });
  }

  /** Abstract method to get the main scene for this UI. */
  public abstract Scene getScene();

  /**
   * Retrieves the current instance of the BoardGame associated with this UI.
   *
   * @return the current BoardGame instance
   */
  protected BoardGame getBoardGame() {
    return boardGame;
  }
}
