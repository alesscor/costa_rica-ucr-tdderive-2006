package orgainfo;
import java.sql.*;
import tdutils.*;
/**
 * Indica información sobre el estado de la base de datos
 * de <tt>tdderive</tt>.
 */
public class OIEstado_tablas extends OIPersistente implements OIActualiza{
  protected boolean si_terminada;
  protected OIEstado_tablas(){
    super(null,false);
  }
  protected OIEstado_tablas(OIDescriptor info0){
    super(info0,false);
  }
  public void open() throws OIExcepcion{
    ResultSet resDB=null;
    if(this.getConnbd()==null){
      throw new OIExcepcion("No se tiene conexión para cargar la tabla "+
                               "'Estado_tablas'.");
    }
    resDB=getRSSQL("select si_terminada from estado_tablas");
    try {
      if(resDB.next()){
        this.si_terminada = resDB.getBoolean("si_terminada");
      }else{
        this.si_terminada = false;
      }
      resDB.close();
    }catch (SQLException ex) {
      throw new OIExcepcion("No se tiene acceso a la base de datos.",ex);
    }
  }
  public void write()throws OIExcepcion{
    int cuenta=doUpdateSQL("update estado_tablas set"+
                             "si_terminada="+tdutils.getQ(si_terminada));
    if(cuenta==0){
      throw new OIExcepcion("No se pudo actualizar la tabla estado_tablas.");
    }
  }
  public void delete()throws OIExcepcion{
  }
  public void close()throws OIExcepcion{
  }
}
