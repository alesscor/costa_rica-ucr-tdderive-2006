package admin;
import org.w3c.dom.*;

/**
 * <p>Title: Admin</p>
 * <p>Description: Administraci�n de procesos</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero [alesscor@ieee.org]
 * @version 1.0
 */

public class zzADMINDescriptorRevisar {
  //////////////////////////////////////////////////////////////////////
  public final static char separador='|';
  /**
   * Alias del proceso que se est� ejecutando.
   */
  private String alias;
  /**
   * Identificaci�n del proceso. Compuesta por
   * <code>alias_nodo + n�mero_proceso_nodo</code>.
   */
  private String id;
  /**
   * Identificaci�n del proceso padre. Compuesta por
   * <code>id</code> del proceso padre.
   */
  private String idPadre;
  /**
   * Identificaci�n del grupo de trabajo del proceso. Compuesta por
   * <code>alias_nodo + n�mero_grupo</code>.<br>
   * <li> Esta identificaci�n es copiada por todos los procesos del
   * mismo grupo.</li>
   */
  private String idGrupo;
  /**
   * Objeto de carga y descarga de y a c�digo XML.
   */
  private xmldescriptor xml;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public zzADMINDescriptorRevisar() {
    alias="";
    id="";
    idPadre="";
    idGrupo="";
    xml=null;
  }
  public zzADMINDescriptorRevisar(
      String id0, String idPadre0, String idGrupo0, zzLEESolicitudProceso sol0) {
    if(sol0!=null){
      alias = sol0.getAlias();
    }
    id=id0;
    idPadre=idPadre0;
    idGrupo=idGrupo0;
    xml=null;
  }
  public String getAlias(){
    return alias;
  }
  public String getId(){
    return id;
  }
  public String getIdPadre(){
    return idPadre;
  }
  public String getIdGrupo(){
    return idGrupo;
  }
  /**
   * Devuelve la identificaci�n del nodo en donde se est� ejecutando
   * el proceso.
   * @param proceso descriptor del proceso de inter�s.
   * @return Nombre del nodo en donde se ejecuta el proceso.
   */
  public static String getNodoProceso(zzADMINDescriptorRevisar proceso){
    String res=null;
    int idx;
    res=proceso.getId();
    idx=res.indexOf(zzADMINDescriptorRevisar.separador);
    if(idx>0){
      res=res.substring(0,idx);
    }
    return res;
  }
  /**
   * Devuelve la identificaci�n del nodo en donde se est� ejecutando
   * el padre del proceso.
   * @param proceso descriptor del proceso de inter�s.
   * @return Nombre del nodo en donde se ejecuta el padre del proceso.
   */
  public static String getNodoPadre(zzADMINDescriptorRevisar proceso){
    String res=null;
    int idx;
    res=proceso.getIdPadre();
    idx=res.indexOf(zzADMINDescriptorRevisar.separador);
    if(idx>0){
      res=res.substring(0,idx);
    }
    return res;
  }
  /**
   * Devuelve la identificaci�n del nodo en donde se origin�
   * el grupo de procesos al que pertenece el proceso dado.
   * @param proceso descriptor del proceso del grupo de inter�s.
   * @return Nombre del nodo en donde se origin� el grupo.
   */
  public static String getNodoGrupo(zzADMINDescriptorRevisar proceso){
    String res=null;
    int idx;
    res=proceso.getIdGrupo();
    idx=res.indexOf(zzADMINDescriptorRevisar.separador);
    if(idx>0){
      res=res.substring(0,idx);
    }
    return res;
  }
  public void setXMLParser(){
    if(xml==null){
      xml = new xmldescriptor(this);
    }
  }
  public void loadFromXMLNode(Node nodo) throws ADMINGLOExcepcion{
    try{
      xml.setFromXMLNode(nodo);
    }catch(Exception ex){
      throw new ADMINGLOExcepcion(ex.getMessage(),ex);
    }
  }
  public String storeToXMLNode() throws ADMINGLOExcepcion{
    String res="";
    this.setXMLParser();
    try{
      res = xml.getXMLElem(null);
    }catch(Exception ex){
      throw new ADMINGLOExcepcion(ex.getMessage(),ex);
    }
    return res;
  }
  //////////////////////////////////////////////////////////////////////
  /***
   * Clase dedicada a cargar y descargar el objeto ADMINDescriptorRevisar en un
   * XML.
   */
  private class xmldescriptor extends mens.MENSMensaje{
    //////////////////////////////////////////////////////////////////////
    private zzADMINDescriptorRevisar pd;
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    public xmldescriptor(zzADMINDescriptorRevisar pd0){
      pd=pd0;
    }
    protected String getXMLContainedElements(){
      String xml="";
      xml+="<descriptor>\n";
      xml+="\t<alias>";
      xml+=pd.alias;
      xml+="</alias>\n";
      xml+="\t<id>";
      xml+=pd.id;
      xml+="</id>\n";
      xml+="\t<padre>";
      xml+=pd.idPadre;
      xml+="</padre>\n";
      xml+="\t<grupo>";
      xml+=pd.idGrupo;
      xml+="</grupo>\n";
      xml+="</descriptor>";
      return xml;
    }
    protected void setContentFromDoc(Node nodo,int[]err,String[]merr){
      pd.alias=getElementText(nodo,"alias").trim();
      pd.id=getElementText(nodo,"id").trim();
      pd.idPadre=getElementText(nodo,"padre").trim();
      pd.idGrupo=getElementText(nodo,"grupo").trim();
    }
    protected void toleraXML(int[]err,String[]merr){
      if(pd.alias=="" || pd.id=="" || pd.idPadre =="" || pd.idGrupo ==""){
        // cero tolerancia
        err[0]=3;
        merr[0]="No se encuentra un elemento importante para " +
                "cargar el descriptor de procesos.";
      }
    }
    public boolean isVacio(){
      return (pd.getId()=="");
    }
  }
}
