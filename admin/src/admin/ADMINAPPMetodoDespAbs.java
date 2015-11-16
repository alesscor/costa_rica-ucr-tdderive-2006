package admin;

import oact.*;
import org.w3c.dom.*;
import java.util.*;
import orgainfo.*;
import aco.*;
import admin.PERSCoordinacion.Sub_trabajos;
import admin.PERSCoordinacion.Tareas;
import mens.*;
/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Recibe a cualquier solicitud y registra en el sistema datos
 * escenciales sobre ésta.<br>
 * Registra una nueva instancia de estas entidades:<br>
 * <li>Solicitantes</li>
 * <li>Tareas*</li>
 * <li>Sol_tar</li>
 * En esta clase debe tenerse mucho cuidado, pues se debe
 * mantener la estructura de la programación a pesar
 * de tener al código dirigido por los eventos siguientes:
 * <pre>
 *       this.setFromXMLSource(...) llama a
 *           this.preCargaSolicitud()
 *       this.setContentFromDoc() llama a
 *           this.setContentFromDoc0()
 *       this.postCargaSolicitud() llama a
 *           this.revisaEstados()
 *       this.getXMLContainedElements() llama a
 *           this.getXMLContainedElements0()
 * </pre>
 * ____________________<br>
 * *Se revisa si hay asociación con una tarea.<br>
 */
public abstract class ADMINAPPMetodoDespAbs extends OACTSolicitud {
  PERSCoordinacion.Solicitantes solicitante;
  PERSCoordinacion.Tareas tarea;
  Tareas tareaV=null;
  Sub_trabajos subtraV=null;

  /// <2005 considerando subtrabajos>
  /// <2006 considerando fin de subtrabajos>
  PERSCoordinacion.Sub_trabajos subtrabajo;
  Map mpArchivos;
  TreeMap mpRetornos;
  String[] ambiente;
  ADMINGLOInfo info;
  boolean _si_local;
  boolean _si_esperar;
  protected int _tamano_archivos;
  protected int _cantidad_archivos;
  protected boolean _si_copiar_a_dir_trabajo;
  final static String SOLICITUD_NOENVIAR="NO_ENVIAR";
  final static String SOLICITUD_ERROR="ERROR";
  final static String SOLICITUD_ENVIAR="ENVIAR";
  final static String SOLICITUD_CERRAR="CERRAR";
  final static String SOLICITUD_SEPARADOR="\n";
  /**
   * Tiempo en que se espera un receive.
   */
  final static int TIEMPOESPERA_LECTURA=30000;
  /**
   * Tiempo en que se espera una conexión (connect).
   */
  final static int TIEMPOESPERA_CONEXION=35000;
  public final static int TAMANO_ARCHIVOS=1024*300; // i.e. 300KB
  public final static int TAMANO_EXPORTAIMPORTA=10*1024; // i.e. 10KB
  public ADMINAPPMetodoDespAbs() {
    super("op_usuario");
    _inicio();
  }
  public ADMINAPPMetodoDespAbs(OACTSirvienteAbs sirviente0) {
    super(sirviente0);
    _inicio();
  }
  public ADMINAPPMetodoDespAbs(String servantID) {
    super(servantID);
    _inicio();
  }
  private void _inicio(){
    info=null;
    _si_local=false;
    _tamano_archivos=TAMANO_ARCHIVOS;
    _cantidad_archivos=0;
    _si_copiar_a_dir_trabajo=false;
    _si_esperar=true;
    // mpArchivos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    mpArchivos = Collections.synchronizedMap(new TreeMap(String.CASE_INSENSITIVE_ORDER));
    mpRetornos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
  }
  void open(ADMINGLOInfo info0)throws OIExcepcion,ADMINGLOExcepcion{
    if(info0==null){
      throw new ADMINGLOExcepcion("Falta información para abrir la solicitud.");
    }
    info=info0;
    //
    // abre un solicitante nuevo
    //
    solicitante=new PERSCoordinacion.Solicitantes(
        info,false);
    //
    // guarda info de este solicitante
    //
    solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_INICIO);
    solicitante.write();
    //
    // todavía no hay información sobre tareas, hay que leer solicitud,
    // se actualiza estado en setContentFromDoc.
    //
    tarea=new PERSCoordinacion.Tareas(info,true);
    tarea.setEstadoTarea(ADMINAPPITareas.TAREA_INICIO);
    tarea.write();
    this.info.getConex().dbCommit();
    // <2005 />
    subtrabajo=null;
  }
  void close()throws OIExcepcion,ADMINGLOExcepcion{
    solicitante.write();
  }
  public boolean isVacio(){
    return false;
  }
  public boolean ejecutarInicio() throws Exception{
    return true;
  }
  public void ejecutarFin(OACTSolicitud futuro,boolean siiniciado) throws Exception{
  }
  public abstract OACTSolicitud ejecutar() throws Exception;
  protected void setContentFromDoc0(Node parm1, int[] parm2, String[] parm3){
  }
  protected void toleraXML0(int[] parm1, String[] parm2){
  }
  protected String getXMLContainedElements0(){
    return "";
  }
  /**
   * Obtiene la representación del objeto en formato XML.
   * <li>Una subclase debe extender el método <tt>getXMLContainedElements0</tt>.</li>
   * @return La representación del objeto en formato XML.
   */
  protected String getXMLContainedElements() {
    String xml="",subxml="";
    Iterator itr;
    PERSCoordinacion.Archivos arch;
    PERSCoordinacion.Retornos ret;
    int i=0;
    xml+="<solicitud>\n";
    xml += "\t<id_tarea>";
    if(tarea.getIdTarea()!=null){
      xml += tarea.getIdTarea();
    }
    xml += "</id_tarea>\n";
    xml+="\t<alias>";
    xml+=tarea.getAlias();
    xml+="</alias>\n";
    xml+="\t<si_local>";
    xml+=_si_local;
    xml+="</si_local>\n";
    // <2005/>
    // <2006/>
    if(this.siSubtrabajo()){
      xml+="\t<id_subtrabajo>";
      xml+=subtrabajo.getIdSubtrabajo();
      xml+="\t</id_subtrabajo>";
      xml+="\t<carga>";
      xml+=""+subtrabajo.getCarga()+"";
      xml+="\t</carga>";
      xml+="\t<si_fin>";
      xml+=""+subtrabajo.getSiFin()+"";
      xml+="\t</si_fin>";
      xml+="\t<parametros>";
      xml+=subtrabajo.getComando();      
      xml+="</parametros>\n";
      //
      // 2006 v
      if(this.siSigueVivo()){
        xml+="\t<contremoto>";
        xml+=subtrabajo.getContRemoto();
        xml+="</contremoto>\n";
      }
      //
      //
    }else{
      xml+="\t<parametros>";
      xml+=tarea.getParametros();
      xml+="</parametros>\n";
    }    
    xml+="\t<ambiente>\n";
    subxml="";
    if(ambiente!=null){
      i=0;
      while (i < ambiente.length) {
        subxml += ("\t\t<ambiente" + (i + 1) + ">");
        subxml += ambiente[i].trim();
        subxml += ("</ambiente" + (i + 1) + ">\n");
        i++;
      }
      xml+=subxml;
      subxml="";
    }
    xml+="\t</ambiente>\n";
    subxml="";
    xml+="\t<archivos>\n";
    if(mpArchivos!=null){
      i=0;
      itr=mpArchivos.values().iterator();
      while (itr.hasNext()) {
        arch=(PERSCoordinacion.Archivos)itr.next();
        subxml += ("<archivo" + (i + 1) + ">");
        subxml += ("<nombre>");
        subxml += arch.getNombre();
        subxml += ("</nombre>");
        subxml += ("<info_archivo>");
        subxml += arch.getInfoArchivo();
        subxml += ("</info_archivo>");

        subxml += ("<ruta_original>");
         subxml += arch.getRutaOriginal();
        subxml += ("</ruta_original>");
        if(arch.getBloque()!=""){
          subxml += ("<bloque>");
          subxml += arch.getBloque();
          subxml += ("</bloque>");
        }
        if(arch.getSiLocal()){
          subxml += ("<si_local>");
           subxml += "true";
          subxml += ("</si_local>");
        }
        subxml += ("</archivo" + (i + 1) + ">\n");
        i++;
      }
      xml += subxml;
      subxml="";
    }
    xml+="\t</archivos>\n";
    // carga retornos
    subxml="";
    xml+="\t<retornos>\n";
    if(mpRetornos!=null){
      i=0;
      itr=mpRetornos.values().iterator();
      while (itr.hasNext()) {
        ret=(PERSCoordinacion.Retornos)itr.next();
        subxml += ("<retorno" + (i + 1) + ">");
        subxml += ("<tipo_retorno>");
        subxml += ret.getTipoRetorno();
        subxml += ("</tipo_retorno>");
        subxml += ("<valor_retorno>");
        subxml += ret.getValorRetorno();
        subxml += ("</valor_retorno>");
        subxml += ("<estado_retorno>");
        subxml += ret.getEstadoRetorno();
        subxml += ("</estado_retorno>");
        subxml += ("</retorno" + (i + 1) + ">\n");
        i++;
      }
      xml += subxml;
      subxml="";
    }
    xml+="\t</retornos>\n";
    
    xml += "\t<directorio>";
//    if(tarea.getDirectorio()!=null){
//      xml += tarea.getDirectorio();
//    }
    xml +="(null)";
    xml += "</directorio>\n";
    xml+="\t<si_esperar>";
    if(_si_esperar){
      xml+="true";
    }else{
      xml+="false";
    }
    xml+="</si_esperar>\n";
    xml+=getXMLContainedElements0();
    xml+="</solicitud>";
    return xml;
  }
  public boolean isPreparado() {
    boolean resultado=false;
    resultado=ADMINAPPISolicitantes.SOLICITANTE_LISTO.compareTo(
        solicitante.getEstadoSolicitante())!=0;
    resultado=resultado&&(info!=null);
    return resultado;
  }
  /**
   * Carga el objeto a partir de un arreglo de caracteres
   * en formato XML.
   * <li>Una subclase debe extender el método <tt>setContentFromDoc0</tt>.</li>
   * @param nodo Nodo del cual leer la fuente de caracteres.
   * @param parm2 Número de error a devolver.
   * @param parm3 Mensaje de error a devolver.
   */
  protected void setContentFromDoc(Node nodo, int[] parm2, String[] parm3) {
    TreeMap lista=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    Iterator itr;
    Node bookmark=nodo;
    String texto="";
    PERSCoordinacion.Archivos archivo=null;
    PERSCoordinacion.Retornos retorno=null;
    int i=0;
    if(info==null&&this.getSirviente()!=null){
      try {
        this.open( ( (ADMINAPPDespachador)this.getSirviente()).info);
      }
      catch (ADMINGLOExcepcion ex) {
        return;
      }
      catch (OIExcepcion ex) {
        return;
      }
    }
    if(nodo.getNodeName().compareToIgnoreCase("solicitud")!=0){
      nodo=this.getDocumento();    
      nodo=MENSMensaje.getNextElement(nodo, "solicitud");
    }
    if(nodo==null){
      return;
    }
    bookmark=nodo;
    tarea.setIdTarea(getElementText(nodo,"id_tarea"));
    tarea.setAlias(getElementText(nodo,"alias"));
    tarea.setParametros(getElementText(nodo,"parametros"));
    // tarea.setDirectorio(getElementText(nodo,"directorio"));
    
    // <2005 />
    texto=getElementText(nodo,"id_subtrabajo").trim();
    if(texto!=""){
      subtrabajo=new PERSCoordinacion.Sub_trabajos(texto,tarea);
      // ((PERSCoordinacion.Sub_trabajos)subtrabajo).setIdSubtrabajo(texto);
      texto=getElementText(nodo,"carga").trim();
      try {
        subtrabajo.setCarga(Double.parseDouble(texto));
      } catch (NumberFormatException e) {
        subtrabajo.setCarga(0.0);
      }
      texto=getElementText(nodo,"si_fin").trim();
      if(texto!="" && texto.compareToIgnoreCase("true")==0){
        subtrabajo.setSiFin(true);
      }else{
        subtrabajo.setSiFin(false);
      }
      subtrabajo.setComando(tarea.getParametros());
      tarea.setParametros("");
      //
      // 2006 v
      if(subtrabajo.siLocal() && !subtrabajo.getSiFin()){
        // no actualiza en vano la hora de actualización del
        // reporte del nodo
        texto=getElementText(nodo,"contremoto").trim();
        if(texto!=""){
          subtrabajo.setContRemoto(texto);
          this.siSigueVivo();
        }
      }
      //
      //
    }
    texto=getElementText(nodo,"si_local");
    if((texto.compareToIgnoreCase("true")==0)||
       (texto.compareToIgnoreCase("verdadero")==0)){
      _si_local=true;
    }else{
      _si_local=false;
    }
    texto=getElementText(nodo,"si_esperar");
    if((texto.compareToIgnoreCase("true")==0)||
       (texto.compareToIgnoreCase("verdadero")==0)){
      _si_esperar=true;
    }else{
      _si_esperar=false;
    }
    // carga archivos
    if(nodo.getNodeName().compareToIgnoreCase("archivos")!=0){
      nodo = MENSMensaje.getNextElement(nodo, "archivos");
    }
    if(nodo!=null){
      i=0;
      lista.clear();
      nodo = MENSMensaje.getNextElement(nodo, "archivo1");
      while((nodo!=null)&&(nodo.getNodeName().
                           compareToIgnoreCase("archivo"+(i+1))==0)){
        archivo=new PERSCoordinacion.Archivos(this.info);
        // debe haber info sobre parámetros
        // loadFromMensajes(nodo,"archivo"+(i+1),archivo);
        archivo.setNombre(getElementText(nodo, "nombre"));
        archivo.setInfoArchivo(getElementText(nodo, "info_archivo"));
        archivo.setBloque(getElementText(nodo, "bloque"));
        archivo.setSiLocal(Boolean.valueOf(
          getElementText(nodo, "si_local")).booleanValue());      
        archivo.setRutaOriginal(getElementText(nodo, "ruta_original"));      
        if(subtrabajo!=null && subtrabajo.getSiFin()){
          archivo.setSiEntrada(false);
        }
        
        lista.put(archivo.getNombre()/*+":"+archivo.getBloque()*/,archivo);
        i++;
        nodo = MENSMensaje.getNextSiblingElement(nodo, "archivo"+(i+1));
      }
      //
      // pone los valores del archivo
      //
      mpArchivos.putAll(lista);
    }
    nodo=bookmark;
    // carga enunciados de retorno
    if(nodo.getNodeName().compareToIgnoreCase("retornos")!=0){
      nodo = MENSMensaje.getNextElement(nodo, "retornos");
    }
    if(nodo!=null){
      i=0;
      lista.clear();
      nodo = MENSMensaje.getNextElement(nodo, "retorno1");
      while((nodo!=null)&&(nodo.getNodeName().
                           compareToIgnoreCase("retorno"+(i+1))==0)){
        retorno=new PERSCoordinacion.Retornos(this.info);
        retorno.setTipoRetorno(getElementText(nodo, "tipo_retorno"));
        retorno.setValorRetorno(getElementText(nodo, "valor_retorno"));
        retorno.setEstadoRetorno(getElementText(nodo, "estado_retorno"));
        
        lista.put(retorno.getValorRetorno(),retorno);
        i++;
        nodo = MENSMensaje.getNextSiblingElement(nodo, "retorno"+(i+1));
      }
      //
      // pone los valores del retorno
      //
      mpRetornos.putAll(lista);
    }    
    setContentFromDoc0(bookmark,parm2,parm3);
  }
  /**
   * Toma decisiones y cambia estados luego de haber leído la solicitud.
   */
  final protected void postCargaSolicitud_Pasar_a_ejecutaInicio()throws OIExcepcion{
    /**
     * pone estados al solicitante y a la tarea.
     */
    try {
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_INICIO);
      if (tarea.getIdTarea() == "") {
        /**
         * Tarea nueva
         */
        solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_PREPTAREA);
        solicitante.write();
        solicitante.setTarea(tarea);
        // this.importaArchivos();
      }
      else {
        /**
         * Tarea vieja, interesa conocer la tarea ya está lista.
         */
        if (true) {
          /**
           * La tarea y sus archivos ya están en el sistema.
           */
          solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_YATAREA);
          tarea.write();
        }
        else {
          /**
           *  ---->       c a s o   p e n d i e n t e       <----
           */
          /**
           * La tarea no está completa en el sistema, ni sus archivos
           */
          solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_PREPTAREA);
          // this.importaArchivos();
        }
        solicitante.write();
        solicitante.setTarea(tarea);
      }
      /**            |
       *             |
       *             V
       * realiza chequeos de instancias concretas de LEESolicitudAbs
       */
      revisaEstados();
      /**
       * el grupo de la solicitud debe ser el mismo código de la tarea.
       */
      solicitante.setIdGrupo(tarea.getIdTarea());
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_LISTO);
      solicitante.write();
      tarea.write();
      info.getConex().dbCommit();
    }catch (Exception ex1) {
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_INVALIDO);
    }

  }
  /**
   * Toma la lista de archivos con el contenido de cada uno y lo
   * escribe en el sistema.
   * <>Indica un cambio en el estado que corresponde a cada archivo
   * respaldado.</li>
   * @throws ADMINExcepcion Si hay errores.
   */
  private void zrespaldaArchivos_BORRAR() throws ADMINGLOExcepcion,ACONExcArbitraria, ACONExcOmision,
      ACONExcTemporizacion{
    PERSCoordinacion.Archivos archivoI;
    Iterator itr;
    itr=mpArchivos.values().iterator();
    try {
      while(itr.hasNext()){
        archivoI=(PERSCoordinacion.Archivos)itr.next();
        // se trae al archivoI
          archivoI.setContenido(this.receive().getBytes());
      }
    }catch (OACTExcepcion ex) {
      throw new ADMINGLOExcepcion("Error en importación de archivos.",ex);
    }
    itr=null;
  }
  /**
   * Agrega un archivo a la solicitud.
   * <li>Internamente se parte el archivo en varios subarchivos
   * comprimidos a dejar en el directorio comprimido.</li>
   * @param archivo0 Archivo a agregar.
   * @throws ADMINExcepcion
   */
  public void addArchivo(String archivo0) throws ADMINGLOExcepcion{
    mpArchivos.put(archivo0,archivo0);
  }

  /**
   * Da la oportunidad de revisar el estado de la solicitud y de actualizarlo.
   * <li>Realiza chequeos de instancias concretas
   * de <tt>LEESolicitudAbs</tt>.</li>
   */
  protected abstract void revisaEstados();
  protected void toleraXML(int[] parm1, String[] parm2) {
    /**@todo Implement this mens.MENSMensaje abstract method*/
  }
  private void loadFromMensajes(Node nodo,String elemento,PERSCoordinacion.Archivos arch){
    Node backup=nodo;
    try{
      arch.setNombre(getElementText(nodo, "nombre"));
      arch.setBloque(getElementText(nodo, "bloque"));
      arch.setSiLocal(Boolean.valueOf(getElementText(nodo, "si_local")).booleanValue());      
      arch.setRutaOriginal(getElementText(nodo, "ruta_original"));      
//      arch.setNombre(getElementText(nodo, elemento).trim());
//      nodo = getNextElement(nodo, "bloque");
//      if(nodo!=null){
//        arch.setBloque(getElementText(nodo, "bloque").trim());
//      }
//      nodo=backup;
//      nodo = getNextElement(nodo, "si_local");
//      if(nodo!=null){
//        arch.setSiLocal(Boolean.valueOf(getElementText(nodo, "si_local").trim()).booleanValue());
//      }
//      nodo=backup;
//      nodo = getNextElement(nodo, "ruta_original");
//      if(nodo!=null){
//        arch.setRutaOriginal(getElementText(nodo, "ruta_original").trim());
//      }
    }catch(Exception ex){
    }
 }
  protected boolean getSiLocal(){
    return _si_local;
  }
  protected void setSiLocal(boolean setSiLocal){
    _si_local=setSiLocal;
  }
  public long getTamanoArchivos(){
    return _tamano_archivos;
  }
  public void setTamanoArchivos(int setTamanoArchivos){
    _tamano_archivos=setTamanoArchivos;
  }
  public int getCantidadArchivos(){
    return _cantidad_archivos;
  }
  public void setCantidadArchivos(int setCantidadArchivos){
    _cantidad_archivos=setCantidadArchivos;
  }
  public boolean getCopiarEnDir(){
    return _si_copiar_a_dir_trabajo;
  }
  public void setCopiarEnDir(boolean setCopiarEnDir){
    _si_copiar_a_dir_trabajo=setCopiarEnDir;
  }
  final public boolean siSubtrabajo(){
    return subtrabajo != null;
  }
  final public boolean siSubtraSolicitud(){
    return siSubtrabajo() && !this.siSigueVivo() && !this.siSubtrabajoFin(); 
  }
  final public boolean siSubtrabajoFin(){
    boolean res=false;
    if(siSubtrabajo()){
      res=subtrabajo.getSiFin();
    }else{
      res=false;
    }
    return res;
  }
  /**
   * Determina para los demás, si un subtrabajo sigue vivo.
   * Un subtrabajo está vivo cuando un controlador que lo está
   * procesando se lo indica a los nodos que les concierne (bueno
   * por ahora solamente le interesa al nodo coordinador del 
   * subtrabajo).
   * @return Si el subtrabajo está vivo.
   */
  final public boolean siSigueVivo(){
    boolean res=false;
    if(siSubtrabajo()){
      res=subtrabajo.getContRemoto()!="";
      res=res && subtrabajo.getEstadoSubtrabajo().compareTo(ADMINAPPISub_trabajos.SUBTRA_MARCHA)==0;
    }
    return res;
  }
}