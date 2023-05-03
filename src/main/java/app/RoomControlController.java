package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Room;
import model.User;
import repository.RoomRepository;
import repository.UserRepository;

import java.io.IOException;
import java.util.Set;

public class RoomControlController {

    @FXML public TextField inputUsername;
    @FXML public BorderPane usersListContainer;
    private VBox usersList;
    private BorderPane homeView;
    private BorderPane chatView;
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private Room room;
    private User user;
    private HomeController homeController;

    public void initialize() {
        userRepository = new UserRepository();
        roomRepository = new RoomRepository();

        Platform.runLater(() -> {
            createUsersList();
        });
    }

    public void addUserToRoom() {
        User user = userRepository.findUserByUsername(inputUsername.getText());
        if (user != null) {
            roomRepository.addUser(room, user.getId());
            createUsersList();
        } else {
            System.out.println("El usuario no existe.");
        }
    }

    public void closeRoomControls() {
        homeView.setCenter(chatView);
    }

    public void setChatView(BorderPane chatView) {
        this.chatView = chatView;
    }

    public void setHomeView(BorderPane homeView) {
        this.homeView = homeView;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void createUsersList() {
        updateRoom();
        if (room != null) {
            Set<User> users = room.getUsers();

            usersList = new VBox();
            usersList.setSpacing(5.0);
            usersList.setAlignment(Pos.CENTER);
            usersListContainer.setCenter(usersList);

            for (User u: users) {
                createUserItem(u);
            }
        }
    }
    public void createUserItem(User u) {
        HBox userItem = new HBox();
        userItem.setAlignment(Pos.CENTER);
        userItem.setSpacing(10.0);

        // Crea un círculo en lugar de un Pane
        Circle imgUser = new Circle();
        imgUser.setRadius(20.0);
        imgUser.setFill(Color.web("#f8efad"));

        HBox labelUserContainer = new HBox();
        Label labelUser = new Label();
        labelUser.setText(u.getNombreUsuario());


        labelUser.setFont(Font.font("System", FontWeight.BOLD, 14));
        labelUser.setTextFill(Color.WHITE);

        labelUserContainer.getChildren().add(labelUser);
        labelUserContainer.setAlignment(Pos.CENTER_LEFT);
        labelUserContainer.setPadding(new Insets(0, 0, 0,10));
        HBox.setHgrow(labelUserContainer, Priority.ALWAYS);

        userItem.getChildren().add(imgUser);
        userItem.getChildren().add(labelUserContainer);
        if (!u.getId().equals(user.getId())) {
            Button btnRemove = new Button("Borrar");
            btnRemove.setStyle("-fx-background-color: #e75334");
            btnRemove.setFont(Font.font("System", FontWeight.BOLD, 13));
            btnRemove.setTextFill(Color.WHITE);
            btnRemove.setOnMouseClicked(event -> {
                removeUserFromRoom(u);
            });
            userItem.getChildren().add(btnRemove);
        }

        usersList.getChildren().add(userItem);
    }


    public void removeUserFromRoom(User user) {
        roomRepository.removeUser(room.getId(), user.getId());
        createUsersList();
    }

    public void updateRoom() {
        this.room = roomRepository.updateRoom(room);
    }

    public void leaveRoom() throws IOException {
        updateRoom();
        roomRepository.removeUser(room.getId(), user.getId());
        if (room.getUsers().size() == 0) {
            roomRepository.removeRoom(room.getId());
        }
        homeController.placePlaceholder();
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
}
