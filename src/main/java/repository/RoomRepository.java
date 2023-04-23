package repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
        String query = "SELECT * FROM `salas` WHERE id_salas = (SELECT id_salas from relacion_user_salas where " +
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
}
