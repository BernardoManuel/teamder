package app;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import repository.UserRepository;

import utils.EmailValidator;
import utils.PasswordUtil;
import utils.PasswordValidator;
import utils.UsernameValidator;

import java.security.NoSuchAlgorithmException;

public class RegistroController {

    @FXML
    private Hyperlink hyperlinkIniciarSesion;
    @FXML
    private Button buttonVolver;
    @FXML
    private ImageView imageViewLeftPane;
    @FXML
    private Button buttonRegistro;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField correoField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField confirmPasswordField;
    @FXML
    private Pane errorPane;
    @FXML
    private Pane errorPane2;
    @FXML
    private Label errorMessage;
    @FXML
    private Label errorMessage2;
    private UserRepository userRepository;
    private EmailValidator emailValidator;
    private PasswordValidator passwordValidator;
    private UsernameValidator usernameValidator;

    public void initialize() {
        //Creamos el Repositorio de Usuarios
        userRepository = new UserRepository();


        //insertamos el fondo del left pane
        Image imagenFondo = new Image("file:src/main/resources/backgrounds/fondo_left_pane.png");
        imageViewLeftPane.setImage(imagenFondo);

        Image imageBackArrow = new Image("file:src/main/resources/back_arrow.png");
        buttonVolver.setBackground(new Background(new BackgroundImage(imageBackArrow, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        //Colocamos el focus en el boton
        Platform.runLater(() -> buttonRegistro.requestFocus());

        //Evento de boton volver
        buttonVolver.setOnAction(actionEvent ->
                formIniciarSesion()
        );

        //Evento de click en hyperlink Inicia tu sesion
        hyperlinkIniciarSesion.setOnMouseClicked(mouseEvent ->
                formIniciarSesion()
        );

        //Evento de boton Registrate
        buttonRegistro.setOnAction(actionEvent ->
                {
                    try {
                        handleRegister();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

    }

    //Metodo que envia al inicio de sesion
    private void formIniciarSesion() {
        try {
            FXMLLoader formLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            AnchorPane form = formLoader.load();
            Scene formScene = new Scene(form);

            //Recuperamos y mostramos la vista registro
            Stage stage = (Stage) buttonVolver.getScene().getWindow();
            stage.setScene(formScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Metodo que manejara los datos del registro, comprueba en la base de datos por coincidencias.
    //Y almacenara el usuario nuevo.
    private void handleRegister() throws NoSuchAlgorithmException {

        User nuevoUsuario = new User();
        usernameValidator = new UsernameValidator();

        //Recogemos los campos
        String nombreUsuario = usernameField.getText();
        String correo = correoField.getText();
        String pass1 = passwordField.getText();
        String pass2 = confirmPasswordField.getText();

        //Validar campos vacios
        if (nombreUsuario.isEmpty() || correo.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
            mostrarMensajeError("Debe rellenar todos los campos");
            return;
        }

        // Comprobar nombre de usuario único
        if (userRepository.isNombreUsuarioExists(nombreUsuario)) {
            // El nombre de usuario ya existe, muestra un mensaje de error o lanza una excepción
            mostrarMensajeError("El nombre de usuario ya existe.");
            return;
        }
        nuevoUsuario.setNombreUsuario(nombreUsuario);

        // Comprobar correo electrónico único
        if (userRepository.isCorreoExists(correo)) {
            // El correo electrónico ya existe, muestra un mensaje de error o lanza una excepción
            mostrarMensajeError("El correo electronico ya existe");
            return;
        } else {
            //Verificamos de que sea un correo valido.
            emailValidator = new EmailValidator();
            if (emailValidator.validate(correo)) {
                //Set correo electronico
                nuevoUsuario.setCorreo(correo);
            } else {
                // El correo electrónico no tiene un formato valido.
                mostrarMensajeError("Correo electronico no valido");
                return;
            }
        }


        //Comprobar contraseñas concuerdan
        if (pass1.equals(pass2)) {
            String password = passwordField.getText();
            byte[] salt = PasswordUtil.generateSalt();
            byte[] hashedPassword = PasswordUtil.getHashedPassword(password, salt);
            String hashStr = PasswordUtil.bytesToHex(hashedPassword); // Convertir a representación hexadecimal
            String saltStr = PasswordUtil.bytesToHex(salt); // Convertir a representación hexadecimal

            //Validar seguridad de la contraseña
            passwordValidator = new PasswordValidator();
            if (!passwordValidator.validateLength(password)) {
                mostrarMensajeErrorGrande("Contraseña debe contener", "al menos 8 caracteres");
                return;
            } else if (!passwordValidator.validateUpperCase(password)) {
                mostrarMensajeErrorGrande("Contraseña debe contener", "al menos una letra mayúscula");
                return;
            } else if (!passwordValidator.validateLowerCase(password)) {
                mostrarMensajeErrorGrande("Contraseña debe contener", "al menos una letra minúscula");
                return;
            } else if (!passwordValidator.validateDigits(password)) {
                mostrarMensajeErrorGrande("Contraseña debe contener", "al menos un digito");
                return;
            } else if (!passwordValidator.validateSpecialChars(password)) {
                mostrarMensajeErrorGrande("Contraseña debe contener al menos", "un caracter especial \"!@#$%^&*()-+\"");
                return;
            } else {
                // Guardar hashStr y salt en la base de datos para el nuevo usuario
                nuevoUsuario.setPassword(hashStr);
                nuevoUsuario.setSalt(saltStr);
            }

        } else {
            mostrarMensajeError("Las contraseñas no coinciden");
            return;
        }

        if (!usernameValidator.validate(nombreUsuario)) {
            mostrarMensajeError("El nombre de usuario no es valido");
        } else {
            //Guardamos el nuevo usuario
            userRepository.save(nuevoUsuario);

            //Volver al login
            formIniciarSesion();
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

    // Método para mostrar el mensaje de error en el Pane
    private void mostrarMensajeErrorGrande(String mensaje1, String mensaje2) {
        // Configura el mensaje de error en el Label
        errorMessage.setText(mensaje1);
        errorMessage2.setText(mensaje2);
        // Hace visible el Pane de error
        errorPane.setVisible(true);
        errorPane2.setVisible(true);

        // Crea una Timeline para ocultar el mensaje de error después de 3 segundos
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Oculta el mensaje de error
                ocultarMensajeErrorGrande();
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    private void ocultarMensajeErrorGrande() {

        errorPane.setVisible(false);
        errorPane2.setVisible(false);
    }


}
