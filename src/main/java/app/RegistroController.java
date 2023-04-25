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
import model.Usuario;
import model.Calificacion;
import repository.CalificacionRepository;
import repository.UsuariosRepository;
import utils.ConnectionUtil;
import utils.PasswordUtil;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class RegistroController {

    @FXML private Hyperlink hyperlinkIniciarSesion;
    @FXML private Button buttonVolver;
    @FXML private ImageView imageViewLeftPane;
    @FXML private Button buttonRegistro;
    @FXML private TextField usernameField;
    @FXML private TextField correoField;
    @FXML private TextField passwordField;
    @FXML private TextField confirmPasswordField;
    @FXML private Pane errorPane;
    @FXML private Label errorMessage;
    private UsuariosRepository usuariosRepository;
    private CalificacionRepository calificacionRepository;
    private Connection connection;


    public void initialize() throws SQLException {
        //Creamos la conexion a la BBDD y creamos el Repositorio de Usuarios
        connection = ConnectionUtil.getConnection();
        usuariosRepository = new UsuariosRepository(connection);

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
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

    }

    //Metodo que envia al inicio de sesion
    private void formIniciarSesion() {
        try{
            FXMLLoader formLoader = new FXMLLoader(getClass().getResource("login-vista.fxml"));
            AnchorPane form = formLoader.load();
            Scene formScene = new Scene(form);

            //Recuperamos y mostramos la vista registro
            Stage stage = (Stage) buttonVolver.getScene().getWindow();
            stage.setScene(formScene);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //Metodo que manejara los datos del registro, comprueba en la base de datos por coincidencias.
    //Y almacenara el usuario nuevo.
    private void handleRegister() throws SQLException, NoSuchAlgorithmException {

        Usuario nuevoUsuario = new Usuario();

        // Comprobar nombre de usuario único
        String nombreUsuario = usernameField.getText();
        if (usuariosRepository.isNombreUsuarioExists(nombreUsuario)) {
            // El nombre de usuario ya existe, muestra un mensaje de error o lanza una excepción
            // TODO: Mostrar mensaje de error o lanzar excepción
            mostrarMensajeError("El nombre de usuario ya existe.");
            return;
        }
        nuevoUsuario.setNombreUsuario(nombreUsuario);

        // Comprobar correo electrónico único
        String correo = correoField.getText();
        if (usuariosRepository.isCorreoExists(correo)) {
            // El correo electrónico ya existe, muestra un mensaje de error o lanza una excepción
            // TODO: Mostrar mensaje de error o lanzar excepción
            mostrarMensajeError("El correo electronico ya existe");
            return;
        }
        //Set correo electronico
        nuevoUsuario.setCorreo(correo);

        //Comprobar contraseñas concuerdan
        String pass1 = passwordField.getText();
        String pass2 = confirmPasswordField.getText();

        if(pass1.equals(pass2)){
            String password = passwordField.getText();
            byte[] salt = PasswordUtil.generateSalt();
            byte[] hashedPassword = PasswordUtil.getHashedPassword(password, salt);
            String hashStr = PasswordUtil.bytesToHex(hashedPassword); // Convertir a representación hexadecimal
            String saltStr = PasswordUtil.bytesToHex(salt); // Convertir a representación hexadecimal

            // Guardar hashStr y salt en la base de datos para el nuevo usuario
            nuevoUsuario.setContraseña(hashStr);
            nuevoUsuario.setSalt(saltStr);
        } else {
            mostrarMensajeError("Las contraseñas no coinciden");
            return;
        }

        //Validar campos vacios
        if(nombreUsuario.isEmpty()||correo.isEmpty()||pass1.isEmpty()||pass2.isEmpty()){
            mostrarMensajeError("Debe rellenar todos los campos");
            return;
        }else{
            //Guardamos el nuevo usuario
            usuariosRepository.save(nuevoUsuario);

            //Valoracion por defecto
            Calificacion calificacionDefault = new Calificacion();
            calificacionDefault.setIdUser(nuevoUsuario.getId());
            calificacionDefault.setComentario("Nuevo usuario registrado");
            calificacionDefault.setFechaCalif(new Date());
            calificacionDefault.setValoracion(8.0);//valoracion por defecto

            calificacionRepository.guardarCalificacion(calificacionDefault);

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


}
