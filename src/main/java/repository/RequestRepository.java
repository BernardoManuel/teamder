package repository;

import database.HibernateUtil;
import model.Request;
import model.User;
import org.hibernate.Session;

import java.util.List;
import java.util.Set;

public class RequestRepository {

    public RequestRepository() {
    }

    public void saveRequest(Request request) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!session.isOpen()) {
            session = HibernateUtil.getSessionFactory().openSession();
        }
        try {
            session.beginTransaction();
            session.persist(request);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public List<Request> getPendingRequests(User user) {
        List<Request> pendingFriendRequests = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            org.hibernate.query.Query<Request> query = session.createQuery("FROM Request WHERE solicitado = :user AND estado = 'pendiente' AND isShown = false", Request.class);
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


    public void updateRequestStatus(Request request) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(request);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}