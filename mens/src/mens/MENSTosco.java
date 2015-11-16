package mens;
import org.w3c.dom.*;
import org.apache.xerces.parsers.*;
import java.io.*;
import org.xml.sax.*;
/**
 * Clase a ser extendida por todo objeto que quiere leer en forma
 * general una fuente XML.
 */
public class MENSTosco extends MENSMensaje {
  private String contenido;
  private boolean _isset_contenido;
  public final String getContenido(){
    return contenido;
  }
  public final void setContenido(String contenido0){
    if(contenido0!=null){
      contenido = contenido0;
      _isset_contenido=true;
    }
  }
  public MENSTosco() {
    contenido="";
    _isset_contenido=false;
  }
  public final boolean isVacio() {
    return !_isset_contenido;
  }
  /**
   * Da el contenido del documento XML en un elemento XML nuevo.
   * @return El contenido XML encerrado en tags <contenido/>.
   */
  protected String getXMLContainedElements() {
    return "<contenido>\n"+
        this.getContenido()+
        "</contenido>\n";
  }
  /**
   * Solamente le pone el XML fuente al campo "contenido".
   * @param parm1 Nodo del documento XML a revisar. No es leído ni revisado.
   * @param parm2 Número de error. No es modificado.
   * @param parm3 Hilera de error. No es modificado.
   */
  protected void setContentFromDoc(Node parm1, int[] parm2, String[] parm3) {
    setContenido(getFuenteXML());
  }
  /**
   * Método muy importante porque realiza verificaciones a la hora de hacer
   * setFromXML...().
   * @param parm1 Si se pone en 0 es para decir que no hay errores.
   * @param parm2 Si se pone en "" es para decir que no hay errores.
   */
  protected void toleraXML(int[] parm1, String[] parm2) {
    // deja todo como está
    parm1[0]=0;
    parm2[0]="";
  }
  protected void postXMLLoading() {
  }
  public final void loadFromString(String hilera) throws MENSException{
    // debe cargar los campos className y contenido
    Document doc=null;
    Node node=null;
    org.apache.xerces.parsers.SAXParser saxp;
    DOMParser domp=new DOMParser();
    try {
      domp.parse(new org.xml.sax.InputSource(new java.io.ByteArrayInputStream(
          hilera.getBytes())));
      doc=domp.getDocument();
      node=doc.getElementsByTagName("contenido").item(0);
      if(node!=null){
        this.setContenido(getDocumentoParcial(node.getFirstChild()));
      }
      loadFromStringSpecialFields(doc);
//      System.out.println(this.getContenido());
    }
    catch (IOException ex) {
      throw new MENSException("Error al abrir hilera XML");
    }
    catch (SAXException ex) {
      throw new MENSException("Error al interpretar hilera XML");
    }
  }
  /**
   * Para cargar otros campos cuando este método es invocado por
   * el método loadFromString().
   * @param doc A partir de dónde se empiezan a buscar los valores de
   * los campos.
   * @throws MENSException Error cuando se detecte la falta de algún valor
   * para algún campo.
   */
  protected void loadFromStringSpecialFields(Document doc) throws MENSException{
  }
}