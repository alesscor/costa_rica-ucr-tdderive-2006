package admin;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import mens.MENSException;
import mens.MENSMensaje;
import oact.OACTDistribuidor;

import org.w3c.dom.Node;
import orgainfo.OIConexion;
import orgainfo.OIEstado_instancia;
import orgainfo.OIExcepcion;

import tdutils.Invocable;
import aco.ACONAceptadorDesp;
// import aco.ACONAceptadorServidor;
// import aco.BITAServidor;
import admin.PERSAmbiente.Computadora;
import admin.PERSRecuperacion.Estado_instancia;
import admin.PERSRecuperacion.Estado_tablas;

/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Mantiene información de las instancias de <b><tt>tdderive</tt></b> en general,
 * que se inician cuando el sistema se pone en marcha.
 */

class ADMINGLOServidor {
  /**
   * Indica que <tt>tdderive</tt> va a cerrarse.
   */
  static final int INSTANCIACERRADA=16;
  /**
   * Indica que otra aplicación se ha apropiado de recursos exclusivos
   * de <tt>tdderive</tt>.
   */
  static final int INSTANCIANOFACTIBLE=2;
  /**
   * Indica que otra instancia de admin está en curso.
   */
  static final int INSTANCIAOTRA=1;
  /**
   * Indica que <tt>tdderive</tt> se ha abierto por primera vez.
   */
  static final int INSTANCIAPRIMERAVEZ=32;
  /**
   * Indica que <tt>tdderive</tt> trabaja luego de un cierre brusco.
   */
  static final int INSTANCIARECUPERADA=8;
  /**
   * Indica que <tt>tdderive</tt> trabaja normalmente.
   */
  static final int INSTANCIAVALIDA=4;
  /**
   * Objeto para receptar mensajes y despacharlos
   * al objeto responsable de su atención
   * (patrón <tt>Aceptador-Conector</tt>) en la administración
   * de tdderive.
   */
  ACONAceptadorDesp acoDespachador;
  /*
   * Objeto para consultar el intercambio de mensajes
   * (patrón <tt>Aceptador-Conector</tt>).
   */
  // ACONAceptadorServidor acoReportero;
  /**
   * Nombre por omisión del archivo de configuración global.
   */
  private String arch_conf_global="admin_global.xml";
  /**
   * Nombre por omisión del archivo de configuración local.
   */
  private String arch_conf_local="admin.xml";
  /*
   * Objeto de bitácora (recepción de mensajes enviados por
   * objetos de las distintas capas de tdderive que estén en la compu
   * local).
   */
  // BITAServidor bitaBitacora;
  // --------------------------------------´
  private ConfigGlobal confGlobal;
  private ConfigLocal confLocal;
  /**
   * Indica el estado de la instancia actual de admin en general y de tdderive
   * en general.
   */
  public int estado_instancia;
  /**
   * Objeto de información.
   */
  ADMINGLOInfo info;
  ADMINPOLLector lector=null;
  private Node nodoConfGlobal;
  private Node nodoConfLocal;
  /**
   * Realiza las funciones de despacho de servicios
   * a brindar por sirvientes responsables (patrón <tt>Objeto Activo</tt>
   * en la administración de tdderive).
   */
  OACTDistribuidor oactDespachador;
  /**
   * Se encarga de mantener comunicación entre los planificadores
   * de tdderive (para la administración de tdderive).
   */
  OACTDistribuidor oactBalanceador;
  /**
   * Sirviente encargado de procesar solicitudes de tareas y subtrabajos
   * y prepararlos para el planificador del sistema que eventualmente
   * los ejecutará.
   */
  ADMINAPPDespachador sirvienteAPP;
  /**
   * Sirviente encargado de la planificación de los subtrabajos
   * del sistema y del balance de carga global.
   */
  DIRBalances sirvienteBAL;

  public ADMINGLOServidor(String nombreglobal,String nombrelocal) {
    PERSAmbiente.Computadora compu=null;
    // debe leerse el objeto de configuración
    this.nodoConfGlobal=null;
    this.nodoConfLocal=null;
    if(nombreglobal!=null && nombreglobal!=""){
      this.arch_conf_global=nombreglobal;
    }
    if(nombrelocal!=null && nombrelocal!=""){
      this.arch_conf_local=nombrelocal;
    }
    /*
     * lee configuración global (solo 
     * va a preparar nodo XML)
     */
    this.confGlobal = new ConfigGlobal();
    try {
      this.confGlobal.setFromXMLURI(this.arch_conf_global);
      this.nodoConfGlobal=confGlobal.getDocumento();
    } catch (MENSException e) {
      e.printStackTrace();
    }
    /*
     * lee configuración local (carga
     * valores y prepara nodo XML)
     */
    this.confLocal = new ConfigLocal();
    try {
      this.confLocal.setFromXMLURI(this.arch_conf_local);
      this.nodoConfLocal=this.confLocal.getDocumento();
    } catch (MENSException e) {
      e.printStackTrace();
    }
    /*
     * prepara la base de datos
     */
    info=new ADMINGLOInfo(
        this.confLocal._alias,
        this.confLocal._bd_controlador,
        this.confLocal._bd_url,
        this.confLocal._usuario,"",
        this.confLocal._script_inibd_url,
        this.confLocal._script_finbd_url);
    compu=new PERSAmbiente.Computadora(info);
    lector=new ADMINPOLLector(info);
    info.setLector(lector);
    cargaConfBajoNivel(compu);
    //
    // se inician los objetos de información persistentes iniciales
    //  ___________________________________________________
    //                                                      `
    info.setComputadora(compu);                          // |
    info.setEstadoTablas(new Estado_tablas(info));       // |
    info.setEstadoInstancia(new Estado_instancia(info)); // |
    //_____________________________________________________´
    /*
     * los pesos, umbrales, ubicaciones y dominios de
     * balance se obtienen del archivo de la configuración
     * global.
     */
    info.setPesosUmbralesGlobalesUrl(nombreglobal);
    info.setUbicacionesUrl(nombreglobal);
    /*
     * este valor predomina hasta el open de objetos persistentes, método a
     * partir del cual se lee de la base de datos.
     */
    if(this.confGlobal.clase_balance!=null && this.confGlobal.clase_balance!=""){
      info.setClaseBalance(this.confGlobal.clase_balance);
    }
    this.confLocal._aco_archivo=nombreglobal;
    this.confLocal._aco_cons_archivo=nombreglobal;
    this.confLocal._bitacora_archivo=nombreglobal;
    this.confLocal._oact_despa_archivo=nombreglobal;
    this.confLocal._oact_plani_archivo=nombreglobal;
    /*
     * asigna el nombre del directorio base de tdderive.
     */
    info.setRaiztdderive(this.confLocal._raiz_tdderive);
    /*
     * los programas se leen del archivo de la configuración local.
     */
    info.setPesosUmbralesLocalesUrl(nombrelocal);
    info.setProgramasUrl(nombrelocal);
    sirvienteAPP=new ADMINAPPDespachador(info);
    info.setPlanificador(new ADMINPOLPlanificador(info));
    info.setDespachador(sirvienteAPP);
  }
  void close()throws ADMINGLOExcepcion{
    info.println("programa","inicia final");
    _cierraPersistentes();
    _cierraSubyacentes();
    info.println("programa","termina final");
  }
  void open()throws ADMINGLOExcepcion{
    info.println("programa","inicia inicio");
    if(this.nodoConfGlobal==null){
      info.println("programa","erra inicio");
      throw new ADMINGLOExcepcion("El archivo de configuración " +
                               "global no se encuentra.");
    }
    _abrePersistentes();
    _abreSubyacentes();
    _abreAdministracion();
    info.setPuerto(this.oactDespachador.getDescripcion().localport);
//    info.println("programa","nombre servidor:"+this.info.getAliasLocal());
    info.println("programa","termina inicio");
    
    info.println("despachador","puerto:"+this.oactDespachador.getPuertoLocal());
    info.println("balanceador","puerto:"+this.oactBalanceador.getPuertoLocal());
  }
  /**
   * 
   */
  private void _abreAdministracion() throws ADMINGLOExcepcion {
    Computadora compu=info.getComputadora();
    Constructor constructor;
    int espera_s=0;
    /*
     * Pone a trabajar al planificador con su clase de balance
     * concreta definida (sacada de objetos persistentes o de
     * los archivos de configuración).
     */
    if(compu.getClaseBalance()!=null && compu.getClaseBalance()!=""){
      info.setClaseBalance(compu.getClaseBalance());
    }else{
      if(info.getClaseBalance()==null||info.getClaseBalance()==""){
        info.setClaseBalance(this.confGlobal.clase_balance);
      }
      compu.setClaseBalance(info.getClaseBalance());
    }
    //
    // se carga la instancia del balance de carga
    // __________________________________________________________________
    //                                                               //   `   
    sirvienteBAL=null;                                              //    '
    try{                                                             //    '
      constructor=Class.forName(info.getClaseBalance()).             //    '
          getConstructor(new Class[]{ADMINGLOInfo.class});           //    '
      if(constructor!=null){                                         //    '
        sirvienteBAL=(DIRBalances)constructor.newInstance(          //    '
             new Object[]{info});                                    //    '
        oactBalanceador.getObjetosActivos().addNew(sirvienteBAL);  //    '
      }else{                                                         //    '
        sirvienteBAL=null;                                          //    '
        System.err.println("No se encontró el constructor " +        //    '
          "de la clase '"+  info.getClaseBalance() +"'.");           //    '
        throw new ADMINGLOExcepcion("No se pudo iniciar el " +       //    '
            "sirviente de balance de carga");                        //    '
      }                                                              //    '
    }catch(Exception ex){                                            //    '
      throw new ADMINGLOExcepcion("No se pudo iniciar el " +         //    '
          "sirviente de balance de carga",ex);                       //    '
    }                                                                //    '
    // __________________________________________________________________ ´
    if(sirvienteBAL==null){
      throw new ADMINGLOExcepcion("No se pudo iniciar el " +
          "sirviente de balance de carga");
    }
    info.setBalanceador(sirvienteBAL);
    /*
     * Milisegundos que el lector espera antes de volver a
     * leer el sistema.
     */
    try{
      espera_s=(int)compu.getPesoUmbralPolitica("lector_intervalo_s");
      if(espera_s==0){
        if(this.confLocal._intervalo_s!=0){
          espera_s=this.confLocal._intervalo_s;
        }else{
          espera_s=ADMINPOLLector.INTERVALOms_OMISION/1000;
        }    
      }
    lector.setIntervaloms(espera_s*1000);
    }catch(ADMINGLOExcepcion e){
      if(this.confLocal._intervalo_s!=0){
        lector.setIntervaloms(this.confLocal._intervalo_s*1000);
      }else{
        lector.setIntervaloms(ADMINPOLLector.INTERVALOms_OMISION);
      }    
    }
    /*
     * registra direcciones virtuales de computadoras vecinas.
     */
    oactDespachador.setDireccionesVirtuales(info.getDireccionesVirtuales());
    oactBalanceador.setDireccionesVirtuales(info.getDireccionesVirtuales());
    /*
     * registra los métodos de observadores de lecturas internas
     */
    lector.registraMetodo(new Invocable(){
      public Object invoca(Object args[]){
        info.getPlanificador().notificaLectura((Computadora)args[0]);
        return null;
      }
    });
    // registra los métodos de observadores de planificaciones externas
    sirvienteBAL.registraMetodo(new Invocable(){
      public Object invoca(Object args[]){
        Map[] mapas=(Map[])args;
        info.getPlanificador().notificaTransferencia(mapas[0],
            mapas[1],mapas[2]);
        return null;
      }
    });
    /*
     * recarga trabajos en el planificador
     */
    info.getPlanificador().recargaTrabajos();
    /*
     * abre instancias lectoras local (lector) y externa (sirvienteBAL).
     */
    lector.open();
    sirvienteBAL.open();
  }
  /**
   * Inicia componentes persistentes de la aplicación, detectando cuál
   * es el estado de la aplicación, según la información gruardada.
   * Realiza actividades para el inicio de los servicios de tdderive.
   * @throws ADMINExcepcion Si ocurren errores por falta de un controlador
   * de datos o de JDBC.
   */
  private void _abrePersistentes() throws ADMINGLOExcepcion{
    estado_instancia=OIEstado_instancia.INSTANCIANOFACTIBLE;
    try{
      estado_instancia=info.open();
    }
    catch(OIExcepcion ex){
      estado_instancia=OIEstado_instancia.INSTANCIANOFACTIBLE;
      throw new ADMINGLOExcepcion("No es factible iniciar la instancia.",ex);
    }
    switch(estado_instancia){
      case OIEstado_instancia.INSTANCIAPRIMERAVEZ:
        //
        // borra el directorio de trabajo principal, si existía y lo vuelve a crear
        //
        File fDirTrabajos=null;
        fDirTrabajos=new File(info.getRaiztdderive()+"/"+
                ADMINAPPITareas.DIR_TRABAJOS);
        if(fDirTrabajos.exists()){
          if(!tdutils.tdutils.borraDir(fDirTrabajos)){
            System.out.println("No se pudo borrar algún archivo " +
                    "del directorio de trabajo.");
          }else{
            System.out.println("Se han borrado los archivos del " +
                    "directorio de trabajo.");
          }
        }
        fDirTrabajos.mkdirs();
        break;
      case OIEstado_instancia.INSTANCIACERRADA:
        break;
      case OIEstado_instancia.INSTANCIANOFACTIBLE:
      case OIEstado_instancia.INSTANCIAOTRA:
        throw new ADMINGLOExcepcion("No es factible iniciar la instancia.");
      case OIEstado_instancia.INSTANCIARECUPERADA:
        // debe leer la base de datos y recuperarse
        //
        // info de sí misma y de otras computadoras es leída de la base
        // de datos
        break;
      case OIEstado_instancia.INSTANCIAVALIDA:
        // debe leer la base de datos
        //
        // info de sí misma y de otras computadoras es leída de la base
        // de datos
        break;
    }
//    info.actualizaInfoCompu();
  }
  /**
   * Inicia los componentes subyacentes a la capa de administración.
   * <li><tt>aco</tt>.</li>
   * <li><tt>bitacoras.</tt></li>
   * <li><tt>oact.</tt></li><br>
   * Aquí se determina el estado de la instancia.
   * @throws OIExcepcion Error si algún recurso ha sido apropidado
   * antes de que lo haya apropiado en forma exclusiva la administración de
   * <tt>tdderive</tt>.
   */
  private void _abreSubyacentes() throws ADMINGLOExcepcion{
    boolean apropiados=false;
    boolean otrainstancia=false;
    boolean caidoantes=false;
    Node nodoAbreviador=this.nodoConfGlobal;
    /*
     * (1) si apropiados
     * (1.a) revisa si es otra instancia de tdderive (revisa bd, si la hay)
     * (1.a.i) si no hay bd, sale
     * (1.a.ii) si hay bd, revisa valores de Instancia_TDDerive.
     */
    acoDespachador=new ACONAceptadorDesp(this.nodoConfGlobal);
    // bitaBitacora=new BITAServidor(nodoConfGlobal);
    // acoReportero=new ACONAceptadorServidor(nodoConfGlobal);
    acoDespachador.manejaEventos();
    nodoAbreviador=MENSMensaje.getNextElement(this.nodoConfGlobal, "nivel_oact_planificador");
    oactBalanceador=new OACTDistribuidor(nodoAbreviador);
    oactBalanceador.setIdentificacion("oactBalanceador");
    nodoAbreviador=MENSMensaje.getNextElement(this.nodoConfGlobal, "nivel_oact_despachador");
    oactDespachador=new OACTDistribuidor(nodoAbreviador);
    oactDespachador.setIdentificacion("oactDespachador");
    // info.actualizaInfoCompu();
    /*
     * Si hay errores, es o porque la configuración estaba
     * mal hecha o porque hubo recursos que están siendo
     * utilizados en forma exclusiva por otro sistema,
     * que incluso puede ser otra instancia de tdderive.
     */
    try {
      // abre bitácora
      // bitaBitacora.open(acoDespachador);
      // abre servidor de consultas del aceptador
      // acoReportero.desplaza_puerto=info.getDesplazaPuerto();
      // acoReportero.open(acoDespachador);
      // abre servidores de objetos activos
      oactDespachador.desplaza_puerto=info.getDesplazaPuerto();
      oactDespachador.open(acoDespachador);
      oactBalanceador.desplaza_puerto=info.getDesplazaPuerto();
      oactBalanceador.open(acoDespachador);
      // ingresa los sirvientes del despachador
      oactDespachador.getObjetosActivos().addNew(sirvienteAPP);
      /*
       * ingresa los sirvientes del planificador: postpuesto
       * pues se necesita información de la base de datos
       * y si no, de los archivos de configuración. Ver el open
       * de administración, _abreAdministracion().
       */
      // oactPlanificador.getObjetosActivos().addNew(sirvientePLAN);
    }catch(Exception ex) {
      apropiados=true;
      throw new ADMINGLOExcepcion("No pudieron abrirse los "+
                               "objetos activos para "+
                               "la planificación y el "+
                               "despacho.",ex);
    }
    // el sistema va a detectar si los datos evidencian la presencia de otra
    // instancia de tdderive
  }
  private void _cierraAdministracion()throws ADMINGLOExcepcion{
    if(lector!=null){
      lector.close();
      lector=null;
    }
    if(sirvienteBAL!=null){
      sirvienteBAL.close();
    }
  }
  private void _cierraPersistentes()throws ADMINGLOExcepcion{
    estado_instancia=ADMINGLOServidor.INSTANCIACERRADA;
    try {
      info.close();
    }
    catch (OIExcepcion ex) {
      throw new ADMINGLOExcepcion(ex.getMessage(),ex);
    }
  }
  private void _cierraSubyacentes()throws ADMINGLOExcepcion{
    if(acoDespachador!=null){
      acoDespachador.close();
    }
    // if(bitaBitacora!=null){
    //   bitaBitacora.close();
    // }
    // if(acoReportero!=null){
    //  acoReportero.close();
    // }
    if(oactBalanceador!=null){
      oactBalanceador.close();
    }
    if(oactDespachador!=null){
      oactDespachador.close();
    }
    acoDespachador=null;
    // bitaBitacora=null;
    // acoReportero=null;
    oactBalanceador=null;
    oactDespachador=null;
    this.nodoConfGlobal=null;
  }
  /**
  * Realiza actividades para la finalización de los servicios de tdderive.
  * <li>El tipo de cierre depende del valor de la propiedad "estado_instancia".
  * </li>
  * @throws OIExcepcion Si ocurren errores por falta de un controlador
  * de datos o de JDBC.
  */
  private void _final()throws OIExcepcion{
    boolean errores=false;
/*    if(this.connbd!=null){
      try {
        connbd.close();
      }
      catch (SQLException ex) {
        errores=true;
      }
    }
*/
  }
  /**
   * Carga valores de la computadora desde la configuración local.
   * @param compu Instancia de computadora a cargar.
   */
  private void cargaConfBajoNivel(Computadora compu) {
    compu.setMicroReloj(this.confLocal._relojMHz);
    compu.setBusesTipo(this.confLocal._tipoBuses);
    if(this.confLocal._cantMicrop!=0){
      compu.setMicroCant(this.confLocal._cantMicrop);
    }else{
      compu.setMicroCant(1);
    }
  }
  private class ConfigGlobal extends mens.MENSMensaje{
    String aco_archivo="aco.xml";
    String aco_cons_archivo="aco.cons.xml";
    String admin_global="admin_global.xml";
    String bd_controlador="";
    String bd_url="";
    String bitacora_archivo="bitacoras.xml";
    OIConexion conn;
    String oact_despa_archivo="oact.despa.xml";
    String oact_plani_archivo="oact.plani.xml";
    String password="";
    String raiz_tdderive="/tdderive";
    String script_finbd_url="";
    String script_inibd_url="";
    String usuario="";
    String clase_balance="admin.DIRHidrodinamico";
    ConfigGlobal(){
      conn=new OIConexion();
    }
    /**
     * @see mens.MENSMensaje#isVacio()
     */
    public boolean isVacio() {
      return false;
    }
    /**
     * @see mens.MENSMensaje#getXMLContainedElements()
     */
    protected String getXMLContainedElements() {
      int longitud=0;
      String xml="";
      return xml;
    }
    /**
     * @see mens.MENSMensaje#setContentFromDoc(org.w3c.dom.Node, int[], java.lang.String[])
     */
    protected void setContentFromDoc(Node nodo, int[] problema, String[] mensaje) {
      int longitud=0;
      String valor="";
      nodo=this.getDocumento();
      if(nodo!=null){
        nodo=MENSMensaje.getNextElement(nodo, "nivel_oact_planificador");
        clase_balance="";
        if(nodo!=null){
          valor=MENSMensaje.getElementText(nodo,"sirviente");
          clase_balance=valor;
        }
      }
    }
    /**
     * @see mens.MENSMensaje#toleraXML(int[], java.lang.String[])
     */
    protected void toleraXML(int[] problema, String[] mensaje) {
    }
  }
private class ConfigLocal extends mens.MENSMensaje{
  String _aco_archivo="aco.xml";
  String _aco_cons_archivo="aco.cons.xml";
  String _admin_global="admin_global.xml";
  /**
   * Alias de la computadora que hospeda o de la computadora virtual que
   * hospeda.
   */
  String _alias="localhost";
  String _bd_controlador="";
  String _bd_url="";
  String _bitacora_archivo="bitacoras.xml";
  public int _cantMicrop;
  int _intervalo_s=120;
  String _oact_despa_archivo="oact.despa.xml";
  String _oact_plani_archivo="oact.plani.xml";
  String _password="";
  String _raiz_tdderive="/tdderive";
  int _relojMHz=0;
  String _script_finbd_url="";
  String _script_inibd_url="";
  String _tipoBuses="";
  String _usuario="";
  OIConexion conn;
  ConfigLocal(){
    conn=new OIConexion();
  }
  /**
   * @see mens.MENSMensaje#isVacio()
   */
  public boolean isVacio() {
    return false;
  }
  /**
   * @see mens.MENSMensaje#getXMLContainedElements()
   */
  protected String getXMLContainedElements() {
    int longitud=0;
    String xml="";
    Inflater inflador;
    Deflater desinflador;
    byte[] contrasennna=new byte[100];
    xml+="\n<bd_controlador>"+_bd_controlador+"<bd_controlador>";
    xml+="\n<bd_url>"+_bd_url+"<bd_url>";
    xml+="\n<script_inibd_url>"+_script_inibd_url+"<script_inibd_url>";
    xml+="\n<script_finbd_url>"+_script_finbd_url+"<script_finbd_url>";
    xml="\n<usuario>"+_usuario+"</usuario>";
    xml="\n<raiz_tdderive>"+_raiz_tdderive+"</raiz_tdderive>";
    if(_password!=null&&_password!=""){
      desinflador=new Deflater();
      desinflador.setInput(_password.getBytes());
      desinflador.finish();
      longitud=desinflador.deflate(contrasennna);
      _password=new String(contrasennna,0,longitud);
      xml="\n<password>"+_password+"</password>";
    }
    xml="<tdderive_base>"+xml+"\n</tdderive_base>";
    xml="\n<alias>"+_alias+"<alias>"+xml;
    xml+="\n";
    xml+="<tdderive_bajonivel>\n";
    xml+="<relojMHz>"+_relojMHz+"</relojMHz>\n";
    xml+="<tipoBuses>"+_tipoBuses+"</tipoBuses>";
    xml+="<intervalo_s>"+_intervalo_s+"</intervalo_s>";
    xml+="<cantMicrop>"+_cantMicrop+"</cantMicrop>";
    xml+="\n</tdderive_bajonivel>";
    return xml;
  }
  /**
   * @see mens.MENSMensaje#setContentFromDoc(org.w3c.dom.Node, int[], java.lang.String[])
   */
  protected void setContentFromDoc(Node nodo, int[] problema, String[] mensaje) {
    int longitud=0;
    String valor="";
    byte[] contrasennna=new byte[100];
    Inflater inflador;
    Node nodobak=nodo;
    if(nodo.getNodeName().compareToIgnoreCase("alias_local")!=0){      
      nodo=MENSMensaje.getNextSiblingElement(nodo, "alias_local");
      if(nodo!=null){
        _alias=MENSMensaje.getElementText(nodo, "alias_local");
      }
    }else{
      _alias=MENSMensaje.getElementText(nodo, "alias_local");
    }
    nodo=nodobak;
    if(nodo.getNodeName().compareToIgnoreCase("tdderive_base")!=0){
      nodo=MENSMensaje.getNextSiblingElement(nodo, "tdderive_base");
    }
    if(nodo==null){
      return;
    }
    _bd_controlador=MENSMensaje.getElementText(nodo, "bd_controlador");
    _bd_url=MENSMensaje.getElementText(nodo, "bd_url");
    _script_inibd_url=MENSMensaje.getElementText(nodo, "script_inibd_url");
    _script_finbd_url=MENSMensaje.getElementText(nodo, "script_finbd_url");
    _usuario=MENSMensaje.getElementText(nodo, "usuario");
    _password=MENSMensaje.getElementText(nodo, "password");
    _raiz_tdderive=MENSMensaje.getElementText(nodo, "raiz_tdderive");
    if(_password!=null&&_password!=""){
      inflador=new Inflater();
      inflador.setInput(_password.getBytes());
      inflador.end();
      try {
        inflador.inflate(contrasennna);
        _password=new String(contrasennna);
      } catch (DataFormatException e) {
        e.printStackTrace();
      }
    }
    nodo=nodobak;
    if(nodo.getNodeName().compareToIgnoreCase("tdderive_bajonivel")!=0){
      nodo=MENSMensaje.getNextSiblingElement(nodo, "tdderive_bajonivel");
    }
    if(nodo==null){
      return;
    }
    _relojMHz=Integer.parseInt(MENSMensaje.getElementText(nodo, "relojMHz"));
    _tipoBuses=MENSMensaje.getElementText(nodo, "tipoBuses");
    try{
      _intervalo_s=Integer.parseInt(MENSMensaje.getElementText(nodo, "intervalo_s"));
    }catch(Exception e){
      _intervalo_s=120;
    }
    try{
      _cantMicrop=Integer.parseInt(MENSMensaje.getElementText(nodo, "cantMicrop"));
    }catch(Exception e){
      _cantMicrop=1;
    }
    if (_tipoBuses.compareTo(Computadora.BUSES_PC)!=0){
      if (_tipoBuses.compareTo(Computadora.BUSES_SERVIDOR)!=0){
        _tipoBuses=Computadora.BUSES_PC;
      }
    }
  }
  /**
   * @see mens.MENSMensaje#toleraXML(int[], java.lang.String[])
   */
  protected void toleraXML(int[] problema, String[] mensaje) {
  }
}  
  //////////////////////////////////////////////////////////////////////
}
