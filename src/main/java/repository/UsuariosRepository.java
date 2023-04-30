package repository;

import database.HibernateUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import model.User;
import org.hibernate.Session;
import java.util.List;

public class UsuariosRepository {
    public UsuariosRepository() {}

    public User findUserByUsername(String username) {
        User result = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(cb.equal(root.get("nombreUsuario"), username));
            List<User> users = session.createQuery(query).getResultList();
            if (!users.isEmpty()) {
                result = users.get(0);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    public void save(User usuario) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.persist(usuario);
        session.getTransaction().commit();
        session.close();
    }

    public boolean isNombreUsuarioExists(String username) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<User> result = null;
        try {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(cb.equal(root.get("nombreUsuario"), username));
            result = session.createQuery(query).getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return (result != null && result.size() > 0);
    }

    public boolean isCorreoExists(String correo){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<User> result = null;
        try {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(cb.equal(root.get("correo"), correo));
            result = session.createQuery(query).getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return (result != null && result.size() > 0);
    }
}
