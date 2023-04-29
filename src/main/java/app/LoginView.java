package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class LoginView extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            System.out.println("dieguete");

            FXMLLoader formLoader = new FXMLLoader(getClass().getResource("login-vista.fxml"));
            AnchorPane form = formLoader.load();
            Scene formScene = new Scene(form);
            primaryStage.setScene(formScene);
            primaryStage.setResizable(true); //para que la ventana no sea redimensionable.

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }


    }

