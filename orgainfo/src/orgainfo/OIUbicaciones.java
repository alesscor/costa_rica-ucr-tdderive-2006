package orgainfo;
import java.sql.ResultSet;
import java.sql.SQLException;

import tdutils.tdutils;
/**
 * Lleva la información necesaria ubicaciones de nodos en <tt>tdderive</tt>. 
 */
public class OIUbicaciones extends OIPersistente{
  public String nombre;
  public String direccion;
  public int desplaza_puerto;
  protected boolean si_local;
  protected OIUbicaciones(){
    super(null,false);
  }
  protected void openRS(ResultSet rs) throws SQLException{
    nombre=rs.getString("NOMBRE");
    direccion=rs.getString("DIRECCION");
    si_local=rs.getBoolean("SI_LOCAL");
    try{
      desplaza_puerto=rs.getInt("DESPLAZA_PUERTO");
    }catch(Exception exc){
      desplaza_puerto=0;
    }
  }
  protected void write()throws OIExcepcion{
    int res=0;
    //
    // actualiza
    res = doUpdateSQL("UPDATE ubicaciones set " +
                     "direccion=" + tdutils.getQ(direccion) +
                     ",nombre=" + tdutils.getQ(nombre) +
                     ",si_local=" + si_local +
                     ",desplaza_puerto=" + desplaza_puerto +
                     " WHERE nombre=" + tdutils.getQ(nombre));
    //
    // si actualización no sirve, hace un insert
    if (res==0) {
      res = doUpdateSQL("insert into ubicaciones (nombre,direccion," +
                       "si_local,desplaza_puerto) values(" +
                       tdutils.getQ(nombre) +
                       "," + tdutils.getQ(direccion) +
                       "," + si_local +
                       "," + desplaza_puerto + ")");
    }
    if (res==0) {
      throw new OIExcepcion("No se pudo actualizar la tabla ubicaciones.");
    }
    // System.out.println("Escrito "+nombre+" "+direccion);
  }
}
