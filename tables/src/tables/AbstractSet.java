package tables;
import tdutils.tdutils;
import java.io.*;
/**
 * Implemente un contenedor abstracto de objetos del mismo tipo, o
 * al menos con la misma superclase.
 */
public class AbstractSet {
  public final AbstractSet_Constants constantes=new AbstractSet_Constants();
  private int count=0;
  private boolean EOF=true;
  private String type=null;
  private int mBookmark=-1;
  private int mMaxLength=0;
  private double mTotals[]=null;
  private String mFieldsOfTotals[]=null;
  private String mFields[]=null;
  private Class mTypeOfClass=null;
  private boolean mTraceTotals=false;
  private boolean mTraceMinNMax=false;
  private Iterator mIterators[]=null;
  private String mFields2Iterate[]=null;
  private boolean bIs2Iterate=false;
  private IElement mFirst=null;
  private IElement mLast=null;
  private IElement mCurrent=null;
  private Class mClass=null;

  public boolean getEoF(){
    return this.EOF;
  }
  public String getType(){
    return this.type;
  }
  public int getCount(){
    return this.count;
  }

  public AbstractSet(int nMáximo,Object clsClase,boolean bTraceTotals,boolean bTraceMinNMax,String cCamposIterar[]){
    abstractSet0(nMáximo,clsClase,bTraceTotals,bTraceMinNMax,cCamposIterar);
  }
  public AbstractSet(int nMáximo,Object clsClase,boolean bTraceTotals,boolean bTraceMinNMax) {
    abstractSet0(nMáximo,clsClase,bTraceTotals,bTraceMinNMax,null);
  }
  private void abstractSet0(int nMáximo,Object clsClase,boolean bTraceTotals,boolean bTraceMinNMax,String cCamposIterar[]) {
  int i,nNumTotalizables=0;
  String Type="";
  String cCamposTot="";
    mMaxLength=nMáximo;
    mTypeOfClass=clsClase.getClass().getSuperclass();
    mClass=clsClase.getClass();    
    Type=mTypeOfClass.getName();
    mBookmark=-1;
    EOF=true;
    mTraceTotals=bTraceTotals;
    mTraceMinNMax=bTraceMinNMax;
    if(bTraceTotals || bTraceMinNMax){
      for(i=0;i<mClass.getFields().length;i++){
        Type=mClass.getFields()[i].getType().getName();
        Type=Type.substring(Type.lastIndexOf(".")+1);
        if(Type.compareToIgnoreCase("int")==0 || Type.compareToIgnoreCase("double")==0 ||
           Type.compareToIgnoreCase("long")==0 || Type.compareToIgnoreCase("double")==0){
          nNumTotalizables++;
          cCamposTot=cCamposTot+mClass.getFields()[i].getName()+";";
        }
        if(nNumTotalizables>0){
          mTotals=new double[nNumTotalizables];
          mFieldsOfTotals=new String[nNumTotalizables];
          tdutils.split(cCamposTot,mFieldsOfTotals,";");
        }
      }
    }
    if(cCamposIterar!=null){
      mFields2Iterate=cCamposIterar;
      mIterators=new Iterator[mFields2Iterate.length];
      for(i=0;i<mFields2Iterate.length;i++){
        mIterators[i]=new Iterator(mFields2Iterate[i]);
      }
      bIs2Iterate=true;
    }
  }
  public AbstractSet(Object clsClase) {
    abstractSet0(constantes.MAX_ROWS,clsClase,false,false,null);
  }
  public AbstractSet(int nMáximo,Object clsClase) {
    abstractSet0(nMáximo,clsClase,false,false,null);
  }
  public String containedTypeName(){
      return mTypeOfClass.getName();
/*
 B            byte
 C            char
 D            double
 F            double
 I            int
 J            long
 Lclassname;  class or interface
 S            short
 Z            boolean
 */
  }
  public boolean print(String cAlgo){
    return print(System.out,false,cAlgo);
  }
  public String getContenido(String cAlgo){
    return getContenido(System.out,false,cAlgo);
  }
  public boolean print(){
    return print(System.out,false,null);
  }
  public boolean print(boolean bHeader){
    return print(System.out,bHeader,null);
  }
  public boolean print(OutputStream Output,boolean bHeader,String cAlgo){
  boolean bOK=false;
  IElement ieCurrent=null;
  int i=0,j=0;
    if(!mClass.isArray()){
      try{
        if(cAlgo!=null){
          Output.write((cAlgo+"\n").getBytes());
        }else{
          Output.write("\n".getBytes());
        }
        if(bHeader){
          for(j=0;j<mClass.getFields().length;j++){
            Output.write(("<"+
                          mClass.getFields()[j].getName()+":"+
                          mClass.getFields()[j].getType().getName()+
                          ">").getBytes());
            if(j!=mClass.getFields().length-1){
              Output.write(":".getBytes());
            }else{
              Output.write("\n".getBytes());
            }
          }
        }
        ieCurrent=mFirst;
        if(mClass.isPrimitive()|| mClass=="unstring".getClass()){
          for(i=0;(i<count)&&(ieCurrent!=null);i++,ieCurrent=ieCurrent.next){
            Output.write(ieCurrent.value.toString().getBytes());
            Output.write("\n".getBytes());
          }
        }else{
          for(i=0;(i<count)&&(ieCurrent!=null);i++,ieCurrent=ieCurrent.next){
            for(j=0;j<mClass.getFields().length;j++){
              try{
                Output.write(mClass.getFields()[j].get(
                    ieCurrent.value).toString().getBytes());
              }
              catch(java.lang.IllegalAccessException except){
                Output.write("error".getBytes());
              }
              if(j!=mClass.getFields().length-1){
                Output.write(":".getBytes());
              }else{
                Output.write("\n".getBytes());
              }
            }
          }
        }
      }
      catch(IOException except){
        bOK=false;
      }
    }else{
    }
    return bOK;
  }
  public String getContenido(OutputStream Output,boolean bHeader,String cAlgo){
    boolean bOK=false;
    String resultado="";
    IElement ieCurrent=null;
    int i=0,j=0;
      if(!mClass.isArray()){
        try{
          if(cAlgo!=null){
            resultado+=(cAlgo+"\n");
          }else{
            resultado+="\n";
          }
          if(bHeader){
            for(j=0;j<mClass.getFields().length;j++){
              resultado+=(("<"+
                            mClass.getFields()[j].getName()+":"+
                            mClass.getFields()[j].getType().getName()+
                            ">"));
              if(j!=mClass.getFields().length-1){
                resultado+=(":");
              }else{
                resultado+=("\n");
              }
            }
          }
          ieCurrent=mFirst;
          if(mClass.isPrimitive()|| mClass=="unstring".getClass()){
            for(i=0;(i<count)&&(ieCurrent!=null);i++,ieCurrent=ieCurrent.next){
              resultado+=(ieCurrent.value.toString());
              resultado+=("\n");
            }
          }else{
            for(i=0;(i<count)&&(ieCurrent!=null);i++,ieCurrent=ieCurrent.next){
              for(j=0;j<mClass.getFields().length;j++){
                try{
                  resultado+=(mClass.getFields()[j].get(
                      ieCurrent.value).toString());
                }
                catch(java.lang.IllegalAccessException except){
                  resultado+=("error");
                }
                if(j!=mClass.getFields().length-1){
                  resultado+=(":");
                }else{
                  resultado+=("\n");
                }
              }
            }
          }
        }
        catch(Exception except){
          resultado+="(error desconocido: "+except.getMessage()+")";
        }
      }else{
      }
      return resultado;
    }  
  public boolean delete(){
    return delete("");
  }
  public boolean delete(String cCriterio){
  boolean bOK=false;
  IElement ieCurrent=null;
  int idx=-1;
    if(cCriterio==""){
      idx=mBookmark;
    }else{
      idx=findFirst(cCriterio);
      moveTo(idx);
    }
    bOK=idx>=0;
    if(bOK){
      ieCurrent=mCurrent;
      mCurrent=mCurrent.prev;
      if(mCurrent!=null){
        mCurrent.next=ieCurrent.next;
      }else{
        mFirst=ieCurrent.next;
      }
      if(ieCurrent.next==null){
        mLast=mCurrent;
      }
      ieCurrent=null;
      // ya se sabe que hubo borrado
      count--;
      if(idx==mBookmark){
        mBookmark=-1;
        EOF=true;
        // pues ya no está el elemento de interés
      }
      if(idx<mBookmark){
        mBookmark--;
        // pues fue borrado un elemento a la izquierda del bookmark
      }
    }
    return bOK;
  }
  public boolean addNew(Object oo){
  boolean bOK=false;
    String obj="";
    bOK=((mMaxLength>count) &&
         ((oo.getClass().getSuperclass()==mTypeOfClass) ||
          (oo.getClass().getSuperclass()==mClass)||
          (oo.getClass().getSuperclass().getSuperclass()==mClass) ||
          (mClass.isInstance(oo)) ||
          (
            mClass.getSuperclass().isInstance(oo) &&
           (mClass.getSuperclass()!=obj.getClass().getSuperclass())
           /* evita que sea el tipo básico Object */
          )
          ));
    if(bOK){
      mCurrent=new IElement(oo);
      mCurrent.prev=mLast;
      mCurrent.next=null;
      mCurrent.index=count;
      if(mLast!=null){
        mLast.next=mCurrent;
      }
      mLast=mCurrent;
      if(mFirst==null){
        mFirst=mCurrent;
        mCurrent.prev=null;
      }
      mBookmark=count;
      EOF=false;
      count++;
      if(mTraceTotals){
        this.updateTotalizables(oo);
      }
      if(bIs2Iterate){
        this.update2Iterator(oo);
      }
    }
    return bOK;
  }
  public void clean(){
  boolean bOK=false;
    int i;
    mBookmark=-1;
    mFirst=null;
    while(mLast!=null){
      mCurrent=mLast;
      mLast=mCurrent.prev;
      mCurrent=null;
    }
    count=0;
    EOF=true;
    if(mTraceTotals){
      for (i=0;i<mTotals.length;i++){
        mTotals[i]=0;
      }
    }
    if(bIs2Iterate){
      for (i=0;i<this.mIterators.length;i++){
        mIterators[i].Clean();
        mIterators[i]=null;
      }
    }
  }
  private boolean update2Iterator(Object oo){
  boolean bOK=false;
  double nValue=0;
  int nIndex=0;
  int i=0;
    for(i=0;i<mFields2Iterate.length;++i){
      try{
        nValue=Float.parseFloat(mClass.getField(mFields2Iterate[i]).get(oo).toString());
      }
      catch(Exception except){
        nValue=0;
      }
      nIndex=mBookmark;
      mIterators[i].AddNew(nValue,nIndex,oo);
    }
    return bOK;
  }
  public boolean update(Object oo,String cCriterio){
  boolean bOK=false;
  Object ooo=null;
  int idx=-1;
    if(cCriterio!=""){
      idx=findFirst(cCriterio);
      moveTo(idx);
    }else{
      idx=mBookmark;
    }
    bOK=idx>=0;
    if(bOK){
      if(mTraceTotals){
        ooo=mCurrent.value;
        this.updateTotalizables(oo,ooo,false);
      }else{
      }
      mCurrent.value=oo;
    }
    return bOK;
  }
  public boolean update(Object oo){
    return update(oo,"");
  }

  public Object getObject(){
  Object oo[]=new Object[1];
    if(getObject(oo)){
      return oo[0];
    }else{
      return null;
    }
  }
  public boolean getObject(Object oo[]){
    boolean bOK=false;
    bOK=!EOF;
    if(bOK){
      oo[0]=mCurrent.value;
      if ((mTypeOfClass!=oo[0].getClass().getSuperclass())&&(mClass!=oo[0].getClass().getSuperclass())&&(mClass!=oo[0].getClass().getSuperclass().getSuperclass())){
        oo[0]=null;
        bOK=false;
      }
    }
    return bOK;
  }
  public double getTotal(String cCampo){
  double nValor=0;
  int i=0;
    if(mFieldsOfTotals==null){
      return 0;
    }
    for(i=0;i<this.mFieldsOfTotals.length;){
      if(this.mFieldsOfTotals[i].compareToIgnoreCase(cCampo)==0){
        nValor=this.mTotals[i];
        break;
      }
    }
    return nValor;
  }
  public boolean moveFirst(){
    boolean bOK=false;
    bOK=count!=0;
    if(bOK){
      mBookmark=0;
      mCurrent=mFirst;
      EOF=false;
    }
    return bOK;
  }
  public boolean moveLast(){
    boolean bOK=false;
    bOK=count!=0;
    if(bOK){
      mBookmark=count-1;
      mCurrent=mLast;
      EOF=false;
    }
    return bOK;
  }
  public boolean moveNext(){
    boolean bOK=false;
    bOK=(count>0 && !EOF && mBookmark<count);
    if(bOK){
      mBookmark++;
      if(!(mBookmark<count)){
        // llegó al último
        EOF=true;
        mCurrent=null;
      }else{
        mCurrent=mCurrent.next;
      }
    }
    return bOK;
  }
  public int getBookmark(){
    return mBookmark;
  }
  public boolean moveTo(int nBookmark){
    boolean bOK=false;
    int i=0;
    bOK=(count>0 &&  nBookmark<count);
    if(bOK){
      mBookmark=nBookmark;
      if(mBookmark>=count){
        // pasó al último
        EOF=true;
        mCurrent=null;
      }else{
        mCurrent=mFirst;
        for(i=0;i<nBookmark;i++){
          mCurrent=mCurrent.next;
        }
      }
    }
    return bOK;
  }
  public int findFirst(String cFilter){
    if(mClass.isArray()){
      return -1;
    }else{
      return findFirst(cFilter,0,count);
    }

  }
  public int findFirst(String cFilter,int Bookmark,int nRightBoundary){
    int index=-1;
    int kBookmark=Bookmark;
    String[][] aCriterios=new String[constantes.MAX_CRITERIES][4];
    if(mClass.isArray()){
      return -1;
    }
    if((count>0)&&(kBookmark<count)&&(kBookmark<nRightBoundary)&&(nRightBoundary<=count)){
      if(cFilter!=""){
        // forma el arreglo de criterios
        if(makeCriteriaStruct(cFilter,aCriterios)){
          // evalúa el criterio desde kBookmark hasta el límite derecho
          index=findFirst0(aCriterios,Bookmark,nRightBoundary);
        }else{
          // el criterio estaba mal escrito
          index=-1;
        }
      }
    }
    return index;
  }
  private boolean makeCriteriaStruct(String cFilter,String[][] aCriterios){
  // forma el arreglo de criterios
  // 0: índice del atributo
  // 1: valor a comparar el atributo
  // 2: tipo de comparación =, !, <, >, etc.
  // 3: tipo de conjugación && ó ||
  // todos deben de estar separados por CRITERIA_SEPARATOR
  // va criterio por criterio
    int i=0,idx=0,jdx=0;
    boolean bOK=false;
    jdx=cFilter.indexOf(constantes.CRITERIA_SEPARATOR);
    if(cFilter!=""){
      try{
        do{
            aCriterios[i][0]= cFilter.substring(idx,jdx);
            idx=jdx+1;
            jdx=cFilter.indexOf(constantes.CRITERIA_SEPARATOR,idx);
            aCriterios[i][2]= cFilter.substring(idx,jdx);
            idx=jdx+1;
            jdx=cFilter.indexOf(constantes.CRITERIA_SEPARATOR,idx);
            aCriterios[i][1]= cFilter.substring(idx,jdx);
            idx=jdx+1;
            jdx=cFilter.indexOf(constantes.CRITERIA_SEPARATOR,idx);
            if(!(jdx<0)){
              aCriterios[i][3]= cFilter.substring(idx,jdx);
            }else{
              aCriterios[i][3]= "&&";
            }
            idx=jdx+1;
            jdx=cFilter.indexOf(constantes.CRITERIA_SEPARATOR,idx);
            i++;
        }while((idx > 0) && (i<mTypeOfClass.getFields().length) && (i<constantes.MAX_CRITERIES));
        bOK= checkCriteriaSpecification(aCriterios);
      }
      catch(Exception except){
         bOK=false;
      }
    }
    return bOK;
  }
  private boolean checkCriteriaSpecification(String[][] aCriterios){
    boolean bOK=false;
    int i=0,idx=0;
    for(i=0;(i<constantes.MAX_CRITERIES)&&(aCriterios[i][0]!=null);i++){
      try{
        idx=Integer.parseInt(aCriterios[i][0]);
      }
      catch(NumberFormatException except){
/*        try{
          idx=mTypeOfClass.getField(aCriterios[i][0]).h;
        }
        catch(java.lang.NoSuchFieldException exception2){
          idx=-1;
        }
*/
        idx=tdutils.getFieldIndex(aCriterios[i][0],mClass);
        if(idx<0){
          bOK=false;
          break;
        }
      }
      bOK=true;
      aCriterios[i][0]=Integer.toString(idx);
    }
    return bOK;
  }



  private int findFirst0(String[][] aCriterios,int Bookmark,int nRightBoundary){
  // Complemento de bajo nivel para findFirst(...).
  // - Asume que la estructura de criterios estA bien formada,
  //   es decir CheckCriteriaSpecification(aCriterios)==true.
  // - Igualmente asume que aCriterios ha sido formado con
  //   MakeCriteriaStruct().
    int index=-1,i=0,j=0,idx=0,jdx=0;
    int kBookmark=Bookmark;
    boolean bCriterio=false;
    String cComparing0="",cComparing1="";
    int nComparing0=0,nComparing1=0;
    java.lang.reflect.Field fldField[]=null;
    String Type;
    if((count>0)&&(kBookmark<count)&&(kBookmark<nRightBoundary)&&(nRightBoundary<=count)){
      // evalúa el criterio desde kBookmark hasta el límite derecho
      IElement ieCurrent=mFirst;
      // mueve el current hasta el bookmark
      for(i=0;i<kBookmark;i++){
        ieCurrent=ieCurrent.next;
      }
      for(kBookmark=Bookmark;(kBookmark<nRightBoundary)&&(!(bCriterio))&&(ieCurrent!=null);kBookmark++,ieCurrent=ieCurrent.next){
        // inicializa adecuadamente el bCriterio de acuerdo con la primera
        // conjugación lógica
        if(aCriterios[0][3].compareTo("||")==0){
          bCriterio=false;
        }else{
          bCriterio=true;
        }
        // evalúa criterio por criterio y luego revisa si
        // bCriterio es verdadero en cuyo caso debe terminar
        // el for, pues la tupla ha sido encontrada.
        for(i=0;i<10;i++){
          if((aCriterios[i][2]==null)||(aCriterios[i][2]=="")){
            // ya terminó de evaluar todos los criterios en esta tupla
            break;
          }
          idx=Integer.parseInt( aCriterios[i][0]);
          // delega los valores a comparar en cComparing0 y cComparing1
          fldField=ieCurrent.value.getClass().getFields();
          Type=fldField[idx].getType().getName();
          if(Type=="java.lang.String"){
            try{
              cComparing0=(String)fldField[idx].get(ieCurrent.value);
            }
            catch(java.lang.IllegalAccessException exception){
              cComparing0="";
            }
            // cComparing0=cRecords[kBookmark][idx];

            if(cComparing0==null){
              cComparing0="";
            }
            cComparing1=aCriterios[i][1];
            if(cComparing1==null){
              cComparing1="";
            }
            // realiza la comparación indicada por el campo 2
            switch(aCriterios[i][2].charAt(0)){
            case '=':
              if(aCriterios[i][3].compareTo("&&")==0){
                bCriterio=bCriterio && (cComparing0.compareToIgnoreCase(cComparing1)==0);
              }else{
                bCriterio=bCriterio || (cComparing0.compareToIgnoreCase(cComparing1)==0);
              }
              break;
            case '!':
              if(aCriterios[i][3].compareTo("&&")==0){
                bCriterio=bCriterio && (cComparing0.compareToIgnoreCase(cComparing1)!=0);
              }else{
                bCriterio=bCriterio || (cComparing0.compareToIgnoreCase(cComparing1)!=0);
              }
              break;
            case '<':
              if(aCriterios[i][3].compareTo("&&")==0){
                bCriterio=bCriterio && (cComparing0.compareToIgnoreCase(cComparing1)<0);
              }else{
                bCriterio=bCriterio || (cComparing0.compareToIgnoreCase(cComparing1)<0);
              }
              break;
            case '>':
              if(aCriterios[i][3].compareTo("&&")==0){
                bCriterio=bCriterio && (cComparing0.compareToIgnoreCase(cComparing1)>0);
              }else{
                bCriterio=bCriterio || (cComparing0.compareToIgnoreCase(cComparing1)>0);
              }
              break;
            default:
              // nada que comparar
            }
          }else{
          // tipo no es string
            if(Type=="java.lang.Boolean"){
            }else{
              nComparing0=(int)tdutils.getNumber(ieCurrent.value,idx,mTypeOfClass);
              // cComparing0=cRecords[kBookmark][idx];
              nComparing1=(int)Float.parseFloat(aCriterios[i][1]);
              // realiza la comparación indicada por el campo 2
              switch(aCriterios[i][2].charAt(0)){
              case '=':
                if(aCriterios[i][3].compareTo("&&")==0){
                  bCriterio=bCriterio && (nComparing0==nComparing1);
                }else{
                  bCriterio=bCriterio || (nComparing0==nComparing1);
                }
                break;
              case '!':
                if(aCriterios[i][3].compareTo("&&")==0){
                  bCriterio=bCriterio && (nComparing0!=nComparing1);
                }else{
                  bCriterio=bCriterio || (nComparing0!=nComparing1);
                }
                break;
              case '<':
                if(aCriterios[i][3].compareTo("&&")==0){
                  bCriterio=bCriterio && (nComparing0<nComparing1);
                }else{
                  bCriterio=bCriterio || (nComparing0<nComparing1);
                }
                break;
              case '>':
                if(aCriterios[i][3].compareTo("&&")==0){
                  bCriterio=bCriterio && (nComparing0>nComparing1);
                }else{
                  bCriterio=bCriterio || (nComparing0>nComparing1);
                }
                break;
              default:
                // nada que comparar
              }
            }
          }
        }// del for de los criterios (i)
        if (bCriterio){
          // ha sido encontrado la primera tupla que cumple con el criterio
          // finaliza el trabajo.
          break;
        }
      }// del for del kBookmark
      // forma el resultado
      if(bCriterio && (kBookmark<nRightBoundary)){
        index=kBookmark;
      }else{
        index=-1;
      }
    }
    return index;
  }
  private int updateTotalizables(Object oo){
  // llamado cuando es por primera vez
    return updateTotalizables(oo,null,true);
  }
  private int updateTotalizables(Object oo,Object ooo,boolean bFirstTime){
  // llamado para actualizar los totales de los valores de los objetos
  // ooo es null ó el contenido viejo de oo
  int idx=-1,i=0,j=0;
  String cNombre;
    if(bFirstTime){
      j=0;
      for(i=0; (i < oo.getClass().getFields().length) && (j < mTotals.length);i++){
        cNombre=oo.getClass().getFields()[i].getName();
        if(cNombre.compareToIgnoreCase(this.mFieldsOfTotals[j])==0){
          try{
            mTotals[j]=mTotals[j]+Double.parseDouble(oo.getClass().getFields()[i].get(oo).toString());
          }
          catch(java.lang.IllegalAccessException except){
            mTotals[j]=-1;
          }
          j++;
        }
      }
    }else{
    // no es la primera vez bFirstTime==false
      if(ooo==null){
        return -1;
      }
      for(i=0; (i < oo.getClass().getFields().length) && (j < mTotals.length);i++){
        cNombre=oo.getClass().getFields()[i].getName();
        if(cNombre.compareToIgnoreCase(this.mFieldsOfTotals[j])==0){
          try{
            mTotals[j]=mTotals[j]+Double.parseDouble(oo.getClass().getFields()[i].get(oo).toString())-Double.parseDouble(ooo.getClass().getFields()[i].get(ooo).toString());
          }
          catch(java.lang.IllegalAccessException except){
            mTotals[j]=-1;
          }
          /*try{
            mTotals[j]=mTotals[j]+oo.getClass().getFields()[i].getFloat(oo)-oo.getClass().getFields()[i].getFloat(ooo);
          }
          catch(Exception except0){
            try{
              mTotals[j]=mTotals[j]+oo.getClass().getFields()[i].getFloat(oo)-oo.getClass().getFields()[i].getFloat(ooo);
            }
            catch(Exception except1){
              try{
                mTotals[j]=mTotals[j]+(double)oo.getClass().getFields()[i].getDouble(oo)-(double)oo.getClass().getFields()[i].getDouble(ooo);
              }
              catch(Exception except2){
                try{
                  mTotals[j]=mTotals[j]+(double)oo.getClass().getFields()[i].getLong(oo)-(double)oo.getClass().getFields()[i].getDouble(ooo);
                }
                catch(Exception except3){
                  mTotals[j]=mTotals[j]+1-1;
                }
              }
            }
          }
          */
          j++;
        }
      }
    }
    return j;
  }
  /***************************
   * Funciones de iteradores *
   ***************************/
  public boolean initIteration(String cName){
    return initIteration(cName,true);
  }
  public boolean initIteration(String cName,boolean bIsFirst){
  boolean bOK=false;
  int i=0;
    if(bIs2Iterate){
      for(i=0;i<mFields2Iterate.length;i++){
        if(cName.compareToIgnoreCase(mFields2Iterate[i])==0){
          bOK=true;
          break;
        }
      }
    }
    if(bOK){
      if(bIsFirst){
        bOK=mIterators[i].MoveFirst();
      }else{
        bOK=mIterators[i].MoveLast();
      }
    }
    return bOK;
  }
  public boolean lastOfIteration(String cName){
  boolean bOK=false;
  int i=0;
    if(bIs2Iterate){
      for(i=0;i<mFields2Iterate.length;i++){
        if(cName.compareToIgnoreCase(mFields2Iterate[i])==0){
          bOK=true;
          break;
        }
      }
    }
    if(bOK){
      bOK=mIterators[i].MoveLast();
    }
    return bOK;
  }
  public boolean firstOfIteration(String cName){
  boolean bOK=false;
  int i=0;
    if(bIs2Iterate){
      for(i=0;i<mFields2Iterate.length;i++){
        if(cName.compareToIgnoreCase(mFields2Iterate[i])==0){
          bOK=true;
          break;
        }
      }
    }
    if(bOK){
      bOK=mIterators[i].MoveFirst();
    }
    return bOK;
  }
  public Object nextIteration(String cName){
    return nextIteration(cName,null);
  }
  public Object nextIteration(String cName,Object oBookmark){
  boolean bOK=false;
  Object oo=null;
  int nIndex[]=new int[1];
  Object oPuntero[]=new Object[1];
  int i=0;
    if(bIs2Iterate){
      for(i=0;i<mFields2Iterate.length;i++){
        if(cName.compareToIgnoreCase(mFields2Iterate[i])==0){
          bOK=true;
          break;
        }
      }
    }
    if(bOK){
      bOK=false;
      if(mIterators[i].GetCurrent(new double[1],nIndex,oPuntero)){
        oBookmark=mIterators[i].GetBookmark();
        mIterators[i].MoveNext();
        bOK=true;
        if(oPuntero[0]==null){
          oPuntero[0]=mCurrent.value;
        }
        oo=oPuntero[0];
      }
    }
    if(bOK){
      return oo;
    }else{
      return null;
    }
  }
  public Object prevIteration(String cName){
    return prevIteration(cName,null);
  }
  public Object prevIteration(String cName,Object oBookmark){
  boolean bOK=false;
  Object oo=null;
  int nIndex[]=new int[1];
  Object oPuntero[]=new Object[1];
  int i=0;
    if(bIs2Iterate){
      for(i=0;i<mFields2Iterate.length;i++){
        if(cName.compareToIgnoreCase(mFields2Iterate[i])==0){
          bOK=true;
          break;
        }
      }
    }
    if(bOK){
      bOK=false;
      if(mIterators[i].GetCurrent(new double[1],nIndex,oPuntero)){
        oBookmark=mIterators[i].GetBookmark();
        mIterators[i].MovePrev();
        bOK=true;
        if(oPuntero[0]==null){
          oPuntero[0]=mCurrent.value;
        }
        oo=oPuntero[0];
      }
    }
    if(bOK){
      return oo;
    }else{
      return null;
    }
  }
  public boolean setIteratorBookmark(String cName,Object oo){
  boolean bOK=false;
  int nIndex[]=new int[1];
  int i=0;
    if(bIs2Iterate){
      for(i=0;i<mFields2Iterate.length;i++){
        if(cName.compareToIgnoreCase(mFields2Iterate[i])==0){
          bOK=true;
          break;
        }
      }
    }
    if(bOK){
      bOK=mIterators[i].MoveTo(oo);
    }
    return bOK;
  }
  public Object getIteratorBookmark(String cName){
  boolean bOK=false;
  Object oo=null;
  int nIndex[]=new int[1];
  int i=0;
    if(bIs2Iterate){
      for(i=0;i<mFields2Iterate.length;i++){
        if(cName.compareToIgnoreCase(mFields2Iterate[i])==0){
          bOK=true;
          break;
        }
      }
    }
    if(bOK){
      oo=mIterators[i].GetBookmark();
    }
    if(bOK){
      return oo;
    }else{
      return null;
    }
  }
  private class IElement{
    public Object value=null;
    public int index=-1;
    public IElement prev=null;
    public IElement next=null;
    IElement(Object oo){
      value=oo;
    }
  }
  final class AbstractSet_Constants{
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