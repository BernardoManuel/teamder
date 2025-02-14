package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

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
            e.printStackTrace();
        }
    }

    /**
     * Metodo que cierra las conexiones y flujos de datos relacionados con el chat de voz.
     * @param socket
     * @param dataInputStream
     * @param dataOutputStream
     */
    public void closeEverything(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        RoomServer.socketsVoz.remove(socket);
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

    /**
     * Hilo de chat de voz que recibe los paquetes de voz y los reenvia a los clientes conectados excepto al mismo
     * cliente que lo ha enviado.
     */
    @Override
    public void run() {
        try {
            // Crear streams de entrada y salida para el cliente
            DataInputStream clienteInputStream = new DataInputStream(socket.getInputStream());

            // Bucle para recibir datos de audio del cliente y reenviarlos a todos los clientes conectados
            while (socket.isConnected()) {
                byte[] buffer = new byte[1024];
                int numBytesRecibidos = clienteInputStream.read(buffer, 0, buffer.length);
                debugMsg("VOICE CHAT HANDLER: datos de audio recibidos del cliente cliente");

                // Enviar los datos de audio recibidos a todos los clientes conectados
                for (Socket socketConectado : RoomServer.socketsVoz) {
                    if (socketConectado!=socket) {
                        clienteOutputStream = new DataOutputStream(socketConectado.getOutputStream());
                        try {
                            if (numBytesRecibidos >= 0) {
                                clienteOutputStream.write(buffer, 0, numBytesRecibidos);
                                clienteOutputStream.flush();
                                debugMsg("VOICE CHAT HANDLER: datos de audio enviados al cliente");
                            }
                        } catch (SocketException e) {
                            debugMsg("VOICE CHAT HANDLER: SocketException - " + e.getMessage());
                            closeEverything(socket, dataInputStream, clienteOutputStream);
                        }
                    }
                }
            }
        } catch (IOException e) {
            debugMsg("VOICE CHAT HANDLER: Error al comunicarse con el cliente: " + e.getMessage());
            closeEverything(socket, dataInputStream, clienteOutputStream);
        }
    }

    private void debugMsg(String msg) {
        // System.out.println(msg);
    }
}
