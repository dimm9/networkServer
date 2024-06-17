package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    private BufferedReader input;
    private String username;
    private ServerHandler handler;

    public Client() {
        input = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start(String host, int port) {
        try {
            handler = new ServerHandler(host, port);
            Thread thread = new Thread(handler);
            thread.start();
            System.out.println("Enter username: ");
            username = input.readLine();
            handler.send(username);
            String message;
            while ((message = input.readLine()) != null) {
                if (!message.equals("Bye")) {
                    handler.send(message);
                } else {
                    handler.send(message);
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start("localhost", 2136);
    }
}
