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
 * Excepción por interrupción.
 */
public class ADMINAPPExcInterrupcion extends ADMINAPPExcepcion {
  /**
   * 
   */
  public ADMINAPPExcInterrupcion() {
    super();
  }
  /**
   * @param message
   */
  public ADMINAPPExcInterrupcion(String message) {
    super(message);
  }
  /**
   * @param message
   * @param cause
   */
  public ADMINAPPExcInterrupcion(String message, Throwable cause) {
    super(message, cause);
  }
  /**
   * @param cause
   */
  public ADMINAPPExcInterrupcion(Throwable cause) {
    super(cause);
  }
}
