package admin;
import java.sql.*;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Set;
import java.util.zip.*;
import orgainfo.*;
import tdutils.Zip;
/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Clase principal de pruebas. 
 */
public class Pruebas {
  public static void prueba001() throws ADMINGLOExcepcion{
    String bd_url="jdbc:hsqldb:adminbd";
    String usuario_bd="";
    String password_bd="";
    Connection connbd;
    DatabaseMetaData metadatos;
    ResultSet resDB;
    try {
      Class.forName ( "org.hsqldb.jdbcDriver" );  // carga el controlador JDBC específico
      connbd = DriverManager.getConnection (      // realiza la conexión
          bd_url,"sa","");
    }
    catch (ClassNotFoundException ex) {
      throw new ADMINGLOExcepcion("No se encontró el controlador para la base de datos.",ex);
    }
    catch (SQLException ex) {
      throw new ADMINGLOExcepcion("Excepción de JDBC.",ex);
    }
    try {
      connbd.setAutoCommit(false);
      metadatos=connbd.getMetaData();
      resDB=metadatos.getTables(null,null,null,null);
      System.out.println(resDB.getFetchSize());
      if(resDB.getFetchSize()<2){
        System.err.println("La base de datos está vacía!!!");
      }
    }
    catch (SQLException ex3) {
      // error si ocurren errores de acceso a la base de datos.
    }
    try {
      connbd.close();
    }
    catch (SQLException ex1) {
    }
  }
  public static void prueba002() throws OIExcepcion,ADMINGLOExcepcion{
    ADMINGLOInfo info=new ADMINGLOInfo(null,null,null,null,null,null,null);
    int estadobd=0;
    estadobd=info.open();
    switch(estadobd){
      case OIConexion.BDINCOMPLETA:
        System.out.println("La base de datos está incompleta.\nSe reconstruirá.");
        info.getConex().script_exec_url("destroy_db.txt");
        info.getConex().script_exec_url("script_db.txt");
        break;
      case OIConexion.BDPREPARADA:
        System.out.println("La base de datos está preparada.");
        break;
      case OIConexion.BDSINCONSTRUIR:
        System.out.println("La base de datos está vacía.\nSe construirá.");
        info.getConex().script_exec_url("script_db.txt");
        break;
    }
    info.close();
  }
  public static void prueba003() throws Exception{
    String direccion="0.0.0.0";
    try {
      direccion=java.net.InetAddress.getLocalHost().getHostAddress();
      direccion=java.net.InetAddress.getLocalHost().getHostName();
    }
    catch (UnknownHostException ex) {
    }
    System.out.println("La dirección del host es: "+direccion);
  }
  public static void prueba004() throws Exception{
    // necesita recursos como:
    // create_db.txt
    // destroy_db.txt
    // .ubicaciones.xml
    // .programas.xml
    ADMINGLOServidor instancia=new ADMINGLOServidor("configs/admin_global.xml",
                                                 "configs/admin_local.xml");
    instancia.open();
    // instancia.close();
  }
  public static void prueba005() throws Exception{
    ADMINGLOServidor instancia=new ADMINGLOServidor("","");
    instancia.open();
    zzADMINSolicitudPrueba solpba=new zzADMINSolicitudPrueba();
    solpba.open(instancia.info);
    String xml="";
    solpba.setFromXMLURI("solicitud.xml");
    xml=solpba.getXMLRootElem(null);
    instancia.close();
    System.out.println(xml);
  }
  public static void prueba006() throws Exception{
    String xml="";
    xml="algo";
    xml=tdutils.tdutils.padR(xml,'X',20);
    System.out.println(xml);
  }
  public static void prueba007() throws Exception{
    PERSCoordinacion.Tareas tareapba=new PERSCoordinacion.Tareas(null,true);
    Inflater infla=new Inflater();
    Deflater desinfla=new Deflater();
    java.util.TreeMap tmArchivos;
    byte[] res0=new byte[100];
    byte[] res1=new byte[100];
    int total=-1;
    desinfla.setInput("aadfasd asdflkasdlf adslfka1sldññññf".getBytes());
    desinfla.finish();
    while((total = desinfla.deflate(res0))!=0){
      System.out.print(new String(res0).trim());
    }
    System.out.print("\n");
    infla.setInput(res0);
    while((total=infla.inflate(res1))!=0){
      System.out.print(new String(res1).trim());
    }
    System.out.print("\n");
    infla.end();

//    tmArchivos=tareapba.comprime("solicitud.xml");
//    tareapba.descomprime(tmArchivos);
  }
  public static void imprimePropiedades(){
    Properties pr = System.getProperties(); // get the system properties
    Set allKeys = pr.keySet();              // turn keys into a set
//     Turn the Set into an array of Strings
    String[] keys = (String[])allKeys.toArray(new String[allKeys.size()]);
    Arrays.sort(keys);  // sort the array
    for (int i=0; i<keys.length; i++) {
     // print each key and its value
     System.out.println(i + " " + keys[i] + "=" + pr.get(keys[i]));
    }
  }
  public static void imprimeDiscoLibre(){
    long capacidad=0;
    capacidad=ADMINPOLLector.capacidadDisco("/tdderive");
    System.out.println("La capacidad del " +
        "disco es de "+capacidad/(1024*1024)+"MB.");
  }
  public static void prueba008(){
    ADMINPOLLector.LectorBajoNivelWIN32 lecWin=null;
    lecWin=new ADMINPOLLector.LectorBajoNivelWIN32();
    System.out.println("La identificación es "+
        lecWin.getIdentificacion()+".");
    synchronized(Thread.currentThread()){
      try {
        Thread.currentThread().wait(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    System.out.println("En disco hay un total de "+
        lecWin.getDiscoTotal("/tdderive")+" megas.");
    System.out.println("En disco hay "+
        lecWin.getDiscoUsado("/tdderive")+" megas usados.");
    System.out.println("Total de memoria: "+
        lecWin.getMemoriaTotal()+"MB.");
    System.out.println("Memoria Usada: "+
        lecWin.getMemoriaUsada()+"MB.");
    System.out.println("Uso de CPU: "+
        lecWin.getCPUPorcentajeUso()+"%.");
    System.out.println("La identificación es "+
        lecWin.getIdentificacion()+".");
    System.out.println("Hay "+
        lecWin.getNumProcesadores()+" procesadores.");
  }
  public static void prueba506(String[] args) throws Exception{
    // necesita recursos como:
    // create_db.txt
    // destroy_db.txt
    // .ubicaciones.xml
    // .programas.xml
    if(args.length>1){
      ADMINGLOServidor servidor=new ADMINGLOServidor( /*global*/ args[0],
                                                         /*local */ args[1]);
      servidor.open();
    }else{
      System.out.println("Solo hay "+ args.length +" parámetros.");
    }
    // instancia.close();
  }  
  public static void prueba507(String[]args) throws Exception{
    String dirAComprimir=null;
    String nombreArchivo=null;
    Calendar calendario=GregorianCalendar.getInstance();
      /*
       * Comprime el directorio de resultados
       * Hace con el directorio de resultados lo que corresponda
       * según lo solicitó el usuario:
       * (o) Escribirlo en el directorio del usuario.
       * (o) Mandarlo por correo electrónico a donde indicó el usuario.
       */
    dirAComprimir="C:\\.Trash-alessandro";
    nombreArchivo="c:\\scripts";
    nombreArchivo=nombreArchivo+"/trash";
    nombreArchivo=nombreArchivo+"-resultado-";    
    nombreArchivo=nombreArchivo+calendario.get(Calendar.YEAR) +"."+(calendario.get(Calendar.MONTH)+1)+"."+calendario.get(Calendar.DAY_OF_MONTH);
    nombreArchivo=nombreArchivo+"_"+calendario.get(Calendar.HOUR_OF_DAY)+"_"+calendario.get(Calendar.MINUTE)+"_"+calendario.get(Calendar.SECOND)+"_"+calendario.get(Calendar.MILLISECOND);
    nombreArchivo=nombreArchivo+".zip";
    try {
      Zip.zipFile(nombreArchivo,dirAComprimir);
    } catch (IOException e) {
      throw new ADMINAPPExcepcion(e);
    }    
  }
  private static void hora(){
    Calendar calendario=GregorianCalendar.getInstance();
    String texto="";
    texto=texto+calendario.get(Calendar.YEAR) +"/"+(calendario.get(Calendar.MONTH)+1)+"/"+calendario.get(Calendar.DAY_OF_MONTH);
    texto=texto+";"+calendario.get(Calendar.HOUR_OF_DAY)+":"+calendario.get(Calendar.MINUTE)+":"+calendario.get(Calendar.SECOND)+":"+calendario.get(Calendar.MILLISECOND);
    System.out.println(texto);
  }
  public static void main(String args[]){
    try {
      prueba506(args);
      // prueba008();
      // hora();
    }
    catch (Exception ex) {
      System.err.println(ex.getMessage());
      ex.printStackTrace();
      System.exit(0);
    }
  }
}