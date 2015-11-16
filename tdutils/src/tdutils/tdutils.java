package tdutils;
import java.io.*;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Contiene métodos y clases utilitarios de propósito general.
 * @version 1.0
 */
public class tdutils {
  /**
   * <br>Se dedica a portar los objetos y los métodos
   * que deben ser invocados por cada pulso del lector.</br>
   */
  public static class PortaMetodos{
    public Method met;
    public Object obj;
    public PortaMetodos(Object obj0,Method met0){
      obj=obj0;
      met=met0;
    }
    public void limpia(){
      obj=null;
      met=null;
    }
  }
  public static class SplitInfo{
    public String nombreArchivo;
    public int tamanoArchivo;
  }
  public static final int ZIP_MAXLENTH=1024*30; // 30KB
  public static void appendFiles(String cFileName,String cSubFileName){
    DataInputStream isSubFileReader;
    DataOutputStream osFileWriter;
    byte[] buffer=new byte[1024];
    try {
      isSubFileReader = new DataInputStream(
          new FileInputStream(cSubFileName));
      osFileWriter = new DataOutputStream(
          new FileOutputStream(cFileName,true));
      while(isSubFileReader.available()>0){
        isSubFileReader.read(buffer);
        osFileWriter.write(buffer);
      }
      isSubFileReader.close();
      osFileWriter.flush();
      osFileWriter.close();
    }
    catch (FileNotFoundException ex) {
    }
    catch (IOException ex) {
    }
  }
  public static void appendFilesExt(String cFileName,String[] asSubFilesNames){
    DataInputStream isSubFileReader;
    DataOutputStream osFileWriter;
    String cSubFileName="";
    int i=0;
    byte[] buffer=new byte[1024];
    try {
      while (i < asSubFilesNames.length) {
        cSubFileName=asSubFilesNames[i];
        isSubFileReader = new DataInputStream(
            new FileInputStream(cSubFileName));
        osFileWriter = new DataOutputStream(
            new FileOutputStream(cFileName, true));
        while (isSubFileReader.available() > 0) {
          isSubFileReader.read(buffer);
          osFileWriter.write(buffer);
        }
        isSubFileReader.close();
        osFileWriter.flush();
        osFileWriter.close();
        i++;
      }
    }
    catch (FileNotFoundException ex) {
    }
    catch (IOException ex) {
    }
  }
  /**
   * Borra el contenido de un directorio recursivamente.
   * @param directorio El directorio a borrar.
   */
  public static boolean borraDir(File directorio){
    int i=0;
    boolean bOK=false;
    File fDirI=null;
    if(directorio.isFile()){
      bOK=directorio.delete();
      if(!bOK){
//        System.err.println("No se pudo borrar el archivo '" +
//            directorio.getPath()+"'");
      }
    }else{
      if(directorio.isDirectory()){
        i=0;
        while(directorio.listFiles().length>0){
          fDirI=directorio.listFiles()[0];
          bOK=borraDir(fDirI);
          if(!bOK){
            break;
          }
        }
        bOK=directorio.delete();
      }
    }
    if(!bOK){
      System.err.println("No se pudo borrar el archivo o directorio '" +
            directorio.getPath()+"'");
    }
    return bOK;
  }
  /**
   * Crea un filtrador de archivos que tengan el prefijo dado.
   * @param nombre_prefijo El valor del sufijo a comparar con el de los 
   * archivos, null para indicar que solo filtre archivos.
   * @return El objeto filtrador.
   */
  public static java.io.FilenameFilter creaFiltroPrefijoArchivos(final 
    String nombre_prefijo){
    FilenameFilter ofFiltra=new FilenameFilter(){
      public boolean accept(File dir, String name) {
        File archivo;
        if(nombre_prefijo==null){
          archivo=new File(dir,name);
          if(archivo.isFile()){
            return true;
          }else{
            return false;
          }
        }
        if(name.startsWith(nombre_prefijo)){
          archivo=new File(dir,name);
          if(archivo.isDirectory()){
            return false;
          }else{
            return true;
         }
        }else{
          return false;
        }
      }      
     };
     return ofFiltra;
   }
  /**
   * Crea un filtrador de archivos que tengan el patrón dado.
   * @param patron El valor del patrón a comparar con el de los 
   * archivos, null para indicar que solo filtre archivos.
   * @return El objeto filtrador.
   */
  public static java.io.FilenameFilter creaFiltroGenericoArchivos(final 
    String patron){
    final Pattern pPatron=Pattern.compile(patron);
    FilenameFilter ofFiltra=new FilenameFilter(){
      public boolean accept(File dir, String name) {
        File archivo;
        if(patron==null){
          archivo=new File(dir,name);
          if(archivo.isFile()){
            return true;
          }else{
            return false;
          }
        }
        Matcher mComparador=pPatron.matcher(name);
        if(mComparador.matches()){
          archivo=new File(dir,name);
          if(archivo.isDirectory()){
            return false;
          }else{
            return true;
         }
        }else{
          return false;
        }
      }      
     };
     return ofFiltra;
  }
  /**
   * Crea un filtrador de archivos o directorios que tengan el patrón dado.
   * @param patron El valor del patrón a comparar con el de los 
   * archivos.
   * @return El objeto filtrador.
   */
  public static FilenameFilter creaFiltroGenerico(final 
    String patron){
    final Pattern pPatron;
    if(patron==null){
        pPatron=Pattern.compile("*");
    }else{
      pPatron=Pattern.compile(patron);
    }
    FilenameFilter ofFiltra=new FilenameFilter(){
      public boolean accept(File dir, String name) {
        File archivo;
        if(patron==null){
          return true;
        }
        Matcher mComparador=pPatron.matcher(name);
        if(mComparador.matches()){
          archivo=new File(dir,name);
          return true;
        }else{
          return false;
        }
      }      
     };
     return ofFiltra;
   }
  /**
  * Crea un filtrador de directorios que tengan el sufijo dado.
  * @param nombre_prefijo El valor del sufijo a comparar con el de los 
  * directorios, null para indicar que solo filtre directorios.
  * @return El objeto filtrador.
  */
 public static java.io.FilenameFilter creaFiltroPrefijoDirectorios(final 
   String nombre_prefijo){
   FilenameFilter ofFiltra=new FilenameFilter(){
     public boolean accept(File dir, String name) {
       File dir_contenido;
       if(nombre_prefijo==null){
         dir_contenido=new File(dir,name);
         if(dir_contenido.isDirectory()){
           return true;
         }else{
           return false;
         }
       }
       if(name.startsWith(nombre_prefijo)){
         dir_contenido=new File(dir,name);
         if(!dir_contenido.isDirectory()){
           return false;
         }else{
           return true;
        }
       }else{
         return false;
       }
     }      
    };
    return ofFiltra;
  }   

   /**
   * Crea un filtrador de archivos que tengan el sufijo dado.
   * @param nombre_sufijo El valor del sufijo a comparar con el de los 
   * archivos, null para indicar que solo filtre archivos.
   * @return El objeto filtrador.
   */
  public static java.io.FilenameFilter creaFiltroSufijoArchivos(final 
    String nombre_sufijo){
    FilenameFilter ofFiltra=new FilenameFilter(){
      public boolean accept(File dir, String name) {
        File archivo;
        if(nombre_sufijo==null){
          archivo=new File(dir,name);
          if(archivo.isFile()){
            return true;
          }else{
            return false;
          }
        }
        if(name.endsWith(nombre_sufijo)){
          archivo=new File(dir,name);
          if(archivo.isDirectory()){
            return false;
          }else{
            return true;
         }
        }else{
          return false;
        }
      }      
     };
     return ofFiltra;
   }
  /**
  * Crea un filtrador de directorios que tengan el sufijo dado.
  * @param nombre_sufijo El valor del sufijo a comparar con 
  * el de los directorios, null para indicar que solo filtre directorios.
  * @return El objeto filtrador.
  */
 public static java.io.FilenameFilter creaFiltroSufijoDirectorios(final 
   String nombre_sufijo){
   FilenameFilter ofFiltra=new FilenameFilter(){
     public boolean accept(File dir, String name) {
       File dir_contenido;
       if(nombre_sufijo==null){
         dir_contenido=new File(dir,name);
         if(dir_contenido.isDirectory()){
           return true;
         }else{
           return false;
         }
       }
       if(name.endsWith(nombre_sufijo)){
         dir_contenido=new File(dir,name);
         if(!dir_contenido.isDirectory()){
           return false;
         }else{
           return true;
        }
       }else{
         return false;
       }
     }      
    };
    return ofFiltra;
  }
/**
 * Examina un archivo en formato tabular y devuelve la 
 * cantidad de columnas halladas por la cantidad de filas. 
 * @param fEntrada El archivo tabular a examinar.
 * @return La cantidad de columnas por la cantidad de filas
 * que hay en un archivo que se supone estar dispuesto en
 * forma tabular. 
 */  
  public static long getColumnasXFilas(File fEntrada) 
        throws FileNotFoundException{
    long nColumnas=0,nFilas=0,nBytes=0;
    String sTemp="";
    LineNumberReader lnrEntrada=null;
    lnrEntrada=new LineNumberReader(new FileReader(fEntrada));
    nBytes=fEntrada.length();
    try {
      sTemp=lnrEntrada.readLine();
      lnrEntrada.skip(nBytes-(sTemp.getBytes().length));
      nFilas=lnrEntrada.getLineNumber();
      lnrEntrada.close();
      nColumnas=sTemp.split("\t").length;
    } catch (IOException e) {
    	e.printStackTrace();
    }
    return nFilas*nColumnas;
  }
  /**
   * Obtiene el tiempo en milisegundos transcurrido entre la media noche
   * del primero de enero de 1970 y el instante de la invocación al método.
   * @return El número de milisegundos transcurrido.
   */
  final public static long getCurrentTime(){
    try {
      return System.currentTimeMillis();
    }
    catch (Exception ex) {
      return 0L;
    }
  }
  public static int getFieldIndex(String cField,Class oTypeOfClass){
    int idx=-1;
    if(oTypeOfClass.getFields().length>0){
      do{
        idx++;
        if(cField.equalsIgnoreCase(oTypeOfClass.getFields()[idx].getName())){
          break;
        }
      }while((idx<oTypeOfClass.getFields().length));
      if(!(idx<oTypeOfClass.getFields().length)){
        idx=-1;
      }
    }
    return idx;
  }
  public static double getNumber(Object oo,int nCampo,Class oTypeOfClass){
  double nResultado=0.0;
  String nombre;
    //catch(java.lang.IllegalAccessException except)
    nombre=oo.getClass().getFields()[nCampo].getName();
    try{
      nResultado = oo.getClass().getField(nombre).getDouble(oo);
    }catch(Exception ex){
    }
    return nResultado;
    /*
    try{
      nResultado=(int)oTypeOfClass.getFields()[nCampo].getByte(oo);
    }catch(Exception except0){
      try{
        nResultado=(int)oTypeOfClass.getFields()[nCampo].getChar(oo);
      }catch(Exception except1){
        try{
          nResultado=(int)oTypeOfClass.getFields()[nCampo].getDouble(oo);
        }catch(Exception except2){
          try{
            nResultado=(int)oTypeOfClass.getFields()[nCampo].getFloat(oo);
          }catch(Exception except3){
            try{
              nResultado=(int)oTypeOfClass.getFields()[nCampo].getInt(oo);
            }catch(Exception except4){
              try{
                nResultado=(int)oTypeOfClass.getFields()[nCampo].getLong(oo);
              }catch(Exception except5){
                try{
                  nResultado=(int)oTypeOfClass.getFields()[nCampo].getShort(oo);
                }catch(Exception except6){
                  try{
                    nResultado=Double.parseDouble(""+oTypeOfClass.getFields()
                    [nCampo].get(oo));
                  }catch(Exception except7){
                    System.err.println(except7.getMessage());
                    except7.printStackTrace();
                    nResultado=0;
                  }
                }
              }
            }
          }
        }
      }
    }
    return nResultado;
        */
  }
  public final static String getQ(boolean str){
    return "\'"+str+"\'";
  }
  public final static String getQ(byte str){
    return "\'"+str+"\'";
  }
  public final static String getQ(double str){
    return "\'"+str+"\'";
  }
  public final static String getQ(float str){
    return "\'"+str+"\'";
  }
  public final static String getQ(int str){
    return "\'"+str+"\'";
  }
  public final static String getQ(long str){
    return "\'"+str+"\'";
  }
  public final static String getQ(Object str){
    return "\'"+str+"\'";
  }
  /**
   * Obtiene la hilera de caracteres contenida en un arreglo de bytes.
   * @param abytes Arreglo de bytes del cual sacar la hilera.
   * @return Hilera de letras contenidas en el arreglo de bytes.
   */
  final public static String getString(byte[] abytes){
    String interpretación="";
    int nread=0;
    for(int i=0;i<abytes.length;i++){
      nread=abytes[i];
      if(nread==0){
        break;
      }
      interpretación+=(char)nread;
    }
    return interpretación;
  }
  /**
   * Obtiene datos del momento actual.
   * @return El objeto Date del instante en que el método fue llamado.
   */
  public final static java.util.Date getTime(){
    return new java.util.GregorianCalendar().getTime();
  }
  public final static String getXMLHeader(){
    return "<?xml version=\"1.0\" ?>";
  }
  public static class Complejo{
    public double real;
    public double imaginaria;
    public Complejo(double i,double r){
      this.real=r;
      this.imaginaria=i;
    }
  }
  public static void prueba001(){
    int i=0;
    FilenameFilter filtro=null;
    File dir=new File("c:\\MyProjects\\eclipse\\mineria\\" +
            "experimentos\\gigante\\uniforme\\sin_terminar02");
    File[] archivos=null;
    // filtro=creaFiltroGenerico("D[LT][0-9]{8}.+\\.lck");
    filtro=creaFiltroGenerico("D[LT][0-9]{8}[^\\.].*");
    archivos=dir.listFiles(filtro);
    i=0;
    while(i<archivos.length){
      System.out.println(archivos[i].getAbsolutePath());
      i++;
    }    
  }
  public static void prueba002(){
    MapaEstadistico map=new MapaEstadistico(String.CASE_INSENSITIVE_ORDER,
        Complejo.class);
    try{
      map.agrega("1",new Complejo(3,4));
      map.agrega("2",new Complejo(4,7));
      map.agrega("3",new Complejo(8,6));
      map.agrega("4",new Complejo(6,2));
      map.agrega("5",new Complejo(8,8));
      map.agrega("6",new Complejo(4,9));
      map.agrega("7",new Complejo(3,4));
      map.agrega("8",new Complejo(2,3));
      map.agrega("9",new Complejo(6,7));
      map.agrega("10",new Complejo(4,3));
      System.out.println("sumatoria parte real: "+ 
          map.demeTotal("real"));
      System.out.println("promedio parte real: "+ 
          map.demeMedia("real"));
      System.out.println("sumatoria parte imaginaria: "+ 
          map.demeTotal("imaginaria"));
      System.out.println("promedio parte imaginaria: "+ 
          map.demeMedia("imaginaria"));
      System.out.println("Mediana real: "+
          ((Complejo)map.demeMediana("real")).real);
      System.out.println("Mediana imaginaria: "+
          ((Complejo)map.demeMediana("imaginaria")).imaginaria);
    }catch(Exception ex){
      System.err.println(ex.getMessage());
    }    
  }
  public static void main(String args[]){
    prueba002();
  }
  public final static String pad(String codigo,char car,int length,boolean left){
    String res="";
    int nfill=length-codigo.length();
    res=padExt(codigo,car,nfill,length,left);
    return res;
  }
  public final static String padExt(String codigo,char car,int nfill,
      int length,boolean left){
    String res="";
    int i=0;
    while (i<nfill){
      i++;
      res+=car;
    }
    if(left){
      res = (res + codigo);
      if(res.length() > length && length>0){
        res = res.substring(res.length() - length);
      }
    }else{
      res = (codigo + res);
      if(res.length()>length && length>0){
        res = res.substring(0, length);
      }
    }
    return res;
  }
  public final static String padL(String codigo,char car,int length){
    return pad(codigo,car,length,true);
  }
  public final static String padLExt(String codigo,char car,
      int nfill,int length){
    return padExt(codigo,car,nfill,length,true);
  }
  public final static String padR(String codigo,char car,int length){
    return pad(codigo,car,length,false);
  }
  public final static String padRExt(String codigo,char car,int nfill,
      int length){
    return padExt(codigo,car,nfill,length,false);
  }
  public static byte[] readFile(String cNombreArchivo){
    DataInputStream isSubFileReader;
    byte[] abContenido=new byte[1024];
    ByteArrayOutputStream baOut=new ByteArrayOutputStream(abContenido.length);
    int length=0,total=0;
    try {
      isSubFileReader = new DataInputStream(
          new FileInputStream(cNombreArchivo));
          
      while(isSubFileReader.available()>0){
        length=isSubFileReader.read(abContenido);
        baOut.write(abContenido,0,length);
        total+=length;
      }
      abContenido=null;
      abContenido=baOut.toByteArray();
      baOut.close();
      isSubFileReader.close();
      System.out.println("\nEl total leído es: "+total+" bytes.\n");
    }
    catch (FileNotFoundException ex) {
    }
    catch (IOException ex) {
    }
    return abContenido;
  }
  public static int split(String cIN,String[] cOUT,String cSeparator0){
    int j=0,idx=0,jdx=0;
    if(cIN!=""){
      // parsea la tupla para obtener sus atributos
      // porque split de String viene a partir de JDK 1.4
      idx=0;
      jdx=cIN.indexOf(cSeparator0);
      do{
        if (jdx<0){
          cOUT[j]= cIN.substring(idx);
        } else{
          cOUT[j]= cIN.substring(idx,jdx);
        }
        idx=jdx+1;
        jdx=cIN.indexOf(cSeparator0,idx);
        j++;
      }while((idx > 0) && (j<cOUT.length));
    }
    return j;
  }
  public static int split(String cIN,String[] cOUT,String cSeparator0,
      int nMaxFields){
    int j=0,idx=0,jdx=0;
    if(cIN!=""){
      // parsea la tupla para obtener sus atributos
      // porque split de String viene a partir de JDK 1.4
      idx=0;
      jdx=cIN.indexOf(cSeparator0);
      do{
        if (jdx<0){
          cOUT[j]= cIN.substring(idx);
        } else{
          cOUT[j]= cIN.substring(idx,jdx);
        }
        idx=jdx+1;
        jdx=cIN.indexOf(cSeparator0,idx);
        j++;
      }while((idx > 0) && (j<nMaxFields));
    }
    return j;
  }
  public static int splitFile(String cFileName,int nPartsSize,boolean isSize){
    return splitFile(cFileName,nPartsSize,isSize,"");
  }
  /**
   * Original tomado de sourceforge.net fue hecho por x_dim001@yahoo.com .
   * Cambiado por Alessandro Cordero (alesscor@ieee.org).
   * @param cFileName Nombre del archivo a dividir.
   * @param nPartsSize Número de partes o tamaño máximo de los nuevos archivos.
   * @param isSize Si nPartsSize es el tamaño de los nuevos archivos.
   * @param cDirDestino Directorio de destino de los nuevos archivos.
   * @return La cantidad de archivos obtenidos.
   */
  public static int splitFile(String cFileName,int nPartsSize,boolean isSize,
                               String cDirDestino){
    //current position of the file pointer
    long pos = 0;
    int i = 0;
    double nTotalSize;
    int rem,nAmount=0;
    RandomAccessFile raSubArchivo=null;
    RandomAccessFile raMaestro;
    byte[] buffer;
    if(cDirDestino!=""){
      cDirDestino += "/";
    }
    try {
      raMaestro = new RandomAccessFile(cFileName, "r");
      nTotalSize = raMaestro.length();
      // The remainder if the nTotalSize is not exactly divisible by nPartsSize
      rem = (int) nTotalSize % nPartsSize;
      if(!isSize){
        nAmount=nPartsSize;
      }else{
        nAmount=(int)Math.round((double)nTotalSize/(double)nPartsSize +0.5000);
      }
      // Create n-1 file pieces
      for (; i < nAmount - 1; i++) {
        raMaestro.seek(pos);
        if(isSize){
          buffer = new byte[nPartsSize];
        }else{
          buffer = new byte[ (int) (nTotalSize / nPartsSize)];
        }
        raMaestro.read(buffer);
        pos = raMaestro.getFilePointer();
        //write the piece
        raSubArchivo = new RandomAccessFile(
            cDirDestino+cFileName + "." + 
            padL(String.valueOf(i+1),'0',10), "rw");
        raSubArchivo.write(buffer);
        raSubArchivo.close();
        raSubArchivo=null;
      }
      // write the last piece with 'remainder' if any
      raMaestro.seek(pos);
      if(isSize){
        buffer = new byte[rem];
      }else{
        buffer = new byte[ (int) (nTotalSize / nPartsSize) + rem];
      }
      raMaestro.read(buffer);
      raSubArchivo = new RandomAccessFile(
          cDirDestino+cFileName + "." + 
          padL(String.valueOf(i+1),'0',10), "rw");
      raSubArchivo.write(buffer);
      raSubArchivo.close();
      raSubArchivo=null;
      raMaestro.close();
      raMaestro=null;
    }catch (Exception ex) {
        ex.printStackTrace();
    }
    return nAmount;
  }
  public static SplitInfo[] splitFileExt(String cFileName,int nPartsSize,
                                      boolean isSize){
    return splitFileExt(cFileName,nPartsSize,isSize,"");
  }
  /**
   * Original tomado de sourceforge.net fue hecho por x_dim001@yahoo.com .
   * Cambiado por Alessandro Cordero (alesscor@ieee.org).
   * @param cFileName Nombre del archivo a dividir.
   * @param nPartsSize Número de partes o tamaño máximo de los nuevos archivos.
   * @param isSize Si nPartsSize es el tamaño de los nuevos archivos.
   * @param cDirDestino Directorio de destino de los nuevos archivos.
   * @return Pares ordenados que indican el nombre de un archivo y su tamaño.
   */
  public static SplitInfo[] splitFileExt(String cFileName,int nPartsSize,
                                      boolean isSize,String cDirDestino){
    //current position of the file pointer
    long pos = 0;
    int i = 0;
    double nTotalSize;
    int rem,nAmount=0;
    RandomAccessFile raSubArchivo=null;
    RandomAccessFile raMaestro;
    File fMaestro;
    byte[] buffer;
    SplitInfo[] asNombres=null;
    if(cDirDestino!=""){
      cDirDestino += "/";
    }
    try {
      fMaestro=new File(cFileName);
      raMaestro = new RandomAccessFile(fMaestro, "r");
      cFileName=fMaestro.getName();
      nTotalSize = raMaestro.length();
      // The remainder if the nTotalSize is not exactly divisible by nPartsSize
      rem = (int) nTotalSize % nPartsSize;
      if(!isSize){
        nAmount=nPartsSize;
      }else{
        nAmount=(int)Math.round((double)nTotalSize/(double)nPartsSize +0.5000);
      }
      asNombres=new SplitInfo[nAmount];
      // Create n-1 file pieces
      for (; i < nAmount - 1; i++) {
        asNombres[i]=new SplitInfo();
        asNombres[i].nombreArchivo=cDirDestino+cFileName + "." + 
          padL(String.valueOf(i+1),'0',10);        
        raMaestro.seek(pos);
        if(isSize){
          buffer = new byte[nPartsSize];
        }else{
          buffer = new byte[ (int) (nTotalSize / nPartsSize)];
        }
        asNombres[i].tamanoArchivo=buffer.length;
        raMaestro.read(buffer);
        pos = raMaestro.getFilePointer();
        //write the piece
        raSubArchivo = new RandomAccessFile(asNombres[i].nombreArchivo, "rw");
        raSubArchivo.write(buffer);
        raSubArchivo.close();
        raSubArchivo=null;
      }
      // write the last piece with 'remainder' if any
      asNombres[i]=new SplitInfo();
      asNombres[i].nombreArchivo=cDirDestino+cFileName + "." + 
        padL(String.valueOf(i+1),'0',10);
      raMaestro.seek(pos);
      if(isSize){
        buffer = new byte[rem];
      }else{
        buffer = new byte[ (int) (nTotalSize / nPartsSize) + rem];
      }
      asNombres[i].tamanoArchivo=buffer.length;
      raMaestro.read(buffer);
      raSubArchivo = new RandomAccessFile(asNombres[i].nombreArchivo, "rw");
      raSubArchivo.write(buffer);
      raSubArchivo.close();
      raSubArchivo=null;
      raMaestro.close();
      raMaestro=null;
    }catch (Exception ex) {
        ex.printStackTrace();
    }
    return asNombres;
  }
  public static boolean copiaArchivo(File fOrigen,
        File fDestino)throws IOException{
    return mueveArchivo(fOrigen,fDestino,true);
  }
  public static boolean mueveArchivo(File fOrigen,
      File fDestino)throws IOException{
  return mueveArchivo(fOrigen,fDestino,false);
}
  public static boolean mueveArchivo(File fOrigen,File fDestino,
        boolean noborrar)throws IOException{
    int leido=0;
    boolean bOK=false;
    byte[] buffer=new byte[1024*4];
    BufferedInputStream biOrigen=null;
    BufferedOutputStream boDestino=null;
    if(fDestino.exists()||!fOrigen.exists()||!fDestino.
            getParentFile().exists()){
      return false;
    }
    if(fDestino.exists()||!fOrigen.exists()){
      return false;
    }
    if(!noborrar){
      bOK=fOrigen.renameTo(fDestino);
    }
    if(bOK){
      while(!fDestino.exists()){
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      return bOK;
    }
    biOrigen=new BufferedInputStream(new FileInputStream(fOrigen));
    boDestino=new BufferedOutputStream(new FileOutputStream(fDestino));
    while(true){
      leido=biOrigen.read(buffer);
      if(leido<0){
        break;
      }
      boDestino.write(buffer, 0, leido);
    }
    biOrigen.close();
    boDestino.flush();
    boDestino.close();
    if(noborrar){
      bOK=true;
    }else{
      bOK=fOrigen.delete();
    }
    while(bOK && !fDestino.exists()){
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return bOK;
  }
  public static class Parejas{
    String nombre;
    double valor;
    public Parejas(){
      nombre="";
      valor=0;
    }
    public Parejas(String nombre0,double valor0){
      nombre=nombre0;
      valor=valor0;
    }
  }
  /**
   * Particiona el conjunto dado en sus subconjuntos substituyentes.
   * <li>Devuelve un conjunto de cardinalidad 2^lista.size().</li>
   * @param lista El conjunto a particionar.
   * @return Las particiones del conunto dado.
   */
  public static Collection[] particionaArreglo(Collection lista){
    Collection resultado[]=new Collection[2^lista.size()];
    Object listaEntrada[]=lista.toArray();
    int indices[]=new int[resultado.length];
    int i,j,pos,p;
    // los primeros elementos
    for(i=0;i<lista.size();i++){
      resultado[i]=new HashSet();
      resultado[i].add(listaEntrada[i]);
      indices[i]=i;
    }
    // crea el conjunto vacío
    resultado[resultado.length-1]=new HashSet();
    // prepara los demás elementos del resultado
    i=lista.size();
    for(pos=lista.size();pos<resultado.length;pos++){
      j=pos-resultado.length;
      p=0;
      while(p<lista.size()){
        if(indices[j]<p){
          // los elementos del conjunto j deben estar en el conjunto i
          // así como el nuevo elemento de p.
          resultado[i]=new HashSet();
          resultado[i].addAll(resultado[j]);
          resultado[i].addAll(resultado[p]);
          indices[i]=p;
          i++;
        }
        p++;
      }
    }
    return resultado;
  }
  /**
   * Particiona el conjunto dado en sus subconjuntos substituyentes.
   * <li>Devuelve un conjunto de cardinalidad 2^lista.size().</li>
   * @param lista El conjunto a particionar.
   * @return Las particiones del conunto dado.
   */
  public static Collection particionaColeccion(Collection lista){
    Map resultado=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    int tamanoMaximo=2^lista.size();
    Object listaEntrada[]=lista.toArray();
    Collection collI;
    int indices[]=new int[tamanoMaximo];
    int i,j,pos,p;
    // los primeros elementos
    for(i=0;i<lista.size();i++){
      collI=new HashSet();
      resultado.put(String.valueOf(i),collI);
      indices[i]=i;
    }
    // crea el conjunto vacío
    collI=new HashSet();
    resultado.put(String.valueOf(0),collI);
    // prepara los demás elementos del resultado
    i=lista.size();
    for(pos=lista.size();pos<tamanoMaximo;pos++){
      j=pos-tamanoMaximo;
      p=0;
      while(p<lista.size()){
        if(indices[j]<p){
          // los elementos del conjunto j deben estar en el conjunto i
          // así como el nuevo elemento de p.
          collI=new HashSet();
          collI.addAll((Collection)resultado.get(String.valueOf(j)));
          collI.addAll((Collection)resultado.get(String.valueOf(p)));
          resultado.put(String.valueOf(i),collI);
          indices[i]=p;
          i++;
        }
        p++;
      }
    }
    return resultado.values();
  }
  public static Map reparteOptimo(Map lista,double nMaximo){
    double[] pesos=new double[lista.size()];
    Map[] mapas=new Map[lista.size()*lista.size()];
    Parejas[][] parejas=new Parejas[lista.size()][lista.size()];
    java.util.Iterator itr=lista.values().iterator();
    Map resultado=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    int i=0,j=0;
    while(i<(lista.size()*lista.size())){
      mapas[i]=new TreeMap(String.CASE_INSENSITIVE_ORDER);
      if(i<lista.size()){
        mapas[i].put(String.valueOf(i), lista.values().toArray()[i]);
      }else{
        // para el presente i
        j=i-lista.size();
        itr=mapas[j].values().iterator();
        while(itr.hasNext()){
          mapas[i].put(String.valueOf(i), itr.next());
          j++;
        }
        j=(i%lista.size())+1;
        if(j<lista.size()){
          mapas[i].put(String.valueOf(i+j), lista.values().toArray()[j]);
        }
      }
      i++;
    }
    // llena el resto del arreglo menos el campo size()xsize().
    i=1;
    while(i<lista.size()){
      j=0;
      while(j<lista.size()){
        
      }
    }
    return resultado;
  }
  
  public tdutils() {
  }
}