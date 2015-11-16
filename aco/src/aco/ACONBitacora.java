package aco;


/**
 * Se dedoca a informar sobre variables de ambiente que indican si
 * el paquete se está depurando.<br>
 */

public final class ACONBitacora {
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  private static boolean _isDebugging=false;
  private static boolean _wasQuerried=false;
  private static String header="<?xml version=\"1.0\"?>\n";
  private static String initext="<acceptorlog>";
  private static String finitext="\n</acceptorlog>";
  private static String name=".acon.acc.log.xml";
  private static int cantidad=0;
  private static int puerto=4412;
  private static boolean conbitacora=false;
  private static BITAServidor servidor=null;
  private static ACONAceptadorDesp aceptador=null;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public ACONBitacora() {
    _isDebugging=false;
    _wasQuerried=false;
  }
  /**
   * Indica si se está depurando el paquete.
   * @return Verdadero si se está depurando el paquete.
   */
  private static boolean isDebugging(){
    boolean res=false;
    String texto="";
    if(!_wasQuerried){
      _wasQuerried=true;
      try {
        texto=System.getProperty("ACONDEBUG");
        if(texto==null){
          _isDebugging=false;
        }else if(texto.compareToIgnoreCase("true")==0){
          _isDebugging=true;
        }else if(texto.compareToIgnoreCase("verdadero")==0){
          _isDebugging=true;
        }
      }
      catch (Exception ex) {
        _isDebugging=false;
      }
    }
    res=_isDebugging;
    return res;
  }
  public static void print(String message){
    if(isDebugging()){
      System.out.println(message);
    }
  }

  public static synchronized void addLog(final String valor,int nada){
    //init(aceptador);
    if(!conbitacora){
      return;
    }
    Thread th;
    cantidad++;
    /****************************************************************/
    Runnable rnn=new Runnable(){
      public void run (){
        // solamente manda datagrama al servidor de bitácoras
        try {
           java.net.DatagramSocket datagramas =
               new java.net.DatagramSocket();
           datagramas.send(new java.net.DatagramPacket(
              valor.getBytes(),valor.length(),
              new java.net.InetSocketAddress("localhost", puerto)));
           datagramas.close();
         }
         catch (java.net.SocketException ex) {
           ex.printStackTrace();
         }
         catch (java.io.IOException ex) {
           ex.printStackTrace();
         }
      }
    };
    /****************************************************************/
    th=new Thread(rnn,"Escritura."+cantidad);
    th.start();
  }
  public static synchronized void setPuertoBitacora(int puerto0){
    if(puerto0>0){
      puerto = puerto0;
    }else{
      puerto=4412;
    }
  }
  public static synchronized int getPuertoBitacora(){
    return puerto;
  }
  public static ACONAceptadorDesp getAceptador(){
    return aceptador;
  }
  public static void setAceptador(ACONAceptadorDesp acc){
    aceptador=acc;
  }
  public static void setConBitacora(boolean value){
    conbitacora=value;
  }
  public static boolean getConBitacora(){
    return conbitacora;
  }
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
}