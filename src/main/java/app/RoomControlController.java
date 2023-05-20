package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Window;
import model.Request;
import model.Room;
import model.User;
import repository.RequestRepository;
import repository.RoomRepository;
import repository.UserRepository;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class RoomControlController {

    @FXML
    public TextField inputUsername;
    @FXML
    public BorderPane usersListContainer;
    private VBox usersList;
    private BorderPane homeView;
    private BorderPane chatView;
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private Room room;
    private User user;
    private HomeController homeController;
    private Request request;

    public void initialize() {
        userRepository = new UserRepository();
        roomRepository = new RoomRepository();

        Platform.runLater(() -> {
            createUsersList();
        });
    }

    @FXML
    public void addUserToRoom() {

        RequestRepository requestRepository = new RequestRepository();
        User solicitado = userRepository.findUserByUsername(inputUsername.getText());

        // Comprobamos que los campos no esten vacios
        if (solicitado != null) {

            // Comprobamos si ya existe la amistad en la lista de amistades.
            Set<Request> requestSet = user.getRequests();
            Boolean alreadyRequested = false;
            for (Request r : requestSet) {
                if (solicitado.getNombreUsuario().equals(r.getSolicitado().getNombreUsuario())) {
                    alreadyRequested = true;
                    showError("Error", solicitado.getNombreUsuario() + " ya ha enviado una solicitud a este usuario.");
                }
            }
            updateUser();

            // Comprobamos que no se envia una solicitud al mismo usuario que la solicita.
            if (solicitado.getNombreUsuario().equals(user.getNombreUsuario().toString())) {
                showError("Error", "No puede enviar una solicitud a usted mismo.");

            } else {
                // Creamos la solicitud de amistad
                if (!alreadyRequested) {

                    request = new Request();
                    request.setSolicitante(user);
                    request.setSolicitado(solicitado);
                    request.setEstado("pendiente");
                    request.setSala(room);
                    request.setShown(false);

                    requestRepository.saveRequest(request);

                    showAlert("Éxito", "Se envió la solicitud a " + solicitado.getNombreUsuario());
                    inputUsername.clear();
                } else {
                    if (!alreadyRequested) {
                        showError("Error", "No se encontró el usuario con el nombre de usuario " + solicitado.getNombreUsuario());
                    }
                }
            }
        } else {
            showError("Error", "No se encontró ningun usuario.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        // Obtener la ventana actual
        Window currentWindow = inputUsername.getScene().getWindow();
        // Establecer la ventana actual como propietario de la alerta
        alert.initOwner(currentWindow);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        // Obtener la ventana actual
        Window currentWindow = inputUsername.getScene().getWindow();
        // Establecer la ventana actual como propietario de la alerta
        alert.initOwner(currentWindow);
        alert.showAndWait();
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

            for (User u : users) {
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

        // Crea un ImageView con la imagen
        ImageView imgView = new ImageView(new Image("file:src/main/resources/icons/friend_icon2.png"));
        imgView.setFitWidth(40.0);
        imgView.setFitHeight(40.0);

        // Crea un StackPane para superponer el ImageView sobre el círculo
        StackPane stackPane = new StackPane(imgUser, imgView);

        HBox labelUserContainer = new HBox();
        Label labelUser = new Label();
        labelUser.setText(u.getNombreUsuario());

        labelUser.setFont(Font.font("System", FontWeight.BOLD, 14));
        labelUser.setTextFill(Color.WHITE);

        labelUserContainer.getChildren().add(labelUser);
        labelUserContainer.setAlignment(Pos.CENTER_LEFT);
        labelUserContainer.setPadding(new Insets(0, 0, 0, 10));
        HBox.setHgrow(labelUserContainer, Priority.ALWAYS);

        userItem.getChildren().add(stackPane);
        userItem.getChildren().add(labelUserContainer);
        if (!u.getId().equals(user.getId())) {
            Button btnRemove = new Button("Borrar");
            btnRemove.setStyle("-fx-background-color: #e75334");
            btnRemove.setFont(Font.font("System", FontWeight.BOLD, 13));
            btnRemove.setTextFill(Color.WHITE);
            btnRemove.setOnMouseClicked(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Sacar amigo de la sala");
                alert.setHeaderText("Desea sacar a " + u.getNombreUsuario() + " de la sala?");
                alert.setContentText("Pulse Aceptar sacar amigo de la sala");

                ButtonType acceptButton = new ButtonType("Aceptar");
                ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(acceptButton, cancelButton);

                // Obtener la ventana actual
                Window currentWindow = inputUsername.getScene().getWindow();
                // Establecer la ventana actual como propietario de la alerta
                alert.initOwner(currentWindow);

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == acceptButton) {
                    removeUserFromRoom(u);
                }

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

    private void updateUser() {
        user = userRepository.updateUser(user);
    }
}
