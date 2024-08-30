package umg.pgm2.dao;

import umg.pgm2.db.DatabaseConnection;
import umg.pgm2.model.Respuesta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RespuestaDao {
    public boolean crearRespuesta(Respuesta respuesta) {
        String sql = "INSERT INTO tb_respuestas (seccion, telegram_id, pregunta_id, respuesta_texto) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, respuesta.getSeccion());
            statement.setLong(2, respuesta.getTelegramId());
            statement.setInt(3, respuesta.getPreguntaId());
            statement.setString(4, respuesta.getRespuestaTexto());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
