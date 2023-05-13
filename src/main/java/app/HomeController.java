package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import listeners.FriendshipRequestListener;
import model.Friendship;
import model.Room;
import model.User;
import repository.FriendshipRepository;
import repository.RoomRepository;
import repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class HomeController implements FriendshipRequestListener {

    @FXML
    public ScrollPane friendshipsListContainer;
    @FXML
    private BorderPane homeView;
    @FXML
    private VBox chatsList;
    @FXML
    private Text userLogged;
    private VBox friendshipsList;
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
                updateFriendshipsList();
                startListeningForRequests(user);
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
        updateFriendshipsList();
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


    public void updateFriendshipsList() {
        new Thread(() -> {
            while (true) {
                try {
                    friendshipsList = new VBox();
                    Set<Friendship> friends = friendshipRepository.getFriendships(user);
                    if (friends != null && friends.size() > 0) {
                        for (Friendship friendship : friends) {
                            createFriendshipItem(friendship);
                        }
                    }
                    friendshipsListContainer.setContent(friendshipsList);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void createFriendshipItem(Friendship f) {
        HBox userItem = new HBox();
        userItem.setAlignment(Pos.CENTER);
        userItem.setSpacing(10.0);

        // Crea un círculo en lugar de un Pane
        Circle imgUser = new Circle();
        imgUser.setRadius(20.0);
        imgUser.setFill(javafx.scene.paint.Color.web("#f8efad"));

        HBox labelUserContainer = new HBox();
        Label labelUser = new Label();
        labelUser.setText(f.getAmigo2().getNombreUsuario());


        labelUser.setFont(javafx.scene.text.Font.font("System", FontWeight.BOLD, 14));
        labelUser.setTextFill(javafx.scene.paint.Color.BLACK);

        labelUserContainer.getChildren().add(labelUser);
        labelUserContainer.setAlignment(Pos.CENTER_LEFT);
        labelUserContainer.setPadding(new Insets(0, 0, 0, 10));
        HBox.setHgrow(labelUserContainer, Priority.ALWAYS);

        userItem.getChildren().add(imgUser);
        userItem.getChildren().add(labelUserContainer);
        Button btnRemove = new Button("Borrar");
        btnRemove.setStyle("-fx-background-color: #e75334");
        btnRemove.setFont(Font.font("System", FontWeight.BOLD, 13));
        btnRemove.setTextFill(Color.WHITE);
        btnRemove.setOnMouseClicked(event -> {
            removeUserFromFriendship(f);
        });
        userItem.getChildren().add(btnRemove);

        friendshipsList.getChildren().add(userItem);
    }

    public void removeUserFromFriendship(Friendship f) {
        friendshipRepository.deleteFriendship(f);
        updateFriendshipsList();
    }


    public void updateUser() {
        this.user = userRepository.updateUser(user);
    }


    // Hilo del escuchador de solicitudes de amistades
    public void startListeningForRequests(User usuario) {
        new Thread(() -> {
            while (true) {
                List<Friendship> pendingFriendRequests = friendshipRepository.getPendingFriendRequests(usuario);
                // Mostrar las solicitudes no mostradas
                for (Friendship friendRequest : pendingFriendRequests) {
                    onRequestReceived(usuario, friendRequest);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Implementación del escuchador
    @Override
    public void onRequestReceived(User usuario, Friendship friendRequest) {
        User requester = friendRequest.getAmigo1();
        // Verificar si requester no es nulo antes de usarlo
        if (requester != null) {
            System.out.println("Solicitud de amistad encontrada de " + requester.getNombreUsuario());
            // Mostrar el diálogo Alert en el hilo principal de JavaFX
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Solicitud de amistad");
                alert.setHeaderText(requester.getNombreUsuario() + " quiere ser tu amigo.");
                alert.setContentText("¿Aceptas la solicitud de amistad?");

                ButtonType acceptButton = new ButtonType("Aceptar");
                ButtonType rejectButton = new ButtonType("Rechazar");
                ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(acceptButton, rejectButton, cancelButton);

                friendRequest.setShown(true);
                friendshipRepository.updateFriendshipStatus(friendRequest);

                // Obtener la ventana actual
                Window currentWindow = homeView.getScene().getWindow();
                // Establecer la ventana actual como propietario de la alerta
                alert.initOwner(currentWindow);

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == acceptButton) {
                    // Aceptar la solicitud y guardar en la base de datos
                    friendRequest.setSolicitud("aceptado");
                    friendshipRepository.updateFriendshipStatus(friendRequest);
                } else if (result.isPresent() && result.get() == rejectButton) {
                    // Rechazar la solicitud y guardar en la base de datos
                    friendRequest.setSolicitud("rechazado");
                    friendshipRepository.updateFriendshipStatus(friendRequest);
                } else {
                    // Cancelar la solicitud
                    friendshipRepository.updateFriendshipStatus(friendRequest);
                }
            });
        }
    }




}

