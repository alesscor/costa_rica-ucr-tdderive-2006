/*
 * Created on 23/07/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package admin;

/**
 * <p>Title: <b>admin</b>:: admin</p>
 * <p>Description: DIRTrigger.java.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCR - ECCI</p>
 * <br>@author Alessandro</br>
 * <br>@version 1.0</br>
 */
/**
 * Valora si se debe disturbiar el subsistema
 * de planificación por eventos relevantes en
 * un dominio de balance. Totalmente ligado al
 * balance de carga hidrodinámico de Hui y Chanson.
 */
public interface DIRTrigger {
  public boolean Trigger(double nIValue,double nJValue);
}
