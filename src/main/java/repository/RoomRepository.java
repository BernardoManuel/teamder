package repository;

import database.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Message;
import model.Room;
import model.User;
import org.hibernate.Hibernate;
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
            Hibernate.initialize(user.getRooms());
            rooms = user.getRooms();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.getTransaction().commit();
            session.close();
        }

        return rooms;
    }

    public void save(Room room, User user) throws SQLException {
        try {

            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            user.getRooms().add(room);
            room.getUsers().add(user);

            session.merge(room);

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
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
