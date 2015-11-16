package tables;
import java.io.*;
/**
 * Contenedor con métodos de acceso sincronizados.
 */
public class SynAbstractSet {
  private AbstractSet mAbstractSet=null;
  private boolean synFetchAllowed=true;
  public final SynAbstractSet_Constants constantes=new SynAbstractSet_Constants();

  public SynAbstractSet(Object clsClase){
    _synAbstractSet0(0,clsClase);
  }
  public SynAbstractSet(int nMáximo,Object clsClase){
    _synAbstractSet0(nMáximo,clsClase);
  }
  private void _synAbstractSet0(int nMáximo,Object clsClase) {
    mAbstractSet=new AbstractSet(nMáximo,clsClase,false,false,null);
  }
  public synchronized boolean addNew(Object oo){
  boolean bOK=false;
  int nOldPos=-1;
    SYNControl0();
    nOldPos=mAbstractSet.getBookmark();
    bOK=mAbstractSet.addNew(oo);
    if(nOldPos>=0){
      mAbstractSet.moveTo(nOldPos);
    }
    SYNControl1();
    return bOK;
  }
  public void clean(){
    SYNControl0();
    mAbstractSet.clean();
    SYNControl1();
  }
  public String containedTypeName(){
  String cTypeName="";
    SYNControl0();
    cTypeName= mAbstractSet.containedTypeName();
    SYNControl1();
    return cTypeName;
  }
  public boolean delete(){
    return delete("");
  }
  public synchronized boolean delete(int nIndex){
  int nOldPos=-1;
  boolean bOK=false;
    SYNControl0();
    nOldPos=mAbstractSet.getBookmark();
    if(mAbstractSet.moveTo(nIndex)){
      bOK=mAbstractSet.delete();
    }
    if(bOK){
      if(nIndex<nOldPos){
        nOldPos--;
        // porque se eliminó un elemento a la izquierda de la posición.
      }
      if(nOldPos==nIndex){
        // tiene que quedar así como lo dejó el
        // AbstractSet.delete, porque el elemento ha sido borrado.
      }else{
        if(nOldPos >=mAbstractSet.getCount()){
          nOldPos--;
        }
        mAbstractSet.moveTo(nOldPos);
      }
    }else{
      // no ha sido borrado ningún elemento
      mAbstractSet.moveTo(nOldPos);
    }
    SYNControl1();
    return bOK;
  }
  public synchronized boolean delete(String cCriterio){
  boolean bOK=false;
    SYNControl0();
    bOK=mAbstractSet.delete(cCriterio);
    SYNControl1();
    return bOK;
  }
  public synchronized int findFirst(String cFilter,int Bookmark,int nRightBoundary){
    int index=-1;
    SYNControl0();
    index=mAbstractSet.findFirst(cFilter,Bookmark,nRightBoundary);
    SYNControl1();
    return index;
  }
  public synchronized int findFirst(String cFilter){
    int index=-1;
    SYNControl0();
    index=mAbstractSet.findFirst(cFilter);
    SYNControl1();
    return index;
  }
  public synchronized Object getObject(int nIndex){
  int nOldPos=-1;
  Object oo[]=new Object[1];
    SYNControl0();
    nOldPos=mAbstractSet.getBookmark();
    if(mAbstractSet.moveTo(nIndex)){
      oo[0]=mAbstractSet.getObject();
    }else{
      oo[0]=null;
    }
    mAbstractSet.moveTo(nOldPos);
    SYNControl1();
    return oo[0];
  }
  public int getBookmark(){
  int nBookmark=-1;
    SYNControl0();
    nBookmark=mAbstractSet.getBookmark();
    SYNControl1();
    return nBookmark;
  }
  public boolean moveFirst(){
    boolean bOK=false;
    SYNControl0();
    bOK=mAbstractSet.moveFirst();
    SYNControl1();
    return bOK;
  }
  public boolean moveLast(){
    boolean bOK=false;
    SYNControl0();
    bOK=mAbstractSet.moveLast();
    SYNControl1();
    return bOK;
  }
  public boolean moveNext(){
    boolean bOK=false;
    SYNControl0();
    bOK=mAbstractSet.moveNext();
    SYNControl1();
    return bOK;

  }
  public boolean moveTo(int nBookmark){
    boolean bOK=false;
    SYNControl0();
    bOK=mAbstractSet.moveTo(nBookmark);
    SYNControl1();
    return bOK;
  }
  public int getCount(){
  int nCount=0;
    SYNControl0();
    nCount=mAbstractSet.getCount();
    SYNControl1();
    return nCount;
  }
  public String getType(){
  String cType="";
    SYNControl0();
    cType=mAbstractSet.getType();
    SYNControl1();
    return cType;
  }
  public boolean getEoF(){
  boolean bEoF=true;
    SYNControl0();
    bEoF=mAbstractSet.getEoF();
    SYNControl1();
    return bEoF;
  }
  public boolean print(){
  boolean bOK=false;
    SYNControl0();
    bOK=mAbstractSet.print(System.out,false,null);
    SYNControl1();
    return bOK;
  }
  public boolean print(boolean bHeader){
  boolean bOK=false;
    SYNControl0();
    bOK=mAbstractSet.print(System.out,bHeader,null);
    SYNControl1();
    return bOK;
  }
  public boolean print(OutputStream Output,boolean bHeader,String cAlgo){
  boolean bOK=false;
    SYNControl0();
    bOK=mAbstractSet.print(Output,bHeader,cAlgo);
    SYNControl1();
    return bOK;
  }
  public boolean update(Object oo,String cCriterio){
  boolean bOK=false;
    SYNControl0();
    bOK=mAbstractSet.update(oo,cCriterio);
    SYNControl1();
    return bOK;
  }
  public boolean update(Object oo,int nIndex){
  int nOldPos=-1;
  boolean bOK=false;
    SYNControl0();
    nOldPos=mAbstractSet.getBookmark();
    if(mAbstractSet.moveTo(nIndex)){
      bOK=mAbstractSet.update(oo);
      mAbstractSet.moveTo(nOldPos);
    }
    SYNControl1();
    return bOK;
  }
  private void SYNControl0(){
    // control de sincronizaciOn
    // en los cambios de los valores
    while(!synFetchAllowed){
      try{
         wait();
      }
      catch(InterruptedException except){
      // nada, se ha percibido
      // una señal solamente,
      // debe evaluarse si es
      // cambio en el valor de ctrl
      }
    }
    synFetchAllowed=false;
    // forza la detecciOn del cambio
    // en el valor del control de sincronizaciOn
    notify();
  }
  private void SYNControl1(){
    synFetchAllowed=true;
    // forza la detecciOn del cambio
    // en el valor del control de sincronizaciOn
    notify();
  }
  final class SynAbstractSet_Constants{
    final int MAX_FIELDS=10;
    final int MAX_ROWS=200;
    final int MAX_CRITERIES=10;
    final String ERR_VALUE="nofield";
    final String DEF_SEPARATOR="\t";
    final String CRITERIA_SEPARATOR="°";
    final String MAX_STRING="zzzzzzzzzzzzzzzzz";
    final int MAX_INT=100000000;
    final String LIST_SEPARATOR=",";
  }
}