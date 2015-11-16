package mineria;
import java.io.File;

import admin.*;
/**
 * Implementa el inicio de una aplicación específica, en este
 * caso de minería de datos.
 */

public class MINERIniciador extends ADMINAPPIniciador{
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  static final private String APP_ALIAS="dderive"; 
  public MINERIniciador(String archivo_conf) {
    super(archivo_conf);
  }
  /**
   * Analiza los argumentos encomendados a la ejecución de <tt>dderive</tt>
   * como aplicación de <tt>tdderive</tt>.
   * @param argumentos Argumentos desde la invocación del programa.
   * @throws ADMINAPPExcepcion Hay errores cuando:
   * <li>Se ha solicitado más de un método para buscar el mejor atributo.</li>
   * <li>Hay más de un archivo de datos.</li> 
   * <li>Hay más de un archivo de recodificación.</li> 
   * <li>No se encuentran archivos de entrada.</li> 
   * <li>Hay opciones inválidas.</li> 
   * <li>Hay ambigüedad en las opciones.</li> 
   * <li>No se indicó un archivo de entrada.</li> 
   * @see admin.ADMINAPPIniciador#analizaArgumentos(java.lang.String[])
   */
  public void analizaArgumentos(String[] argumentos) throws ADMINAPPExcepcion{
    int i=0;
    boolean bOK=true,bListoMetodo=false,bListoEntrada=false,bListoRecodif=false;
    String sArchivo,sParametros=""+ADMINAPPIniciador.APP_SEPARADORARGUMENTOS;
    File fArchivo=null;
    ADMINAPPIArchivos archivoI=null;
    ADMINAPPIRetornos retornoI=null;
    this.setAliasPrograma(APP_ALIAS);
    while(i<argumentos.length){
      if(argumentos[i].startsWith("-")){
        switch(argumentos[i].charAt(1)){
          case 'f': // es un archivo
          case 'r': // es otro archivo
            if(argumentos[i].charAt(1)=='f'){
              if(!bListoEntrada){
                bListoEntrada=true;
              }else{
                throw new ADMINAPPExcepcion("Solamente debe haber " +
                        "un archivo de entrada.");
              }
            }else{
              if(!bListoRecodif){
                bListoRecodif=true;
              }else{
                throw new ADMINAPPExcepcion("Solamente debe haber " +
                        "una tabla de recodificación.");
              }
            }
            sArchivo=new String(argumentos[i].substring(2));
            fArchivo=new File(sArchivo);
            if(fArchivo.exists()&&fArchivo.isFile()){
              // todo bien
              archivoI=this.addArchivo(sArchivo);
              // info para indicar cuál archivo de entrada es (si de -f o de -r)
              archivoI.setInfoArchivo(argumentos[i].substring(0,2));
            }else{
              throw new ADMINAPPExcepcion("No se encuentra un archivo " +
                      "de entrada '"+sArchivo+"'.");
            }
            break;
          case 'd': // este argumento no funciona
          case 'v': // este argumento no funciona
          case 'm': // este argumento no funciona
          case 'p': // este argumento no funciona
          case 't': // este argumento no funciona
            argumentos[i]="";
            break;
          case 'w': // debe desplegar el árbol en la salida estándar (sinc)
            this.addRetorno(ADMINAPPIRetornos.RETORNO_TIPOSALIDAESTANDAR,"");            
            break;
          case 's': // debe guardar árbol en un archivo .dtf
            this.addRetorno(ADMINAPPIRetornos.RETORNO_TIPODIRECTORIO,
                    this.getDirectorioLocal());
            break;
          case 'b': // método de partición: determinación forzando árbol binario
          case 'D': // método de partición: determinación de segundo orden
          case 'u': // método de partición: determinación con distrib. uniforme
          case 'e': // método de partición: entropía
          case 'g': // método de partición: índice gini
            if(!bListoMetodo){
              bListoMetodo=true;
            }else{
              // error por solicitar dos métodos
                throw new ADMINAPPExcepcion("Se ha solicitado más de un método " +
                        "para buscar el mejor atributo.");
            }
            break;
          case 'c':
          case 'i':
          case 'E':
          case 'F':
          case 'I':
          case 'M':
          case 'P':
          case 'T':
          case 'V':
          case 'G':
            break;
          default:
            throw new ADMINAPPExcepcion("Opción inválida en los argumentos " +
                    "del programa.");
        }
        if(argumentos[i].length()==2 && sParametros.indexOf(
            ADMINAPPIniciador.APP_SEPARADORARGUMENTOS+
            argumentos[i].substring(0,2)+
						ADMINAPPIniciador.APP_SEPARADORARGUMENTOS)>=0){
          // hay opciones repetidas, lo que se evita para tdderive
          argumentos[i]="";
        }
      }else{
        if (argumentos[i].startsWith(this.getSeparadorDestino())) {
          // sí empieza con '@'
          this.setDestino(argumentos[i].substring(1));
        }
      }
      if(argumentos[i]!=""){
        if(argumentos[i].length()>1 && sParametros.indexOf(
                ADMINAPPIniciador.APP_SEPARADORARGUMENTOS+
                argumentos[i].substring(0,2))>=0){
        	throw new ADMINAPPExcepcion("Existe ambigüedad en la opción '"+
    			    argumentos[i].substring(0,2)+"'.");
         }
        // continúa
      	sParametros+=argumentos[i]+ADMINAPPIniciador.APP_SEPARADORARGUMENTOS;        
      }
      i++;
    }
    if(!bListoEntrada){
      throw new ADMINAPPExcepcion("El archivo de datos no ha sido especificado.");
    }
    this.setParametrosPrograma(sParametros.trim());
    System.out.println(this.getParametrosPrograma());
    // solicitudMensaje
  }
  //////////////////////////////////////////////////////////////////////
}