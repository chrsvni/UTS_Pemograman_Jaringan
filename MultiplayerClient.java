import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;

public class MultiplayerClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 12345);
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            // Enter player name
            System.out.print("Enter your name: ");
            String playerName = input.readLine();
            output.println(playerName);

            // Display common question
            String question = serverInput.readLine();
            System.out.println(question);

            while (true) {
                System.out.print("Enter your guess (1-100): ");
                String guess = input.readLine();

                output.println(guess);

                String response = serverInput.readLine();
                System.out.println(response);

                if (response.contains("Correct")) {
                    // Extract and display points from the response
                    String[] parts = response.split(" ");
                    System.out.println("Your points: " + parts[parts.length - 1]);
                    break;
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
