/*
 * Created on 02/08/2006
 *
 */

package admin;
import java.util.Date;
import corabitas.CORACliente;
/**
 * @author Alessandro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ADMINGLOReportero {
  public corabitas.CORACliente bitacoreador=null;
  /**
   * Iniciador del reportero.
   */
  public ADMINGLOReportero() {
    super();
    bitacoreador=new CORACliente("..\\config\\corabitas-config.txt");
  }
  public void setUbicacionArchivo(){
    
  }
  /**
   * Agrega una entrada a la bitácora con el siguiente formato:<br/>
   * <code>yyyy/mm/dd;hh:mm:ss:mil;objeto;texto\n</code><br/>
   * Por ahora los objetos son los siguientes:<br/>
   * <li>programa</li>
   * <li>despachador</li>
   * <li>controlador</li>
   * <li>planificador</li>
   * <li>lector</li>
   * @param objeto Nombre del objeto que se quiere describir.
   * @param texto Un evento descrito en el que interviene el objeto.
   */
  public synchronized void println(String objeto,String texto){    
    String linea="";
    linea="("+objeto+")"+ ";"+texto;
    try{
      bitacoreador.escribe(linea);
    }catch(Exception e){
      // nada
    }
    linea=(new Date()).toString() + linea;
    System.out.println(linea);
  }
  
}
