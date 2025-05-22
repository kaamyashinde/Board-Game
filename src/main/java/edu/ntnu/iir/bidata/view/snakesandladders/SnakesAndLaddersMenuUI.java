package edu.ntnu.iir.bidata.view.snakesandladders;

import edu.ntnu.iir.bidata.controller.SnakesAndLaddersController;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriterGson;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.board.BoardFactory;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.config.TileConfiguration;
import edu.ntnu.iir.bidata.model.utils.DefaultGameMediator;
import edu.ntnu.iir.bidata.model.utils.GameMediator;
import edu.ntnu.iir.bidata.view.common.BoardManagementUI;
import edu.ntnu.iir.bidata.view.common.CommonButtons;
import edu.ntnu.iir.bidata.view.common.JavaFXBoardGameLauncher;
import edu.ntnu.iir.bidata.view.common.PlayerSelectionResult;
import edu.ntnu.iir.bidata.view.common.PlayerSelectionUI;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;

/**
 * The SnakesAndLaddersMenuUI class is responsible for initializing and managing the user interface
 * for the Snakes and Ladders game's main menu. It provides functionality for selecting players,
 * loading boards, and starting the game with different difficulty levels.
 *
 * <p>This class uses JavaFX components to set up the menu layout, which includes elements such as
 * navigation buttons, level selection buttons, and player selection dialogs. It also integrates
 * with other classes including `BoardManagementUI`, `GameMediator`, and
 * `SnakesAndLaddersController` to configure and start the game.
 *
 * <p>The class includes various helper methods to set up different parts of the menu, such as the
 * logo stack, title label, board buttons, and level buttons. Additionally, this class manages
 * player selections and game configurations for launching the game with appropriate settings.
 */
public class SnakesAndLaddersMenuUI {
  private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersMenuUI.class.getName());
  private final Stage primaryStage;
  private final Consumer<PlayerSelectionResult> onStartGame;
  private final BoardManagementUI boardManagementUI;

  /** -- GETTER -- Get the list of selected players. */
  @Getter private List<String> selectedPlayers = new ArrayList<>();

  @Getter private Map<String, String> selectedPlayerTokens = new java.util.HashMap<>();

  private Label playerCountLabel;

  /**
   * Creates a new Snakes and Ladders Menu UI.
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

  /**
   * Initializes and sets up the menu UI for the Snakes & Ladders game. Configures the primary
   * layout, including the top bar, logo, and central game options. This method creates and
   * organizes the UI components, assigns styles, and binds actions for navigation within the
   * application.
   *
   * <p>The following steps are performed: - Sets the window title to "Snakes & Ladders". -
   * Configures a root layout with padding and styling. - Creates a top navigation bar with a back
   * button to return to the main menu. - Adds a logo section to the left side of the menu layout. -
   * Sets up the central area of the menu containing game-related options. - Generates a scene from
   * the constructed layout and sets it on the primary stage.
   */
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

  /**
   * Creates a vertical stack of colored logo elements to be used as part of the user interface. The
   * stack consists of several rectangular regions with varying sizes and shades of green, arranged
   * vertically with spacing and padding.
   *
   * @return a VBox containing the arranged stack of logo elements.
   */
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
    java.util.stream.IntStream.range(0, 11)
        .forEach(
            i -> {
              Region r = new Region();
              r.setPrefSize((i % 3 == 0 ? 40 : (i % 3 == 1 ? 30 : 60)), heights[i]);
              r.setStyle(
                  "-fx-background-radius: 15; -fx-background-color: "
                      + toHexString(greens[i % greens.length])
                      + ";");
              logoStack.getChildren().add(r);
            });
    return logoStack;
  }

  /**
   * Sets up and configures the central VBox containing UI components for the Snakes & Ladders Menu.
   * The VBox is aligned to the top center and includes spacing, padding, and various child elements
   * for player selection, board buttons, and difficulty level options. The child elements are
   * composed of labeled buttons and styled components.
   *
   * @return a VBox containing the configured central UI layout for the
   */
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

    easyBtn.setOnAction(e -> startGameWithLevel("easy",
        "/snakeandladder_boardgame/snakes_and_ladders_easy.jpg"));
    mediumBtn.setOnAction(e -> startGameWithLevel("medium",
        "/snakeandladder_boardgame/snakes_and_ladders_board.jpeg"));
    hardBtn.setOnAction(e -> startGameWithLevel("hard",
        "/snakeandladder_boardgame/snakes_and_ladders_hard_board.png"));

    HBox levelButtons = new HBox(20, easyBtn, mediumBtn, hardBtn);
    levelButtons.setAlignment(Pos.CENTER);

    centerBox
        .getChildren()
        .addAll(playerCountLabel, titlePane, choosePlayersBtn, boardButtons, levelButtons);
    return centerBox;
  }

  /**
   * Creates a new scene using the provided game UI layout and applies the required CSS stylesheets.
   * The method ensures the provided {@code gameUI} is safely detached from any previous scene
   * before being assigned to the new scene. It then sets the newly created scene to the primary
   * stage and displays it.
   *
   * @param gameUI the {@code BorderPane} layout to be used as the root of the new scene
   */
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
            getClass().getResource("/style/styles.css").toExternalForm(),
            getClass().getResource("/style/snakesandladders.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Converts a given {@link Color} object to its hexadecimal string representation. The returned
   * string will be in the format "#RRGGBB", where RR, GG, and BB are the red, green, and blue color
   * components represented as two-digit hexadecimal values.
   *
   * @param color the {@link Color} object to be converted to a hexadecimal string
   * @return a hexadecimal string representation of the provided {@link Color} in the format
   *     "#RRGGBB"
   */
  private String toHexString(Color color) {
    return String.format(
        "#%02X%02X%02X",
        (int) (color.getRed() * 255),
        (int) (color.getGreen() * 255),
        (int) (color.getBlue() * 255));
  }

  /**
   * Configures and creates a stylized title component for the center section of the Snakes and
   * Ladders Menu UI. The method sets up a {@code StackPane} containing a centered title {@code
   * Label} with specific styling and dimensions.
   *
   * @return a {@code StackPane} containing the centered title label styled as "SNAKES & LADDERS"
   */
  private static StackPane setUpCenterBoxTitleLabel() {
    StackPane titlePane = new StackPane();
    titlePane.setPrefSize(400, 60);
    titlePane.getStyleClass().add("snl-title-pane");
    Label titleLabel = new Label("SNAKES & LADDERS");
    titleLabel.getStyleClass().add("snl-title-label");
    titlePane.getChildren().add(titleLabel);
    return titlePane;
  }

  /**
   * Sets up and configures an HBox containing a button for loading a game board. The HBox is
   * aligned to the center with spacing between components. The load board button is created with
   * specific dimensions and styling and is configured to trigger the board loading dialog when
   * clicked.
   *
   * @return an HBox containing the load board button.
   */
  private HBox setUpCenterBoxBrdBtns() {
    HBox boardButtons = new HBox(30);
    boardButtons.setAlignment(Pos.CENTER);
    Button loadBoardBtn = createMenuButton("LOAD BOARD");
    loadBoardBtn.setOnAction(e -> showLoadBoardDialog());
    boardButtons.getChildren().add(loadBoardBtn);
    return boardButtons;
  }

  /**
   * Configures and creates a button for selecting players in the Snakes and Ladders menu UI. The
   * button is labeled "Choose The Players" and styled using the existing menu button configuration.
   * When clicked, it triggers the player selection interface, allowing users to choose players for
   * the game.
   *
   * @return a {@code Button} styled and configured for initiating the player selection process.
   */
  private Button setUpCenterBoxChoosePlayersBtn() {
    Button choosePlayersBtn = createMenuButton("Choose The Players");
    choosePlayersBtn.setOnAction(e -> openPlayerSelection());
    return choosePlayersBtn;
  }

  /**
   * Creates a styled menu button with predefined dimensions and styles. The button is designed for
   * use in the Snakes and Ladders Menu UI.
   *
   * @param text the label text to display on the button
   * @return a {@code Button} instance styled and configured for the menu UI
   */
  private Button createMenuButton(String text) {
    Button button = new Button(text);
    button.setPrefWidth(200);
    button.setPrefHeight(50);
    button.getStyleClass().add("snl-menu-button");
    return button;
  }

  /**
   * Starts a new game of Snakes and Ladders with the specified level and board background image.
   * This method validates player selection, initializes the game configuration, creates the game
   * board, sets up the game UI, and begins the game session.
   *
   * @param level the difficulty level of the game, such as "easy" or "hard"
   * @param imagePath the file path to the image used as the board background
   */
  private void startGameWithLevel(String level, String imagePath) {
    if (selectedPlayers.isEmpty()) {
      playerCountLabel.setText("Please select at least one player!");
      playerCountLabel.setStyle("-fx-text-fill: red;");
      return;
    }
    List<Player> players =
        selectedPlayers.stream()
            .map(name -> new Player(name, selectedPlayerTokens.get(name)))
            .toList();
    LOGGER.info("Setting up the tile configuration with level: " + level);
    TileConfiguration config = new TileConfiguration(level);
    LOGGER.info("Starting game with level: " + level);
    int boardSize = level.equalsIgnoreCase("easy") ? 90 : 100;
    Board board = BoardFactory.createSnakesAndLaddersBoard(boardSize, players, config);
    BoardGame boardGame = new BoardGame(board, 2);
    boardGame.setPlayers(players);
    boardGame.setLevel(level);
    GameMediator mediator = new DefaultGameMediator();
    SnakesAndLaddersController controller =
        new SnakesAndLaddersController(
            boardGame,
            new BoardGameFileWriterGson(),
            new BoardGameFileReaderGson(),
            mediator,
            config);
    SnakesAndLaddersGameUI gameUI =
        new SnakesAndLaddersGameUI(
            boardGame, primaryStage, controller, players, mediator, imagePath);
    boardGame.addObserver(gameUI);
    controller.setPlayerNames(players.stream().map(Player::getName).toList());
    controller.startGame();
    createAndSetScene(gameUI.getRoot());
  }

  /**
   * Displays a dialog to load a board game for the Snakes and Ladders application.
   *
   * <p>The method creates and presents a dialog that allows users to input the name of the game
   * they wish to load. If a valid game name is provided, it attempts to read the associated game
   * board and initialize the corresponding User Interface (UI). The generated scene is then set up
   * and displayed on the primary application stage.
   *
   * <p>Behavior: - Displays a dialog for the user to input the name of the game to load. - If the
   * input is valid (not empty), reads the game board file associated with the provided name. -
   * Creates a new game UI instance associated with the loaded board data. - Sets up and displays
   * the game scene using the provided UI. - Logs an error if an exception occurs while loading the
   * game.
   *
   * <p>Notes: - If the user cancels or inputs an empty name, no further actions are performed. -
   * The game loading process is dependent on the `readBoardGameFromSelectedFile` and
   * `getSnakesAndLaddersGameUI` methods.
   */
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

  /**
   * Opens the player selection interface for the Snakes and Ladders game. This method creates and
   * displays a new instance of the {@code PlayerSelectionUI}, allowing users to choose players and
   * their associated tokens. Upon confirmation, the selected players and their tokens are stored in
   * the corresponding instance variables, and the player count label is updated accordingly.
   *
   * <p>Behavior: - Initializes a {@code PlayerSelectionUI} instance tied to the primary stage. -
   * Displays the player selection dialog and retrieves the list of selected players. - Retrieves
   * the mapping between players and their selected tokens. - If players are successfully selected:
   * - Updates the {@code selectedPlayers} and {@code selectedPlayerTokens} attributes. - Calls
   * {@code updatePlayerCountLabel()} to refresh the UI with the new player count.
   *
   * <p>Notes: - If no players are selected, the method exits without making changes. - The method
   * depends on the {@code PlayerSelectionUI} component for UI interactions.
   */
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

  /**
   * Reads a {@link BoardGame} object from a JSON file corresponding to the provided game name. The
   * JSON file is expected to be located in the directory
   * "src/main/resources/saved_games/snakesandladder".
   *
   * @param gameName the name of the game file to be read (without the ".json" extension)
   * @return the {@link BoardGame} object loaded from the specified file
   * @throws IOException if an error occurs while reading the file
   */
  private static BoardGame readBoardGameFromSelectedFile(String gameName) throws IOException {
    BoardGameFileReaderGson reader = new BoardGameFileReaderGson();
    BoardGame boardGame =
        reader.readBoardGame(
            Paths.get("src/main/resources/saved_games/snakesandladder", gameName + ".json"));
    return boardGame;
  }

  /**
   * Always creates a new SnakesAndLaddersGameUI instance for each new game/scene. Never reuse a
   * previous instance or its root node.
   */
  private SnakesAndLaddersGameUI getSnakesAndLaddersGameUI(String gameName, BoardGame boardGame) {
    // Create mediator
    GameMediator mediator = new DefaultGameMediator();
    // Create view and controller (always new instance)
    String level = boardGame.getLevel();
    TileConfiguration config = new TileConfiguration(level);
    SnakesAndLaddersController controller =
        new SnakesAndLaddersController(
            boardGame,
            new BoardGameFileWriterGson(),
            new BoardGameFileReaderGson(),
            mediator,
            config);
    // Determine image path based on level
    String imagePath =
        switch (level) {
          case "easy" -> "/snakeandladder_boardgame/snakes_and_ladders_easy.jpg";
          case "hard" -> "/snakeandladder_boardgame/snakes_and_ladders_hard_board.png";
          default -> "/snakeandladder_boardgame/snakes_and_ladders_board.jpeg";
        };
    SnakesAndLaddersGameUI gameUI =
        new SnakesAndLaddersGameUI(
            boardGame, primaryStage, controller, boardGame.getPlayers(), mediator, imagePath);
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

  /**
   * Updates the player count label in the Snakes and Ladders Menu UI.
   *
   * <p>This method checks the state of the selected players list and updates the label text to
   * reflect the current number of selected players. If no players are selected, the label displays
   * "No players selected". Otherwise, it displays the count of selected players followed by
   * "player(s) selected". The label is also styled with the CSS class "snl-player-count-label" when
   * players are selected.
   */
  private void updatePlayerCountLabel() {
    if (selectedPlayers.isEmpty()) {
      playerCountLabel.setText("No players selected");
    } else {
      playerCountLabel.setText(selectedPlayers.size() + " player(s) selected");
      playerCountLabel.getStyleClass().add("snl-player-count-label");
    }
  }
}
