package aco;

/**
 * Error de término de tiempos de espera en las comunicaciones.
 */

public class ACONExcTemporizacion extends ACONExcepcion {

  public ACONExcTemporizacion() {
  }

  public ACONExcTemporizacion(String message) {
    super(message);
  }

  public ACONExcTemporizacion(int tipo, Object ex, int ext,String host, int port) {
    super(tipo, ex, ext,host,port);
  }

  public ACONExcTemporizacion(String message, Throwable cause) {
    super(message, cause);
  }

  public ACONExcTemporizacion(Throwable cause) {
    super(cause);
  }
}