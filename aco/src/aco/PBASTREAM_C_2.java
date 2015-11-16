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
public class PBASTREAM_C_2 extends ACONGestor {

  public PBASTREAM_C_2() {
  }

  public PBASTREAM_C_2(ACONDescriptor info0) {
    super(info0);
  }
  public void acepta() throws aco.ACONExcepcion {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void manejaEvento() {
    /**@todo Implement this aco.ACONGestor abstract method*/
  }
  public void open() {
    /**@todo Implement this aco.ACONGestor abstract method*/
    String mensaje;
    try {
      System.out.println("Cliente trabajando normalmente (streams).");
      mensaje=this.receive();
      System.out.println("Se ha recibido el mensaje del servidor:\n\""+mensaje+"\".");
      this.send("Tome, otra vez:\n\""+mensaje+"\"");
      this.close();
    }
    catch (ACONExcOmision ex) {
      System.err.println("Error de ACON.");
      ex.printStackTrace();
    }
    catch (ACONExcTemporizacion ex) {
      System.err.println("Error de ACON.");
      ex.printStackTrace();
    }
    catch (ACONExcArbitraria ex) {
      System.err.println("Error de ACON.");
      ex.printStackTrace();
    }
    catch (ACONExcepcion ex) {
      System.err.println("Error de ACON.");
      ex.printStackTrace();
    }
    catch(Exception ex){
      System.err.println("Error.");
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
