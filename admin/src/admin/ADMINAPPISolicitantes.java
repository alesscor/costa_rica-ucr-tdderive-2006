/*
 * Created on 31/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

/**
 * <p>T�tulo: admin</p>
 * <p>Descripci�n: Interfaz para el manejo de solicitantes.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzaci�n: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Interfaz para el manejo de la informaci�n de un solicitante
 * de aplicaci�n.
 */
public interface ADMINAPPISolicitantes {
  /**
   * Indica que el resultado ha sido entregado al sistema solicitante.
   */
	  public final static String SOLICITANTE_ENTREGADO="SOLICITANTE_ENTREGADO";
	/**
   * Indica que la tarea ha terminado y que aun no se confirma
   * la entrega del resultado.
   */
	  public final static String SOLICITANTE_FIN="SOLICITANTE_FIN";
	/**
   * Indica que la solicitud se est� preparando.
   */
	  public final static String SOLICITANTE_INICIO="SOLICITANTE_INICIO";
	/**
   * Indica si a la solicitud no se debe considerar m�s.
   */
	  public final static String SOLICITANTE_INVALIDO="SOLICITANTE_INVALIDO";
	/**
   * Indica si la solicitud est� completa.
   */
	  public final static String SOLICITANTE_LISTO="SOLICITANTE_LISTO";
	/**
   * Indica que la tarea se encuentra en marcha.
   */
	  public final static String SOLICITANTE_MARCHA="SOLICITANTE_MARCHA";
	/**
   * Indica que est� preparando su tarea.
   */
	  public final static String SOLICITANTE_PREPTAREA="SOLICITANTE_PREPTAREA";
	/**
   * Indica si el programa solicitado no se encuentra en el sistema.
   */
	  public final static String SOLICITANTE_SINPROGRAMA="SOLICITANTE_SINPROGRAMA";
	/**
   * Indica que ya tiene lista su tarea.
   */
	  public final static String SOLICITANTE_YATAREA="SOLICITANTE_YATAREA";
    public final static String SOLICITANTE_TIPOSUBTRABAJO="SOLICITANTE_TIPOSUBTRABAJO";
 /**
   * Obtiene el tipo de solicitud del solicitante.
   * @return El tipo de solicitud.
   */
  public String getTipoSol();
  /**
   * Obtiene el texto de retorno que el usuario a solicitado y ha dejado
   * en la solicitud.
   * @return El texto de retorno.
   */
  public String getRetorno();
  /**
   * Obtiene el nombre de la computadora del usuario que ha solicitado
   * la ejecuci�n.
   * @return El nombre o direcci�n de la computadora del usuario.
   */
  public String getDesdeNombre();
  /**
   * El n�mero del puerto en el que el usuario ha establecido una conexi�n
   * para solicitar una tarea.
   * @return El n�mero del puerto del usuario.
   */
  public long getDesdePuerto();
  /**
   * Indica si se ha entregado todo lo que corresponde como retorno al usuario.
   * @return Si se ha entregado todo al usuario.
   */
  public boolean getSiEntregado();
  /**
   * Obtiene el estado del solicitante.
   * @return El estado del solicitante.
   */
  public String getEstadoSolicitante();
  /**
   * Obtiene la identificaci�n parcial del solicitante.
   * @return Identificaci�n parcial.
   */
  public String getIdParcial();
}
