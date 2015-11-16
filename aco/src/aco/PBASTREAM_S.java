package aco;

/**
 * <p>Title: ACO Patr�n Aceptador Conectador</p>
 * <p>Description: Implementaci�n del Patr�n Aceptador Conectador</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero alesscor@ieee.org
 * @version 1.0
 */
/**
 * Clase de pruebas.
 * <br></br>
 */
public class PBASTREAM_S extends ACONGestor {

  public PBASTREAM_S() {
  }

  public PBASTREAM_S(ACONDescriptor info0) {
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
      System.out.println("Servidor trabajando normalmente.");
      this.send("Esto deber�a ser una hablada bien larga.\nBien, bien larga\n");
      System.out.println("Ya envi� mensaje.");
      mensaje=this.receive();
      System.out.println("El cliente me mand�:\n\""+mensaje+"\"");
      this.close();
    }
    catch (ACONExcOmision ex) {
      System.err.println("Error de omisi�n.");
      ex.printStackTrace();
    }
    catch (ACONExcArbitraria ex) {
      System.err.println("Error arbitrario.");
      ex.printStackTrace();
    }
    catch (ACONExcTemporizacion ex) {
      System.err.println("Error de temporizaci�n.");
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