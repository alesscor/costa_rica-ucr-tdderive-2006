package admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import mens.MENSException;
import mens.MENSMensaje;

// import oact.OACTDistribuidorBase.DireccionesVirtuales;

import oact.OACTDistribuidorBase.DireccionesVirtuales;

import org.w3c.dom.Node;

import admin.PERSCoordinacion.Programas;


import orgainfo.*;
import tdutils.EscritorSalidas;
import tdutils.tdutils;

/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Anida clases para la persistencia de datos sobre el ambiente
 * de un agente de <tt>tdderive</tt>. 
 */
public class PERSAmbiente {
  static class Computadora
      extends OIComputadora {
    public final static String BUSES_PC = "BUSES_PC"; 
    public final static String BUSES_SERVIDOR = "BUSES_SERVIDOR";
    private EscritorSalidas _escritor;
    /**
     * Puerto base, necesario para trabajar con computadoras
     * virtuales, es decir tdderives que trabajan en una sola
     * computadora utilizando máquinas virtuales.
     */
    private int desplaza_puerto;
    Map getDireccionesVirtuales(){
      return this._direccionesVirtuales;
    }
    Computadora() {
      super();
      _inicio();
    }
    private void _inicio(){
      _escritor=new EscritorSalidas("Computadoras");
    }

    Computadora(ADMINGLOInfo info0) {
      super(info0);
      _inicio();
    }

    double getPesoUmbralPolitica(String sNombre) throws ADMINGLOExcepcion{
      double valor=0;
      Pesos_Umbrales_Politicas peso;
      if((peso=((Pesos_Umbrales_Politicas)
          this._pesos_umbrales.get(sNombre)))!=null){
        return peso.getValor();        
      }else{
        throw new ADMINGLOExcepcion("No se encontró el valor " +
            "para '"+ sNombre +"'.");
      }
    }
    String getNombre() {
      return nombre;
    }

    public String getDireccion() {
      return direccion;
    }

    double getCapacidad() {
      return capacidad;
    }

    double getCargaFuncional() {
      return carga_funcional;
    }

    double getCargaAplicacion() {
      return carga_aplicacion;
    }

    int getVecinasUsadas() {
      String sSQL=
        " SELECT DISTINCT SUBSTRING(id_parcial,1,LENGTH(id_parcial)-10) AS vecino_sirviente " +
        " FROM envolturas " +
        " WHERE SUBSTRING(id_parcial,1,LENGTH(id_parcial)-10)!= "+ tdutils.getQ(this.nombre) +
        " AND si_actual=true " +
        " AND SUBSTRING(id_tarea,1,LENGTH(id_tarea)-10)="+tdutils.getQ(this.nombre);
      ResultSet resDB=null;
      vecinas_usadas=0;
      try {
        resDB=getRSSQL(this.info,sSQL);
        if(resDB!=null){
          while(resDB.next()){
            vecinas_usadas++;
          }
          resDB.close();
        }
      } catch (OIExcepcion e) {
      } catch (SQLException e) {
      }
      return vecinas_usadas;
    }
    int getVecinasApoyadas() {
      String sSQL=
        " SELECT DISTINCT SUBSTRING(id_parcial,1,LENGTH(id_parcial)-10) AS vecino_sirviente " +
        " FROM envolturas " +
        " WHERE SUBSTRING(id_parcial,1,LENGTH(id_parcial)-10)= "+ tdutils.getQ(this.nombre) +
        " AND si_actual=true " +
        " AND SUBSTRING(id_tarea,1,LENGTH(id_tarea)-10)!="+tdutils.getQ(this.nombre);
      ResultSet resDB=null;
      vecinas_apoyadas=0;
      try {
        resDB=getRSSQL(this.info,sSQL);
        if(resDB!=null){
          while(resDB.next()){
            vecinas_apoyadas++;
          }
          resDB.close();
        }
      } catch (OIExcepcion e) {
      } catch (SQLException e) {
      }
      return vecinas_apoyadas;
    }    

    /**
     * Obtiene la cantidad de subtrabajos en marcha o la 
     * sumatoria de sus cargas.
     * @return La cantidad de subtrabajos en marcha 
     * o la sumatoria de sus cargas.
     */
    double getLocalActiva() {
      String sSQL=
        " SELECT SUM(su.carga) AS carga_activas " +
        " FROM envolturas AS en INNER JOIN sub_trabajos AS su " +
        " ON (en.id_tarea=su.id_tarea AND en.id_subtrabajo=su.id_subtrabajo) " +
        " WHERE en.si_actual=true";
      ResultSet resDB=null;
      local_activa=0;
      try {
        resDB=getRSSQL(this.info,sSQL);
        if(resDB!=null){
          if(resDB.next()){
            local_activa=resDB.getDouble("carga_activas");
          }
          resDB.close();
        }
      } catch (OIExcepcion e) {
      } catch (SQLException e) {
      }
      return local_activa;
    }

    /**
     * Obtiene la cantidad de subtrabajos en espera o 
     * la sumatoria de sus cargas.
     * @return La cantidad de subtrabajos en espera o la 
     * sumatoria de sus cargas.
     */
    double getLocalEspera() {
      String sSQL=
        " SELECT SUM(su.carga) AS carga_espera " +
        " FROM sub_trabajos AS su LEFT JOIN envolturas AS en " +
        " ON (su.id_tarea=en.id_tarea) " +
        " WHERE su.id_subtrabajo=en.id_subtrabajo " +
        " AND (en.si_actual=null OR en.si_actual=false)";
      ResultSet resDB=null;
      local_espera=0;
      try {
        resDB=getRSSQL(this.info,sSQL);
        if(resDB!=null){
          if(resDB.next()){
            local_espera=resDB.getDouble("carga_espera");
          }
          resDB.close();
        }
      } catch (OIExcepcion e) {
      } catch (SQLException e) {
      }
      return local_espera;
    }

    int getVecinosCant() {
      return _vecinos.size();
    }

    String getBusesTipo() {
      return buses_tipo;
    }

    int getMicroCant() {
      return micro_cant;
    }

    double getMicroReloj() {
      return micro_reloj;
    }

    double getMemCant() {
      return memoria_cant;
    }
    String getClaseBalance() {
      return this.clase_balance;
    }

    double getDiscoCant() {
      return disco_cant;
    }

    double getMicroLibre() {
      return micro_libre;
    }

    double getMemLibre() {
      return mem_libre;
    }

    double getDiscoLibre() {
      return disco_libre;
    }

    java.util.Map getVecinos() {
      return _vecinos;
    }

    java.util.Map getUbicaciones() {
      return _ubicaciones;
    }

    java.util.Map getProgramas() {
      return _programas;
    }

    void setNombre(String getNombre) {
      nombre = getNombre;
    }

    public void setDireccion(String getDireccion) {
      direccion = getDireccion;
    }

    void setCapacidad(double getCapacidad) {
      capacidad = getCapacidad;
    }

    void setCargaFuncional(double getCarga_funcional) {
      carga_funcional = getCarga_funcional;
    }

    void setCargaAplicacion(double getCarga_aplicacion) {
      carga_aplicacion = getCarga_aplicacion;
    }

    void setVecinasUsadas(int getVecinas_usadas) {
      vecinas_usadas = getVecinas_usadas;
    }

    void setLocalActiva(double getLocal_activa) {
      local_activa = getLocal_activa;
    }

    void setLocalEspera(double getLocal_espera) {
      local_espera = getLocal_espera;
    }

    void setVecinosCant(int getVecinos_cant) {
      vecinos_cant = getVecinos_cant;
    }
    public boolean siComputadoraVirtual(){
      return desplaza_puerto!=0;
    }
    void setDesplazaPuerto(int desplazapuerto){
      this.desplaza_puerto=desplazapuerto;
    }

    void setBusesTipo(String getBuses_tipo) {
      buses_tipo = getBuses_tipo;
    }

    void setMicroCant(int getMicro_cant) {
      micro_cant = getMicro_cant;
    }

    void setMicroReloj(double getMicro_reloj) {
      micro_reloj = getMicro_reloj;
    }

    void setMemCant(double setMemoriaCant) {
      memoria_cant = setMemoriaCant;
    }

    void setDiscoCant(double getDisco_cant) {
      disco_cant = getDisco_cant;
    }

    void setMicroLibre(double getMicro_libre) {
      micro_libre = getMicro_libre;
    }

    void setMemLibre(double getMem_libre) {
      mem_libre = getMem_libre;
    }

    void setDiscoLibre(double getDisco_libre) {
      disco_libre = getDisco_libre;
    }
    void setClaseBalance(String setClaseBalance) {
      this.clase_balance=setClaseBalance;
    }

    /**
     * Carga pesos y umbrales, primero de la configuración y luego de la
     * base de datos.
     * @param uriUmbrales
     * @param nodoPesos
     * @throws OIExcepcion
     */
    private void loadPesosUmbrales(String uriUmbrales, Node nodoPesos) throws OIExcepcion {
  		if(nodoPesos!=null){
  			Pesos_Umbrales_Politicas.openMapXML(nodoPesos,_pesos_umbrales);
  		}else{
  			Pesos_Umbrales_Politicas.openMapXML(uriUmbrales,_pesos_umbrales);
  		}
      Pesos_Umbrales_Politicas.openMap(this.info,_pesos_umbrales);        
  		Pesos_Umbrales_Politicas.writeMap(this.info,_pesos_umbrales);
    }

    /**
     * Carga programas.
     * @param uriProgramas
     * @param nodoProgramas
     * @throws OIExcepcion
     */
    private void loadProgramas(String uriProgramas, Node nodoProgramas) throws OIExcepcion {
    	Programas.openMap(this.info,_programas);
    	if(_programas.isEmpty()){
    		if(nodoProgramas!=null){
    			Programas.openMapXML(nodoProgramas,_programas);
    		}else{
    			Programas.openMapXML(uriProgramas,_programas);
    		}			
    		Programas.writeMap(this.info,_programas);
    	}
    }

    /**
     * Carga ubicaciones.
     * @param uriUbicaciones
     * @param nodoUbicaciones
     * @throws OIExcepcion
     */
    private void loadUbicaciones(String uriUbicaciones, Node nodoUbicaciones) throws OIExcepcion,ADMINGLOExcepcion {
      Ubicaciones ubica;
      Iterator itr;
    	Ubicaciones.openMap(this.info,_ubicaciones);
    	//|
    	//| Actualiza nombre
    	//|
    	itr=_ubicaciones.values().iterator();
    	while(itr.hasNext()){
    		ubica=(Ubicaciones)itr.next();
    		if(ubica.getSiLocal()){
    			this.nombre=ubica.nombre;
          this.desplaza_puerto=ubica.desplaza_puerto;
    			break;
    		}
    	}
      //|
      //| Carga dominios de balance de la compu local
      //|
    	itr=null;
    	if(_ubicaciones.size()>0){
    		//
    		// carga dominio de balance de la computadora local
    		//
    		Dominios_bal.openMap(this.info, _vecinos, this.nombre);
    		//
    		// si no hay información, la trae de archivos XML
    		//
    		if (_vecinos.isEmpty()) {
    			// problema, debería haber otras computadoras,
    			// se busca en archivo de configuración.
    			this.nombre=Ubicaciones.openMapXML(this.direccion,this.nombre,this._ubicaciones,
              this._vecinos, uriUbicaciones,null,((ADMINGLOInfo)this.info).alias_local,this);
    			Ubicaciones.writeMap(info, _ubicaciones);
          Dominios_bal.writeMap(info, _vecinos);
    			// throw new OIExcepcion("No hay información de nodos.");
    		}
    	}else{
    		if(nodoUbicaciones!=null){
          this.nombre=Ubicaciones.openMapXML(this.direccion,this.nombre,this._ubicaciones,
              this._vecinos,null,nodoUbicaciones,
              ((ADMINGLOInfo)this.info).alias_local,this);
    		}else{
          try{
            this.nombre=Ubicaciones.openMapXML(this.direccion,this.nombre,this._ubicaciones,
                this._vecinos,uriUbicaciones,null,
                ((ADMINGLOInfo)this.info).alias_local,this);
          }catch(Exception ex){
            if(this.nombre==null){
              throw new ADMINGLOExcepcion("Problema en el nombre del servidor.",ex);
            }else{
              throw new ADMINGLOExcepcion("No hay información de sobre el servidor.",ex);
            }
          }
    		}			
        Ubicaciones.writeMap(info, _ubicaciones);
        Dominios_bal.writeMap(info, _vecinos);
    		if (_ubicaciones.isEmpty() || _vecinos.isEmpty()) {
    			throw new ADMINGLOExcepcion("No hay información de sobre el servidor.");
    		}
    	}
      itr=_ubicaciones.values().iterator();
      while(itr.hasNext()){
        ubica=(Ubicaciones)itr.next();
          _direccionesVirtuales.put(ubica.nombre,
              new DireccionesVirtuales(
                  ubica.nombre,ubica.direccion,ubica.desplaza_puerto));              
        }
      }
    final void loadVecinos(String uriUbicaciones) throws OIExcepcion,ADMINGLOExcepcion {
      Ubicaciones ubica;
      Iterator itr;
      //|
      //| Carga dominios de balance de la compu local
      //|
      itr=null;
      if(_ubicaciones.size()>0){
        //
        // carga dominio de balance de la computadora local
        //
        Dominios_bal.openMap(this.info, _vecinos, this.nombre);
        //
        // si no hay información, la trae de archivos XML
        //
        if (_vecinos.isEmpty()) {
          // problema, debería haber otras computadoras,
          // se busca en archivo de configuración.
          Ubicaciones.openMapXML(this.direccion,this.nombre,this._ubicaciones,
              this._vecinos, uriUbicaciones,null,((ADMINGLOInfo)this.info).alias_local,this);
          Ubicaciones.writeMap(info, _ubicaciones);
          Dominios_bal.writeMap(info, _vecinos);
          // throw new OIExcepcion("No hay información de nodos.");
        }
      }
    }
    /**
     * Clona un objeto con información de la computadora.
     * <li>Las referencias del objeto original se mantienen, 
     * específicamente las de objetos contenedores de ubicaciones, 
     * dominio de balance y pesos.</li>
     * @return El clon de la computadora actual.
     */
    public Computadora clonese() {
      Computadora clon=new Computadora();
      clon.imita(this);      
      return clon;
    }
    /**
     * Retorna el puerto base de la computadora virtual.
     * @return el puerto base.
     */
    public int getDesplazaPuerto() {
      return desplaza_puerto;
    }
    /**
     * Pone en la computadora actual los valores de otra instancia.
     * <li>Las referencias de la instancia a imitar se mantienen, 
     * específicamente las de objetos contenedores de ubicaciones, 
     * dominio de balance y pesos.</li>
     * @param otra La computadora a copiar.
     */
    public void imita(Computadora otra){
      this.setBusesTipo(otra.getBusesTipo());
      this.setCapacidad(otra.getCapacidad());
      this.setCargaAplicacion(otra.getCargaAplicacion());
      this.setCargaFuncional(otra.getCargaFuncional());
      this.setClaseBalance(otra.getClaseBalance());
      this.setDescriptor(otra.getDescriptor());
      this.setDireccion(otra.getDireccion());
      this.setDesplazaPuerto(otra.getDesplazaPuerto());
      this.setDiscoCant(otra.getDiscoCant());
      this.setDiscoLibre(otra.getDiscoLibre());
      this.setLocalActiva(otra.getLocalActiva());
      this.setLocalEspera(otra.getLocalEspera());
      this.setMemCant(otra.getMemCant());
      this.setMemLibre(otra.getMemLibre());
      this.setMicroCant(otra.getMicroCant());
      this.setMicroLibre(otra.getMicroLibre());
      this.setMicroReloj(otra.getMicroReloj());
      this.setNombre(otra.getNombre());
      this.setVecinasUsadas(otra.getVecinasUsadas());
      this.setVecinosCant(otra.getVecinosCant());
      this._vecinos=otra._vecinos;
      this._pesos_umbrales=otra._pesos_umbrales;
      this._programas=otra._programas;
      this._ubicaciones=otra._ubicaciones;
      this._direccionesVirtuales=otra._direccionesVirtuales;
    }
    /**
     * Carga desde la base de datos.
     * @param uriUbicaciones Archivo de las ubicaciones de computadoras.
     * @param uriProgramas Archivo con información de programas.
     * @param uriUmbrales Archivo con información de pesos y umbrales.
     * @throws OIExcepcion Si hay error.
     */
    public void open(String uriUbicaciones,String uriProgramas,String uriUmbrales)
        throws OIExcepcion{
      super.loadComputadora();
      _open(uriUbicaciones,uriProgramas,uriUmbrales,null);
    }
    private void _open(String uriUbicaciones,String uriProgramas,
    String uriUmbrales,Node nodo)throws OIExcepcion{
    	Node nodoUbicaciones=nodo,nodoProgramas=nodo,nodoPesos=nodo;
    	//
    	// revisa la conexión
    	//
    	if(this.info.getConex()==null){
    		throw new OIExcepcion("No se ha cargado la conexión.");
    	}
    	loadUbicaciones(uriUbicaciones, nodoUbicaciones);
    	loadProgramas(uriProgramas, nodoProgramas);
    	loadPesosUmbrales(uriUmbrales, nodoPesos);
    }
    protected void addVecina(Ubicaciones ubica){
      Dominios_bal vecina=null;
      _escritor.escribeMensaje("Previo a bloqueo addVecina.entrada.");
      synchronized(this._ubicaciones){
        _escritor.escribeMensaje("Fin de bloqueo addVecina.entrada.");
        if(_ubicaciones.get(ubica.nombre)==null){
          // @TODO, incompleto, faltan puerto e IP
          _ubicaciones.put(ubica.nombre,ubica);
          try{
            Ubicaciones.writeMap(this.info,_ubicaciones);
            _escritor.escribeMensaje("Ubicación agregada: "+ubica.nombre+":"+ubica.direccion);
          }catch(OIExcepcion e){
          }
        }
        if(_vecinos.get(ubica.nombre)==null){
          vecina=new Dominios_bal((ADMINGLOInfo)this.info);
          vecina.nombre=this.nombre;
          vecina.vecino=ubica.nombre;
          this._vecinos.put(ubica.nombre,vecina);
          try {
            Dominios_bal.writeMap(this.info,_vecinos);
            _escritor.escribeMensaje("Vecino agregado: "+ubica.nombre+":"+ubica.direccion);
          } catch (OIExcepcion e) {
          }
        }
      }
      _escritor.escribeMensaje("Fin a bloqueo addVecina.salida.");
    }
  }

  static class Dominios_bal
      extends OIDominios_bal {
  
    double getAltura() {
      return altura;
    }
    
    synchronized long getUltimoMensaje() {
      return ultimo_mensaje;
    }
    
    String getNombre() {
      return nombre;
    }
  
    String getVecino() {
      return vecino;
    }
  
    double getPeso() {
      return peso;
    }
  
    double getCapacidad() {
      return capacidad;
    }
  
    double getCargaAplicacion() {
      return carga_aplicacion;
    }
    void setAltura(double setAltura) {
      altura = setAltura;
    }
    synchronized void setUltimoMensaje(long setHora) {
      ultimo_mensaje = setHora;
    }
    void setNombre(String setNombre) {
      nombre = setNombre;
    }
  
    void setVecino(String setVecino) {
      vecino = setVecino;
    }
  
    void setPeso(double setPeso) {
      peso = setPeso;
    }
  
    void setCapacidad(double setCapacidad) {
      capacidad = setCapacidad;
    }
  
    void setCargaAplicacion(double setCargaAplicacion) {
      carga_aplicacion = setCargaAplicacion;
    }
  
    Dominios_bal() {
    }
  
    Dominios_bal(ADMINGLOInfo info0db0) {
      super(info0db0);
    }
    Dominios_bal clonese(){
      Dominios_bal compu0=new Dominios_bal();
      compu0.peso=0.0;
      compu0.capacidad=this.capacidad;
      compu0.carga_aplicacion=this.carga_aplicacion;
      compu0.altura=this.altura;
      return compu0;
    }
  
    static void openMap(OIDescriptor info0,java.util.Map map,String nombre)throws OIExcepcion{
      ResultSet resDB;
      OIDominios_bal pareja;
      //
      // carga dominios_bal
      //
      resDB=getRSSQL(info0,"SELECT * from dominios_bal "+
                     " WHERE nombre="+tdutils.getQ(nombre)+
                     " OR vecino="+tdutils.getQ(nombre));
      try {
        if(resDB==null||!resDB.next()){
          // no hay computadoras en el dominio de la computadora cuyo nombre
          // fue dado
        }else{
          // sí se tienen computadoras
          do{
            pareja=new Dominios_bal();
            pareja.openRS(resDB);
            if(pareja.nombre.compareToIgnoreCase(nombre)!=0){
              // corrige orden de los nombres
              pareja.vecino=pareja.nombre;
              pareja.nombre=nombre;              
            }
            pareja.setDescriptor(info0);
            map.put(pareja.vecino,pareja);
          }while(resDB.next());
          resDB.close();
        }
      }catch (SQLException ex) {
        throw new OIExcepcion("No se tiene acceso a la base de datos.",ex);
      }
    }
  
    static void writeMap(OIDescriptor info0,java.util.Map map)throws OIExcepcion{
      Iterator itr=null;
      Dominios_bal vecino;
      int res=0;
      res = doUpdateSQL(info0, "delete from dominios_bal");
      itr = map.values().iterator();
      while (itr.hasNext()) {
        vecino = (Dominios_bal) itr.next();
        vecino.setDescriptor(info0);
        vecino.write();
      }
    }
  }

  static class Pesos_Umbrales_Politicas
      extends OIPesos_Umbrales_Politicas {
    
    /**
     * Indica la cantidad de lecturas
     * que el valor del objeto no debería ser
     * cambiado.
     */
    private int nVecesEspera;
    /**
     * Indica la cantidad de lecturas que se han hecho sobre
     * el objeto luego de la última asignación de veces por esperar.
     */
    private int nVecesEsperadas;
    /**
     * Somete la modificación de los valores del objeto por parte del lector 
     * a la cantidad de esperas que se indica.
     * @param setVecesEspera La cantidad de esperas.
     * @return Si la asignación de la espera se pudo realizar.
     * <li>Retorna falso si faltan esperas para realizar el cambio.</li>
     * <li>Para forzar su asignación se puede utilizar 
     * <tt>esperaReinicia</tt>.</li>
     */
    boolean esperaSetVeces(int setVecesEspera){
      boolean bOK=false;
      if(nVecesEspera<0){
        bOK=true;
      }
      if(esperaSiModificable()){
        nVecesEsperadas=0;
        nVecesEspera=setVecesEspera;
        bOK=true;
      }
      return bOK;
    }
    /**
     * Incrementa en uno la cantidad de esperas realizadas.
     */
    void esperaIncrementa(){
      if(nVecesEspera>0){
        nVecesEsperadas++;
        if(nVecesEsperadas>=nVecesEspera){
          esperaReinicia();
        }
      }
    }
    /**
     * Indica si el valor del objeto es modificable por el lector.
     * @return Si el valor se pude modificar.
     */
    boolean esperaSiModificable(){
      return (nVecesEspera==0)||
                    (nVecesEspera>0 && nVecesEsperadas>=nVecesEspera);
    }
    /**
     * Libera la modificación del objeto de sus esperas.
     */
    void esperaReinicia(){
      nVecesEsperadas=0;
      nVecesEspera=0;
    }
    String getIdPesoUmbral() {
      return id_pesoumbral;
    }
  
    double getValor() {
      return valor;
    }
  
    void setIdPesoumbral(String getIdPesoumbral) {
      id_pesoumbral = getIdPesoumbral;
    }
  
    void setValor(double getValor) {
      valor = getValor;
    }
    static void openMap(OIDescriptor info0,java.util.Map map)throws OIExcepcion{
      ResultSet resDB;
      Pesos_Umbrales_Politicas umbral;   
      //
      // carga ubicaciones
      //
      resDB=getRSSQL(info0,"SELECT * from Pesos_Umbrales_Politicas");
      try {
        if(resDB==null||!resDB.next()){
          // no hay computadoras ubicadas
        }else{
          // sí se tienen computadoras
          do{
            umbral=new Pesos_Umbrales_Politicas();
            umbral.openRS(resDB);
            map.put(umbral.id_pesoumbral,umbral);
          }while(resDB.next());
          resDB.close();
        }
      }catch (SQLException ex) {
        throw new OIExcepcion("No se tiene acceso a la base de datos.",ex);
      }
    }
    private static void _openMapXML(String URI,Node nodo,java.util.Map map)
    throws OIExcepcion{
      LEEPesosUmbrales lista=new LEEPesosUmbrales();
      LEEPesoUmbral umbralI;
      java.util.Iterator itr;
      try {
        if(nodo!=null){
          lista.setFromXMLNode(nodo);
        }else{
          lista.setFromXMLURI(URI);
        }     
      }
      catch (MENSException ex) {
        throw new OIExcepcion("Error al abrir archivo de umbrales y pesos.",ex);
      }
      itr=lista.pesos_umbrales.values().iterator();
      while(itr.hasNext()){
        umbralI=(LEEPesoUmbral)itr.next();
        map.put(umbralI.peso_umbral.id_pesoumbral,umbralI.peso_umbral);
      }   
    }    
  
    Pesos_Umbrales_Politicas(ADMINGLOInfo info0db0) {
      super(info0db0);
    }
    Pesos_Umbrales_Politicas(){
      super();
    }
    /**
     * Pone los nombres de los pesos y sus correspondientes valores
     * en el mapa dado.
     * @param mpPesosUmbrales Mapa que contendrá los pesos y umbrales 
     * leídos de la base de datos (objetos de tipo Pesos_Umbrales_Politicas).
     * @throws ADMINGLOExcepcion Si ocurre un error al abrir los datos.
     */
    static void loadPesosUmbrales(ADMINGLOInfo desc,Map mpPesosUmbrales) throws ADMINGLOExcepcion{      
      String sql="SELECT * FROM pesos_umbrales_globales ";
      try{
        ResultSet rs=getRSSQL(desc,sql);
        Pesos_Umbrales_Politicas pesoI=null;
        mpPesosUmbrales.clear();
        while (rs.next()) {
          pesoI=new Pesos_Umbrales_Politicas();
          pesoI.openPesoUmbral(rs);
          mpPesosUmbrales.put(pesoI.getIdPesoUmbral(),pesoI);
        }
        rs.close();
      }
        catch(SQLException ex){
        throw new ADMINGLOExcepcion(ex.getCause());
      }
        catch(ADMINGLOExcepcion ex){
        throw new ADMINGLOExcepcion(ex.getCause());
      }
        catch(OIExcepcion ex){
        throw new ADMINGLOExcepcion(ex.getCause());
      }
    }
  
    /**
     * @param rs
     * @throws ADMINGLOExcepcion
     */
    protected void openPesoUmbral(ResultSet rs) throws ADMINGLOExcepcion {
      try {
        this.openRS(rs);
      }catch (SQLException ex) {
        throw new ADMINGLOExcepcion("Error leyendo peso_umbral.",ex);
      }      
    }
    static void openMapXML(Node nodo,java.util.Map map)throws OIExcepcion{
    	_openMapXML(null,nodo,map);
    }
    static void openMapXML(String URI,java.util.Map map)throws OIExcepcion{
    	_openMapXML(URI,null,map);
    }
    static void writeMap(OIDescriptor info0, java.util.Map map)throws OIExcepcion{
    	java.util.Iterator itr=null;
    	Pesos_Umbrales_Politicas umbral=null;
    	// int res=0;
    	// res=doUpdateSQL(info0,"delete from Pesos_Umbrales_Politicas");
    	itr=map.values().iterator();
    	while(itr.hasNext()){
    		umbral=(Pesos_Umbrales_Politicas)itr.next();
    		umbral.setDescriptor(info0);
    		umbral.write();
    	}
    }
    protected final static class LEEPesoUmbral extends mens.MENSMensaje{
      public Pesos_Umbrales_Politicas peso_umbral;
      private boolean _isset_nombre;
      private boolean _isset_valor;
      LEEPesoUmbral() {
        peso_umbral=new Pesos_Umbrales_Politicas();
        peso_umbral.id_pesoumbral="";
        peso_umbral.setValor(0.0);
        _isset_nombre=false;
        _isset_valor=false;
      }
      void setPesoUmbralID(String nodoid0){
        if (nodoid0==""){
          return;
        }
        peso_umbral.id_pesoumbral=nodoid0;
        _isset_nombre=true;
      }
      void setValor(double valor){
        peso_umbral.valor=valor;
        _isset_valor=true;
      }
      public boolean isVacio() {
        return (peso_umbral.id_pesoumbral==null) || (peso_umbral.id_pesoumbral=="");
      }
      protected String getXMLContainedElements() {
        String xml="";
        xml+="<umbral>";
        if(_isset_nombre){
          xml+="\n  <nombre>"+peso_umbral.id_pesoumbral+"</nombre>";
        }
        if(_isset_valor){
          xml+="\n  <valor>"+peso_umbral.valor+"</valor>";
        }
        xml+="\n</umbral>";
        return xml;
      }
      protected void setContentFromDoc(Node parm1, int[] parm2, String[] parm3) {
        // parm1=this.getDocumento();
        setPesoUmbralID(getElementText(parm1,"nombre"));
        try {
          setValor(Double.parseDouble(getElementText(parm1,"valor")));
        } catch (NumberFormatException e) {
          setValor(0.0);
        }
      }
      protected void toleraXML(int[] parm1, String[] parm2) {
        if(_isset_nombre && _isset_valor){
          parm1[0]=0;
          parm2[0]="Bien en lectura de umbrales y pesos.";
        }else{
          parm1[0]=3;
          parm2[0]="Error en lectura de umbrales y pesos.";
        }
      }
      String getString(){
        String res="";
        res+="<politicas_globales>\n";
        res+=getXMLContainedElements();
        res+="\n</politicas_globales>";
        return res;
      }
    }
    protected final static class LEEPesosUmbrales  extends MENSMensaje {
      public TreeMap pesos_umbrales;    
      public LEEPesosUmbrales() {
        pesos_umbrales=new TreeMap();     
      }
      public synchronized boolean isVacio() {
        return pesos_umbrales.size()==0;
      }
      protected synchronized String getXMLContainedElements() {
        String res="";
        LEEPesoUmbral pesoumbI;
        Iterator itr=null;
        int i;
        res ="<politicas_globales>\n";
        itr=pesos_umbrales.values().iterator();
        while (itr.hasNext()) {
          pesoumbI = (LEEPesoUmbral) itr.next();
          res += pesoumbI.getXMLElem(null);       
        }
        res+="\n</politicas_globales>";
        return res;
      }
      protected synchronized void setContentFromDoc(Node nodo, int[] nError, 
      String[] mError) {
        LEEPesoUmbral pesoumbI;     
        Node nodobak;
        nodobak=nodo;
        // mete umbrales y pesos
        nodo=nodo.getOwnerDocument();
        if(nodo.getNodeName().compareToIgnoreCase("politicas")!=0){
          nodo = MENSMensaje.getNextElement(nodo, "politicas");
          // MENSMensaje.g
        }
        if(nodo!=null){
          nodo = MENSMensaje.getNextElement(nodo, "umbral");
          while((nodo!=null)&&(nodo.getNodeName().compareToIgnoreCase("umbral")==0)){
            // debe haber info sobre nodos
            pesoumbI = new LEEPesoUmbral();
            try {
              pesoumbI.setFromXMLNode(nodo);
              pesos_umbrales.put(pesoumbI.peso_umbral.id_pesoumbral,pesoumbI);
              pesoumbI = null;
              nodo = MENSMensaje.getNextSiblingElement(nodo, "umbral");
            }
            catch (MENSException ex) {
              pesoumbI=null;
              break;
            }
          }
        }
        // lee enlaces
        nodo=nodobak;
      }
      protected void toleraXML(int[] nError, String[] mError) {
        /**@todo Implement this mens.MENSMensaje abstract method*/
      }
      /**
       * Indica la cantidad de nodos registrados.
       * @return Cantidad de nodos registrados.
       */
      synchronized int getCount(){
        return pesos_umbrales.size();
      }
      /**
       * Agrega un nuevo nodo a la lista de ubicaciones. No revisa si un nodo con
       * la misma identificación ya había sido agregado.
       * @param pesoumb El peso o umbral a agregar a la lista de ubicaciones y 
       * pesos.
       * @throws OIExcepcion Si hay error en el agregado.
       */
      synchronized void addNew(LEEPesoUmbral pesoumb) throws OIExcepcion{
        pesos_umbrales.put(pesoumb.peso_umbral.id_pesoumbral,pesoumb);
      }
      /**
       * Borra el umbral o peso de la identifiación dada.
       * @param umbralid Identificación del umbral a borrar.
       * @throws OIExcepcion Si hay problema en el borrado.
       */
      synchronized void delete(String umbralid) throws OIExcepcion{
        if(pesos_umbrales.size()==0){
          throw new OIExcepcion("Error al borrar el peso o umbral '"+umbralid+"'.");
        }
        _operateById(umbralid,null,true);
      }
      /**
       * Devuelve el peso o umbral de la identificación dada.
       * @param umbralid Identificación del umbral o peso a devolver.
       * @return El umbral encontrado o null.
       * @throws OIExcepcion Si hay problema al hallar el umbral.
       */
      synchronized LEEPesoUmbral findById(String umbralid)throws OIExcepcion{
        return _operateById(umbralid,null,false);
      }
      private LEEPesoUmbral _operateById(String nodoid,LEEPesoUmbral pesoumb,boolean delete)throws OIExcepcion{
        int index=-1;
        LEEPesoUmbral res=null;
        if(pesos_umbrales.size()==0){
          throw new OIExcepcion("No hay nodos.");
        }
        if(delete){
          pesos_umbrales.remove(nodoid);
          return null;
        }
        if(pesoumb!=null){
          pesos_umbrales.put(pesoumb.peso_umbral.id_pesoumbral,pesoumb);
          res = pesoumb;
        }
        return res;
      }
      /**
       * Actualiza o agrega un nuevo nodo a la lista dependiendo de si el nodo
       * existía o no.
       * @param umbral El umbral a agregar a la lista.<br>
       * - Primero determina si el nodo existe en la lista, y si ésto ocurre lo
       * reemplaza por el nodo dado. Si el nodo no existe, entonces agrega el nodo
       * a la lista.
       * @throws OIExcepcion Si hay error en el agregado o en la actualización.
       */
      synchronized void addNewUpdate(LEEPesoUmbral umbral)throws OIExcepcion{
        LEEPesoUmbral res;
        if(pesos_umbrales.size()==0){
          throw new OIExcepcion("No hay umbrales ni pesos.");
        }
        pesos_umbrales.put(umbral.peso_umbral.id_pesoumbral,umbral);
      }
      TreeMap getpesos_umbrales(){
        return pesos_umbrales;
      }
    }
  }

  static class Ubicaciones
      extends OIUbicaciones {
    boolean getSiLocal(){
      return this.si_local; 
    }
    
    static class LEEUbicaNodo extends mens.MENSMensaje{
      //////////////////////////////////////////////////////////////////////
      public OIUbicaciones ubicacion;
      /**
       * Conjunto de los aristas que involucran este nodo. Sus componentes
       * son de la clase DIREnlace.
       */
      private LEEEnlaces vecinos;
      //////////////////////////////////////////////////////////////////////
      private boolean _isset_nombre;
      private boolean _isset_direccion;
      private boolean _isset_desplaza_puerto;
      //////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////
      LEEUbicaNodo() {
        ubicacion=new Ubicaciones();
        ubicacion.nombre="";
        ubicacion.direccion="";
        ubicacion.desplaza_puerto=0;
        vecinos=new LEEEnlaces();
        _isset_nombre=false;
        _isset_direccion=false;
        _isset_desplaza_puerto=false;
      }
      LEEEnlaces getEnlaces(){
        return vecinos;
      }
      void setNodoId(String nodoid0){
        if (nodoid0==""){
          return;
        }
        ubicacion.nombre=nodoid0;
        _isset_nombre=true;
      }
      void setDireccion(String direccion0){
        if (direccion0==""){
          return;
        }
        ubicacion.direccion=direccion0;
        _isset_direccion=true;
      }
      void setPuertoBase(int desplaza_puerto0){
        ubicacion.desplaza_puerto=desplaza_puerto0;
        _isset_desplaza_puerto=true;
      }
      public boolean isVacio() {
        return (ubicacion.nombre==null) || (ubicacion.nombre=="");
      }
      protected String getXMLContainedElements() {
        String xml="";
        xml+="<ubicanodo>";
        if(_isset_nombre){
          xml+="\n  <nombre>"+ubicacion.nombre+"</nombre>";
        }
        if(_isset_direccion){
          xml+="\n  <direccion>"+ubicacion.direccion+"</direccion>";
        }
        if(_isset_desplaza_puerto){
          xml+="\n  <desplaza_puerto>"+ubicacion.desplaza_puerto+"</desplaza_puerto>";
        }
        xml+="\n</ubicanodo>";
        return xml;
      }
      protected void setContentFromDoc(Node parm1, int[] parm2, String[] parm3) {
        // parm1=this.getDocumento();
        setNodoId(getElementText(parm1,"nombre"));
        setDireccion(getElementText(parm1,"direccion"));
        try{
          setPuertoBase(Integer.parseInt(getElementText(parm1,"desplaza_puerto")));
        }catch(Exception exc){
          ubicacion.desplaza_puerto=0;
        }
      }
      protected void toleraXML(int[] parm1, String[] parm2) {
        if(_isset_nombre && _isset_direccion){
          parm1[0]=0;
          parm2[0]="Bien en lectura de ubicación de nodo.";
        }else{
          parm1[0]=3;
          parm2[0]="Error en lectura de ubicación de nodo.";
        }
      }
      String getString(){
        String res="";
        res+="<nodo>\n";
        res+=getXMLContainedElements();
        res+="\n";
        res+=this.vecinos.getString();
        res+="\n</nodo>";
        return res;
      }
      String getConVecinosString(){
        String res="";
        res+="<nodo>\n";
        res+=getXMLContainedElements();
        res+="\n";
        res+=this.vecinos.getVecinosString();
        res+="\n</nodo>";
        return res;
      }
      //////////////////////////////////////////////////////////////////////
    }
    static class LEEUbicaciones  extends MENSMensaje {
      //////////////////////////////////////////////////////////////////////
      private TreeMap direcciones;
      private LEEEnlaces enlaces;
      //////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////
      public LEEUbicaciones() {
        direcciones=new TreeMap();
        enlaces=new LEEEnlaces();
      }
      public synchronized boolean isVacio() {
        return direcciones.size()==0;
      }
      protected synchronized String getXMLContainedElements() {
        String res="";
        LEEUbicaNodo nodoI;
        LEEEnlace enlaceI;
        Iterator itr=null;
        // se limpian los enlaces iniciales porque van a ser considerados
        // los enlaces con que quedaron los nodos.
        enlaces.clean();
        int i;
        res ="<ubicaciones>\n";
        // se toman en cuenta los enlaces de cada nodo
        itr=direcciones.values().iterator();
        while (itr.hasNext()) {
          nodoI = (LEEUbicaNodo) itr.next();
          res += nodoI.getXMLElem(null);
          // aquí es donde se recuperan los enlaces
          if(nodoI.getEnlaces().getCount()>0){
            try {
              nodoI.getEnlaces().moveFirst();
              while((enlaceI=nodoI.getEnlaces().getVecinoMoveNext())!=null){
                enlaces.addNewUpdateVecino(nodoI.ubicacion.nombre,enlaceI);
              }
              nodoI.getEnlaces().moveFirst();
            }
            catch (OIExcepcion ex) {
            }
          }
        }
        res+="\n</ubicaciones>";
        // pone el grupo de enlaces
        res+="\n<enlaces>";
        try {
          if (enlaces.getCount() > 0) {
            enlaces.moveFirst();
          }
          while ( (enlaceI = enlaces.getVecinoMoveNext()) != null) {
            res+=enlaceI.getXMLElem(null);
          }
          if (enlaces.getCount() > 0) {
            enlaces.moveFirst();
          }
        }
        catch (OIExcepcion ex1) {
        }
        res+="\n</enlaces>";
        return res;
      }
      protected synchronized void setContentFromDoc(Node nodo, int[] nError, String[] mError) {
        LEEUbicaNodo nodoI;
        LEEEnlace enlaceI;
        Node nodobak;
        nodobak=nodo;
        // mete ubicaciones de nodos
    		nodo=nodo.getOwnerDocument();
        if(nodo.getNodeName().compareToIgnoreCase("ubicaciones")!=0){
          nodo = MENSMensaje.getNextElement(nodo, "ubicaciones");
        }
        if(nodo!=null){
    			nodo = MENSMensaje.getNextElement(nodo, "ubicanodo");
          while((nodo!=null)&&(nodo.getNodeName().compareToIgnoreCase("ubicanodo")==0)){
            // debe haber info sobre nodos
            nodoI = new LEEUbicaNodo();
            try {
              nodoI.setFromXMLNode(nodo);
              direcciones.put(nodoI.ubicacion.nombre,nodoI);
              nodoI = null;
              nodo = MENSMensaje.getNextSiblingElement(nodo, "ubicanodo");
            }
            catch (MENSException ex) {
              nodoI=null;
              break;
            }
          }
        }
        // lee enlaces
        nodo=nodobak;
    		nodo=nodo.getOwnerDocument();
    		nodo=this.getDocumento();
        if(nodo.getNodeName().compareToIgnoreCase("enlaces")!=0){
          nodo = MENSMensaje.getNextElement(nodo, "enlaces");        
        }
        if(nodo!=null){
    			nodo = MENSMensaje.getNextElement(nodo, "enlace");
          while ( (nodo != null) &&
                 (nodo.getNodeName().compareToIgnoreCase("enlace") == 0)) {
            // debe haber info sobre enlaces de nodos
            enlaceI = new LEEEnlace();
            try {
              enlaceI.setFromXMLNode(nodo);
              System.out.println(enlaceI.getString());
              enlaces.addNewUpdateVecino(null, enlaceI);
              enlaceI = null;
              nodo = MENSMensaje.getNextSiblingElement(nodo, "enlace");
            }
            catch (MENSException ex) {
              nodoI = null;
              break;
            }
            catch (OIExcepcion ex) {
              nodoI = null;
              break;
            }
          }
        }
      }
      public TreeMap getEnlaces(){
        return enlaces.getVecinos();
      }
      protected void toleraXML(int[] nError, String[] mError) {
        /**@todo Implement this mens.MENSMensaje abstract method*/
      }
      //////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////
      /**
       * Indica la cantidad de nodos registrados.
       * @return Cantidad de nodos registrados.
       */
      synchronized int getCount(){
        return direcciones.size();
      }
      /**
       * Agrega un nuevo nodo a la lista de ubicaciones. No revisa si un nodo con
       * la misma identificación ya había sido agregado.
       * @param nodo El nodo a agregar a la lista de ubicaciones.
       * @throws OIExcepcion Si hay error en el agregado.
       */
      synchronized void addNew(LEEUbicaNodo nodo) throws OIExcepcion{
        direcciones.put(nodo.ubicacion.nombre,nodo);
      }
      /**
       * Borra el nodo de la identifiación dada.
       * @param nodoid Identificación del nodo a borrar.
       * @throws OIExcepcion Si hay problema en el borrado.
       */
      synchronized void delete(String nodoid) throws OIExcepcion{
        if(direcciones.size()==0){
          throw new OIExcepcion("Error al borrar el nodo "+nodoid+".");
        }
        _operateById(nodoid,null,true);
      }
      /**
       * Devuelve el nodo de la identificación dada.
       * @param nodoid Identificación del nodo a devolver.
       * @return El nodo encontrado o null.
       * @throws OIExcepcion Si hay problema al hallar el nodo.
       */
      synchronized LEEUbicaNodo findById(String nodoid)throws OIExcepcion{
        return _operateById(nodoid,null,false);
      }
      private LEEUbicaNodo _operateById(String nodoid,LEEUbicaNodo nodo,boolean delete)throws OIExcepcion{
        int index=-1;
        LEEUbicaNodo res=null;
        if(direcciones.size()==0){
          throw new OIExcepcion("No hay nodos.");
        }
        if(delete){
          direcciones.remove(nodoid);
          return null;
        }
        if(nodo!=null){
          direcciones.put(nodo.ubicacion.nombre,nodo);
          res = nodo;
        }
        return res;
      }
      /**
       * Actualiza o agrega un nuevo nodo a la lista dependiendo de si el nodo
       * existía o no.
       * @param nodo El nodo a agregar a la lista.<br>
       * - Primero determina si el nodo existe en la lista, y si ésto ocurre lo
       * reemplaza por el nodo dado. Si el nodo no existe, entonces agrega el nodo
       * a la lista.
       * @throws OIExcepcion Si hay error en el agregado o en la actualización.
       */
      synchronized void addNewUpdate(LEEUbicaNodo nodo)throws OIExcepcion{
        LEEUbicaNodo res;
        if(direcciones.size()==0){
          throw new OIExcepcion("No hay nodos.");
        }
        direcciones.put(nodo.ubicacion.nombre,nodo);
      }
      public TreeMap getDirecciones(){
        return direcciones;
      }
      //////////////////////////////////////////////////////////////////////
    }
    static class LEEEnlaces {
      //////////////////////////////////////////////////////////////////////
      private TreeMap vecinos;
      //////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////
      LEEEnlaces() {
        // vecinos=new AbstractSet(200,new LEEEnlace(),false,true,new String[]{"tespera"});
        vecinos=new TreeMap();
      }
      /**
       * Agrega o actualiza un enlace al nodo con otro nodo.
       * @param enlace Enlace a agregar o a actualizar. Si es diferente a null
       * se crea una nueva instancia del enlace porque el enlace va a agregarse
       * a un nodo.
       * @param nodoid0 Nodo del enlace.
       * @throws OIExcepcion Si hubo error al agregar al conjunto de
       * enlaces.
       */
      void addNewUpdateVecino(String nodoid0,LEEEnlace enlace) throws OIExcepcion{
        LEEEnlace res;
        LEEEnlace nuevo;
        nuevo=enlace;
        if(nodoid0!=null){
          nuevo=enlace.clona();
          nuevo.setEsteNodo(nodoid0);
        }
        res=_operateById(nuevo.getEnlaceId(),nuevo,false);
        if(res==null){
          // no se pudo
          throw new OIExcepcion("No se pudo ni actualizar ni agregar el enlace "+
                                 enlace.getEnlaceId()+ ".");
        }
      }
      void delete(String enlaceid) throws OIExcepcion{
        if(vecinos.size()==0){
          throw new OIExcepcion("Error al borrar el enlace "+enlaceid+".");
        }
        _operateById(enlaceid,null,true);
      }
      private LEEEnlace _operateById(String enlaceid,LEEEnlace enlace,boolean delete)throws OIExcepcion{
        LEEEnlace res=null;
        if((delete||(enlace==null)) && vecinos.size()==0){
          throw new OIExcepcion("No hay enlaces.");
        }
        if (vecinos.size() > 0) {
          if(delete){
            // borrado
            vecinos.remove(enlaceid);
            return null;
          }
          if(enlace!=null){
            // actualización
            vecinos.put(enlace.nodo1id+":"+enlace.nodo2id,enlace);
            res=enlace;
          }
        }else{
          if(enlace!=null){
            res=enlace;
            vecinos.put(enlace.nodo1id+":"+enlace.nodo2id,enlace);
          }
        }
        return res;
      }
      final void moveFirst() throws OIExcepcion{
      }
      final LEEEnlace getVecinoMoveNext() throws OIExcepcion{
        LEEEnlace res=null;
        return res;
      }
      final int getCount(){
        return vecinos.size();
      }
      final void clean(){
        vecinos.clear();
      }
      final String getString(){
        LEEEnlace enlaceI;
        Iterator itr=null;
        String res="";
        res+="<enlaces>\n";
        if(vecinos.size()>0){
          itr=vecinos.values().iterator();
          while(itr.hasNext()){
            enlaceI=(LEEEnlace)itr.next();
            res+=enlaceI.getXMLElem(null);
          }
        }
        res+="\n</enlaces>";
        return res;
      }
      final String getVecinosString(){
        LEEEnlace enlaceI;
        Iterator itr;
        String res="";
        res+="<vecinos>\n";
        if(vecinos.size()>0){
          itr=vecinos.values().iterator();
          while(itr.hasNext()){
            enlaceI=(LEEEnlace)itr.next();
            if(enlaceI.getOtroNodo()!=""){
              res += enlaceI.getOtroNodo();
            }
            if(itr.hasNext()){
              res += "\n";
            }
          }
        }
        res+="\n</vecinos>";
        return res;
      }
      final int getMaxTEspera(){
        int res=0;
        if(vecinos.size()>0){
          // vecinos.initIteration("tespera", false);
          // res=((LEEEnlace)vecinos.nextIteration("tespera")).getTEspera();
        }
        return res;
      }
      TreeMap getVecinos(){
        return vecinos;
      }
      //////////////////////////////////////////////////////////////////////
    }
    static class LEEEnlace extends mens.MENSMensaje{
      //////////////////////////////////////////////////////////////////////
      /**
       * Identificación del primer nodo.
       */
      public String nodo1id;
      /**
       * Identificación del segundo nodo.
       */
      public String nodo2id;
      /**
       * Datos sobre el nodo.
       */
      public double peso;
      /**
       * Indica cuál nodo es el que ve el enlace.
       */
      private String estenodoid;
      /**
       * Indica cuál es el otro nodo.
       */
      private String otronodoid;
      /**
       * Indica el tiempo de espera de mensajes entre un nodo y otro.
       */
      int tespera;
      private boolean _isset_nodo1id;
      private boolean _isset_nodo2id;
      private boolean _isset_datos;
      private boolean _isset_tespera;
      //////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////
      //////////////////////////////////////////////////////////////////////
      LEEEnlace() {
        estenodoid="";
        otronodoid="";
        nodo1id="";
        nodo2id="";
        peso=0.0;
        tespera=-1;
        _isset_nodo1id=false;
        _isset_nodo2id=false;
        _isset_datos=false;
        _isset_tespera=false;
      }
      LEEEnlace(String nodo1id0,String nodo2id0,double peso0,int tespera0) {
        estenodoid="";
        otronodoid="";
        setNodo1Id(nodo1id0);
        setNodo2Id(nodo2id0);
        setPeso(peso0);
        setTEspera(tespera0);
      }
      LEEEnlace(String nodo1id0,String nodo2id0,double peso0) {
        estenodoid="";
        otronodoid="";
        setNodo1Id(nodo1id0);
        setNodo2Id(nodo2id0);
        setPeso(peso0);
      }
      /**
       * Asigna la identificación del primer nodo. Si el segundo nodo
       * está asignado, forza a que el nodo de menor valor quede
       * como primer nodo.
       * @param nodo1id0 Identificación del primer nodo.
       */
      final void setNodo1Id(String nodo1id0){
        if(nodo1id0==""){
          return;
        }
        nodo1id=nodo1id0;
        _isset_nodo1id=true;
        if(_isset_nodo2id){
          if(nodo1id.compareToIgnoreCase(nodo2id)>0){
            nodo1id=nodo2id;
            nodo2id=nodo1id0;
          }
        }
      }
      /**
       * Asigna la identificación del segundo nodo. Si el primer nodo
       * está asignado, forza a que el nodo de mayor valor quede
       * como segundo nodo.
       * @param nodo2id0 Identificación del primer nodo.
       */
      final void setNodo2Id(String nodo2id0){
        if(nodo2id0==""){
          return;
        }
        nodo2id=nodo2id0;
        _isset_nodo2id=true;
        if(_isset_nodo1id){
          if(nodo1id.compareToIgnoreCase(nodo2id)>0){
            nodo2id=nodo1id;
            nodo1id=nodo2id0;
          }
        }
      }
      /**
       * Asigna los datos del enlace.
       * @param peso0 Peso del enlace.
       */
      final void setPeso(double peso0){
        if(peso0==0.0){
          return;
        }
        peso=peso0;
        _isset_datos=true;
      }
      final String  getNodo1Id(){
        return nodo1id;
      }
      final String  getNodo2Id(){
        return nodo2id;
      }
      final double  getPeso(){
        return peso;
      }
      /**
       * Devuelve la identificación del enlace, formada
       * por la identificación del primer nodo, seguida por
       * dos puntos y luego seguida por la identificación
       * del segundo nodo.
       * @return Identificación del enlace.
       */
      final String getEnlaceId(){
        if(_isset_nodo1id &&_isset_nodo2id){
          return nodo1id + ":" + nodo2id;
        }else{
          return "";
        }
      }
      String getString(){
        return "<enlace>"+getEnlaceId()+":"+getPeso()+":"+this.getTEspera()+"</enlace>";
      }
      int getTEspera(){
        return tespera;
      }
      void setTEspera(int tespera0){
        if(tespera0>=0){
          tespera = tespera0;
          _isset_tespera=true;
        }
      }
      //////////////////////////////////////////////////////////////////////
      public boolean isVacio() {
        return (!_isset_nodo1id) || (!_isset_nodo2id);
      }
      protected String getXMLContainedElements() {
        String xml="";
        xml+="<enlace>";
        if(_isset_nodo1id){
          xml+="\n  <nodo1id>"+nodo1id+"</nodo1id>";
        }
        if(_isset_nodo2id){
          xml+="\n  <nodo2id>"+nodo2id+"</nodo2id>";
        }
        if(_isset_tespera){
          xml+="\n  <espera>"+tespera+"</espera>";
        }
        if(_isset_datos){
          xml+="\n  <peso>"+peso+"</peso>";
        }
        xml+="\n</enlace>";
        return xml;
      }
      protected void setContentFromDoc(Node parm1, int[] parm2, String[] parm3) {
        // parm1=this.getDocumento();
        setNodo1Id(getElementText(parm1,"nodo1id"));
        setNodo2Id(getElementText(parm1,"nodo2id"));
        try{
          setPeso(Double.parseDouble(getElementText(parm1, "peso")));
        }catch(NumberFormatException ex){
          setPeso(0.0);
        }
        try {
          setTEspera(Integer.parseInt(getElementText(parm1, "espera")));
        }
        catch (NumberFormatException ex) {
          setTEspera(0);
        }
      }
      protected void toleraXML(int[] parm1, String[] parm2) {
        if(_isset_nodo1id && _isset_nodo2id){
          parm1[0]=0;
          parm2[0]="Bien en lectura de enlace.";
        }else{
          parm1[0]=3;
          parm2[0]="Error en lectura de enlace.";
        }
      }
      public final void setEsteNodo(String estenodo0) throws OIExcepcion{
        if(_isset_nodo1id && _isset_nodo2id){
          if(estenodo0.compareToIgnoreCase(nodo1id)==0){
            estenodoid=nodo1id;
            otronodoid=nodo2id;
          }else{
            if(estenodo0.compareToIgnoreCase(nodo2id)==0){
              estenodoid=nodo2id;
              otronodoid=nodo1id;
            }else{
              throw new OIExcepcion(
                  "Error en la asignación del nodo \"dueño\" del enlace.");
            }
          }
        }else{
          throw new OIExcepcion("No han sido asignados nodos al enlace.");
        }
      }
      public final String getEsteNodo(){
        return estenodoid;
      }
      public final String getOtroNodo(){
        return otronodoid;
      }
      public LEEEnlace clona(){
        return new LEEEnlace(nodo1id,nodo2id,peso,tespera);
      }
      //////////////////////////////////////////////////////////////////////
    }
    Ubicaciones() {
    }
    static void openMap(OIDescriptor info0,java.util.Map map)throws OIExcepcion{
      ResultSet resDB;
      Ubicaciones ubica;
      Dominios_bal vecino;
      //
      // carga ubicaciones
      //
      resDB=getRSSQL(info0,"SELECT * from ubicaciones");
      try {
        if(resDB==null||!resDB.next()){
          // no hay computadoras ubicadas
        }else{
          // sí se tienen computadoras
          do{
            ubica=new Ubicaciones();
            ubica.openRS(resDB);
            ubica.setDescriptor(info0);
            map.put(ubica.nombre,ubica);
          }while(resDB.next());
          resDB.close();
        }
      }catch (SQLException ex) {
        throw new OIExcepcion("No se tiene acceso a la base de datos.",ex);
      }
    }
    /**
     * Toma información de los nodos a partir de un archivo de XML.
     * Este siempre se ejecuta cuando no existen datos en la base de datos.
     * <li>Ha sido considerado el caso cuando se trabaja con varias
     * computadoras virtuales sobre una misma computadora.</li>
     * @param direccion Dirección verdadera de la compu local.
     * @param nombre0 Nombre inicial de la compu local (puede cambiar).
     * @param _ubicaciones Mapa de ubicaciones.
     * @param _dominios_bal Mapa de info del dominio de balance.
     * @param URL URL del archivo de configuración XML.
     * @param nodo Nodo del archivo XML.
     * @param computadora La computadora de valores a completar.
     * @return El nombre de la computadora.
     * @throws OIExcepcion
     */
    static String openMapXML(String direccion,String nombre0,Map _ubicaciones,Map _dominios_bal,String URL,Node nodo,
        String alias_local, Computadora computadora) throws OIExcepcion,ADMINGLOExcepcion{
      final Ubicaciones.LEEUbicaciones ubica=new Ubicaciones.LEEUbicaciones();
      boolean casiLocal=false;
      boolean existeLocal=false;
      Iterator itr;
      String dirNodo=direccion;
      String nombre=nombre0;
      Ubicaciones nodoInfo;
      Ubicaciones.LEEUbicaNodo nodoXML;
      Dominios_bal parInfo;
      Ubicaciones.LEEEnlace parXML;
      _ubicaciones.clear();
      _dominios_bal.clear();
      try {
        if(nodo!=null){
          ubica.setFromXMLNode(nodo);
        }else{
          ubica.setFromXMLURI(URL);
        }     
      }
      catch (MENSException ex) {
        throw new OIExcepcion("No se encontró el URL.",ex);
      }
      //
      // Trabaja con las ubicaciones
      //
      itr=ubica.getDirecciones().values().iterator();
      while(itr.hasNext()){
        nodoXML=(Ubicaciones.LEEUbicaNodo)itr.next();
        nodoInfo=new Ubicaciones();
        nodoInfo.nombre=nodoXML.ubicacion.nombre;
        nodoInfo.direccion=nodoXML.ubicacion.direccion;
        nodoInfo.desplaza_puerto=nodoXML.ubicacion.desplaza_puerto;
        casiLocal=(
            nodoInfo.direccion.compareToIgnoreCase(dirNodo)==0);
        if(casiLocal){
          if(nodoInfo.siComputadoraVirtual()){
            // debe revisarse el alias encontrado en configuración local.
            casiLocal=alias_local.compareTo(
                nodoXML.ubicacion.nombre)==0;
          }          
        }
        nodoInfo.si_local=casiLocal;
        if(casiLocal){
          // ahora si es un local legal.
          nombre=nodoInfo.nombre;
          computadora.setNombre(nodoInfo.nombre);
          computadora.setDesplazaPuerto(nodoInfo.desplaza_puerto);
        }
        _ubicaciones.put(nodoInfo.nombre,nodoInfo);
        existeLocal=existeLocal||casiLocal;
      }
      //
      // Trabaja con los enlaces
      //
      itr=ubica.getEnlaces().values().iterator();
      while(itr.hasNext()){
        parXML=(Ubicaciones.LEEEnlace)itr.next();
        parInfo=new Dominios_bal();
        parInfo.nombre=parXML.nodo1id;
        parInfo.vecino=parXML.nodo2id;
        // selecciona solamente info que tenga que ver con el nodo local
        if((parInfo.vecino.compareToIgnoreCase(nombre)==0)||
           (parInfo.nombre.compareToIgnoreCase(nombre)==0)){
          if(parInfo.vecino.compareToIgnoreCase(nombre)==0){
            parInfo.vecino=parInfo.nombre;
            parInfo.nombre=nombre;
          }
          _dominios_bal.put(parInfo.vecino,parInfo);
          parInfo.peso=parXML.peso;
        }
      }
      if(!existeLocal){
      	throw new ADMINGLOExcepcion("No se encontró información local " +
      			"sobre el nodo '"+direccion+"'");
      }
      return nombre;
      /*
       * Preparadas las ubicaciones y los enlaces, así como información sobre
       * el nodo local.
       */
    }
    static void writeMap(OIDescriptor info0, java.util.Map map)throws OIExcepcion{
      java.util.Iterator itr=null;
      Ubicaciones nodo;
      int res=0;
      res=doUpdateSQL(info0,"delete from ubicaciones");
      itr=map.values().iterator();
      while(itr.hasNext()){
        nodo=(Ubicaciones)itr.next();
        nodo.setDescriptor(info0);
        nodo.write();
        // System.out.println("Escrito "+nodo.nombre+" "+nodo.direccion);
      }
    }
    final String getDireccion() {
      return direccion;
    }
    final String getNombre() {
      return nombre;
    }
    boolean siComputadoraVirtual(){
      return this.desplaza_puerto!=0;
    }    
    final void setDireccion(String direccion) {
      this.direccion = direccion;
    }
    final void setNombre(String nombre) {
      this.nombre = nombre;
    }
  }
}