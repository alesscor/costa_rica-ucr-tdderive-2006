package orgainfo;
import java.sql.*;

/**
 * Interfaz para todo ente persistente de <tt>tdderive</tt>.
 */
public abstract class OIPersistente {
  public OIPersistente() {
  }
  public OIDescriptor getDescriptor(){
    return info;
  }
  public void setDescriptor(OIDescriptor setDescriptor){
    info=setDescriptor;
  }
  protected OIDescriptor info;
  public OIPersistente(OIDescriptor info0,boolean siVacio) {
    info=info0;
    if(siVacio){
      try {
        creaVacio();
      }
      catch (OIExcepcion ex) {
      }
    }else{
      try {
        creaUltimo();
      }
      catch (OIExcepcion ex1) {
      }
    }
  }
  public void creaVacio() throws OIExcepcion{
  }
  public void creaUltimo() throws OIExcepcion{
  }
  /**
   * Ejecuta una instruccion de SQL sobre la conexión dada e indica si
   * ésta tuvo éxito.
   * @param sql La instrucción a ejecutar.
   * @return Si hubo éxito en la operación..
   * @throws OIExcepcion Si hay error de acceso a los datos.
   */
  protected int doUpdateSQL(String sql) throws OIExcepcion{
    int cuenta=0;
    Statement instruccion=null;
    try {
       instruccion=this.info.infobd.connbd.createStatement();
       cuenta = instruccion.executeUpdate(sql);
    }catch (SQLException ex) {
      throw new OIExcepcion("Problema con objeto de datos persistente.",ex);
    }
    return cuenta;
  }
  Connection getConnbd(){
    return info.infobd.connbd;
  }
  /**
   * Ejecuta una instruccion de SQL sobre la conexión dada e indica si
   * ésta tuvo éxito.
   * @param conn La conexión a utilizar.
   * @param sql La instrucción a ejecutar.
   * @return Si hubo éxito en la operación..
   * @throws OIExcepcion Si hay error de acceso a los datos.
   */
  public static int doUpdateSQL(OIDescriptor conn,String sql)
      throws OIExcepcion{
    int count=0;
    Statement instruccion=null;
    boolean res=false;
    try {
      instruccion=conn.infobd.connbd.createStatement();
      count = instruccion.executeUpdate(sql);
    }catch (SQLException ex) {
      throw new OIExcepcion("Problema con objeto de datos persistente.",ex);
    }
    return count;
  }
  /**
   * Ejecuta una instruccion de SQL sobre la base de datos de la conexión
   * del objeto.
   * @param sql La instrucción a ejecutar.
   * @return El <tt>Recordset</tt> resultante.
   * @throws OIExcepcion Si hay error de acceso a los datos.
   */
  protected java.sql.ResultSet getRSSQL(String sql) throws OIExcepcion{
    ResultSet resDB=null;
    Statement instruccion=null;
    try {
      instruccion=info.infobd.connbd.createStatement();
      resDB = instruccion.executeQuery(sql);
    }catch (SQLException ex) {
      throw new OIExcepcion("Problema con objeto de datos persistente.",ex);
    }
    return resDB;
  }

  /**
   * Ejecuta una instruccion de SQL sobre la conexión dada.
   * @param conn La conexión a utilizar.
   * @param sql La instrucción a ejecutar.
   * @return El <tt>Resultset</tt> resultante.
   * @throws OIExcepcion Si hay error de acceso a los datos.
   */
  public static java.sql.ResultSet getRSSQL(OIDescriptor conn,String sql)
      throws OIExcepcion{
    ResultSet resDB=null;
    Statement instruccion=null;
    try {
      instruccion=conn.infobd.connbd.createStatement();
      resDB = instruccion.executeQuery(sql);
    }catch (SQLException ex) {
      throw new OIExcepcion("Problema con objeto de datos persistente.",ex);
    }
    return resDB;
  }

}