package orgainfo;
import java.sql.*;
import tdutils.*;
/**
 * Encapsula los datos sobre pesos, umbrales y datos importantes
 * de política de administración de recursos en <tt>tdderive</tt>.
 */
public class OIPesos_Umbrales_Politicas extends OIPersistente{
  public String id_pesoumbral;
  protected double valor;
  protected OIPesos_Umbrales_Politicas(OIDescriptor info0db0){
    super(info0db0,false);
  }
  final public String toString(){
    return "("+this.id_pesoumbral+","+this.valor+")";
  }
	protected OIPesos_Umbrales_Politicas(){
		super(null,true);
	}
	protected void openRS(ResultSet rs) throws SQLException{
		id_pesoumbral=rs.getString("id_pesoumbral");
		valor=rs.getDouble("valor");		
	}
	protected void write()throws OIExcepcion{
		int res=0;
		//
		// actualiza
		res = doUpdateSQL("UPDATE Pesos_Umbrales_Politicas set " +
										 "id_pesoumbral=" + tdutils.getQ(id_pesoumbral) +
										 ",valor=" + valor +
										 " WHERE id_pesoumbral=" + tdutils.getQ(id_pesoumbral));
		//
		// si actualización no sirve, hace un insert
		if (res==0) {
			res = doUpdateSQL("insert into Pesos_Umbrales_Politicas (id_pesoumbral,"+
                        "valor) values(" +
											  tdutils.getQ(id_pesoumbral) +
											  "," + valor + ")");
		}
		if (res==0) {
			throw new OIExcepcion("No se pudo actualizar la tabla "+
                            "'Pesos_Umbrales_Politicas'.");
		}
	}  
}
