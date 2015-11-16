/*
 * Created on 01/06/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

/**
 * <p>Título: admin</p>
 * <p>Descripción: Encargada de guiar la terminación de una aplicación
 * formada por múltiples subtrabajos.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzación: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Esbozo para guiar la terminación de un trabajo formado por múltiples
 * subtrabajos locales.
 */
public abstract class ADMINAPPUnificadoras {
  public final static String SIN_TERMINAR="SIN_TERMINAR";
  public final static String TERMINADO="TERMINADO";
  public final static String INTERRUMPIDO="INTERRUMPIDO";
  /**
   * 
   */
  public ADMINAPPUnificadoras() {
  }
  /**
   * Indica si el resultado de la aplicación se ha obtenido con éxito, uniendo
   * los resultados de los subtrabajos que la componen. 
   * Para la aplicación dderive el proceso coordinador indica el final del trabajo
   * generando un archivo cuyo nombre es igual al del archivo de entrada con el 
   * sufijo .dtf.
   * TODO: 00000 detección del final. Es tarea del planificador. 
   * @param trabajo El trabajo a examinar.
   * @return Si el trabajo ha terminado y su resultado está listo para ser
   * retornado.
   * @throws Exception Cualquier error en el proceso de unificación de
   * resultados.
   */
  protected abstract boolean unificaTrabajo(ADMINAPPITrabajos trabajo) throws
      Exception;
  /**
   * Verifica la validez de untrabajo al ser unificados los resultados de
   * sus subtrabajos.
   * @param trabajo Trabajo a examinar con la información suficiente para
   * realizar esta verificación.
   * @return Si los resultados del trabajo son válidos.
   */
  protected abstract boolean verificaTrabajo(ADMINAPPITrabajos trabajo);      
  /**
   * Reúne los resultados parciales del trabajo.
   * @param trabajo
   *        El trabajo del cual se reunen los resultados.
   * @return
   *        Verdadero si todos los subtrabajos del trabajo han terminado
   *        y sus resultados se han reunido.
   * @throws ADMINAPPExcPosejecucion
   *        Si hay excepción desde el unificador concreto de la aplicación.
   */
  final boolean unificaTrabajoAdmin(ADMINAPPTrabajos trabajo) throws ADMINAPPExcPosejecucion{
    boolean bRes=false;    
    try{
      bRes=this.unificaTrabajo(trabajo);
    }catch(Exception ex){
      throw new ADMINAPPExcPosejecucion("Error luego de " +        "unificar unificar Trabajo",ex);
    }
    return bRes;
  }
  /**
   * La unión concreta de los resultados de la tarea es válida.
   * @param trabajo
   * @return Si el trabajo es válido.
   */
  boolean verificaTrabajoAdmin(ADMINAPPITrabajos trabajo){
    boolean bOK=true;
    /*
     * revisa si los resultados son válidos 
     */
    if(false){
      bOK=false;
    }
    return bOK;
  }
  /**
   * Realiza la administración de un subtrabajo recién terminado y
   * su entorno.
   * <li>Por ejemplo la ubicación de archivos de salida.</li>
   * @param subtrabajo El subtrabajo terminado.
   * @return Si se hubo o no problemas en la gestión del fin del subtrabajo.
   */
  abstract protected 
      boolean gestionaFinSubtrabajo(ADMINAPPISub_trabajos subtrabajo);
}

