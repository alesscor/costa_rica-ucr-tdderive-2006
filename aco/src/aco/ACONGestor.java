package aco;
import java.net.*;
import java.io.*;

/**
 * Clase abstracta gestora de servicios de red. El gestor define una
 * parte del servicio en un sistema de red, sea el cliente o el servidor, o
 * ambos roles en forma simultánea (C/S & P2P).
 * Un gestor de servicios procesa las solicitudes de datos que vienen
 * de una pareja de comunicación desde un canal de comunicación.<br>
 * <li>Las clases que extiendan esta definición debe implementar el método
 * open().</li>
 * <li>Un objeto de esta clase es activado e inicializado mediante un
 * <tt>Aceptador</tt> o un <tt>Conector</tt>.</li>
 */
public abstract class ACONGestor {
  /**
   * Máximo tamaño del buffer usado en la lectura de streams.
   */
  final static int MAXBUFF=1024;
  /**
   * Valores importanes del <b>ACONGestor</b>.
   */
  public ACONDescriptor info;
  /**
   * Canal por el cual se trabaja orientado a la conexión.
   */
  private Socket stream;
  /**
   * Canal por el cual se trabaja con datagramas.
   */
  private DatagramSocket dgram;
  /**
   * Último paquete de datagrama recibido por el objeto.
   */
  private java.net.DatagramPacket dgrampack;
  /**
   * Inicializador de canal para servidores.
   */
  private ServerSocket connect;
  /**
   * Flujo de entrada.
   */
  private DataInputStream fdInput;
  /**
   * Flujo de salida.
   */
  private java.io.DataOutputStream fdOutput;
  /**
   * Tiempo de vencimiento para esperas de eventos como conexiones,
   * recepciones de mensajes y envíos de mensajes.<br>
   * Su intención es soportar el reporte de errores por temporización.
   */
  private int timestamp;
  private int tiempoConexion;
  private String bloqueos_verfin;
//  /**
//   * Indica el puerto a responder por datagramas.<br>
//   * <b>Esta instancia va a vivir para un hilo de atención.</b>
//   * <li>Su valor es asignado en cada receive.</li>
//   */
//  private int dgramlastport;
  /**
   * Obtiene el puerto local.
   * @return Puerto local.
   */
  final public int getPort(){
    return info.localport;
  }
  /**
   * Obtiene el puerto remoto.
   * @return Puerto remoto.
   */
  final public int getRemotePort(){
    return info.remoteport;
  }
  /**
   * Obtiene el nombre o IP del host local.
   * @return Nombre o IP del host local.
   */
  final public String getHostName(){
    return info.localhost;
  }
  /**
   * Obtiene el nombre o IP del host remoto.
   * @return Nombre o IP del host remoto.
   */
  final public String getRemoteHostName(){
    return info.remotehost;
  }
  /**
   * Obtiene la dirección del extremo de conexión local.
   * @return Dirección interpretada como InetSocketAddress.
   */
  final public java.net.SocketAddress getSocketAddress(){
    return new java.net.InetSocketAddress(info.localhost,info.localport);
  }
  /**
   * Obtiene la dirección del extremo de conexión remota.
   * @return Dirección interpretada como InetSocketAddress.
   */
  final public java.net.SocketAddress getRemoteSocketAddress(){
    return new java.net.InetSocketAddress(info.remotehost,info.remoteport);
  }
  /**
   * Obtiene el socket orientado a la conexión.
   * @return El Socket de comunicación.
   */
  final public Socket getStream(){
    return stream;
  }
  /**
   * Asigna un socket de comunicación orientado a la conexión.
   * @param par0 El socket orientado a la conexión.
   * @throws ACONExcArbitraria Error al asignar socket.
   */
  final public void setStream(Socket par0)throws ACONExcArbitraria{
    stream=par0;
    if(par0==null){
      return;
    }
    try {
      if(!par0.isBound()&&!par0.isConnected()){
        par0.bind(this.getLocalSocketAddress());
      }
      if(par0.isConnected()){
        this.setEntradaSalida();
      }
    }
    catch (Exception ex) {
      throw new ACONExcArbitraria(ACONExcepcion.CONEXION,ex,0,this.getHostName(),this.getPort());
    }
  }
  /**
   * Obtiene el socket no orientado a la conexión.
   * @return El socket datagrama.
   */
  final public DatagramSocket getParDatagrama(){
    return dgram;
  }
  final public DatagramPacket getDatagramPack(){
    return dgrampack;
  }
  /**
   * Asigna un socket de datagrama.
   * @param par0 El socket datagrama.
   */
  final public void setDgram(DatagramSocket par0){
    dgram=par0;
  }
  /**
   * Asigna un paquete recibido en el canal de comunicación
   * @param pak Paquete recibido.
   */
  final public void setDgramPacket(DatagramPacket pak){
    dgrampack=pak;
  }
  /**
   * Obtiene el socket servidor de la conexión.
   * @return El socket servidor.
   */
  final public ServerSocket getConnection(){
    return connect;
  }
  /**
   * Asigna el socket servidor de la conexión.
   * @param par0 El socket servidor de la conexión.
   */
  final public void setConnect(ServerSocket par0){
    connect=par0;
  }
  /**
   * Inicializador. Inicializa un tipo de interfaz de comunicación nula.
   */
  private void _inicializador(){
    this.fdInput=null;
    this.fdOutput=null;
    stream=null;
    dgram=null;
    connect=null;
    bloqueos_verfin="sin iniciar";    
//    dgramlastport=-1;
  }
  /**
   * Constructor. Inicializa un tipo de interfaz de comunicación nula.
   */
  public ACONGestor(){
    _inicializador();
  }
  /**
   * Constructor con información.
   * Inicializa un tipo de interfaz de comunicación nula.<br>
   * El timestamp indicado por el descriptor puede
   * cambiar con la interfaz que provee esta clase.
   * @param info0 Información inicial del objeto.
   */
  public ACONGestor(ACONDescriptor info0){
    _inicializador();
    setInfo(info0);
    setTiempoEsperaGestor(info0.tiempoEspera);
    setTiempoConexion(info0.tiempoConexion);
  }
  public void setTiempoConexion(int tiempoConexion0) {
    if(tiempoConexion0<0){
      tiempoConexion=0;
      return;
    }
    tiempoConexion=tiempoConexion0;
  }
  //  /**
//   * Asina una dirección al Gestor actual.
//   * @param addr Dirección formada por host:puerto:puertolocal.
//   * @throws ACONExcArbitraria Error si la dirección está mal formada.
//   */
//  public final void setDireccion(String addr) throws ACONExcArbitraria{
//    int pos0=0,pos1=0;
//    try{
//      pos0 = addr.indexOf(":");
//      pos1 = addr.indexOf(":",pos0+1);
//      if(pos1<0){
//        pos1=addr.length();
//      }
//      info.remotehost = addr.substring(0, pos0);
//      info.remoteport = Integer.parseInt(addr.substring(pos0 + 1,pos1));
//      if(pos1<addr.length()){
//        info.localport = Integer.parseInt(addr.substring(pos1 + 1));
//      }else{
//        info.localport = 0;
//      }
//    }
//    catch(Exception ex){
//      throw new ACONExcArbitraria("Error al derivar dirección y puerto\n",ex);
//    }
//  }
//  /**
//   * Método utilizado por el objeto <b>Despachador</b> para contactar de vuelta
//   * un objeto <b>Aceptador</b>.
//   * @throws ACONExcepcion Ver clase TDAceptador.
//   */
//  public abstract void acepta() throws ACONExcepcion,Exception;
  /**
   * Método utilizado por el objeto <b>Despachador</b> para contactar de vuelta
   * un objeto <b>Conector</b> que hizo conexión asincrónica.
   * @throws ACONExcepcion En caso de error.
   */
  public abstract void completa() throws ACONExcepcion,Exception;
  /**
   * Método utilizado por el objeto <b>Despachador</b> para contactar de vuelta
   * un objeto <b>GestorServicios</b>.
   * @throws ACONExcepcion En caso de error.
   */
  public abstract void open() throws ACONExcepcion,Exception;
//  /**
//   * Método utilizado por el objeto <b>Despachador</b> para
//   * inicializar el manejo de eventos realizado por
//   * un <b>GestorServicios</b>.
//   * @throws ACONExcepcion En caso de error.
//   */
//  public abstract void manejaEvento() throws ACONExcepcion;
//  /**
//   * Método utilizado por el objeto <b>Despachador</b> para realizar el manejo
//   * concreto de una conexión.
//   * @throws ACONExcepcion En caso de error.
//   */
//  public abstract void sirvase() throws ACONExcepcion;
  /**
   * Envía un mensaje a un host remoto. La información de a cuál host se
   * envía el mensaje se asigna en la inicialización del objeto.
   * @param mensaje Mensaje a enviar.
   * @throws ACONExcArbitraria En caso de error en el envío.
   * @throws ACONExcOmision En caso de error en el envío.
   */
  public final void send(String mensaje)
      throws ACONExcArbitraria, ACONExcOmision{
    int i=0;
    byte mensa[]=null;    
    if(info.socket_type==ACONDescriptor.DGRAM){
      if(mensaje.length()>info.tamannoMaximo){
        mensa=new byte[info.tamannoMaximo];
        for(i=0;i<info.tamannoMaximo;i++){
          mensa[i]=mensaje.getBytes()[i];
        }
      }else{
        mensa=mensaje.getBytes();
      }
      try {
        this.getParDatagrama().send(new DatagramPacket(mensa,
            mensa.length,this.getRemoteSocketAddress()));
        ACONBitacora.print("Para host: " +this.info.remotehost+
                        " en puerto: "+this.info.remoteport);
      }
      catch (Exception ex) {
        // se va a suponer que es error por omisión
        if ((ex instanceof SecurityException)||
            (ex instanceof java.nio.channels.IllegalBlockingModeException)){
          throw new ACONExcArbitraria(ACONExcepcion.EMISION,ex,0,this.getRemoteHostName(),this.getRemotePort());
        }else{
          throw new ACONExcOmision(ACONExcepcion.EMISION,ex,0,this.getRemoteHostName(),this.getRemotePort());
        }
      }
    }
    if (info.socket_type==ACONDescriptor.STREAM) {
      try {
        // alesscor puse el writebytes
        fdOutput.writeUTF(mensaje);
      }catch (Exception ex) {
        if(ex instanceof java.io.IOException){
          throw new ACONExcOmision(ACONExcepcion.EMISION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
        }else{
          throw new ACONExcArbitraria(ACONExcepcion.EMISION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
        }
      }
    }
  }
  public final void sendb(byte[] mensaje)
      throws ACONExcArbitraria, ACONExcOmision{
    int i=0;
    byte mensa[]=null;
    if(info.socket_type==ACONDescriptor.DGRAM){
      if(mensaje.length>info.tamannoMaximo){
        mensa=new byte[info.tamannoMaximo];
        for(i=0;i<info.tamannoMaximo;i++){
          mensa[i]=mensaje[i];
        }
      }else{
        mensa=mensaje;
      }
      try {
        this.getParDatagrama().send(new DatagramPacket(mensa,
            mensa.length,this.getRemoteSocketAddress()));
        ACONBitacora.print("Para host: " +this.info.remotehost+
                        " en puerto: "+this.info.remoteport);
      }
      catch (Exception ex) {
        // se va a suponer que es error por omisión
        if ((ex instanceof SecurityException)||
            (ex instanceof java.nio.channels.IllegalBlockingModeException)){
          throw new ACONExcArbitraria(ACONExcepcion.EMISION,ex,0,this.getRemoteHostName(),this.getRemotePort());
        }else{
          throw new ACONExcOmision(ACONExcepcion.EMISION,ex,0,this.getRemoteHostName(),this.getRemotePort());
        }
      }
    }
    if (info.socket_type==ACONDescriptor.STREAM) {
      try {
        fdOutput.write(mensaje);
        fdOutput.flush();
        System.out.println("\nEl total escrito es: "+mensaje.length+
          " bytes.\n");
      }catch (Exception ex) {
        if(ex instanceof java.io.IOException){
          throw new ACONExcOmision(ACONExcepcion.EMISION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
        }else{
          throw new ACONExcArbitraria(ACONExcepcion.EMISION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
        }
      }
    }
  }
  public final void sendb(byte[] mensaje,int length)throws ACONExcArbitraria, 
    ACONExcOmision{
    int i=0;
    byte mensa[]=null;
    if(info.socket_type==ACONDescriptor.DGRAM){
      /*
       * Se corta el mensaje.
       */
      if(length>info.tamannoMaximo){
        mensa=new byte[info.tamannoMaximo];
        for(i=0;i<info.tamannoMaximo;i++){
          mensa[i]=mensaje[i];
        }
      }else{
        mensa=mensaje;
      }
      try {
        this.getParDatagrama().send(new DatagramPacket(mensa,
            mensa.length,this.getRemoteSocketAddress()));
        ACONBitacora.print("Para host: " +this.info.remotehost+
                        " en puerto: "+this.info.remoteport);
      }
      catch (Exception ex) {
        // se va a suponer que es error por omisión
        if ((ex instanceof SecurityException)||
            (ex instanceof java.nio.channels.IllegalBlockingModeException)){
          throw new ACONExcArbitraria(ACONExcepcion.EMISION,ex,0,this.getRemoteHostName(),this.getRemotePort());
        }else{
          throw new ACONExcOmision(ACONExcepcion.EMISION,ex,0,this.getRemoteHostName(),this.getRemotePort());
        }
      }
    }
    if (info.socket_type==ACONDescriptor.STREAM) {
      try {
        fdOutput.write(mensaje,0,length);
        fdOutput.flush();
        System.out.println("\nEl total escrito es: "+length+
          " bytes.\n");
      }catch (Exception ex) {
        if(ex instanceof java.io.IOException){
          throw new ACONExcOmision(ACONExcepcion.EMISION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
        }else{
          throw new ACONExcArbitraria(ACONExcepcion.EMISION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
        }
      }
    }
  }  
  /**
   * Recibe un mensaje. La información de en cuál puerto se recibe el mensaje
   * se asigna en la inicialización del objeto.
   * <li>Si el gestor es orientado a datagramas, entonces solamente se
   * lee un mensaje por envío, así que si se invoca nuevamente a este método,
   * no se leerá un nuevo paquete.</li>
   * @return Mensaje leído.
   * @throws ACONExcArbitraria En caso de error.
   * @throws ACONExcOmision En caso de error.
   * @throws ACONExcTemporizacion En caso de error.
   */
  public final String receive() throws ACONExcArbitraria, ACONExcOmision,
      ACONExcTemporizacion{
    String mensaje="";
    byte[] hilera;
    byte mensa[]=new byte[info.tamannoMaximo];
    // I Parte: datagramas
    if(info.socket_type==ACONDescriptor.DGRAM){
      try {
        DatagramPacket peticion=this.dgrampack;
        if(peticion!=null){
          // AQUI SOLO SE LEE EL PAQUETE.
          // EL RECEIVE DE DATAGRAMAS ESTA EN ACONAceptadorDesp.ev_manejaEventos
          if (peticion != null) {
            // se bloquea mientras espera v
            mensaje = new String(peticion.getData());
            // se bloquea mientras espera ^
            mensaje = mensaje.substring(0, peticion.getLength());
            this.dgrampack = peticion;
          } else {
            ACONBitacora.print(
                "No se han recibido paquetes (y no debería haber impreso esto).");
          }
        }else{
          // el paquete de datagrama es null, como evidentemente se quiere
          // recibir paquetes, entonces realiza preparativos para tal cosa
          // controlados por el ACONAceptador.
          //
          // NO ESTA IMPLEMENTADO
          //
        }
      }
      catch(Exception ex){
        if (ex instanceof java.net.SocketTimeoutException){
          throw new ACONExcTemporizacion(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
        }else{
          if (ex instanceof java.io.IOException) {
            throw new ACONExcOmision(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
          }
          else{
            throw new ACONExcArbitraria(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
          }
        }
      }
    }
    // II Parte: streams
    if (info.socket_type==ACONDescriptor.STREAM) {
      try {
        // chequea si pusieron tiempo de espera
        if(this.getTiempoEsperaGestor()>=0){
          System.err.println("Configurando timestamp en stream");
          this.getStream().setSoTimeout(this.getTiempoEsperaGestor());
        }
        do{
          // alesscor puse el + y el read byte
          mensaje += fdInput.readUTF();
        }while(fdInput.available()>0);
      }
      catch(Exception ex){
        if (ex instanceof java.net.SocketTimeoutException){
          throw new ACONExcTemporizacion(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
        }else{
          if (ex instanceof java.io.IOException) {
            throw new ACONExcOmision(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
          }
          else{
            throw new ACONExcArbitraria(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
          }
        }
      }
    }
    return mensaje;
  }
  /**
   * Asigna el número de puerto local.
   * @param plocal Puerto local.
   */
  public final void setPuertoLocal(int plocal){
    info.localport=plocal;
  }
  /**
   * Obtiene el número de puerto local.
   * @return El número de puerto local.
   */
  public final int getPuertoLocal(){
    return info.localport;
  }
  /**
   * Obtiene la dirección local.
   * @return Dirección local.
   */
  final public java.net.SocketAddress getLocalSocketAddress(){
    try{
      return new java.net.InetSocketAddress(java.net.InetAddress.getLocalHost(),
                                            info.localport);
    }catch(Exception except){
      return null;
    }
 }
 /**
  * Asigna los objetos de entrada y salida.
  * @throws ACONExcArbitraria En caso de error.
  */
 private void setEntradaSalida()throws ACONExcArbitraria{
  try {
    fdInput = new DataInputStream(this.stream.getInputStream());
    fdOutput = new DataOutputStream(this.stream.getOutputStream());
  }
  catch (Exception ex) {
    throw new ACONExcArbitraria("Error al asignar entrada y "+
                                   "salida del socket stream",ex);
  }
 }
 /**
  * Obtiene el objeto de entrada de datos de una conexión.
  * @return El objeto de entrada.
  */
 final public DataInputStream getEntrada(){
   return fdInput;
 }
 /**
  * Obtiene el objeto de salida de datos de una conexión.
  * @return El objeto de salida.
  */
 final public DataOutputStream getSalida(){
   return fdOutput;
 }
 /**
  * Obtiene el tamaño máximo de un datagrama.
  * @return Tamaño máximo de un datagrama.
  */
 final public int getTamanoMaximo(){
   return info.tamannoMaximo;
 }
 /**
  * Asogma el tamaño máximo de un datagrama.
  * @param tamanoMaximo0 Tamaño máximo de un datagrama.
  */
 final public void setTamanoMaximo(int tamanoMaximo0){
   info.tamannoMaximo=tamanoMaximo0;
 }
 /**
  * Asigna si se debe esperar el procesamiento del gestor de servicios.
  * @param wait0 Si se espera o no.
  */
 final public void setWait(boolean wait0){
   info.wait=wait0;
 }
  public final boolean esFin(){
    synchronized(bloqueos_verfin){
      while(bloqueos_verfin!=""){
        try {
          bloqueos_verfin.wait();
        }catch (InterruptedException e) {      
        }
      }
      return bloqueos_verfin=="";
    }
  }
  void setFin(){
    synchronized(bloqueos_verfin){
      bloqueos_verfin.notify();    
      bloqueos_verfin="";
    }
  }
 /**
  * Obtiene si se debe esperar el procesamiento del gestor de servicios.
  * @return Si se espera o no el procesamiento de servicios.
  */
 final public boolean getWait(){
   return info.wait;
 }
 /**
  * Carga la información en la instancia.
  * @param info0 Información a cargar.
  */
 final public void setInfo(ACONDescriptor info0){
   info=info0;
   if(info!=null){
     setNavegables(info.aoNavegables);
   }
 }
 /**
  * Cierra el canal de comunicación.<br>
  * <li>Si el canal es de datagramas, y el objeto no está ligado
  * a un ACONAceptador, se realiza el cierre.</li>
  * <li>Si el canal es de datagamas, y el objeto está ligado
  * a un ACONAceptador, se realiza el cierre sólo si el objeto
  * trabaja como parte de un descriptor (nunca, pero se deja
  * la posibilidad abierta).</li>
  * @throws ACONExcArbitraria Si no se pudo hacer el
  * @throws ACONExcepcion Si no se pudo hacer el
  * cierre del canal.
  */
  final public void close() throws ACONExcArbitraria,ACONExcepcion{
    String texto="",textoerr="";
    Exception ex0=null;
    if(this.info.socket_type==ACONDescriptor.STREAM){
      if(stream!=null){
        try {
          stream.close();
          ACONBitacora.print("stream cerrado");
        }catch (IOException ex) {
          textoerr="Servicio de stream no pudo cerrarse";
          ex0=ex;
        }
      }
    }
    if(this.info.socket_type==ACONDescriptor.DGRAM){
      // Solo se cierra el datagrama si el objeto
      // no participa en un ACONAceptador, porque el ACONAceptador
      // tiene el control central sobre la escucha de datagramas; o
      // si el objeto es un Descriptor en ACONAceptador.
      if(this.dgram!=null){
        try {
          if(!(info instanceof ACONDescriptorAdmin)||
             ((info instanceof ACONDescriptorAdmin)&&
              (((ACONDescriptorAdmin)info).isDescriptor()))){
            dgram.close();
            if(info instanceof ACONDescriptorAdmin){
              System.err.println("Caso interesante: cerrando un DatagramSocket"+
                                 " desde un gestor con características de "+
                                 " descriptor, explicar a alesscor@ieee.org");
            }
            ACONBitacora.print("datagrama cerrado");
          }
        }catch (Exception ex) {
            textoerr="Servicio de datagrama no pudo cerrarse";
            ex0=ex;
        }
      }
    }
    if(ex0==null){
      // si no hubo error cierra ACONDescriptor, que
      // para Gestores en atención es una llamada
      // a un ACONDescriptorAdmin::close.
      info.close();
    }else{
      System.err.println("¡¡¡Hubo error en cierre!!!");
      ex0.printStackTrace();
    }
    // escribe en la bitácora lo que corresponde.
    texto += "\n<onclose>";
    texto += ACONDescriptorAdmin.getStatusLog(this.info, true);
    texto += "\n<comentario>";
    if ((ex0 != null)&& textoerr!="") {
      texto += textoerr;
    }else {
      texto += "Gestor cerrado";
    }
    texto += "</comentario>";
    texto += "</onclose>";
    //oif(fdLog!=null){
    if((info.log==null) || (info.log=="")||
       (info.log.compareToIgnoreCase("true")==0)||
       (info.log.compareToIgnoreCase("verdadero")==0)){
      ACONBitacora.addLog(texto,0);
    }
    //o}
    if(ex0!=null){
      throw new ACONExcArbitraria(textoerr,ex0);
    }
  }
  public final void setTiempoEsperaGestor(int timestamp0){
    if(timestamp0<0){
      timestamp=0;
      return;
    }
    timestamp=timestamp0;
  }
  public final int getTiempoEsperaGestor(){
    return timestamp;
  }
  /**
   * Da la oportunidad de escribir los objetos navegables por el
   * gestor concreto.<br>
   * <li>Esta operación debe ser implementada para lograr
   * que la instancia navegue por otras instancias auxiliares.
   * Por ejemplo ver la que le toca a los gestories BITA.</li>
   * <li>A implementar si conviene.</li>
   * @param navegables Navegables de la instancia.
   */
  protected void setNavegables(Object[] navegables){
    // a implementar si conviene.
  }
  /**
   * Recibe un mensaje. La información de en cuál puerto se recibe el mensaje
   * se asigna en la inicialización del objeto.
   * <li>Lee el mensaje que es enviado por el canal de comunicación de una
   * forma completa.</li>
   * <li>Si el gestor es orientado a datagramas, entonces solamente se
   * lee un mensaje por envío, así que si se invoca nuevamente a este método,
   * no se leerá un nuevo paquete.</li>
   * @return Mensaje leído.
   * @throws ACONExcArbitraria En caso de error.
   * @throws ACONExcOmision En caso de error.
   * @throws ACONExcTemporizacion En caso de error.
   */
  public final byte[] receiveb() throws ACONExcArbitraria, ACONExcOmision,
      ACONExcTemporizacion{
    int length,total=0;
    byte[] hilera=null;
    ByteArrayOutputStream baRecibido=null;
    // I Parte: datagramas
    if(info.socket_type==ACONDescriptor.DGRAM){
      try {
        DatagramPacket peticion=this.dgrampack;
        if(peticion!=null){
          // AQUI SOLO SE LEE EL PAQUETE.
          // EL RECEIVE DE DATAGRAMAS ESTA EN ACONAceptadorDesp.ev_manejaEventos
          if (peticion != null) {
            // se bloquea mientras espera v
            hilera = peticion.getData();
            // se bloquea mientras espera ^
            this.dgrampack = peticion;
          } else {
            ACONBitacora.print(
                "No se han recibido paquetes (y no debería haber impreso esto).");
          }
        }else{
          // el paquete de datagrama es null, como evidentemente se quiere
          // recibir paquetes, entonces realiza preparativos para tal cosa
          // controlados por el ACONAceptador.
          //
          // NO ESTA IMPLEMENTADO
          //
        }
      }
      catch(Exception ex){
        if (ex instanceof java.net.SocketTimeoutException){
          throw new ACONExcTemporizacion(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
        }else{
          if (ex instanceof java.io.IOException) {
            throw new ACONExcOmision(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
          }
          else{
            throw new ACONExcArbitraria(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
          }
        }
      }
    }
    // II Parte: streams
    if (info.socket_type==ACONDescriptor.STREAM) {
      hilera=new byte[MAXBUFF];
      try {
        // chequea si pusieron tiempo de espera
        if(this.getTiempoEsperaGestor()>=0){
          System.err.println("Configurando timestamp en stream");
          this.getStream().setSoTimeout(this.getTiempoEsperaGestor());
        }
        baRecibido=new ByteArrayOutputStream(MAXBUFF);
        do{
          do{
            length=fdInput.read(hilera);
            if(length<0){
              break;
            }
            baRecibido.write(hilera,0,length);
            total+=length;          
          }while(fdInput.available()>0);
          baRecibido.flush();
        }while(length>0);
//        while((length=fdInput.read())>=0){          
//          baRecibido.write(length);
//          total++;                  
//        }        
        hilera=null;
        hilera=baRecibido.toByteArray();
        baRecibido.close();
        System.out.println("\nEl total leído es: "+total+" bytes.\n");        
      }
      catch(Exception ex){
        if (ex instanceof java.net.SocketTimeoutException){
          throw new ACONExcTemporizacion(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
        }else{
          if (ex instanceof java.io.IOException) {
            throw new ACONExcOmision(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
          }
          else{
            throw new ACONExcArbitraria(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
          }
        }
      }
    }
    return hilera;
  }
  /**
   * Recibe parte de un mensaje enviado por un canal de comunicación.
   * La información de en cuál puerto se recibe el mensaje
   * se asigna en la inicialización del objeto.
   * <li>Lee el mensaje enviado por el canal de comunicación de una forma
   * parcial.</li>
   * <li>Se devuleve -1 si ya no hay datos que leer del canal de 
   * comunicación.</li>
   * <li>Si el gestor es orientado a datagramas, entonces solamente se
   * lee un mensaje por envío, así que si se invoca nuevamente a este método,
   * no se leerá un nuevo paquete.</li>
   * @param buffer Buffer a escribir con lo leído.
   * @return La cantidad de bytes leídos y dejados en el buffer.
   * @throws ACONExcArbitraria En caso de error.
   * @throws ACONExcOmision En caso de error.
   * @throws ACONExcTemporizacion En caso de error.
   */
  public final int receiveb(byte[] buffer) throws ACONExcArbitraria, 
      ACONExcOmision,ACONExcTemporizacion{        
    int length=-1,total=0,i=0;
    byte[] hilera=null;
    if(buffer==null || buffer.length<=0){
      throw new ACONExcArbitraria("No se puede recibir fuente y dejarla en " +        "un buffer nulo o vacío.");
    }
    // I Parte: datagramas
    if(info.socket_type==ACONDescriptor.DGRAM){
      // TODO Datagramas no necesitan esta característica.
      try {
        DatagramPacket peticion=this.dgrampack;
        if(peticion!=null){
          // AQUI SOLO SE LEE EL PAQUETE.
          // EL RECEIVE DE DATAGRAMAS ESTA EN ACONAceptadorDesp.ev_manejaEventos
          if (peticion != null) {
            // se bloquea mientras espera v
            hilera = peticion.getData();
            if(hilera!=null){
              length=hilera.length;
            }
            // se bloquea mientras espera ^
            this.dgrampack = peticion;
          } else {
            ACONBitacora.print(
                "No se han recibido paquetes (y no debería haber impreso esto).");
          }
        }else{
          // el paquete de datagrama es null, como evidentemente se quiere
          // recibir paquetes, entonces realiza preparativos para tal cosa
          // controlados por el ACONAceptador.
          //
          // NO ESTA IMPLEMENTADO
          //
        }
      }
      catch(Exception ex){
        if (ex instanceof java.net.SocketTimeoutException){
          throw new ACONExcTemporizacion(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
        }else{
          if (ex instanceof java.io.IOException) {
            throw new ACONExcOmision(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
          }
          else{
            throw new ACONExcArbitraria(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
          }
        }
      }
    }
    // II Parte: streams
    if (info.socket_type==ACONDescriptor.STREAM) {
      hilera=new byte[buffer.length];
      try {
        // chequea si pusieron tiempo de espera
        if(this.getTiempoEsperaGestor()>=0){
          System.err.println("Configurando timestamp en stream");
          this.getStream().setSoTimeout(this.getTiempoEsperaGestor());
        }
        // do{
          length=fdInput.read(hilera);
        //  if(length<0){
        //    break;
        //  }
          total+=length;          
        // }while(fdInput.available()>0);
        System.out.println("\nEl total leído es: "+total+" bytes.\n");        
      }
      catch(Exception ex){
        if (ex instanceof java.net.SocketTimeoutException){
          throw new ACONExcTemporizacion(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
        }else{
          if (ex instanceof java.io.IOException) {
            throw new ACONExcOmision(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
          }
          else{
            throw new ACONExcArbitraria(ACONExcepcion.RECEPCION, ex, 0,this.getRemoteHostName(),this.getRemotePort());
          }
        }
      }
    }
    if(length>0){
      i=0;      
      while((i<length)&&(i<buffer.length)){
        buffer[i]=hilera[i];
        i++;
      }
    }
    return length;
  }
  /**
   * @return Tiempo establecido para la conexión.
   */
  public int getTiempoConexion() {
    return this.tiempoConexion;
  }
  /**
   * Devuelve los objetos navegables que fueron pasados
   * por el descriptor del servicio.
   * @return Arreglo de objetos navegables.
   */
  final public Object[] getNavegables(){
    return this.info.aoNavegables;
  }
  
}
