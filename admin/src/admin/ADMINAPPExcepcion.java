package admin;

/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Errores en la administración de <tt>tdderive</tt>.
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