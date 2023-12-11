import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Random;

public class MultiplayerServer {
    private static final int MAX_PLAYERS = 3;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server listening on port 12345");

            for (int i = 0; i < MAX_PLAYERS; i++) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Player " + (i + 1) + " connected: " + clientSocket);

                // Generate a random number for the client to guess
                Random random = new Random();
                int randomNumber = random.nextInt(100) + 1;
                System.out.println("Random number to guess: " + randomNumber);

                Thread playerThread = new Thread(new PlayerHandler(clientSocket, randomNumber));
                playerThread.start();
            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class PlayerHandler implements Runnable {
    private Socket clientSocket;
    private int randomNumber;
    private int points;
    private String playerName;

    public PlayerHandler(Socket clientSocket, int randomNumber) {
        this.clientSocket = clientSocket;
        this.randomNumber = randomNumber;
        this.points = 0;
        this.playerName = null;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

            // Receive and display player name
            playerName = input.readLine();
            System.out.println("Player " + playerName + " connected: " + clientSocket);

            // Ask a common question
            output.println("Welcome, " + playerName + "! Guess the number between 1 and 100.");

            while (true) {
                String clientGuess = input.readLine();
                if (clientGuess == null) {
                    break;
                }

                int guess = Integer.parseInt(clientGuess);
                System.out.println("Received guess from Player " + playerName + ": " + guess);

                if (guess == randomNumber) {
                    points++;
                    output.println("Correct! You guessed the right number. Your points: " + points);
                    break;
                } else {
                    String hint = provideHint(guess, randomNumber);
                    output.println(hint + " Your points: " + points);
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String provideHint(int guess, int randomNumber) {
        int difference = Math.abs(guess - randomNumber);

        if (difference <= 10) {
            return "You're getting close! Keep going.";
        } else if (guess < randomNumber) {
            return "Higher! Try again.";
        } else {
            return "Lower! Try again.";
        }
    }
}
