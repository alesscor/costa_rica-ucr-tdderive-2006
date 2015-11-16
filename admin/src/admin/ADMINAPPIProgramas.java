/*
 * Created on 31/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

/**
 * <p>Título: admin</p>
 * <p>Descripción: Interfaz para el manejo de programas.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzación: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Interfaz dedicada a exponer la personalidad de las aplicaciones.
 */
public interface ADMINAPPIProgramas {
  /**
   * Obtiene el alias de un programa (identificación única).
   * @return El alias del programa.
   */
  public String getAlias();
  /**
   * Obtiene el nombre de la aplicación (solamente para uso de desplegue).
   * @return El nombre de la aplicación.
   */
  public String getNombreAplicacion();
  /**
   * Obtiene el nombre de la ruta del programa en el sistema local (URL).
   * @return La ruta del programa.
   */
  public String getRuta();
  /**
   * Obtiene la clase miscelánea del programa (aún sin implementar).
   * @return El nombre de la clase.
   */
  public String getClase();
  /**
   * Obtiene el nombre de la clase que divide una tarea en varios subtrabajos.
   * @return El nombre de la clase divisora.
   */
  public String getDivisora();
  /**
   * Obtiene el nombre de la clase que une los subtrabajos cuyos resultados
   * componen el resultado de una tarea.
   * @return El nombre de la clase unificadora.
   */
  public String getUnificadora();
  /**
   * Obtiene la cantidad de tiempo que se tolera en el sistema a un 
   * programa en espera.
   * <li>Si ésta dura mucho, entonces consulta si debe continuar.</li>
   * @return El tiempo tolerable de la aplicación en el sistema.
   */
  public long getTiempoEnSistema();
  /**
   * Obtiene el valor que indica cada cuántos segundos debe confirmarse la 
   * extensión de tiempo de una tarea cuya tolerancia está vencida.
   * @return Segundos antes de una confirmación.
   */
  public long getPeriodoConfirmacion();
  /**
   * Indica cuánto debe esperarse.
   * @return Las ocasiones aceptadas para la espera del fin de una tarea 
   * (extensión del tiempo de extra-gracia).
   */
  public long getUmbralEspera();
  /**
   * Indica si se debe cambiar la compu cuando la tolerancia en la
   * espera está vencida, incluyendo el periodo de extra-gracia
   * dado por las revisiones de su término.
   * @return Si se cambia de computadora.
   */
  public boolean getSiCambiarCompu();
}
