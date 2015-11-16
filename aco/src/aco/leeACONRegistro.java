package aco;

import mens.*;
import org.w3c.dom.*;
import tables.AbstractSet;

/**
 * Objeto especializado en leer descriptores múltiples
 * en un almacenamiento.
 */

public final class leeACONRegistro extends MENSMensaje {
  //////////////////////////////////////////////////////////////////////
  AbstractSet descriptoresregistrados;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public leeACONRegistro() {
    descriptoresregistrados=null;
  }
  public void setlistagestores(AbstractSet listagestores){
    descriptoresregistrados=listagestores;
  }
  public AbstractSet getlistadescriptores(){
    return descriptoresregistrados;
  }
  public boolean isVacio() {
    return (descriptoresregistrados==null) ||(descriptoresregistrados.getCount()==0);
  }
  protected String getXMLContainedElements() {
    String xml="";
    leeACONDescriptor lector=new leeACONDescriptor();
    ACONDescriptorAdmin infogestor;
    if(descriptoresregistrados!=null){
      for(descriptoresregistrados.moveFirst();descriptoresregistrados.getEoF();
          descriptoresregistrados.moveNext()){
        infogestor=(ACONDescriptorAdmin)descriptoresregistrados.getObject();
        lector.setobj(infogestor);
        xml+=lector.getXMLRootElem(null);
      }
    }
    return xml;
  }
  protected void setContentFromDoc(Node nodo, int[] nerr, String[] merr) {
    leeACONDescriptor lector=null;
    if(this.descriptoresregistrados==null){
      this.descriptoresregistrados=new AbstractSet(new ACONDescriptorAdmin());
    }
    nodo=this.getDocumento();
    nodo=MENSMensaje.getNextElement(nodo,"servicio");
    while(nodo!=null){
      lector=new leeACONDescriptor();
      lector.setContentFromDoc(nodo, nerr, merr);
      descriptoresregistrados.addNew(lector.getobj());
      nodo=MENSMensaje.getNextSiblingElement(nodo,"servicio");
      lector=null;
    }
  }
  protected void toleraXML(int[] nerr, String[] merr) {
    if((descriptoresregistrados==null)||(descriptoresregistrados.getCount()==0)){
      nerr[0]=3;
      merr[0]="no hay info sobre gestores registrados";
    }
  }
  //////////////////////////////////////////////////////////////////////
}