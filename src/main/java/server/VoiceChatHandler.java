package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class VoiceChatHandler implements Runnable {

    private Socket socket;

    DataInputStream dataInputStream;
    DataOutputStream clienteOutputStream;

    public VoiceChatHandler(Socket socket){
        this.socket = socket;
        try {
            this.dataInputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeEverything(socket, dataInputStream, clienteOutputStream);
            throw new RuntimeException(e);
        }
    }

    public void closeEverything(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        try {
            if (dataInputStream != null) {
                dataInputStream.close();
            }
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Crear streams de entrada y salida para el cliente
            DataInputStream clienteInputStream = new DataInputStream(socket.getInputStream());

            // Bucle para recibir datos de audio del cliente y reenviarlos a todos los clientes conectados
            while (socket.isConnected()) {
                byte[] buffer = new byte[1024];
                int numBytesRecibidos = clienteInputStream.read(buffer, 0, buffer.length);
                System.out.println("VOICE CHAT HANDLER: datos de audio recibidos del cliente cliente");

                // Enviar los datos de audio recibidos a todos los clientes conectados
                for (Socket socketConectado : RoomServer.socketsVoz) {
                    if (socketConectado!=socket) {
                        clienteOutputStream = new DataOutputStream(socketConectado.getOutputStream());
                        clienteOutputStream.write(buffer, 0, numBytesRecibidos);
                        clienteOutputStream.flush();
                        System.out.println("VOICE CHAT HANDLER: datos de audio enviados al cliente");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("VOICE CHAT HANDLER: Error al comunicarse con el cliente: " + e.getMessage());
            closeEverything(socket, dataInputStream, clienteOutputStream);
        }
    }
}
