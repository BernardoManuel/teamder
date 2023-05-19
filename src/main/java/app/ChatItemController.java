package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ChatItemController extends HBox {
    @FXML
    private Text roomTitle;

    @FXML private ImageView roomIcon;

    private String title;

    public void initialize() {

        Platform.runLater(() -> {
            roomTitle.setText(title);
            Image icono = new Image("file:src/main/resources/icons/room_icon.png", 45.0, 45.0, true, true);
            roomIcon.setImage(icono);
        });

    }

    public void setTitle(String title) {
        this.title = title;
    }
}
