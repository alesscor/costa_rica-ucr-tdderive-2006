package admin;



/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Iniciador de aplicaciones abstracto. 
 */
public abstract class ADMINAPPIniciador {
  public final static char APP_SEPARADORARGUMENTOS='\n';
  public final static String APP_CUALQUIERDESTINO="*";
  public final static String APP_SEPARADORDESTINO="@";
  public final static String APP_ALIASOMISION="dderive";
  /**
   * Utilizado como argumento de una aplicación para 
   * forzar la distribución de un procesamiento. En esta versión
   * del sistema siempre se invoca.
   */
  public final static String APP_ARGFORZADISTRIBUCION="TDDFORZDISTRIB";
  private char cSeparadorArgumentos;
  private String cDestino;
  private String cSeparadorDestino;
  private String[] acParametros;
  private ADMINAPPDespachadorProxy proxy;
  private ADMINAPPMetodoSolicitud solicitudMensaje;
  private String cAlias;
  private String directorio;
  ADMINAPPDistribuidorProxy base;
  public ADMINAPPIniciador(String cConfOACT){
    cSeparadorArgumentos=APP_SEPARADORARGUMENTOS;
    cDestino=APP_CUALQUIERDESTINO;
    cSeparadorDestino=APP_SEPARADORDESTINO;
    proxy=new ADMINAPPDespachadorProxy();
    solicitudMensaje=new ADMINAPPMetodoSolicitud();
    cAlias=APP_ALIASOMISION;
    base=new ADMINAPPDistribuidorProxy(cConfOACT);
    String directorio_usuario="";
    directorio_usuario=System.getProperty("user.dir");
    directorio=directorio_usuario+"/"+ADMINAPPITareas.DIR_PREFIJOTAREA+
        String.valueOf(System.currentTimeMillis());
//    System.err.println(directorio);
  }
  public char getSeparadorArgumentos(){
    return cSeparadorArgumentos;
  }
  protected void setSeparadorArgumentos(char setSeparadorArgumentos){
    cSeparadorArgumentos=setSeparadorArgumentos;
  }
  public String getDestino(){
    return cDestino;
  }
  protected void setDestino(String setDestino){
    cDestino=setDestino;
    solicitudMensaje.setNombreDestino(cDestino);
    proxy.setDestino(cDestino);
  }
  public String getSeparadorDestino(){
    return cSeparadorDestino;
  }
  protected void setSeparadorDestino(String setSeparadorDestino){
    cSeparadorDestino=setSeparadorDestino;
  }
  public String getAlias(){
    return cAlias;
  }
  protected void setAlias(String setAlias){
    cAlias=setAlias;
  }
  public String[] getParametros(){
    return this.acParametros;
  }
  /**
   * Prepara los objetos para con ellos realizar una invocación.
   * <li>Prepara el objeto de solicitudes <i>solicitudMensaje</i>.</li>
   * <li>Da valor al objeto <i>cDestino</i>.</li>
   * <li>Indica los parámetros en <i>acParametros</i>.</li>
   * @param argumentos Argumentos de la aplicación con base de los cuales
   * se obtienen los valores de los objetos que se preparan en este método
   * (indicados arriba).
   * @throws Exception si hay error en el análisis de los objetos.
   */
  protected abstract void analizaArgumentos(String[] argumentos) throws
    ADMINAPPExcepcion;
  /**
   * Método que invoca el trabajo, con los valores establecidos por
   * <i>analizaArgumentos</i>.
   * <li>Realiza una llamada al método abstracto <i>analizaArgumentos</i>,
   * con el cual se deben concretar los objetos necesarios para la
   * invocación.</li>
   * @param argumentos Argumentos de la aplicación con los cuales
   * se realiza la ejecución.
   */
  public final void ejecuta(String[] argumentos) throws ADMINAPPExcepcion{
    this.analizaArgumentos(argumentos);
    if(this.getDestino().compareTo(ADMINAPPIniciador.APP_CUALQUIERDESTINO)==0){
      this.setDestino(base.getDestinoOmision());
      // @TODO Campo si_local puesto verdadero parece forzado.
      this.solicitudMensaje.setSiLocal(
          this.base.getDestinoOmision().compareToIgnoreCase("localhost")==0);
    }
    this.solicitudMensaje.solicitante.setEstadoSolicitante(
            ADMINAPPISolicitantes.SOLICITANTE_LISTO);
    this.solicitudMensaje.setDirectorioLocal(this.getDirectorioLocal());
    this.proxy.setDistribuidor(base);
    this.proxy.setSolicitudMensaje(solicitudMensaje);
    this.proxy.ejecuta(this.getAlias(),this.getParametros());
  }
  /**
   * Indica cuál es el directorio local del iniciador de una aplicación
   * @return El nombre del directorio del iniciador.
   */
  protected String getDirectorioLocal(){
    return directorio;
  }
  /**
   * Agrega un nuevo objeto Retorno.
   * @param tipoRetorno El tipo del retorno.
   * @param valorRetorno El valor específico para el tipo de retorno.
   */
  final protected void addRetorno(String tipoRetorno,String valorRetorno){
    this.solicitudMensaje.addRetorno(tipoRetorno, valorRetorno);
  }
  /**
   * Agrega un nuevo objeto Retorno.
   * @param tipoRetorno El tipo del retorno.
   */
  final protected void addRetorno(String tipoRetorno){
    this.addRetorno(tipoRetorno, "");
  }
  final protected void setAliasPrograma(String setAliasPrograma){
    this.solicitudMensaje.setAliasPrograma(setAliasPrograma);
  }
  final protected ADMINAPPIArchivos addArchivo(String addArchivo){
    return this.solicitudMensaje.addArchivoExt(addArchivo);
  }
  final protected void setParametrosPrograma(String setParametrosPrograma){
    // alesscor 051220
    String adicional="--"+ADMINAPPIniciador.APP_ARGFORZADISTRIBUCION;
    if(setParametrosPrograma.indexOf(adicional)<0){
      setParametrosPrograma=setParametrosPrograma+" "+adicional;
    }
    this.solicitudMensaje.setParametrosPrograma(setParametrosPrograma);
  }
  final protected String getParametrosPrograma(){
    return this.solicitudMensaje.getParametrosPrograma();
  }
}
