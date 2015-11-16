/*
 * Created on 31/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

/**
 * <p>Título: admin</p>
 * <p>Descripción: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzación: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 * <b>ADMINAPPIArchivos</b>
 */
/**
 * Interfaz para trabajar con archivos.
 */
public interface ADMINAPPIArchivos {
  /**
   * Indica que el archivo no ha sido cargado.
   */
  public final static String ARCHIVO_AUSENTE="ARCHIVO_AUSENTE";
  /**
   * Indica que ya no se va a utilizar.
   */
  public final static String ARCHIVO_BORRAR="ARCHIVO_BORRAR";
  /**
   * Indica que se está cargando en el sistema.
   */
  public final static String ARCHIVO_IMPORTANDO="ARCHIVO_IMPORTANDO";
  /**
   * Indica que está comprimido y descomprimido.
   */
  public final static String ARCHIVO_LISTO="ARCHIVO_LISTO";
  /**
   * Indica que está comprimido en el sistema (carpeta ./comprimido).
   */
  public final static String ARCHIVO_PRESENTE="ARCHIVO_PRESENTE";
  /**
   * Obtiene el nombre del bloque de un archivo.
   * <li>Si este tiene valor, entonces quiere decir que 
   * el archivo del nombre dado por getNombre() está dividido en 
   * varios bloques.</li>
   * @return El nombre del bloque del archivo.
   */
  public String getBloque();

  /**
   * Ruta del directorio del archivo.
   * @return La ruta del directorio que debe contener al archivo.
   */
  public String getDirectorio();

  /**
   * El código del estado del archivo.
   * @return El estado del archivo.
   */
  public String getEstadoArchivo();

  /**
   * Obtiene la identificación del subtrabajo al que le corresponde este
   * archivo.
   * @return El nombre del subtrabajo al que corresponde este archivo.
   */
  public String getIdSubtrabajo();

  /**
   * Obtiene la identificación de la tarea a la que le corresponde 
   * este archivo.
   * @return El nombre de la tarea a la que le corresponde este archivo.
   */
  public String getIdTarea();

  /**
   * Obtiene el nombre del archivo.
   * @return Nombre del archivo.
   */
  public String getNombre();

  /**
   * Obtiene la ruta original del archivo.
   * @return Ruta original del archivo.
   */
  public String getRutaOriginal();

  /**
   * Obtiene si el archivo es de entrada (si el valor es verdadero).
   * @return Si el archivo es de entrada.
   */
  public boolean getSiEntrada();
  /**
   * Indica si el archivo está en el mismo sistema que en el del cliente, 
   * así como en el del servidor.
   * @return Si el archivo es el mismo para el cliente que para el servidor.
   */
  public boolean getSiLocal();
  /**
   * Obtiene la tarea a la que corresponde este archivo.
   * @return La tarea a la que corresponde el archivo.
   */
  public ADMINAPPITareas getTarea();
  /**
   * Asigna el nombre del archivo.
   * @param setNombre El nombre del archivo.
   */
  public void setNombre(String setNombre);
  public void setRutaOriginal(String setRutaOriginal);
  public void setInfoArchivo(String info0);
  public String getInfoArchivo();
//  /**
//   * Asigna el estado del archivo.
//   * @param setNombre El nombre del archivo.
//   */
//  public void setEstadoArchivo(String setEstadoArchivo);  
}
