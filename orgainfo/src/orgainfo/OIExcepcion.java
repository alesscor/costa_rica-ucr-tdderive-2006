package orgainfo;

/**
 * Excepci�n general a nivel de organizaci�n de informaci�n.
 */
public class OIExcepcion extends Exception{
    public OIExcepcion() {
    }

    public OIExcepcion(String message) {
      super(message);
    }

    public OIExcepcion(String message, Throwable cause) {
      super(message, cause);
    }

    public OIExcepcion(Throwable cause) {
      super(cause);
    }
}