package edu.ntnu.iir.bidata.filehandling.boardgame;

import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.board.MonopolyBoardFactory;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileWriterGson;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
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

    @Test
    public void testPlayerCurrentTileRestoration() {
        Board monopolyBoard = MonopolyBoardFactory.createBoard();
        Dice dice = new Dice(2);
        BoardGame boardGame = new BoardGame(monopolyBoard, dice);

        // Use SimpleMonopolyPlayer for Monopoly
        SimpleMonopolyPlayer alice = new SimpleMonopolyPlayer("Alice");
        SimpleMonopolyPlayer bob = new SimpleMonopolyPlayer("Bob");
        boardGame.setPlayers(List.of(alice, bob));
        alice.setCurrentTile(monopolyBoard.getTile(5));
        bob.setCurrentTile(monopolyBoard.getTile(10));

        // Write to file
        assertDoesNotThrow(() -> boardGameFileWriterGson.writeBoardGame(boardGame, testFilePath, true));

        // Read back
        BoardGame loaded = assertDoesNotThrow(() -> boardGameFileReaderGson.readBoardGame(testFilePath));

        // Check player positions
        assertEquals(5, loaded.getPlayers().get(0).getCurrentTile().getId());
        assertEquals(10, loaded.getPlayers().get(1).getCurrentTile().getId());
        assertSame(loaded.getBoard().getTile(5), loaded.getPlayers().get(0).getCurrentTile());
        assertSame(loaded.getBoard().getTile(10), loaded.getPlayers().get(1).getCurrentTile());
    }

    @Test
    public void testMonopolyPlayerSerializationAndDeserialization() {
        Board monopolyBoard = MonopolyBoardFactory.createBoard();
        Dice dice = new Dice(2);
        BoardGame boardGame = new BoardGame(monopolyBoard, dice);

        // Create players with properties and positions
        SimpleMonopolyPlayer alice = new SimpleMonopolyPlayer("Alice");
        SimpleMonopolyPlayer bob = new SimpleMonopolyPlayer("Bob");
        boardGame.setPlayers(List.of(alice, bob));
        ((SimpleMonopolyPlayer) boardGame.getPlayers().get(0)).setMoney(1200);
        ((SimpleMonopolyPlayer) boardGame.getPlayers().get(1)).setMoney(900);
        boardGame.getPlayers().get(0).setCurrentTile(monopolyBoard.getTile(7));
        boardGame.getPlayers().get(1).setCurrentTile(monopolyBoard.getTile(12));
    
        

        // Write to file
        assertDoesNotThrow(() -> boardGameFileWriterGson.writeBoardGame(boardGame, testFilePath, true));

        // Read back
        BoardGame loaded = assertDoesNotThrow(() -> boardGameFileReaderGson.readBoardGame(testFilePath));

        // Check player count
        assertEquals(2, loaded.getPlayers().size());

        // Check Alice
        Player loadedAlice = loaded.getPlayers().stream().filter(p -> p.getName().equals("Alice")).findFirst().orElse(null);
        assertNotNull(loadedAlice);
        assertTrue(loadedAlice instanceof SimpleMonopolyPlayer);
        SimpleMonopolyPlayer loadedAliceMonopoly = (SimpleMonopolyPlayer) loadedAlice;
        assertEquals(1200, loadedAliceMonopoly.getMoney());
        assertEquals(7, loadedAliceMonopoly.getCurrentTile().getId());

        // Check Bob
        Player loadedBob = loaded.getPlayers().stream().filter(p -> p.getName().equals("Bob")).findFirst().orElse(null);
        assertNotNull(loadedBob);
        assertTrue(loadedBob instanceof SimpleMonopolyPlayer);
        SimpleMonopolyPlayer loadedBobMonopoly = (SimpleMonopolyPlayer) loadedBob;
        assertEquals(900, loadedBobMonopoly.getMoney());
        assertEquals(12, loadedBobMonopoly.getCurrentTile().getId());
    }
} 