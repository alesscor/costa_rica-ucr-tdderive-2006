package oact;

import aco.*;

/**
 * Gestor base para la construcción de clientes del 
 * objeto activo.
 */
public class OACTGestorProxy extends ACONGestor {
  private String entrada;
  private String salida;
  private OACTDistribuidorBase servidor;
  private OACTSolicitud solicitud;
  public OACTGestorProxy() {
  }

  public OACTGestorProxy(OACTDistribuidorBase servidor0,ACONDescriptor info0,String entrada0,
                         String salida0,OACTSolicitud solicitud0) {
    super(info0);
    entrada=entrada0;
    salida=salida0;
    servidor=servidor0;
    solicitud=solicitud0;
    solicitud.setGestor(this);
  }
  public void acepta() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void manejaEvento() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void open() throws aco.ACONExcepcion,OACTExcepcion {
    String resultado="";
    if(solicitud!=null){
      try {
        // debía mandar la solicitud
        this.send(this.entrada);
        solicitud.ejecutarAdmin();
      }catch (Exception ex) {
        throw new OACTExcepcion("No se pudo ejecutar solicitud.",ex);
      }
      return;
    }
    // si no hay solicitud que se maneja a sí misma,
    // hace el manejo genérico definido por e gestor
    if(entrada==null){
      salida=null;
      return;
    }
    this.send(entrada);
    this.setTiempoEsperaGestor(this.servidor.getTimeStamp());
    resultado=this.receive();
    salida=resultado;
    this.close();
  }
  public String getResultado(){
    return salida;
  }
  public void sirvase() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void completa() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
}