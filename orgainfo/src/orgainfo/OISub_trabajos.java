package orgainfo;

import java.sql.ResultSet;
import java.sql.SQLException;


import tdutils.tdutils;

/**
 * Lleva la información necesaria sobre un subtrabajo de <tt>tdderive</tt>. 
 */
public class OISub_trabajos extends OIPersistente implements OIActualiza{
  protected String id_tarea;
  protected String id_subtrabajo;
  protected String directorio;
  protected String rutas_entrada;
  protected String rutas_salida;
  protected double carga;
  protected double progreso;
  protected String estado_subtrabajo;
  protected String comando;
  protected long hora_ingreso;
  /// <2006 indica el fin de un subtrabajo />
  protected boolean si_fin=false;

  protected OISub_trabajos(OIDescriptor info0,boolean sivacio){
    super(info0,sivacio);
  }
  public void open(){
  }
  /**
   * Carga un subtrabajo a partir de un resultset de sql.
   * @param rs Conjunto de resultados a leer.
   * @throws SQLException
   */
  public void openRS(ResultSet rs) throws SQLException{
    id_subtrabajo=rs.getString("id_subtrabajo");
    id_tarea=rs.getString("id_tarea");
    directorio=rs.getString("directorio");
    rutas_entrada=rs.getString("rutas_entrada");
    rutas_salida=rs.getString("rutas_salida");
    carga=rs.getDouble("carga");    
    progreso=rs.getDouble("progreso");    
    estado_subtrabajo=rs.getString("estado_subtrabajo");
    comando=rs.getString("comando");
    hora_ingreso=rs.getLong("hora_ingreso");
    si_fin=rs.getBoolean("si_fin");
  }
  /**
   * 
   * @see orgainfo.OIActualiza#write()
   */
  public void write() throws OIExcepcion{
    int res=0;
    //
    // actualiza
    //
    res = doUpdateSQL("UPDATE sub_trabajos set " +
                     "id_subtrabajo=" + tdutils.getQ(id_subtrabajo) +
                     ",id_tarea=" + tdutils.getQ(id_tarea) +
                     ",directorio=" + tdutils.getQ(directorio) +
                     ",rutas_entrada=" + tdutils.getQ(rutas_entrada) +
                     ",rutas_salida=" + tdutils.getQ(rutas_salida) +
                     ",carga=" + carga +                     
                     ",progreso=" + progreso +                     
                     ",estado_subtrabajo=" + tdutils.getQ(estado_subtrabajo) +
                     ",comando=" + tdutils.getQ(comando) +
                     ",hora_ingreso=" + hora_ingreso +
                     ",si_fin=" + Boolean.toString(si_fin)  +
                     " WHERE id_tarea=" + tdutils.getQ(id_tarea)+" AND " +
                            "id_subtrabajo="+tdutils.getQ(id_subtrabajo));
    //
    // si actualización no sirve, hace un insert
    //
    if (res==0) {
      res = doUpdateSQL("insert into sub_trabajos (id_tarea,id_subtrabajo,directorio," +
                       "rutas_entrada,rutas_salida,carga,"+
                       "progreso,estado_subtrabajo,"+
                       "comando,hora_ingreso,si_fin) values(" +
                       tdutils.getQ(id_tarea) +
                       "," + tdutils.getQ(id_subtrabajo) +
                       "," + tdutils.getQ(directorio) +
                       "," + tdutils.getQ(rutas_entrada) +
                       "," + tdutils.getQ(rutas_salida) +
                       "," + carga +
                       "," + progreso +
                       "," + tdutils.getQ(estado_subtrabajo) +
                       "," + tdutils.getQ(comando) +
                       "," + hora_ingreso +
                       "," + Boolean.toString(si_fin) +
                       ")");
    }
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo actualizar tabla 'Sub_trabajos'.");
    }
  }
  public void delete() throws OIExcepcion{
    int res=0;
    res = doUpdateSQL("DELETE sub_trabajos " +
                     " WHERE id_tarea=" + tdutils.getQ(id_tarea)+" AND " +
                            "id_subtrabajo="+tdutils.getQ(id_subtrabajo));
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo borrar de la tabla 'Sub_trabajos'.");
    }
    res = doUpdateSQL("DELETE archivos " +
        " WHERE id_tarea=" + tdutils.getQ(id_tarea)+" AND " +
        "id_subtrabajo="+tdutils.getQ(id_subtrabajo));
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo borrar de la tabla 'Archivos'.");
    }
  }
  public void close(){
  }
  public void creaVacio(){
  }
  public void creaUltimo(){
  }
}
