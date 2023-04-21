package app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import repository.UsuariosRepository;
import utils.ConnectionUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class HomeController extends Scene {

    @FXML private BorderPane homeView;
    @FXML private VBox chatsList;

    private String username;
    private HBox itemClicked;
    private ChatController currentChatController;
    private UsuariosRepository usuariosRepository;
    private Connection connection;

    public HomeController(Parent parent) {
        super(parent);
    }

    @FXML
    public void initialize() throws IOException, SQLException {
        FXMLLoader loader;

        System.out.println(username);

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
        if (currentChatController != null) {
            currentChatController.closeEverything();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
        Parent root = loader.load();
        currentChatController = loader.getController();

        return root;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

