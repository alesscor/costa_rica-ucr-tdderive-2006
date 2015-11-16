/*
 * Created on 01/06/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

/**
 * <p>T�tulo: admin</p>
 * <p>Descripci�n: Encargada de guiar la terminaci�n de una aplicaci�n
 * formada por m�ltiples subtrabajos.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzaci�n: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Esbozo para guiar la terminaci�n de un trabajo formado por m�ltiples
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
   * Indica si el resultado de la aplicaci�n se ha obtenido con �xito, uniendo
   * los resultados de los subtrabajos que la componen. 
   * Para la aplicaci�n dderive el proceso coordinador indica el final del trabajo
   * generando un archivo cuyo nombre es igual al del archivo de entrada con el 
   * sufijo .dtf.
   * TODO: 00000 detecci�n del final. Es tarea del planificador. 
   * @param trabajo El trabajo a examinar.
   * @return Si el trabajo ha terminado y su resultado est� listo para ser
   * retornado.
   * @throws Exception Cualquier error en el proceso de unificaci�n de
   * resultados.
   */
  protected abstract boolean unificaTrabajo(ADMINAPPITrabajos trabajo) throws
      Exception;
  /**
   * Verifica la validez de untrabajo al ser unificados los resultados de
   * sus subtrabajos.
   * @param trabajo Trabajo a examinar con la informaci�n suficiente para
   * realizar esta verificaci�n.
   * @return Si los resultados del trabajo son v�lidos.
   */
  protected abstract boolean verificaTrabajo(ADMINAPPITrabajos trabajo);      
  /**
   * Re�ne los resultados parciales del trabajo.
   * @param trabajo
   *        El trabajo del cual se reunen los resultados.
   * @return
   *        Verdadero si todos los subtrabajos del trabajo han terminado
   *        y sus resultados se han reunido.
   * @throws ADMINAPPExcPosejecucion
   *        Si hay excepci�n desde el unificador concreto de la aplicaci�n.
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
   * La uni�n concreta de los resultados de la tarea es v�lida.
   * @param trabajo
   * @return Si el trabajo es v�lido.
   */
  boolean verificaTrabajoAdmin(ADMINAPPITrabajos trabajo){
    boolean bOK=true;
    /*
     * revisa si los resultados son v�lidos 
     */
    if(false){
      bOK=false;
    }
    return bOK;
  }
  /**
   * Realiza la administraci�n de un subtrabajo reci�n terminado y
   * su entorno.
   * <li>Por ejemplo la ubicaci�n de archivos de salida.</li>
   * @param subtrabajo El subtrabajo terminado.
   * @return Si se hubo o no problemas en la gesti�n del fin del subtrabajo.
   */
  abstract protected 
      boolean gestionaFinSubtrabajo(ADMINAPPISub_trabajos subtrabajo);
}

