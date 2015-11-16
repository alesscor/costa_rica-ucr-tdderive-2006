package mineria;

import admin.ADMINAPPExcepcion;

/**
 * Excepci�n de la capa de aplicaci�n, espec�ficamente,
 * de la miner�a de datos.
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