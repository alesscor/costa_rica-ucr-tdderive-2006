package aco;
import java.net.*;

/**
 * Objeto que establece conexiones e inicializa su gestor de servicio
 * de manera sincr�nica o asincr�nica.<br>
 * Implementa la estrategia para activamente conectar e inicializar
 * un Gestor de servicios concreto (puede ser llamado cliente, pero
 * es mejor ponerle como nombre "parte activa de la conexi�n").<br>
 * La conexi�n que inicia activamente es para contactar a un Gestor de
 * servicios concreto remoto, probablemente un objeto de la
 * clase <tt>AceptadorDesp</tt>.<br>
 * M�todos de inter�s a redefinir:<br>
 * <li>preconnect.</li>
 * <li>postconnect.</li><br>
 */
public class ACONConectorDesp {
  //////////////////////////////////////////////////////////////////////
  /**
   * Archivo de bit�cora.
   */
  protected java.io.OutputStream log;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public ACONConectorDesp() {
  }
  /**
   * Conecta el servicio de al extremo del canal de comunicaci�n indicado
   * en <tt>sh.info</tt>.
   * <li>Realiza control local para las conexiones</li>
   * @param sh Gestor de servicios a conectar.
   * @throws ACONExcArbitraria Si hay error en la conexi�n.
   */
  public final void conecta(final ACONGestor sh)throws ACONExcArbitraria,
  ACONExcOmision{
    if(!preconnect(sh)){
      // no se acepta la conexi�n.
      return;
    }
    Runnable rnn=new Runnable(){
    // hace extremo activo de conexi�n para streams
    /*+*\--------------------------------------------------\*+*/
    /*|*/  public void run(){                              /*|*/
    /*|*/    Object canal=null;String err_msg="";          /*|*/
    /*|*/    try{                                          /*|*/
    /*|*/      if(sh.info.socket_type==                    /*|*/
    /*|*/        ACONDescriptor.STREAM){                   /*|*/
    /*|*/        canal = new Socket();                     /*|*/
    /*|*/        ((Socket)canal).setReuseAddress(true);    /*|*/
    /*|*/ if(!((Socket)canal).getReuseAddress()){          /*|*/
    /*|*/   System.err.println("no sirve el reuse");       /*|*/
    /*|*/}                                                 /*|*/
    /*|*/        ((Socket)canal).bind(new                  /*|*/
    /*|*/          InetSocketAddress(sh.info.localport));  /*|*/
    /*>*/        ((Socket)canal).setReuseAddress(true);    /*<*/
    /*|*/             ((Socket)canal).connect(new          /*|*/
    /*|*/          InetSocketAddress(sh.info.remotehost,   /*|*/
    /*|*/               sh.info.remoteport),               /*|*/
    /*|*/               sh.getTiempoConexion());           /*|*/
    /*|*/        sh.setStream((Socket)canal);              /*|*/
    /*|*/      }else if(sh.info.socket_type==              /*|*/
    /*|*/        ACONDescriptor.DGRAM){                    /*|*/
    /*|*/        canal = new DatagramSocket(               /*|*/
    /*|*/                      sh.info.localport);         /*|*/
    /*|*/        sh.setDgram((DatagramSocket)canal);       /*|*/
    /*|*/      }                                           /*|*/
    /*|*/    }catch(Exception ex){                         /*|*/
    /*|*\---------------- excepci�n -----------------------\*|*/
    /*|*/      err_msg=ACONExcepcion.getMessage(           /*|*/
    /*|*/      ACONExcepcion.CONEXION,ex,0,                /*|*/
    /*|*/      sh.info.remotehost,sh.info.remoteport);     /*|*/
    /*|*/      System.err.println(err_msg);                /*|*/
    /*|*/      // ex.printStackTrace();                    /*|*/
    /*|*/      canal=null;                                 /*|*/
    /*|*\--------------------------------------------------\*|*/
    /*|*/    }                                             /*|*/
    /*|*/    if(canal!=null){                              /*|*/
    /*|*/      if(!postconnect(sh,canal)){                 /*|*/
    /*|*/        return;                                   /*|*/
    /*|*/      }                                           /*|*/
    /*|*/      ACONBitacora.print("Abriendo cliente");     /*|*/
    /*|*\--------------- se abre el gestor ----------------\*|*/
    try{
      /*|*/  sh.open();    /*|*/
      /*|*/  sh.setFin();  /*|*/
    }catch(Exception ex){
      // buscar una forma asincr�nica para reportar un eventual error
      ex.printStackTrace();
    }
    /*|*\--------------------------------------------------\*|*/
    /*|*/      ACONBitacora.print("\"Cliente\" ha sido "+  /*|*/
    /*|*/                "abierto desde el conector");     /*|*/
    /*|*/    }else{                                        /*|*/
    /*|*/      ACONBitacora.print("El canal es nulo "+     /*|*/
    /*|*/                   "desde el conector");          /*|*/
    /*|*/    }                                             /*|*/
    /*|*/  }                                               /*|*/
    /*|*/  };                                              /*|*/
    /*+*\--------------------------------------------------\*+*/
    Thread hilo=new Thread(rnn);
    if(sh.info.wait){
    // no hace hilo extra, hay bloqueo pues este es
    // un connect sincr�nico
      hilo.run();
    }else{
    // hace hilo extra para el connect asincr�nico.
      hilo.start();
    }
    return;
  }
  /**
   * Examina el gestor de servicios antes de iniciar
   * la conexi�n. ACONGestor::info tiene la suficiente informaci�n
   * para tomar decisiones.
   * Esta operaci�n no decide nada. Debe sobrecargarse.
   * @param sh El gestor de servicios a examinar.
   * @return Si se acepta iniciar la conexi�n.
   * <br>Patr�n <b>Strategy</b> inclu�do en m�todo <tt>conecta</tt>.
   */
  public synchronized boolean preconnect(ACONGestor sh){
    // aqu� no decide nada.
    boolean res=true;
    return res;
  }
  /**
   * Examina el gestor de servicios y el canal de
   * comunicaci�n para la conexi�n luego de obtener
   * a �ste �ltimo. ACONGestor:: info tiene la suficiente informaci�n
   * para tomar decisiones.
   * Esta operaci�n no decide nada. Debe sobrecargarse.
   * @param sh El gestor de servicios a examinar.
   * @param canal El canal de comunicaci�n a examinar.
   * @return Si se acepta proseguir con la comunicaci�n
   * usando el canal.
   * <br>Patr�n <b>Strategy</b> inclu�do en m�todo <tt>conecta</tt>.
   */
  public synchronized boolean postconnect(ACONGestor sh,Object canal){
    boolean res=true;
    return res;
  }
}