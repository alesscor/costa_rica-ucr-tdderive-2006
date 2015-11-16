package tables;

/**
 * <br></br>
 */

public class Pruebas {

  public static void Prueba1() {
  Pruebas pruebas1 = new Pruebas();
  String cValues;
  String aValues[]=new String[6];
  String cWho="n[1]",nType="tipo",nNodeID="n[j]";
  double nCapacity=1.2,nWorkLoad=2.3,nInfoOfGiving=3.4,nHeight=4.5;
    TABLE oMessages=new TABLE();
    oMessages.open("mensajes.msg",":");
    cValues="false,"+ nType+","+cWho+","+nNodeID+","+nCapacity+","+nWorkLoad+","+nInfoOfGiving+","+nHeight;
    // oMessages.SetOrAddValues("leído°=°nuevo°","leído,tipo,de,para,capacidad,"+
    // "carga,adar,altura",cValues);
    oMessages.getAndSetValues("para°=°"+cWho+"°&&°leído°=°false°",
      "tipo,de,capacidad,carga,adar,altura",aValues,"leído","true");
    oMessages.print();
    oMessages.close();
  }
  public static void Prueba2() {
  AbstractSet absLista1=new AbstractSet("hilera");
    absLista1.addNew("hilera0");
    absLista1.addNew("hilera1");
    absLista1.print("[1]");
    absLista1.addNew("hilera2");
    absLista1.addNew("hilera3");
    absLista1.print("[2]");
    absLista1.addNew("hilera4");
    absLista1.print("[3]");
    absLista1.moveFirst();
    absLista1.update("primera");
    absLista1.moveNext();
    absLista1.moveNext();
    absLista1.update("tercera");
    absLista1.print("[final]");
  }
  public static void Prueba3() {
  AbstractSet absLista1=new AbstractSet(new tupla(0,"",0));
    absLista1.addNew(new tupla(0,"cero",0));
    absLista1.addNew(new tupla(1,"uno",1));
    absLista1.print("[1]");
    absLista1.addNew(new tupla(2,"dos",2));
    absLista1.addNew(new tupla(3,"tres",3));
    absLista1.print("[2]");
    absLista1.addNew(new tupla(4,"cuatro",4));
    absLista1.print("[3]");
    //absLista1.moveFirst();
    //absLista1.update(new tupla(0,"primera",0));
    //absLista1.moveNext();
    //absLista1.moveNext();
    absLista1.moveLast();
    absLista1.update(new tupla(1,"segunda",1),"Índice°=°1°");
    absLista1.update(new tupla(1,"segunda",2),"Índice°=°2°");
    absLista1.print("[4]");
    absLista1.update(new tupla(2,"tercera",2),"Índice°=°1°&&°Dato°=°2°");
    absLista1.print("[5]");
    absLista1.delete("Índice°=°2°&&°Dato°=°2°");
    absLista1.print("[final]");
  }

  public static void main(String[] args) {
    Pruebas.Prueba3();
    System.exit(0);
  }
}