/*
 * Created on 01/06/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;


import tdutils.tdutils;



/**
 * <p>Título: admin</p>
 * <p>Descripción: Contiene la descripción detallada de una tarea y
 * sus subtrabajos. </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzación: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Implementa el manejo de una tarea, uniendo los solicitantes con su
 * tarea solicitada y con los subtrabajos responsables
 * de completarla. 
 */
public class ADMINAPPTrabajos implements ADMINAPPITrabajos {
  PERSCoordinacion.Tareas tarea;
  PERSCoordinacion.Solicitantes solicitante;
  Map procesos;
  Map resultados;
  ADMINAPPTrabajos(PERSCoordinacion.Tareas tarea0,
        PERSCoordinacion.Solicitantes solicitante0){
     tarea=tarea0;
     solicitante=solicitante0;
     procesos=new java.util.TreeMap();
     resultados=new java.util.TreeMap();
  }
  /**
   * 
   * @see admin.ADMINAPPITrabajos#getSolicitante()
   */
  public ADMINAPPISolicitantes getSolicitante() {
    return solicitante;
  }
  /**
   * 
   * @see admin.ADMINAPPITrabajos#getTarea()
   */
  public ADMINAPPITareas getTarea() {
    return tarea;
  }
  /**
   * 
   * @see admin.ADMINAPPITrabajos#addSubTrabajo()
   */
  public ADMINAPPISub_trabajos addSubTrabajo(){
    PERSCoordinacion.Sub_trabajos subtrabajo=null;
    subtrabajo=new PERSCoordinacion.Sub_trabajos(tarea);
    return subtrabajo;
  }


  /**
   * Obtiene el estado actual que conforma un trabajo.<br>
   * Se incluye:<br>
   * <li>Detalles de la tarea.</li>
   * <li>Detalles del solicitante.</li>
   * <li>Detalles de los subtrabajos.</li>
   * <li>Detalles de los resultados.</li>
   */
  void obtieneEstadoActual(){
    
  }
    /**
     * @see admin.ADMINAPPITrabajos#getSubTrabajos()
     */
    public Map getSubTrabajos() {
    	return tarea.getSubtrabajos();
    }
    /**
     * 
     * @throws ADMINAPPExcepcion
     * @see admin.ADMINAPPITrabajos#ubicaEntradas(admin.ADMINAPPISub_trabajos, boolean)
     */
    public void ubicaEntradas(ADMINAPPISub_trabajos subtrabajo,
            boolean sicopiar) throws ADMINAPPExcepcion{
      boolean bOK=false;
      String sRutaOriginal,sRutaTrabajo;
      Iterator itr=null;
      File fEntrada=null;
      PERSCoordinacion.Sub_trabajos subtra=null;
      PERSCoordinacion.Archivos arEntrada=null;
      /*
       * [1] Crear subtrabajo para el directorio.
       * [2] Mover o copiar el archivo de entrada.
       * [3] Asignar la nueva ruta de entrada.
       * [4] Confirmar los datos para el nuevo subtrabajo. 
       */
      try{
      	subtra=(PERSCoordinacion.Sub_trabajos)subtrabajo;
      }catch(Exception e){
        throw new ADMINAPPExcepcion("No se pudo convertir un objeto a " +
                "su tipo versión administradora.",e);
      }
      try {
    		subtra.createDirs();
    	} catch (ADMINGLOExcepcion e) {
            throw new ADMINAPPExcepcion("Error en la creación de los " +
                    "directorios del subtrabajo.",e);
    	}
      // lleva el archivo de entrada al directorio de trabajo
      itr=subtra.getArchivos().values().iterator();
      while(itr.hasNext()){
        arEntrada=(PERSCoordinacion.Archivos)itr.next();
        sRutaOriginal=arEntrada.getRutaOriginal();
        sRutaTrabajo=subtra.getSubtrabajoDir()+"/"+arEntrada.getNombre();
        fEntrada=new File(sRutaOriginal);
        arEntrada.setEstadoArchivo(ADMINAPPIArchivos.ARCHIVO_AUSENTE);
        if(!fEntrada.exists()){
          // pasa al siguiente archivo
          continue;
        }
        try{
          if(sicopiar){
            bOK=tdutils.copiaArchivo(fEntrada, new File(sRutaTrabajo));
          }else{
            // ver si mueveArchivo sirve: SÍ
            bOK=tdutils.mueveArchivo(fEntrada, new File(sRutaTrabajo));
            if(!bOK){
              bOK=tdutils.copiaArchivo(fEntrada, new File(sRutaTrabajo));
            }          
          }
        }catch(IOException ex){
          throw new ADMINAPPExcepcion("Problemas al crear archivos " +
                "de subtrabajo.",ex);
        }
        if(bOK){
          arEntrada.setEstadoArchivo(ADMINAPPIArchivos.ARCHIVO_LISTO);
          subtra.setRutasEntrada(sRutaTrabajo);
          arEntrada.setRutaOriginal(sRutaTrabajo);
          arEntrada.setSiEntrada(true);
        }
      }
      try {
        arEntrada.write();
    		subtra.write();
//        System.out.println("Subtrabajo '" + subtra.getIdSubtrabajo() + 
//            "' escrito.");
    	} catch (ADMINGLOExcepcion e1) {
        System.err.println("Error al escribir el subtrabajo " +subtra.getIdSubtrabajo()+
            " en la base de datos.");
         e1.printStackTrace();
         throw new ADMINAPPExcepcion("Error en la escritura del subtrabajo.",e1);
    	}
    }
    /**
     * Verifica si el trabajo tiene subtrabajos. Con ello se determina 
     * si hay necesidad de realizar una división cuando un trabajo se planifica.
     * <li>Como parte de la verificación revisa si ya existe una tarea.</li>
     * <2005 />
     * @return Verdadero si hay subtrabajos.
     */
    public final boolean siHaySubTrabajos() {
      return (tarea!=null)&&(tarea.getSubtrabajos()!=null) && (!tarea.getSubtrabajos().isEmpty());
    }
    public final String toString(){
      if(this.tarea==null){
        return "(null)";
      }
      return this.tarea.getIdTarea();
    }

}