package edu.ntnu.iir.bidata.view.common;

import static java.util.logging.Level.INFO;

import edu.ntnu.iir.bidata.controller.BaseGameController;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

/**
 * CommonButtons is a utility class that provides commonly used JavaFX button components with
 * predefined functionality for board game applications.
 */
public class CommonButtons {

  private static final Logger LOGGER = Logger.getLogger(CommonButtons.class.getName());

  /**
   * Constructs a private instance of the CommonButtons class. The constructor is private to prevent
   * instantiation of this utility class, as it is intended to provide static methods for creating
   * and managing common buttons used in the application.
   */
  private CommonButtons() {}

  /**
   * Creates and returns a "Back to Main Menu" button. Clicking the button initiates navigation to
   * the main menu, optionally saving the current game if desired by the user.
   *
   * @param primaryStage the primary stage of the JavaFX application where the main menu will be
   *     displayed
   * @param isMonopoly a flag indicating whether the current game is Monopoly or another game
   * @param controller the controller instance managing the game's logic and state
   * @return a button configured to navigate back to the main menu
   */
  public static Button backToMainMenu(
      Stage primaryStage, boolean isMonopoly, BaseGameController controller) {
    Button backToMainMenu = new Button("Back to Main Menu");
    backToMainMenu.setOnAction(
        e -> handleBackToMainMenuAction(primaryStage, isMonopoly, controller));

    return backToMainMenu;
  }

  /**
   * Handles the "Back to Main Menu" action triggered by a user. This method displays a confirmation
   * dialog, allowing the user to save the game before exiting or exit without saving. Based on the
   * user's choice, the application navigates back to the main menu or performs no action if
   * cancelled.
   *
   * @param primaryStage the primary stage of the JavaFX application where the main menu is
   *     displayed
   * @param isMonopoly a flag indicating whether the current game is Monopoly or another game
   * @param controller the controller instance managing the game's logic and state
   */
  private static void handleBackToMainMenuAction(
      Stage primaryStage, boolean isMonopoly, BaseGameController controller) {
    LOGGER.info("Handling back to main menu action");
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Exit " + (isMonopoly ? "Monopoly" : "Snakes and Ladders"));
    alert.setHeaderText("Do you want to save your game before exiting?");
    ButtonType saveAndExit = new ButtonType("Save and Exit");
    ButtonType exitWithoutSaving = new ButtonType("Exit without Saving");
    ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(saveAndExit, exitWithoutSaving, cancel);
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent()) {
      if (result.get() == saveAndExit) {
        LOGGER.info("Attempting to save a game");
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Enter a name for your saved game:");
        Optional<String> saveName = dialog.showAndWait();
        saveName.ifPresent(
            name -> {
              saveGameHandling(isMonopoly, controller, saveName.get());
              JavaFXBoardGameLauncher.getInstance().showMainMenu(primaryStage);
            });
      } else if (result.get() == exitWithoutSaving) {
        JavaFXBoardGameLauncher.getInstance().showMainMenu(primaryStage);
        LOGGER.log(INFO, "Exiting without saving");
      }
    }
  }

  /**
   * Handles the functionality to save a game by invoking the saveGame method of the given
   * controller. This method delegates the actual saving process to the BaseGameController
   * implementation.
   *
   * @param isMonopoly a flag indicating whether the current game is Monopoly or another game
   * @param controller the controller instance responsible for managing game logic and state
   * @param gameName the name of the game to be saved
   */
  private static void saveGameHandling(
      boolean isMonopoly, BaseGameController controller, String gameName) {
    controller.saveGame(gameName, isMonopoly);
  }

  /**
   * Creates and returns a "Save Game" button. When clicked, the button initiates a save game action
   * by prompting the user to enter a name for the game and saving the game state using the provided
   * game controller.
   *
   * @param isMonopoly a flag indicating whether the current game is Monopoly or another game
   * @param controller the controller instance responsible for managing game logic and saving the
   *     game
   * @param actionLabel a label to display feedback messages, such as indicating whether the game
   *     was saved successfully
   * @return a button configured to handle game saving functionality
   */
  public static Button saveGameBtn(
      boolean isMonopoly, BaseGameController controller, Label actionLabel) {
    LOGGER.log(INFO, "Setting up the save game button");
    Button saveButton = new Button("Save Game");
    saveButton.setOnAction(e -> handleSaveBtnAction(isMonopoly, controller, actionLabel));
    return saveButton;
  }

  /**
   * Handles the action triggered by clicking the "Save Game" button. This method prompts the user
   * with a dialog to enter a name for their saved game, attempts to save the game using the
   * provided controller, and updates the action label with success or error messages based on the
   * outcome.
   *
   * @param isMonopoly a flag indicating whether the current game is Monopoly or another game
   * @param controller the controller instance responsible for managing game logic and performing
   *     the save operation
   * @param actionLabel a label used to display feedback messages, such as the success or error
   *     status of the save operation
   */
  private static void handleSaveBtnAction(
      boolean isMonopoly, BaseGameController controller, Label actionLabel) {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Save Game");
    dialog.setHeaderText("Enter a name for your saved game");
    dialog.setContentText("Game name:");

    Optional<String> result = dialog.showAndWait();
    result.ifPresent(
        gameName -> {
          try {
            LOGGER.info("Saving game as: " + gameName);
            saveGameHandling(isMonopoly, controller, gameName);
            actionLabel.setText("Game saved as: " + gameName);
          } catch (Exception ex) {
            actionLabel.setText("Error saving game: " + ex.getMessage());
          }
        });
  }
}
