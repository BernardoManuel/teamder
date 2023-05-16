package app;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class AppController extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showSplashScreen();
    }

    private void showSplashScreen() {
        try {
            // Crea un nuevo Stage para el splash
            Stage splashStage = new Stage();

            FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("splash-vista.fxml"));
            StackPane splash = splashLoader.load();
            Scene splashScene = new Scene(splash);
            splashStage.initStyle(StageStyle.UNDECORATED); // Quita la barra de título del splash
            splashStage.initOwner(primaryStage);
            splashStage.setScene(splashScene);
            splashStage.show();

            // Duración de la pantalla de carga: (3 segundos)
            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(3));
            pauseTransition.setOnFinished(event -> {
                showLoginScreen();
                splashStage.close(); // Cierra el splash al finalizar la transición
            });
            pauseTransition.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoginScreen() {
        try {
            FXMLLoader formLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            AnchorPane form = formLoader.load();
            Scene formScene = new Scene(form);
            primaryStage.setScene(formScene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
