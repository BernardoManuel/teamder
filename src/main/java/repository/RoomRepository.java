package repository;

import database.HibernateUtil;
import model.Room;
import model.User;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import java.util.Set;

public class RoomRepository {

    public RoomRepository() {
    }

    public Set<Room> findUserRooms(User user) {
        Set<Room> rooms = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            Hibernate.initialize(user.getRooms());
            rooms = user.getRooms();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return rooms;
    }

    public void save(Room room, User user) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
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
}
