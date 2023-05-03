package app;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SplashController {

    @FXML
    private ImageView imageLoading;
    @FXML
    private void initialize() {

        Image logoCarga = new Image("file:src/main/resources/logo/logo_sin_fondo.png");
        imageLoading.setImage(logoCarga);

    }
}
