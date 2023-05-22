package app;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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

    /**
     * Metodo que carga y muestra el panel de carga de la aplicacion.
     */
    private void showSplashScreen() {
        try {
            // Crea un nuevo Stage para la pantalla de carga.
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

    /**
     * Metodo que carga y muestra la vista de iniciar sesion.
     */
    private void showLoginScreen() {
        try {
            FXMLLoader formLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            AnchorPane form = formLoader.load();
            Scene formScene = new Scene(form);
            primaryStage.setScene(formScene);
            primaryStage.setResizable(false);

            // Carga el icono de la aplicación
            Image icon = new Image("file:src/main/resources/logo/logo_sin_fondo.png");
            primaryStage.getIcons().add(icon);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
