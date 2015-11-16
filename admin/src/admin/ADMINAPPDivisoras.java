/*
 * Created on 01/06/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

import java.util.Iterator;
import java.util.Map;

import orgainfo.OIExcepcion;
/**
 * <p>Título: admin</p>
 * <p>Descripción: Encargada de guiar la división concreta de un trabajo
 * en subtrabajos.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzación: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Se encarga de dar un esbozo funcional para guiar la división concreta de
 * un trabajo en subtrabajos.
 */
public abstract class ADMINAPPDivisoras {
  /**
   * 
   */
  public ADMINAPPDivisoras() {
  }
  /**
   * Divide un trabajo o subtrabajo en subtrabajos, ligando los nuevos
   * objetos de trabajo a su tarea correspondiente.
   * <li>Debe asignarse cada subtrabajo con sus detalles correspondientes:
   * comando de ejecución, rutas de archivos de entrda y salida.</li>
   * @param trabajo La descripción del trabajo a dividir.
   * @param siTarea Si se debe dividir una tarea.
   */
  protected abstract void divideTrabajoOderSubTrabajo(
        ADMINAPPITrabajos trabajo,boolean siTarea)throws ADMINAPPExcPreejecucion;
  /**
   * Divide la tarea o el trabajo, en subtrabajos, dejándolos en el mapa 
   * sub_trabajos de la tarea a la que corresponden.
   * @param trabajo Trabajo a dividir.
   * @throws ADMINAPPExcPreejecucion Si hay error en la división del trabajo,
   * es decir, si los subtrabajos no se pudieron generar o quedaron inválidos.
   */
  void divideTrabajoAdmin(ADMINAPPTrabajos trabajo) 
          throws ADMINAPPExcPreejecucion{
    PERSCoordinacion.Tareas tareareal=null;
    /*
     * divide el trabajo (la tarea en este caso), llamando al divisor
     * concreto que le corresponde. Se supone que este divisor asigna valores
     * de carga a cada subtrabajo.
     */
    this.divideTrabajoOderSubTrabajo(trabajo,true);
    /*
     * verifica el resultado de la división dejado en el objeto
     * "trabajo".
     */
     tareareal=(PERSCoordinacion.Tareas)trabajo.getTarea();
     if(this.verificaTrabajoAdmin(trabajo)){
       // bien en la revisión del trabajo.
       if(tareareal!=null){
         tareareal.setEstadoTarea(ADMINAPPITareas.TAREA_MARCHA);
       }
     }else{
       if(tareareal!=null){
         tareareal.setEstadoTarea(ADMINAPPITareas.TAREA_ERROR);
       }
       throw new ADMINAPPExcPreejecucion("Error al verificar la " +        "división del trabajo.");
     }
     if(tareareal!=null){
       try {
        tareareal.write();
        tareareal.getDescriptor().getConex().dbCommit();
      } catch (OIExcepcion e) {
        e.printStackTrace();
      }
     }     
  }
  /**
   * Verifica la validez de untrabajo al ser dividido en subtrabajos.
   * @param trabajo Trabajo a examinar con la información suficiente para
   * realizar esta verificación.
   * @return Si el trabajo es válido.
   */
  protected boolean verificaTrabajo(ADMINAPPITrabajos trabajo){
    return true;
  }
  /**
   * Verifica si la instancia trabajo tiene subtrabajos válidos según la
   * división concreta de la aplicación.
   * @param trabajo El trabajo cuyos subtrabajos se revisarán.
   * @return Si hubo errores en algún subtrabajo.
   */
  boolean verificaTrabajoAdmin(ADMINAPPITrabajos trabajo){
    boolean bOK=false,bOKGlobal=true;
    Map sub_trabajos=null;
    ADMINAPPIArchivos archI=null;
    PERSCoordinacion.Sub_trabajos subtraI=null;
    Iterator itrP=null,itrQ=null;
    /*
     * revisa si los subtrabajos son válidos, así como 
     * el comando de ejecución 
     */
    bOK=verificaTrabajo(trabajo);
    if(bOK){
      bOK=true;
      /*
       * revisa:
       * i. si hay subtrabajos.
       * ii. si hay archivos listos en cada subtrabajo que los requiera.
       */
      sub_trabajos=trabajo.getSubTrabajos();
      if(sub_trabajos!=null && sub_trabajos.size()>0){
        itrP=sub_trabajos.values().iterator();
        while(itrP.hasNext()){
          subtraI=(PERSCoordinacion.Sub_trabajos)itrP.next();
          // hace persistente el estado del subtrabajo.
          try {
            subtraI.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_ESPERA);
            subtraI.write();
            ((ADMINGLOInfo)subtraI.getDescriptor()).println("controlador","divide y obtiene nuevo subtrabajo: " + subtraI.getIdTarea()+":"+subtraI.getIdSubtrabajo());
          } catch (orgainfo.OIExcepcion e) {
            e.printStackTrace();
          }
          // revisa que todos los archivos del subtrabajo estén listos
          bOK=true;
          if(subtraI.getArchivos()!=null && subtraI.getArchivos().size()>0){
            // hay archivos
            itrQ=subtraI.getArchivos().values().iterator();
            while(itrQ.hasNext()){
              archI=(ADMINAPPIArchivos)itrQ.next();
              if(archI.getEstadoArchivo().compareToIgnoreCase(
                  ADMINAPPIArchivos.ARCHIVO_LISTO)!=0){
                // hubo problemas con un archivo
                bOK=false;
                bOKGlobal=false;
                break;
              }
            }
            if(!bOK){
              // hay problema con este subtrabajo (no tiene al menos 
              // un archivo)
              try {
                subtraI.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_ERROR);
                subtraI.write();
              } catch (orgainfo.OIExcepcion e) {
                e.printStackTrace();
              }
            }
          }
          // examina el siguiente subtrabajo
        }
        if(subtraI!=null){
          try {
            subtraI.getDescriptor().getConex().dbCommit();
          } catch (OIExcepcion e) {
            e.printStackTrace();
          }        
        }
      }
    }
    return bOKGlobal;
  }
}
