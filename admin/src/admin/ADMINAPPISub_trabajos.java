/*
 * Created on 02/06/2004
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
 * Interfaz para ponerle valores a un subtrabajo. 
 */
public interface ADMINAPPISub_trabajos {
  /**
   * Estado inicial de un subtrabajo.
   */
  public final static String SUBTRA_INICIO="SUBTRA_INICIO";
  /**
   * Indica que el subtrabajo ha iniciado recientemente.
   */
  public final static String SUBTRA_MARCHA="SUBTRA_MARCHA";
  /** 
   * En el agente participante indica que el subtrabajo ha sido terminado 
   * pero sin tener ubicados los archivos de salida. En el agente 
   * coordinador indica que el subtrabajo ha sido terminado y que los archivos
   * están pendientes de ser recibidos.
   */
  public final static String SUBTRA_FIN="SUBTRA_FIN";
  /**
   * Indica que el subtrabajo ha está en exportación
   */
  public final static String SUBTRA_ENEXPORTACION="SUBTRA_ENEXPORTACION";
  /**
   * Indica que el subtrabajo ha sido exportado para la ejecución remota,
   * es decir, para que otro sistema lo ejecute.
   */
  public final static String SUBTRA_EXTERNO="SUBTRA_EXTERNO";
  /**
   * En el agente participante indica que el subtrabajo 
   * ha sido terminado y que sus archivos de salida han sido ubicados.
   * Este estado no se usa en el agente coordinador.
   */
  public final static String SUBTRA_FIN2="SUBTRA_FIN2";
  /**
   * En un agente participante, indica que el subtrabajo terminado 
   * y sus archivos de salida fueron entregados con éxito. En un agente 
   * coordinador indica que el subtrabajo terminado y sus archivos de 
   * salida fueron recibidos con éxito.
   */
  public final static String SUBTRA_ENTREGADO="SUBTRA_ENTREGADO";
  /**
   * Subtrabajo completamente configurado y listo para cargar que se
   * encuentra en lista de espera.
   */
  public final static String SUBTRA_ESPERA="SUBTRA_ESPERA";
  public final static String SUBTRA_ERROR="SUBTRA_ERROR";
  /**
   * Indica que la carga del trabajo no ha sido determinada.
   */
  public final static double SIN_CARGA=-1.0;
  /**
   * Obtiene la carga del subtrabajo para el sistema local.
   * @return Carga del subtrabajo para el sistema local.
   */
  public double getCarga();
  /**
   * Obtiene el directorio de trabajo del subtrabajo.
   * @return Directorio de trabajo del subtrabajo.
   */
  public String getSubtrabajoDir();
  /**
   * Obtiene el directorio de resultados del subtrabajo.
   * @return Directorio de resultados del subtrabajo.
   */
  public String getResultadosDir();
  /**
   * Obtiene el comando de trabajo del subtrabajo.
   * @return Comando de trabajo del subtrabajo.
   */
  public String getComando();  
  /**
   * Obtiene el estado del subtrabajo.
   * @return El estado del subtrabajo.
   */
  public String getEstadoSubtrabajo();
  /**
   * Obtiene la identificación del subtrabajo (única).
   * @return Nombre del subtrabajo.
   */
  public String getIdSubtrabajo();
  /**
   * Obtiene la identificación de la tarea de la cual el subtrabajo es parte.
   * @return Identificación de la tarea del subtrabajo.
   */
  public String getIdTarea();
  /**
   * Obtiene el porcentaje de avance del trabajo que representa este objeto.
   * @return Porcentaje de avance en el trabajo que este objeto representa.
   */
  public double getProgreso();
  /**
   * Obtiene la rutas de los archivos de entrada de este subtrabajo.
   * @return Las rutas de los archivos de entrada de este subtrabajo.
   */
  public String getRutasEntrada();
  /**
   * Obtiene la rutas de los archivos de salida de este subtrabajo.
   * @return Las rutas de los archivos de salida de este subtrabajo.
   */
  public String getRutasSalida();
  /**
   * Obtiene el objeto de tarea de la cual el subtrabajo forma parte.
   * @return Objeto de tarea de la cual el subtrabajo forma parte.
   */
  public ADMINAPPITareas getTarea();
  /**
   * Obtiene el mapa de archivos de entrada del subtrabajo.
   * @return El mapa de archivos de entrada.
   */
  public Map getArchivos();
  /**
   * Obtiene el mapa de archivos de salida del subtrabajo.
   * @return El mapa de archivos de salida.
   */
  public Map getArchivosSalida();
  /**
   * Asigna la carga local que el subtrabajo requiere para ser realizado.
   * @param setCarga Carga local requerida por el sistema para realizar el
   * subtrabajo.
   */
  public void setCarga(double setCarga);
//  /**
//   * Asigna el directorio de trabajo del subtrabajo.
//   * @param setDirectorio Directorio de trabajo del subtrabajo.
//   */
//  public void setDirectorio(String setDirectorio);
  /**
   * Asigna el estado del subtrabajo.
   * @param setEstadoSubtrabajo El estado para el subtrabajo.
   */
  public void setEstadoSubtrabajo(String setEstadoSubtrabajo);
  /**
   * Asigna las rutas de los archivos de entrada del subtrabajo.
   * @param setRutasEntrada Rutas de los archivos de entrada del subtrabajo.
   */
  public void setRutasEntrada(String setRutasEntrada);
  /**
   * Asigna las rutas de los archivos de salida del subtrabajo.
   * @param setRutasSalida Rutas de los archivos de salida del subtrabajo.
   */
  public void setRutasSalida(String setRutasSalida);
  /**
   * Asigna el comando a ser ejecutado para la realización del subtrabajo.
   * @param comando Comando a ser ejecutado para realizar el subtrabajo.
   */
  public void setComando(String comando);
  /**
   * Agrega un archivo al subtrabajo.
   * @param nombre Nombre del archivo.
   * @return Instancia de archivo cuyos valores deben ser completados.
   */
  public ADMINAPPIArchivos addArchivo(String nombre);
//  /**
//   * Ubica los archivos del subtrabajo en el directorio de trabajo.
//   * dirOriginal Lugar donde se encuentran los archivos.
//   */
//  public void ubicaArchivosDirTrabajo(String dirOriginal);
//  /**
//   * Ubica los archivos del subtrabajo en el directorio de trabajo.
//   */
//  public void ubicaArchivosDirTrabajo();
  /**
   * Obtiene la hora de ingreso del subtrabajo al sistema.
   * @return La hora de ingreso del subtrabajo al sistema.
   */
  public long getHoraIngreso();
  /// <2006 considerando fin de subtrabajos>
  /**
   * <2006 />
   * Indica si el subtrabajo ha terminado.
   * @return Verdadero si el subtrabajo ha terminado.
   */
  public boolean getSiFin();
  /**
   * <2006/>
   * Asigna si el subtrabajo ha terminado.
   * @param siFin Verdadero si el subtrabajo ha terminado.
   * (Yupi!!!)
   */
  public void setSiFin(boolean siFin);
  public String toString();
}
