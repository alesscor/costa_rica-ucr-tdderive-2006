package aco;


/**
 * Programa el gestor de ingreso de registros
 * de bitácora.
 */
public class BITAGestorIn extends ACONGestor {
  BITAServidor servidor;
  public BITAGestorIn() {
    servidor=null;
  }

  public BITAGestorIn(ACONDescriptor info0) {
    super(info0);
  }
  public void acepta() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void manejaEvento() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void open() throws aco.ACONExcepcion {
    String mensaje="";
    boolean mismohost=false;
    // solo acepta datagramas generados en el host local
    //otry {
      mismohost = true/*//othis.getDatagramPack().getAddress().getHostAddress().
          compareToIgnoreCase(this.getDatagramPack().getAddress().
                              getLocalHost().getHostAddress()) == 0*/;
    //o}
    //ocatch (UnknownHostException ex) {
    //o  mismohost=false;
    //o}
    if(mismohost){
      mensaje=this.receive();
      // registra mensaje en bitácora
      servidor.addRegistro(mensaje);
      this.close();
    }else{
      System.err.println(this.getDatagramPack().getAddress().getHostAddress() +
                         " != "+
                         this.getParDatagrama().getLocalAddress().getHostAddress());
      this.close();
    }
  }
  public void sirvase() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void completa() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  protected void setNavegables(Object[] navegables){
    if(navegables!=null){
     servidor=(BITAServidor)navegables[0];
   }else{
     // craso error, esto nunca debe pasar
     System.err.println("No se tiene un BITAServidor navegable");
   }
  }
}