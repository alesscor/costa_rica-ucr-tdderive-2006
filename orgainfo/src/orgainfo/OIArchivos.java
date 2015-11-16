package orgainfo;
import java.sql.*;
import tdutils.*;

/**
 * Clase para representar los archivos involucrados con 
 * procesos internos de <tt>tdderive</tt>.
 */
public class OIArchivos extends OIPersistente implements OIActualiza{
  protected String id_tarea;
  protected String id_subtrabajo;
  protected String nombre;
  protected String bloque;
  protected String estado_archivo;
  /**
   * Atributo para información adicional.
   */
  protected String info_archivo;
  protected String ruta_original;//
  protected boolean si_local;    //
  protected boolean si_entrada;  //
  protected OIArchivos(){
    super(null,false);
    _inicio();
  }
  protected OIArchivos(OIDescriptor info0){
    super(info0,false);
  }
  private void _inicio(){
    id_tarea="";
    id_subtrabajo="";
    nombre="";
    bloque="";
    estado_archivo="";
    si_local=false;
    ruta_original="";
    info_archivo="";
    si_entrada=true;
  }
  protected void openRS(ResultSet rs) throws SQLException{
    id_tarea=rs.getString("id_tarea");
    id_subtrabajo=rs.getString("id_subtrabajo");
    nombre=rs.getString("nombre");
    bloque=rs.getString("bloque");
    estado_archivo=rs.getString("estado_archivo");
    info_archivo=rs.getString("info_archivo");
    si_local=rs.getBoolean("si_local");
    si_entrada=rs.getBoolean("si_entrada");
    ruta_original=rs.getString("ruta_original");
  }
  public void open()throws OIExcepcion{
  }
  /**
   * Guarda el contenido en un archivo físico que luego es descomprimido.
   * <li>Si hay contenido, lo respalda en dos archivos: uno comprimido y
   * otro descomprimido, e inmediatamente después libera la memoria
   * ocupada.</li>
   * <li>Borra el contenido del archivo, i.e. el campo "_contenido".</li>
   * <li>El archivo comprimido lo guarda en la carpeta "comprimido".</li>
   * @throws OIExcepcion Si hay error.
   */
  public void splitFile(String file0)throws OIExcepcion{

  }
  public void close()throws OIExcepcion{
  }
  public void delete()throws OIExcepcion{
    int res=0;
    //
    // actualiza
    //
    res = doUpdateSQL("DELETE Archivos " +
                     " WHERE id_tarea=" + tdutils.getQ(id_tarea)+
                     " AND id_subtrabajo="+ tdutils.getQ(id_subtrabajo)+
                     " AND nombre="+ tdutils.getQ(nombre)+
                     " AND bloque="+ tdutils.getQ(bloque)
                     );
    //
    // si actualización no sirve, hace un insert
    //
    if (res==0) {
      throw new OIExcepcion("No se pudo borrar de la tabla 'Archivos'.");
    }
  }
  public void write()throws OIExcepcion{
    if(id_tarea==null||id_tarea==""){
      throw new OIExcepcion("Un archivo debe tener asignada "+
      "una tarea.");
    }
    int res=0;
    //
    // actualiza
    //
    res = doUpdateSQL("UPDATE Archivos set " +
                     "id_tarea=" + tdutils.getQ(id_tarea) +
                     ",id_subtrabajo=" + tdutils.getQ(id_subtrabajo) +
                     ",nombre=" + tdutils.getQ(nombre) +
                     ",bloque=" + tdutils.getQ(bloque) +
                     ",estado_archivo=" + tdutils.getQ(estado_archivo) +
                     ",info_archivo=" + tdutils.getQ(info_archivo) +
                     ",ruta_original=" + tdutils.getQ(ruta_original) +
                     ",si_entrada=" + si_entrada +
                     ",si_local=" + si_local +
                     " WHERE id_tarea=" + tdutils.getQ(id_tarea)+
                     " AND id_subtrabajo="+ tdutils.getQ(id_subtrabajo)+
                     " AND nombre="+ tdutils.getQ(nombre)+
                     " AND bloque="+ tdutils.getQ(bloque)
                     );
    //
    // si actualización no sirve, hace un insert
    //
    if (res==0) {
      res = doUpdateSQL("insert into Archivos (id_tarea,id_subtrabajo," +
                       "nombre,bloque,estado_archivo,info_archivo,si_local,si_entrada,ruta_original) values(" +
                       tdutils.getQ(id_tarea) +
                       "," + tdutils.getQ(id_subtrabajo) +
                       "," + tdutils.getQ(nombre) +
                       "," + tdutils.getQ(bloque) +
                       "," + tdutils.getQ(estado_archivo) +
                       "," + tdutils.getQ(info_archivo) +
                       "," + si_local +
                       "," + si_entrada +
                       "," + tdutils.getQ(ruta_original) +
                       ")");
    }
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo actualizar el archivo en la base de datos.");
    }
  }
}
