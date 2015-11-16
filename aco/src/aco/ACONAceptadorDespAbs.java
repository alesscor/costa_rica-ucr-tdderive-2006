/*
 * Created on 15/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package aco;

/**
 * <br>Abstracción de despachador.</br>
 */
public abstract class ACONAceptadorDespAbs {
  private String bloqueos_verlistocanal;
  public ACONAceptadorDespAbs() {
    bloqueos_verlistocanal="no es el fin";
  }
  protected static void log_postaccept(ACONDescriptor reg,Object canal,Object canal0[]){
    String texto="";
    int n = 1;
    if((reg.log==null) || (reg.log=="")||
       (reg.log.compareToIgnoreCase("true")==0)||
       (reg.log.compareToIgnoreCase("verdadero")==0)){
      texto += "<postcontacto>";
      texto += log_xxxx0(reg);
      texto += "</postcontacto>";
      ACONBitacora.print(texto);
      ACONBitacora.addLog(texto,0);
      //oACONBitacora.flushLog(null);
    }
  }


  protected static void log_preaccept(ACONDescriptor reg){
    String texto="";
    if((reg.log==null) || (reg.log=="")||
       (reg.log.compareToIgnoreCase("true")==0)||
       (reg.log.compareToIgnoreCase("verdadero")==0)){
      texto+="<inicio>";
      texto+=log_xxxx0(reg);
      texto+="</inicio>";
      ACONBitacora.print(texto);
      ACONBitacora.addLog(texto,0);
    }
  }

  protected static String log_xxxx0(ACONDescriptor reg){
    String texto="";
    texto=ACONDescriptorAdmin.getStatusLog(reg,true);
    return texto;
  }

  /**
   * Toma un servicio concreto, espera en el puerto correspondiente para
   * crea un objeto gestor de servicio <b>ACONGestor</b> concreto y así
   * ponerlo a trabajar mediante una llamada a su método <tt>open()</tt>.
   * <li>La inicialización de cada servicio considera los valores
   * de <tt>inforeg</tt></li>
   * <li>Si la espera en cada puerto se realiza, entonces cada
   * <tt>inforeg</tt> queda activado, esto es, tiene el campo
   * <tt>hiloEspera</tt> diferente que nulo.</li>
   * @param infoDescriptor Descriptor del gestor a crear. Crea un hilo por cada
   * puerto.
   * @throws ACONExcArbitraria Si hay error en la apertura del servicio.
   */
  protected final void acepta(final ACONDescriptorAdmin infoDescriptor)
  throws ACONExcArbitraria{
    /**
     * revisa info del servicio particular, si no se debe activar
     * el servicio, se sale de una vez.
     */
    if(infoDescriptor.isAbierto()){
      throw new ACONExcArbitraria("El servicio ya estaba abierto");
    }
    log_preaccept(infoDescriptor);
    if(!preaccept(infoDescriptor)){
      return;
    }
    /*+*\-----------------------------------------------------------\*+*/
    /*|*\ Hace un runnable para escuchar en nombre de un servicio   \*|*/
    /*|*\ configurado según indica infoDescriptor                   \*|*/
    /*+*\-----------------------------------------------------------\*+*/
    /*(*/ Runnable rnn=new Runnable(){                              /*(*/
    /*)*/ public void run(){                                        /*)*/
    /*(*/   boolean salir=false;                                    /*(*/
    /*)*\   int i=0;                                                \*)*/
    /*(*/   Object canal0[]=new Object[1];                          /*(*/
    /*)*/   canal0[0]=null;                                         /*)*/
    /*(*/   while(!salir){                                          /*(*/
    /*)*\   i++;                                                    \*)*/
    /*(*/   Object canal=null;                                      /*(*/
    /*)*\   if(i>1){                                                \*)*/
    /*(*\     i++; // solo para hacer un break point                \*(*/
    /*)*\   }                                                       \*)*/
    /*(*/   try{                                                    /*(*/
    /*)*/     canal=ev_manejaEventos(infoDescriptor,                /*)*/
    /*(*/                canal0);                                   /*(*/
    /*)*/                                                           /*)*/
    /*(*\  revisa info del canal y del ser-                         \*(*/
    /*)*\  vicio, puede que se salga de                             \*)*/
    /*(*\  una vez.                                                 \*(*/
    /*)*/     if((canal==null)&&                                    /*)*/
    /*(*/       (infoDescriptor.isDetenido)){                       /*(*/
    /*)*/       salir = true;                                       /*)*/
    /*(*/       continue;                                           /*(*/
    /*)*/     }                                                     /*)*/
    /*(*/     ACONBitacora.print("Tabajando para "+                 /*(*/
    /*)*/                   infoDescriptor.id);                     /*)*/
    /*(*/     if(postaccept(infoDescriptor,canal,canal0)){          /*(*/
    /*)*/       ACONBitacora.print("Abriendo para "+                /*)*/
    /*(*/                infoDescriptor.id);                        /*(*/
    /*)*\-----------------------------------------------------------\*)*/
    /*(*\ aquí se hace concreto el manejo del evento de contacto    \*(*/
    /*)*\ en el _open0 se determina si debe iniciarse un nuevo hilo \*)*/
    /*(*/       ev_open(infoDescriptor, canal,canal0);              /*(*/
    /*)*\ continúa con el hilo que hace esperas para el servicio    \*)*/
    /*(*\-----------------------------------------------------------\*(*/
    /*)*/       ACONBitacora.print("Terminando para "+              /*)*/
    /*(*/                infoDescriptor.id);                        /*(*/
    /*)*/     }                                                     /*)*/
    /*(*/   }catch(Exception ex){  // TODO Registrar errores asincr./*(*/
    /*(*/      setFin();                                            /*(*/
    /*)*/      if(infoDescriptor.isDetenido){                       /*)*/
    /*(*/         System.err.println("Detenido");                   /*(*/
    /*)*/      }else{                                               /*)*/
    /*(*/        ACONBitacora.print("Error para "+                  /*(*/
    /*)*/                infoDescriptor.id);                        /*)*/
    /*(*/       }                                                   /*(*/
    /*)*/      salir=true;                                          /*)*/
    /*(*/      System.err.println(                                  /*(*/
    /*)*/         ex.getMessage());                                 /*)*/
    /*(*/      ex.printStackTrace();                                /*(*/
    /*)*/   }//catch                                                /*)*/
    /*(*/   }//while principal                                      /*(*/
    /*)*/   }//run                                                  /*)*/
    /*(*/ };                                                        /*(*/
    /*+*\-----------------------------------------------------------\*+*/
          Thread hilo=new Thread(rnn);
          hilo.setName(infoDescriptor.id);
          /*+*\------------------\*+*/
          /*|*/   hilo.start();  /*|*/
          /*+*\------------------\*+*/
//    /*
//     * Espera a que el canal haya sido iniciado.
//     */
//    this.esFin();
  }

  protected static void log_onopen(ACONDescriptor reg,Object canal,Object canal0[],String comentario){
    String texto="";
    int n = 1;
    if((reg.log==null) || (reg.log=="")||
       (reg.log.compareToIgnoreCase("true")==0)||
       (reg.log.compareToIgnoreCase("verdadero")==0)){
      texto += "<onopen>";
      texto += log_xxxx0(reg);
      texto += "\n<comentario>";
      texto += comentario;
      texto += "</comentario>";
      texto += "</onopen>";
      ACONBitacora.print(texto);
      ACONBitacora.addLog(texto,0);
      //oACONBitacora.flushLog(null);
    }
  }




  /**
   * Examina info del gestor de servicios y del canal de
   * comunicación para la conexión luego de obtener
   * a éste último. ACONDescriptorAdmin::info tiene la suficiente información
   * para tomar decisiones.
   * Esta operación no decide nada. Debe sobrecargarse.
   * @param reg Info del gestor de servicios a examinar.
   * @param canal El canal de comunicación a examinar, puede ser un
   * DatagramPacket o un Socket, en el caso que el gestor sea para
   * datagramas o streams respectivamente.
   * @param base La base del canal de comunicación, con información importante
   * que se puede utilizar para verificación.
   * @return Si se acepta proseguir con la comunicación
   * usando el canal.
   * <br>Patrón <b>Strategy</b> incluído en método <tt>acepta</tt>.
   */
  protected boolean postaccept(ACONDescriptorAdmin reg,Object canal,Object base[] ){
    return true;
  }

  

  protected boolean preaccept(ACONDescriptorAdmin reg){
    return true;
  }
  protected abstract Object ev_manejaEventos(ACONDescriptorAdmin infoDescriptor,Object[] base0) throws
        ACONExcepcion;

  protected abstract void ev_open(ACONDescriptorAdmin descriptor,Object canal,Object canal0[])
        throws ACONExcArbitraria;
  public abstract void close();
  /**
   * Lee archivo de configuración para cargar la información
   * de servicios registrados en éste.
   */
  public void open(){
  }
  void setFin(){
    synchronized(bloqueos_verlistocanal){
      bloqueos_verlistocanal.notify();
      bloqueos_verlistocanal="";
    }
  }
  public final boolean esFin(){
    synchronized(bloqueos_verlistocanal){
      while(bloqueos_verlistocanal!=""){
        try{
          bloqueos_verlistocanal.wait();
        }catch(InterruptedException ex){
        }
      }
      return (bloqueos_verlistocanal=="");
    }
  }
  /**
   * <br>Operaciones de un despachador.</br>
   */
  final public class operaciones {
    /* ______________________________________________ */
    /*´                                              `*/
    /*|*/   public final static int ALL=1;    // 2^0 |*/
    /*|*/   public final static int ADD=2;    // 2^1 |*/
    /*|*/   public final static int GET=4;    // 2^2 |*/
    /*|*/   public final static int OPEN=8;   // 2^3 |*/
    /*|*/   public final static int CLOSE=16; // 2^4 |*/
    /*|*/   public final static int UPDATE=32;// 2^5 |*/
    /*|*/   public final static int REMOVE=64;// 2^6 |*/
    /*`______________________________________________´*/
  }
}
