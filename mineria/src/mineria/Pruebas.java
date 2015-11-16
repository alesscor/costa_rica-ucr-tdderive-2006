package mineria;
import java.io.*;

import tdutils.tdutils;
// import admin.*;
import java.util.Map;

import admin.ADMINAPPIArchivos;
import admin.ADMINAPPITareas;
import admin.ADMINAPPIProgramas;
import admin.ADMINAPPExcepcion;
import admin.ADMINAPPITrabajos;
import admin.ADMINAPPExcPreejecucion;
import admin.ADMINAPPISub_trabajos;
import admin.ADMINAPPISolicitantes;
/**
 * <p>Title: Capa de minería de datos</p>
 * <p>Description: Minería de datos para tdderive especializada en dderive.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCR - ECCI</p>
 * @author Alessandro Cordero alesscor@ieee.org
 * @version 1.0
 */

public class Pruebas {
  public static void prueba506(String[] argumentos){
    MINERIniciador iniciador=null;
//    if(argumentos.length>0 ){
//      iniciador=new MINERIniciador(argumentos[0]);
//    }else{
      iniciador=new MINERIniciador("configs/oact.xml");
//    }
    try {
      iniciador.ejecuta(argumentos);
    }
    catch (ADMINAPPExcepcion ex) {
      System.err.println("Error en la ejecución del iniciador.");
      ex.printStackTrace(System.err);
    }
  }
  public static void prueba003(String[] argumentos){
    int cod;
    String texto1="",texto2="";
    File ofDirectorio=new File("c:/scripts/dderive/experimentos/eclipse");
    Runtime objetoEjecucion;
    Process objetoEjecutado=null;
    objetoEjecucion=Runtime.getRuntime();
    BufferedReader brError,brSalida;
    if(!ofDirectorio.exists()){
      ofDirectorio.mkdirs();
    }
    FilenameFilter ofFiltraLCKs=tdutils.creaFiltroPrefijoArchivos("DT");
    try {
      /*
       * no usar opciones -v ni -d
       */
      objetoEjecutado=objetoEjecucion.exec("c:\\scripts\\dderive.exe " +        "-ftenis.txt -s -I -F ",null,ofDirectorio);
//      "-p01 -s -I -F > res.res 2>&1",null,ofDirectorio);
//      brError=new BufferedReader(new java.io.InputStreamReader(
//          objetoEjecutado.getErrorStream()));
//      brSalida=new BufferedReader(new java.io.InputStreamReader(
//          objetoEjecutado.getInputStream()));
//      while((texto1=brSalida.readLine())!=null){
//        System.out.println(texto1);
//      }
//      while((texto2=brError.readLine())!=null){
//        System.err.println(texto2);
//      }
      cod=objetoEjecutado.waitFor();
      if(cod!=0){
        System.out.println("----------- No cero.");
      }else{
        System.out.println("----------- Cero.");
      }
    } catch (IOException e) {
      System.err.println("No se pudo ejecutar dderive.");
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    int conteo=0;
    /**
     * Filtra archivos con sufijo .lck, dejando por fuera los directorios
     * con tal terminación. 
     */
//    if(ofDirectorio.isDirectory()){
//      while(conteo<6){
//        conteo=ofDirectorio.listFiles(ofFiltraLCKs).length;
//        System.out.println("Hay "+conteo+" objetos en el directorio.");
//      }
//      System.out.println("Ya hay 6 objetos o más en el directorio.");      
//    }
  }
  public static void prueba002(String[] argumentos){
    File ofDirectorio=new File("c:/scripts/dderive/experimentos/eclipse");
    int conteo=0;
    /**
     * Filtra archivos con sufijo .lck, dejando por fuera los directorios
     * con tal terminación. 
     */
    FilenameFilter ofFiltraLCKs=tdutils.creaFiltroSufijoArchivos(".lck");
    if(!ofDirectorio.exists()){
      ofDirectorio.mkdirs();
    }
    if(ofDirectorio.isDirectory()){
      conteo=ofDirectorio.listFiles(ofFiltraLCKs).length;
      System.out.println("Hay "+conteo+" objetos en el directorio.");
    }
  }
  public static void prueba2006(String args[]){
    // programa dderive
    final ADMINAPPIProgramas programa=new ADMINAPPIProgramas(){
      public String getAlias(){
        return "dderive";
      }
      public String getNombreAplicacion(){
        return "Derivación de árboles de inducción";
      }
      public String getRuta(){
        return "C:/scripts/dderive.exe";
      }
      public String getClase(){
        return "mineria.MINERControlador";
      }
      public String getDivisora(){
        return "mineria.MINERDivisoras";
      }
      public String getUnificadora(){
        return "mineria.MINERUnificadoras";
      }
      public long getTiempoEnSistema(){
        return 2100000;
      }
      public long getPeriodoConfirmacion(){
        return 2100000;
      }
      public long getUmbralEspera(){
        return 2100000;
      }
      public boolean getSiCambiarCompu(){
        return true;
      }

    };
    final ADMINAPPITareas tarea=new ADMINAPPITareas(){
      public String getTareaDir(){
        return "C:/MyProjects/eclipse/mineria/experimentos/arbol2006";
      }
      public String getResultadosDir(){
        return getTareaDir();
      }
      public String getComprimidosDir(){
        return getTareaDir();
      }
      public String getBloquesDir(){
        return getTareaDir();
      }
      public String getIdTarea(){
        return "unica";
      }
      public String getDirectorio(){
        return getTareaDir();
      }
      public String getRutasEntrada(){
        return getTareaDir();
      }
      public String getRutasSalida(){
        return getTareaDir();
      }
      public boolean getSiCoordina(){
        return true;
      }
      public boolean getSiTerminado(){
        return false;
      }
      public String getEstadoTarea(){
        return "";
      }
      public long getHoraSolicitud(){
        return 0;
      }
      public long getHoraInicio(){
        return 0;
      }
      public long getHoraFin(){
        return 0;
      }
      public String getAlias(){
        return programa.getAlias();
      }
      public String getParametros(){
        return "-s -F -i -ftenis_gigante.txt";
      }
      public String[] getParametrosArr(){
        return null;
      }
      public ADMINAPPIProgramas getPrograma(){
        return programa;
      }
      public Map getArchivos(){
        final ADMINAPPITareas tarea0=this;
        final ADMINAPPIArchivos archivo=new ADMINAPPIArchivos(){
          public String getBloque(){
            return "";
          }
          public String getDirectorio(){
            return "C:/MyProjects/eclipse/mineria/experimentos/arbol2006";
          }
          public String getEstadoArchivo(){
            return "";
          }
          public String getIdSubtrabajo(){
            return "";
          }
          public String getIdTarea(){
            return "";
          }
          public String getNombre(){
            return "tenis_gigante.txt";
          }
          public String getRutaOriginal(){
            return "";
          }
          public boolean getSiEntrada(){
            return true;
          }
          public boolean getSiLocal(){
            return true;
          }
          public ADMINAPPITareas getTarea(){
            return tarea0;
          }
          public void setNombre(String setNombre){
            
          }
          public void setRutaOriginal(String setRutaOriginal){
            
          }
          public void setInfoArchivo(String info0){
            
          }
          public String getInfoArchivo(){
            return "-f";
          }
        };
        java.util.TreeMap mapa=new java.util.TreeMap();
        mapa.put("archivo único",archivo);
        return mapa;
      }
      public String getNodoCreador(){
        return "<nodo creador>";
      }
      public Map getSubtrabajos(){
        return null;
      }
      public int getTiempoEnSistema(){
        return 0;
      }
    };
    ADMINAPPITrabajos trabajo=new ADMINAPPITrabajos(){
      public ADMINAPPISolicitantes getSolicitante(){
        return null;
      }
      public ADMINAPPITareas getTarea(){
        return tarea;
      }
      public ADMINAPPISub_trabajos addSubTrabajo(){
        return null;
      }
      public Map getSubTrabajos(){
        return null;
      }
      public void ubicaEntradas(ADMINAPPISub_trabajos subtrabajo,
          boolean copiar) throws ADMINAPPExcepcion{
      }
    };
    MINERDivisoras divisora=new MINERDivisoras();
    try {
      divisora.divideTrabajoOderSubTrabajo(trabajo,true);
    } catch (ADMINAPPExcPreejecucion e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args){
    // prueba2006(args);
    prueba506(args);
  }
}