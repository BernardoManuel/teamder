package app;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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
import model.Usuario;
import repository.RoomRepository;
import utils.ConnectionUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class HomeController {

    @FXML private BorderPane homeView;
    @FXML private VBox chatsList;
    @FXML private Text userLogged;

    private Usuario user;
    private ChatController currentChatController;

    private RoomRepository roomRepository;
    private Connection connection;

    @FXML
    public void initialize() throws SQLException {

        //Utilizamos el util de conexion para crear una conexion a nuestra BBDD
        connection = ConnectionUtil.getConnection();
        roomRepository = new RoomRepository(connection);

        Platform.runLater(() -> {
            homeView.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
            try {
                generateHome();
                generateChatsList();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

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

    private void generateHome() throws IOException {
        userLogged.setText(user.getNombreUsuario());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("placeholder-view.fxml"));
        homeView.setCenter(loader.load());
    }

    public void generateChatsList() throws SQLException, IOException {
        ObservableList<Room> rooms = roomRepository.findUserRooms(user.getId());
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

    public void addNewRoomToChatsList(Room room) throws IOException {
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

    public void setUsername(Usuario user) {
        this.user = user;
    }

    private void closeWindowEvent(WindowEvent event) {
        closeCurrentChat();
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
    private void closeCurrentChat() {
        if (currentChatController != null) {
            currentChatController.closeEverything();
            currentChatController = null;
        }
    }
}

