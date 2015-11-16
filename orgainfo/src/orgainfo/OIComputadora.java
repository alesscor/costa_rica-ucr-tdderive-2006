package orgainfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import tdutils.*;
/**
 * Clase para representar las propiedades de una computadora que son
 * de interés de <tt>tdderive</tt>.
 */
public abstract class OIComputadora extends OIPersistente {
  protected String nombre;
  protected String direccion;
  protected double capacidad;
  public double carga_funcional;
  public double carga_aplicacion;
  protected int vecinas_usadas;
  protected int vecinas_apoyadas;
  protected String clase_balance;
  /**
   * Cantidad de subtrabajos en marcha.
   */
  protected double local_activa;
  /**
   * Cantidad de subtrabajos en espera.
   */
  protected double local_espera;
  protected int vecinos_cant;
  protected String buses_tipo;
  protected int micro_cant;
  protected double micro_reloj;
  protected double memoria_cant;
  protected double disco_cant;
  protected double micro_libre;
  protected double mem_libre;
  protected double disco_libre;
  /**
   * Lista de computadoras que habitan en el dominio y sus detalles.
   */
  protected java.util.Map _vecinos;
  /**
   * Lista de computadoras y sus direcciones.
   */
  protected java.util.Map _ubicaciones;
  /**
   * Lista de programas disponibles para la computadora.
   */
  protected java.util.Map _programas;
  /**
   * Lista de pesos y umbrales.
   */
  protected java.util.Map _pesos_umbrales;
  /**
   * Lista de computadoras virtuales para el sistema.
   */
  protected Map _direccionesVirtuales;
  
  
  protected OIComputadora(){
    super(null,false);
    _inicio();
  }
  protected OIComputadora(OIDescriptor info0){
    super(info0,false);
    _inicio();
  }
  private void _inicio(){
    // inicia conjuntos
    _vecinos=java.util.Collections.synchronizedMap(
        new TreeMap(String.CASE_INSENSITIVE_ORDER));
    _ubicaciones=java.util.Collections.synchronizedMap(
        new TreeMap(String.CASE_INSENSITIVE_ORDER));
    _programas=java.util.Collections.synchronizedMap(
        new TreeMap(String.CASE_INSENSITIVE_ORDER));
		_pesos_umbrales=java.util.Collections.synchronizedMap(
				new TreeMap(String.CASE_INSENSITIVE_ORDER));
    _direccionesVirtuales=java.util.Collections.synchronizedMap(
        new TreeMap(String.CASE_INSENSITIVE_ORDER));
    // recupera la dirección
    try {
      direccion = java.net.InetAddress.getLocalHost().getHostAddress();
    }catch (Exception ex) {
      System.err.println("Usando la dirección por omisión.");
      direccion="localhost";
    }
  }
  /**
   * Carga en el objeto los valores de la computadora que están en 
   * la base de datos.
   * <li>Si no encuentra información le pone al nombre el valor nulo.</li>
   * @throws OIExcepcion Si no hay acceso a la base de datos.
   */
  protected void loadComputadora() throws OIExcepcion {
    ResultSet resDB;
    resDB=getRSSQL(info,"SELECT * FROM Computadora ");
    try {
      if(resDB==null||!resDB.next()){
        nombre=null;
      }else{
        // sí se tiene info de la computadora guardada
        openRS(resDB);
        resDB.close();
      }
    }catch (SQLException ex) {
      throw new OIExcepcion("No se tiene acceso a la base de datos.",ex);
    }
  }
  
  /**
   * Carga desde la base de datos.
   * <li>Método a sobrecargar.</li>
   * @param uriUbicaciones Archivo de las ubicaciones de computadoras.
   * @param uriProgramas Archivo con información de programas.
   * @param uriUmbrales Archivo con información de pesos y umbrales.
   * @throws OIExcepcion Si hay error.
   */
  protected void open(String uriUbicaciones,String uriProgramas,String uriUmbrales)
      throws OIExcepcion{
  }
  /**
   * Escribe en la base de datos.
   * @throws OIExcepcion
   */
  public void write() throws OIExcepcion{
    int res=0;
    res = doUpdateSQL(info, "UPDATE computadora " +
        " SET " +
        " nombre="+tdutils.getQ(nombre)+
        " ,clase_balance="+tdutils.getQ(clase_balance)+
        " ,buses_tipo="+tdutils.getQ(buses_tipo)+
        " ,micro_cant="+micro_cant+
        " ,micro_reloj="+micro_reloj+
        " ,memoria_cant="+memoria_cant+
        " ,disco_cant="+disco_cant+
        " ,micro_libre="+micro_libre+
        " ,mem_libre="+mem_libre+
        " ,disco_libre="+disco_libre+
        "");
    if(res==0){
      res = doUpdateSQL(info,
          " INSERT INTO computadora " +
          " (nombre,clase_balance,buses_tipo,micro_cant,micro_reloj," +
          " memoria_cant,disco_cant,micro_libre,mem_libre," +
          " disco_libre)"+ "" +
          " VALUES (" +
          tdutils.getQ(nombre)+
          ","+tdutils.getQ(clase_balance)+
          ","+tdutils.getQ(buses_tipo)+
          ","+micro_cant+
          ","+micro_reloj+
          ","+memoria_cant+
          ","+disco_cant+
          ","+micro_libre+
          ","+mem_libre+
          ","+disco_libre+
          ")");      
    }
    if(res==0){
      throw new OIExcepcion("No se pudo guardar en la tabla 'Computadora'");
    }
  }
  /**
   * Borra de la base de datos.
   * @throws OIExcepcion
   */
  public void delete() throws OIExcepcion{
  }
  /**
   * Cierra el objeto.
   * @throws OIExcepcion
   */
  public void close() throws OIExcepcion{
  }
  /**
   * @return La dirección de la computadora.
   */
  public String getDireccion() {
    return direccion;
  }

  /**
   * @param string
   */
  public void setDireccion(String string) {
    direccion= string;
  }
  protected void openRS(ResultSet rs) throws SQLException{
    nombre=rs.getString("nombre");
    buses_tipo=rs.getString("buses_tipo");
    clase_balance=rs.getString("clase_balance");
    micro_cant=rs.getInt("micro_cant");
    micro_reloj=rs.getInt("micro_reloj");
    memoria_cant=rs.getDouble("memoria_cant");
    disco_cant=rs.getDouble("disco_cant");
    clase_balance=rs.getString("clase_balance");
    micro_libre=rs.getDouble("micro_libre");
    mem_libre=rs.getDouble("mem_libre");
    disco_libre=rs.getDouble("disco_libre");
  }

}
