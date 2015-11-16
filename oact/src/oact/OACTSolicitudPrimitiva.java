package oact;
import mens.*;
import org.w3c.dom.*;

/**
 * Clase que indica la forma básica de una solicitud a
 * un sirviente dentro del patrón objeto activo.
 */

public class OACTSolicitudPrimitiva extends MENSTosco {
  private String caracteristica;
  /**
   * Identificación de la solicitud, necesaria para bitácoras.
   */
  public String identificacion;
  private boolean atendido;
  public String className;
  private String servantName;
  /**
   * Gestor, por si es necesario.
   */
  private OACTGestorDistribuidor gestor;
  /**
   * Lleva el conteo de los intentos de ejecución no satisfechos.
   */
  private int devoluciones;
  private boolean _isset_caracteristica;
  private boolean _isset_identificacion;
  private boolean _isset_className;
  private boolean _isset_servantName;
  public OACTSolicitudPrimitiva() {
    caracteristica="";
    atendido=false;
    className="";
    _isset_caracteristica=false;
    _isset_identificacion=false;
    _isset_className=false;
    _isset_servantName=false;
    devoluciones=-1;
    gestor=null;
//    this.setIdentificacion(Long.toString(
//        System.currentTimeMillis() - idcomplemento));
  }
//  protected void toleraXML(int[] parm1, String[] parm2) {
//    parm1[0]=0;
//    parm2[0]="OASolicitud";
//  }
  public String getCaracteristica(){
    return caracteristica;
  }
  public void setCaracteristica(String carac0){
    if(carac0==""){
      caracteristica = carac0;
      _isset_caracteristica=true;
    }
  }
  public String getIdentificacion(){
      return identificacion;
  }
  public void setIdentificacion(String id){
    if(id!=""){
      identificacion=id;
      _isset_identificacion=true;
    }
  }
  public void setAtendido(boolean val){
    atendido=val;
  }
  public boolean getAtendido(){
    return atendido;
  }
  public void setClassName(String val){
    if(val!=""){
      className = val;
      _isset_className=true;
    }
  }
  public String getClassName(){
    return this.className;
  }
  public void incDevoluciones(){
    devoluciones++;
  }
  public int getDevoluciones(){
    return devoluciones;
  }
  public void setServantName(String iname){
    if((iname!=null) && (iname!="")){
      servantName=iname;
      _isset_servantName=true;
    }
  }
  public String getServantName(){
    return servantName;
  }
  //////////////////////////////////////////////////////////////////////
  /**
   * Extiende el método de OAMsjTosco, asignando el nombre de la clase
   * relacionada con el mensaje encargada de ejecutar la interpretación
   * del propio mensaje.
   * @param parm1 Nodo del documento XML a examinar.
   * @param parm2 Puntero al número de error.
   * @param parm3 Puntero al mensaje de error.
   */
  protected void setContentFromDoc(Node parm1, int[] parm2, String[] parm3) {
    parm1=this.getDocumento();
    setClassName(getElementText(parm1,"className"));
    setClassName(getElementText(parm1,"servantName"));
    parm1=null;
    super.setContentFromDoc(parm1,parm2,parm3);
  }
  protected String getXMLContainedElements() {
    return "<solicitud>\n"+
        "<className>"+
        this.getClassName()+
        "</className>\n"+
        "<servantName>"+
        this.getServantName()+
        "</servantName>\n"+
        "<contenido>\n"+
        this.getContenido()+
        "\n</contenido>\n"+
        "</solicitud>\n";
  }
  public void loadFromMensaje(OACTSolicitud mensaje){
    // alesscor
    // this.setContenido(mensaje.getXMLRootElem(null));
    this.setContenido(mensaje.getXMLElem(null));
    if(mensaje.getIdSirviente()!=null){
      this.setServantName(mensaje.getIdSirviente());
    }
    if(mensaje.getComplemento()!=null){
      this.setClassName(mensaje.getComplemento().getName());
    }else{
      if (mensaje.getClass() != null) {
        this.setClassName(mensaje.getClass().getName());
      }
    }
    this.setComando(mensaje.getComando());

  }
  protected void loadFromStringSpecialFields(Document doc) throws MENSException{
    Node node=null;
    node=doc.getElementsByTagName("className").item(0);
    if(node!=null){
      this.setClassName(getElementText(node,"className"));
    }else{
      throw new MENSException("No se encontro el nombre de la clase.");
    }
    node=doc.getElementsByTagName("servantName").item(0);
    if(node!=null){
      this.setServantName(getElementText(node,"servantName"));
    }else{
      throw new MENSException("No se encontó el nombre de la interfaz.");
    }
  }
  public final OACTGestorDistribuidor getGestorDist(){
    return gestor;
  }
  public final void setGestorDist(OACTGestorDistribuidor gestor0){
    gestor=gestor0;
  }
  public final String getStatus(){
    String status="";
    String contenido="";
    if((this.identificacion!=null)&&(this.identificacion!="")){
      status+="<id>";
      status+=this.identificacion;
      status+="</id>";
    }
    status+="<className>";
    status+=this.className;
    status+="</className>";
    status+="<servantName>";
    status+=this.servantName;
    status+="</servantName>";
    status+="<contenido>";
    if(this.getContenido().length()>30){
      contenido = this.getContenido().substring(0, 29);
    }else{
      contenido = this.getContenido();
    }
    contenido=contenido.replace('<','(');
    contenido=contenido.replace('>',')');
    status+=contenido;
    status+="</contenido>";
    if(this.atendido){
      status += "<atendido>true</atendido>";
    }else{
      status += "<atendido>false</atendido>";
    }
    status = "<solicitud>" + status + "</solicitud>";
    return status;
  }
}