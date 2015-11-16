/*
 * Created on 07/08/2004
 */
package tdutils;
/**
 * <br>Describe cualquier método de servicio que admita como argumento
 * un <tt>Object</tt>, proporcione como resultado un <tt>Object</tt>
 * y pueda lanzar una excepción. </br>
 * <br>Patrón recomendado por Doug Lea en <i>Programación concurrente
 * en Java: Principios de diseño y patrones</i>, de Addison Wesley; Madrid, 
 * 2001.</br>
 */
public interface Invocable {
  /**
   * Invoca un método. 
   * @param args Argumento del método.
   * @return Devolución del método.
   * @throws Exception Si hay un error.
   */
  Object invoca(Object args[]) throws Exception;
}
