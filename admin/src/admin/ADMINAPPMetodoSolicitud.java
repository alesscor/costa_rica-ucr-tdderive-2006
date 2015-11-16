package admin;

import oact.*;

import java.text.SimpleDateFormat;
import java.util.*;
import orgainfo.*;

import java.io.*;
import tdutils.*;
import aco.*;
/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Implementa la recepción que se brinda a una solicitud
 * que llega al objeto activo.<br>
 * Llegada la solicitud al despachador, una instancia de
 * esta clase es creada para analizar la solicitud.
 * 
 */
public class ADMINAPPMetodoSolicitud extends ADMINAPPMetodoDespAbs {
  private String _cNombreDestino;
  private Map _mpArchivosACargar;
  private boolean _solo_cliente;

  private String id_solicitud;
  /**
   * Directorio para exportaciones.
   */
  private String _cDirectorioLocal;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public ADMINAPPMetodoSolicitud() {
    _inicio();
  }

  public ADMINAPPMetodoSolicitud(OACTSirvienteAbs sirviente0) {
    super(sirviente0);
    _inicio();
  }

  public ADMINAPPMetodoSolicitud(String servantID) {
    super(servantID);
    _inicio();
  }
  private void _inicio(){
    _mpArchivosACargar=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    _solo_cliente=false;
    _cNombreDestino="localhost";
      // clase complementaria de la de this
    this.setComplemento(admin.ADMINAPPMetodoAtencion.class);
    _cDirectorioLocal="(sin asignar)";
    tarea=new PERSCoordinacion.Tareas(null,true);
    solicitante=new PERSCoordinacion.Solicitantes(null,true);
    solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_INICIO);
    solicitante.setHoraSolicitud(System.currentTimeMillis());
  }
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  protected void revisaEstados() {
    /**@todo Implement this admin.LEESolicitudAbs abstract method*/
  }
  /**
   * Es lo primero en ser ejecutado por un oact.OACTGestorProxy, cuando
   * ha podido conectarse a un par de tdderive.
   * Tiene la capacidad de escribir en la red y leer de ésta.
   * @return Si resultó bien la ejecución.
   * @throws ADMINExcepcion si hay problema.
   */
  public boolean ejecutarInicio() throws ADMINGLOExcepcion{
    boolean bSiIniciado=true;
   if(this._si_local){
     // los archivos no se exportan, pues son alcanzables por el sistema global
   }else{
     // exporta archivos, sincronizándose con el extremo receptor
    try {
      /*
       * los exporta
       */
      exportaArchivos();
    }
    catch (ADMINGLOExcepcion ex) {
      bSiIniciado=false;
    }
    catch (OIExcepcion ex) {
      bSiIniciado=false;
    }
   }
   if(info!=null && info.getConex()!=null){
     try {
      /*
        * da la transacción por consumada
        * luego de realizar la transacción,
        * sea lo que sea que haya pasado.
        */
       info.getConex().dbCommit();
     }catch (OIExcepcion e) {
      e.printStackTrace();
     }
   }
   return bSiIniciado;
  }

  public OACTSolicitud ejecutar() throws java.lang.Exception {
    String sIndicaRemota="";
    String sufijo="";
    SimpleDateFormat sdf=null;
    /*
     * [5] determina si debe esperar resultados o poner fin a su trabajo 
     * (en cuyo caso se asume que el sistema manda respuesta a las instancias
     * de retorno especificadas en la solicitud del sistema)
     */
     if(_si_esperar && !this.siSubtrabajo()){
       /*
        * [5.1.1] recibe un indicio de la respuesta 
        */
       /*
        * el nombre del archivo que se va a importar
        * al directorio actual
        */
       sIndicaRemota=this.receive();
       /*
        * el archivo resultante, por ahora no divide el nombre (qué vaina!!!)
        */
       if(this.solicitante.getHoraSolicitud()>0){
         sdf=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
         sufijo="."+sdf.format(new Date(this.solicitante.getHoraSolicitud()));
       }
       this.recibeArchivo(".","resultado."+tarea.getAlias() + sufijo+ ".zip");
       /*
        * [5.1.2] termina de importar la respuesta y la analiza 
        */
       /*
        * [5.1.3] termina cerrando la comunicación
        */
       this.closeComm();
     }else{
       /*
        * [5.2.1] termina cerrando la comunicación 
        */
       this.closeComm();
     }
    
    return null;
  }
  /**
   * Prepara un archivo para su eventual exportación.
   */
  public ADMINAPPIArchivos addArchivoExt(String cNombreArchivo){
    PERSCoordinacion.Archivos archivo=new PERSCoordinacion.Archivos(null);
    archivo.setNombre(cNombreArchivo);
    _mpArchivosACargar.put(cNombreArchivo, archivo);
    return archivo;
  }
  public void addRetorno(String tipoRetorno,String valorRetorno){
    PERSCoordinacion.Retornos retorno=new PERSCoordinacion.Retornos();
    retorno.setTipoRetorno(tipoRetorno);
    retorno.setValorRetorno(valorRetorno);
    retorno.setEstadoRetorno(ADMINAPPIRetornos.RETORNO_ESTADOPENDIENTE);
    mpRetornos.put(retorno.getValorRetorno(),retorno);
  }  
  /**
   * Remueve un archivo del conjunto de archivos preparados para la
   * exportación.
   * @param cNombreArchivo El nombre del archivo a excluir.
   */
  public void remArchivo(String cNombreArchivo){
    mpArchivos.remove(cNombreArchivo);
  }
  /**
   * Obtiene el alias de un programa.
   * @return El alias del programa.
   */
  public String getAliasPrograma(){
    if(this.siSubtrabajo()){
      return this.subtrabajo.getTarea().getAlias();
    }
    return tarea.getAlias();
  }
  /**
   * Asigna el alias con el que se hace referencia a un programa.
   * @param setAliasPrograma El alias del programa.
   */
  public void setAliasPrograma(String setAliasPrograma){
    if(!this.siSubtrabajo()){
      tarea.setAlias(setAliasPrograma);
    }
  }
  /**
   * Obtiene los parámetros del sistema.
   * <li>Éstos son separados por la constante 
   * ADMINAPPIniciador.APP_SEPARADORARGUMENTOS</li>
   * @return Los parámetros del sistema.
   */
  public String getParametrosPrograma(){
    if(this.siSubtrabajo()){
      return this.subtrabajo.getComando();
    }
    return tarea.getParametros();
  }
  /**
   * Obtiene los parámetros del sistema.
   * <li>Éstos son separados por la constante 
   * ADMINAPPIniciador.APP_SEPARADORARGUMENTOS</li>
   * @param setParametrosPrograma Los parametros del sistema.
   */
  public void setParametrosPrograma(String setParametrosPrograma){
    if(!this.siSubtrabajo()){
      tarea.setParametros(setParametrosPrograma);
    }
  }
/**
 * Obtiene el retorno configurado para un programa.
 * <li>Son instrucciones a seguir para la entrega de
 * resultados del programa.</li>
 * @return El retorno del programa.
 */
  public String getRetornoPrograma(){
    return solicitante.getRetorno();
  }
  /**
   * Asigna el retorno configurado de un programa.
   * <li>Son instrucciones a seguir para la entrega de
   * resultados del programa.</li>
   * @param setRetornoPrograma Los valores de retorno.
   */
  public void setRetornoPrograma(String setRetornoPrograma){
    solicitante.setRetorno(setRetornoPrograma);
  }
  public boolean getComoCliente(){
    return this._solo_cliente;
  }
  public void setComoCliente(boolean setComoCliente){
    _solo_cliente=setComoCliente;
  }
  /**
   * Obtiene el nombre de la compu de destino.
   * @return Nombre de la compu de  destino.
   */
  String getNombreDestino(){
    return _cNombreDestino;
  }
  /**
   * Asigna el nombre de la compu de destino.
   * @param setNombreDestino Compu de destino.
   */
  void setNombreDestino(String setNombreDestino){
    _cNombreDestino=setNombreDestino;
  }
  // @TODO getDirectorioLocal debe ser documentado.
  String getDirectorioLocal(){
    return _cDirectorioLocal;
  }
  // @TODO setDirectorioLocal debe ser documentado.
  void setDirectorioLocal(String setDirectorioLocal){
    if(!this.siSubtrabajo()){
      _cDirectorioLocal=setDirectorioLocal;
    }
  }
  /**
   * Crea un directorio local, incluyendo directorios
   * para archivos comprimidos y bloques.
   *
   */
  void createDirectoriosLocales() throws ADMINGLOExcepcion{
    int i=0;
    File dirLocal=null;
    String[] nombres = {this._cDirectorioLocal+"/"+
      PERSCoordinacion.Tareas.DIR_COMPRIMIDOS,
      this._cDirectorioLocal+"/"+
      PERSCoordinacion.Tareas.DIR_BLOQUES};
    for(i=0;i<nombres.length;i++){
      dirLocal=new File(nombres[i]);
      if(!dirLocal.mkdirs()){
        if(!dirLocal.exists()){
          throw new ADMINGLOExcepcion("No se pudo crear directorio temporal '"+
            nombres[i]+"'.");
        }else{
          // el directorio ya existía
        }
      }
      dirLocal=null;
    }
  }

  /**
   * Copia los archivos a utilizar en el programa, en su directorio de trabajo,
   * comprime tales archivos y deja bloques en sus directorios correspondientes.
   * <li>Registra los archivos de la tarea.</li>
   * <li>Si se debe exportar se crea un directorio cuyo nombre será
   * el indicado por <tt>_cDirectorioLocal</tt>.</li>
   * <li>Si se exporta, se exportan los archivos bloque por bloque, asi
   * que aquí se indica el nombre de cada bloque.</li>
   * <li>Si no se exporta, entonces no se indican bloques, pero
   * se indica que cada archivo está local.</li>
   * @throws ADMINExcepcion Si error.
   * @throws OIExcepcion Si error.
   * @deprecated Mejor el método <tt>ubicaArchivos</tt>, que ya no realiza 
   * la redundancia de crear subarchivos.
   */
  protected void ubicaArchivosOld() throws OIExcepcion,ADMINGLOExcepcion{
    int i = 0, idx = 0;
    String cNombreArchivo, cNombreComprimido, cBloque;
    tdutils.SplitInfo[] acBloques;
    PERSCoordinacion.Archivos archivoI, archivoII = null;
    File fArchivo = null;
    Iterator itr;
    mpArchivos.clear();
    itr = this._mpArchivosACargar.values().iterator();
    while (itr.hasNext()) {
      cNombreArchivo = (String) itr.next();
      fArchivo = new File(cNombreArchivo);
      if(!fArchivo.exists()){
        // el archivo no existe!!!
        throw new ADMINGLOExcepcion(
            "El siguiente archivo de entrada no existe: '" +
            cNombreArchivo + "'.");
      }
      // el archivo o directorio sí existe
      // si se debe exportar -> se comprime en directorio de comprimidos
      if(!this._si_local){
        cNombreComprimido = this.getDirectorioLocal()+"/"+
            PERSCoordinacion.Tareas.DIR_COMPRIMIDOS + "/" +
            fArchivo.getName();
        try{
        this.createDirectoriosLocales();        
        Zip.zipFile(cNombreComprimido,
                            cNombreArchivo);
        }catch(IOException ex){
          throw new ADMINGLOExcepcion("No se pudo comprimir archivo.",ex);
        }
        // se hacen bloques en el directorio de bloques (para exportación)
        //
        if (this._cantidad_archivos > 0) {
          acBloques = tdutils.splitFileExt(cNombreComprimido,
              _cantidad_archivos, false,
              this.getDirectorioLocal()+"/"+PERSCoordinacion.Tareas.DIR_BLOQUES);
        }
        else {
          acBloques = tdutils.splitFileExt(cNombreComprimido,
              _tamano_archivos, true,
              this.getDirectorioLocal()+"/"+PERSCoordinacion.Tareas.DIR_BLOQUES);
        }
        if (_si_copiar_a_dir_trabajo) {
          // el archivo debe ser copiado en el directorio de trabajo
          Zip.unzipFile(cNombreComprimido,
                                this.getDirectorioLocal());
        }
        i = 0;
        while (i < acBloques.length) {
          archivoI = new PERSCoordinacion.Archivos(this.info);
          archivoI.setNombre(fArchivo.getName());
          archivoI.setSiLocal(false);
          idx = acBloques[i].nombreArchivo.lastIndexOf(".")+1;
          cBloque = acBloques[i].nombreArchivo.substring(idx);
          archivoI.setBloque(cBloque);
          // prepara lista de archivos a exportar
          mpArchivos.put(archivoI.getNombre() /*+ ":" + archivoI.getBloque()*/,
                         archivoI);
          i++;
        }
//        _max_tamano_exportacion=Math.max(
//            acBloques[0].tamanoArchivo,acBloques[acBloques.length-1].tamanoArchivo);
      }else{
        // los archivos no se exportan
        archivoI = new PERSCoordinacion.Archivos(this.info);
        archivoI.setNombre(fArchivo.getName());
        archivoI.setSiLocal(true);
        archivoI.setRutaOriginal(fArchivo.getAbsolutePath());
        mpArchivos.put(archivoI.getNombre(),
                       archivoI);
      }
    }
  }
  /**
   * Copia los archivos a utilizar en el programa, en su directorio de trabajo,
   * comprime tales archivos y deja bloques en sus directorios correspondientes.
   * <li>Registra los archivos de la tarea.</li>
   * <li>Si se debe exportar se crea un directorio cuyo nombre será
   * el indicado por <tt>_cDirectorioLocal</tt>.</li>
   * <li>Si se exporta, se exportan los archivos bloque por bloque, asi
   * que aquí se indica el nombre de cada bloque.</li>
   * <li>Si no se exporta, entonces no se indican bloques, pero
   * se indica que cada archivo está local.</li>
   * @throws ADMINExcepcion Si error.
   * @throws OIExcepcion Si error.
   */
  protected void ubicaArchivos() throws OIExcepcion,ADMINGLOExcepcion{
    int i = 0, idx = 0;
    String cNombreArchivo, cNombreComprimido, cBloque;
    tdutils.SplitInfo[] acBloques;
    PERSCoordinacion.Archivos archivoI;
    ADMINAPPIArchivos archivo0;
    File fArchivo = null;
    Iterator itr;
    mpArchivos.clear();
    itr = this._mpArchivosACargar.values().iterator();
    while (itr.hasNext()) {
      archivo0=(ADMINAPPIArchivos)itr.next();
      if(this.siSubtrabajo()){
        cNombreArchivo = archivo0.getDirectorio();
      }else{
        cNombreArchivo = archivo0.getNombre();
      }      
      fArchivo = new File(cNombreArchivo);
      if(!fArchivo.exists()){
        // el archivo no existe!!!
        throw new ADMINGLOExcepcion(
            "El siguiente archivo de entrada no existe: '" +
            cNombreArchivo + "'.");
      }
      // el archivo o directorio sí existe
      // si se debe exportar -> se comprime en directorio de comprimidos
      if(!this._si_local && (!this.siSubtrabajo() )){
        cNombreComprimido = this.getDirectorioLocal()+"/"+
            PERSCoordinacion.Tareas.DIR_COMPRIMIDOS + "/" +
            fArchivo.getName();
        try{
          // crea directorios
          this.createDirectoriosLocales();
          if(!this.siSubtrabajo()){
            Zip.zipFile(cNombreComprimido,
                              cNombreArchivo);
          }else{
            // tdutils.copiaArchivo()
          }
        }catch(IOException ex){
          throw new ADMINGLOExcepcion("No se pudo comprimir archivo.",ex);
        }
        if (_si_copiar_a_dir_trabajo) {
          // el archivo debe ser copiado en el directorio de trabajo
          Zip.unzipFile(cNombreComprimido,
                                this.getDirectorioLocal());
        }
        // antes habían bloques
        i=0;
        archivoI = new PERSCoordinacion.Archivos(null);
        archivoI.setNombre(fArchivo.getName());
        // información para la aplicación
        archivoI.setInfoArchivo(archivo0.getInfoArchivo());
        // falso el valor de si_local, pues se eligió la exportación.
        archivoI.setSiLocal(false);
        /*
         * TODO Borrar este comentario: esto es por si las moscas.
         */
        archivoI.setRutaOriginal(fArchivo.getAbsolutePath());
        // no hay bloques
        archivoI.setBloque("");
        // agrega el archivo a la lista de archivos a exportar
        mpArchivos.put(archivoI.getNombre(),archivoI);
      }else{
        // los archivos no se exportan
        archivoI = new PERSCoordinacion.Archivos(this.info);
        archivoI.setNombre(fArchivo.getName());
        archivoI.setSiLocal(true);
        // información para la aplicación
        archivoI.setInfoArchivo(archivo0.getInfoArchivo());
        archivoI.setRutaOriginal(fArchivo.getAbsolutePath());
        // agrega el archivo a la lista de archivos a ubicar
        mpArchivos.put(archivoI.getNombre(),
                       archivoI);
      }
    } // fin del while del iterador por el mapa de archivos
  }  
  /**
   * Realiza los preparativos para la lectura remota de este
   * objeto.
   * @return Si el objeto está preparado para ser enviado
   * a un objeto activo remoto.<br>
   * Es lo primero en ser ejecutado por un oact.OACTGestorProxy.
   * @return Si resultó bien la ejecución.
   * @throws ADMINExcepcion si hay problema.
   * <b>Requiere:</b>
   * <li>Que los archivos a cargar por el programa solicitado
   * hayan sido indicados usando el método addArchivo(...).</li>
   * <li>Que el alias del programa haya sido indicado.</li>
   * <li>Que los parámetros necesarios hayan sido expresados.</li>
   * <li>Que haya sido provista la información de retorno.</li>
   * <b>Otros detalles:</b>
   * <li>Puede indicarse el tamaño de los bloques en que se divide cada archivo,
   * o la cantidad de bloques en la que cada archivo se divide.</li>
   * <li>Puede indicarse si el programa trabaja como cliente o como par de tdderive.</li>
   * <li>Puede indicarse el destino al cual va esta solicitud.</li>
   */
  public boolean isPreparado() {
    boolean resultado=false;
    if(tarea.getAlias()==null || tarea.getAlias()==""){
      return false;
    }
    if(_mpArchivosACargar.size()>0){
      try {
        ubicaArchivos();
      }
      catch (ADMINGLOExcepcion ex) {
        return false;
      }
      catch (OIExcepcion ex) {
        return false;
      }
    }
    if(this.siSubtrabajo()){
      resultado=ADMINAPPISub_trabajos.SUBTRA_ESPERA.compareTo(
          subtrabajo.getEstadoSubtrabajo())==0;
      if(ADMINAPPISub_trabajos.SUBTRA_FIN.compareTo(
          subtrabajo.getEstadoSubtrabajo())==0){
        resultado=true;
      }
    if(ADMINAPPISub_trabajos.SUBTRA_FIN2.compareTo(
    subtrabajo.getEstadoSubtrabajo())==0){
  resultado=true;
}
    }else{
      resultado=ADMINAPPISolicitantes.SOLICITANTE_LISTO.compareTo(
          solicitante.getEstadoSolicitante())==0;
    }
    return resultado;
  }
  /**
   * Exporta los archivos de una tarea solicitada.
   * @throws ADMINExcepcion
   * @throws OIExcepcion
   * @deprecated Utilizar <tt>exportaArchivos</tt>, pues ya no se exportan 
   * archivos partidos escritos en alamcenamiento secundario, y además ahora
   * se trabaja con un canal exclusivo para la transmisión de datos.
   */
  protected void exportaArchivosOld() throws OIExcepcion,ADMINGLOExcepcion{
    int campoAccion=1,campoSolicitud=0,campoOtros=2;
    String sIndicaRemota="",sContenido="",sNombreArchivoI="";
    String asIndicaciones[];
    PERSCoordinacion.Archivos archivoI,archivoII=null;
    Iterator itr;
    Runnable rnn=null;
    Thread thr;
    byte[] contenidoI;
//    if(_max_tamano_exportacion>0){
//      // ya no por problema con archivos de distintos tamaños 
//      // contenidoI = new byte[_max_tamano_exportacion];
//    }else{
//      // no hay archivos que exportar
//      return;
//    }
    itr=mpArchivos.values().iterator();
    try {
      /*
       * Espera si se debe mandar el archivo y cuál es
       * el número de solicitud para siguientes contactos
       * y consultas.
       */
      sIndicaRemota = this.receive();
      asIndicaciones=sIndicaRemota.split(SOLICITUD_SEPARADOR,
      campoOtros+1);
      id_solicitud=asIndicaciones[campoSolicitud];
      sIndicaRemota=asIndicaciones[campoAccion];
      if(sIndicaRemota.compareTo(SOLICITUD_ENVIAR)==0){
        // se manda el archivo
        while(itr.hasNext()){
          archivoI=(PERSCoordinacion.Archivos)itr.next();
          sNombreArchivoI=
              this.getDirectorioLocal()+"/"+
              PERSCoordinacion.Tareas.DIR_BLOQUES+
              "/"+archivoI.getNombre()+"."+archivoI.getBloque();
          contenidoI=tdutils.readFile(sNombreArchivoI);
          this.sendb(contenidoI);
        }
      }else{
        if(sIndicaRemota.compareTo(SOLICITUD_NOENVIAR)==0){
          // no se debe enviar el archivo.
        }else{
          if(sIndicaRemota.compareTo(SOLICITUD_ERROR)==0){
            // ha ocurrido un error.
            throw new ADMINGLOExcepcion("Error en exportación de archivos, "+
                                     "indicado por el importador.");
          }
        }
      }
      itr=null;
      archivoI=null;
      archivoII=null;
      this.closeComm();
    }catch (OACTExcepcion ex) {
      // errores de envío y recepción, se asume que no se puede
      // establecer contacto.
      itr=null;
      archivoI=null;
      archivoII=null;
      try {
        this.closeComm();
      }
      catch (OACTExcepcion ex1) {
      }
      throw new ADMINGLOExcepcion("Error en exportación de archivos.",ex);
    }
    catch (ACONExcArbitraria ex) {
    }
    catch (ACONExcOmision ex) {
      ex.printStackTrace();
    }
    catch (ACONExcTemporizacion ex) {
    }
  }
  /**
   * Exporta los archivos de una tarea o subtrabajo
   * solicitados.<br/>
   * <2005 En caso de ser un subtrabajo, se deben exportar 
   * los archivos que se encuentran en el directorio de 
   * trabajo y que se regitran como archivos de entrada./>
   * @throws ADMINExcepcion
   * @throws OIExcepcion
   */
  protected void exportaArchivos() throws OIExcepcion,ADMINGLOExcepcion{
    /*
     * TODO: Febrero 2006 Iniciar aquí
     * Jalar los archivos del directorio del subtrabajo o de
     * donde diga la base de datos y así exportar.
     */
    int campoAccion=1,campoSolicitud=0,campoOtros=2,puertolocal=0;
    byte[] contenidoI;
    String sIndicaRemota="",sContenido="",sNombreArchivoI="";
    String asIndicaciones[];
    ACONDescriptor desc=new ACONDescriptor();
    ACONAceptadorDespSimple despa=new ACONAceptadorDespSimple();
    desc.localport=0;
    desc.id=this.getClass().getName();
    desc.server=ADMINGLOGestionExportadora.class.getName();
    desc.aoNavegables=new Object[]{
        mpArchivos,
        this.getDirectorioLocal(),
        this.subtrabajo};
    desc.socket_type=ACONDescriptor.STREAM;
    try {
      /*
       * [1] recibe visto bueno para la atención 
       */
      sIndicaRemota=this.receive();
      asIndicaciones=sIndicaRemota.split(SOLICITUD_SEPARADOR,
      campoOtros+1);
      id_solicitud=asIndicaciones[campoSolicitud];
      sIndicaRemota=asIndicaciones[campoAccion];
      if(sIndicaRemota.compareToIgnoreCase(SOLICITUD_ENVIAR)==0){
        // debe realizarse el envío
        /*
         * inicializa el extremo pasivo del subprotocolo (ver paso 3 del
         * protocolo)
         */
        puertolocal=0;
        despa.manejaEventos(desc);
      /*|*\================================================================\*|*/
      /*|*\ try {                                                          \*|*/
      /*|*\   synchronized(Thread.currentThread()){                        \*|*/
      /*|*\     Thread.currentThread().wait(10000);                        \*|*/
      /*|*\   }                                                            \*|*/
      /*|*\ } catch (InterruptedException e) {                             \*|*/
      /*|*\   e.printStackTrace();                                         \*|*/
      /*|*\ }                                                              \*|*/
      /*|*\ int i=0;                                                       \*|*/
      /*|*\ TODO Una vulgaridad: espera activa el cambio de un valor y     \*|*/
      /*|*\ no una señal                                                   \*|*/
      /*|*\ while(puertolocal==0){                                         \*|*/
      /*|*\   puertolocal=despa.getPuertoLocal();                          \*|*/
      /*|*\   System.out.println("El puerto es cero por "+(i++)+"ª vez."); \*|*/
      /*|*\ }                                                              \*|*/
      /*|*\====[esta es la solución a la anterior vulgaridad]==============\*|*/
      /*|*/        despa.esFin();                                          /*|*/
      /*|*/        puertolocal=despa.getPuertoLocal();                     /*|*/
      /*|*/        System.out.println("Usará el puerto: "+puertolocal+".");/*|*/
      /*|*\                                                                \*|*/
      /*|*\================================================================\*|*/
        /*
         * [2] informa detalles de la conexión (solamente el puerto
         * local, por ahora)
         */
        this.send(""+puertolocal+"");
        /*
         * [3] a estas alturas ya estaba iniciado el subprotocolo (el extremo
         * pasivo de la conexión es iniciado en este hilo y es gestionado por
         * el objeto "despa")
         */
         /*
          * [4] espera si debe cerrar la conexión de datos
          */
        sIndicaRemota=this.receive();
        System.out.println(sIndicaRemota);
        System.out.println(">>Cerrará el despacho.<<");
        despa.close();
      }else{
        // no se debe enviar nada      
        if(sIndicaRemota.compareToIgnoreCase(SOLICITUD_NOENVIAR)==0){
          if(this.siSubtrabajo()){
            System.out.println("No se exporta "+this.subtrabajo.toString());
          }
          
        }
      }
      
    }catch (OACTExcepcion ex) {
      // errores de envío y recepción, se asume que no se puede
      // establecer contacto.
      throw new ADMINGLOExcepcion("Error en exportación de archivos.",ex);
    }
    catch (ACONExcArbitraria ex) {
    }
    catch (ACONExcOmision ex) {
      ex.printStackTrace();
    }
    catch (ACONExcTemporizacion ex) {
    }
  }  
  /**
   * <2005 />
   * Asigna el subtrabajo a solicitar.<br/>
   * Se asume que el subtrabajo no es nulo.
   * <li>Obtiene de ahí los archivos que se deben exportar</li>
   * @param subtrabajo Subtrabajo a solicitar.
   */
  protected final void setSubtrabajo(ADMINAPPISub_trabajos subtrabajo){
    if(this.tarea!=null){
      this.tarea=null;
    }
    this.tarea=(PERSCoordinacion.Tareas)subtrabajo.getTarea();
    this.subtrabajo=(PERSCoordinacion.Sub_trabajos)subtrabajo;
    if(!subtrabajo.getSiFin()){
      this._cDirectorioLocal=this.subtrabajo.getSubtrabajoDir();
      this.mpArchivos.clear();
      this.mpArchivos.putAll(subtrabajo.getArchivos());
      /*
       * lo siguiente para evitar posibles desmadres
       * this.tarea=(PERSCoordinacion.Tareas)subtrabajo.getTarea(); 
       */    
    }else{
      /*
       * El subtrabajo está terminado, solamente van a exportarse
       * los archivos de los resultados
       */
      this._cDirectorioLocal=this.subtrabajo.getRutasSalida();
      this.mpArchivos.clear();
      this.mpArchivos.putAll(subtrabajo.getArchivosSalida());
      this._mpArchivosACargar.clear();
      this._mpArchivosACargar.putAll(this.mpArchivos);
    }
  }
}