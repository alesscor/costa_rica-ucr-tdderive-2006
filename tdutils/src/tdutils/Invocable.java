/*
 * Created on 07/08/2004
 */
package tdutils;
/**
 * <br>Describe cualquier m�todo de servicio que admita como argumento
 * un <tt>Object</tt>, proporcione como resultado un <tt>Object</tt>
 * y pueda lanzar una excepci�n. </br>
 * <br>Patr�n recomendado por Doug Lea en <i>Programaci�n concurrente
 * en Java: Principios de dise�o y patrones</i>, de Addison Wesley; Madrid, 
 * 2001.</br>
 */
public interface Invocable {
  /**
   * Invoca un m�todo. 
   * @param args Argumento del m�todo.
   * @return Devoluci�n del m�todo.
   * @throws Exception Si hay un error.
   */
  Object invoca(Object args[]) throws Exception;
}
