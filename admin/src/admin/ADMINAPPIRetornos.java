/*
 * Created on 31/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

/**
 * <p>Título: admin</p>
 * <p>Descripción: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzación: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Interfaz dedicada a forjar las características de un retorno. 
 */
public interface ADMINAPPIRetornos {
  public final static String RETORNO_ESTADOENTREGADO="ENTREGADO";
  public final static String RETORNO_ESTADOPENDIENTE="PENDIENTE";
  public final static String RETORNO_ESTADOFALLIDO="FALLIDO";
  /**
   * Propicia entrega asincrónica del resultado.
   */
  public final static String RETORNO_TIPODIRECTORIO="DIRECTORIO";
  /**
   * Propicia entrega asincrónica del resultado.
   */
  public final static String RETORNO_TIPOCORREO="CORREO";
  /**
   * Propicia entrega sincrónica del resultado.
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
   * Obtiene la identificación parcial del proceso al que le corresponde
   * este retorno.
   * @return Identificación parcial del proceso del retorno.
   */
  public String getIdParcial();


  /**
   * Obtiene el tipo de retorno.
   * @return El tipo de retorno.
   */
  public String getTipoRetorno();


  /**
   * Obtiene el valor de la indicación de retorno.
   * @return La indicación del retorno.
   */
  public String getValorRetorno();
  /**
   * Obtiene el tipo de retorno.
   * @param setTipoRetorno El tipo de retorno a asignar.
   * @return El tipo de retorno.
   */
  public void setTipoRetorno(String setTipoRetorno);


  /**
   * Obtiene el valor de la indicación de retorno.
   * @param setValorRetorno El valor de retorno a asignar.
   * @return La indicación del retorno.
   */
  public void setValorRetorno(String setValorRetorno);

}
