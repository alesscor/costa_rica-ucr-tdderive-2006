package admin;
import mens.MENSIComandos;
import oact.*;
import orgainfo.*;

import java.io.*;

import tdutils.*;
import tdutils.Invocable;
import aco.*;

import java.util.*;
/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0<br></br>
 */ 
/**Implementa el manejo de una solicitud de tarea a ser 
 * manejada por el sistema.<br>
 * Registra en el sistema todo lo que tiene que ver con la tarea
 * solicitada y datos relacionados.<br>
 * Esta clase se llamaba antes admin.LEESolicitudAbs.
 */
public class ADMINAPPMetodoAtencion extends ADMINAPPMetodoDespAbs {
  final String bloqueo_respuesta="bloqueo_respuesta";
  public ADMINAPPMetodoAtencion() {
  }
  public ADMINAPPMetodoAtencion(OACTSirvienteAbs sirviente0) {
    super(sirviente0);
  }
  public ADMINAPPMetodoAtencion(String servantID) {
    super(servantID);
  }
  /**
   * Lleva a cabo preparaciones para ejecutar el método del sirviente
   * en el método ejecutar(), además de hacer la recepción de eventuales
   * resultados de subtrabajos.
   * @return Si se puede dar inicio a la ejecución. De lo contrario
   * ésta (la ejecución del método invocado) no se realiza y se
   * llamada en cambio a ejecutarFin con parámetros null y false.
   * @throws ADMINExcepcion Si hay error.<br>
   * <b>Estado de los archivos (fisico y el lógico que les corresponde)</b>
   * <pre>
   * +--------------------+--------------------+
   * | Estado físico      | Estado lógico*     |
   * +--------------------+--------------------+
   * | ausente            | ausente            |
   * | partido            | importando         |
   * | unido y comprimido | presente           |
   * | descomprimido      | listo              |
   * +--------------------+--------------------+
   * __________________________________
   * *El estado lógico se da al verificar el estado físico
   * de los archivos y es registrado en la base de datos con
   * el fin de no hacer verificaciones físicas.
   * </pre>
   * <li>Si un archivo está listo, no se modifica.</li>
   * <li>Si un archivo está presente, se descomprime y se pone listo</li>
   * <li>Si un archivo no está en uno de los estados anteriores,
   * entonces se borra y se impone el contenido importado.</li>
   * <b>Estado del solicitante<b>
   * <pre>
   * +------------------------+---------------------------------------------+
   * | Estado                 | Situación                                   |
   * +------------------------+---------------------------------------------+
   * | SOLICITANTE_INICIO     | El solicitante está recién iniciado         |
   * |                        |                                             |
   * | SOLICITANTE_PREPTAREA  | El solicitante tiene una tarea registrada   |
   * |                        |                                             |
   * | SOLICITANTE_YATAREA    | El solicitante importó sus archivos         |
   * |                        |                                             |
   * | SOLICITANTE_LISTO      | El solicitante descomprimió sus archivos    |
   * |                        |                                             |
   * | SOLICITANTE_INVALIDO   | El solicitante, su tarea y sus archivos     |
   * |                        | tuvieron problemas al ser establecidos      |
   * |                        | para tdderive.                              |
   * +------------------------+---------------------------------------------+
   * </pre>
   */
  public boolean ejecutarInicio() throws ADMINGLOExcepcion{
    ADMINPOLPlanificador planificador=null;
    ADMINAPPControladores controlador=null;
    boolean bSiIniciado=false;
    int caso=0;
    /*
     * Esto para que la instancia de la tarea y del subtrabajo sean los mismos
     * respetar el estado de la tarea que ya tiene el servidor, pues el open se lo come
     */
    // tarea.setEstadoTarea(ADMINAPPITareas.TAREA_MARCHA); // ya no
    if(this.siSubtrabajo()){
      /*
       * para subtrabajos URGE hacer el controlador, si se espera hay problemas de
       * concurrencia y más de un hilo querrá hacer su controlador
       */
      planificador=info.getPlanificador();
      synchronized(planificador.objeto_bloqueo_decidiendo){
        controlador=planificador.nuevoControlador(new ADMINAPPTrabajos(tarea,solicitante));
      }
      this.tareaV=this.tarea;
      this.subtraV=this.subtrabajo;
//      controlador=this.info.getPlanificador().cargaControlador(this.tarea.getIdTarea());
//      if(controlador!=null){
        synchronized(controlador){
          tareaV=controlador.getTarea();
          if(tareaV!=null){
            this.subtraV=controlador.getSubtrabajo(this.subtrabajo.getIdSubtrabajo());
            if(subtraV==null){
              this.subtraV=this.subtrabajo;
              controlador._tarea.addSubtrabajo(this.subtraV);
            }
          }
        }
//      }else{
//      }
    }
    /*
     * pone estados al solicitante y a la tarea
     * <2005/> y al subtrabajo
     * [ ] Debe revisarse si la tarea o el subtrabajo ya están presentes.
     * [ ] Si existen el solicitante de la tarea siempre va a ser nuevo (importante
     *     para análisis de tiempos).
     */
    /*
     * <2006/> Toqueteo para considerar los keepalive.
     */
    caso=this.revisaConsistencia();
    if(caso==0){
      // mejor aquí no porque la tarea aún no tiene id
      // info.println("despachador","recibe solicitud tarea: " + tarea.getIdTarea());
    }
    if(caso==2 && (!this.siSubtrabajo())){
      // la tarea ya estaba solicitada
      return true;
    }
    /*
     * en todo caso, debe saberse si el subtrabajo es importado
     * para ponerle el controlador remoto que le corresponde (el
     * nombre del nodo actual)
     */
    if(this.siSubtrabajo() && !subtraV.siLocal()){
      // debe escribirse el nombre del servidor actual
      subtraV.setContRemoto(this.info.getComputadora().
          getNombre());
    }
    if(this.siSubtraSolicitud() && this.subtrabajo.hashCode()!=this.subtraV.hashCode()){
      // el subtrabajo se repite
      if(this.subtraV.getEstadoSubtrabajo().compareTo(ADMINAPPISub_trabajos.SUBTRA_EXTERNO)==0){
        // para que los muertos no revivan
        this.subtraV.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_ESPERA);
      }
      // this.tareaV.loadPrograma();
      this.subtraV.write();
      try {
        info.getConex().dbCommit();
      } catch (OIExcepcion e1) {
        e1.printStackTrace();
      }
      /*
       * debe indicarse al solicitante que ya tiene los datos
       * y que no hace falta que se los exporte
       */
      info.println("despachador","recibe subtrabajo a planificarr: " + tareaV.getIdTarea()+":"+subtraV.getIdSubtrabajo());
      try {
        this.omiteImportacion();
      } catch (ADMINGLOExcepcion e2) {
        e2.printStackTrace();
      } catch (OIExcepcion e2) {
        e2.printStackTrace();
      }
      return true;
    }
    if(caso==3){
      // la tarea y el subtrabajo ya estaban listos
      // debe revisarse si se está devolviendo el resultado
      // de un subtrabajo...
      // o si estamos ante un keepalive.
      if(this.siSubtrabajoFin()){
        // YUPI!!!!
        recibeResultado();
        info.println("despachador","recibe subtrabajo terminado: " + tareaV.getIdTarea()+":"+subtraV.getIdSubtrabajo());
        try {
          info.recibeMensajeDeNodo(this.getHostRemoto());
        } catch (OACTExcepcion e1) {
          e1.printStackTrace();
        }
        return true;
      }
      if(subtraV.siLocal() && this.siSigueVivo()){
        recibeSigueVivo();
        info.println("despachador","recibe subtrabajo sigue vivo: " + tareaV.getIdTarea()+":"+subtraV.getIdSubtrabajo());
        try {
          info.recibeMensajeDeNodo(this.getHostRemoto());
        } catch (OACTExcepcion e1) {
          e1.printStackTrace();
        }
        return true;
      }
    }
    if(this.siSubtrabajo()){
      info.println("despachador","recibe subtrabajo a planificar: " + tareaV.getIdTarea()+":"+subtraV.getIdSubtrabajo());
      try {
        info.recibeMensajeDeNodo(this.getHostRemoto());
      } catch (OACTExcepcion e1) {
        e1.printStackTrace();
      }
    }
    try {
      // el solicitante siempre es nuevo
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_INICIO);
      solicitante.setTarea(tarea);
      solicitante.write();
      if(caso==0 && !this.siSubtrabajo()){
        info.println("despachador","recibe solicitud tarea: (" + tarea.getAlias()+") " + tarea.getIdTarea());
      }
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_PREPTAREA);
      solicitante.setDesdeNombre(this.getHostRemoto());
      solicitante.setDesdePuerto(this.getPuertoRemoto());
      tarea.createDirs();
      tarea.loadPrograma();
      if(caso==2){
        /*
         * tarea presente mientras se solicita un subtrabajo ausente
         * califica al solicitante
         */
        solicitante.setTipoSol(ADMINAPPISolicitantes.SOLICITANTE_TIPOSUBTRABAJO);
      }
      solicitante.write();

      // <2005 Revisa si es un subtrabajo/>
      if(this.siSubtrabajo()){
        tareaV.setEstadoTarea(ADMINAPPITareas.TAREA_PREPARACION);
        subtraV.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_INICIO);
        /*
         * es un subtrabajo cuyo ambiente debe ser 
         * configurado por mientras la tarea ya está lista. 
         */
        tareaV.write();
        /*
         * ojo: el directorio del subtrabajo se debe acoplar
         * siempre a la configuración local del 
         * agente de tdderive, es por eso que tal dirección
         * no viene predeterminada para el subtrabajo en 
         * la solicitud html.
         */ 
        subtraV.setDirectorio(tareaV.getTareaDir()+"/"+
            subtraV.getIdSubtrabajo());
        subtraV.createDirs();
        // importa archivos para el subtrabajo
        if(this.importaArchivos()){
          subtraV.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_ESPERA);
          /*
           * hace la asociación entre el subtrabajo y 
           * sus archivos
           */
          subtraV.setArchivos(this.mpArchivos);
        }
        /*
         * se asegurar la persistencia de la relación 
         * entre el subtrabajo y sus archivos.
         */
        subtraV.write();
        tareaV.setEstadoTarea(ADMINAPPITareas.TAREA_MARCHA);
      }else{
        // no es subtrabajo
        /*
         * la tarea se pide por primera vez, es la coordinadora,
         * quien la da a conocer al resto del sistema.
         * De este hecho solamente hay certeza en este punto
         */
        tarea.setEstadoTarea(ADMINAPPITareas.TAREA_INICIO);
        this.tarea.setSiCoordina(true);
        if(!_si_local){
          // todos los archivos deben importarse
          this.importaArchivos();        
        }else{
          // todos los archivos son locales
          this.ubicaArchivos();
        }        
        tarea.setArchivos(this.mpArchivos);
        tarea.write();
      }
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_YATAREA);
      solicitante.write();
      descompArchivos();
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_LISTO);
      /*             |
       *             |
       *             V
       * realiza chequeos de instancias concretas de LEESolicitudAbs
       */
      revisaEstados();
      /*
       * el grupo de la solicitud debe ser el mismo código de la tarea.
       */
      solicitante.setIdGrupo(tarea.getIdTarea());
      solicitante.setRetornos(this.mpRetornos);
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_LISTO);
      solicitante.write();
      if(tareaV!=null){
        tareaV.write();
      }else{
        tarea.write();
      }
      // @FIXME escribir los retornos en la base de datos
      // @TODO Commit que debería ser el primero.
      info.getConex().dbCommit();
      bSiIniciado=true;
    }catch (ADMINGLOExcepcion ex) {
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_INVALIDO);
      bSiIniciado=false;
    }catch(OIExcepcion ex){
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_INVALIDO);
      bSiIniciado=false;
    }catch(OACTExcepcion ex){
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_INVALIDO);
      bSiIniciado=false;
    }
    try {
      solicitante.write(); 
    }catch (OIExcepcion e) {
      // ni modo, no se pudo hacer persistente el estado
    }
    return bSiIniciado;
  }
  /**
   * Lleva a cabo preparaciones para establecer el fin de un subtrabajo.
   * @return Si se puede dar inicio a la ejecución. De lo contrario
   * ésta (la ejecución del método invocado) no se realiza y se
   * llamada en cambio a ejecutarFin con parámetros null y false.
   * @throws ADMINExcepcion Si hay error.<br>
   * <b>Estado de los archivos (fisico y el lógico que les corresponde)</b>
   * <pre>
   * +--------------------+--------------------+
   * | Estado físico      | Estado lógico*     |
   * +--------------------+--------------------+
   * | ausente            | ausente            |
   * | partido            | importando         |
   * | unido y comprimido | presente           |
   * | descomprimido      | listo              |
   * +--------------------+--------------------+
   * __________________________________
   * *El estado lógico se da al verificar el estado físico
   * de los archivos y es registrado en la base de datos con
   * el fin de no hacer verificaciones físicas.
   * </pre>
   * <li>Si un archivo está listo, no se modifica.</li>
   * <li>Si un archivo está presente, se descomprime y se pone listo</li>
   * <li>Si un archivo no está en uno de los estados anteriores,
   * entonces se borra y se impone el contenido importado.</li>
   * <b>Estado del solicitante<b>
   * <pre>
   * +------------------------+---------------------------------------------+
   * | Estado                 | Situación                                   |
   * +------------------------+---------------------------------------------+
   * | SOLICITANTE_INICIO     | El solicitante está recién iniciado         |
   * |                        |                                             |
   * | SOLICITANTE_PREPTAREA  | El solicitante tiene una tarea registrada   |
   * |                        |                                             |
   * | SOLICITANTE_YATAREA    | El solicitante importó sus archivos         |
   * |                        |                                             |
   * | SOLICITANTE_LISTO      | El solicitante descomprimió sus archivos    |
   * |                        |                                             |
   * | SOLICITANTE_INVALIDO   | El solicitante, su tarea y sus archivos     |
   * |                        | tuvieron problemas al ser establecidos      |
   * |                        | para tdderive.                              |
   * +------------------------+---------------------------------------------+
   * </pre>
   */
  private void recibeResultado() throws ADMINGLOExcepcion{
    boolean bSiIniciado=false;
    try {
      // no podemos dejar botado al solicitante
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_INICIO);
      solicitante.write();
      solicitante.setTarea(tareaV);
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_PREPTAREA);
      solicitante.setDesdeNombre(this.getHostRemoto());
      solicitante.setDesdePuerto(this.getPuertoRemoto());
      tareaV.createDirs();
      tareaV.loadPrograma();
      solicitante.write();
      /*
       * es un subtrabajo cuyo ambiente debe ser 
       * configurado. Por mientras la tarea ya está lista. 
       */
      tareaV.write();
      /*
       * ojo: el directorio del subtrabajo se debe acoplar
       * siempre a la configuración local del 
       * agente de tdderive, es por eso que tal dirección
       * no viene predeterminada para el subtrabajo en 
       * la solicitud html.
       */ 
      subtraV.setDirectorio(tareaV.getTareaDir()+"/"+
          subtraV.getIdSubtrabajo());
      // crea directorios si no aún no están
      subtraV.createDirs();
      // importa archivos para el subtrabajo
      if(!this.importaArchivos()){
        return;
      }
      /*
       * hace la asociación entre el subtrabajo y 
       * sus archivos resultantes
       */
      subtraV.setArchivos(this.mpArchivos);
      /*
       * se asegurar la persistencia de la relación 
       * entre el subtrabajo y sus archivos.
       */
      subtraV.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_FIN);
      subtraV.write();
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_YATAREA);
      solicitante.write();
      descompArchivos();
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_LISTO);
      /*             |
       *             |
       *             V
       * realiza chequeos de instancias concretas de LEESolicitudAbs
       */
      revisaEstados();
      /*
       * el grupo de la solicitud debe ser el mismo código de la tarea.
       */
      solicitante.setIdGrupo(tarea.getIdTarea());
      solicitante.setRetornos(this.mpRetornos);
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_LISTO);
      solicitante.write();
      tareaV.write();
      subtraV.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_ENTREGADO);
      subtraV.write();
      info.getConex().dbCommit();
      bSiIniciado=true;
    }catch (ADMINGLOExcepcion ex) {
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_INVALIDO);
      bSiIniciado=false;
    }catch(OIExcepcion ex){
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_INVALIDO);
      bSiIniciado=false;
    }catch(OACTExcepcion ex){
      solicitante.setEstadoSolicitante(ADMINAPPISolicitantes.SOLICITANTE_INVALIDO);
      bSiIniciado=false;
    }
    try {
      solicitante.write(); 
    }catch (OIExcepcion e) {
      // ni modo, no se pudo hacer persistente el estado
    }
  }
  /**
   * Lleva a cabo las acciones pertinentes para que un
   * trabajo se declare como vivo.<br/>
   * (Por ahora es solamente escribir en la base de datos
   * el estado que tiene que ver con cont_externo.)
   *
   */
  private void recibeSigueVivo(){
    try {
      this.subtraV.write();
      info.getConex().dbCommit();
    } catch (ADMINGLOExcepcion e) {
      e.printStackTrace();
    } catch (OIExcepcion e) {
      e.printStackTrace();
    }
  }
  
  /**
   * <2005 />
   * Verifica si la tarea o el subtrabajo ya están presentes.<br/>
   * Si existen, debe recuperarse el solicitante de la tarea.<br/>
   * Si es un subtrabajo de una tarea ya presente, el estado del 
   * solicitante no debe tocarse.
   * <pre>
   * +-----+----------+-------------+
   * |tarea|subtrabajo|     caso    |
   * +-----+----------+-------------+
   * |  0  |    0     |      0      |
   * +-----+----------+-------------+
   * |  0  |    1     |    1(n/a)   |
   * +-----+----------+-------------+
   * |  1  |    0     |      2      |
   * +-----+----------+-------------+
   * |  1  |    1     |      3      |
   * +-----+----------+-------------+
   * </pre>
   * @return <div>Caso a tomar en cuenta: 0 tarea no presente, 
   * 1 imposible, 2 tarea presente con subtrabajo no presente, 3
   * tarea y subtrabajo presentes.
   * </div>
   */
  private int revisaConsistencia(){
    return tarea.revisaConsistencia(subtrabajo);
  }
  /**
   * Importa los archivos de una tarea solicitada y los guarda en
   * forma volátil en la lista de archivos.
   * @throws ADMINExcepcion
   * @throws OIExcepcion
   * <li>Realiza los chequeos documentados por ejecutarInicio().</li>
   * <li>Cambia estados de los archivos: de "Ausente" a "Listo".</li>
   * @deprecated Mejor utilizar importaArchivos, debido a que ahora se
   * cuenta con un canal de comunicacion dedicado a la transmisión de datos.
   */
  protected void importaArchivosOld() throws OIExcepcion,ADMINGLOExcepcion{
    int medida=0;
    byte[] contenidoI=null;
    PERSCoordinacion.Archivos archivo0,archivo00=null;
    Iterator itr;
    itr=mpArchivos.values().iterator();
    Map mpArchListos,mpArchPresentes,mpArchivosErroneos;
    mpArchListos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    mpArchPresentes=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    mpArchivosErroneos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    tarea.loadArchivosListos(mpArchListos);
    tarea.loadArchivosPresentes(mpArchPresentes);
    try {
      /*
       * Solamente va a haber un archivo.
       */
      if(itr.hasNext()){
        archivo0=(PERSCoordinacion.Archivos)itr.next();
        archivo0.setTarea(this.tarea);
        if(mpArchListos.get(archivo0.getNombre())!=null){
          // el archivo ya está listo, no se toca
          this.send(solicitante.getIdParcial()+tarea.getIdTarea()+
          SOLICITUD_SEPARADOR+SOLICITUD_NOENVIAR);
        }else{
          // el archivo no está listo
          if(mpArchPresentes.get(archivo0.getNombre())!=null){
            // el archivo está sin descomprimir
            this.send(solicitante.getIdParcial()+tarea.getIdTarea()+
            SOLICITUD_SEPARADOR+SOLICITUD_NOENVIAR);
          }else{            
            if(mpArchivosErroneos.get(archivo0.getNombre())!=null){              
              // TODO Archivo con error debe poderse enviar de nuevo.
              this.send(solicitante.getIdParcial()+tarea.getIdTarea()+
              SOLICITUD_SEPARADOR+SOLICITUD_NOENVIAR);
            }else{
              // el archivo no se encuentra y se necesita importar
              this.send(solicitante.getIdParcial()+tarea.getIdTarea()+
              SOLICITUD_SEPARADOR+SOLICITUD_ENVIAR);
              archivo0.setEstadoArchivo(ADMINAPPIArchivos.ARCHIVO_IMPORTANDO);
              /*
               * TODO Debe esperar el número de puerto para el enlace de datos
               * usado para el intercambio de archivos.
               */
              
              archivo0.write();
              try {
                contenidoI=new byte[10*1024]; // 10KB
                while((medida=this.receiveb(contenidoI))>=0){
                  archivo0.setContenido(contenidoI,medida);
                  archivo0.write();
                  System.err.println("\nSe han escrito "+medida+" bytes.\n");
                  
                } 
              }catch (ADMINGLOExcepcion ex) {
                // no se pudo escribir archivo (¿?)
                // detiene la importación
                mpArchivosErroneos.put(
                    archivo0.getNombre(),archivo0.getNombre());              
                archivo0.setContenido(null);
                archivo0.setEstadoArchivo(ADMINAPPIArchivos.ARCHIVO_IMPORTANDO);
                archivo0.write();
              }
            }
          }
        }
      }
      this.info.getConex().dbCommit();
      mpArchListos.clear();
      mpArchListos=null;
      mpArchPresentes.clear();
      mpArchPresentes=null;
      mpArchivosErroneos.clear();
      mpArchivosErroneos=null;
      itr=null;
      archivo0=null;
      archivo00=null;
      this.closeComm();
    }catch (OACTExcepcion ex) {
      // errores de envío y recepción, se asume que no se puede
      // establecer contacto.
      if(mpArchListos!=null){
        mpArchListos.clear();
        mpArchListos=null;
      }
      if(mpArchPresentes!=null){
        mpArchPresentes.clear();      
        mpArchPresentes=null;
      }
      if(mpArchivosErroneos!=null){
        mpArchivosErroneos.clear();
        mpArchivosErroneos=null;
      }
      itr=null;
      archivo0=null;
      archivo00=null;
      try {
        this.closeComm();
      }
      catch (OACTExcepcion ex1) {
      }
      throw new ADMINGLOExcepcion("Error en importación de archivos.",ex);
    }
    catch (ACONExcArbitraria ex) {
      ex.printStackTrace();
    }
    catch (ACONExcOmision ex) {
      ex.printStackTrace();
    }
    catch (ACONExcTemporizacion ex) {
    }
  }
  /**
   * Pide que se omita la importación de archivos.
   * @throws OIExcepcion
   * @throws ADMINGLOExcepcion
   */
  protected void omiteImportacion() throws OIExcepcion,ADMINGLOExcepcion{
    String sIndicacion="";
    try {
      if(true){
        /*
         * no importa archivos, pide que no se envíen
         */
        if(this.siSubtrabajo()){
          this.send(solicitante.getIdParcial()+tarea.getIdTarea()+subtraV.getIdSubtrabajo()+
              SOLICITUD_SEPARADOR+SOLICITUD_NOENVIAR);
        }else{
          this.send(solicitante.getIdParcial()+tarea.getIdTarea()+
              SOLICITUD_SEPARADOR+SOLICITUD_NOENVIAR);
        }
      }
      }catch(Exception ex){
      }
      // this.send(SOLICITUD_CERRAR);
  }  
  /**
   * Importa los archivos de una tarea solicitada y los guarda en
   * forma volátil en la lista de archivos.
   * @throws ADMINExcepcion
   * @throws OIExcepcion
   * <li>Realiza los chequeos documentados por ejecutarInicio().</li>
   * <li>Cambia estados de los archivos: de "Ausente" a "Listo".</li>
   */
  protected final boolean importaArchivos() throws OIExcepcion,ADMINGLOExcepcion{
    boolean res=false;
    int medida=0,puertoremoto=0;
    byte[] contenidoI=null;
    String sIndicacion="";
    Map mpArchListos,mpArchPresentes,mpArchivosErroneos,mpEsperas;
    Iterator itr;
    ACONDescriptor desc;
    ACONConectorDesp conec=new ACONConectorDesp();
    PERSCoordinacion.Archivos archivo0;
    ACONGestor gestor;
    itr=mpArchivos.values().iterator();
    mpArchListos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    mpArchPresentes=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    mpArchivosErroneos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    mpEsperas=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    if(this.siSubtrabajo()){
      if(this.siSubtrabajoFin()){
        subtraV.loadArchivosSalidaListos(mpArchListos);
        subtraV.loadArchivosSalidaPresentes(mpArchPresentes);
      }else{
        subtraV.loadArchivosListos(mpArchListos);
        subtraV.loadArchivosPresentes(mpArchPresentes);
      }
    }else{
      tarea.loadArchivosListos(mpArchListos);
      tarea.loadArchivosPresentes(mpArchPresentes);
    }
    // @TODO considerar carga de archivos corruptos
    try {
      /*
       * decide si importa archivos o no
       */
      if(mpArchivos.size()>0){
        /*
         * importa archivos, pide inicio de envío
         */
        if(this.siSubtrabajo()){
          this.send(solicitante.getIdParcial()+tarea.getIdTarea()+subtraV.getIdSubtrabajo()+
              SOLICITUD_SEPARADOR+SOLICITUD_ENVIAR);
        }else{
          this.send(solicitante.getIdParcial()+tarea.getIdTarea()+
              SOLICITUD_SEPARADOR+SOLICITUD_ENVIAR);
        }
      }else{
        /*
         * no importa archivos, pide que no se envíe
         */
        if(this.siSubtrabajo()){
          this.send(solicitante.getIdParcial()+tarea.getIdTarea()+subtraV.getIdSubtrabajo()+
              SOLICITUD_SEPARADOR+SOLICITUD_NOENVIAR);
        }else{
          this.send(solicitante.getIdParcial()+tarea.getIdTarea()+
              SOLICITUD_SEPARADOR+SOLICITUD_NOENVIAR);
        }
      }
      /*
       * [0] Recibe puerto del exportador de archivos
       * que ya espera pasivamente las conexiones
       */
      try{
        this.setTiempoEspera((int)info.getPesoUmbralPolitica(
            "DESPA_ESPERA_DETALLEIMPORT"));
      }catch(ADMINGLOExcepcion ex){
        this.setTiempoEspera(TIEMPOESPERA_LECTURA);
      }
      sIndicacion=this.receive();
      try{
        puertoremoto=Integer.parseInt(sIndicacion);
        if(puertoremoto==0){
          System.err.println("El puerto remoto es cero.");
        }
      }catch(Exception ex){
        System.err.println("El puerto remoto es cero.");
        puertoremoto=0;
      }
      System.out.println("Usará el puerto: "+puertoremoto+".");
      if(puertoremoto!=0){
        /*
         * [1] Ciclo de importación de archivos. Se importa
         *     archivo por archivo utilizando un hilo diferente.
         */
        while(itr.hasNext()){
          archivo0=(PERSCoordinacion.Archivos)itr.next();
          /*
           * los archivos adquieren 'personalidad' como
           * parte de una tarea o de un subtrabajo
           * 
           */
          if(this.siSubtrabajo()){
            System.out.println("----------- Prepara importación 1 "+this.subtraV.toString()+ " -----------");
            archivo0.setSubTrabajo(this.subtraV);
            archivo0.setSiEntrada(!this.subtrabajo.getSiFin());
          }else{
            archivo0.setTarea(this.tarea);
          }
          if(mpArchListos.get(archivo0.getNombre())!=null){
            // el archivo ya está listo, no se toca
          }else{
            // el archivo no está listo
            if(mpArchPresentes.get(archivo0.getNombre())!=null){
              // el archivo está sin descomprimir
            }else{            
              if(mpArchivosErroneos.get(archivo0.getNombre())!=null){              
                // @TODO Archivo con error debe poderse mandar de nuevo.
              }else{
                System.out.println("----------- Prepara importación 2 -----------");
                // el archivo no está
                archivo0.write();
                this.info.getConex().dbCommit();
                desc=new ACONDescriptor();
                desc.localport=0;
                desc.remoteport=puertoremoto;
                desc.remotehost=this.getHostRemoto();
                desc.socket_type=ACONDescriptor.STREAM;
                desc.aoNavegables=new Object[]{this.info,archivo0};
                /*
                 * se hace un hilo por cada archivo a importar 
                 */
                desc.wait=false;
                try{
                  desc.tiempoConexion=(int)info.getPesoUmbralPolitica(
                      "DESPA_ESPERA_IMPORTACIONCONEX");
                }catch(ADMINGLOExcepcion ex){
                  desc.tiempoConexion=TIEMPOESPERA_CONEXION;
                }
                try{
                  desc.tiempoEspera=(int)info.getPesoUmbralPolitica(
                      "DESPA_ESPERA_ARCHIVOIMPORT");
                }catch(ADMINGLOExcepcion ex){
                  desc.tiempoEspera=TIEMPOESPERA_LECTURA;
                }
                gestor=new ADMINGLOGestionImportadora(desc);
                conec.conecta(gestor);
                mpEsperas.put(archivo0.getNombre(),gestor);
                res=true;
              }
            }
          }
        }
      }else{
        /*
         * se recibió un puerto inválido para el canal de
         * transmisión de datos
         */
         System.err.println("Error en el puerto del canal de " +          "transmisión de datos.");
      }
      /*
       * [2] cierra el extremo pasivo exportador 
       */
      if(mpEsperas.size()>0){        
        /*
         * espera a que todos los hilos hayan terminado de 
         * importar
         */
        itr=mpEsperas.values().iterator();
        while(itr.hasNext()){
          gestor=(ACONGestor)itr.next();
          if(gestor.esFin()){
            // camina simplemente          
          }
        }
      }
      this.send(SOLICITUD_CERRAR);
      /*
       * [3] determina si debe esperar el resultado de la tarea
       */
      if(_si_esperar){
        /*
         * [3.1] al sistema lo están esperando
         */
      }else{
        /*
         * [3.2] al sistema no lo están esperando
         */
      }
      this.info.getConex().dbCommit();
      mpArchListos.clear();
      mpArchListos=null;
      mpArchPresentes.clear();
      mpArchPresentes=null;
      mpArchivosErroneos.clear();
      mpArchivosErroneos=null;
      itr=null;
      archivo0=null;
    }catch (OACTExcepcion ex) {
      // errores de envío y recepción, se asume que no se puede
      // establecer contacto.
      if(mpArchListos!=null){
        mpArchListos.clear();
        mpArchListos=null;
      }
      if(mpArchPresentes!=null){
        mpArchPresentes.clear();      
        mpArchPresentes=null;
      }
      if(mpArchivosErroneos!=null){
        mpArchivosErroneos.clear();
        mpArchivosErroneos=null;
      }
      itr=null;
      archivo0=null;
      throw new ADMINGLOExcepcion("Error en importación de archivos.",ex);
    }
    catch (ACONExcArbitraria ex) {
      ex.printStackTrace();
    }
    catch (ACONExcOmision ex) {
      ex.printStackTrace();
    }
    catch (ACONExcTemporizacion ex) {
      ex.printStackTrace();
    }
    return res;
  }  
  private void limpiaBloques(){
    String oldName="";
    PERSCoordinacion.Archivos archivo=null;
    Iterator itr=null;
    itr=this.mpArchivos.entrySet().iterator();
    Map.Entry pareja=null;
    while(itr.hasNext()){
      pareja=(Map.Entry)itr.next();
      archivo=(PERSCoordinacion.Archivos)pareja.getValue();
      if(archivo.getNombre().compareToIgnoreCase(oldName)==0){
        itr.remove();
      }
      oldName=archivo.getNombre();
    }
  }
  /**
   * Copia los archivos a utilizar por una aplicación, en el directorio de 
   * trabajos de tdderive, comprime tales archivos y los divide dejando los 
   * resultados de ambas acciones en sus directorios correspondientes.
   * <li>Registra los archivos de la tarea.</li>
   * <li>Si se debe exportar se crea un directorio cuyo nombre será
   * <tt>"tarea"+MilisegundosDesde1ºEnero1970</tt>.</li>
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
    PERSCoordinacion.Archivos archivoI, archivoII = null;
    File fArchivo = null;
    Iterator itr;
    Map mpArchivosACargar=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    mpArchivosACargar.putAll(mpArchivos);
    mpArchivos.clear();
    itr = mpArchivosACargar.values().iterator();
    while (itr.hasNext()) {
      archivoI = (PERSCoordinacion.Archivos) itr.next();
      cNombreArchivo=archivoI.getRutaOriginal();
      fArchivo=new File(cNombreArchivo);
      // el archivo o directorio sí existe
      // se comprime en directorio de comprimidos
      cNombreComprimido = tarea.getComprimidosDir() + "/" +
          archivoI.getNombre();
      try{
        Zip.zipFile(cNombreComprimido,
                            cNombreArchivo);
      }catch(IOException ex){
        throw new ADMINGLOExcepcion("No se pudo comprimir archivo.",ex);
      }
      archivoII = new PERSCoordinacion.Archivos(this.info);
      archivoII.setNombre(archivoI.getNombre());
      archivoII.setInfoArchivo(archivoI.getInfoArchivo());
      archivoII.setSiLocal(false);
      archivoII.setEstadoArchivo(ADMINAPPIArchivos.ARCHIVO_PRESENTE);        
      // prepara lista de archivos a exportar
      mpArchivos.put(archivoII.getNombre(),archivoII);
    }
  }
  /**
   * Descomprime los archivos de la tarea.
   */
  protected void descompArchivos() throws ADMINGLOExcepcion{
    String cNombreAnt="";
    Iterator itr;
    PERSCoordinacion.Archivos archI;
    Map mpArchListos,mpArchPresentes,mpArchAusentes;
    mpArchListos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    mpArchPresentes=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    if(this.siSubtrabajo()){
      if(this.siSubtrabajoFin()){
        subtraV.loadArchivosSalidaListos(mpArchListos);
        subtraV.loadArchivosSalidaPresentes(mpArchPresentes);
      }else{
        subtraV.loadArchivosListos(mpArchListos);
        subtraV.loadArchivosPresentes(mpArchPresentes);
      }
    }else{
//      // provisional [
//      try {
//        this.info.getConex().dbCommit();
//      } catch (OIExcepcion e) {
//        System.err.println("[Provisional] No pudo escribirse en la base de datos");
//      }
//      // provisional ]
      tarea.loadArchivosListos(mpArchListos);
      tarea.loadArchivosPresentes(mpArchPresentes);
    }
    itr=mpArchivos.values().iterator();
    while(itr.hasNext()){
      archI=(PERSCoordinacion.Archivos)itr.next();
      if(mpArchListos.get(archI.getNombre())!=null){
        // el archivo no se debe descomprimir, ya está listo.
      }else{
        if(mpArchPresentes.get(archI.getNombre())!=null){
          // el archivo se debe descomprimir.
          if(this.siSubtrabajo()){
            if(this.siSubtrabajoFin()){
//              Zip.unzipFile(subtrabajo.getComprimidosDir()+
//                  "/"+archI.getNombre(),
//                  subtrabajo.getResultadosDir());
              // tdutils.copiaArchivo()

              try {
                tdutils.copiaArchivo(new File(subtraV.getSubtrabajoDir()+"/"+ archI.getNombre()), new File(subtraV.getResultadosDir()+"/"+archI.getNombre()));
                // tdutils.copiaArchivo(new File(subtraV.getSubtrabajoDir()+"/"+ archI.getNombre()), new File(subtraV.getResultadosDir()+"/"+archI.getNombre()));
              } catch (IOException e) {
                e.printStackTrace();
              }

            }else{
//              Zip.unzipFile(subtrabajo.getComprimidosDir()+
//                  "/"+archI.getNombre(),
//                  subtrabajo.getSubtrabajoDir());
            }
          }else{
            Zip.unzipFile(tarea.getComprimidosDir()+
                "/"+archI.getNombre(),
                tarea.getTareaDir());
          }
          try {
            archI.setEstadoArchivo(ADMINAPPIArchivos.ARCHIVO_LISTO);
            archI.write();
          }
          catch (ADMINGLOExcepcion ex) {
          }
        }else{
          // el archivo está ausente
          archI.setEstadoArchivo(ADMINAPPIArchivos.ARCHIVO_AUSENTE);
          try {
            archI.write();
          }
          catch (ADMINGLOExcepcion ex1) {
          }
          continue;
        }
      }
    }
  }
  /**
   * Dependiendo del retorno controla el fin de la aplicación o la devolución
   * de valores al usuario.
   */
  public void ejecutarFin(OACTSolicitud futuro,boolean siiniciado) throws ADMINGLOExcepcion{
    /*
     * @TODO AD...Atencion.ejecutarFin: debe ser implementado. 
     */
  }
  /**
   * Llama a ejecutar la aplicación solicitada por un usuario.
   * <li>En el caso de haberse solicitado un subtrabajo, todos los 
   * datos sobre el mismo ya están registrados.</li>
   * <li>En el caso de haberse solicitado una aplicación, sus datos 
   * ya están en el sistema y todo está preparado para empezar la 
   * ejecución que le corresponde (partición en subtrabajos).</li>
   */
  public OACTSolicitud ejecutar() throws Exception {
    final String sIndicaRemota="mejor cierre";
    OACTFuturo futuro=null;
    if(!_si_esperar || this.siSubtrabajo()){
      /*
       * El cliente no espera, se cierra la conexión.
       */      
      this.closeComm();
      if(this.siSubtrabajo() && !this.siSigueVivo() && !this.siSubtrabajoFin()){
        /*
         * ojo, cualquiera se pone en espera,
         * aunque tenga error en sus archivos
         */
        // this.subtraV.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_ESPERA);        
      }
    }
    /* Listo para la ejecución de la tarea o del subtrabajo.
     * -  El el subtrabajo y la tarea necesitan un controlador.
     * -  La tarea necesita dividirse en subtrabajos.
     * @TODO Estos cambios son urgentes.
     * -> Antes debe buscarse si ya hay trabajo para la tarea,
     *    en ese caso el trabajo no se crea, sino que se le informa
     *    del subtrabajo que se está pidiendo.
     */
    if(this.tareaV==null){
      this.tareaV=this.tarea;
    }
    if(!this.siSubtrabajo()){
      this.getDespachador().ejecuta(
            new ADMINAPPTrabajos(tareaV,solicitante));
    }
    tareaV.write();
    if(this.siSubtrabajo()){
      subtraV.write();
    }
    info.getConex().dbCommit();
     /*
      * Finaliza el procesamiento de la tarea.
      */
    if(_si_esperar && !this.siSubtrabajo()){
      /*
       * Si se debe devolver el resultado.
       * TODO Final Final YUPI!!!!!!
       * - Poner la invocación del send con el archivo
       *   resultante en espera en una estructura dedicada,
       *   cuyo id sea el id de la aplicación (tarea).
       */
      futuro=new OACTFuturo("remiendo");
      MENSIComandos comando=new MENSIComandos(){
        public boolean continuar(){
          return false;
        }
        public boolean demorarse(){
          return true;
        }
        public boolean detenerse(){
          return false;
        }
      };
      futuro.setComando(comando);
      Invocable invocable=new Invocable(){
        private final ADMINAPPMetodoAtencion atencion=ADMINAPPMetodoAtencion.this;
        private final PERSCoordinacion.Tareas tarea=ADMINAPPMetodoAtencion.this.tarea;
        public void enviaContenidoArchivo(String nombreArchivo){
          long tiempoEspera=2;
          try {
            /*
             * primero el nombre del archivo
             */
            atencion.send(nombreArchivo);
            
            try {
              tiempoEspera=(long)ADMINAPPMetodoAtencion.this.info.getPesoUmbralPolitica("ESPERA_ENVIO_S");
            } catch (ADMINGLOExcepcion e1) {
              tiempoEspera=2;
              e1.printStackTrace();
            }
            atencion.espera(tiempoEspera*1000);
            /*
             * luego el contenido a como sea (binario)
             */
            synchronized(atencion.bloqueo_respuesta){
              atencion.mandaArchivo("",nombreArchivo);
              // atencion.bloqueo_respuesta.notify();
            }
          } catch (ACONExcepcion e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          } catch (OACTExcepcion e) {
            e.printStackTrace();
          }
        }
        public Object invoca(Object args[]) throws Exception{
          /*
           * poner código para que sIndicaRemota sea
           * el contenido del archivo que se va a
           * mandar como respuesta al usuario.
           */
          String archivoResultado=this.tarea.getArchivoResultado();
          if(archivoResultado!=null && archivoResultado!=""){
            this.enviaContenidoArchivo(archivoResultado);
          }else{
            atencion.sendb("error".getBytes());
          }
          /*
           * [4] cierra el canal de comunicaciones dedicado a la
           * transmisión de datos.
           */
          synchronized(ADMINAPPMetodoAtencion.this.bloqueo_respuesta){
            // ADMINAPPMetodoAtencion.this.bloqueo_respuesta.wait();
          }
          atencion.closeComm();
          return null;
        }
      };
      info.println("despachador","almacena conexión: " + this.tarea.getIdTarea());
      info.getPlanificador().registraInvocacion(this.tarea.getIdTarea(),invocable);
    }else{
      this.closeComm();
    }
    return futuro;
  }
  /**
   * Obtiene el despachador que funge como sirviente del control
   * de tareas de <tt>tdderive</tt>.
   * @return El despachador que funge como sirviente del control de tareas.
   */
  public ADMINAPPDespachador getDespachador(){
    return (ADMINAPPDespachador)(this.getSirviente());
  }
  protected void revisaEstados() {
  }
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
}