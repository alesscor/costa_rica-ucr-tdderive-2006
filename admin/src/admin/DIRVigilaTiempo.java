/*
 * Created on 24/07/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package admin;

import tdutils.tdutils;

/**
 * <p>Title: <b>admin</b>:: admin</p>
 * <p>Description: DIRVigilaTiempo.java.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCR - ECCI</p>
 * <br>@author Alessandro</br>
 * <br>@version 1.0</br>
 * <br><b>DIRVigilaTiempo</b></br>
 * <br></br>
 */
/**
 * Estructura para monitorear los tiempos entre
 * cada recepción de mensaje. 
 */
class DIRVigilaTiempo {
  public String nodoid;
  public long ultimo_instante;
  public long este_instante;
  public long diferencia;
  public DIRVigilaTiempo() {
    nodoid="";
    ultimo_instante=0;
    este_instante=0;
    diferencia=0;
  }
  public DIRVigilaTiempo(String nodoid0) {
    nodoid=nodoid0;
    ultimo_instante=0;
    este_instante=0;
    diferencia=0;
    setInstante();
  }
  /**
   * Asigna el instante en milisegundos desde de la medianoche del
   * primero de enero de 1970 hasta el instante de la invocación.
   */
  public void setInstante(){
    ultimo_instante=este_instante;
    este_instante=tdutils.getCurrentTime();
    if(ultimo_instante!=0){
      diferencia = este_instante - ultimo_instante;
    }else{
      diferencia=0;
    }
  }
  public long getUltimoInstante(){
    return ultimo_instante;
  }
  public long getEsteInstante(){
    return este_instante;
  }
  public long getDiferencia(){
    return diferencia;
  }
  public String getString(){
    String res="";
    res=nodoid+":"+ultimo_instante+":"+este_instante+":"+diferencia;
    return res;
  }
}
