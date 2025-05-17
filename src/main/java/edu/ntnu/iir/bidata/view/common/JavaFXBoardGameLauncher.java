package edu.ntnu.iir.bidata.view.common;

import edu.ntnu.iir.bidata.controller.LudoController;
import edu.ntnu.iir.bidata.controller.SnakesAndLaddersController;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.board.BoardFactory;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.view.ludo.LudoGameUI;
import edu.ntnu.iir.bidata.view.ludo.LudoMenuUI;
import edu.ntnu.iir.bidata.view.snakesandladders.SnakesAndLaddersGameUI;
import edu.ntnu.iir.bidata.view.snakesandladders.SnakesAndLaddersMenuUI;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson;
import edu.ntnu.iir.bidata.view.common.PlayerSelectionUI;
import edu.ntnu.iir.bidata.view.monopoly.MonopolyMenuUI;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.ArrayList;

/**
 * JavaFX application launcher for the board games UI. This class only handles UI navigation between
 * different screens, with no backend game logic implementation.
 */
public class JavaFXBoardGameLauncher extends Application {

  private static final Logger LOGGER = Logger.getLogger(JavaFXBoardGameLauncher.class.getName());
  private static JavaFXBoardGameLauncher instance;

  /**
   * Main method - entry point for the application.
   *
   * @param args Command line arguments (not used)
   */
  public static void main(String[] args) {
    LOGGER.info("Launching JavaFX application");
    launch(args);
  }

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
   * Displays the main menu UI.
   *
   * @param stage The primary stage to show the menu on
   */
  public void showMainMenu(Stage stage) {
    LOGGER.info("Showing main menu");
    MainMenuUI menuUI = new MainMenuUI(stage,
        gameType -> {
            switch (gameType) {
                case LUDO -> showLudoMenu(stage);
                case SNAKES_AND_LADDERS -> showSnakesAndLaddersMenu(stage);
                case MONOPOLY -> showMonopolyMenu(stage);
            }
        });
  }

  /**
   * Displays the Snakes and Ladders game menu. This screen allows player selection and
   * configuration.
   *
   * @param stage The primary stage to show the menu on
   */
  public void showSnakesAndLaddersMenu(Stage stage) {
    LOGGER.info("Showing Snakes and Ladders menu");
    SnakesAndLaddersMenuUI menuUI = new SnakesAndLaddersMenuUI(stage,
        selectedPlayerNames -> {
            List<Player> players = selectedPlayerNames.stream().map(Player::new).toList();
            showSnakesAndLaddersGameBoard(stage, players);
        });
  }

  /**
   * Displays the Ludo game menu. This screen allows player selection and configuration.
   *
   * @param stage The primary stage to show the menu on
   */
  public void showLudoMenu(Stage stage) {
    LOGGER.info("Showing Ludo menu");
    LudoMenuUI menuUI = new LudoMenuUI(stage,
        selectedPlayerNames -> {
            List<Player> players = selectedPlayerNames.stream().map(Player::new).toList();
            showLudoGameBoard(stage, players);
        });
  }

  /**
   * Displays the Monopoly game menu or board. For now, launches a default Monopoly game.
   *
   * @param stage The primary stage to show the menu on
   */
  public void showMonopolyMenu(Stage stage) {
    LOGGER.info("Showing Monopoly menu");
    MonopolyMenuUI menuUI = new edu.ntnu.iir.bidata.view.monopoly.MonopolyMenuUI(stage,
        selectedPlayerNames -> {
            try {
                Board board = edu.ntnu.iir.bidata.filehandling.board.MonopolyBoardFactory.createBoard();
                BoardGame boardGame = new BoardGame(board, 1);
                List<Player> players = new ArrayList<>();
                for (String name : selectedPlayerNames) {
                    players.add(new edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer(name));
                }
                boardGame.setPlayers(players);
                edu.ntnu.iir.bidata.view.monopoly.MonopolyGameUI gameUI = new edu.ntnu.iir.bidata.view.monopoly.MonopolyGameUI(boardGame, stage);
                stage.setScene(gameUI.getScene());
                stage.show();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error initializing Monopoly game", e);
            }
        });
  }

  /**
   * Displays the Snakes and Ladders game board UI with a loaded game.
   *
   * @param stage   The primary stage to show the game on
   * @param gameName The name of the loaded game
   */
  public void showSnakesAndLaddersGameBoardWithLoad(Stage stage, String gameName) {
    LOGGER.info("Loading Snakes and Ladders game: " + gameName);
    try {
      // Load the game state
      BoardGameFileReaderGson reader = new BoardGameFileReaderGson();
      Path savePath = Paths.get("src/main/resources/saved_games", gameName + ".json");
      BoardGame boardGame = reader.readBoardGame(savePath);
      List<Player> players = boardGame.getPlayers();

      // Create view
      SnakesAndLaddersGameUI gameUI = new SnakesAndLaddersGameUI(stage, players);
      gameUI.setLoadedGame(true, gameName);

      // Create controller and connect it with the view
      SnakesAndLaddersController controller = new SnakesAndLaddersController(boardGame);
      gameUI.setController(controller);

      // Load the game state into the controller
      controller.loadGame(gameName, gameUI);

      // Register the UI as an observer
      boardGame.addObserver(gameUI);

      // Start the game
      controller.startGame();
      LOGGER.info("Snakes and Ladders game loaded successfully");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error loading Snakes and Ladders game", e);
    }
  }

  /**
   * Displays the Snakes and Ladders game board UI.
   *
   * @param stage   The primary stage to show the game on
   * @param players The list of player names to use in the game
   */
  private void showSnakesAndLaddersGameBoard(Stage stage, List<Player> players) {
    LOGGER.info("Initializing Snakes and Ladders game with players: " + players);
    try {
      // Create view
      SnakesAndLaddersGameUI gameUI = new SnakesAndLaddersGameUI(stage, players);

      // Create model
      Board board = BoardFactory.createSnakesAndLaddersBoard(90, players);
      BoardGame boardGame = new BoardGame(board, 1);

      // Add players to the model
      for (Player player : players) {
        boardGame.addPlayer(player.getName());
      }

      // Register the UI as an observer
      boardGame.addObserver(gameUI);

      // Create controller and connect it with the view
      SnakesAndLaddersController controller = new SnakesAndLaddersController(boardGame);
      gameUI.setController(controller);

      // Start the game
      controller.startGame();
      LOGGER.info("Snakes and Ladders game started successfully");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error initializing Snakes and Ladders game", e);
    }
  }

  /**
   * Displays the Ludo game board UI.
   *
   * @param stage   The primary stage to show the game on
   * @param players The list of player names to use in the game
   */
  private void showLudoGameBoard(Stage stage, List<Player> players) {
    LOGGER.info("Initializing Ludo game with players: " + players);
    try {
      // Create view
      LudoGameUI gameUI = new LudoGameUI(stage, players);

      // Create model
      Board board = BoardFactory.createLudoBoard(players);
      BoardGame boardGame = new BoardGame(board, 1);

      // Add players to the model
      for (Player player : players) {
        boardGame.addPlayer(player.getName());
      }

      // Register the UI as an observer
      boardGame.addObserver(gameUI);

      // Create controller and connect it with the view
      LudoController controller = new LudoController(boardGame);
      gameUI.setController(controller);
      controller.setPlayerNames(players.stream().map(Player::getName).toList());

      // Start the game
      controller.startGame();
      LOGGER.info("Ludo game started successfully");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error initializing Ludo game", e);
    }
  }

  /**
   * Displays the Monopoly game board UI with a loaded game.
   *
   * @param stage   The primary stage to show the game on
   * @param gameName The name of the loaded game
   */
  public void showMonopolyGameBoardWithLoad(Stage stage, String gameName) {
    LOGGER.info("Loading Monopoly game: " + gameName);
    try {
      // Load the game state
      edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson reader = new edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson();
      java.nio.file.Path savePath = java.nio.file.Paths.get("src/main/resources/saved_games", gameName + ".json");
      edu.ntnu.iir.bidata.model.gamestate.MonopolyGameState gameState = reader.readMonopolyGameState(savePath);
      edu.ntnu.iir.bidata.model.BoardGame boardGame = gameState.toBoardGame();

      // Create view
      edu.ntnu.iir.bidata.view.monopoly.MonopolyGameUI gameUI = new edu.ntnu.iir.bidata.view.monopoly.MonopolyGameUI(boardGame, stage);
      stage.setScene(gameUI.getScene());
      stage.show();
      LOGGER.info("Monopoly game loaded successfully");
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error loading Monopoly game", e);
    }
  }
}