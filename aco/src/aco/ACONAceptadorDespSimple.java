/*
 * Created on 15/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package aco;
import java.net.*;
import java.io.*;

/**
 * <br>Despachador simple.</br>
 */
public class ACONAceptadorDespSimple extends ACONAceptadorDespAbs{
  /**
   * Descriptor del servicio.
   */
  private ACONDescriptorAdmin descriptor;
  public ACONAceptadorDespSimple() {
    super();
    descriptor=null;
  }
  /**
   * Realiza utilizando hilos por servicio, esperas de conexiones en
   * cada uno de los servicios registrados, los cuales están presentes
   * en la lista <tt>this.serversregistrados</tt>.
   */
  public final void manejaEventos(ACONDescriptor desc){
  /**\-----------------------------------------------------------------\**/
  /**\      realiza accept para cada nodo en un hilo separado.         /**/
  /**\      para cada servicio registrado.                             /**/
  /**/      descriptor=new ACONDescriptorAdmin(desc,true);             /**/
  /**/      descriptor.hiloEspera=null;                                /**/
  /**/      try{                                                       /**/
  /**/        acepta(descriptor);                                      /**/
  /**/      }catch(ACONExcepcion ex){                                  /**/
  /**/        ex.printStackTrace();                                    /**/
  /**/      }                                                          /**/
 /**\------------------------------------------------------------------\**/
  }
  /**
   * Retorna el puerto local del canal de comunicación creado.
   * @return El número de puerto.
   */
  public final int getPuertoLocal(){
    if(descriptor!=null){      
      return descriptor.localport;
    }else{
      return 0;
    }
  }
  public void close(){
    if(descriptor!=null){
      try {
        if(descriptor.isAbierto()){
          descriptor.close();
        }
      } catch (ACONExcepcion e) {
        e.printStackTrace();
      }
    }
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
    boolean error = false;
    ACONDescriptorAdmin inforeg=null;
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
    /*|*/ inforeg=new ACONDescriptorAdmin(descriptor,false);
    /*|*/ inforeg.hiloEspera=Thread.currentThread();
    /*|*/
    /*|*| importante: asigna un ACONDescriptorAdmin como el ACONDescriptor
    |*|*| del gestor, esto, principalmente, para que el método
    |*|*| ACONDescriptor::close()
    |*|*| sea el evolucionado de un ACONDescriptorAdmin
    |*|*/ Gestor.setInfo(inforeg);
    /*|*/ inforeg.setAtencion(Gestor);
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
        /*)*/ gestorreferencia.open();  /*)*/
        /*(*/ gestorreferencia.setFin();/*(*/
      }catch(Exception ex){
        /* TODO buscar una forma asincronica para reportar el error. */
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
  protected Object ev_manejaEventos(ACONDescriptorAdmin infoDescriptor,Object[] base0) throws
      ACONExcepcion{
    Object base = base0[0], canaltrabajo = null;
    //
    // considera el peor caso: que el servicio no ha sido inicializado
    // synchronized(infoDescriptor){
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
        this.setFin();
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
        throw new ACONExcTemporizacion(ACONExcepcion.CONEXION,ex,0,this.descriptor.localhost,this.descriptor.localport);
      }catch (IOException ex) {
        if (infoDescriptor.isDetenido) {
          return null;
        }
        throw new ACONExcOmision(ACONExcepcion.CONEXION,ex,0,this.descriptor.localhost,this.descriptor.localport);
      }catch (Exception ex) {
        if (infoDescriptor.isDetenido) {
          return null;
        }
        throw new ACONExcArbitraria(ACONExcepcion.CONEXION,ex,0,this.descriptor.localhost,this.descriptor.localport);
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
    }
    base0[0] = base;
    return canaltrabajo;
  }  
}
