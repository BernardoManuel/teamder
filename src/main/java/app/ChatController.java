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
    private Socket socket;
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
    @FXML private Button buscarJugadoresBtn;
    @FXML private Button invitarAmigoBtn;


    public void initialize() throws SQLException {

        connection = ConnectionUtil.getConnection();
        messageRepository = new MessageRepository(connection);
        usuariosRepository = new UsuariosRepository(connection);

        Platform.runLater(() -> {
            chatTitle.setText(room.getNombre());
            loadMessages();
            try {
                socket = new Socket("localhost", 50000);
                socket.setSoLinger(true, 0);
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.dataInputStream = new DataInputStream(socket.getInputStream());
                this.dataOutputStream = new DataOutputStream(socket.getOutputStream());

                listenForMessage();
                sendMessage();
                listenForVoice();
                sendVoice();
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter,dataInputStream,dataOutputStream);
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
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

    // Mensaje de conexión al servidor.
    public void sendMessage() {
        try {
            bufferedWriter.write(user.getNombreUsuario());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println("Mensaje enviado");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter,dataInputStream,dataOutputStream);
        }
    }

    public void sendMessage(String msg) {
        try {
            if (socket.isConnected()) {
                bufferedWriter.write(user.getNombreUsuario() + ": " + msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                saveMessage(msg);
                System.out.println("Mensaje enviado");
            }
        } catch (IOException | SQLException e) {
            closeEverything(socket, bufferedReader, bufferedWriter,dataInputStream,dataOutputStream);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromRoom;
                while (socket != null && socket.isConnected()) {
                    try {
                        msgFromRoom = bufferedReader.readLine();
                        printMessage(msgFromRoom);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter, dataInputStream,dataOutputStream);
                    }
                }
            }
        }).start();
    }

    // Bucle para el envio de datos de audio al servidor
    public void sendVoice() {
        new Thread(() -> {
            while (socket != null && socket.isConnected()) {
                try {
                    // Configurar la línea de entrada de audio (micrófono)
                    AudioFormat formatoAudioEntrada = new AudioFormat(16000.0f, 16, 1, true, true);
                    TargetDataLine lineaEntradaAudio = AudioSystem.getTargetDataLine(formatoAudioEntrada);
                    lineaEntradaAudio.open(formatoAudioEntrada);
                    lineaEntradaAudio.start();

                    // Buffer para los datos de audio
                    byte[] buffer = new byte[1024];

                    int numBytesLeidos = lineaEntradaAudio.read(buffer, 0, buffer.length);

                    // Enviar datos de audio al servidor
                    dataOutputStream.write(buffer, 0, numBytesLeidos);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter, dataInputStream,dataOutputStream);
                } catch (LineUnavailableException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter, dataInputStream,dataOutputStream);
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    // Bucle para la reproducción de audio recibido del servidor
    public void listenForVoice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket != null && socket.isConnected()) {
                    try {
                        // Configurar la línea de salida de audio (altavoces)
                        AudioFormat formatoAudioSalida = new AudioFormat(16000.0f, 16, 1, true, true);
                        SourceDataLine lineaSalidaAudio = AudioSystem.getSourceDataLine(formatoAudioSalida);
                        lineaSalidaAudio.open(formatoAudioSalida);
                        lineaSalidaAudio.start();

                        // Buffer para los datos de audio
                        byte[] buffer = new byte[1024];

                        int numBytesRecibidos = dataInputStream.read(buffer, 0, buffer.length);

                        // Reproducir datos de audio en los altavoces
                        lineaSalidaAudio.write(buffer, 0, numBytesRecibidos);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter, dataInputStream,dataOutputStream);
                    } catch (LineUnavailableException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter, dataInputStream,dataOutputStream);
                        throw new RuntimeException(e);
                    }
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
            if (socket != null) {
                socket.close();
                socket = null;
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
