package admin;

/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */

class zzADMINDespachadorProxy extends zzADMINDespachadorAbs {
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////
  // c o n s t r u c t o r e s
  public zzADMINDespachadorProxy() {
  }

  public zzADMINDespachadorProxy(String id0) {
    super(id0);
  }
  //////////////////////////////////////////////////////////////////////
  // c o n f i g u r a c i ó n
  public void confBalance() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  public void confRetardo() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  public void confProgramas() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  public void confDominios() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  public void confAplicacion() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  //////////////////////////////////////////////////////////////////////
  // e j e c u c i ó n
  public String ejecuta(String id_tarea,String comando, String parametros)
     throws admin.ADMINAPPExcepcion, admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
    return "";
  }
  public void entregaResultados() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  public void traeResultados() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  //////////////////////////////////////////////////////////////////////
  // l e c t u r a s   e s t a d o   l o c a l
  public void getConfProgramas() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  public void getConfBalance() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  public void getConfRetardo() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  public void getEstadoOperaciones() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  public void getConfAplicacion() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  public void getPlanificaciones() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  public void getConfDominios() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
  public void getEstadoTareas() throws admin.ADMINGLOExcepcion {
    /**@todo Implement this admin.ADMINDespachadorAbs abstract method*/
  }
}
