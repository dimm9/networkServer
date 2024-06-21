package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    ClientHandler handler;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void listen() {
        System.out.println("Server started");
        while (true) {
            try {
                Socket client = serverSocket.accept();
                System.out.println("Client connected");
                handler = new ClientHandler(client);
                Thread clientThread = new Thread(handler);
                clientThread.start();
                clientThread.join();  // Wait for the current client to disconnect
                System.out.println("Client disconnected");
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(2145);
            server.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
