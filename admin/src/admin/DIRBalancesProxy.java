/*
 * Created on 23/07/2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package admin;
import oact.OACTExcepcion;
import oact.OACTSolicitud;
/**
 * <p>Title: <b>admin</b>:: admin</p>
 * <p>Description: DIRBalancesProxy.java.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCR - ECCI</p>
 * <br>@author Alessandro</br>
 * <br>@version 1.0</br>
 */
/**
 * Intermediario para realizar contacto con un balanceador
 * remoto. 
 */
class DIRBalancesProxy extends DIRBalances {
  private ADMINGLOInfo desc;
  private String sDestino;
  public DIRBalancesProxy() {
    super(null);
    _inicio();
  }
  public DIRBalancesProxy(String id0) {
    super(id0,null);
    _inicio();
  }
  public void balancea(final OACTSolicitud mensaje) throws DIRException {
    String[] salida=new String[1];
    try {
      this.interProxy(sDestino,mensaje, salida);
    } catch (OACTExcepcion e) {
      throw new DIRException("No se pudo enviar mensaje de balance a '"+
          this.getDestino()+"'",e);
    }
  }
  /**
   * @see admin.DIRBalances#close()
   */
  public void close() throws DIRException {
  }
  public String getDestino(){
    return sDestino;
  }
  /**
   * @see admin.DIRBalances#open()
   */
  public void open() throws DIRException {

  }
  public void setDestino(String setDestino){
    sDestino=setDestino;
  }
  private void _inicio(){
  }
  public final void informaNodo(String destino,
      double a,double b) throws ADMINGLOExcepcion{
    
  }
  public void informaDominio(double a,double b) throws 
  ADMINGLOExcepcion{
  }
}
