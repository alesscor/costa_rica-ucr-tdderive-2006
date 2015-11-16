package oact;

/**
 * Excepción general a nivel de objeto activo.
 */
public class OACTExcepcion extends Exception {

  public OACTExcepcion() {
  }

  public OACTExcepcion(String message) {
    super(message);
  }

  public OACTExcepcion(String message, Throwable cause) {
    super(message, cause);
  }

  public OACTExcepcion(Throwable cause) {
    super(cause);
  }
}