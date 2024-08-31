package umg.pgm2.service;

import umg.pgm2.dao.RespuestaDao;
import umg.pgm2.model.Respuesta;

public class RespuestaService {
    private RespuestaDao respuestaDAO = new RespuestaDao();

    public void saveRespuesta(Respuesta respuesta) {
        respuestaDAO.save(respuesta);
    }
}
