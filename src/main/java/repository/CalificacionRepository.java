package repository;

        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import model.Calificacion;
        import model.Game;
        import model.Usuario;

        import java.sql.*;

public class CalificacionRepository {

    private Connection connection;

    public CalificacionRepository(Connection connection) {
        this.connection = connection;
    }


    public ObservableList<Calificacion> getCalificacionesByUserId(Integer idUser) throws SQLException {
        String query = "SELECT * FROM calificaciones WHERE id_user = ?;";
        ObservableList<Calificacion> calificaciones = FXCollections.observableArrayList();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUser);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Calificacion calificacion = new Calificacion();
                    calificacion.setId(resultSet.getInt("id_calif"));
                    calificacion.setIdUser(resultSet.getInt("id_user"));
                    calificacion.setIdJuego(resultSet.getInt("id_juego"));
                    calificacion.setIdEvaluador(resultSet.getInt("id_evaluador"));
                    calificacion.setFechaCalif(resultSet.getDate("fecha_calif"));
                    calificacion.setComentario(resultSet.getString("comentario"));
                    calificacion.setValoracion(resultSet.getDouble("valoracion"));
                    calificaciones.add(calificacion);
                }
            }
        }

        return calificaciones;
    }

    public void guardarCalificacion(Calificacion calificacion) throws SQLException {
        String query = "insert into calificaciones (id_user, id_juego, id_evaluador, fecha_calif, comentario, valoracion) values (?, ?, ?, ?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, calificacion.getIdUser());
            statement.setInt(2, calificacion.getIdJuego());
            statement.setInt(3, calificacion.getIdEvaluador());
            statement.setDate(4, (Date) calificacion.getFechaCalif());
            statement.setString(5, calificacion.getComentario());
            statement.setDouble(6, calificacion.getValoracion());
            statement.executeUpdate();
        }
    }

}

