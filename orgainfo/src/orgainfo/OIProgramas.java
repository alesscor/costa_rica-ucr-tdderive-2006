package orgainfo;
import java.sql.ResultSet;
import java.sql.SQLException;

import tdutils.tdutils;

/**
 * Guarda información sobre los programas de aplicación que
 * se registran para poder ser llamados en <tt>tdderive</tt>.
 */
public class OIProgramas extends OIPersistente implements OIActualiza {
  protected String alias;
  protected String nombre_aplicacion;
  protected String ruta;
  protected String clase;
  protected long tiempo_ensistema;
  protected long periodo_confirmacion;
  protected long umbral_espera;
  protected boolean si_cambiarcompu;
  protected String divisora;
  protected String unificadora;
  protected OIProgramas(){
    super(null,false);
  }
  protected OIProgramas(OIDescriptor info0,boolean sivacio){
    super(info0,sivacio);
  }
  public void open(){
  }
  public void delete(){
  }
  public void close(){
  }
  public void creaVacio(){
  }
  public void creaUltimo(){
  }
  protected void openRS(ResultSet rs) throws SQLException{
    alias=rs.getString("alias");
    nombre_aplicacion=rs.getString("nombre_aplicacion");
    ruta=rs.getString("ruta");
    clase=rs.getString("clase");
    divisora=rs.getString("divisora");
    unificadora=rs.getString("unificadora");
    tiempo_ensistema=rs.getLong("tiempo_ensistema");
    periodo_confirmacion=rs.getLong("periodo_confirmacion");
    umbral_espera=rs.getLong("umbral_espera");
    si_cambiarcompu=rs.getBoolean("si_cambiarcompu");
  }
//  static void openMapXML(String URI,java.util.Map map)throws OIExcepcion{
//		_openMapXML(URI,null,map);
//  }
//	static void openMapXML(Node nodo,java.util.Map map)throws OIExcepcion{
//		_openMapXML(null,nodo,map);
//	}
//  private static void _openMapXML(String URI,Node nodo,java.util.Map map)
//  throws OIExcepcion{
//		LEEProgramas lista=new LEEProgramas();
//		LEEProgramas.LEEPrograma prograI;
//		java.util.Iterator itr;
//		try {
//			if(nodo!=null){
//				lista.setFromXMLNode(nodo);
//			}else{
//				lista.setFromXMLURI(URI);
//			}			
//		}
//		catch (MENSException ex) {
//			throw new OIExcepcion("Error al abrir archivo de programas.",ex);
//		}
//		itr=lista.programas.values().iterator();
//		while(itr.hasNext()){
//			prograI=(LEEProgramas.LEEPrograma)itr.next();
//			map.put(prograI.progra.alias,prograI.progra);
//		}  	
//  }
//  	
//  static void openMap(OIDescriptor info0,java.util.Map map)throws OIExcepcion{
//    ResultSet resDB;
//    OIProgramas progra;
//    //
//    // carga programas
//    //
//    resDB=getRSSQL(info0,"SELECT * from programas");
//    try {
//      if(resDB==null||!resDB.next()){
//        // no hay programas
//      }else{
//        // sí se tienen programas
//        do{
//          progra=new OIProgramas(info0,false);
//          progra.openRS(resDB);
//          map.put(progra.alias,progra);
//        }while(resDB.next());
//        resDB.close();
//      }
//    }catch (SQLException ex) {
//      throw new OIExcepcion("No se tiene acceso a la base de datos.",ex);
//    }
//  }
//  static void writeMap(OIDescriptor info0,java.util.Map map)throws OIExcepcion{
//    java.util.Iterator itr=null;
//    OIProgramas progra;
//    int res=0;
//    res = doUpdateSQL(info0, "delete from programas");
//    itr = map.values().iterator();
//    while (itr.hasNext()) {
//      progra = (OIProgramas) itr.next();
//      progra.info = info0;
//      progra.write();
//    }
//  }
  public void write()throws OIExcepcion{
    int res=0;
    //
    // actualiza
    //
    res = doUpdateSQL("UPDATE Programas set " +
                     "alias=" + tdutils.getQ(alias) +
                     ",nombre_aplicacion=" + tdutils.getQ(nombre_aplicacion) +
                     ",ruta=" + tdutils.getQ(ruta) +
                     ",clase=" + tdutils.getQ(clase) +
                     ",divisora=" + tdutils.getQ(divisora) +
                     ",unificadora=" + tdutils.getQ(unificadora) +
                     ",tiempo_ensistema=" + tiempo_ensistema +
                     ",periodo_confirmacion=" + periodo_confirmacion +
                     ",umbral_espera=" + umbral_espera +
                     ",si_cambiarcompu=" + si_cambiarcompu +
                     " WHERE alias=" + tdutils.getQ(alias));
    //
    // si actualización no sirve, hace un insert
    //
    if (res==0) {
      res = doUpdateSQL("insert into Programas (alias,nombre_aplicacion," +
                       "ruta,clase,divisora,unificadora,tiempo_ensistema," +                       "periodo_confirmacion,"+
                       "umbral_espera,si_cambiarcompu) values(" +
                       tdutils.getQ(alias) +
                       "," + tdutils.getQ(nombre_aplicacion) +
                       "," + tdutils.getQ(ruta) +
                       "," + tdutils.getQ(clase) +
                       "," + tdutils.getQ(divisora) +
                       "," + tdutils.getQ(unificadora) +
                       "," + tiempo_ensistema +
                       "," + periodo_confirmacion +
                       "," + umbral_espera +
                       "," + si_cambiarcompu +
                       ")");
    }
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo actualizar lista de programas.");
    }
  }
}
