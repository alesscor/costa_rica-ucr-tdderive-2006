package corabitas;

import java.io.IOException;

import corabitas.CORAServidor.Configuracion;
import aco.ACONDescriptor;
import aco.ACONExcepcion;
import aco.ACONGestor;

/**
 * @author Alessandro
 */
public final class CORAAtencion extends ACONGestor {
  public CORAAtencion() {
    super();
  }
  public CORAAtencion(ACONDescriptor info0) {
    super(info0);
  }

  public void completa() throws ACONExcepcion, Exception {

  }

  public void open() throws ACONExcepcion, Exception {
    String mensaje="",respuesta="";
    CORAServidor servi=null;
    this.setTiempoEsperaGestor(Configuracion.stream_espera);
    try{
      mensaje = this.receive();
      this.close();
    }
    catch(ACONExcepcion ex){
      return;
    }
    if(this.getNavegables().length>0){
      servi=(CORAServidor)(this.getNavegables()[0]);
    }else{
      return;
    }
    try {
      servi.imprime(mensaje);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
}
