package admin;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
/**
 * <p>Title: Administraci�n de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero<br>
 * @version 1.0<br>
 */
/**
 * Realiza las indicaciones de los componentes locales, de otros despachadores,
 * e instancias locales y remotas de ADMINAPPIniciadores. Estos se�alamientos
 * incluyen la creaci�n o terminaci�n de aplicaciones o procesos, recolecci�n
 * de resultados, notificaci�n de estados de una aplicaci�n, etc.
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
   * Inicia la ejecuci�n de una aplicaci�n.
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
   * Se encarga de ejecutar una aplicaci�n descrita en el objeto trabajo.
   * @param trabajo Objeto que describe la aplicaci�n.
   * @throws ADMINAPPExcepcion En caso de error durante la 
   * ejecuci�n de la aplicaci�n.
   * <li>Forma parte del comportamiento 2, donde un despachador
   * local es contactado por un iniciador que crea un
   * controlador dedicado a la ejecuci�n de subtrabajos
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
    // para una tarea que ya lo ten�a.
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