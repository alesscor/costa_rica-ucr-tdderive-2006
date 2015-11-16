package aco;
import mens.MENSMensaje;

/**
 * Contiene información sobre gestores de servicio a ser registrada
 * por el despachador.
 */
public class ACONDescriptorAdmin extends ACONDescriptor {
  /**
   * Señala cuál es el hilo encargado de esperar contactos por un
   * canal de comunicación. Por supuesto que solamente puede haber
   * un hilo haciendo ésto.
   */
  public Thread hiloEspera;
  private int instancias;
  private Object canal;
  private ACONInfoAtencion atencion;
  private boolean isDescriptorServicio;
  private boolean isGestorServicio;
  public ACONDescriptorAdmin() {
    canal=null;
    atencion=null;
    instancias=0;
    hiloEspera=null;
    isDescriptorServicio=true;
    isGestorServicio=false;
  }
  /**
   * Intenta ser copia del descriptor info. Los objetos quedan
   * referenciados.
   * @param info Descriptor de servicio a copiar.
   * @param isdescriptor0 Si es un descriptor de servicio, siempre
   * es verdadero;... bueno, revisar esto.
   */
  public ACONDescriptorAdmin(ACONDescriptor info,boolean isdescriptor0) {
    int i=0;
    this.id=info.id;
    this.isControlFlujo=info.isControlFlujo;
    this.isDeCualquiera=info.isDeCualquiera;
    this.isDestinoConocido=info.isDestinoConocido;
    this.isOrdenados=info.isOrdenados;
    this.isPuedenHaberDuplicados=info.isPuedenHaberDuplicados;
    this.isPuedenHaberPerdidos=info.isPuedenHaberPerdidos;
    this.localhost=info.localhost;
    this.localport=info.localport;
    this.protocol=info.protocol;
    this.remotehost=info.remotehost;
    this.remoteport=info.remoteport;
    this.server=info.server;
    this.socket_type=info.socket_type;
    this.tamannoMaximo=info.tamannoMaximo;
    this.user=info.user;
    this.wait=info.wait;
    this.objNotificacion=info.objNotificacion;
    this.log=info.log;
    this.isDetenido=info.isDetenido;
    this.instancias=0;
    this.tiempoEspera=info.tiempoEspera;
    this.tiempoConexion=info.tiempoConexion;
    if(info.aoNavegables!=null){
      this.aoNavegables=new Object[info.aoNavegables.length];
      while(i<info.aoNavegables.length){
        this.aoNavegables[i]=info.aoNavegables[i];
        i++;
      }
    }
    hiloEspera=null;
    canal=null;
    atencion=null;
    if(isdescriptor0){
      setDescriptor();
    }else{
      setGestor();
    }
  }
  public boolean isDescriptor(){
    return isDescriptorServicio;
  }
  public boolean isGestor(){
    return isGestorServicio;
  }
  public void setGestor(){
    isGestorServicio=true;
    isDescriptorServicio=false;
  }
  public void setDescriptor(){
    isDescriptorServicio=true;
    isGestorServicio=false;
  }
  public boolean isActivado(){
    return this.hiloEspera!=null;
  }
  public boolean isAbierto(){
    boolean res=false;
    if(hiloEspera!=null){
      res=hiloEspera.isAlive();
    }
    return res;
  }
  public boolean isAtendido(){
    return this.atencion!=null;
  }
  public Thread getHiloEspera(){
    return this.hiloEspera;
  }
  public void incInstancias(){
    this.instancias++;
//    System.err.println("Incrementando instancias");
  }
  public void decInstancias(){
    this.instancias--;
  }
  public int getInstancias(){
    return this.instancias;
  }
  public static final String getStatusLog(ACONDescriptor info,boolean tolog){
    String res="";
    java.util.Date momento;
    if(tolog){
      momento=tdutils.tdutils.getTime();
      res+="\n<fecha>";
      res+= new java.text.SimpleDateFormat("yyyy/MM/dd").format(momento);
      res+="</fecha>";
      res+="\n<hora>";
      res+=new java.text.SimpleDateFormat("HH:mm:ss").format(momento);
      res+="</hora>";
    }
    res+="\n<id>";
    res+=info.id;
    res+="</id>";
    res+="\n<server>";
    res+=info.server;
    res+="</server>";
    res+="\n<localhost>";
    res+=info.localhost;
    res+="</localhost>";
    res+="\n<localport>";
    res+=info.localport;
    res+="</localport>";
    res+="\n<wait>";
    if(info.wait){
      res += "true";
    }else{
      res += "false";
    }
    res+="</wait>";
    res+="\n<type>";
    if(info.socket_type==ACONDescriptor.DGRAM){
      res += "dgram";
    }else if(info.socket_type==ACONDescriptor.STREAM){
      res += "stream";
    }else if(info.socket_type==ACONDescriptor.SEQPACKET){
      res += "seqpacket";
    }
    res+="</type>";
    if(info instanceof ACONDescriptorAdmin){
      res += "\n<ninstancias>";
      res += ((ACONDescriptorAdmin)info).getInstancias();
      res += "</ninstancias>";
      if(!((ACONDescriptorAdmin)info).isAtendido()){
        res += "\n<isActivado>";
        if (((ACONDescriptorAdmin)info).isActivado()) {
          res += "true";
        }
        else {
          res += "false";
        }
        res+="</isActivado>";
      }
      if(((ACONDescriptorAdmin)info).isAtendido()){
        res+="\n<remotehost>";
        res+=info.remotehost;
        res+="</remotehost>";
        res+="\n<remoteport>";
        res+=info.remoteport;
        res+="</remoteport>";
        if(((ACONDescriptorAdmin)info).getInicio()!=null){
          res += "\n<iniciofecha>";
          res += new java.text.SimpleDateFormat("yyyy/MM/dd").format(
              ((ACONDescriptorAdmin)info).getInicio());
          res += "</iniciofecha>";
          res += "<iniciohora>";
          res += new java.text.SimpleDateFormat("HH:mm:ss").format(
              ((ACONDescriptorAdmin)info).getInicio());
          res += "</iniciohora>";
        }
        if(((ACONDescriptorAdmin)info).getTermino()!=null){
          res += "\n<terminofecha>";
          res += new java.text.SimpleDateFormat("yyyy/MM/dd").format(
              ((ACONDescriptorAdmin)info).getTermino());
          res += "</terminofecha>";
          res += "<terminohora>";
          res += new java.text.SimpleDateFormat("HH:mm:ss").format(
              ((ACONDescriptorAdmin)info).getTermino());
          res += "</terminohora>";
        }
      }
      if((((ACONDescriptorAdmin)info).atencion!=null)&&
         (((ACONDescriptorAdmin)info).atencion.getGestor()!=null)){
        ACONGestor gestor=((ACONDescriptorAdmin)info).atencion.getGestor();
        res += "\n<open>";
        if(info.socket_type==ACONDescriptor.DGRAM){
          if(gestor.getParDatagrama().isClosed()){
            res += "false";
          }else{
            res += "true";
          }
        }
        if(info.socket_type==ACONDescriptor.STREAM){
          if(gestor.getStream().isClosed()){
            res += "false";
          }else{
            res += "true";
          }
        }
        res += "</open>";
      }
    }
    return res;
  }

  public final String getStatusLog(boolean tolog){
    String res="";
    res=getStatusLog(this,tolog);
    return res;
  }
  public final String getStatus(){
    String res="";
    res+="\n<servicio>";
    res+=getStatusLog(false);
    res+="\n</servicio>";
    return res;
  }
  public void setCanal(Object canal0){
    canal=canal0;
  }
  /**
   * Cierra el servicio o ejecuta operaciones de cierre
   * para el <tt>ACONGestor</tt> que tienen que ser controlados
   * por el <tt>ACONAceptador</tt>.
   * @throws ACONExcepcion En caso de error.
   */
  public void close() throws ACONExcepcion{
    if(isGestor()){
      System.err.println("1 Cerrando InfoRegistro " + id+ " como Gestor." );
    }else{
      System.err.println("1 Cerrando InfoRegistro " + id+ " como Descriptor." );
    }
    if (isGestor()) {
      if(isAtendido()){
        // si es un gestor de servicios hace su cierre
        // indica el momento en que se cerró el gestor de
        // servicios
        atencion.setTermino();
        // ACONBitacora.print("Cierre desde ACONDescriptorAdmin.");
        System.err.println("2 Cerrando InfoRegistro " + id);
      }
    }
    if(isDescriptor()){
      // si no es un gestor de servicios, es un descriptor de servicios
      System.err.println("3 Cerrando InfoRegistro " + id);
      if (!isAbierto()) {
        throw new ACONExcArbitraria("El servicio no está abierto.");
      }
      else {
        System.err.println("4 Cerrando InfoRegistro " + id);
        try {
          isDetenido = true;
          System.err.println("Hilo interrumpido para ID " + id);
          hiloEspera.interrupt();
          closeCanal();
          ACONBitacora.print("Hilo interrumpido para ID " + id);
        }
        catch (Exception ex) {
          throw new ACONExcArbitraria(
              "No se pudo interrumpir el hilo para ID " +
              id, ex);
        }
      }
    }
  }
  private void closeCanal() throws ACONExcepcion{
    if(canal==null){
      return;
    }
    try{
      if (this.socket_type == ACONDescriptor.DGRAM) {
        if(canal instanceof java.net.DatagramSocket){
          ( (java.net.DatagramSocket) canal).close();
        }else{
          System.err.println("Error de instanciación: no es DatagramSocket");
        }
      }
      if (this.socket_type == ACONDescriptor.STREAM) {
        if(canal instanceof java.net.ServerSocket){
          ( (java.net.ServerSocket) canal).close();
        }else{
          System.err.println("Error de instanciación: no es ServerSocket");
        }
      }
      ACONBitacora.print("Canal cerrado para ID "+id+".");
    }catch(Exception ex){
      throw new ACONExcOmision(ACONExcepcion.CIERRE,ex,0,this.localhost,this.localport);
    }
  }
  public void setAtencion(ACONGestor gestor){
    atencion=new ACONInfoAtencion(gestor);
    System.err.println("Puesto el objeto de atención a ID "+id+".");
  }
  public java.util.Date getInicio(){
    if(atencion!=null){
      return atencion.getInicio();
    }else{
      return null;
    }
  }
  public java.util.Date getTermino(){
    if(atencion!=null){
      return atencion.getTermino();
    }else{
      return null;
    }
  }
  public void activa(Thread hilo){
    hiloEspera=hilo;
    if(hilo!=null){
      isDetenido=false;
    }
  }
  /**
   * <br>Clase de configuración.</br>
   */
  public abstract class config extends MENSMensaje{
    ACONDescriptorAdmin acargar;
    public config(ACONDescriptorAdmin acargar0){
      acargar=acargar0;
    }
  }
  //////////////////////////////////////////////////////////////////////
}