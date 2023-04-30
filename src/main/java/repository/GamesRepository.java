package repository;

import database.HibernateUtil;
import model.Game;
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
            result = session.createNativeQuery("SELECT * FROM juegos", Game.class).list();
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
            List<Game> gamesResult = session.createNativeQuery("SELECT * FROM juegos WHERE nom_juego = :pnom_juego", Game.class)
                    .setParameter("pnom_juego", name)
                    .list();
            game = gamesResult.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return game;
    }
}
