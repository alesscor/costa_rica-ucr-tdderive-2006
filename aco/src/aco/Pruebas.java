package aco;

import mens.*;
import tables.AbstractSet;
import java.net.*;
import java.io.*;
/**
 * <p>Title: ACO Patrón Aceptador Conectador</p>
 * <p>Description: Implementación del Patrón Aceptador Conectador</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero alesscor@ieee.org
 * @version 1.0
 */
/**
 * Clase principal de pruebas.
 * <br></br>
 */
public class Pruebas {
  //////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public static void prueba001(){
    leeACONDescriptor infoXML=new leeACONDescriptor();
    ACONDescriptor infoGestor=null;
    try {
      infoXML.setFromXMLURI("confregistro.xml");
      infoGestor=infoXML.getobj();
    }
    catch (MENSException ex) {
    }
    if(infoGestor!=null){
      System.out.println("todo bien: "+infoGestor.user);
      System.out.println(infoXML.getXMLRootElem());
    }else{
      System.out.println("hubo error");
    }
  }
  public static void prueba002(){
    leeACONRegistro infoXML=new leeACONRegistro();
    ACONDescriptor infoGestor=null;
    AbstractSet lista;
    try {
      infoXML.setFromXMLURI("confregistro.xml");
      lista=infoXML.getlistadescriptores();
      lista.moveFirst();
      while(!lista.getEoF()){
        infoGestor = (ACONDescriptor) lista.getObject();
        System.out.println(infoGestor.id+ " "+infoGestor.remotehost);
        lista.moveNext();
      }
    }
    catch (MENSException ex) {
    }
  }
  public static void prueba003(){
    ACONAceptadorDesp acc=new ACONAceptadorDesp((String)null);
    try{
      acc.registraDescriptores("confregistro.2.xml");
    }catch(Exception ex){
      ex.printStackTrace();
    }
    acc.manejaEventos();
  }
  public static void prueba004(){
    ACONAceptadorDesp acc=new ACONAceptadorDesp((String)null);
    try{
      acc.registraDescriptores("confregistro.xml");
    }catch(Exception ex){
      ex.printStackTrace();
    }
    acc.manejaEventos();
    synchronized(Thread.currentThread()){
      try {
        Thread.currentThread().wait(1500);
      }
      catch (InterruptedException ex) {
        System.err.println("Error en hilo.");
      }
    }
    System.out.println("iniciando el cliente");
    PBADGRAM_C dgrams=new PBADGRAM_C();
    dgrams.manda();
    PBASTREAM_C streams=new PBASTREAM_C();
    streams.trabaja();
  }
  public static void prueba005(){
    ACONConectorDesp conn=new ACONConectorDesp();
    ACONAceptadorDesp acc=new ACONAceptadorDesp((String)null);
    ACONDescriptor infoClienteD=new ACONDescriptor();
    ACONDescriptor infoClienteS=new ACONDescriptor();

    infoClienteD.localport=65433;
    infoClienteD.localhost="localhost";
    infoClienteD.remoteport=65434;
    infoClienteD.remotehost="192.168.0.1";
    infoClienteD.socket_type=ACONDescriptor.DGRAM;
    infoClienteD.wait=false;

    infoClienteS.localport=55436;
    infoClienteS.localhost="localhost";
    infoClienteS.remoteport=65435;
    infoClienteS.remotehost="192.168.0.1";
    infoClienteS.socket_type=ACONDescriptor.STREAM;
    infoClienteS.wait=false;

    PBADGRAM_C_2 gestordgrams2=new PBADGRAM_C_2(infoClienteD);
    PBASTREAM_C_2 gestorstreams2=new PBASTREAM_C_2(infoClienteS);
    // inicia el servidor
    try{
      acc.registraDescriptores("confregistro.xml");
    }catch(Exception ex){
      ex.printStackTrace();
    }
    acc.manejaEventos();
    synchronized(Thread.currentThread()){
      try {
        Thread.currentThread().wait(1500);
      }
      catch (InterruptedException ex) {
        System.err.println("Error en hilo.");
      }
    }
    // inicia conexión de los clientes
    try{
      conn.conecta(gestorstreams2);
      conn.conecta(gestordgrams2);
    }catch(ACONExcepcion ex){
      ex.printStackTrace();
    }
    synchronized(Thread.currentThread()){
      try {
        Thread.currentThread().wait(20000);
      }
      catch (InterruptedException ex) {
        System.err.println("Error en hilo.");
      }
    }
    System.out.println(acc.getStatusDescriptores());
    acc.close();
    System.out.println(acc.getStatusGestores());
    /*
    ACONDescriptor info=new ACONDescriptor();
    info.localport=65429;
    info.id="001";
    info.server="aco.PBAOTRA_S";
    if(acc.op_findInfoRegGestor(info)!=null){
      System.out.println("Sí se encuentra el servicio.");
    }else{
      System.out.println("No se encuentra el servicio.");
    }
    */
  }
  public static void prueba006(){
    System.out.println(
        new java.text.SimpleDateFormat("y/MM/dd HH:mm:ss").format(
          new java.util.GregorianCalendar().getTime()));
  }
  public static void prueba007(){
    String texto=null;
    // para pruebas en red   v
    boolean inCompa=false;
    try {
      texto=System.getProperty("PBACOMPA");
      if(texto==null){
        inCompa=false;
      }else if(texto.compareToIgnoreCase("true")==0){
        inCompa=true;
      }else if(texto.compareToIgnoreCase("verdadero")==0){
        inCompa=true;
      }
    }
    catch (Exception ex) {
      inCompa=false;
    }
    // para pruebas en red   ^
    // en compa no van a haber clientes
    if(!inCompa){
      ACONConectorDesp conn = new ACONConectorDesp();
      ACONDescriptor infoClienteD=new ACONDescriptor();
      ACONDescriptor infoClienteS=new ACONDescriptor();

      infoClienteD.localport=65033;
      infoClienteD.localhost="localhost";
      infoClienteD.remoteport=65434;
      infoClienteD.remotehost="192.168.0.3";
      infoClienteD.socket_type=ACONDescriptor.DGRAM;
      infoClienteD.wait=false;

      infoClienteS.localport=55036;
      infoClienteS.localhost="localhost";
      infoClienteS.remoteport=65435;
      infoClienteS.remotehost="192.168.0.3";
      infoClienteS.socket_type=ACONDescriptor.STREAM;
      infoClienteS.wait=false;

      PBADGRAM_C_2 gestordgrams2=new PBADGRAM_C_2(infoClienteD);
      PBASTREAM_C_2 gestorstreams2=new PBASTREAM_C_2(infoClienteS);
/*      synchronized(Thread.currentThread()){
        try {
          Thread.currentThread().wait(1500);
        }
        catch (InterruptedException ex) {
          System.err.println("Error en hilo.");
        }
      }
*/
      // inicia conexión de los clientes
      try{
        conn.conecta(gestorstreams2);
        conn.conecta(gestordgrams2);
      }
      catch(ACONExcArbitraria ex){
        ex.printStackTrace();
      }
      catch(ACONExcOmision ex){
        ex.printStackTrace();
      }
    }else{
      // inCompa==true
      ACONAceptadorDesp acc = new ACONAceptadorDesp((String)null);
      // inicia el servidor
      try{
        acc.registraDescriptores("confregistro.xml");
      }
      catch(ACONExcArbitraria ex){
        ex.printStackTrace();
      }
      acc.manejaEventos();
      synchronized(Thread.currentThread()){
        try {
          Thread.currentThread().wait(20000);
        }
        catch (InterruptedException ex) {
          System.err.println("Error en hilo.");
        }
      }
      System.out.println(acc.getStatusDescriptores());
      //acc.close();
      //System.out.println(acc.getStatusGestores());
    }
  }
  public static void prueba008(){
    String valor="";
    ACONAceptadorDesp acc=new ACONAceptadorDesp((String)null);
    try {
      acc.registraDescriptores("confregistro.xml");
      acc.manejaEventos();
    }
    catch (ACONExcArbitraria ex) {
      System.err.println("No se pudo abrir el despachador");
      return;
    }
    BITAServidor servidor=new BITAServidor(".bita.xml");
    valor="<fecha>";
    valor+= new java.text.SimpleDateFormat("yyyy/MM/dd").format(new java.util.GregorianCalendar().getTime());
    valor+="</fecha>";
    valor+="\n<hora>";
    valor+=new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.GregorianCalendar().getTime());
    valor+="</hora>";
    servidor.addRegistro("primer registro\n"+ valor);
    valor=servidor.getRegistros(BITAServidor.DEME_SUPUESTO_COMPLETO);
    System.out.println(valor);
    valor="<fecha>";
    valor+= new java.text.SimpleDateFormat("yyyy/MM/dd").format(new java.util.GregorianCalendar().getTime());
    valor+="</fecha>";
    valor+="\n<hora>";
    valor+=new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.GregorianCalendar().getTime());
    valor+="</hora>";
    servidor.addRegistro("segundo registro\n"+ valor);
    valor=servidor.getRegistros(BITAServidor.DEME_SUPUESTO_COMPLETO);
    System.out.println(valor);
    servidor.flushRegistros();
    System.out.println("------------------");

  }
  public static void prueba009(){
    String texto=null,valor="";
    // para pruebas en red   v
    boolean inCompa=false;
    boolean isConsulta=false;
    try {
      texto=System.getProperty("PBACOMPA");
      if(texto==null){
        inCompa=false;
      }else if(texto.compareToIgnoreCase("true")==0){
        inCompa=true;
      }else if(texto.compareToIgnoreCase("verdadero")==0){
        inCompa=true;
      }
    }
    catch (Exception ex) {
      inCompa=false;
    }
    try {
      texto=System.getProperty("PBACONSULTA");
      if(texto==null){
        isConsulta=false;
      }else if(texto.compareToIgnoreCase("true")==0){
        isConsulta=true;
      }else if(texto.compareToIgnoreCase("verdadero")==0){
        isConsulta=true;
      }
    }
    catch (Exception ex) {
      inCompa=false;
    }
    // para pruebas en red   ^
    // en compa no van a haber clientes
    //o inCompa=true;
    if(inCompa){
      // servidor
      ACONAceptadorDesp acc=new ACONAceptadorDesp((String)null);
      try {
        acc.registraDescriptores("confregistro.xml");
        acc.manejaEventos();
      }
      catch (ACONExcArbitraria ex1) {
        System.err.println("No se pudo iniciar el servidor, error en despachador.");
        return;
      }
      BITAServidor servidor=new BITAServidor(".bita.xml");
      try {
        servidor.open(acc);
      }
      catch (Exception ex) {
        System.err.println("Error en BITAServicio.");
        ex.printStackTrace();
      }
      synchronized(Thread.currentThread()){
        try {
          Thread.currentThread().wait(1000*60*20); // 20 minutos
        }
        catch (InterruptedException ex) {
          System.err.println("Error en hilo.");
        }
      }
      servidor.close();
    }else{
      isConsulta=true;
      if(!isConsulta){
        /**
         * cliente manda datagramas --------------------v
         */
       valor="<fecha><comm>otra vez</comm>";
       valor+= new java.text.SimpleDateFormat("yyyy/MM/dd").format(
            new java.util.GregorianCalendar().getTime());
       valor+="</fecha>";
       valor+="\n<hora>";
       valor+=new java.text.SimpleDateFormat("HH:mm:ss").format(
            new java.util.GregorianCalendar().getTime());
       valor+="</hora>";
       try {
          java.net.DatagramSocket datagramas = new java.net.DatagramSocket();
          datagramas.send(new java.net.DatagramPacket(valor.getBytes(),valor.length(),
                          new java.net.InetSocketAddress("192.168.0.3", 4412)));
          datagramas.close();
        }
        catch (SocketException ex) {
          ex.printStackTrace();
        }
        catch (java.io.IOException ex) {
          ex.printStackTrace();
        }
        /**
         * cliente manda datagramas --------------------^
         */
      }else{
        /**
         * cliente pide consulta -----------------------v
         */
        valor=BITAServidor.cDEME_SUPUESTO_COMPLETO+"quiero ver";
        java.net.Socket streamcliente =new java.net.Socket();
        try {
          streamcliente.connect(new java.net.InetSocketAddress("192.168.0.3",
              4413));
          java.io.DataOutputStream salida=new java.io.DataOutputStream(
              streamcliente.getOutputStream());
          java.io.DataInputStream entrada=new java.io.DataInputStream(
              streamcliente.getInputStream());
          synchronized(Thread.currentThread()){
            try {
              Thread.currentThread().wait(1000); // un segundo
            }
            catch (InterruptedException ex) {
              System.err.println("Error en espera.");
            }
          }
          //valor=entrada.readUTF();
          salida.writeUTF(valor);
          valor=entrada.readUTF();
          System.out.println("---------------------");
          System.out.println(valor);
          System.out.println("---------------------");
          streamcliente.close();
        }
        catch (IOException ex) {
          ex.printStackTrace();
        }
        /**
         * cliente pide consulta -----------------------^
         */
      }
    }
  }
  public static void prueba010(){
    String texto = null, valor = "";
    // para pruebas en red   v
    boolean inCompa = false;
    boolean isConsulta = false;
    try {
      texto = System.getProperty("PBACOMPA");
      if (texto == null) {
        inCompa = false;
      }
      else if (texto.compareToIgnoreCase("true") == 0) {
        inCompa = true;
      }
      else if (texto.compareToIgnoreCase("verdadero") == 0) {
        inCompa = true;
      }
    }
    catch (Exception ex) {
      inCompa = false;
    }
    try {
      texto = System.getProperty("PBACONSULTA");
      if (texto == null) {
        isConsulta = false;
      }
      else if (texto.compareToIgnoreCase("true") == 0) {
        isConsulta = true;
      }
      else if (texto.compareToIgnoreCase("verdadero") == 0) {
        isConsulta = true;
      }
    }
    catch (Exception ex) {
      inCompa = false;
    }
    // para pruebas en red   ^
    //oinCompa=true;
    if (inCompa) {
      ACONAceptadorServidor aconservicio = new ACONAceptadorServidor(
          "servidor.xml");
      try {
        aconservicio.open(null);
      }
      catch (ACONExcArbitraria ex) {
        System.err.println("Error en la apertura del servidor principal ACON.");
        ex.printStackTrace();
      }
    }else {
      isConsulta=true;
      if (isConsulta) {
        /**
         * cliente pide consulta -----------------------v
         */
        // valor=BITAServidor.cDEME_SUPUESTO_COMPLETO+"quiero ver";
        valor=ACONAceptadorServidor.cDEME_BITACORA_SUPUESTO_COMPLETO +"quE es";
        //valor=BITAServidor.cDEME_SUPUESTO_COMPLETO +"quiero ver";
        java.net.Socket streamcliente =new java.net.Socket();
        try {
          streamcliente.connect(new java.net.InetSocketAddress("192.168.0.1",
              3310));
          java.io.DataOutputStream salida=new java.io.DataOutputStream(
              streamcliente.getOutputStream());
          java.io.DataInputStream entrada=new java.io.DataInputStream(
              streamcliente.getInputStream());
          synchronized(Thread.currentThread()){
            try {
              Thread.currentThread().wait(1000); // un segundo
            }
            catch (InterruptedException ex) {
              System.err.println("Error en espera.");
            }
          }
          //valor=entrada.readUTF();
          salida.writeUTF(valor);
          valor=entrada.readUTF();
          System.out.println("---------------------");
          System.out.println(valor);
          System.out.println("---------------------");
          streamcliente.close();
        }
        catch (IOException ex) {
          ex.printStackTrace();
        }
        /**
         * cliente pide consulta -----------------------^
         */
      }else {
        /**
         * cliente manda datagramas --------------------v
         */
       valor="<fecha><comm>otra vez</comm>";
       valor+= new java.text.SimpleDateFormat("yyyy/MM/dd").format(
            new java.util.GregorianCalendar().getTime());
       valor+="</fecha>";
       valor+="\n<hora>";
       valor+=new java.text.SimpleDateFormat("HH:mm:ss").format(
            new java.util.GregorianCalendar().getTime());
       valor+="</hora>";
       try {
          java.net.DatagramSocket datagramas = new java.net.DatagramSocket();
          datagramas.send(new java.net.DatagramPacket(valor.getBytes(),valor.length(),
                          new java.net.InetSocketAddress("192.168.0.1", 4412)));
          datagramas.close();
        }
        catch (SocketException ex) {
          ex.printStackTrace();
        }
        catch (java.io.IOException ex) {
          ex.printStackTrace();
        }
        /**
         * cliente manda datagramas --------------------^
         */
      }
    }
  }
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public static void main(String[] args) {
    prueba010();
  }
  //////////////////////////////////////////////////////////////////////
}