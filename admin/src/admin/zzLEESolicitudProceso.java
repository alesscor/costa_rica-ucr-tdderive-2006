package admin;
import org.w3c.dom.*;
import tables.AbstractSet;
import mens.*;
/**
 * <p>Title: Admin</p>
 * <p>Description: Administración de procesos</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero [alesscor@ieee.org]
 * @version 1.0
 */
/***
 * Contiene la solicitud de ejecución de un determinado programa
 * utilizando los parámetros dados. Para cumplir la solicitud
 * intervienen tres procesos: el proceso que administra todos los trabajos
 * que se realizan, el proceso que ejecuta el programa y el proceso que
 * acompaña al programa y que sirve de interfaz con el objeto administrador
 * <b>ADMInventarioProcesos</b> para concretar operaciones
 * sobre el programa.<br><br>
 * Cada solicitud tiene la siguiente estructura.
 * <pre>
 * alias
 * +-- -parámetro1
 * +--  parámetro2
 * +-- -parámetro3
 * +-- -parámetro4
 * +--  parámetro5
 * </pre>
 * En donde alias es el nombre simplificado de un programa que tiene su
 * ruta en la computadora local y donde parámetro<i>i</i> es un parámetro
 * de ese programa.
 */

public class zzLEESolicitudProceso {
  //////////////////////////////////////////////////////////////////////
  /**
   * Alias del programa que se está ejecutando.
   */
  private String alias;
  /**
   * Directorio de trabajo a partir del cual está trabajando el proceso.
   */
  private String directorioTrabajo;
  /**
   * Parametros del proceso.<br>
   * <li>El parámetro 0 es la ruta la llamada al proceso.</li>
   */
  private String parametros[];
  /**
   * Variables de ambiente para el proceso a ejecutar.
   */
  private String ambiente[];
  /**
   * Nodo en donde se está originó el proceso. Es formado por
   * el alias del nodo.
   */
  private String maquinaOrigen;
  /**
   * Indica si el proceso debe llamarse sincrónica o asincrónicamente,
   * es decir, si debe bloquear el hilo invocante o no.
   */
  private boolean esperar;
  /**
   * Comando que le corresponde al alias.
   */
  private String comando;
  /**
   * Objeto de carga y descarga de y a código XML.
   */
  private xmlsolicitaproceso xml;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public String getAlias(){
    return alias;
  }
  public void setAlias(String alias0){
    alias=alias0;
  }
  public void setComando(String comando0){
    comando=comando0;
  }
  public void setAmbiente(String ambiente0[]){
    ambiente=ambiente0;
  }
  public void setParametros(String param0[]){
    parametros=param0;
  }
  public void setEsperar(boolean esperar0){
    esperar=esperar0;
  }
  public void setDirectorio(String dir0){
    directorioTrabajo=dir0;
  }
  public String[] getComandos(){
    String comandos[];
    int i=0;
    if(parametros!=null){
      comandos = new String[parametros.length + 1];
      comandos[0] = comando;
      for (i = 0; i < parametros.length; i++) {
        comandos[i + 1] = parametros[i];
      }
    }else{
      comandos = new String[1];
      comandos[0] = comando;
    }
    return comandos;
  }
  public String getComando(){
    return comando;
  }
  public String[] getAmbiente(){
    /** @todo: por ahora no sé como tomar las variables de ambiente de otra
     *         forma que no sea por JNI.
     */
      return ambiente;
  }
  public boolean getEsperar(){
    return esperar;
  }
  public java.io.File getDirectorio()throws ADMINGLOExcepcion{
    java.io.File dir=null;
    try {
      dir = new java.io.File(directorioTrabajo);
      if(!dir.isDirectory()){
        // mal, debe darse error
        throw new ADMINGLOExcepcion("\"" +directorioTrabajo +
                               "\" no es nombre de directorio");
      }
    }catch (Exception ex) {
      throw new ADMINGLOExcepcion("\"" +directorioTrabajo +"\" no se encuentra");
    }

    return dir;
  }
  public void loadFromXMLNode(Node nodo) throws ADMINGLOExcepcion{
    try{
      xml.setFromXMLNode(nodo);
    }catch(Exception ex){
      throw new ADMINGLOExcepcion(ex.getMessage(),ex);
    }
  }
  public void setXMLParser(){
    if(xml==null){
      xml = new xmlsolicitaproceso(this);
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
   * Clase dedicada a cargar y descargar el objeto ADMDescriptor en un
   * XML.
   */
  private class xmlsolicitaproceso extends mens.MENSMensaje{
    //////////////////////////////////////////////////////////////////////
    private zzLEESolicitudProceso pd;
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    public xmlsolicitaproceso(zzLEESolicitudProceso pd0){
      pd=pd0;
    }
    protected String getXMLContainedElements(){
      String xml="",subxml="";
      int i=0;
      xml+="<solicitud>\n";
      xml+="\t<alias>";
      xml+=pd.alias;
      xml+="</alias>\n";
      xml+="\t<comando>";
      xml+=pd.comando;
      xml+="</comando>\n";
      xml+="\t<ambiente>\n";
      subxml="";
      if(ambiente!=null){
        while (i < pd.ambiente.length) {
          subxml += ("\t\t<ambiente" + (i + 1) + ">");
          subxml += pd.ambiente[i].trim();
          subxml += ("</ambiente" + (i + 1) + ">\n");
          i++;
        }
        xml+=subxml;
        subxml="";
      }
      xml+="\t</ambiente>\n";
      xml+="\t<parametros>\n";
      if(parametros!=null){
        while (i < pd.parametros.length) {
          subxml += ("\t\t<parametro" + (i + 1) + ">");
          subxml += pd.parametros[i].trim();
          subxml += ("</parametro" + (i + 1) + ">\n");
          i++;
        }
        xml += subxml;
        subxml="";
      }
      xml+="\t</parametros>\n";
      xml+="\t<directorio>";
      xml+=pd.directorioTrabajo;
      xml+="</directorio>\n";
      xml+="\t<esperar>";
      if(pd.esperar){
        xml+="true";
      }else{
        xml+="false";
      }
      xml+="</esperar>\n";
      xml+="\t<maquinaorigen>";
      xml+=pd.maquinaOrigen;
      xml+="</maquinaorigen>\n";
      xml+="</solicitud>";
      return xml;
    }
    protected void setContentFromDoc(Node nodo,int[]err,String[]merr){
      AbstractSet lista=new AbstractSet(new String[]{""});
      Node bookmark=nodo;
      String texto="";
      int i=0;
      pd.alias=getElementText(nodo,"alias").trim();
      pd.comando=getElementText(nodo,"comando").trim();
      pd.directorioTrabajo=getElementText(nodo,"directorio").trim();
      if(getElementText(nodo,"esperar").trim().compareToIgnoreCase("true")==0){
        pd.esperar=true;
      }else{
        pd.esperar=false;
      }
      pd.maquinaOrigen=getElementText(nodo,"maquinaorigen").trim();
      // carga listas de ambiente y parámetros
      if(nodo.getNodeName().compareToIgnoreCase("parametros")!=0){
        nodo = MENSMensaje.getNextElement(nodo, "parametros");
      }
      if(nodo!=null){
        i=0;
        lista.clean();
        while((nodo!=null)&&(nodo.getNodeName().
                             compareToIgnoreCase("parametro"+(i+1))==0)){
          // debe haber info sobre parámetros
          texto=getElementText(nodo,"parametro"+(i+1)).trim();
          i++;
          nodo = MENSMensaje.getNextSiblingElement(nodo, "parametro"+(i+1));
        }
        pd.parametros=new String[lista.getCount()];
        lista.moveFirst();
        i=0;
        while(!lista.getEoF()){
          pd.parametros[i]=((String)lista.getObject()).trim();
          i++;
          lista.moveNext();
        }
      }
      nodo=bookmark;
      if(nodo.getNodeName().compareToIgnoreCase("ambiente")!=0){
        nodo = MENSMensaje.getNextElement(nodo, "ambiente");
      }
      if(nodo!=null){
        i=0;
        lista.clean();
        while((nodo!=null)&&(nodo.getNodeName().
                             compareToIgnoreCase("ambiente"+(i+1))==0)){
          // debe haber info sobre parámetros
          texto=getElementText(nodo,"ambiente"+(i+1)).trim();
          i++;
          nodo = MENSMensaje.getNextSiblingElement(nodo, "ambiente"+(i+1));
        }
        pd.ambiente=new String[lista.getCount()];
        lista.moveFirst();
        i=0;
        while(!lista.getEoF()){
          pd.ambiente[i]=((String)lista.getObject()).trim();
          i++;
          lista.moveNext();
        }
      }
    }
    protected void toleraXML(int[]err,String[]merr){
      if(pd.comando=="" || pd.directorioTrabajo==""){
        // cero tolerancia
        err[0]=3;
        merr[0]="No se encuentra un elemento importante para " +
                "cargar la solicitud de proceso.";
      }
    }
    public boolean isVacio(){
      return (pd.comando=="");
    }
  }
}
