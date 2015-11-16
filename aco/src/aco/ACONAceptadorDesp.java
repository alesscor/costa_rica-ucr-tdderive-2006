package aco;
import org.w3c.dom.*;
import mens.*;
import tables.AbstractSet;
import java.net.*;
import java.io.*;
/**
 * Objeto que registra servicios con características dadas en 
 * descriptores, y que escucha para cada clase
 * un evento sobre un canal de comunicación, y que, después de
 * detectar un evento, crea e unicia un objeto gestor concreto 
 * para que siga con la comunicación.
 * Tiene la limitación de estar atento a solamente una interfaz, pues en el
 * ambiente particular de Java, no se cuenta con un método de interfaz como
 * el select() o waitForMultipleObjects() de POSIX y WIN32 respectivamente.
 * Ello podría lograrse más adelante con Java Native Interface, como
 * recomiendan Merlin Huges, Michael Shoffner y Derek Hamner en Java Network
 * Programming, Manning Publications Co., 1999.<br>
 * Tratando de hacer un análogo incompleto y cambiado para el xinetd, usando
 * java y xml, se va a considerar de cada servicio lo siguiente:
 * <pre>
+-------------------------------------------------------+
| id           disable      no_access      banner_fail  |
| type         socket_type* access_times   per_source   |
|   RPC          stream     log_type       cps          |
|   INTERNAL     dgram      log_on_success max_load     |
|   UNLISTED     raw        log_on_failure groups       |
| flags          seqpacket  rpc_version*   umask        |
|   INTERCEPT  protocol*    rpc_number*    enabled      |
|   NORETRY    wait*        env            include      |
|   IDONLY     user*        passenv        incudedir    |
|   NAMEINARGS group        port*          rlimit_as    |
|   NODELAY    instances    redirect       rlimit_cpu   |
|   KEEPALIVE  nice         bind           rlimit_data  |
|   NOLIBWRAP  server*      interface      rlimit_rss   |
|   SENSOR     server_args  banner         rlimit_stack |
|   IPv4       only_from    banner_success deny_time    |
|   IPv6                                                |
+-------------------------------------------------------+
 * </pre>
 * <br>Las características que se proveen en esta versión de despachador
 * son las siguientes:
 * <br><b>id</b><br>
 * Nombre del servicio, el cual es único.
 * <br><b>socket_type</b><br>
 * Tipo de conexión: orientada a la conexión (stream), basada en
 * datagramas (dgram), acceso directo a capa IP (raw),
 * basado en datagramas con transmisión secuencial-confiable
 * de paquetes (seqpacket).
 * <br><b>protocol</b><br>
 * Protocolo utilizado por el servicio. Revisar /etc/protocols.
 * <br><b>wait</b><br>
 * Determina si un servicio es de hilo único (valor yes) o de múltiples hilos.
 * <br><b>user</b><br>
 * Determina la identificación del usuario para el proceos de servicio.
 * <br><b>server</b><br>
 * Indica el programa a ejecutar (clase a ejecutar) para el servicio.
 * <br><b>port</b><br>
 * Indica el número de puerto para el servicio.
 * <br><br>
 * <li>Maneja una lista de descriptores de servicios.</li>
 * <li>Si se deben manejar datagramas, el aceptador se encarga de esperar, sin
 * excepción, cada paquete y crear para cada uno (i.e. para cada llegada
 * de paquete) un gestor de servicios concreto.</li>
 *<br>
 * <b>Gestores y descriptores</b>
 * Se resalta una diferencia entre gestores y descriptores de servicios.<br>
 * <i>Gestores</i><br>
 * Un gestor de servicios es un objeto que se encarga de dar atención concreta
 * a un servicio dado en un extemo de comunicación tipo
 * cliente-servidor o par-a-par. Pueden haber varios gestores por servicio.<br>
 * <i>Descriptor</i><br>
 * Un descriptor de servicios es un objeto que maneja información sobre
 * la disponibilidad y características de un servicio, además brinda
 * información administrativa para manejar el servicio de una forma
 * centralizada desde un ACONAceptador.<br>
 * <li>Un gestor y un descriptor tienen un hilo de trabajo.</li>
 * <li>Un descriptor es modificado solamente en un hilo.</li>
 * <li>Un descriptor es consultado o accesado desde varios hilos sin
 * ser modificado.</li>
 * <li>Un gestor es modificado solamente en un hilo.</li>
 * <li>Un gestor es consultado o accesado solamente desde un hilo.</li>
 * <li>Hay un gestor nuevo por cada entrada de datagrama. Si se recibieron
 * 100 datagramas, entonces se crearon 100 gestores, un gestor por datagrama.
 * </li>
 */
public class ACONAceptadorDesp extends ACONAceptadorDespAbs {
  /**
   * Servicios registrados para ser activados por el despachador. Cada
   * servicio despierta cuando el despachador decide pasarle el manejo
   * del canal de comunicación. Contiene objetos de clase
   * <b>ACONDescriptorAdmin</b>, heredera de <b>ACONDescriptor</b>.<br>
   * <b>No contiene copias</b>.
   */
  protected AbstractSet serversregistrados;
  /**
   * Información sobre instancias que gestionan servicios.<br>
   * <b>No contiene copias</b>, hay puras referencias o punteros.
   */
  protected AbstractSet gestoresenatencion;
  /**
   * Objeto de lecturas de configuración.
   */
  protected Config configuracion;
  /**
   * Número de evento, esto es para la bitácora.
   */
  protected int num;
  public ACONAceptadorDesp(String archivo) {
    _inicio(archivo,null);
  }
  public ACONAceptadorDesp(Node nodo) {
    _inicio(null,nodo);
  }
  private void _inicio(String archivo,Node nodo){
    configuracion=new Config();
    gestoresenatencion=new AbstractSet(new ACONDescriptorAdmin());
    serversregistrados=new AbstractSet(new ACONDescriptorAdmin());
    if((archivo!=null) || (archivo!="")|| (nodo!=null)){
      try {
        if(nodo!=null){
          configuracion.setFromXMLNode(nodo);
        }else{
          configuracion.setFromXMLURI(archivo);
        }
        ACONBitacora.setConBitacora(configuracion.conbitacora);
        if(configuracion.puerto_bitacora>0){
          ACONBitacora.setPuertoBitacora(configuracion.puerto_bitacora);
        }
      }catch(mens.MENSException ex){
        System.err.println("Sin datos de configuración especiales para el aceptador.");
        System.err.println("Utilizando los valores por omisión.");
      }
    }
  }
  /**
   * Carga lista de servicios usando un archivo.
   * @param archivo Archivo del cual cargar.
   * @throws ACONExcArbitraria Si hay error.
   */
  public final void registraDescriptores(String archivo) throws ACONExcArbitraria{
    _registraDescriptores(archivo,null);
  }
  public final void registraDescriptores(Node nodo) throws ACONExcArbitraria{
    _registraDescriptores(null,nodo);
  }
  private void _registraDescriptores(String archivo,Node nodo) throws ACONExcArbitraria{
    leeACONRegistro infoXML=new leeACONRegistro();
    try {
      if(nodo!=null){
        infoXML.setFromXMLNode(nodo);
      }else{
        infoXML.setFromXMLURI(archivo);
      }
      if(serversregistrados!=null){
        synchronized(serversregistrados){
          serversregistrados.clean();
          serversregistrados=null;
        }
      }
      this.serversregistrados=infoXML.descriptoresregistrados;
    }catch (MENSException ex) {
      throw new ACONExcArbitraria(
          "No se pudieron registrar los descriptores de servicios.",ex);
    }
  }
  /**
   * Agrega un nuevo servicio en la lista de servicios a atender.
   * @param info Información del servicio a registrar.
   * @throws ACONExcArbitraria Si hay error agregando el servicio.
   * @return El servicio agregado y registrado.
   *
   */
  private ACONDescriptorAdmin op_addDescriptor(ACONDescriptor info) throws
      ACONExcArbitraria{
    ACONDescriptorAdmin inforeg=null;
    boolean errores=true;
    inforeg=this.op_findDescriptor(info);
    if(inforeg!=null){
      throw new ACONExcArbitraria("El descriptor ya había sido registrado.");
    }else{
      // el descriptor no había sido registrado
      inforeg=new ACONDescriptorAdmin(info,true);
      inforeg.hiloEspera=null;
      synchronized(serversregistrados){
        serversregistrados.moveFirst();
        /*
         * debe verificarse que:
         * 1. el puerto del servicio no esté asignado a otro servicio activo
         * 2. si un servicio no está activo no debe importar
         */
        if(serversregistrados.addNew(inforeg)){
          errores=false;
        }else{
          throw new ACONExcArbitraria("Error registrando nuevo descriptor"+
                                     " de servicio.");
        }
      }
    }
    return inforeg;
  }
  /**
   * Inicial el trabajo que corresponde a un descriptor de servicio.<br>
   * <li>El descriptor de servicio debe estar registrado en lista
   * de descriptores.</li>
   * <li>El servicio no debe estar abierto.</li>
   * @param info Descriptor del servicio a iniciar.
   * @throws ACONExcArbitraria Si el servicio ya estaba abierto.
   */
  private void op_openDescriptor(ACONDescriptor info)throws ACONExcArbitraria{
    ACONDescriptorAdmin inforeg=null;
    boolean errores=true;
    inforeg=this.op_findDescriptor(info);
    if(inforeg!=null){
      // el descriptor sí está registrado
      try {
        acepta(inforeg);
      }catch (Exception ex) {
        throw new ACONExcArbitraria("Error abriendo descriptor.");
      }
    }else{
      throw new ACONExcArbitraria("El descriptor no ha sido registrado.");
    }
  }
  /**
   * Remueve un descrioptor de servcio de la lista de descriptores
   * de servicio. Así, el servicio que antes se ofrecía, se revoca.
   * <li>Se supone que el servicio ya está registrado.</li>
   * <li>Se supone que el servicio no está abierto.</li>
   * @param info Descriptor del servicio a eliminar.
   * @throws ACONExcArbitraria Si hay error, como cuando el servicio estaba
   * abierto, o como cuando el descriptor no pertenece a
   * un servicio registrado.
   */
  private void op_removeDescriptor(ACONDescriptor info) throws
      ACONExcArbitraria{
    ACONDescriptorAdmin res=null;
    int findBy=0;
    String field="id";
    String value="";
    int index=-1,bookmark=-1;
    // findBy:
    // 0: busca por puerto
    // 1: busca por ID
    // 2: busca por SERVER
    if(info.localport > 0){
      findBy=0;
      field="localport";
      value=""+info.localport;
    }else if((info.id!=null)&&(info.id!="")){
      findBy=1;
      field="id";
      value=info.id;
    }else if((info.server!=null)&&(info.server!="")){
      findBy=2;
      field="server";
      value=info.server;
    }else{
      /**
       * El usuario parece que no quiere nada.
       */
      return;
    }
    synchronized(serversregistrados){
      if(serversregistrados.getCount()<1){
        return;
      }
      bookmark=serversregistrados.getBookmark();
      serversregistrados.moveFirst();
      index=serversregistrados.findFirst(field+"°=°"+value+"°");
      if(index>=0){
        serversregistrados.moveTo(index);
        if(!serversregistrados.delete()){
          throw new ACONExcArbitraria("No se pudo "+
                                         "desregistrar el descriptor");
        }
      }
      if((index>=0)&&bookmark>index){
        bookmark--;
      }
      serversregistrados.moveTo(bookmark);
    }
    return;
  }


  /**
   * Cierra el ACONAceptadorDesp terminando la espera de contactos
   * en el canal de comunicación.
   */
  public void close(){
    op_closeDescriptores();
  }
  /**
   * Cierra los descriptores de servicios que está registrados.
   * <li>Solamente se cierran aquellos servicios
   * que estan abiertos.</li>
   */
  private void op_closeDescriptores(){
    ACONDescriptorAdmin info;
    synchronized(serversregistrados){
      serversregistrados.moveFirst();
      while(!serversregistrados.getEoF()){
        info=(ACONDescriptorAdmin)serversregistrados.getObject();
        try {
          op_closeDescriptor(info);
        }
        catch (ACONExcepcion ex) {
          ex.printStackTrace();
        }
        serversregistrados.moveNext();
      }
    }
  }
  /**
   * Realiza operaciones en los descriptores de servicios.<br>
   * <li>Si viene la combinación con <tt>operaciones.ALL</tt>,
   * se consideran a todos los servicios registrados. </li>
   * <li>Si no se combina con <tt>operaciones.ALL</tt>,
   * se considera solamente el servicio que se indica. </li>
   * <li>Solo se pueden abrir o cerrar servicios que estén
   * registrados.</li>
   * <li>La llave para hacer comparaciones y búsquedas
   * de servicios es el número de puerto;
   * si no hay número de puerto con qué realizar la búsqueda
   * se utiliza el ID de servicio; si no hay ID de servicio
   * entonces se utiliza el campo SERVER.</li><br>
   * <b>Operaciones soportadas</b><br>
   * <li><tt>ALL|OPEN</tt>Abre solamente servicios no abiertos.</li>
   * <li><tt>ALL|CLOSE</tt>Cierra solamente los que estaban abiertos.</li>
   * <li><tt>ADD</tt>Abre solamente si no está abierto.</li>
   * <li><tt>ADD|OPEN</tt>Se asegura de que el servicio esté registrado y que
   * el servicio no esté abierto.</li>
   * <li><tt>OPEN</tt>Abre solamente si el servicio no está abierto.</li>
   * <li><tt>GET</tt>No pasa nada si no se encuentra el objeto buscado.</li>
   * <li><tt>UPDATE</tt>Se asegura de que el servicio esté registrado, y de que
   * éste no esté abierto.</li>
   * <li><tt>CLOSE</tt>Se asegura de que el servicio esté abierto.</li>
   * <li><tt>REMOVE</tt>Se asegura de que el servicio esté registrado y
   * no esté abierto.</li>
   * @param info0 Descriptor a operar.
   * @param op Número de operación. Ver clase operaciones. Puede
   * haber combinaciones de operaciones usando |, mientras tenga
   * sentido la operación que se indica con la combinación.
   * @throws ACONExcArbitraria Si hay error.
   */
  public final void operaDescriptor(ACONDescriptor info0,int op)
      throws ACONExcArbitraria{
    ACONDescriptorAdmin info=null;
    if((info0==null) && (op & operaciones.ALL)==0){
      throw new ACONExcArbitraria("La información del registro es nula.");
    }
    switch(op){
      case operaciones.ALL|operaciones.OPEN:
        manejaEventos();
        break;
      case operaciones.ALL|operaciones.CLOSE:
        op_closeDescriptores();
        break;
      case operaciones.ADD:
        op_addDescriptor(info0);
        break;
      case operaciones.ADD|operaciones.OPEN:
        info = op_addDescriptor(info0);
        if(info!=null){
          op_openDescriptor(info);
        }
        break;
      case operaciones.OPEN:
        op_openDescriptor(info0);
        break;
      case operaciones.GET:
        op_getDesciptor(info0);
        break;
      case operaciones.UPDATE:
        op_updateDescriptor(info0);
        break;
      case operaciones.CLOSE:
        op_closeDescriptor(info0);
        break;
      default:
        throw new ACONExcArbitraria("Operación de descriptor no encontrada.");
    }
  }
  /**
   * Encuentra el descriptor de un servicio registrado.
   * <li>Devuelve un objeto de clase <tt>ACONDescriptorAdmin</tt>
   * que tiene información para la administración del servicio.</li>
   * @param info Descriptor del servicio a buscar.
   * @return Información para la administración del servicio.
   */
  private ACONDescriptorAdmin op_findDescriptor(ACONDescriptor info){
    ACONDescriptorAdmin res=null;
    int findBy=0;
    String field="id";
    String value="";
    int index=-1,bookmark=-1;
    // findBy:
    // 0: busca por puerto
    // 1: busca por ID
    // 2: busca por SERVER
    if(info.localport > 0){
      findBy=0;
      field="localport";
      value=""+info.localport;
    }else if((info.id!=null)&&(info.id!="")){
      findBy=1;
      field="id";
      value=info.id;
    }else if((info.server!=null)&&(info.server!="")){
      findBy=2;
      field="server";
      value=info.server;
    }else{
      /**
       * El usuario parece que no quiere nada.
       */
      return null;
    }
    synchronized(serversregistrados){
      if(serversregistrados.getCount()<1){
        return null;
      }
      bookmark=serversregistrados.getBookmark();
      serversregistrados.moveFirst();
      index=serversregistrados.findFirst(field+"°=°"+value+"°");
      if(index>=0){
        serversregistrados.moveTo(index);
        res=(ACONDescriptorAdmin)serversregistrados.getObject();
      }
      serversregistrados.moveTo(bookmark);
    }
    return res;
  }
  /**
   * Cierra un servicio determinado por su descriptor.<br>
   * <li>Se supone que el servicio está abierto.</li>
   * @param info0 Descriptor de servicio a cerrar.
   * @throws ACONExcArbitraria Si el servicio no está abierto o no
   * se presta.
   */
  private void op_closeDescriptor(ACONDescriptor info0)throws
      ACONExcArbitraria{
      ACONDescriptorAdmin info=null;
      int i=0;
      if(!(info instanceof ACONDescriptorAdmin)){
        info = this.op_findDescriptor(info0);
        if (info == null) {
          throw new ACONExcArbitraria("No se encontró el servicio a cerrar");
        }
      }
      if (info.isAbierto()) {
        try{
          info.close();
        }catch(ACONExcepcion ex){
          System.err.println("No se pudo cerrar el servicio.");
          ex.printStackTrace();
          throw new ACONExcArbitraria("No se pudo cerrar el servicio.");
        }
      }
  }
  /**
   * Actualiza la información que describe un servicio.<br>
   * <li>El descriptor registrado se actualiza con los valores
   * del descriptor dado.</li>
   * <li>Se asume que el servicio está registrado y está cerrado.</li>
   * @param info0 Descriptor del servicio a actualizar.
   * @throws ACONExcArbitraria Si el servicio no se presta o si el servicio
   * está abierto.
   */
  private void op_updateDescriptor(ACONDescriptor info0)throws
      ACONExcArbitraria{
    ACONDescriptorAdmin info=null;
    int i=0;
    info=this.op_findDescriptor(info0);
    if(info==null){
      throw new ACONExcArbitraria("No se encontró el servicio a actualizar");
    }
    /**
     * buscar en el registro la llave info.id
     * y actualizar servicio con los valores de info.
     */
    if(info.isAbierto()){
      // no actualiza pues el servicio todavía está activo.
      throw new ACONExcArbitraria("El servicio a actualizar está abierto");
    }
    try{
      for (i = 0;i<info0.getClass().getFields().length ;i++ ) {
        info0.getClass().getFields()[i].set(info0,
          info0.getClass().getFields()[i].
          get((ACONDescriptor)info));
      }
    }catch(Exception ex){
      System.err.println("Error actualizando descriptor. "+
                         "Revisar UPDATE del registro.");
      throw new ACONExcArbitraria("Error actualizando descriptor. Revisar"+
                                 "UPDATE del regitro.");
    }
  }
  /**
   * Obtiene información de un descriptor de servicio registrado.
   * <li>El servcio puede o no estar abierto.</li>
   * <li>La información queda guardada en info0.</li>
   * @param info0 Descriptor del servicio del cual se quiere obtener
   * información.
   * @throws ACONExcArbitraria Si el servicio no se encuentra.
   */
  private void op_getDesciptor(ACONDescriptor info0) throws
      ACONExcArbitraria{
    ACONDescriptorAdmin info=null;
    int i=0;
    info=this.op_findDescriptor(info0);
    if(info==null){
      throw new ACONExcArbitraria("No se encontró el servicio a actualizar");
    }
    /**
     * buscar en el registro la llave info.id
     * y actualizar info con los valores del servicio.
     */
    try{
      for (i = 0;i<info0.getClass().getFields().length;i++ ) {
        info0.getClass().getFields()[i].set((ACONDescriptor)info,
          info0.getClass().getFields()[i].
          get(info0));
      }
    }catch(Exception ex){
      System.err.println("Error consultando descriptor. "+
                         "Revisar GET del registro.");
      throw new ACONExcArbitraria("Error consultando descriptor. "+
                                 "Revisar GET del registro.");
    }
  }
  /**
   * Realiza utilizando hilos por servicio, esperas de conexiones en
   * cada uno de los servicios registrados, los cuales están presentes
   * en la lista <tt>this.serversregistrados</tt>.
   */
  public final void manejaEventos(){
  /**\-----------------------------------------------------------------\**/
  /**/  int i=0;                                                       /**/
  /**/  synchronized(serversregistrados){                              /**/
  /**/    for(serversregistrados.moveFirst();                          /**/
  /**/        !serversregistrados.getEoF();                            /**/
  /**/        serversregistrados.moveNext()){                          /**/
  /**/      ACONDescriptorAdmin inforeg=                               /**/
  /**/          (ACONDescriptorAdmin)serversregistrados.getObject();   /**/
  /**\-----------------------------------------------------------------\**/
  /**\         realiza accept para cada nodo en un hilo separado.      /**/
  /**\         para cada servicio registrado.                          /**/
  /**/      try{                                                       /**/
  /**/        acepta(inforeg);                                         /**/
  /**/      }catch(ACONExcepcion ex){                           /**/
  /**/        ex.printStackTrace();                                    /**/
  /**/      }                                                          /**/
  /**\-----------------------------------------------------------------\**/
  /**/    }                                                            /**/
  /**/  }                                                              /**/
 /**\-----------------------------------------------------------------\**/
  }
  /**
   * Operación realizada por un hilo exclusivo para la detección
   * de conexiones a un servicio particular.
   * <li>Si el puerto es iniciado con el valor 0, entonces al descriptor
   * se le asigna un puerto libre disponible en el sistema.</li>
   * @param infoDescriptor Información con la cual realizar la
   * escucha más adecuada en el canal indicado.
   * @param base0 Indica el objeto del cual se obtiene el canal de
   * comunicación si éste ya había sido inicializado (ServerSocket o
   * DatagramSocket).
   * @return El canal de comunicación (Socket o DatagramPacket). Si es
   * un DatagramPackete, éste será leído al ser llamado el método
   * receive del ACONGestor.
   * @throws ACONExcepcion Cuando hay error.
   */
  protected Object ev_manejaEventos(ACONDescriptorAdmin infoDescriptor,
      Object[] base0) throws ACONExcepcion{
    Object base = base0[0], canaltrabajo = null;
    //
    // considera el peor caso: que el servicio no ha sido inicializado
//    synchronized(infoDescriptor){
      infoDescriptor.activa(null);
      //
      // I Parte: inicia base, si no había sido iniciado previamente
      try {
        switch (infoDescriptor.socket_type) {
          case ACONDescriptor.DGRAM:
            if (base == null) {
              base = new DatagramSocket(infoDescriptor.localport);
              // por si el puerto fue iniciado en 0.
              infoDescriptor.localport=((DatagramSocket)base).getLocalPort();
              this.setFin();
            }
            else {
              // el base ya había sido iniciado
            }
            break;
          case ACONDescriptor.STREAM:
            if (base == null) {
              base = new ServerSocket(infoDescriptor.localport);
              // por si el puerto fue iniciado en 0.
              infoDescriptor.localport=((ServerSocket)base).getLocalPort();
              this.setFin();
              //((ServerSocket)base).setSoTimeout(...);
            }
            else {
              // el base ya había sido iniciado
            }
            break;
          case ACONDescriptor.SEQPACKET:
            /**@todo estudiar este caso, por ahora no hace nada */
            return null;
        }
      }
      catch (Exception ex) {
        base = null;
        throw new ACONExcArbitraria("No se pudo iniciar extremo de " +
                                       "comunicación para ID " + infoDescriptor.id,
                                       ex);
      }
      // II Parte: escucha por el base, bloqueando
      try {
        if (base != null) {
          //
          // si no hubo error los servicios se consideran activados,
          // de manera que el servicio se considera bien establecido.
          infoDescriptor.activa(Thread.currentThread());
          infoDescriptor.incInstancias();
          infoDescriptor.setCanal(base);
          //
          // todo listo para escuchar el base.
          if (infoDescriptor.socket_type == ACONDescriptor.DGRAM) {
            // con datagramas (no orientado a la conexión)
            // el base está listo para recibir paquetes
            /**
             * deben leerse paquetes en este punto, habrá bloqueo
             * [alesscor031229]
             */
            if ( ( (DatagramSocket) base).isClosed()) {
              System.err.println("Datagrama cerrado, no pudo escuchar.");
              return null;
            }
            byte mensaje[] = new byte[infoDescriptor.tamannoMaximo];
            canaltrabajo = new DatagramPacket(mensaje, infoDescriptor.tamannoMaximo);
            if(infoDescriptor.tiempoEspera>=0){
              ( (DatagramSocket) base).setSoTimeout(infoDescriptor.tiempoEspera);
              if(infoDescriptor.tiempoEspera>0){
                System.err.println("Este valor siempre debería ser cero, pero "+
                                   "aquí es una excepción.");
              }
            }
            ( (DatagramSocket) base).receive( (DatagramPacket) canaltrabajo);
            if (canaltrabajo != null) {
              // carga info de con qué host se hizo contacto,
              // considerando IP y puerto
              infoDescriptor.remotehost = ( (DatagramPacket) canaltrabajo).
                  getAddress().getHostAddress();
              infoDescriptor.remoteport = ( (DatagramPacket) canaltrabajo).getPort();
              ACONBitacora.print("Contacto con host: " + infoDescriptor.remotehost +
                              ", puerto: " + infoDescriptor.remoteport + ".");
            }else{
              System.err.println("Canal de datagramas nulo, no se dejó.");
            }
            /**
             * [alesscor031229]
             */
          }
          else {
            // con streams (orientado a la conexión), hay bloqueo
            if ( ( (ServerSocket) base).isClosed()) {
              return null;
            }
            // el timestamp se va a utilizar para las recepciones
            // y no se usará para el inicio de cada atención.
//            if(infoDescriptor.timestamp>=0){
//              ( (ServerSocket) base).setSoTimeout(infoDescriptor.timestamp);
//              if (infoDescriptor.timestamp > 0) {
//                System.err.println("Este valor siempre debería ser cero, pero " +
//                                   "aquí es una excepción.");
//              }
//            }
            ( (ServerSocket) base).setSoTimeout(0);
            canaltrabajo = ( (ServerSocket) base).accept();
            if (canaltrabajo != null) {
              // carga info de con qué host se conectó, considerando IP y puerto
              infoDescriptor.remotehost = ( (Socket) canaltrabajo).getInetAddress().
                  getHostAddress();
              infoDescriptor.remoteport = ( (Socket) canaltrabajo).getPort();
              ACONBitacora.print("Conectado con host: " + infoDescriptor.remotehost +
                              ", puerto: " + infoDescriptor.remoteport + ".");
            }
            else {
              ACONBitacora.print("No hay base para trabajar.");
            }
          }
        }
      }catch (SocketTimeoutException ex) {
        // todo bien, solo tiempo vencido
        ACONBitacora.print("Tiempo de espera vencido para ID " + infoDescriptor.id +
                        ".");
        throw new ACONExcTemporizacion(ACONExcepcion.CONEXION,
            ex,0,infoDescriptor.localhost,infoDescriptor.localport);
      }catch (IOException ex) {
        if (infoDescriptor.isDetenido) {
          return null;
        }
        throw new ACONExcOmision(ACONExcepcion.CONEXION,ex,0,
            infoDescriptor.localhost,infoDescriptor.localport);
      }catch (Exception ex) {
        if (infoDescriptor.isDetenido) {
          return null;
        }
        throw new ACONExcArbitraria(ACONExcepcion.CONEXION,ex,0,
            infoDescriptor.localhost,infoDescriptor.localport);
      }
      if (base != null) {
        ACONBitacora.print("Sin errores para ID " + infoDescriptor.id + " con " +
                        "clase " + infoDescriptor.server + ".");
      }
      // infoDescriptor.
      ACONBitacora.print("Se ha iniciado conexión para el objeto: " +
                      infoDescriptor.id);
//    }// synchronized
    if (canaltrabajo != null){
      System.err.println("Se ha iniciado conexión para el objeto: " +
                         infoDescriptor.id);
    }// synchronized
    base0[0] = base;
    return canaltrabajo;
  }

  /**
   * Realiza <b>la verdadera apertura</b> del gestor de servicios.
   * @param descriptor Información de la instancia de gestor a iniciar.
   * @param canal Canal por el cual el gestor hace la comunicación, puede ser
   * un Socket o un DatagramPacket.
   * @param canal0 Base del punto de contacto, puede ser un ServerSocket o un
   * DatagramSocket.
   * @throws ACONExcArbitraria Si ocurre error.
   */
  protected void ev_open(ACONDescriptorAdmin descriptor,Object canal,Object canal0[])
  throws ACONExcArbitraria{
    ACONDescriptorAdmin inforeg=null;
    boolean error = false;
    if(canal==null){
      throw new ACONExcArbitraria("Canal nulo para ID "+
                                     descriptor.id+".");
    }
    /*+*\------------------------------------------------------\*+*/
    /*|*\ I Parte: obtiene instancia gestora del servicio      \*|*/
    /*+*\------------------------------------------------------\*v*/
    /*|*/ ACONBitacora.print("Haciéndolo para " + descriptor.id);
    /*|*/ if (descriptor == null) {
    /*|*/   log_onopen(descriptor,canal,canal0,"Descriptor nulo");
    /*|*/   throw new ACONExcArbitraria("Descriptor nulo.");
    /*|*/ }
    /*|*/ ACONGestor Gestor = null;
    /*|*/ try {
    /*|*/   Gestor = (ACONGestor) Class.forName(descriptor.server).
    /*|*/        newInstance();
    /*|*/   ACONBitacora.print("Instancia para " + descriptor.id);
    /*|*/ }catch (InstantiationException ex) {
    /*|*/   log_onopen(descriptor,canal,canal0,"Error de instancia");
    /*|*/   throw new ACONExcArbitraria("Error de instancia para ID " +
    /*|*/                                 descriptor.id + " en clase " +
    /*|*/                                 descriptor.server + ".");
    /*|*/ }catch (IllegalAccessException ex) {
    /*|*/   log_onopen(descriptor,canal,canal0,"Error de acceso a clase");
    /*|*/   throw new ACONExcArbitraria("Error de acceso para ID " +
    /*|*/                    descriptor.id + " en clase " +
    /*|*/                    descriptor.server + ".");
    /*|*/ }catch (ClassNotFoundException ex) {
    /*|*/   log_onopen(descriptor,canal,canal0,"No se encontró la clase");
    /*|*/   throw new ACONExcArbitraria("No se encuentra la clase  " +
    /*|*/                 descriptor.server + " para ID " +
    /*|*/                 descriptor.id + ".");
    /*|*/ }
    /*+*\------------------------------------------------------\*+*/
    /*|*\ II Parte: inicia la instancia gestora del servicio   \*|*/
    /*|*\ los puntos de contacto                               \*|*/
    /*+*\------------------------------------------------------\*v*/
    /*|*/ //oGestor.setLog(ACONBitacora.getLog());
    /*|*\ para agregar en lista de atenciones llamada "gestoresenatencion" */
    /*|*/ inforeg=new ACONDescriptorAdmin(descriptor,false);
    /*|*/ inforeg.hiloEspera=Thread.currentThread();
    /*|*/
    /*|*| importante: asigna un ACONDescriptorAdmin como el ACONDescriptor
    |*|*| del gestor, esto, principalmente, para que el método
    |*|*| ACONDescriptor::close()
    |*|*| sea el evolucionado de un ACONDescriptorAdmin
    |*|*/ Gestor.setInfo(inforeg);
    /*|*/ inforeg.setAtencion(Gestor);
    /*|*/ stat_addGestor(inforeg);
    /*|*/ switch (Gestor.info.socket_type) {
    /*|*/   case ACONDescriptor.DGRAM:
    /*|*/     try {
    /*|*/       Gestor.setDgram(((DatagramSocket) canal0[0]));
    /*|*/       Gestor.setDgramPacket( (DatagramPacket) canal);
    /*|*/     }catch (Exception ex1) {
    /*|*/       log_onopen(inforeg,canal,canal0,"Error de asignación de datagramas");
    /*|*/       throw new ACONExcArbitraria(
    /*|*/           "No se pueden mandar datagramas a la clase  " +
    /*|*/           descriptor.server + " para ID " +
    /*|*/           descriptor.id + ".", ex1);
    /*|*/     }
    /*|*/     break;
    /*|*/   case ACONDescriptor.STREAM:
    /*|*/     try {
    /*|*/       Gestor.setStream( (Socket) canal);
    /*|*/     }catch (ACONExcepcion ex2) {
    /*|*/          log_onopen(inforeg,canal,canal0,"Error de asignación de streams");
    /*|*/          throw new ACONExcArbitraria(
    /*|*/              "No se pueden mandar streams a la clase  " +
    /*|*/              descriptor.server + " para ID " +
    /*|*/              descriptor.id + ".", ex2);
    /*|*/     }
    /*|*/     break;
    /*|*/   case ACONDescriptor.SEQPACKET:
    /*|*\     @todo pendiente de hacer: estudiar bien este caso. */
    /*|*/     return;
    /*|*/   default:
    /*|*/     break;
    /*|*/ } // del switch
    /*|*/ ACONBitacora.print("Sin errores para ID " + descriptor.id + " con " +
    /*|*/                 "clase " + descriptor.server + ".");
    /*|*/ log_onopen(inforeg,canal,canal0,"Sin problemas, iniciando servicio");
    /*+*\------------------------------------------------------\*+*/
    /*|*\ III Parte: inicia el trabajo del gestor de servicios.\*|*/
    /*+*\------------------------------------------------------\*+*/
    /*|*\ listo el Gestor con su objeto de                     \*|*/
    /*|*\ comunicación, recordar que solo son casos            \*|*/
    /*|*\ DGRAM y STREAM                                       \*|*/
    /*|*/ final ACONGestor gestorreferencia=Gestor;            /*|*/
    /*|*/ Runnable rungestor=new Runnable(){                   /*|*/
    /*+*\------------------------------------------------------\*+*/
    /*(*/ public void run(){                                   /*(*/
      try{
        /*)*/gestorreferencia.open(); /*)*/
      }catch(Exception ex){
        /** @todo buscar una forma asincronica para reportar el error. */
      }
    /*(*/ }                                                    /*(*/
    /*+*\------------------------------------------------------\*+*/
    /*|*/ };                                                   /*|*/
    /*|*/ if(inforeg.wait){                                    /*|*/
    /*|*/   rungestor.run();                                   /*|*/
    /*|*/ }else{                                               /*|*/
    /*|*/   inforeg.hiloEspera=new Thread(rungestor,           /*|*/
    /*|*/          descriptor.id+"."+                          /*|*/
    /*|*/          descriptor.getInstancias());                /*|*/
    /*|*/   inforeg.hiloEspera.start();                        /*|*/
    /*|*/   ACONBitacora.print("Iniciando el hilo "+           /*|*/
    /*|*/         inforeg.hiloEspera.getName());               /*|*/
    /*|*/ }                                                    /*|*/
    /*+*\------------------------------------------------------\*+*/
    inforeg=null;
  }
  /**
   * Examina info del gestor de servicios por el que se esperan
   * conexiones. ACONDescriptorAdmin::info tiene la suficiente
   * información para tomar decisiones.
   * Esta operación no decide nada. Debe sobrecargarse.
   * @param reg La info sobre el gestor de servicios a examinar.
   * @return Si se acepta proseguir con la creación del canal de
   * comunicación.
   * <br>Patrón <b>Strategy</b> incluído en método <tt>acepta</tt>.
   */
  protected boolean preaccept(ACONDescriptorAdmin reg){
    return true;
  }
  /**
   * Examina info del gestor de servicios y del canal de
   * comunicación para la conexión luego de obtener
   * a éste último. ACONDescriptorAdmin::info tiene la suficiente información
   * para tomar decisiones.
   * Esta operación no decide nada. Debe sobrecargarse.
   * @param reg Info del gestor de servicios a examinar.
   * @param canal El canal de comunicación a examinar, puede ser un
   * DatagramPacket o un Socket, en el caso que el gestor sea para
   * datagramas o streams respectivamente.
   * @param base La base del canal de comunicación, con información importante
   * que se puede utilizar para verificación.
   * @return Si se acepta proseguir con la comunicación
   * usando el canal.
   * <br>Patrón <b>Strategy</b> incluído en método <tt>acepta</tt>.
   */
  protected boolean postaccept(ACONDescriptorAdmin reg,Object canal,Object base[] ){
    return true;
  }
  /**
   * Obtiene el estado de los descriptores de servicio registrados.
   * @return Hilera con el estado en formato XML.
   */
  public final String getStatusDescriptores(){
    String res="<?xml version=\"1.0\"?>";
    res+="\n<estadodescriptores>";
    ACONDescriptorAdmin info;
    synchronized(serversregistrados){
      serversregistrados.moveFirst();
      while(!serversregistrados.getEoF()){
        info=(ACONDescriptorAdmin)serversregistrados.getObject();
        res+=info.getStatus();
        res+="\n";
        serversregistrados.moveNext();
      }
    }
    res+="</estadodescriptores>";
    return res;
  }
  /**
   * Agrega información de un gestor que presta un servicio.
   * @param infoGestor Gestor a agregar a la lista de gestores.
   */
  private void stat_addGestor(ACONDescriptorAdmin infoGestor){
    synchronized(gestoresenatencion){
      gestoresenatencion.addNew(infoGestor);
    }
  }
  /**
   * Obtiene el estado de los gestores de servicio activados.
   * @return Hilera con el estado en formato XML.
   */
  public String getStatusGestores(){
    String res="<?xml version=\"1.0\"?>";
    res+="\n<estadogestores>";
    ACONDescriptorAdmin info;
    synchronized(gestoresenatencion){
      gestoresenatencion.moveFirst();
      while(!gestoresenatencion.getEoF()){
        info=(ACONDescriptorAdmin)gestoresenatencion.getObject();
        res+=info.getStatus();
        res+="\n";
        gestoresenatencion.moveNext();
      }
    }
    res+="</estadogestores>";
    return res;
  }
  /**
   * <br>Clase de configuración.</br>
   */
  protected class Config extends mens.MENSMensaje{
    //////////////////////////////////////////////////////////////////////
    private int puerto_bitacora;
    private boolean conbitacora;
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    public Config(){
      puerto_bitacora=ACONBitacora.getPuertoBitacora();
      conbitacora=ACONBitacora.getConBitacora();
    }
    public boolean isVacio() {
      return false;
    }
    protected String getXMLContainedElements() {
      String xml="";
      if(this.puerto_bitacora!=0){
        xml += "\n<puerto_bitacora>" + this.puerto_bitacora + "</puerto_bitacora>";
      }
      if(conbitacora){
        xml += "\n<con_bitacora>true</con_bitacora>";
      }
      xml="\n<aco>"+xml+"</aco>\n";
      return xml;
    }
    protected void setContentFromDoc(Node node, int[] nerr, String[] merr) {
      String valor="";
      if(node.getNodeName().compareToIgnoreCase("aco")!=0){
        node=MENSMensaje.getNextElement(node,"aco");
      }
      if(node!=null){
        valor=getElementText(node,"con_bitacora");
        if(valor==""){
          conbitacora=false;
        }
        if(valor.compareToIgnoreCase("true")==0){
          conbitacora=true;
        }else{
          if(valor.compareToIgnoreCase("verdadero")==0){
            conbitacora=true;
          }
        }
        try{
          this.puerto_bitacora = Integer.parseInt(getElementText(node, "puerto_bitacora"));
        }catch(Exception ex){
          this.puerto_bitacora=0;
        }
      }
    }
    protected void toleraXML(int[] nerr, String[] merr) {
    }
  }
}