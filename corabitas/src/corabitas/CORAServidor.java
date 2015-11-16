package corabitas;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import aco.ACONAceptadorDespSimple;
import aco.ACONDescriptor;

/**
 * @author Alessandro
 */
public final class CORAServidor {
  final String nombreArchivo="tdderive-bitacora-global.txt";

  /**
   * 
   */
  public CORAServidor() {
    super();
  }
  public final void imprime(String mensaje) throws IOException{
    BufferedOutputStream bsArchivoSalida=null;
    SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy;HH:mm:ss.SSS;");
    Date ya=new Date();
    
    mensaje=sdf.format(ya)+mensaje;
    synchronized(nombreArchivo){
      bsArchivoSalida=
        new BufferedOutputStream(new FileOutputStream(nombreArchivo,true));
      bsArchivoSalida.write(mensaje.getBytes());
      bsArchivoSalida.write("\n".getBytes());
      bsArchivoSalida.close();
    }
    System.out.println(mensaje);
  }

  public static void main(String[] args) {
    CORAServidor servi=new CORAServidor();
    ACONDescriptor descriptor=new ACONDescriptor();
    ACONAceptadorDespSimple despachador=new ACONAceptadorDespSimple();
    descriptor.id="tdderive-bitacora";
    descriptor.localhost="localhost";
    descriptor.localport=8900;
    descriptor.socket_type=ACONDescriptor.STREAM;
    descriptor.wait=false;
    descriptor.server=CORAAtencion.class.getName();
    descriptor.aoNavegables=new Object[]{servi};
    descriptor.log="false"; 
//    if(args.length >0){
//      String nombreArchivo=args[0];
//      Configuracion.cargaDescriptor(nombreArchivo,descriptor);
//    }
    Configuracion.cargaDescriptor("..\\config\\corabitas-config.txt",descriptor);
    despachador.manejaEventos(descriptor);
  }
  public static class Configuracion{
    public final static int stream_espera=1000;
    public final static void cargaDescriptor(String nombreArchivo,ACONDescriptor descriptor){
      try {
        BufferedReader lector=new BufferedReader(new FileReader(nombreArchivo));
        String linea=null;
        String[] parametros=null;
        while((linea=lector.readLine())!=null){
          if(linea.trim()=="" || linea.trim().charAt(0)=='#'){
            continue;
          }
          break;
        }
        if(linea!=null){
          parametros=linea.split("\\s");
          for (int idx=0; idx<parametros.length && idx<3; idx++){
            if(idx==0){
              descriptor.localhost=parametros[idx];
            }
            if(idx==1){
              descriptor.localport=Integer.parseInt(parametros[idx]);
            }
            if(idx==2){
              descriptor.socket_type=Integer.parseInt(parametros[idx]); //1:stream 2:dgram
            }
          }
        }
        lector.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
