package app;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import model.Game;
import model.Room;
import model.User;
import repository.GamesRepository;
import repository.RoomRepository;
import utils.ConnectionUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class RoomCreatorController {

    private HomeController homeController;
    private GamesRepository gamesRepository;
    private RoomRepository roomRepository;
    private Connection connection;
    private User user;

    @FXML
    private ChoiceBox gameSelector;
    @FXML
    private Button btnCreate;
    @FXML
    private TextField inputRoomName;
    @FXML
    private TextField inputMaxPlayers;

    public void initialize() throws SQLException {

        connection = ConnectionUtil.getConnection();
        gamesRepository = new GamesRepository(connection);
        roomRepository = new RoomRepository(connection);

        Platform.runLater(() -> {
            createGamesList();
        });
    }

    public void createGamesList() {
        try {
            ObservableList<Game> games = gamesRepository.findAllGames();

            for (Game game : games) {
                gameSelector.getItems().add(game.getName());
            }

            gameSelector.setValue(games.get(0).getName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void createRoom() {
        try {
            Room room = new Room();

            room.setId_juego(gamesRepository.getGameByName((String) gameSelector.getValue()));
            room.setNombre(inputRoomName.getText());
            room.setMax_jugadores(Integer.parseInt(inputMaxPlayers.getText().trim()));
            room.setId_creador(user.getId());

            roomRepository.save(room, user);
            homeController.addNewRoomToChatsList(room);
            cleanInputs();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void setUser(User user) {
        this.user = user;
    }
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    private void cleanInputs() {
        inputRoomName.setText("");
        inputMaxPlayers.setText("");
    }
}
