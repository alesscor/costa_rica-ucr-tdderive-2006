package admin;
import java.io.*;
/**
 * <p>Title: Admin</p>
 * <p>Description: Administración de procesos</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero [alesscor@ieee.org]
 * @version 1.0
 */
/***
 * Ejecuta un proceso y lo acompaña durante su ejecución y hasta su final,
 * recogiendo resultados del proceso y listo para realizar operaciones
 * al proceso cuando sea necesario, según se decida desde el administrador
 * de procesos <b>ADMInventarioProcesos</b>.<br>
 * Es la herramienta con que un objeto de ADMInventarioProcesos trata a un
 * programa, tanto para iniciarlo como para terminarlo, interrumpirlo, y
 * gestionar la entrega de su resultado.
 */
public class ADMINPOLEscoltas {
  //////////////////////////////////////////////////////////////////////
  /**
   * Objeto que sirve de enlace con proceso nativo en ejecución, dedicado
   * a controlar el proceso y obtener información sobre él. Incluye las
   * capacidades de dar entradas al proceso, obtener las salidas del proceso,
   * esperar a que el proceso termine, chequear el estado actual del proceso y
   * destruir o matar el proceso.
   * <li>Cuando se da valor a ligue el proceso nativo se sigue ejecutando
   * asincrónicamente.</li>
   */
  private Process ligue;
  private zzADMINEstadoProcesoAdmin estadoProceso;
  private boolean completado;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public ADMINPOLEscoltas(zzADMINEstadoProcesoAdmin eProceso0) {
    estadoProceso=eProceso0;
    eProceso0.setGestor(this);
    ligue=null;
    completado=false;
  }
  public void ejecutaProceso() throws ADMINGLOExcepcion{
    Runtime rt;
    String[] comandos_parametros;
    String[] ambiente;
    File directorio;
    boolean esperar;
    rt=Runtime.getRuntime();
    comandos_parametros=estadoProceso.getSolicitud().getComandos();
    ambiente=estadoProceso.getSolicitud().getAmbiente();
    directorio=estadoProceso.getSolicitud().getDirectorio();
    esperar=estadoProceso.getSolicitud().getEsperar();
    //System.getProperties()

    // Executes the specified command and arguments in a separate process
    // with the specified environment and working directory. If there is a
    // security manager, its checkExec method is called with the first
    // component of the array cmdarray as its argument. This may result
    // in a security exception. Given an array of strings cmdarray,
    // representing the tokens of a command line, and an array of strings envp,
    // representing "environment" variable settings, this method creates a new
    // process in which to execute the specified command. If envp is null, the
    // subprocess inherits the environment settings of the current process. The
    // working directory of the new subprocess is specified by dir. If dir is
    // null, the subprocess inherits the current working directory of the
    // current process.
    try {
      ligue = rt.exec(comandos_parametros, ambiente, directorio);
      if (esperar) {
        ligue.waitFor();
        completado=true;
      }
    }
    catch (IOException ex) {
      throw new ADMINGLOExcepcion("No se pudo ejecutar el proceso deseado: "+
                             estadoProceso.getSolicitud().getAlias()+"\n");
    }
    catch (InterruptedException ex1) {
      completado=false;
    }
  }
  /**
   * Destruye al proceso.
   */
  public void destruye(){
    ligue.destroy();
  }
  /**
   * Obtiene el valor retornado por el proceso.
   * @return El valor que el proceso devolvió.
   */
  public int valorDevuelto(){
    return ligue.exitValue();
  }
  /**
   * Obtiene el error estándar.
   * @return El error estándar.
   */
  public InputStream getErrores(){
    return ligue.getErrorStream();
  }
  /**
   * Obtiene la entrada estándar.
   * @return La entrada estándar.
   */
  public InputStream getEntradas(){
    return ligue.getInputStream();
  }
  /**
   * Obtiene la salida estándar.
   * @return La salida estándar.
   */
  public OutputStream getSalidas(){
    return ligue.getOutputStream();
  }
  /**
   * Espera que un proceso se complete o sea
   * terminado.
   */
  public void esperaProceso(){
    if(ligue!=null){
      try {
        ligue.waitFor();
        completado=true;
      }
      catch (InterruptedException ex) {
        completado=false;
      }
    }
  }
  //////////////////////////////////////////////////////////////////////
}
