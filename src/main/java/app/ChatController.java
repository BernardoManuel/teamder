package app;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Message;
import model.Room;
import model.Usuario;
import repository.MessageRepository;
import repository.UsuariosRepository;
import utils.ConnectionUtil;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;

public class ChatController extends BorderPane {
    //CONSTANTES DE FORMATO DE AUDIO
    public static final float SAMPLE_RATE = 8000.0f;
    public static final int SAMPLE_SIZE_IN_BITS = 16;
    public static final int CHANNELS = 1;
    public static final boolean SIGNED = true;
    public static final boolean BIG_ENDIAN = false;

    private Socket textChatSocket;
    private Socket voiceChatSocket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Usuario user;
    private Room room;
    private String inputMessageText;
    private MessageRepository messageRepository;
    private UsuariosRepository usuariosRepository;
    private Connection connection;

    @FXML
    private Text chatTitle;
    @FXML
    private VBox messageContainer;
    @FXML
    private TextField inputMessage;
    @FXML
    private Button buscarJugadoresBtn;
    @FXML
    private Button invitarAmigoBtn;
    SourceDataLine lineaSalidaAudio;
    TargetDataLine lineaEntradaAudio;


    public void initialize() throws SQLException {

        connection = ConnectionUtil.getConnection();
        messageRepository = new MessageRepository(connection);
        usuariosRepository = new UsuariosRepository(connection);

        Platform.runLater(() -> {
            chatTitle.setText(room.getNombre());
            loadMessages();
            try {
                textChatSocket = new Socket("localhost", 50000);
                this.bufferedReader = new BufferedReader(new InputStreamReader(textChatSocket.getInputStream()));
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(textChatSocket.getOutputStream()));

                voiceChatSocket = new Socket("localhost", 50001);
                this.dataInputStream = new DataInputStream(voiceChatSocket.getInputStream());
                this.dataOutputStream = new DataOutputStream(voiceChatSocket.getOutputStream());

                listenForMessage();
                sendMessage();

                // Configurar la línea de salida de audio (altavoces)
                AudioFormat formatoAudio = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
                lineaSalidaAudio = AudioSystem.getSourceDataLine(formatoAudio);
                lineaSalidaAudio.open(formatoAudio);
                lineaSalidaAudio.start();


                // Configurar la línea de entrada de audio (micrófono)
                AudioFormat formatoAudio2 = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
                lineaEntradaAudio = AudioSystem.getTargetDataLine(formatoAudio2);
                lineaEntradaAudio.open(formatoAudio2);
                lineaEntradaAudio.start();

                sendVoz();
                receiveVoz();

            } catch (IOException e) {
                closeEverything(voiceChatSocket, bufferedReader, bufferedWriter, dataInputStream, dataOutputStream);
                closeEverything(textChatSocket, bufferedReader, bufferedWriter, dataInputStream, dataOutputStream);
                lineaEntradaAudio.close();
                lineaSalidaAudio.close();
            } catch (LineUnavailableException e) {
                lineaEntradaAudio.close();
                lineaSalidaAudio.close();
                throw new RuntimeException(e);
            }
        });
    }

    private void printMessage(String message) {
        Platform.runLater(() -> {
            Pane msgPane = new Pane();
            msgPane.setPrefHeight(50.0);
            msgPane.setPrefWidth(320.0);

            Text msgText = new Text(message);
            msgText.setLayoutX(20.0);
            msgText.setLayoutY(30.0);

            msgPane.getChildren().add(msgText);
            messageContainer.getChildren().add(msgPane);
        });
    }

    private void printMessage(String username, String message) {
        Platform.runLater(() -> {
            Pane msgPane = new Pane();
            msgPane.setPrefHeight(50.0);
            msgPane.setPrefWidth(320.0);

            Text msgText = new Text(username + ": " + message);
            msgText.setLayoutX(20.0);
            msgText.setLayoutY(30.0);

            msgPane.getChildren().add(msgText);
            messageContainer.getChildren().add(msgPane);
        });
    }


    @FXML
    private void onButtonClick() {
        inputMessageText = inputMessage.getText();
        printMessage(user.getNombreUsuario() + ": " + inputMessageText);
        sendMessage(inputMessageText);
        inputMessage.setText("");
    }

    @FXML
    private void onBuscarJugadoresClick() {
        //Buscar jugadores segun calificaciones.
    }

    @FXML
    private void onInvitarAmigoClick() {
        //Invitar amigo por username.
    }


    public void loadMessages() {
        try {
            ObservableList<Message> messages = messageRepository.findRoomMessages(room.getId());
            for (Message message : messages) {
                String username = usuariosRepository.getUsernameById(message.getId_user());
                printMessage(username, message.getMensaje());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mensaje de conexión al servidor.
    public void sendMessage() {
        try {
            bufferedWriter.write(user.getNombreUsuario() + "-" + room.getId());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println("Mensaje enviado");
        } catch (IOException e) {
            closeEverything(textChatSocket, bufferedReader, bufferedWriter, dataInputStream, dataOutputStream);
        }
    }

    public void sendMessage(String msg) {
        try {
            if (textChatSocket.isConnected()) {
                bufferedWriter.write(user.getNombreUsuario() + ": " + msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                saveMessage(msg);
                System.out.println("Mensaje enviado");
            }
        } catch (IOException | SQLException e) {
            closeEverything(textChatSocket, bufferedReader, bufferedWriter, dataInputStream, dataOutputStream);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromRoom;
                while (textChatSocket != null && textChatSocket.isConnected()) {
                    try {
                        msgFromRoom = bufferedReader.readLine();
                        printMessage(msgFromRoom);
                    } catch (IOException e) {
                        closeEverything(textChatSocket, bufferedReader, bufferedWriter, dataInputStream, dataOutputStream);
                    }
                }
            }
        }).start();
    }

    // Bucle para el envio de datos de audio al servidor
    public void sendVoz() {
        new Thread(() -> {
            while (voiceChatSocket != null && voiceChatSocket.isConnected()) {
                try {

                    // Bucle para el envio de datos de audio al servidor
                    while (true) {
                        // Buffer para los datos de audio
                        byte[] buffer = new byte[1024];
                        int numBytesLeidos = lineaEntradaAudio.read(buffer, 0, buffer.length);

                        // Enviar datos de audio al servidor
                        dataOutputStream.write(buffer, 0, numBytesLeidos);
                        dataOutputStream.flush();
                        System.out.println("Datos de audio enviados al servidor");
                    }
                } catch (IOException e) {
                    lineaEntradaAudio.close();
                    lineaSalidaAudio.close();
                    closeEverything(voiceChatSocket, bufferedReader, bufferedWriter, dataInputStream, dataOutputStream);
                }
            }
        }).start();
    }

    // Bucle para recibir de datos de audio al servidor
    public void receiveVoz() {
        new Thread(() -> {
            while (voiceChatSocket != null && voiceChatSocket.isConnected()) {
                try {

                    // Bucle para la reproducción de audio recibido del servidor
                    while (true) {
                        // Buffer para los datos de audio
                        byte[] buffer = new byte[1024];

                        int numBytesRecibidos = dataInputStream.read(buffer, 0, buffer.length);
                        System.out.println("Datos de audio recibidos del servidor");

                        // Reproducir datos de audio en los altavoces
                        lineaSalidaAudio.write(buffer, 0, numBytesRecibidos);
                        System.out.println("Audio reproducido en altavoces");
                    }
                } catch (IOException e) {
                    lineaEntradaAudio.close();
                    lineaSalidaAudio.close();
                    closeEverything(voiceChatSocket, bufferedReader, bufferedWriter, dataInputStream, dataOutputStream);
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (dataInputStream != null) {
                dataInputStream.close();
            }
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeEverything() {
        try {
            if (bufferedReader != null) {
                bufferedReader = null;
            }
            if (textChatSocket != null) {
                textChatSocket.close();
                textChatSocket = null;
            }
            if (voiceChatSocket != null) {
                voiceChatSocket.close();
                voiceChatSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    private void saveMessage(String msg) throws SQLException {
        Message message = new Message();

        message.setId_sala(room.getId());
        message.setId_user(user.getId());
        message.setMensaje(msg);
        message.setFecha(Instant.now().getEpochSecond());

        messageRepository.save(message);
    }
}
