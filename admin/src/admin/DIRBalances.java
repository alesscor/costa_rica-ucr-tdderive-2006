/*
 * Created on 23/07/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package admin;


import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import admin.PERSAmbiente.Ubicaciones;


import oact.OACTSirvienteAbs;
import oact.OACTSolicitud;
import tdutils.EscritorSalidas;
import tdutils.Invocable;
import tdutils.tdutils;

/**
 * <p>Title: <b>admin</b>:: admin</p>
 * <p>Description: DIRBalances.java.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCR - ECCI</p>
 * <br>@author Alessandro</br>
 * <br>@version 1.0</br>
 * <br><b>DIRBalances</b></br>
 * <br></br>
 */
/**
 * Clase abstracta que define los atributos y métodos de un
 * ente capaz de negociar un balance de carga entre varias
 * computadoras. 
 */
public abstract class DIRBalances extends OACTSirvienteAbs {
  /**
   * Conjunto de confirmaciones de nodos que esperan apoyo del
   * nodo huésped.
   */
  protected Map _brindaApoyo;
  /**
   * Conjunto de confirmaciones de nodos que confirman apoyo al
   * nodo huésped.
   */
  protected Map _recibeApoyo;
  protected Map _registro;
  /**
   * Conjunto de transferencias, teniendo como elementos instancias
   * de la clase <tt>DIRTransferencias</tt>.
   */
  protected Map _transferencias;
  private ADMINGLOInfo info;
  /**
   * Escribe mensajes de seguimiento.
   */
  protected EscritorSalidas _escritorBalanceador;
  public DIRBalances(ADMINGLOInfo info0){
    _inicio(info0);
  }
  public DIRBalances(String id0,ADMINGLOInfo info0){
    super(id0);
    _inicio(info0);
  }
  /**
   * Realiza el balance.
   * @param mensaje mensaje a procesar para el balance.
   * @throws DIRException si hubo error en el balance.
   */
  public abstract void balancea(OACTSolicitud mensaje) throws DIRException;
  /**
   * Descarga el objeto de balance y lo cierra.
   * @throws DIRException Si hay error al cerrar el balance de carga.
   */
  public abstract void close() throws DIRException;
  public void desregistraMetodo(Object obj,Method met){
    String id;
    tdutils.PortaMetodos portaI;
    synchronized(_registro){
      id=String.valueOf(obj.hashCode());
      id+="|"+met.hashCode();
      portaI=(tdutils.PortaMetodos)_registro.remove(id);
      if(portaI!=null){
        portaI.limpia();
        portaI=null;
      }
    }
  }
  public void manejaSolicitudVacia(){
    try{
      balancea(null);
    }catch(DIRException ex){
      ex.printStackTrace();
      _escritorBalanceador.escribeMensajeInfoHilo(ex.getMessage());
    }
  }
  /**
   * Abre el objeto de balance y carga la configuración.
   * @throws DIRException Si hay error al iniciar el balance de carga.
   */
  public abstract void open() throws DIRException;
  /**
   * Registra un método para su ejecución asincrónica cada vez
   * que el lector haya terminado con su trabajo.
   * <li>Se requiere que el método tenga al tipo Map como único 
   * argumento.</li>
   * <li>Cada argumento Map pasado al método contiene objetos de tipo
   * DIRTransferencias que es el compromiso de carga a repartir en las
   * demás computadoras del dominio de balance.</li>
   * @param llamada La llamada a invocar.
   * tipo Map, para transferir, brindar y recibir).
   */
  public void registraMetodo(Invocable llamada){
    String id;
    synchronized(_registro){
      id=String.valueOf(llamada.hashCode());
      _registro.put(id,llamada);
    }
  }
  /**
   * Invoca asincrónicamente los métodos registrados en el lector.
   * Este método se ejecuta de forma asincrónica luego de 
   * terminar una lectura. Cada método registrado es invocado de
   * forma asincrónica.
   * <li>Clona a transferencias, pues es muy probable su limpieza
   * mientras los hilos de invocación se activen.</li>
   */
  protected void invocaRegistrados(){
    Runnable rnn;
    Thread hilo;
    Iterator itr;
    Invocable llamadaI;
    DIRTransferencias tranI;
    final Map registrados=_registro;
    final Map trans=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    final Map brindas=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    final Map recibes=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    if(_transferencias!=null){
      itr=_transferencias.values().iterator();
      while(itr.hasNext()){
        tranI=(DIRTransferencias)itr.next();
        trans.put(tranI.Node,tranI);
      }
    }
    if(_brindaApoyo!=null){
      itr=_brindaApoyo.values().iterator();
      while(itr.hasNext()){
        tranI=(DIRTransferencias)itr.next();
        brindas.put(tranI.Node,tranI);
      }
      _brindaApoyo.clear();
    }
    if(_recibeApoyo!=null){
      itr=_recibeApoyo.values().iterator();
      while(itr.hasNext()){
        tranI=(DIRTransferencias)itr.next();
        recibes.put(tranI.Node,tranI);
      }
      _recibeApoyo.clear();
    }
    rnn=new Runnable(){
      public void run(){
        synchronized(_registro){
          Iterator itr=registrados.values().iterator();
          while(itr.hasNext()){
            Invocable portaI=(Invocable)itr.next();
            invocaMetodo(portaI,trans,brindas,recibes);
          }
        }
      }
    };
    hilo=new Thread(rnn,"DIRBalances.invoca.registrados."+this.hashCode());
    hilo.start();
  }
  /**
   * Envía mensajes de balance de carga a sus nodos vecinos.<br>
   * @param destino Nombre del nodo destino.
   * @param capacidad Capacidad del nodo actual.
   * @param carga Carga de aplicación del nodo actual.
   * @throws ADMINGLOExcepcion
   */
  public abstract void informaNodo(String destino,double capacidad, double carga) throws ADMINGLOExcepcion;
  /**
   * Envía información sobre balance de carga a nodos vecinos.<br>
   * @param capacidad Capacidad del nodo actual.
   * @param cargaAplicacion Carga de aplicación del nodo actual.
   * @throws ADMINGLOExcepcion
   */
  public abstract void informaDominio(double capacidad,
      double cargaAplicacion)  throws ADMINGLOExcepcion;
//  protected abstract void informaAlDominio(DIRBalanceMensaje mensaje) 
//  throws ADMINGLOExcepcion;  
  private void _inicio(ADMINGLOInfo info0){
    info=info0;
    _registro=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    _escritorBalanceador=new EscritorSalidas("Balanceador");
  }  
  /**
   * Invoca una pareja objeto-método presente en el registro del lector.
   * <li>Crea un clon de transferencias para el nuevo hilo.</li>
   * @param llamada Objeto de método invocable.
   * @param trans Clon del objeto transferencias.
   * @param brindas Clon del objeto brindaApoyo.
   * @param recibes Clon del objeto recibeApoyo.
   * @return Si un nuevo hilo fue ejecutado con el método.
   */
  private boolean invocaMetodo(final Invocable llamada, Map trans,
      Map brindas, Map recibes){
    Runnable rnn;
    Iterator itr;
    Thread hilo;
    DIRTransferencias tranI;
    final Map trans0=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    final Map brindas0=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    final Map recibes0=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    itr=trans.values().iterator();
    while(itr.hasNext()){
      tranI=((DIRTransferencias)itr.next()).clonese();
      trans0.put(tranI.Node,tranI);
    }
    itr=brindas.values().iterator();
    while(itr.hasNext()){
      tranI=((DIRTransferencias)itr.next()).clonese();
      brindas0.put(tranI.Node,tranI);
    }
    itr=recibes.values().iterator();
    while(itr.hasNext()){
      tranI=((DIRTransferencias)itr.next()).clonese();
      recibes0.put(tranI.Node,tranI);
    }
    rnn=new Runnable(){
      public void run(){
        try {
          llamada.invoca(new Object[]{trans0,brindas0,recibes0});
        } catch (Exception e) {
        }
      }
    };
    hilo=new Thread(rnn,
        "DIRBalances.invoca."+llamada.hashCode());
    hilo.start();
    return true;
  }
  /**
   * Se encarga de verificar si el contacto remoto es una nueva vecina de
   * la computadora actual dentro del dominio de balance.
   * @param mensaje Información de la computadora vecina.
   */
  public void revisaSiNuevaVecina(OACTSolicitud mensaje){
    DIRBalanceMensaje dirmensaje=null;
    Ubicaciones ubica=null;
    String nombre="";
    boolean nuevaUbicacion=false;
  	if(this.info.getUbicaciones()!=null){
      if(mensaje instanceof DIRBalanceMensaje){
        dirmensaje=(DIRBalanceMensaje)mensaje;
        nombre=dirmensaje.getDesdeId();
        try {
          if(this.info.getDireccion(nombre)==null){
            /* 
             * debe crearse una nueva ubicación
             */
            nuevaUbicacion=true;  
          }
        } catch (ADMINGLOExcepcion e) {
          nuevaUbicacion=true;
        }
        if(nuevaUbicacion){
          ubica=new Ubicaciones();
          ubica.nombre=dirmensaje.getDesdeId();
          this.info.getComputadora().addVecina(ubica);
        }
        /*
         * indica que un nodo se comunicó
         */
        info.recibeMensajeDeNodo(nombre);
      }
  	}else{
      // distinta solicitud otro caso a considerar.
        _escritorBalanceador.escribeMensaje("Distinta solicitud, otro caso a considerar.");
    }
  }
  /**
   * <p>Title: <b>admin</b>:: admin</p>
   * <p>Description: DIRTransferencias.java.</p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: UCR - ECCI</p>
   * <br>@author Alessandro</br>
   * <br>@version 1.0</br>
   * <br><b>DIRTransferencias</b></br>
   * <br>Porta los datos utilizados para realizar una
   * transferencia.</br>
   */

  public static class DIRTransferencias {
    public double Give=0;
    public String Node="";
    public DIRTransferencias(String sNodo,double nADar){
      Node=sNodo;
      Give=nADar;
    }
    public DIRTransferencias clonese(){
      return new DIRTransferencias(Node,Give);
    }

  }  

}
