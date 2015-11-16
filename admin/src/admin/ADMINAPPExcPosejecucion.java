/*
 * Created on 02/06/2004
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
 */
/**
 * Excepción por aviso de error luego de ejecución.
 */
public class ADMINAPPExcPosejecucion extends ADMINAPPExcepcion {
  /**
   * 
   */
  public ADMINAPPExcPosejecucion() {
    super();
  }
  /**
   * @param message
   */
  public ADMINAPPExcPosejecucion(String message) {
    super(message);
  }
  /**
   * @param message
   * @param cause
   */
  public ADMINAPPExcPosejecucion(String message, Throwable cause) {
    super(message, cause);
  }
  /**
   * @param cause
   */
  public ADMINAPPExcPosejecucion(Throwable cause) {
    super(cause);
  }
}
