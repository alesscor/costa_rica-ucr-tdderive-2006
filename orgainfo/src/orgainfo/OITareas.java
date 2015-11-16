package orgainfo;
import java.sql.*;
import tdutils.*;
import java.util.*;
/**
 * Lleva la información necesaria sobre una tarea de <tt>tdderive</tt>. 
 */
public class OITareas extends OIPersistente implements OIActualiza{
  protected String id_tarea;
  protected String directorio;
  protected String rutas_entrada;
  protected String rutas_salida;
  protected boolean si_coordina;  
  protected String estado_tarea;
  protected long hora_solicitud;
  protected long hora_inicio;
  protected long hora_fin;
  protected String alias;
  protected String parametros;
	protected int tiempo_ensistema;
  protected OITareas(OIDescriptor info0,boolean sivacio){
    super(info0,sivacio);
  }
  boolean isLista(){
    return false;
  }
  public void open(){
  }
  protected void openRS(ResultSet rs) throws SQLException{
    id_tarea=rs.getString("id_tarea");
    directorio=rs.getString("directorio");
    rutas_entrada=rs.getString("rutas_entrada");
    rutas_salida=rs.getString("rutas_salida");
    si_coordina=rs.getBoolean("si_coordina");    
    estado_tarea=rs.getString("estado_tarea");
    hora_solicitud=rs.getLong("hora_solicitud");
    hora_inicio=rs.getLong("hora_inicio");
    hora_fin=rs.getLong("hora_fin");
    alias=rs.getString("alias");
    parametros=rs.getString("parametros");
    tiempo_ensistema=rs.getInt("tiempo_ensistema");
  }
  public void write() throws OIExcepcion{
    int res=0;
    //
    // actualiza
    //
    res = doUpdateSQL("UPDATE Tareas set " +
                     "id_tarea=" + tdutils.getQ(id_tarea) +
                     ",directorio=" + tdutils.getQ(directorio) +
                     ",rutas_entrada=" + tdutils.getQ(rutas_entrada) +
                     ",rutas_salida=" + tdutils.getQ(rutas_salida) +
                     ",si_coordina=" + si_coordina +                     
                     ",estado_tarea=" + tdutils.getQ(estado_tarea) +
                     ",hora_solicitud=" + hora_solicitud +
                     ",hora_inicio=" + hora_inicio +
                     ",hora_fin=" + hora_fin +
                     ",alias=" + tdutils.getQ(alias) +
                     ",parametros=" + tdutils.getQ(parametros) +
										 ",tiempo_ensistema=" + tiempo_ensistema +
                     " WHERE id_tarea=" + tdutils.getQ(id_tarea));
    //
    // si actualización no sirve, hace un insert
    //
    if (res==0) {
      res = doUpdateSQL("insert into Tareas (id_tarea,directorio," +
                       "rutas_entrada,rutas_salida,si_coordina,"+
                       "estado_tarea,hora_solicitud,"+
                       "hora_inicio,hora_fin,alias,parametros,"+
											 "tiempo_ensistema) values(" +
                       tdutils.getQ(id_tarea) +
                       "," + tdutils.getQ(directorio) +
                       "," + tdutils.getQ(rutas_entrada) +
                       "," + tdutils.getQ(rutas_salida) +
                       "," + si_coordina +
                       "," + tdutils.getQ(estado_tarea) +
                       "," + hora_solicitud +
                       "," + hora_inicio +
                       "," + hora_fin +
                       "," + tdutils.getQ(alias) +
                       "," + tdutils.getQ(parametros) +
											 "," + tiempo_ensistema +
                       ")");
    }
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo actualizar tabla 'Tareas'.");
    }
  } // fin de write tareas
  /**
   * Borra una tarea, con su información asociada.
   * @throws OIExcepcion
   */
  public void delete() throws OIExcepcion{
    int res=0;
    res = doUpdateSQL("DELETE tareas " +
                     " WHERE id_tarea=" + tdutils.getQ(id_tarea));
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo borrar de la tabla 'Tareas'.");
    }
    res = doUpdateSQL("DELETE archivos " +
                     " WHERE id_tarea=" + tdutils.getQ(id_tarea));
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo borrar de la tabla 'OIArchivos'.");
    }
  }
  public void close(){
  }
  /**
   * Crea una nueva tarea y le asigna sus valores.
   */
  public void creaUltimo() throws OIExcepcion{
    ResultSet res=null;
    Statement instruc=null;
    String likecompu="",codigoG="";
    long maxG=0;
    likecompu = this.info.compu.nombre + "%";
    try {
      instruc = info.infobd.connbd.createStatement();
    }catch (SQLException ex) {
      throw new OIExcepcion("Error preparando nueva tarea.",ex);
    }
    synchronized(info.objeto_bloqueo_tareas){
      try {
        res = instruc.executeQuery(
            "SELECT MAX RIGHT(T.id_tarea,10) as C1 " +
            "FROM Tareas as T " +
            "WHERE T.id_tarea LIKE " + tdutils.getQ(likecompu));
      }catch (SQLException ex) {
        throw new OIExcepcion("Error registrando nueva tarea.",ex);
      }
      try {
        if (res.next()) {
          maxG = res.getLong("C1");
        }
      }catch (SQLException ex) {
        throw new OIExcepcion("Error registrando nueva tarea.",ex);
      }
      maxG++;
      codigoG = tdutils.padL(String.valueOf(maxG),'0',10);
      codigoG = this.info.compu.nombre + codigoG;
      this.id_tarea=codigoG;
      // escribe en la base de datos los datos de la tarea
      this.write();
    }
  }
  /**
   * Divide un archivo en varios subarchivos.
   * @param cArchivo Archivo a dividir.
   * @return Lista de subarchivos derivados y comprimidos.
   * @throws OACTExcepcion Si hay error.
   */
  public TreeMap comprime(String cArchivo) throws OIExcepcion{
    return null;
  }
  public void descomprime(TreeMap mArchivos) throws OIExcepcion{
    String cNombre="";
    Iterator itr;
    OIArchivos archI=null;
    itr=mArchivos.values().iterator();
    while(itr.hasNext()){
      archI=(OIArchivos)itr.next();
      if(cNombre.compareToIgnoreCase(archI.nombre)==0){
        continue;
      }
      cNombre=archI.nombre;
      Zip.unzipFile(cNombre,this.directorio);
    }
  }
}
