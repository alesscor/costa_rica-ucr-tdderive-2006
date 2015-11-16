package oact;

/**
 * Excepci�n que indica que un trabajo del objeto activo
 * ha sido postpuesto por alguna raz�n y puesto en la lista
 * de activaci�n.
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