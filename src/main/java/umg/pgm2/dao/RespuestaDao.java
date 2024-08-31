package umg.pgm2.dao;

import umg.pgm2.db.DatabaseConnection;
import umg.pgm2.model.Respuesta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RespuestaDao {

    public void save(Respuesta respuesta) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO tb_respuestas (seccion, telegram_id, pregunta_id, respuesta_texto) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, respuesta.getSeccion());
            statement.setLong(2, respuesta.getTelegramId());
            statement.setInt(3, respuesta.getPreguntaId());
            statement.setString(4, respuesta.getRespuestaTexto());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}