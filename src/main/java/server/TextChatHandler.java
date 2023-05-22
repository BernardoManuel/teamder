package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class TextChatHandler implements Runnable {

    public static ArrayList<TextChatHandler> textChatHandlers = new ArrayList<TextChatHandler>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private int id_room;

    public TextChatHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String username_and_idRoom = bufferedReader.readLine();
            this.clientUsername = username_and_idRoom.split("-")[0];
            this.id_room = Integer.parseInt(username_and_idRoom.split("-")[1]);
            textChatHandlers.add(this);

        } catch (IOException e) {
            debugMsg("TEXT CHAT HANDLER: error en el I/O.");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Metodo que envia el mensaje a todos los clientes conectados que se encuentren el la misma sala. Pero no al
     * cliente que envio el mensaje.
     * @param msg mensaje a enviar.
     */
    public void broadcastMessage(String msg) {
        debugMsg("TEXT CHAT HANDLER: reenviando mensaje a clientes conectados.");
        for (TextChatHandler textChatHandler : textChatHandlers) {
            try {
                if (!textChatHandler.clientUsername.equals(clientUsername) && textChatHandler.id_room == id_room) {
                    textChatHandler.bufferedWriter.write(msg);
                    textChatHandler.bufferedWriter.newLine();
                    textChatHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        textChatHandlers.remove(this);
        RoomServer.socketsTexto.remove(this);
    }

    /**
     * Metodo que cierra los flujos de datos y conexiones.
     * @param socket
     * @param bufferedReader
     * @param bufferedWriter
     */
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hilo de chat de texto que reenvia los mensajes recibidos a los clientes.
     */
    @Override
    public void run() {
        String msgFromClient;
        while (socket.isConnected()) {
            try {
                msgFromClient = bufferedReader.readLine();
                debugMsg("TEXT CHAT HANDLER: mensaje recibido del cliente.");

                if(msgFromClient!=null){
                    broadcastMessage(msgFromClient);
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    private void debugMsg(String msg) {
        // System.out.println(msg);
    }
}
