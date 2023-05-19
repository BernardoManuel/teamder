package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Message;
import model.Room;
import model.User;
import repository.MessageRepository;
import repository.RoomRepository;
import repository.UserRepository;
import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.List;


public class ChatController extends BorderPane {
    //CONSTANTES DE FORMATO DE AUDIO
    public static final float SAMPLE_RATE = 16000f;
    public static final int SAMPLE_SIZE_IN_BITS = 16;
    public static final int CHANNELS = 1;
    public static final boolean SIGNED = true;
    public static final boolean BIG_ENDIAN = false;
    public static final String SERVER_ADDRESS = "localhost";
    private Socket textChatSocket;
    private Socket voiceChatSocket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private HomeController homeController;
    private User user;
    private Room room;
    private String inputMessageText;
    private MessageRepository messageRepository;
    private RoomRepository roomRepository;
    private UserRepository userRepository;
    @FXML
    private Text chatTitle;
    @FXML
    private VBox messageContainer;
    @FXML
    private TextField inputMessage;
    SourceDataLine lineaSalidaAudio;
    TargetDataLine lineaEntradaAudio;
    private Boolean calling;
    private BorderPane homeView;
    @FXML
    private Button callBtn;
    @FXML
    private BorderPane chatView;

    private Thread listenForMessageThread;
    private Thread sendVozThread;
    private Thread receiveVozThread;

    public void initialize() {
        messageRepository = new MessageRepository();
        roomRepository = new RoomRepository();
        userRepository = new UserRepository();
        calling = false;
        inputMessage.addEventHandler(KeyEvent.KEY_PRESSED, this::handleEnterKeyPressed);

        Platform.runLater(() -> {
            chatTitle.setText(room.getNombre());
            loadMessages();
            try {
                textChatSocket = new Socket(SERVER_ADDRESS, 50000);
                textChatSocket.setSoLinger(true, 0);
                this.bufferedReader = new BufferedReader(new InputStreamReader(textChatSocket.getInputStream()));
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(textChatSocket.getOutputStream()));

                listenForMessage();
                sendMessage();

            } catch (IOException e) {
                closeEverything();
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

            HBox contentPane = new HBox();
            contentPane.setPadding(new Insets(5, 10, 5, 10));
            contentPane.getStyleClass().add("message");

            Text msgText = new Text(message);
            msgText.getStyleClass().add("message-text");
            contentPane.getChildren().add(msgText);

            msgPane.getChildren().add(contentPane);

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
        } catch (IOException e) {
            closeEverything();
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
            closeEverything();
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
        listenForMessageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromRoom;
                while (textChatSocket != null && textChatSocket.isConnected()) {
                    try {
                        msgFromRoom = bufferedReader.readLine();
                        printMessage(msgFromRoom);
                    } catch (IOException e) {
                        closeEverything();
                    }
                }
            }
        });
        listenForMessageThread.start();
    }

    // Bucle para el envío de datos de audio al servidor
    public void sendVoz() {
        sendVozThread = new Thread(() -> {
            while (voiceChatSocket != null && voiceChatSocket.isConnected()) {
                try {
                    // Verificar si la línea de entrada de audio está inicializada
                    if (lineaEntradaAudio != null) {
                        // Bucle para el envío de datos de audio al servidor
                        while (true) {
                            // Buffer para los datos de audio
                            byte[] buffer = new byte[1024];
                            int numBytesLeidos = lineaEntradaAudio.read(buffer, 0, buffer.length);

                            // Enviar datos de audio al servidor
                            dataOutputStream.write(buffer, 0, numBytesLeidos);
                            dataOutputStream.flush();
                            System.out.println("Datos de audio enviados al servidor");
                        }
                    }
                } catch (IOException e) {
                    lineaEntradaAudio.close();
                    lineaSalidaAudio.close();
                    closeEverything();
                }
            }
        });
        sendVozThread.start();
    }

    // Bucle para recibir datos de audio del servidor
    public void receiveVoz() {
        receiveVozThread = new Thread(() -> {
            while (voiceChatSocket != null && voiceChatSocket.isConnected()) {
                try {
                    // Verificar si la línea de salida de audio está inicializada
                    if (lineaSalidaAudio != null) {
                        // Bucle para la reproducción de audio recibido del servidor
                        while (true) {
                            // Buffer para los datos de audio
                            byte[] buffer = new byte[1024];

                            int numBytesRecibidos = dataInputStream.read(buffer, 0, buffer.length);
                            if (numBytesRecibidos >= 0) {
                                System.out.println("Datos de audio recibidos del servidor");

                                // Reproducir datos de audio en los altavoces
                                lineaSalidaAudio.write(buffer, 0, numBytesRecibidos);
                                System.out.println("Audio reproducido en altavoces");
                            }
                        }
                    }
                } catch (IOException e) {
                    lineaEntradaAudio.close();
                    lineaSalidaAudio.close();
                    closeEverything();
                }
            }
        });
        receiveVozThread.start();
    }


    public void closeEverything() {
        try {
            // Cerrar conexiones de texto
            if (bufferedReader != null) {
                bufferedReader.close();
                bufferedReader = null;
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
                bufferedWriter = null;
            }
            if (textChatSocket != null) {
                textChatSocket.close();
                textChatSocket = null;
            }

            // Detener líneas de audio solo si la llamada está activa
            if (calling) {
                stopAudioThreads();

                if (lineaEntradaAudio != null && lineaEntradaAudio.isOpen()) {
                    lineaEntradaAudio.stop();
                    lineaEntradaAudio.close();
                    lineaEntradaAudio = null;
                }
                if (lineaSalidaAudio != null && lineaSalidaAudio.isOpen()) {
                    lineaSalidaAudio.stop();
                    lineaSalidaAudio.close();
                    lineaSalidaAudio = null;
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                    dataInputStream = null;
                }
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                    dataOutputStream = null;
                }
                if (voiceChatSocket != null) {
                    voiceChatSocket.close();
                    voiceChatSocket = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void openRoomControls() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("room-control-view.fxml"));
            Parent root = loader.load();
            ((RoomControlController) loader.getController()).setChatView(chatView);
            ((RoomControlController) loader.getController()).setHomeView(homeView);
            ((RoomControlController) loader.getController()).setRoom(room);
            ((RoomControlController) loader.getController()).setUser(user);
            ((RoomControlController) loader.getController()).setHomeController(homeController);
            homeView.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void startCall() {
        if (!calling) {
            callBtn.setText("Colgar");
            calling = true;
            try {
                // Asegúrate de que la línea de entrada de audio esté inicializada antes de iniciarla
                if (voiceChatSocket == null) {
                    // Configurar la línea de entrada de audio (micrófono)
                    AudioFormat formatoAudio2 = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
                    lineaEntradaAudio = AudioSystem.getTargetDataLine(formatoAudio2);
                    lineaEntradaAudio.open(formatoAudio2);
                    lineaEntradaAudio.start();

                    voiceChatSocket = new Socket(SERVER_ADDRESS, 50001);
                    voiceChatSocket.setSoLinger(true, 0);
                    this.dataInputStream = new DataInputStream(voiceChatSocket.getInputStream());
                    this.dataOutputStream = new DataOutputStream(voiceChatSocket.getOutputStream());

                    // Configurar la línea de salida de audio (altavoces)
                    AudioFormat formatoAudio = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
                    lineaSalidaAudio = AudioSystem.getSourceDataLine(formatoAudio);
                    lineaSalidaAudio.open(formatoAudio);
                    lineaSalidaAudio.start();

                    sendVoz();
                    receiveVoz();
                } else {
                    lineaEntradaAudio.start();
                    lineaSalidaAudio.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
                callBtn.setText("Llamar");
                calling = false;
                stopAudioThreads();
                closeAudioConnections();
            }
        } else {
            callBtn.setText("Llamar");
            calling = false;
            lineaEntradaAudio.stop();
            lineaSalidaAudio.stop();
        }
    }


    private void stopAudioThreads() {
        if (sendVozThread != null) {
            sendVozThread.interrupt();
            sendVozThread = null;
        }
        if (receiveVozThread != null) {
            receiveVozThread.interrupt();
            receiveVozThread = null;
        }
    }

    private void closeAudioConnections() {
        try {
            if (lineaEntradaAudio != null && lineaEntradaAudio.isOpen()) {
                lineaEntradaAudio.stop();
                lineaEntradaAudio.close();
                lineaEntradaAudio = null;
            }
            if (lineaSalidaAudio != null && lineaSalidaAudio.isOpen()) {
                lineaSalidaAudio.stop();
                lineaSalidaAudio.close();
                lineaSalidaAudio = null;
            }
            if (dataInputStream != null) {
                dataInputStream.close();
                dataInputStream = null;
            }
            if (dataOutputStream != null) {
                dataOutputStream.close();
                dataOutputStream = null;
            }
            if (voiceChatSocket != null) {
                voiceChatSocket.close();
                voiceChatSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopAllThreads() {
        // Detener hilos relacionados con audio
        if (sendVozThread != null) {
            sendVozThread.interrupt();
            sendVozThread = null;
        }
        if (receiveVozThread != null) {
            receiveVozThread.interrupt();
            receiveVozThread = null;
        }
        if (listenForMessageThread != null) {
            listenForMessageThread.interrupt();
            listenForMessageThread = null;
        }
    }

    public void closeApplication() throws IOException {
        // Detener todos los hilos
        stopAllThreads();

        if (bufferedReader != null) {
            bufferedReader.close();
            bufferedReader = null;
        }
        if (bufferedWriter != null) {
            bufferedWriter.close();
            bufferedWriter = null;
        }
        if (dataInputStream != null) {
            dataInputStream.close();
            dataInputStream = null;
        }
        if (dataOutputStream != null) {
            dataOutputStream.close();
            dataOutputStream = null;
        }
        if (textChatSocket != null) {
            textChatSocket.close();
            textChatSocket = null;
        }
        if (voiceChatSocket != null) {
            voiceChatSocket.close();
            voiceChatSocket = null;
        }
        if (lineaEntradaAudio != null && lineaEntradaAudio.isOpen()) {
            lineaEntradaAudio.stop();
            lineaEntradaAudio.close();
            lineaEntradaAudio = null;
        }
        if (lineaSalidaAudio != null && lineaSalidaAudio.isOpen()) {
            lineaSalidaAudio.stop();
            lineaSalidaAudio.close();
            lineaSalidaAudio = null;
        }
        if (dataInputStream != null) {
            dataInputStream.close();
            dataInputStream = null;
        }
        if (dataOutputStream != null) {
            dataOutputStream.close();
            dataOutputStream = null;
        }
        if (voiceChatSocket != null) {
            voiceChatSocket.close();
            voiceChatSocket = null;
        }
    }

    public void setHomeView(BorderPane homeView) {
        this.homeView = homeView;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
}
