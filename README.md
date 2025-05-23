# Board Game JavaFX Application

This project is a JavaFX-based application for playing board games, developed as part of the IDATT2003 course. The application demonstrates the use of object-oriented design principles and best practices, and was developed collaboratively by two students using pair programming. The project supports both a classic Snakes and Ladders game and a simplified version of Monopoly, each with multiple board configurations.

## Table of Contents

- [Features](#features)
- [Game Modes](#game-modes)
- [How to Run](#how-to-run)
- [Usage](#usage)
- [Saving and Loading](#saving-and-loading)
- [Player Management](#player-management)
- [Project Structure](#project-structure)
- [Contributors](#contributors)
- [Future Improvements](#future-improvements)

---

## Features

- **JavaFX GUI**: Modern, user-friendly graphical interface for both games.
- **Multiple Game Types**: Play Snakes and Ladders or Monopoly, each with three board sizes/configurations.
- **Player Management**: Load players from a CSV file or add them manually, with support for custom tokens.
- **Game Saving/Loading**: Save your game progress and load it later from the main menu.
- **Board Management**: Remove or manage custom boards via the UI.
- **Command-Line Interface**: (Optional) Run the game in CLI mode for quick testing or demonstration.
- **Extensible Design**: Easily add new board games or configurations by extending the model and view classes.

---

## Game Modes

- **Snakes and Ladders**: Classic gameplay with three difficulty levels (Easy, Medium, Hard), each with a unique board layout.
- **Monopoly (Simplified)**: A streamlined version of Monopoly with property buying, rent, jail, and three board sizes (Small, Medium, Large).

---

## How to Run

### Prerequisites

- Java 21 or higher (LTS)
- Maven

### Running the Application

1. **Clone the repository** and navigate to the project root.
2. **Run the JavaFX application** using Maven:

   ```sh
   mvn javafx:run
   ```

   This will launch the main menu, where you can select a game and start playing.

---

## Usage

1. **Select a Game**: Choose either "Snakes & Ladders" or "Monopoly" from the main menu.
2. **Choose Players**: Load players from a CSV file or add them manually. Assign unique tokens to each player.
3. **Select Board/Level**: For Snakes and Ladders, pick a difficulty. For Monopoly, pick a board size.
4. **Play**: Take turns rolling dice, moving tokens, and interacting with board tiles.
5. **Save/Load**: Use the in-game menu to save your progress or load a previous game.

---

## Saving and Loading

- **Save Game**: Click the "Save Game" button, enter a name, and your game will be saved in the `src/main/resources/saved_games/` directory.
- **Load Game**: From the game menu, click "LOAD BOARD" and select a previously saved game to resume.

Game state is stored in JSON format, including player positions, money (for Monopoly), and board state.

---

## Player Management

- **Load from CSV**: Use the "Load Available Players from CSV" button to import players from `src/main/resources/saved_players/saved_players.csv`.
- **Add Manually**: Add new players and assign them a token.
- **Token Selection**: Each player can have a unique token image, chosen from the available set.

Example CSV format:
```
Anna, token_red.png
Tom, token_blue.png
Katrina, token_green.png
Kareena, token_purple.png
Dave, token_yellow.png
```

---

## Project Structure

- `src/main/java/edu/ntnu/iir/bidata/model/`: Game logic and data models.
- `src/main/java/edu/ntnu/iir/bidata/view/`: JavaFX UI for each game and common components.
- `src/main/java/edu/ntnu/iir/bidata/controller/`: Controllers for game logic and UI interaction.
- `src/main/java/edu/ntnu/iir/bidata/filehandling/`: File I/O for saving/loading games and players.
- `src/main/resources/`: Game assets, saved games, player CSVs, and stylesheets.

---

## Further Implementations

Ludo Game (creation is in progress)

---

## Contributors

- **Durva Parmar**
- **Kaamya Shinde**

---
