package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RoomServer {
    private ServerSocket serverSocket;

    // Crear una lista para almacenar todos los sockets de los clientes conectados
   private List<Socket> clientesConectados = new ArrayList<>();

    public RoomServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                clientesConectados.add(socket);

                new Thread(() -> {
                    try {
                        // Crear streams de entrada y salida para el cliente
                        DataInputStream clienteInputStream = new DataInputStream(socket.getInputStream());
                        DataOutputStream clienteOutputStream = new DataOutputStream(socket.getOutputStream());

                        // Bucle para recibir datos de audio del cliente y reenviarlos a todos los clientes conectados
                        while (true) {
                            byte[] buffer = new byte[1024];
                            int numBytesRecibidos = clienteInputStream.read(buffer, 0, buffer.length);
                            System.out.println("SERVIDOR: datos de audio recibidos del cliente cliente");

                            // Enviar los datos de audio recibidos a todos los clientes conectados
                            for (Socket socketConectado : clientesConectados) {
                                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                                outputStream.write(buffer, 0, numBytesRecibidos);
                                outputStream.flush();
                                System.out.println("SERVIDOR: datos de audio enviados al cliente");
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Error al comunicarse con el cliente: " + e.getMessage());
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
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

