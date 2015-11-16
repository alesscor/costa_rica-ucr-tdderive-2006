/*
 * Created on 13/06/2004
 *
 */
package mineria;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import tdutils.tdutils;
import admin.ADMINAPPISub_trabajos;
import admin.ADMINAPPITrabajos;
import admin.ADMINAPPUnificadoras;
/**
 * Implementa la unión de los resultados de varios subtrabajos
 * para formar el resultado de la tarea como un todo.
 */
public class MINERUnificadoras extends ADMINAPPUnificadoras {

	public MINERUnificadoras() {
		super();
	}

	/**
   * Une el resultado de un subtrabajo con los demás resultados, 
   * en el nodo coordinador.
   * <li>Cuando en un agente participante se termina un subtrabajo,
   * información de la salida es exportada a este agente coordinador.</li>
	 * @see admin.ADMINAPPUnificadoras#unificaTrabajo(admin.ADMINAPPITrabajos)
	 */
	protected boolean unificaTrabajo(ADMINAPPITrabajos trabajo)
			throws Exception {
    boolean res=false;
    Map mpSubtrabajos=null;
    Iterator itr=null;
    ADMINAPPISub_trabajos subtrabajo=null;
    String nombreArchivo=null,nombreSalidasTarea=null;
    File ofDirectorio=null;
    File[] lista_salidas=null;
    FilenameFilter ofFiltra=null;
    int i=0;
    /*
     * TODO <2006/>
     * Poner 'res' verdadero si 
     * <1> todos los subtrabajos de este trabajo
     *     han terminado exitosamente (subtrabajo.si_fin==true)
     *     
     * <2> todos los subtrabajos de este trabajo
     *     tienen su estado igual a SUBTRA_ENTREGADO (subtrabajo.
     *     estado_subtrabajo=SUBTRA_ENTREGADO), lo que significa
     *     que cada archivo de salida ya está bien ubicado.
     * Con programar el caso 2 basta
     */
    mpSubtrabajos=trabajo.getSubTrabajos();
    itr=mpSubtrabajos.values().iterator();
    res=true;
    while(itr.hasNext() && res){
      subtrabajo=(ADMINAPPISub_trabajos)itr.next();
      res=res && (subtrabajo.getEstadoSubtrabajo().compareToIgnoreCase(
          ADMINAPPISub_trabajos.SUBTRA_ENTREGADO)==0);
      
    }
    if(res){
      /*
       * Los resultados de los subtrabajos ya fueron recibidos (yupi!!!!!!)
       * [1] Crea el directorio de resultados de la tarea.
       * [2] Copia en tal directorio los resultados de los 
       *     subtrabajos de tdderive.
       * [3] Copia la raiz que pega los subtrabajos
       */
      nombreSalidasTarea=trabajo.getTarea().getResultadosDir();
      itr=mpSubtrabajos.values().iterator();
      while(itr.hasNext()){
        subtrabajo=(ADMINAPPISub_trabajos)itr.next();
        nombreArchivo=subtrabajo.getResultadosDir();
        ofDirectorio=new File(nombreArchivo);
        ofFiltra=tdutils.creaFiltroGenerico(".*\\.dtf");
        lista_salidas=ofDirectorio.listFiles(ofFiltra);
        i=0;
        while(i<lista_salidas.length){
          try {
            nombreArchivo=lista_salidas[i].getName();
            nombreArchivo=nombreSalidasTarea+"/"+nombreArchivo;
            tdutils.copiaArchivo(lista_salidas[i], new File(nombreArchivo));
          } catch (IOException e) {
            res=false;
          }
          i++;
        }
      }
      /*
       * viene la raíz
       * 
       */
        nombreArchivo=trabajo.getTarea().getTareaDir();
        ofDirectorio=new File(nombreArchivo);
        ofFiltra=tdutils.creaFiltroGenerico(".*\\.dtf");
        lista_salidas=ofDirectorio.listFiles(ofFiltra);
        i=0;
        // paranoia!!!
        while(i<lista_salidas.length){
          try {
            nombreArchivo=lista_salidas[i].getName();
            nombreArchivo=nombreSalidasTarea+"/"+nombreArchivo;
            tdutils.copiaArchivo(lista_salidas[i], new File(nombreArchivo));
          } catch (IOException e) {
            res=false;
          }
          i++;
        }
        /*
         * viene archivo de texto
         * 
         */
          nombreArchivo=trabajo.getTarea().getTareaDir();
          ofDirectorio=new File(nombreArchivo);
          ofFiltra=tdutils.creaFiltroGenerico(MINERDivisoras.NOMBRE_SALIDA_ESTANDAR);
          lista_salidas=ofDirectorio.listFiles(ofFiltra);
          i=0;
          // paranoia!!!
          while(i<lista_salidas.length){
            try {
              nombreArchivo=lista_salidas[i].getName();
              nombreArchivo=nombreSalidasTarea+"/"+nombreArchivo;
              tdutils.copiaArchivo(lista_salidas[i], new File(nombreArchivo));
            } catch (IOException e) {
              res=false;
            }
            i++;
          }
    }
		return res;
	}

	/**
	 * @see admin.ADMINAPPUnificadoras#verificaTrabajo(admin.ADMINAPPITrabajos)
	 */
	protected boolean verificaTrabajo(ADMINAPPITrabajos trabajo) {
		return false;
	}
  /**
   * Gestiona lo que hace falta para dar por terminado al subtrabajo.
   * <li>Reubica archivos de salida.</li>
   * <li>Trabaja solamente si el valor de subtrabajo.getEstado()==""</li>
   * <li></li>
   * <li></li>
   * <li></li>
   */
  protected boolean gestionaFinSubtrabajo(ADMINAPPISub_trabajos subtrabajo){
    String dirOarchivo="",dirsalida="";
    File ofDirectorio=null;
    File[] lista_salidas=null;
    FilenameFilter ofFiltra=null;
    int i;
    boolean res=false;
    if(subtrabajo.getEstadoSubtrabajo().compareToIgnoreCase(
        ADMINAPPISub_trabajos.SUBTRA_FIN2)==0
        ||
       subtrabajo.getEstadoSubtrabajo().compareToIgnoreCase(
            ADMINAPPISub_trabajos.SUBTRA_ENTREGADO)==0
    ){
      // los archivos de salida ya estaban listos
      res=true;
    }else{
      if(subtrabajo.getSiFin()){
        dirOarchivo=subtrabajo.getSubtrabajoDir();
        dirsalida=subtrabajo.getResultadosDir();
        ofDirectorio=new File(dirOarchivo);
        ofFiltra=tdutils.creaFiltroGenerico(".*\\.dtf");
        lista_salidas=ofDirectorio.listFiles(ofFiltra);
        ofDirectorio=new File(dirsalida);
        if(!ofDirectorio.exists()){
          ofDirectorio.mkdirs();
        }
        i=0;
        while(i<lista_salidas.length){
          try {
            dirOarchivo=lista_salidas[i].getName();
            dirOarchivo=dirsalida+"/"+dirOarchivo;
            tdutils.copiaArchivo(lista_salidas[i], new File(dirOarchivo));
          } catch (IOException e) {
            res=false;
          }
          i++;
        }
        res=true;
      }else{
        res=false;
      }
    }
    return res;
  }

}
