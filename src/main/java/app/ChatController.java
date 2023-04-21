package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.*;
import java.net.Socket;

public class ChatController extends BorderPane {


    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;
    private String inputMessageText;

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField inputMessage;

    public void initialize() {
        System.out.println("Chat incrustado.");

        try {
            socket = new Socket("localhost", 1234);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            username = "diego";

            listenForMessage();
            sendMessage();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

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


    @FXML
    private void onButtonClick() {
        inputMessageText = inputMessage.getText();
        printMessage("Tú: " + inputMessageText);
        sendMessage(inputMessageText);
        inputMessage.setText("");
    }


    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println("Mensaje enviado");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage(String msg) {
        try {
            if (socket.isConnected()) {
                bufferedWriter.write(username + ": " + msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                System.out.println("Mensaje enviado");
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromRoom;
                while (socket.isConnected()) {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
