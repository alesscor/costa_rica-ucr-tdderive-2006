package admin;

import oact.OACTExcepcion;

/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Intermediario un despachador.
 */
public class ADMINAPPDespachadorProxy extends ADMINAPPDespachadorAbs {
  protected ADMINAPPMetodoSolicitud solicitudMensaje;
  private String cDestino;

  public ADMINAPPDespachadorProxy() {
    super(null);
    _inicio();
  }

  public ADMINAPPDespachadorProxy(String id0) {
    super(id0,null);
    _inicio();
  }
  private void _inicio(){
    // el valor de solicitudMensaje debe ser asignado
    // por el iniciador de aplicaciones
    solicitudMensaje=null;
  }
  public String getDestino(){
    return cDestino;
  }
  public void setDestino(String setDestino){
    cDestino=setDestino;
  }
  ADMINAPPMetodoSolicitud getSolicitudMensaje(){
    return solicitudMensaje;
  }
  void setSolicitudMensaje(ADMINAPPMetodoSolicitud setSolicitudMensaje){
    solicitudMensaje=setSolicitudMensaje;
  }
  public String ejecuta(String alias, String[] parametros) throws ADMINAPPExcepcion {
    String[] salida=new String[1];
    try {
      
      this.interProxy(cDestino,solicitudMensaje,salida);
    } catch (OACTExcepcion e) {
      throw new ADMINAPPExcepcion("No se pudo enviar solicitud.",e);
    }
    return salida[0];
  }
}