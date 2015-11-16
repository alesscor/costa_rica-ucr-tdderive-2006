/*
 * Created on 23/07/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package admin;

import tables.AbstractSet;

/**
 * <p>Title: <b>admin</b>:: admin</p>
 * <p>Description: DIREnlaces.java.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCR - ECCI</p>
 * <br>@author Alessandro</br>
 * <br>@version 1.0</br>
 */
/**
 * Clase con información de balance de varios enlaces.
 */
public class DIREnlaces {

  private AbstractSet vecinos;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public DIREnlaces() {
    vecinos=new AbstractSet(200,new DIREnlace(),false,true,new String[]{"tespera"});
  }
  /**
   * Agrega o actualiza un enlace al nodo con otro nodo.
   * @param enlace Enlace a agregar o a actualizar. Si es diferente a null
   * se crea una nueva instancia del enlace porque el enlace va a agregarse
   * a un nodo.
   * @param nodoid0 Nodo del enlace.
   * @throws DIRException Si hubo error al agregar al conjunto de
   * enlaces.
   */
  public void addNewUpdateVecino(String nodoid0,DIREnlace enlace) throws DIRException{
    DIREnlace res;
    DIREnlace nuevo;
    nuevo=enlace;
    if(nodoid0!=null){
      nuevo=enlace.clona();
      nuevo.setEsteNodo(nodoid0);
    }
    res=_operateById(nuevo.getEnlaceId(),nuevo,false);
    if(res==null){
      // no se pudo
      throw new DIRException("No se pudo ni actualizar ni agregar el enlace "+
                             enlace.getEnlaceId()+ ".");
    }
  }
  public void delete(String enlaceid) throws DIRException{
    if(vecinos.getCount()==0){
      throw new DIRException("Error al borrar el enlace "+enlaceid+".");
    }
    _operateById(enlaceid,null,true);
  }
  private DIREnlace _operateById(String enlaceid,DIREnlace enlace,boolean delete)throws DIRException{
    DIREnlace res=null;
    if((delete||(enlace==null)) && vecinos.getCount()==0){
      throw new DIRException("No hay enlaces.");
    }
    if(vecinos.getCount()>0){
      vecinos.moveFirst();
    }
    while(!vecinos.getEoF()){
      res=(DIREnlace)vecinos.getObject();
      if(res.getEnlaceId().compareTo(enlaceid)==0){
        break;
      }
      vecinos.moveNext();
    }
    if(!vecinos.getEoF()){
      if(delete){
        if(vecinos.delete()){
          if(vecinos.getCount() > 0) {
            vecinos.moveFirst();
          }
          return null;
        }else{
          throw new DIRException("No se pudo borrar el enlace " + enlaceid +
                                 ".");
        }
      }else{
        if (enlace != null) {
          vecinos.update(enlace);
          res = enlace;
        }
        vecinos.moveFirst();
        return res;
      }
    }else{
      if(enlace!=null){
        res=enlace;
        vecinos.addNew(enlace);
      }
      if(vecinos.getCount()>0){
        vecinos.moveFirst();
      }
      return res;
    }
  }
  public final void moveFirst() throws DIRException{
    if(!vecinos.moveFirst()){
      throw new DIRException("No hay enlaces.");
    }
  }
  public final DIREnlace getVecinoMoveNext() throws DIRException{
    DIREnlace res=null;
    if(!vecinos.getEoF()){
      res=(DIREnlace)vecinos.getObject();
      if(!vecinos.moveNext()){
        throw new DIRException("No hay enlaces.");
      }
    }
    return res;
  }
  public final int getCount(){
    return vecinos.getCount();
  }
  public final void clean(){
    vecinos.clean();
  }
  public final String getString(){
    DIREnlace enlaceI;
    String res="";
    res+="<enlaces>\n";
    if(vecinos.getCount()>0){
      vecinos.moveFirst();
      while(!vecinos.getEoF()){
        enlaceI=(DIREnlace)vecinos.getObject();
        res+=enlaceI.getXMLElem(null);
        vecinos.moveNext();
      }
      vecinos.moveFirst();
    }
    res+="\n</enlaces>";
    return res;
  }
  public final String getVecinosString(){
    DIREnlace enlaceI;
    String res="";
    res+="<vecinos>\n";
    if(vecinos.getCount()>0){
      vecinos.moveFirst();
      while(!vecinos.getEoF()){
        enlaceI=(DIREnlace)vecinos.getObject();
        if(enlaceI.getOtroNodo()!=""){
          res += enlaceI.getOtroNodo();
        }
        vecinos.moveNext();
        if(!vecinos.getEoF()){
          res += "\n";
        }
      }
      vecinos.moveFirst();
    }
    res+="\n</vecinos>";
    return res;
  }
  public final int getMaxTEspera(){
    int res=0;
    if(vecinos.getCount()>0){
      vecinos.initIteration("tespera", false);
      res=((DIREnlace)vecinos.nextIteration("tespera")).getTEspera();
    }
    return res;
  }
}
