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
/**
 * Clase de pruebas.
 * <br></br>
 */
public class PBASTREAM_C {
  Socket canal=null;
  public PBASTREAM_C() {
    try {
      synchronized(Thread.currentThread()){
        Thread.currentThread().wait(20000);
      }
    }catch(Exception ex1){
      System.err.println("Error en la espera.");
    }
    try {
        canal = new Socket();
        // asigna dirección local
        canal.bind(new java.net.InetSocketAddress(65436));
        // asigna dirección remota
        canal.connect(new java.net.InetSocketAddress("192.168.0.1",65435));
        System.out.println("Conectando socket en el lado del cliente.");
        // canal.connect(new java.net.InetSocketAddress("192.168.0.1",65435));
    }catch (IOException ex) {
      System.err.println("Error en la conexión.");
      ex.printStackTrace();
    }
    catch(Exception ex1){
      System.err.println("Error en la espera.");
      ex1.printStackTrace();
    }
  }
  public void trabaja(){
    String entrada="";
    if(canal==null){
      System.err.println("El canal es nulo.");
      return;
    }
    if(!canal.isConnected()){
      System.err.println("No hay conexón.");
      return;
    }
    try {
      java.io.DataInputStream fin = new DataInputStream(canal.getInputStream());
      java.io.DataOutputStream fout = new DataOutputStream(canal.getOutputStream());
      System.out.println("Trabajando en el lado del cliente.");
      do{
        entrada+=fin.readUTF();
        System.out.println("Leyendo "+entrada);
      }while(fin.available()>0);
      System.out.println("Fue recibido del servidor el mensaje:\n\""+
                         entrada+"\"");
    }
    catch (IOException ex) {
      System.err.println("Error al leer o escribir.");
    }
  }
}