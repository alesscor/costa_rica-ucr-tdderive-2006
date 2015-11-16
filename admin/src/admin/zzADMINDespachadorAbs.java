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

abstract class zzADMINDespachadorAbs extends OACTSirvienteAbs {

  //////////////////////////////////////////////////////////////////////
  ADMINGLOInfo info;
  ADMINPOLPlanificador planificador;
  ADMINPOLLector lector;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  // constructores
  public zzADMINDespachadorAbs() {
  }

  public zzADMINDespachadorAbs(String id0) {
    super(id0);
  }
  //////////////////////////////////////////////////////////////////////
  // ejecución
  public abstract String ejecuta(String id_tarea,String comando, String parametros)
      throws ADMINGLOExcepcion,ADMINAPPExcepcion;
  public abstract void traeResultados() throws ADMINGLOExcepcion,ADMINAPPExcepcion;
  public abstract void entregaResultados() throws ADMINGLOExcepcion,ADMINAPPExcepcion;
  //////////////////////////////////////////////////////////////////////
  // configuraciones
  public abstract void confProgramas() throws ADMINGLOExcepcion;
  public abstract void confDominios() throws ADMINGLOExcepcion;
  public abstract void confBalance() throws ADMINGLOExcepcion;
  public abstract void confRetardo() throws ADMINGLOExcepcion;
  public abstract void confAplicacion() throws ADMINGLOExcepcion;
  //////////////////////////////////////////////////////////////////////
  // consultas de configuración
  public abstract void getConfProgramas() throws ADMINGLOExcepcion;
  public abstract void getConfDominios() throws ADMINGLOExcepcion;
  public abstract void getConfBalance() throws ADMINGLOExcepcion;
  public abstract void getConfRetardo() throws ADMINGLOExcepcion;
  public abstract void getConfAplicacion() throws ADMINGLOExcepcion;
  //////////////////////////////////////////////////////////////////////
  // consultas de balance
  public abstract void getPlanificaciones() throws ADMINGLOExcepcion;
  //////////////////////////////////////////////////////////////////////
  // consultas de tareas
  public abstract void getEstadoTareas() throws ADMINGLOExcepcion;
  //////////////////////////////////////////////////////////////////////
  // consultas de operaciones realizadas
  public abstract void getEstadoOperaciones() throws ADMINGLOExcepcion;

 //////////////////////////////////////////////////////////////////////
}