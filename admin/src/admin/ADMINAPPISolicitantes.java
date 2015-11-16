/*
 * Created on 31/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

/**
 * <p>Título: admin</p>
 * <p>Descripción: Interfaz para el manejo de solicitantes.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzación: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Interfaz para el manejo de la información de un solicitante
 * de aplicación.
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
   * Indica que la solicitud se está preparando.
   */
	  public final static String SOLICITANTE_INICIO="SOLICITANTE_INICIO";
	/**
   * Indica si a la solicitud no se debe considerar más.
   */
	  public final static String SOLICITANTE_INVALIDO="SOLICITANTE_INVALIDO";
	/**
   * Indica si la solicitud está completa.
   */
	  public final static String SOLICITANTE_LISTO="SOLICITANTE_LISTO";
	/**
   * Indica que la tarea se encuentra en marcha.
   */
	  public final static String SOLICITANTE_MARCHA="SOLICITANTE_MARCHA";
	/**
   * Indica que está preparando su tarea.
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
   * la ejecución.
   * @return El nombre o dirección de la computadora del usuario.
   */
  public String getDesdeNombre();
  /**
   * El número del puerto en el que el usuario ha establecido una conexión
   * para solicitar una tarea.
   * @return El número del puerto del usuario.
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
   * Obtiene la identificación parcial del solicitante.
   * @return Identificación parcial.
   */
  public String getIdParcial();
}
