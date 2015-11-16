package aco;
import tables.*;
import java.io.*;

import mens.*;
import org.w3c.dom.*;
/**
 * Se dedica a sistematizar las bitácoras de un paquete en una sola entidad.
 */
public class BITAServidor {
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  /**
   * Lista de mensajes en la bitácora.
   */
  private AbstractSet mensajes;
  public final static String cDEME_SUPUESTO_COMPLETO="DEME_SUPUESTO_COMPLETO";
  public final static String cDEME_SOLO_ULTIMO="DEME_SOLO_ULTIMO";
  public final static String cNO_HAY_PERMISO="NO_HAY_PERMISO";
  public final static String cNO_SE_DA_A_ENTENDER="NO_SE_DA_A_ENTENDER";
  public final static String cTIEMPO_VENCIDO="TIEMPO_VENCIDO";
  public final static String cNO_DISPONIBLE="NO_DISPONIBLE";
  public final static int DEME_SUPUESTO_COMPLETO=1;
  public final static int DEME_SOLO_ULTIMO=2;
  private final static String nombre_bitacora=".log.xml";
  private ACONDescriptor desOut,desIn;
  private boolean nuevoAceptador=false;
  /**
   * Hilo que vela el tiempo de vencimiento
   */
  private Thread thvence;
  /**
   * Intervalo de espera antes de hacer flush. Si istiempo_flush==true
   * el intervalo indica minutos, si no, el intervalo indica el número
   * de registros ingresados antes del flush.
   */
  private int intervalo_flush;
  /**
   * Tiempo de espera por un stream para que el servidor no se bloquee,
   * en milisegundos.
   */
  private int stream_espera;
  private int conteo_intervalo;
  /**
   * Indica si se hace flush en intérvalos de tiempo (minutos)
   * o en intérvalos de registros recibidos.
   */
  private boolean istiempo_flush=true;
  private java.io.DataOutputStream log;
  /**
   * Nombre de la bitácora actual.
   */
  private String bitacoraactual;
  /**
   * Nombre de la bitácora anterior.
   */
  private String bitacoraanterior;
  /**
   * Clave para discriminar consultas.
   */
  private String clave;
  /**
   * Puerto para atender consultas.
   */
  private int puerto_salidas;
  /**
   * Puerto para ingresar registros.
   */
  private int puerto_ingresos;
  /**
   * Archivo de configuración.
   */
  private String archivo_conf=".bita.xml";
  private final String tag_mensaje="rec";
  private final String tag_inicial="<bitacora nombre=\"BITAServidor\">";
  private final String tag_final="</bitacora>";
  /**
   * Clase de configuración de BITAServidor.
   */
  private Config conf;
  private ACONAceptadorDesp despa;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public BITAServidor(String archivo) {
    _inicio(archivo,null);
  }
  public BITAServidor(Node nodo) {
    _inicio(null,nodo);
  }
  private void _inicio(String archivo,Node nodo){
    thvence=null;
    bitacoraactual="";
    bitacoraanterior="";
    clave="";
    conteo_intervalo=0;
    Runnable rnn=null;
    final int intervalo;
    if((archivo!=null)&&(archivo!="")){
      this.archivo_conf=archivo;
    }
    // [conf] lee información de configuración y pone valores a
    // miembros privados
    conf=new BITAServidor.Config();
    /**
     * Valores por si falla la configuración -v
     ******************************************/
    puerto_ingresos=3312;         //          |
    puerto_salidas=3313;          //          |
    istiempo_flush=true;          //          |
    intervalo_flush=10;           //          |
    clave="";                     //          |
    stream_espera=1000;           //          |
    /******************************************
     * Valores por si falla la configuración -^
     */
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
      System.err.println("Valores por defecto:");
      System.err.println("Puerto de ingresos: "+puerto_ingresos);
      System.err.println("Puerto de salidas: "+puerto_salidas);
      if(istiempo_flush){
        System.err.println("Flush orientado a tiempo: VERDADERO");
      }else{
        System.err.println("Flush orientado a tiempo: FALSO");
      }
      System.err.println("Espera: "+stream_espera);
      ex.printStackTrace();
    }
    mensajes=new AbstractSet("");
    this.bitacoraactual=conf.nombre_bitacora;
    this.clave=conf.clave;
    this.intervalo_flush=conf.intervalo_flush;
    intervalo=intervalo_flush;
    this.istiempo_flush=conf.istiempo_flush;
    if(conf.puerto_ingresos!=0){
      this.puerto_ingresos = conf.puerto_ingresos;
    }
    if(conf.puerto_salidas!=0){
      this.puerto_salidas = conf.puerto_salidas;
    }
    if(bitacoraactual==""){
      bitacoraactual=nombre_bitacora;
      conf.nombre_bitacora=nombre_bitacora;
    }
    stream_espera=conf.stream_espera;
    if(stream_espera==0){
      stream_espera=1000;
    }
    bitacoraanterior=iniciaLog(bitacoraactual);
    if((istiempo_flush) && (intervalo>0)){
      // automáticamente llama al flush
      rnn=new Runnable(){
        public void run(){
          try {
            while(true){
              synchronized(Thread.currentThread()){
                Thread.currentThread().wait(1000 * 60 * intervalo);
              }
              flushRegistros();
              System.out.println("Hilo aparte haciendo flush");
            }
          }
          catch (InterruptedException ex) {
            System.err.println("Terminado el flush periódico.");
          }
        }
      };
      thvence=new Thread(rnn,"BITAServidor.veladortiempo");
      thvence.start();
      System.out.println("Va a escribir cada "+intervalo_flush+" minutos.");
    }
  }
  /**
   * Agrega una hilera a la bitácora. Por ahora no hay estructura particular.
   * @param mensaje Mensaje a agregar a la bitácora.
   */
  public void addRegistro(String mensaje){
    synchronized(mensajes){
      conteo_intervalo++;
      mensajes.addNew("<"+tag_mensaje +">\n"+
                      mensaje+ "\n</"+tag_mensaje+">\n");
    }
    if((!istiempo_flush) && (conteo_intervalo>0)){
      if(conteo_intervalo==intervalo_flush){
        conteo_intervalo = 0;
        this.flushRegistros();
      }
    }
  }
  /**
   * Escribe en disco (append) y limpia memoria. Se supone
   * que cada vez que inicia se hace un nuevo archivo, renombrándolo como
   * archivo anterior.
   */
  public void flushRegistros(){
    String str="",contenido="";
    synchronized (mensajes){
      mensajes.moveFirst();
      while(!mensajes.getEoF()){
        contenido=(String)mensajes.getObject();
        str+= contenido;
        mensajes.moveNext();
      }
      mensajes.clean();
    }
    if(log==null){
      System.err.println("Error en el inicio de la bitácora.");
      return;
    }
    synchronized(log){
      try {
        log.writeUTF(str);
        log.flush();
      }
      catch (IOException ex) {
        System.err.println("Error al hacer flush a la bitácora en archivo "+
                           "\"" +bitacoraactual+"\".");
      }
    }
  }
  /**
   * Lee las bitácoras anterior y actual, uniéndolas y añadiendo tags XML. Se
   * supone que al inicio se registra el nombre del guarda anterior.
   * @param cuales Indica cuáles registros de bitácora se deben leer. 1 para
   * leer los registros de las bitácoras anterior y actual.
   * @return La bitacora.
   */
  public String getRegistros(final int cuales){
    String res="",contenido="",contenidoactual="",contenidoanterior="";
    final String name0=bitacoraanterior;
    final String name1=bitacoraactual;
    Thread th=null;
    Runnable rnn=new Runnable(){
      public String contenidocompleto="";
      public void run(){
        String contenido="";
        if(name1==""){
          return;
        }
        try {
          if(cuales==DEME_SUPUESTO_COMPLETO){
            //toma info vieja
            if (name0 != "") {
              java.io.DataInputStream in = new java.io.DataInputStream(
                  new FileInputStream(name0));
              while (in.available() > 0) {
                contenido += in.readUTF();
              }
            }
          }
          contenidocompleto = contenido;
          contenido="";
          java.io.DataInputStream in = new java.io.DataInputStream(
              new FileInputStream(name1));
          while (in.available() > 0) {
            contenido += in.readUTF();
          }
          contenidocompleto += contenido;
        }
        catch (FileNotFoundException ex) {
          // no se reporta nada
        }
        catch (IOException ex) {
          // no se reporta nada
        }
      }
    };
    // [thre] debería hacerlo otro thread y hacer
    // join en un punto más adelante.
    th=new Thread(rnn,"Lee_bitacora");
    th.start();
    // recupera mensajes en memoria
    synchronized (mensajes){
      mensajes.moveFirst();
      while(!mensajes.getEoF()){
        contenido=(String)mensajes.getObject();
        contenidoactual+=contenido;
        mensajes.moveNext();
      }
    }
    if (th != null) {
      try {
        th.join();
        contenidoanterior =
            ( (String) rnn.getClass().getField("contenidocompleto").get(rnn));
      }
      catch (Exception ex) {
        System.err.println("Error leyendo archivo de la bitácora anterior.");
        ex.printStackTrace();
      }
    }

    res=contenidoanterior+contenidoactual;
    res=tag_inicial+res+tag_final;
    res=tdutils.tdutils.getXMLHeader()+ res;
    return res;
  }
  public String getEncabezado(){
    /**
     * manda clave. Ver que no puse a este método un nombre
     * lealmente inteligible.
     */
    return clave;
  }
  /**
   * Crea la nueva bitácora.
   * @param name El nombre de archivo que almacena la bitácora.
   * @return El nombre del archivo que almacena la bitácora anterior.
   */
  private String iniciaLog(String name){
    File flog=null,fold=null;
    String newName="";
    log=null;
    flog=new File(name);
    if(flog.exists()){
      newName = name + ".~" + flog.lastModified()+"~";
      fold = new File(newName);
      if(!fold.exists()){
        flog.renameTo(fold);
      }
      flog=null;
      fold=null;
      flog=new File(name);
    }
    try {
      // abre un archivo capaz de acumular en cada flush
      log = new DataOutputStream(new FileOutputStream(flog, true));
    }catch (FileNotFoundException ex) {
      System.err.println("No se pudo crear la bitácora.");
      newName="";
    }
    return newName;
  }
  private ACONDescriptor getDescriptorIn(){
    ACONDescriptor desIn=new ACONDescriptor();
    desIn.id="BITAGestorIn";
    desIn.localhost="localhost";
    desIn.localport=puerto_ingresos;
    desIn.socket_type=ACONDescriptor.DGRAM;
    desIn.wait=false;
    desIn.server="aco.BITAGestorIn";
    desIn.aoNavegables=new Object[]{this};
    desIn.tamannoMaximo=300;
    desIn.log="false"; // sin bitácora para sí mismo
    return desIn;
  }
  private ACONDescriptor getDescriptorOut(){
    ACONDescriptor desOut=new ACONDescriptor();
    desOut.id="BITAGestorOut";
    desOut.localhost="localhost";
    desOut.localport=puerto_salidas;
    desOut.socket_type=ACONDescriptor.STREAM;
    desOut.wait=false;
    desOut.server="aco.BITAGestorOut";
    desOut.aoNavegables=new Object[]{this};
    desOut.log="false"; // sin bitácora para sí mismo
    return desOut;
  }
  /**
   * Pone a trabajar el servicio.
   * @param acc0 Despachador de responsabilidades.
   * @throws Exception Si hay error en los valores de inicio, el servicio no
   * se ejecuta y lanza este error.
   */
  public void open(ACONAceptadorDesp acc0) throws Exception{
    despa=acc0;
    if (despa==null){
      throw new Exception("No se puede abrir la bitácora, error "+
                          "al iniciar este servicio.");
    }
    desOut=this.getDescriptorOut();
    desIn=this.getDescriptorIn();
    try {
      despa.operaDescriptor(desOut, ACONAceptadorDesp.operaciones.ADD);
      despa.operaDescriptor(desIn, ACONAceptadorDesp.operaciones.ADD);
    }
    catch (ACONExcArbitraria ex) {
      throw new Exception("No se pudo arrancar BITAServidor, error al "+
                          "registrar servicios.",ex);
    }
    despa.operaDescriptor(desIn,ACONAceptadorDesp.operaciones.OPEN);
    despa.operaDescriptor(desOut,ACONAceptadorDesp.operaciones.OPEN);
  }
  public void close(){
    if(this.thvence!=null){
      this.thvence.interrupt();
    }
    this.flushRegistros();
    if(this.despa!=null){
      try {
        despa.operaDescriptor(desIn, ACONAceptadorDesp.operaciones.CLOSE);
        despa.operaDescriptor(desOut,ACONAceptadorDesp.operaciones.CLOSE);
      }
      catch (ACONExcArbitraria ex) {
        System.err.println("Error al cerrar descriptores de servicios.");
      }
    }
    if(log!=null){
      try {
        log.close();
      } catch (IOException e) {
        System.err.println("Error al cerrar servidor de bitácora.");
        e.printStackTrace();
      }
    }
  }
  /**
   * Devuelve el tiempo de espera para un stream.
   * @return El tiempo de espera en milisegundos.
   */
  public int getTimeStamp(){
    return this.stream_espera;
  }
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  /**
   *
   * <p>Title: ACO Patrón Aceptador Conectador</p>
   * <p>Description: Implementación del Patrón Aceptador Conectador</p>
   * <p>Copyright: Copyright (c) 2003</p>
   * <p>Company: ECCI - UCR</p>
   * @author Alessandro Cordero alesscor@ieee.org
   * @version 1.0
   */
  private final class Config extends MENSMensaje{
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    public String clave="";
    public String nombre_bitacora="";
    public int puerto_salidas=0;
    public int puerto_ingresos=0;
    public int intervalo_flush=0;
    public boolean istiempo_flush=true;
    public int stream_espera=0;
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    public boolean isVacio() {
      return false;
    }
    protected String getXMLContainedElements() {
      String xml="";
/*
       "clave"
       "archivo"
       "puerto_ingresos"
       "puerto_salidas"
       "intervalo_flush"
       "istiempo_flush"
 */
      if((this.clave!=null)&&(this.clave!="")){
        xml += "\n<clave>" + this.clave + "</clave>";
      }
      if((this.nombre_bitacora!=null)&&(this.nombre_bitacora!="")){
        xml += "\n<archivo>" + this.nombre_bitacora + "</archivo>";
      }
      if(this.puerto_ingresos!=0){
        xml += "\n<puerto_ingresos>" + this.puerto_ingresos + "</puerto_ingresos>";
      }
      if(this.puerto_salidas!=0){
        xml += "\n<puerto_salidas>" + this.puerto_salidas + "</puerto_salidas>";
      }
      if(this.intervalo_flush!=0){
        xml += "\n<intervalo_flush>" + this.intervalo_flush + "</intervalo_flush>";
      }
      if(!this.istiempo_flush){
        xml += "\n<istiempo_flush>" + "false" + "</istiempo_flush>\n";
      }
      if(this.stream_espera!=0){
        xml += "\n<stream_espera>" + this.stream_espera + "</stream_espera>\n";
      }
      xml="\n<bita>"+xml+"</bita>\n";
      return xml;
    }
    protected void setContentFromDoc(Node node, int[] nerr, String[] merr) {
      String valor="";
      if(node.getNodeName().compareToIgnoreCase("bita")!=0){
        node=MENSMensaje.getNextElement(node, "bita");
      }
      if(node!=null){
        this.clave=getElementText(node,"clave");
        this.nombre_bitacora=getElementText(node,"archivo");
        try{
          this.puerto_ingresos = Integer.parseInt(getElementText(node, "puerto_ingresos"));
        }catch(Exception ex){
          this.puerto_ingresos=0;
        }
        try{
          this.puerto_salidas = Integer.parseInt(getElementText(node, "puerto_salidas"));
        }catch(Exception ex){
          puerto_salidas=0;
        }
        try{
          this.intervalo_flush = Integer.parseInt(getElementText(node, "intervalo_flush"));
        }catch(Exception ex){
          this.intervalo_flush=0;
        }
        try{
          this.stream_espera = Integer.parseInt(getElementText(node, "stream_espera"));
        }catch(Exception ex){
          this.stream_espera=0;
        }
        this.istiempo_flush=true;
        valor=getElementText(node, "istiempo_flush");
        if ((valor.compareToIgnoreCase("false")==0)||(
            valor.compareToIgnoreCase("falso")==0)){
          this.istiempo_flush=false;
        }
        // tiempo_flush
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