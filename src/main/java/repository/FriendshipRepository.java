package repository;

import database.HibernateUtil;
import jakarta.persistence.Query;
import model.Friendship;
import org.hibernate.Session;
import org.hibernate.Transaction;
import model.User;

import java.util.List;

public class FriendshipRepository {

    public FriendshipRepository() {
    }

    public void saveFriendship(Friendship friendship) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(friendship);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Friendship> getPendingFriendRequests(User user) {
        List<Friendship> pendingFriendRequests = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            org.hibernate.query.Query<Friendship> query = session.createQuery("FROM Friendship WHERE amigo2 = :user AND solicitud = 'pendiente'", Friendship.class);
            query.setParameter("user", user);
            pendingFriendRequests = query.getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
        return pendingFriendRequests;
    }


    public void updateFriendshipStatus(Friendship friendship) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(friendship);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteFriendship(Friendship friendship) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.delete(friendship);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }



    /* public User findUserById(Integer id) {
        User user = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            user = session.get(User.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }*/

}