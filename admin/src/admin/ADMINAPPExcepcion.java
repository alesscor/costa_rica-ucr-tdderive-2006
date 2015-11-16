package admin;

/**
 * <p>Title: Administraci�n de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Errores en la administraci�n de <tt>tdderive</tt>.
 */
public class ADMINAPPExcepcion extends Exception {

  public ADMINAPPExcepcion() {
  }

  public ADMINAPPExcepcion(String message) {
    super(message);
  }

  public ADMINAPPExcepcion(String message, Throwable cause) {
    super(message, cause);
  }

  public ADMINAPPExcepcion(Throwable cause) {
    super(cause);
  }
}