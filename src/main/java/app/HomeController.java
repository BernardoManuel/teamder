package app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class HomeController {

    @FXML private BorderPane homeView;

    public void initialize() throws IOException {

/*
        FXMLLoader loader = new FXMLLoader(getClass().getResource("placeholder-view.fxml"));
        homeView.setCenter(loader.load());
 */
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
        homeView.setCenter(loader.load());


    }
}
