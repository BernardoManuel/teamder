package repository;

import database.HibernateUtil;
import model.Message;
import model.Room;
import org.hibernate.Session;

import java.util.List;

public class MessageRepository {
    public MessageRepository() {}

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
        List<Message> result = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            result = session.createNativeQuery("SELECT * FROM mensajes WHERE id_sala = :pid_sala", Message.class)
                            .setParameter("pid_sala", room.getId()).list();
            session.getTransaction().commit();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            session.close();
        }

        return result;
    }
}
