package oact;
import tables.AbstractSet;

/**
 * Conjunto de objetos activos que sirven de fachada formando un objeto
 * activo completo.
 */
public class OACTInventarioSirvientes {
  /**
   * Conjunto de objetos activos que sirven de fachada formando un objeto
   * activo completo.
   */
  private AbstractSet conjunto;
  private OACTDistribuidor servidor;
  public OACTInventarioSirvientes(OACTDistribuidor servidor0) {
    conjunto=new AbstractSet((OACTSirvienteAbs)(new OACTSirvienteAbs()));
    servidor=servidor0;
  }
/**
 * Agrega un nuevo objeto activo (sirviente) al servidor distribuidor de
 * objetos activos.
 * @param oa Objeto activo a agregar.
 * @throws OACTExcepcion En caso de error en el registro de sirvientes.
 */
  public void addNew(OACTSirvienteAbs oa) throws OACTExcepcion{
    boolean res=false;
    synchronized(conjunto){
      System.err.println("sincro addnew");
      res=conjunto.addNew((OACTSirvienteAbs)oa);
      oa.setDistribuidor(servidor);
      log_objetoactivo(oa,"<estado>Enlistado</estado>");
    }
    if (!res){
      log_objetoactivo(oa,"<estado>Error, no enlistado</estado>");
      throw new OACTExcepcion("problema al agregar un nuevo objeto activo");
    }
  }
  public OACTSirvienteAbs getById(String id0)throws OACTExcepcion{
    OACTSirvienteAbs oa=null;
    int bookmark;
    boolean encontrado=false;
    synchronized(conjunto){
      System.err.println("sincro getbyid");
      bookmark=conjunto.getBookmark();
      for(conjunto.moveFirst();(!conjunto.getEoF()) && (!encontrado);
          conjunto.moveNext()){
        oa=(OACTSirvienteAbs)conjunto.getObject();
        if(oa.getId().compareTo(id0)==0){
          encontrado=true;
        }else{
          oa=null;
        }
      }
      conjunto.moveTo(bookmark);
    }
    if(oa==null){
      throw new OACTExcepcion("objeto activo no encontrado");
    }
    return oa;
  }
  public OACTSirvienteAbs moveNext(){
    OACTSirvienteAbs res =null;
    synchronized(conjunto){
      System.err.println("sincro movenext");
      res = (OACTSirvienteAbs) conjunto.getObject();
      conjunto.moveNext();
    }
    return res;
  }
  public void moveFirst(){
    synchronized(conjunto){
      System.err.println("sincro movefirst");
      conjunto.moveFirst();
    }
  }
  public void clean(){
    synchronized(conjunto){
      System.err.println("sincro clean");
      conjunto.moveFirst();
      conjunto.clean();
      conjunto=null;
      this.log_objetoactivo(null,"<estado>Inventario cerrado</estado>");
    }
  }
  private void log_objetoactivo(OACTSirvienteAbs obj, String mensaje){
    if(OACTBitacora.getConBitacora()){
      if(obj!=null){
        mensaje = OACTBitacora.getLogTime() + obj.getStatus() + mensaje;
      }else{
        mensaje = OACTBitacora.getLogTime() + mensaje;
      }
      mensaje = "<oactivo>" + mensaje + "</oactivo>";
      OACTBitacora.addLog(mensaje);
    }
  }
  public String getStatusSirvientes(){
    String res="<?xml version=\"1.0\"?>";
    res+="\n<estadosirvientes>";
    System.err.println("entra en getstatus");
    OACTSirvienteAbs info;
    synchronized(conjunto){
      System.err.println("sincro getstatus");
      conjunto.moveFirst();
      while(!conjunto.getEoF()){
        info=(OACTSirvienteAbs)conjunto.getObject();
        res+=info.getStatus();
        res+="\n";
        System.err.println("ciclo");
        conjunto.moveNext();
      }
    }
    res+="</estadosirvientes>";
    return res;
  }
}