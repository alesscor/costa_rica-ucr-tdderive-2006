package orgainfo;
import java.sql.*;
import tdutils.*;
/**
 * Relaciona una tarea con su solicitante.
 */
public class OISol_Tar extends OIPersistente implements OIActualiza{
  protected String id_tarea;
  protected String id_parcial;
  protected String id_padre;
  protected String id_grupo;
  protected boolean si_iniciador;
  protected OISol_Tar(OIDescriptor info){
    super(info,true);
    id_tarea="";
    id_parcial="";
    id_padre="";
    id_grupo="";
    si_iniciador=false;
  }
  public void write() throws OIExcepcion{
      int res=0;
      //
      // actualiza
      //
      res = doUpdateSQL("UPDATE Sol_tar set " +
                       "id_tarea=" + tdutils.getQ(id_tarea) +
                       ",id_parcial=" + tdutils.getQ(id_parcial) +
                       ",id_padre=" + tdutils.getQ(id_padre) +
                       ",id_grupo=" + tdutils.getQ(id_grupo) +
                       ",si_iniciador=" + si_iniciador +
                       " WHERE id_tarea=" + tdutils.getQ(id_tarea)+
                       " AND id_parcial="+ tdutils.getQ(id_parcial)
                       );
      //
      // si actualización no sirve, hace un insert
      //
      if (res==0) {
        res = doUpdateSQL("insert into Sol_tar (id_tarea,id_parcial," +
                         "id_padre,id_grupo,si_iniciador) values(" +
                         tdutils.getQ(id_tarea) +
                         "," + tdutils.getQ(id_parcial) +
                         "," + tdutils.getQ(id_padre) +
                         "," + tdutils.getQ(id_grupo) +
                         "," + si_iniciador +
                         ")");
      }
      if (res==0) {
        throw new OIExcepcion(
            "No se pudo actualizar lista de programas.");
      }
  }
  public void close() throws OIExcepcion{
  }
  public void delete() throws OIExcepcion{
    int res=0;
    //
    // actualiza
    //
    res = doUpdateSQL("UPDATE Sol_tar set " +
                     "id_tarea=" + tdutils.getQ(id_tarea) +
                     ",id_parcial=" + tdutils.getQ(id_parcial) +
                     ",id_padre=" + tdutils.getQ(id_padre) +
                     ",id_grupo=" + tdutils.getQ(id_grupo) +
                     ",si_iniciador=" + si_iniciador +
                     " WHERE id_tarea=" + tdutils.getQ(id_tarea)+
                     " AND id_subtrabajo="+ tdutils.getQ(id_parcial)
                     );
    //
    // si actualización no sirve, hace un insert
    //
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo actualizar lista de programas.");
    }
  }
  public void open() throws OIExcepcion{
  }
  private void openRS(ResultSet rs) throws SQLException{
    id_tarea=rs.getString("id_tarea");
    id_parcial=rs.getString("id_parcial");
    id_padre=rs.getString("id_padre");
    id_grupo=rs.getString("id_grupo");
    si_iniciador=rs.getBoolean("si_iniciador");
  }

}
