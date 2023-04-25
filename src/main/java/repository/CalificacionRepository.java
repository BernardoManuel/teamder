package repository;

import javax.persistence.EntityManager;
        import javax.persistence.EntityManagerFactory;
        import javax.persistence.Persistence;
        import javax.persistence.Query;
        import java.util.List;

        import model.Calificacion;

public class CalificacionRepository {

    private EntityManagerFactory emf;

    public CalificacionRepository() {
        emf = Persistence.createEntityManagerFactory("myPersistenceUnit");
    }

    public void guardarCalificacion(Calificacion calificacion) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(calificacion);
        em.getTransaction().commit();
        em.close();
    }

    public Calificacion buscarPorId(Integer id) {
        EntityManager em = emf.createEntityManager();
        Calificacion calificacion = em.find(Calificacion.class, id);
        em.close();
        return calificacion;
    }

    public void actualizarCalificacion(Calificacion calificacion) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(calificacion);
        em.getTransaction().commit();
        em.close();
    }

    public void eliminarCalificacion(Integer id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Calificacion calificacion = em.find(Calificacion.class, id);
        if (calificacion != null) {
            em.remove(calificacion);
        }
        em.getTransaction().commit();
        em.close();
    }

    public List<Calificacion> buscarPorUsuario(Integer idUsuario) {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("SELECT c FROM Calificacion c WHERE c.idUser = :idUsuario");
        query.setParameter("idUsuario", idUsuario);
        List<Calificacion> calificaciones = query.getResultList();
        em.close();
        return calificaciones;
    }

}

