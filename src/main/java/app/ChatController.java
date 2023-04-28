package app;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.Message;
import model.Room;
import model.User;
import repository.MessageRepository;
import repository.UsuariosRepository;
import utils.ConnectionUtil;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;

public class ChatController extends BorderPane {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private User user;
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

                listenForMessage();
                sendMessage();
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        });
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
            messageContainer.getStylesheets().add("file:///C:/Users/Estudios/Documents/Proyectos/teamder/src/main/resources/css/message.css");
        });
    }

    private void printMessage(String username, String message) {
        Platform.runLater(() -> {
            VBox msgPane = new VBox();
            msgPane.setMinHeight(50.0);
            msgPane.setAlignment(Pos.CENTER_LEFT);
            msgPane.setFillWidth(false);

            Text msgText = new Text(username + ": " + message);
            msgText.setFill(Color.WHITE);

            msgPane.getChildren().add(msgText);
            msgPane.getStyleClass().add("message");

            if (username.equals(user.getNombreUsuario())) {
                msgPane.getStyleClass().add("own-message");
            }

            messageContainer.getChildren().add(msgPane);
            messageContainer.getStylesheets().add("file:///C:/Users/Estudios/Documents/Proyectos/teamder/src/main/resources/css/message.css");
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
            bufferedWriter.write(user.getNombreUsuario() + "-" + room.getId());
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage(String msg) {
        try {
            if (socket.isConnected()) {
                bufferedWriter.write(user.getNombreUsuario() + ": " + msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                saveMessage(msg);
            }
        } catch (IOException | SQLException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
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
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
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

    public void setUser(User user) {
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
