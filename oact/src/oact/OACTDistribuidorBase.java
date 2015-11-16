package oact;

import aco.*;
import org.w3c.dom.*;
import mens.*;

import java.util.Map;
import java.util.zip.*;

/**
 * Planificador del patrón objeto activo. Actúa como el objeto que
 * distribuye las llamadas a métodos de objetos activos (sirvientes).
 */
public class OACTDistribuidorBase {
  protected ACONConectorDesp conector;
  protected String direccion;
  /**
   * Objeto de configuración.
   */
  protected Configuracion conf;
  /**
   * Despachador de servicios.
   */
  protected ACONAceptadorDesp despa;
  /**
   * Descriptor de servicios.
   */
  protected ACONDescriptor descDistribuidor;
  /**
   * Intervalo de espera en milisegundos
   */
  protected int intervalo_stream;
  /**
   * Puerto de entrada de mensajes para objetos activos.
   */
  protected int puerto;
  /**
   * Milisegundo después del 1 del 1 de 1970, en el que se
   * construyó este objeto.
   */
  protected long inicio;
  /**
   * Cantidad de caracteres para considerar un mensaje como comando.
   */
  protected int cantcomando;
  /**
   * Clave para ejecutar un comando.
   */
  protected String clavecomando;
  protected String identificacion;
  /**
   * Nombre del nodo principal en un archivo de
   * configuración
   */
  protected String nombreNodo;
  /**
   * Mapa de computadoras virtuales, donde
   * en una computadora se pueden tener varias
   * computadoras virtuales. En este mapa se brinda
   * la resolución de nombres o direcciones y 
   * puertos base a sumar a puertos de envío.
   */
  private Map mapaComputadorasVirtuales;
  /**
   * Inicia un distribuidor básico.
   * @param archivo Nombre de archivo XML de configuración.
   */
  public OACTDistribuidorBase(String archivo) {
    nombreNodo="oact";
    _inicio(archivo,null);
  }
  /**
   * Inicia un distribuidor básico.
   * @param archivo Archivo XML de configuración.
   * @param cNombreNodo Nombre de la raíz del objeto de configuración.
   */
  public OACTDistribuidorBase(String archivo,String cNombreNodo) {
    nombreNodo=cNombreNodo;
    _inicio(archivo,null);
  }
  /**
   * Inicia un distribuidor básico.
   * @param nodo Nodo de configuración (objeto DOM de XML).
   */
  public OACTDistribuidorBase(Node nodo) {
    nombreNodo="oact";
    _inicio(null,nodo);    
  }
  /**
   * Inicia un distribuidor básico.
   * @param nodo Nodo de configuración (objeto DOM de XML).
   * @param cNombreNodo Nombre de la raíz del objeto de configuración.
   */
  public OACTDistribuidorBase(Node nodo,String cNombreNodo) {
    nombreNodo=cNombreNodo;
    _inicio(null,nodo);    
  }  
  private void _inicio(String archivo,Node nodo){
    inicio=System.currentTimeMillis();
    conf=new Configuracion();
    direccion="";
    // para navegación de propieadades por parte de nuevos objetos
    // activos que necesitan info de configuración.
    descDistribuidor=null;
    despa=null;
    /*
     * Valores por si la configuración falla  -v
     */
    intervalo_stream=1000;
    puerto=4410;
    cantcomando=conf.cantcomando;
    clavecomando=conf.clavecomando;
    identificacion="distri["+this.getClass().getName()+"]";
    /*
     * Valores por si la configuración falla -^
     */
    if(archivo==null || archivo==""){
      archivo=conf.archivo;
    }
    try{
      if(nodo!=null){
        conf.setFromXMLNode(nodo);
      }else{
         conf.setFromXMLURI(archivo);
      }
      if(conf.puerto>0){
        puerto=conf.puerto;
      }
      if(conf.identificacion!=null && conf.identificacion!=""){
        this.identificacion=conf.identificacion;
      }
      if(conf.intervalo>0){
        intervalo_stream=conf.intervalo;
      }
      if(conf.conbitacora){
        OACTBitacora.setConBitacora(conf.conbitacora);
      }
      if(conf.puertobitacora>0){
        OACTBitacora.setPuertoBitacora(conf.puertobitacora);
      }
      cantcomando=conf.cantcomando;
      clavecomando=conf.clavecomando;
    }catch(MENSException ex){
      System.err.println("Error al leer configuración.");
      System.err.println("Usando valores por defecto:");
      System.err.println("puerto: "+puerto);
      System.err.println("espera: "+intervalo_stream);
      ex.printStackTrace();
    }
    /*
     * Abre inventario, va a contener las solicitudes de ejecución
     * de método.
     */
    conector=new aco.ACONConectorDesp();
    descDistribuidor=this.getDescripcion();
    // this.mapaComputadorasVirtuales=new TreeMap(String.CASE_INSENSITIVE_ORDER);
  }

  /**
   * Asigna el mapa de direcciones virtuales, a utilizar para
   * resolver nombres de las múltiples computadoras virtuales que 
   * pueden correr en una computadora.
   * @param direcciones Mapa de direcciones virtuales.
   */
  public void setDireccionesVirtuales(Map direcciones){
    mapaComputadorasVirtuales=direcciones;
  }
  /**
   * Carga los valores reales de una dirección si ésta se registra como
   * virtual.
   * @param descriptor Descriptor de un extremo de servicio en otra
   * computadora.
   */
  public void resuelveDireccionesVirtuales(ACONDescriptor descriptor){
    if(mapaComputadorasVirtuales!=null){
      DireccionesVirtuales dirv=
        (DireccionesVirtuales)mapaComputadorasVirtuales.get(
            descriptor.remotehost);
      if(dirv!=null){
        descriptor.remotehost=dirv.direccionVerdadera;
        descriptor.remoteport=dirv.puertoBase+descriptor.remoteport;
      }
    // si la dirección no es virtual, entonces todo queda igual.
    }
  }
  /**
   * Indica si una dirección está registrada con una dirección virtual.
   * @param alias
   * @return Si la dirección es virtual.
   */
  public boolean siDireccionVirtual(String alias){
    DireccionesVirtuales resultado=(DireccionesVirtuales)
        mapaComputadorasVirtuales.get(alias);
    if(resultado!=null){
      return resultado.puertoBase==0;
    }
    return false;
  }
  public ACONDescriptor getDescripcion(){
    ACONDescriptor desc=new ACONDescriptor();
    desc.id=this.identificacion;
    desc.localhost="localhost";
    desc.localport=this.puerto;
    desc.socket_type=ACONDescriptor.STREAM;
    desc.wait=false;
    desc.server="oact.OACTGestorDistribuidor";
    desc.aoNavegables=new Object[]{this};
    desc.log="true";
    // desc.timestamp=this.intervalo_stream;
    return desc;
  }
  /**
   * Recibe una hilera y devuelve su correspondiente valor descomprimido
   * según el inflar de zlib.
   * @param hilera Hilera a descomprimir.
   * @return Hilera descomprimida.
   * @throws OACTExcepcion Si hay error en la hilera de entrada.
   */
  public final static String infla(String hilera) throws OACTExcepcion{
    byte[] buffer=new byte[hilera.length()];
    int nTamano=0;
    String comprimido="";
    java.util.zip.Inflater inflador=new java.util.zip.Inflater();
    inflador.setInput(hilera.getBytes());
    try {
      nTamano=inflador.inflate(buffer);
      inflador.end();
    }catch (DataFormatException ex) {
      throw new OACTExcepcion("Error al descomprimir.",ex);
    }
    comprimido=new String(buffer,0,nTamano);
    return comprimido;
  }
  /**
   * Recibe una hilera y devuelve su correspondiente valor comprimido
   * según el desinflar de zlib.
   * @param hilera Hilera a comprimir.
   * @return Hilera comprimida.
   * @throws OACTExcepcion Si hay error en la hilera de entrada.
   */
  public final static String desinfla(String hilera) throws OACTExcepcion{
    byte[] buffer=new byte[tdutils.tdutils.ZIP_MAXLENTH];
    int nTamano=0;
    String descomprimido="";
    java.util.zip.Deflater desinflador=new java.util.zip.Deflater();
    desinflador.setInput(hilera.getBytes());
    desinflador.finished();
    nTamano=desinflador.deflate(buffer);
    descomprimido=new String(buffer,0,nTamano);
    return descomprimido;
  }
  final public void interProxy(String destino, OACTSolicitud mensaje, String[] salida) throws OACTExcepcion{
    interProxy(destino,salida,mensaje);
  }
  /**
   * Realiza intercambio entre un Proxy y un sirviente de
   * un objeto activo.
   * @param destino Nodo del sirviente de interés.
   * @param salida Mensaje a recibir del sirviente (respuesta de solicitud).
   * @param solicitud Solicitud a la cual se le asigna el gestor para ser capaz de
   * comunicarse en la red.
   * @throws OACTExcepcion Si hay error para el envío de la solicitud.
   */
  final public void interProxy(String destino, String[] salida,
      OACTSolicitud solicitud)throws OACTExcepcion{
    String entrada="";
    OACTGestorProxy gestor=null;
    ACONDescriptor desc=null;
    OACTSolicitudPrimitiva sol=null;
    if(solicitud==null){
      throw new OACTExcepcion("La solicitud es nula.");
    }
    if(solicitud.isPreparado()){
      desc=this.getDescripcion();
	    if(this.despa==null){
	    	/*
	    	 * se trata de un cliente que llama por medio del proxy
	    	 * al sirviente adecuado.
	    	 */
          desc.remotehost=destino;
          desc.localport=0;
          desc.id="OACTGestorProxy.Cliente";
	    }else{
	    	/*
	    	 * se trata de un servidor que llama por medio del proxy al
	    	 * sirviente adecuado.
	    	 */
          desc.remotehost=destino;
          desc.remoteport=desc.localport;
          desc.localport=0;
          desc.id="OACTGestorProxy.Servidor";
          this.resuelveDireccionesVirtuales(desc);
	    }
      sol=new OACTSolicitudPrimitiva();
      sol.loadFromMensaje(solicitud);
      entrada=sol.getXMLRootElem(null);
      gestor=new OACTGestorProxy(this, desc,entrada,salida[0],solicitud);
      try {
        this.conector.conecta(gestor);
        salida[0] = gestor.getResultado();
      }
      catch (ACONExcOmision ex) {
        salida[0]="";
        throw new OACTExcepcion("La solicitud no se envió a su receptor.",ex);
      }
      catch (ACONExcArbitraria ex) {
        salida[0]="";
        throw new OACTExcepcion("La solicitud no se envió a su receptor.",ex);
      }
    }else{
      // hubo error.
      salida[0]="";
      throw new OACTExcepcion("La solicitud no estaba preparada o no debió realizarse.");
    }
    desc=null;
  }
  public final void cargaSolicitud(OACTSolicitud obj,String contenido) throws
  OACTExcepcion {
    String contenido0="";
    OACTSolicitudPrimitiva obj0=new OACTSolicitudPrimitiva();
    if(obj==null){
      obj0=null;
      return;
    }
    try{
      obj0.loadFromString(contenido);
    }catch(MENSException ex){
      // el objeto no estaba en XML
      // busca si estaba comprimido
      try {
        contenido0 = OACTDistribuidorBase.infla(contenido);
        obj0.loadFromString(contenido);
      }catch(MENSException ex1){
        // el objeto comprimido no estaba en XML
        throw new OACTExcepcion("No se pudo poner contenido XML al objeto.",ex);
      }
      catch (OACTExcepcion ex1) {
        // el objeto no estaba comprimido
        throw new OACTExcepcion("No se pudo poner contenido XML al objeto.",ex);
      }
    }
    // aquí obj0 está listo
    try{
      obj.loadFromSolicitud(obj0);
    }catch(MENSException ex){
      throw new OACTExcepcion("No se pudo cargar el objeto.",ex);
    }
    // todo bien
  }
  public int getTimeStamp(){
    return this.intervalo_stream;
  }
  public final long getInicio(){
    return inicio;
  }
  public final int getCantComando(){
    return cantcomando;
  }
  public final String getPie(){
    return clavecomando;
  }
  public String getXMLConcreto(){
    return "";
  }
  public void setFromXMLConcreto(Node nodo){
  }
  /**
   * Clase para encapsular direcciones virtuales, y así
   * resolver las direcciones reales de los paquetes a
   * enviar.
   */
  public static class DireccionesVirtuales{
    public String direccionVerdadera;
    public String direccionVirtual;
    public int puertoBase;
    public DireccionesVirtuales(String alias,String direccion,int puertobase){
      direccionVerdadera=direccion;
      direccionVirtual=alias;
      puertoBase=puertobase;
    }
  }
  
  /**
   *
   * <p>Title: Proto OAderive</p>
   * <p>Description: Prototipo para OAderive</p>
   * <p>Copyright: Copyright (c) 2003</p>
   * <p>Company: UCR - ECCI</p>
   * @author Alessandro Cordero [alesscor@ieee.org]
   * @version 1.0
   */
  /***
   * Clase especial para leer la configuración de la clase
   * pública.
   */
  private class Configuracion extends mens.MENSMensaje{
    //////////////////////////////////////////////////////////////////////
    // interesa puerto, tiempo de espera e info de bitácora
    public String archivo=".distribuidor.xml";
    private int puerto;
    private int intervalo;
    private int cantcomando=30;
    private String clavecomando="";
    private boolean conbitacora=OACTBitacora.getConBitacora();
    private int puertobitacora=OACTBitacora.getPuertoBitacora();
    /**
     * Nombre con el que se identifica el objeto activo
     * en un descriptor.
     */
    private String identificacion="";
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    public boolean isVacio(){
      return false;
    }
    public Configuracion(){
      this.setNombreNodoPrincipal(nombreNodo);
    }
    protected void setContentFromDoc(Node node,int[] error,String[] mens){
      String valor="";
      if(node.getNodeName().compareToIgnoreCase(this.getNombreNodoPrincipal())!=0){
        node=MENSMensaje.getNextElement(node,this.getNombreNodoPrincipal());
      }
      if(node!=null){
        this.identificacion=getElementText(node,"identificacion");
        this.clavecomando=getElementText(node,"clave");
        try{
          this.cantcomando=Integer.parseInt(getElementText(node, "cantComando"));
        }catch(Exception ex){
        }
        if(this.cantcomando<0){
          this.cantcomando=30;
        }
        valor=getElementText(node,"con_bitacora");
        if(valor==""){
          this.conbitacora=false;
        }
        if(valor.compareToIgnoreCase("true")==0){
          this.conbitacora=true;
        }else{
          if(valor.compareToIgnoreCase("verdadero")==0){
            this.conbitacora=true;
          }
        }
        try{
          this.puertobitacora = Integer.parseInt(getElementText(node, "puerto_bitacora"));
        }catch(Exception ex){
          this.puertobitacora=0;
        }
        try{
          this.puerto = Integer.parseInt(getElementText(node, "puerto"));
        }catch(Exception ex){
          this.puerto=0;
        }
        try{
          this.intervalo = Integer.parseInt(getElementText(node, "intervalo"));
        }catch(Exception ex){
          this.intervalo=0;
        }
        setFromXMLConcreto(node);
      }
    }
    protected String getXMLContainedElements(){
      String xml="";
      xml += "\n<identificacion>"+identificacion+"</identificacion>";
      xml += "\n<clave>" + this.clavecomando + "</clave>";
      xml += "\n<cantComando>" + this.cantcomando + "</cantComando>";
      if(this.puertobitacora!=0){
        xml += "\n<puerto_bitacora>" + this.puertobitacora + "</puerto_bitacora>";
      }
      if(!conbitacora){
        xml += "\n<con_bitacora>false</con_bitacora>";
      }
      xml+=
          "  <puerto><!-- Para el descriptor en nombre del distribuidor -->"+
            puerto+"</puerto>\n"+
          "  <intervalo><!-- Para el descriptor que espera los eventos del distribuidor-->"+
            intervalo+"</intervalo>\n";
      xml+=getXMLConcreto();
      xml="\n<"+this.getNombreNodoPrincipal()+">"+xml+
            "</"+this.getNombreNodoPrincipal()+">\n";
      return xml;
    }
    public int getPuertoLocal(){
      return this.puerto;
    }    
    protected void toleraXML(int[] error,String[] mens){
    }
    //////////////////////////////////////////////////////////////////////
  }
  final public class Mensajes{
    public final static String NO_HAY_PERMISO="NO_HAY_PERMISO";
    public final static String NO_SE_DA_A_ENTENDER="NO_SE_DA_A_ENTENDER";
    public final static String TIEMPO_VENCIDO="TIEMPO_VENCIDO";
    public final static String SIN_IMPLEMENTAR="SIN_IMPLEMENTAR";
    public final static String NO_DISPONIBLE="NO_DISPONIBLE";
    public final static String DEME_ESTADO_SIRVIENTES="DEME_ESTADO_SIRVIENTES";
    public final static String DEME_ESTADO_ACTIVACION="DEME_ESTADO_ACTIVACION";
  }
  public final void setIdentificacion(String nombre){
    this.identificacion=nombre;
  }
}
