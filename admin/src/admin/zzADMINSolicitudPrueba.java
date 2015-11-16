package admin;

import oact.*;
import org.w3c.dom.*;

/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */

public class zzADMINSolicitudPrueba extends ADMINAPPMetodoDespAbs {

  public zzADMINSolicitudPrueba() {
  }

  public zzADMINSolicitudPrueba(OACTSirvienteAbs sirviente0) {
    super(sirviente0);
  }

  public zzADMINSolicitudPrueba(String servantID) {
    super(servantID);
  }
  public boolean isVacio() {
    return false;
  }
  protected String getXMLContainedElements0() {
    return "";
  }
  protected void toleraXML0(int[] parm1, String[] parm2) {
  }
  public boolean ejecutarInicio() throws ADMINGLOExcepcion {
    return true;
  }
  public void ejecutarFin(OACTSolicitud futuro,boolean siniciado) throws ADMINGLOExcepcion {
  }
  public OACTSolicitud ejecutar() throws ADMINGLOExcepcion {
    return null;
  }
  protected void setContentFromDoc0(Node parm1, int[] parm2, String[] parm3) {
  }
  protected void revisaEstados(){
  }
}