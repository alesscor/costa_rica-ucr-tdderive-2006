package admin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import tdutils.Invocable;
import admin.PERSCoordinacion.Sub_trabajos;

/**
 * <p>Title: Administraci�n de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Ejecuta un proceso y lo acompa�a durante su ejecuci�n y hasta su final,
 * recogiendo resultados del proceso y listo para realizar operaciones
 * al proceso cuando sea necesario, seg�n se decida desde el administrador
 * de procesos <b>ADMInventarioProcesos</b>.<br>
 * Es la herramienta con que un objeto de ADMInventarioProcesos trata a un
 * programa, tanto para iniciarlo como para terminarlo, interrumpirlo, y
 * gestionar la entrega de su resultado.
 * Hurra!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
public class ADMINPOLEnvolturas extends PERSCoordinacion.Envolturas
  implements Invocable {

  /**
   * Construye una envoltura a responsabilizarse del
   * cumplimiento del subtrabajo dado.
   * <li>A la envoltura se le pone un nombre �nico.</li>
   * @param subtra
   */
  public ADMINPOLEnvolturas(ADMINAPPISub_trabajos subtra) {
    super((Sub_trabajos)subtra);
  }

  /**
   * Realiza la ejecuci�n del subtrabajo. Se supone que esto va
   * a ser ejecutado por un hilo dedicado.<br/>
   * <2006 />
   * �El hilo dedicado va a enviar los archivos respectivos
   * a la computadora coordinadora del subtrabajo?<br/>
   * Lo anterior mejor no para no cargar mucho la interfaz de
   * esta clase.<br/>
   * En lugar de ello se va a guardar el estado pertinente del
   * subtrabajo en la base de datos. De esta manera se asume que
   * el lector va a darse cuenta de qu� subtrabajos est�n terminados
   * y entonces iniciar la devoluci�n de resultados. N�tese que este
   * trabajo es completamente an�logo a la exportaci�n de los archivos
   * de entrada de los subtrabajos, solamente que ahora se tiene
   * que realizar tal transacci�n en el feliz t�rmino de la aplicaci�n,
   * o de almenos del trinfo de terminar una parte del trabajo 
   * solicitado.
   * @see tdutils.Invocable#invoca(java.lang.Object[])
   */
  public Object invoca(Object[] args) throws Exception {
    String comando=null;
    String[] comandos_parametros=null;
    String[] ambiente=null/*this._subtrabajo.getAmbiente()*/;
    File directorio;
    Runtime rt;
    rt=Runtime.getRuntime();
    comando= this._subtrabajo.getTarea().getPrograma().getRuta()+ADMINAPPIniciador.APP_SEPARADORARGUMENTOS+
    this._subtrabajo.getComando();
    comandos_parametros=comando.split(""+ADMINAPPIniciador.APP_SEPARADORARGUMENTOS);
    directorio=this._subtrabajo.getDirSubtrabajoDir();
    try {
      this.ligue = rt.exec(comandos_parametros, ambiente, directorio);
      this.ligue.waitFor();
      this.siCompletado=true;
    }
    catch (IOException ex) {
      this.ligue=null;
      throw new ADMINGLOExcepcion("No se pudo ejecutar el proceso deseado: "+
                             this._programa.getAlias());
    }
    catch (InterruptedException ex1) {
      this.siCompletado=false;
    }
    /*
     * Antes de confirmar este c�digo, se verifica si vale la pena
     * llevarse a cabo en una llamada de mayor nivel para as� desde
     * tal nivel verificar este llamado. Tambi�n para tener a este
     * c�digo como parte del algoritmo principal, en otras palabras,
     * QUE NO SE OLVIDE COMO PARTE DE LOS DETALLES SINO QUE PERMANEZCA
     * VISIBLE COMO PARTE DE LO PRINCIPAL (first said by alessandr�n)
     *     if(this.siCompletado){
     *       this._subtrabajo.setEstadoSubtrabajo("!!!TERMINATION���");
     *       this._subtrabajo.write();
     *     }
    */
    return null;
  }

  /**
   * Objeto que sirve de enlace con proceso nativo en ejecuci�n, dedicado a controlar el proceso y obtener informaci�n sobre �l. Incluye las capacidades de dar entradas al proceso, obtener las salidas del proceso, esperar a que el proceso termine, chequear el estado actual del proceso y destruir o matar el proceso. <li>Cuando se da valor a ligue el proceso nativo se sigue ejecutando asincr�nicamente.</li>
   */
  private Process ligue;
  /**
   * @deprecated
   * Indica si es un proceso que se invit� en otra computadora.
   */
  private boolean siInvitado;
  /**
   * Indica si el proceso ejecuta un subtrabajo remoto.
   */
  private boolean siRemoto;
  /**
   * Indica si el proceso se ha completado.
   */
  private boolean siCompletado;
  /**
   * Espera que un proceso se complete o sea
   * terminado.
   */
  public void esperaProceso(){
    if(this.ligue!=null){
      try {
        this.ligue.waitFor();
        this.siCompletado=true;
      }
      catch (InterruptedException ex) {
        this.siCompletado=false;
      }
    }
  }

  /**
   * Obtiene la entrada est�ndar.
   * @return La entrada est�ndar.
   */
  public InputStream getEntradas(){
    return ligue.getInputStream();
  }

  /**
   * Obtiene el error est�ndar.
   * @return El error est�ndar.
   */
  public InputStream getErrores(){
    return ligue.getErrorStream();
  }

  /**
   * Obtiene la salida est�ndar.
   * @return La salida est�ndar.
   */
  public OutputStream getSalidas(){
    return ligue.getOutputStream();
  }
  /**
   * Obtiene la bit�cora est�ndar.
   * @return La bit�cora est�ndar.
   */
  public OutputStream getBitacora(){
    return null;
  }

  /**
   * Obtiene el valor retornado por el proceso.
   * @return El valor que el proceso devolvi�.
   */
  public int valorDevuelto(){
    return this.ligue.exitValue();
  }  
  public boolean getSiCompletado(){
    return this.siCompletado;
  }
}