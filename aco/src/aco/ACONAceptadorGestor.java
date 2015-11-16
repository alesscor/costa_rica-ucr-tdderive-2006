package aco;

/**
 * Permite a objetos de máquinas virtuales de Java remotas
 * acceder al <tt>AceptadorDesp</tt> para operarlo.
 * <li>Extiende a la clase gestor.</li>
 */
public class ACONAceptadorGestor extends ACONGestor {
  /**
   * Despachador a operar. Se recomienda que haya uno
   * por computadora.
   */
  private ACONAceptadorDesp despa;
  /**
   * ACONAceptadorServidor, para jalar información
   * de configuración.
   */
  private ACONAceptadorServidor servidor;
  private BITAServidor bitaservidor;
  public ACONAceptadorGestor() {
  }

  public ACONAceptadorGestor(ACONDescriptor info0) {
    super(info0);
  }
  public void acepta() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void manejaEvento() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  /**
   * Atiende consultas al servidor, discriminando según
   * valor de contraseña. El resultado de lo respondido
   * depende del mensaje de solicitud del servicio recibido.
   * @throws aco.ACONExcepcion Si ocurre un error a reportar por
   * parte del servidor.
   */
  public void open() throws aco.ACONExcepcion {
    String mensaje="",respuesta="";
    this.setTiempoEsperaGestor(this.servidor.getTimeStamp());
    try{
      mensaje=this.receive();
    }
    catch(ACONExcTemporizacion ex){
      respuesta=ACONAceptadorServidor.cTIEMPO_VENCIDO;
      this.send(respuesta);
      this.close();
      return;
    }
    if(servidor.getEncabezado()!=""){
      if(mensaje.indexOf(servidor.getEncabezado())<0){
        // no cumple con la clave
        respuesta=ACONAceptadorServidor.cNO_HAY_PERMISO;
        this.send(respuesta);
        this.close();
        return;
      }
      if(mensaje.indexOf(ACONAceptadorServidor.cDEME_ESTADO_DESCRIPTORES)>=0){
        // quiere descriptores regitrados y su estado
        respuesta=despa.getStatusDescriptores();
        this.send(respuesta);
        this.close();
        return;
      }
      if(mensaje.indexOf(ACONAceptadorServidor.cDEME_ESTADO_GESTORES)>=0){
        // quiere gestores activados y su estado
        respuesta=despa.getStatusGestores();
        this.send(respuesta);
        this.close();
        return;
      }
      if(mensaje.indexOf(ACONAceptadorServidor.cDEME_BITACORA_SUPUESTO_COMPLETO)>=0){
        // quiere info de bitácora
        if(bitaservidor!=null){
          respuesta = bitaservidor.getRegistros(BITAServidor.
                                                DEME_SUPUESTO_COMPLETO);
        }else{
          respuesta=BITAServidor.cNO_DISPONIBLE;
        }
        this.send(respuesta);
        this.close();
        return;
      }
      if(mensaje.indexOf(ACONAceptadorServidor.cDEME_BITACORA_SOLO_ULTIMO)>=0){
        // quiere info de bitácora
        if(bitaservidor!=null){
          respuesta = bitaservidor.getRegistros(BITAServidor.DEME_SOLO_ULTIMO);
        }else{
          respuesta=BITAServidor.cNO_DISPONIBLE;
        }
        this.send(respuesta);
        this.close();
        return;
      }
    }
    // no se sabe lo que quiere
    System.err.println("No se sabe lo que quiere un cliente con \""+
                       mensaje+"\"");
    respuesta=ACONAceptadorServidor.cNO_SE_DA_A_ENTENDER;
    this.send(respuesta);
    this.close();
  }
  public void sirvase() throws aco.ACONExcepcion {
  }
  public void completa() throws aco.ACONExcepcion {
  }
  public void setNavegables(Object[] navegables){
    try{
      if (navegables != null) {
        servidor = (ACONAceptadorServidor) navegables[0];
        despa = (ACONAceptadorDesp) navegables[1];
        bitaservidor=(BITAServidor)navegables[2];
      }
      else {
        System.err.println("No se tiene un ACONAceptadorDesp ni un "+
                           "ACONAceptadorServidor navegables.");
      }
    }catch(Exception ex){
      System.err.println("No se tiene un ACONAceptadorDesp ni un "+
                           "ACONAceptadorServidor navegables.");
    }
  }
}