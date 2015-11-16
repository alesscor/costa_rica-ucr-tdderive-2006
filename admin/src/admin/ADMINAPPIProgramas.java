/*
 * Created on 31/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

/**
 * <p>T�tulo: admin</p>
 * <p>Descripci�n: Interfaz para el manejo de programas.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzaci�n: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Interfaz dedicada a exponer la personalidad de las aplicaciones.
 */
public interface ADMINAPPIProgramas {
  /**
   * Obtiene el alias de un programa (identificaci�n �nica).
   * @return El alias del programa.
   */
  public String getAlias();
  /**
   * Obtiene el nombre de la aplicaci�n (solamente para uso de desplegue).
   * @return El nombre de la aplicaci�n.
   */
  public String getNombreAplicacion();
  /**
   * Obtiene el nombre de la ruta del programa en el sistema local (URL).
   * @return La ruta del programa.
   */
  public String getRuta();
  /**
   * Obtiene la clase miscel�nea del programa (a�n sin implementar).
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
   * <li>Si �sta dura mucho, entonces consulta si debe continuar.</li>
   * @return El tiempo tolerable de la aplicaci�n en el sistema.
   */
  public long getTiempoEnSistema();
  /**
   * Obtiene el valor que indica cada cu�ntos segundos debe confirmarse la 
   * extensi�n de tiempo de una tarea cuya tolerancia est� vencida.
   * @return Segundos antes de una confirmaci�n.
   */
  public long getPeriodoConfirmacion();
  /**
   * Indica cu�nto debe esperarse.
   * @return Las ocasiones aceptadas para la espera del fin de una tarea 
   * (extensi�n del tiempo de extra-gracia).
   */
  public long getUmbralEspera();
  /**
   * Indica si se debe cambiar la compu cuando la tolerancia en la
   * espera est� vencida, incluyendo el periodo de extra-gracia
   * dado por las revisiones de su t�rmino.
   * @return Si se cambia de computadora.
   */
  public boolean getSiCambiarCompu();
}
