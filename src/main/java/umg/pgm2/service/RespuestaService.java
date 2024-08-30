package umg.pgm2.service;

import umg.pgm2.dao.RespuestaDao;
import umg.pgm2.db.DatabaseConnection;
import umg.pgm2.db.TransactionManager;
import umg.pgm2.model.Respuesta;

import java.sql.Connection;
import java.sql.SQLException;

public class RespuestaService {
    private RespuestaDao respuestaDAO = new RespuestaDao();

    public void crearRespuesta(Respuesta respuesta) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            TransactionManager tm = new TransactionManager(connection);
            tm.beginTransaction();
            try {
                respuestaDAO.crearRespuesta(respuesta);
                tm.commit();
            } catch (SQLException e) {
                tm.rollback();
                throw e;
            }
        }
    }
}
