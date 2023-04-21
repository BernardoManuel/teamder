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

import java.io.IOException;

public class HomeController {

    @FXML private BorderPane homeView;
    @FXML private VBox chatsList;

    private HBox itemClicked;
    private ChatController currentChatController;

    @FXML
    public void initialize() throws IOException {
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

    /*
    private BorderPane getChatView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
        currentChatController = loader.load();

        return currentChatController;
    }

     */
    private Parent getChatView() throws IOException {
        if (currentChatController != null) {
            currentChatController.closeEverything();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
        Parent root = loader.load();
        currentChatController = loader.getController();
        System.out.println(currentChatController);

        return root;
    }
}

