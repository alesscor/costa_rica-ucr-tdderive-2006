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
 */
/**
 * Excepción ocurrida previo a una ejecución.
 * <br></br>
 */
public class ADMINAPPExcPreejecucion extends ADMINAPPExcepcion {
  /**
   * 
   */
  public ADMINAPPExcPreejecucion() {
    super();
  }
  /**
   * @param message
   */
  public ADMINAPPExcPreejecucion(String message) {
    super(message);
  }
  /**
   * @param message
   * @param cause
   */
  public ADMINAPPExcPreejecucion(String message, Throwable cause) {
    super(message, cause);
  }
  /**
   * @param cause
   */
  public ADMINAPPExcPreejecucion(Throwable cause) {
    super(cause);
  }
}
