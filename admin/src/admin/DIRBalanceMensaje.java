/*
 * Created on 24/07/2004
 *
 */
package admin;
import org.w3c.dom.Node;

import admin.DIRBalances.DIRTransferencias;
import admin.DIRHidrodinamico.DIRInfoNodo;
import oact.OACTSirvienteAbs;
import oact.OACTSolicitud;

/**
 * <p>Title: <b>admin</b>:: admin</p>
 * <p>Description: DIRBalanceMensaje.java.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCR - ECCI</p>
 * <br>@author Alessandro</br>
 * <br>@version 1.0</br>
 */
/**
 * Define el mensaje usado en el mecanismo de balance. 
 */
public class DIRBalanceMensaje extends OACTSolicitud {
  boolean _isset_altura;
  boolean _isset_capacidad;
  boolean _isset_carga;
  boolean _isset_dar;
  boolean _isset_desdeid;
  boolean _isset_paraid;
  boolean _isset_tipo;
  double altura;
  double capacidad;
  double carga;
  double dar;
  /**
   * Indica de dónde es el mensaje.
   */
  String desdeid;
  /**
   * Indica hacia quién va el mensaje. Para fines ilustrativos de inventario.
   */
  String paraid;
  /**
   * Indica el tipo del mensaje: "B", "G", "R", ó "D".
   */
  String tipo;
  public DIRBalanceMensaje(){
    super("dirhidrodinamico");
    _init("","","",0,-1,-1,-1);
  }
  /**
   * @param sirviente0
   */
  public DIRBalanceMensaje(OACTSirvienteAbs sirviente0) {
    super(sirviente0);
  }
  /**
   * @param servantID
   */
  public DIRBalanceMensaje(String servantID) {
    super(servantID);
  }
  public DIRBalanceMensaje(String desdeid0,String paraid0,  String tipo0,  
      double capacidad0,double carga0,  double dar0,  double altura0) {
    super("dirhidrodinamico");
    _init(desdeid0,paraid0,tipo0,capacidad0,carga0,dar0,altura0);
  }
  public DIRBalanceMensaje(String desdeid0, String tipo0,  double capacidad0,
      double carga0,  double dar0,  double altura0) {
    super("dirhidrodinamico");
    _init(desdeid0,"(nadie en especial)",tipo0,capacidad0,carga0,dar0,altura0);
}  
  public OACTSolicitud ejecutar(){
    DIRHidrodinamico res;
    if(this.getSirviente()!=null){
      try {
        ((DIRHidrodinamico)this.getSirviente()).balancea(this);
      } catch (DIRException e) {
        e.printStackTrace();
      }
    }else{
      // System.err.println("sin sirviente de balance");
      // OK cuando es solo para enviar
    }
    return null;
  }
  public double getAltura(){
    return altura;
  }
  public double getCapacidad(){
    return capacidad;
  }
  public double getCarga(){
    return carga;
  }
  public double getDar(){
    return dar;
  }
  public String getDesdeId(){
    return desdeid;
  }
  public DIRInfoNodo getInfoNodo(){
    return new DIRInfoNodo(desdeid,capacidad,carga);
  }
  public String getParaId(){
    return paraid;
  }
  public String getTipo(){
    return tipo;
  }
  public DIRTransferencias getTransfer(){
    return new DIRTransferencias(desdeid,dar);
  }
  public boolean isPreparado(){
    return true;
  }
  public boolean isVacio() {
    return (desdeid==null) || (desdeid=="");
  }
  public void setAltura(double altura0){
    if (altura0<0){
      return;
    }
    altura=altura0;
    _isset_altura=true;
  }
  public void setCapacidad(double capacidad0){
    if (capacidad0==0){
      return;
    }
    capacidad=capacidad0;
    _isset_capacidad=true;
  }
  public void setCarga(double carga0){
    if (carga0<0){
      return;
    }
    carga=carga0;
    _isset_carga=true;
  }
  public void setDar(double dar0){
    dar=dar0;
    _isset_dar=true;
  }
  public void setDesdeId(String desdeid0){
    if (desdeid0==""){
      return;
    }
    desdeid=desdeid0;
    _isset_desdeid=true;
  }
  public void setParaId(String paraid0){
    if (paraid0==""){
      return;
    }
    paraid=paraid0;
    _isset_paraid=true;
  }
  public void setTipo(String tipo0){
    if (tipo0==""){
      return;
    }
    tipo=tipo0;
    _isset_tipo=true;
  }
  protected String getXMLContainedElements() {
    String xml="";
    xml+="<hidromensaje>\n";
    xml+="  <desdeid>"+desdeid+"</desdeid>\n";
    xml+="  <tipo>"+tipo+"</tipo>\n";
    if(_isset_paraid){
      xml+="  <paraid>"+paraid+"</paraid>\n";
    }
    if(_isset_capacidad){
      xml+="  <capacidad>"+capacidad+"</capacidad>\n";
    }
    if(_isset_carga){
      xml+="  <carga>"+carga+"</carga>\n";
    }
    if(_isset_altura){
      xml+="  <altura>"+altura+"</altura>\n";
    }
    if(_isset_dar){
      xml+="  <dar>"+dar+"</dar>\n";
    }
    xml+="</hidromensaje>";
    return xml;
  }
  protected void setContentFromDoc(Node parm1, int[] parm2, String[] parm3) {
    parm1=this.getDocumento();
    setDesdeId(getElementText(parm1,"desdeid"));
    setParaId(getElementText(parm1,"paraid"));
    setTipo(getElementText(parm1,"tipo"));
    try {
      setCapacidad(Double.parseDouble(getElementText(parm1, "capacidad")));
    }
    catch (NumberFormatException ex) {
      setCapacidad(0);
    }
    try {
      setCarga(Double.parseDouble(getElementText(parm1, "carga")));
    }
    catch (NumberFormatException ex1) {
      setCarga(0);
    }
    try {
      setAltura(Double.parseDouble(getElementText(parm1, "altura")));
    }
    catch (NumberFormatException ex2) {
      setAltura(-1);
    }
    try {
      setDar(Double.parseDouble(getElementText(parm1, "dar")));
    }
    catch (NumberFormatException ex3) {
      setDar(-1);
    }
  }
  protected void toleraXML(int[] parm1, String[] parm2) {
    if(_isset_desdeid && _isset_tipo){
      parm1[0]=0;
      parm2[0]="Bien en lectura de hidromensaje.";
    }else{
      parm1[0]=3;
      parm2[0]="Error en lectura de hidromensaje.";
    }
  }
  private void _init(String desdeid0,String paraid0,  String tipo0,  double capacidad0,
                         double carga0,  double dar0,  double altura0){
        _isset_desdeid=false;
        _isset_paraid=false;
        _isset_tipo=false;
        _isset_capacidad=false;
        _isset_carga=false;
        _isset_dar=false;
        _isset_altura=false;
        setDesdeId(desdeid0);
        setParaId(paraid0);
        setTipo(tipo0);
        setCapacidad(capacidad0);
        setCarga(carga0);
        setDar(dar0);
        setAltura(altura0);
  }
}
