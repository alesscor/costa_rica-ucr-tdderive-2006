package admin;
import orgainfo.*;
/**
 * <p>Title: Administraci�n de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Excepci�n global de <tt>tdderive</tt>. 
 */
public class ADMINGLOExcepcion extends OIExcepcion {

  public ADMINGLOExcepcion() {
  }

  public ADMINGLOExcepcion(String message) {
    super(message);
  }

  public ADMINGLOExcepcion(String message, Throwable cause) {
    super(message, cause);
  }

  public ADMINGLOExcepcion(Throwable cause) {
    super(cause);
  }
}