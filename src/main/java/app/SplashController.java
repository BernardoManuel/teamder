package app;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;


public class SplashController {

    @FXML
    private ImageView imageLoading;
    @FXML
    private ProgressBar progressBar;

    /**
     * Metodo que controla el splash y la barra de progreso al iniciarse la aplicación.
     */
    @FXML
    private void initialize() {

        //Define la imagen a cargar con el logo de la aplicacion.
        Image logoCarga = new Image("file:src/main/resources/logo/logo_sin_fondo.png");
        imageLoading.setImage(logoCarga);

        // Establece el tiempo de la animacion.
        Timeline progressBarAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(3), new KeyValue(progressBar.progressProperty(), 1))
        );

        progressBarAnimation.play();
    }

}

