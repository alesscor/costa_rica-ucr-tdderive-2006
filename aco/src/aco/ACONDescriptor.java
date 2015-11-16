package aco;

/**
 * Guarda información importante para la gestión de un servicio.
 */
public class ACONDescriptor {
  /**
   * Nombre del servicio, el cual es único.
   */
  public String id;
  /**
   * Tipo de conexión: orientada a la conexión (stream),
   * basada en datagramas (dgram), acceso directo a capa
   * IP (raw), basado en datagramas con transmisión
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
   * Determina si un servicio es de hilo único (valor yes) o de
   * múltiples hilos.<br>
   * <li>verdadero para volver a las escuchas luego de servir
   * (hilo único).</li>
   */
  public boolean wait=false;
  /**
   * Determina la identificación del usuario para
   * el proceos de servicio.
   */
  public String user=null;
  /**
   * Indica el programa a ejecutar (clase a ejecutar)
   * para el servicio.
   */
  public String server=null;
  /**
   * Indica el número de puerto local.
   */
  public int localport=-1;
  /**
   * Indica el host local de comunicación.
   */
  public String localhost=null;
  /**
   * Indica el host remoto de comunicación.
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
   * Indica el puerto remoto de comunicación.
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
   * Objetos navegables (atributos de agregación) del
   * gestor a crear. Hay que protegerlos con sincronización
   * si éstos son usados por varios gestores en forma simultánea.
   */
  public Object[] aoNavegables;
  /**
   * No hace nada, se supone que es evolucionado por un
   * <tt>ACONInfoRegistro</tt>, que será eventualmente
   * llamado por <tt>ACOGestor.close()</tt>.
   * @throws ACONExcepcion En caso de error.
   */
  public void close()throws ACONExcepcion{
  }
}