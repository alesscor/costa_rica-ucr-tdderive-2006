package mens;
import org.w3c.dom.*;

/**
 * <p>Title: Mens Mensajes con vida</p>
 * <p>Description: Implementación a usar para objetos activos</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero alesscor@ieee.org
 * @version 1.0
 */

/***
 * Se usa un patrón que se comporta así:
 * define un método definitivo, el cual utiliza instrucciones
 * que son cambiantes por medio de subclases (métodos abstractos
 * protegidos). Entre esos métodos hay uno muy interesante llamado
 * toleraXML.
 */

public class Pruebas extends MENSMensaje {
  //////////////////////////////////////////////////////////////////////
  public int campo1;
  public String campo2;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public static void pruebaTosco(){
    MENSTosco tosco=new MENSTosco();
    String mensaje="<?xml version=\"1.0\"?>"+
        "<solicitud>" +
        "<className algo=\"alguna cosa\" otra=\"otra cosa\">" +
        "activetdderive.TDPruebaMate" +
        "</className>" +
        "<funcion>" +
        "<x>" +
        "2.0" +
        "</x>" +
        "<y>" +
        "0.0<!-- algo algo\nalgo algo -->" +
        "</y>" +
        "</funcion>" +
        "</solicitud>";
    try {
//      tosco.setFromXMLSource(mensaje);
      tosco.loadFromString(mensaje);
      System.out.println("Todo bien.");
    }
    catch (MENSException ex) {
      System.out.println("Todo mal.");
    }
  }
  public boolean isVacio(){
    return ((campo2=="") && (campo1==0));
  }
  public static void prueba1(){
    final Pruebas prueba1 = new Pruebas();
    prueba1.campo1=12;
    prueba1.campo2="Hola";
    try{
      //prueba1.setField("campo2", "sirv.");
      //prueba1.setField("campo1", 33);
      System.out.println(prueba1.getField("campo1") + " " +
                         prueba1.getField("campo2"));
      prueba1.setFromXMLURI("fuente.txt");
      System.out.println(prueba1.getXMLRootElem("mensaje1"));
    }catch(Exception except){
      except.printStackTrace();
    }
  }
  public static void prueba2(){
//    final TDInfo_Nodo inodo = new TDInfo_Nodo();
//    try{
//      inodo.setFromXMLURI("fuente.txt");
//      System.out.println(inodo.getXMLRootElem("mensaje1"));
//    }catch(Exception except){
//      except.printStackTrace();
//    }
  }
  public static void prueba3(){
//    final TDInfo_Nodos inodos = new TDInfo_Nodos();
//    try{
//      inodos.setFromXMLURI("fuente.txt");
//      System.out.println(inodos.getXMLRootElem("mensaje1"));
//    }catch(Exception except){
//      except.printStackTrace();
//    }
  }
  public static void prueba4(){
    MENSTosco tos=new MENSTosco();
    try{
      tos.setFromXMLURI("fuente.txt");
    }catch(Exception except){
      System.err.println(except.getMessage());
      except.printStackTrace();
    }
//    System.out.println(tos.getContenido());
    System.out.println(tos.getXMLRootElem("tosca"));
  }
  public static void main(String[] args) {
    pruebaTosco();
    System.exit(0);
  }
  protected String getXMLContainedElements(){
    return "<contenido>\n"+
           "  <campo1>"+campo1+"</campo1>\n"+
           "  <campo2>"+campo2+"</campo2>\n"+
           "</contenido>\n";
  }
  protected void setContentFromDoc(Node doc,int problema[],String mensaje[]){
    mensaje[0]="";
    problema[0]=0;
    try{
      campo1 = Integer.parseInt(getElementText(doc, "campo1"));
    }catch(Exception except){
      problema[0]=3;
      mensaje[0]="problema al jalar campo1\n";
    }
    campo2=getElementText(doc,"campo2");
    if(campo2==""){
      problema[0]=3;
      mensaje[0]+="problema al jalar campo2\n";
    }
  }
  protected void toleraXML(int problema[],String mensaje[]){
//    if(this.getNodo()!=""){
      problema[0]=0;
      mensaje[0]="";
//    }
  }
}