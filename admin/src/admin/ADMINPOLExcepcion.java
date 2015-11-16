/*
 * Created on 27/06/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package admin;

/**
 * <p>Title: <b>admin</b>:: admin</p>
 * <p>Description: ADMINPOLExcepcion.java.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCR - ECCI</p>
 * <br>@author Alessandro</br>
 * <br>@version 1.0</br>
 */
/**
 * Excepción en la consecución de las políticas
 * del sistema. 
 */
public class ADMINPOLExcepcion extends Exception {
  public ADMINPOLExcepcion() {
    super();
  }
  public ADMINPOLExcepcion(String message) {
    super(message);
  }
  public ADMINPOLExcepcion(String message, Throwable cause) {
    super(message, cause);
  }
  public ADMINPOLExcepcion(Throwable cause) {
    super(cause);
  }
}
