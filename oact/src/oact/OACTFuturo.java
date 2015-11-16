/*
 * Created on 02/05/2006
 *
 */
package oact;

import org.w3c.dom.Node;

/**
 * @author alessandro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OACTFuturo extends OACTSolicitud {
  private String contenido="";
  /**
   * @param sirviente0
   */
  public OACTFuturo(OACTSirvienteAbs sirviente0) {
    super(sirviente0);
  }

  /**
   * @param servantID
   */
  public OACTFuturo(String servantID) {
    super(servantID);
    // TODO Auto-generated constructor stub
  }

  protected OACTSolicitud ejecutar() throws Exception {
    return null;
  }

  public boolean isPreparado() {
    return true;
  }

  public boolean isVacio() {
    return contenido!="";
  }

  protected String getXMLContainedElements() {
    return contenido;
  }

  /* (non-Javadoc)
   * @see mens.MENSMensaje#setContentFromDoc(org.w3c.dom.Node, int[], java.lang.String[])
   */
  protected void setContentFromDoc(Node document, int[] problema,
      String[] mensaje) {
  }

  protected void toleraXML(int[] problema, String[] mensaje) {
  }

}
