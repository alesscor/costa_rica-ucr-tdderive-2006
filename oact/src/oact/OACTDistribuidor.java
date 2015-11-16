package oact;
import aco.*;
import mens.*;
import org.w3c.dom.Node;
/**
 * Planificador del patr�n objeto activo. Act�a como el objeto que
 * distribuye las llamadas a m�todos de objetos activos (sirvientes).
 */
public final class OACTDistribuidor extends OACTDistribuidorBase{
  static public final String SERVER_CLASS="oact.OACTGestorDistribuidor";
  /**
   * Cola de mensajes recibidos a ser revisados y activados.
   */
  private OACTListaActivacion listaactivacion;
  /**
   * Puerto base para soportar varias computadoras virtuales sobre una
   * misma computadora real.
   */
  public int desplaza_puerto=0;
  /**
   * Objeto activo. Relacionado con la fachada operacional del objeto
   * de tdderive.
   */
  private  OACTInventarioSirvientes inventario;
	public OACTDistribuidor(Node nodo) {
		super(nodo);
		_inicio();
	}
  public OACTDistribuidor(String archivo) {
    super(archivo);
		_inicio();
  }
  private void _inicio(){
		// para navegaci�n de propieadades por parte de nuevos objetos
		// activos que necesitan info de configuraci�n.
		inventario=new OACTInventarioSirvientes(this);
		listaactivacion=null;
		/**
		 * Abre inventario, va a contener las solicitudes de ejecuci�n
		 * de m�todo.
		 */
		listaactivacion=new OACTListaActivacion();
		conector=new aco.ACONConectorDesp();
  }
  public OACTInventarioSirvientes getObjetosActivos(){
    return inventario;
  }
  public boolean isOpen(){
    return listaactivacion!=null;
  }
  /**
   * Deber�a cargar la configuraci�n a partir de los archivos de configuraci�n.
   * Para esto necesita un objeto especializado, pues los archivos de
   * configuraci�n est�n en formato XML.
   * @param acc Objeto aceptador dedicado a iniciar el servicio y de
   * esperar eventuales conexiones.
   * @throws OACTExcepcion Si ocurre un error al interpretar la configuraci�n
   * para arrancar la existencia de este objeto.
   */
  public void open(ACONAceptadorDesp acc) throws OACTExcepcion{
    ACONDescriptor descriptor=null;
    despa=acc;
    if(despa==null){
      throw new OACTExcepcion("No hay despachador para el distribuidor de "+
                              "llamadas a m�todos remotos.");
    }
    descriptor=this.getDescripcion();
    descriptor.localport=this.getPuertoLocal();
    try {
      despa.operaDescriptor(descriptor, ACONAceptadorDesp.operaciones.ADD);
    }
    catch (ACONExcArbitraria ex) {
      throw new OACTExcepcion("No se pudo arrancar OACTGestorDistribuidor, error al "+
                          "registrar servicios.",ex);
    }
    try {
      despa.operaDescriptor(descriptor, ACONAceptadorDesp.operaciones.OPEN);
    }
    catch (ACONExcArbitraria ex) {
      throw new OACTExcepcion("No se pudo abrir OACTGestorDistribuidor",ex);
    }
  }
  /**
   * Cierra el difusor, incluyendo la provisi�n de su objeto remoto.
   */
  public void close(){
    if(despa==null){
      return;
    }
    try {
      despa.operaDescriptor(this.descDistribuidor,
                            ACONAceptadorDesp.operaciones.CLOSE);
    }
    catch (ACONExcArbitraria ex) {
      System.err.println("El objeto no se pudo cerrar: "+
                         this.descDistribuidor.id+".");
      ex.printStackTrace();
    }
    despa=null;
    conf=null;
    inventario.clean();
    inventario=null;
    listaactivacion.clean();
    listaactivacion=null;
    descDistribuidor=null;
  }
  /**
   * Saca un mensaje de solicitud de la cola y lo ejecuta.
   * @return El futuro de la ejecuci�n del mensaje a ser analizado como
   * respuesta.
   * @throws OACTExcPostPuesto Si no se puede ejecutar inmediatamente
   * la solicitud.
   */
  public MENSMensaje despacha() throws OACTExcPosPuesto,OACTExcepcion {
    return despacha(null);
  }
  /**
   * Ejecuta un mensaje de solicitiud.
   * @param solicitud Solicitud a ejecutar. Si es nula saca el mensaje de
   * solicitud de la cola de mensajes.
   * @return El futuro de la ejecuci�n del mensaje a ser analizado como
   * respuesta.
   * @throws OACTExcPostPuesto Si no se puede ejecutar inmediatamente
   * la solicitud.
   */
  public OACTSolicitudPrimitiva despacha(OACTSolicitudPrimitiva solicitud) throws OACTExcPosPuesto,OACTExcepcion {
    /** @todo: revisar si hay problemas de sincronizaci�n y evitar
     * los bloqueos dobles.
     */
    /** @todo: revisar c�mo se utiliza este m�todo. */
    OACTSolicitudPrimitiva m=null;
    OACTSolicitud futuro=null;
    boolean dequeue_propio=false;
    OACTSirvienteAbs oactivo1=null;
    if(solicitud!=null){
      m=solicitud;
    }else{
      // para cuando el hilo de actividad peri�dica del servidor
      // llama a esta operaci�n.
      m = this.getInventario().dequeueMensaje();
      // importante recordarlo para completar a el correspondiente
      // gestor.
      dequeue_propio=true;
    }
    try {
      // System.err.println("inicia la operaci�n de despacho");
      // crea instancia de la solicitud a ejecutar
      // System.err.println("------------> "+m.getClassName());
      // System.err.println("------------> "+m.getServantName());
      OACTSolicitud aejecutar = (OACTSolicitud)
          Class.forName(m.getClassName()).newInstance();
      /**
       * obtiene identificaci�n de la instancia del sirviente.
       */
      oactivo1=inventario.getById(m.getServantName());
      /**
       * inicializa el ejecutor para llamar a su m�todo.
       */
      aejecutar.setSirviente(oactivo1);
      aejecutar.setGestor(m.getGestorDist());
      aejecutar.loadFromSolicitud(m);      
      System.out.println(aejecutar.getXMLRootElem(null));
      if(aejecutar.isPreparado()){
        /**
         * ejecuta el m�todo del sirviente.
         */
        m.setAtendido(true);
        try{
          futuro = aejecutar.ejecutarAdmin();
        }catch(Exception ex){
          throw new OACTExcepcion("No se pudo ejecutar cuando todo estaba "+
                                  "supuestamente preparado",ex);
        }
        // System.err.println("termina la operaci�n de despacho");
      }else{
        // m�todo no fue ejecutado, vuelve a planificar la
        // solicitud de ejecuci�n.
        aejecutar=null;
        this.getInventario().addMensaje(m);
        throw new OACTExcPosPuesto("Gesti�n de servicio postpuesta"+
                                    "por tener sirviente no preparado.");
      }
      // m�todo s� fue ejecutado
      if(dequeue_propio){
        try {
          // termina de ejecutar la gesti�n del servicio.
          // el gestor debe ser informado del futuro.
          if(m.getGestorDist()!=null){
            m.getGestorDist().setFuturo(futuro);
            m.getGestorDist().completa();
          }
        }
        catch (ACONExcepcion ex1) {
          // ni modo, error al completar.
        }
      }
      m=null;
    }
    catch (InstantiationException ex) {
      System.err.println("\nError\n"+ex.getMessage());
      ex.printStackTrace();
    }
    catch (IllegalAccessException ex) {
      System.err.println("\nError\n"+ex.getMessage());
      ex.printStackTrace();
    }
    catch (ClassNotFoundException ex) {
      System.err.println("\nError\n"+ex.getMessage());
      ex.printStackTrace();
    }
    catch (MENSException ex) {
      System.err.println("\nError\n"+ex.getMessage());
      ex.printStackTrace();
    }
    catch (OACTExcepcion ex) {
      System.err.println("\nError\n"+ex.getMessage());
      ex.printStackTrace();
    }
    catch (SecurityException ex) {
      System.err.println("\nError\n"+ex.getMessage());
      ex.printStackTrace();
    }
    m=null;
    if(futuro!=null){    
      m=new OACTSolicitudPrimitiva();
      m.loadFromMensaje(futuro);
    }
    return m;
  }
  /**
   * Revisar esto.
   * @param inventario �?
   * @throws OACTExcepcion �?
   */
  protected void setInventario(OACTListaActivacion inventario) throws OACTExcepcion{
    /** @todo: revisar esto.**/
    if(inventario!=null){
      listaactivacion=inventario;
    }else{
      throw new OACTExcepcion("El inventario no se puede asignar a null");
    }
  }
  public OACTListaActivacion getInventario(){
    /** @todo: revisar esto.**/
    return listaactivacion;
  }
  public ACONDescriptor getDescripcion(){
    ACONDescriptor desc=new ACONDescriptor();
    desc.id=this.identificacion+"["+SERVER_CLASS+"]";
    desc.localhost="localhost";
    desc.localport=this.puerto;
    desc.socket_type=ACONDescriptor.STREAM;
    desc.wait=false;
    desc.server=SERVER_CLASS;
    desc.aoNavegables=new Object[]{this};
    desc.log="true";
    // desc.timestamp=this.intervalo_stream;
    return desc;
  }
  public final int getPuertoLocal(){
    return this.puerto+this.desplaza_puerto;
  }
  public final String getStatusActivacion(){
    return this.listaactivacion.getStatusActivacion();
  }
  public final String getStatusSirvientes(){
    return this.inventario.getStatusSirvientes();
  }
}