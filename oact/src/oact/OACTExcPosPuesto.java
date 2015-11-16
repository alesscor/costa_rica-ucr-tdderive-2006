package oact;

/**
 * Excepción que indica que un trabajo del objeto activo
 * ha sido postpuesto por alguna razón y puesto en la lista
 * de activación.
 */
public class OACTExcPosPuesto extends OACTExcepcion {

  public OACTExcPosPuesto() {
  }

  public OACTExcPosPuesto(String message) {
    super(message);
  }

  public OACTExcPosPuesto(String message, Throwable cause) {
    super(message, cause);
  }

  public OACTExcPosPuesto(Throwable cause) {
    super(cause);
  }
}