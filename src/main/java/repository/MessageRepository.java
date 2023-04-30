package repository;

import database.HibernateUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import model.Message;
import model.Room;
import model.User;
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
/*
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

 */
    public List<Message> findRoomMessages(Room room) {
        List<Message> result = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Message> query = cb.createQuery(Message.class);
            Root<Message> root = query.from(Message.class);
            query.select(root).where(cb.equal(root.get("room"), room));
            result = session.createQuery(query).getResultList();
            session.getTransaction().commit();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            session.close();
        }

        return result;
    }
}
