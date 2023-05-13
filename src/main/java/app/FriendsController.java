package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Friendship;
import model.User;
import database.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import repository.FriendshipRepository;
import repository.UserRepository;

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
        if (!friendUsername.isEmpty()) {
            UserRepository userRepository = new UserRepository();
            User friend = userRepository.findUserByUsername(friendUsername);
            if (friend != null) {
                Friendship friendship = new Friendship();
                friendship.setAmigo1(currentUser);
                friendship.setAmigo2(friend);
                friendship.setSolicitud("pendiente");
                friendship.setShown(false);

                FriendshipRepository friendshipRepository = new FriendshipRepository();
                friendshipRepository.saveFriendship(friendship);
                showAlert("Éxito", "Se envió la solicitud de amistad a " + friendUsername);
            } else {
                showAlert("Error", "No se encontró el usuario con el nombre de usuario " + friendUsername);
            }
        } else {
            showAlert("Error", "Por favor, introduce un nombre de usuario");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
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
