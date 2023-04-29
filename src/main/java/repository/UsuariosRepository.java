package repository;

import database.HibernateUtil;
import model.User;
import org.hibernate.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuariosRepository {

    private Connection connection;

    public UsuariosRepository(Connection connection) {
        this.connection = connection;
    }


    public User findUserByUsername(String username) {
        User usuario = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            List<User> users = session.createNativeQuery("SELECT * FROM usuarios WHERE nom_user = :pusername", User.class)
                    .setParameter("pusername", username)
                    .list();
            if (users.size() > 0) {
                usuario = users.get(0);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            session.getTransaction().commit();
            session.close();
        }

        return usuario;
    }

    public void save(User usuario) {
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.persist(usuario);
        session.getTransaction().commit();
        session.close();
    }

    public boolean isNombreUsuarioExists(String nombreUsuario) throws SQLException {
        String query = "SELECT COUNT(*) FROM usuarios WHERE nom_user = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nombreUsuario);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    public boolean isCorreoExists(String correo) throws SQLException {
        String query = "SELECT COUNT(*) FROM usuarios WHERE correo = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, correo);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    public Usuario findUsuarioByNombreUsuario(String nombreUsuario) throws SQLException {
        String query = "SELECT * FROM usuarios WHERE nom_user = ?";
        Usuario usuario = null;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nombreUsuario);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    usuario = new Usuario();
                    usuario.setId(resultSet.getInt("cod_user"));
                    usuario.setNombreUsuario(resultSet.getString("nom_user"));
                    usuario.setContraseña(resultSet.getString("contraseña"));
                    usuario.setSalt(resultSet.getString("salt"));
                    usuario.setCorreo(resultSet.getString("correo"));
                    usuario.setDescripcion(resultSet.getString("descripcion"));
                }
            }
        }
        return usuario;
    }

    public String getUsernameById(int id_user) throws SQLException {
        String query = "select nom_user from usuarios where cod_user = ?;";

        Usuario usuario = null;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id_user);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    usuario = new Usuario();
                    usuario.setNombreUsuario(resultSet.getString("nom_user"));
                }
            }
        }

        return usuario.getNombreUsuario();
    }


}
