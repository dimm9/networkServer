package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private Server server;
    private String username;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            username = input.readLine();  // Read the initial username sent by the client
            server.broadcast("---" + username + " connected---", this);
            String message;
            while ((message = input.readLine()) != null) {
                if (!message.equalsIgnoreCase("Bye")) {
                    sendToSelf("[Me]: " + message);
                    if(message.startsWith("/")){
                        if(message.equals("/online")){
                            List<ClientHandler> handlers = server.getClientConnections();
                            System.out.println("----Users online----");
                            for(ClientHandler h : handlers){
                                sendToSelf("|" + h.username + "|");
                            }
                        }
                        if(message.startsWith("/w")){
                            handlePrivateMessage(message);
                        }
                    }else{
                        server.broadcast("[" + username + "]: " + message, this);
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                input.close();
                output.close();
                clientSocket.close();
                server.broadcast("[" + username + " disconnected]", this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handlePrivateMessage(String message){
        String[] parts = message.split(" ", 3);
        if (parts.length < 3) {
            sendToSelf("Invalid private message format. Use /w recipient message");
            return;
        }
        String login = parts[1];
        String text = parts[2];
        ClientHandler recipient = server.getClientByUserName(login);
        if(recipient != null){
            sendToSelf("[Private to " + recipient.getUsername() + "]: " + text);
            server.broadcastToUser(("[Private from " + username + "]: " + text), login);
        }else{
            sendToSelf("User " + login + " is not online");
        }
    }

    public void send(String message) {
        if (output != null) {
            output.println(message);
        }
    }

    private void sendToSelf(String message) {
        if (output != null) {
            output.println(message);
        }
    }

    public String getUsername() {
        return username;
    }
}

