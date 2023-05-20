package app;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Window;
import model.Friendship;
import model.User;
import repository.FriendshipRepository;

import java.util.Optional;
import java.util.Set;

public class UserItem {

    private HBox userItem;
    private User user;
    private Friendship friendship;
    private VBox parentContainer;

    public UserItem(User user, Friendship friendship, VBox parentContainer) {
        this.user = user;
        this.friendship = friendship;
        this.parentContainer = parentContainer;
    }


    public void generateUserItem() {
        userItem = new HBox();
        userItem.setAlignment(Pos.CENTER);
        userItem.setSpacing(10.0);

        // Crea un círculo en lugar de un Pane
        Circle imgUser = new Circle();
        imgUser.setRadius(20.0);
        imgUser.setFill(javafx.scene.paint.Color.web("#f8efad"));

        // Crea un ImageView con la imagen
        ImageView imgView = new ImageView(new Image("file:src/main/resources/icons/friend_icon2.png"));
        imgView.setFitWidth(40.0);
        imgView.setFitHeight(40.0);

        // Crea un StackPane para superponer el ImageView sobre el círculo
        StackPane stackPane = new StackPane(imgUser, imgView);

        HBox labelUserContainer = new HBox();
        Label labelUser = new Label();
        labelUser.setText(friendship.getAmigo2().getNombreUsuario());

        labelUser.setFont(javafx.scene.text.Font.font("System", FontWeight.BOLD, 14));
        labelUser.setTextFill(javafx.scene.paint.Color.BLACK);

        labelUserContainer.getChildren().add(labelUser);
        labelUserContainer.setAlignment(Pos.CENTER_LEFT);
        labelUserContainer.setPadding(new Insets(0, 0, 0, 10));
        HBox.setHgrow(labelUserContainer, Priority.ALWAYS);

        userItem.getChildren().add(stackPane);
        userItem.getChildren().add(labelUserContainer);

        Button btnRemove = new Button("Borrar");
        btnRemove.setStyle("-fx-background-color: #e75334");
        btnRemove.setFont(Font.font("System", FontWeight.BOLD, 13));
        btnRemove.setTextFill(Color.WHITE);

        btnRemove.setOnMouseClicked(event -> {
            removeUserFromFriendship(friendship);
            // Eliminar el VBox del padre
        });

        userItem.getChildren().add(btnRemove);
    }


    // Cuadro de dialogo cuando se presiona el boton de borrar amistad.
    public void removeUserFromFriendship(Friendship f) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Amistad");
        alert.setHeaderText("Desea eliminar la amistad con " + f.getAmigo2().getNombreUsuario() + "?");
        alert.setContentText("Pulse Aceptar para eliminar la amistad");

        ButtonType acceptButton = new ButtonType("Aceptar");
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(acceptButton, cancelButton);

        // Obtener la ventana actual
        Window currentWindow = parentContainer.getScene().getWindow();
        // Establecer la ventana actual como propietario de la alerta
        alert.initOwner(currentWindow);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == acceptButton) {
            Platform.runLater(() -> {
                FriendshipRepository friendshipRepository = new FriendshipRepository();

                Set<Friendship> friendshipSet = f.getAmigo2().getAmistades();
                Friendship friendshipToDelete = new Friendship();
                for (Friendship fs : friendshipSet) {
                    if (fs.getAmigo2().getId() == user.getId()) {
                        friendshipToDelete = fs;
                    }
                }
                f.setSolicitud("eliminado");
                friendshipRepository.updateFriendshipStatus(f);
                friendshipToDelete.setSolicitud("eliminado");
                friendshipRepository.updateFriendshipStatus(friendshipToDelete);

            });
            parentContainer.getChildren().remove(userItem);
            // Eliminar el VBox del padre
        }

    }


    public HBox getUserItem() {
        return userItem;
    }

    public void setUserItem(HBox userItem) {
        this.userItem = userItem;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Friendship getFriendship() {
        return friendship;
    }

    public void setFriendship(Friendship friendship) {
        this.friendship = friendship;
    }
}
