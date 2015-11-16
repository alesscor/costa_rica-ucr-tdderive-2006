package tdutils;

import java.io.*;
import java.util.*;
import java.util.zip.*;
/**
 * <br>Clase dedicada a la compresión y descompresión de archivos.</br>
 */
public class Zip {
  // by Daniel F. Savarese mailto:dfs@savarese.org
  // Unzipping Files with java.util.zip.ZipFile
  // http://www.devx.com/getHelpOn/10MinuteSolution/20447
  private static final void copyInputStream(InputStream in, OutputStream out) throws
      IOException {
    byte[] buffer = new byte[1024];
    int len;

    while ( (len = in.read(buffer)) >= 0) {
      out.write(buffer, 0, len);

    }
    in.close();
    out.close();
  }
  public static final void unzipFile(String cFileName) {
    unzipFile(cFileName,"");
  }
  /**
   * De un archivo comprimido extrae los archivos contenidos al directorio dado.
   * Por Mike Levin, http://www.mike-levin.com/java/java-index.html .
   * Cambiado por Alessandro Cordero (alesscor@ieee.org).
   * @param cFileName Nombre del archivo comprimido.
   * @param cDirDest Nombre del directorio de destino.
   */
  public static final void unzipFile(String cFileName,String cDirDest) {
    Enumeration entries;
    ZipFile zipFile;
    File oDateiOderVerzeichnis;
    FileOutputStream fos;
    if (cFileName == "") {
      return;
    }
    try {
      oDateiOderVerzeichnis=new File(cFileName);
      zipFile = new ZipFile(oDateiOderVerzeichnis);
      if(cDirDest==""){
        cDirDest=oDateiOderVerzeichnis.getParentFile().getCanonicalPath();
      }
      // oDateiOderVerzeichnis=null;
      if(cDirDest!=""){
        cDirDest += "/";
      }

      entries = zipFile.entries();

      while (entries.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) entries.nextElement();
        if (entry.isDirectory()) {
          // Assume directories are stored parents first then children.
          System.err.println("Extracting directory: " + cDirDest+entry.getName());
          // This is not robust, just for demonstration purposes.
          oDateiOderVerzeichnis = (new File(cDirDest+entry.getName()));
          if (!oDateiOderVerzeichnis.exists()) {
            oDateiOderVerzeichnis.mkdirs();
          }
          continue;
        }

        // System.err.println("Extracting file: " +cDirDest+ entry.getName());
        oDateiOderVerzeichnis=new File(cDirDest+entry.getName());
        if(!oDateiOderVerzeichnis.getParentFile().exists()){
          oDateiOderVerzeichnis.getParentFile().mkdirs();
          oDateiOderVerzeichnis.createNewFile();
        }
        if(!oDateiOderVerzeichnis.exists()){
          oDateiOderVerzeichnis.createNewFile();
          oDateiOderVerzeichnis.exists();
        }
        fos = new FileOutputStream(oDateiOderVerzeichnis);
        copyInputStream(zipFile.getInputStream(entry),
                        new BufferedOutputStream(fos));
      }

      zipFile.close();
      oDateiOderVerzeichnis=null;
    }
    catch (IOException ioe) {
      System.err.println("Unhandled exception:");
      ioe.printStackTrace();
      return;
    }
  }


  /**
   * Comprime un archivo o directorio.
   * Por Mike Levin, http://www.mike-levin.com/java/java-index.html .
   * Cambiado por Alessandro Cordero (alesscor@ieee.org).
   * @param cZipName Nombre del archivo comprimido.
   * @param cSourceName Nombre del recurso de origen.
   */
  public static void zipFile(String cZipName, String cSourceName) throws IOException {
    ZipOutputStream zos = null;
    if (cZipName == null || cZipName == "") {
      return;
    }
    // Call the dateFunc method.
    // String dateName = dateFunc();

    // Add Notation to variable to be used in filename.
    // if (args.length > 1) {
    //        dateName = dateName+"_"+args[1];
    // }
    // Create the file output streams for both the file and the zip.
    // These object variables had to be declared globally outside main.
    zos = new ZipOutputStream(new FileOutputStream(cZipName));
    // It isn't known if source is either a file or a directory
    dirFunc(cSourceName,"", zos);

    // Close the file output streams for both the file and the zip.
    zos.flush();
    zos.close();
  } // main

  // New dirFunc method (not part of main method).
  private static void dirFunc(String dirName, String entries,
      ZipOutputStream zos) {    
    File dirObj = new File(dirName);
    File[] fileList=null;
    // resource name regardlessing descendents
    entries=entries+dirObj.getName();    
    if (dirObj.exists() == true) {
      if (dirObj.isDirectory() == true) {
        entries=entries+"/";
        // Create an array of File objects, one for each file or directory in dirObj.
        fileList = dirObj.listFiles();
        // Loop through File array and display.
        for (int i = 0; i < fileList.length; i++) {
          if (fileList[i].isDirectory()) {
            dirFunc(fileList[i].getPath(),entries, zos);
          }else{
            if (fileList[i].isFile()) {
              // Call the zipFunc function              
              zipFunc(fileList[i].getPath(),
                      entries+fileList[i].getName(), zos);
            }
          }
        } // for loop
      }else {
        // It isn't a directory path.
        zipFunc(dirName,entries,zos);
      }
    }else {
      System.out.println("Source object '" + dirName + "' does not exist.");
    }
    dirObj=null;
    fileList=null;
  }

  // New zipFunc method.
  private static void zipFunc(String filePath,String entries, ZipOutputStream zos) {
    File file;
    // Using try is required because of file io.
    try {
      file=new File(filePath);
      // Create a file input stream and a buffered input stream.
      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(fis);
      // Create a Zip Entry and put it into the archive (no data yet).
      ZipEntry fileEntry = new ZipEntry(entries);
      zos.putNextEntry(fileEntry);

      // Create a byte array object named data and declare byte count variable.
      byte[] data = new byte[1024];
      int byteCount;
      // Create a loop that reads from the buffered input stream and writes
      // to the zip output stream until the bis has been entirely read.
      while ( (byteCount = bis.read(data, 0, 1024)) > -1) {
        zos.write(data, 0, byteCount);
      }
      file=null;
      bis.close();
      fis.close();
    }
    catch (IOException e) {
    }

    // System.out.println("'"+filePath + "' as '"+entries+"'.");
  }

  // New dateFunc method.
  private static String dateFunc() {

    // this creates a Calender object full of current day information.
    Calendar calendar = Calendar.getInstance();
    // This sequence sets String objects.
    // The +"" forces a conversion to String objects.
    String YY = (calendar.get(Calendar.YEAR) + "").substring(2);
    String MM = ( (calendar.get(Calendar.MONTH) + 1) + "");
    String DD = (calendar.get(Calendar.DAY_OF_MONTH) + "");
    String HH = (calendar.get(Calendar.HOUR) + "");
    String MI = (calendar.get(Calendar.MINUTE) + "");

    // The AM_PM field contains 0 or 1, which I have to convert to AM or PM.
    int AMint = (calendar.get(Calendar.AM_PM));
    String AMStr;
    if (AMint == 0) {
      AMStr = "AM";
    }
    else {
      AMStr = "PM";
    }

    // And I do this for consistant date appearance. For example, 00-06-10_04-42-PM.
    if (MM.length() == 1) {
      MM = "0" + MM;
    }
    if (DD.length() == 1) {
      DD = "0" + DD;
    }
    if (HH.length() == 1) {
      HH = "0" + HH;
    }
    if (MI.length() == 1) {
      MI = "0" + MI;
    }

    return (YY + "-" + MM + "-" + DD + "_" + HH + "-" + MI + "-" + AMStr);
  } // end dateFunc

}
