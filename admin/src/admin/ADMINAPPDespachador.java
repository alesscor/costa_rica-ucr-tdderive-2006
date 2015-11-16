package admin;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero<br>
 * @version 1.0<br>
 */
/**
 * Realiza las indicaciones de los componentes locales, de otros despachadores,
 * e instancias locales y remotas de ADMINAPPIniciadores. Estos señalamientos
 * incluyen la creación o terminación de aplicaciones o procesos, recolección
 * de resultados, notificación de estados de una aplicación, etc.
 */
public class ADMINAPPDespachador extends ADMINAPPDespachadorAbs {
  /**
   * 
   * Lista de los controladores que este despachador ha activado.
   */
  private Map controladores;
  public ADMINAPPDespachador(ADMINGLOInfo info0) {
    super("op_usuario",info0);
  }
  private void _inicia(){
    controladores=Collections.synchronizedMap(
        new TreeMap(String.CASE_INSENSITIVE_ORDER));
  }
  public ADMINAPPDespachador(String id0,ADMINGLOInfo info0) {
    super(id0,info0);
  }
  /**
   * Inicia la ejecución de una aplicación.
   * @return La salida en forma de texto.
   * @see admin.ADMINAPPDespachadorAbs#ejecuta(java.lang.String, java.lang.String[])
   */
  public String ejecuta(String alias, String[] parametros)
    throws ADMINAPPExcepcion {
      // crea un ADMINAPPControlador
      // le ordena preparar un trabajo
      /*
       * divide el trabajo
       */
      
      /*
       * preprara la respuesta
       */
    return "";
  }
  /**
   * Se encarga de ejecutar una aplicación descrita en el objeto trabajo.
   * @param trabajo Objeto que describe la aplicación.
   * @throws ADMINAPPExcepcion En caso de error durante la 
   * ejecución de la aplicación.
   * <li>Forma parte del comportamiento 2, donde un despachador
   * local es contactado por un iniciador que crea un
   * controlador dedicado a la ejecución de subtrabajos
   * por medio de envolturas.</li>
   */
  void ejecuta(ADMINAPPTrabajos trabajo) throws ADMINAPPExcepcion{
    ADMINAPPControladores controlador;
    /*
     * Divide el trabajo en caso de necesitarse, obteniendo las 
     * divisiones del trabajo. 
     * Pone en lista de espera.
     */
    // @TODO 2005: Debe verificarse que no se cree un controlador
    // para una tarea que ya lo tenía.
    controlador=info.getPlanificador().nuevoControlador(trabajo);
    /*
     * ahora pone al trabajo (precisamente son los subtrabajos)
     * en la lista de espera.
     */
    controlador.iniciaAplicacion();
    // toma resultados
    // devuelve resultados
  }
  private void addEnLista(ADMINAPPTrabajos trabajo)
    throws ADMINAPPExcPreejecucion{
    
  }
  
}