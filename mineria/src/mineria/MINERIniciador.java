package mineria;
import java.io.File;

import admin.*;
/**
 * Implementa el inicio de una aplicaci�n espec�fica, en este
 * caso de miner�a de datos.
 */

public class MINERIniciador extends ADMINAPPIniciador{
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  static final private String APP_ALIAS="dderive"; 
  public MINERIniciador(String archivo_conf) {
    super(archivo_conf);
  }
  /**
   * Analiza los argumentos encomendados a la ejecuci�n de <tt>dderive</tt>
   * como aplicaci�n de <tt>tdderive</tt>.
   * @param argumentos Argumentos desde la invocaci�n del programa.
   * @throws ADMINAPPExcepcion Hay errores cuando:
   * <li>Se ha solicitado m�s de un m�todo para buscar el mejor atributo.</li>
   * <li>Hay m�s de un archivo de datos.</li> 
   * <li>Hay m�s de un archivo de recodificaci�n.</li> 
   * <li>No se encuentran archivos de entrada.</li> 
   * <li>Hay opciones inv�lidas.</li> 
   * <li>Hay ambig�edad en las opciones.</li> 
   * <li>No se indic� un archivo de entrada.</li> 
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
                        "una tabla de recodificaci�n.");
              }
            }
            sArchivo=new String(argumentos[i].substring(2));
            fArchivo=new File(sArchivo);
            if(fArchivo.exists()&&fArchivo.isFile()){
              // todo bien
              archivoI=this.addArchivo(sArchivo);
              // info para indicar cu�l archivo de entrada es (si de -f o de -r)
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
          case 'w': // debe desplegar el �rbol en la salida est�ndar (sinc)
            this.addRetorno(ADMINAPPIRetornos.RETORNO_TIPOSALIDAESTANDAR,"");            
            break;
          case 's': // debe guardar �rbol en un archivo .dtf
            this.addRetorno(ADMINAPPIRetornos.RETORNO_TIPODIRECTORIO,
                    this.getDirectorioLocal());
            break;
          case 'b': // m�todo de partici�n: determinaci�n forzando �rbol binario
          case 'D': // m�todo de partici�n: determinaci�n de segundo orden
          case 'u': // m�todo de partici�n: determinaci�n con distrib. uniforme
          case 'e': // m�todo de partici�n: entrop�a
          case 'g': // m�todo de partici�n: �ndice gini
            if(!bListoMetodo){
              bListoMetodo=true;
            }else{
              // error por solicitar dos m�todos
                throw new ADMINAPPExcepcion("Se ha solicitado m�s de un m�todo " +
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
            throw new ADMINAPPExcepcion("Opci�n inv�lida en los argumentos " +
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
          // s� empieza con '@'
          this.setDestino(argumentos[i].substring(1));
        }
      }
      if(argumentos[i]!=""){
        if(argumentos[i].length()>1 && sParametros.indexOf(
                ADMINAPPIniciador.APP_SEPARADORARGUMENTOS+
                argumentos[i].substring(0,2))>=0){
        	throw new ADMINAPPExcepcion("Existe ambig�edad en la opci�n '"+
    			    argumentos[i].substring(0,2)+"'.");
         }
        // contin�a
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