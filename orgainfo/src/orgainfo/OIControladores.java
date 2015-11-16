package orgainfo;
import java.sql.*;
import tdutils.*;
/**
 * Clase para representar los controladores de tareas de aplicación
 * manejados internamente en <tt>tdderive</tt>.
 */
public class OIControladores extends OIProcesos implements OIActualiza{
  protected String id_tarea;
  protected boolean si_activo;
  protected OIControladores(OIDescriptor info0,boolean sivacio){
    super(info0,sivacio);
  }
  protected OIControladores(OIDescriptor info0,boolean sivacio,
        OIProcesos padre){
    super(info0,sivacio,padre);
  }
  public void open(){
  }
  public String inserttext1()throws OIExcepcion{
    String res;
    res = ","+tdutils.getQ(id_tarea) +"," + si_activo;
    return res;
  }
  public String inserttext0()throws OIExcepcion{
    String res;
    res = ",id_tarea,si_activo";
    return res;
  }
  public String updatetext()throws OIExcepcion{
    String res;
    res = ",id_tarea="+tdutils.getQ(id_tarea)+
          ",si_activo="+si_activo;
    return res;
  }

  protected void openRS0(ResultSet rs) throws SQLException{
    id_tarea=rs.getString("id_tarea");
    si_activo=rs.getBoolean("si_activo");
  }
  public void close(){
  }
  protected final void preWrite() throws OIExcepcion {
    int res=0;
    String tabla="Controladores"; 
    res = doUpdateSQL("UPDATE "+tabla+" SET " +
        " si_activo=false" +
        " WHERE id_parcial!=" + tdutils.getQ(id_parcial) + 
        " AND id_tarea=" + tdutils.getQ(id_tarea));
  }  
  protected final void preWriteMalaCorreccion() throws OIExcepcion {
    int res=0;
    String tabla="Controladores"; 
    res = doUpdateSQL("UPDATE "+tabla+" SET " +
        " si_activo=" + si_activo + "," +
        " id_tarea=" + tdutils.getQ(id_tarea)+
        " WHERE id_parcial=" + tdutils.getQ(id_parcial));
  }  
}
