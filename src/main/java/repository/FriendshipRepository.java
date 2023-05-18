package repository;

import database.HibernateUtil;
import jakarta.persistence.Query;
import model.Friendship;
import org.hibernate.Session;
import org.hibernate.Transaction;
import model.User;

import java.util.List;
import java.util.Set;

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
            org.hibernate.query.Query<Friendship> query = session.createQuery("FROM Friendship WHERE amigo2 = :user AND solicitud = 'pendiente' AND isShown = false", Friendship.class);
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
            friendship.setAmigo1(null);
            friendship.setAmigo2(null);
            session.delete(friendship);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public Set<Friendship> getFriendships(User puser) {
        Set<Friendship> result = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            User user = session.get(User.class, puser.getId());
            result = user.getAmistades();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    public void deleteFriendshipsBySolicitud(String solicitud) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            Query query = session.createQuery("DELETE FROM Friendship WHERE solicitud = :solicitud");
            query.setParameter("solicitud", solicitud);
            query.executeUpdate();
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