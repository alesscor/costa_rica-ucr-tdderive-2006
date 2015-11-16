package orgainfo;
import java.sql.*;
import java.io.*;
/**
 * Contiene información importante para ubicar la base de datos.
 */
public class OIConexion {
    /**
     * Controlador de la base de datos.
     */
    String bd_controlador="org.hsqldb.jdbcDriver";
    /**
     * URL de la base de datos.
     */
    String bd_url="jdbc:hsqldb:info/adminbd";
    /**
     * URL del script "creador" de la base de datos.
     */
    String script_inibd_url="create_db.txt";
    /**
     * URL del script "destructor" de la base de datos.
     */
    String script_finbd_url="destroy_db.txt";
    /**
     * Nombre del usuario de la base de datos. Se supone que el
     * usuario es único.
     */
    String bd_usuario="sa";
    /**
     * Password del usuario de la base de datos. Se supone que el usuario
     * es único.
     */
    String bd_password="";
    /**
     * Referencia de una conexión independiente a la base de datos
     * presente en la JVM.
     */
    Connection connbd;
    /**
     * Información de los metadatos de la base de datos.
     */
    DatabaseMetaData metadatos;
    /**
     * Archivo de ubicaciones de los nodos, con información sobre
     * nombres y sus direcciones IP.
     */
    public String ubicaciones_url =".ubicaciones.xml";
    /**
     * Archivo de ubicación de programas, con información
     * sobre nombres, rutas, etc.
     */
    String programas_url =".programas.xml";
		/**
		 * Archivo de ubicación de pesos y umbrales globales.
		 */
		String pesos_umbrales_globales_url =".pesos_umbrales.xml";
    /**
     * Archivo de ubicación de pesos y umbrales ajustados localmente.
     */
    String pesos_umbrales_locales_url=".pesos_umbrales_locales.xml";
    public static final int BDSINCONSTRUIR=1;
    public static final int BDPREPARADA=2;
    public static final int BDINCOMPLETA=3;
    public static final int BDCORRUPTA=4;
    /**
     * Construye la base de datos.
     * @throws OIExcepcion Si hubo error en la construcción.
     */
    public void dbConstruct() throws OIExcepcion{
      // pone rollback como punto de inicio
      this.dbRollback();
      // construye la base de datos
      this.script_exec_url(this.script_inibd_url);
      // marca base de datos construida en forma exitosa

      // marca el estado de tdderive persistentemente
      this.dbCommit();

    }
    /**
     * Destruye la base de datos.
     * @throws OIExcepcion Si hubo error en la destrucción.
     */
    public void dbDestroy() throws OIExcepcion{
      // pone rollback como punto de inicio
      this.dbRollback();
      // destruye la base de datos
      this.script_exec_url(this.script_finbd_url);
      this.dbCommit();
    }
    public void dbCommit() throws OIExcepcion{
      try {
        this.connbd.commit();
      }
      catch (SQLException ex) {
        throw new OIExcepcion("No se pudo consumar la transacción.",ex);
      }
    }
    public void dbRollback() throws OIExcepcion{
      try {
        this.connbd.rollback();
      }
      catch (SQLException ex) {
        throw new OIExcepcion("No se pudo revertir la transacción.",ex);
      }
    }
    /**
     * Ejecuta un archivo de instrucciones para realizar operaciones sobre
     * la base de datos.
     * Cualquier línea que empiece con "#" es un considerada como comentario.
     * @param url Dirección del archivo a interpretar.
     * @throws OIExcepcion Si ocurren errores.
     */
    public void script_exec_url(String url) throws OIExcepcion{
      StringBuffer instrucciones=new StringBuffer();
      String linea="";
      BufferedReader lector=null;
      int nlinea=0;
      try {
        lector = new BufferedReader(new FileReader(url));
      }
      catch (FileNotFoundException ex) {
        throw new OIExcepcion("No se encuentra el archivo de instrucciones.",ex);
      }
      try {
        while ( (linea = lector.readLine()) != null) {
          nlinea++;
          linea = linea.trim();
          // saca los comentarios
            if (linea != null && linea != "" && linea.length()>0) {
              try{
                if (linea.charAt(0) != '#') {
                  instrucciones.append(linea);
                }
              }catch(Exception ex){
                System.out.println("Error en línea nº "+nlinea+": \""+linea+"\""  );
                ex.printStackTrace();
              }
              // ojo, si hay comentarios, va un \n para llevar el conteo de las líneas
              instrucciones.append("\n");
            }
        }
      }
      catch (IOException ex) {
        throw new OIExcepcion("Error en la lectura del archivo de "+
                                 "instrucciones, línea "+nlinea +".",ex);
      }
      // se supone que el script está listo para ser ejecutado
      script_exec(instrucciones.toString());
    }
    /**
     * Interpreta las instrucciones dadas.
     * @param instrucciones Instrucciones a ser ejecutadas.
     * @throws OIExcepcion Si hay error en una instrucción.
     */
    public void script_exec(String instrucciones) throws OIExcepcion{
      String[] lineas=null;
      Statement instruccion=null;
      int i;
      try {
        instruccion = this.connbd.createStatement();
      }
      catch (SQLException ex) {
        throw new OIExcepcion("No se puede cargar la lista de instrucciones.",
                                 ex);
      }
      instrucciones.replaceAll("\n"," ");
      lineas=instrucciones.split(";");
      if(instruccion!=null){
        for (i = 0; i < lineas.length; i++) {
          try {
            if(lineas[i].trim()!=""){
              System.out.println("A ejecutar:\n \""+lineas[i].trim()+"\"");
              instruccion.execute(lineas[i].trim());
              System.out.println("Echo.");
            }
            // todo bien
          }
          catch (SQLException ex) {
            throw new OIExcepcion("Error en la "+ i+"-ésima instrucción.",ex);
          }
        } // for
      }
    }
//    /**
//     * @return
//     */
//    String getBdControlador() {
//      return bd_controlador;
//    }
//
//    /**
//     * @return
//     */
//    String getBdPassword() {
//      return bd_password;
//    }
//
//    /**
//     * @return
//     */
//    String getBdUrl() {
//      return bd_url;
//    }
//
//    /**
//     * @return
//     */
//    String getBdUsuario() {
//      return bd_usuario;
//    }
//
//    /**
//     * @return
//     */
//    String getPesosUmbralesUrl() {
//      return pesos_umbrales_url;
//    }
//
//    /**
//     * @return
//     */
//    String getProgramasUrl() {
//      return programas_url;
//    }
//
//    /**
//     * @return
//     */
//    String getScriptFinbdUrl() {
//      return script_finbd_url;
//    }
//
//    /**
//     * @return
//     */
//    String getScriptInibdUrl() {
//      return script_inibd_url;
//    }
//
//    /**
//     * @return
//     */
//    String getUbicacionesUrl() {
//      return ubicaciones_url;
//    }
//
//    /**
//     * @param string
//     */
//    void setBdControlador(String string) {
//      bd_controlador = string;
//    }
//
//    /**
//     * @param string
//     */
//    void setBdPssword(String string) {
//      bd_password = string;
//    }
//
//    /**
//     * @param string
//     */
//    void setBdUrl(String string) {
//      bd_url = string;
//    }
//
//    /**
//     * @param string
//     */
//    void setBdUsuario(String string) {
//      bd_usuario = string;
//    }
//
//    /**
//     * @param string
//     */
//    void setPesosUmbralesUrl(String string) {
//      pesos_umbrales_url = string;
//    }
//
//    /**
//     * @param string
//     */
//    void setProgramasUrl(String string) {
//      programas_url = string;
//    }
//
//    /**
//     * @param string
//     */
//    void setScriptFinbdUrl(String string) {
//      script_finbd_url = string;
//    }
//
//    /**
//     * @param string
//     */
//    void setScriptInibdUrl(String string) {
//      script_inibd_url = string;
//    }
//
//    /**
//     * @param string
//     */
//    void setUbicacionesUrl(String string) {
//      ubicaciones_url = string;
//    }

}