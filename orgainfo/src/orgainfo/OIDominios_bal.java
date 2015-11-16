package orgainfo;
import java.sql.*;
import tdutils.*;

/**
 * Brinda información sobre un integrante del dominio de balance
 * de una computadora en <tt>tdderive</tt>.
 */
public class OIDominios_bal extends OIPersistente{
  public String nombre;
  public String vecino;
  public double peso;
  protected double capacidad;
  protected double carga_aplicacion;
  protected double altura;
  protected long ultimo_mensaje;
  protected OIDominios_bal(){
    super(null,false);
  }
  protected OIDominios_bal(OIDescriptor info0db0){
    super(info0db0,false);
  }
  public void openRS(ResultSet rs) throws SQLException{
    nombre=rs.getString("nombre");
    vecino=rs.getString("vecino");
    peso=rs.getDouble("peso");
    capacidad=rs.getDouble("capacidad");
    carga_aplicacion=rs.getDouble("carga_aplicacion");
    altura=rs.getDouble("altura");
    ultimo_mensaje=rs.getLong("ultimo_mensaje");
  }
//  static void openMap(OIDescriptor info0,java.util.Map map,String nombre)throws OIExcepcion{
//    ResultSet resDB;
//    OIDominios_bal pareja;
//    //
//    // carga dominios_bal
//    //
//    resDB=getRSSQL(info0,"SELECT * from dominios_bal "+
//                   " WHERE nombre="+tdutils.getQ(nombre)+
//                   " OR vecino="+tdutils.getQ(nombre));
//    try {
//      if(resDB==null||!resDB.next()){
//        // no hay computadoras en el dominio de la computadora cuyo nombre
//        // fue dado
//      }else{
//        // sí se tienen computadoras
//        do{
//          pareja=new OIDominios_bal();
//          pareja.openRS(resDB);
//          if(pareja.nombre.compareToIgnoreCase(nombre)!=0){
//            // corrige orden de los nombres
//            pareja.vecino=pareja.nombre;
//            pareja.nombre=nombre;
//          }
//          map.put(pareja.vecino,pareja);
//        }while(resDB.next());
//        resDB.close();
//      }
//    }catch (SQLException ex) {
//      throw new OIExcepcion("No se tiene acceso a la base de datos.",ex);
//    }
//  }
//  static void writeMap(OIDescriptor info0,java.util.Map map)throws OIExcepcion{
//    java.util.Iterator itr=null;
//    OIDominios_bal vecino;
//    int res=0;
//    res = doUpdateSQL(info0, "delete from dominios_bal");
//    itr = map.values().iterator();
//    while (itr.hasNext()) {
//      vecino = (OIDominios_bal) itr.next();
//      vecino.info = info0;
//      vecino.write();
//    }
//  }
  public void write()throws OIExcepcion{
    int res=0;
    //
    // actualiza
    res = doUpdateSQL("UPDATE dominios_bal set " +
                     "peso=" + peso +
                     ",capacidad=" + capacidad +
                     ",carga_aplicacion=" + carga_aplicacion +
                     ",altura=" + altura +
                     ",ultimo_mensaje=" + ultimo_mensaje +
                     " WHERE nombre=" + tdutils.getQ(nombre) +
                     " AND vecino=" + tdutils.getQ(vecino));
    //
    // si actualización no sirve, hace un insert
    if (res==0) {
      res = doUpdateSQL("insert into dominios_bal (nombre,vecino," +
                       "peso,capacidad,carga_aplicacion,altura,ultimo_mensaje) values(" +
                       tdutils.getQ(nombre) +
                       "," + tdutils.getQ(vecino) +
                       "," + peso +
                       "," + capacidad +
                       "," + carga_aplicacion +
                       "," + altura +
                       "," + ultimo_mensaje +                       
                       ")");
    }
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo actualizar el dominio de balance.");
    }
  }
}
