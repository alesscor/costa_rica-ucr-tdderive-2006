/*
 * Created on 23/07/2004
 */
package admin;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import oact.OACTSolicitud;
import admin.PERSAmbiente.Computadora;
import admin.PERSAmbiente.Dominios_bal;
import admin.PERSAmbiente.Ubicaciones;
import tables.AbstractSet;
import tdutils.EscritorSalidas;

/**
 * Extiende una estrategia de balance de carga basada en
 * un enfoque hidrodinámico que busca que toda computadora
 * de un dominio de balance tenga la misma carga que las demás
 * según su capacidad. Se sigue la propuesta del balance de
 * carga hidrodinámico de Hui y Chanson.
 */
public class DIRHidrodinamico extends DIRBalances {
  /**
   * Conjunto de procesamiento de trabajo a dar.
   */
  private AbstractSet _aDar;
  /**
   * A borrar. Ya no se usará una cola de mensajes global.
   */
  private AbstractSet asGlobalMessages;
  /**
   * Usado para esperar el <tt>open</tt> y el <tt>HeteroLB</tt>.
   * Hay dos casos parecidos a nivel de orgainfo en 
   * <tt>OIDescriptor</tt> y otro de más alto nivel en
   * <tt>ADMINGLOInfo</tt>.
   */
  String objeto_bloqueo_hidro="objeto_bloqueo_hidro";  
  /**
   * Utilizada en HeteroLB.
   */
  boolean bTerminar=false;
  /**
   * Cantidad de instancias corriendo el HeteroLB.
   * Un hilo incrementa el valor de nEnMarcha al iniciar el
   * HeteroLB y lo decrementa al salir de éste.
   * Si un hilo se encuentra un valor de uno, entonces tiene
   * que cumplir con los pasos 11.c y 11.d.
   */
  private int nHilosEnMarcha=0;
  private ADMINGLOInfo desc;
  private EscritorSalidas _escritorHidrodinamico;
  private boolean esperaConfirmacionDeme;
  private boolean esperaConfirmacionReciba;
  /**
   * Información de nodos recopilada proveniente de la red.
   */
  private AbstractSet _infoNodosRecopilada;
  /**
   * Mensajes. A borrar.
   */
  private AbstractSet mensajes;
  /**
   * Conjunto de mensajes enviados.
   */
  private AbstractSet mensajesEnviados;
  /**
   * Información del nodo de la computadora anfitriona (el actual).
   */
  private Dominios_bal _nodoAnfitrion;
  /**
   * Lista de nodos vecinos al nodo nodoNI.
   */
  private String nodosVecinos[];
  private DIRTrigger reaccion;
  /**
   * Registra los tiempos de llegada de los mensajes
   * y la diferencia con el tiempo de llegada anterior,
   * tomando como referencia al nodo emisor del mensaje.
   */
  private AbstractSet tiemposDiferencia;

  
  private Map _vecinillos;
  final static int ADYACENTES_MAXIMO=100;
  final static String MENSAJES_BROADCAST="B";
  final static String MENSAJES_DISTURB="D";
  final static String MENSAJES_GIVE="G";
  final static int MENSAJES_MAXIMO=200;
  final static String MENSAJES_RECEIVE="R";
  /**
   * Constructor del sirviente del balance de carga hidrodinámico.
   * @param desc0 Información básica de tdderive.
   */
  public DIRHidrodinamico(ADMINGLOInfo desc0) {
    super(desc0);
    super.setId("dirhidrodinamico");
    desc=desc0;
    double umbral_alfa;
    try {
      umbral_alfa=desc.getPesoUmbralPolitica("PLANIF_UMBRAL_ALFA");
    } catch (ADMINGLOExcepcion e) {
      umbral_alfa=0.5;
    }
    final double umbralff=umbral_alfa;
    reaccion=new DIRTrigger(){
      public boolean Trigger(double nIValue,double nJValue){
        return nJValue-nIValue>umbralff;
      }
    };
    esperaConfirmacionDeme=false;
    esperaConfirmacionReciba=false;
    tiemposDiferencia=null;
    _escritorHidrodinamico=new EscritorSalidas("Hidrodinamico");
  }
  /**
   * Balance hidrodinámico concreto.
   */
  public void balancea(OACTSolicitud mensaje)  throws DIRException{
    this.revisaSiNuevaVecina(mensaje);    
    _escritorBalanceador.escribeMensajeInfoHilo("Previo llamada balancea.entrada",true);
    this.HeteroLB((DIRBalanceMensaje)mensaje);
    _escritorBalanceador.escribeMensajeInfoHilo("Fin llamada balancea.entrada",true);
  }

  public void close()  throws DIRException{
  }
  /**
   * Realiza los primeros 4 pasos de HeteroLB de Chui y Chanson.
   * @throws DIRException
   */
  public void open() throws DIRException{
    String sNombre="";
    Iterator itr;
    Dominios_bal domI;
    int lectura=0;
    _escritorBalanceador.escribeMensaje("Previo bloqueo 1 open.entrada");
    synchronized(objeto_bloqueo_hidro){
      _escritorBalanceador.escribeMensaje("Fin bloqueo 1 open.entrada");
      while(desc.esperaLectura()<1){
        _escritorBalanceador.escribeMensaje("Dentro while bloqueo 2 open.while");
        // espera hasta que el lector haya leído.
        _escritorBalanceador.escribeMensaje("Espera #"+(lectura++));
      }
      _escritorBalanceador.escribeMensaje("Fin while bloqueo 2 open.while");
      //|
      //| información de vecinos
      //|
      _vecinillos=((Computadora)this.desc.getComputadora()).getVecinos();
      if(_vecinillos==null||_vecinillos.size()==0){
        throw new DIRException("No se han encontrado nodos vecinos.");
      }
      //|
      //| información de la computadora huesped
      //|
      sNombre=((Computadora)this.desc.getComputadora()).getNombre();
      _nodoAnfitrion=new Dominios_bal();
      _nodoAnfitrion.setNombre(sNombre);
      _nodoAnfitrion.setCapacidad(
          ((Computadora)this.desc.getComputadora()).getCapacidad());
      _nodoAnfitrion.setCargaAplicacion(
          ((Computadora)this.desc.getComputadora()).getCargaAplicacion());
      if(_nodoAnfitrion.getCapacidad()!=0.0){
        _nodoAnfitrion.setAltura(
            _nodoAnfitrion.getCargaAplicacion()/_nodoAnfitrion.getCapacidad());
      }
      /**
       * inicia la lista de diferencias de tiempos en la recepción
       * de mensajes.
       */
      tiemposDiferencia=new AbstractSet(new DIRVigilaTiempo());
      //|
      //| Pasos tomados del HeteroLB de Chui y Chanson.
      //|
// 00
      int i,j;
      if(_infoNodosRecopilada==null){
        _infoNodosRecopilada=new AbstractSet(
            ADYACENTES_MAXIMO+1,new DIRInfoNodo("",0,0 ),true,true);
        // + 1 porque se incluye el nodo i
      }else{
        _infoNodosRecopilada.clean();
      }
      if(_aDar==null){
        _aDar= new AbstractSet(ADYACENTES_MAXIMO,
                              new DIRTransferencias("",0),true,false);
      }else{
        _aDar.clean();
      }
// 01
      try{
        this.informaDominio0(new DIRBalanceMensaje(_nodoAnfitrion.nombre,
            MENSAJES_BROADCAST,_nodoAnfitrion.getCapacidad(),
            _nodoAnfitrion.getCargaAplicacion(),0,0));
      }catch(Exception ex){
        _escritorBalanceador.escribeMensajeInfoHilo("Error al informar al dominio");
        ex.printStackTrace();
      }
// 02
      bTerminar=false;
// 03
      _infoNodosRecopilada.addNew(new DIRInfoNodo(_nodoAnfitrion.nombre,
                            _nodoAnfitrion.getCapacidad(),_nodoAnfitrion.getCargaAplicacion()));
// 04
      itr=_vecinillos.values().iterator();
      while(itr.hasNext()){
        domI=(Dominios_bal)itr.next();
        _aDar.addNew(new DIRTransferencias(
            domI.getVecino(),0));      
      }
    } // del synchronized
    _escritorBalanceador.escribeMensaje("Fin bloqueo 1 open.salida");
  }

  protected void informaDominio0(DIRBalanceMensaje mensaje) throws 
  ADMINGLOExcepcion{
    Iterator itr;
    Dominios_bal domI;
    DIRBalanceMensaje mensajeclon;
    itr=_vecinillos.values().iterator();
    while(itr.hasNext()){
      domI=(Dominios_bal)itr.next();
      try{
        mensajeclon=new DIRBalanceMensaje(_nodoAnfitrion.getNombre(),
            domI.getVecino(),
            mensaje.getTipo(),mensaje.getCapacidad(),
            mensaje.getCarga(),mensaje.getDar(),mensaje.getAltura());
        this.informaNodo0(mensajeclon);
      }catch(Exception ex){
        _escritorBalanceador.escribeMensajeInfoHilo("No se pudo enviar mensaje a " +
            "'"+domI.getVecino()+"'.");
      }
    }
  }
  public void informaNodo(String destino,double capacidad, double carga) throws ADMINGLOExcepcion{
    Iterator itr;
    Dominios_bal domI;
    DIRBalanceMensaje mensaje;
    mensaje=new DIRBalanceMensaje(_nodoAnfitrion.getNombre(),destino,
        MENSAJES_BROADCAST,capacidad,
        carga,0,0);
    this.informaNodo0(mensaje);
  }
  public void informaDominio(double capacidad, double carga) throws ADMINGLOExcepcion{
    Iterator itr;
    Dominios_bal domI;
    DIRBalanceMensaje mensaje;
    mensaje=new DIRBalanceMensaje(_nodoAnfitrion.getNombre(),
        MENSAJES_BROADCAST,capacidad,
        carga,0,0);
    this.informaDominio0(mensaje);
  }
  
  
  /**
   * Los primeros 4 pasos se realizan en el método open().<br>
   * - Se asigna tiempo de espera cuando se envía el mensaje "G" (deme) y
   * se espera la recepción de un mensaje "R" (recepción).<br>
   * - Se asigna tiempo de espera cuando se envía el mensaje "R" (recepción) y
   * se espera la recepción de un mensaje "B" (presentación).
   * @param mensaje Mensaje de balance de carga a procesar. Si su
   * valor es null es porque el tiempo de espera venció y debe llevarse
   * a cabo el manejo del vencimiento.
   */
  protected final void HeteroLB(DIRBalanceMensaje mensaje){
    int i = 0, j = 0;
    boolean bChangeWorkLoad;
    boolean bChangeCapacity;
    Iterator itr;
    DIRInfoNodo niInfo = null;
    DIRTransferencias tnrTrans = null;
    String aNode[] = new String[1];
    double aCapacity[] = new double[1],
        aWorkLoad[] = new double[1],
        niInfoMigración[] = new double[1],
        aHeight[] = new double[1];
    double nHeightNueva = 0;
    char aTypeMessage[] = new char[1];
    double nR = 0;
    DIRVigilaTiempo tiempo=null;
    int tiempoidx=-1;
    boolean bEventosImportantes=false;
    _escritorBalanceador.escribeMensajeInfoHilo("Previo bloqueo 1 HeteroLB.entrada.");
    synchronized(objeto_bloqueo_hidro){
      _escritorBalanceador.escribeMensajeInfoHilo("Fin bloqueo 1 HeteroLB.entrada.");
      /*
       * da a conocer a los demás hilos su presencia en el sistema
       */
      nHilosEnMarcha++;
    } // del synchronized 1
    _escritorBalanceador.escribeMensajeInfoHilo("Fin bloqueo 1 HeteroLB.salida.");
    _escritorBalanceador.escribeMensajeInfoHilo("Previo bloqueo 2 HeteroLB.entrada.");
    synchronized(objeto_bloqueo_hidro){
      _escritorBalanceador.escribeMensajeInfoHilo("Fin bloqueo 2 HeteroLB.entrada.");
// 05
      /*
       * carga el mensaje. Se supone que el mensaje ha sido el leído como parte
       * del sistema de balance dinámico de carga.
       */
      if ((mensaje == null)) {
        /*------------------ time out vencido -------------------
         * se supone que el tiempo de vencimiento terminó.
         *
         */
        if((_aDar.getCount()>0) && (_aDar.getTotal("Give")>0)){
          /*
           * estaba esperando mensajes "R" que no llegaron a tiempo. Como
           * no llegaron entonces por retraso son eliminados
           */
          DIRTransferencias trans;
          _aDar.moveFirst();
          while(!_aDar.getEoF()){
            trans=(DIRTransferencias)_aDar.getObject();
            trans.Give=0.0;
            _aDar.moveNext();
          }
          _aDar.moveFirst();
        }
        _escritorBalanceador.escribeMensajeInfoHilo("time out vencido.");
        return;
        /*------------------------------------------------------------*/
      }
      if(mensaje.getTipo().compareToIgnoreCase("R")==0 &&
         (_aDar.getCount()>0) && (_aDar.getTotal("Give")==0.0) ){
        /*------------------ el mensaje llegó tarde -------------------
         * se supone que el tiempo de vencimiento de estos nodos
         * para este tipo de mensaje ya terminó.
         */
        _escritorBalanceador.escribeMensajeInfoHilo("mensaje atrasado.");
        return;
        /*------------------------------------------------------------*/
      }
      aNode[0] = mensaje.getDesdeId();
      aTypeMessage[0] = mensaje.tipo.charAt(0);
      aCapacity[0] = mensaje.getCapacidad();
      aWorkLoad[0] = mensaje.getCarga();
      niInfoMigración[0] = mensaje.dar;
      aHeight[0] = mensaje.getAltura();
      if (aNode[0].compareTo("") == 0) {
        return;
      }
      if ((tiemposDiferencia.getCount()>0)&&
          ((tiempoidx=tiemposDiferencia.findFirst("nodoid°=°"+aNode[0]+"°"))>=0)){
        tiemposDiferencia.moveTo(tiempoidx);
        tiempo=(DIRVigilaTiempo)tiemposDiferencia.getObject();
        tiempo.setInstante();
        /*
         * TODO Revisar diferencia de tiempos para descartar o no el mensaje 
         * recibido y llevar a cabo las acciones pertinentes.
         */
        _escritorHidrodinamico.escribeMensajeInfoHilo((tiemposDiferencia.getContenido("tiempos de diferencia en el nodo "+
            _nodoAnfitrion.nombre)));
      }else{
        tiemposDiferencia.addNew(tiempo=(new DIRVigilaTiempo(aNode[0])));
        tiempo.setInstante();
      }
      // revisa si hay nuevos nodos adyacentes o si alguno ha sido eliminado.
      /*
       * (1) en caso de que existan cambios, deben actualizarse las siguientes
       *     estructuras de EH_HeteroLB():
       *        nodosVecinos (String[]), tamaño y constitución
       *        infoNodosRecopilada, con info de solamente el nodo this
       *        aDar, valores en cero
       *        (por mí, que se ejecute la inicialización y los pasos
       *        00,01,02,03 y 04)
       * (3) ta bueno lo anterior pero deben detectarse los nodos adyacentes
       *     nuevos.
       * (2) como anteriormente se dijo, mandar broadcastas a los nodos
       *     adyacentes nuevos.
       */
      // valores iniciales para detectar cambios en medidas del nodo i
      bChangeWorkLoad = false;
      bChangeCapacity = false;
      switch (aTypeMessage[0]) {
// 06
        case 'D': {
          /*
           * incrementan medidas del Node i
           */
          niInfo = null;
          niInfo = new DIRInfoNodo();
          this._nodoAnfitrion.setCapacidad(this._nodoAnfitrion.getCapacidad() + aCapacity[0]);
          this._nodoAnfitrion.setCargaAplicacion(this._nodoAnfitrion.getCargaAplicacion() + 
              aWorkLoad[0]);
          niInfo.Node = aNode[0];
          niInfo.Capacity = aCapacity[0];
          niInfo.Load = aWorkLoad[0];
          niInfo.Height = niInfo.Load / niInfo.Capacity;
          _escritorHidrodinamico.escribeMensajeInfoHilo(_infoNodosRecopilada.getContenido("info de " + this._nodoAnfitrion.nombre));
          if (_infoNodosRecopilada.update(niInfo,
              "Node°=°" + aNode[0] + "°")) {
            // ya actualizó la información del Node f
          }
          else {
            _infoNodosRecopilada.addNew(niInfo);
          }
          _escritorHidrodinamico.escribeMensajeInfoHilo(_infoNodosRecopilada.getContenido("info de " + this._nodoAnfitrion.nombre));
          // hubo cambio en medidas del Node i
          bChangeWorkLoad = true;
          bChangeCapacity = true;
          break;
        }
// 07
        case 'B': {
        	_escritorHidrodinamico.escribeMensajeInfoHilo(_infoNodosRecopilada.getContenido("info de " + this._nodoAnfitrion.nombre));
          if (_infoNodosRecopilada.update(new DIRInfoNodo(aNode[0],
              aCapacity[0],
              aWorkLoad[0]), "Node°=°" + aNode[0] + "°")) {
            // ya actualizó la información del Node f
          }
          else {
            _infoNodosRecopilada.addNew(new DIRInfoNodo(aNode[0],
                aCapacity[0], aWorkLoad[0]));
          }
          _escritorHidrodinamico.escribeMensajeInfoHilo(_infoNodosRecopilada.getContenido("info de " + this._nodoAnfitrion.nombre));
          break;
        }
// 08
        case 'G': {
          // saca el mínimo de f.Height x i.Capacity y f.infomigración
          if (aHeight[0] * this._nodoAnfitrion.getCapacidad() - 
                this._nodoAnfitrion.getCargaAplicacion() < niInfoMigración[0]) {
            nR = aHeight[0] * this._nodoAnfitrion.getCapacidad() - 
                this._nodoAnfitrion.getCargaAplicacion();
          }
          else {
            nR = niInfoMigración[0];
          }
          if (nR < 0) {
            nR = 0;
          }
          // modifica info sobre Node i
          this._nodoAnfitrion.setCargaAplicacion(this._nodoAnfitrion.getCargaAplicacion() + nR);
          niInfo = null;
          niInfo = new DIRInfoNodo();
          niInfo.Node = this._nodoAnfitrion.nombre;
          niInfo.Capacity = this._nodoAnfitrion.getCapacidad();
          niInfo.Load = this._nodoAnfitrion.getCargaAplicacion();
          niInfo.Height = niInfo.Load / niInfo.Capacity;
          _escritorHidrodinamico.escribeMensajeInfoHilo(_infoNodosRecopilada.getContenido("info de " + this._nodoAnfitrion.nombre));
          _infoNodosRecopilada.update(niInfo,
                                     "Node°=°" + niInfo.Node + "°");
          _escritorHidrodinamico.escribeMensajeInfoHilo(_infoNodosRecopilada.getContenido("info de " + this._nodoAnfitrion.nombre));
          // modifica info sobre Node f
          niInfo = null;
          niInfo = new DIRInfoNodo();
          niInfo.Node = aNode[0];
          niInfo.Capacity = aCapacity[0];
          niInfo.Load = aWorkLoad[0];
          niInfo.Height = niInfo.Load / niInfo.Capacity;
          _escritorHidrodinamico.escribeMensajeInfoHilo(_infoNodosRecopilada.getContenido("info de " + this._nodoAnfitrion.nombre));
          if (_infoNodosRecopilada.update(niInfo,
              "Node°=°" + niInfo.Node + "°")) {
            // ya actualizó la información del Node f
          }
          else {
            _infoNodosRecopilada.addNew(niInfo);
          }
          _escritorHidrodinamico.escribeMensajeInfoHilo(_infoNodosRecopilada.getContenido("info de " + this._nodoAnfitrion.nombre));
          // Envía mensaje
          try{
            this.informaNodo0(new DIRBalanceMensaje(_nodoAnfitrion.nombre,
                aNode[0], MENSAJES_RECEIVE,
                this._nodoAnfitrion.getCapacidad(),
                this._nodoAnfitrion.getCargaAplicacion(), nR, 0));
          }catch(ADMINGLOExcepcion ex){
            _escritorBalanceador.escribeMensajeInfoHilo("No se pudo enviar mensaje a '"+
                aNode[0]+"'.");
          }
          // hubo cambio en medidas del Node i
          bChangeWorkLoad = true;
          _brindaApoyo.put(aNode[0],new DIRTransferencias(aNode[0],nR));
          bEventosImportantes=true;
  //        /**
  //         * asigna el tiempo de vencimiento para confirmar cambio en los
  //         * valores de carga y altura, cuando reciba un mensaje tipo "B"
  //         * del nodo dado (aNode[0]). Si la llegada del mensaje es satisfactoria
  //         * se confirma la recepción de trabajo por parte de aNode[0].
  //         * ...................................................................
  //         */
  //        setTimeOut(esperaMayor);
  //        esperaConfirmacionReciba=true;
  //        /**
  //         * ...................................................................
  //         */
          break;
        }
// 09
        case 'R': {
          // modifica info sobre Node i
          this._nodoAnfitrion.setCargaAplicacion((this._nodoAnfitrion.getCargaAplicacion() - niInfoMigración[0]));
          niInfo = null;
          niInfo = new DIRInfoNodo();
          niInfo.Node = this._nodoAnfitrion.nombre;
          niInfo.Capacity = this._nodoAnfitrion.getCapacidad();
          niInfo.Load = this._nodoAnfitrion.getCargaAplicacion();
          niInfo.Height = niInfo.Load / niInfo.Capacity;
          _escritorHidrodinamico.escribeMensajeInfoHilo(_infoNodosRecopilada.getContenido("info de " + this._nodoAnfitrion.nombre));
          _infoNodosRecopilada.update(niInfo,
                                     "Node°=°" + niInfo.Node + "°");
          _escritorHidrodinamico.escribeMensajeInfoHilo(_infoNodosRecopilada.getContenido("info de " + this._nodoAnfitrion.nombre));
          // modifica info sobre Node f
          niInfo = null;
          niInfo = new DIRInfoNodo();
          niInfo.Node = aNode[0];
          niInfo.Capacity = aCapacity[0];
          niInfo.Load = aWorkLoad[0];
          niInfo.Height = niInfo.Load / niInfo.Capacity;
          _escritorHidrodinamico.escribeMensajeInfoHilo(_infoNodosRecopilada.getContenido("info de " + this._nodoAnfitrion.nombre));
          if (_infoNodosRecopilada.update(niInfo,
              "Node°=°" + niInfo.Node + "°")) {
            // ya actualizó la información del Node f
          }
          else {
            _infoNodosRecopilada.addNew(niInfo);
          }
          _escritorHidrodinamico.escribeMensajeInfoHilo(_infoNodosRecopilada.getContenido("info de " + this._nodoAnfitrion.nombre));
          // modifica Para Give
          _aDar.update(new DIRTransferencias(niInfo.Node, 0),
                      "Node°=°" + niInfo.Node + "°");
  
          // hubo cambio en medidas del Node i
          bChangeWorkLoad = true;
          _recibeApoyo.put(aNode[0],new DIRTransferencias(aNode[0],
              niInfoMigración[0]));
          bEventosImportantes=true;
          break;
        }
        default: {
          niInfo = null;
          niInfo = new DIRInfoNodo();
        }
      } // fin del switch
      // Lectura de mensaje -----------------------------^^^
// 10
      if (this._transferencias != null) {
        _transferencias.clear();
        /*
         * ello para no seguir enviando mensajes
         * al mismo j del transferencias cuando se lea un
         * broadcast de cualquiera y aDar.GetTotal()!=0
         */
      }
      if (_aDar.getTotal("Give") == 0) {
        /*--------------------- repara time out ----------------------
         * repara el timeOut, garantizado que ya no se van a recibir
         * mensajes "R" atrasados hasta que se de el motivo para prevenirlo.
         */
  //      setTimeOut(0);
        /*-----------------------------------------------------------*/
        // le corresponde llenar el transferencias
        _transferencias = ComputeTransfer();
      } else {
        _transferencias = null;
      }
// 11 (a, b)
      if (bChangeWorkLoad || bChangeCapacity) {
        bTerminar = false;
        _infoNodosRecopilada.moveFirst();
        while (!_infoNodosRecopilada.getEoF()) {
          niInfo = null;
          niInfo = (DIRInfoNodo) _infoNodosRecopilada.getObject();
          if (Trigger(this._nodoAnfitrion.getCargaAplicacion() / 
                      this._nodoAnfitrion.getCapacidad(),
                      niInfo.Load / niInfo.Capacity)) {
            // manda mensaje al nodo que hace la diferencia
            try{
            this.informaNodo0(new DIRBalanceMensaje(
                _nodoAnfitrion.nombre, niInfo.Node, MENSAJES_BROADCAST,
                this._nodoAnfitrion.getCapacidad(), 
                this._nodoAnfitrion.getCargaAplicacion(), 0, 0));
            }catch(ADMINGLOExcepcion ex){
              _escritorBalanceador.escribeMensajeInfoHilo("No se pudo enviar mensaje a '"+
                  niInfo.Node+"'.");
            }
          }
          _infoNodosRecopilada.moveNext();
        }
      }
      nHilosEnMarcha--;
    } // del synchronized 2
    _escritorBalanceador.escribeMensajeInfoHilo("Fin bloqueo 2 HeteroLB.salida.");
    /*
     * Finalizando con esta sincronización se le da oportunidad 
     * de llenar y vaciar el transferencias a los demás hilos,
     * responsabilizando al hilo presente del envío del mensaje final
     * del estado a los demás nodos del dominio de balance. 
     */
    _escritorBalanceador.escribeMensajeInfoHilo("Previo bloqueo 3 HeteroLB.entrada.");
    synchronized(objeto_bloqueo_hidro){
      _escritorBalanceador.escribeMensajeInfoHilo("Fin bloqueo 3 HeteroLB.entrada.");
// else del if anterior
//    11 (c, d)
      if (!(bChangeWorkLoad || bChangeCapacity)) {
        if ( (_transferencias == null && !bTerminar && (nHilosEnMarcha==0)) ||
            ( (_transferencias != null) && (!bTerminar) &&
             (_transferencias.size() == 0) && (nHilosEnMarcha==0))) {
          // termina lecturas con un broadcast
          bTerminar = true;
          try {
            _escritorBalanceador.escribeMensajeInfoHilo("Mandando último mensaje al dominio.");
            itr=_vecinillos.values().iterator();
            Dominios_bal domI;
            while(itr.hasNext()){
              domI=(Dominios_bal)(itr.next());
              this.informaNodo0(
                  new DIRBalanceMensaje(_nodoAnfitrion.nombre,
                      domI.getVecino(),
                      MENSAJES_BROADCAST,
                      this._nodoAnfitrion.getCapacidad(),
                      this._nodoAnfitrion.getCargaAplicacion(), 0, 0));
            }
          }
          catch (Exception ex) {
            _escritorBalanceador.escribeMensajeInfoHilo("Error en HeteroLB():" + ex.getMessage());
            ex.printStackTrace();
          }
        }
      } // de si hay cambios
//    12
      if (_transferencias != null) {
        /*
         * operación especial para el cambio de Height
         * considera la DIRTransferencias a cada Node
         */
        nHeightNueva=0;
        itr=_transferencias.values().iterator();
        while(itr.hasNext()){
          tnrTrans = (DIRTransferencias) itr.next();
          nHeightNueva+=tnrTrans.Give;
        }
        nHeightNueva = (this._nodoAnfitrion.getCargaAplicacion() -
            nHeightNueva) /this._nodoAnfitrion.getCapacidad();
        /*
         * Este código estaba cuando transferencias era un AbstractSet
         * teniendo un rendimiento O(1). Ahora uso Map porque tal
         * conjunto es pasado al nivel Planificador de forma más estándar.
         * 
         * nHeightNueva = (this.nodoNI.getCargaAplicacion() -
         *                transferencias.getTotal("Give")) /
         *    this.nodoNI.getCapacidad();
         */
// 13
        itr=_transferencias.values().iterator();
        while (_transferencias != null && itr.hasNext()) {
          tnrTrans = (DIRTransferencias) itr.next();
          try{
          this.informaNodo0(new DIRBalanceMensaje(
              _nodoAnfitrion.nombre, tnrTrans.Node, MENSAJES_GIVE,
              this._nodoAnfitrion.getCapacidad(),
              this._nodoAnfitrion.getCargaAplicacion(), tnrTrans.Give, nHeightNueva));
          }catch(ADMINGLOExcepcion ex){
            _escritorBalanceador.escribeMensajeInfoHilo("No se pudo enviar mensaje a '"+tnrTrans.Node+"'.");
          }
          _aDar.update(tnrTrans, "Node°=°" + tnrTrans.Node + "°");
        }
// 13.1
        /*
         * asigna tiempo de vencimiento si se envió un mensaje tipo "G",
         * pues debe esperar la llegada satisfactoria (dentro de ese tiempo)
         * de un mensaje "R".
         * ...................................................................
         */
        if(_transferencias.size()>0 && _aDar.getCount()>0 &&
           _aDar.getTotal("Give")>0){
  //          setTimeOut(esperaMayor);
  //        esperaConfirmacionDeme = true;
        }
      }
      if(bEventosImportantes||_transferencias!=null){
        this.invocaRegistrados();
        if(_transferencias!=null){
          _transferencias.clear();
          _transferencias = null;
        }
      }
    } // del synchronized 3
    _escritorBalanceador.escribeMensajeInfoHilo("Fin bloqueo 3 HeteroLB.salida.");    
// 14 FIN
  }
  protected final void informaNodo0(DIRBalanceMensaje mensaje) throws ADMINGLOExcepcion{
    String sDestino="";
    Ubicaciones ubicacion=null;
    DIRBalancesProxy balproxy=null;
    sDestino=mensaje.getParaId();
    sDestino=desc.getDireccion(sDestino);
    balproxy=new DIRBalancesProxy();
    balproxy.setDestino(sDestino);
    balproxy.setDistribuidor(this.getDistribuidor());
    try {
      balproxy.balancea(mensaje);
    }
    catch (Exception ex) {
      _escritorBalanceador.escribeMensajeInfoHilo("Mensaje de balance no se pudo enviar a '"+
          mensaje.getParaId()+"'.");
      // ex.printStackTrace();
    }
  }  
  /**
   * Paso 05 del método ComputeTransfer().
   * @param asOrden Conjunto ordenado de información de nodos.
   * @param asTrans Conjunto de transferencias a formar.
   * @param K k-ésimo elemento o nivel evaluado.
   * @param niInfo información del nodo actual interesado.
   * @param H altura global revisada hasta el késimo elemento.
   */
  private void BuildTrans(AbstractSet asOrden,Map asTrans,
                             Object K,DIRInfoNodo niInfo,double H){
    asTrans.clear();
    asOrden.initIteration("Height");
    if (asOrden.getIteratorBookmark("Height") != K) {
      niInfo = (DIRInfoNodo) asOrden.nextIteration("Height"); // se brinca el primer elemento que es i
      while ( (niInfo = (DIRInfoNodo) asOrden.nextIteration("Height")) != null) {
        asTrans.put(niInfo.Node,new DIRTransferencias(niInfo.Node,
                                       (H - niInfo.Height) *
                                       niInfo.Capacity));
        if ( (K != null) && (asOrden.getIteratorBookmark("Height") == K)) {
          // fin de la asignación de los valores a dar que corresponde cuando
          // se llega al caésimo nodo.
          break;
        }
      }
    }
    else {
      //
    }
  }

  /**
   * Computa un arreglo de transferencia que logre balancear la carga de
   * trabajo en las computadoras seleccionadas en donde se supone que es
   * factible y recomendable realizar el balance (usando la función Trigger()).
   * - Se caracteriza por utilizar un enfoque hidrodinámico para realizar
   *   el balance.
   * @return el arreglo de transferencia a pedir.
   */
  private Map ComputeTransfer(){
  Map asTrans=new TreeMap(String.CASE_INSENSITIVE_ORDER) /*AbstractSet(vecinillos.size(),
                                      new DIRTransferencias("",0),true,false)*/;
  AbstractSet asOrden=new AbstractSet(_vecinillos.size()+1,
  new DIRInfoNodo("",0,0),true,false,new String[]{"Height"});
  int j=0;
  DIRInfoNodo niKNode=null;
  DIRInfoNodo niKNodeNext=null;
  double nHJ=0;
  Object K=null;
  double MAXFILL=0;
  double TOFILL=0;
  Object temporal=null;
// 01
    niKNode = new DIRInfoNodo(_nodoAnfitrion.nombre, _nodoAnfitrion.getCapacidad(), _nodoAnfitrion.getCargaAplicacion());
    niKNode.Height=0;
    asOrden.addNew(niKNode);
    niKNode=null;
// 02
      _infoNodosRecopilada.moveFirst();
      if (!_infoNodosRecopilada.getEoF()) {
        // este nodo ya fue incluido en 01
        _infoNodosRecopilada.moveNext();
      }
      while (!_infoNodosRecopilada.getEoF()) {
        // de donde saco niKNode?,... de infoNodosRecopilada
        niKNode = (DIRInfoNodo) _infoNodosRecopilada.getObject();
        /**
         * si la altura de ni es lo suficientemente más alta que que nk,
         * entonces ejecuta el balance.
         */
        if (Trigger(niKNode.Load / niKNode.Capacity,
                    this._nodoAnfitrion.getCargaAplicacion() / this._nodoAnfitrion.getCapacidad())) {
          asOrden.addNew(niKNode);
        }
        niKNode = null;
        _infoNodosRecopilada.moveNext();
      }
// 03
    double C=0;
    double L = this._nodoAnfitrion.getCargaAplicacion();
    double H=0;
// 04
    asOrden.initIteration("Height");
    j=0;
// 04.a y 04.b
    try {
      while ( (niKNode = (DIRInfoNodo) asOrden.nextIteration("Height", K)) != null) {
        nHJ = niKNode.Height;
        C = C + niKNode.Capacity;
        niKNodeNext = null;
        while ( (niKNodeNext = (DIRInfoNodo) asOrden.nextIteration("Height")) != null) {
          if (niKNodeNext.Height == nHJ) {
            // C se va acumulando
            C = C + niKNodeNext.Capacity;
            niKNode = niKNodeNext;
            K = asOrden.getIteratorBookmark("Height");
            // al final del ciclo K es el mayor valor válido
          }
          else {
            // se devuelve porque los que vienen son alguras mayores
            // niKNodeNext es el siguiente
            // niKNode es el actual, al que corresponde K
            if (asOrden.getIteratorBookmark("Height") == null) {
              // niKNodeNext es la última tupla, se devuelve
              // de la siguiente forma porque el iterador ya
              // se desbordó
              asOrden.lastOfIteration("Height");
            }
            else {
              // niKNodeNext no es la última tupla,
              // se devuelve de la siguiente forma porque el
              // iterador no se ha desbordado
              asOrden.prevIteration("Height");
            }
            // K, igual a asOrden.GetIteratorBookmark("Height"),
            // corresponde a niKNode
            // K+1 corresponde a niKNodeNext
            break;
          }
        }
// 04.c
        if (niKNodeNext == null) {
          // la última tupla de la lista ordenada
          // K queda con el último valor válido o null
          H = H + L / C;
          BuildTrans(asOrden, asTrans, K, niKNode, H);
          break;
        }
// 04.d
        // niKNode.Height es igual a nHJ
        MAXFILL = (niKNodeNext.Height - niKNode.Height) * C;
// 04.e
        // lo siguiente para pasar a alturas menores
        // elige mínimos
        if (MAXFILL <= L) {
          TOFILL = MAXFILL;
        }
        else {
          TOFILL = L;
        }
// 04.f
        H = H + TOFILL / C;
// 04.g
        L = L - TOFILL;
// 04.h
        if (L == 0) {
          // ya toda la carga se ha repartido
          // hace el paso 05 del ComputerTr de Hui y Chanson
          BuildTrans(asOrden, asTrans, K, niKNode, H);
          break;
        }
// 04.i
        if (K != null) {
          asOrden.setIteratorBookmark("Height", K);
          // continúa el balance
        }
      }
    }
    catch (Exception ex) {
    	_escritorHidrodinamico.escribeMensajeInfoHilo("Error en ComputeTr():\n"+ex.getMessage());
      ex.printStackTrace();
    }
// 06
    return asTrans;
  }
  /**
   * Con esta función el nodo i se entera que está con un nivel de
   * carga de trabajo más alto que el de los demás.
   * @param nIHeight Altura o relación de carga del nodo i (el actual)
   * @param nJHeight Altura o relación de carga del nodo j (un vecino)
   * @return si debe o no haber una reacción por la diferencia de carga.
   */
  private boolean Trigger(double nIHeight,double nJHeight){
    return reaccion.Trigger(nIHeight,nJHeight);
  }
  /**
   * Información de un nodo que se puede compartir en el
   * balance de carga hidrodinámico.
   */
  public static class DIRInfoNodo {
    public String Node="";
    public double Capacity=0;
    public double Load=0;
    public double Height=0;
    public DIRInfoNodo(){
      Node="";
      Capacity=-0.001;
      Load=0;
      Height=0;
    };
    public DIRInfoNodo(String cNode,double nCapacity,double nLoad){
      Node=cNode;
      Capacity=nCapacity;
      Load=nLoad;
      if(nCapacity!=0){
        Height=nLoad/nCapacity;
      }else{
        Height=0;
      }
    };

  }
  

}
