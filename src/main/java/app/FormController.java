package app;

import database.HibernateUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import org.hibernate.Session;
import repository.UserRepository;
import utils.PasswordUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FormController {
    @FXML
    private ImageView imageViewLogo;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ImageView imageViewLeftPane;
    @FXML
    private Button buttonLogin;
    @FXML
    private Hyperlink hyperlinkCrearCuenta;
    @FXML
    private Pane errorPane;
    @FXML
    private Label errorMessage;
    private UserRepository userRepository;

    public void initialize() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        userRepository = new UserRepository();

        passwordField.addEventHandler(KeyEvent.KEY_PRESSED, this::handleEnterKeyPressed);

        //insertamos el logo del login
        Image logoFormulario = new Image("file:src/main/resources/logo/logo_sin_fondo.png");
        imageViewLogo.setImage(logoFormulario);

        //insertamos el fondo del left pane
        Image imagenFondo = new Image("file:src/main/resources/backgrounds/fondo_left_pane.png");
        imageViewLeftPane.setImage(imagenFondo);

        //Colocamos el focus en el boton
        Platform.runLater(() -> buttonLogin.requestFocus());
        buttonLogin.setOnAction(actionEvent -> {
            try {
                handleLogin();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });
        hyperlinkCrearCuenta.setOnMouseClicked(mouseEvent -> formRegistro());
        session.close();
    }

    private void formRegistro() {
        try {
            //Cargamos la vista home
            FXMLLoader formLoader = new FXMLLoader(getClass().getResource("register-view.fxml"));
            AnchorPane registro = formLoader.load();
            Scene registroScene = new Scene(registro);
            //Recuperamos y mostramos la vista registro
            Stage stage = (Stage) buttonLogin.getScene().getWindow();
            stage.setScene(registroScene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo que carga la vista home y la muestra.
     * Establece la propiedad de redimensionar a verdadero.
     */
    private void cargarVistaHome(User usuario) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home-view.fxml"));
            Parent root = fxmlLoader.load();
            ((HomeController) fxmlLoader.getController()).setUsername(usuario);

            Scene scene = new Scene(root, 905, 621);
            Stage stage = (Stage) buttonLogin.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleLogin() throws NoSuchAlgorithmException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validar usuario
        User usuario = userRepository.findUserByUsername(username);

        if (usuario != null) {
            // Obtener el salt del usuario encontrado
            String saltStr = usuario.getSalt();
            // Convertir el salt de hexadecimal a bytes
            byte[] saltBytes = PasswordUtil.hexToBytes(saltStr);
            // Generar el hash de la contraseña ingresada + salt obtenido de la BBDD
            byte[] bytePassword = PasswordUtil.getHashedPassword(password, saltBytes);
            // Obtener el HEX a partir del arreglo de bytes generado
            String passwordGenerada = PasswordUtil.bytesToHex(bytePassword);

            // Obtener la contraseña almacenada en la BBDD
            String passwordBbdd = usuario.getPassword();

            // Comparar las contraseñas de manera segura
            boolean passwordsIguales = MessageDigest.isEqual(passwordGenerada.getBytes(), passwordBbdd.getBytes());

            if (passwordsIguales) {
                // Si es correcto cambiar scene
                cargarVistaHome(usuario);
            } else {
                //Lanzar error de inicio de sesión.
                mostrarMensajeError("Usuario o contraseña no coinciden");
            }
        } else {
            //Lanzar error de inicio de sesión.
            mostrarMensajeError("Usuario o contraseña no coinciden");
        }
    }

    // Método para mostrar el mensaje de error en el Pane
    private void mostrarMensajeError(String mensaje) {
        // Configura el mensaje de error en el Label
        errorMessage.setText(mensaje);
        // Hace visible el Pane de error
        errorPane.setVisible(true);

        // Crea una Timeline para ocultar el mensaje de error después de 3 segundos
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Oculta el mensaje de error
                ocultarMensajeError();
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    private void ocultarMensajeError() {
        errorPane.setVisible(false);
    }


    private void handleEnterKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                handleLogin();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                mostrarMensajeError("Error al iniciar sesión: " + e.getMessage());
            }
        }
    }
}