package oact;
import aco.*;
import mens.*;
/**
 * Gestor de las actividades del objeto activo como servidor
 * de un cliente.
 */
public class OACTGestorDistribuidor extends ACONGestor {
  private OACTSolicitudPrimitiva sol;
  private String idsolicitud;
  private MENSMensaje futuro;
  /**
   * Estado del objeto usuario principal.
   */
  private OACTDistribuidor servidor;
  public OACTGestorDistribuidor() {
    servidor=null;
    sol=null;
    idsolicitud="";
  }
  public OACTGestorDistribuidor(ACONDescriptor info0) {
    super(info0);
    sol=null;
    idsolicitud="";
  }
  /**
   * Gestiona el servicio de esta parte de la comunicación, es decir
   * de la que le corresponde al distribuidor.
   */
  public void open(){
    String buffer1 = null,clave="";
    clave=servidor.getPie();
    idsolicitud=Long.toString((System.currentTimeMillis()-servidor.getInicio()));
    // --------------
    this.log_apertura();
    // --------------
    /*************************************************
     * Obtiene mensaje desde el canal de comunicación.
     */
    buffer1=ges_traeMensaje();
    if(buffer1==null){
      return;
    }
    /****************************************************************
     * Desenvuelve el mensaje empaquetado.
     * Revisa si es comando. Si no es, entonces trata de leer un XML
     * compatible con OACTSolicitud0.
     */
    if(ges_isComando(buffer1)){
      // esto es un comando,
      // lo procesa y termina
      this.log_atencion("<estado>comando</estado>");
      procesaComando(buffer1);
      return;
    }
    // no es un comando, continúa suponiendo
    // que es una solicitud normal o comprimida
    sol=ges_cargaSolicitud(buffer1);
    if(sol==null){
      // hubo error en la carga de la solicitud XML
      return;
    }
    sol.setGestorDist(this);
    /**************************************
     * Inicia el despacho de la solicitud.
     */
    OACTSolicitudPrimitiva future = null;
    try {
      future = servidor.despacha(sol);
      /****************************************************
       * Revisa el resultado de la solicitud o almenos, si debe
       * devolverse algo puesto en future al par que hizo la solicitud.
       */
      ges_gestionaFuturo(future);
    }
    catch (OACTExcPosPuesto ex) {
      /**
       * Aquí el distribuidor decidió postponer la gestión del servicio.
       * Entonces hace, sin cerrar la comunicación con el par del otro
       * extremo, las previsiones para un futuro contacto que supuestamente
       * se realizará próximamente.
       * Para ello se debe tomar en cuenta el método _completa_ para la "llamada
       * asincrónica".
       */
      // --------------
      this.log_atencion("<estado>postpuesto</estado>");
      // --------------
      return;
    }
    catch(OACTExcepcion ex){
      /**
       * Hubo otra excepción, en la ejecución del método solicitado.
       */
      ges_gestionaFuturo(future);
    }
  }
  public void acepta() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void manejaEvento() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void sirvase() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void completa() throws aco.ACONExcepcion {
    // --------------
    this.log_reapertura("<estado>reabierto</estado>");
    // --------------
    sol=null;
    this.ges_gestionaFuturo(futuro);
    futuro=null;
    servidor=null;
  }
  protected void setNavegables(Object[] navegables){
    if(navegables!=null){
     servidor=(OACTDistribuidor)navegables[0];
    }else{
      // craso error, esto nunca debe pasar
      System.err.println("No se tiene un servidor navegable.");
    }
  }
  /**
   * Sirve para completar una llamada asincrónica debido a postposición
   * de la gestión del servicio de parte del distribuidor.
   * Con este método, el distribuidor retoma la provisión del servicio
   * del sistema.
   * @param futuro0 Resultado de la llamada a un método del sirviente.
   */
  public void setFuturo(MENSMensaje futuro0){
    futuro=futuro0;
  }
  private final String log_getStatusDescriptor(){
    String status="";
    status+="<descriptor>";
    status+="<iddescriptor>";
    status+=this.info.id;
    status+="</iddescriptor>";
    status+="<localhost>";
    status+=this.info.localhost;
    status+="</localhost>";
    status+="<localport>";
    status+=this.info.localport;
    status+="</localport>";
    status+="<remotehost>";
    status+=this.info.remotehost;
    status+="</remotehost>";
    status+="<remoteport>";
    status+=this.info.remoteport;
    status+="</remoteport>";
    status+="</descriptor>";
    return status;
  }
  private final void log_apertura(){
    String xml = "";
    if(OACTBitacora.getConBitacora()){
      xml += "<solicitud>";
      xml += "<id>";
      xml += this.idsolicitud;
      xml += "</id>";
      xml += "<momento>apertura</momento>";
      xml += OACTBitacora.getLogTime();
      xml += log_getStatusDescriptor();
      xml += "</solicitud>";
      OACTBitacora.addLog(xml);
    }
  }
  private final void log_atencion(String mensaje){
    String xml = "";
    if(OACTBitacora.getConBitacora()){
      xml += "<solicitud>";
      xml += "<id>";
      xml += this.idsolicitud;
      xml += "</id>";
      xml += "<momento>atencion</momento>";
      xml += OACTBitacora.getLogTime();
      xml += mensaje;
      xml += "</solicitud>";
      OACTBitacora.addLog(xml);
    }
  }
  private final void log_cierre(String mensaje){
    String xml="";
    if(OACTBitacora.getConBitacora()){
      xml += "<solicitud>";
      xml += "<id>";
      xml += this.idsolicitud;
      xml += "</id>";
      xml += "<momento>cierre</momento>";
      xml += OACTBitacora.getLogTime();
      xml += mensaje;
      xml += "</solicitud>";
      OACTBitacora.addLog(xml);
    }
  }
  private final void log_reapertura(String mensaje){
    String xml="";
    if(OACTBitacora.getConBitacora()){
      xml += "<solicitud>";
      xml += "<id>";
      xml += this.idsolicitud;
      xml += "</id>";
      xml += "<momento>reapertura</momento>";
      xml += OACTBitacora.getLogTime();
      xml += mensaje;
      xml += "</solicitud>";
      OACTBitacora.addLog(xml);
    }
  }
  private final void procesaComando(String mensaje){
    String respuesta = "";
    try {
      if (mensaje.indexOf(OACTDistribuidor.Mensajes.DEME_ESTADO_SIRVIENTES) >=
          0) {
        // quiere gestores activados y su estado
        System.err.println("obtiene sirvientes inicio");
        respuesta = servidor.getStatusSirvientes();
        System.err.println("obtiene sirvientes fin");
        this.send(respuesta);
        System.err.println("Manda respuesta");
        log_cierre("<estado>completado:"+
                   OACTDistribuidor.Mensajes.DEME_ESTADO_SIRVIENTES+"</estado>");
        this.close();
        return;
      }
      if (mensaje.indexOf(OACTDistribuidor.Mensajes.DEME_ESTADO_ACTIVACION) >=
          0) {
        // quiere gestores activados y su estado
        respuesta = servidor.getStatusActivacion();
        this.send(respuesta);
        System.err.println("Manda respuesta");
        log_cierre("<estado>completado:"+
                   OACTDistribuidor.Mensajes.DEME_ESTADO_ACTIVACION+"</estado>");
        this.close();
        return;
      }
      // no se sabe lo que quiere
      System.err.println("No se sabe lo que quiere un cliente con \"" +
                         mensaje + "\"");
      respuesta = OACTDistribuidor.Mensajes.NO_SE_DA_A_ENTENDER;
      this.send(respuesta);
      System.err.println("Manda respuesta");
      log_cierre("<estado>completado:"+OACTDistribuidor.Mensajes.NO_SE_DA_A_ENTENDER+"</estado>");
      this.close();
    }catch(aco.ACONExcArbitraria ex){
      log_cierre("<estado>error</estado>");
    }catch(aco.ACONExcOmision ex){
      log_cierre("<estado>error</estado>");
    }catch(aco.ACONExcepcion ex){
      log_cierre("<estado>error</estado>");
    }
  }
  private final OACTSolicitudPrimitiva ges_cargaSolicitud(String buffer1){
    String bufferinflado;
    OACTSolicitudPrimitiva sol0=new OACTSolicitudPrimitiva();
    try {
      sol0.loadFromString(buffer1);
      sol0.setIdentificacion(idsolicitud);
      // --------------
      this.log_atencion("<estado>"+sol0.getStatus()+
                        "</estado>");
      // --------------
      return sol0;
    }
    catch (MENSException ex) {
      // el mensaje no está en XML
      // revisa si está en formato comprimido de zlib.
      try {
        bufferinflado = OACTDistribuidorBase.infla(buffer1);
        // el mensaje sí estaba comprimido
        buffer1=bufferinflado;
        sol0.loadFromString(buffer1);
        // --------------
        this.log_atencion("<estado>"+"desencriptado"+sol0.getStatus()+
                          "</estado>");
        // --------------
        return sol0;
      }
      catch (OACTExcepcion ex1) {
        // el mensaje no está en formato comprimido de zlib.
        sol0=null;
        // --------------
        this.log_atencion("<estado>error</estado>");
        // --------------
        try {
          send(OACTDistribuidor.Mensajes.NO_SE_DA_A_ENTENDER);
          this.close();
          return sol0;
        }
        catch (ACONExcepcion ex2) {
          sol0=null;
          System.err.println("No se pudo notificar error.");
          try {
            this.close();
          }
          catch (ACONExcepcion ex5) {
          }
          return sol0;
        }
      }
      catch (MENSException ex1) {
        // el mensaje sí estaba comprimido pero
        // otra vez el mensaje no estaba en XML
        sol0=null;
        // --------------
        this.log_atencion("<estado>error</estado>");
        // --------------
        try {
          send(OACTDistribuidor.Mensajes.NO_SE_DA_A_ENTENDER);
          this.close();
          return sol0;
        }
        catch (ACONExcepcion ex2) {
          sol0=null;
          System.err.println("No se pudo notificar error.");
          try {
            this.close();
          }
          catch (ACONExcepcion ex5) {
          }
          return sol0;
        }
      }
    }
  }
  private final void ges_gestionaFuturo(MENSMensaje future){
    MENSIComandos comando=null;
    if(future==null){
      // nada se devuelve
      // se supone que el solicitante, i.e., el par que está en el otro
      // extremo del contacto de comunicación,
      // no necesita notificación de nada.
      // --------------
      this.log_cierre("<estado>completado sin devolucion</estado>");
      // --------------
      sol=null;
      try {
        this.close();
      }
      catch (ACONExcepcion ex3) {
        // no se pudo cerrar, ni modo.
      }
    }else{
      // el solicitante, i.e., el par que está en el otro extremo del
      // contacto de comunicación, necesita el envío del contenido
      // de futuro.
      // --------------
      this.log_cierre("<estado>completado con devolucion</estado>");
      // --------------
      comando=future.getComando();
      if(comando!=null && comando.demorarse()){
        /*
         * otra instancia se encarga de realizar la devolución. 
         */
      }else{
        System.out.println("Servidor:El resultado a devolver es:"+future.getXMLRootElem(null));
        try {
          this.send(future.getXMLRootElem(null));
          this.close();
        }
        catch (ACONExcepcion ex4) {
          // no se pudo enviar el trabajo, o no
          // se pudo cerrar la comunicación.
        }
      }
    }
  }
  private final String ges_traeMensaje(){
    String buffer1=null;
    try {
      this.setTiempoEsperaGestor(this.servidor.getTimeStamp());
      buffer1 = this.receive();
    }catch (ACONExcArbitraria ex) {
      System.err.println("Error arbitrario.");
      // --------------
      this.log_atencion("<estado>error</estado>");
      // --------------
      try {
        this.close();
      }
      catch (ACONExcepcion ex5) {
      }
    }catch (ACONExcOmision ex) {
      // --------------
      this.log_atencion("<estado>error</estado>");
      // --------------
      System.err.println("Error de conexion.");
      try {
        this.close();
      }
      catch (ACONExcepcion ex5) {
      }
    }catch (ACONExcTemporizacion ex) {
      //Error de tiempo de espera
      // --------------
      this.log_atencion("<estado>"+OACTDistribuidor.Mensajes.TIEMPO_VENCIDO+
                        "</estado>");
      // --------------
      try {
        send(OACTDistribuidor.Mensajes.TIEMPO_VENCIDO);
        this.close();
      }
      catch (ACONExcepcion ex1) {
        System.err.println("No se pudo notificar error.");
        try {
          this.close();
        }
        catch (ACONExcepcion ex5) {
        }
      }
    }
    return buffer1;
  }
  private final boolean ges_isComando(String mensaje){
    boolean bOK=false;
    String clave=servidor.getPie();
    if(mensaje.length()<=servidor.getCantComando()){
      if (mensaje.indexOf(clave) >= 0) {
        bOK=true;
      }
    }
   return bOK;
  }
}