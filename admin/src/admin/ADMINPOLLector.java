package admin;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import tdutils.EscritorSalidas;
import tdutils.Invocable;
import tdutils.tdutils;

import admin.PERSAmbiente.Computadora;
/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.3.0
 */ 
/**
 * Se dedica a leer periódicamente información sobre los objetos 
 * persistentes para encontrar las siguientes situaciones:
 * <li>Computadora con alta o baja carga funcional.</li>
 * <li>Computadora con alta o baja carga de aplicación</li>
 * <li>Subtrabajos de encargo remotos demorados</li>
 * <li>Tareas sin atender.</li>
 * <li>Tareas de resultado no entregado.</li>
 * <li>Subtrabajos no iniciados.</li>
 * <br>En el caso de encontrar alguna de esas situaciones,
 * notifica al planificador detalles del caso para su resolución.</br>
 * <b>Algunas lecturas</b>
 <pre>
Consultas para el cálculo de la carga de aplicación
===================================================
Peso de subtrabajos en marcha
-----------------------------
[x]
select sum(su.carga) as cargas_tareas from envolturas as en 
inner join sub_trabajos as su on (en.id_tarea=su.id_tarea and en.id_subtrabajo=su.id_subtrabajo)  where en.si_actual=true

Peso de subtrabajos en espera (hay que sacar los que tienen un subtrabajo que 
-----------------------------  está activo)
[ ]
select sum(su.carga) as carga_tareas from sub_trabajos as su left join 
envolturas as en on (su.id_tarea=en.id_tarea) 
where su.id_subtrabajo!=en.id_subtrabajo

[x]
select sum(su.carga) as carga_tareas from sub_trabajos as su left join 
envolturas as en on (su.id_tarea=en.id_tarea) 
where su.id_subtrabajo=en.id_subtrabajo and (en.si_actual=null or en.si_actual=false)

Otra para lo anterior
---------------------
[ ]
select sum(su.carga) as carga_tareas from sub_trabajos as su left join 
envolturas as en on (su.id_tarea=en.id_tarea) 
where su.id_subtrabajo=en.id_subtrabajo and (en.si_actual=null or en.si_actual=false)

Peso de todos los subtrabajos
-----------------------------
[x]
select sum(carga) as carga_tareas from sub_trabajos

Tareas en marcha con su carga
-----------------------------
select su.id_tarea,sum(su.carga) as carga_tarea from envolturas as en 
inner join sub_trabajos as su on 
(en.id_tarea=su.id_tarea and en.id_subtrabajo=su.id_subtrabajo)  where en.si_actual=true group by su.id_tarea 

Tareas en espera con su carga
-----------------------------
[x]
select su.id_tarea,sum(su.carga) as carga_tarea from sub_trabajos as su 
left join envolturas as en on (su.id_tarea=en.id_tarea) 
where su.id_subtrabajo=en.id_subtrabajo and (en.si_actual=null or en.si_actual=false) group by su.id_tarea

Esbozo de registro ficticio en envolturas
-----------------------------------------
Registro falso en envolturas
insert into envolturas (id_parcial,id_padre,id_grupo,estado_control,
hora_solicitud,hora_inicio,hora_fin,pid_parcial,pid_padre,pid_grupo,si_actual,
alias,numero_confirmaciones,id_tarea,id_subtrabajo)values ('id1','id0','ig0',
'LOCO',0,0,0,'id1','id0','ig0',true,'programilla',1,'001mx.10000000004','0000000001') 
 </pre>
 **/

class ADMINPOLLector {
  /**
   * Indica tiene ligamen con la plataforma subyacente
   * teniendo la capacidad de leer su información.
   */
  public static final String ESTADO_INCRUSTADO="ESTADO_INCRUSTADO";
  /**
   * Indica que no tiene ligamen con la plataforma subyacente
   * teniendo la capacidad de leer su información.
   */
  public static final String ESTADO_SININCRUSTAR="ESTADO_SININCRUSTAR";
  public static final int INTERVALOms_OMISION=60*5*1000;
  private int _capacidad;
  private int _carga_aplicacion;

  private int _carga_funcional;
  /**
   * Registro de métodos a llamar.
   */
  private Map _registro;
  private boolean _si_abierto;
  /**
   * Instancia de computadora del proceso lector.
   */
  private PERSAmbiente.Computadora compuLector;
  private ADMINGLOInfo desc;
  private zzADMINDespachador despachador;
  private EscritorSalidas _escritor;
  /**
   * Intervalo de lectura de la CPU.
   */
  private int intervalo_lectura_cpu_ms;
  /**
   * Intervalo en milisegundos que el lector debe esperar antes de realizar
   * una nueva lectura de la información del sistema.
   */
  private int intervalo_ms;
  private String lector_estado;
  private ADMINPOLILectorBajoNivel lectorbn;
  /**
   * Número de lectura.
   */
  private int nLectura;  
  private ADMINPOLPlanificador planificador;
  /**
   * Hilo con el cual el lector realiza las lecturas del sistema.
   * <li>Las lecturas se realizan cada 'intervalo_ms' milisegundos.</li>
   */
  private Thread thHiloLector;
  
  static long capacidadDisco(String dirbase){
    long size = 1 * 1024 * 1024;
//    long gap = 1 * 1024 * 1024;
//    String nombre="big"+System.currentTimeMillis();
//    File file=new File(dirbase+"/"+nombre);
//    RandomAccessFile access = null ;
//    try {
//      access = new RandomAccessFile(file, "rws");
//      while (true) {
//        try {
//          access.setLength(size);
//          size += size ;
//          System.out.println("->Size: " + size );
//        }catch (IOException e) {
//          size -= gap;
//        }
//      }
//    } catch ( Exception ex ) {
//      System.out.println("->Exception: " + ex.getMessage());
//    } finally {
//      if (access != null) {
//        try { 
//          access.close(); 
//        } catch ( Exception e ) {
//        }
//      }
//    }
//    file.delete();
//    System.out.println(
//      "Capacity = "
//      + size
//      + " bytes = "
//      + (size / 1024)
//      + " kb = "
//      + (size / (1024 * 1024))
//      + " Mb");
    return size;
  }

  /**
   * @throws ADMINPOLExcepcion
   * 
   */
  private static ADMINPOLILectorBajoNivel traeLectorBN() throws 
                                    ClassNotFoundException,
                                    IllegalAccessException,
                                    InstantiationException, ADMINPOLExcepcion{
    ADMINPOLILectorBajoNivel bn=null;
    String sPropI="";
    Properties props;
    props=System.getProperties();
    sPropI=props.getProperty("os.name");
    if(sPropI==""){
      // problemas, tdderive no puede trabajar
      throw new ADMINPOLExcepcion("No se encuentra información " +
          "sobre el sistema operativo");
    }
    if(sPropI.indexOf("Windows")>=0){
      bn=(ADMINPOLILectorBajoNivel)Class.forName(
          LectorBajoNivelWIN32.class.getName()).newInstance();
    }
    if(sPropI.indexOf("Linux")>=0){
      bn=(ADMINPOLILectorBajoNivel)Class.forName(
          LectorBajoNivel_NIX.class.getName()).newInstance();
    }
    if(sPropI.indexOf("Unix")>=0){
      bn=(ADMINPOLILectorBajoNivel)Class.forName(
          LectorBajoNivel_NIX.class.getName()).newInstance();
    }
    if(sPropI.indexOf("Sun")>=0){
      bn=(ADMINPOLILectorBajoNivel)Class.forName(
          LectorBajoNivel_NIX.class.getName()).newInstance();
    }
    if(sPropI.indexOf("FreeBSD")>=0){
      bn=(ADMINPOLILectorBajoNivel)Class.forName(
          LectorBajoNivel_NIX.class.getName()).newInstance();
    }
    return bn;
  }
  public ADMINPOLLector(ADMINGLOInfo desc0){
    ADMINPOLILectorBajoNivel bn;
    thHiloLector=null;
    desc=desc0;
    _registro=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    try {
      lectorbn=traeLectorBN();
      lector_estado=ADMINPOLLector.ESTADO_INCRUSTADO;
    } catch (Exception e) {
      e.printStackTrace();
      lectorbn=null;
      lector_estado=ADMINPOLLector.ESTADO_SININCRUSTAR;
    }
    nLectura=0;
    _escritor=new EscritorSalidas("Lector");
  }
  /**
   * Calcula la capacidad de la computadora.
   * @return La capacidad de la computadora.
   * @throws ADMINGLOExcepcion
   */
  public double calcCapacidad(Computadora compu0) throws ADMINGLOExcepcion{
    return ADMINPOLValorador.calcCapacidad(compu0);
  }
  /**
   * Calcula la carga de aplicación de la computadora.
   * @return La carga de aplicación de la computadora.
   * @throws ADMINGLOExcepcion
   */
  public double calcCargaAplicacion(Computadora compu0) throws ADMINGLOExcepcion{
    return ADMINPOLValorador.calcCargaAplicacion(compu0);
  }
  /**
   * Calcula la carga funcional de la computadora.
   * @return La carga funcional de la computadora.
   * @throws ADMINGLOExcepcion
   */
  public double calcCargaFuncional(Computadora compu0) throws ADMINGLOExcepcion{
    return ADMINPOLValorador.calcCargaFuncional(compu0);
  }
  /**
   * Invoca una pareja objeto-método presente en el registro del lector.
   * @param llamable Invocación a realizar.
   * @return Si un nuevo hilo fue ejecutado con el método.
   */
  private boolean invocaMetodo(final Invocable llamable,
      final Computadora compuLectura){
    Runnable rnn;
    Thread hilo;
    rnn=new Runnable(){
      public void run(){
        try {
          llamable.invoca(new Object[]{compuLectura});
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    hilo=new Thread(rnn,
        "Lector.invoca."+llamable.hashCode());
    hilo.start();
    return true;
  }
  /**
   * Invoca asincrónicamente los métodos registrados en el lector.
   * Este método se ejecuta de forma asincrónica luego de 
   * terminar una lectura. Cada método registrado es invocado de
   * forma asincrónica.
   * @param compuLectura Valores de la computadora que el lector
   * ha encontrado y calculado.
   */
  private void invocaRegistrados(final Computadora compuLectura){
    Runnable rnn;
    Thread hilo;
    final Map registrados=_registro;
    rnn=new Runnable(){
      public void run(){
        Computadora compuI=null;
        synchronized(_registro){
          Iterator itr=registrados.values().iterator();
          while(itr.hasNext()){
            compuI=compuLectura.clonese();
            Invocable llamaI=(Invocable)itr.next();
            invocaMetodo(llamaI,compuLectura);
          }
        }
      }
    };
    hilo=new Thread(rnn,"Lector.invoca.registrados");
    hilo.start();
  }
  /**
   * Registra un método para su ejecución asincrónica cada vez
   * que el lector haya terminado con su trabajo.
   * @param llamada La llamada a invocar cuando se dé el evento de lectura.
   */
  public void registraMetodo(Invocable llamada){
    String id;
    synchronized(_registro){
      id=String.valueOf(llamada.hashCode());
      _registro.put(id,llamada);
    }
  }
  public void desregistraMetodo(Invocable llamada){
    String id;
    Invocable portaI;
    synchronized(_registro){
      id=String.valueOf(llamada.hashCode());
      portaI=(Invocable)_registro.remove(id);
    }
  }  
  /**
   * Realiza todas las lecturas para actualizar la instancia
   * de Computadora.
   * <li>Este método solamente debería ser llamado por
   * el hilo que se pone en marcha en <tt>open()</tt> cada
   * <tt>intervalo_ms</tt> milisegundos.</li> 
   */
  protected final void lee() throws ADMINGLOExcepcion {
    lee(this.compuLector);
  }
  /**
   * Carga la información leída de la computadora en un objeto de 
   * información.
   * @param compu0 Objeto para la información de la computadora.
   * @throws ADMINGLOExcepcion Si hay error en alguna lectura.
   */
  protected final void lee(Computadora compu0) throws ADMINGLOExcepcion{
    try {
      if(compu0.getMicroReloj()==0.0){
        /*
         * primera lectura
         * se supone que estos valores ya habían sido asignados
         * desde el inicio de tdderive.
         */
        compu0.setBusesTipo(compuLector.getBusesTipo());
        compu0.setMicroReloj(compuLector.getMicroReloj());
        compu0.setMicroCant(compuLector.getMicroCant());
        compu0.setDesplazaPuerto(desc.getDesplazaPuerto());
        if(lectorbn!=null){
          compu0.setMicroCant(lectorbn.getNumProcesadores());
          if(lectorbn.getCPUMegaHertz()>0){
            compu0.setMicroReloj(lectorbn.getCPUMegaHertz());
          }
        }
      }
      if (this.lectorbn==null){
        throw new ADMINGLOExcepcion("No se tiene la capacidad de leer " +
            "información de bajo nivel.");
      }
      //|
      //| lecturas de bajo nivel
      //|
      // los valores que vienen sí cambian
      // cpu
      compu0.setMicroLibre(1-(lectorbn.getCPUPorcentajeUso2(
          this.intervalo_lectura_cpu_ms)));
      // ram
      compu0.setMemCant(this.lectorbn.getMemoriaTotal());
      compu0.setMemLibre(1-(lectorbn.getMemoriaUsada()/
          this.lectorbn.getMemoriaTotal()));
      // disco
      compu0.setDiscoCant(this.lectorbn.getDiscoTotal());
      compu0.setDiscoLibre(1-(lectorbn.getDiscoUsado()/
          this.lectorbn.getDiscoTotal()));
      //|
      //| lecturas a nivel de aplicación
      //| consultas SQL a la base de datos
      //|
      
      // pone a imprimir los valores leídos hasta ahora
      _escritor.escribeMensaje("Alias de esta computadora: "+compu0.getNombre());
      _escritor.escribeMensaje("Dirección de esta computadora: "+
          compu0.getDireccion());
      if(compu0.siComputadoraVirtual()){
        _escritor.escribeMensaje("Puerto base de esta computadora virtual: "+
            compu0.getDesplazaPuerto());
      }
      _escritor.escribeMensaje("Identificación de proceso: " + lectorbn.getIdentificacion());
      _escritor.escribeMensaje("Buses, tipo de: " + compu0.getBusesTipo());
      _escritor.escribeMensaje("Partición total (MB): "+compu0.getDiscoCant());
      _escritor.escribeMensaje("Partición libre (%): "+compu0.getDiscoLibre()*100.0);
      _escritor.escribeMensaje("Memoria total (MB): "+compu0.getMemCant());
      _escritor.escribeMensaje("Memoria libre (%): "+compu0.getMemLibre()*100.0);
      _escritor.escribeMensaje("CPUs, cantidad: "+compu0.getMicroCant());
      _escritor.escribeMensaje("CPU libre (%): "+compu0.getMicroLibre()*100.0);
      _escritor.escribeMensaje("CPU reloj (MHz): "+compu0.getMicroReloj());
      _escritor.escribeMensaje("Carga en marcha: "+compu0.getLocalActiva());
      _escritor.escribeMensaje("Carga en espera: "+compu0.getLocalEspera());
      _escritor.escribeMensaje("Vecinas usadas: "+compu0.getVecinasUsadas());
      _escritor.escribeMensaje("Vecinas apoyadas: "+compu0.getVecinasApoyadas());
      // cálculos del valorador
      _escritor.escribeMensaje("Capacidad de la computadora: "+this.calcCapacidad(compu0));
      _escritor.escribeMensaje("Carga funcional de la computadora: "+this.calcCargaFuncional(compu0));
      _escritor.escribeMensaje("Carga de aplicación de la computadora: "+this.calcCargaAplicacion(compu0));
    } catch (ADMINPOLExcepcion e) {
      throw new ADMINGLOExcepcion("Error en lectura de capa subyacente.",e);
    }
  }  
  
  /**
   * Cierra el objeto lector.
   */
  void close() {
    Iterator itr;
    tdutils.PortaMetodos portaI;
    // pone al objeto en estado cerrado
    _si_abierto=false;
    // cierra el hilo lector
    this.thHiloLector.interrupt();
    if(_registro!=null){
      synchronized(_registro){
        itr=_registro.values().iterator();
        while(itr.hasNext()){
          portaI=(tdutils.PortaMetodos)itr.next();
          portaI.limpia();
        }
        _registro.clear();
      }
    }
  }

  Computadora getComputadora(){
    return compuLector;
  }
  final int getIntervalo_ms() {
    return intervalo_ms+intervalo_lectura_cpu_ms;
  }
  int getNLectura(){
    return nLectura;
  }  
  /**
   * Abre el objeto lector, que realizará primero una lectura y luego,
   * en otro hilo y cada <tt>intervalo_ms</tt> segundos va a realizar
   * lecturas y actualizaciones al objeto compu.
   *
   */
  void open(){
    Runnable rnn;
    this.compuLector=desc.getComputadora().clonese();
    _si_abierto=true;
    rnn=new Runnable(){
      public void run(){
        do{
          try{
            // realiza todas las lecturas
            synchronized(desc.objeto_bloqueo_observalector){
              nLectura++;
              _escritor.escribeMensaje("Lector realizando su lectura periódica.");
              lee(compuLector);
              desc.objeto_bloqueo_observalector.wait(intervalo_ms);
              desc.objeto_bloqueo_observalector.notify();
            }
            // llama los eventos registrados sin parámetros
            invocaRegistrados(compuLector);
          }catch(InterruptedException e){
            // hilo interrumpido.
          } catch (ADMINGLOExcepcion e) {
            e.printStackTrace();
          }
        }while(_si_abierto);
      }
    };
    // asigna el hilo de espera
    thHiloLector=new Thread(rnn,this.getClass().getName());
    thHiloLector.start();
  }
  final void setIntervaloms(int intervalo_ms0) {
    // el intérvalo de lecturas de cpu es el 0.8333% del
    // intérvalo de lecturas
    intervalo_lectura_cpu_ms=(int)((double)intervalo_ms0*(5.0/100));
    this.intervalo_ms = intervalo_ms0-intervalo_lectura_cpu_ms;
    
  }
  boolean siAbierto(){
    return _si_abierto;
  }
  /**
   * Indica si la carga de aplicación de la computadora es alta.
   * @return Si la carga funcinal es alta.
   */
  boolean siCargaAplicacionAlta(){
    boolean cargaAlta=false;
    return cargaAlta;
  }
  /**
   * Indica si la carga de aplicación de la computadora es baja.
   * @return Si la carga funcinal es baja.
   */
  boolean siCargaAplicacionBaja(){
    boolean cargaBaja=false;
    return cargaBaja;
  }
  /**
   * Indica si la carga funcional de la computadora es alta.
   * @return Si la carga funcinal es alta.
   */
  boolean siCargaFuncionalAlta(){
    boolean cargaAlta=false;
  	return cargaAlta;
  }
  /**
   * Indica si la carga funcional de la computadora es baja.
   * @return Si la carga funcinal es baja.
   */
  boolean siCargaFuncionalBaja(){
    boolean cargaBaja=false;
    return cargaBaja;
  }
        
  /**
   * Obtiene una lista de subtrabajos en espera.
   * @return La lista en un map.
   */
  Map subtrabajosEspera(){
    Map lista_espera=null;
    return lista_espera;
  }
  /**
   * Obtiene una lista de subtrabajos locales demorados.
   * @return La lista en un map.
   */
  Map subTrabajosLocalesDemorados(){
    Map lista_demorados=null;
    return lista_demorados;
  }

  /**
   * Obtiene una lista de subtrabajos de encargo remoto demorados.
   * @return La lista en un map.
   */
  Map subTrabajosRemotosDemorados(){
    Map lista_demorados=null;
    return lista_demorados;
  }
  /**
   * Obtiene una lista de tareas sin atender.
   * @return La lista en un map.
   */
  Map tareasSinAtender(){
    Map lista_sinatender=null;
    return lista_sinatender;
  }
  /**
   * Obtiene una lista de tareas sin retornos entregados.
   * @return La lista en un map.
   */
  Map tareasSinRetorno(){
    Map lista_sinretorno=null;
    return lista_sinretorno;
  }
  /**
	 * <p>Title: <b>admin</b>:: admin</p>
	 * <p>Description: ADMINPOLLector.java.</p>
	 * <p>Copyright: Copyright (c) 2004</p>
	 * <p>Company: UCR - ECCI</p>
	 * <br>@author Alessandro</br>
	 * <br>@version 1.0</br>
	 * <br><b>LectorBajoNivel_NIX</b></br>
	 * <br>Lector de datos de bajo nivel para sistemas *NIX.</br>
	 */
  public static class LectorBajoNivel_NIX implements ADMINPOLILectorBajoNivel{
    private String dispositivoDir="";
    private File fdProc;
    private long identificacion;
    private double lectorCPUMhz=0.0;
    private double memoriaTotal=0.0;
    private String monturaDir="";
    private int numProcesadores=0;
		public LectorBajoNivel_NIX() {
    }
    /**
     * Trata que todo se cierre.
     * @see admin.ADMINPOLILectorBajoNivel#close()
     */
    public void close() throws ADMINPOLExcepcion {
    }
    public double getCPUMegaHertz() throws ADMINPOLExcepcion{
      String sTupla="";
      BufferedReader raMicroprocesador=null;
      int idx=-1;
      try {
        raMicroprocesador=new BufferedReader(
            new FileReader(PROCCPU));
      } catch (FileNotFoundException e) {
        throw new ADMINPOLExcepcion("No se pudo leer archivo " +
            "con información de la CPU.",e);
      }
      if(lectorCPUMhz==0){
        try {
          while((sTupla=raMicroprocesador.readLine())!=null){
            if(sTupla.indexOf("cpu MHz")>=0){
              idx=sTupla.indexOf(":");
              if(idx<0){
                // no hay información sobre velocidad de CPU
                return -1;
              }
              idx+=2;
              lectorCPUMhz=Double.parseDouble(sTupla.substring(idx));
            }
          }
          raMicroprocesador.close();
          raMicroprocesador=null;
        } catch (IOException e) {
          e.printStackTrace();
          throw new ADMINPOLExcepcion("No se pudo leer archivo " +
              "con información de la CPU.",e);
        } catch (Exception e) {
          e.printStackTrace();
          throw new ADMINPOLExcepcion("Error obteniendo información " +
              "de la CPU.",e);
        }
      }
      return lectorCPUMhz;
    }
    /**
     * Obtiene el uso de la CPU.
     * @see admin.ADMINPOLILectorBajoNivel#getCPUPorcentajeUso()
     */
    public double getCPUPorcentajeUso()throws ADMINPOLExcepcion{return 0.0;}
    /**
     * Obtiene el uso de la CPU.
     */
    public double getCPUPorcentajeUso2(long intervalo_precision)throws ADMINPOLExcepcion{
      /*
       * Por ahora devuelve el porcentaje usado por los procesos
       * que no son el proceso actual.
       * Lee los archivos /proc/stat y /proc/self/stat
       */
      long nCentProcModoUsuario=0,nCentProcModoKernel=0,nCentProcInicio=0,
        nCentModoUsuario=0,nCentModoKernel=0,nCentModoUBajaPrio=0,
        nCentProcOcupado=0,nCentTranscurrido=0,nCentProcCPU=0,nTOTALCPU=0;
      double usoCPU=0.0;
      String sTuplaProc="",sTupla="";
      String[] asTupla,asTuplaProc;
      BufferedReader brUso0=null,brUso1=null;
      try{
        brUso0=new BufferedReader(new FileReader(PROCCPUU0));
        brUso1=new BufferedReader(new FileReader(PROCCPUU1));
        sTupla=brUso0.readLine();
        sTuplaProc=brUso1.readLine();
        brUso0.close();
        brUso1.close();
        asTupla=sTupla.split(" ");
        asTuplaProc=sTuplaProc.split(" ");
        if(asTupla.length==0 || asTuplaProc.length<35){
          throw new ADMINPOLExcepcion("No se pudo leer información " +
              "sobre carga de CPU.");
        }
        nCentProcModoUsuario=Long.parseLong(asTuplaProc[13]);
        nCentProcModoKernel=Long.parseLong(asTuplaProc[14]);
        nCentProcInicio=Long.parseLong(asTuplaProc[21]);
        nCentProcCPU=nCentProcModoUsuario+nCentProcModoKernel;
        
        nCentModoUsuario=Long.parseLong(asTupla[1]);
        nCentModoKernel=Long.parseLong(asTupla[2]);
        nCentModoUBajaPrio=Long.parseLong(asTupla[3]);
        nCentTranscurrido=Long.parseLong(asTupla[4])+
            nCentModoUsuario+nCentModoKernel+nCentModoUBajaPrio;
        
        nCentProcOcupado=nCentTranscurrido-nCentProcInicio;
        
        if(nCentProcOcupado<0){
          throw new ADMINPOLExcepcion("Información de uso de CPU inválida.");
        }
        if(nCentProcOcupado==0){
          usoCPU=1.0;          
        }else{
          usoCPU=1.0-((double)nCentProcCPU/(double)nCentProcOcupado);
        }
      }catch(FileNotFoundException e){
        throw new ADMINPOLExcepcion("No se encontraron los archivos " +
            "con datos sobre carga de CPU.",e);
      } catch (Exception e) {
        throw new ADMINPOLExcepcion("No se pudo leer información " +
            "sobre carga de CPU.",e);
      }
      return usoCPU;
    }
    /**
     * Lee de la salida estándar del programa df.
     * @see admin.ADMINPOLILectorBajoNivel#getDiscoTotal()
     */
    public double getDiscoTotal() throws ADMINPOLExcepcion{
      String directorio="/tdderive";
      String sDispositivo="",sMontura="",sTupla="",sDispRaiz="",sMontRaiz="";
      long nTotal=0,nDisponible=0,nTotalRaiz=0,nDisponibleRaiz=0;
      String[] aTupla;
      Process comando=null;
      BufferedReader raMonturas=null;
      try {
        comando=Runtime.getRuntime().exec("df");
        comando.waitFor();
        raMonturas=new BufferedReader(new InputStreamReader(
            comando.getInputStream()));
        while((sTupla=raMonturas.readLine())!=null){
          aTupla=sTupla.split(" ");
          if(aTupla.length>5){
            sMontura=aTupla[5];
            sDispositivo=aTupla[0];
            nTotal=Long.parseLong(aTupla[1]);
            nDisponible=Long.parseLong(aTupla[3]);
            if(sMontura.compareTo("/")==0){
              sDispRaiz=sDispositivo;
              sMontRaiz=sMontura;
              nTotalRaiz=Long.parseLong(aTupla[1]);
              nDisponibleRaiz=Long.parseLong(aTupla[3]);
              // más chequeos se postponen
            }else{
              if(directorio.indexOf(sMontura)>=0){
                // es el punto de montura del dispositivo
                // todo bien, se sale del ciclo
                break;
              }else{
                // era otro punto de montura
                sMontura="";
                sDispositivo="";
                nTotal=0;
                nDisponible=0;
              }
            }
          }
        }
        raMonturas.close();
        raMonturas=null;
        if(sMontura==""){
          sMontura=sMontRaiz;
          sDispositivo=sDispRaiz;
          nTotal=nTotalRaiz;
          nDisponible=nDisponibleRaiz;
        }
        if(sMontura==""){
          throw new ADMINPOLExcepcion("No se pudo encontrar el dispositivo " +
              "del disco de tdderive.");
        }
        monturaDir=sMontura;
        dispositivoDir=sDispositivo;
      } catch (IOException e) {
        e.printStackTrace();
        throw new ADMINPOLExcepcion("No pudo leerse el archivo de discos.",e);
      } catch (InterruptedException e) {
        e.printStackTrace();
        throw new ADMINPOLExcepcion("No pudo leerse el comando de lectura de discos.",e);
      }
      return (double)nTotal/(double)1024;
    }
    public double getDiscoUsado() throws ADMINPOLExcepcion{
      String directorio="/tdderive";
      String sDispositivo="",sMontura="",sTupla="",sDispRaiz="",sMontRaiz="";
      long nTotal=0,nDisponible=0,nTotalRaiz=0,nDisponibleRaiz=0;
      String[] aTupla;
      Process comando=null;
      BufferedReader raMonturas=null;
      try {
        comando=Runtime.getRuntime().exec("df");
        comando.waitFor();
        raMonturas=new BufferedReader(new InputStreamReader(comando.getInputStream()));
        while((sTupla=raMonturas.readLine())!=null){
          aTupla=sTupla.split(" ");
          if(aTupla.length>5){
            sMontura=aTupla[5];
            sDispositivo=aTupla[0];
            nTotal=Long.parseLong(aTupla[1]);
            nDisponible=Long.parseLong(aTupla[3]);
            if(sMontura.compareTo("/")==0){
              sDispRaiz=sDispositivo;
              sMontRaiz=sMontura;
              nTotalRaiz=Long.parseLong(aTupla[1]);
              nDisponibleRaiz=Long.parseLong(aTupla[3]);
              // más chequeos se postponen
            }else{
              if(directorio.indexOf(sMontura)>=0){
                // es el punto de montura del dispositivo
                // todo bien, se sale del ciclo
                break;
              }else{
                // era otro punto de montura
                sMontura="";
                sDispositivo="";
                nTotal=0;
                nDisponible=0;
              }
            }
          }
        }
        raMonturas.close();
        raMonturas=null;
        if(sMontura==""){
          sMontura=sMontRaiz;
          sDispositivo=sDispRaiz;
          nTotal=nTotalRaiz;
          nDisponible=nDisponibleRaiz;
        }
        if(sMontura==""){
          throw new ADMINPOLExcepcion("No se pudo encontrar el dispositivo " +
              "del disco de tdderive.");
        }
        monturaDir=sMontura;
        dispositivoDir=sDispositivo;
      } catch (IOException e) {
        e.printStackTrace();
        throw new ADMINPOLExcepcion("No pudo leerse el archivo de discos.",e);
      } catch (InterruptedException e) {
        e.printStackTrace();
        throw new ADMINPOLExcepcion("No pudo leerse el comando de lectura de discos.",e);
      }
      return (double)(nTotal-nDisponible)/(double)1024;
    }
    /**
     * @see admin.ADMINPOLILectorBajoNivel#getIdentificacion()
     */
    public long getIdentificacion() throws ADMINPOLExcepcion {
      long nCentProcModoUsuario=0,nCentProcModoKernel=0,nCentProcInicio=0,
        nCentModoUsuario=0,nCentModoKernel=0,nCentModoUBajaPrio=0,
        nCentProcOcupado=0,nCentTranscurrido=0,nCentProcCPU=0,nTOTALCPU=0;
      double usoCPU=0.0;
      String sTuplaProc="";
      String[] asTupla,asTuplaProc;
      BufferedReader brUso1=null;
      if(identificacion==0){
        try{
          brUso1=new BufferedReader(new FileReader(PROCCPUU1));
          sTuplaProc=brUso1.readLine();
          brUso1.close();
          asTuplaProc=sTuplaProc.split(" ");
          identificacion=Integer.parseInt(asTuplaProc[0]);          
        }catch(FileNotFoundException e){
          throw new ADMINPOLExcepcion("No se encontraron los archivos " +
              "con datos sobre el proceso.",e);
        } catch (Exception e) {
          throw new ADMINPOLExcepcion("No se pudo leer información " +
              "sobre el proceso.",e);
        }        
      }
      if(identificacion==0){
        throw new ADMINPOLExcepcion("Identificación del proceso es inválida.");
      }
      return identificacion;
    }
    public double getMemoriaTotal()throws ADMINPOLExcepcion{
      int idx=-1;
      long nTotal=0,nDisponible=0;
      String sTupla="";
      BufferedReader raMemoria=null;
      try {
        raMemoria=new BufferedReader(new FileReader(PROCMEM));
        while(((sTupla=raMemoria.readLine())!=null) && (nTotal==0)){
          if(sTupla.indexOf("MemTotal")==0){
            idx=sTupla.indexOf(":");
            if(idx<0){
              throw new ADMINPOLExcepcion("Información de memoria total inválida.");
            }
            idx++;
            nTotal=Long.parseLong(sTupla.substring(idx).trim());
          }
        }
        raMemoria.close();
        raMemoria=null;
        memoriaTotal=((double)nTotal)/1024.0;
      } catch (IOException e) {
        e.printStackTrace();
        throw new ADMINPOLExcepcion("No pudo leerse el archivo de memoria.",e);
      }
      return memoriaTotal;
    }
    public double getMemoriaUsada() throws ADMINPOLExcepcion{
      int idx=-1;
      long nTotal=0,nDisponible=0;
      String sTupla="";
      BufferedReader raMemoria=null;
      try {
        raMemoria=new BufferedReader(new FileReader(PROCMEM));
        while(((sTupla=raMemoria.readLine())!=null) && 
            (nTotal==0||nDisponible==0)){
          if(sTupla.indexOf("MemTotal")==0){
            idx=sTupla.indexOf(":");
            if(idx<0){
              throw new ADMINPOLExcepcion("Información de memoria total inválida.");
            }
            idx++;
            nTotal=Long.parseLong(sTupla.substring(idx).trim());
            continue;
          }
          if(sTupla.indexOf("MemFree")==0){
            idx=sTupla.indexOf(":");
            if(idx<0){
              throw new ADMINPOLExcepcion("Información de memoria libre inválida.");
            }
            idx++;
            nDisponible=Long.parseLong(sTupla.substring(idx).trim());
          }
        }
        raMemoria.close();
        raMemoria=null;
        memoriaTotal=nTotal;
      } catch (IOException e) {
        e.printStackTrace();
        throw new ADMINPOLExcepcion("No pudo leerse el archivo de memoria.",e);
      }
      if(nTotal>=0){
        throw new ADMINPOLExcepcion("Valor de memoria inválido.");
      }
      return ((double)(nDisponible-nTotal))/1024.0;
  }
    public int getNumProcesadores() throws ADMINPOLExcepcion{
      String sTupla="";
      int idx=-1;
      BufferedReader raMicroprocesador=null;
      try {
        raMicroprocesador=new BufferedReader(new FileReader(PROCCPU));
      } catch (FileNotFoundException e) {
        throw new ADMINPOLExcepcion("No se pudo leer archivo " +
            "con información de la CPU.",e);
      }
      if(lectorCPUMhz==0){
        try {
          while((sTupla=raMicroprocesador.readLine())!=null){
            if(sTupla.indexOf("processor")>=0){
              idx=sTupla.indexOf(":");
              if(idx<0){
                // no hay información sobre velocidad de CPU
                return -1;
              }
              idx+=2;
              numProcesadores=Integer.parseInt(sTupla.substring(idx));
              if(numProcesadores==0){
                numProcesadores=1;
              }
            }
          }
          raMicroprocesador.close();
        } catch (IOException e) {
          e.printStackTrace();
          throw new ADMINPOLExcepcion("No se pudo leer archivo " +
              "con información de la CPU.",e);
        } catch (Exception e) {
          e.printStackTrace();
          throw new ADMINPOLExcepcion("Error obteniendo información " +
              "de la CPU.",e);
        }
      }
      return numProcesadores;
    }
    /**
     * @see admin.ADMINPOLILectorBajoNivel#open()
     */
    public void open() throws ADMINPOLExcepcion {
      String montura;
      BufferedReader raMemoria=null;
      BufferedReader raMicroprocesador=null;
      try {
        fdProc=new File(DIRPROC);
        if(!fdProc.exists()){
          throw new ADMINPOLExcepcion("Servicio de información " +
              "ausente.");
        }
        raMemoria=new BufferedReader(new FileReader(PROCMEM));
        raMicroprocesador=new BufferedReader(new FileReader(PROCCPU));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        throw new ADMINPOLExcepcion("Error al leer de archivos",e);
      }      
    }
	}
  /**
   * <p>Title: <b>admin</b>:: admin</p>
   * <p>Description: ADMINPOLLector.java.</p>
   * <p>Copyright: Copyright (c) 2004</p>
   * <p>Company: UCR - ECCI</p>
   * <br>@author Alessandro</br>
   * <br>@version 1.0</br>
   * <br><b>LectorBajoNivelWIN32</b></br>
   * <br></br>
   */

  public static class LectorBajoNivelWIN32 implements ADMINPOLILectorBajoNivel {
    static{
      System.loadLibrary("tdllmain2");
    }
    /**
     * Obtiene el tiempo de CPU que ha sido utilizado por este
     * proceso.
     * @return El tiempo de CPU que ha sido utilizado por este proceso.
     */
    static native public long getTiempoCPUProceso();
    public static FotoUsoCPU hazFotoUsoCPU(){
      return new FotoUsoCPU(System.currentTimeMillis(),getTiempoCPUProceso());
    }
    public LectorBajoNivelWIN32() {
    }

    /**
     * @see admin.ADMINPOLILectorBajoNivel#close()
     */
    public void close() throws ADMINPOLExcepcion {
      
    }

    /**
     * @see admin.ADMINPOLILectorBajoNivel#getCPUMegaHertz()
     */
    native public double getCPUMegaHertz();

    /**
     * @see admin.ADMINPOLILectorBajoNivel#getCPUPorcentajeUso()
     */
    native public double getCPUPorcentajeUso();
    /**
     * @param intervalo_precision Intérvalo de precisión.
     * @return El uso de la CPU.
     */
    public double getCPUPorcentajeUso2(long intervalo_precision){
      double uso_cpu=0.0;
      FotoUsoCPU f1,f2;
      f1=hazFotoUsoCPU();
      synchronized(this){
        try {
          this.wait(intervalo_precision);
        } catch (InterruptedException e) {
        }
      }
      f2=hazFotoUsoCPU();
      uso_cpu=(double)(f2.tiempoCPU-f1.tiempoCPU)/(f2.tiempo-f1.tiempo);
      if(uso_cpu<0.0 || uso_cpu>1.0){
        // hay algo malo en los valores devueltos por el sistema
        uso_cpu=0.70;
      }
      return uso_cpu; 
    }    

    /**
     * @see admin.ADMINPOLILectorBajoNivel#getDiscoTotal()
     */
    public double getDiscoTotal() {
      return getDiscoTotal("/tdderive");
    }
    native public double getDiscoTotal(String dir);

    /**
     * @see admin.ADMINPOLILectorBajoNivel#getDiscoUsado()
     */
    public double getDiscoUsado() {
      return getDiscoUsado("/tdderive");
    }
    native public double getDiscoUsado(String dir);

    /**
     * @see admin.ADMINPOLILectorBajoNivel#getIdentificacion()
     */
    native public long getIdentificacion();

    /**
     * @see admin.ADMINPOLILectorBajoNivel#getMemoriaTotal()
     */
    native public double getMemoriaTotal();

    /**
     * @see admin.ADMINPOLILectorBajoNivel#getMemoriaUsada()
     */
    native public double getMemoriaUsada();

    /**
     * @see admin.ADMINPOLILectorBajoNivel#getNumProcesadores()
     */
    native public int getNumProcesadores();

    /**
     * @see admin.ADMINPOLILectorBajoNivel#open()
     */
    public void open() throws ADMINPOLExcepcion {
      
    }
    public static final class FotoUsoCPU{
      public final long tiempo,tiempoCPU;
      private FotoUsoCPU(long tiempo0,long tiempoCPU0){
        tiempo=tiempo0; // sab0406
        tiempoCPU=tiempoCPU0;
        // System.err.println("("+ tiempo+","+tiempoCPU+ ")");
      }
    }

  }
  }