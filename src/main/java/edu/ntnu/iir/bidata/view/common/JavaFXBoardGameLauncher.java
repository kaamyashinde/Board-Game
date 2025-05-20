package edu.ntnu.iir.bidata.view.common;

import edu.ntnu.iir.bidata.controller.SnakesAndLaddersController;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.board.BoardFactory;
import edu.ntnu.iir.bidata.model.board.MonopolyBoardFactory;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.view.snakesandladders.SnakesAndLaddersGameUI;
import edu.ntnu.iir.bidata.view.snakesandladders.SnakesAndLaddersMenuUI;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson;
import edu.ntnu.iir.bidata.view.monopoly.MonopolyGameUI;
import edu.ntnu.iir.bidata.view.monopoly.MonopolyMenuUI;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.scene.Scene;

/**
 * JavaFX application launcher for the board games UI. This class only handles UI navigation between
 * different screens, with no backend game logic implementation.
 */
public class JavaFXBoardGameLauncher extends Application {

  private static final Logger LOGGER = Logger.getLogger(JavaFXBoardGameLauncher.class.getName());
  private static JavaFXBoardGameLauncher instance;

  /**
   * Enum representing the different types of games available
   */
  private enum GameType {
    SNAKES_AND_LADDERS,
    MONOPOLY
  }

  /**
   * Start the application and show the main menu
   */
  @Override
  public void start(Stage primaryStage) {
    LOGGER.info("Starting JavaFX Board Game Launcher");
    instance = this;
    showMainMenu(primaryStage);
  }

  /**
   * Get the singleton instance of the launcher
   */
  public static JavaFXBoardGameLauncher getInstance() {
    return instance;
  }

  /**
   * Main method - entry point for the application.
   *
   * @param args Command line arguments (not used)
   */
  public static void main(String[] args) {
    LOGGER.info("Launching JavaFX application");
    launch(args);
  }

  /**
   * Displays the main menu UI.
   *
   * @param stage The primary stage to show the menu on
   */
  public void showMainMenu(Stage stage) {
    LOGGER.info("Showing main menu");
    MainMenuUI menuUI = new MainMenuUI(stage,
        gameType -> {
            switch (gameType) {
                case SNAKES_AND_LADDERS -> showSnakesAndLaddersMenu(stage);
                case MONOPOLY -> showMonopolyMenu(stage);
            }
        });
  }

  /**
   * Handles the player selection callback for game menus.
   * Converts selected player names to Player objects and shows the appropriate game board.
   *
   * @param stage The primary stage to show the game on
   * @param selectedPlayerNames List of selected player names
   * @param gameType The type of game to show (LUDO or SNAKES_AND_LADDERS)
   */
  private void handlePlayerSelection(Stage stage, List<String> selectedPlayerNames, GameType gameType) {
    switch (gameType) {
      case SNAKES_AND_LADDERS -> {
        // For Snakes and Ladders, use generic Player
        List<Player> players = selectedPlayerNames.stream().map(Player::new).toList();
        showSnakesAndLaddersGameBoard(stage, players);
      }
      case MONOPOLY -> {
        // For Monopoly, use SimpleMonopolyPlayer
        List<SimpleMonopolyPlayer> players = selectedPlayerNames.stream()
            .map(SimpleMonopolyPlayer::new)
            .toList();
        showMonopolyGameBoard(stage, players);
      }
    }
  }

  //TODO: Make this into a more generic method
  /**
   * Displays the Snakes and Ladders game menu. This screen allows player selection and
   * configuration.
   *
   * @param stage The primary stage to show the menu on
   */
  public void showSnakesAndLaddersMenu(Stage stage) {
    LOGGER.info("Showing Snakes and Ladders menu");
    SnakesAndLaddersMenuUI menuUI = new SnakesAndLaddersMenuUI(stage,
        selectedPlayerNames -> handlePlayerSelection(stage, selectedPlayerNames, GameType.SNAKES_AND_LADDERS));
  }

  /**
   * Displays the Monopoly game menu or board. For now, launches a default Monopoly game.
   *
   * @param stage The primary stage to show the menu on
   */
  public void showMonopolyMenu(Stage stage) {
    LOGGER.info("Showing Monopoly menu");
    MonopolyMenuUI menuUI = new MonopolyMenuUI(stage,
        selectedPlayerNames -> handlePlayerSelection(stage, selectedPlayerNames, GameType.MONOPOLY));
  }

  /**
   * Displays the Monopoly game board UI.
   *
   * @param stage   The primary stage to show the game on
   * @param players The list of player names to use in the game
   */
  private void showMonopolyGameBoard(Stage stage, List<SimpleMonopolyPlayer> players) {
    LOGGER.info("Initializing Monopoly game with players: " + players);
    try {
      Board board = MonopolyBoardFactory.createBoard();
        BoardGame boardGame = new BoardGame(board, 2);
      players.forEach(player -> boardGame.addPlayer(player.getName()));
      boardGame.setPlayers(new ArrayList<>(players)); // Cast to List<Player> if needed
      MonopolyGameUI monopolyGameUI = new MonopolyGameUI(boardGame, stage);
      stage.setScene(monopolyGameUI.getScene());
      stage.show();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error initializing Monopoly game", e);
    }
  }
  
  /**
   * Displays the Snakes and Ladders game board UI.
   * Optimized version that avoids adding players twice.
   *
   * @param stage   The primary stage to show the game on
   * @param players The list of players to use in the game
   */
  private void showSnakesAndLaddersGameBoard(Stage stage, List<Player> players) {
    LOGGER.info("Initializing Snakes and Ladders game with players: " + players);
    try {
      // Create view first
      SnakesAndLaddersGameUI gameUI = new SnakesAndLaddersGameUI(stage, players);

      // Create model
      Board board = BoardFactory.createSnakesAndLaddersBoard(100, players);
      BoardGame boardGame = new BoardGame(board, 2);

      // Set players directly instead of adding them one by one (avoids potential duplicates)
      boardGame.setPlayers(players);

      boardGame.addObserver(gameUI);

      SnakesAndLaddersController controller = new SnakesAndLaddersController(boardGame);
      gameUI.setController(controller);

      controller.setPlayerNames(players.stream().map(Player::getName).toList());

      controller.startGame();
      LOGGER.info("Snakes and Ladders game started successfully");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error initializing Snakes and Ladders game", e);
    }
  }

  /**
   * Displays the Snakes and Ladders game board UI with a loaded game state.
   *
   * @param stage The primary stage to show the game on
   * @param gameName The name of the saved game to load
   */
  public void showSnakesAndLaddersGameBoardWithLoad(Stage stage, String gameName) {
    LOGGER.info("Loading Snakes and Ladders game: " + gameName);
    try {
      // Create controller and load game
      BoardGameFileReaderGson reader = new BoardGameFileReaderGson();
      BoardGame boardGame = reader.readBoardGame(Paths.get("src/main/resources/saved_games", gameName + ".json"));
      List<Player> players = boardGame.getPlayers();
      
      // Create view and controller
      SnakesAndLaddersGameUI gameUI = new SnakesAndLaddersGameUI(stage, players);
      SnakesAndLaddersController controller = new SnakesAndLaddersController(boardGame);
      gameUI.setController(controller);
      gameUI.setBoardGame(boardGame);
      
      // Register UI as observer
      boardGame.addObserver(gameUI);
      
      // Load game state and start
      controller.loadSnakesAndLadderGame(gameName);
      controller.startGame();
      
      // Create and set the scene
      Scene scene = new Scene(gameUI.getRoot(), 1200, 800);
      scene.getStylesheets().addAll(
        getClass().getResource("/styles.css").toExternalForm(),
        getClass().getResource("/snakesandladders.css").toExternalForm()
      );
      stage.setScene(scene);
      stage.show();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error loading Snakes and Ladders game", e);
    }
  }
 }