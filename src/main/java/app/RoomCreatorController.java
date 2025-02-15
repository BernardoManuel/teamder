package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import model.Game;
import model.Room;
import model.User;
import repository.GamesRepository;
import repository.RoomRepository;
import java.io.IOException;
import java.util.List;

public class RoomCreatorController {

    private HomeController homeController;
    private GamesRepository gamesRepository;
    private RoomRepository roomRepository;
    private User user;

    @FXML
    private ChoiceBox gameSelector;
    @FXML
    private TextField inputRoomName;
    @FXML
    private TextField inputMaxPlayers;

    // Interfaz para crear la sala a tu gusto.
    public void initialize() {
        gamesRepository = new GamesRepository();
        roomRepository = new RoomRepository();

        Platform.runLater(() -> {
            createGamesList();
        });
    }

    /**
     * Metodo que itera y crea la lista de juegos segun los registros de juegos en la base de datos.
     */
    public void createGamesList() {
        List<Game> games = gamesRepository.findAllGames();
        if (games != null) {
            for (Game game : games) {
                gameSelector.getItems().add(game.getName());
            }
            gameSelector.setValue(games.get(0).getName());
        }
    }

    /**
     * Metodo que recoge los datos introducidos y crea la sala correspondiente.
     */
    @FXML
    private void createRoom() {
        try {
            Room room = new Room();
            room.setNombre(inputRoomName.getText());
            room.setMax_jugadores(Integer.parseInt(inputMaxPlayers.getText().trim()));
            room.setCreador(user);
            room.setGame(gamesRepository.getGameByName((String) gameSelector.getValue()));
            //guarda la sala en la base de datos.
            roomRepository.save(room, user);
            //Actualiza la lista de salas del homeController
            homeController.updateChatsList();
            cleanInputs();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * Metodo que limpia los campos del formulario de creacion de sala.
     */
    private void cleanInputs() {
        inputRoomName.setText("");
        inputMaxPlayers.setText("");
    }
}
