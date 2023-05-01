package server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerApp {
    

    public static void main(String[] args) throws IOException {


        ServerSocket serverSocketText = new ServerSocket(50000);
        ServerSocket serverSocketVoice = new ServerSocket(50001);

        RoomServer roomServer = new RoomServer(serverSocketText,serverSocketVoice);
        roomServer.startServer();

        System.out.println("<<< SERVIDOR INICIADO >>>\n");
    }
}
