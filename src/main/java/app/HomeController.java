package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import model.Room;
import model.User;
import repository.RoomRepository;
import repository.UsuariosRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HomeController {

    @FXML private BorderPane homeView;
    @FXML private VBox chatsList;
    @FXML private Text userLogged;

    private User user;
    private ChatController currentChatController;
    private RoomRepository roomRepository;
    private UsuariosRepository userRepository;

    @FXML
    public void initialize(){
        roomRepository = new RoomRepository();
        userRepository = new UsuariosRepository();

        Platform.runLater(() -> {
            homeView.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
            try {
                generateHome();
                generateChatsList();
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

    public void generateChatsList() throws IOException {
        Set<Room> rooms = roomRepository.findUserRooms(user);
        if (rooms != null) {
            for (Room room : rooms) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-item-view.fxml"));
                HBox item = loader.load();

                ((ChatItemController) loader.getController()).setTitle(room.getNombre());

                chatsList.getChildren().add(item);
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
    }

    public void placePlaceholder() throws IOException {
        closeCurrentChat();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("placeholder-view.fxml"));
        homeView.setCenter(loader.load());
    }

    private Parent getChatView(Room room) throws IOException {
        closeCurrentChat();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
        Parent root = loader.load();
        ((ChatController) loader.getController()).setUser(user);
        ((ChatController) loader.getController()).setRoom(room);
        currentChatController = loader.getController();

        return root;
    }

    public void updateChatsList() throws IOException {
        updateUser();
        Set<Room> rooms = user.getRooms();

        if (rooms != null && !rooms.isEmpty()) {
            System.out.println(rooms.size());
            List<HBox> roomsItems = new ArrayList<>();
            for (Room room: rooms) {
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
            chatsList.getChildren().setAll(roomsItems);
        }
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
        homeView.setCenter(loader.load());
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

    public void updateUser() {
        this.user = userRepository.updateUser(user);
    }
}

