package repository;

import database.HibernateUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import model.Game;
import model.Message;
import org.hibernate.Session;

import java.util.List;

public class GamesRepository {

    public GamesRepository() {
    }

    public List<Game> findAllGames() {
        List<Game> result = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Game> query = cb.createQuery(Game.class);
            Root<Game> root = query.from(Game.class);
            query.select(root);
            result = session.createQuery(query).getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return result;
    }

    public Game getGameByName(String name) {
        Game game = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Game> query = cb.createQuery(Game.class);
            Root<Game> root = query.from(Game.class);
            query.select(root).where(cb.equal(root.get("name"), name));
            List<Game> result = session.createQuery(query).getResultList();
            if (result != null && !result.isEmpty()) {
                game = result.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return game;
    }
}
