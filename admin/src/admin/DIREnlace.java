/*
 * Created on 23/07/2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package admin;

import org.w3c.dom.Node;

import mens.MENSMensaje;

/**
 * <p>Title: <b>admin</b>:: admin</p>
 * <p>Description: DIREnlace.java.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCR - ECCI</p>
 * <br>@author Alessandro</br>
 * <br>@version 1.0</br>
 */
/**
 * Información sobre un enlace en las operaciones
 * del balance. 
 */
public class DIREnlace extends MENSMensaje {
  /**
   * Identificación del primer nodo.
   */
  public String nodo1id;
  /**
   * Identificación del segundo nodo.
   */
  public String nodo2id;
  /**
   * Datos sobre el nodo.
   */
  public String datos;
  /**
   * Indica cuál nodo es el que ve el enlace.
   */
  private String estenodoid;
  /**
   * Indica cuál es el otro nodo.
   */
  private String otronodoid;
  /**
   * Indica el tiempo de espera de mensajes entre un nodo y otro.
   */
  public int tespera;
  private boolean _isset_nodo1id;
  private boolean _isset_nodo2id;
  private boolean _isset_datos;
  private boolean _isset_tespera;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public DIREnlace() {
    estenodoid="";
    otronodoid="";
    nodo1id="";
    nodo2id="";
    datos="";
    tespera=-1;
    _isset_nodo1id=false;
    _isset_nodo2id=false;
    _isset_datos=false;
    _isset_tespera=false;
  }
  public DIREnlace(String nodo1id0,String nodo2id0,String datos0,int tespera0) {
    estenodoid="";
    otronodoid="";
    setNodo1Id(nodo1id0);
    setNodo2Id(nodo2id0);
    setDatos(datos0);
    setTEspera(tespera0);
  }
  public DIREnlace(String nodo1id0,String nodo2id0,String datos0) {
    estenodoid="";
    otronodoid="";
    setNodo1Id(nodo1id0);
    setNodo2Id(nodo2id0);
    setDatos(datos0);
  }
  /**
   * Asigna la identificación del primer nodo. Si el segundo nodo
   * está asignado, forza a que el nodo de menor valor quede
   * como primer nodo.
   * @param nodo1id0 Identificación del primer nodo.
   */
  public final void setNodo1Id(String nodo1id0){
    if(nodo1id0==""){
      return;
    }
    nodo1id=nodo1id0;
    _isset_nodo1id=true;
    if(_isset_nodo2id){
      if(nodo1id.compareToIgnoreCase(nodo2id)>0){
        nodo1id=nodo2id;
        nodo2id=nodo1id0;
      }
    }
  }
  /**
   * Asigna la identificación del segundo nodo. Si el primer nodo
   * está asignado, forza a que el nodo de mayor valor quede
   * como segundo nodo.
   * @param nodo2id0 Identificación del primer nodo.
   */
  public final void setNodo2Id(String nodo2id0){
    if(nodo2id0==""){
      return;
    }
    nodo2id=nodo2id0;
    _isset_nodo2id=true;
    if(_isset_nodo1id){
      if(nodo1id.compareToIgnoreCase(nodo2id)>0){
        nodo2id=nodo1id;
        nodo1id=nodo2id0;
      }
    }
  }
  /**
   * Asigna los datos del enlace.
   * @param datos0 Datos del enlace.
   */
  public final void setDatos(String datos0){
    if(datos0==""){
      return;
    }
    datos=datos0;
    _isset_datos=true;
  }
  public final String  getNodo1Id(){
    return nodo1id;
  }
  public final String  getNodo2Id(){
    return nodo2id;
  }
  public final String  getDatos(){
    return datos;
  }
  /**
   * Devuelve la identificación del enlace, formada
   * por la identificación del primer nodo, seguida por
   * dos puntos y luego seguida por la identificación
   * del segundo nodo.
   * @return Identificación del enlace.
   */
  public final String getEnlaceId(){
    if(_isset_nodo1id &&_isset_nodo2id){
      return nodo1id + ":" + nodo2id;
    }else{
      return "";
    }
  }
  public String getString(){
    return "<enlace>"+getEnlaceId()+":"+getDatos()+":"+this.getTEspera()+"</enlace>";
  }
  public int getTEspera(){
    return tespera;
  }
  public void setTEspera(int tespera0){
    if(tespera0>=0){
      tespera = tespera0;
      _isset_tespera=true;
    }
  }
  //////////////////////////////////////////////////////////////////////
  public boolean isVacio() {
    return (!_isset_nodo1id) || (!_isset_nodo2id);
  }
  protected String getXMLContainedElements() {
    String xml="";
    xml+="<enlace>";
    if(_isset_nodo1id){
      xml+="\n  <nodo1id>"+nodo1id+"</nodo1id>";
    }
    if(_isset_nodo2id){
      xml+="\n  <nodo2id>"+nodo2id+"</nodo2id>";
    }
    if(_isset_tespera){
      xml+="\n  <espera>"+tespera+"</espera>";
    }
    if(_isset_datos){
      xml+="\n  <datos>"+datos+"</datos>";
    }
    xml+="\n</enlace>";
    return xml;
  }
  protected void setContentFromDoc(Node parm1, int[] parm2, String[] parm3) {
    // parm1=this.getDocumento();
    setNodo1Id(getElementText(parm1,"nodo1id"));
    setNodo2Id(getElementText(parm1,"nodo2id"));
    setDatos(getElementText(parm1,"datos"));
    try {
      setTEspera(Integer.parseInt(getElementText(parm1, "espera")));
    }
    catch (NumberFormatException ex) {
      setTEspera(0);
    }
  }
  protected void toleraXML(int[] parm1, String[] parm2) {
    if(_isset_nodo1id && _isset_nodo2id){
      parm1[0]=0;
      parm2[0]="Bien en lectura de enlace.";
    }else{
      parm1[0]=3;
      parm2[0]="Error en lectura de enlace.";
    }
  }
  public final void setEsteNodo(String estenodo0) throws DIRException{
    if(_isset_nodo1id && _isset_nodo2id){
      if(estenodo0.compareToIgnoreCase(nodo1id)==0){
        estenodoid=nodo1id;
        otronodoid=nodo2id;
      }else{
        if(estenodo0.compareToIgnoreCase(nodo2id)==0){
          estenodoid=nodo2id;
          otronodoid=nodo1id;
        }else{
          throw new DIRException(
              "Error en la asignación del nodo \"dueño\" del enlace.");
        }
      }
    }else{
      throw new DIRException("No han sido asignados nodos al enlace.");
    }
  }
  public final String getEsteNodo(){
    return estenodoid;
  }
  public final String getOtroNodo(){
    return otronodoid;
  }
  public DIREnlace clona(){
    return new DIREnlace(nodo1id,nodo2id,datos,tespera);
  }

}
