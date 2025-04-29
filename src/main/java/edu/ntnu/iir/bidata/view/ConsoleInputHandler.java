package edu.ntnu.iir.bidata.view;

import java.util.Scanner;

/**
 * Handles console input for the game.
 */
public class ConsoleInputHandler {
    private final Scanner scanner;

    public ConsoleInputHandler() {
        this.scanner = new Scanner(System.in);
    }

    public int getNumberOfPlayers() {
        System.out.print("Enter number of players (2-4): ");
        int numPlayers;
        while (true) {
            try {
                numPlayers = Integer.parseInt(scanner.nextLine());
                if (numPlayers >= 2 && numPlayers <= 4) {
                    break;
                }
                System.out.print("Please enter a number between 2 and 4: ");
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
        return numPlayers;
    }

    public String getPlayerName(int playerNumber) {
        System.out.print("Enter name for Player " + playerNumber + ": ");
        return scanner.nextLine();
    }

    public void close() {
        scanner.close();
    }
} 