package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class ChatItemController extends HBox {
    @FXML
    private Text roomTitle;

    private String title;

    public void initialize() {

        Platform.runLater(() -> {
            roomTitle.setText(title);
        });

    }

    public void setTitle(String title) {
        this.title = title;
    }
}
