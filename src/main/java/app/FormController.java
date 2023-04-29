package app;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Usuario;
import repository.UsuariosRepository;
import utils.ConnectionUtil;
import utils.PasswordUtil;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    private static final String DB_URL = "jdbc:mysql://localhost:3306/teamder";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private UsuariosRepository usuariosRepository;
    private Connection connection;

    public void initialize() throws SQLException {
        //Utilizamos el util de conexion para crear una conexion a nuestra BBDD
        connection = ConnectionUtil.getConnection();
        usuariosRepository = new UsuariosRepository(connection);

        //insertamos el logo del login
        Image logoFormulario = new Image("file:src/main/resources/logo/logo_sin_fondo.png");
        imageViewLogo.setImage(logoFormulario);

        //insertamos el fondo del left pane
        Image imagenFondo = new Image("file:src/main/resources/backgrounds/fondo_left_pane.png");
        imageViewLeftPane.setImage(imagenFondo);

        //Colocamos el focus en el boton
        Platform.runLater(() -> buttonLogin.requestFocus());

        passwordField.addEventHandler(KeyEvent.KEY_PRESSED, this::handleEnterKeyPressed);

        buttonLogin.setOnAction(actionEvent ->
                {
                    try {
                        handleLogin();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        hyperlinkCrearCuenta.setOnMouseClicked(mouseEvent ->
                formRegistro()
        );
    }

    private void handleEnterKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                handleLogin();
            } catch (SQLException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                mostrarMensajeError("Error al iniciar sesión: " + e.getMessage());
            }
        }
    }


    private void formRegistro() {
        try {
            //Cargamos la vista home
            FXMLLoader formLoader = new FXMLLoader(getClass().getResource("registro.fxml"));
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
    private void iniciarSesion(Usuario usuario) {
        try {
//            //Cargamos la vista home
//            FXMLLoader formLoader = new FXMLLoader(getClass().getResource("homePage.fxml"));
//            AnchorPane home = formLoader.load();
//            Scene homeScene = new Scene(home);
//            //Recuperamos y mostramos la vista home
//            Stage stage = (Stage) buttonLogin.getScene().getWindow();
//            stage.setResizable(true);//Permitimos la redimension de la ventana
//            stage.setScene(homeScene);

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
    private void handleLogin() throws SQLException, NoSuchAlgorithmException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validar usuario
        Usuario usuario = usuariosRepository.findUsuarioByNombreUsuario(username);

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
            String passwordBbdd = usuario.getContraseña();

            // Comparar las contraseñas de manera segura
            boolean passwordsIguales = MessageDigest.isEqual(passwordGenerada.getBytes(), passwordBbdd.getBytes());

            if (passwordsIguales) {
                // Si es correcto cambiar scene
                iniciarSesion(usuario);
            } else {
                //Lanzar error de inicio de sesión.
                mostrarMensajeError("Usuario o contraseña no coinciden");
                return;
            }
        } else {
            //Lanzar error de inicio de sesión.
            mostrarMensajeError("Usuario o contraseña no coinciden");
            return;
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

    private void sendUsernameToHomeView(Parent root, String username) {
        Text usernameField = (Text) root.lookup("#usernameLogged");
        usernameField.setText(username);
    }

}