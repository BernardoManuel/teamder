package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import model.Friendship;
import model.User;
import repository.FriendshipRepository;
import repository.UserRepository;

import java.util.Set;

public class FriendsController {
    @FXML
    private TextField usernameTextField;
    private UserRepository userRepository;
    private User currentUser;

    @FXML
    public void initialize() {
        userRepository = new UserRepository();

        Platform.runLater(() -> {
        });

    }

    public FriendsController() {
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Metodo que maneja el evento del click del boton "Añadir" de la vista friend-view.fxml
     * Recupera los datos introducidos y maneja una serie de validaciones antes de enviar la solicitud de amistad.
     * Finalmente una vez pasadas las validaciones envia la solicitud de amistad
     */
    @FXML
    public void handleAddFriendButtonAction() {
        String friendUsername = usernameTextField.getText().trim();
        FriendshipRepository friendshipRepository = new FriendshipRepository();

        // Comprobamos que los campos no esten vacios
        if (!friendUsername.isEmpty()) {
            UserRepository userRepository = new UserRepository();
            User friend = userRepository.findUserByUsername(friendUsername);

            // Comprobamos si ya existe la amistad en la lista de amistades.
            Set<Friendship> friendshipSet = currentUser.getAmistades();
            Boolean alreadyFriends = false;
            Boolean pendingRequest = false;
            for (Friendship f : friendshipSet) {

                if (friendUsername.equals(f.getAmigo2().getNombreUsuario().toString())) {
                    if (f.getSolicitud().equals("pendiente")) {
                        pendingRequest = true;
                        showError("Error", "Ya has enviado una solicitud de amistad a " + friendUsername + ". Por favor, espera su respuesta.");
                    }

                    if (!f.getSolicitud().equals("eliminado") && !pendingRequest) {
                        alreadyFriends = true;
                        showError("Error", friendUsername + " ya está en su lista de amistades.");
                    }
                }
            }

            // Comprobamos que no se envia una solicitud al mismo usuario que la solicita.
            if (friendUsername.equals(currentUser.getNombreUsuario().toString())) {
                showError("Error", "No puede enviar una solicitud de amistad a usted mismo.");
            } else {
                // Creamos la solicitud de amistad
                if (friend != null && !alreadyFriends && !pendingRequest) {
                    Friendship friendship = new Friendship();
                    friendship.setAmigo1(currentUser);
                    friendship.setAmigo2(friend);
                    friendship.setSolicitud("pendiente");
                    friendship.setShown(false);

                    friendshipRepository.saveFriendship(friendship);
                    showAlert("Éxito", "Se envió la solicitud de amistad a " + friendUsername);
                } else {
                    if (!alreadyFriends && !pendingRequest) {
                        showError("Error", "No se encontró el usuario con el nombre de usuario " + friendUsername);
                    }
                }
            }
            updateUser();
        } else {
            showError("Error", "Por favor, introduce un nombre de usuario");
        }
    }


    /**
     * Metodo que muestra una alerta de confirmacion al usuario.
     * @param title titulo de la alerta de confirmacion.
     * @param content mensaje a mostrar en la alerta
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        // Obtener la ventana actual
        Window currentWindow = usernameTextField.getScene().getWindow();
        // Establecer la ventana actual como propietario de la alerta
        alert.initOwner(currentWindow);
        alert.showAndWait();
    }

    /**
     * Metodo que muestra una alerta de Error al usuario.
     * @param title titulo de la alerta de error.
     * @param content mensaje a mostrar en la alerta
     */
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        // Obtener la ventana actual
        Window currentWindow = usernameTextField.getScene().getWindow();
        // Establecer la ventana actual como propietario de la alerta
        alert.initOwner(currentWindow);
        alert.showAndWait();
    }

    private void updateUser() {
        this.currentUser = userRepository.updateUser(currentUser);
    }

}
