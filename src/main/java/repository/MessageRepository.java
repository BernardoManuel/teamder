package repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageRepository {

    private Connection connection;

    public MessageRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Message message) throws SQLException {
        String query = "INSERT INTO mensajes (id_sala, code_user, mensaje, fecha) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, message.getId_sala());
            statement.setInt(2, message.getId_user());
            statement.setString(3, message.getMensaje());
            statement.setLong(4, message.getFecha());
            // Configura más parámetros del statement según tu base de datos y entidad Message
            statement.executeUpdate();
        }
    }


    public ObservableList<Message> findRoomMessages(Integer id_room) throws SQLException {
        ObservableList<Message> messages = FXCollections.observableArrayList();
        String query = "SELECT * FROM `mensajes` WHERE id_sala = ? ORDER by fecha asc;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id_room);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Message message = new Message();
                    message.setId(resultSet.getInt("id_mensaje"));
                    message.setId_sala(resultSet.getInt("id_sala"));
                    message.setId_user(resultSet.getInt("code_user"));
                    message.setMensaje(resultSet.getString("mensaje"));
                    message.setFecha(resultSet.getLong("fecha"));

                    // Agrega más atributos de la entidad Message según tu base de datos
                    messages.add(message);
                }
            }
        }
        return messages;
    }
}
