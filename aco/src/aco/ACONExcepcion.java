package aco;

/**
 * Exepción genérica en las comunicaciones.
 */
public class ACONExcepcion extends Exception {
  // para el valor de tipo
  public final static int CONEXION=1;
  public final static int CIERRE=2;
  public final static int EMISION=4;
  public final static int RECEPCION=8;
  // para el valor de ext
  public final static int ACTIVO=1;  // 2^0
  public final static int PASIVO=2;  // 2^1
  public ACONExcepcion() {
  }

  public ACONExcepcion(String message) {
    super(message);
  }
  public ACONExcepcion(int tipo, Object ex,int ext,String host,int port) {
    super(ACONExcepcion.getMessage(tipo, ex,ext,host,port));
    if(ex!=null){
      if(ex instanceof Exception){
        this.initCause((Exception) ex);
      }
    }
  }

  public ACONExcepcion(String message, Throwable cause) {
    super(message, cause);
  }

  public ACONExcepcion(Throwable cause) {
    super(cause);
  }
  public static String getMessage(int tipo,Object ex,int ext,String host,int port){
    String err_msg="";
    boolean detectado=false;
    if((ext & ACTIVO)!=0){
      err_msg="En el objeto activo. ";
    }
    if((ext & PASIVO)!=0){
      err_msg="En el objeto pasivo. ";
    }
    switch(tipo){
      // errores de conexión  v
      case (CONEXION):
        if (ex != null) {
          if(ex instanceof java.net.SocketTimeoutException){
            err_msg+="Tiempo de espera vencido.";
            detectado=true;
          }
          if (ex instanceof java.io.IOException) {
            err_msg += "Error de omisión " +
                "de bajo nivel al iniciar conexión.";
            detectado=true;
          }
          if (ex instanceof java.nio.channels.
              IllegalBlockingModeException) {
            err_msg += "Error aleatorio " +
                "al iniciar conexión ya asignada.";
            detectado=true;
          }
          if (ex instanceof IllegalArgumentException) {
            err_msg += "Error aleatorio " +
                "en solicitud mal formada de conexión.";
            detectado=true;
          }
          if(!detectado){
            err_msg +="Error aleatorio en la conexión.";
            if(ex instanceof java.lang.Exception){
              err_msg += "\n" + ((java.lang.Exception)ex).getMessage();
            }
          }
        }else{
          err_msg+="Error en la conexión.";
        }
        break;
        // errores de conexión  ^
        // errores de cierre de punto de comunicación v
      case CIERRE:
        err_msg+="Error al cerrar comunicación.";
        if(ex!=null){
          if(ex instanceof java.lang.Exception){
            err_msg += "\n" + ((java.lang.Exception)ex).getMessage();
          }
        }
        break;
        // errores de cierre de punto de comunicación ^
        // errores en envío o recepción v
        case EMISION:
        case RECEPCION:
          if(tipo==EMISION){
            err_msg+="Error en el envío.";
          }else{
            err_msg+="Error en la recepción.";
          }
          if(ex!=null){
/*
             Para datagramas:
             IOException
             SecurityException
             PortUnreachableException
             IllegalBlockingModeException
*/
            if(ex instanceof java.lang.Exception){
              if(ex instanceof java.net.SocketTimeoutException){
                err_msg+="Tiempo de espera vencido.";
              }
              if(ex instanceof java.io.IOException){
                err_msg+="Error de omisión en las comunicaciones.";
              }
              err_msg += "\n" + ( (java.lang.Exception) ex).getMessage();
            }
          }
          break;
        // errores en envío o recepción ^
      default:
        err_msg += "ACO detecta error no determinado.";
        if(ex!=null){
          if(ex instanceof java.lang.Exception){
            err_msg += "\n" + ((java.lang.Exception)ex).getMessage();
          }
        }
    }
    return err_msg+ " "+host+":"+port+".";
  }

}