package repository;

import database.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Game;
import org.hibernate.Session;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    public Game getGameByName(String name) throws SQLException {
        Game game = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List<Game> gamesResult = session.createNativeQuery("SELECT * FROM juegos WHERE nom_juego = :pnom_juego", Game.class)
                    .setParameter("pnom_juego", name)
                    .list();
            game = gamesResult.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return game;
    }
}
