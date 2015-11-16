package orgainfo;
import java.sql.*;
import tdutils.*;

/**
 * Da forma al solicitante de una aplicación de <tt>tdderive</tt>.
 */

public class OISolicitantes extends OIProcesos {
  protected String tipo_sol;
  protected String retorno;
  protected String desde_nombre;
  protected long desde_puerto;
  protected boolean si_entregado;
  protected String estado_solicitante;
  protected OISolicitantes(){
    super(null,false);
  }

  protected OISolicitantes(OIDescriptor info0,boolean sivacio){
    super(info0,sivacio);
  }
  public void open(){
  }
  public String inserttext1()throws OIExcepcion{
    return ","+tdutils.getQ(tipo_sol)+","+
        tdutils.getQ(retorno)+","+si_entregado+","+
        tdutils.getQ(desde_nombre)+","+desde_puerto+
        ","+tdutils.getQ(estado_solicitante);
  }
  public String inserttext0()throws OIExcepcion{
    String res="";
    res = ",tipo_sol,retorno,si_entregado,desde_nombre,"+
          "desde_puerto,estado_solicitante";
    return res;
  }
  public String updatetext()throws OIExcepcion{
    return ",tipo_sol="+tdutils.getQ(tipo_sol)+
           ",retorno="+tdutils.getQ(retorno)+
           ",si_entregado="+si_entregado+
           ",desde_puerto="+desde_puerto+
           ",desde_nombre="+tdutils.getQ(desde_nombre)+
           ",estado_solicitante="+tdutils.getQ(estado_solicitante);
  }
  protected void openRS0(ResultSet rs) throws SQLException{
    tipo_sol=rs.getString("tipo_sol");
    retorno=rs.getString("retorno");
    si_entregado=rs.getBoolean("si_entregado");
    desde_nombre=rs.getString("desde_nombre");
    desde_puerto=rs.getLong("desde_puerto");
    estado_solicitante=rs.getString("estado_solicitante");
  }
  public void close(){
  }

}
