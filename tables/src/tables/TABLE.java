package tables;
import java.io.*;
import tdutils.tdutils;
/**
 * Contenedor simulador de tablas.
 */

public class TABLE {
  public final TABLE_Constantes constantes=new TABLE_Constantes();
  private String[][] cRecords;
  private String[] cFields;
  private int nRows=0;
  private int nnFields=0;
  private int nFields=0;
  private int nBookmark=0;
  private String cSeparator="";
  private int nChanges=0;
  private String cFileName="";
// control sobre sincronizaciOn
  private boolean synFetchAllowed=true;
  private boolean synWriteAllowed=true;
// los procedimientos con prefijo "_" no ejercen
// bloqueo sobre los controles de sincronización
  public TABLE(){
    cRecords=null;
    cFields=null;
    nRows=0;
    nnFields=0;
    nFields=0;
    nBookmark=0;
    cSeparator="";
    nChanges=0;
    cFileName="";
  }
  public void open(String cName){
    open(cName,constantes.DEF_SEPARATOR);
  }
  public synchronized void open(String cName,String cSeparator0){
  // [todo] dar error cuando se trabaja y no se ha abierto el objeto
  // Abre un archivo y lo carga en memoria como si éste fuera una
  // tabla.
  // - \n delimita las tuplas
  // - <cSeparator0> delimita los atributos
  // - carga un arreglo de campos en <cFields> a partir de una primera
  //   línea de archivo que tenga '#' como primer caracter.
    String cRow;
    int value=0;
    int i=0,j=0,idx=0,jdx=0;
    SYNControl0();
    cSeparator=cSeparator0;
    cFileName=cName;
    try{
      // abre el archivo
      BufferedReader brFILE=new BufferedReader(new FileReader(cName));
      i=0;
      idx=0;
      cRow="";
      cRecords=new String[constantes.MAX_ROWS][constantes.MAX_FIELDS];
      cFields=new String[constantes.MAX_FIELDS];
      // carga el archivo en el cRecords
      cRow=brFILE.readLine();
      while((cRow!=null) && (i<constantes.MAX_ROWS)){
        j=0;
        idx=cRow.indexOf("#");
        if(idx>=0){
        // hay un comentario dentro del archivo que hay que quitar
          if(idx==0){
            if((i==0)&&(cRow!="")){
              // primera línea puede indicar cuáles son los campos
              // de la tabla
              cRow=cRow.substring(1);
              nFields=tdutils.split(cRow,cFields,cSeparator0,constantes.MAX_FIELDS);
              nnFields=nFields;
            }
            cRow="";
          }else{
            cRow=cRow.substring(0, idx);
          }
        }
        // la hilera está lista para ser parseada
        if(cRow!=""){
          // parsea la tupla para obtener sus atributos
          // porque split viene a partir de JDK 1.4
          j=tdutils.split(cRow,cRecords[i],cSeparator0,constantes.MAX_FIELDS);
          if(nnFields<j){
            nnFields=j;
          }
          i++;
        }
        cRow=brFILE.readLine();
      }
      nRows=i;
      brFILE.close();
    // si i==0 el archivo estaba vacío
    }
    catch(IOException except){
      System.err.println("no se pudo abrir el archivo '"+cName+"'\n" +
        except.toString());
      // no se sale del sistema
    }
    SYNControl1();
  }

  private synchronized void openOBS(String cName,String cFilter,String cSeparator0){
  // Abre un archivo y lo carga en memoria como si éste fuera una
  // tabla.
  // - \n delimita las tuplas
  // - <cSeparator0> delimita los atributos
  // - Obsoleto
    SYNControl0();
    String cRow;
    int value=0;
    int i=0,j=0,idx=0,jdx=0;
    cSeparator=cSeparator0;
    try{
      // abre el archivo
      BufferedReader brFILE=new BufferedReader(new FileReader(cName));
      i=0;
      idx=0;
      cRow="";
      cRecords=new String[constantes.MAX_ROWS][constantes.MAX_FIELDS];
      // carga el archivo en el cRecords
      cRow=brFILE.readLine();
      while((cRow!=null) && (i<constantes.MAX_ROWS)){
        j=0;
        idx=cRow.indexOf("#");
        if(idx>=0){
        // hay un comentario dentro del archivo que hay que quitar
          if(idx==0){
            cRow="";
          }else{
            cRow=cRow.substring(0, idx);
          }
        }
        // la hilera está lista para ser parseada
        if(cRow!=""){
          // parsea la tupla para obtener sus atributos
          // porque split viene a partir de JDK 1.4
          idx=0;
          jdx=cRow.indexOf(cSeparator0);
          do{
            if (jdx<0){
              cRecords[i][j]= cRow.substring(idx);
            } else{
              cRecords[i][j]= cRow.substring(idx,jdx);
            }
            idx=jdx+1;
            jdx=cRow.indexOf(cSeparator0,idx);
            j++;
          }while((idx > 0) && (j<constantes.MAX_FIELDS));
          if(nFields<j){
            nFields=j;
          }
          i++;
        }
        cRow=brFILE.readLine();
      }
      nRows=i;
      brFILE.close();
    // si i==0 el archivo estaba vacío
    }
    catch(IOException except){
      System.err.println("no se pudo abrir el archivo '"+cName+"'\n" +
        except.toString());
      SYNControl1();
      System.exit(1);
    }
    //System.exit(0);
    SYNControl1();
  }
  public synchronized boolean clean(){
  boolean bOK=false;
  int i=0,j=0;
    SYNControl0();
    for(i=0;i<nRows;i++){
      for(j=0;j<nFields;j++){
        cRecords[i][j]="";
      }
    }
    bOK=true;
    nRows=0;
    nBookmark=0;
    nChanges++;
    SYNControl1();
    return bOK;
  }
  public synchronized boolean close(){
    SYNControl0();
    boolean bOK=true;
    cRecords=null;
    cFields=null;
    nRows=0;
    nnFields=0;
    nFields=0;
    nBookmark=0;
    cSeparator="";
    nChanges=0;
    cFileName="";
    SYNControl1();
    return bOK;
  }
/*  public synchronized String z_GetFieldValue(int nIndex){
    SYNControl0();
    String value="";
    if((nIndex<nFields) && (nBookmark<nRows)){
      value=cRecords[nBookmark][nIndex].trim();
    }
    SYNControl1();
    return value;
  }*/
  public synchronized boolean isLast(){
    SYNControl0();
    boolean bIsLast=false;
    if(nBookmark<nRows){
      if(nBookmark==(nRows-1)){
        bIsLast=true;
      }
    }
    SYNControl1();
    return bIsLast;
  }
  public synchronized boolean isEmpty(){
    SYNControl0();
    boolean bIsEmpty=true;
    if((nBookmark<nRows)&&(nRows>0)){
      bIsEmpty=false;
    }
    SYNControl1();
    return bIsEmpty;
  }
  public synchronized boolean moveToFirst(){
    SYNControl0();
    boolean bOK=false;
    if((nBookmark<nRows)&&(nRows>0)){
      nBookmark=0;
      bOK=true;
    }
    SYNControl1();
    return bOK;
  }
  public synchronized boolean moveToLast(){
    SYNControl0();
    boolean bOK=false;
    if((nBookmark<nRows)&&(nRows>0)){
      nBookmark=nRows-1;
      bOK=true;
    }
    SYNControl1();
    return bOK;
  }
  public synchronized boolean moveToNext(){
    SYNControl0();
    boolean bIsThereAnother=false;
    if (nBookmark<nRows){
      nBookmark++;
      bIsThereAnother=true;
    }else{
      // no quedan más tuplas y se ha llegado al final
      // del archivo
    }
    SYNControl1();
    return bIsThereAnother;
  }
  public synchronized boolean moveTo(int n){
    SYNControl0();
    boolean bOK=false;
    if(((nBookmark+n)<nRows)&&(nRows>0)){
      nBookmark+=n;
      bOK=true;
    }
    SYNControl1();
    return bOK;
  }
  public int findFirst(String cFilter){
    return findFirst(cFilter,nBookmark,nRows);
  }
  public int findFirst(String cFilter,int Bookmark){
    return findFirst(cFilter,Bookmark,nRows);
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
        }while((idx > 0) && (i<nFields) && (i<constantes.MAX_CRITERIES));
        bOK= checkCriteriaSpecification(aCriterios);
      }
      catch(Exception except){
         bOK=false;
      }
    }
    return bOK;
  }
  private int findFirst0(String[][] aCriterios,int Bookmark,int nRightBoundary){
  // Complemento de bajo nivel para FindFirst(...).
  // - Asume que la estructura de criterios estA bien formada,
  //   es decir CheckCriteriaSpecification(aCriterios)==true.
  // - Igualmente asume que aCriterios ha sido formado con
  //   MakeCriteriaStruct().
    int index=-1,i=0,j=0,idx=0,jdx=0;
    int kBookmark=Bookmark;
    boolean bCriterio=false;
    String cComparing0="",cComparing1="";
    if((nRows>0)&&(kBookmark<nRows)&&(kBookmark<nRightBoundary)&&(nRightBoundary<=nRows)){
      // evalúa el criterio desde kBookmark hasta el límite derecho
      for(kBookmark=Bookmark;(kBookmark<nRightBoundary)&&(!(bCriterio));kBookmark++){
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
            cComparing0=cRecords[kBookmark][idx];
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
              bCriterio=bCriterio && (cComparing0.compareTo(cComparing1)==0);
            }else{
              bCriterio=bCriterio || (cComparing0.compareTo(cComparing1)==0);
            }
            break;
          case '!':
            if(aCriterios[i][3].compareTo("&&")==0){
              bCriterio=bCriterio && (cComparing0.compareTo(cComparing1)!=0);
            }else{
              bCriterio=bCriterio || (cComparing0.compareTo(cComparing1)!=0);
            }
            break;
          case '<':
            if(aCriterios[i][3].compareTo("&&")==0){
              bCriterio=bCriterio && (cComparing0.compareTo(cComparing1)<0);
            }else{
              bCriterio=bCriterio || (cComparing0.compareTo(cComparing1)<0);
            }
            break;
          case '>':
            if(aCriterios[i][3].compareTo("&&")==0){
              bCriterio=bCriterio && (cComparing0.compareTo(cComparing1)>0);
            }else{
              bCriterio=bCriterio || (cComparing0.compareTo(cComparing1)>0);
            }
            break;
          default:
            // nada que comparar
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
  private int _addNew(String cFilter){
  // Agrega una nueva tupla a la tabla.
  // - mueve el <nBookmark> a la nueva posición
  //   si hay éxito
    int index=-1;
    int i;
    if(cRecords!=null){
      if(nRows+1<constantes.MAX_ROWS){
        index=nRows;
        nBookmark=index;
        nRows++;
      }else{
        index=-1;
        if(cFilter!=""){
          index=_findFirst(cFilter,0,nRows);
          if(index>=0){
            // Hay uno libre y se borra
            nBookmark=index;
            // nRows++; no debe aumentar
            for(i=0;i<nFields;i++){
              cRecords[index][i]="";
            }
          }
        }
      }
    }
    // acumula búsquedas en el inicio de la lista
    // si es muy eficiente la actualización de ésta.
    // por ejemplo, si se usa en una cola de mensajes
    // y la cola de mensajes siempre está actualizada
    // donde apenas llega un mensaje éste es leído,
    // los nuevos elementos se agregarán siempre al
    // inicio de la cola.
    /*
    ejemplo:
    #leído:tipo:de:para:capacidad:carga:adar:altura
    true:B:n[1]:n[2]:2.0:5.866666603088379:0.0:0.0
    true:B:n[2]:n[1]:1.0:2.933333396911621:0.0:0.0
    true:G:n[2]:n[1]::::
    true::n[1]:n[2]::::
    true::n[2]:n[1]::::
    true:R:n[1]:n[2]::::
    true::n[1]:n[2]::::
    true::n[1]:n[2]::::
    true::n[2]:n[1]::::
    true::n[2]:n[1]::::
    true::n[1]:n[2]::::
    true::n[1]:n[2]::::
    true:G:n[2]:n[1]::::
    true:R:n[1]:n[2]::::
    true::n[1]:n[2]::::
    true::n[2]:n[1]::::
    true::n[1]:n[2]::::
    true::n[2]:n[1]::::
    true:B:n[2]:n[1]:1.0:4.5:0.0:0.0
    Cola de mensajes de tamaño 19.
    Este estado fue construido cuando habían 19 mensajes en la
    cola. Quedó así luego de más de 7 iteraciones.
    */
    return index;
  }
  private int _addNew(){
    return _addNew("");
  }
  private int _findFirst(String cFilter,int Bookmark,int nRightBoundary){
    int index=-1;
    int kBookmark=Bookmark;
    String[][] aCriterios=new String[constantes.MAX_CRITERIES][4];
    if((nRows>0)&&(kBookmark<nRows)&&(kBookmark<nRightBoundary)&&(nRightBoundary<=nRows)){
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
  public synchronized int findFirst(String cFilter,int Bookmark,int nRightBoundary){
  // Busca el índice de la primera tupla que cumpla con el
  // filtro dado, buscando a partir del lugar indicado.
  // - nRightBoundary: cota derecha para nRows
  // - solamente hace comparaciones entre textos.
  // - cFilter: filtro de la búsqueda
  //   formato: field_no="value" [{'&&','||'} field_no="value"]
  //            solo permite MAX_CRITERIES comparaciones, evaluando de
  //            izquierda a derecha.
  // - Bookmark: inicio de la búsqueda
    SYNControl0();
    int index=_findFirst(cFilter,Bookmark,nRightBoundary);
    SYNControl1();
    return index;
  }
  public int getMaxValue(String cField,String[] cValue,boolean bIsNumerical){
     return getMaxValue("",cField,cValue,bIsNumerical);
  }
  public int getMaxValue(String cCriteria,String cField,String[] cValue,boolean bIsNumerical){
  // Obtiene el mAximo valor de un campo que pertenezca al conjunto
  // de datos delimitado por cCriteria.
    return getExtremeValue(cCriteria,cField,cValue,bIsNumerical,true);
  }
  public int getMinValue(String cField,String[] cValue,boolean bIsNumerical){
     return getMinValue("",cField,cValue,bIsNumerical);
  }
  public int getMinValue(String cCriteria,String cField,String[] cValue,boolean bIsNumerical){
  // Obtiene el mInimo valor de un campo que pertenezca al conjunto
  // de datos delimitado por cCriteria.
    return getExtremeValue(cCriteria,cField,cValue,bIsNumerical,false);
  }
  private synchronized int getExtremeValue(String cCriteria,String cField,String[] cValue,boolean bIsNumerical,boolean bIsMaximun){
  // Obtiene el valor mAximo de un campo dado, en el
  // conjunto de registros que cumplan con el criterio
  // dado.
  // - Devuelve el Indice del elemento y el valor mAximo
  //   del campo en cValue.
    SYNControl0();
    //cValue=new String();
    cValue[0]="";
    int i=-1;
    int index=-1;
    boolean bOK=false;
    String[][] aCriterios=new String[constantes.MAX_CRITERIES][4];
    if(cCriteria!=""){
      bOK=makeCriteriaStruct(cCriteria,aCriterios);
    }else{
      bOK=true;
    }
    if(bOK){
      bOK=false;
      String cComparing="";
      int nSign=1;
      int nField=getFieldIndex(cField);
      if(bIsMaximun){
        // se busca el mInimo
        cValue[0]="";
      }else{
        nSign=-1;
        if(bIsNumerical){
          cValue[0]=Integer.toString(constantes.MAX_INT);
        }else{
          cValue[0]=constantes.MAX_STRING;
        }
      }
      if(bIsNumerical){
        for(i=0;i<nRows;i++){
          cComparing=cRecords[i][nField];
          // comparaciOn entre nUmeros
          try{
            if((Float.parseFloat(cComparing)*nSign)>(Float.parseFloat((String)cValue[0])*nSign)){
              // debe cumplirse con el filtro, si es que hubo
              if(cCriteria!=""){
                if(!(findFirst0(aCriterios,i,i+1)<0)){
                  cValue[0]=cComparing;
                  bOK=true;
                  index=i;
                }
              }else{
                cValue[0]=cComparing;
                bOK=true;
                index=i;
              }
            }
          }
          catch(Exception except){
            if(bIsMaximun){
              cValue[0]="0";
            }else{
              cValue[0]=constantes.MAX_STRING;
            }
          }
        }
      }else{
        for(i=0;i<nRows;i++){
          cComparing=cRecords[i][nField];
          // comparaciOn entre strings
          if(cComparing.compareTo(cValue[0])*nSign>0){
            // debe cumplirse con el filtro, si es que hubo
            if(cCriteria!=""){
              if(!(findFirst0(aCriterios,i,i+1)<0)){
                cValue[0]=cComparing;
                bOK=true;
                index=i;
              }
            }else{
              cValue[0]=cComparing;
              bOK=true;
              index=i;
            }
          }
        }
      }
    }
    if(bOK){
      // todo estA bien, se encontrO un valor extremo
      i=index;
    }else{
      // no encontrO el valor extremo
      i=-1;
      cValue[0]=constantes.ERR_VALUE;
    }
    SYNControl1();
    return i;
  }
  private int getFieldIndex(String cField){
    int idx=-1;
    if(nFields>0){
      do{
        idx++;
        if(cField.equalsIgnoreCase(cFields[idx])){
          break;
        }
      }while((idx<nFields));
      if(!(idx<nFields)){
        idx=-1;
      }
    }
    return idx;
  }
/*  public synchronized String z_GetFieldValue(String cField){
    SYNControl0();
    int idx=0;
    String value="";
    idx=GetFieldIndex(cField);
    if(idx<0){
      value=constantes.ERR_VALUE;
    }else{
      value=GetFieldValue(idx);
    }
    SYNControl1();
    return value;
  }*/
  private boolean checkCriteriaSpecification(String[][] aCriterios){
    boolean bOK=false;
    int i=0,idx=0;
    for(i=0;(i<constantes.MAX_CRITERIES)&&(aCriterios[i][0]!=null);i++){
      try{
        idx=Integer.parseInt(aCriterios[i][0]);
      }
      catch(NumberFormatException except){
        idx=getFieldIndex(aCriterios[i][0]);
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
/*  public synchronized boolean z_SetFieldValue(int nIndex,String value){
    SYNControl0();
    boolean bOK=false;
    if(nBookmark<nRows){
      if(nIndex<nFields){
        cRecords[nBookmark][nIndex]=value;
        bOK=true;
        nChanges++;
      }
    }
    SYNControl1();
    return bOK;
  }*/
/*  public synchronized boolean z_SetFieldValue(String cField,String value){
    SYNControl0();
    boolean bOK=false;
    int idx=0;
    idx=GetFieldIndex(cField);
    if(idx<0){
    }else{
      bOK=SetFieldValue(idx,value);
    }
    SYNControl1();
    return bOK;
  }*/

  public synchronized boolean commitTrans(){
    SYNControl0();
    boolean bOK=false;
    int i=0,j=0;
    String cField="";
    if(nChanges>0){
      // necesita guardar en el archivo
      try{
        BufferedWriter bwFILE=new BufferedWriter(new FileWriter(cFileName,false));
        if (cFields!=null){
          // escribe los campos, empezando línea con #
          bwFILE.write("#");
          for(j=0;j<nFields-1;j++){ // -1 para no escribir el separador al final
            if(cFields[j]==null){
              cField="";
            }else{
              cField=cFields[j].trim();
            }
            bwFILE.write(cField+cSeparator);
          }
          if(j<nFields){  // complemento para las anteriores escrituras
            if(cFields[j]==null){
              cField="";
            }else{
              cField=cFields[j].trim();
            }
            bwFILE.write(cField);
            j++;
          }
          bwFILE.newLine();
        }
        // escribe cada tupla, solo con los valores de sus atributos
        for(i=0;i<nRows;i++){
          for(j=0;j<nFields-1;j++){   // -1 para no escribir el separador al final
            if(cRecords[i][j]==null){
              cField="";
            }else{
              cField=cRecords[i][j].trim();
            }
            cField=cField+cSeparator;
            bwFILE.write(cField);
          }
          if(j<nFields){ // complemento para las anteriores escrituras
            if(cRecords[i][j]==null){
              cField="";
            }else{
              cField=cRecords[i][j].trim();
            }
            bwFILE.write(cField);
            j++;
          }
          bwFILE.newLine();
        }
        bwFILE.flush();
        bwFILE.close();
        bOK=true;
        nChanges=0;
      }
      catch(IOException except){
       System.err.println("no se pudo abrir el archivo '"+cFileName+"'\n" +
        except.toString());
      }
    }else{
      bOK=true;
    }
    SYNControl1();
    return bOK;
  }
/*  public synchronized int z_GetBookmark(){
  int kBookmark=0;
    SYNControl0();

    kBookmark=nBookmark;
    SYNControl1();
    return kBookmark;
  }*/
  public synchronized int addNew(){
  // Agrega una nueva tupla a la tabla.
  // - mueve el <nBookmark> a la nueva posición
  //   si hay éxito
    SYNControl0();
    int index=-1;
    if(cRecords!=null){
      if(nRows+1<constantes.MAX_ROWS){
        index=nRows;
        nBookmark=index;
        nRows++;
      }else{
        index=-1;
      }
    }
    SYNControl1();
    return index;
  }
/*  public synchronized boolean z_SetValues(String cFields,String cValues){
    SYNControl0();
    boolean bOK=false;
    String[] aFields=new String[constantes.MAX_FIELDS];
    String[] aValues=new String[constantes.MAX_FIELDS];
    int i=0,j=0;
    i=Split(cFields,aFields,constantes.LIST_SEPARATOR);
    if((i<0)||(i>=constantes.MAX_FIELDS)){
      bOK=false;
    }else{
      j=Split(cValues,aValues,constantes.LIST_SEPARATOR);
      if((j<0)||(j>=constantes.MAX_FIELDS)||(j!=i)){
        bOK=false;
      }else{
        bOK=true;
        for(j=0;(j<i)&&(bOK);j++){
          bOK=SetFieldValue(aFields[j],aValues[j]);
        }
      }
    }
    SYNControl1();
    return bOK;
  }*/
/*  public synchronized boolean z_GetValues(String cFields,String[] aValues){
  // obtiene el arreglo de valores que corresponden a los
  // campos solicitados
  // - destrulle el contenido del arreglo <aString>
    SYNControl0();
    boolean bOK=false;
    String[] aFields=new String[constantes.MAX_FIELDS];
    int i=0,j=0;
    i=Split(cFields,aFields,constantes.LIST_SEPARATOR);
    if((i<0)||(i>=constantes.MAX_FIELDS)||(aValues.length!=i)){
      bOK=false;
    }else{
      bOK=true;
      for(j=0;(j<i)&&(bOK);j++){
        aValues[j]=GetFieldValue(aFields[j]);
        bOK=aValues[j]!=constantes.ERR_VALUE;
      }
    }
    SYNControl1();
    return bOK;
  }*/
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      v
  // correcciones con nBookmark externo para soportar multi-threading
  //                      ^
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  public synchronized boolean isLast(int kBookmark){
    SYNControl0();
    boolean bIsLast=false;
    if(kBookmark<nRows){
      if(kBookmark==(nRows-1)){
        bIsLast=true;
      }
    }
    SYNControl1();
    return bIsLast;
  }
  public synchronized boolean isAway(int kBookmark){
    SYNControl0();
    boolean bIsAway=true;
    if(kBookmark<nRows){
      bIsAway=false;
    }
    SYNControl1();
    return bIsAway;
  }
  private synchronized int _firstIndex(){
    SYNControl0();
    int kBookmark=-1;
    if(nRows>0){
      nBookmark=0;
    }
    SYNControl1();
    return kBookmark;
  }
  public synchronized int lastIndex(){
    SYNControl0();
    int kBookmark=-1;
    if(nRows>0){
      kBookmark=nRows-1;
    }
    SYNControl1();
    return kBookmark;
  }
  public synchronized int nextIndex(int kBookmark){
    SYNControl0();
    int idx=-1;
    if (kBookmark+1<nRows){
      idx=nBookmark+1;
    }else{
      // no quedan más tuplas y se ha llegado al final
      // del archivo
    }
    SYNControl1();
    return idx;
  }
  public synchronized int moveTo(int kBookmark,int n){
    SYNControl0();
    int idx=-1;
    if(((kBookmark+n)<nRows)&&(nRows>0)){
      idx=nBookmark+n;
    }
    SYNControl1();
    return idx;
  }
  //------------g----e----t----s-----------------------------------vvvvvvvvvvvvvvvvvvvv
  public synchronized boolean getValues(String cCriteria,String cFields,String[] aValues){
  // obtiene el arreglo de valores que corresponden a los
  // campos solicitados en la tupla que cumple con el criterio
  // indicado
  // - destrulle el contenido del arreglo <aValues>
    SYNControl0();
    boolean bOK=false;
    int idx=0;
    idx=_findFirst(cCriteria,0,nRows);
    if(idx>=0){
      bOK=_getValues(idx,cFields,aValues);
    }
    SYNControl1();
    return bOK;
  }
  public synchronized String getFieldValue(int kBookmark,int nIndex){
    String cValue="";
    SYNControl0();
    cValue=_getFieldValue(kBookmark,nIndex);
    SYNControl1();
    return cValue;
  }
  public synchronized String getFieldValue(int kBookmark,String cField){
    String cValue="";
    SYNControl0();
    cValue=_getFieldValue(kBookmark,cField);
    SYNControl1();
    return cValue;
  }
  public synchronized boolean getValues(int kBookmark,String cFields,String[] aValues){
    boolean bOk=false;
    SYNControl0();
    bOk=_getValues(kBookmark,cFields,aValues);
    SYNControl1();
    return bOk;
  }

  private String _getFieldValue(int kBookmark,int nIndex){
    String value="";
    if((nIndex<nFields) && (kBookmark<nRows)){
      try{
        value=cRecords[kBookmark][nIndex].trim();
      }
      catch(Exception except){
        // protección contra valores nulos
        value="";
      }
    }
    return value;
  }
  private String _getFieldValue(int kBookmark,String cField){
    int idx=0;
    String value="";
    idx=getFieldIndex(cField);
    if(idx<0){
      value=constantes.ERR_VALUE;
    }else{
      value=_getFieldValue(kBookmark,idx);
    }
    return value;
  }
  private boolean _getValues(int kBookmark,String cFields,String[] aValues){
  // obtiene el arreglo de valores que corresponden a los
  // campos solicitados
  // - destrulle el contenido del arreglo <aString>
    boolean bOK=false;
    String[] aFields=new String[constantes.MAX_FIELDS];
    int i=0,j=0;
    i=tdutils.split(cFields,aFields,constantes.LIST_SEPARATOR,constantes.MAX_FIELDS);
    if((i<0)||(i>=constantes.MAX_FIELDS)||(aValues.length!=i)){
      bOK=false;
    }else{
      bOK=true;
      for(j=0;(j<i)&&(bOK);j++){
        aValues[j]=_getFieldValue(kBookmark,aFields[j]);
        bOK=aValues[j]!=constantes.ERR_VALUE;
      }
    }
    return bOK;
  }
  public synchronized boolean print(){
  // para director
  // obtiene el arreglo de valores que corresponden a los
  // campos solicitados en la tupla que cumple con el criterio
  // indicado
  // - destrulle el contenido del arreglo <aValues>
    SYNControl0();
    boolean bOK=false;
    int i=0,j=0;
    for(i=0;i<this.nRows;i++){
      for(j=0;j<this.nFields;j++){
        System.out.print(cRecords[i][j]);
        if(j<nFields-1){
          System.out.print(":");
        }else{
          System.out.print("\n");
        }
      }
    }
    SYNControl1();
    return bOK;
  }
  //------------g----e----t----s-----------------------------------^^^^^^^^^^^^^^^^^^^^
  //------------s----e----t----s-----------------------------------vvvvvvvvvvvvvvvvvvvv
  public synchronized boolean getAndSetValues(String cCriteria,String cFields,
    String[] aValues,String cFields2Set,String cValues2Set){
  // para director
  // obtiene el arreglo de valores que corresponden a los
  // campos solicitados en la tupla que cumple con el criterio
  // indicado
  // - destrulle el contenido del arreglo <aValues>
    SYNControl0();
    boolean bOK=false;
    int idx=0;
    idx=_findFirst(cCriteria,0,nRows);
    if(idx>=0){
      bOK=_getValues(idx,cFields,aValues);
    }
    if(bOK){
      bOK=_setValues(idx,cFields2Set,cValues2Set);
    }
    SYNControl1();
    return bOK;
  }
  public synchronized boolean setOrAddValues(String cCriteria,String cFields,String cValues){
    return SetOrAddValues(cCriteria,cFields,cValues,"");
  }
  public synchronized boolean SetOrAddValues(String cCriteria,String cFields,String cValues,String cFreeables){
  // para director
    SYNControl0();
    boolean bOK=false;
    int idx=0;
    idx=_findFirst(cCriteria,0,nRows);
    if(idx>=0){
      bOK=_setValues(idx,cFields,cValues);
    }else{
      idx=_addNew(cFreeables);
      bOK=_setValues(idx,cFields,cValues);
    }
    SYNControl1();
    return bOK;
  }
  public synchronized boolean setValues(String cCriteria,String cFields,String cValues){
    SYNControl0();
    boolean bOK=false;
    int idx=0;
    idx=_findFirst(cCriteria,0,nRows);
    if(idx>=0){
      bOK=_setValues(idx,cFields,cValues);
    }
    SYNControl1();
    return bOK;
  }
  public synchronized boolean setFieldValue(int kBookmark,int nIndex,String cValue){
    boolean bOK=false;
    SYNControl0();
    bOK=_setFieldValue(kBookmark,nIndex,cValue);
    SYNControl1();
    return bOK;
  }
  public synchronized boolean setFieldValue(int kBookmark,String cField,String cValue){
    boolean bOK=false;
    SYNControl0();
    bOK=_setFieldValue(kBookmark,cField,cValue);
    SYNControl1();
    return bOK;
  }
  public synchronized boolean setValues(int kBookmark,String cFields,String cValues){
    boolean bOK=false;
    SYNControl0();
    bOK=_setValues(kBookmark,cFields,cValues);
    SYNControl1();
    return bOK;
  }
  private boolean _setFieldValue(int kBookmark,int nIndex,String value){
    boolean bOK=false;
    if(kBookmark<nRows){
      if(nIndex<nFields){
        cRecords[kBookmark][nIndex]=value;
        bOK=true;
        nChanges++;
      }
    }
    return bOK;
  }
  private boolean _setFieldValue(int kBookmark,String cField,String value){
    boolean bOK=false;
    int idx=0;
    idx=getFieldIndex(cField);
    if(idx<0){
    }else{
      bOK=_setFieldValue(kBookmark,idx,value);
    }
    return bOK;
  }
  private boolean _setValues(int kBookmark,String cFields,String cValues){
    boolean bOK=false;
    String[] aFields=new String[constantes.MAX_FIELDS];
    String[] aValues=new String[constantes.MAX_FIELDS];
    int i=0,j=0;
    i=tdutils.split(cFields,aFields,constantes.LIST_SEPARATOR,constantes.MAX_FIELDS);
    if((i<0)||(i>=constantes.MAX_FIELDS)){
      bOK=false;
    }else{
      j=tdutils.split(cValues,aValues,constantes.LIST_SEPARATOR,constantes.MAX_FIELDS);
      if((j<0)||(j>=constantes.MAX_FIELDS)||(j!=i)){
        bOK=false;
      }else{
        bOK=true;
        for(j=0;(j<i)&&(bOK);j++){
          bOK=_setFieldValue(kBookmark,aFields[j],aValues[j]);
        }
      }
    }
    return bOK;
  }
  //------------s----e----t----s-----------------------------^^^^^^^^^^^^^^^^^^^^
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
  //                      v
  // controles para sincronizaciOn de los mEtodos
  //                      ^
  //                      |
  //                      |
  //                      |
  //                      |
  //                      |
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
  final class TABLE_Constantes{
    final int MAX_FIELDS=10;
    final int MAX_ROWS=20;
    final int MAX_CRITERIES=10;
    final String ERR_VALUE="nofield";
    final String DEF_SEPARATOR="\t";
    final String CRITERIA_SEPARATOR="°";
    final String MAX_STRING="zzzzzzzzzzzzzzzzz";
    final int MAX_INT=100000000;
    final String LIST_SEPARATOR=",";
  }
}

