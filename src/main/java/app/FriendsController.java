package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import model.Friendship;
import model.User;
import database.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import repository.FriendshipRepository;
import repository.UserRepository;

import java.util.Set;

public class FriendsController {
    @FXML
    private TextField usernameTextField;

    @FXML
    private Button addFriendButton;

    private User currentUser;

    @FXML
    public void initialize() {

        Platform.runLater(() -> {
        });

    }

    public FriendsController() {

    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

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
            for (Friendship f : friendshipSet){
                if(friendUsername.equals(f.getAmigo2().getNombreUsuario().toString())){
                    alreadyFriends=true;
                    showError("Error",friendUsername+" ya está en su lista de amistades.");
                }
            }

            // Comprobamos que no se envia una solicitud al mismo usuario que la solicita.
            if(friendUsername.equals(currentUser.getNombreUsuario().toString())){
                showError("Error","No puede enviar una solicitud de amistad a usted mismo.");

            }else
                // Creamos la solicitud de amistad
                if (friend != null && !alreadyFriends) {
                Friendship friendship = new Friendship();
                friendship.setAmigo1(currentUser);
                friendship.setAmigo2(friend);
                friendship.setSolicitud("pendiente");
                friendship.setShown(false);

                friendshipRepository.saveFriendship(friendship);
                    showAlert("Éxito", "Se envió la solicitud de amistad a " + friendUsername);
            } else {
                    showError("Error", "No se encontró el usuario con el nombre de usuario " + friendUsername);
            }
        } else {
            showError("Error", "Por favor, introduce un nombre de usuario");
        }
    }



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


    private User findUserByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE nombreUsuario = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        }
    }

    private User getUserById(Session session, Integer id) {
        User user = null;
        try {
            user = session.get(User.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
