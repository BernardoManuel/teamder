package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RoomServer {
    private ServerSocket textServerSocket;
    private ServerSocket voiceServerSocket;

    // Crear una lista para almacenar todos los sockets de los clientes conectados
    public static List<Socket> socketsVoz = new ArrayList<>();
    public static List<Socket> socketsTexto = new ArrayList<>();

    public RoomServer(ServerSocket textServerSocket, ServerSocket voiceServerSocket) {
        this.textServerSocket = textServerSocket;
        this.voiceServerSocket = voiceServerSocket;

    }

    public void startServer() {
        voiceChatSocketsAccept();
        textChatSocketsAccept();

    }

    /**
     * Metodo que inicia un hilo para la aceptacion de clientes al socket de chat de voz en el servidor.
     * Y lanza un hilo voiceChatHandler por cada cliente.
     */
    public void voiceChatSocketsAccept() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!voiceServerSocket.isClosed()) {
                        Socket socket = voiceServerSocket.accept();
                        System.out.println("ROOM SERVER: cliente conectado al chat de voz...");

                        socketsVoz.add(socket);

                        VoiceChatHandler voiceChatHandler = new VoiceChatHandler(socket);
                        Thread hiloClientHandler = new Thread(voiceChatHandler);
                        hiloClientHandler.start();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    if(voiceServerSocket!=null){
                        try {
                            voiceServerSocket.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * Metodo que lanza hilo para aceptar conexiones de clientes al chat de texto.
     * Acepta la conexion al socket de chat de texto y lanza hilo de TextChatHandler.
     */
    public void textChatSocketsAccept() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!textServerSocket.isClosed()) {
                        Socket socket = textServerSocket.accept();
                        System.out.println("ROOM SERVER: cliente conectado al chat de texto...");

                        socketsTexto.add(socket);

                        TextChatHandler textChatHandler = new TextChatHandler(socket);
                        Thread hiloClientHandler = new Thread(textChatHandler);
                        hiloClientHandler.start();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    if(textServerSocket!=null){
                        try {
                            textServerSocket.close();

                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        }).start();
    }

}

