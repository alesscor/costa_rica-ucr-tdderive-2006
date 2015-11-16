/*
 * Created on 26/06/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package admin;

/**
 * <p>Title: <b>admin</b>:: admin</p>
 * <p>Description: ADMINPOLLectorBajoNivel.java.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCR - ECCI</p>
 * <br>@author Alessandro</br>
 * <br>@version 1.0</br>
 */
/**
 * Interfaz para la lectura en bajo nivel. 
 */
public interface ADMINPOLILectorBajoNivel {
  // TODO Cambiar esto, pues es privisional (debe estar el valor "/proc")
  /**
   * Raíz del sistema de información.
   */
  public final static String DIRPROC="/tdderive/proc";
  /**
   * Archivo con información sobre la CPU.
   */
  public final static String PROCCPU=DIRPROC+"/cpuinfo";
  /**
   * Archivo con información sobre la memoria.
   */
  public final static String PROCMEM=DIRPROC+"/meminfo";
  /**
   * Archivo con información sobre la ejecución del kernel (tiempos).
   */
  public final static String PROCCPUU0=DIRPROC+"/stat";
  /**
   * Archivo con información sobre la ejecución de un proceso (tiempos).
   */
  public final static String PROCCPUU1=DIRPROC+"/self/stat";
  /**
   * @deprecated
   */
  public final static String PROCMNT0=DIRPROC+"/mounts";
  /**
   * @deprecated
   */
  public final static String PROCMNT1=DIRPROC+"/partitions";

  /**
   * Obtiene la cantidad de microprocesadores de la computadora.
   * @return La cantidad de microprocesadores de la computadora.
   */
  public int getNumProcesadores() throws ADMINPOLExcepcion;
  /**
   * Obtiene la velocidad del microprocesador en megahertz.
   * <li>Si la computadora tiene más de un microprocesador,
   * se asume que todos tienen la misma velocidad.</li>
   * @return La velocidad en megahertz.
   */
  public double getCPUMegaHertz() throws ADMINPOLExcepcion;
  /**
   * Obtiene el porcentaje de uso de CPU.
   * @deprecated Mejor usar <tt>getCPUPorcentajeUso2</tt>.
   * @return El porcentaje de uso de CPU, valor en [0,1].
   */
  public double getCPUPorcentajeUso() throws ADMINPOLExcepcion;
  /**
   * Obtiene el porcentaje de uso de CPU.
   * @param intervalo_precision El intérvalo de precisión.
   * @return El porcentaje de uso de CPU, valor en [0,1].
   */
  public double getCPUPorcentajeUso2(long intervalo_precision) throws ADMINPOLExcepcion;  
  /**
   * Obtiene la memoria física total del sistema en megabytes.
   * @return La memoria física total del sistema en MB.
   */
  public double getMemoriaTotal() throws ADMINPOLExcepcion;
  /**
   * Obtiene la memoria física usada por el sistema en megabytes. 
   * @return La memoria física usada por el sistema en MB.
   */
  public double getMemoriaUsada() throws ADMINPOLExcepcion;
  /**
   * Obtiene el espacio total del disco de tdderive en megabytes.
   * @return El espacio total del disco de tdderive en MB.
   */
  public double getDiscoTotal() throws ADMINPOLExcepcion;
  /**
   * Obtiene el espacio usado del disco de tdderive en megabytes.
   * @return El espacio usado del disco de tdderive en MB.
   */
  public double getDiscoUsado() throws ADMINPOLExcepcion;
  /**
   * Abre el objeto para la lectura de su información.
   * @throws ADMINPOLExcepcion Si el objeto de lectura está incompleto
   * para realizar su actividad.
   */
  public void open() throws ADMINPOLExcepcion;
  /**
   * Cierra el objeto para la lectura de su información.
   * @throws ADMINPOLExcepcion Si el objeto de lectura quedó incompleto
   * para realizar su actividad.
   */
  public void close() throws ADMINPOLExcepcion;
  /**
   * Obtiene la identificación del proceso.
   * @return La identificación del proceso.
   * @throws ADMINPOLExcepcion En caso de error en la lectura
   * de información de bajo nivel.
   */
  public long getIdentificacion() throws ADMINPOLExcepcion;
}
