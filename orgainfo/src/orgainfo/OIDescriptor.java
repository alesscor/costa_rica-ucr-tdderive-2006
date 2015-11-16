package orgainfo;
import java.sql.*;
import java.io.*;
import java.util.zip.*;
/**
 * Descriptor de la base de datos y sus características.
 */
public class OIDescriptor {
  // block size for copying data
  private static final int COPY_BLOCK_SIZE = 1 << 16;
  protected OIComputadora compu;
  protected OIEstado_tablas estado_tablas;
  protected OIEstado_instancia estado_instancia;
  protected OIConexion infobd;
  protected String raiz_tdderive="/tdderive";
  /**
   * Nombre de la clase que realiza el balance de carga dinámico.
   */
  protected String clase_balance="admin.DIRHidrodinamico";
  public OIDescriptor(String controlador, String url,String usuario,String contrasennna,
            String constr,String destr){
    this.infobd=new OIConexion();
    if(controlador!=null && controlador!=""){
      this.infobd.bd_controlador=controlador;
    }
    if(url!=null && url!=""){
      this.infobd.bd_url=url;
    }
    if(usuario!=null && usuario!=""){
      this.infobd.bd_usuario=usuario;
      this.infobd.bd_password=contrasennna;
    }
    if(constr!=null && constr!=""){
      this.infobd.script_inibd_url=constr;
    }
    if(destr!=null && destr!=""){
      this.infobd.script_finbd_url=destr;
    }
  }
  /**
   * Abre la base de datos y carga objetos de información que toman datos
   * de ella.
   * @throws OIExcepcion Si hubo error en la apertura.
   * @return El estado de la instancia interpretada, a partir de los datos:
   * INSTANCIAPRIMERAVEZ ó BDINCOMPLETA ó BDPREPARADA.
   */
  public int open() throws OIExcepcion{
    ResultSet resDB=null;
    Statement instruccion=null;
    int estado=OIConexion.BDSINCONSTRUIR;
    try {
      Class.forName (this.infobd.bd_controlador);        // carga el controlador JDBC específico
      infobd.connbd = DriverManager.getConnection (      // realiza la conexión
          infobd.bd_url,this.infobd.bd_usuario,this.infobd.bd_password);
    }
    catch (ClassNotFoundException ex) {
      throw new OIExcepcion("No se encontró el controlador para la base de datos.",ex);
    }
    catch (SQLException ex) {
      // he visto que la base de datos se puede desmadrar
      estado = OIConexion.BDCORRUPTA;
      infobd.connbd=null;
      ex.printStackTrace();      
      throw new OIExcepcion("Excepción de JDBC.",ex);
    }
    try {
      // pudo abrir el servidor de base de datos
      infobd.connbd.setAutoCommit(false);
      //
      // revisa información sobre la conexión realizada
      // si la base de datos no existe, la construye y
      // si la base de datos está incompleta, la completa.
      //
      // ví que hubo basura binaria escrita en directorio .script .
      infobd.metadatos=infobd.connbd.getMetaData();
      resDB=infobd.metadatos.getTables(null,null,null,null);
      if(!resDB.next()){ // no hay registros
        System.err.println("La base de datos está vacía!!!");
        // --> la construye
        estado=OIConexion.BDSINCONSTRUIR;
        this.infobd.dbConstruct();
				resDB.close();
				resDB=null;
      }else{
      	// sí hay registros
        // --> debe revisar si la base de datos está incompleta o preparada
        System.err.println("Ya hay tablas en la base de datos!!!");
        resDB.close();
        resDB=null;
        try{
          estado_tablas.open();
        }catch(OIExcepcion ex){
          throw ex;
        }
        if(estado_tablas.si_terminada){
          // --> lista
          estado = OIConexion.BDPREPARADA;
        }else{
          estado = OIConexion.BDINCOMPLETA;
          // --> la destruye y luego la construye
          this.infobd.dbDestroy();
          this.infobd.dbConstruct();
        }
      }
      //
      // revisa el estado de la instancia de tdderive
      //
      try{
        estado_instancia.open();
      }
      catch(OIExcepcion ex){
        throw ex;
      }
      if(estado==OIConexion.BDPREPARADA){
        if ((!estado_instancia.si_iniciada) && estado_instancia.si_terminada) {
          // la instancia está bien
          estado = OIEstado_instancia.INSTANCIAVALIDA;
        }else{
          if (!estado_instancia.si_terminada) {
            estado = OIEstado_instancia.INSTANCIARECUPERADA;
          }
        }
      }else{
        // la bd ahora está lista por primera vez
        estado = OIEstado_instancia.INSTANCIAPRIMERAVEZ;
      }
      estado_instancia.si_iniciada=true;
      estado_instancia.si_terminada=false;
      estado_instancia.write();
      //
      // consuma cambios
      //
      infobd.dbCommit();
      //
      // prepara información de la compu según el estado
      //
      switch(estado){
      case OIEstado_instancia.INSTANCIAPRIMERAVEZ:
        // debe leer archivos de configuración
        //
        // info de sí misma y de otras computadoras está presente en
        // un archivo de configuración.
        // compu.openXML(infobd.ubicaciones_url);
          compu.open(infobd.ubicaciones_url,infobd.programas_url,
          infobd.pesos_umbrales_globales_url);
        // compu.write();
        break;
      case OIEstado_instancia.INSTANCIACERRADA:
        break;
      case OIEstado_instancia.INSTANCIANOFACTIBLE:
        break;
      case OIEstado_instancia.INSTANCIAOTRA:
        break;
      case OIEstado_instancia.INSTANCIARECUPERADA:
        // debe leer la base de datos y recuperarse
        //
        // info de sí misma y de otras computadoras es leída de la base
        // de datos
          infobd.dbRollback();
          compu.open(infobd.ubicaciones_url,infobd.programas_url,
          infobd.pesos_umbrales_globales_url);
        break;
      case OIEstado_instancia.INSTANCIAVALIDA:
        // debe leer la base de datos
        //
        // info de sí misma y de otras computadoras es leída de la base
        // de datos
          compu.open(infobd.ubicaciones_url,infobd.programas_url,
          infobd.pesos_umbrales_globales_url);
        break;
      }
      infobd.dbCommit();
    }catch (SQLException ex) {
      throw new OIExcepcion("No se tiene acceso a los objetos "+
                               "de datos persistentes.",ex);
    }
    //
    // lista la apertura y la carga de los objetos de información.
    // con el estado de la aplicación leído de los datos
    //
    return estado;
  }
  /**
   * Cierra la base de datos.
   * @throws OIExcepcion Si hubo error en el cierre.
   */
  public void close() throws OIExcepcion{
    try {
      estado_tablas.close();
      estado_instancia.close();
      infobd.dbCommit();
      infobd.connbd.close();
    }
    catch (SQLException ex) {
    try{
      infobd.connbd.close();
    }catch(Exception ex1){
    }
      throw new OIExcepcion("Error al cerrar la base de datos.",ex);
    }
    catch(OIExcepcion ex){
    try{
      infobd.connbd.close();
    }catch(Exception ex1){
    }
      throw new OIExcepcion("Error al cerrar los objetos de información.",ex);
    }
  }
  protected OIComputadora getCompu(){return compu;}
  public OIEstado_tablas getEstado_tablas(){return estado_tablas;}
  public OIEstado_instancia getEstadoInstancia(){return estado_instancia;}
  public OIConexion getConex(){return infobd;}
  public String getRaiztdderive(){return raiz_tdderive;}
  public void setRaiztdderive(String setRaiztdderive){
    raiz_tdderive=setRaiztdderive;
  }
  /**
   * @return El nombre de la clase encargada de realizar
   * las labores de administración de la base de datos.
   */
  public String getBdControlador() {
    return infobd.bd_controlador;
  }

  /**
   * @return El password para entrar a administrar la base de datos.
   */
  public String getBdPassword() {
    return infobd.bd_password;
  }

  /**
   * @return El URL de la base de datos.
   */
  public String getBdUrl() {
    return infobd.bd_url;
  }

  /**
   * @return El nombre del usuario que administra la
   * base de datos.
   */
  public String getBdUsuario() {
    return infobd.bd_usuario;
  }

  /**
   * @return El nombre del archivo que contiene los
   * pesos y los umbrales.
   */
  public String getPesosUmbralesUrl() {
    return infobd.pesos_umbrales_globales_url;
  }

  /**
   * @return El nombre del archivo que contiene
   * las aplicaciones del sistema y sus detalles.
   */
  public String getProgramasUrl() {
    return infobd.programas_url;
  }

  /**
   * @return El archivo guión de destrucción de la base
   * de datos.
   */
  public String getScriptFinbdUrl() {
    return infobd.script_finbd_url;
  }

  /**
   * @return El archivo guión de la creación de la base
   * de datos.
   */
  public String getScriptInibdUrl() {
    return infobd.script_inibd_url;
  }

  /**
   * @return El nombre del archiov que contiene las ubicaciones
   * de las computadoras del sistema.
   */
  public String getUbicacionesUrl() {
    return infobd.ubicaciones_url;
  }

  /**
   * Cambia el nombre del objeto encargado de administrar
   * la base de datos.
   * @param string El controlador a asignar.
   */
  public void setBdControlador(String string) {
    infobd.bd_controlador = string;
  }

  /**
   * Asigna la constraseña del usuario administrador.
   * <li>Esta operación no cambia la constraseña del sistema para el usuario
   * administrador.</li>
   * @param string El password del administrador.
   */
  public void setBdPssword(String string) {
    infobd.bd_password = string;
  }

  /**
   * Asigna el URL de la base de datos.
   * @param string La ruta de la base de datos.
   */
  public void setBdUrl(String string) {
    infobd.bd_url = string;
  }

  /**
   * Cambia el usuario de la base de datos.
   * @param string El nombre del usuario.
   */
  public void setBdUsuario(String string) {
    infobd.bd_usuario = string;
  }

  /**
   * Asigna el nombre del archivo que contiene los
   * pesos y umbrales globales del sistema.
   * @param string El archivo con estos datos.
   */
  public void setPesosUmbralesGlobalesUrl(String string) {
    infobd.pesos_umbrales_globales_url = string;
  }
  /**
   * Asigna el nombre del archivo que contiene los
   * pesos y umbrales del sistema ajustados localmente.
   * @param string El archivo con estos datos.
   */
  public void setPesosUmbralesLocalesUrl(String string) {
    infobd.pesos_umbrales_locales_url = string;
  }  

  /**
   * Asigna el nombre del archivo que contiene los
   * programas locales del sistema.
   * @param string
   */
  public void setProgramasUrl(String string) {
    infobd.programas_url = string;
  }

  /**
   * Asigna el nombre del archivo con el guión de destrucción
   * de la base de datos del sistema.
   * @param string Nombre del guión.
   */
  public void setScriptFinbdUrl(String string) {
    infobd.script_finbd_url = string;
  }

  /**
   * Asigna el nombre del archivo con el guión de construcción
   * de la base de datos del sistema.
   * @param string Nombre del guión.
   */
  public void setScriptInibdUrl(String string) {
    infobd.script_inibd_url = string;
  }

  /**
   * Asigna el nombre del archivo que contiene la ubicación global
   * de las computadoras del sistema, así como sus enlaces.
   * @param string
   */
  public void setUbicacionesUrl(String string) {
    infobd.ubicaciones_url = string;
  }
  public static void restoreBackup(String sFileBackup,String sFileCache) throws SQLException {

//      if (Trace.TRACE) {
//          Trace.trace("not closed last time!");
//      }

      if (!(new File(sFileBackup)).exists()) {

          // the backup don't exists because it was never made or is empty
          // the cache file must be deleted in this case
          (new File(sFileCache)).delete();

          return;
      }

      try {
          long time = 0;

//          if (Trace.TRACE) {
//              time = System.currentTimeMillis();
//          }

          InflaterInputStream f =
              new InflaterInputStream(new FileInputStream(sFileBackup),
                                      new Inflater());
          FileOutputStream cache = new FileOutputStream(sFileCache);
          byte             b[]   = new byte[COPY_BLOCK_SIZE];

          while (true) {
              int l = f.read(b, 0, COPY_BLOCK_SIZE);

              if (l == -1) {
                  break;
              }

              cache.write(b, 0, l);
          }

          cache.close();
          f.close();

//          if (Trace.TRACE) {
//              Trace.trace(time - System.currentTimeMillis());
//          }
      } catch (Exception e) {
//          throw Trace.error(Trace.FILE_IO_ERROR, sFileBackup);
      }
  }


/**
 * Asigna el estado de la instancia.
 * @param estado_instancia0 El estado de la intancia a asignar.
 */
public void setEstadoInstancia(OIEstado_instancia estado_instancia0) {
	this.estado_instancia = estado_instancia0;
}
/**
 * @param compu0 El nombre de la computadora.
 */
protected void setCompu(OIComputadora compu0) {
	this.compu = compu0;
}
/**
 * Asigna el estado de las tablas.
 * @param estado_tablas0 El nuevo estado de las tablas.
 */
public void setEstadoTablas(OIEstado_tablas estado_tablas0) {
	this.estado_tablas = estado_tablas0;
}
/**
 * Asigna el nombre de la clase que negocia el balance de carga
 * entre las computadoras de un dominio.
 * @param clase_balance0 Nombre de la clase que negocia el 
 * balance de carga.
 */
public void setClaseBalance(String clase_balance0) {
  clase_balance=clase_balance0;
}
/**
 * @return La clase de la instancia responsable del balance.
 */
public String getClaseBalance() {
  return clase_balance;
}
/**
 * Usado para obligar la exclusividad en la asignación de 
 * identificaciones de procesos (operación considerada como área crítica).
 */
String objeto_bloqueo_tareas = "objeto_bloqueo_tareas";
/**
 * Usado para obligar la exclusividad en la asignación de identificaciones de 
 * procesos (operación considerada como área crítica).
 */
String objeto_bloqueo_idprocesos = "objeto_bloqueo_procesos";
}