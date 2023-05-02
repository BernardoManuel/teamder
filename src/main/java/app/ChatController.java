package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.Message;
import model.Room;
import model.User;
import repository.MessageRepository;
import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.List;

public class ChatController extends BorderPane {
    //CONSTANTES DE FORMATO DE AUDIO
    public static final float SAMPLE_RATE = 44100.0f;
    public static final int SAMPLE_SIZE_IN_BITS = 16;
    public static final int CHANNELS = 1;
    public static final boolean SIGNED = true;
    public static final boolean BIG_ENDIAN = true;
    private Socket textChatSocket;
    private Socket voiceChatSocket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private User user;
    private Room room;
    private String inputMessageText;
    private MessageRepository messageRepository;
    @FXML private Text chatTitle;
    @FXML private VBox messageContainer;
    @FXML private TextField inputMessage;
    SourceDataLine lineaSalidaAudio;
    TargetDataLine lineaEntradaAudio;


    public void initialize() {
        messageRepository = new MessageRepository();
        inputMessage.addEventHandler(KeyEvent.KEY_PRESSED, this::handleEnterKeyPressed);

        Platform.runLater(() -> {
            chatTitle.setText(room.getNombre());
            loadMessages();
            try {
                textChatSocket = new Socket("localhost", 50000);
                textChatSocket.setSoLinger(true, 0);
                this.bufferedReader = new BufferedReader(new InputStreamReader(textChatSocket.getInputStream()));
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(textChatSocket.getOutputStream()));

                voiceChatSocket = new Socket("localhost", 50001);
                voiceChatSocket.setSoLinger(true, 0);
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

    public void setUser(User user) {
        this.user = user;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    private void printMessage(String message) {
        Platform.runLater(() -> {
            VBox msgPane = new VBox();
            msgPane.setMinHeight(50.0);
            msgPane.setAlignment(Pos.CENTER_LEFT);
            msgPane.setFillWidth(false);

            Text msgText = new Text(message);
            msgText.setFill(Color.WHITE);

            msgPane.getChildren().add(msgText);
            msgPane.getStyleClass().add("message");

            messageContainer.getChildren().add(msgPane);
            messageContainer.getStylesheets().add(getClass().getResource("/css/message.css").toExternalForm());
        });
    }

    private void printMessage(String username, String message) {
        Platform.runLater(() -> {
            VBox msgPane = new VBox();
            msgPane.setMinHeight(50.0);
            msgPane.setAlignment(Pos.CENTER_LEFT);
            msgPane.setFillWidth(false);

            HBox contentPane = new HBox();
            contentPane.setPadding(new Insets(5, 10, 5, 10));
            contentPane.getStyleClass().add("message");

            Text msgText = new Text(username + ": " + message);
            msgText.getStyleClass().add("message-text");
            contentPane.getChildren().add(msgText);

            msgPane.getChildren().add(contentPane);

            if (username.equals(user.getNombreUsuario())) {
                msgPane.getStyleClass().add("own-message");
            }

            messageContainer.getChildren().add(msgPane);
            messageContainer.getStylesheets().add(getClass().getResource("/css/message.css").toExternalForm());
        });
    }

    @FXML
    private void onButtonClick() {
        inputMessageText = inputMessage.getText();
        printMessage(user.getNombreUsuario(), inputMessageText);
        sendMessage(inputMessageText);
        inputMessage.setText("");
    }

    public void loadMessages() {
        try {
            List<Message> messages = messageRepository.findRoomMessages(room);
            if (messages != null && !messages.isEmpty()) {
                for (Message message : messages) {
                    User userMessage = message.getUser();
                    printMessage(userMessage.getNombreUsuario(), message.getMensaje());
                }
            }
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

    // Mensaje de conexión al servidor.
    public void sendMessage() {
        try {
            bufferedWriter.write(user.getNombreUsuario() + "-" + room.getId());
            bufferedWriter.newLine();
            bufferedWriter.flush();
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
            }
        } catch (IOException e) {
            closeEverything(textChatSocket, bufferedReader, bufferedWriter, dataInputStream, dataOutputStream);
        }
    }

    private void saveMessage(String msg) {
        Message message = new Message();

        message.setUser(user);
        message.setMensaje(msg);
        message.setFecha(Instant.now().getEpochSecond());
        message.setRoom(room);

        messageRepository.save(message);
    }

    private void handleEnterKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onButtonClick();
            event.consume();
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
                    // Buffer para los datos de audio
                    byte[] buffer = new byte[1024];
                    int numBytesLeidos = lineaEntradaAudio.read(buffer, 0, buffer.length);

                    // Enviar datos de audio al servidor
                    dataOutputStream.write(buffer, 0, numBytesLeidos);
                    dataOutputStream.flush();
                    System.out.println("Datos de audio enviados al servidor");
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
                    // Buffer para los datos de audio
                    byte[] buffer = new byte[1024];

                    int numBytesRecibidos = dataInputStream.read(buffer, 0, buffer.length);
                    if (numBytesRecibidos >= 0) {
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

}
