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
            room.getUsers().add(user);
            session.merge(room);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void addUser(Room p_room, int user_id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            User user = session.get(User.class, user_id);
            Room room = session.get(Room.class, p_room.getId());
            room.getUsers().add(user);
            session.merge(room);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void removeUser(int room_id, int user_id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            User user = session.get(User.class, user_id);
            Room room = session.get(Room.class, room_id);
            room.getUsers().remove(user);
            session.merge(room);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void removeRoom(int room_id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            Room room = session.get(Room.class, room_id);
            room.setGame(null);
            room.setCreador(null);
            session.remove(room);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public Room updateRoom(Room p_room) {
        Room room = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            room = session.get(Room.class, p_room.getId());
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return room;
    }
}