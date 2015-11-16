package tables;

/**
 * Iterador de un contenedor <tt>AbstractSet</tt>.
 */
public class Iterator {
  private IElement ieMin=null;
  private IElement ieMax=null;
  private String Field="";
  private IElement mieBookmark=null;
  private int Length=0;
  private IElement Sorted[]=null;

  public Iterator(String cField) {
    Field=cField;
    mieBookmark=null;
  }
  public boolean MoveFirst(){
  boolean bOK=false;
    bOK=ieMin!=null;
    if(bOK){
      mieBookmark=ieMin;
    }
    return bOK;
  }
  public boolean MoveLast(){
  boolean bOK=false;
    bOK=ieMax!=null;
    if(bOK){
      mieBookmark=ieMax;
    }
    return bOK;
  }
  public boolean MovePrev(){
  boolean bOK=false;
    bOK=mieBookmark.Anterior!=null;
    if(bOK){
      mieBookmark=mieBookmark.Anterior;
    }
    return bOK;
  }
  public boolean MoveNext(){
  boolean bOK=false;
    bOK=mieBookmark!=null;
    if(bOK){
      mieBookmark=mieBookmark.Siguiente;
    }
    return bOK;
  }
  public Object GetBookmark(){
    return mieBookmark;
  }
  public boolean MoveTo(Object oo){
  boolean bOK=false;
    bOK=oo!=null;
    if(bOK){
      if(oo.getClass().getName().compareTo(mieBookmark.getClass().getName())==0){
        mieBookmark=(IElement)oo;
      }else{
        bOK=false;
      }
    }
    return bOK;
  }
  public boolean GetCurrent(double nValue[],int nIndex[],Object oPuntero[]){
  boolean bOK=false;
    bOK= mieBookmark!=null;
    if(bOK){
      nValue[0]=mieBookmark.Valor;
      nIndex[0]=mieBookmark.Index;
      oPuntero[0]=mieBookmark.puntero;
    }
    return bOK;
  }
  public boolean GetCurrent(int nIndex[]){
    return GetCurrent(new double[1],nIndex,new Object[1]);
  }
  public boolean GetCurrent(double nValue[]){
    return GetCurrent(nValue,new int[1],new Object[1]);
  }

  public boolean Clean(){
  boolean bOK=false;
  IElement ie0[]=new IElement[1];
  IElement ie1=null;
    ie0[0]=ieMin;
    ie1=ie0[0].Siguiente;
    while(ie0[0]!=null){
      ie0[0]=null;
      ie0[0]=ie1;
      if (ie1!=null){
        ie1=ie1.Siguiente;
      }
    }
    ieMax=null;
    ieMin=null;
    return bOK;
  }
  public void RefreshSorted(){
  IElement ie=null;
  int i=0;
    Sorted=null;
    Sorted=new IElement[this.Length];
    ie=ieMin;
    while(ie!=null){
      Sorted[i]=ie;
      ie=ie.Siguiente;
      i++;
    }
  }
  public boolean Update(double nNewVal,int nIndex){
  boolean bOK=false;
    return bOK;
  }

  public boolean AddNew(double nNewVal,int nIndex){
    return AddNew(nNewVal,nIndex,null);
  }
  public boolean AddNew(double nNewVal,int nIndex,Object oPuntero){
  boolean bOK=false;
  IElement ieComp[]=new IElement[1];
  IElement ieNuevo=new IElement(nNewVal,nIndex,oPuntero);
  boolean bFinish=false;
    ieComp[0]=ieMin;
    while(!bFinish){
      if((ieComp[0]==null) || (ieNuevo.Valor<ieComp[0].Valor)){
      // si es menor o igual
        if(ieMin==null){
          ieMin=ieNuevo; // el primer elemento
          ieMax=ieNuevo;
          ieComp[0]=ieNuevo;
          bFinish=true;
        }else{
          ieNuevo.Anterior=ieComp[0].Anterior;
          if(ieComp[0].Anterior!=null){
            ieComp[0].Anterior.Siguiente=ieNuevo;
          }
          ieComp[0].Anterior=ieNuevo;
          ieNuevo.Siguiente=ieComp[0];
          bFinish=true;
          if (ieComp[0]==ieMin){
            ieMin=ieNuevo;
          }
        }
        Length++;
        bOK=true;
      }else{
      // si es mayor, o es el máximo o camina
        if(ieNuevo.Valor>=ieMax.Valor){
          ieComp[0]=ieMax;
          ieNuevo.Anterior=ieComp[0];
          ieComp[0].Siguiente=ieNuevo;
          ieNuevo.Siguiente=null;
          bFinish=true;
          ieMax=ieNuevo;
          ieComp[0]=null;  //i.e. ieNuevo para salir
          Length++;
          bOK=true;
        }
        if(ieComp[0]!=null){
          ieComp[0]=ieComp[0].Siguiente;
        }
      }
      if(!bFinish){
        bFinish=ieComp[0]==null;
      }
    }
    return bOK;
  }
  private class IElement{
    double Valor=0;
    int Index=-1;
    Object puntero;
    IElement Anterior=null;
    IElement Siguiente=null;

    IElement(double nValor,int nIndex){
      Valor=nValor;
      Index=nIndex;
      puntero=null;
    }
    IElement(double nValor,int nIndex,Object oPuntero){
      Valor=nValor;
      Index=nIndex;
      puntero=oPuntero;
    }
  }
}

