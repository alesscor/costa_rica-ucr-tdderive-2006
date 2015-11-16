/*
 * Created on 23/07/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package admin;

/**
 * <p>Title: <b>admin</b>:: admin</p>
 * <p>Description: DIRException.java.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCR - ECCI</p>
 * <br>@author Alessandro</br>
 * <br>@version 1.0</br>
 */
/**
 * Excepción a nivel de balance de carga.
 */
public class DIRException extends ADMINGLOExcepcion {

  /**
   * 
   */
  public DIRException() {
    super();
  }

  /**
   * @param message
   */
  public DIRException(String message) {
    super(message);
  }

  /**
   * @param message
   * @param cause
   */
  public DIRException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param cause
   */
  public DIRException(Throwable cause) {
    super(cause);
  }

}
