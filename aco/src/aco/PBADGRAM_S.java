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
public class PBADGRAM_S extends ACONGestor {

  public PBADGRAM_S() {
  }

  public PBADGRAM_S(ACONDescriptor info0) {
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
      mensaje = this.receive();
      System.out.println("Fue recibido del cliente el mensaje:\n\""+
                         mensaje+"\"");
      this.send("Recibí de usted:\n\""+mensaje+"\"");
      try{
        this.close();
      }catch(ACONExcepcion ex){
        ex.printStackTrace();
      }
    }
    catch (ACONExcOmision ex) {
      System.err.println(ex.getMessage());
      ex.printStackTrace();
    }
    catch (ACONExcArbitraria ex) {
      System.err.println(ex.getMessage());
      ex.printStackTrace();
    }
    catch (ACONExcTemporizacion ex) {
      System.err.println(ex.getMessage());
      ex.printStackTrace();
    }
  }
  public void sirvase() {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void completa() {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
}