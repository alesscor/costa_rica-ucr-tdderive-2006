package aco;

/**
 * <p>Title: ACO Patrón Aceptador Conectador</p>
 * <p>Description: Implementación del Patrón Aceptador Conectador</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero alesscor@ieee.org
 * @version 1.0
 */
/**
 * Clase de pruebas.
 * <br></br>
 */
public class PBADGRAM_C_2 extends ACONGestor {

  public PBADGRAM_C_2() {
  }

  public PBADGRAM_C_2(ACONDescriptor info0) {
    super(info0);
  }
  public void acepta() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void manejaEvento() {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void open() {
    String mensaje="";
    try {
      this.send("<<Prueba con gestor de servicios en el lado "+
                          "del cliente (datagramas)>>");

      synchronized(Thread.currentThread()){
        try{
          Thread.currentThread().wait(20000);
        }
        catch(Exception ex){
          ex.printStackTrace();
        }
      }
/*
      mensaje=this.receive();
      System.out.println(
          "El mensaje recibido como respuesta del servidor es:\n\"" +
          mensaje + "\"");
*/
      this.close();
    }
    catch (ACONExcTemporizacion ex) {
      System.err.println(ex.getMessage());
    }
    catch (ACONExcOmision ex) {
      System.err.println(ex.getMessage());
    }
    catch (ACONExcArbitraria ex) {
      System.err.println(ex.getMessage());
    }
    catch (ACONExcepcion ex) {
      System.err.println(ex.getMessage());
    }
  }
  public void sirvase() {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void completa() {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
}
