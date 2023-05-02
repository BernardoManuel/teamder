package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private Button btnCreate;
    @FXML
    private TextField inputRoomName;
    @FXML
    private TextField inputMaxPlayers;

    public void initialize() {
        gamesRepository = new GamesRepository();
        roomRepository = new RoomRepository();

        Platform.runLater(() -> {
            createGamesList();
        });
    }

    public void createGamesList() {
        List<Game> games = gamesRepository.findAllGames();
        if (games != null) {
            for (Game game : games) {
                gameSelector.getItems().add(game.getName());
            }
            gameSelector.setValue(games.get(0).getName());
        }
    }

    @FXML
    private void createRoom() {
        try {
            Room room = new Room();
            room.setNombre(inputRoomName.getText());
            room.setMax_jugadores(Integer.parseInt(inputMaxPlayers.getText().trim()));
            room.setId_creador(user.getId());
            room.setGame(gamesRepository.getGameByName((String) gameSelector.getValue()));
            roomRepository.save(room, user);
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

    private void cleanInputs() {
        inputRoomName.setText("");
        inputMaxPlayers.setText("");
    }
}
