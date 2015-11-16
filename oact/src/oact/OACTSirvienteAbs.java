package oact;

/**
 * Estructura de un proveedor de servicios abstracto dentro
 * del objeto activo, del cual se solicitan operaciones.
 */
public class OACTSirvienteAbs {
  private String id;
  private boolean _isset_id;
//  private aco.ACONGestor gestor;
  /**
   * Distribuidor de los servicios en el patrón del objeto activo.
   */
  private OACTDistribuidorBase servidor;
  public OACTSirvienteAbs(){
    id=null;
    _isset_id=false;
  }
  public OACTSirvienteAbs(String id0){
    setId(id0);
  }
  public final String getId(){
    return id;
  }
  public final void setId(String id0){
    if ((id0!="") && (id0!=null)){
      id=id0;
      _isset_id=true;
    }
  }
//  public final void setGestorServicios(aco.ACONGestor sh0){
//    gestor=sh0;
//  }
//  public final aco.ACONGestor getGestorServicios(){
//    return gestor;
//  }
  /**
   * Maneja solicitudes vacías que por vencimiento de tiempo no pudieron
   * llegar.<br>
   * - Este método debe ser sobredefinido por clases derivadas.
   */
  public void manejaSolicitudVacia(){
  }
//  /**
//   * Recupera el servidor distribuidor de la atención a las
//   * solicitudes de ejecución de métodos.
//   * @return El objeto distribuidor.
//   */
//  protected final OACTDistribuidor getDistribuidor(){
//    return servidor;
//  }
  /**
   * Asigna el servidor distribuidor de la atención a las solicitudes
   * de ejecución de métodos.
   * @param dist0 El servidor distribuidor a asignar.
   */
  public final void setDistribuidor(OACTDistribuidorBase dist0){
    servidor=dist0;
  }
  /**
   * Obtiene el servidor distribuidor de la atención a las solicitudes
   * de ejecución de métodos.
   * @return El servidor distribuidor a asignar.
   */
  public final OACTDistribuidorBase getDistribuidor(){
    return servidor;
  }  
  public void interProxy(String destino, OACTSolicitud entrada, 
      String[] salida) throws OACTExcepcion{
    this.servidor.interProxy(destino,entrada,salida);
  }
//  public void interProxy(String destino, String entrada, String[] salida){
//    System.out.println("\nVa a mandar:\n"+entrada);
//    this.servidor.interProxy(destino,salida,null);
//    System.out.println("\nVa a recibir:\n"+salida[0]);
//  }
  public String infla(String hilera) throws OACTExcepcion{
    return OACTDistribuidorBase.infla(hilera);
  }
  public String desinfla(String hilera) throws OACTExcepcion{
    return OACTDistribuidorBase.desinfla(hilera);
  }
  public void cargaSolicitud(OACTSolicitud obj,String contenido) throws
 OACTExcepcion {
    this.servidor.cargaSolicitud(obj,contenido);
  }
  public final String getStatus(){
    String status="";
    status+="<id>";
    status+=this.id;
    status+="</id>";
    status+="<servidor>";
    status+=this.servidor.getClass().getName();
    status+="</servidor>";
    status+="<className>";
    status+=this.getClass().getName();
    status+="</className>";
    status+=getConcreteStatus();
    return status;
  }
  protected String getConcreteStatus(){
    return "";
  }
}