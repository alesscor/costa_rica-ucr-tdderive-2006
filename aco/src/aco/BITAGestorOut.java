package aco;

/**
 * Programa el gestor cliente de solicitudes de
 * registro de bitácora.
 */
public class BITAGestorOut extends ACONGestor {
  private BITAServidor servidor;

  public BITAGestorOut() {
    servidor=null;
  }

  public BITAGestorOut(ACONDescriptor info0) {
    super(info0);
  }
  public void acepta() throws aco.ACONExcepcion {
  }
  public void manejaEvento() throws aco.ACONExcepcion {
  }
  /**
   * Atiende las conexiones, y a aquellas que cumplen con
   * la clave, manda parte de la bitácora. El contenido del
   * resultado devuelto depende de lo que se pidió en el
   * mensaje.
   * @throws aco.ACONExcepcion
   */
  public void open() throws aco.ACONExcepcion {
    String mensaje="",respuesta="";
    this.setTiempoEsperaGestor(this.servidor.getTimeStamp());
    try{
      mensaje = this.receive();
    }
    catch(ACONExcTemporizacion ex){
      respuesta=BITAServidor.cTIEMPO_VENCIDO;
      this.send(respuesta);
      this.close();
      return;
    }
    if(servidor.getEncabezado()!=""){
      if(mensaje.indexOf(servidor.getEncabezado())<0){
        // no cumple la clave
        respuesta=BITAServidor.cNO_HAY_PERMISO;
        this.send(respuesta);
        this.close();
        return;
      }
      if(mensaje.indexOf(BITAServidor.cDEME_SOLO_ULTIMO)>=0){
        // solo quiere la última info
        respuesta=servidor.getRegistros(BITAServidor.DEME_SOLO_ULTIMO);
        this.send(respuesta);
        this.close();
        return;
      }
      if(mensaje.indexOf(BITAServidor.cDEME_SUPUESTO_COMPLETO)>=0){
        // solo quiere la última info
        respuesta=servidor.getRegistros(BITAServidor.DEME_SUPUESTO_COMPLETO);
        this.send(respuesta);
        this.close();
        return;
      }
    }
    // no se sabe lo que quiere
    System.err.println("No se sabe lo que quiere un cliente con \""+
                       mensaje+"\"");
    respuesta=BITAServidor.cNO_SE_DA_A_ENTENDER;
    this.send(respuesta);
    this.close();

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