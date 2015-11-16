package admin;
import oact.OACTSirvienteAbs;
/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Abstracción del despachador.
 */
public abstract class ADMINAPPDespachadorAbs extends OACTSirvienteAbs {
  ADMINGLOInfo info;
  public ADMINAPPDespachadorAbs(ADMINGLOInfo info0) {
    _inicio(info0);
  }

  public ADMINAPPDespachadorAbs(String id0,ADMINGLOInfo info0) {
    super(id0);
    _inicio(info0);
  }
  private void _inicio(ADMINGLOInfo info0){
    info=info0;
  }
  /**
   * Ejecuta al programa del alias dado.
   * @param alias Nombre del programa a ejecutar.
   * @param parametros Parámetros del programa a ejecutar.
   * @return Resultado de la salida estándar del prgrama a ejecutar.
   * @throws ADMINAPPExcepcion En caso de error en la aplicación.
   */
  public abstract String ejecuta(String alias, String[] parametros)throws ADMINAPPExcepcion;
}