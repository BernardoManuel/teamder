package repository;

import database.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Message;
import model.Room;
import model.User;
import org.hibernate.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class RoomRepository {
    private Connection connection;
    private Session session;

    public RoomRepository() {
    }

    public RoomRepository(Connection connection) {
        this.connection = connection;
    }

    public Set<Room> findUserRooms(User user) {
        Set<Room> rooms = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            User tmpUser = session.get(User.class, user.getId());
            rooms = tmpUser.getRooms();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            session.getTransaction().commit();
            session.close();
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
