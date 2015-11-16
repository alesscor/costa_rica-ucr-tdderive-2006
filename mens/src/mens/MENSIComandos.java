/*
 * Creado el 02/05/2006
 *
 */
package mens;

/**
 * @author alessandro
 * Interfaz cuyas instancias indican qué hacer con un mensaje.
 */
public interface MENSIComandos {
  /**
   * Indica si el procesamiento del mensaje
   * debe ejecutarse.
   * @return Si se debe continuar el procesamiento.
   */
  public boolean continuar();
  /**
   * Indica si el procesamiento del mensaje debe
   * demorarse.
   * @return Si se debe demorar el procesamiento.
   */
  public boolean demorarse();
  /**
   * Indica si el procesamiento del mensaje
   * debe detenerse.
   * @return Si se debe detener el procesamiento.
   */
  public boolean detenerse();
}
