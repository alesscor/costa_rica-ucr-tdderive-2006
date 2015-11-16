package aco;

/**
 * Guarda informaci�n importante para la gesti�n de un servicio.
 */
public class ACONDescriptor {
  /**
   * Nombre del servicio, el cual es �nico.
   */
  public String id;
  /**
   * Tipo de conexi�n: orientada a la conexi�n (stream),
   * basada en datagramas (dgram), acceso directo a capa
   * IP (raw), basado en datagramas con transmisi�n
   * secuencial-confiable de paquetes (seqpacket).
   */
  public int socket_type;
  /*+-------------------------------------------------+*/
  /*|*/   public final static int STREAM=1;    // 2^0 |
  /*|*/   public final static int DGRAM=2;     // 2^1 |
  /*|*/   public final static int SEQPACKET=4; // 2^2 |
  /*+-------------------------------------------------+*/
  /**
   * Protocolo utilizado por el servicio.
   * Revisar /etc/protocols.
   */
  public String protocol=null;
  /**
   * Determina si un servicio es de hilo �nico (valor yes) o de
   * m�ltiples hilos.<br>
   * <li>verdadero para volver a las escuchas luego de servir
   * (hilo �nico).</li>
   */
  public boolean wait=false;
  /**
   * Determina la identificaci�n del usuario para
   * el proceos de servicio.
   */
  public String user=null;
  /**
   * Indica el programa a ejecutar (clase a ejecutar)
   * para el servicio.
   */
  public String server=null;
  /**
   * Indica el n�mero de puerto local.
   */
  public int localport=-1;
  /**
   * Indica el host local de comunicaci�n.
   */
  public String localhost=null;
  /**
   * Indica el host remoto de comunicaci�n.
   */
  public String remotehost=null;
  /**
   * Tiempo de espera inicial para un read.
   */
  public int tiempoEspera=0;
  /**
   * Tiempo de espera para un connect.
   */
  public int tiempoConexion=0;  
  /**
   * Indica el puerto remoto de comunicaci�n.
   */
  public int remoteport=-1;
  // Propiedades necesarias para datagramas y streams
  public boolean isDestinoConocido=false;
  public boolean isPuedenHaberDuplicados=false;
  public boolean isOrdenados=false;
  public boolean isControlFlujo=false;
  public boolean isPuedenHaberPerdidos=false;
  public boolean isDeCualquiera=false;
  public boolean isDetenido=false;
  public int tamannoMaximo=100000;
  public Object objNotificacion=null;
  public String log="";
  /**
   * Objetos navegables (atributos de agregaci�n) del
   * gestor a crear. Hay que protegerlos con sincronizaci�n
   * si �stos son usados por varios gestores en forma simult�nea.
   */
  public Object[] aoNavegables;
  /**
   * No hace nada, se supone que es evolucionado por un
   * <tt>ACONInfoRegistro</tt>, que ser� eventualmente
   * llamado por <tt>ACOGestor.close()</tt>.
   * @throws ACONExcepcion En caso de error.
   */
  public void close()throws ACONExcepcion{
  }
}