/**
 * Paquete de provisión de comunicaciones.
 */
package aco;

/***
 * Espera pasivamente una forma de conexión e inicializa el
 * gestor adecuado. Crea todo lo que el
 * Los gestores son quienes transmiten datos
 * entre extremos de comunicación.
<pre>
 Ojo, desde aquí ya se necesita una estructura de mensaje en XML.
&lt;?xml version="1.0"?>
&lt;mensaje>
&lt;nombreservicio>Nombre&lt;/nombreservicio>
&lt;contenido/>
&lt;/mensaje>
Con el formato anterior, deben ser reconocidos y distribuidos por el
 ACONDespachador.
 </pre>
 *
 */
public class ACONAceptador {
  /**
   * Nombre de la clase del manejador de servicios concreto
   * del objeto a crear cuando se detecta evento de conexión.
   */
  private String nombreManejadorConcreto;
  /**
   * Nombre del servicio del manejador de servicios concreto.
   */
  private String nombreServicio;
  /**
   * Puerto al cual se conecta a esperar conexiones.
   */
  private int puerto;
  /**
   * Nombre del nodo para atender conexiones.
   */
  private String nodoid;

  public ACONAceptador() {
  }
  /**
   * Inicializa el extremo pasivo de la conexión.
   * Se registra para el despachador.
   */
  public void open(){
    
  }
  /**
   * Crea un nuevo ManejadorConcreto que se dedique a
   * trabajar este extremo de la conexión y lo pone a trabajar.
   */
  public void accept(){
    // fabrica el manejador de servicios concreto a la hora
    // que se tenga que atender.

  }
}