package app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class HomeController {

    @FXML private BorderPane homeView;
    @FXML private VBox chatsList;

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
        }
    }

    public void placePlaceholder() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("placeholder-view.fxml"));
        homeView.setCenter(loader.load());
    }

    private BorderPane getChatView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
        return loader.load();
    }
}

