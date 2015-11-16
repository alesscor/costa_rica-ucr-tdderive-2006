/*
 * Created on 17/05/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package admin;

import aco.ACONDescriptor;
import aco.ACONExcArbitraria;
import aco.ACONExcOmision;
import aco.ACONExcTemporizacion;
import aco.ACONExcepcion;
import aco.ACONGestor;

import orgainfo.OIExcepcion;

/**
 * <p>Título: admin</p>
 * <p>Descripción: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organzación: ECCI - UCR</p>
 * <p>@author Alessandro</p>
 * <p>@version 1.0</p>
 */
/**
 * Clase con la infraestructura para importar archivos en <tt>tdderive</tt>.
 */
public class ADMINGLOGestionImportadora extends ACONGestor {
  private PERSCoordinacion.Archivos _archivo;
  private ADMINGLOInfo info_servidortdderive; 
  /**
   * 
   */
  public ADMINGLOGestionImportadora() {
    super();
  }
  /**
   * @param info0
   */
  public ADMINGLOGestionImportadora(ACONDescriptor info0) {
    super(info0);
  }
  /**
   * @see aco.ACONGestor#completa()
   */
  public void completa() throws ACONExcepcion, Exception {
  }
  /**
   * Realiza la importación de archivos.
   * @see aco.ACONGestor#open()
   */
  public void open() throws ACONExcepcion, Exception {
    importaArchivos();
  }
  /**
   * Devuelve el nombre del archivo que se va a importar.
   * @return El nombre del archivo a importar.
   */
  public PERSCoordinacion.Archivos getArchivo() {
    return _archivo;
  }
  
  /**
   * Importa los archivos de una tarea o subtrabajo solicitado y los guarda en
   * forma volátil en la lista de archivos.
   * @throws ADMINExcepcion
   * @throws OIExcepcion
   * <li>Realiza los chequeos documentados por ejecutarInicio().</li>
   * <li>Cambia estados de los archivos: de "Ausente" a "Listo".</li>
   */
  protected void importaArchivos() throws OIExcepcion,ADMINGLOExcepcion{
    int medida=0;
    byte[] contenidoI=null;
    try {
      /*
       * Importa el archivo para el que se ha dedicado este objeto.
       */
      System.out.println("----------- Importando archivos1 -----------");
      if(_archivo!=null){
        // pide el archivo
        this.send(_archivo.getNombre());
        _archivo.setEstadoArchivo(ADMINAPPIArchivos.ARCHIVO_IMPORTANDO);
        _archivo.write();
        try {
          contenidoI=new byte[ADMINAPPMetodoDespAbs.TAMANO_EXPORTAIMPORTA];
          while((medida=this.receiveb(contenidoI))>=0){
            _archivo.setContenido(contenidoI,medida);
            _archivo.write();
            System.err.println("\nSe han escrito "+medida+" bytes.\n");
          }
        }catch (ADMINGLOExcepcion ex) {
          // no se pudo escribir archivo (¿?)
          // detiene la importación
          _archivo.setContenido(null);
          _archivo.setEstadoArchivo(ADMINAPPIArchivos.ARCHIVO_IMPORTANDO);
          _archivo.write();
          ex.printStackTrace();
        }
      }
      this.info_servidortdderive.getConex().dbCommit();
      this.close();
      System.out.println("----------- Importando archivos2 -----------");
    }
    catch (ACONExcArbitraria ex) {
      ex.printStackTrace();
      System.out.println("----------- Importando archivose1 -----------");
    }
    catch (ACONExcOmision ex) {
      ex.printStackTrace();
      System.out.println("----------- Importando archivose2 -----------");
    }
    catch (ACONExcTemporizacion ex) {
      System.out.println("----------- Importando archivose3 -----------");
    }
    catch (ACONExcepcion ex) {
      System.out.println("----------- Importando archivose4 -----------");
      ex.printStackTrace();
    }
  }
  protected void setNavegables(Object[] navegables){
    if(navegables!=null&&navegables.length>1){
      this.info_servidortdderive=(ADMINGLOInfo)navegables[0];
      this._archivo=(PERSCoordinacion.Archivos)navegables[1];
    }
  }  

  /**
   * Asigna un archivo a importar.
   * @param archivo0 El nombre del archivo a importar.
   */
  private void setArchivo(PERSCoordinacion.Archivos archivo0) {
    _archivo= archivo0;
  }

}
