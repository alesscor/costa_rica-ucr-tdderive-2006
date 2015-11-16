package aco;
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
/***
 * El servidor debe iniciar escuchadon por el puerto 65434.
 */
/**
 * Clase de pruebas.
 * <br></br>
 */
public class PBADGRAM_C  {
  DatagramSocket canal;
  DatagramPacket paquete;
  public PBADGRAM_C() {
    try{
      canal = new DatagramSocket();
      paquete=new DatagramPacket("Mensaje para 001".getBytes(),16,
              new java.net.InetSocketAddress("192.168.0.1",65434));
    }catch(Exception ex){
      System.err.println("Error creando datagrama cliente.");
      ex.printStackTrace();
    }
  }
  public void manda(){
    try {
      canal.send(paquete);
    }catch (IOException ex) {
      ex.printStackTrace();
    }
  }

}