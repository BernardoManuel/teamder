package repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GamesRepository {

    private Connection connection;

    public GamesRepository(Connection connection) {
        this.connection = connection;
    }


    public ObservableList<Game> findAllGames() throws SQLException {
        ObservableList<Game> games = FXCollections.observableArrayList();
        String query = "SELECT * FROM `juegos`;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Game game = new Game();

                    game.setId(resultSet.getInt("cod_juego"));
                    game.setName(resultSet.getString("nom_juego"));
                    game.setDescripcion(resultSet.getString("descripcion"));
                    game.setGenero(resultSet.getString("genero"));

                    // Agrega más atributos de la entidad Game según tu base de datos
                    games.add(game);
                }
            }
        }
        return games;
    }

    public int getGameByName(String name) throws SQLException {
        String query = "select cod_juego from juegos where nom_juego = ?;";

        Game game = null;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    game = new Game();
                    game.setId(resultSet.getInt("cod_juego"));
                }
            }
        }

        return game.getId();
    }
}
