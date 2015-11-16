/*
 * Created on 31/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

import java.util.*;
/**
 * <p>Título: admin</p>
 * <p>Descripción: Interfaz para el manejo de tareas.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzación: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Interfaz para leer y poner valores a una tarea.
 */
public interface ADMINAPPITareas {
  public static final String TAREA_INICIO="TAREA_INICIO";
  public static final String TAREA_ERROR="TAREA_ERROR";
  public static final String TAREA_PREPARACION="TAREA_PREPARACION";
  public static final String TAREA_MARCHA="TAREA_MARCHA";
  public static final String TAREA_COMSUMACION="TAREA_COMSUMACION";
  public static final String TAREA_ENIMPORTACION="TAREA_ENIMPORTACION";
  public static final String TAREA_EXISTENTE="TAREA_EXISTENTE";
  public static final String TAREA_FIN="TAREA_FIN";
  public static final String TAREA_ENTREGADA="TAREA_ENTREGADA";
  final static String DIR_TRABAJOS="trabajos";
  final static String DIR_PREFIJOTAREA="tarea";
  final static String DIR_COMPRIMIDOS="comprimidos";
  final static String DIR_RESULTADOS="resultados";
  final static String DIR_BLOQUES="bloques";
  /**
   * Obtiene el directorio de trabajo de la tarea.
   * @return La ruta del directorio de trabajo.
   */
  public String getTareaDir();
  /**
   * Obtiene el directorio en donde se dejan los resultados
   * de la tarea.
   * @return La ruta del directorio de resultados.
   */
  public String getResultadosDir();
  /**
   * Obtiene el directorio en donde se dejan los archivos de entrada
   * comprimidos para la tarea (esto en caso de que se haya importado
   * el trabajo).
   * @return La ruta del directorio de archivos de entrada comprimidos.
   */
  public String getComprimidosDir();
  /**
   * @deprecated
   * Obtiene el directorio de bloques de archivos de entrada comprimidos 
   * (Obsoleto).
   * @return La ruta del directorio de bloques de archivos de entrada comprim.
   */
  public String getBloquesDir();
  /**
   * Obtiene la identificación de una tarea (único).
   * @return El nombre de una tarea.
   */
  public String getIdTarea();
  /**
   * @deprecated
   * (Obsoleto).
   * @return (Obsoleto).
   */
  public String getDirectorio();
  /**
   * @deprecated
   * (Obsoleto).
   * @return (Obsoleto).
   */
  public String getRutasEntrada();
  /**
   * @deprecated
   * (Obsoleto).
   * @return (Obsoleto).
   */
  public String getRutasSalida();
  /**
   * Indica si la computadora es coordinadora (se ha encargado de recibir una
   * solicitud de usuario y de su correspondiente despacho en subtrabajos).
   * @return Si la computadora local es coordinadora.
   */
  public boolean getSiCoordina();
  /**
   * Indica si la tarea ha terminado, es decir, si todos sus subtrabajos 
   * componentes han terminado.
   * @return Si la tarea ha terminado.
   */
  public boolean getSiTerminado();
  /**
   * Obtiene el estado de una tarea.
   * @return El estado de una tarea.
   */
  public String getEstadoTarea();
  /**
   * Obtiene la hora en que una solicitud fue realizada, (en milisegundos UTF).
   * @return La hora de solicitud.
   */
  public long getHoraSolicitud();
  /**
   * Obtiene la hora en que una solicitud fue iniciada, (en milisegundos UTF).
   * @return La hora de inicio.
   */
  public long getHoraInicio();
  /**
   * Obtiene la hora en que una solicitud fue finalizada, (en milisegundos UTF).
   * @return La hora de finalziación.
   */
  public long getHoraFin();
  /**
   * Obtiene el alias del programa que el usuario desaa ejecutar.
   * @return El alias del programa que se debe ejecutar.
   */
  public String getAlias();
  /**
   * Obtiene los parámetros del programa que el usuario desea ejecutar.
   * @return Los parámetros del programa que se debe ejecutar.
   */
  public String getParametros();
  /**
   * Obtiene, en forma de arreglo, los parámetros del programa que 
   * el usuario desea ejecutar.
   * @return Los parámetros del programa que se debe ejecutar 
   * en forma de arreglo.
   */
  public String[] getParametrosArr();
  /**
   * Obtiene la instancia del programa que se desea ejecutar.
   * @return La instancia del programa que se desea ejecutar.
   */    
  public ADMINAPPIProgramas getPrograma();
  /**
   * Obtiene la lista de archivos de entrada involucrados en la
   * ejecución de la aplicación solicitada por el usuario.
   * @return La lista de archivos de entrada del programa que se debe
   * ejecutar.
   */
  public Map getArchivos();
  public Map getSubtrabajos();
  /**
   * Tiempo en que la tarea debe durar en el sistema, al ser vencido
   * se rige un trato de tolerancia.
   * @return Tiempo en el que la tarea debe permanecer en el sistema.
   */
  public int getTiempoEnSistema();
  /**
   * Obtiene el nombre del agente creador de la tarea.
   * @return El nombre del nodo que ha creado a la tarea.
   */
  public String getNodoCreador();
  public String toString();
}
