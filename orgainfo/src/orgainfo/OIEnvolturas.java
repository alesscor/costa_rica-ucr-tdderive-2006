package orgainfo;
import java.sql.*;
import tdutils.*;
/**
 * Maneja la información de una envoltura de subtrabajo
 * en el sistema <tt>tdderive</tt>.
 */
public class OIEnvolturas extends OIProcesos {
  protected String id_tarea;
  protected String id_subtrabajo;
  protected String estado_control;
  protected String alias;
  protected boolean si_actual;
  protected boolean si_invitado;
  protected boolean si_remoto;
  protected int numero_confirmaciones;
  public final static String PROC_COMPLETADO="PROC_COMPLETADO";
  public final static String PROC_INICIO="PROC_INICIO";
  public final static String PROC_INTERRUMPIDO="PROC_INTERRUMPIDO";
  public final static String PROC_MARCHA="PROC_MARCHA";
  protected OIEnvolturas(OIDescriptor info0,boolean sivacio){
    super(info0,sivacio);
  }
  public void open(){
  }
  public String inserttext0()throws OIExcepcion{
    String res;
    res = ",estado_control,id_subtrabajo,id_tarea"+
          ",numero_confirmaciones,si_actual,alias";
    return res;
  }
  public String inserttext1()throws OIExcepcion{
    String res;
    res = ","+tdutils.getQ(estado_control) +
                     "," + tdutils.getQ(id_subtrabajo) +
                     "," + tdutils.getQ(id_tarea) +
                     "," + numero_confirmaciones +
                     "," + si_actual +
                     "," + tdutils.getQ(alias);
    return res;
  }
  public String updatetext()throws OIExcepcion{
    String res;
    res = ",estado_control="+tdutils.getQ(estado_control)+
          ",id_subtrabajo="+tdutils.getQ(id_subtrabajo)+
          ",id_tarea="+tdutils.getQ(id_tarea)+
          ",numero_confirmaciones="+numero_confirmaciones+
          ",si_actual="+si_actual+
          ",alias="+tdutils.getQ(alias);
    return res;
  }
  protected final void preWrite() throws OIExcepcion {
    int res=0;
    String tabla="Envolturas";
    res = doUpdateSQL("UPDATE "+tabla+" SET " +
        " si_actual=false " +
        ",estado_control="+tdutils.getQ(PROC_INTERRUMPIDO)+
        " WHERE id_parcial!=" + tdutils.getQ(id_parcial)+
        " AND id_tarea="+tdutils.getQ(id_tarea)+
        " AND id_subtrabajo="+tdutils.getQ(id_subtrabajo));
  }  
  protected void openRS0(ResultSet rs) throws SQLException{
    estado_control=rs.getString("estado_control");
    id_subtrabajo=rs.getString("id_subtrabajo");
    id_tarea=rs.getString("id_tarea");
    alias=rs.getString("alias");
    numero_confirmaciones=rs.getInt("numero_confirmaciones");
    si_actual=rs.getBoolean("si_actual");
  }
  public void close(){
  }
}
