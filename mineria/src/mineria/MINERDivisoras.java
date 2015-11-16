/*
 * Created on 10/06/2004
 *
 */
package mineria;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import tdutils.tdutils;
import admin.ADMINAPPDivisoras;
import admin.ADMINAPPExcPreejecucion;
import admin.ADMINAPPExcepcion;
import admin.ADMINAPPIArchivos;
import admin.ADMINAPPISub_trabajos;
import admin.ADMINAPPITrabajos;
import admin.ADMINAPPIniciador;
/**
 * <br>Instancias de esta clase dividen la entrada de datos de 
 * una aplicación de tdderive.</br>
 <pre>
      ..    .                                     .
    ..  .   ..                                    .
   ..   ..  ..    ..   ...  ...   ...  ....    .. . ....   ...
  ........  ..   .... ...  ...   .   . ..  . .    . ..  . .  ..
  ..    ..  ..  ..      ..   ..  .  .. .   . .    . ..    .   .
  ..    ..  ..   ...  ...  ...    .. . .   .   . .  ..     ...
 </pre>
 */

public class MINERDivisoras extends ADMINAPPDivisoras {
  final String bloqueo_gestion_divisora="objeto de bloqueo de gestión divisora";
  final static String NOMBRE_SALIDA_ESTANDAR="dderive.salida-estandar.txt";
  final static String NOMBRE_SALIDA_ERROR="dderive.salida-error.txt";
  final static String MENSAJE_ERROR="Error en el archivo de entrada";
	/**
	 * 
	 */
	public MINERDivisoras() {
	}
	/**
   * Divide a un trabajo o subtrabajo.
   * @param trabajo
   *                La tarea o el subtrabajo a dividir.
   * @param siTrabajo
   *                Si es una tarea o un subtrabajo.
	 */
  protected void divideTrabajoOderSubTrabajo(ADMINAPPITrabajos trabajo,
        boolean siTrabajo) throws admin.ADMINAPPExcPreejecucion{
  	// divideTrabajo02(trabajo,siTrabajo);
    divideTrabajoDeVerdad(trabajo,siTrabajo);
  }
  /**
   * Divide el trabajo propuesto a dderive en subtrabajos más pequeños. 
	 * @throws ADMINAPPExcepcion
	 */
	private void divideTrabajo01(ADMINAPPITrabajos trabajo) 
        throws admin.ADMINAPPExcPreejecucion{
    int conteo=0,i=0,j=0;
    String sDirTrabajo="",sArchNombre="",comando="";
    final String comandoFinal;
    String[] parametros;
    final File ofDirectorio;
    File[] lista_archivos=null;
    Iterator itr=null;
    FilenameFilter ofFiltraDTs=null,ofFiltraLCKs=null;
    Runtime objetoEjecucion=Runtime.getRuntime();
    ADMINAPPIArchivos archivo=null;
    ADMINAPPISub_trabajos subtraI=null;
		/*
     * revisa detalles del trabajo,
     * particularmente los datos de entrada para
     * subdividir el trabajo. 
		 */
    //|
    //| nombres y comprobaciones
    //|
    sDirTrabajo=trabajo.getTarea().getTareaDir();
    // busca el archivo de entrada, revisando en la información dejada 
    // en info_archivo.
    itr=trabajo.getTarea().getArchivos().values().iterator();
    while(itr.hasNext()){
      archivo=(ADMINAPPIArchivos)itr.next();
      if(archivo.getInfoArchivo().compareTo("-f")==0){
        break;
      }else{
        archivo=null;
      }
    }
    if(archivo==null){
      throw new admin.ADMINAPPExcPreejecucion("No se encuentra el archivo de " +
            "entrada de dderive.");        
    }
    sArchNombre=archivo.getNombre();
    // chequeos de su existencia
    ofDirectorio=new File(sDirTrabajo);
    ofFiltraDTs=tdutils.creaFiltroPrefijoArchivos("DT");
    ofFiltraLCKs=tdutils.creaFiltroSufijoArchivos(".lck");
    if(ofDirectorio.exists()&&ofDirectorio.isDirectory()){
        //|
        //| prepara la división
        //|
        System.out.println("Prepara el comando.");
        comando=trabajo.getTarea().getPrograma().getRuta()+
                " -f"+sArchNombre+" -s -I -F";
        comandoFinal=comando;
        System.out.println("El comando va a ser: "+comando);
        lista_archivos=gestionaDivision02(comando,ofDirectorio,ofFiltraLCKs,
                objetoEjecucion,3);
        //|
        //| crea subtrabajos, asignado los archivos de entrada como
        //| debe corresponder y demás valores cruciales.
        //|
        i=0;
        while(i<lista_archivos.length){
          // agrega un subtrabajo que trabajará sobre el archivo
          // correspondiente
          subtraI=trabajo.addSubTrabajo();
          subtraI.setRutasEntrada(lista_archivos[i].getName());
          subtraI.setCarga(lista_archivos[i].length());
          parametros=trabajo.getTarea().getParametrosArr();
          // prepara el comando de la ejecución de un subtrabajo
          j=0;
          comando="";
          while(j<parametros.length){
            if(parametros[j].startsWith("-f")){
              comando+="-f"+lista_archivos[i].getName()+ADMINAPPIniciador.APP_SEPARADORARGUMENTOS;
            }else{
              comando+=parametros[j];
            }
            j++;
          } // de parámetros
          // el comando está listo y se completa para darlo a un subtrabajo.
          /*
           * <2005 El comando ya está listo. />
           * comando=trabajo.getTarea().getPrograma().getRuta() + ADMINAPPIniciador.APP_SEPARADORARGUMENTOS+comando;
           */
          subtraI.setComando(comando);
          
          // agrega el archivo de entrada del subtrabajo.
          archivo=subtraI.addArchivo(lista_archivos[i].getName());
          // asigna archivo al subtrabajo
          archivo.setNombre(lista_archivos[i].getName());
          // reubica los archivos 
//          subtraI.ubicaArchivosDirTrabajo();
          i++;
        }
    	
      // el trabajo está siendo dividido
    }else{
      throw new ADMINAPPExcPreejecucion("Problemas en el sistema de archivos " +
            "supuesto para la ejecución.");
    }		
	}
    /**
     * Divide el trabajo propuesto a dderive en subtrabajos más pequeños.
     * @throws ADMINAPPExcepcion
     */
    private void divideTrabajo02(ADMINAPPITrabajos trabajo,boolean siTarea)
        throws admin.ADMINAPPExcPreejecucion{
    String sArchNombre="",comando="",parametros0;
    String[] sDirTrabajo=new String[]{""};
    final File ofDirectorio;
    File[] lista_archivos=null;
    FilenameFilter ofFiltraDTs=null,ofFiltraLCKs=null;
    ADMINAPPIArchivos iaEntrada=null;
    ADMINAPPISub_trabajos isSubTra=null;
    //|
    //| nombres y comprobaciones para sacar el archivo de entrada
    //|
    iaEntrada=sacaArchivoEntrada(siTarea,trabajo,sDirTrabajo);
    if(iaEntrada==null){
      throw new admin.ADMINAPPExcPreejecucion("No se encuentra el " +
            "archivo de entrada de dderive.");        
    }
    sArchNombre=iaEntrada.getNombre();
    parametros0=trabajo.getTarea().getParametros();
    ofDirectorio=new File(sDirTrabajo[0]);
    // este filtro se especializa en encontrar evidencias de trabajo 
    // en un subnivel inferior al del conjunto de datos inicial,
    // éstos son llamados ARCHIVOS RESIDUALES
    ofFiltraLCKs=tdutils.creaFiltroGenerico("D[LT][0-9]{8}[^\\.].*");
    if(ofDirectorio.exists()&&ofDirectorio.isDirectory()){
        comando = generaComando(trabajo, sArchNombre, parametros0);
        //|
        //| realiza la división.
        //|
        lista_archivos=this.gestionaDivision04(comando,ofDirectorio,ofFiltraLCKs,3);  // ojo, que yo mas desesperado (al 2006/11/2)
        // borra archivos residuales
        borraResiduos(ofDirectorio, lista_archivos,ofFiltraLCKs);
        //|
        //| crea subtrabajos, asignado los archivos de entrada como
        //| debe corresponder y demás valores cruciales
        //| toma los archivos a usar como entrada de cada subtrabajo.
        //|
        ofFiltraLCKs=tdutils.creaFiltroGenerico("D[LT][0-9]{8}");
        lista_archivos=ofDirectorio.listFiles(ofFiltraLCKs);
        this.preparaSubTrabajo(trabajo, lista_archivos);
    }else{
      throw new ADMINAPPExcPreejecucion("Problemas en el sistema " +
            "de archivos supuesto para la ejecución.");
    }       
  }
    /**
     * Divide el trabajo propuesto a dderive en subtrabajos más pequeños.
     * @throws ADMINAPPExcepcion
     */
    private void divideTrabajoDeVerdad(ADMINAPPITrabajos trabajo,boolean siTarea)
        throws admin.ADMINAPPExcPreejecucion{
    String sArchNombre="",comando="",parametros0;
    String[] sDirTrabajo=new String[]{""};
    final File ofDirectorio;
    File[] lista_archivos=null;
    FilenameFilter ofFiltraDTs=null,ofFiltra=null;
    ADMINAPPIArchivos iaEntrada=null;
    ADMINAPPISub_trabajos isSubTra=null;
    //|
    //| nombres y comprobaciones para sacar el archivo de entrada
    //|
    iaEntrada=sacaArchivoEntrada(siTarea,trabajo,sDirTrabajo);
    if(iaEntrada==null){
      throw new admin.ADMINAPPExcPreejecucion("No se encuentra el " +
            "archivo de entrada de dderive.");        
    }
    sArchNombre=iaEntrada.getNombre();
    parametros0=trabajo.getTarea().getParametros();
    ofDirectorio=new File(sDirTrabajo[0]);
    // este filtro se especializa en encontrar evidencias de trabajo 
    // en un subnivel inferior al del conjunto de datos inicial,
    // éstos son llamados ARCHIVOS RESIDUALES
    ofFiltra=tdutils.creaFiltroGenerico("D[LT][0-9]{8}[^\\.].*");
    if(ofDirectorio.exists()&&ofDirectorio.isDirectory()){
        comando = generaComandoDeVerdad(trabajo, sArchNombre, parametros0);
        //|
        //| realiza la división.
        //|
        lista_archivos=this.gestionaDivisionDeVerdad(comando,ofDirectorio,ofFiltra);
        // borra archivos residuales
         borraResiduos(ofDirectorio, lista_archivos,ofFiltra);
        //|
        //| crea subtrabajos, asignado los archivos de entrada como
        //| debe corresponder y demás valores cruciales
        //| toma los archivos a usar como entrada de cada subtrabajo.
        //|
        ofFiltra=tdutils.creaFiltroGenerico("D[LT][0-9]{8}");
        lista_archivos=ofDirectorio.listFiles(ofFiltra);
        this.preparaSubTrabajo(trabajo, lista_archivos);
    }else{
      throw new ADMINAPPExcPreejecucion("Problemas en el sistema " +
            "de archivos supuesto para la ejecución.");
    }       
  }
  /**
   * Pone valores a cada subtrabajo con el fin de dejarlo preparado
   * para el planificador central y para la envoltura que lo acompañará
   * en su ejecución.
	 * @param trabajo El trabajo cuyos subtrabajos serán completados.
	 * @param lista_archivos La lista de archivos que corresponde 
   *        a cada subtrabajo.
	 */
	private void preparaSubTrabajo(ADMINAPPITrabajos trabajo,
            File[] lista_archivos) {
		int i;
		int j;
		String comando;
		String[] parametros;
		ADMINAPPIArchivos iaEntrada;
		ADMINAPPISub_trabajos isSubTra;
		i=0;
    // el trabajo será dividido
    while(i<lista_archivos.length){
      // agrega un subtrabajo que trabajará sobre el archivo
      // correspondiente
      isSubTra=trabajo.addSubTrabajo();
      // [1] hacer directorio para el subtrabajo
      // [2] mover el archivo de entrada a ese directorio o copiarlo
      // [3] asignar la nueva ruta de entrada
      // [4] confirmar los datos para el subtrabajo nuevo
      isSubTra.setRutasEntrada(lista_archivos[i].getAbsolutePath());
      try {
        //|
        //| pone carga al subtrabajo.
        //|
        //| antes era: isSubTra.setCarga(lista_archivos[i].length());
        //|
        MINERDivisoras.setCarga(isSubTra,lista_archivos[i]);
      } catch (FileNotFoundException e1) {
        e1.printStackTrace();
        isSubTra.setCarga(ADMINAPPISub_trabajos.SIN_CARGA);
      }
      parametros=trabajo.getTarea().getParametrosArr();
      // prepara el comando de la ejecución de un subtrabajo
      j=0;
      comando="";
      while(j<parametros.length){
        if(parametros[j].startsWith("-f")){
          comando+="-f"+lista_archivos[i].getName()+
                        ADMINAPPIniciador.APP_SEPARADORARGUMENTOS;
        }else{
          comando+=parametros[j]+
          ADMINAPPIniciador.APP_SEPARADORARGUMENTOS;
        }
        j++;
      } // de parámetros
      /* 
       * <2005 No por encontrar un ambiente autóctono probablemente distinto cuando se exporta/>
       * comando=trabajo.getTarea().getPrograma().getRuta() + ADMINAPPIniciador.APP_SEPARADORARGUMENTOS+comando;
       * el comando ya está listo, otros detalles son brindados por la tarea (alias y su ruta)        
       */
      isSubTra.setComando(comando);
      // agrega el archivo de entrada del subtrabajo.
      iaEntrada=isSubTra.addArchivo(lista_archivos[i].getName());
      iaEntrada.setNombre(lista_archivos[i].getName());
      iaEntrada.setRutaOriginal(lista_archivos[i].getAbsolutePath());
      try {
    		// reubica los archivos 
    		  trabajo.ubicaEntradas(isSubTra,false);
    	} catch (ADMINAPPExcepcion e) {
        System.err.println("Error al ubicar los archivos.");
    		e.printStackTrace();
        // si no puede trabajar, que la carga no valga nada
        isSubTra.setCarga(ADMINAPPISub_trabajos.SIN_CARGA);
    	}
      //|
      //| el comando del subtrabajo está listo y completo para darlo 
      //| a una envoltura, por mientras se puede poner en lista de espera
      //| bajo lo que dicte el planificador.
      //|
      i++;
    }
    if(lista_archivos.length==0){
      System.out.println("Error en la entrada");
      File ferror=new File(trabajo.getTarea().getResultadosDir()+"/"+ MINERDivisoras.NOMBRE_SALIDA_ERROR);
      try{
        ferror.createNewFile();
        BufferedOutputStream boDestino=new BufferedOutputStream(new FileOutputStream(ferror));
        boDestino.write(MINERDivisoras.MENSAJE_ERROR.getBytes());
        boDestino.flush();
        boDestino.close();
      }catch(IOException e){
        e.printStackTrace();
      }
    }
	}

 /**
   * Genera el comando de la división del trabajo.    
	 * @param trabajo
	 * @param sArchNombre
	 * @param parametros0
	 * @return El comando generado.
	 */
	private String generaComando(ADMINAPPITrabajos trabajo, 
            String sArchNombre, String parametros0) {
		String comando;
		    //|
        //| prepara la división, adaptándose a los parámetros
        //| de generación solicitados.
        //|
        // System.out.println("Prepara el comando.");
        // construye el comando
        // supone que los parámetros no se repiten
        comando=trabajo.getTarea().getPrograma().getRuta()+
                " -f"+sArchNombre+" -s -I -F";
        if(parametros0.indexOf("-u")>=0){
          comando+=" -u";
        }
        if(parametros0.indexOf("-D")>=0){
          comando+=" -D";
        }
        if(parametros0.indexOf("-g")>=0){
          comando+=" -g";
        }
        if(parametros0.indexOf("-e")>=0){
          comando+=" -e";
        }
        // System.out.println("El comando va a ser: "+comando);
		return comando;
	}

   /**
   * Genera el comando de la división del trabajo.    
   * @param trabajo
   * @param sArchNombre
   * @param parametros0
   * @return El comando generado.
   */
  private String generaComandoDeVerdad(ADMINAPPITrabajos trabajo, 
            String sArchNombre, String parametros0) {
    String comando;
        //|
        //| prepara la división, adaptándose a los parámetros
        //| de generación solicitados.
        //|
        // System.out.println("Prepara el comando.");
        // construye el comando
        // supone que los parámetros no se repiten
        comando=trabajo.getTarea().getPrograma().getRuta()+
                " -f"+sArchNombre+" -s -I -S -F";
        if(parametros0.indexOf("-u")>=0){
          comando+=" -u";
        }
        if(parametros0.indexOf("-D")>=0){
          comando+=" -D";
        }
        if(parametros0.indexOf("-g")>=0){
          comando+=" -g";
        }
        if(parametros0.indexOf("-e")>=0){
          comando+=" -e";
        }
        // System.out.println("El comando va a ser: "+comando);
        comando=comando + " > "+ MINERDivisoras.NOMBRE_SALIDA_ESTANDAR +" 2>&1 ";
        System.out.println("El comando va a ser: "+comando);
    return comando;
  }

  
/**
	 * @param ofDirectorio
	 * @param lista_residuos
	 */
	private void borraResiduos(final File ofDirectorio, File[] lista_residuos,
            FilenameFilter ofFiltraLCKs) {
		int i;
		    //
        // borra archivos restantes de la división
        //
        i=0;
        while(i<lista_residuos.length){
          lista_residuos[i].delete();
          i++;
        }
        //
        // borra otros archivos residuales
        //
        ofFiltraLCKs=tdutils.creaFiltroGenerico("D[LT][0-9]{8}\\.lck");
        lista_residuos=ofDirectorio.listFiles(ofFiltraLCKs);
        i=0;
        while(i<lista_residuos.length){
          lista_residuos[i].delete();
          while(lista_residuos[i].exists()){
            lista_residuos[i].delete();
          }
          System.out.println("borrando a "+lista_residuos[i].getName());
          i++;
        }
	}
  private void limpiaDirectorio(final File ofDirectorio) {
    File[] lista_residuos=null;
    FilenameFilter ofFiltraLCKs=null;
    int i;
    ofFiltraLCKs=tdutils.creaFiltroGenerico("D[LT][0-9]{8}.*");
    lista_residuos=ofDirectorio.listFiles(ofFiltraLCKs);
    i=0;
    while(i<lista_residuos.length){
      lista_residuos[i].delete();
      i++;
    }
    // borra otros archivos que son de bloqueo
    ofFiltraLCKs=tdutils.creaFiltroGenerico(".*\\.[lL][cC][kK]");
    lista_residuos=ofDirectorio.listFiles(ofFiltraLCKs);
    i=0;
    while(i<lista_residuos.length){
      lista_residuos[i].delete();
      i++;
    }    
  }

private File[] gestionaDivision01(final String comando,final File directorio,
        final FilenameFilter filtro,final Runtime objetoEjecucion,final int cantidad){
    int conteo=0;
    File[] lista_archivos=null;
    Runnable rnn=null;
    Thread hilo=null;
    long lTInicio=0,lTActual=0;
    rnn=new Runnable(){
      // ----------------------------------------------------
        public Process proceso=null;                      //-
          public void run(){                              //-
            try {                                         //-
                proceso=objetoEjecucion.exec(comando,     //-       
                        null,directorio);                 //-
              try {                                       //-
                proceso.waitFor();                        //-
              } catch (InterruptedException e1) {         //-
                proceso.destroy();                        //-
                System.err.println("Proceso destruído " + //-
                        "en hilo envolvente.");           //-
              }                                           //-
            } catch (IOException e) {                     //-
                e.printStackTrace();                      //-
            }                                             //-
          }                                               //-
       // ---------------------------------------------------
    };
    hilo=new Thread(rnn);
    while(conteo<cantidad){
        if(!hilo.isAlive()){
          hilo.start();
          lTInicio=System.currentTimeMillis();
        }
        /*
         * La generación de cada árbol y hoja se realiza de forma secuencial,
         * empezando con el conjunto inicial de datos, siguiendo a partir
         * de éste con la generación de sus hojas y subárboles, y luego en 
         * la generación de cada subárbol de la misma forma aplicada al
         * conjunto inicial en comportamiento recursivo.
         * 
         * Acciones llevadas a cabo por dderive:
         * Los archivos DT son árboles, a partir de los cuales se pueden
         * generar hojas o subárboles.
         * Los archivos DL son hojas.
         * Los DT son acompañados por achivos de bloqueo (sufijo .lck) mientras
         * éstos son generados y mientras sus subárboles u hojas son generados.
         * Los DL son acompañados por archivos de bloqueo (sufijo .lck) mientras
         * éstos son generados.
         * Cuando un archivo DT es terminado de generar, junto con todos sus
         * descendientes, se borra su archivo de bloqueo.
         * Cuando un archivo DL es terminado de generar, se burra su archivo 
         * de bloqueo.
         * Cuando los DL son terminados de generar los DT son borrados
         */
        lTActual=System.currentTimeMillis();
        conteo=directorio.listFiles(filtro).length;
        if(conteo>=cantidad){
          hilo.interrupt();
          System.out.println("Hilo supuestamente matado.");            
        }
        if(lTActual-lTInicio>20000){
          if(hilo.isAlive()){
            hilo.interrupt();
          }
          break;
        }
        System.out.println("Hay "+conteo+" objetos en el directorio.");
    }
    lista_archivos=directorio.listFiles(filtro);
    conteo=lista_archivos.length;
    System.out.println("Finalmente hay "+conteo +" objetos en " +
            "el directorio.");
    return lista_archivos;
  }
  private File[] gestionaDivision02(final String comando,final File directorio,
      final FilenameFilter filtro,final Runtime objetoEjecucion,final int cantidad){
      File[] lista_archivos=null;
      Runnable rnn=null;
      Thread hilo=null;
      final boolean[] salir=new boolean[1];
      final Process[] proceso=new Process[1];
      rnn=new Runnable(){
        // ----------------------------------------------------
            public void run(){                              //-
              int conteo=0;
              salir[0]=false;
              try{
                  while(conteo<cantidad && !salir[0]){
                  	conteo=directorio.listFiles(filtro).length;
                  	if(conteo>=cantidad){
                      if(proceso[0]!=null){
                      	proceso[0].destroy();
                    		System.out.println("Proceso supuestamente matado.");
                        break;
                      }
                  	}
                    System.out.println("Hay "+conteo+" objetos en el directorio.");
                  }
                  if(salir[0]){
                    System.out.println("La salida forzosa terminó!!!!");
                  }
                }catch(Exception ex){
                  System.out.println("Saldrá del run().");
                }
            }
         // ---------------------------------------------------
      };
      proceso[0]=null;
      hilo=new Thread(rnn);
      hilo.start();
      try {
        proceso[0]=objetoEjecucion.exec(comando,null,directorio);
        hilo.join(20000);
        if(hilo.isAlive()){
          hilo.interrupt();
          salir[0]=true;
          System.out.println("Se ha intentado MATAR AL HILO < 1 >.");
          proceso[0].destroy();
        }
      } catch (IOException e) {
        e.printStackTrace();
        hilo.interrupt();
        salir[0]=true;
        System.out.println("Se ha intentado MATAR AL HILO < 2 >.");
        proceso[0].destroy();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }    
      lista_archivos=directorio.listFiles(filtro);
      System.out.println("Finalmente hay "+lista_archivos.length+" objetos en " +
          "el directorio.");
    return lista_archivos;
  }
  private File[] gestionaDivision03(final String comando,final File directorio,
      final FilenameFilter filtro,final int cantidad){
  int conteo=0;
  long lTInicio=0,lTActual=0;
  File[] lista_archivos=null;
  Runnable rnn=null;
  Thread hilo=null;
  final Runtime objetoEjecucion=Runtime.getRuntime();
  rnn=new Runnable(){
    // ----------------------------------------------------
      public Process proceso=null;                      //-
        public void run(){                              //-
          try {                                         //-
            proceso=objetoEjecucion.exec(comando,       //-       
                    null,directorio);                   //-
            synchronized(bloqueo_gestion_divisora){     //-
              try {                                     //-
                  bloqueo_gestion_divisora.wait();      //-
                  proceso.destroy();                    //-
                  System.err.println("Proceso " +       //-
                      "de división destruído " +        //-
                      "en hilo envolvente (hilo).");    //-
              } catch (InterruptedException e1) {       //-
                proceso.destroy();                      //-
                System.err.println("Proceso " +         //-
                    "de división destruído " +          //-
                    "en hilo envolvente (ex).");        //-
              }                                         //-
            }                                           //-
          } catch (IOException e0) {                    //-
              e0.printStackTrace();                     //-
          }                                             //-
        }                                               //-
     // ---------------------------------------------------
  };
  hilo=new Thread(rnn);
  while(conteo<cantidad){
      if(!hilo.isAlive()){
        hilo.start();
        if(!hilo.isAlive()&&!hilo.isInterrupted()){
          // se resucita el hilo (¿?)
          hilo=null;
          hilo=new Thread(rnn);
          hilo.start();
        }
        lTInicio=System.currentTimeMillis();
      }
      lTActual=System.currentTimeMillis();
      conteo=directorio.listFiles(filtro).length;
      if(conteo>=cantidad){
        synchronized(bloqueo_gestion_divisora){
          bloqueo_gestion_divisora.notifyAll();
        }
        System.out.println("Hilo supuestamente terminado.");            
      }
      // TODO 00 Este valor (3min) debe venir de la configuración de programas.
      if(lTActual-lTInicio>180000){
        if(hilo.isAlive()){
          hilo.interrupt();
        }
        break;
      }
      System.out.println("Hay "+conteo+" objetos en el directorio.");
  }
  lista_archivos=directorio.listFiles(filtro);
  conteo=lista_archivos.length;
  System.out.println("Finalmente hay "+conteo +" objetos en " +
          "el directorio.");
  return lista_archivos;
  }
  private File[] gestionaDivision04(final String comando,final File directorio,
      final FilenameFilter filtro,final int cantidad){
    int conteo=0;
    long lTInicio=0,lTActual=0;
    File[] lista_archivos=null;
    Thread hilo=null;
    final Runtime objetoEjecucion=Runtime.getRuntime();
    Process proceso=null;
    proceso=null;
    lTInicio=0;
    lTActual=0;
    conteo=0;
    while(conteo<cantidad){
      if (proceso==null && conteo==0){
        synchronized(objetoEjecucion){
          try {
              if(lTInicio>0){
                limpiaDirectorio(directorio);
  //              System.out.println("- Directorio limpio para trabajar. -");
              }
              proceso=objetoEjecucion.exec(comando,null,directorio);
          } catch (IOException e) {
            e.printStackTrace();
          }
          lTInicio=System.currentTimeMillis();
        }
      }
      lTActual=System.currentTimeMillis();
      conteo=directorio.listFiles(filtro).length;
      if(conteo>=cantidad){
        synchronized(objetoEjecucion){
          try {
            objetoEjecucion.wait(0, 2);
          } catch (InterruptedException e) {
          }
          proceso.destroy();
          proceso=null;
        }
  //      System.out.println("Proceso de división destruído (1).");            
      }
      // TODO 00 Este valor (3min) debe venir de la configuración de programas.
      if((lTActual-lTInicio>180000)&&!(conteo>=cantidad)){
  //      proceso.destroy();
  //     proceso=null;
  //      System.out.println("Proceso de división destruído (2).");            
      }
  //    System.out.println("Hay "+conteo+" objetos en el directorio.");
    }
    lista_archivos=directorio.listFiles(filtro);
    conteo=lista_archivos.length;
  //  System.out.println("Finalmente hay "+conteo +" objetos en " +
  //          "el directorio.");
    return lista_archivos;
  }  
  private File[] gestionaDivisionDeVerdad(final String comando,final File directorio,
      final FilenameFilter filIndeseables){
    int conteo=0;
    long lTInicio=0;
    File[] lista_archivos=null;
    Thread hilo=null;
    final Runtime objetoEjecucion=Runtime.getRuntime();
    Process proceso=null;
    proceso=null;
    lTInicio=0;
    conteo=0;
    while(true){
      if (proceso==null && conteo==0){
        synchronized(objetoEjecucion){
          try {
              if(lTInicio>0){
                limpiaDirectorio(directorio);
  //              System.out.println("- Directorio limpio para trabajar. -");
              }
              proceso=objetoEjecucion.exec(comando,null,directorio);
          } catch (IOException e) {
            e.printStackTrace();
          }
          lTInicio=System.currentTimeMillis();
        }
      }
      conteo=directorio.listFiles(filIndeseables).length;
      if(true){
        synchronized(objetoEjecucion){
          try {
            // objetoEjecucion.wait();
            proceso.waitFor();
          } catch (InterruptedException e) {
          }

          proceso=null;
          break;
        }
      }
    }
    lista_archivos=directorio.listFiles(filIndeseables);
    conteo=lista_archivos.length;
    return lista_archivos;
  }  

	/**
	 * @see admin.ADMINAPPDivisoras#verificaTrabajo(admin.ADMINAPPITrabajos)
	 */
	protected boolean verificaTrabajo(ADMINAPPITrabajos trabajo) {
		
		return true;
	}
  private ADMINAPPIArchivos sacaArchivoEntrada(boolean siTarea,
        ADMINAPPITrabajos trabajo,String[] sDirTrabajo){
    Iterator itr;
    ADMINAPPISub_trabajos subtraI=null;
    ADMINAPPIArchivos archivo=null;
    if(siTarea){
      // es para dividir una tarea
      sDirTrabajo[0]=trabajo.getTarea().getTareaDir();
      // busca el archivo de entrada, revisando en la información dejada 
      // en info_archivo.
      itr=trabajo.getTarea().getArchivos().values().iterator();
    }else{
      // es para dividir un subtrabajo
      itr=trabajo.getTarea().getSubtrabajos().values().iterator();
      if(itr.hasNext()){
        subtraI=(ADMINAPPISub_trabajos)itr.next();
        sDirTrabajo[0]=subtraI.getSubtrabajoDir();
        itr=subtraI.getArchivos().values().iterator();
      }else{
        archivo=null;
        itr=null;
      }
    }
    while((itr!=null)&&(itr.hasNext())){
      archivo=(ADMINAPPIArchivos)itr.next();
      if((!siTarea)||(archivo.getInfoArchivo().compareTo("-f")==0)){
        break;
      }else{
        archivo=null;
      }
    }
    return archivo;
  }
	/**
   * Asigna la carga del subtrabajo, según el archivo que tiene que
   * operar. 
	 * @param sub_trabajo El subtrabajo cuya carga será asignada.
	 * @param fEntrada El archivo a examinar.
	 * @throws FileNotFoundException Si no se encuentra el archivo.
	 */
  private static void setCarga(ADMINAPPISub_trabajos sub_trabajo,
            File fEntrada) throws FileNotFoundException{
    long nCarga=0;
    nCarga=tdutils.getColumnasXFilas(fEntrada);
    sub_trabajo.setCarga(nCarga);
  }

}
