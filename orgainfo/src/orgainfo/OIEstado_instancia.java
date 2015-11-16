package orgainfo;
import java.sql.*;

/**
 * Contiene información sobre la instancia anterior y actual de
 * <tt>tdderive</tt>.
 */
public class OIEstado_instancia extends OIPersistente implements OIActualiza{
  /**
   * Indica que otra instancia de admin está en curso.
   */
	public static final int INSTANCIAOTRA=1;
  /**
   * Indica que otra aplicación se ha apropiado de recursos exclusivos
   * de <tt>tdderive</tt>.
   */
  public static final int INSTANCIANOFACTIBLE=2;
  /**
   * Indica que <tt>tdderive</tt> trabaja normalmente.
   */
	public static final int INSTANCIAVALIDA=4;
  /**
   * Indica que <tt>tdderive</tt> trabaja luego de un cierre brusco.
   */
	public static final int INSTANCIARECUPERADA=8;
  /**
   * Indica que <tt>tdderive</tt> va a cerrarse.
   */
	public static final int INSTANCIACERRADA=16;
  /**
   * Indica que <tt>tdderive</tt> se ha abierto por primera vez.
   */
	public static final int INSTANCIAPRIMERAVEZ=32;
	/**
	 * Indica si la instancia ha sido iniciada.
	 */
  protected boolean si_iniciada;
  /**
   * Indica si la instancia ha sido terminada.
   */
  protected boolean si_terminada;
  protected OIEstado_instancia(){
    super(null,false);
  }
  protected OIEstado_instancia(OIDescriptor info0){
    super(info0,false);
  }
  public void open()throws OIExcepcion{
    ResultSet resDB=null;
    if(this.getConnbd()==null){
      throw new OIExcepcion("No se ha asignado una conexión para abrir "+
                               "la tabla 'estado_instancia'.");
    }
    resDB=getRSSQL("select * from estado_instancia");
    try {
      if(resDB.next()){
        this.si_terminada = resDB.getBoolean("si_terminada");
        this.si_iniciada = resDB.getBoolean("si_iniciada");
      }else{
        throw new OIExcepcion("No hay registros en tabla estado_instancia.");
      }
      resDB.close();
    }catch (SQLException ex) {
      throw new OIExcepcion("No se tiene acceso a la base de datos.",ex);
    }
  }
  public void write()throws OIExcepcion{
    int res=doUpdateSQL("update estado_instancia set "+
                             "si_iniciada="+si_iniciada+
                             ",si_terminada="+si_terminada);
    if(res==0){
      throw new OIExcepcion("No se pudo actualizar tabla estado_instancia.");
    }
  }
  public void delete()throws OIExcepcion{
  }
  public void close()throws OIExcepcion{
    this.si_iniciada=false;
    this.si_terminada=true;
    this.write();
  }
}
