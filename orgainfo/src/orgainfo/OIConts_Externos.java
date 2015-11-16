/*
 * Created on 22/07/2004
 */
package orgainfo;

import java.sql.ResultSet;
import java.sql.SQLException;

import tdutils.tdutils;

/**
 * Clase que representa los controladores externos
 * que manejan tareas de un nodo anfitrión en <tt>tdderive</tt>.
 */
public class OIConts_Externos extends OIPersistente implements OIActualiza {
  protected String id_contlocal;
  protected String id_contremoto;
  protected String id_tarea;
  protected String id_subtrabajo;
  protected long hora_solicitud;
  protected long hora_ejecucionremota;
  protected String estado_contremoto;

  /**
   * 
   */
  public OIConts_Externos() {
    super();
  }

  /**
   * @param info0
   * @param siVacio
   */
  public OIConts_Externos(OIDescriptor info0, boolean siVacio) {
    super(info0, siVacio);
  }

  /**
   * @see orgainfo.OIActualiza#open()
   */
  public void open() throws OIExcepcion {

  }

  /**
   * @see orgainfo.OIActualiza#write()
   */
  public void write() throws OIExcepcion {
    int res=0;
    //
    // actualiza
    //
    res = doUpdateSQL("UPDATE Conts_externos SET " +
                     "id_tarea=" + tdutils.getQ(id_tarea) +
                     ",id_subtrabajo=" + tdutils.getQ(id_subtrabajo) +
                     ",id_contlocal=" + tdutils.getQ(id_contlocal) +
                     ",hora_solicitud=" + String.valueOf(hora_solicitud)  +
                     ",hora_ejecucionremota=" + String.valueOf(hora_ejecucionremota)  +
                     ",id_contremoto=" + tdutils.getQ(id_contremoto) +
                     ",estado_contremoto=" + tdutils.getQ(estado_contremoto) +
                     " WHERE id_contlocal=" + tdutils.getQ(id_contlocal)+
                     " AND id_contremoto="+ tdutils.getQ(id_contremoto)+
                     " AND id_subtrabajo="+ tdutils.getQ(id_subtrabajo)+
                     " AND id_tarea="+ tdutils.getQ(id_tarea)
                     );
    //
    // si actualización no sirve, hace un insert
    //
    if (res==0) {
      res = doUpdateSQL("INSERT INTO Conts_externos (id_tarea,id_subtrabajo,id_contlocal," +
                       "id_contremoto,estado_contremoto,hora_solicitud,hora_ejecucionremota) VALUES(" +
                       tdutils.getQ(id_tarea) +
                       "," + tdutils.getQ(id_subtrabajo) +
                       "," + tdutils.getQ(id_contlocal) +
                       "," + tdutils.getQ(id_contremoto) +
                       "," + tdutils.getQ(estado_contremoto) +
                       "," + String.valueOf(hora_solicitud) +
                       "," + String.valueOf(hora_ejecucionremota) +
                       ")");
    }
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo actualizar lista de controladores remotos.");
    }
  }

  /**
   * @see orgainfo.OIActualiza#delete()
   */
  public void delete() throws OIExcepcion {
    int res=0;
    res = doUpdateSQL("DELETE FROM Conts_externos " +
                     " WHERE id_tarea=" + tdutils.getQ(id_tarea)+
                     " AND id_subtrabajo="+ tdutils.getQ(id_subtrabajo)+
                     " AND id_contlocal="+ tdutils.getQ(id_contlocal)+
                     " AND id_contremoto="+ tdutils.getQ(id_contremoto)
                     );
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo borrar en lista de controladores.");
    }
  }

  /**
   * @see orgainfo.OIActualiza#close()
   */
  public void close() throws OIExcepcion {

  }
  private void openRS(ResultSet rs) throws SQLException{
    id_tarea=rs.getString("id_tarea");
    id_subtrabajo=rs.getString("id_subtrabajo");
    id_contremoto=rs.getString("id_contremoto");
    id_contlocal=rs.getString("id_contlocal");
    hora_solicitud=rs.getLong("hora_solicitud");
    hora_ejecucionremota=rs.getLong("hora_ejecucionremota");
    estado_contremoto=rs.getString("estado_contremoto");
  }
}
