package oact;
import tables.AbstractSet;

/**
 * Lista de mensajes de tareas por atender. Su acceso es sincronizado,
 * independiente del acceso a otros recursos que necesitan sincronización.
 */
public class OACTListaActivacion {
//  /**
//   * Lista de los trabajos que han sido iniciados. Algunos pueden haber terminado.
//   * Su acceso es sincronizado, independiente del acceso a otros recursos
//   * que necesitan sincronización.
//   */
//  private AbstractSet listaTrabajos;
  /**
   * Lista de mensajes de tareas por atender. Su acceso es sincronizado,
   * independiente del acceso a otros recursos que necesitan sincronización.
   */
  private AbstractSet listaMensajes;
//  private long semilla_mensajes;
//  private long semilla_trabajos;
  public OACTListaActivacion() {
    listaMensajes=new AbstractSet(new OACTSolicitudPrimitiva());
  }
  public final int addMensaje(OACTSolicitudPrimitiva sol){
    int res=0;
    int bookMark=0;
    synchronized(listaMensajes){
//      sol.setIdentificacion(Long.toString(semilla_mensajes));
      sol.incDevoluciones();
      sol.setAtendido(false);
      if(!listaMensajes.getEoF()){
        bookMark=listaMensajes.getBookmark();
        listaMensajes.moveLast();
      }
      if(listaMensajes.addNew(sol)){
        res=1;
        listaMensajes.moveTo(bookMark);
      }
    }
//    if(res==1){
//      synchronized(this){
//        semilla_mensajes++;
//      }
//    }
    return res;
  }
  public final int delMensaje(String id){
    synchronized(listaMensajes){
      listaMensajes.delete("identificacion=" + id);
    }
    return 0;
  }
  public final int updMensaje(OACTSolicitudPrimitiva sol){
    synchronized(listaMensajes){
      listaMensajes.update(sol,"identificacion="+sol.getIdentificacion());
    }
    return 0;
  }
  public final String getEstadoMensajes() {
    String res="";
    int bookmark;
    synchronized(listaMensajes){
      bookmark = listaMensajes.getBookmark();
      for(listaMensajes.moveFirst();
          !listaMensajes.getEoF();
          listaMensajes.moveNext()){
        res+= ( (OACTSolicitudPrimitiva) listaMensajes.getObject()).getXMLElem(null)+"\n";
      }
      listaMensajes.moveTo(bookmark);
    }
    return res;
  }
  public final int getCountMensajes(){
    int res;
    synchronized(listaMensajes){
      res=listaMensajes.getCount();
    }
    return res;
  }
  /**
   * Saca el primer mensaje sin atender de la cola de mensajes. El mensaje
   * sacado inmediatamente obtiene el valor true de atendido.
   * @return El primer mensaje sin atender.
   */
  public final OACTSolicitudPrimitiva dequeueMensaje(){
    OACTSolicitudPrimitiva m=null;
    int bookMark,index=-1;
    synchronized(listaMensajes){
      bookMark=listaMensajes.getBookmark();
      listaMensajes.moveFirst();
      index=listaMensajes.findFirst("atendido°=°false°°");
      if(index>=0){
        if(listaMensajes.moveTo(index)){
          m=(OACTSolicitudPrimitiva)listaMensajes.getObject();
          m.setAtendido(true);
        }
      }
      listaMensajes.moveTo(bookMark);
    }
    return m;
  }
  public synchronized int delElemento(String id) {
    int i=0;
    System.out.println("borra elemento");
      double num=5.0;
      System.out.println("el número es " + num);
    return i;
  }
  public void clean(){
    synchronized(listaMensajes){
      listaMensajes.moveFirst();
      listaMensajes.clean();
//      listaTrabajos=null;
      listaMensajes=null;
    }
  }
  public String getStatusActivacion(){
    String res="<?xml version=\"1.0\"?>";
    res+="\n<estadoactivacion>";
    OACTSolicitudPrimitiva info;
    synchronized(listaMensajes){
      listaMensajes.moveFirst();
      while(!listaMensajes.getEoF()){
        info=(OACTSolicitudPrimitiva)listaMensajes.getObject();
        res+=info.getStatus();
        res+="\n";
        listaMensajes.moveNext();
      }
    }
    res+="</estadoactivacion>";
    return res;
  }
}