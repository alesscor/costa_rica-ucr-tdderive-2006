package aco;
/**
 * Exepción de omisión en las comunicaciones.
 */
public class ACONExcOmision extends ACONExcepcion {

  public ACONExcOmision() {
  }

  public ACONExcOmision(String message) {
    super(message);
  }

  public ACONExcOmision(int tipo, Object ex, int ext,String host,int port) {
    super(tipo, ex, ext,host,port);
  }

  public ACONExcOmision(String message, Throwable cause) {
    super(message, cause);
  }

  public ACONExcOmision(Throwable cause) {
    super(cause);
  }
}