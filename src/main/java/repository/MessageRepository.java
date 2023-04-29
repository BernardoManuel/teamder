package repository;

import database.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Message;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageRepository {

    private Connection connection;

    public MessageRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Message message) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.persist(message);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }


    public ObservableList<Message> findRoomMessages(Integer id_room) throws SQLException {
        ObservableList<Message> messages = FXCollections.observableArrayList();
        String query = "SELECT * FROM `mensajes` WHERE id_sala = ? ORDER by fecha asc;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id_room);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Message message = new Message();
                    message.setId(resultSet.getInt("id_mensaje"));
                    message.setMensaje(resultSet.getString("mensaje"));
                    message.setFecha(resultSet.getLong("fecha"));

                    // Agrega más atributos de la entidad Message según tu base de datos
                    messages.add(message);
                }
            }
        }
        return messages;
    }
}
