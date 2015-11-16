package oact;
import mens.*;
import aco.*;
import java.io.*;
/**
 * Clase del Objeto Activo que da forma a una solicitud y
 * que prepara la ejecución por parte de un Sirviente.
 * Métodos a sobredefinir:
 * <li>Obligatoriamente: ejecutar().</li>
 * <li>Si se quiere tener el poder de tomar control antes de ejecutar
 * para llevar a cabo prerarativos: ejecutarInicio().</li>
 * <li>Si se quiere tener la capacidad de tomar control luego de ejecutar
 * para llevar a cabo labores de cierre de ejecución: ejecutarFin().</li>
 */

public abstract class OACTSolicitud extends MENSMensaje {
  /**
   * Objeto responsable de ejecutar el mensaje.
   */
  private OACTSirvienteAbs sirviente;
  private boolean _isset_sirviente;
  /**
   * Identificación del objeto sirviente.
   */
  private String id_sirviente;
  private boolean _isset_id_sirviente;
  private aco.ACONGestor gestor;
  private int tiempo_espera;
  /**
   * Un valor null indica que la clase trabaja sola.
   * Un valor distinto de null indica que la clase necesita un complemento.
   */
  protected Class clase_complemento;
  public OACTSolicitud(OACTSirvienteAbs sirviente0) {
    setSirviente(sirviente0);
    gestor=null;
    tiempo_espera=0;
    clase_complemento=null;
  }
  public OACTSolicitud(String servantID) {
    setSirviente(null);
    setIdSirviente(servantID);
    gestor=null;
    tiempo_espera=0;
    clase_complemento=null;
  }
  public final OACTSirvienteAbs getSirviente(){
    return sirviente;
  }
  final void setSirviente(OACTSirvienteAbs sirviente0){
    if(sirviente0==null){
      return;
    }
    sirviente=sirviente0;
    _isset_sirviente=true;
    setIdSirviente(sirviente.getId());
  }
  public void loadFromSolicitud(OACTSolicitudPrimitiva sol) throws MENSException{
    this.setFromXMLSource("<?xml version=\"1.0\"?>"+sol.getContenido());
  }
  public final void setIdSirviente(String id){
    if((id!=null) && (id!="")){
      id_sirviente=id;
      _isset_sirviente=true;
    }
  }
  public final String getIdSirviente(){
    return id_sirviente;
  }
  final void setGestor(aco.ACONGestor gestor0){
    gestor=gestor0;
  }
  protected boolean conGestor(){
    return gestor!=null;
  }
  /*
   * Envía un mensaje al solicitante.
   */
  protected final void send(String msg) throws OACTExcepcion,ACONExcArbitraria, ACONExcOmision{
    if(gestor==null){
      throw new OACTExcepcion("No se ha cargado un gestor.");
    }
    synchronized(gestor){
      gestor.send(msg);
    }
  }
  /**
   * Contiene al gestor por los milisegundos indicados.
   * @param miliseconds Milisegundos a contener al gestor.
   */
  protected final void espera(long miliseconds){
    synchronized(gestor){
      try {
        gestor.wait(miliseconds);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  protected final void sendb(byte[] msg) throws OACTExcepcion,ACONExcArbitraria, ACONExcOmision{
    if(gestor==null){
      throw new OACTExcepcion("No se ha cargado un gestor.");
    }
    synchronized(gestor){
      gestor.sendb(msg);
    }
  }
  protected final void closeComm() throws OACTExcepcion{
    if(gestor==null){
      throw new OACTExcepcion("No se ha cargado un gestor.");
    }
    try {
      synchronized(gestor){      
        gestor.close();
      }
    }catch (ACONExcepcion ex) {
      throw new OACTExcepcion("No se puede cerrar el canal.",ex);
    }
  }
  /**
   * Recibe un archivo desde el canal de comunicaciones y lo guarda en
   * el sistema de archivos local, en la ubicación especificada.
   * @param directorio El directorio del archivo
   * @param nombreArchivo El nombre del archivo
   */
  protected void recibeArchivo(String directorio, String nombreArchivo) throws IOException,ACONExcepcion{
    byte[] contenidoI=new byte[10*1024]; // 10KB
    int medida=0;
    BufferedOutputStream osCompri = null;
    File archivosalida=new File(directorio+"/"+nombreArchivo);
    osCompri = new BufferedOutputStream(new FileOutputStream(
        directorio+"/"+nombreArchivo,true));
    // una contención necesaria para que
    // esté listo el archivo de salida
    while(!archivosalida.exists()){
      archivosalida.createNewFile();
    }
    contenidoI=new byte[10*1024]; // 10KB
    synchronized(gestor){    
      while((medida=gestor.receiveb(contenidoI))>=0){
        osCompri.write(contenidoI);
      }
    }
    osCompri.flush();
    osCompri.close();
  }
  /**
   * Toma un archivo ubicado en el sistema local, en el lugar especificado, y lo manda 
   * por el canal de comunicaciones.
   * @param directorio El directorio del archivo
   * @param nombreArchivo El nombre del archivo
   * @throws IOException
   * @throws ACONExcepcion
   */
  protected void mandaArchivo(String directorio, String nombreArchivo) throws IOException,ACONExcepcion{
    byte[] contenidoI=new byte[1024];
    int length=0,total=0;
    DataInputStream isSubFileReader;
    if(directorio!=""){
      nombreArchivo=directorio+"/"+nombreArchivo;
    }
    contenidoI=tdutils.tdutils.readFile(nombreArchivo);
    
    ByteArrayOutputStream baOut=new ByteArrayOutputStream(contenidoI.length);    
      isSubFileReader = new DataInputStream(
          new FileInputStream(nombreArchivo));
      synchronized(gestor){
        while(isSubFileReader.available()>0){
          length=isSubFileReader.read(contenidoI);
          baOut.write(contenidoI,0,length);
          contenidoI=baOut.toByteArray();
          gestor.sendb(contenidoI);
          contenidoI=null;
          total+=length;
        }
      }
      baOut.close();
      isSubFileReader.close();
      // System.out.println("\nEl total leído es: "+total+" bytes.\n");
    
    
  }

  protected final String receive() throws OACTExcepcion,ACONExcArbitraria,
      ACONExcOmision,ACONExcTemporizacion{
    String leido="";
    if(gestor==null){
      throw new OACTExcepcion("No se ha cargado un gestor.");
    }
    synchronized(gestor){
      gestor.setTiempoEsperaGestor(tiempo_espera);
      leido=gestor.receive();
    }
    return leido;
  }
  protected final byte[] receiveb() throws OACTExcepcion,ACONExcArbitraria,
      ACONExcOmision,ACONExcTemporizacion{
    byte[] leido=null;
    if(gestor==null){
      throw new OACTExcepcion("No se ha cargado un gestor.");
    }
    synchronized(gestor){
      gestor.setTiempoEsperaGestor(tiempo_espera);
      leido=gestor.receiveb();
    }
    return leido;
  }
  /**
   * Recibe parte de un mensaje enviado por un canal de comunicación.
   * La información de en cuál puerto se recibe el mensaje
   * se asigna en la inicialización del objeto.
   * <li>Lee el mensaje enviado por el canal de comunicación de una forma
   * parcial.</li>
   * <li>Se devuleve -1 si ya no hay datos que leer del canal de 
   * comunicación.</li>
   * @param buff Buffer a escribir con lo leído.
   * @return La cantidad de bytes leídos y dejados en el buffer.
   * @throws OACTExcepcion En caso de error.
   * @throws ACONExcArbitraria En caso de error.
   * @throws ACONExcOmision En caso de error.
   * @throws ACONExcTemporizacion En caso de error.
   */
  protected final int receiveb(byte[] buff) throws OACTExcepcion,
      ACONExcArbitraria,ACONExcOmision,ACONExcTemporizacion{
    int nbytes=0;
    if(gestor==null){
      throw new OACTExcepcion("No se ha cargado un gestor.");
    }
    synchronized(gestor){
      gestor.setTiempoEsperaGestor(tiempo_espera);
      nbytes=gestor.receiveb(buff);
    }
    return nbytes;
  }  
  protected final void setTiempoEspera(int tiempo){
    tiempo_espera=tiempo;
  }
  protected final int getTiempoEspera(){
    return tiempo_espera;
  }
  /**
   * Obtiene el puerto con el que el host remoto hace contacto.
   * @return El puerto del host remoto..
   * @throws OACTExcepcion Error si el objeto no tiene un
   * gestor de servicios.
   */
  protected final int getPuertoRemoto() throws OACTExcepcion{
    if(gestor==null){
      throw new OACTExcepcion("No se ha cargado un gestor.");
    }
    return gestor.info.remoteport;
  }
  /**
   * Obtiene la dirección del host remoto.
   * @return La dirección del host remoto.
   * @throws OACTExcepcion Error si el objeto no tiene un
   * gestor de servicios.
   */
  protected final String getHostRemoto() throws OACTExcepcion{
    if(gestor==null){
      throw new OACTExcepcion("No se ha cargado un gestor.");
    }
    return gestor.info.remotehost;
  }
  protected final Class getComplemento(){
    return clase_complemento;
  }
  protected final void setComplemento(Class oClaseComplemento){
    clase_complemento=oClaseComplemento;
  }
  OACTSolicitud ejecutarAdmin() throws OACTExcepcion,Exception{
    boolean bSiIniciado=false;
    OACTSolicitud futuro=null;
    bSiIniciado = ejecutarInicio();
    if (bSiIniciado) {
      futuro = ejecutar();
      ejecutarFin(futuro, true);
    }
    else {
      ejecutarFin(null, false);
    }
    return futuro;
  }
  /**
   * Prepara la ejecución. Puede leer de la red y escribir en ella.
   * @return Si hubo éxito en la lectura de la red.
   * @throws OACTExcepcion Si hay error.
   */
  protected boolean ejecutarInicio() throws Exception{
    return true;
  }
  /**
   * Prepara el resultado de la ejecución. Puede leer la red y escribir en
   * ella.
   * <li>Si futuro=null y siiniciado=null, entonces la operación no alcanzó
   * a ser ejecutada por ejecutar.</li>
   * @param futuro El resultado de la operación.
   * @param siiniciado Si la operación inició con éxito.
   * @throws OACTExcepcion Si hay error.
   */
  protected void ejecutarFin(OACTSolicitud futuro,boolean siiniciado) throws Exception{
    ;
  }
  /**
   * Ejecuta la operación solicitada, devolviendo en futuro la respuesta.
   * @return La respuesta dentro de un objeto OACTSolicitud.
   * @throws OACTExcepcion Si hay error.
   */
  protected abstract OACTSolicitud ejecutar() throws Exception;
  /**
   * Indica si el objeto activo local está preparado para el envío de una solicitud
   * a un objeto activo remoto.
   * @return Si el objeto activo está preparado.
   */
  public abstract boolean isPreparado();
}