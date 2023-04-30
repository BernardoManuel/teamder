package repository;

import database.HibernateUtil;
import model.User;
import org.hibernate.Session;
import java.util.List;

public class UsuariosRepository {
    private Session session;
    public UsuariosRepository() {}

    public User findUserByUsername(String username) {
        User result = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            List<User> users = session.createNativeQuery("SELECT * FROM usuarios WHERE nom_user = :pusername", User.class)
                    .setParameter("pusername", username)
                    .list();
            if (users.size() > 0) {
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
        session = HibernateUtil.getSessionFactory().getCurrentSession();
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
            result = session.createNativeQuery("SELECT * FROM usuarios WHERE nom_user = :pnom_user", User.class)
                    .setParameter("pnom_user", username)
                    .list();
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
            result = session.createNativeQuery("SELECT * FROM usuarios WHERE correo = :pcorreo", User.class)
                    .setParameter("pcorreo", correo)
                    .list();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return (result != null && result.size() > 0);
    }
}
