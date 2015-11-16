package admin;
import java.util.*;
import java.io.*;

import mens.MENSException;
import mens.MENSMensaje;
import orgainfo.*;
import tdutils.*;
import java.sql.*;
import org.w3c.dom.Node;
/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Anida clases para la persistencia de datos sobre la coordinación
 * de tareas del usuario en <tt>tdderive</tt>.
 */
abstract class PERSCoordinacion {
  /**
   * <p>Título: admin</p>
   * <p>Descripción: </p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Organzación: ECCI - UCR</p>
   * <p>@author Alessandro</p>
   * <p>@version 1.0</p>
   * <b>Retornos</b>
   * <!-- http://127.0.0.1:55070/help/nftopic/jar:
   * file:/C:/JBuilder/doc/jdk_docs.jar!/java/api/index.html -->
   */
  static class Retornos extends OIRetornos implements ADMINAPPIRetornos {
    /**
     * @param descriptor
     */
    public Retornos(ADMINGLOInfo descriptor) {
      super(descriptor,true);
    }
    /**
     * 
     */
    public Retornos() {
      super();      
    }
    /**
     * @param info0
     * @param siVacio
     */
    public Retornos(OIDescriptor info0, boolean siVacio) {
      super(info0, siVacio);
    }
    /**
     * Devuelve el estado del retorno.
     * @return El estado del retorno.
     */
    public String getEstadoRetorno() {
      return estado_retorno;
    }
    /**
     * 
     * @see admin.ADMINAPPIRetornos#getIdGrupo()
     */
    public String getIdGrupo() {
      return id_grupo;
    }
    /**
     * 
     * @see admin.ADMINAPPIRetornos#getIdPadre()
     */
    public String getIdPadre() {
      return id_padre;
    }
    /**
     * 
     * @see admin.ADMINAPPIRetornos#getIdParcial()
     */
    public String getIdParcial() {
      return id_parcial;
    }
    public String getIdRetorno() {
      return id_retorno;
    }
    /**
     * 
     * @see admin.ADMINAPPIRetornos#getTipoRetorno()
     */
    public String getTipoRetorno() {
      return tipo_retorno;
    }
    /**
     * 
     * @see admin.ADMINAPPIRetornos#getValorRetorno()
     */
    public String getValorRetorno() {
      return valor_retorno;
    }
    /**
     * 
     * @param string
     */
    public void setEstadoRetorno(String string) {
      estado_retorno= string;
    }

    /**
     * @param string
     */
    public void setIdGrupo(String string) {
      id_grupo= string;
    }

    /**
     * @param string
     */
    public void setIdPadre(String string) {
      id_padre= string;
    }

    /**
     * @param string
     */
    public void setIdParcial(String string) {
      id_parcial= string;
    }
    public void setIdRetorno(String string) {
      id_retorno= string;
    }

    /**
     * @param string
     */
    public void setTipoRetorno(String string) {
      tipo_retorno= string;
    }

    /**
     * @param string
     */
    public void setValorRetorno(String string) {
      valor_retorno= string;
    }
    static void writeMap(OIDescriptor info0,Map map)
        throws OIExcepcion{
      java.util.Iterator itr=null;
      Retornos retorI;
      itr=map.values().iterator();
      while(itr.hasNext()){
        retorI=(Retornos)itr.next();
        retorI.info=info0;
        retorI.write();
      }
    }
  }
  /**
   *
   * <p>Title: Administración de recursos</p>
   * <p>Description: Administrador de recursos para tdderive</p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: ECCI - UCR</p>
   * @author Alessandro Cordero
   * @version 1.0
   */
  /***
   * Solicitantes: clase de coordinación que registra a todo
   * aquello que hace una consulta a tdderive (orden para cumplir con
   * cierta tarea).
   */
  static class Solicitantes
      extends OISolicitantes implements ADMINAPPISolicitantes{
    protected Map _retornos;
    Solicitantes() {
        _inicio();
    }
    Solicitantes(ADMINGLOInfo info0, boolean sivacio) {
      super(info0, sivacio);
      _inicio();
    }
    private void _inicio(){
        _retornos=java.util.Collections.synchronizedMap(
            new TreeMap(String.CASE_INSENSITIVE_ORDER));
    }
    /**
     * Asigna una tarea a un proceso solicitante.
     * <li>Si la tarea es nueva la registra en el sistema.</li>
     * @param tarea0 Tarea a asociar.
     * @throws OIExcepcion Cuando hay error.
     */
    void setTarea(Tareas tarea0) throws OIExcepcion{
      Sol_Tar solicitante_tarea=null;
      if(tarea0.getIdTarea()==null||tarea0.getIdTarea()=="" ){
        /**
         * debe crearse una nueva tarea asociada al solicitante
         */
        try {
          tarea0.creaUltimo();
        }catch (OIExcepcion ex) {
          throw new OIExcepcion("No se pudo asignar una tarea al "+
                                   "solicitante.",ex);
        }
      }else{
        /**
         * la tarea ya existía, la escribe a la bd.
         */
        this.setIdGrupo(tarea0.getIdTarea());
        tarea0.write();
      }
      solicitante_tarea= new Sol_Tar(tarea0,this);
      solicitante_tarea.write();
      info.getConex().dbCommit();
    }

    public String getTipoSol() {
      return tipo_sol;
    }

    public String getRetorno() {
      return retorno;
    }

    public String getDesdeNombre() {
      return desde_nombre;
    }

    public long getDesdePuerto() {
      return desde_puerto;
    }

    public boolean getSiEntregado() {
      return si_entregado;
    }
    public String getEstadoSolicitante() {
      return estado_solicitante;
    }
    void setTipoSol(String setTipoSol) {
      tipo_sol = setTipoSol;
    }

    void setRetorno(String setRetorno) {
      retorno = setRetorno;
    }

    void setDesdeNombre(String setDesdeNombre) {
      desde_nombre = setDesdeNombre;
    }

    void setDesdePuerto(long setDesdePuerto) {
      desde_puerto = setDesdePuerto;
    }

    void setSiEntregado(boolean setSiEntregado) {
      si_entregado = setSiEntregado;
    }

    void setEstadoSolicitante(String setEstadoSolicitante) {
      estado_solicitante=setEstadoSolicitante;
    }
    public void write() throws ADMINGLOExcepcion{
      try {
    		super.write();
        if(_retornos!=null && _retornos.size()>0){
        	Retornos.writeMap(this.info, _retornos);
        }
    	} catch (OIExcepcion e) {
        throw new ADMINGLOExcepcion("Error al escribir un solicitante.",e);
    	}
    }
    /**
     * Asigna retornos a un solicitante.
     * @param retornos La lista de retornos a relacionar con el solicitante.
     * @throws ADMINGLOExcepcion Si hay error al relacionar los retornos
     * con sus solicitantes.
     */
    void setRetornos(Map retornos) throws ADMINGLOExcepcion{
      Iterator itr;
      Retornos retornoI=null;
      int i=0;
      if(retornos==null){
        _retornos=null;
        return;
      }
      itr=retornos.values().iterator();
      while(itr.hasNext()){
        i++;
        try{
          retornoI=(Retornos)itr.next();
        }catch(Exception e){
          throw new ADMINGLOExcepcion("Error al cargar lista de retornos.",e);
        }
        retornoI.setIdParcial(this.getIdParcial());
        retornoI.setIdPadre(this.getIdPadre());
        retornoI.setIdGrupo(this.getIdGrupo());
        retornoI.setIdRetorno(tdutils.padL(""+i, '0', 10));
        _retornos.put(retornoI.getIdRetorno(), retornoI);
      }
      
    }
    void openSolicitante(ResultSet rs)throws ADMINGLOExcepcion{
      try {
        this.openRS(rs);
      }catch (SQLException ex) {
        throw new ADMINGLOExcepcion("Error leyendo solicitante.",ex);
      }
    }

  }

  
  /**
   *
   * <p>Title: Administración de recursos</p>
   * <p>Description: Administrador de recursos para tdderive</p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: ECCI - UCR</p>
   * @author Alessandro Cordero
   * @version 1.0
   */
  /***
   * Coordinadores: clase de coordinación que registra los procesos
   * que coordinan aplicaciones (grupos de procesos orientados a
   * resolver los objetivos de una tarea dada.)
   */

  static class Controladores
      extends OIControladores {
    Tareas _tarea=null;
    Map _externos=null;
    Controladores(ADMINGLOInfo info0) {
      super(info0, true);
      _inicio();
    }
    Controladores(ADMINGLOInfo info0, boolean sivacio,
            Solicitantes solicitante) {
      super(info0,sivacio,solicitante);
      _inicio();
    }
    private void _inicio(){
      _externos=java.util.Collections.synchronizedMap(
          new TreeMap(String.CASE_INSENSITIVE_ORDER));      
    }
    final void addContRemoto(Conts_Externos contremoto){
      contremoto.setIdTarea(this.id_tarea);
      _externos.put(contremoto.getIdContRemoto(),contremoto);
    }
    String getIdTarea() {
      return id_tarea;
    }

    boolean getSiActivo() {
      return si_activo;
    }

    Tareas getTarea() {
      return (Tareas) _tarea;
    }
    Sub_trabajos getSubtrabajo(String idSubtrabajo){
      Sub_trabajos subtrabajo0=null;
      Map subtrabajos=_tarea.getSubtrabajos();
      subtrabajo0=(Sub_trabajos)subtrabajos.get(idSubtrabajo);
      return subtrabajo0;
    }
    void setIdTarea(String setIdTarea) {
      id_tarea = setIdTarea;
    }

    void setSiActivo(boolean setSiActivo) {
      si_activo = setSiActivo;
    }

    void setTarea(Tareas setTarea) {
      _tarea = setTarea;
      if(_tarea!=null){
        this.id_tarea=setTarea.getIdTarea();
      }
    }
    public void write() throws OIExcepcion{
      super.write();
      if(_externos!=null && _externos.size()>0){
        Conts_Externos.writeMap(this.info,_externos);
      }
    }
    void openControlador(ResultSet rs)throws ADMINGLOExcepcion{
      try {
        this.openRS(rs);
      }catch (SQLException ex) {
        throw new ADMINGLOExcepcion("Error leyendo controlador.",ex);
      }
    }
    
  }

  //////////////////////////////////////////////////////////////////////
  /**
   *
   * <p>Title: Administración de recursos</p>
   * <p>Description: Administrador de recursos para tdderive</p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: ECCI - UCR</p>
   * @author Alessandro Cordero
   * @version 1.0
   * Tareas: clase de coordinación que registra los detalles de una
   * tarea solicitada.
   */
  static class Tareas
      extends OITareas implements ADMINAPPITareas {
    Programas _programa=null;
    protected Map _archivos;
    protected Map _sub_trabajos;
    String archivoResultado=null;
    boolean si_terminado;
    Resultados _resultado=null;
    Tareas(ADMINGLOInfo info0, boolean sivacio) {
      super(info0, sivacio);
      _archivos=java.util.Collections.synchronizedMap(
          new TreeMap(String.CASE_INSENSITIVE_ORDER));
      _sub_trabajos=java.util.Collections.synchronizedMap(
          new TreeMap(String.CASE_INSENSITIVE_ORDER));      
    }
    /**
     * 
     * @see admin.ADMINAPPITareas#getTareaDir()
     */
    public String getTareaDir(){
      return info.getRaiztdderive()+
          "/"+DIR_TRABAJOS+"/"+DIR_PREFIJOTAREA+id_tarea;
    }
    /**
     * 
     * @see admin.ADMINAPPITareas#getResultadosDir()
     */
    public String getResultadosDir(){
      return getTareaDir()+"/"+DIR_RESULTADOS;
    }
    /**
     * 
     * @see admin.ADMINAPPITareas#getComprimidosDir()
     */
    public String getComprimidosDir(){
      return getTareaDir()+"/"+DIR_COMPRIMIDOS;
    }
    /**
     * 
     * @see admin.ADMINAPPITareas#getBloquesDir()
     */
    public String getBloquesDir(){
      return getTareaDir()+"/"+DIR_BLOQUES;
    }
    public void close(){
      if(_archivos!=null){
        _archivos.clear();
      }      
    }
    /**
     * Le asigna a la tarea su lista de archivos.
     * @param archivos0 La lista de archivos a asignar.
     */
    public final void setArchivos(Map archivos0){
      Iterator itr=null;
      Archivos archI;
      itr=archivos0.values().iterator();
      while(itr.hasNext()){
        archI=(Archivos)itr.next();
        archI.setIdTarea(this.id_tarea);
        archI.setTarea(this);
        _archivos.put(archI.getNombre()/*+":"+archI.getBloque()*/,archI);
      }
      itr=null;
    }
    public final void setSubtrabajos(Map subtrabajos0){
      Iterator itr=null;
      Sub_trabajos subI;
      itr=subtrabajos0.values().iterator();
      while(itr.hasNext()){
        subI=(Sub_trabajos)itr.next();
        subI.setIdTarea(this.id_tarea);
        subI.setTarea(this);
        _sub_trabajos.put(subI.getIdSubtrabajo(),subI);
      }
      itr=null;
    }
    void setResultado(Resultados resultado0){
      this._resultado=resultado0;
      resultado0.setTarea(this);
      resultado0.setSubtrabajo(null);
    }
    Resultados getResultado(){
      return _resultado;
    }
    /**
     * Le agrega a la tarea un archivo.
     * @param archivo0 El archivo a agregar.
     */
    final void addArchivo(Archivos archivo0){
      archivo0.setIdTarea(this.id_tarea);
      archivo0.setTarea(this);            
      _archivos.put(archivo0.getNombre()/*+":"+archivo0.getBloque()*/,archivo0);
    }
    final void addSubtrabajo(Sub_trabajos subtrabajo0){
      subtrabajo0.setIdTarea(this.id_tarea);
      subtrabajo0.setTarea(this);
      if(subtrabajo0.getIdSubtrabajo()==null || subtrabajo0.getIdSubtrabajo()==""){
        subtrabajo0.setIdSubtrabajo(tdutils.padL(""+(_sub_trabajos.size()+1), 
            '0', 10));
      }
      subtrabajo0.setDirectorio(this.getTareaDir()+"/"+
            subtrabajo0.getIdSubtrabajo());
      _sub_trabajos.put(subtrabajo0.getIdSubtrabajo(),subtrabajo0);
    }    
    /**
     * Verifica si hay directorio de trabajo para la tarea.
     * Si no lo hay, lo crea.
     */
    public void createDirs()throws ADMINGLOExcepcion{
      int i=0;
      boolean bSiNoExiste=false;
      String[] nombres=null;      
      File dir;
      if(id_tarea==null||id_tarea==""){
        throw new ADMINGLOExcepcion("La tarea necesita una identificación.");
      }
      nombres=new String[]{this.getComprimidosDir(),this.getResultadosDir(),
                           this.getBloquesDir()};
      while(i<nombres.length){
        dir=new File(nombres[i]);
        bSiNoExiste=!dir.exists();
        if(bSiNoExiste){
          // crea el o los directorios
          if(!dir.mkdirs()){
            throw new ADMINGLOExcepcion("No se pudo crear el directorio '"+
                                     nombres[i]+"'.");
            }
        }else{
          // el directorio ya existía.
        }
        i++;
      }
      return;
    }
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    public void write()throws ADMINGLOExcepcion{
      this.directorio=this.getTareaDir();
      try{
        super.write();
      }catch(OIExcepcion ex){
        throw new ADMINGLOExcepcion("Error en la escritura de una tarea.",ex);
      }
      /**
       * dependiendo del estado de la tarea,
       * se guardan los archivos
       */
      if(this.estado_tarea.compareToIgnoreCase(
        PERSCoordinacion.Tareas.TAREA_INICIO)==0){
        // primera vez, hay que guardar los archivos.
        Archivos.writeMap(this.info,_archivos,this.directorio);
      }
      if((_sub_trabajos!=null) && (_sub_trabajos.size()>0)){
          Sub_trabajos.writeMap(this.info,_sub_trabajos);
      }
      try {
        this.info.getConex().dbCommit();
      } catch (OIExcepcion e) {
      }
    }
    /**
     * 
     * @see admin.ADMINAPPITareas#getIdTarea()
     */
    public String getIdTarea() {
      return id_tarea;
    }

    /**
     * @deprecated
     * @see admin.ADMINAPPITareas#getDirectorio()
     */
    public String getDirectorio() {
      return directorio;
    }

    /**
     * 
     * @see admin.ADMINAPPITareas#getRutasEntrada()
     */
    /**
     * 
     */
    public String getRutasEntrada() {
      return rutas_entrada;
    }

    /**
     * 
     * @see admin.ADMINAPPITareas#getRutasSalida()
     */
    public String getRutasSalida() {
      return rutas_salida;
    }

    /**
     * 
     * @see admin.ADMINAPPITareas#getSiCoordina()
     */
    public boolean getSiCoordina() {
      return si_coordina;
    }

    /**
     * 
     * @see admin.ADMINAPPITareas#getSiTerminado()
     */
    public boolean getSiTerminado() {
      return si_terminado;
    }

    /**
     * 
     * @see admin.ADMINAPPITareas#getEstadoTarea()
     */
    public String getEstadoTarea() {
      return estado_tarea;
    }

    /**
     * 
     * @see admin.ADMINAPPITareas#getHoraSolicitud()
     */
    public long getHoraSolicitud() {
      return hora_solicitud;
    }

    /**
     * 
     * @see admin.ADMINAPPITareas#getHoraInicio()
     */
    public long getHoraInicio() {
      return hora_inicio;
    }

    /**
     * 
     * @see admin.ADMINAPPITareas#getHoraFin()
     */
    public long getHoraFin() {
      return hora_fin;
    }

  /**
   * 
   * @see admin.ADMINAPPITareas#getAlias()
   */
    public String getAlias() {
      return alias;
    }

    /**
     * 
     * @see admin.ADMINAPPITareas#getParametros()
     */
    public String getParametros() {
      return parametros;
    }
    /**
     * 
     * @see admin.ADMINAPPITareas#getParametrosArr()
     */
    public String[] getParametrosArr() {
      if(parametros!=null){
        return parametros.split(ADMINAPPIniciador.APP_SEPARADORARGUMENTOS+"");
      }else{
        return null;
      }
    }
    

    /**
     * 
     * @see admin.ADMINAPPITareas#getPrograma()
     */
    public ADMINAPPIProgramas getPrograma() {
      return (Programas) _programa;
    }

    /**
     * 
     * @see admin.ADMINAPPITareas#getArchivos()
     */
    public Map getArchivos() {
      return _archivos;
    }
    public Map getSubtrabajos() {
      return _sub_trabajos;
    }
    /**
     * 
     * @see admin.ADMINAPPITareas#getTiempoEnSistema()
     */
    public int getTiempoEnSistema(){
      return tiempo_ensistema;
    }

    void setIdTarea(String setIdTarea) {
      id_tarea = setIdTarea;
    }

    void setRutasEntrada(String setRutasEntrada) {
      rutas_entrada = setRutasEntrada;
    }

    void setRutasSalida(String setRutasSalida) {
      rutas_salida = setRutasSalida;
    }

    void setSiCoordina(boolean setSiCoordina) {
      si_coordina = setSiCoordina;
    }

    void setSiTerminado(boolean setSiTerminado) {
      si_terminado = setSiTerminado;
    }

    void setEstadoTarea(String setEstadoTarea) {
      estado_tarea = setEstadoTarea;
    }

    void setHoraSolicitud(long setHoraSolicitud) {
      hora_solicitud = setHoraSolicitud;
    }

    void setHoraInicio(long setHoraInicio) {
      hora_inicio = setHoraInicio;
    }

    void setHoraFin(long setHoraFin) {
      hora_fin = setHoraFin;
    }

    void setAlias(String setAlias) {
      alias = setAlias;
    }

    void setParametros(String setParametros) {
      parametros = setParametros;
    }

    void setPrograma(Programas setPrograma) {
      _programa = setPrograma;
    }


    void setTiempoEnSistema(int setTiempoEnSistema){
      tiempo_ensistema=setTiempoEnSistema;
    }
    void revisaArchivos(Map mpListos, Map mpPresentes,Map mpAusentes){
      String sql="";
    }
    /**
     * Carga los archivos que corresponden a la tarea según un criterio dado.
     * <li>El criterio más frecuente a utilizar es el estado del archivo.</li>
     * @param criterio
     * @param mpArchivos Mapa a cargar con los archivos de la tarea que cumplen
     * con el criterio.
     * @throws ADMINExcepcion Si hay un error.
     */
    void loadArchivos(String criterio,Map mpArchivos) throws ADMINGLOExcepcion{
      String sql="SELECT * FROM archivos "+
                 "WHERE id_tarea="+tdutils.getQ(id_tarea)+
                 " AND (id_subtrabajo IS NULL OR id_subtrabajo='' OR id_subtrabajo='null')" +
                 " AND ("+criterio+")";
      try{
        ResultSet rs=this.getRSSQL(sql);
        Archivos archI=null;
        mpArchivos.clear();
        while (rs.next()) {
          archI=new Archivos();
          archI.openArchivo(rs);
          mpArchivos.put(archI.getNombre(),archI.getNombre());
        }
        rs.close();
      }
      catch(SQLException ex){
        throw new ADMINGLOExcepcion(ex.getCause());
      }
      catch(ADMINGLOExcepcion ex){
        throw new ADMINGLOExcepcion(ex.getCause());
      }
      catch(OIExcepcion ex){
        throw new ADMINGLOExcepcion(ex.getCause());
      }
    }
    void loadArchivosListos(Map mpArchivos) throws ADMINGLOExcepcion{
      loadArchivos("estado_archivo="+tdutils.getQ(Archivos.ARCHIVO_LISTO),
      mpArchivos);
    }
    void loadArchivosPresentes(Map mpArchivos) throws ADMINGLOExcepcion{
      loadArchivos("estado_archivo="+tdutils.getQ(Archivos.ARCHIVO_PRESENTE),
      mpArchivos);
    }
    /**
     * Carga información sobre la aplicación a ser ejecutada
     * con la tarea.
     * @throws ADMINGLOExcepcion Si se presenta un error.
     */
    void loadPrograma()throws ADMINGLOExcepcion{
      if(_programa!=null){
        return;
      }
      if(alias!=null&&alias!=""){
        String sql="SELECT * FROM programas "+
        "WHERE alias="+tdutils.getQ(alias);
        try{
          ResultSet rs=Tareas.getRSSQL(info, sql);
          if(rs.next()){
            _programa=new Programas((ADMINGLOInfo)info,true);
            _programa.openPrograma(rs);            
          }
          rs.close();
        }
        catch(SQLException ex){
            throw new ADMINGLOExcepcion(ex);
        }
        catch(ADMINGLOExcepcion ex){
        	throw new ADMINGLOExcepcion(ex.getCause());
        }
        catch(OIExcepcion ex){
        	throw new ADMINGLOExcepcion(ex.getCause());
        }        
      }
    }
    void loadArchivosAusentes(Map mpArchivos) throws ADMINGLOExcepcion{
      loadArchivos("(estado_archivo<>"+tdutils.getQ(Archivos.ARCHIVO_LISTO)+
                   ") AND (estado_archivo<>"+
                   tdutils.getQ(Archivos.ARCHIVO_PRESENTE)+")",mpArchivos);
    }
    protected final int revisaConsistencia(Sub_trabajos st){
      int res=0;
      java.sql.ResultSet rs=null;
      try {
        rs=this.getRSSQL(
            "SELECT id_tarea FROM Tareas " +
            "WHERE id_tarea="+tdutils.getQ(id_tarea));
        if(rs.next()){
          res=2;
        }
        rs.close();
        if((res==2) && (st!=null)){
          rs=this.getRSSQL(
              "SELECT id_subtrabajo FROM Sub_trabajos " +
              "WHERE id_tarea="+tdutils.getQ(id_tarea)+ " "+
              "AND id_subtrabajo="+tdutils.getQ(st.getIdSubtrabajo()));
          if(rs.next()){
            res=res+1;
          }
          rs.close();
        }
      } catch (OIExcepcion e) {
        e.printStackTrace();
      } catch (java.sql.SQLException e) {
        e.printStackTrace();
      }
      return res;
    }
    /**
     * Obtiene el nombre del nodo originador del subtrabajo.
     * <li>Importante es ver orgainfo.OITareas.creaUltimo() (quién sabe por qué,
     * es un comentario viejo)</li>
     * @return El nombre del nodo originador del subtrabajo.
     */
    public String getNodoCreador(){
      String res="errorennombrenodo";
      if(this.id_tarea.length()>10){
        res=this.id_tarea.substring(0,this.id_tarea.length()-10);
      }
      return res;
    }
    public final void setArchivoResultado(String nombreArchivo){
      this.archivoResultado=nombreArchivo;
    }
    public final String getArchivoResultado(){
      return this.archivoResultado;
    }
    void openTarea(ResultSet rs)throws ADMINGLOExcepcion{
      try {
        this.openRS(rs);
      }catch (SQLException ex) {
        throw new ADMINGLOExcepcion("Error leyendo tarea.",ex);
      }
    }
    /**
     * Reasigna dentro del mapa de subtrabajos las identificaciones
     * de los que corresponden.
     *
     */
    final void reconstruyeListaSubtrabajos(){
      Sub_trabajos subtrabajo=null;
      Collection respaldo=new ArrayList();
      Iterator itr=null;
      synchronized(this._sub_trabajos){
        respaldo.addAll(this._sub_trabajos.values());
        this._sub_trabajos.clear();
        itr=respaldo.iterator();
        while(itr.hasNext()){
          subtrabajo=(Sub_trabajos)itr.next();
          this._sub_trabajos.put(subtrabajo.getIdSubtrabajo(),subtrabajo);
        }
      }
      respaldo=null;
      subtrabajo=null;
      itr=null;
    }
    public final String toString(){
      return this.getIdTarea();
    }
    
  }

  /**
   *
   * <p>Title: Administración de recursos</p>
   * <p>Description: Administrador de recursos para tdderive</p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: ECCI - UCR</p>
   * @author Alessandro Cordero
   * @version 1.0
   */
  /***
   * Sub_trabajos: indica el estado de una sub tarea (proción de una
   * tarea).
   */
static class Sub_trabajos
      extends OISub_trabajos implements ADMINAPPISub_trabajos {
  /**
   * Último controlador remoto que ha indicado que
   * está procesando activamente al subtrabajo.
   */
    String _cont_remoto="";
    Tareas _tarea=null;
    Conts_Externos _cont_externo=null;
    protected Map _archivos=null;
    Resultados _resultado=null;
    void _inicio(String id_subtrabajo0,Tareas tarea){
      if(id_subtrabajo0!="" && id_subtrabajo0!=null){
        this.id_subtrabajo=id_subtrabajo0;
      }
      tarea.addSubtrabajo(this);
      setCarga(0);
      setProgreso(0.0);
      setEstadoSubtrabajo(
          PERSCoordinacion.Sub_trabajos.SUBTRA_INICIO);      
      _archivos=java.util.Collections.synchronizedMap(
          new TreeMap(String.CASE_INSENSITIVE_ORDER));      
    }
    Sub_trabajos(String id_subtrabajo0,Tareas tarea) {
      super(tarea.getDescriptor(), true);
      _inicio(id_subtrabajo0,tarea);
    }

    Sub_trabajos(Tareas tarea) {
      super(tarea.getDescriptor(), true);
      _inicio("",tarea);
    }
    public String getIdTarea() {
      return id_tarea;
    }

    public String getIdSubtrabajo() {
      return id_subtrabajo;
    }

    public String getSubtrabajoDir() {
      return directorio;
    }
    public File getDirSubtrabajoDir()throws ADMINGLOExcepcion{
      File dir=null;
      try {
        dir = new File(directorio);
        if(!dir.isDirectory()){
          // mal, debe darse error
          throw new ADMINGLOExcepcion("\"" +directorio+
                                 "\" no es nombre de directorio");
        }
      }catch (Exception ex) {
        throw new ADMINGLOExcepcion("\"" +directorio+"\" no se encuentra");
      }
      return dir;
    }    

    public String getRutasEntrada() {
      return rutas_entrada;
    }

    public String getRutasSalida() {
      return this.getResultadosDir();
    }

    public double getCarga() {
      return carga;
    }
    public String getComando() {
      return comando;
    }    

    public double getProgreso() {
      return progreso;
    }

    public String getEstadoSubtrabajo() {
      return estado_subtrabajo;
    }

    public ADMINAPPITareas getTarea() {
      return (Tareas) _tarea;
    }
    public long getHoraIngreso(){
      return this.hora_ingreso;
    }

    void setIdTarea(String setIdTarea) {
      id_tarea = setIdTarea;
    }

    void setIdSubtrabajo(String setIdSubtrabajo) {
      id_subtrabajo = setIdSubtrabajo;
    }

    public void setDirectorio(String setDirectorio) {
      directorio = setDirectorio;
    }

    public void setRutasEntrada(String setRutasEntrada) {
      rutas_entrada = setRutasEntrada;
    }

    public void setRutasSalida(String setRutasSalida) {
    }

    public void setCarga(double setCarga) {
      carga = setCarga;
    }

    void setProgreso(double setProgreso) {
      progreso = setProgreso;
    }
    void setHoraIngreso(long hora){
      this.hora_ingreso=hora;
    }
    public void setEstadoSubtrabajo(String setEstadoSubtrabajo) {
      estado_subtrabajo = setEstadoSubtrabajo;
    }
    /**
     * 
     * @see admin.ADMINAPPISub_trabajos#setComando(java.lang.String)
     */
    public void setComando(String com){
      comando=com;
    }
    /**
     * Asigna un objeto de tarea.
     * @param setTarea Tarea a la que pertenece el subtrabajo.
     */
    void setTarea(Tareas setTarea) {
      _tarea = setTarea;
    }
    void setResultado(Resultados resultado0){
      this._resultado=resultado0;
      resultado0.setSubtrabajo(this);
    }
    Resultados getResultado(){
      return _resultado;
    }    
    /**
     * 
     * @see admin.ADMINAPPISub_trabajos#addArchivo()
     */
    public ADMINAPPIArchivos addArchivo(String nombreArchivo){
     Archivos archivo=null;
     archivo=new Archivos((ADMINGLOInfo)info);
     archivo.setNombre(nombreArchivo);
     archivo.setTarea(this._tarea);
     archivo.setIdSubtrabajo(this.id_subtrabajo);
     archivo.setIdTarea(this.id_tarea);
     // ojo, se identifica en esta lista volátil de acuerdo
     // al orden de entrada.
     // _archivos.put(tdutils.padL(""+_archivos.size(),'0', 10) , archivo);
     _archivos.put(nombreArchivo, archivo);
     return archivo;
    }
//    /**
//     * 
//     * @see admin.ADMINAPPISub_trabajos#ubicaArchivosDirTrabajo()
//     */
//    public void ubicaArchivosDirTrabajo(String sDirOriginal){
//      String sDirectorioSubtrabajo="";
//      Iterator itr=null;
//      File fOrigen=null,fDestino=null;
//      Archivos archI=null;
//      if(this._archivos.size()==0){
//        return;
//      }
//      // revisa directorio de origen
//      if(sDirOriginal==null||sDirOriginal==""){
//        sDirOriginal=_tarea.getTareaDir();
//      }
//      // prepara directorio de destino
//      sDirectorioSubtrabajo=_tarea.getTareaDir()+"/"+this.id_subtrabajo;
//      this.setDirectorio(sDirectorioSubtrabajo);
//      fDestino=new File(sDirectorioSubtrabajo);
//      fDestino.mkdirs();
//      // ubica los archivos del subtrabajo desde el lugar dado
//      // hasta su nuevo lugar.
//      itr=_archivos.values().iterator();
//      while(itr.hasNext()){
//        archI=(Archivos)itr.next();
//        archI.setEstadoArchivo(ADMINAPPIArchivos.ARCHIVO_AUSENTE);
//        fOrigen=null;
//        fOrigen=new File(sDirOriginal+"/"+archI.getNombre());
//        if(fOrigen.exists()){
//          // el archivo existe y debe moverse.
//          if(fOrigen.renameTo(new File(fDestino,archI.getNombre()))){
//            // sin problema al mover
//            archI.setEstadoArchivo(ADMINAPPIArchivos.ARCHIVO_LISTO);
//          }else{
//            // problema al mover, queda en estado ARCHIVO_AUSENTE.
//          }
//        }else{
//          // el archivo no existe, queda en estado ARCHIVO_AUSENTE.
//        }
//      } // del while de archivos
//    }
//    /**
//     * 
//     * @see admin.ADMINAPPISub_trabajos#ubicaArchivosDirTrabajo()
//     */
//    public void ubicaArchivosDirTrabajo(){
//      ubicaArchivosDirTrabajo(null);
//    }
    public void write() throws ADMINGLOExcepcion{
      try{
        super.write();
      }catch(OIExcepcion ex){
        throw new ADMINGLOExcepcion("Error en la escritura de un " +
                "subtrabajo.",ex);
      }
      // también se guarda el estado de los archivos
      Archivos.writeMap(this.info,_archivos,this.directorio);
      if(this._cont_externo!=null){
        try {
          this._cont_externo.write();
        } catch (OIExcepcion e) {
          throw new ADMINGLOExcepcion("Error en la escritura de un " +
              "subtrabajo, específicamente un cont_exteno.",e);
        }
      }
    }
    static void writeMap(OIDescriptor info0,Map map)throws ADMINGLOExcepcion{
      Iterator itr=null;
      Sub_trabajos subI=null;
      if(map==null){
        return;
      }
      itr=map.values().iterator();
      while(itr.hasNext()){
        subI=(Sub_trabajos)itr.next();
        subI.info=info0;
        subI.write();
      }
    }
  /**
   * Obtiene la estructura de control externo que
   * está ejecutando al subtrabajo.
   * @return El control externo que está ejecutando al subtrabajo.
   */
  Conts_Externos getControladorExterno(){
    return this._cont_externo;
  }
  /**
   * Asigna la estructura de control externo que está ejecutando
   * al subtrabajo.
   * @param cont_externo El control externo que está ejecutando al subtrabajo.
   */
  void setControladorExternoNoUsar(Conts_Externos cont_externo){
    this._cont_externo=cont_externo;
  }
	/**
	 * @return Returns the _archivos.
	 */
	public Map getArchivos() {
		return _archivos;
	}
  /**
   * Obtiene el mapa de archivos de salida.<br/>
   * Se supone que esto se hace con poca frecuencia,
   * es por eso que no existe una estructura permanente
   * para portar tales archivos antílopes.
   * <li>Se asume que el coordinador pone el valor del directorio
   * de resultados. Este directorio es indicado por la clase unificadora
   * de la aplicación.</li>
   * <li>Se ha asumido que la persistencia de los nombres y 
   * características de estos archivos no es necesaria desde la
   * perspectiva de un agente participante. Lo contrario es desde
   * la perspectiva de un agente coordinador, lo cual se contempla 
   * en la importación de los resultados.</li>
   */
  public Map getArchivosSalida(){
    int i=0;
    Archivos archI;
    File dir=null;
    File[] archivos_salida=null;
    String directoriosalida=this.getRutasSalida();
    Map archivosSalida=java.util.Collections.synchronizedMap(
        new TreeMap(String.CASE_INSENSITIVE_ORDER));
    
    try {
      dir = new File(directoriosalida);
      if(dir.isDirectory()){
        archivos_salida=dir.listFiles();
        for(i=0;i<archivos_salida.length;i++){
          archI=new Archivos();
          archI.setSubTrabajo(this);
          archI.setNombre(archivos_salida[i].getName());
          archI.setDirectorio(archivos_salida[i].getAbsolutePath());
          // falso porque esto es un archivo de salida.
          archI.setSiEntrada(false);
          archivosSalida.put(archI.getNombre(),archI);          
        }
      }else{
        //  mal, debe darse error
      }
    }catch (Exception ex) {
    }
    return archivosSalida;
  }
	/**
	 * Carga los archivos de entrada que corresponden al subtrabajo según un criterio dado.
	 * <li>El criterio más frecuente a utilizar es el estado del archivo.</li>
	 * @param criterio
	 * @param mpArchivos Mapa a cargar con los archivos del subtrabajo que cumplen
	 * con el criterio.
	 * @throws ADMINExcepcion Si hay un error.
	 */
	void loadArchivos(String criterio,Map mpArchivos) throws ADMINGLOExcepcion{
    this.loadArchivosExt(criterio,mpArchivos,true);
	}
  /**
   * Carga los archivos de salida que corresponden al subtrabajo según un criterio dado.
   * <li>El criterio más frecuente a utilizar es el estado del archivo.</li>
   * @param criterio
   * @param mpArchivos Mapa a cargar con los archivos del subtrabajo que cumplen
   * con el criterio.
   * @throws ADMINExcepcion Si hay un error.
   */
  void loadArchivosSalida(String criterio,Map mpArchivos) throws ADMINGLOExcepcion{
    this.loadArchivosExt(criterio,mpArchivos,false);
  }
  /**
   * Carga los archivos que corresponden al subtrabajo según un criterio dado.
   * <li>El criterio más frecuente a utilizar es el estado del archivo.</li>
   * @param criterio
   * @param mpArchivos Mapa a cargar con los archivos del subtrabajo que cumplen
   * con el criterio.
   * @param sientrada Si se consulta por los archivos de entrada.
   * @throws ADMINExcepcion Si hay un error.
   */
  void loadArchivosExt(String criterio,Map mpArchivos,boolean sientrada) throws ADMINGLOExcepcion{
    String subsql;
    String sql;
    if(sientrada){
      subsql="si_entrada='TRUE'";
    }else{
      subsql="si_entrada='FALSE'";
    }
    sql="SELECT * FROM archivos "+
               "WHERE id_tarea="+tdutils.getQ(id_tarea)+
               " AND id_subtrabajo ="+tdutils.getQ(id_subtrabajo)+
               " AND " + subsql +
               " AND ("+criterio+")";
    try{
      ResultSet rs=this.getRSSQL(sql);
      Archivos archI=null;
      mpArchivos.clear();
      while (rs.next()) {
        archI=new Archivos();
        archI.openArchivo(rs);
        mpArchivos.put(archI.getNombre(),archI.getNombre());
      }
      rs.close();
    }
    catch(SQLException ex){
      throw new ADMINGLOExcepcion(ex.getCause());
    }
    catch(ADMINGLOExcepcion ex){
      throw new ADMINGLOExcepcion(ex.getCause());
    }
    catch(OIExcepcion ex){
      throw new ADMINGLOExcepcion(ex.getCause());
    }
  }
	void loadArchivosAusentes(Map mpArchivos) throws ADMINGLOExcepcion{
	  loadArchivos("(estado_archivo<>"+tdutils.getQ(Archivos.ARCHIVO_LISTO)+
	               ") AND (estado_archivo<>"+
	               tdutils.getQ(Archivos.ARCHIVO_PRESENTE)+")",mpArchivos);
	}
	void loadArchivosListos(Map mpArchivos) throws ADMINGLOExcepcion{
	  loadArchivos("estado_archivo="+tdutils.getQ(Archivos.ARCHIVO_LISTO),
	  mpArchivos);
	}
	void loadArchivosPresentes(Map mpArchivos) throws ADMINGLOExcepcion{
	  loadArchivos("estado_archivo="+tdutils.getQ(Archivos.ARCHIVO_PRESENTE),
	  mpArchivos);
	}
/****************************/
  void loadArchivosSalidaAusentes(Map mpArchivos) throws ADMINGLOExcepcion{
    loadArchivosSalida("(estado_archivo<>"+tdutils.getQ(Archivos.ARCHIVO_LISTO)+
                 ") AND (estado_archivo<>"+
                 tdutils.getQ(Archivos.ARCHIVO_PRESENTE)+")",mpArchivos);
  }
  void loadArchivosSalidaListos(Map mpArchivos) throws ADMINGLOExcepcion{
    loadArchivosSalida("estado_archivo="+tdutils.getQ(Archivos.ARCHIVO_LISTO),
    mpArchivos);
  }
  void loadArchivosSalidaPresentes(Map mpArchivos) throws ADMINGLOExcepcion{
    loadArchivosSalida("estado_archivo="+tdutils.getQ(Archivos.ARCHIVO_PRESENTE),
    mpArchivos);
  }
/****************************/
  void revisaArchivos(Map mpListos, Map mpPresentes,Map mpAusentes){
	  String sql="";
	}
  /**
   * Verifica si hay directorio de trabajo y resultados para el subtrabajo.
   * Si no los hay, los crea.
   */
  public void createDirs()throws ADMINGLOExcepcion{
    int i=0;
    boolean bSiNoExiste=false;
    String[] nombres=null;      
    File dir;
    if(id_subtrabajo==null||id_subtrabajo==""||id_tarea==null||id_tarea==""){
      throw new ADMINGLOExcepcion("El directorio necesita una identificación " +
            "y estar asignado a una tarea.");
    }
    nombres=new String[]{this.getSubtrabajoDir(),this.getResultadosDir()};
    while(i<nombres.length){
      dir=new File(nombres[i]);
      bSiNoExiste=!dir.exists();
      if(bSiNoExiste){
        // crea el o los directorios
        if(!dir.mkdirs()){
          throw new ADMINGLOExcepcion("No se pudo crear el directorio '"+
                                   nombres[i]+"'.");
          }
      }else{
        // el directorio ya existía.
      }
      i++;
    }
    return;
  }
  /**
   * @return El directorio de resultados.
   */
  public String getResultadosDir() {	
  	return directorio+"/"+Tareas.DIR_RESULTADOS;
  }
  /**
   * Le asigna al subtrabajo su lista de archivos,
   * sean de entada, sean de salida.
   * @param archivos0 La lista de archivos a asignar.
   */
  public final void setArchivos(Map archivos0){
    Iterator itr=null;
    Archivos archI;
    itr=archivos0.values().iterator();
    while(itr.hasNext()){
      archI=(Archivos)itr.next();
      archI.setIdTarea(this.id_tarea);
      archI.setSubTrabajo(this);
      // archI.setSiEntrada(!this.getSiFin());
      _archivos.put(archI.getNombre()/*+":"+archI.getBloque()*/,archI);
    }
    itr=null;
  }
  /**
   * Indica el directorio donde se ubican los archivos comprimidos.
   * <li>Considera si los archivos son de entrada o de salida.</li>
   * @return El directorio donde están los archivos comprimidos.
   */
  public String getComprimidosDir(){
    String res="";
    if(this.getSiFin()){
      res=this.getResultadosDir()+"/"+Tareas.DIR_COMPRIMIDOS;
    }else{
      res=getSubtrabajoDir()+"/"+Tareas.DIR_COMPRIMIDOS;
    }
    return res;
  }
  /**
   * Indica el directorio donde se ubican los bloques que forman archivos.
   * <li>Considera si los archivos son de entrada o de salida.</li>
   * @return El directorio donde están los bloques de los archivos.
   */
  public String getBloquesDir(){
    String res="";
    if(this.getSiFin()){
      res=this.getResultadosDir()+"/"+Tareas.DIR_BLOQUES;
    }else{
      res=getSubtrabajoDir()+"/"+Tareas.DIR_BLOQUES;
    }
    return res;
  }
  public boolean getSiFin(){
    return si_fin;
  }
  public void setSiFin(boolean siFin){
    si_fin=siFin;
  }
  void openSubtrabajo(ResultSet rs)throws ADMINGLOExcepcion{
    try {
      this.openRS(rs);
    }catch (SQLException ex) {
      throw new ADMINGLOExcepcion("Error leyendo subtrabajo.",ex);
    }
  }
  /**
   * Actualiza la información del controlador remoto
   * que supuestamente está ejecutando al subtrabajo.
   * <li>Si el subtrabajo es local, entonces recolecta y 
   * guarda información del controlador remoto. En este
   * caso se trata de un mensaje de sigueVivo</li>
   * @param contremoto El controlador remoto que procesa al subtrabajo.
   */
  void setContRemoto(String contremoto){
    this._cont_remoto=contremoto;
    if(this.siLocal()){
      if(this._cont_externo==null){
        this._cont_externo=new PERSCoordinacion.Conts_Externos(this,contremoto);
        this._cont_externo.setIdContLocal(this.getNodoCreador());
      }else{
        this._cont_externo.setIdContRemoto(contremoto);
      }
      this._cont_externo.setHoraEjecucionRemota(System.currentTimeMillis());
    }
  }
  /**
   * Último controlador remoto que ha sido reportado
   * como aquél que está procesando el subtrabajo.
   * @return
   */
  public String getContRemoto(){
    return this._cont_remoto;
  }
  /**
   * Obtiene el nombre del nodo originador del subtrabajo.
   * @return El nombre del nodo originador del subtrabajo.
   */
  public String getNodoCreador(){
    String resultado="";
    if(this._tarea!=null){
      resultado=this._tarea.getNodoCreador();
    }
    return resultado;
  }
  /**
   * Indica si el subtrabajo ha sido originado por el nodo local.
   * En otras palabras, indica si el coordinador del subtrabajo
   * es el nodo actual.
   * @return Verdadero si el nodo actual es el coordinador del subtrabajo.
   */
  public boolean siLocal(){
    boolean resultado=false;
    if(ADMINGLOInfo.class.isInstance(this.getDescriptor())){
      resultado=((ADMINGLOInfo)this.getDescriptor()).getComputadora().
      getNombre().compareTo(this.getNodoCreador())==0;  
    }
    return resultado;
  }
  public final String toString(){
    return this.getIdTarea() +"(" + this.getIdSubtrabajo()+")";
  }

}

  //////////////////////////////////////////////////////////////////////
  /**
   *
   * <p>Title: Administración de recursos</p>
   * <p>Description: Administrador de recursos para tdderive</p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: ECCI - UCR</p>
   * @author Alessandro Cordero
   * @version 1.0
   */
  /***
   * Envolturas: clase de coordinación. Representa el estado de un proceso
   * que ha sido o está siendo responsable de parte del trabajo de una
   * tarea (subtrabajo).
   */
  static class Envolturas
      extends OIEnvolturas {
    Sub_trabajos _subtrabajo=null;
    Programas _programa =null;
    /**
     * Inicia una instancia de envoltura, necesita información
     * sobre el subtrabajo que le corresponde completar.
     * @param subtra El subtrabajo a completar.
     */
    Envolturas(Sub_trabajos subtra) {      
      super(subtra.getDescriptor(),false);
      this._programa=(Programas)subtra.getTarea().getPrograma();
      this._subtrabajo=(Sub_trabajos)subtra;
      this.setIdTarea(subtra.getTarea().getIdTarea());
      this.setIdSubtrabajo(this._subtrabajo.getIdSubtrabajo());
    }

    //////////////////////////////////////////////////////////////////////
    String getIdTarea() {
      return id_tarea;
    }

    String getIdSubtrabajo() {
      return id_subtrabajo;
    }

    String getEstadoControl() {
      return estado_control;
    }

    String getAlias() {
      return alias;
    }

    boolean getSiActual() {
      return si_actual;
    }

    boolean getSiInvitado() {
      return si_invitado;
    }

    boolean getSiRemoto() {
      return si_remoto;
    }

    int getNumeroConfirmaciones() {
      return numero_confirmaciones;
    }

    Sub_trabajos getSubtrabajo() {
      return (Sub_trabajos) _subtrabajo;
    }

    Programas getPrograma() {
      return (Programas) _programa;
    }

    void setIdTarea(String setIdTarea) {
      id_tarea = setIdTarea;
    }

    void setIdSubtrabajo(String setIdSubtrabajo) {
      id_subtrabajo = setIdSubtrabajo;
    }

    void setEstadoControl(String setEstadoControl) {
      estado_control = setEstadoControl;
    }

    void setAlias(String setAlias) {
      alias = setAlias;
    }

    void setSiActual(boolean setSiActual) {
      si_actual = setSiActual;
    }

    void setSiInvitado(boolean setSiInvitado) {
      si_invitado = setSiInvitado;
    }

    void setSiRemoto(boolean setSiRemoto) {
      si_remoto = setSiRemoto;
    }

    void setNumeroConfirmaciones(int setNumeroConfirmaciones) {
      numero_confirmaciones = setNumeroConfirmaciones;
    }

    void setSubtrabajo(Sub_trabajos setSubtrabajo) {
      _subtrabajo = (Sub_trabajos) setSubtrabajo;
    }

    void setPrograma(Programas setPrograma) {
      _programa = (Programas) setPrograma;
    }
  }

  //////////////////////////////////////////////////////////////////////
  /**
   *
   * <p>Title: Administración de recursos</p>
   * <p>Description: Administrador de recursos para tdderive</p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: ECCI - UCR</p>
   * @author Alessandro Cordero
   * @version 1.0
   */
  /***
   * Resultados: clase de coordinación que registra los resultados de una tarea
   * así como los resultados parciales de sus subtrabajos.
   */
  static class Resultados
      extends OIResultados {
    Tareas _tarea=null;
    Sub_trabajos _subtrabajo=null;
    //////////////////////////////////////////////////////////////////////
    Resultados(ADMINGLOInfo info0, boolean sivacio) {
      super(info0, sivacio);
    }
    Resultados(Tareas tarea0) {
      super(tarea0.getDescriptor(), true);
      
    }
    Resultados(Sub_trabajos subtrabajo0) {
      super(subtrabajo0.getDescriptor(), true);
    }    
    //////////////////////////////////////////////////////////////////////
    String getIdTarea(){return id_tarea;}
    String getIdSubtrabajo(){return id_subtrabajo;}
    long getHoraFin(){return hora_fin;}
    String getRutas(){return rutas;}
    boolean getSiCompletado(){return si_completado;}
    Tareas getTarea(){return (Tareas)_tarea;}
    Sub_trabajos getSubtrabajo(){return (Sub_trabajos)_subtrabajo;}

    void setIdTarea(String setIdTarea){id_tarea=setIdTarea;}
    void setIdSubtrabajo(String setIdSubtrabajo){id_subtrabajo=setIdSubtrabajo;}
    void setHoraFin(long setHoraFin){hora_fin=setHoraFin;}
    void setRutas(String setRutas){rutas=setRutas;}
    void setSiCompletado(boolean setSiCompletado){si_completado=setSiCompletado;}
    void setTarea(Tareas setTarea){
      _tarea=setTarea;
      this.setIdTarea(_tarea.getIdTarea());
    }
    void setSubtrabajo(Sub_trabajos setSubtrabajo){
      _subtrabajo=setSubtrabajo;
      this.setIdSubtrabajo(_subtrabajo.getIdSubtrabajo());
      this.setIdTarea(_subtrabajo.getIdTarea());
    }

  }

  /**
   *
   * <p>Title: Administración de recursos</p>
   * <p>Description: Administrador de recursos para tdderive</p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: ECCI - UCR</p>
   * @author Alessandro Cordero
   * @version 1.0
   */
  /***
   * Programas: clase de coordinación, es el inventario de los
   * programas (servicios) que tdderive puede ejecutar (brindar), y
   * por los cuales pueden ser solicitados por clientes de tdderive.
   */
  static class Programas
      extends OIProgramas implements ADMINAPPIProgramas {
    protected final static class LEEProgramas extends MENSMensaje {
      public TreeMap programas;
      public LEEProgramas() {
        programas=new TreeMap();
      }
      public boolean isVacio() {
        return false;
      }
      protected String getXMLContainedElements() {
        String res="";
        LEEPrograma prograI;
        Iterator itr=null;
        // se limpian los enlaces iniciales porque van a ser considerados
        // los enlaces con que quedaron los nodos.
        int i;
        res ="<programas>\n";
        // se toman en cuenta los enlaces de cada nodo
        itr=programas.values().iterator();
        while (itr.hasNext()) {
          prograI = (LEEPrograma) itr.next();
          res += prograI.getXMLElem(null);
        }
        res+="\n</programas>";
        return res;
      }
      protected void setContentFromDoc(Node nodo, int[] parm2, String[] parm3) {
        LEEPrograma prograI;
        Node nodobak;
        nodobak=nodo;
    		nodo=nodo.getOwnerDocument();
        if(nodo.getNodeName().compareToIgnoreCase("programas")!=0){
          nodo = MENSMensaje.getNextElement(nodo, "programas");
        }
        if(nodo!=null){
    			nodo = MENSMensaje.getNextElement(nodo, "programa");
          while((nodo!=null)&&(nodo.getNodeName().compareToIgnoreCase("programa")==0)){
            // debe haber info sobre nodos
            prograI = new LEEPrograma();
            try {
              prograI.setFromXMLNode(nodo);
              programas.put(prograI.progra.alias,prograI);
              prograI = null;
              nodo = MENSMensaje.getNextSiblingElement(nodo, "programa");
            }catch (MENSException ex) {
              prograI=null;
              break;
            }
          }
        }
      }
      protected void toleraXML(int[] parm1, String[] parm2) {
        /**@todo Implement this mens.MENSMensaje abstract method*/
      }
      public final static class LEEPrograma extends mens.MENSMensaje{
        public Programas progra;
        //////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////
        LEEPrograma() {
          progra=new Programas();
          progra.alias="";
          progra.nombre_aplicacion="";
          progra.ruta="";
          progra.clase="";
          progra.divisora="";
          progra.unificadora="";
          progra.tiempo_ensistema=0;
          progra.periodo_confirmacion=0;
          progra.umbral_espera=0;
          progra.si_cambiarcompu=false;
        }
        public boolean isVacio() {
          return progra.alias==null || progra.alias=="";
        }
        protected String getXMLContainedElements() {
          String xml="";
          xml+="<programa>";
            xml+="\n  <alias>"+progra.alias+"</alias>";
            xml+="\n  <nombre_aplicacion>"+progra.nombre_aplicacion+"</nombre_aplicacion>";
            xml+="\n  <ruta>"+progra.ruta+"</ruta>";
            xml+="\n  <clase>"+progra.clase+"</clase>";
            xml+="\n  <divisora>"+progra.divisora+"</divisora>";
            xml+="\n  <unificadora>"+progra.unificadora+"</unificadora>";
            xml+="\n  <tiempo_ensistema>"+progra.tiempo_ensistema+"</tiempo_ensistema>";
            xml+="\n  <periodo_confirmacion>"+progra.periodo_confirmacion+"</periodo_confirmacion>";
            xml+="\n  <umbral_espera>"+progra.umbral_espera+"</umbral_espera>";
            xml+="\n  <si_cambiarcompu>"+progra.si_cambiarcompu+"</si_cambiarcompu>";
          xml+="\n</programa>";
          return xml;
        }
        protected void setContentFromDoc(Node parm1, int[] parm2, String[] parm3) {
          // parm1=this.getDocumento();
          progra.alias=getElementText(parm1,"alias");
          progra.nombre_aplicacion= getElementText(parm1,"nombre_aplicacion");
          progra.ruta= getElementText(parm1,"ruta");
          progra.clase= getElementText(parm1,"clase");
          progra.divisora= getElementText(parm1,"divisora");
          progra.unificadora= getElementText(parm1,"unificadora");
          try {
            progra.tiempo_ensistema = Long.parseLong(getElementText(parm1,
                "tiempo_ensistema"));
          }
          catch (NumberFormatException ex) {
          }
          try {
            progra.periodo_confirmacion = Long.parseLong(getElementText(parm1,
                "periodo_confirmacion"));
          }
          catch (NumberFormatException ex) {
          }
          try {
            progra.umbral_espera = Long.parseLong(getElementText(parm1,
                "umbral_espera"));
          }
          catch (NumberFormatException ex) {
          }
          try {
            progra.si_cambiarcompu = Boolean.getBoolean(getElementText(parm1,
                "si_cambiarcompu"));
          }
          catch (Exception ex) {
          }
    
        }
        protected void toleraXML(int[] parm1, String[] parm2) {
          if(progra.alias!=null && progra.alias!=""){
            parm1[0]=0;
            parm2[0]="Bien en lectura de programa.";
          }else{
            parm1[0]=3;
            parm2[0]="Error en lectura de programa.";
          }
        }
        //////////////////////////////////////////////////////////////////////
      }
    
    }
    //////////////////////////////////////////////////////////////////////
    Programas() {
    }
    Programas(ADMINGLOInfo info0, boolean sivacio) {
      super(info0, sivacio);
    }
    //////////////////////////////////////////////////////////////////////
    public String getAlias() {
      return alias;
    }

    public String getNombreAplicacion() {
      return nombre_aplicacion;
    }

    public String getRuta() {
      return ruta;
    }

    public String getClase() {
      return clase;
    }
    public String getDivisora() {
      return divisora;
    }
    public String getUnificadora() {
      return unificadora;
    }    
        

    public long getTiempoEnSistema() {
      return tiempo_ensistema;
    }

    public long getPeriodoConfirmacion() {
      return periodo_confirmacion;
    }

    public long getUmbralEspera() {
      return umbral_espera;
    }

    public boolean getSiCambiarCompu() {
      return si_cambiarcompu;
    }

    public void setAlias(String setAlias) {
      alias = setAlias;
    }

    public void setNombreAplicacion(String setNombreAplicacion) {
      nombre_aplicacion = setNombreAplicacion;
    }

    public void setRuta(String setRuta) {
      ruta = setRuta;
    }

    public void setClase(String setClase) {
      clase = setClase;
    }
    public void setDivisora(String setDivisora) {
      divisora = setDivisora;
    }
    public void setUnificadora(String setUnificadora) {
      unificadora = setUnificadora;
    }
    void openPrograma(ResultSet rs)throws ADMINGLOExcepcion{
      try {
        this.openRS(rs);
      }catch (SQLException ex) {
        throw new ADMINGLOExcepcion("Error leyendo programa.",ex);
      }
    }    

    public void setTiempoEnSistema(long setTiempoEnSistema) {
      tiempo_ensistema = setTiempoEnSistema;
    }

    public void setPeriodoConfirmacion(long setPeriodoConfirmacion) {
      periodo_confirmacion = setPeriodoConfirmacion;
    }

    public void setUmbralEspera(long setUmbralEspera) {
      umbral_espera = setUmbralEspera;
    }

    public void setSiCambiarCompu(boolean setSiCambiarCompu) {
      si_cambiarcompu = setSiCambiarCompu;
    }
    private static void _openMapXML(String URI,Node nodo,java.util.Map map)
    throws OIExcepcion{
    	LEEProgramas lista=new LEEProgramas();
    	LEEProgramas.LEEPrograma prograI;
    	java.util.Iterator itr;
    	try {
    		if(nodo!=null){
    			lista.setFromXMLNode(nodo);
    		}else{
    			lista.setFromXMLURI(URI);
    		}			
    	}
    	catch (MENSException ex) {
    		throw new OIExcepcion("Error al abrir archivo de programas.",ex);
    	}
    	itr=lista.programas.values().iterator();
    	while(itr.hasNext()){
    		prograI=(LEEProgramas.LEEPrograma)itr.next();
    		map.put(prograI.progra.alias,prograI.progra);
    	}  	
    }
    static void openMap(OIDescriptor info0,java.util.Map map)throws OIExcepcion{
      ResultSet resDB;
      Programas progra;
      //
      // carga programas
      //
      resDB=getRSSQL(info0,"SELECT * from programas");
      try {
        if(resDB==null||!resDB.next()){
          // no hay programas
        }else{
          // sí se tienen programas
          do{
            progra=new Programas((ADMINGLOInfo)info0,false);
            progra.openRS(resDB);
            map.put(progra.alias,progra);
          }while(resDB.next());
          resDB.close();
        }
      }catch (SQLException ex) {
        throw new OIExcepcion("No se tiene acceso a la base de datos.",ex);
      }
    }
    static void openMapXML(Node nodo,java.util.Map map)throws OIExcepcion{
    	_openMapXML(null,nodo,map);
    }
    static void openMapXML(String URI,java.util.Map map)throws OIExcepcion{
    	_openMapXML(URI,null,map);
    }
    static void writeMap(OIDescriptor info0,java.util.Map map)throws OIExcepcion{
      java.util.Iterator itr=null;
      Programas progra;
      int res=0;
      res = doUpdateSQL(info0, "delete from programas");
      itr = map.values().iterator();
      while (itr.hasNext()) {
        progra = (Programas) itr.next();
        progra.setDescriptor(info0);
        progra.write();
      }
    }
  }

  //////////////////////////////////////////////////////////////////////
  /**
   *
   * <p>Title: Administración de recursos</p>
   * <p>Description: Administrador de recursos para tdderive</p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: ECCI - UCR</p>
   * @author Alessandro Cordero
   * @version 1.0
   */
  /***
   * Sol_Tar: clase de coordinación, sirve para detallar relaciones
   * entre solicitantes y tareas, es decir, entre lo que es solicitado
   * por un ente que pide servicio a tdderive.
   */
  static class Sol_Tar
      extends OISol_Tar {
    Tareas _tarea=null;
    Solicitantes _solicitante=null;
    //////////////////////////////////////////////////////////////////////
    Sol_Tar(Tareas tarea0, Solicitantes solicitante0) {
      super(tarea0.getDescriptor());
      _tarea=tarea0;
      _solicitante=solicitante0;
      id_tarea=_tarea.getIdTarea();
      id_parcial=_solicitante.getIdParcial();
      id_padre=_solicitante.getIdPadre();
      id_grupo=_solicitante.getIdGrupo();
      si_iniciador=false;
    }
    //////////////////////////////////////////////////////////////////////

    String getIdPadre() {
      return id_padre;
    }

    String getIdGrupo() {
      return id_grupo;
    }

    boolean getSiIniciador() {
      return si_iniciador;
    }

    Tareas getTarea() {
      return (Tareas) _tarea;
    }

    Solicitantes getSolicitante() {
      return (Solicitantes) _solicitante;
    }

    void setIdTarea(String setIdTarea) {
      id_tarea = setIdTarea;
    }

    void setIdParcial(String setIdParcial) {
      id_parcial = setIdParcial;
    }

    void setIdPadre(String setIdPadre) {
      id_padre = setIdPadre;
    }

    void setIdGrupo(String setIdGrupo) {
      id_grupo = setIdGrupo;
    }

    void setSiIniciador(boolean setSiIniciador) {
      si_iniciador = setSiIniciador;
    }

    void setTarea(Tareas setTarea) {
      _tarea = (Tareas) setTarea;
    }

    void setSolicitante(Solicitantes setSolicitante) {
      _solicitante = setSolicitante;
    }

  }

  //////////////////////////////////////////////////////////////////////
  /**
   *
   * <p>Title: Administración de recursos</p>
   * <p>Description: Administrador de recursos para tdderive</p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: ECCI - UCR</p>
   * @author Alessandro Cordero
   * @version 1.0
   */
  /***
   * Archivos, clase de coordinación que indica el estado y ubicación
   * de los archivos utilizados por los servicios de tdderive en su
   * ejecución, que pueden servir como entrada o salida.
   */
  static class Archivos
      extends OIArchivos implements ADMINAPPIArchivos {
    byte[] _contenido;
    String _directorio;
    Tareas _tarea;
    Sub_trabajos _subtrabajo;
    Archivos() {
      _inicio();
    }

    Archivos(ADMINGLOInfo info0) {
      super(info0);
      _inicio();
    }
    private void _inicio(){
      _contenido=null;
      _directorio="";
    }    
    public void write()throws ADMINGLOExcepcion{
      try{
        super.write();        
      }catch(OIExcepcion ex){
        throw new ADMINGLOExcepcion("Error en la escritura de un archivo.",ex);
      }
      if(this._contenido!=null){
        this.confirmaArchivo();
      }
    }
    static void writeMap(OIDescriptor info0,Map map,String directorio)
        throws ADMINGLOExcepcion{
      java.util.Iterator itr=null;
      Archivos archI;
      itr=map.values().iterator();
      while(itr.hasNext()){
        archI=(Archivos)itr.next();
        archI.info=info0;
        archI._directorio=directorio;
        archI.write();
      }
    }
    /**
     * Escribe un archivo en el sistema, específicamente en el directorio
     * de archivos comprimidos (cuando está comprimido) y en el directorio
     * de trabajo.
     * <2005 Ahora considera a los subtrabajos/>
     * @throws ADMINExcepcion Si hay error al escribir archivo o descomprimir
     * archivo.
     */
    public void confirmaArchivo()throws ADMINGLOExcepcion{
      BufferedOutputStream osCompri = null;
      if(this._contenido==null){
        return;
      }
      /*
       * escribe archivo comprimido (lo forma bloque por bloque)
       */
      try {
        if(this.siSubtrabajo()){
          osCompri = new
          BufferedOutputStream(new FileOutputStream(
              _subtrabajo.getSubtrabajoDir()+"/"+ this.nombre,true));
        }else{
          osCompri = new
          BufferedOutputStream(new FileOutputStream(
            _tarea.getComprimidosDir()+"/"+ this.nombre,true));
        }
        osCompri.write(this._contenido);
        osCompri.flush();
        osCompri.close();
        this.estado_archivo=ADMINAPPIArchivos.ARCHIVO_PRESENTE;
      }catch(IOException ex){
        // problema en el sistema de archivos.
        throw new ADMINGLOExcepcion("El archivo no se puede crear o escribir.");
      }
      /*
       * Acumula el archivo y respalda el bloque.
       */
      if(!this.siSubtrabajo()){
        this.confirmaBloque();
      }      
      this._contenido=null;
      this.write();
      osCompri=null;
    }
    /**
     * Escribe un archivo en el sistema, específicamente en el directorio
     * de archivos comprimidos (cuando está comprimido) y en el directorio
     * de trabajo.
     * <li>Considera si los archivos son de entrada o de salida (es decir, 
     * de resultado).</li>
     * <2005 Ahora considera a los subtrabajos/>
     * @throws ADMINExcepcion Si hay error al escribir archivo o descomprimir
     * archivo.
     */
    private void confirmaBloque()throws ADMINGLOExcepcion{
      BufferedOutputStream osCompri = null;
      if(this._contenido==null){
        return;
      }
      /*
       * escribe archivo comprimido (lo forma bloque por bloque)
       */
      try {
        if(this.siSubtrabajo()){
          osCompri = new
          BufferedOutputStream(new FileOutputStream(
              _subtrabajo.getBloquesDir()+"/"+ this.nombre+"."+this.bloque,false));
        }else{
          osCompri = new
          BufferedOutputStream(new FileOutputStream(
            _tarea.getBloquesDir()+"/"+ this.nombre+"."+this.bloque,false));
        }
        osCompri.write(this._contenido);
        osCompri.flush();
        osCompri.close();
        this.estado_archivo=ADMINAPPIArchivos.ARCHIVO_PRESENTE;
      }catch(IOException ex){
        // problema en el sistema de archivos.
        throw new ADMINGLOExcepcion("El archivo no se puede crear o escribir.");
      }
      /*
       * Listo el archivo comprimido, ahora lo descomprime.
       */
      this._contenido=null;
      this.write();
      osCompri=null;
    }    
    /**
     * 
     * @see admin.ADMINAPPIArchivos#getIdTarea()
     */
    public String getIdTarea() {
      return id_tarea;
    }

    /**
     * 
     * @see admin.ADMINAPPIArchivos#getIdSubtrabajo()
     */
    public String getIdSubtrabajo() {
      return id_subtrabajo;
    }

    /**
     * 
     * @see admin.ADMINAPPIArchivos#getNombre()
     */
    public String getNombre() {
      return nombre;
    }

    /**
     * 
     * @see admin.ADMINAPPIArchivos#getBloque()
     */
    public String getBloque() {
      return bloque;
    }

    /**
     * 
     * @see admin.ADMINAPPIArchivos#getEstadoArchivo()
     */
    public String getEstadoArchivo() {
      return estado_archivo;
    }

    byte[] getContenido() {
      return _contenido;
    }

    /**
     * 
     * @see admin.ADMINAPPIArchivos#getDirectorio()
     */
    public String getDirectorio() {
      return _directorio;
    }

    /**
     * 
     * @see admin.ADMINAPPIArchivos#getSiLocal()
     */
    public boolean getSiLocal() {
      return si_local;
    }
    /**
     * 
     * @see admin.ADMINAPPIArchivos#getSiEntrada()
     */
    public boolean getSiEntrada() {
      return si_entrada;
    }
    /**
     * 
     * @see admin.ADMINAPPIArchivos#getRutaOriginal()
     */
    public String getRutaOriginal() {
      return ruta_original;
    }

    void setIdTarea(String setIdTarea) {
      id_tarea = setIdTarea;
    }

    void setIdSubtrabajo(String setIdSubtrabajo) {
      id_subtrabajo = setIdSubtrabajo;
    }

    public void setNombre(String setNombre) {
      nombre = setNombre;
    }

    void setBloque(String setBloque) {
      bloque = setBloque;
    }

    void setEstadoArchivo(String setEstadoArchivo) {
      estado_archivo = setEstadoArchivo;
    }

    void setContenido(byte[] setContenido) {
      ByteArrayOutputStream contenido=
          new ByteArrayOutputStream(setContenido.length);
      try{
        contenido.write(setContenido);
      }catch(IOException ex){
      }
      _contenido = contenido.toByteArray();
    }
    void setContenido(byte[] setContenido,int length) {
      ByteArrayOutputStream contenido=
          new ByteArrayOutputStream(length);
      contenido.write(setContenido,0,length);
      _contenido = contenido.toByteArray();
    }

    void setDirectorio(String setDirectorio) {
      _directorio = setDirectorio;
    }

    void setSiLocal(boolean setSiLocal) {
      si_local = setSiLocal;
    }
    public void setInfoArchivo(String info0) {
      info_archivo= info0;
    }
    public String getInfoArchivo() {
      return info_archivo;
    }
    void setSiEntrada(boolean setSiEntrada) {
      si_entrada = setSiEntrada;
    }
    public void setRutaOriginal(String setRutaOriginal) {
      ruta_original=setRutaOriginal;
    }
    void openArchivo(ResultSet rs)throws ADMINGLOExcepcion{
      try {
        this.openRS(rs);
      }catch (SQLException ex) {
        throw new ADMINGLOExcepcion("Error leyendo archivo.",ex);
      }
    }

    /**
     * 
     * @see admin.ADMINAPPIArchivos#getTarea()
     */
    public ADMINAPPITareas getTarea() {
      return _tarea;
    }

    /**
     * @param tareas
     */
    public void setTarea(Tareas tareas) {
      _tarea= tareas;
      if(tareas!=null){
        this.id_tarea=tareas.getIdTarea();
      }
    }
    public void setSubTrabajo(Sub_trabajos subtra) {
      _subtrabajo= subtra;
      this.id_subtrabajo=_subtrabajo.getIdSubtrabajo();
      if(subtra.getTarea()!=null){
        this.setTarea(subtra._tarea);
      }
    }
    public final boolean siSubtrabajo(){
      return _subtrabajo!=null;
    }
    public final boolean siSubtrabajoFin(){
      boolean res=false;
      if(_subtrabajo!=null){
        res=_subtrabajo.getSiFin();
      }
      return res;
    }
  }
  /**
   *
   * <p>Title: Administración de recursos</p>
   * <p>Description: Administrador de recursos para tdderive</p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: ECCI - UCR</p>
   * @author Alessandro Cordero
   * @version 1.0
   */
  /***
   * Conts_Externos: clase de coordinación, sirve para detallar relaciones
   * entre el coordinador de un subtrabajo y los controladores externos que 
   * lo están ejecutando.
   */
  static class Conts_Externos
      extends OIConts_Externos{
    Sub_trabajos _subtrabajo=null;
    Conts_Externos(Sub_trabajos subtrabajo, String destino) {
      super(subtrabajo.getDescriptor(),true);
      _subtrabajo=subtrabajo;
      id_subtrabajo=_subtrabajo.getIdSubtrabajo();
      id_tarea=_subtrabajo.getTarea().getIdTarea();
      id_contlocal="";
      id_contremoto=destino;
      estado_contremoto="";
      hora_solicitud=System.currentTimeMillis();
      hora_ejecucionremota=0;
    }
  
    /**
     * @throws OIExcepcion
     * 
     */
    public static void writeMap(OIDescriptor desc,Map mapa) throws OIExcepcion {
      Iterator itr;
      itr=mapa.values().iterator();
      Conts_Externos contE1=null;
      while(itr.hasNext()){
        contE1=(Conts_Externos)itr.next();
        contE1.info=desc;
        contE1.write();
      }
    }

    String getDestino() {
      return id_contremoto;
    }
  
    final String getEstadoContRemoto() {
      return estado_contremoto;
    }

    final String getIdContLocal() {
      return id_contlocal;
    }

    final String getIdSubtrabajo() {
      return id_subtrabajo;
    }

    
    final String getIdContRemoto() {
      return id_contremoto;
    }

    final long getHoraSolicitud() {
      return hora_solicitud;
    }

    final long getHoraEjecucionRemota() {
      return hora_ejecucionremota;
    }

    final void setIdTarea(String tarea) {
      id_tarea=tarea;
    }
    final String getIdTarea() {
      return id_tarea;
    }
    
    final void setEstadoContRemoto(String estado_contremoto) {
      this.estado_contremoto = estado_contremoto;
    }

    final void setHoraSolicitud(long hora) {
      this.hora_solicitud = hora;
    }

    final void setHoraEjecucionRemota(long hora) {
      this.hora_ejecucionremota = hora;
    }
    
    final void setIdContLocal(String id_contlocal) {
      this.id_contlocal = id_contlocal;
    }

    final void setIdContRemoto(String id_contremoto) {
      this.id_contremoto = id_contremoto;
    }

  
  }
}