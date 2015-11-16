/*
 * Created on 01/06/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

import java.util.Map;

/**
 * <p>Título: admin</p>
 * <p>Descripción: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzación: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Interfaz para consultar y dar valor a los atributos de un trabajo.
 * <br></br>
 */
public interface ADMINAPPITrabajos {
  /**
   * Obtiene el solicitante de un trabajo.
   * @return El solicitante de un trabajo.
   */
  public ADMINAPPISolicitantes getSolicitante();
  /**
   * Obtiene la tarea que describe un trabajo solicitado.
   * @return La tarea que describe un trabajo solicitado.
   */
  public ADMINAPPITareas getTarea();
  /**
   * Agrega un subtrabajo al sistema.
   * <li><b>Deben realizarse cambios a la instancia obtenida.</b></li>
   * @return El subtrabajo agregado, listo para realizarle cambios internos.
   */
  public ADMINAPPISub_trabajos addSubTrabajo();
//  /**
//   * Remueve un subtrabajo del sistema.
//   * @param subtrabajo El subtrabajo a sacar del sistema.
//   */
//  public void removeSubTrabajo(ADMINAPPISub_trabajos subtrabajo);
  /**
   * Obtiene un mapa de subtrabajos.
   * @return Un mapa de objetos ADMINAPPISub_trabajos.
   */
  public Map getSubTrabajos();
  /**
   * Confirma los valores del nuevo subtrabajo y realiza reubicaciones internas,
   * pasando los archivos de entrada especificados a los directorios de entrada.
   * @param copiar Indica si el archivo de entrada debe copiarse o moverse al
   * directorio de trabajo del subtrabajo.
 * @throws ADMINAPPExcepcion
   */
  public void ubicaEntradas(ADMINAPPISub_trabajos subtrabajo, boolean copiar) throws ADMINAPPExcepcion;
  public String toString();
}
