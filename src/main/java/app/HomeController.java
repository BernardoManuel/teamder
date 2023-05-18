package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import listeners.FriendshipRequestListener;
import model.Friendship;
import model.Request;
import model.Room;
import model.User;
import repository.FriendshipRepository;
import repository.RequestRepository;
import repository.RoomRepository;
import repository.UserRepository;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class HomeController {

    @FXML
    public ScrollPane friendshipsListContainer;
    @FXML
    private BorderPane homeView;
    @FXML
    private VBox chatsList;
    @FXML
    private Text userLogged;
    @FXML
    private Button logoutButton;
    private VBox friendshipsList;
    private User user;
    private ChatController currentChatController;
    private RoomRepository roomRepository;
    private UserRepository userRepository;
    private FriendshipRepository friendshipRepository;
    private RequestRepository requestRepository;
    private ScheduledExecutorService executorService;



    @FXML
    public void initialize() {
        roomRepository = new RoomRepository();
        userRepository = new UserRepository();
        friendshipRepository = new FriendshipRepository();
        requestRepository = new RequestRepository();

        friendshipsList = new VBox();

        Platform.runLater(() -> {
            homeView.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
            try {
                generateHome();
                updateChatsList();
                updateFriendshipsList();
                startListenForFriendships(user);
                startListenForRequests(user);

                // Obtener el Stage principal
                Stage primaryStage = (Stage) homeView.getScene().getWindow();

                // Agregar controlador de eventos para cerrar la aplicación
                primaryStage.setOnCloseRequest(event -> {
                    // Detener los hilos y cerrar la aplicación
                    stopAll();
                    // Detenemos todos los hilos y flujos de datos en el controller del chat.
                    if(currentChatController!=null){
                        try {
                            currentChatController.closeApplication();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Platform.exit();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        logoutButton.setOnAction(actionEvent -> {
            try {
                handleLogoutButtonAction();
            } catch (IOException e) {
                throw new RuntimeException(e);
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
        Platform.runLater(() -> {
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(this::updateFriendships, 0, 2, TimeUnit.SECONDS);
        });
    }

    private void updateFriendships() {
        Platform.runLater(() -> {
        Set<Friendship> friends = friendshipRepository.getFriendships(user);
        if (friends != null && friends.size() > 0) {
            // Ordenar los amigos por nombre de usuario
            List<Friendship> sortedFriends = friends.stream()
                    .sorted(Comparator.comparing(f -> f.getAmigo2().getNombreUsuario()))
                    .collect(Collectors.toList());

                friendshipsList.getChildren().removeAll();
                friendshipsList.getChildren().clear();
                for (Friendship friendship : sortedFriends) {
                    if (friendship.getSolicitud().equals("aceptado")) {
                        createFriendshipItem(friendship);
                    }
                }
                friendshipsListContainer.setContent(friendshipsList);
        }
        });
    }

    public void stopUpdateFriendshipsList() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }


    public void createFriendshipItem(Friendship f) {
        Platform.runLater(() -> {
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
        });
    }

    public void removeUserFromFriendship(Friendship f) {
        Platform.runLater(() -> {
            Set<Friendship> friendshipSet = f.getAmigo2().getAmistades();
            Friendship friendshipToDelete = new Friendship();
            for (Friendship fs : friendshipSet) {
                if (fs.getAmigo2().getId() == user.getId()) {
                    friendshipToDelete = fs;
                }
            }
            friendshipRepository.deleteFriendship(f);
            friendshipRepository.deleteFriendship(friendshipToDelete);

            // Eliminar el elemento correspondiente de la lista
            HBox itemToRemove = null;
            for (Node node : friendshipsList.getChildren()) {
                if (node instanceof HBox) {
                    HBox userItem = (HBox) node;
                    Friendship friendship = (Friendship) userItem.getUserData();
                    if (friendship != null && friendship.equals(f)) {
                        itemToRemove = userItem;
                        break;
                    }
                }
            }
            if (itemToRemove != null) {
                friendshipsList.getChildren().remove(itemToRemove);
            }
        });
    }


    public void updateUser() {
        this.user = userRepository.updateUser(user);
    }

    public void startListenForFriendships(User usuario) {
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            List<Friendship> pendingFriendRequests = friendshipRepository.getPendingFriendRequests(usuario);
            // Mostrar las solicitudes no mostradas
            for (Friendship friendRequest : pendingFriendRequests) {
                Platform.runLater(() -> onFriendRequestReceived(usuario, friendRequest));
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stopListeningForFriendships() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }

    public void onFriendRequestReceived(User usuario, Friendship friendRequest) {
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

                    // Creamos la amistad aceptada para el cliente que recibe la solicitud.
                    Friendship friendship = new Friendship();
                    friendship.setAmigo1(usuario);
                    friendship.setAmigo2(friendRequest.getAmigo1());
                    friendship.setSolicitud("aceptado");
                    friendship.setShown(true);

                    friendshipRepository.saveFriendship(friendship);
                    //Actualizamos la lista de amistades
                    updateFriendshipsList();
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

    public void startListenForRequests(User usuario) {
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            List<Request> pendingRequests = requestRepository.getPendingRequests(usuario);
            // Mostrar las solicitudes no mostradas
            for (Request request : pendingRequests) {
                Platform.runLater(() -> onRequestReceived(usuario, request));
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stopListeningForRequests() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }

    public void onRequestReceived(User usuario, Request request) {
        User solicitante = request.getSolicitante();
        // Verificar si requester no es nulo antes de usarlo
        if (solicitante != null) {
            System.out.println(solicitante.getNombreUsuario()+" le ha invitado a unirse a su sala: "+request.getSala().getNombre());
            // Mostrar el diálogo Alert en el hilo principal de JavaFX
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Invitación");
                alert.setHeaderText(solicitante.getNombreUsuario() + " le ha invitado a unirse a su sala: \"+request.getSala().getNombre()");
                alert.setContentText("¿Aceptas unirte a la sala?");

                ButtonType acceptButton = new ButtonType("Aceptar");
                ButtonType rejectButton = new ButtonType("Rechazar");
                ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(acceptButton, rejectButton, cancelButton);

                request.setShown(true);
                requestRepository.updateRequestStatus(request);

                // Obtener la ventana actual
                Window currentWindow = homeView.getScene().getWindow();
                // Establecer la ventana actual como propietario de la alerta
                alert.initOwner(currentWindow);

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == acceptButton) {
                    // Aceptar la solicitud y guardar en la base de datos
                    request.setEstado("aceptado");
                    requestRepository.updateRequestStatus(request);

                    // Agregamos el usuario a la sala
                    roomRepository.addUser(request.getSala(), request.getSolicitado().getId());

                    try {
                        updateChatsList();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else if (result.isPresent() && result.get() == rejectButton) {
                    // Rechazar la solicitud y guardar en la base de datos
                    request.setEstado("rechazado");
                    requestRepository.updateRequestStatus(request);
                } else {
                    // Cancelar la solicitud
                    request.setEstado("cancelado");
                    requestRepository.updateRequestStatus(request);
                }
            });
        }
    }

    private void handleLogoutButtonAction() throws IOException {
        // Cerrar la sesión actual y volver a la pantalla de inicio de sesión

        // Limpia el usuario actual
        FormController.currentUser = null;

        // Detener los hilos y esperar a que finalicen
        stopAll();
        // Detenemos todos los hilos y flujos de datos en el controller del chat.
        if(currentChatController!=null){
            currentChatController.closeApplication();
        }

        // Cambiar a la vista de inicio de sesión
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml")); // Asegúrate de que esta ruta es correcta
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopAll(){
        stopUpdateFriendshipsList();
        stopListeningForFriendships();
        stopListeningForRequests();
    }

}

