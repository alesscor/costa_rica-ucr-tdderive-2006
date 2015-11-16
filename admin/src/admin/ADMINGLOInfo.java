/*
 * Created on 05/07/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package admin;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import oact.OACTDistribuidorBase.DireccionesVirtuales;

import orgainfo.OIDescriptor;
import orgainfo.OIExcepcion;

import admin.PERSAmbiente.Computadora;
import admin.PERSAmbiente.Ubicaciones;

/**
 * <p>Title: <b>admin</b>:: admin</p>
 * <p>Description: ADMINGLOInfo.java.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCR - ECCI</p>
 * <br>@author Alessandro</br>
 * <br>@version 1.0</br>
 */
/**
 * Contiene informaci�n global de inter�s: objetos de acceso 
 * a la base de datos, informaci�n de la computadora, informaci�n
 * de dominio de balance, etc.
 * <br></br>
 */
class ADMINGLOInfo extends OIDescriptor {
  private int _puerto=0;
  private ADMINGLOReportero reportero=null;
  /**
   * El alias local de la computadora, identifica cu�l computadora virtual
   * est� en ejecuci�n.
   */
  String alias_local="";
  /**
   * Lista de las computadoras del sistema, al menos las del dominio
   * de balance de la computadora anfitriona; referencia la lista
   * de la computadora.
   */
  private Map _ubicaciones;
  /**
   * El sirviente despachador encargado de procesar 
   * solicitudes de tareas y subtrabajos.
   */
  private ADMINAPPDespachador _despachador;
  /**
   * El puerto base para la computadora virtual.
   */
  int desplaza_puerto=0;
  /**
   * Usado para indicar fin de lectura y permitir la
   * recuperaci�n de valores del lector.
   * Hay dos casos parecidos a nivel de orgainfo en 
   * <tt>OIDescriptor</tt>.
   */
  String objeto_bloqueo_observalector="objeto_bloqueo_observalector";
  private ADMINPOLLector lector;
  private ADMINPOLPlanificador planificador;
  private DIRBalances balanceador;
  /**
   * Lista de direcciones virtuales, referencia la lista de la computadora.
   */
  private Map _direccionesVirtuales;

    
  /**
   * @param aliaslocal El alias local de la computadora. Esto se da cuando
   * se trabaja con varios servidores en una sola computadora.
   * @param controlador
   * @param url
   * @param usuario
   * @param contrasennna
   * @param constr
   * @param destr
   */
  public ADMINGLOInfo(String aliaslocal,String controlador, String url, 
      String usuario,String contrasennna, String constr, String destr) {
    super(controlador, url, usuario, contrasennna, constr, destr);
    this.alias_local=aliaslocal;
    this.lector=null;
    this.desplaza_puerto=0;
    this._direccionesVirtuales=null;    
    this._ubicaciones=null;
    this.reportero=new ADMINGLOReportero();
  }

  /**
   * Indica si est� trabajando como computadora virtual.
   * @return
   */
  boolean siComputadoraVirtual(){
    return ((Computadora)this.compu).siComputadoraVirtual();
  }
  public String getAliasLocal(){
    return alias_local;
  }
  public Computadora getComputadora(){
    return (Computadora) this.compu;
  }
  public Collection getVecinos(){
    if(this.compu!=null){
      return ((Computadora)this.compu).getVecinos().values();
    }else{
      return null;
    }
    
  }
  public double getPesoUmbralPolitica(String sNombre) throws ADMINGLOExcepcion{
    return ((Computadora)this.compu).getPesoUmbralPolitica(sNombre);
  }
  public int getDesplazaPuerto(){
    return ((Computadora)this.compu).getDesplazaPuerto();
  }
  public String getNombreComputadora(){
    return ((Computadora)this.compu).getNombre();
  }  
  public void setComputadora(Computadora compu0){
    this.compu=compu0;
    this._direccionesVirtuales=compu0.getDireccionesVirtuales();
    this._ubicaciones=compu0.getUbicaciones();
    this.desplaza_puerto=compu0.getDesplazaPuerto();
  }
  public void updateDireccionesVirtuales(){
    this._direccionesVirtuales=((Computadora)this.compu).getDireccionesVirtuales();
  }
  public void setLector(ADMINPOLLector lector0){
    if(lector0!=null){
      lector=lector0;
    }
  }
  int esperaLectura(){
    // System.out.println("Sistema en espera del lector.");
    synchronized(objeto_bloqueo_observalector){
      // System.out.println("Espera del lector terminada.");
    }
    if(lector==null){
      return 0;
    }
    return lector.getNLectura();
  }
  void setPlanificador(ADMINPOLPlanificador plan0){
    planificador=plan0;
  }
  void setBalanceador(DIRBalances balanceador0){
    balanceador=balanceador0;
  }  
  
  ADMINPOLPlanificador getPlanificador(){
    return planificador;
  }
  DIRBalances getBalanceador(){
    return balanceador;
  }
  /**
   * Devuelve la direcci�n del alias de una computadora.
   * Si la computadora es virtual, entonces devuelve su alias.
   * @param alias El alias de una computadora.
   * @return La direcci�n de la computadora o, en caso de computadora virtual,
   * su alias.
   * @throws ADMINGLOExcepcion
   */
  String getDireccion(String alias)throws ADMINGLOExcepcion {
    Ubicaciones ubicacion=null;
    ubicacion=(Ubicaciones)(this._ubicaciones.get(alias));
    if(ubicacion==null){
      throw new ADMINGLOExcepcion("No se encontr� la " +
          "direcci�n de '"+alias+"'.");
    }
    if(ubicacion.siComputadoraVirtual()){
      return ubicacion.getNombre();
    }
    return ubicacion.getDireccion();
  }
  Map getUbicaciones(){
    return this._ubicaciones;
  }
  Map getDireccionesVirtuales(){
    return this._direccionesVirtuales;
  }
  DireccionesVirtuales getUbicacion(String nombre){
    DireccionesVirtuales resultado=null;
    resultado=(DireccionesVirtuales)this._direccionesVirtuales.get(nombre);
    return resultado;
  }
  
  /**
   * @param sirvienteAPP
   */
  protected void setDespachador(ADMINAPPDespachador sirvienteAPP) {
    this._despachador=sirvienteAPP;
  }
  protected ADMINAPPDespachador getDespachador(){
    return this._despachador;
  }
  /**
   * Registra la �ltima hora en la que un nodo se ha comunicado con el 
   * servidor.
   * @param nombreNodo Nombre del nodo que se comunica.
   */
  public void recibeMensajeDeNodo(String nombreNodo){
    Map ubicaciones=this.getComputadora().getUbicaciones();
    synchronized(ubicaciones){
      Map vecinos=this.getComputadora().getVecinos();
      PERSAmbiente.Dominios_bal vecino;
      vecino=(PERSAmbiente.Dominios_bal)vecinos.get(nombreNodo);
      if(vecino!=null){
        vecino.setUltimoMensaje(System.currentTimeMillis());
        try {
          vecino.write();
          this.infobd.dbCommit();
        } catch (OIExcepcion e) {
        }
      }
    }
  }
  /**
   * Obtiene las ubicaciones de los nodos cuyo �ltimo mensaje
   * ha sido recibido luego de una hora l�mite en milisegundos.
   * @param limite Hora en milisegundos para despu�s de la cual obtener los nodos activos.
   * @return El mapa de nodos que est�n activos luego de tal hora.
   */
  public Map getNodosReportados(long limite){
    Map ubicaciones=this.getComputadora().getUbicaciones();
    Map reportados=new TreeMap();
    PERSAmbiente.Ubicaciones ubicacion=null;
    synchronized(ubicaciones){
      Map dominios=this.getComputadora().getVecinos();
      PERSAmbiente.Dominios_bal vecino;
      Iterator itr=dominios.values().iterator();
      while(itr.hasNext()){
        vecino=(PERSAmbiente.Dominios_bal)itr.next();
        if(vecino.getUltimoMensaje()>=limite){
          ubicacion=(PERSAmbiente.Ubicaciones)ubicaciones.get(vecino.getVecino());
          if(ubicacion!=null){
            reportados.put(ubicacion.getNombre(),ubicacion);
            System.out.println("Pone en planificaci�n a "+vecino.getVecino());
          }
        }else{
          System.out.println("Saca de planificaci�n a "+vecino.getVecino());
        }
      }
    }
    return reportados;
  }
  /**
   * Agrega una entrada a la bit�cora con el siguiente formato:<br/>
   * <code>yyyy/mm/dd;hh:mm:ss:mil;objeto;texto\n</code><br/>
   * Por ahora los objetos son los siguientes:<br/>
   * <li>programa</li>
   * <li>despachador</li>
   * <li>controlador</li>
   * <li>planificador</li>
   * <li>lector</li>
   * @param objeto Nombre del objeto que se quiere describir.
   * @param texto Un evento descrito en el que interviene el objeto.
   */
  public void println(String objeto,String texto){
    objeto=this.getAliasLocal()+"("+objeto+")";
    reportero.println(objeto,texto);
  }
  public void actualizaInfoCompu(){
    PERSAmbiente.Ubicaciones infocompu=null;
    infocompu=(PERSAmbiente.Ubicaciones)this._ubicaciones.get(this.alias_local);
    Computadora compu0=(Computadora)this.compu;
    if(infocompu!=null){
      this.desplaza_puerto=infocompu.desplaza_puerto;
      compu0.setNombre(this.alias_local);
      compu0.setDesplazaPuerto(infocompu.desplaza_puerto);
      try {
        compu0.loadVecinos(this.getConex().ubicaciones_url);
      } catch (ADMINGLOExcepcion e) {
        e.printStackTrace();
      } catch (OIExcepcion e) {
        e.printStackTrace();
      }
    }
  }
  void setPuerto(int puerto){
    this._puerto=puerto;
  }
  public int getPuerto(){
    return this._puerto;
  }
}
