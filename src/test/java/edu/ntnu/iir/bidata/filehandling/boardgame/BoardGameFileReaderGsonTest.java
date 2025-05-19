package edu.ntnu.iir.bidata.filehandling.boardgame;

import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.board.MonopolyBoardFactory;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriterGson;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.dice.Dice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class BoardGameFileReaderGsonTest {

    private BoardGameFileReaderGson boardGameFileReaderGson;
    private BoardGameFileWriterGson boardGameFileWriterGson;
    private Path testFilePath;

    @BeforeEach
    public void setUp() {
        boardGameFileReaderGson = new BoardGameFileReaderGson();
        boardGameFileWriterGson = new BoardGameFileWriterGson();
        testFilePath = Paths.get("src/main/resources/saved_games/test_monopoly_game.json");
    }

    @Test
    public void testWriteMonopolyGame() {
        // Create a Monopoly board using the factory
        Board monopolyBoard = MonopolyBoardFactory.createBoard();

        // Create a Dice object
        Dice dice = new Dice(2); // Assuming 2 dice are used in the game

        // Construct the BoardGame
        BoardGame monopolyBoardGame = new BoardGame(monopolyBoard, dice);

        // Write the board game to a file
        assertDoesNotThrow(() -> boardGameFileWriterGson.writeBoardGame(monopolyBoardGame, testFilePath, true));

        // Read the board game back from the file
        BoardGame readBoardGame = assertDoesNotThrow(() -> boardGameFileReaderGson.readBoardGame(testFilePath));

        // Verify the board game read from the file is the same as the original
        assertEquals(monopolyBoardGame.getBoard().getSizeOfBoard(), readBoardGame.getBoard().getSizeOfBoard());
        assertEquals(monopolyBoardGame.getDice(), readBoardGame.getDice());
        assertEquals(monopolyBoardGame.getCurrentPlayerIndex(), readBoardGame.getCurrentPlayerIndex());
        assertEquals(monopolyBoardGame.getPlayers(), readBoardGame.getPlayers());
            }
} 