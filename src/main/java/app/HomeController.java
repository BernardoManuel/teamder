package app;

import database.HibernateUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import model.Friendship;
import model.Room;
import model.User;
import org.hibernate.Session;
import repository.FriendshipRepository;
import repository.RoomRepository;
import repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class HomeController {

    @FXML
    private BorderPane homeView;
    @FXML
    private VBox chatsList;
    @FXML
    private Text userLogged;

    private User user;
    private ChatController currentChatController;
    private RoomRepository roomRepository;
    private UserRepository userRepository;
    private FriendshipRepository friendshipRepository;


    @FXML
    public void initialize() {
        roomRepository = new RoomRepository();
        userRepository = new UserRepository();
        friendshipRepository = new FriendshipRepository();

        Platform.runLater(() -> {
            homeView.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
            try {
                generateHome();
                updateChatsList();
                solicitudes(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void generateHome() throws IOException {
        userLogged.setText(user.getNombreUsuario());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("placeholder-view.fxml"));
        homeView.setCenter(loader.load());
    }

    public void placePlaceholder() throws IOException {
        closeCurrentChat();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("placeholder-view.fxml"));
        homeView.setCenter(loader.load());
        updateChatsList();
    }

    public Parent getChatView(Room room) throws IOException {
        closeCurrentChat();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
        Parent root = loader.load();
        ((ChatController) loader.getController()).setUser(user);
        ((ChatController) loader.getController()).setRoom(room);
        ((ChatController) loader.getController()).setHomeView(homeView);
        ((ChatController) loader.getController()).setHomeController(this);
        currentChatController = loader.getController();

        return root;
    }

    public void updateChatsList() throws IOException {
        updateUser();
        Set<Room> rooms = user.getRooms();

        List<HBox> roomsItems = new ArrayList<>();
        if (rooms != null && !rooms.isEmpty()) {
            roomsItems = new ArrayList<>();
            for (Room room : rooms) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-item-view.fxml"));
                HBox item = loader.load();
                ((ChatItemController) loader.getController()).setTitle(room.getNombre());

                roomsItems.add(item);
                Node view = item.getChildren().get(0).getParent();
                view.setOnMouseClicked(event -> {
                    try {
                        homeView.setCenter(getChatView(room));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        chatsList.getChildren().setAll(roomsItems);
    }

    public void setUsername(User user) {
        this.user = user;
    }


    @FXML
    private void openRoomCreator() throws IOException {
        closeCurrentChat();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("room-creator-view.fxml"));
        Parent root = loader.load();
        ((RoomCreatorController) loader.getController()).setUser(user);
        ((RoomCreatorController) loader.getController()).setHomeController(this);
        homeView.setCenter(root);
    }

    @FXML
    private void openFriendsView() throws IOException {
        closeCurrentChat();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("friend-view.fxml"));
        Parent root = loader.load();
        ((FriendsController) loader.getController()).setCurrentUser(user);
        homeView.setCenter(root);
    }

    private void closeWindowEvent(WindowEvent event) {
        closeCurrentChat();
    }

    private void closeCurrentChat() {
        if (currentChatController != null) {
            currentChatController.closeEverything();
            currentChatController = null;
        }
    }

    @FXML
    private TableColumn<Friendship, String> solicitanteColumn;
    @FXML
    private TableColumn<Friendship, String> accionColumn;
    @FXML
    private TableView<Friendship> friendRequestsTable;


    private void solicitudes(User usuario) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        FriendshipRepository friendshipRepository = new FriendshipRepository();
        List<Friendship> pendingFriendRequests = friendshipRepository.getPendingFriendRequests(usuario);

        System.out.println("Hay " + pendingFriendRequests.size() + " solicitudes de amistad pendientes para el usuario: " + usuario.getNombreUsuario());

        for (Friendship friendRequest : pendingFriendRequests) {
            User requester = friendRequest.getAmigo1();

            // Verifica si requester no es nulo antes de usarlo
            if (requester != null) {
                System.out.println("Solicitud de amistad encontrada de " + requester.getNombreUsuario());

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Solicitud de amistad");
                alert.setHeaderText(requester.getNombreUsuario() + " quiere ser tu amigo.");
                alert.setContentText("¿Aceptas la solicitud de amistad?");

                ButtonType acceptButton = new ButtonType("Aceptar");
                ButtonType rejectButton = new ButtonType("Rechazar");
                ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(acceptButton, rejectButton, cancelButton);

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == acceptButton) {
                    // Aceptar la solicitud y guardar en la base de datos
                    friendRequest.setSolicitud("aceptado");
                    friendshipRepository.updateFriendshipStatus(friendRequest);
                } else if (result.isPresent() && result.get() == rejectButton) {
                    // Rechazar la solicitud y eliminar de la base de datos
                    friendshipRepository.deleteFriendship(friendRequest);
                }
            }
        }
        session.close();
    }


    public void updateUser() {
        this.user = userRepository.updateUser(user);
    }
}

