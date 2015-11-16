/*
 * Created on 31/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

/**
 * <p>T�tulo: admin</p>
 * <p>Descripci�n: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzaci�n: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Interfaz dedicada a forjar las caracter�sticas de un retorno. 
 */
public interface ADMINAPPIRetornos {
  public final static String RETORNO_ESTADOENTREGADO="ENTREGADO";
  public final static String RETORNO_ESTADOPENDIENTE="PENDIENTE";
  public final static String RETORNO_ESTADOFALLIDO="FALLIDO";
  /**
   * Propicia entrega asincr�nica del resultado.
   */
  public final static String RETORNO_TIPODIRECTORIO="DIRECTORIO";
  /**
   * Propicia entrega asincr�nica del resultado.
   */
  public final static String RETORNO_TIPOCORREO="CORREO";
  /**
   * Propicia entrega sincr�nica del resultado.
   */
  public final static String RETORNO_TIPOSALIDAESTANDAR="SALIDAESTANDAR";
  /**
   * Obtiene el estado del retorno representado por esta instancia.
   * @return El estado del retorno.
   */
  public String getEstadoRetorno();


  /**
   * Obtiene el nombre del grupo del proceso al que corresponde este retorno.
   * @return El grupo del retorno.
   */
  public String getIdGrupo();


  /**
   * Obtiene el nombre del padre del proceso al que le corresponde este retorno.
   * @return El padre del retorno.
   */
  public String getIdPadre();


  /**
   * Obtiene la identificaci�n parcial del proceso al que le corresponde
   * este retorno.
   * @return Identificaci�n parcial del proceso del retorno.
   */
  public String getIdParcial();


  /**
   * Obtiene el tipo de retorno.
   * @return El tipo de retorno.
   */
  public String getTipoRetorno();


  /**
   * Obtiene el valor de la indicaci�n de retorno.
   * @return La indicaci�n del retorno.
   */
  public String getValorRetorno();
  /**
   * Obtiene el tipo de retorno.
   * @param setTipoRetorno El tipo de retorno a asignar.
   * @return El tipo de retorno.
   */
  public void setTipoRetorno(String setTipoRetorno);


  /**
   * Obtiene el valor de la indicaci�n de retorno.
   * @param setValorRetorno El valor de retorno a asignar.
   * @return La indicaci�n del retorno.
   */
  public void setValorRetorno(String setValorRetorno);

}
