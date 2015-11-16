package corabitas;

import aco.ACONConectorDesp;
import aco.ACONDescriptor;
import aco.ACONExcArbitraria;
import aco.ACONExcOmision;
import aco.ACONExcepcion;
import aco.ACONGestor;

/**
 * @author Alessandro
 */
public class CORACliente {
  final ACONDescriptor descriptor=new ACONDescriptor();
  public CORACliente() {
    _inicio();
  }
  public CORACliente(String archivo){
    _inicio();
    CORAServidor.Configuracion.cargaDescriptor(archivo,descriptor);
    descriptor.remotehost=descriptor.localhost;
    descriptor.remoteport=descriptor.remoteport;
    descriptor.localhost="localhost";
    descriptor.localport=0;    
  }
  private void _inicio(){
    descriptor.id="tdderive-bitacora-cliente";
    descriptor.remotehost="localhost";
    descriptor.remoteport=8900;
    descriptor.localhost="localhost";
    descriptor.localport=0;
    descriptor.socket_type=ACONDescriptor.STREAM;
    descriptor.wait=false;
    descriptor.log="false"; 
  }
  public void escribe(final String texto){
    
    ACONConectorDesp conec=new ACONConectorDesp();
    ACONGestor gestor;
    gestor=new ACONGestor(descriptor){
      public void completa() throws ACONExcepcion, Exception {
      }
      public void open() throws ACONExcepcion, Exception {
        this.send(texto);
        this.close();
      }
    };
    try {
      conec.conecta(gestor);
    } catch (ACONExcArbitraria e) {
      e.printStackTrace();
    } catch (ACONExcOmision e) {
      e.printStackTrace();
    }    
  }
  public static void main(String[] args) {
    CORACliente cli=new CORACliente();
    cli.escribe("hola");
    cli.escribe("esto");
    cli.escribe("es");
    cli.escribe("una");
    cli.escribe("prueba");
  }
}
