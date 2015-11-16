package aco;
/**
 * Exepción arbitraria de las comunicaciones.
 */
public class ACONExcArbitraria extends ACONExcepcion {

  public ACONExcArbitraria() {
  }

  public ACONExcArbitraria(String message) {
    super(message);
  }

  public ACONExcArbitraria(int tipo, Object ex, int ext,String host,int port) {
    super(tipo, ex, ext,host,port);
  }

  public ACONExcArbitraria(String message, Throwable cause) {
    super(message, cause);
  }

  public ACONExcArbitraria(Throwable cause) {
    super(cause);
  }
}