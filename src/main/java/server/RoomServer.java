package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RoomServer {
    private ServerSocket serverSocket;

    public RoomServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado.");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(50000);
        RoomServer roomServer = new RoomServer(serverSocket);
        roomServer.startServer();
    }
}

