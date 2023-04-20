package app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.MenuButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML private ImageView imageViewLogo;
    @FXML private ImageView imageViewFlecha;
    @FXML private MenuButton menuButtonJuegos;
    @FXML private Button cancelButton;


    public HomeController() {
    }

    public void initialize() {

        Image logoHome = new Image("file:src/main/resources/logo/logo_sin_fondo.png");
        imageViewLogo.setImage(logoHome);

        Image flechaHome = new Image("file:src/main/resources/flecha/flecha.png");
        imageViewFlecha.setImage(flechaHome);

    }

    public void seleccionarJuego(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        menuButtonJuegos.setText(menuItem.getText());
    }

    @FXML
    public void agregarAmigo(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("popUp.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage nuevaVentana = new Stage();
        nuevaVentana.setScene(scene);
        nuevaVentana.initModality(Modality.APPLICATION_MODAL);

        nuevaVentana.showAndWait();
    }
}

