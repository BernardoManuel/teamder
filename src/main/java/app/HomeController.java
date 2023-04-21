package app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;

public class HomeController {

    @FXML private BorderPane homeView;
    @FXML private VBox chatsList;
    @FXML private Text usernameLogged;

    private String username;
    private HBox itemClicked;
    private ChatController currentChatController;
    @FXML
    public void initialize() throws IOException, SQLException {
        FXMLLoader loader;

        loader = new FXMLLoader(getClass().getResource("placeholder-view.fxml"));
        homeView.setCenter(loader.load());

        for (int i = 0; i < 25; i++) {
            loader = new FXMLLoader(getClass().getResource("chat-item-view.fxml"));
            HBox item = loader.load();
            chatsList.getChildren().add(item);

            Node view = item.getChildren().get(0).getParent();
            view.setOnMouseClicked(event -> {
                try {
                    homeView.setCenter(getChatView());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            itemClicked = item;
        }

    }

    public void placePlaceholder() throws IOException {
        if (currentChatController != null) {
            currentChatController.closeEverything();
            currentChatController = null;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("placeholder-view.fxml"));
        homeView.setCenter(loader.load());
    }

    private Parent getChatView() throws IOException {
        username = usernameLogged.getText();
        if (currentChatController != null) {
            currentChatController.closeEverything();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
        Parent root = loader.load();
        currentChatController = loader.getController();
        currentChatController.setUsername(username);

        return root;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}

