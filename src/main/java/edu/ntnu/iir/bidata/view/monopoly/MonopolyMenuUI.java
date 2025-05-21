package edu.ntnu.iir.bidata.view.monopoly;

import edu.ntnu.iir.bidata.controller.MonopolyController;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriterGson;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.utils.DefaultGameMediator;
import edu.ntnu.iir.bidata.model.utils.GameMediator;
import edu.ntnu.iir.bidata.view.common.CommonButtons;
import edu.ntnu.iir.bidata.view.common.JavaFXBoardGameLauncher;
import edu.ntnu.iir.bidata.view.common.PlayerSelectionUI;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;
import edu.ntnu.iir.bidata.Inject;
import edu.ntnu.iir.bidata.model.board.MonopolyBoardFactory;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;

/**
 * Represents the UI for the Monopoly game main menu.
 *
 * <p>This class sets up and manages the user interface for the Monopoly menu, allowing users to: -
 * Load a game board - Select players for the game - Start a new game
 *
 * <p>The class utilizes JavaFX components to create a responsive menu with a dynamic layout and
 * styled visual elements.
 */
public class MonopolyMenuUI {
  private static final Logger LOGGER = Logger.getLogger(MonopolyMenuUI.class.getName());
  private final Stage primaryStage;
  private final Consumer<PlayerSelectionResult> onStartGame;
  String monopolyPlayerCountLabelClass = "monopolyPlayerCountLabelClass";
  @Getter private List<String> selectedPlayers = new ArrayList<>();
  @Getter private Map<String, String> selectedPlayerTokens = new java.util.HashMap<>();
  private Label playerCountLabel;

  /**
   * Constructs a new MonopolyMenuUI instance for the Monopoly game application.
   *
   * <p>This constructor initializes the primary stage and a callback function to handle the start
   * game action. It also sets up the menu UI for the game.
   *
   * @param primaryStage the primary stage of the JavaFX application where the Monopoly menu UI will
   *     be displayed
   * @param onStartGame a callback function that accepts a list of selected players' names and
   *     triggers the start of the game
   */
  @Inject
  public MonopolyMenuUI(Stage primaryStage, Consumer<PlayerSelectionResult> onStartGame) {
    this.primaryStage = primaryStage;
    this.onStartGame = onStartGame;
    setupMenu();
  }

  /**
   * Initializes and configures the menu UI for the Monopoly game application.
   *
   * <p>This method sets up the main structure, style, and behavior for the Monopoly menu screen,
   * including the top navigation bar, logo display, central menu options, and action handlers for
   * various buttons. It also applies stylesheets and loads the UI scene onto the primary stage.
   *
   * <p>Key elements of the menu include: - A back button in the top bar to navigate to the main
   * menu. - A logo display in the left pane created using the {@code createLogoStack} method. - A
   * title pane with a stylized "MONOPOLY" label. - Buttons for loading a board, selecting players,
   * and starting the game. - A label to indicate the number of selected players.
   *
   * <p>Menu options and their action handlers: - "LOAD BOARD": Opens a dialog to load a game board,
   * implemented by the {@code showLoadBoardDialog} method. - "Choose The Players": Opens the player
   * selection UI, invoking {@code openPlayerSelection}. - "START": Validates selected players and
   * invokes the provided start game consumer if at least two players are selected.
   *
   * <p>The method ensures proper alignment, padding, and spacing for the UI components and styles
   * elements using the predefined CSS classes and stylesheets.
   *
   * <p>The UI is rendered within a {@code BorderPane} layout and displayed as a scene on the
   * primary stage.
   */
  private void setupMenu() {
    primaryStage.setTitle("Monopoly");

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20));
    root.getStyleClass().add("monopoly-menu-root");

    // Top bar with back button
    HBox topBar = new HBox(10);
    topBar.setPadding(new Insets(10));
    topBar.setAlignment(Pos.CENTER_LEFT);
    Button backButton = new Button("← Back to Main Menu");
    backButton.getStyleClass().add("monopoly-back-button");
    backButton.setOnAction(e -> JavaFXBoardGameLauncher.getInstance().showMainMenu(primaryStage));
    topBar.getChildren().add(backButton);
    root.setTop(topBar);

    VBox logoStack = createLogoStack();
    root.setLeft(logoStack);

    VBox centerBox = new VBox(30);
    centerBox.setAlignment(Pos.TOP_CENTER);
    centerBox.setPadding(new Insets(40, 0, 0, 0));

    StackPane titlePane = new StackPane();
    titlePane.setPrefSize(400, 60);
    titlePane.getStyleClass().add("monopoly-title-pane");
    Label titleLabel = new Label("MONOPOLY");
    titleLabel.getStyleClass().add("monopoly-title-label");
    titlePane.getChildren().add(titleLabel);
    centerBox.getChildren().add(titlePane);

    HBox boardButtons = new HBox(30);
    boardButtons.setAlignment(Pos.CENTER);
    Button loadBoardBtn = createMenuButton("LOAD BOARD");
    loadBoardBtn.setOnAction(e -> showLoadBoardDialog());
    boardButtons.getChildren().add(loadBoardBtn);
    centerBox.getChildren().add(boardButtons);

    Button choosePlayersBtn = createMenuButton("Choose The Players");
    choosePlayersBtn.setOnAction(e -> openPlayerSelection());
    centerBox.getChildren().add(choosePlayersBtn);

    playerCountLabel = new Label("No players selected");
    playerCountLabel.getStyleClass().add(monopolyPlayerCountLabelClass);
    centerBox.getChildren().add(playerCountLabel);

    Button startGameBtn = createMenuButton("Small board");
    startGameBtn.setOnAction(
        e -> {
          if (selectedPlayers.size() >= 2) {
            BoardGame boardGame = new BoardGame(MonopolyBoardFactory.createBoard(), 1);
            boardGame.setPlayers((List) selectedPlayers.stream().map(name -> new SimpleMonopolyPlayer(name, selectedPlayerTokens.get(name))).toList());
            MonopolyGameUI gameUI = getMonopolyGameUI(boardGame, true);
            createAndSetScene(gameUI);
          } else {
            playerCountLabel.setText("Please select at least two players!");
            playerCountLabel.getStyleClass().clear();
            playerCountLabel.getStyleClass().add("warning-label");
            playerCountLabel.getStyleClass().add(monopolyPlayerCountLabelClass);
          }
        });
    centerBox.getChildren().add(startGameBtn);

    // Medium board button
    Button mediumBoardBtn = createMenuButton("Medium board");
    mediumBoardBtn.setOnAction(
        e -> {
          if (selectedPlayers.size() >= 2) {
            BoardGame boardGame = new BoardGame(MonopolyBoardFactory.createBoard28(), 1);
            boardGame.setPlayers((List) selectedPlayers.stream().map(name -> new SimpleMonopolyPlayer(name, selectedPlayerTokens.get(name))).toList());
            MonopolyGameUI gameUI = getMonopolyGameUI(boardGame, true);
            createAndSetScene(gameUI);
          } else {
            playerCountLabel.setText("Please select at least two players!");
            playerCountLabel.getStyleClass().clear();
            playerCountLabel.getStyleClass().add("warning-label");
            playerCountLabel.getStyleClass().add(monopolyPlayerCountLabelClass);
          }
        });
    centerBox.getChildren().add(mediumBoardBtn);

    // Large board button
    Button largeBoardBtn = createMenuButton("Large board");
    largeBoardBtn.setOnAction(
        e -> {
          if (selectedPlayers.size() >= 2) {
            BoardGame boardGame = new BoardGame(MonopolyBoardFactory.createBoard32(), 1);
            boardGame.setPlayers((List) selectedPlayers.stream().map(name -> new SimpleMonopolyPlayer(name, selectedPlayerTokens.get(name))).toList());
            MonopolyGameUI gameUI = getMonopolyGameUI(boardGame, true);
            createAndSetScene(gameUI);
          } else {
            playerCountLabel.setText("Please select at least two players!");
            playerCountLabel.getStyleClass().clear();
            playerCountLabel.getStyleClass().add("warning-label");
            playerCountLabel.getStyleClass().add(monopolyPlayerCountLabelClass);
          }
        });
    centerBox.getChildren().add(largeBoardBtn);

    root.setCenter(centerBox);

    Scene scene = new Scene(root, 1200, 800);
    scene.getStylesheets().add(getClass().getResource("/monopoly.css").toExternalForm());
    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Creates and returns a VBox configured to display a stack of color-coded regions.
   *
   * <p>This method initializes a VBox container with predefined padding, alignment, and spacing
   * properties. It uses the {@code setUpLogoStackHeight} method to populate the VBox with a stack
   * of styled regions, each taking a color from a predefined array.
   *
   * @return a VBox containing a vertically-stacked arrangement of colored regions
   */
  private VBox createLogoStack() {
    VBox logoStack = new VBox(8);
    logoStack.setPadding(new Insets(10, 20, 10, 10));
    logoStack.setAlignment(Pos.TOP_LEFT);
    Color[] colors = {
        Color.web("#3b3b6d"),
        Color.web("#f7e6c7"),
        Color.web("#b39ddb"),
        Color.web("#e69a28"),
        Color.web("#c2c2fa")
    };
    setUpLogoStackHeight(colors, logoStack);
    return logoStack;
  }

  /**
   * Creates a stylized menu button with the given text.
   *
   * <p>This method instantiates a new Button with the provided text, applies the
   * "monopoly-menu-button" style class to the button, and returns it for further use.
   *
   * @param text the text to be displayed on the button
   * @return a Button instance with the specified text and applied style
   */
  private Button createMenuButton(String text) {
    Button btn = new Button(text);
    btn.getStyleClass().add("monopoly-menu-button");
    return btn;
  }

  /**
   * Displays a dialog to load a game board file and initializes the game UI if a valid file is
   * selected.
   *
   * <p>This method sets up a dialog for the user to input the name of a game board file. If the
   * user provides a non-empty input, the method attempts to load the corresponding board game by
   * calling {@code readBoardGameFromSelectedFile}. If the board game is successfully loaded, it
   * initializes the {@code MonopolyGameUI} for the game and sets the scene using {@code
   * createAndSetScene}. If an error occurs during file loading or UI initialization, the error is
   * logged.
   *
   * <p>The dialog interacts with the user through a {@link Dialog} instance, showing a prompt and
   * waiting for the user's response.
   *
   * <p>Exceptions such as IO errors or game loading issues are handled and logged within the
   * method.
   */
  private void showLoadBoardDialog() {
    Dialog<String> dialog = CommonButtons.setUpStringDialog(true);

    dialog
        .showAndWait()
        .ifPresent(
            gameName -> {
              if (!gameName.isEmpty()) {
                try {
                  BoardGame boardGame = readBoardGameFromSelectedFile(gameName);
                  MonopolyGameUI gameUI = getMonopolyGameUI(boardGame, false);
                  createAndSetScene(gameUI);
                } catch (Exception e) {
                  LOGGER.log(Level.SEVERE, "Error loading Monopoly game", e);
                }
              }
            });
  }

  /**
   * Opens the player selection UI and updates the selected players and UI elements accordingly.
   *
   * <p>This method initializes a {@code PlayerSelectionUI} instance, displays the player selection
   * dialog to the user, and retrieves the list of selected players. If any players are selected,
   * they are saved in the {@code selectedPlayers} field, and the text of {@code playerCountLabel}
   * is updated to reflect the number of players selected. If no players are selected, a default "No
   * players selected" message is displayed in the label.
   *
   * <p>Additionally, the {@code playerCountLabel} CSS style class is updated to include the {@code
   * monopolyPlayerCountLabelClass}.
   */
  private void openPlayerSelection() {
    PlayerSelectionUI playerSelection = new PlayerSelectionUI(primaryStage);
    List<String> playerNames = playerSelection.showAndWait();
    Map<String, String> tokens = playerSelection.getPlayerTokenMap();

    // Reset style classes first
    playerCountLabel.getStyleClass().clear();
    playerCountLabel.getStyleClass().add(monopolyPlayerCountLabelClass);

    if (playerNames != null && !playerNames.isEmpty()) {
      selectedPlayers = playerNames;
      selectedPlayerTokens = tokens;
      playerCountLabel.setText(selectedPlayers.size() + " players selected");
    } else {
      playerCountLabel.setText("No players selected");
    }
  }

  /**
   * Configures and populates the specified VBox with a stack of color-coded regions, each with
   * predefined heights and styles based on the provided colors array.
   *
   * @param colors an array of Color objects used to set the background colors of the regions
   * @param logoStack the VBox container to which the styled regions will be added
   */
  private void setUpLogoStackHeight(Color[] colors, VBox logoStack) {
    int[] heights = {40, 30, 40, 20, 30, 20, 40, 30, 20, 40, 30};
    for (int i = 0; i < 11; i++) {
      Region r = new Region();
      int operation = i % 3 == 1 ? 30 : 60;
      r.setPrefSize((i % 3 == 0 ? 40 : operation), heights[i]);
      r.setStyle(
          "-fx-background-radius: 15; -fx-background-color: "
              + toHexString(colors[i % colors.length])
              + ";");
      logoStack.getChildren().add(r);
    }
  }

  /**
   * Reads a board game configuration from a JSON file located in the Monopoly game's saved games
   * directory.
   *
   * <p>This method attempts to load a board game file by appending the provided game name with the
   * ".json" extension and locating it in the "src/main/resources/saved_games/monopoly" directory.
   * The file is then parsed to create and return a {@code BoardGame} instance.
   *
   * @param gameName the name of the board game file (without extension) to read and load
   * @return the {@code BoardGame} instance created from the loaded file
   * @throws IOException if an I/O error occurs while reading the board game file
   */
  private static BoardGame readBoardGameFromSelectedFile(String gameName) throws IOException {
    BoardGameFileReaderGson reader = new BoardGameFileReaderGson();
    return reader.readBoardGame(
        Paths.get("src/main/resources/saved_games/monopoly", gameName + ".json"));
  }

  /**
   * Initializes and returns the UI for the Monopoly game.
   *
   * <p>This method creates a new instance of {@code MonopolyGameUI}, sets up a controller for the
   * game, and registers the UI as an observer for the game state. It also logs essential
   * information about the current player and their position during initialization and starts the
   * game using the controller.
   *
   * @param boardGame the {@code BoardGame} instance representing the Monopoly game, containing the
   *     game's state and logic
   * @return a {@code MonopolyGameUI} instance that serves as the user interface for the Monopoly
   *     game
   */
  private MonopolyGameUI getMonopolyGameUI(BoardGame boardGame) {
    // Dependency injection wiring
    GameMediator mediator = new DefaultGameMediator();
    MonopolyController controller = new MonopolyController(
        boardGame,
        new BoardGameFileWriterGson(),
        new BoardGameFileReaderGson(),
        mediator
    );
    MonopolyGameUI gameUI = new MonopolyGameUI(boardGame, primaryStage, controller, mediator);
    LOGGER.info(
        "Game loaded successfully"
            + boardGame.getCurrentPlayer().getName()
            + " "
            + boardGame.getCurrentPlayer().getCurrentPosition());
    // Register UI as observer
    boardGame.addObserver(gameUI);

    // Load game state and start
    controller.startGame();
    return gameUI;
  }

  /**
   * Creates a new scene for the provided game UI and sets it on the primary stage.
   *
   * <p>This method creates a JavaFX {@code Scene} using the given {@code BorderPane} as the root
   * layout. It configures the scene with specific dimensions and applies the required stylesheets
   * to it. Finally, the scene is set to the application's primary stage, and the stage is
   * displayed.
   *
   * @param gameUI the {@code BorderPane} that serves as the root layout for the scene
   */
  private void createAndSetScene(MonopolyGameUI gameUI) {
    primaryStage.setScene(gameUI.getScene());
    primaryStage.show();
  }

  /**
   * @param boardGame    loaded or fresh game state
   * @param startNew     if true → controller.startGame(), else skip startGame()
   */
  private MonopolyGameUI getMonopolyGameUI(BoardGame boardGame, boolean startNew) {
    GameMediator mediator = new DefaultGameMediator();
    MonopolyController controller = new MonopolyController(
        boardGame,
        new BoardGameFileWriterGson(),
        new BoardGameFileReaderGson(),
        mediator
    );
    MonopolyGameUI gameUI = new MonopolyGameUI(boardGame, primaryStage, controller, mediator);
    boardGame.addObserver(gameUI);
    // Set player names in controller after loading
    List<String> playerNames = boardGame.getPlayers().stream().map(p -> p.getName()).toList();
    controller.setPlayerNames(playerNames);
    if (startNew) controller.startGame();
    return gameUI;
  }

  /**
   * Converts the given {@code Color} object to a hexadecimal string representation.
   *
   * <p>The returned string is in the format "#RRGGBB", where RR, GG, and BB are the hexadecimal
   * values of the red, green, and blue color components, scaled to a range of 0–255 and represented
   * as two-digit uppercase hexadecimal numbers.
   *
   * @param c the {@code Color} instance to be converted to a hexadecimal string; it should have
   *     red, green, and blue components in the range 0.0 to 1.0
   * @return a string representing the hexadecimal color code, such as "#FFFFFF" for white
   */
  private String toHexString(Color c) {
    return String.format(
        "#%02X%02X%02X",
        (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
  }
}