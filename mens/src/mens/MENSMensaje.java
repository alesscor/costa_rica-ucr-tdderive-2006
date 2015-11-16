package mens;
import org.w3c.dom.*;
import org.apache.xerces.parsers.DOMParser;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import tdutils.tdutils;
/**
 * Clase abstracta para clases de mensajes derivadas concretas que se
 * construyen a partir de fuentes de datos dados en formato XML.<br>
 * Las clases concretas derivadas deben implementar los métodos siguientes:<br>
 * <ul>
 * <code>isVacio() - </code>Indica si el mensaje interpretado está vacío.<br>
 * </ul>
 * <ul>
 * <code>getXMLContainedElements() - </code>Obtiene los elementos del mensaje
 * interpretado en una hilera de caracteres con formato XML.<br>
 * </ul>
 * <ul>
 * <code>setContentFromDoc() - </code>Asigna los valores a los campos del
 * objeto a partir de un documento W3C.DOM. Complementa la formación del objeto
 * a partir de la fuente XML.<br>
 * </ul>
 * <ul>
 * <code>toleraXML() - </code>Indica si se tolera o no la falta de información
 * en los campos del objeto, o sus valores respectivos.<br>
 * </ul>
 */

public abstract class MENSMensaje implements MENSMensajeI  {
  static public int VENCIMIENTO_NOESPERA=-1;
  static public int VENCIMIENTO_INFINITO=0;
  private MENSIComandos comando=null;
//  /**
//   * El <code>IP</code> o el <code>hostname</code>.
//   */
//  private String nodo;
//  /**
//   * Alias dentro de TDderive.
//   */
//  private String alias;
//  /**
//   * Indice único dentro de TDderive.
//   */
//  private int indice;
//  /**
//   * Información para verificar un trabajo en general.
//   */
//  private String verificaGeneral;
//  /**
//   * Tipo de mensaje.
//   */
//  private char tipoMensaje;
//  /**
//   * Indica el tipo de vencimiento del mensaje.<br>
//   * <code>-1</code> El mensaje no se espera.
//   * <code> 0</code> El mensaje se espera en forma infinita.
//   * <code> n>0</code> El mensaje se espera en n milisegundos.
//   */
//  private int vencimiento;
  /**
   * Contiene la fuente XML del mensaje.
   */
  private String fuenteXML;
  /**
   * Documento a ser parseado.
   */
  private Document doc;
  /**
   * 
   */
  private String nombreNodoPrincipal;
  private boolean _isset_fuenteXML;
  
  
  /**
   * Obtiene el contenido del objeto XML.
   */
  public final String getFuenteXML(){
    return fuenteXML;
  }
  /**
   * Obtiene el documento que contiene el objeto
   * XML.
   * @return El documento contenedor.
   */
  public final Document getDocumento(){
    return doc;
  }
  // asignaciones de los principales campos v
//  public final void setAlias(String alias0) {
//    if(alias0=="")return;
//    alias=alias0;
//    _isset_alias=true;
//  }
//  public final void setIndice(int indice0) {
//    indice=indice0;
//    _isset_indice=true;
//  }
//  public final void setNodo(String nodo0) {
//    if(nodo0=="")return;
//    nodo=nodo0;
//    _isset_nodo=true;
//  }
//  public final void setTipoMensaje(char tipoMensaje0) {
//    tipoMensaje=tipoMensaje0;
//    _isset_tipoMensaje=true;
//  }
//  public final void setVerificaGeneral(String verificaGeneral0) {
//    if(verificaGeneral0=="")return;
//    verificaGeneral=verificaGeneral0;
//    _isset_verificaGeneral=true;
//  }
//  public final void setVencimiento(int vencimiento0){
//    vencimiento=vencimiento0;
//    _isset_vencimiento=true;
//  }
  /**
   * Asigna el contenido XML del objeto XML.
   */
  public final void setFuenteXML(String fuente){
    if(fuente!=""){
      fuenteXML = fuente;
      _isset_fuenteXML=true;
    }
  }
  // asignaciones de los principales campos ^
  public String getInfoVerificacion(String infoVerificacion) {
    /**@todo Implement this prototdderive.MENSMensajeI method*/
    throw new java.lang.UnsupportedOperationException("Method getInfoVerificacion() not yet implemented.");
  }
  public String getNombreObjetoEmisor() {
    /**@todo Implement this prototdderive.MENSMensajeI method*/
    throw new java.lang.UnsupportedOperationException("Method getNombreObjetoEmisor() not yet implemented.");
  }
  public String[] getMensaje() {
    /**@todo Implement this prototdderive.MENSMensajeI method*/
    throw new java.lang.UnsupportedOperationException("Method getMensaje() not yet implemented.");
  }
  public final void setInfoVerificacion(String infoVerificacion) {
    /**@todo Implement this prototdderive.MENSMensajeI method*/
    throw new java.lang.UnsupportedOperationException("Method setInfoVerificacion() not yet implemented.");
  }
  public final void setMensaje(String[] mensaje) {
    /**@todo Implement this prototdderive.MENSMensajeI method*/
    throw new java.lang.UnsupportedOperationException("Method setMensaje() not yet implemented.");
  }
  public MENSMensaje() {
//    _isset_alias=false;
//    _isset_indice=false;
//    _isset_nodo=false;
//    _isset_tipoMensaje=false;
//    _isset_verificaGeneral=false;
//    _isset_vencimiento=false;
    _isset_fuenteXML=false;
  }
  /**
   * Asigna una instancia al campo de un objeto.
   * @param fieldName Nombre del campo.
   * @param fieldValue Nueva instancia para el campo.
   * @throws MENSException Error si no se encuentra el campo.
   */
  final void setField(String fieldName, Object fieldValue)throws MENSException {
    Class thisclass=null;
    boolean encontrado=false;
    java.lang.reflect.Field[] thesefields=null;
    int i=0;
    thisclass=this.getClass();
    thesefields=thisclass.getFields();
    for(i=0;i<thesefields.length;i++){
      if(fieldName.equalsIgnoreCase(thesefields[i].getName())){
        try{
          if(thesefields[i].isAccessible()){
            thesefields[i].set(this, fieldValue);
          }else{
            thesefields[i].setAccessible(true);
            thesefields[i].set(this, fieldValue);
          }
        }catch(Exception except){
          except.printStackTrace();
        }
        encontrado=true;
        break;
      }
    }
    if(!encontrado){
      throw new MENSException("El campo dado no se ha encontrado");
    }
  }
  final void setField(String fieldName, boolean fieldValue)throws MENSException {
    Class thisclass=null;
    boolean encontrado=false;
    java.lang.reflect.Field[] thesefields=null;
    int i=0;
    thisclass=this.getClass();
    thesefields=thisclass.getFields();
    for(i=0;i<thesefields.length;i++){
      if(fieldName.equalsIgnoreCase(thesefields[i].getName())){
        try{
          if(thesefields[i].isAccessible()){
            thesefields[i].setBoolean(this, fieldValue);
          }else{
            thesefields[i].setAccessible(true);
            thesefields[i].setBoolean(this, fieldValue);
          }
        }catch(Exception except){
          except.printStackTrace();
        }
        encontrado=true;
        break;
      }
    }
    if(!encontrado){
      throw new MENSException("El campo dado no se ha encontrado");
    }
  }
  final void setField(String fieldName, byte fieldValue)throws MENSException {
    Class thisclass=null;
    boolean encontrado=false;
    java.lang.reflect.Field[] thesefields=null;
    int i=0;
    thisclass=this.getClass();
    thesefields=thisclass.getFields();
    for(i=0;i<thesefields.length;i++){
      if(fieldName.equalsIgnoreCase(thesefields[i].getName())){
        try{
          if(thesefields[i].isAccessible()){
            thesefields[i].setByte(this, fieldValue);
          }else{
            thesefields[i].setAccessible(true);
            thesefields[i].setByte(this, fieldValue);
          }
        }catch(Exception except){
          except.printStackTrace();
        }
        encontrado=true;
        break;
      }
    }
    if(!encontrado){
      throw new MENSException("El campo dado no se ha encontrado");
    }
  }
  final void setField(String fieldName, char fieldValue)throws MENSException {
    Class thisclass=null;
    boolean encontrado=false;
    java.lang.reflect.Field[] thesefields=null;
    int i=0;
    thisclass=this.getClass();
    thesefields=thisclass.getFields();
    for(i=0;i<thesefields.length;i++){
      if(fieldName.equalsIgnoreCase(thesefields[i].getName())){
        try{
          if(thesefields[i].isAccessible()){
            thesefields[i].setChar(this, fieldValue);
          }else{
            thesefields[i].setAccessible(true);
            thesefields[i].setChar(this, fieldValue);
          }
        }catch(Exception except){
          except.printStackTrace();
        }
        encontrado=true;
        break;
      }
    }
    if(!encontrado){
      throw new MENSException("El campo dado no se ha encontrado");
    }
  }
  final void setField(String fieldName, double fieldValue)throws MENSException {
    Class thisclass=null;
    boolean encontrado=false;
    java.lang.reflect.Field[] thesefields=null;
    int i=0;
    thisclass=this.getClass();
    thesefields=thisclass.getFields();
    for(i=0;i<thesefields.length;i++){
      if(fieldName.equalsIgnoreCase(thesefields[i].getName())){
        try{
          if(thesefields[i].isAccessible()){
            thesefields[i].setDouble(this, fieldValue);
          }else{
            thesefields[i].setAccessible(true);
            thesefields[i].setDouble(this, fieldValue);
          }
        }catch(Exception except){
          except.printStackTrace();
        }
        encontrado=true;
        break;
      }
    }
    if(!encontrado){
      throw new MENSException("El campo dado no se ha encontrado");
    }
  }
  final void setField(String fieldName, float fieldValue)throws MENSException {
    Class thisclass=null;
    boolean encontrado=false;
    java.lang.reflect.Field[] thesefields=null;
    int i=0;
    thisclass=this.getClass();
    thesefields=thisclass.getFields();
    for(i=0;i<thesefields.length;i++){
      if(fieldName.equalsIgnoreCase(thesefields[i].getName())){
        try{
          if(thesefields[i].isAccessible()){
            thesefields[i].setFloat(this, fieldValue);
          }else{
            thesefields[i].setAccessible(true);
            thesefields[i].setFloat(this, fieldValue);
          }
        }catch(Exception except){
          except.printStackTrace();
        }
        encontrado=true;
        break;
      }
    }
    if(!encontrado){
      throw new MENSException("El campo dado no se ha encontrado");
    }
  }
  final void setField(String fieldName, int fieldValue)throws MENSException {
    Class thisclass=null;
    boolean encontrado=false;
    java.lang.reflect.Field[] thesefields=null;
    int i=0;
    thisclass=this.getClass();
    thesefields=thisclass.getFields();
    for(i=0;i<thesefields.length;i++){
      if(fieldName.equalsIgnoreCase(thesefields[i].getName())){
        try{
          if(thesefields[i].isAccessible()){
            thesefields[i].setInt(this, fieldValue);
          }else{
            thesefields[i].setAccessible(true);
            thesefields[i].setInt(this, fieldValue);
          }
        }catch(Exception except){
          except.printStackTrace();
        }
        encontrado=true;
        break;
      }
    }
    if(!encontrado){
      throw new MENSException("El campo dado no se ha encontrado");
    }
  }
  final void setField(String fieldName, long fieldValue)throws MENSException {
    Class thisclass=null;
    boolean encontrado=false;
    java.lang.reflect.Field[] thesefields=null;
    int i=0;
    thisclass=this.getClass();
    thesefields=thisclass.getFields();
    for(i=0;i<thesefields.length;i++){
      if(fieldName.equalsIgnoreCase(thesefields[i].getName())){
        try{
          if(thesefields[i].isAccessible()){
            thesefields[i].setLong(this, fieldValue);
          }else{
            thesefields[i].setAccessible(true);
            thesefields[i].setLong(this, fieldValue);
          }
        }catch(Exception except){
          except.printStackTrace();
        }
        encontrado=true;
        break;
      }
    }
    if(!encontrado){
      throw new MENSException("El campo dado no se ha encontrado");
    }
  }
  final void setField(String fieldName, short fieldValue)throws MENSException {
    Class thisclass=null;
    boolean encontrado=false;
    java.lang.reflect.Field[] thesefields=null;
    int i=0;
    thisclass=this.getClass();
    thesefields=thisclass.getFields();
    for(i=0;i<thesefields.length;i++){
      if(fieldName.equalsIgnoreCase(thesefields[i].getName())){
        try{
          if(thesefields[i].isAccessible()){
            thesefields[i].setShort(this, fieldValue);
          }else{
            thesefields[i].setAccessible(true);
            thesefields[i].setShort(this, fieldValue);
          }
        }catch(Exception except){
          except.printStackTrace();
        }
        encontrado=true;
        break;
      }
    }
    if(!encontrado){
      throw new MENSException("El campo dado no se ha encontrado");
    }
  }
  /**
   * Obtiene el valor de un campo de la clase <code>this</code> o de una de sus
   * subclases.
   * @param fieldName El nombre del campo a obtener.
   * @return El valor retornado como String.
   */
  String getField(String fieldName) {
    String value="";
    Class thisclass=null;
    java.lang.reflect.Field[] thesefields=null;
    int i=0;
    thisclass=this.getClass();
    thesefields=thisclass.getFields();
    for(i=0;i<thesefields.length;i++){
      if(fieldName.equalsIgnoreCase(thesefields[i].getName())){
        try{
          if(thesefields[i].isAccessible()){
            value = thesefields[i].get(this).toString();
          }else{
            thesefields[i].setAccessible(true);
            value = thesefields[i].get(this).toString();
            thesefields[i].setAccessible(false);
          }
        }catch(Exception except){
          except.printStackTrace();
        }
      }
    }
    return value;
  }
  //////////////////////////////////////////////////////////////////////
  /**
   * Método abstracto que indica si el objeto mensaje concreto está vacío.
   * @return Verdadero para indicar que sí está vacío.
   */
  public abstract boolean isVacio();
  /**
   * Obtiene el elemento raíz de la fuente XML a partir de la cual se forma
   * el objeto, encabezándolo con un elemento llamado "mensaje".
   * @return El elemento raíz.
   */
  public final String getXMLRootElem(){
    return getXMLRootElem("mensaje");
  }
  /**
   * Obtiene el objeto XML y le pone como primer elemento uno
   * con el nombre dado, incluyendo el encabezado XML.
   */
  public final String getXMLRootElem(String nombre){
    String res="";
    res="<?xml version=\"1.0\"?>\n";
    res+=getXMLElem(nombre);
    return res;
  }
  /**
   * Obtiene el objeto XML y le pone como primer elemento, si es
   * distinto de nulo, el nombre dado.
   * @param nombre Nombre del elemento contenedor del objeto XML.
   * @return El objeto XML.
   */
  public final String getXMLElem(String nombre){
    String res="";
    if(nombre!=null){
      res += "<" + nombre + ">\n";
    }
//    if(_isset_nodo || _isset_alias || _isset_indice || _isset_verificaGeneral || _isset_tipoMensaje || _isset_vencimiento){
//      res += "  <nucleo>\n";
//    }
//    if(_isset_nodo)res+=
//    "    <nodo>"+nodo+"</nodo>\n";
//    if(_isset_alias)res+=
//    "    <alias>"+alias+"</alias>\n";
//    if(_isset_indice)res+=
//    "    <indice>"+indice+"</indice>\n";
//    if(_isset_tipoMensaje)res+=
//    "    <tipoMensaje>"+tipoMensaje+"</tipoMensaje>\n";
//    if(_isset_vencimiento)res+=
//    "    <vencimiento>"+vencimiento+"</vencimiento>\n";
//    if(_isset_verificaGeneral)res+=
//    "    <verificaGeneral>"+verificaGeneral+"</verificaGeneral>\n";
//    if(_isset_nodo || _isset_alias || _isset_indice || _isset_verificaGeneral || _isset_tipoMensaje || _isset_vencimiento){
//      res += "  </nucleo>\n";
//    }
    res+=getXMLContainedElements();
    if(nombre!=null){
      res += "\n</" + nombre + ">";
    }
    return res;
  }
  /**
   * Método abstracto para obtener la representación XML de una porción
   * concreta de un objeto XML.
   * @return La porción concreta de un objeto XML.
   */
  protected abstract String getXMLContainedElements();
  /**
   * Navega por un XML en el nodo indicado para obtener valores.
   * @param node Nodo a partir del cual se puede realizar la navegación.
   * @throws MENSException Si hay error.
   */
  public final void setFromXMLNode(Node node)throws MENSException{
    int problema[]=new int[1];
    int problemaExterno[]=new int[1];
    String mensaje[]=new String[1];
    String mensajeExterno[]=new String[1];
    mensaje[0]="";
    problema[0]=0;
    preXMLLoading();
    try{
      if(node.getNodeType()==Node.ELEMENT_NODE){
        // el nodo ya está listo
      }else{
        node=getNextElement(node);
      }
    }catch(Exception except){
      problema[0]=1;
    }
    if(node==null){
      problema[0]=1;
    }
    if(problema[0]!=0){
      mensaje[0]="Problema al obtener el primer elemento del documento.";
    }
    if(problema[0]==0){
//      setAlias(getElementText(node,"alias").trim());
//      if(getAlias()!=""){
//      }else{
//        problema[0]=2;
//      }
//      try{
//        setIndice(Integer.parseInt(getElementText(node, "indice")));
//      }catch(Exception except){
//        indice=-1;
//      }
//      if(getIndice()!=-1){
//      }else{
//        problema[0]=2;
//      }
//      setNodo(getElementText(node,"nodo").trim());
//      if(getNodo()!=""){
//      }else{
//        problema[0]=2;
//      }
//      try{
//        setTipoMensaje(getElementText(node, "tipoMensaje").trim().charAt(0));
//      }catch(Exception except){
//        tipoMensaje='\0';
//      }
//      if(getTipoMensaje()!='\0'){
//      }else{
//        problema[0]=2;
//      }
//      try{
//        setVencimiento(Integer.parseInt(getElementText(node, "vencimiento")));
//      }catch(Exception except){
//        vencimiento=-2;
//      }
//      if(getVencimiento()!=-2){
//      }else{
//        problema[0]=2;
//      }
//      setVerificaGeneral(getElementText(node,"verificaGeneral").trim());
//      if(getVerificaGeneral()!=""){
//      }else{
//        problema[0]=2;
//      }
//      setContentFromDoc(getNextSiblingElement(node),problemaExterno,mensajeExterno);
      // cambié el de arriba por el de abajo al poner los comentarios de más arriba
      // en el intento de generalización de personalidad de este objeto.
      setContentFromDoc(node,problemaExterno,mensajeExterno);
      if(problemaExterno[0]>0){
        // hay problema externo
        problema[0]=3;
        mensaje[0]+="\n"+mensajeExterno[0];
      }
      toleraXML(problema,mensaje);
    }
    if (problema[0]!=0){
      switch(problema[0]){
        case 1:
          throw new MENSException("problemas al abrir URI\n"+mensaje[0]);
        case 2:
          throw new MENSException("problemas al obtener campo\n"+mensaje[0]);
        default:
          throw new MENSException(mensaje[0]);
      }
    }
    postXMLLoading();
  }
  /**
   * Para llevar acciones antes de construir al objeto a partir de una
   * fuente con formato XML.
   */
  protected void preXMLLoading(){

  }
  /**
   * Para llevar acciones luego de construir al objeto a partir de una
   * fuente con formato XML.
   */
  protected void postXMLLoading(){

  }

  /**
   * Carga la parte básica de una clase <code>MENSMensaje</code> a partir de una
   * fuente de XML dada.<br>
   * - Es complementado por el método abstracto <code>setContentFromDoc</code>
   * para asignar adecuadamente a los nuevos campos.
   * @param source Fuente de XML en una hilera de caracteres.
   * @throws MENSException Excepción levantada cuando no se encuentra
   * el valor de un campo considerado obligatorio de asignar a la clase. Ésta
   * también es levantada cuando hay problemas de acceso a la fuente XML.
   *
   */
  public final void setFromXMLSource(String source)throws MENSException{
    this.setFuenteXML(source);
    _setFromXMLSource(new ByteArrayInputStream(source.getBytes()));
  }
  /**
   * Carga la parte básica de una clase <code>MENSMensaje</code> a partir de una
   * fuente de XML dada.<br>
   * - Es complementado por el método abstracto <code>setContentFromDoc</code>
   * para asignar adecuadamente valores a los nuevos campos.
   * @param source Fuente de XML.
   * @throws MENSException Excepción levantada cuando no se encuentra
   * el valor de un campo considerado obligatorio de asignar a la clase. Ésta
   * también es levantada cuando hay problemas de acceso a la fuente XML.
   */
  public final void setFromXMLSource(InputStream source)throws MENSException{
    String strsource="";
    byte[] buff;
    try{
      if (source.available() > 0) {
        buff = new byte[source.available()];
        source.read(buff);
        strsource = tdutils.getString(buff);
        this.setFuenteXML(strsource);
      }
    }catch(Exception except){
      except.printStackTrace();
    }
    _setFromXMLSource(new ByteArrayInputStream(strsource.getBytes()));
  }
  private final void _setFromXMLSource(InputStream source)throws MENSException{
    setFromXML0(new org.xml.sax.InputSource(source));
  }
  /**
   * Carga la parte básica de una clase <code>MENSMensaje</code> a partir de un
   * URI dado.<br>
   * - Es complementado por el método abstracto <code>setContentFromDoc</code>
   * para asignar adecuadamente a los nuevos campos.
   * @param URI Ubicación de la fuente XML.
   * @throws MENSException Excepción levantada cuando no se encuentra
   * el valor de un campo considerado obligatorio de asignar a la clase. Ésta
   * también es levantada cuando hay problemas de acceso a la fuente XML.
   */
  public final void setFromXMLURI(String URI)throws MENSException{
    String source="";
    byte[] buff;
    org.xml.sax.InputSource isource=new org.xml.sax.InputSource();
    try{
      isource.setByteStream(new java.io.FileInputStream(URI));
      if (isource.getByteStream().available() > 0) {
        buff = new byte[isource.getByteStream().available()];
        isource.getByteStream().read(buff);
        source = tdutils.getString(buff);
        this.setFuenteXML(source);
      }
    }catch(Exception excep){
      excep.printStackTrace();
      try{
        isource.setSystemId(URI);
      }catch(Exception excep2){
        excep2.printStackTrace();
      }
    }
    _setFromXMLSource(new ByteArrayInputStream(source.getBytes()));
  }
  private final void setFromXML0(org.xml.sax.InputSource source)
      throws MENSException{
    int problema[]=new int[1];
    DOMParser parser=new DOMParser();
    Node node=null,backup=null;
    Document document=null;
    int problemaExterno[]=new int[1];
    String mensaje[]=new String[1];
    String mensajeExterno[]=new String[1];
    mensaje[0]="";
    problema[0]=0;
    try{
      parser.parse(source);
      // source.getByteStream();
      // parser.getDocument().getEncoding();
    }catch(Exception except){
      except.printStackTrace();
      problema[0]=1;
      mensaje[0]="Problema al abrir fuente XML.";
      throw new MENSException(mensaje[0]);
    }
    if(problema[0]==0){
      // I M P O R T A N T E: se utiliza el primer nodo para obtener
      // la información de la clase actual
      try{
        document = parser.getDocument();
        this.doc=document;
				node = document.getFirstChild();
				node=getNextElement(node);
        backup=node;
				if(this.nombreNodoPrincipal!=null&&this.nombreNodoPrincipal!=""){
					node=getNextElement(node,this.nombreNodoPrincipal);
          if(node==null){
            node=backup;
          }					
				}
        setFromXMLNode(node);
      }catch(MENSException except){
        throw except;
      }
    }
  }
  /**
   * Complementa la carga del resto de la clase a partir de un documento.<br>
   * - Es utilizada especialmente como parte del método
   * <code>setFromXMLURI</code>.
   * @param document Documento completo provisto por <code>setFromXMLURI</code>.
   * @param problema Referencia a ser utilizada provista por <code>setFromXMLURI</code>.
   * @param mensaje Mensaje a ser puesto a <code>setFromXMLURI</code>.
   */
  protected abstract void setContentFromDoc(Node document,int problema[],String mensaje[]);
  /**
   * Busca en todo un nodo del elemento cuyo nombre es dado, el valor del texto.
   * - La búsqueda se hace en forma recursiva.
   * @param node Nodo a buscar.
   * @return El valor del texto dentro del nodo.
   */
  private static String getElementText0(Node node){
    String res="";
    int i=0;
    if(node.getNodeType()!=Node.TEXT_NODE){
      for(i=0;i<(node.getChildNodes().getLength())&&(res=="");i++){
        res=getElementText0(node.getChildNodes().item(i));
      }
    }else{
      res=node.getNodeValue();
    }
    return res;
  }
  /**
   * Busca un nodo de tipo <code>ELEMENT_NODE</code> que tenga
   * el nombre de tag dado. Profundidad primero y luego de izquierda a derecha.
   * @param node Nodo bajo el cual se realiza la búsqueda.
   * @param nombre Nombre del elemento a encontrar.
   * @return Devuelve el nodo del elemento deseado o null;
   */
  private static Node getElementText00(Node node,String nombre){
    Node node_res=null;
    NodeList nlist;
    int i=0;
    if((node.getNodeType()==Node.ELEMENT_NODE)&&
       (node.getNodeName().compareToIgnoreCase(nombre)==0)){
      return node;
    }
    nlist=node.getChildNodes();
    for(i=0;(i<nlist.getLength())&&(node_res==null);i++){
      node_res=getElementText00(nlist.item(i),nombre);
    }
    return node_res;
  }
  /**
   * Busca en todo un nodo del elemento cuyo nombre es dado, el valor del texto.<br>
   * - La búsqueda se hace en forma recursiva.
   * @param document Elemento del documento ya descifrado o "parseado".
   * @param nombre Nombre del elemento cuyo valor se desea obtener.
   * @return Devuelve en texto, el valor del elemento dado.
   */
  public static String getElementText(Node document,String nombre){
    String res="";
    Node node;
    NodeList nlist=null;
    int problema,i;
    // primero obtiene al elemento del nombre dado
    node=getElementText00(document,nombre);
    if(node!=null){
      // encontró al nodo en el subdocumento dado, así que
      // debe encontrar el texto para devolverlo.
      res=getElementText0(node);
    }
    return res.trim();
  }
  /**
   * Brinda tolerancia para la lectura de un XML.<br>
   * - De esta manera se permite que se omita la asignación de ciertos campos
   * en la clase cuando los elementos y sus valores no se presenten en la
   * fuente XML.
   * @param problema Número de problema, que puede ser modificado. No hay
   * excepción si su valor no es mayor a cero.
   * @param mensaje Mensaje a desplegar al emitir la MENSException. Si no hay
   * excepción, entonces no desplega el mensaje.
   */
  protected abstract void toleraXML(int problema[],String mensaje[]);
  public static Node getNextElement(Node node){
    Node node_res=null;
    NodeList nlist=null;
    int i=0;
    nlist=node.getChildNodes();
    for(i=0;(i<nlist.getLength())&&(node_res==null);i++){
      if(nlist.item(i).getNodeType()==Node.ELEMENT_NODE){
        node_res=nlist.item(i);
      }else{
        node_res=getNextElement(nlist.item(i));
      }
    }
    return node_res;
  }
  public static Node getNextElement(Node node,String name){
    Node node_res=null;
    NodeList nlist=null;
    int i=0;
    nlist=node.getChildNodes();
    for(i=0;(i<nlist.getLength())&&(node_res==null);i++){
      if((nlist.item(i).getNodeType()==Node.ELEMENT_NODE)&&(nlist.item(i).getNodeName().compareToIgnoreCase(name)==0)){
        node_res=nlist.item(i);
      }else{
        node_res=getNextElement(nlist.item(i),name);
      }
    }
    return node_res;
  }
  public static Node getNextSiblingElement(Node node){
    Node node_res=null;
    int i=0;
    node=node.getNextSibling();
    while((node!=null)&&(node_res==null)){
      if(node.getNodeType()==Node.ELEMENT_NODE){
        node_res=node;
      }
      node=node.getNextSibling();
    }
    return node_res;
  }
  public static Node getNextSiblingElement(Node node,String name){
    Node node_res=null;
    int i=0;
    node=node.getNextSibling();
    while((node!=null)&&(node_res==null)){
      if((node.getNodeType()==Node.ELEMENT_NODE)&&
         (node.getNodeName().compareToIgnoreCase(name)==0)){
        node_res=node;
      }
      node=node.getNextSibling();
    }
    return node_res;
  }
  final protected static String getContents(Node node){
    String contenido="";
    if(node==null){
      return "";
    }
    contenido+=getContents0(node);
    node=node.getNextSibling();
    while(node!=null){
      contenido+=getContents0(node);
      node=node.getNextSibling();
    }
    return contenido;
  }
  final protected static String getContents0(Node node){
    NodeList nlist;
    String contenido="";
    int i=0;
    if(node==null){
      return "";
    }
    if(node.getNodeType()==Node.ELEMENT_NODE){
      contenido+="<"+node.getNodeName();
    }
    if(node.getNodeType()==Node.TEXT_NODE){
      contenido+=">"+node.getNodeValue();
    }
    if(node.getNodeType()==Node.ENTITY_NODE){
      contenido+="ENTIDAD";
    }
    nlist=node.getChildNodes();
    for(i=0;i<nlist.getLength();i++){
      contenido+=getContents0(nlist.item(i));
    }
    return contenido;
  }
  protected static String getDocumentoParcial(Node node) {
    String resultado="";
    NamedNodeMap nnm=null;
    if(node==null){
      return resultado;
    }
    while(node!=null){
      if (node.getNodeType() == Node.DOCUMENT_NODE) {
        resultado += getDocumentoParcial(node.getFirstChild());
      }
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        if(node.hasAttributes()){
          nnm=node.getAttributes();
          resultado += "<" + node.getNodeName();
          for(int i=0;i<nnm.getLength();i++){
            if(nnm.item(i).getNodeType()==Node.ATTRIBUTE_NODE){
              resultado += " "+ nnm.item(i).getNodeName()+"=\"";
              resultado +=      nnm.item(i).getNodeValue()+"\"";
            }
          }
          resultado += ">";

        }else{
          resultado += "<" + node.getNodeName() + ">";
        }
        resultado += getDocumentoParcial(node.getFirstChild());
        resultado += "</" + node.getNodeName() + ">";
      }
      if (node.getNodeType() == Node.TEXT_NODE) {
        resultado += node.getNodeValue();
      }
      if (node.getNodeType() == Node.COMMENT_NODE) {
        resultado += "<!--"+node.getNodeValue()+"-->";
      }
      node=node.getNextSibling();
    }

    return resultado;
  }
  /**
   * Consulta el nombre del nodo principal XML.
   * @return Nombre del nodo principal a partir del cual se inicia
   * la búsqueda.
   */
  public String getNombreNodoPrincipal() {
    return nombreNodoPrincipal;
  }

  /**
   * Asigna el nombre del nodo principal del objeto XML.
   * @param string Nombre del nodo principal del XML.
   */
  public void setNombreNodoPrincipal(String string) {
    nombreNodoPrincipal = string;
  }
  public final void setComando(MENSIComandos comando0){
    this.comando=comando0;
  }
  public final MENSIComandos getComando(){
    return this.comando;
  }
}
