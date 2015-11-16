/*
 * Created on 17/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

import java.io.DataInputStream;
import java.io.FileInputStream;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import orgainfo.OIExcepcion;

import aco.ACONDescriptor;
import aco.ACONExcArbitraria;
import aco.ACONExcOmision;
import aco.ACONExcTemporizacion;
import aco.ACONExcepcion;
import aco.ACONGestor;

/**
 * <p>Título: admin</p>
 * <p>Descripción: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzación: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Clase con la infraestructura para exportar archivos en <tt>tdderive</tt>.
 */
public final class ADMINGLOGestionExportadora extends ACONGestor {
  private String _directorioLocal;
  private Map mpArchivos;
  private PERSCoordinacion.Sub_trabajos subtrabajo;
  /**
   * 
   */
  public ADMINGLOGestionExportadora() {
    super();
  }
  /**
   * @param info0
   */
  public ADMINGLOGestionExportadora(ACONDescriptor info0) {
    super(info0);
  }
  /**
   * @see aco.ACONGestor#completa()
   */
  public void completa() throws ACONExcepcion, Exception {
  }
  /**
   * @see aco.ACONGestor#open()
   */
  public void open() throws ACONExcepcion, Exception {
    exportaArchivos();
    if(subtrabajo!=null && subtrabajo.getSiFin()){
      subtrabajo.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_ENTREGADO);
      subtrabajo.write();
    }
    
  }
  /**
   * Exporta los archivos de una tarea solicitada o de un subtrabajo.
   * <li>Al exportar de un subtrabajo, se pueden exportar tanto
   * archivos de entrada como archivos de salida</li>
   * @throws ADMINExcepcion
   * @throws OIExcepcion
   */
  protected void exportaArchivos() throws OIExcepcion,ADMINGLOExcepcion{
    int length=0;
    String sIndicaRemota="",sNombreArchivo="",sNombreArchivoFisico="";    
    String asIndicaciones[];
    PERSCoordinacion.Archivos archivoI,archivoII=null;
    Map mpProvisional=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    mpProvisional.putAll(mpArchivos);
    Iterator itr;
    byte[] abContenido=new byte[ADMINAPPMetodoDespAbs.TAMANO_EXPORTAIMPORTA];
    DataInputStream diLector;
    try {
      if(mpArchivos.size()>0){
        // recibe el nombre del archivo
        sNombreArchivo=receive();
        // obtiene el archivo para su exportación
        archivoI=(PERSCoordinacion.Archivos)mpArchivos.get(sNombreArchivo);
        if(archivoI!=null){
          /*
           * El archivo sí se encuentra.
           */
          /*
           * <2006/>
           * Esta bifurcación es importante para que
           * no sea necesario tener preparados archivos
           * comprimidos en los casos de cuando se
           * necesita exportar archivos de entrada o de 
           * salida de subtrabajos
           * 
           * NO IMPORTA, MEJOR QUE SÍ LOS COMPRIMA,
           * POR ESO PUSE LA TAUTOLOGÍA
           * 
           */
          if(true || !archivoI.siSubtrabajoFin()){
            /*
             * this.getDirectorioLocal() da el nombre
             * del directorio a conveniencia, según
             * lo puesto en el this.setDirectorioLocal(),
             * primer objeto navegable.
             * En el caso particular de esta clase, se hace
             * en ADMINAPPMetodoSolicitud.exportaArchivos().
             */
            sNombreArchivoFisico=
              this.getDirectorioLocal()+"/"+
              /*PERSCoordinacion.Tareas.DIR_COMPRIMIDOS + "/"+*/ 
              archivoI.getNombre();
          }else{
            sNombreArchivoFisico=
              this.getDirectorioLocal()+"/"+archivoI.getNombre();
          }
          try{
            System.out.println("----------- Exportando archivos1 -----------");
            // abre archivo
            diLector = new DataInputStream(
                      new FileInputStream(sNombreArchivoFisico));
            while(length>=0){
              // se manda bloque por bloque
              try{
                length=diLector.read(abContenido);
                if(length>0){
                  this.sendb(abContenido,length);
                }
              }catch(IOException ex){
                /*
                 * No se pudo leer el archivo.
                 */
                 System.err.println("Error, no se pudo leer el archivo " +
                  "'"+sNombreArchivo+"' ("+sNombreArchivoFisico+")");
                this.sendb(new byte[]{'\0'});
              }
            }
            // cierra archivo
            diLector.close();
            System.out.println("----------- Exportando archivos2 -----------");
          }catch(IOException ex){
            /*
             * No se encontró el archivo.
             */
             System.err.println("Error, no se encontró un archivo " +
              "cuya presencia se asumía '"+sNombreArchivo+"' ("+sNombreArchivoFisico+")");
            this.sendb(new byte[]{'\0'});
          }
        }else{
          /*
           * No se encontró el archivo.
           */
           System.err.println("Error, no se encontró un archivo " +            "cuya presencia se asumía '"+sNombreArchivo+"' ("+sNombreArchivoFisico+")");
          this.sendb(new byte[]{'\0'});
        }
      }
      itr=null;
      archivoI=null;
      archivoII=null;
      this.close();
    }
    catch (ACONExcArbitraria ex) {
    }
    catch (ACONExcOmision ex) {
      ex.printStackTrace();
    }
    catch (ACONExcTemporizacion ex) {
    }
    catch (ACONExcepcion ex) {
    }
  }
  protected void exportaArchivosOld() throws OIExcepcion,ADMINGLOExcepcion{
    int length=0;
    String sIndicaRemota="",sNombreArchivo="",sNombreArchivoFisico="";    
    String asIndicaciones[];
    PERSCoordinacion.Archivos archivoI,archivoII=null;
    Map mpProvisional=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    mpProvisional.putAll(mpArchivos);
    Iterator itr;
    byte[] abContenido=new byte[ADMINAPPMetodoDespAbs.TAMANO_EXPORTAIMPORTA];
    DataInputStream diLector;
    try {
      if(mpArchivos.size()>0){
        // recibe el nombre del archivo
        sNombreArchivo=receive();
        // obtiene el archivo
        archivoI=(PERSCoordinacion.Archivos)mpArchivos.get(sNombreArchivo);        
        if(archivoI!=null){
          /*
           * El archivo sí se encuentra.
           */
          sNombreArchivoFisico=
              this.getDirectorioLocal()+"/"+
              PERSCoordinacion.Tareas.DIR_COMPRIMIDOS +
              "/"+archivoI.getNombre();
          try{
            // abre archivo
            diLector = new DataInputStream(
                      new FileInputStream(sNombreArchivoFisico));
            while(length>=0){
              // se manda bloque por bloque
              try{
                length=diLector.read(abContenido);
                if(length>0){
                  this.sendb(abContenido,length);
                }
              }catch(IOException ex){
                /*
                 * No se pudo leer el archivo.
                 */
                 System.err.println("Error, no se pudo leer el archivo " +
                  "'"+sNombreArchivo+"'");
                this.sendb(new byte[]{'\0'});
              }
            }
            // cierra archivo
            diLector.close();            
          }catch(IOException ex){
            /*
             * No se encontró el archivo.
             */
             System.err.println("Error, no se encontró un archivo " +
              "cuya presencia se asumía ('"+sNombreArchivo+"')");
            this.sendb(new byte[]{'\0'});
          }
        }else{
          /*
           * No se encontró el archivo.
           */
           System.err.println("Error, no se encontró un archivo " +
            "cuya presencia se asumía ('"+sNombreArchivo+"')");
          this.sendb(new byte[]{'\0'});
        }
      }
      itr=null;
      archivoI=null;
      archivoII=null;
      this.close();
    }
    catch (ACONExcArbitraria ex) {
    }
    catch (ACONExcOmision ex) {
      ex.printStackTrace();
    }
    catch (ACONExcTemporizacion ex) {
    }
    catch (ACONExcepcion ex) {
    }
  }
  protected void setNavegables(Object[] navegables){
    subtrabajo=null;
    if(navegables!=null&&navegables.length>1){
      setArchivos((Map)navegables[0]);
      setDirectorioLocal((String)navegables[1]);
      if(navegables.length>2){
        subtrabajo=(PERSCoordinacion.Sub_trabajos)navegables[2];
      }
    }
  }  
  /**
   * Devuelve el conjunto de archivos exportables.
   * @return El conjunto (mapa de búsqueda) de archivos exportables.
   */
  public java.util.Map getArchivos() {
    return mpArchivos;
  }
  

  /**
   * Asigna el conjunto de archivos exportables.
   * @param map Conjunto de archivos exportables.
   */
  private void setArchivos(Map map) {
    mpArchivos= map;
  }

  /**
   * @return El directorio local.
   */
  public String getDirectorioLocal() {
    return _directorioLocal;
  }

  /**
   * @param string
   */
  private void setDirectorioLocal(String string) {
    _directorioLocal= string;
  }

}
