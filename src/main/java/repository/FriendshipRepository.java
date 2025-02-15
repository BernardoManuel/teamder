package repository;

import database.HibernateUtil;
import jakarta.persistence.Query;
import model.Friendship;
import model.Request;
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
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(friendship);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
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

    public void deleteFriendships(Friendship friendship, User user1, User user2) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            friendship.setAmigo1(null);
            friendship.setAmigo2(null);
            user1.getAmistades().remove(user2);
            user2.getAmistades().remove(user1);
            session.merge(friendship);
            session.merge(user1);
            session.merge(user2);
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

    public boolean checkIfPendingRequest(Request request) {
        boolean result = false;

        return result;
    }
}