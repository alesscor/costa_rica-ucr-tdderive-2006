package admin;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

import orgainfo.OIExcepcion;
import orgainfo.OIPersistente;
import tdutils.Zip;
import tdutils.tdutils;

/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero<br>
 * @version 1.0<br>
 */
/** Controla el ambiente de ejecución y la ejecución misma de una aplicación, 
 * siguiendo las órdenes dadas por el despachador que afectan directamente 
 * sobre la ejecución: valores iniciales, facilitación de archivos iniciales,
 * iniciar y seguir.
 */
class ADMINAPPControladores extends PERSCoordinacion.Controladores {
  private ADMINAPPDivisoras divisora;
  private ADMINAPPUnificadoras unificadora;
  private ADMINAPPTrabajos trabajo;
  private Map procesos;
  public ADMINAPPControladores(ADMINGLOInfo desc0,ADMINAPPTrabajos trabajo0) {
    /*
     * lo siguiente debe poner identificación al controlador:
     * i. parcial: <nombre_compu> + <secuencia de procesos>
     * ii. padre: <identificación del solicitante>
     * iii. grupo: <grupo del solicitante> (<==> <id de la tarea>)
     */
    super(desc0,false,trabajo0.solicitante);

    _inicia(trabajo0);
  }
  /**
   * Divide la tarea solicitada por el usuario y asigna cargas a los subtrabajos
   * resultantes de la división.
   * @throws ADMINAPPExcPreejecucion Si hubo error en la división.
   */
  private void divide() throws ADMINAPPExcPreejecucion{
    this.getDivisora().divideTrabajoAdmin(trabajo);
  }
  private void _inicia(ADMINAPPTrabajos trabajo0){    
    String sNombreDivisora="";
    String sNombreUnificadora="";
    divisora=null;
    unificadora=null;
    trabajo=trabajo0;
    procesos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    if(trabajo!=null){
      if(trabajo.getTarea()!=null){
        this.setTarea(trabajo.tarea);
      }
    }
  }
  private void _revisionInicial() throws ADMINAPPExcPreejecucion{
    if(this.getDivisora()==null || this.getUnificadora()==null || trabajo==null){
      throw new ADMINAPPExcPreejecucion("No se ha asignado algún objeto " +
        "que describa, divida o una a la tarea ni a sus partes.");
    }
    if(trabajo.getTarea()==null){
      throw new ADMINAPPExcPreejecucion("El trabajo no tiene una tarea" +
        "asignada.");      
    }
    if(trabajo.getTarea().getPrograma()==null){
      throw new ADMINAPPExcPreejecucion("La tarea es inválida.");      
    }    
  }
  /**
   * Prepara el ambiente para que se pueda ejecutar una
   * aplicación. Aquí el hilo de control se bifurca así que
   * la instancia será compartida por varios hilos.
   * <li>Primero divide la tarea en subtrabajos.</li>
   * <li>Luego prepara los procesos para cada subtrabajo.</li>
   * <li>Al final pone los procesos en la lista de espera del planificador.</li>
   * <li>Un hilo se encargará, según le convenga al sistema desde la
   * perspectiva de las políticas de administración, de poner en marcha
   * a cada subtrabajo.</li>
   * <li>Esta operación se devuelve inmediatamente, luego de poner
   * a los procesos en lista de espera.</li>
   * @throws ADMINAPPExcPreejecucion
   */
  public void iniciaAplicacion() throws ADMINAPPExcPreejecucion{
    _revisionInicial();
    if(this.getSubtrabajos().size()!=0){      
    }
    if(this.getSubtrabajos().size()==0){
      try {
        
        this.divide();
      } catch (ADMINAPPExcPreejecucion e) {
        throw new ADMINAPPExcPreejecucion("No se pudo dividir el trabajo " +
                  "del grupo '"+this.getIdGrupo()+"'",e);
      }      
    }
    try{
    	ponProcesosListaEspera();
    }catch(ADMINAPPExcPreejecucion e){
      throw new ADMINAPPExcPreejecucion("No se pudo poner en lista de espera " +
            "el trabajo del grupo '"+getIdGrupo()+"'",e);
    }
  }
  /**
   * Obtiene el resultado de la aplicación, devolviendo verdadero en caso
   * de estar completada y falso en otro caso.
   * <li>Actualiza el estado de la tarea en caso de haberse terminado.</li>
   * @return Si la aplicación ha sido completada.
   */
  public boolean reuneResultados()throws ADMINAPPExcepcion {
    boolean bOK=false;
    PERSCoordinacion.Tareas tarea=null;
    this._revisionInicial();
    tarea=(PERSCoordinacion.Tareas)trabajo.getTarea();
    // if(tarea.getEstadoTarea().compareTo(ADMINAPPITareas.TAREA_MARCHA)==0 ||tarea.getEstadoTarea().compareTo(ADMINAPPITareas.TAREA_INICIO)==0){
    if(tarea.getEstadoTarea().compareTo(ADMINAPPITareas.TAREA_MARCHA)==0 ){
      // System.out.println("misterio:"+tarea.getEstadoTarea());
      bOK=this.getUnificadora().unificaTrabajoAdmin(trabajo);
      try{
        if(bOK){
          /*
           * <2006 />
           * [1] Cambia el estado de la tarea a terminado
           * [2] Hace commit transaction
           */
          ((ADMINGLOInfo)this.info).println("controlador","une subtrabajos: " + tarea.getIdTarea());
          tarea=(PERSCoordinacion.Tareas)trabajo.getTarea();
          tarea.setEstadoTarea(ADMINAPPITareas.TAREA_FIN);
          tarea.write();
          this.info.getConex().dbCommit();
        }
      }catch(Exception ex){
        ((ADMINGLOInfo)this.info).println("controlador","erra unión de subtrabajos: " + tarea.getIdTarea());
        throw new ADMINAPPExcPosejecucion("Error luego de " +
          "unificar unificar Trabajo",ex);
      }
    }
    return bOK;
  }
  /**
   * Crea los valores iniciales de los procesos que se encargarán
   * de trabajar para la aplicación.
   * <li>Se va a tener un proceso por subtrabajo.</li>
   * <li>Por hacer cuando se quiera hacer esto arbitrariamente,
   * por ahora el planificador es el encargado.</li>
   */
  private void creaProcesosAplicacion() throws ADMINAPPExcPreejecucion{
    
  }
  /**
   * Pone a los subtrabajos en la lista de espera del planificador.
   * <li>Por hacer cuando se quiera hacer esto arbitrariamente.</li>
   */
  private void ponProcesosListaEspera() throws ADMINAPPExcPreejecucion{
    
  }
  /**
   * Termina una tarea, interrumpiendo sus actividades.
   * <li>Por hacer cuando se quiera hacer esto arbitrariamente,
   * por ahora el planificador es el encargado.</li>
   * @throws ADMINAPPExcInterrupcion Si ocurre error.
   */
  public void terminaAplicacion() throws ADMINAPPExcInterrupcion{
  }
  /**
   * Carga los objetos de la tarea desde los objetos persistentes,
   * dejando actualizada la información de las instancias.
   * @throws ADMINAPPExcepcion Si ocurre error.
   */
  public void obtieneEstadoActual() throws ADMINAPPExcepcion {
    
  }
  /**
   * Obtiene la identificación del trabajo.
   * @return La identificación del trabajo.
   */
  public String getIdentificacion(){
    if(trabajo!=null){
      if(trabajo.getTarea()!=null){
        return trabajo.getTarea().getIdTarea();
      }
    }
    return "";
  }
  /**
   * Ejecuta el subtrabajo dado.
   * <li>Ejecuta una parte de la tarea.</li>
   * <2006/>
   * <li>Pone a este subtrabajo el estado que le corresponde
   * según si se pudo terminar o no.</li>
   * <li>Se asume que el hilo que se crea aquí va
   * a escribir en la base de datos el estado del 
   * subtrabajo.</li>
   * @param subtrabajo El subtrabajo a ejecutar.
   */
  public void ejecuta(ADMINAPPISub_trabajos subtrabajo)
  throws ADMINPOLExcepcion,ADMINAPPExcepcion {
    /*| TODO Varios pendientes 2006 
     *| [0] PENDIENTE: Crear un hilo para la ejecución de los 
     *|     pasos siguientes, que vaya desde [hn hasta hn]. 
     *| [1] Crea una envoltura.
     *| [2] Registra la envoltura.
     *| [3] Hace que la envoltura se ejecute en otro hilo.
     */
    
    // [hn
    
    ADMINPOLEnvolturas env=new ADMINPOLEnvolturas(subtrabajo);
    // tal vez esto es una porquería de código, pero de alguna forma 
    // tengo que hacer la escritura en el subtrabajo 
    PERSCoordinacion.Sub_trabajos subtrabajoreal=(PERSCoordinacion.Sub_trabajos)subtrabajo;
    try {
      subtrabajoreal.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_MARCHA);
      subtrabajoreal.setHoraIngreso(System.currentTimeMillis());
      subtrabajoreal.write();
      env.write();
      this.getDescriptor().getConex().dbCommit();
    } catch (OIExcepcion e) {
      throw new ADMINAPPExcepcion("Problema al abrir envoltura " +
          "para su ejecución.");
    }
    try{
      env.invoca(null);
    }catch(Exception exc){
      /*|[Exc] 
       *| Debería registrarse un error en el subtrabajo
       *| env.setEstado(...); env.write();
       */
      // env.setEstadoControl("");
    }
    if(env.getSiCompletado()){
      subtrabajo.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_FIN);
      subtrabajo.setSiFin(true);
    }else{
      subtrabajo.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_ERROR);
      subtrabajo.setSiFin(false);
    }
    try {
      subtrabajoreal.write();
    } catch (ADMINGLOExcepcion e) {
      // ni modo, no se pudo escribir el subtrabajo.
    }
    if(subtrabajo.getSiFin()){
      /*
       * debe traerse al unificador para que gestione el fin de la tarea
       */
      this.getUnificadora().gestionaFinSubtrabajo(subtrabajo);
      subtrabajo.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_FIN2);
      /*
       * si el subtrabajo es local, poner el 
       * valor ADMINAPPISub_trabajos.SUBTRA_ENTREGADO
       * 
       */
      if(subtrabajo.getTarea().getSiCoordina()){
        /*
         * el subtrabajo es local, es decir, la computadora
         * es la actual coordinadora 
         */
        subtrabajo.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_ENTREGADO);
      }
      try {
        subtrabajoreal.write();
        this.getDescriptor().getConex().dbCommit();
      } catch (ADMINGLOExcepcion e) {
        // ni modo, no se pudo escribir el subtrabajo.
        e.printStackTrace();
      } catch (OIExcepcion e1) {
        e1.printStackTrace();
      }
    }
    
    // hn]
    
  }
  /**
   * Indica la identificación de un controlador que se encarga de una tarea.
   * @param desc Conexión con la base de datos.
   * @param tarea Tarea a la cual se le busca el controlador.
   * @return Identificación del controlador, vacío si el 
   * controlador no fue encontrado en la base de datos.
   */
  protected static final String getControlador(ADMINGLOInfo desc, ADMINAPPITareas tarea){
    String sql="";
    /**
     * Recordar que el id del controlador es el mismo de la tarea
     */
    String id_controlador="";
    ResultSet rs=null;
    /// recordar que el id del controlador es el mismo de la tarea
    sql="SELECT id_tarea " +
        "FROM Controladores " +
        "WHERE id_tarea=" + tdutils.getQ(tarea.getIdTarea());
    try {
      rs=ADMINAPPControladores.getRSSQL(desc,sql);
      if(rs.next()){
        /// recordar que el id del controlador es el mismo de la tarea
        id_controlador=rs.getString("id_tarea");
      }
      rs.close();
    } catch (OIExcepcion e) {
      e.printStackTrace();
      System.err.println("[Buscando controlador] Problema en la lectura de la base de datos");
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println("[Buscando controlador] Problema en la lectura de la base de datos");
    }
    return id_controlador;
  }
  /**
   * Indica la identificación de un controlador que se encarga de una tarea.
   * @param desc Conexión con la base de datos.
   * @param trabajo Trabajo de la tarea.
   * @return La identificación del controlador.
   */
  protected static final String getControlador(ADMINGLOInfo desc, ADMINAPPTrabajos trabajo){
    ADMINAPPITareas tarea;
    tarea=trabajo.getTarea();
    return getControlador(desc,tarea);
  }
  /**
   * Devuelve el resultado de una tarea a su usuario de la manera adecuada.
   * @throws ADMINAPPExcepcion En caso de error.
   */
  final void retornaResultado()throws ADMINAPPExcepcion{
    String dirAComprimir=null;
    String nombreArchivo=null;
    boolean devolucion=false;
    Calendar calendario=GregorianCalendar.getInstance();
      /*
       * Comprime el directorio de resultados
       * Hace con el directorio de resultados lo que corresponda
       * según lo solicitó el usuario:
       * (o) Escribirlo en el directorio del usuario.
       * (o) Mandarlo por correo electrónico a donde indicó el usuario.
       */
    dirAComprimir=this.getTarea().getResultadosDir();
    nombreArchivo=this.getTarea().getTareaDir();
    nombreArchivo=nombreArchivo+"/"+this.getTarea().getAlias();
    nombreArchivo=nombreArchivo+"-resultado-";    
    nombreArchivo=nombreArchivo+calendario.get(Calendar.YEAR) +"."+(calendario.get(Calendar.MONTH)+1)+"."+calendario.get(Calendar.DAY_OF_MONTH);
    nombreArchivo=nombreArchivo+"_"+calendario.get(Calendar.HOUR_OF_DAY)+"_"+calendario.get(Calendar.MINUTE)+"_"+calendario.get(Calendar.SECOND)+"_"+calendario.get(Calendar.MILLISECOND);
    nombreArchivo=nombreArchivo+".zip";
    this.getTarea().setArchivoResultado(nombreArchivo);
    try {
      Zip.zipFile(nombreArchivo,dirAComprimir);
    } catch (IOException e) {
      ((ADMINGLOInfo)this.info).println("controlador","erra compresión de resultado: " + this.getIdTarea());
      throw new ADMINAPPExcepcion(e);
    }
    /*
     * Hace la devolución si el usuario espera sincrónicamente.
     */
    ((ADMINGLOInfo)this.info).println("controlador","desalmacena conexión: " + this.getIdTarea());
    devolucion=((ADMINGLOInfo)this.info).getPlanificador().realizaInvocacion(this.getIdTarea());
    if(devolucion){
      // se activó la entrega del archivo de salida
      ((ADMINGLOInfo)this.info).println("controlador","termina y devuelve archivo de salida: " + this.getIdTarea());
    }else{
      // TODO se asume que esto no va a pasar, es decir, que el usuario pida el resultado de una manera asincrónica.
      ((ADMINGLOInfo)this.info).println("controlador","termina sin archivo de salida: " + this.getIdTarea());
    }
  }
  /**
   * Devuelve el mapa de subtrabajos.
   * @return
   */
  final Map getSubtrabajos(){
    Map mp=new TreeMap(),mp0;
    if(this._tarea!=null){
      mp0=this._tarea.getSubtrabajos();
      if(mp0!=null){
        mp.putAll(mp0);
      }
    }
    return mp;
  }
  static ADMINAPPControladores cargaControlador(ADMINGLOInfo desc, ADMINAPPTrabajos trabajo){
    ADMINAPPControladores controlador=new ADMINAPPControladores(desc,trabajo);
    ResultSet rs=null;
    String sql="",id_parcial=null,id_tarea=null;
    id_parcial=trabajo.getSolicitante().getIdParcial();
    id_tarea=trabajo.getTarea().getIdTarea();
    sql=" SELECT * FROM Controladores " +
        " WHERE si_activo=true AND id_tarea=" + tdutils.getQ(id_tarea);
    try {
      rs=OIPersistente.getRSSQL(desc,sql);
      rs.next();
      controlador.openControlador(rs);
      rs.close();
    } catch (OIExcepcion e1) {
      e1.printStackTrace();
    } catch (SQLException e2) {
      e2.printStackTrace();
    }
    return controlador;
  }
  /**
   * Indica si la computadora actual es la que coordina 
   * @return Verdadero si la compu actual es la coordinadora.
   */
  public boolean siLocal(){
    boolean resultado=false;
    if(ADMINGLOInfo.class.isInstance(this.getDescriptor()) && this.getTarea()!=null){
      resultado=((ADMINGLOInfo)this.getDescriptor()).getComputadora().
                getNombre().compareTo(this.getTarea().getNodoCreador())==0;
    }
    return resultado;
  }
  private final ADMINAPPDivisoras getDivisora(){
    String sNombreDivisora="";
    if(divisora==null){
      if(trabajo.getTarea().getPrograma()!=null){
        sNombreDivisora=trabajo.getTarea().getPrograma().getDivisora();
        /*
         * obtiene las instancias de las clases divisora y unificadora
         */
        try {
          divisora=(ADMINAPPDivisoras)Class.
                forName(sNombreDivisora).newInstance();
        } catch (InstantiationException e) {
          // 
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          // 
          e.printStackTrace();
        } catch (ClassNotFoundException e) {
          // 
          e.printStackTrace();
        }
      }
    }
    return this.divisora;
  }
  private final ADMINAPPUnificadoras getUnificadora(){
    String sNombreUnificadora="";
    if(unificadora==null){
      if(trabajo.getTarea().getPrograma()!=null){
        sNombreUnificadora=trabajo.getTarea().getPrograma().getUnificadora();
        /*
         * obtiene las instancias de las clases divisora y unificadora
         */
        try {
          unificadora=(ADMINAPPUnificadoras)Class.
                forName(sNombreUnificadora).newInstance();
        } catch (InstantiationException e) {
          // 
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          // 
          e.printStackTrace();
        } catch (ClassNotFoundException e) {
          // 
          e.printStackTrace();
        }
      }
    }
    return this.unificadora;
  }
  final public String toString(){
    if(trabajo!=null){
      return trabajo.toString();
    }else{
      return "(vacio)";      
    }
  }
}