package app;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class PlaceholderController extends BorderPane {

    @FXML
    private ImageView placeholderLogo;

    public void initialize() {
        //Establece la imagen del place holder con el logo de la aplicacion.
        Image logo = new Image("file:src/main/resources/logo/logo_sin_fondo.png");
        placeholderLogo.setImage(logo);
    }
}
