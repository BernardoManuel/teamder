package app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HomeController {

    @FXML private BorderPane homeView;
    @FXML private VBox chatsList;

    public void initialize() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("placeholder-view.fxml"));
        homeView.setCenter(loader.load());
        /*
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
        homeView.setCenter(loader.load());
         */

        for (int i = 0; i < 25; i++) {
            loader = new FXMLLoader(getClass().getResource("chat-item-view.fxml"));
            chatsList.getChildren().add(loader.load());
        }
    }

    public BorderPane getHomeView() {
        return homeView;
    }
}
