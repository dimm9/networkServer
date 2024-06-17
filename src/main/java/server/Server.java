package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    List<ClientHandler> clientConnections = new ArrayList<>();

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);

    }

    public void listen(){
        System.out.println("Server started");
        while(true){
            try {
                Socket client  = serverSocket.accept();
                ClientHandler handler = new ClientHandler(client, this);
                clientConnections.add(handler);
                Thread thread = new Thread(handler);
                thread.start();
                System.out.println("Client connected");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void broadcast(String message, ClientHandler sender){
        synchronized (clientConnections){
            for(ClientHandler c : clientConnections){
                if(c != sender){
                    c.send(message);
                }
            }
        }
    }

    public void broadcastToUser(String message, String name){
        synchronized (clientConnections){
            for(ClientHandler c : clientConnections){
                if(c.getUsername().equals(name)){
                    c.send(message);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(2136);
            server.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ClientHandler getClientByUserName(String username){
        synchronized (clientConnections){
            for(ClientHandler c : clientConnections){
                if(c.getUsername().equals(username)){
                    return c;
                }
            }
        }
        return null;
    }

    public List<ClientHandler> getClientConnections() {
        return clientConnections;
    }
}
