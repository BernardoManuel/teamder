package repository;

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

    public ObservableList<Usuario> findAll() throws SQLException {
        ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
        String query = "SELECT * FROM usuarios";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Usuario usuario = new Usuario();
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

    public void save(Usuario usuario) throws SQLException {
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


}
