package edu.ntnu.iir.bidata.view.common;

import static java.util.logging.Level.INFO;
import edu.ntnu.iir.bidata.controller.BaseGameController;
import edu.ntnu.iir.bidata.view.monopoly.MonopolyGameUI;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class CommonButtons {
  private static final Logger LOGGER = Logger.getLogger(CommonButtons.class.getName());

  public static Button backToMainMenu(Stage primaryStage, boolean isMonopoly, BaseGameController controller) {
    Button backToMainMenu = new Button("Back to Main Menu");
    backToMainMenu.setOnAction(e ->     handleBackToMainMenuAction(primaryStage, isMonopoly, controller));

    return backToMainMenu;
  }

  private static void handleBackToMainMenuAction(Stage primaryStage, boolean isMonopoly, BaseGameController controller) {
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
              controller.saveGame(name, isMonopoly);
              JavaFXBoardGameLauncher.getInstance()
                  .showMainMenu(primaryStage);
            });
      } else if (result.get() == exitWithoutSaving) {
        JavaFXBoardGameLauncher.getInstance()
            .showMainMenu(primaryStage);
        LOGGER.log(INFO, "Exiting without saving");
      }
      // If cancel, do nothing
    }
  }
}
