package orgainfo;
import java.sql.*;
import tdutils.*;

/**
 * Guarda información sobre los procesos ejecutantes de
 * <tt>tdderive</tt>.
 */
public abstract class OIProcesos extends OIPersistente{
  /**
   * nombre_nodo + proceso
   */
  String id_parcial;
  String id_padre;
  String id_grupo;
  long hora_solicitud;
  long hora_inicio;
  long hora_fin;
  OIProcesos _ppadre;
  OIProcesos(OIDescriptor info0,boolean sivacio,OIProcesos ppadre0){
    super(info0,sivacio);
    this._ppadre=ppadre0;
  }
  OIProcesos(OIDescriptor info0,boolean sivacio){
    super(info0,sivacio);
    this._ppadre=null;
  }
  /**
   * Crea un proceso, poniendo a cada uno de sus valores en blanco o en nulo.
   * @throws OIExcepcion Si hay un error.
   */
  final public void creaVacio() throws OIExcepcion{
    id_parcial="";
    id_padre="";
    id_grupo="";
    hora_solicitud=0;
    hora_inicio=0;
    hora_fin=0;
    _ppadre=null;
  }
  /**
   * Crea un proceso, tomando los mayores valores locales, y considerando
   * si el padre del proceso está especificado o no.
   * <li>Entra a un bloque crítico para obtener la identificación
   * del nuevo proceso y registrarlo en la base de datos.</li>
   * @throws OIExcepcion Si hay error.
   */
  final public void creaUltimo()throws OIExcepcion{
    java.sql.Statement instruc;
    java.sql.ResultSet res1=null,res2=null,res3=null,res4=null;
    long maxSol=0,maxCoo=0,maxPro=0;
    long max=0,maxG=0;
    String codigo="",codigoG="";
    String likecompu="";
    //
    // asigna horas iniciales
    //
    this.hora_inicio = System.currentTimeMillis();
    this.hora_solicitud = hora_inicio;
    //
    // determina los códigos
    //
    likecompu = this.info.compu.nombre + "%";
    try {
      instruc = this.getConnbd().createStatement();
    }
    catch (SQLException ex1) {
      return;
    }
    //
    // accesa al bloque crítico de procesos
    //
    synchronized(this.info.objeto_bloqueo_idprocesos){
      try {
        res1 = instruc.executeQuery(
            "SELECT MAX RIGHT(S.id_parcial,10) as C1 " +
            "FROM solicitantes as S " +
            "WHERE S.id_parcial LIKE " + tdutils.getQ(likecompu));
      }
      catch (SQLException ex) {
        ex.printStackTrace();
      }
      try {
        instruc = this.getConnbd().createStatement();
      }
      catch (SQLException ex1) {
        return;
      }
      try {
        res2 = instruc.executeQuery(
            "SELECT MAX RIGHT(C.id_parcial,10) as C1 " +
            "FROM controladores as C " +
            "WHERE C.id_parcial LIKE " + tdutils.getQ(likecompu));
      }
      catch (SQLException ex) {
        ex.printStackTrace();
      }
      try {
        instruc = this.getConnbd().createStatement();
      }
      catch (SQLException ex1) {
        return;
      }
      try {
        res3 = instruc.executeQuery(
            "SELECT MAX RIGHT(P.id_parcial,10) as C1 " +
            "FROM envolturas as P " +
            "WHERE P.id_parcial LIKE " + tdutils.getQ(likecompu));
      }
      catch (SQLException ex) {
        ex.printStackTrace();
      }
      try {
        instruc = this.getConnbd().createStatement();
      }
      catch (SQLException ex1) {
        return;
      }
      try {
        res4 = instruc.executeQuery(
            "SELECT MAX RIGHT(T.id_tarea,10) as C2 " +
            "FROM Tareas as T " +
            "WHERE T.id_tarea LIKE " + tdutils.getQ(likecompu));
      }
      catch (SQLException ex) {
        ex.printStackTrace();
      }
      try {
        if (res1 == null || !res1.next()) {
          // es primero en Solicitantes
          if (res1 != null) {
            res1.close();
          }
        }
        else {
          // no es primero
          try {
            maxSol = Long.parseLong(res1.getString("C1"));
            res1.close();
          }
          catch (Exception ex) {
          }
        }
        if (res2 == null || !res2.next()) {
          // es primero en Controladores
          if (res2 != null) {
            res2.close();
          }
        }
        else {
          // no es primero en Controladores
          try {
            maxCoo = Long.parseLong(res2.getString("C1"));
            res2.close();
          }
          catch (Exception ex) {
          }
        }
        if (res3 == null || !res3.next()) {
          // es primero en OIEnvolturas
          if (res3 != null) {
            res3.close();
          }
        }
        else {
          // no es primero en OIEnvolturas
          try {
            maxPro = Long.parseLong(res3.getString("C1"));
            res3.close();
          }
          catch (Exception ex) {
          }
        }
        // pone grupo si no se indica el padre
        if (this._ppadre == null) {
          if (res4 == null || !res4.next()) {
            // es primero en Tareas
            if (res4 != null) {
              res4.close();
            }
          }
          else {
            // no es primero en Tareas
            try {
              maxG = Long.parseLong(res4.getString("C2"));
              res4.close();
            }
            catch (Exception ex) {
            }
          }
        }
      }
      catch (SQLException ex) {
        ex.printStackTrace();
      }
      maxSol++;
      maxCoo++;
      maxPro++;
      max = Math.max(maxSol, Math.max(maxCoo, maxPro)); // ahora sí
      maxG++; // este valor ya estaba listo
      //
      // prepara el código
      //
      codigo=tdutils.padL(String.valueOf(max),'0',10);
      codigo = this.info.compu.nombre + codigo;
      //
      // prepara el grupo
      //
      codigoG = tdutils.padL(String.valueOf(maxG),'0',10);
      codigoG = this.info.compu.nombre + codigoG;
      //
      // lístos los códigos
      //
      this.id_parcial = codigo;
      this.id_grupo = codigoG;
      this.id_padre = codigo; // su valor definitivo se asignará después
      if (this._ppadre != null && _ppadre.id_parcial != null && _ppadre.id_grupo != null
          && _ppadre.id_parcial != "" && _ppadre.id_grupo != "") {
        this.id_padre = _ppadre.id_parcial;
        this.id_grupo = _ppadre.id_grupo;
      }
      this.write();
    } // fin de bloque crítico, desbloquea objeto
  } // fin de crea último
  final protected void openRS(ResultSet rs) throws SQLException{
    id_parcial=rs.getString("id_parcial");
    id_padre=rs.getString("id_padre");
    id_grupo=rs.getString("id_grupo");
    hora_solicitud=rs.getLong("hora_solicitud");
    hora_inicio=rs.getLong("hora_inicio");
    hora_fin=rs.getLong("hora_fin");
    if(this._ppadre!=null){
      _ppadre.id_parcial=rs.getString("pid_parcial");
      _ppadre.id_padre=rs.getString("pid_padre");
      _ppadre.id_grupo=rs.getString("pid_grupo");
    }
    openRS0(rs);
  }
  protected abstract void openRS0(ResultSet rs) throws SQLException;
  /**
   * A sobredefinir.
   * @throws OIExcepcion
   */
  protected void preWrite() throws OIExcepcion {
  }
  public void write() throws OIExcepcion {
    String tabla="";
    int res=0;
    //
    // hace un prewrite
    //
    preWrite();
    //
    // determina la tabla
    //
    if(this instanceof OISolicitantes){
      tabla="Solicitantes";
    }
    if(this instanceof OIControladores){
      tabla="Controladores";
    }
    if(this instanceof OIEnvolturas){
      tabla="Envolturas";
    }
    //
    // escribe en la tabla
    //
    //
    // primero intenta actualizar
    //
    res = doUpdateSQL("UPDATE "+tabla+" set " +
                     "id_parcial=" + tdutils.getQ(id_parcial) +
                     ",id_padre=" + tdutils.getQ(id_padre) +
                     ",id_grupo=" + tdutils.getQ(id_grupo) +
                     ",hora_solicitud=" + hora_solicitud +
                     ",hora_inicio=" + hora_inicio +
                     ",hora_fin=" + hora_fin +
                     ((_ppadre==null)?"":",pid_parcial=" + tdutils.getQ(_ppadre.id_parcial)) +
                     ((_ppadre==null)?"":",pid_padre=" + tdutils.getQ(_ppadre.id_padre)) +
                     ((_ppadre==null)?"":",pid_grupo=" + tdutils.getQ(_ppadre.id_grupo)) +
                     updatetext()+
                     " WHERE id_parcial=" + tdutils.getQ(id_parcial));
    //
    // si actualización no sirve, hace un insert
    //
    if (res==0) {
      res = doUpdateSQL("insert into "+tabla+" (id_parcial,id_padre," +
                       "id_grupo,hora_solicitud,hora_inicio,hora_fin,"+
                       "pid_parcial,pid_padre,pid_grupo"+
                       inserttext0()+
                       ") values(" +
                       tdutils.getQ(id_parcial) +
                       "," + tdutils.getQ(id_padre) +
                       "," + tdutils.getQ(id_grupo) +
                       "," + hora_solicitud +
                       "," + hora_inicio +
                       "," + hora_fin +
                       "," + ((_ppadre==null)?tdutils.getQ(""): tdutils.getQ(_ppadre.id_parcial)) +
                       "," + ((_ppadre==null)?tdutils.getQ(""): tdutils.getQ(_ppadre.id_padre)) +
                       "," + ((_ppadre==null)?tdutils.getQ(""): tdutils.getQ(_ppadre.id_grupo)) +
                       inserttext1()+
                       ")");
    }
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo actualizar lista de programas.");
    }
    //
    // actualiza campos específicos
    //

  }
  abstract String inserttext0() throws OIExcepcion;
  abstract String inserttext1() throws OIExcepcion;
  abstract String updatetext() throws OIExcepcion;
  public final void delete() throws OIExcepcion{
    String tabla="";
    int res=0;
    //
    // determina la tabla
    //
    if(this instanceof OISolicitantes){
      tabla="Solicitantes";
    }
    if(this instanceof OIControladores){
      tabla="Controladores";
    }
    if(this instanceof OIEnvolturas){
      tabla="Envolturas";
    }
    res = doUpdateSQL("DELETE "+tabla+" " +
                     " WHERE id_parcial=" + tdutils.getQ(id_parcial));
    //
    // si actualización no sirve, hace un insert
    //
    if (res==0) {
      throw new OIExcepcion(
          "No se pudo borrar de la tabla '"+tabla+"'.");
    }

  }
  final public String getIdParcial(){return id_parcial;}
  final public String getIdPadre(){return id_padre;}
  final public String getIdGrupo(){return id_grupo;}
  final public long getHoraSolicitud(){return hora_solicitud;}
  final public long getHoraInicio(){return hora_inicio;}
  final public long getHoraFin(){return hora_fin;}
  final public OIProcesos getppadre(){return _ppadre;}

  final public void setIdParcial(String setIdParcial){id_parcial=setIdParcial;}
  final public void setIdPadre(String setIdPadre){id_padre=setIdPadre;}
  final public void setIdGrupo(String setIdGrupo){id_grupo=setIdGrupo;}
  final public void setHoraSolicitud(long setHoraSolicitud){hora_solicitud=setHoraSolicitud;}
  final public void setHoraInicio(long setHoraInicio){hora_inicio=setHoraInicio;}
  final public void setHoraFin(long setHoraFin){hora_fin=setHoraFin;}
  final public void setppadre(OIProcesos setppadre){_ppadre=setppadre;}

}
