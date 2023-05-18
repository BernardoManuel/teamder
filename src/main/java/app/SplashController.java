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

    @FXML
    private void initialize() {

        Image logoCarga = new Image("file:src/main/resources/logo/logo_sin_fondo.png");
        imageLoading.setImage(logoCarga);

        Timeline progressBarAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(3), new KeyValue(progressBar.progressProperty(), 1))
        );

        progressBarAnimation.play();
    }

}

