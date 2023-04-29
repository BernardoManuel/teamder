package repository;

import database.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Message;
import model.Room;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class MessageRepository {

    private Connection connection;

    public MessageRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Message message) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            message.getRoom().getMessages().add(message);
            session.persist(message);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }


    public List<Message> findRoomMessages(Room room) {

        List    <Message> messages = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            messages = session.createNativeQuery("SELECT * FROM mensajes WHERE id_sala = :pid_sala", Message.class)
                            .setParameter("pid_sala", room.getId()).list();
            session.getTransaction().commit();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            session.close();
        }

        return messages;
    }
}
