package mineria;

import admin.ADMINAPPExcepcion;

/**
 * Excepción de la capa de aplicación, específicamente,
 * de la minería de datos.
 */
public class MINERExcepcion extends ADMINAPPExcepcion {

  public MINERExcepcion() {
  }

  public MINERExcepcion(String message) {
    super(message);
  }

  public MINERExcepcion(String message, Throwable cause) {
    super(message, cause);
  }

  public MINERExcepcion(Throwable cause) {
    super(cause);
  }
}