/*
 * Created on 11/05/2004
 */
package orgainfo;
import java.sql.*;
import tdutils.*;
/**
 * Contiene información sobre la entrega de resultados.
 */
public class OIRetornos extends OIPersistente implements OIActualiza {
  protected String tipo_retorno;
  protected String valor_retorno;
  protected String estado_retorno;
  protected String id_parcial;
  protected String id_retorno;
  protected String id_padre;
  protected String id_grupo;
  /**
   * 
   */
  public OIRetornos() {
    super();
  }
  /**
   * @param info0
   * @param siVacio
   */
  public OIRetornos(OIDescriptor info0, boolean siVacio) {
    super(info0, siVacio);
  }
  protected void openRS(ResultSet rs) throws SQLException{
    id_parcial=rs.getString("id_parcial");
    id_padre=rs.getString("id_padre");
    id_grupo=rs.getString("id_grupo");
    id_retorno=rs.getString("id_retorno");
    tipo_retorno=rs.getString("tipo_retorno");
    valor_retorno=rs.getString("valor_retorno");
    estado_retorno=rs.getString("estado_retorno");    
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
    if(id_parcial==null||id_parcial==""||id_padre==""||id_grupo==""
        ||id_retorno==""){
      throw new OIExcepcion("Un retorno debe tener asignado "+
      "un solicitante y una identificación.");
    }
    int res=0;
    //
    // actualiza
    //
    res = doUpdateSQL("UPDATE Retornos set " +
                     "id_parcial=" + tdutils.getQ(id_parcial) +
                     ",id_padre=" + tdutils.getQ(id_padre) +
                     ",id_grupo=" + tdutils.getQ(id_grupo) +
                     ",id_retorno=" + tdutils.getQ(id_retorno) +
                     ",valor_retorno=" + tdutils.getQ(valor_retorno) +
                     ",estado_retorno=" + tdutils.getQ(estado_retorno) +
                     ",tipo_retorno=" + tdutils.getQ(tipo_retorno) +
                     " WHERE id_parcial=" + tdutils.getQ(id_parcial)+
                     " AND id_padre="+ tdutils.getQ(id_padre)+
                     " AND id_padre="+ tdutils.getQ(id_padre)+
                     " AND id_retorno="+ tdutils.getQ(id_retorno));
    //
    // si actualización no sirve, hace un insert
    //
    if (res==0) {
      res = doUpdateSQL("INSERT INTO Retornos (id_parcial,id_padre," +
                       "id_grupo,id_retorno,valor_retorno,estado_retorno," +                       "tipo_retorno) values(" +
                       tdutils.getQ(id_parcial) +
                       "," + tdutils.getQ(id_padre) +
                       "," + tdutils.getQ(id_grupo) +
                       "," + tdutils.getQ(id_retorno) +
                       "," + tdutils.getQ(valor_retorno) +
                       "," + tdutils.getQ(estado_retorno) +
                       "," + tdutils.getQ(tipo_retorno) +
                       ")");
    }
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo actualizar en tabla 'Retornos'.");
    }
  }
  /**
   * @see orgainfo.OIActualiza#delete()
   */
  public void delete() throws OIExcepcion {
    int res=0;
    //
    // actualiza
    //
    res = doUpdateSQL("DELETE Retornos " +
                     " WHERE id_parcial=" + tdutils.getQ(id_parcial)+
                     " AND id_padre="+ tdutils.getQ(id_padre)+
                     " AND id_padre="+ tdutils.getQ(id_padre)+
                     " AND id_retorno="+ tdutils.getQ(id_retorno));
    //
    // si actualización no sirve, hace un insert
    //
    if (res==0) {
      throw new OIExcepcion("No se pudo borrar de la tabla 'Retornos'.");
    }
  }
  /**
   * @see orgainfo.OIActualiza#close()
   */
  public void close() throws OIExcepcion {
  }
}
