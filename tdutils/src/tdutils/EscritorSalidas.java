/*
 * Creado el 18/11/2004
 * por Alessandro
 */
package tdutils;

/**
 * Clase dedicada a la escritura de mensajes en salida estándar
 * cuando corresponda, y a llamar a un complemento que maneje 
 * también el mensaje.
 */
public class EscritorSalidas {
  /**
   * Objeto invocable que sirve de complemento a la lectura
   * de archivos.
   */
  private Invocable _invocable;
  private boolean _paraSalida;
  private boolean _paraComplemento;
  /**
   * Inicia el objeto escritor. Revisa si una propiedad del
   * sistema tiene valor 1, 2 o 3, donde se activa el escritor
   * para escribir en consola, sólo complemento o ambos respectivamente.
   * @param texto El nombre de la propiedad del sistema a buscar en el
   * ambiente.
   */
  public EscritorSalidas(String texto) {
    _inicio(texto);
  }
  private void _inicio(String texto){
    _invocable=null;
    _paraSalida=System.getProperty(texto)!=null;
    _paraComplemento=false;
    if(_paraSalida){
      _paraSalida=false;
      texto=System.getProperty(texto);
      _paraSalida=texto.compareToIgnoreCase("1")==0;
      if(!_paraSalida){
        _paraSalida=texto.compareToIgnoreCase("3")==0;
        _paraComplemento=_paraSalida;
      }
      if(!_paraComplemento){
        _paraComplemento=texto.compareToIgnoreCase("2")==0;
      }
    }    
  }
  /**
   * Escribe el mensaje si, es que ello debe realizarse,
   * en el lugar que corresponde.
   * @param mensaje El mensaje a escribir.
   */
  public void escribeMensaje(String mensaje){
    escribeMensaje(mensaje, false,false);
  }
  /**
   * Escribe el mensaje si, es que ello debe realizarse,
   * en el lugar que corresponde.
   * @param mensaje El mensaje a escribir.
   * @param siError Si se debe escribir al error estándar.
   */
  public void escribeMensaje(String mensaje,boolean siError){
    escribeMensaje(mensaje, siError,false);
  }  
  /**
   * Como sus similares, solo que agregando automáticamente
   * información sobre el hilo de control actual.
   */
  public void escribeMensajeInfoHilo(String mensaje){
    escribeMensaje(mensaje, false,true);
  }  
  /**
   * Como sus similares, solo que agregando automáticamente
   * información sobre el hilo de control actual.
   */
  public void escribeMensajeInfoHilo(String mensaje,boolean siError){
    escribeMensaje(mensaje, siError,true);
  }  
  /**
   * Escribe el mensaje si, es que ello debe realizarse,
   * en el lugar que corresponde.
   * @param mensaje El mensaje a escribir.
   * @param siError Indica si el mensaje debe escribirse al error
   * estándar.
   */
  public void escribeMensaje(String mensaje,boolean siError,
      boolean siInfoHilo){
    if(_paraSalida || _paraComplemento){
      if(mensaje==null){
        mensaje="(vacio)";
      }
      if(siInfoHilo){
        mensaje=mensaje + " [hilo:"+Thread.currentThread().hashCode()+"]";
      }
      if(_paraSalida){
        if(siError){
          System.err.println(mensaje);
        }else{
          System.out.println(mensaje);
        }
      }
      if(_paraComplemento){
        if(_invocable!=null){
          try{
            _invocable.invoca(new Object[]{mensaje,new Boolean(siError)});
          }catch(Exception exc){
            // error, se ignora.
          }
        }
      }
    }
  }
  /**
   * Invocable a complementar el despliegue del mensaje.
   * @param invocable Invocable a asignar, requiere que su invocación
   * reciba primero un String y luego un Boolean.
   */
  public void setComplemento(Invocable invocable){
    _invocable=invocable;
  }
}
