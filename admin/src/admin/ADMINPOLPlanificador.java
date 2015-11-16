package admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import oact.OACTDistribuidorBase;
import oact.OACTDistribuidorBase.DireccionesVirtuales;

import orgainfo.OIExcepcion;
import orgainfo.OIPersistente;
import tdutils.EscritorSalidas;
import tdutils.MapaEstadistico;
import tdutils.tdutils;
import tdutils.Invocable;

import admin.DIRBalances.DIRTransferencias;
import admin.PERSAmbiente.Computadora;
import admin.PERSAmbiente.Ubicaciones;
/**
 * <br>Recibe notificaciones sobre eventos importantes 
 * detectados por un componente lector y otros componentes planificadores 
 * de otras computadoras con agentes de <tt>tdderive</tt>.</br>
 */
public class ADMINPOLPlanificador {
  /**
   * Tiempo mínimo que un subtrabajo debe cumplir para no ser
   * detenido en una planificación.
   */
  public final static int PLANIF_TIEMPOMINMARCHA=180;
  /**
   * Umbral para el retardo de una ejecución.
   */
  public final static double PLANIF_UMBRAL_BETA=200.0;
  /**
   * Umbral para la discriminación de la calidad.
   */
  public final static double PLANIF_UMBRAL_GAMMA=60.0;
  /**
   * Lista de trabajos que se han solicitado.
   */
  private Map _controladores;
  /**
   * Lista de espera de respuestas. Su formato es (id_tarea,gestor de respuesta).
   */
  private Map _esperaRespuestas;
  private EscritorSalidas _escritor;
  /**
   * Descriptor del entorno del sistema.
   */
  ADMINGLOInfo _desc;
  /**
   * Sirve para bloquear al objeto de eventos mientras está tomando 
   * una decisión.
   */
  public String objeto_bloqueo_decidiendo="objeto_bloqueo_decidiendo";
  /**
   * Estadísticas históricas de las lecturas de una computadora.
   */
  private MapaEstadistico _estadisticasCompu;
  /**
   * Tiempo máximo en el que un nodo se considera
   * dentro del dominio de balance.
   */
  private long tiempoNodoFuera=0;
  /**
   * Tiempo máximo de espera de una notificación de subtrabajo vivo.
   * Vencido, el subtrabajo se replanifica.
   */
  private long tiempoReinicioSubtrabajo=0;
  /**
   * Constructor.
   * @param desc0 Descriptor del entorno del sistema.
   */
  public ADMINPOLPlanificador(ADMINGLOInfo desc0) {
    _desc=desc0;
    _controladores=java.util.Collections.synchronizedMap(
        new TreeMap(String.CASE_INSENSITIVE_ORDER));
    _esperaRespuestas=java.util.Collections.synchronizedMap(
        new TreeMap(String.CASE_INSENSITIVE_ORDER));
    _escritor=new EscritorSalidas("Planificador");
    _estadisticasCompu=new MapaEstadistico(String.CASE_INSENSITIVE_ORDER,
        Computadora.class); 
  }
  /**
   * <br>Aplica las políticas <b><i>alfa</i></b>, <b><i>beta</i></b> y 
   * <b><i>gamma</i></b>, luego de que el lector haya realizado su 
   * correspondiente recolección de datos sobre tdderive y el sistema 
   * de hospedaje.</br>
   * <li>Método a ser registrado ante el lector como sensor de
   * eventos del planificador.</li>
   * <li>El planificador lee el estado nuevo que se vive según el lector
   * y lo compara con el estado oficial de la computadora.</li>
   * <li>El planificador realiza u omite las actividades que sean necesarias
   * para adaptarse a la nueva situación.</li>
   * <br><b>Concorde con el diseño de la administración de recursos tenemos 
   * estas actividades:</b></br>.
   * <li>1. Se evita el inicio de subtrabajos en espera si la compu está 
   * muy cargada.</li>
   * <li>2. Se manda el estado al exterior si se encuentra baja carga 
   * funcional o de aplicación.</li>
   * <li>3. Se detienen procesos si se detecta que están muy atrasados.</li>
   * <li>4. Coloca subtrabajos a la computadora anfitriona.</li>
   * <li>5. Reintenta retornos anteriormente fallados.</li>
   * <br><b>Actividad 1</b> (política <b><i>beta</i></b>)</br>
   * <li>Si la carga funcional leída es el doble de la promedio 
   * (sin participar 
   * ésta en el promedio) no se hacen las actividades 2 ni 4.</li>
   * <li>En caso contrario sí se pasan a hacer las actividades 2 y 4.</li>
   * <li>Las actividades 3 y 5 siempre se realizan.</li>
   * <br><b>Actividad 2 (política <b><i>alfa</i></b>)</b></br>
   * <li>Se evalúa si la carga funcional es baja, que es cuando 
   * la nueva carga funcional es menor a la carga funcional mediana 
   * (sin participar ésta en la mediana).</li>
   * <li>Análoga a la carga funcional es la evaluación de la carga de 
   * aplicación.</li>
   * <br><b>Actividad 3 (política <b><i>gamma</i></b>)</b></br>
   * <li>Se ponen en lista de espera los procesos trabajados en el exterior 
   * cuya duración excede la tolerancia indicada en configuración.</li>
   * <br><b>Actividad 4 (política <b><i>beta</i></b>)</b></br>
   * <li>Si la actividad ha sido permitida por la 1 realiza lo siguiente:</li>
   * <pre>
   *     Se traen por orden descendente de edad los subtrabajos 
   *     que cumplan con determinada carga de aplicación.
   *     
   *     Debe generarse un peso de aplicación permitido al momento, que se 
   *     calcule a partir de la carga funcional de la computadora en el 
   *     momento y así poner a trabajar los procesos en espera cuya sumatoria 
   *     de carga funcional no la sobrepase.
   *     
   *     carga_funcional_permitida=carga_funcional_promedio*2
   *     carga_funcionaladicional_permitida=
   *                carga_funcional_permitida-carga_funcional_actual
   *     
   *     carga_aplicaciónadicional_permitida=
   *                carga_funcionaladicional_permitida*factor
   *     
   *     factor=carga_aplicación_promedio/carga_funcional_promedio
   *     
   *     éste es el factor que transforma cargas funcionales a cargas de 
   *     aplicación
   *
   *     carga_aplicaciónadicional_permitida es la carga de aplicación 
   *     que deben sumar los subtrabajos a iniciar. Si no hay subtrabajo que 
   *     la cumpla entonces pone a trabajar al primero (como quien 
   *     dice: ni modo).
   * 
   *     Esta parte del algoritmo para conseguir subtrabajos a ejecutar
   *     en forma local es voraz por priorizar la antiguedad de
   *     los subtrabajos. Hubiera sido una búsqueda óptimizada según
   *     la suma de las cargas de aplicación, pero se considera la forma
   *     voraz por la antiguedad para evitar la inanición de los subtrabajos
   *     más antiguos que pesen mucho.
   *
   * </pre>
   * <br><b>Actividad 5</b> (política <b><i>gamma</i></b>)</br>
   * <li>Siempre se reintenta la entrega de los retornos de subtrabajos 
   * que no hayan sido entregados a la compu interesada.</li>
   * <br></br>
   * <br></br>
   * <br></br>
   * <br></br>
   * <br><b>Política <i>beta</i></b></br>
   * <br>Se pone a procesar el primer subtrabajo en espera,
   * indicándoselo a su coordinador.</br>
   * <li>El primer subtrabajo en espera es aquel que está en espera
   *   con el menor tiempo de solicitud, i.e. el solicitado desde 
   *   hace mucho tiempo cuyo resultado no ha sido obtenido y no
   *   está en marcha.</li>
   * <li>Dependiendo de la carga funcional se procesa más de un
   * trabajo siguendo una función que relaciona la carga funcional
   * con la carga de aplicación.</li>
   * <b>2005 Cambiado por adaptación</b>
   * @param compuLectura Estado de la computadora según el lector luego
   * de su última exploración.
   */
  public void notificaLectura(Computadora compuLectura){
    boolean primera_vez=false;
    boolean carga_alta=false;
    boolean carga_baja=false;
    double umbral_beta=0.0;
    double umbral_gamma=0.0;
    double carga_funcional_promedio=0.0;
    double carga_aplicacion_promedio=0.0;
    double carga_funcional_permitida=0.0;
    double carga_funcionaladicional_permitida=0.0;
    double carga_aplicacionadicional_permitida=0.0;
    double factor=0.0;
    Computadora compuMediana;
    /*| 
     *| Preparación inicial
     *| ===================
     *| Si es la notificación de la primera lectura, entonces
     *| actualiza inmediatamente el estado de la computadora.
     */
    primera_vez=_desc.getComputadora().getMicroReloj()==0.0;
    if(primera_vez){
      /*
       * es la primera vez, copia la información leída
       * al "estado oficial"
       */
      _desc.getComputadora().imita(compuLectura);
      /* por si acaso realiza lo siguiente */
      if(_desc.getComputadora().getMicroReloj()==0.0){
        // esto no debería suceder
        _escritor.escribeMensajeInfoHilo("Estado no esperado, se ha leído una computadora" +
            "con un reloj de 0MHz, puesto por el planificador a 1.0MHz.",true);
        _desc.getComputadora().setMicroReloj(1.0);
      }
    } // listo en cuanto a la primera vez
    carga_funcional_promedio=_estadisticasCompu.demeMedia("carga_funcional");
    carga_aplicacion_promedio=_estadisticasCompu.demeMedia("carga_aplicacion");
    compuMediana=(Computadora)_estadisticasCompu.demeMediana("carga_funcional");
    /*
     * continúa la preparación:
     * trae valores de los umbrales
     */
    try {
      umbral_beta=_desc.getPesoUmbralPolitica("PLANIF_UMBRAL_BETA");
    } catch (ADMINGLOExcepcion e) {
      umbral_beta=PLANIF_UMBRAL_BETA;
    }
    try {
      umbral_gamma=_desc.getPesoUmbralPolitica("PLANIF_UMBRAL_GAMMA");
    } catch (ADMINGLOExcepcion e) {
      umbral_gamma=PLANIF_UMBRAL_GAMMA;
    }
    this._desc.println("lector","activa planificación");
    this.imprimeVecinos();
    /*|
     *| Actividad 1
     *| ============
     *| Determina carga alta o baja.
     *| Si la carga es baja hace actividades 2 y 4.
     */
    carga_alta=compuLectura.getCargaFuncional()>(umbral_beta*0.01*
        carga_funcional_promedio);
    /*|
     *| Actividad 2
     *| ===========
     *| Manda estado al exterior
     */
    if(!carga_alta){
      /*
       * detecta si la carga es realmente baja
       * primero lo intenta con la carga funcional
       */
      if(compuMediana==null){
        /*
         * probablemente por este momento solo
         * existe el agente local actual
         */
        carga_baja=false;
      }else{
          carga_baja=compuLectura.getCargaFuncional()<=
              compuMediana.getCargaFuncional();
      }
      if(!carga_baja){
        /*
         * si no ha encontrado que la carga sea baja
         * entonces lo determina con la carga de aplicación
         */
        if(compuMediana!=null){
          carga_baja=compuLectura.getCargaAplicacion()<=
            compuMediana.getCargaAplicacion();
        }
      }
      /*
       * ahora sí, si la carga (funcional o de aplicación) es verdaderamente 
       * baja hace lo que corresponde.
       */
      if(carga_baja){
        // manda mensaje a computadoras del dominio de balance
        this.plan02OfertaAlDominio();
      }
    }
    try {
      /*
       * agrega la información de la computadora a las
       * estadísticas 
       */
      this._estadisticasCompu.agrega(
          String.valueOf(compuLectura.hashCode()),compuLectura);
    } catch (Exception e1) {
      _escritor.escribeMensajeInfoHilo("No se agregó info de computadora a la " +
          "colección estadística",true);
    }
    /*|
     *| Actividad 3
     *| ===========
     *| Detiene subtrabajos con bajo rendimiento.
     */
    this.plan03DetieneMalaCalidad();
    /*|
     *| Actividad 4 
     *| ===========
     *| Ejecuta subtrabajos en espera.
     *| <2005 Reemplazada por la actividad 4' >
     */
//    if(!carga_alta){
//      factor=carga_aplicacion_promedio/carga_funcional_promedio;
//      carga_funcional_permitida=umbral_beta*0.01*carga_funcional_promedio;
//      carga_funcionaladicional_permitida=carga_funcional_permitida-
//          compuLectura.getCargaFuncional();
//      carga_aplicacionadicional_permitida=factor*
//          carga_funcionaladicional_permitida;
//      this.plan04IniciaSubtrabajosLocales(carga_aplicacionadicional_permitida);
//    }
    /*|
     *| Actividad 4' 
     *| ===========
     *| Asigna trabajos a los agentes del dominio de balance de la siguiente manera
     *|     listastrabajos: es la lista de subtrabajos
     *|     listanodos: es la lista de agentes (donde se incluye al agente local)
     *|     while(listastrabajos haya uno sin asignar){
     *|        while(listanodos recorrido un ciclo){
     *|          listastrabajos[i] asignado a listanodos[j]
     *|        }
     *|     }
     *| <2005 Reemplaza a la actividad 4 >
     */
    this.plan04bisIniciaSubtrabajosLocales();
    /*|
     *| Actividad 4.5
     *| =============
     *| Indica subtrabajos en marcha a coordinadores
     *| respectivos.
     */
    this.plan0405IndicaSubtrabajosEnMarcha();
    /*|
     *| Actividad 5
     *| ===========
     *| Organiza y reintenta retornos.
     */
    this.plan05OrganizaSubtrabajosTerminados();
    this.plan05ReintentaRetornos();
    /*
     * 
     * 
     * TODO Revisar lo siguiente, seguro hay que borrarlo.
     * 
     * 
     */
    
//    /*
//     * Aplica BETA
//     * ===========
//     */
//    if(compuLectura.getCargaFuncional()>umbral_beta){
//      /*
//       * Se pone a procesar el primer subtrabajo en espera,
//       * indicándoselo a su coordinador.
//       * - El primer subtrabajo en espera es aquel que está en espera
//       *   con el menor tiempo de solicitud, i.e. el solicitado desde 
//       *   hace mucho tiempo cuyo resultado no ha sido obtenido y no
//       *   está en marcha.
//       * - Dependiendo de la carga funcional se procesan más trabajos.
//       */
//      /*
//       * problema: seleccionar subtrabajos sin que la compu se cargue mucho,
//       * --------- pero que los más viejos se pongan en marcha.
//       */
//    }
//    /*
//     * Si hay cambios que informar al vecindario, se provoca un disturbio
//     * para la instancia balanceadora.
//     */
//    if(Math.abs(this.getCargaAplicacion()-
//        compuLectura.getCargaAplicacion())<60.0){
//      // indica disturbio
//    }
  }
  /**
   * Organiza los subtrabajos terminados: exporta resultados 
   * de subtrabajos a las computadoras pertinentes, actualiza 
   * cuáles subtrabajos fueron exportados y cuáles tareas están 
   * terminadas.
   */
  private void plan05OrganizaSubtrabajosTerminados(){
    Map subtrabajosterminados=null;
    /// [05.1] primero colecta subtrabajos.
    subtrabajosterminados=this.buscaSubtrabajosTerminados();
    /// [05.2] exporta información de subtrabajos terminados al lugar de origen.
    /// [05.3] en el fin de cada exportación de subtrabajo debe cambiarse el 
    ///        estado del subtrabajo en la base de datos del agente local.
    this.exportaSubtrabajosTerminados(subtrabajosterminados);
    
  }
  /**
   * <b>Actividad 5</b>. Reintenta retornos anteriormente fallados.
   * Vuelve a enviar los retornos de las tareas terminadas
   * a la computadora pertinente (a aquella a la cual fueron 
   * solicitados), de la manera adecuada. 
   */
  private void plan05ReintentaRetornos() {
    /*
     * [05.3] actualiza los trabajos que verdaderamente están terminados.
     *  ---> por cada controlador, invoca a reuneResultados()
     * [05.4] retorna los trabajos terminados en el hilo que corresponde.
     *  ---> si el valor de reune resultados es verdadero
     *       entonces invoca a retornaResultado().
     */
    Iterator itr=null;
    ADMINAPPControladores ctrl=null;
    boolean bGo=false;
    synchronized(this._controladores){
      itr=this._controladores.values().iterator();
      while(itr.hasNext()){
        ctrl=(ADMINAPPControladores)itr.next();
        if(!ctrl.siLocal()){
          // La compu no es la coordinadora del trabajo
          continue;
        }
        try {
          bGo=ctrl.reuneResultados();
          if(bGo){
            ctrl.retornaResultado();
          }
        } catch (ADMINAPPExcepcion e) {
          e.printStackTrace();
        }
      }
    }
  }
  /**
   * <b>Actividad 4</b>. Coloca subtrabajos a la computadora anfitriona. 
   * Pone a trabajar los subtrabajos de manera local, dando prioridad a
   * aquellos que son más antiguos.
   * <li>La carga de aplicación adicional recibida como parámetro
   * no debe ser sobrepasada, exceptuando el caso donde el proceso
   * más antiguo la sobrepasa.</li>
   * @param carga_aplicacionadicional_permitida La carga de aplicación que
   * no debe ser sobrepasada.
   * <li>Requiere que la carga de trabajo (funcional o de aplicación) 
   * de la computadora hospedera no sea alta.</li>
   */
  private void plan04IniciaSubtrabajosLocales(double carga_aplicacionadicional_permitida) {
    /*
     * se traen y ejecutan subtrabajos por orden descendente de edad
     * cuya carga de aplicación sume no más de la 
     * carga_aplicacionadicional_permitida.
     * Esta búsqueda no va a ser optimizada para la sumatoria de la carga
     * de aplicación adicional, sino que toma como prioridad la
     * edad de los subtrabajos (algoritmo voraz).
     * 
     * .----|----.
     * | A  | B  | A: subtrabajos con carga menor que la dada
     * |  .-|-.  | B: subtrabajos con carga mayor que la dada
     * |  |C| |  | C: subtrabajos considerados como viejos
     * |  ._|_.  | Se le da prioridad a C, luego a AC, luego a BC, 
     * .____|____. luego a A y luego a B en este caso indirectamente 
     *             cuando pasan a ser BC. 
     *             Los elementos de A y los elementos de B tienden a
     *             pasar al conjunto C.
     */
    int caso=0;
    double diferencia[]={0.0};
    double acumulado=0.0;
    String sFiltro,sOrden;
    Map subtrabajosviejos=null,subtrabajoscarga=null;
    ADMINAPPControladores controlador=null;
    /// esta colección va a tener los subtrabajos a ejecutar
    Collection subtrabajosejecutar=null;
    Iterator itr=null;
    ADMINAPPISub_trabajos subtrabajo=null;
    sFiltro="carga<=" + String.valueOf(carga_aplicacionadicional_permitida);
    sOrden="hora_ingreso asc";
    /*|
     *| [1] deben traerse los subtrabajos
     *| [2] deben sacarse subtrabajos hasta llegar cubrir la
     *|     carga de aplicación adicional
     */
    subtrabajoscarga=this.buscaSubtrabajosEspera(sFiltro, sOrden);
    itr=subtrabajoscarga.values().iterator();
    if(itr.hasNext()){
      subtrabajo=(ADMINAPPISub_trabajos)itr.next();
    }
    if(subtrabajo==null){
      // no habían tareas en espera con la carga indicada
      sFiltro="hora_ingreso>0";
    }else{
      // sí habían tareas en espera con la carga indicada
      // así que busca al más anciano
      sFiltro="hora_ingreso<" + String.valueOf(subtrabajo.getHoraIngreso());
    }
    subtrabajo=null;
    sOrden="hora_ingreso asc";
    subtrabajosviejos=this.buscaSubtrabajosEspera(sFiltro, sOrden);
    if(subtrabajoscarga.isEmpty()){
      caso=1;
    }    
    if(subtrabajosviejos.isEmpty()){
      caso=caso+2;
    }
    switch(caso){
      case 3:
        /*
         * no hay subtrabajos en espera ¬A¬B¬C
         */
        break;
      case 2:
        /*
         * no hay más subtrabajos más viejos
         * que los que están en subtrabajoscarga AC || A
         * -----
         * Hace la búsqueda optimizada de los subtrabajos que sumen
         * la cantidad adicional de carga de aplicación.
         */
        subtrabajosejecutar=this.encuentraOptimo(subtrabajoscarga.values(), 
            carga_aplicacionadicional_permitida, diferencia);
        break;
      case 1:
        /*
         * no hay subtrabajos que cumplan con la carga 
         * así que trabaja con los más viejos que están
         * en subtrabajosviejos BC || C
         * ------
         * Toma los más viejos hasta que la carga funcional
         * se haya sobrepasado (es decir, solamente se ejecuta
         * el más viejo).
         */
        // como es lo mismo que el caso 0, entonces no hay break.
      case 0:
        /*
         * hay subtrabajos con menos de la carga indicada
         * pero también hay subtrabajos viejos con más de
         * la carga buscada (A || C)||(A || AC)||(A || BC)
         * ------
         * Idem caso 2 por eso no hay break. Se respeta la 
         * urgencia de los subtrabajos viejos.
         */
        itr=subtrabajosviejos.values().iterator();
        if(itr.hasNext()){
          subtrabajosejecutar=new HashSet();
          do{
            subtrabajo=(ADMINAPPISub_trabajos)itr.next();
            subtrabajosejecutar.add(subtrabajo);
            acumulado+=subtrabajo.getCarga();
          }while(itr.hasNext()&&
              (acumulado<=carga_aplicacionadicional_permitida));
        }        
        break;
    }
    /*|
     *| listos y bien identificados los siguientes trabajos a
     *| ejecutar
     *| [3] Ejecutar cada uno de los subtrabajos. Ello le corresponde
     *|     al controlador responsable de cada uno. Este objeto termina
     *|     aquí su responsabilidad.
     */
    itr=subtrabajosejecutar.iterator();
    while(itr.hasNext()){
      subtrabajo=(ADMINAPPISub_trabajos)itr.next();
      controlador=this.traeControlador(subtrabajo);
      try{
        // TODO: 00000 aquí se empiezan a ejecutar los subtrabajos
        controlador.ejecuta(subtrabajo);
      }catch(ADMINPOLExcepcion e){
        _escritor.escribeMensajeInfoHilo("Error administrativo al ejecutar subtrabajo",true);
        e.printStackTrace();
      }catch(ADMINAPPExcepcion e){
        _escritor.escribeMensajeInfoHilo("Error de aplicación al ejecutar subtrabajo",true);
        e.printStackTrace();
      }
    }
  }
  /**
   * <b>Actividad 4'</b>. Coloca subtrabajos a las computadoras.
   * <2005 Algoritmo explicado en notificaLectura() > 
   */
  private void plan04bisIniciaSubtrabajosLocales() {
    int caso=0,i=0;
    String sFiltro,sOrden;
    Map subtrabajoscarga=null;
    ADMINAPPControladores controlador=null;
    /// esta lista va a tener los subtrabajos a ejecutar localmente
    ArrayList subtrabajosejecutar=null,listaotronodo=null;
    /// este mapa va a tener los subtrabajos y los correspondientes agentes a los que se exportarán
    TreeMap exportaciones=null;
    /// lista de los nodos a los cuales se les asignará subtrabajos
    ArrayList nodosaasignar=null;
    Iterator itr=null;
    Ubicaciones compudeldominio,otracompu=null;
    ADMINAPPISub_trabajos subtrabajo=null;
    sFiltro="1=1";
    sOrden="hora_ingreso asc";
    /*|
     *| [0] se prepara la lista de nodos
     *|     el primero de la lista es el nodo que representa esta instancia de tdderive    
     *|     le siguen los demás nodos
     *|
     */
    compudeldominio=new Ubicaciones();
    compudeldominio.setNombre(((Computadora)this._desc.getComputadora()).getNombre());
    nodosaasignar=new ArrayList();
    nodosaasignar.add(compudeldominio);
    nodosaasignar.addAll(this._desc.getNodosReportados(System.currentTimeMillis()-this.getTiempoNodoFuera()*1000).values());
    /*|
     *| [1] deben traerse los subtrabajos
     *| [2] deben sacarse todos los subtrabajos en espera
     *|
     */
    subtrabajoscarga=this.buscaSubtrabajosEspera(sFiltro, sOrden);
    if(!subtrabajoscarga.isEmpty() && !subtrabajoscarga.values().isEmpty()){
      /*
       * Asigna procesador a los subtrabajos en espera
       */      
      itr=subtrabajoscarga.values().iterator();
      subtrabajosejecutar=new ArrayList();
      exportaciones=new TreeMap(String.CASE_INSENSITIVE_ORDER);
      i=0;
      while(itr.hasNext()){
        subtrabajo=((ADMINAPPISub_trabajos)itr.next());
        if(i % nodosaasignar.size()==0){
          // trabajo para la compu local
          subtrabajosejecutar.add(subtrabajo);
        }else{
          otracompu=(Ubicaciones)nodosaasignar.get(i % nodosaasignar.size());
          listaotronodo=(ArrayList)exportaciones.get(otracompu.getNombre());
          if(listaotronodo==null){
            // para la primera vez
            listaotronodo=new ArrayList();
            exportaciones.put(otracompu.getNombre(),listaotronodo);
          }
          listaotronodo.add(subtrabajo);
        }
        i++;
      }
    }else{
      /*
       * Nada que hacer, no hay subtrabajos en espera
       */
    }
    /*|
     *| listos y bien identificados los subtrabajos a ejecutar
     *| [3] Se exportan lo subtrabajos a las correspondientes computadoras. 
     *|     Este objeto termina aquí su responsabilidad. Esto en un hilo aparte.
     *| [4] Ejecutar cada uno de los subtrabajos. Ello le corresponde
     *|     al controlador responsable de cada uno en su propio hilo. 
     *|
     */
    if(exportaciones!=null && exportaciones.size()>0){
      this.exportaSubtrabajos(exportaciones);
    }
    if(subtrabajosejecutar!=null){
      itr=subtrabajosejecutar.iterator();
      while(itr.hasNext()){
        subtrabajo=(ADMINAPPISub_trabajos)itr.next();
        // solamente ejecuta al subtrabajo si éste está en espera
        if(subtrabajo==null){
          continue;
        }
        if(subtrabajo.getEstadoSubtrabajo().compareToIgnoreCase(ADMINAPPISub_trabajos.SUBTRA_ESPERA)!=0){
          continue;
        }
        controlador=this.traeControlador(subtrabajo);
        try{
          // TODO: 00000 aquí se empiezan a ejecutar los subtrabajos localmente
          this._desc.println("planificador","ejecuta subtrabajo: "+subtrabajo.getIdTarea()+":"+subtrabajo.getIdSubtrabajo());
          controlador.ejecuta(subtrabajo);
        }catch(ADMINPOLExcepcion e){
          _escritor.escribeMensajeInfoHilo("Error administrativo al ejecutar subtrabajo",true);
          e.printStackTrace();
        }catch(ADMINAPPExcepcion e){
          _escritor.escribeMensajeInfoHilo("Error de aplicación al ejecutar subtrabajo",true);
          e.printStackTrace();
        }
      }
    }
  }
  /**
   * <b>Actividad 4.5</b>
   * Indica a los coordinadores respectivos que
   * el nodo tiene subtrabajos en marcha. Específicamente
   * se encarga de mandar un mensaje de keepalive.
   * <li>Se garantiza que el subtrabajo está en marcha.</li>
   */
  private void plan0405IndicaSubtrabajosEnMarcha(){
    // Se hace el HP envío a los HP coordinadores respectivos
    long tiempoActual=0;
    long tiempoMilisegundos=0;
    Iterator itrI=null;
    Iterator itrJ=null;
    PERSCoordinacion.Sub_trabajos stra=null;
    PERSCoordinacion.Conts_Externos cext=null;
    ADMINAPPControladores cont=null;
    
    synchronized(this._controladores){
      itrI=this._controladores.values().iterator();
      while(itrI.hasNext()){
        cont=(ADMINAPPControladores)itrI.next();
        if(!cont.siLocal()){
          /*
           * actividad de interés solo si 
           * tdderive es el procesador de la tarea
           * y es el participante y no el coordinador
           */
          itrJ=cont.getSubtrabajos().values().iterator();
          stra=null;
          while(itrJ.hasNext()){
            stra=(PERSCoordinacion.Sub_trabajos)itrJ.next();
            if(stra.getEstadoSubtrabajo().compareTo(
            ADMINAPPISub_trabajos.SUBTRA_MARCHA)==0){
              this._desc.println("planificador","indica subtrabajo sigue vivo: "+stra.getIdTarea()+":"+stra.getIdSubtrabajo());
              this.notificaSigueVivo(stra);
            }
          }
        }
      }
    }
  }
  

  /**
   * <b>Actividad 2</b>. Se manda el estado al exterior si se 
   * encuentra baja carga funcional o de aplicación.
   * Manda mensajes sobre la carga de aplicación de la
   * computadora a las computadoras presentes en el dominio.
   * <li>Requiere que la carga de trabajo (funcional y de 
   * aplicación) sea baja.</li> 
   */
  private void plan02OfertaAlDominio() {
    DIRBalances balances=_desc.getBalanceador();
    try{
      balances.informaDominio(_desc.getComputadora().getCapacidad(),
          _desc.getComputadora().getCargaAplicacion());
    }catch(Exception ex){
      _escritor.escribeMensajeInfoHilo("Error al informar a todos los nodos del dominio.",true);
    }
  }
  double getCargaFuncional(){
    return this._desc.getComputadora().getCargaFuncional();
  }
  double getCargaAplicacion(){
    return this._desc.getComputadora().getCargaAplicacion();
  }  
  /**
   * <br>Aplica la política <b><i>alfa</i></b>.</br>
   * <br>Método a ser registrado ante el balanceador como sensor de
   * eventos del planificador.</br>
   * <li>Mediate este método se obtiene el conjunto de transferencias,
   * (instancias de tipo admin.DIRTransferencias).</li>
   * <li>El conjunto de transferencias recibido es único para este hilo de
   * control.</li>
   * <br><b>Concorde con el diseño de la administración de recursos 
   * tenemos el complemento de las actividades de la notificación de 
   * lecturas:</b></br>
   * <li>6. Considera el ofrecimiento externo para la puesta en marcha de 
   * subtrabajos en espera.</li>
   * <li>7. Prepara la ejecución de subtrabajos externos.</li>
   * <br><b>Actividad 6</b> (política <b><i>alfa</i></b>)</br>
   * <li>Recibe apoyo de otras computadoras, así que prepara
   * los subtrabajos que hay que exportar.</li>
   * <br><b>Actividad 7</b> (política <b><i>alfa</i></b>)</br>
   * <li>Prepara apoyo para computadoras que eventualmente mandarán
   * al nodo anfitrión solicitudes de ejecución de subtrabajos.</li>
   * <li>Los subtrabajos nuevos serán puestos en la lista de espera.</li>
   * @param transferencias Distribución de cargas. Este parámetro no se
   * usa, y refleja la distribución de cargas a exportar a computadoras
   * remotas dentro del dominio de balance.<b>Solamente viene cargado de
   * valores cuando se recibe apoyo de parte de compus externas.</b>
   * @param brindaApoyo Conjunto de nodos a los cuales hay que brindar
   * apoyo.
   * @param recibeApoyo Conjunto de nodos de los cuales se va a recibir
   * apoyo.
   */
  public void notificaTransferencia(Map transferencias,Map brindaApoyo,
      Map recibeApoyo){
    Iterator itr;
    DIRTransferencias tranI;
    ADMINAPPISub_trabajos straI;
    Computadora compu=this._desc.getComputadora();
    Map mapTrabajosLocales,mapTrabajosExternos,mapTrabajosEnMarcha;
    /*
     * Conjunto de subtrabajos elegidos. Identificados por 
     * parejas "id_tarea|id_subtrabajo"
     */
    Map mejoresAlDominio=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    HashSet planExportaciones=new HashSet();
    HashSet buscaMejoresI=new HashSet();
    double[]faltante=new double[1];
    Collection mejorExpAlNodo;
    double nAExportar=0.0;
    int nTiempoMinimoMarcha_s=0;
    ParTransferencia plan[]=null;
    int i;
    /*| Actividad 6
     *| ===========
     *| Debe exportar a las computadoras de 
     *| la lista de apoyo en el tamaño adecuado.
     *| [1] Busca subtrabajos adecuados para la exportación.
     *| [1.2] Busca subtrabajos en espera que quepan en la carga a prestar.
     *| [1.2] Si no hay subtrabajos en espera, elige entre los que están en
     *| marcha.
     *| [2] Revisa si la oferente ya tiene la tarea definida.
     *| [2.1] Si no, exporta tarea.
     *| [2.2] Si sí, exporta el subtrabajo elegido.
     */
    if(recibeApoyo.size()>0){
      this.plan06Exportacion(recibeApoyo, mejoresAlDominio, buscaMejoresI, 
          faltante, nTiempoMinimoMarcha_s);
    }
    /*| Actividad 7
     *| ===========
     *| 
     *| Debe dar apoyo a las computadoras de la lista 
     *| en los tamaños indicados.
     *| Aquí debería haber una preparación para solicitudes
     *| que eventualmente van a llegar.
     */
    if(brindaApoyo.size()>0){
      this.plan07Apoyo(brindaApoyo, mejoresAlDominio, buscaMejoresI, 
          faltante, nTiempoMinimoMarcha_s);
    }
  }
  /**
   * <b>Actividad 7</b>. Prepara apoyo para computadoras que 
   * eventualmente mandarán al nodo anfitrión solicitudes de ejecución de 
   * subtrabajos.
   * <li>Los subtrabajos nuevos serán puestos en la lista de espera.</li>
   * @param brindaApoyo Lista de las computadoras que van a ser apoyadas.
   * @param mejoresAlDominio A ver si hace falta.
   * @param buscaMejoresI A ver si hace falta.
   * @param faltante A ver si hace falta.
   * @param tiempoMinimoMarcha_s A ver si hace falta.
   */
  private void plan07Apoyo(Map brindaApoyo, Map mejoresAlDominio, HashSet buscaMejoresI, double[] faltante, int tiempoMinimoMarcha_s) {
    
  }
  /**
   * <b>Actividad 6</b>. Considera el ofrecimiento externo para la puesta 
   * en marcha de subtrabajos en espera.
   * @param recibeApoyo Detalles de dónde se espera recibir apoyo.
   * @param mejoresAlDominio Lista de los subtrabajos más apropiados 
   *        para exportar.
   * @param buscaMejoresI
   * @param faltante Carga de aplicación que falta por exportar.
   * @param nTiempoMinimoMarcha_s Tiempo mínimo para 
   *        detener trabajos en marcha y así exportarlos.
   */
  private void plan06Exportacion(Map recibeApoyo, Map mejoresAlDominio, HashSet buscaMejoresI, double[] faltante, int nTiempoMinimoMarcha_s) {
    Iterator itr;
    DIRTransferencias tranI;
    Map mapTrabajosLocales;
    Map mapTrabajosExternos;
    Map mapTrabajosEnMarcha;
    Collection mejorExpAlNodo;
    double nAExportar;
    ParTransferencia[] plan;
    int i;
    i=0;
    plan=new ParTransferencia[recibeApoyo.size()];
    itr=recibeApoyo.values().iterator();
    while(itr.hasNext()){
      faltante[0]=0;
      tranI=(DIRTransferencias)itr.next();
      nAExportar=tranI.Give;
      /*
       * primero carga trabajos en espera
       */
      buscaMejoresI.clear();
      mapTrabajosLocales=buscaSubtrabajosEsperaPorCarga("<=",
          nAExportar,true,mejoresAlDominio);
      mapTrabajosExternos=buscaSubtrabajosEsperaPorCarga("<=",
          nAExportar,false,mejoresAlDominio);
      buscaMejoresI.addAll(mapTrabajosExternos.values());
      buscaMejoresI.addAll(mapTrabajosLocales.values());
      /*
       * La mejor organización para exportar al nodo tranI.Node.
       */
      mejorExpAlNodo=encuentraOptimo(buscaMejoresI,nAExportar,faltante);
      /*
       * Las tareas obtenidas deben ser apartadas para esta 
       * exportación en particular.
       */
      actualizaElegidas(mejorExpAlNodo,mejoresAlDominio);
      nAExportar-=faltante[0];
      /*
       * Busca entre los trabajos en marcha el más adecuado para
       * ser detenido y posteriormente exportado.
       * Puede ser:
       * - Como no hay subtrabajos en espera, busca uno en marcha que
       *   no tenga mucho de estar trabajando
       */
      if(nAExportar>0){
        try {
          nTiempoMinimoMarcha_s=
            (int)_desc.getPesoUmbralPolitica("PLANIF_TIEMPOMINMARCHA");
        } catch (ADMINGLOExcepcion e) {
          nTiempoMinimoMarcha_s=PLANIF_TIEMPOMINMARCHA;
        }
        mapTrabajosEnMarcha=buscaSubtrabajosMarchaPorCargaTiempo(
            "<=",nAExportar,"<",nTiempoMinimoMarcha_s,mejoresAlDominio);
      }
      /*
       * confirma el mejor plan para exportar al nodo Node
       */
      plan[i]=new ParTransferencia(tranI,mejorExpAlNodo);
      i++;
    }
  }
  /**
   * Pone en memoria los subtrabajos elegidos en esta planificación.
   * @param mejores Los subtrabajos elegidos.
   * @param elegidasGlobales Los subtrabajos elegidos en toda la planificación
   * de exportación de trabajos.
   */
  private void actualizaElegidas(Collection mejores, Map elegidasGlobales) {
    Iterator itr=mejores.iterator();
    ADMINAPPISub_trabajos subtI;
    while(itr.hasNext()){
      subtI=(ADMINAPPISub_trabajos)itr.next();
      elegidasGlobales.put(subtI.getIdTarea()+"|"+subtI.getIdSubtrabajo(), 
          subtI.getIdTarea()+"|"+subtI.getIdSubtrabajo());
    }
  }
  /**
   * Encuentra la mejor combinación de subtrabajos a exportar o
   * a ejecutar localmente (actividades de planificación 6 
   * y 4 respectivamente).
   * <li>Se devuelve la mayor carga menor que el máximo dado.</li>
   * <li>Utiliza una estrategia exhaustiva.</li>
   * @param subtrabajos Conjunto de subtrabajos a analizar.
   * @param maximo Valor máximo de cargas a totalizar.
   * @param diferencia Valor que faltó por planificar.
   * @return Mejor combinación de subtrabajos que totalizan 
   * el valor máximo dado.
   */
  private Collection encuentraOptimo(Collection subtrabajos,
      double maximo,double[]diferencia){
    int indiceMejor=0;
    double diferenciaMenor=Double.MAX_VALUE;
    int indice;
    double diferenciaI;
    Iterator itr,itrI;
    Collection collI;
    Collection particion;
    ADMINAPPISub_trabajos subtI;
    particion= tdutils.particionaColeccion(subtrabajos);
    itr=particion.iterator();
    indice=0;
    while(itr.hasNext()){
      collI=(Collection)itr.next();
      itrI=collI.iterator();
      diferenciaI=0;
      while(itrI.hasNext()){
        subtI=(ADMINAPPISub_trabajos)itrI.next();
        diferenciaI+=subtI.getCarga();
        if((diferenciaI>maximo)||((maximo-diferenciaI)>diferenciaMenor)){
          /*
           * la info de este conjunto no coolabora
           * mejor salir del ciclo.
           */
          break;
        }
      }
      diferenciaI=maximo-diferenciaI;
      if((diferenciaI<diferenciaMenor)&&(diferenciaI>0)){
        diferenciaMenor=diferenciaI;
        indiceMejor=indice;
      }
      indice++;
    }
    // el mejor subconjunto es el del índice indiceMejor
    collI=(Collection)particion.toArray()[indiceMejor];
    diferencia[0]=diferenciaMenor;
    return collI;
  }
  /**
   * <br>Caracteriza una actividad de transferencia de
   * carga de trabajo de una computadora más cargada a
   * una computadora más liviana.</br>
   * <br>Tiene información sobre el nodo de destino y
   * la carga de trabajo aceptada y confirmada por éste,
   * y el conjunto de subtrabajos a exportar.</br>
   */
  private static class ParTransferencia{
    /**
     * Características de la transferencia.
     */
    DIRTransferencias tran;
    /**
     * Conjunto de los subtrabajos que mejor cumplen con esa
     * transferencia.
     */
    Collection subtrabajos;
    ParTransferencia(DIRTransferencias tran0,Collection sub0){
      tran=tran0;
      subtrabajos=sub0;
    }
  }  
  
  /**
   * Busca en la base de datos un subtrabajo en espera con la carga de 
   * trabajo indicada según el signo de (des)igualdad.
   * <li>El subtrabajo pertenece a la computadora local.</li>
   * @param signo Signo de (des)igualdad.
   * @param medida Tamaño de la carga de trabajo.
   * @return El subtrabajo encontrado.
   */
  private ADMINAPPISub_trabajos buscaSubtrabajoLocalEsperaPorCarga(
      String signo,double medida) {
    return buscaSubtrabajoEsperaPorCarga(signo,medida,true);
  }
  /**
   * Busca en la base de datos un subtrabajo en espera con la carga de 
   * trabajo indicada según el signo de (des)igualdad.
   * <li>El subtrabajo pertenece a una computadora externa.</li>
   * @param signo Signo de (des)igualdad.
   * @param medida Tamaño de la carga de trabajo.
   * @return El subtrabajo encontrado.
   */
  private ADMINAPPISub_trabajos buscaSubtrabajoExternoEsperaPorCarga(
      String signo,double medida) {
    return buscaSubtrabajoEsperaPorCarga(signo,medida,false);
  }
  /**
   * Busca en la base de datos un subtrabajo en espera con la carga de 
   * trabajo indicada según el signo de (des)igualdad.
   * @param signo Signo de (des)igualdad.
   * @param medida Tamaño de la carga de trabajo.
   * @param siLocal Indica si el subtrabajo a buscar debe ser local
   * o externo.
   * @return El subtrabajo encontrado.
   */
  private ADMINAPPISub_trabajos buscaSubtrabajoEsperaPorCarga(
      String signo,double medida,boolean siLocal) {
    String sSQL;
    String sSubSQL;
    String sSubtrabajo;
    String sTarea;
    ResultSet rs;
    ADMINAPPISub_trabajos stra=null;
    ADMINAPPControladores cont=null;
    if(siLocal){
      sSubSQL="SUBSTRING(su.id_tarea,1,LENGTH(su.id_tarea)-10)="+
          tdutils.getQ(_desc.getNombreComputadora());
    }else{
      sSubSQL="SUBSTRING(su.id_tarea,1,LENGTH(su.id_tarea)-10)!="+
          tdutils.getQ(_desc.getNombreComputadora());
    }
    sSQL=
        "SELECT su.id_tarea,su.id_subtrabajo " +
        "FROM sub_trabajos AS su LEFT OUTER JOIN envolturas AS en " +
        " ON su.id_tarea=en.id_tarea "+
        "WHERE su.id_subtrabajo=en.id_subtrabajo " +
        " AND (en.si_actual=null) " +
        " AND "+ sSubSQL +
        " AND (su.carga "+signo.trim()+" "+ String.valueOf(medida) +")";
    try {
      rs=OIPersistente.getRSSQL(_desc, sSQL);
      // ...
      if(rs.next()){
        sTarea=rs.getString("id_tarea");
        sSubtrabajo=rs.getString("id_subtrabajo");
        synchronized(_controladores){
          cont=(ADMINAPPControladores)_controladores.get(sTarea);
        }
        if(cont!=null){
          stra=(ADMINAPPISub_trabajos)cont._tarea._sub_trabajos.
                get(sSubtrabajo);
        }
      }
      rs.close();
    } catch (OIExcepcion e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return stra;
  }
  
  /**
   * Busca en la base de datos los subtrabajos en espera con la carga de 
   * trabajo indicada según el signo de (des)igualdad.
   * <li>Los subtrabajos encontrados se ordenan por orden ascendente
   * de carga de trabajo.</li>
   * <li>Si el subtrabajo está en el conjunto de excepciones entonces
   * no se elige (ya fue elegido en la misma planificación).</li>
   * @param signo Signo de (des)igualdad.
   * @param medida Tamaño de la carga de trabajo.
   * @param siLocal Indica si el subtrabajo a buscar debe ser local
   * @param excepciones Conjunto de excepciones.
   * o externo.
   * @return Los subtrabajos encontrados.
   */
  private Map buscaSubtrabajosEsperaPorCarga(
      String signo,double medida,boolean siLocal,Map excepciones) {
    String sSQL;
    String sSubSQL;
    String sSubtrabajo;
    String sTarea;
    Map mapSubtrabajos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    ResultSet rs;
    ADMINAPPISub_trabajos stra=null;
    ADMINAPPControladores cont=null;
    if(siLocal){
      sSubSQL="SUBSTRING(su.id_tarea,1,LENGTH(su.id_tarea)-10)="+
          tdutils.getQ(_desc.getNombreComputadora());
    }else{
      sSubSQL="SUBSTRING(su.id_tarea,1,LENGTH(su.id_tarea)-10)!="+
          tdutils.getQ(_desc.getNombreComputadora());
    }
    sSQL=
        "SELECT su.id_tarea,su.id_subtrabajo " +
        "FROM sub_trabajos AS su LEFT OUTER JOIN envolturas AS en " +
        " ON su.id_tarea=en.id_tarea "+
        "WHERE su.id_subtrabajo=en.id_subtrabajo " +
        " AND (en.si_actual=null) " +
        " AND "+ sSubSQL +
        " AND (su.carga "+signo.trim()+" "+ String.valueOf(medida) +")" +
        " ORDER BY su.carga asc ";
    try {
      rs=OIPersistente.getRSSQL(_desc, sSQL);
      // ...
      if(rs.next()){
        synchronized(_controladores){
          while(rs.next()){
            sTarea=rs.getString("id_tarea");
            sSubtrabajo=rs.getString("id_subtrabajo");
            if(excepciones.get(sTarea+"|"+sSubtrabajo)!=null){
              // excluye a la compu de las estudiables
              continue;
            }
              cont=(ADMINAPPControladores)_controladores.get(sTarea);
            if(cont!=null){
              stra=(ADMINAPPISub_trabajos)cont._tarea._sub_trabajos.
                    get(sSubtrabajo);
              mapSubtrabajos.put(String.valueOf(mapSubtrabajos.size()+1), stra);
            }
          }
        }
      }
      rs.close();
    } catch (OIExcepcion e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return mapSubtrabajos;
  }
  /**
   * Busca en la base de datos los subtrabajos en marcha con la carga de 
   * trabajo indicada y el tiempo de ejecución indicado según los signos
   * de desigualdad o igualdad indicados.
   * <li>Los subtrabajos encontrados se ordenan por orden ascendente
   * de tiempo en ejecución.</li>
   * <li>Si el subtrabajo está en el conjunto de excepciones entonces
   * no se elige (ya fue elegido en la misma planificación).</li>
   * @param signo_medida Signo de (des)igualdad para filtrar por carga.
   * @param medida Tamaño de la carga de trabajo.
   * @param signo_tiempo Signo de (des)igualdad para filtrar por tiempo 
   * de estar en marcha.
   * @param tiempo_s Tiempo a usar como filtro según el signo de comporación.
   * @param excepciones Conjunto de excepciones.
   * @return Los subtrabajos encontrados con las características de carga
   * y tiempo dados.
   */
  private Map buscaSubtrabajosMarchaPorCargaTiempo(
      String signo_medida,double medida,String signo_tiempo,int tiempo_s,Map excepciones) {
    String sSQL;
    String sSubSQL;
    String sSubtrabajo;
    String sTarea;
    Map mapSubtrabajos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    ResultSet rs;
    ADMINAPPISub_trabajos stra=null;
    ADMINAPPControladores cont=null;
    sSQL=
        "SELECT su.id_tarea,su.id_subtrabajo, " +
        "(en.hora_inicio-en.hora_solicitud) AS tiempo_marcha " +
        "FROM sub_trabajos AS su INNER JOIN envolturas AS en " +
        " ON su.id_tarea=en.id_tarea and su.id_subtrabajo=en.id_subtrabajo "+
        "WHERE (en.si_actual=true) " +
        " AND (su.carga "+signo_medida.trim()+" "+ String.valueOf(medida) +")" +
        " AND ((en.hora_inicio-en.hora_solicitud) "+signo_tiempo.trim()+" " + 
        String.valueOf(tiempo_s*1000) +")" +
        " ORDER BY 3 asc ";
    try {
      rs=OIPersistente.getRSSQL(_desc, sSQL);
      // ...
      if(rs.next()){
        synchronized(_controladores){
          while(rs.next()){
            sTarea=rs.getString("id_tarea");
            sSubtrabajo=rs.getString("id_subtrabajo");
            if(excepciones.get(sTarea+"|"+sSubtrabajo)!=null){
              // excluye a la compu de las estudiables
              continue;
            }
              cont=(ADMINAPPControladores)_controladores.get(sTarea);
            if(cont!=null){
              stra=(ADMINAPPISub_trabajos)cont._tarea._sub_trabajos.
                    get(sSubtrabajo);
              mapSubtrabajos.put(String.valueOf(mapSubtrabajos.size()+1), stra);
            }
          }
        }
      }
      rs.close();
    } catch (OIExcepcion e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return mapSubtrabajos;
  }  
  /**
   * Busca en la base de datos un subtrabajo en espera con la carga de 
   * trabajo indicada según el signo de (des)igualdad.
   * <li>La consulta a la base de datos identifica la tabla sub_trabajos 
   * como 'su' y la tabla envolturas como 'en'.</li>
   * <br>La consulta, en SQL, es la siguiente, incluyendo el fitro</br>
<pre>
    SELECT su.id_tarea,su.id_subtrabajo
    FROM sub_trabajos AS su LEFT OUTER JOIN envolturas AS en
     ON su.id_tarea=en.id_tarea
    WHERE su.id_subtrabajo=en.id_subtrabajo
     AND (en.si_actual=null)
     AND ( sFiltro );
</pre>
   * @param sFiltro El criterio de búsqueda de un subtrabajo.
   * @return El subtrabajo encontrado.
   */
  private ADMINAPPISub_trabajos buscaSubtrabajo(
      String sFiltro) {
    String sSQL;
    String sSubtrabajo;
    String sTarea;
    ResultSet rs;
    ADMINAPPISub_trabajos stra=null;
    ADMINAPPControladores cont=null;
    sSQL=
        "SELECT su.id_tarea,su.id_subtrabajo " +
        "FROM sub_trabajos AS su LEFT OUTER JOIN envolturas AS en " +
        " ON su.id_tarea=en.id_tarea "+
        "WHERE su.id_subtrabajo=en.id_subtrabajo " +
        " AND (en.si_actual=null) " +
        " AND ("+ sFiltro +")";
    try {
      rs=OIPersistente.getRSSQL(_desc, sSQL);
      // ...
      if(rs.next()){
        sTarea=rs.getString("id_tarea");
        sSubtrabajo=rs.getString("id_subtrabajo");
        synchronized(_controladores){
          cont=(ADMINAPPControladores)_controladores.get(sTarea);
        }
        if(cont!=null){
          stra=(ADMINAPPISub_trabajos)cont._tarea._sub_trabajos.
                get(sSubtrabajo);
        }
      }
      rs.close();
    } catch (OIExcepcion e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return stra;
  }
  /**
   * Busca en la base de datos los subtrabajos que cumplan con el 
   * criterio indicado.
   * <li>La consulta a la base de datos identifica la tabla sub_trabajos 
   * como 'su' y la tabla envolturas como 'en'.</li>
   * <li>Los subtrabajos se devuelven según el orden indicado.</li>
   * <br>La consulta, en SQL, es la siguiente, incluyendo el fitro 
   * y el orden</br>
<pre>
    SELECT su.id_tarea,su.id_subtrabajo
    FROM sub_trabajos AS su LEFT OUTER JOIN envolturas AS en
     ON su.id_tarea=en.id_tarea
    WHERE su.id_subtrabajo=en.id_subtrabajo
     AND (en.si_actual=null)
     AND ( sFiltro )
     ORDER BY sOrden;
</pre>
   * @param sFiltro El criterio de búsqueda de un subtrabajo.
   * @param sOrden El orden con el que van los registros de salida.
   * @return Los subtrabajos encontrados en el orden establecido.
   */
  private Map buscaSubtrabajosEspera(
      String sFiltro,String sOrden) {
    String sSQL;
    String sSubtrabajo;
    String sTarea;
    ResultSet rs;
    ADMINAPPISub_trabajos stra=null;
    ADMINAPPControladores cont=null;
    Map mapSubtrabajos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    sSQL=
        "SELECT su.id_tarea,su.id_subtrabajo " +
        "FROM sub_trabajos AS su LEFT OUTER JOIN envolturas AS en " +
        " ON su.id_tarea=en.id_tarea "+
        "WHERE su.id_subtrabajo=en.id_subtrabajo " +
        " AND (en.si_actual IS NULL) " +
        " AND (su.si_fin IS NULL OR su.si_fin<>TRUE) "+
        " AND ("+ sFiltro +")" +
        " ORDER BY "+sOrden;
    try {
      rs=OIPersistente.getRSSQL(_desc, sSQL);
      synchronized(_controladores){
        while(rs.next()){
          sTarea=rs.getString("id_tarea");
          sSubtrabajo=rs.getString("id_subtrabajo");
            cont=(ADMINAPPControladores)_controladores.get(sTarea);
          if(cont!=null){
            /*
             * el controlador existe
             */
            stra=(ADMINAPPISub_trabajos)cont._tarea._sub_trabajos.
                  get(sSubtrabajo);
            if(stra!=null && stra.getEstadoSubtrabajo().compareTo(ADMINAPPISub_trabajos.SUBTRA_ESPERA)==0){
              mapSubtrabajos.put(String.valueOf(mapSubtrabajos.size()+1), stra);
            }
          }else{
            /*
             * el controlador no existe
             * ------------------------
             * esto ya no va a suceder porque desde un principio se llama a
             * this.recargaTrabajos()
             */
            cont=this.cargaControlador(sTarea);
          }
        }
      }
      rs.close();
    } catch (OIExcepcion e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return mapSubtrabajos;
  }  
  String consulta(){
  	return ""; 
  }
  void demoraSubTrabajos(){
    
  }
  void informaDisturbioCargaAplicacion(){
    
  }
  void informaDisturbioCargaFuncional(){
    
  }
  void iniciaTareasSinAtender(){
    
  }
  void intentaRetornos(){
    
  }
  void manejaEvento(int CODIGO, Object objeto){
    /*
     * despachador.inicietarea
     * despachador.terminetarea
     * despachador.exportatarea
     * despachador.iniciasubtrabajo
     * despachador.consultasubtrabajo
     * despachador.terminarsubtrabajo
     * despachador.exportarsubtabajo
     */
  }
  /**
   * Crea una instancia controladora de tareas y la agrega a 
   * la lista de controladores.
   * @param trabajos La descripción de la tarea a controlar.
   * @return La nueva instancia controladora.
   */
  ADMINAPPControladores nuevoControlador(ADMINAPPTrabajos trabajos){
    ADMINAPPControladores control=null;
    /// recordar que el id del controlador es el mismo de la tarea.
    String id_controlador="";
    /*
     * @TODO 2005: Debe verificarse que ya exista el controlador.
     *             En caso de darse, deben unirse las listas
     *             de subtrabajos. 
     * -----
     * 1) Buscar identificación del controlador en la tabla 
     *     controladores usando como llave a id_tarea.
     * 2.1) Si se encuentra entonces debe usarse el controlador existente
     *      y unir a los trabajos ya existentes el nuevo trabajo. Esto último
     *      significa que la tarea del controlador va a adoptar en este caso 
     *      un nuevo subtrabajo.
     * 2.2) Si no se encuentra el controlador entonces debe crearse para la tarea
     *      solicitada. 
     */
    synchronized(_controladores){
      id_controlador=ADMINAPPControladores.getControlador(_desc,trabajos);
      if(id_controlador !=""){
        // ya hay un controlador
        control=(ADMINAPPControladores)_controladores.get(id_controlador);
        if(control==null){
          // el controlador debería de cargarse de la base de datos
          // esto no debería suceder
        }
      }
      if(control==null){
        control=new ADMINAPPControladores(_desc,trabajos);
        control.setSiActivo(true);
        _controladores.put(control.getIdentificacion(), control);        
      }
    }
    // <2005 se actualiza el control/>
    try {
      control.write();
    } catch (OIExcepcion e) {
      e.printStackTrace();
    }
    return control;
  }
  /**
   * Carga el último controlador que estuvo a cargo de la tarea, reconstruye las
   * instancias que se relacionan con un coordinador.
   * @param trabajos Identificación de la tarea.
   * @return El controlador instanciado correctamente.
   */
  ADMINAPPControladores cargaControlador(String tarea){
    ADMINAPPControladores control=null;
    synchronized(this._controladores){
      control=(ADMINAPPControladores)this._controladores.get(tarea);
    }
    return control;
  }

  /**
   * Obtiene el controlador que le corresponde al subtrabajo.
   * <li>Este se busca según la identificación de la tarea.</li>
   * @param subtrabajo 
   *                   Subtrabajo del controlador a buscar.
   * @return El controlador de la tarea.
   */
  ADMINAPPControladores traeControlador(ADMINAPPISub_trabajos subtrabajo){
    ADMINAPPControladores control=null;
    synchronized(this._controladores){
      control=(ADMINAPPControladores)this._controladores.get(
          subtrabajo.getIdTarea());
    }
    return control;
  }
    
  void planificaProcesos(){
    
  }
  /**
   * <b>Actividad 3</b>. Se detienen procesos si se detecta que 
   * están muy atrasados. Detiene los subtrabajos locales (solicitados al anfitrión) que se 
   * ejecutan en forma remota y que tienen bajo desempeño.
   * <li>Es decir, aquellos cuyo KeepAlive está muy atrasado.</li>
   */
  private void plan03DetieneMalaCalidad(){
    // Aquí se pone la HP regla para replanificar un subtrabajo que
    // está remoto y atrasado.
    this.reiniciaSubtrabajos(this.getTiempoReinicioSubtrabajo()*1000);
  }
  /**
   * <2005 Necesitado por plan 4'>
   * Ejecuta un plan de exportación de subtrabajos en un hilo distinto
   * al de la planificación.<br/>
   * Se realiza la exportación de todos los subtrabajos a sus correspondientes
   * nodos.
   * Aquí debe hacerse efectiva la exportación, subtrabajo
   * por subtrabajo, con una solicitud análoga a la que se
   * realiza en ADMINAPPIniciador::ADMINAPPIniciador(String)
   * <pre> 
   * Se debe procesar la siguiente estructura
   * 
   *     +-----------+----------------+
   *  ...+nombre_nodo|listasubtrabajos+...
   *     +-----------+----------------+
   *              |
   *             \ /
   *              v 
   *     +------+---------+
   *  ...+String|ArrayList+...
   *     +------+---------+
   *              |
   *             \ /
   *              v 
   *     +------+-----------------------+
   *  ...+String|[ADMINAPPISub_trabajos]+...
   *     +------+-----------------------+
   * </pre>
   * @param planexportacion Mapa con la estructura elemental de la forma (nombre_nodo,listasubtrabajos)
   */
  private void exportaSubtrabajos(Map planexportacion){
    String nombrenodo="";
    Map.Entry pareja=null;
    Iterator itrI=null;
    Iterator itrJ=null;
    List listasubtrabajos=null;
    ADMINAPPISub_trabajos subtrabajo=null;
    ADMINAPPMetodoSolicitud solicitudMensaje=null;
    OACTDistribuidorBase base=this._desc.getDespachador().getDistribuidor();
    ADMINAPPDespachadorProxy proxy=null;
    itrI=planexportacion.entrySet().iterator();
    while(itrI.hasNext()){
      // procesa cada tupla (nodo,listasubtrabajos)
      pareja=(Map.Entry)itrI.next();
      nombrenodo=(String)pareja.getKey();
      listasubtrabajos=(List)pareja.getValue();
      /*
       * un proxy por destino para la ejecución remota
       */
      proxy=new ADMINAPPDespachadorProxy();
      proxy.setDistribuidor(base);
      proxy.setDestino(nombrenodo);
      itrJ=listasubtrabajos.iterator();
      while(itrJ.hasNext()){
        subtrabajo=(ADMINAPPISub_trabajos)itrJ.next();
        boolean error=false;
        if(subtrabajo.getEstadoSubtrabajo().compareToIgnoreCase(ADMINAPPISub_trabajos.SUBTRA_ESPERA)!=0){
          continue;
        }
        /*
         * crea la solicitud de la ejecución remota del subtrabajo
         * actual
         */
        solicitudMensaje=new ADMINAPPMetodoSolicitud();
        solicitudMensaje.setNombreDestino(proxy.getDestino());
        solicitudMensaje.setSubtrabajo(subtrabajo);
        
        proxy.setSolicitudMensaje(solicitudMensaje);
        PERSCoordinacion.Sub_trabajos subtrabajoreal=(PERSCoordinacion.Sub_trabajos)subtrabajo;
        try {
          
//          subtrabajoreal.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_ENEXPORTACION);
//          subtrabajoreal.write();
          
          proxy.ejecuta("subtrabajo",new String[1]);
          this._desc.println("planificador","exporta hacia: "+nombrenodo+" subtrabajo: "+subtrabajo.getIdTarea()+":"+subtrabajo.getIdSubtrabajo());
          /*
           * en este punto la exportación del subtrabajo está garantizada,
           * se escribe el estado y se hace commit
           */
          subtrabajo.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_EXTERNO);
          if(subtrabajoreal.siLocal()){
            subtrabajoreal.setContRemoto(nombrenodo);
          }
          /*
           * debe registrarse un mapa de las tareas de un nodo en el
           * mapa de los nodos con subtrabajos externos
           */
        } catch (ADMINAPPExcepcion e) {
          e.printStackTrace();
          error=true;
        }
        if(error){
          subtrabajo.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_ESPERA);
          // this._desc.println("planificador","no exporta hacia: "+nombrenodo+" subtrabajo: "+subtrabajo.getIdTarea()+":"+subtrabajo.getIdSubtrabajo());
        }
        try {
          subtrabajoreal.write();
          this._desc.getConex().dbCommit();
        } catch (ADMINGLOExcepcion e) {
          e.printStackTrace();
        } catch (OIExcepcion e) {
          e.printStackTrace();
        }
        
      }
    }
  }
  /**
   * <2006/>
   * Exporta subtrabajos terminados a su lugar de origen.
   * <li>Se encarga de actualizar sus estados en el agente local,
   * dada la confirmación de su importación en el sistema interesado.</li>
   * @param exportaciones Mapa de subtrabajos a exportar.
   */
  private void  exportaSubtrabajosTerminados(Map exportaciones){
    String nombrenodo="";
    Map.Entry pareja=null;
    Iterator itrI=null;
    Iterator itrJ=null;
    List listasubtrabajos=null;
    ADMINAPPISub_trabajos subtrabajo=null;
    ADMINAPPMetodoSolicitud solicitudMensaje=null;
    OACTDistribuidorBase base=this._desc.getDespachador().getDistribuidor();
    ADMINAPPDespachadorProxy proxy=null;
    PERSCoordinacion.Sub_trabajos subtrabajoreal=null;
    itrI=exportaciones.entrySet().iterator();
    while(itrI.hasNext()){
      // procesa cada tupla (nodo,listasubtrabajos)
      pareja=(Map.Entry)itrI.next();
      nombrenodo=(String)pareja.getKey();
      listasubtrabajos=(List)pareja.getValue();
      /*
       * un proxy por destino para la devolución
       */
      proxy=new ADMINAPPDespachadorProxy();
      proxy.setDistribuidor(base);
      proxy.setDestino(nombrenodo);
      itrJ=listasubtrabajos.iterator();
      while(itrJ.hasNext()){
        subtrabajo=(ADMINAPPISub_trabajos)itrJ.next();
        /*
         * crea la solicitud de la devolución del subtrabajo
         * actual
         */
        solicitudMensaje=new ADMINAPPMetodoSolicitud();
        solicitudMensaje.setNombreDestino(proxy.getDestino());
        solicitudMensaje.setSubtrabajo(subtrabajo);
        /*
         * importante: 
         *            subtrabajo.si_fin
         * indica el final del subtrabajo, tal información,
         * se reitera, está en el estado del subtrabajo mismo.
         *                                 |
         *                                 |
         *                                 v
         */
        solicitudMensaje.setSubtrabajo(subtrabajo);
        /*
         *                                 ^
         *                                 |
         *                                 |
         */
        proxy.setSolicitudMensaje(solicitudMensaje);
        try {
          proxy.ejecuta("subtrabajo",new String[1]);
          /*
           * debería hacerse una verificación de si en verdad
           * las novedades del subtrabajo fueron recibidas
           * por el nodo destino.
           * Se realiza en ADMINGLOGestionExportadora.open()
           */
        }catch (ADMINAPPExcepcion e) {
          e.printStackTrace();
        }
      }
    }
  }
  /**
   * <2006 />
   * Busca en la base de datos el conjunto de los subtrabajos terminados 
   * para devolver resultados al nodo solicitante y así intentar sus retornos al usuario.
   * @return El mapa con los subtrabajos terminados, conteniendo la pareja (nodo,(subtrabajos)).
   */
  private Map buscaSubtrabajosTerminados() {
    String sSQL;
    String sSubSQL;
    String sSubtrabajo;
    String sTarea;
    String sNodo;
    String sOldNodo="";
    String likecompu;
    Map mapSubtrabajos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    ArrayList subtrabajosejecutar=null;
    ResultSet rs;
    ADMINAPPISub_trabajos stra=null;
    ADMINAPPControladores cont=null;
    likecompu = this._desc.getComputadora().getNombre() + "%";
    sSQL=
        " SELECT SU.id_tarea,SU.id_subtrabajo " +
        " FROM sub_trabajos AS SU " +
        " INNER JOIN Tareas AS T ON T.id_tarea=SU.id_tarea " +
        " WHERE SU.estado_subtrabajo IN ("+tdutils.getQ(ADMINAPPISub_trabajos.SUBTRA_FIN)+","+tdutils.getQ(ADMINAPPISub_trabajos.SUBTRA_FIN2)+")" +
        " AND T.estado_tarea<>" +tdutils.getQ(ADMINAPPITareas.TAREA_FIN) +
        " AND T.estado_tarea<>" +tdutils.getQ(ADMINAPPITareas.TAREA_ENTREGADA) +
        " AND (NOT T.id_tarea LIKE "+tdutils.getQ(likecompu) +")" +
        " ORDER BY SU.id_tarea,SU.id_subtrabajo ";
    try {
      rs=OIPersistente.getRSSQL(_desc, sSQL);
      if(rs.next()){
        synchronized(_controladores){
          {
            // Es mejor exportar a partir de un mapa con el formato (nodo,subtrabajos:map)
            sTarea=rs.getString("id_tarea");
            sSubtrabajo=rs.getString("id_subtrabajo");
            cont=(ADMINAPPControladores)_controladores.get(sTarea);
            if(cont!=null){
              stra=(ADMINAPPISub_trabajos)cont._tarea._sub_trabajos.
                    get(sSubtrabajo);
              // hay subtrabajo que enlistar
              // busquemos el nodo que le corresponde
              sNodo=stra.getTarea().getNodoCreador();
              if(sNodo.compareToIgnoreCase(sOldNodo)!=0){
                // primera vez: no hay una lista de exportaciones hacia ese nodo
                subtrabajosejecutar=new ArrayList();
                mapSubtrabajos.put(sNodo,subtrabajosejecutar);
                sOldNodo=sNodo;
              }
              // ponemos el subtrabajo en la lista de subtrabajos del nodo
              subtrabajosejecutar.add(stra);
            }
          }while(rs.next());
        }
      }
      rs.close();
    } catch (OIExcepcion e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return mapSubtrabajos;
  }  
  /**
   * <2006 />
   * Reinicia los subtrabajos remotos que llevan cierto tiempo
   * de no reportarse como en marcha.
   * <li>Los subtrabajos que cambian este estado son aquellos
   * que tienen su estado en SUBTRA_EXTERNO.</li>
   * @param tiempoMilisegundos Milisegundos en los que no se tiene
   * noticia de un subtrabajo.
   */
  private void reiniciaSubtrabajos(long tiempoMilisegundos) {
    long tiempoActual=0;
    Iterator itrI=null;
    Iterator itrJ=null;
    PERSCoordinacion.Sub_trabajos stra=null;
    PERSCoordinacion.Conts_Externos cext=null;
    ADMINAPPControladores cont=null;
    tiempoActual=System.currentTimeMillis();
    if(tiempoMilisegundos <1){
      tiempoMilisegundos=this.getTiempoReinicioSubtrabajo()*1000;
    }
    synchronized(this._controladores){
      itrI=this._controladores.values().iterator();
      while(itrI.hasNext()){
        cont=(ADMINAPPControladores)itrI.next();
        if(cont.siLocal() && cont.getTarea().getEstadoTarea().compareTo(ADMINAPPITareas.TAREA_MARCHA)==0){
          /*
           * actividad de interés solo si 
           * tdderive es el coordinador de la tarea
           */
          itrJ=cont.getSubtrabajos().values().iterator();
          stra=null;
          while(itrJ.hasNext()){
            stra=(PERSCoordinacion.Sub_trabajos)itrJ.next();
            /*
             * nos interesa la perspectiva del controlador local,
             * quien sabe qué subtrabajos han sido terminados
             */
            if(stra.getEstadoSubtrabajo().compareTo(
            ADMINAPPISub_trabajos.SUBTRA_EXTERNO)==0){
              cext=stra.getControladorExterno();
              if(cext!=null){
                if(tiempoActual-cext.getHoraEjecucionRemota()>tiempoMilisegundos){
                  // hay un exceso de tiempo, el subtrabajo debe ser replanificado
                  stra.setEstadoSubtrabajo(ADMINAPPISub_trabajos.SUBTRA_ESPERA);
                  this._desc.println("planificador","detiene subtrabajo: "+stra.getIdTarea()+":"+stra.getIdSubtrabajo());
                  try {
                    stra.write();
                  } catch (ADMINGLOExcepcion e1) {
                    e1.printStackTrace();
                  }
                }
              }
            }
          }
          if(stra!=null){
            try {
              stra.getDescriptor().getConex().dbCommit();
            } catch (OIExcepcion e1) {
              e1.printStackTrace();
            }
          }
        }
      }
    }
  }  

  private Map buscaSubtrabajosTerminadosOld() {
    // no era económica
    String sSQL;
    String sSubSQL;
    String sSubtrabajo;
    String sTarea;
    String sNodo;
    String likecompu;
    Map mapSubtrabajos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    ArrayList subtrabajosejecutar=null;
    ResultSet rs;
    ADMINAPPISub_trabajos stra=null;
    ADMINAPPControladores cont=null;
    likecompu = this._desc.getComputadora().getNombre() + "%";
    sSQL=
        " SELECT SU.id_tarea,SU.id_subtrabajo " +
        " FROM sub_trabajos AS SU " +
        " INNER JOIN Tareas AS T ON T.id_tarea=SU.id_tarea " +
        " WHERE SU.estado_subtrabajo="+tdutils.getQ(ADMINAPPISub_trabajos.SUBTRA_FIN) +
        " AND T.estado_tarea<>" +tdutils.getQ(ADMINAPPITareas.TAREA_FIN) +
        " AND T.estado_tarea<>" +tdutils.getQ(ADMINAPPITareas.TAREA_ENTREGADA) +
        " AND (NOT T.id_tarea LIKE "+tdutils.getQ(likecompu) +")" +
        " ORDER BY SU.id_tarea,SU.id_subtrabajo ";
    try {
      rs=OIPersistente.getRSSQL(_desc, sSQL);
      if(rs.next()){
        synchronized(_controladores){
          while(rs.next()){
            // Es mejor exportar a partir de un mapa con el formato (nodo,subtrabajos:map)
            sTarea=rs.getString("id_tarea");
            sSubtrabajo=rs.getString("id_subtrabajo");
            cont=(ADMINAPPControladores)_controladores.get(sTarea);
            if(cont!=null){
              stra=(ADMINAPPISub_trabajos)cont._tarea._sub_trabajos.
                    get(sSubtrabajo);
              // hay subtrabajo que enlistar
              // busquemos el nodo que le corresponde
              sNodo=stra.getTarea().getNodoCreador();
              // veamos si ya hay una lista de exportaciones hacia ese nodo
              subtrabajosejecutar=(ArrayList)mapSubtrabajos.get(sNodo);
              if(subtrabajosejecutar==null){
                // creamos lista de exportaciones y la agregamos al mapa
                subtrabajosejecutar=new ArrayList();
                mapSubtrabajos.put(sNodo,subtrabajosejecutar);
              }
              // ponemos el subtrabajo en la lista de subtrabajos del nodo
              subtrabajosejecutar.add(stra);
            }
          }
        }
      }
      rs.close();
    } catch (OIExcepcion e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return mapSubtrabajos;
  }
  /**
   * Registra una invocación en la lista de espera.
   * @param llave Se supone que es el id de una tarea.
   * @param invocable Se supone que es la devolución del resultado de
   * la tarea (archivo) al usuario.
   */
  public void registraInvocacion(String llave,Invocable invocable){
    synchronized(this._esperaRespuestas){
      this._esperaRespuestas.put(llave,invocable);
    }
  }
  /**
   * Realiza la invocación de una entrada de la lista de espera.
   * @param llave Se supone que es el id de una tarea.
   * @return Si se pudo ejecutar la invocación que le corresponde a la tarea.
   */
  public boolean realizaInvocacion(String llave){
    Invocable invocable=null;
    boolean res=false;
    synchronized(this._esperaRespuestas){
      invocable=(Invocable)this._esperaRespuestas.get(llave);
      if(invocable!=null){
        try {
          invocable.invoca(null);
          res=true;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return res;
  }
  /**
   * Recarga los trabajos del sistema, en caso de que
   * éste haya sido detenido, de tal manera que se recuperan el 
   * controlador y solicitante últimos de cada trabajo.
   * <li>Solamente debería de ejecutarse una vez, el punto
   * más apropiado es cuando se abre la administración 
   * del sistema (ADMINGLOPrograma._abreAdministracion()).</li>
   *
   */
  final void recargaTrabajos(){
    /*
     * [1] instanciarse cada tarea
     * [2] debe reconstruirse el solicitante de la tarea
     * [3] debe instanciarse el controlador
     * [4] deben instanciarse los subtrabajos de la tarea
     */
    ResultSet rs=null,rs2=null;
    String sql="";
    String id_tarea="";
    ADMINAPPTrabajos trabajo=null;
    PERSCoordinacion.Tareas tarea=null;
    PERSCoordinacion.Solicitantes solicitante=null;
    ADMINAPPControladores controlador=null;
    PERSCoordinacion.Sub_trabajos subtrabajo=null;
    String id_subtrabajo="";
    /*
     * primero se trae la información de la tarea
     */
    sql=" SELECT T.* FROM Controladores AS C " +
        " INNER JOIN Tareas AS T ON T.id_tarea=C.id_tarea " +
        " WHERE C.si_activo=TRUE";
    try {
      rs=OIPersistente.getRSSQL(this._desc,sql);
      while(rs.next()){
        tarea=new PERSCoordinacion.Tareas(this._desc,true);
        tarea.openTarea(rs);
        tarea.loadPrograma();
        id_tarea=tarea.getIdTarea();
        /*
         * ahora trae sus subtrabajos 
         */
        try{
          sql=" SELECT * FROM Sub_trabajos WHERE id_tarea="+tdutils.getQ(id_tarea);
          rs2=OIPersistente.getRSSQL(this._desc,sql);
          while(rs2.next()){
            id_subtrabajo=rs2.getString("id_subtrabajo");
            subtrabajo=new PERSCoordinacion.Sub_trabajos(id_subtrabajo,tarea);
            subtrabajo.openRS(rs2);
          }
          rs2.close();
          tarea.reconstruyeListaSubtrabajos();          
        } catch (OIExcepcion e) {
          e.printStackTrace();
        } catch (SQLException e) {
          e.printStackTrace();
        }
        /*
         * luego se trae la información del solicitante
         */
        sql="SELECT S.* FROM Solicitantes AS S " +
            " INNER JOIN Sol_Tar AS ST ON S.id_parcial=ST.id_parcial AND S.id_grupo=ST.id_grupo AND S.id_padre=ST.id_padre " +
            " INNER JOIN Controladores AS C ON C.id_tarea=ST.id_tarea AND C.pid_parcial=ST.id_parcial" +
            " WHERE C.si_activo=TRUE AND C.id_tarea="+tdutils.getQ(id_tarea);
        try{
          rs2=OIPersistente.getRSSQL(this._desc,sql);
          if(rs2.next()){
            solicitante=new PERSCoordinacion.Solicitantes(this._desc,true);
            solicitante.openSolicitante(rs2);
          }
          rs2.close();
        } catch (OIExcepcion e) {
          e.printStackTrace();
        } catch (SQLException e) {
          e.printStackTrace();
        }
        solicitante.setTarea(tarea);
        /*
         * por último se carga el controlador
         */
        trabajo=new ADMINAPPTrabajos(tarea,solicitante);        
        controlador=ADMINAPPControladores.cargaControlador(this._desc,trabajo);
        controlador.setSiActivo(true);
        synchronized(_controladores){
            _controladores.put(controlador.getIdentificacion(), controlador);        
        }
      }
      rs.close();
      
    } catch (OIExcepcion e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  /**
   * Envía un mensaje al nodo coordinador de un subtrabajo para indicarle que
   * su subtrabajo se está procesando en el tdderive local.
   * @param subtrabajo El subtrabajo que este nodo está ejecutando.
   */
  private void notificaSigueVivo(PERSCoordinacion.Sub_trabajos subtrabajo){
    String destino=null;
    ADMINAPPMetodoSolicitud solicitudMensaje=null;
    ADMINAPPDespachadorProxy proxy=null;
    OACTDistribuidorBase base=this._desc.getDespachador().getDistribuidor();    
    destino=subtrabajo.getNodoCreador();
    proxy=new ADMINAPPDespachadorProxy();
    proxy.setDistribuidor(base);
    proxy.setDestino(destino);
    solicitudMensaje=new ADMINAPPMetodoSolicitud();
    solicitudMensaje.setNombreDestino(proxy.getDestino());
    solicitudMensaje.setSubtrabajo(subtrabajo);
    proxy.setSolicitudMensaje(solicitudMensaje);
    try{
      proxy.ejecuta("subtrabajo_vivo",new String[1]);
      // YUPIIII, YUPIIIIII!!!!!!!!!!!!!!!!!!!!!!!!!!!!!, AUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUHUUHUUUAUUUUUUUUUUUUUUUUUUUUH
    }catch(ADMINAPPExcepcion e){
      e.printStackTrace();
    }
  }
  /**
   * Imprime los vecinos del servidor actual.
   *
   */
  private void imprimeVecinos(){
    // this._desc.println("planificador","ejecuta subtrabajo: "+subtrabajo.getIdTarea()+":"+subtrabajo.getIdSubtrabajo());
    Collection vecinos=this._desc.getVecinos();
    Iterator itr=null;
    PERSAmbiente.Dominios_bal vecino=null;
    DireccionesVirtuales ubicacion=null;
    itr=vecinos.iterator();
    while(itr.hasNext()){
      vecino=(PERSAmbiente.Dominios_bal)itr.next();
      ubicacion=this._desc.getUbicacion(vecino.vecino);
      if(ubicacion!=null){
        this._desc.println("planificador","conoce vecino:"+vecino.vecino+":"+ubicacion.direccionVerdadera+":"+(int)(this._desc.getPuerto()+ubicacion.puertoBase));
      }
    }
  }
  /**
   * Tiempo máximo en el que un nodo se considera
   * dentro del dominio de balance.
   */
  private long getTiempoNodoFuera(){
    if(tiempoNodoFuera==0){
      try {
        tiempoNodoFuera=(long)_desc.getPesoUmbralPolitica("NODOFUERA_INTERVALO_S");
      } catch (ADMINGLOExcepcion e) {
        tiempoNodoFuera=60*3;
      }      
    }
    return tiempoNodoFuera;
  }
  
  /**
   * Tiempo máximo de espera de una notificación de subtrabajo vivo.
   * Vencido, el subtrabajo se replanifica.
   */
  private long getTiempoReinicioSubtrabajo(){
    if(tiempoReinicioSubtrabajo==0){
      try {
        tiempoReinicioSubtrabajo=(long)_desc.getPesoUmbralPolitica("SUBREINICIO_INTERVALO_S");
      } catch (ADMINGLOExcepcion e) {
        tiempoReinicioSubtrabajo=60*3;
      }      
    }
    return tiempoReinicioSubtrabajo;
  }
  
}
