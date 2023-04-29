package repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Message;
import model.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomRepository {

    private Connection connection;

    public RoomRepository(Connection connection) {
        this.connection = connection;
    }

    public ObservableList<Room> findUserRooms(Integer id_user) throws SQLException {
        ObservableList<Room> rooms = FXCollections.observableArrayList();
        String query = "SELECT * FROM `salas` WHERE id_salas IN (SELECT id_sala from relacion_user_salas where " +
                "id_user = ?);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id_user);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Room room = new Room();
                    room.setId(resultSet.getInt("id_salas"));
                    room.setId_juego(resultSet.getInt("id_juego"));
                    room.setNombre(resultSet.getString("nombre"));
                    room.setMax_jugadores(resultSet.getInt("max_jugadores"));
                    room.setMax_jugadores(resultSet.getInt("creador"));

                    // Agrega más atributos de la entidad Room según tu base de datos
                    rooms.add(room);
                }
            }
        }
        return rooms;
    }

    public void save(Room room) throws SQLException {
        String generatedColumns[] = { "id_salas" };
        String query = "INSERT INTO salas (id_juego, nombre, max_jugadores, creador) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, generatedColumns)) {

            statement.setInt(1, room.getId_juego());
            statement.setString(2, room.getNombre());
            statement.setInt(3, room.getMax_jugadores());
            statement.setInt(4, room.getId_creador());

            // Configura más parámetros del statement según tu base de datos y entidad Room
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    room.setId(rs.getInt(1));
                    createRelationRoomToUser(room);
                }
            }
        }
    }

    public void createRelationRoomToUser(Room room) throws SQLException {
        String query = "INSERT INTO relacion_user_salas (id_user, id_sala) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, room.getId_creador());
            statement.setInt(2, room.getId());

            statement.executeUpdate();
        }
    }
}
