package admin;
/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
class zzADMINDespachador extends zzADMINDespachadorAbs {
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  final private static String nombre="despachador";
  // Información de la aplicación.
  ADMINGLOInfo info;
  //////////////////////////////////////////////////////////////////////
  // c o n s t r u c t o r e s
  public zzADMINDespachador(ADMINGLOInfo info0) {
    super(nombre);
    _inicia(info0);
  }
  public zzADMINDespachador(String id0,ADMINGLOInfo info0) {
    super(id0);
    _inicia(info0);
  }
  private void _inicia(ADMINGLOInfo info0){
    info=info0;
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
  /**
   * Ejecuta un programa.<br>
   * <li>Se asume que el solicitante ya ha sido registrado.</li>
   * <li>Se asume que los archivos de entrada ya han sido preparados.</li>
   * <li>Se asume que el directorio de trabajo ya ha sido preparado.</li>
   * @param id_tarea Tarea cuya salida debe operarse.
   * @param comando Alias del programa a ejecutar.
   * @param parametros Parámetros del programa.
   * @return El resultado del programa.
   * @throws admin.MINERExcepcion Si hay error por parte de la aplicación.
   * @throws admin.ADMINExcepcion Si hay error por parte del sistema subyacente.
   */
  public String ejecuta(String id_tarea,String comando, String parametros)
      throws admin.ADMINAPPExcepcion, admin.ADMINGLOExcepcion {
    PERSCoordinacion.Programas progra=null;
    // busca en lista de programas
    progra=(PERSCoordinacion.Programas)(
        (java.util.Map)(((PERSAmbiente.Computadora)
                         info.getComputadora()).getProgramas())).get(comando);
    if(progra==null){
      // no se encuentra el programa
      throw new ADMINAPPExcepcion("No se encuentró el programa solicitado.");
    }
    //
    // ejecuta el programa solicitado
    //

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