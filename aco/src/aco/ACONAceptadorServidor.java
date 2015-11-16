package aco;
import mens.*;
import org.w3c.dom.*;
/**
 * Se dedica a sistematizar las bitácoras de un paquete en 
 * una sola entidad.
 */
public class ACONAceptadorServidor {
  /**
   * Objeto para el registro de servicios y su despacho en la red.
   */
  private ACONAceptadorDesp desp;
  /**
   * Descriptor de la escucha y atención de solicitudes de este objeto.
   */
  private ACONDescriptor descAtencion;
  /**
   * Servicio de bitácora.
   */
  private BITAServidor bitaServidor;
  /**
   * Puerto base de la computadora virutal, ello para poder soportar
   * múltiples computadoras virtuales sobre una misma computadora
   * real.
   */
  public int desplaza_puerto=0;
  public final static String cDEME_ESTADO_DESCRIPTORES="DEME_ESTADO_DESCRIPTORES";
  public final static String cDEME_ESTADO_GESTORES="DEME_ESTADO_GESTORES";
  public final static String cDEME_BITACORA_SUPUESTO_COMPLETO="DEME_BITACORA_SUPUESTO_COMPLETO";
  public final static String cDEME_BITACORA_SOLO_ULTIMO="DEME_BITACORA_SOLO_ULTIMO";
  public final static String cNO_HAY_PERMISO="NO_HAY_PERMISO";
  public final static String cNO_SE_DA_A_ENTENDER="NO_SE_DA_A_ENTENDER";
  public final static String cTIEMPO_VENCIDO="TIEMPO_VENCIDO";
  public final static int DEME_ESTADO_DESCRIPTORES=1;
  public final static int DEME_ESTADO_GESTORES=2;
  public final static int DEME_BITACORA_SUPUESTO_COMPLETO=3;
  public final static int DEME_BITACORA_SOLO_ULTIMO=4;
  /**
   * Tiempo de espera por un stream para que el servidor no se bloquee,
   * en milisegundos.
   */
  private int espera;
  /**
   * Clave para discriminar consultas.
   */
  private String clave;
  /**
   * Puerto de contacto.
   */
  private int puerto;
  /**
   * Archivo de configuración.
   */
  private String archivo_conf=".acon.xml";
  private String bitacora_conf=".bita.xml";
  private String acepta_conf=".acepta.xml";
  private String servicios_conf=".servicios.xml";
  private final String tag_mensaje="rec";
  private final String tag_inicial="<acon nombre=\"ACONServidor\">";
  private final String tag_final="</acon>";
  /**
   * Clase de configuración de ACONServidor.
   */
  private Config conf;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public ACONAceptadorServidor(String archivo) {
    _inicio(archivo,null);
  }
  public ACONAceptadorServidor(Node nodo) {
    _inicio(null,nodo);
  }
  private void _inicio(String archivo,Node nodo){
    clave="";
    // [conf] lee información de configuración y pone valores a
    // miembros privados
    conf=new ACONAceptadorServidor.Config();
    /**
     * Valores por si falla la configuración -v
     ******************************************/
    puerto=3311;                  //          |
    clave="";                     //          |
    espera=1000;                  //          |
    /******************************************
     * Valores por si falla la configuración -^
     */
    if((archivo!=null) && (archivo!="")){
      this.archivo_conf=archivo;
    }
    try {
      if(nodo!=null){
        conf.setFromXMLNode(nodo);
      }else{
        conf.setFromXMLURI(this.archivo_conf);
      }

    }
    catch (MENSException ex) {
      System.err.println("No se va a poder trabajar bien, no se encuentra el "+
                         "archivo de configuración: \""+this.archivo_conf+"\".");
      ex.printStackTrace();
    }
    this.clave=conf.clave;
    if(conf.puerto!=0){
      this.puerto = conf.puerto;
    }
    espera=conf.espera;
    if(espera==0){
      espera=1000;
    }
    if(conf.archivo_conf_bitacora!=""){
      bitacora_conf=conf.archivo_conf_bitacora;
    }
    if(conf.archivo_conf_servicios!=""){
      servicios_conf=conf.archivo_conf_servicios;
    }
    bitaServidor=null;
  }
  /**
   * Abre el servicio de ACONAceptador y comienza a despachar. También inicia
   * el servicio de bitácora.
   * @param acc Aceptador.
   * @throws ACONExcArbitraria Si ocurre un error, motivado principalmente
   * por la inicialización del objeto con valores inadecuados.
   */
  public void open(ACONAceptadorDesp acc) throws ACONExcArbitraria{
    if(acc==null){
      throw new ACONExcArbitraria("Debe asignarse un aceptador.");
    }else{
      desp=acc;
    }
    this.puerto=this.desplaza_puerto;
    if(servicios_conf!=null && servicios_conf!=""){
//      try {
//        desp.registraDescriptores(servicios_conf);
//      }
//      catch (ACONExcArbitraria ex) {
//        desp=null;
//        System.err.println("Error al leer servicios iniciales del aceptador.");
//      }
    }
    //[aless 040304]
    setACONAceptadorGestor();
    desp.operaDescriptor(descAtencion,ACONAceptadorDesp.operaciones.ADD|
                         ACONAceptadorDesp.operaciones.OPEN);
//    /**
//     * como aquí se inicia de una vez el aceptador, entonces se le
//     * pone a despachar.
//     */
//    desp.manejaEventos();
  }
  public String getEncabezado(){
    /**
     * manda clave. Ver que no puse a este método un nombre
     * lealmente inteligible.
     */
    return clave;
  }
  private void setACONAceptadorGestor(){
    descAtencion=new ACONDescriptor();
    descAtencion.id="ACONAceptadorGestor";
    descAtencion.localhost="localhost";
    descAtencion.localport=puerto;
    descAtencion.socket_type=ACONDescriptor.STREAM;
    descAtencion.wait=false;
    descAtencion.server="aco.ACONAceptadorGestor";
    descAtencion.aoNavegables=new Object[]{this,desp,bitaServidor};
    descAtencion.log="false"; // sin bitácora para sí mismo
  }
  public void close(){
    if(this.desp!=null){
      desp.close();
    }
  }
  /**
   * Devuelve el tiempo de espera para un stream.
   * @return El tiempo de espera en milisegundos.
   */
  public int getTimeStamp(){
    return this.espera;
  }
  public void setBitacora(BITAServidor bita){
    bitaServidor=bita;
  }
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  /**
   * Clase de configuración.
   */
  private final class Config extends MENSMensaje{
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    public String clave="";
    public String archivo_conf_bitacora="";
    public String archivo_conf_servicios="";
    public int puerto=0;
    public int espera=0;
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    public boolean isVacio() {
      return false;
    }
    protected String getXMLContainedElements() {
      String xml="";
      if((this.clave!=null)&&(this.clave!="")){
        xml += "\n<clave>" + this.clave + "</clave>";
      }
      if((this.archivo_conf_bitacora!=null)&&(this.archivo_conf_bitacora!="")){
        xml += "\n<archivo_conf_bitacora>" + this.archivo_conf_bitacora +
            "</archivo_conf_bitacora>";
      }
      if((this.archivo_conf_servicios!=null)&&(this.archivo_conf_servicios!="")){
        xml += "\n<archivo_conf_servicios>" + this.archivo_conf_servicios +
            "</archivo_conf_servicios>";
      }
      if(this.puerto!=0){
        xml += "\n<puerto>" + this.puerto + "</puerto>";
      }
      if(this.espera!=0){
        xml += "\n<espera>" + this.espera + "</espera>\n";
      }
      xml="\n<acocons>"+xml+"</acocons>\n";
      return xml;
    }
    protected void setContentFromDoc(Node node, int[] nerr, String[] merr) {
      String valor="";
      if(node.getNodeName().compareToIgnoreCase("acocons")!=0){
        node=MENSMensaje.getNextSiblingElement(node, "acocons");
      }
      if(node!=null){
        this.clave=getElementText(node,"clave");
        this.archivo_conf_bitacora=getElementText(node,"archivo_conf_bitacora");
        this.archivo_conf_servicios=getElementText(node,"archivo_conf_servicios");
        try{
          this.puerto = Integer.parseInt(getElementText(node, "puerto"));
        }catch(Exception ex){
          this.puerto=0;
        }
        try{
          this.espera = Integer.parseInt(getElementText(node, "espera"));
        }catch(Exception ex){
          this.espera=0;
        }
      }
    }
    protected void toleraXML(int[] nerr, String[] merr) {
    }
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
  }
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
}
