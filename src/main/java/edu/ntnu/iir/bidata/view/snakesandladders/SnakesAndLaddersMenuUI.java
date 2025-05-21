package edu.ntnu.iir.bidata.view.snakesandladders;

import edu.ntnu.iir.bidata.controller.SnakesAndLaddersController;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriterGson;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.view.common.BoardManagementUI;
import edu.ntnu.iir.bidata.view.common.CommonButtons;
import edu.ntnu.iir.bidata.view.common.JavaFXBoardGameLauncher;
import edu.ntnu.iir.bidata.view.common.PlayerSelectionUI;
import edu.ntnu.iir.bidata.model.utils.DefaultGameMediator;
import edu.ntnu.iir.bidata.model.utils.GameMediator;
import edu.ntnu.iir.bidata.view.common.PlayerSelectionResult;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;

public class SnakesAndLaddersMenuUI {
  private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersMenuUI.class.getName());
  private final Stage primaryStage;
  private final Consumer<PlayerSelectionResult> onStartGame;
  private final BoardManagementUI boardManagementUI;

  /** -- GETTER -- Get the list of selected players */
  @Getter private List<String> selectedPlayers = new ArrayList<>();
  @Getter private Map<String, String> selectedPlayerTokens = new java.util.HashMap<>();

  private Label playerCountLabel;

  /**
   * Creates a new Snakes and Ladders Menu UI
   *
   * @param primaryStage The primary stage
   * @param onStartGame Consumer that accepts the list of selected players when starting the game
   */
  public SnakesAndLaddersMenuUI(Stage primaryStage, Consumer<PlayerSelectionResult> onStartGame) {
    this.primaryStage = primaryStage;
    this.onStartGame = onStartGame;
    this.boardManagementUI = new BoardManagementUI(primaryStage);
    setupMenu();
  }

  private void setupMenu() {
    primaryStage.setTitle("Snakes & Ladders");

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20));
    root.getStyleClass().add("snl-menu-root");

    // Create top bar with back button
    HBox topBar = new HBox(10);
    topBar.setPadding(new Insets(10));
    topBar.setAlignment(Pos.CENTER_LEFT);

    Button backButton = new Button("← Back to Main Menu");
    backButton.getStyleClass().add("snl-back-button");
    backButton.setOnAction(
        e -> {
          JavaFXBoardGameLauncher.getInstance().showMainMenu(primaryStage);
        });

    topBar.getChildren().add(backButton);
    root.setTop(topBar);

    VBox logoStack = createLogoStack();
    root.setLeft(logoStack);

    VBox centerBox = setUpCenterBox();
    root.setCenter(centerBox);

    createAndSetScene(root);
  }

  private VBox createLogoStack() {
    VBox logoStack = new VBox(8);
    logoStack.setPadding(new Insets(10, 20, 10, 10));
    logoStack.setAlignment(Pos.TOP_LEFT);
    Color[] greens = {
        Color.web("#006400"),
        Color.web("#008000"),
        Color.web("#00A000"),
        Color.web("#4caf50"),
        Color.web("#bdebc8")
    };
    int[] heights = {40, 30, 40, 20, 30, 20, 40, 30, 20, 40, 30};
    for (int i = 0; i < 11; i++) {
      Region r = new Region();
      r.setPrefSize((i % 3 == 0 ? 40 : (i % 3 == 1 ? 30 : 60)), heights[i]);
      r.setStyle(
          "-fx-background-radius: 15; -fx-background-color: "
              + toHexString(greens[i % greens.length])
              + ";");
      logoStack.getChildren().add(r);
    }
    return logoStack;
  }

  private VBox setUpCenterBox() {
    VBox centerBox = new VBox(30);
    centerBox.setAlignment(Pos.TOP_CENTER);
    centerBox.setPadding(new Insets(40, 0, 0, 0));

    StackPane titlePane = setUpCenterBoxTitleLabel();
    HBox boardButtons = setUpCenterBoxBrdBtns();
    Button choosePlayersBtn = setUpCenterBoxChoosePlayersBtn();
    playerCountLabel = new Label("No players selected");
    playerCountLabel.getStyleClass().add("snl-player-count-label");

    // Level buttons
    Button easyBtn = createMenuButton("Level: Easy");
    Button mediumBtn = createMenuButton("Level: Medium");
    Button hardBtn = createMenuButton("Level: Hard");

    easyBtn.setOnAction(e -> startGameWithLevel("easy", "/snakes_and_ladders_easy.jpg"));
    mediumBtn.setOnAction(e -> startGameWithLevel("medium", "/snakes_and_ladders_board.jpeg"));
    hardBtn.setOnAction(e -> startGameWithLevel("hard", "/snakes_and_ladders_hard_board.png"));

    HBox levelButtons = new HBox(20, easyBtn, mediumBtn, hardBtn);
    levelButtons.setAlignment(Pos.CENTER);

    centerBox.getChildren().addAll(levelButtons, playerCountLabel, titlePane, choosePlayersBtn, boardButtons);
    return centerBox;
  }

  private void createAndSetScene(BorderPane gameUI) {
    // SAFETY: Always ensure the root node is not already attached to another scene
    if (gameUI.getScene() != null) {
      // Detach from previous scene to avoid IllegalArgumentException
      gameUI.getScene().setRoot(new Pane());
    }
    Scene scene = new Scene(gameUI, 1200, 800);
    scene
        .getStylesheets()
        .addAll(
            getClass().getResource("/styles.css").toExternalForm(),
            getClass().getResource("/snakesandladders.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private String toHexString(Color color) {
    return String.format(
        "#%02X%02X%02X",
        (int) (color.getRed() * 255),
        (int) (color.getGreen() * 255),
        (int) (color.getBlue() * 255));
  }

  private static StackPane setUpCenterBoxTitleLabel() {
    StackPane titlePane = new StackPane();
    titlePane.setPrefSize(400, 60);
    titlePane.getStyleClass().add("snl-title-pane");
    Label titleLabel = new Label("SNAKES & LADDERS");
    titleLabel.getStyleClass().add("snl-title-label");
    titlePane.getChildren().add(titleLabel);
    return titlePane;
  }

  private HBox setUpCenterBoxBrdBtns() {
    HBox boardButtons = new HBox(30);
    boardButtons.setAlignment(Pos.CENTER);
    Button loadBoardBtn = createMenuButton("LOAD BOARD");
    loadBoardBtn.setOnAction(e -> showLoadBoardDialog());
    boardButtons.getChildren().add(loadBoardBtn);
    return boardButtons;
  }

  private Button setUpCenterBoxChoosePlayersBtn() {
    Button choosePlayersBtn = createMenuButton("Choose The Players");
    choosePlayersBtn.setOnAction(e -> openPlayerSelection());
    return choosePlayersBtn;
  }

  private Button createMenuButton(String text) {
    Button button = new Button(text);
    button.setPrefWidth(200);
    button.setPrefHeight(50);
    button.getStyleClass().add("snl-menu-button");
    return button;
  }

  private void showLoadBoardDialog() {
    Dialog<String> dialog = CommonButtons.setUpStringDialog(false);

    dialog
        .showAndWait()
        .ifPresent(
            gameName -> {
              if (!gameName.isEmpty()) {
                try {
                  BoardGame boardGame = readBoardGameFromSelectedFile(gameName);
                  SnakesAndLaddersGameUI gameUI = getSnakesAndLaddersGameUI(gameName, boardGame);
                  // Create and set the scene
                  createAndSetScene(gameUI.getRoot());
                } catch (Exception e) {
                  LOGGER.log(Level.SEVERE, "Error loading Snakes and Ladders game");
                }
              }
            });
  }

  private void openPlayerSelection() {
    PlayerSelectionUI playerSelection = new PlayerSelectionUI(primaryStage);
    List<String> players = playerSelection.showAndWait();
    Map<String, String> tokens = playerSelection.getPlayerTokenMap();
    if (players != null && !players.isEmpty()) {
      this.selectedPlayers = players;
      this.selectedPlayerTokens = tokens;
      updatePlayerCountLabel();
    }
  }

  private static BoardGame readBoardGameFromSelectedFile(String gameName) throws IOException {
    BoardGameFileReaderGson reader = new BoardGameFileReaderGson();
    BoardGame boardGame =
        reader.readBoardGame(Paths.get("src/main/resources/saved_games/snakesandladder", gameName + ".json"));
    return boardGame;
  }

  /**
   * Always creates a new SnakesAndLaddersGameUI instance for each new game/scene.
   * Never reuse a previous instance or its root node.
   */
  private SnakesAndLaddersGameUI getSnakesAndLaddersGameUI(String gameName, BoardGame boardGame) {
    // Create mediator
    GameMediator mediator = new DefaultGameMediator();
    // Create view and controller (always new instance)
    SnakesAndLaddersController controller = new SnakesAndLaddersController(
        boardGame,
        new BoardGameFileWriterGson(),
        new BoardGameFileReaderGson(),
        mediator
    );
    // Determine image path based on level
    String level = boardGame.getLevel();
    String imagePath = switch (level) {
      case "easy" -> "/snakes_and_ladders_easy.jpg";
      case "hard" -> "/snakes_and_ladders_hard_board.png";
      default -> "/snakes_and_ladders_board.jpeg";
    };
    SnakesAndLaddersGameUI gameUI =
        new SnakesAndLaddersGameUI(boardGame, primaryStage, controller, boardGame.getPlayers(), mediator, imagePath);
    gameUI.setLoadedGame(true, gameName);
    LOGGER.info(
        "Game loaded successfully"
            + boardGame.getCurrentPlayer().getName()
            + " "
            + boardGame.getCurrentPlayer().getCurrentPosition());
    // Register UI as observer
    boardGame.addObserver(gameUI);

    // Load game state and start
    controller.loadSnakesAndLadderGame(gameName);
    return gameUI;
  }

  private void updatePlayerCountLabel() {
    if (selectedPlayers.isEmpty()) {
      playerCountLabel.setText("No players selected");
    } else {
      playerCountLabel.setText(selectedPlayers.size() + " player(s) selected");
      playerCountLabel.getStyleClass().add("snl-player-count-label");
    }
  }

  private void startGameWithLevel(String level, String imagePath) {
    if (selectedPlayers.isEmpty()) {
      playerCountLabel.setText("Please select at least one player!");
      playerCountLabel.setStyle("-fx-text-fill: red;");
      return;
    }
    List<edu.ntnu.iir.bidata.model.player.Player> players = selectedPlayers.stream()
      .map(name -> new edu.ntnu.iir.bidata.model.player.Player(name, selectedPlayerTokens.get(name)))
      .toList();
    edu.ntnu.iir.bidata.model.tile.config.TileConfiguration config = new edu.ntnu.iir.bidata.model.tile.config.TileConfiguration(level);
    edu.ntnu.iir.bidata.model.board.Board board = edu.ntnu.iir.bidata.model.board.BoardFactory.createSnakesAndLaddersBoard(100, players, config);
    edu.ntnu.iir.bidata.model.BoardGame boardGame = new edu.ntnu.iir.bidata.model.BoardGame(board, 2);
    boardGame.setPlayers(players);
    boardGame.setLevel(level);
    edu.ntnu.iir.bidata.model.utils.GameMediator mediator = new edu.ntnu.iir.bidata.model.utils.DefaultGameMediator();
    edu.ntnu.iir.bidata.controller.SnakesAndLaddersController controller = new edu.ntnu.iir.bidata.controller.SnakesAndLaddersController(
      boardGame,
      new edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriterGson(),
      new edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson(),
      mediator
    );
    edu.ntnu.iir.bidata.view.snakesandladders.SnakesAndLaddersGameUI gameUI =
      new edu.ntnu.iir.bidata.view.snakesandladders.SnakesAndLaddersGameUI(boardGame, primaryStage, controller, players, mediator, imagePath);
    boardGame.addObserver(gameUI);
    controller.setPlayerNames(players.stream().map(edu.ntnu.iir.bidata.model.player.Player::getName).toList());
    controller.startGame();
    createAndSetScene(gameUI.getRoot());
  }
}