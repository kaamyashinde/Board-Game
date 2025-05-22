package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.Inject;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReader;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriter;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.exception.LowMoneyException;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import edu.ntnu.iir.bidata.model.utils.GameMediator;
import java.util.List;
import java.util.logging.Logger;

/**
 * The MonopolyController class manages the game logic for a Monopoly-like board game. It extends
 * the BaseGameController and handles player actions, dice rolls, property management, and other
 * events during the gameplay. It interacts with the game mediator, file readers/writers, and
 * oversees player states such as being in jail or awaiting specific actions.
 */
public class MonopolyController extends BaseGameController {
  private static final Logger LOGGER = Logger.getLogger(MonopolyController.class.getName());
  private final BoardGameFileWriter boardGameWriter;
  private final BoardGameFileReader boardGameReader;
  private final GameMediator mediator;
  private final boolean jailRolledSix = false;
  private boolean gameStarted = false;
  private boolean awaitingPlayerAction = false;
  private PropertyTile pendingPropertyTile = null;
  private boolean awaitingRentAction = false;
  private PropertyTile pendingRentPropertyTile = null;
  private boolean awaitingJailAction = false;
  private boolean diceRolled = false;

  /**
   * Constructs a MonopolyController which manages the game logic and interactions for the Monopoly
   * game. Dependencies are injected at runtime.
   *
   * @param boardGame the BoardGame instance representing the game board and its state
   * @param boardGameWriter the BoardGameFileWriter used for saving game state to a file
   * @param boardGameReader the BoardGameFileReader used for loading game state from a file
   * @param mediator the GameMediator facilitating communication between different game components
   */
  @Inject
  public MonopolyController(
      BoardGame boardGame,
      BoardGameFileWriter boardGameWriter,
      BoardGameFileReader boardGameReader,
      GameMediator mediator) {
    super(boardGame);
    this.boardGameWriter = boardGameWriter;
    this.boardGameReader = boardGameReader;
    this.mediator = mediator;
    LOGGER.info("MonopolyController initialized");
  }

  /**
   * Sets the names of the players for the game. This method overrides the method in the base class
   * to handle additional logic specific to the Monopoly game setup.
   *
   * @param playerNames a list of player names to be set
   */
  @Override
  public void setPlayerNames(List<String> playerNames) {
    super.setPlayerNames(playerNames);
    // Do not add players here; they are already added in the launcher
    LOGGER.info("Setting player names: " + playerNames);
  }

  /**
   * Handles the logic for a player's turn in the Monopoly game. This method takes into
   * consideration the player's current state (such as being in jail) and progresses the turn
   * accordingly. It rolls the dice, moves the player, and resolves the player's landing actions
   * based on the tile type and game rules.
   *
   * <p>The following key scenarios are handled:
   *
   * <ul>
   *   <li>If the player is in jail, it prompts for a jail-related action and ends the turn.
   *   <li>If the game has not started yet, this method initiates the first turn.
   *   <li>Ensures that the player's turn cannot proceed if awaiting prior pending actions (such as
   *       property purchase, rent payment, or jail actions).
   *   <li>Rolls the dice to determine the player's movement on the board and logs the move.
   *   <li>Handles landing on different tile types including unowned properties, properties owned by
   *       others (rent payments), or tiles with specific actions.
   *   <li>Prompts the player for additional actions if needed (e.g., deciding to purchase a
   *       property or paying rent).
   *   <li>Notifies game observers and communicates with the game mediator when required, such as
   *       when proceeding to the next player or awaiting player inputs.
   * </ul>
   */
  @Override
  public void handlePlayerMove() {
    SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) boardGame.getCurrentPlayer();
    if (currentPlayer.isInJail()) {
      awaitingJailAction = true;
      boardGame.notifyObservers();
      return;
    }
    if (!gameStarted) {
      gameStarted = true;
      LOGGER.info("First turn started");
    }
    if (awaitingPlayerAction || awaitingRentAction || awaitingJailAction) {
      LOGGER.warning("Still awaiting player action. Turn cannot proceed.");
      return;
    }
    // Roll the dice before moving
    boardGame.getDice().rollAllDice();
    int[] diceValues = boardGame.getCurrentDiceValues();
    int steps = java.util.Arrays.stream(diceValues).sum();
    // Move the player
    currentPlayer.move(steps);
    LOGGER.info(currentPlayer.getName() + " moved " + steps + " steps");
    // Handle the tile the player landed on
    Tile currentTile = currentPlayer.getCurrentTile();
    if (currentTile instanceof PropertyTile propertyTile) {
      if (propertyTile.getOwner() == null) {
        // Property is available for purchase
        LOGGER.info("Property at position " + propertyTile.getId() + " is available for purchase");
        awaitingPlayerAction = true;
        pendingPropertyTile = propertyTile;
        boardGame.notifyObservers();
        return;
      } else if (propertyTile.getOwner() != currentPlayer) {
        // Player needs to pay rent
        LOGGER.info(
            currentPlayer.getName()
                + " must pay rent for property at position "
                + propertyTile.getId());
        awaitingRentAction = true;
        pendingRentPropertyTile = propertyTile;
        boardGame.notifyObservers();
        return;
      }
    } else if (currentTile.getAction() != null) {
      currentTile.getAction().executeAction(currentPlayer, currentTile);
      // If the player is now in jail, end their turn immediately
      if (currentPlayer.isInJail()) {
        mediator.notify(this, "nextPlayer");
        return;
      }
    }
    // No action needed, move to next player
    mediator.notify(this, "nextPlayer");
  }

  /**
   * Determines if the current player in the game is in jail.
   *
   * @return true if the current player is in jail and is an instance of SimpleMonopolyPlayer, false
   *     otherwise.
   */
  public boolean isCurrentPlayerInJail() {
    return boardGame.getCurrentPlayer() instanceof SimpleMonopolyPlayer
        && ((SimpleMonopolyPlayer) boardGame.getCurrentPlayer()).isInJail();
  }

  /**
   * Handles the action of rolling dice for a player who is currently in jail. This method
   * determines whether the player rolls a six, which allows them to leave jail, and updates the
   * game state accordingly.
   *
   * <p>Key operations performed by this method include:
   *
   * <ul>
   *   <li>Retrieves the current player and checks their state.
   *   <li>Rolls all dice associated with the game.
   *   <li>Checks if the dice roll includes a six. If a six is rolled, the player is released from
   *       jail.
   *   <li>Updates the `awaitingJailAction` field to indicate the jail-related action has concluded.
   *   <li>Notifies the game mediator to proceed to the next player's turn after completing the
   *       current player's jail dice roll action.
   * </ul>
   *
   * <p>This method facilitates the rules of the game where a player has a chance to roll their way
   * out of jail, ensuring game progress and maintaining proper player state transitions.
   */
  public void handleJailRollDice() {
    SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) boardGame.getCurrentPlayer();
    boardGame.getDice().rollAllDice();
    int[] diceValues = boardGame.getCurrentDiceValues();
    boolean rolledSix = java.util.Arrays.stream(diceValues).anyMatch(value -> value == 6);
    if (rolledSix) {
      currentPlayer.setInJail(false);
    }
    awaitingJailAction = false;
    mediator.notify(this, "nextPlayer");
  }

  /**
   * Handles the payment process for a player to leave jail.
   *
   * <p>This method retrieves the current player from the game state and attempts to deduct a fixed
   * amount of money (50 units) as payment for leaving jail. If the payment is successful, the
   * player's "inJail" status is updated to false, allowing them to continue playing. If the player
   * does not have enough money, the operation is skipped without any changes to their state.
   *
   * <p>Additionally, this method updates the game state to indicate that no jail action is awaiting
   * for the current player and signals the game mediator to proceed to the next player's turn.
   *
   * <p>Key operations:
   * <li>Deducts money from the current player's balance for jail payment.
   * <li>Updates the player's "inJail" status upon successful payment.
   * <li>Handles insufficient funds scenario gracefully without stopping game flow.
   * <li>Notifies the game mediator to continue with the next player turn.
   */
  public void handleJailPay() {
    SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) boardGame.getCurrentPlayer();
    try {
      currentPlayer.payRent(50);
      currentPlayer.setInJail(false);
    } catch (Exception e) {
      // Not enough money, do nothing
    }
    awaitingJailAction = false;
    mediator.notify(this, "nextPlayer");
  }

  /**
   * Determines whether the game is currently awaiting an action from the player.
   *
   * @return true if the game is awaiting an action from the player, false otherwise.
   */
  public boolean isAwaitingPlayerAction() {
    return awaitingPlayerAction;
  }

  /**
   * Handles the logic for the current player to purchase the property they have landed on.
   *
   * <p>This method ensures that the game's conditions and state are appropriate for allowing the
   * property purchase. If the game is not awaiting a player action or there is no pending property,
   * the method exits without performing any actions.
   *
   * <p>Main operations performed by this method include:
   * <li>Retrieving the current player from the game controller.
   * <li>Initiating the purchase of the pending property by the current player.
   * <li>Updating the game state to reflect that no further player action is awaited.
   * <li>Progressing the game to the next player's turn.
   * <li>Notifying the game mediator that the game has transitioned to the next player.
   *
   *     <p>This method is called when a player chooses to purchase a property during their turn.
   */
  public void buyPropertyForCurrentPlayer() {
    if (!awaitingPlayerAction || pendingPropertyTile == null) {
      return;
    }
    SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) boardGame.getCurrentPlayer();
    buyProperty(currentPlayer, pendingPropertyTile);
    awaitingPlayerAction = false;
    pendingPropertyTile = null;
    nextPlayer();
    mediator.notify(this, "nextPlayer");
  }

  /**
   * Handles the process of purchasing a property for a specified player in the game. This method
   * attempts to complete the property purchase and logs relevant events including successful
   * purchases or situations where the player cannot afford the property.
   *
   * @param player the player attempting to purchase the property
   * @param property the property tile the player is attempting to buy
   */
  public void buyProperty(SimpleMonopolyPlayer player, PropertyTile property) {
    try {
      player.buyProperty(property);
      LOGGER.info(player.getName() + " bought property at position " + property.getId());
    } catch (LowMoneyException e) {
      LOGGER.warning(player.getName() + " cannot afford property at position " + property.getId());
      // Handle insufficient funds
    }
  }

  /**
   * Skips the action for the current player and progresses the game's turn to the next player.
   *
   * <p>This method is triggered when the current player opts to forgo their action or is not
   * allowed to take action due to game conditions. It ensures smooth transition to the next
   * player's turn by performing the following steps:
   * <li>Verifies if the game is currently awaiting the current player's action.
   * <li>Clears the game's record of pending actions and resets any associated state variables, such
   *     as `pendingPropertyTile`.
   * <li>Signals the game logic to move forward to the next player by invoking the `nextPlayer`
   *     method.
   * <li>Notifies the `mediator` with a "nextPlayer" event to propagate state changes to other
   *     components or observers.
   *
   *     <p>If the game is not awaiting the current player's action, this method performs no
   *     operations.
   */
  public void skipActionForCurrentPlayer() {
    if (!awaitingPlayerAction) {
      return;
    }
    awaitingPlayerAction = false;
    pendingPropertyTile = null;
    nextPlayer();
    mediator.notify(this, "nextPlayer");
  }

  public boolean isAwaitingRentAction() {
    return awaitingRentAction;
  }

  /**
   * Processes the payment of rent by the current player to the property owner, updates the game
   * state, and transitions to the next player's turn.
   *
   * <p>This method is responsible for handling the rent payment logic in the game. It first
   * verifies that the game is in the appropriate state to process a rent payment by checking if an
   * action for rent payment is pending and if a property tile associated with the rent exists. If
   * these conditions are met:
   * <li>Retrieves the current player from the game board.
   * <li>Executes the rent payment logic by calling the `payRent` method, which adjusts the player's
   *     balance and credits the property owner appropriately.
   * <li>Updates the game state by clearing the pending rent action and associated property tile.
   * <li>Determines the next player and signals the game to proceed by notifying the mediator.
   *
   *     <p>This method ensures that the game's state and flow are maintained properly during the
   *     rent payment process and transitions seamlessly to the next player's turn.
   *
   *     <p>Notes:
   * <li>If the game is not awaiting a rent action or if the pending rent property tile is null, the
   *     method exits without action.
   * <li>The mediator is informed to propagate changes in the game state to other game components or
   *     observers.
   */
  public void payRentForCurrentPlayer() {
    if (!awaitingRentAction || pendingRentPropertyTile == null) {
      return;
    }
    SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) boardGame.getCurrentPlayer();
    payRent(currentPlayer, pendingRentPropertyTile);
    awaitingRentAction = false;
    pendingRentPropertyTile = null;
    nextPlayer();
    mediator.notify(this, "nextPlayer");
  }

  /**
   * Handles the payment of rent from a player to the owner of a property in the context of a
   * Monopoly game. This method adjusts the player's balance based on the rent value of the property
   * and logs the transaction. If the player does not have sufficient funds to pay the rent, a
   * warning is logged.
   *
   * @param player the player who needs to pay the rent
   * @param property the property tile for which rent needs to be paid
   */
  public void payRent(SimpleMonopolyPlayer player, PropertyTile property) {
    try {
      player.payRent(property.getRent());
      LOGGER.info(player.getName() + " paid rent for property at position " + property.getId());
    } catch (LowMoneyException e) {
      LOGGER.warning(
          player.getName() + " cannot afford rent for property at position " + property.getId());
      // Handle insufficient funds
    }
  }

  /**
   * Retrieves the dice values from the most recent roll in the Monopoly game.
   *
   * @return an array of integers representing the values of the dice rolled during the last turn.
   */
  public int[] getLastDiceRolls() {
    return boardGame.getCurrentDiceValues();
  }

  /**
   * Rolls the dice without processing any game logic.
   * This method is used by the UI to get dice values for display before animation.
   */
  public void rollDice() {
    boardGame.getDice().rollAllDice();
    diceRolled = true;
    LOGGER.info("Dice rolled: " + java.util.Arrays.toString(boardGame.getCurrentDiceValues()));
  }

  /**
   * Gets the sum of the last dice roll.
   *
   * @return the sum of all dice values from the last roll
   */
  public int getLastDiceSum() {
    int[] values = boardGame.getCurrentDiceValues();
    return values != null ? java.util.Arrays.stream(values).sum() : 0;
  }
}


