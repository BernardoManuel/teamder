package repository;

import database.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UsuariosRepository {

    private Connection connection;
    private Session session;
    public UsuariosRepository() {

    }
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
            usuario = users.get(0);
        } catch (IndexOutOfBoundsException e) {
            session.getTransaction().commit();
            session.close();
        }

        return usuario;
    }

    public ObservableList<User> findAll() throws SQLException {
        ObservableList<User> usuarios = FXCollections.observableArrayList();
        String query = "SELECT * FROM usuarios";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                User usuario = new User();
                usuario.setId(resultSet.getInt("cod_user"));
                usuario.setNombreUsuario(resultSet.getString("nom_user"));
                usuario.setContraseña(resultSet.getString("contraseña"));
                usuario.setSalt(resultSet.getString("salt"));
                usuario.setCorreo(resultSet.getString("correo"));
                usuario.setDescripcion(resultSet.getString("descripcion"));
                // Agrega más atributos de la entidad Usuario según tu base de datos
                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

    public void save(User usuario) throws SQLException {
        String query = "INSERT INTO usuarios (nom_user, contraseña, salt, correo, descripcion) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, usuario.getNombreUsuario());
            statement.setString(2, usuario.getContraseña());
            statement.setString(3, usuario.getSalt());
            statement.setString(4, usuario.getCorreo());
            statement.setString(5, usuario.getDescripcion());
            // Configura más parámetros del statement según tu base de datos y entidad Usuario
            statement.executeUpdate();
        }
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

    public String getUsernameById(int id_user) throws SQLException {
        String query = "select nom_user from usuarios where cod_user = ?;";

        User usuario = null;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id_user);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    usuario = new User();
                    usuario.setNombreUsuario(resultSet.getString("nom_user"));
                }
            }
        }

        return usuario.getNombreUsuario();
    }


}
