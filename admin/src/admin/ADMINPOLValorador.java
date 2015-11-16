/*
 * Created on 12/07/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package admin;

import admin.PERSAmbiente.Computadora;

/**
 * <p>Title: <b>admin</b>:: admin</p>
 * <p>Description: ADMINPOLValorador.java.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: UCR - ECCI</p>
 * <br>@author Alessandro</br>
 * <br>@version 1.0</br>
 */
/** <br>Realiza los cálculos para un objeto lector, manejando
 * principalmente, el conocimiento de las fórmulas y los pesos
 * y parámetros utilizados en cada una de ellas.</br>
 * <br>Eventualmente podría leer las fórmulas de la base
 * de datos o de otras computadoras con tdderive por medio
 * del planificador.</br>
 * <br>Se requiere que el lector haya actualizado la 
 * computadora del objeto <tt>OIDescriptor</tt>.</br>
 */
class ADMINPOLValorador {
  /**
   * Calcula la capacidad de la computadora.
   * @return La capacidad de la computadora.
   */
  static double calcCapacidad(Computadora compu) throws ADMINGLOExcepcion {
    double capacidad=0.0;
    if(!siCalculoCapacidadHabilitado(compu)){
      throw new ADMINGLOExcepcion("Faltan valores para calcular la " +
          "capacidad de la computadora.");
    }
    capacidad=(compu.getBusesTipo().compareTo(Computadora.BUSES_SERVIDOR)!=0)? 
        compu.getPesoUmbralPolitica("PARM0_FACTOR_ARQ"):1.00;
    capacidad*=compu.getMicroCant()*compu.getMicroReloj()/
        (compu.getPesoUmbralPolitica("PARM0_RELOJ_BASE"));
    capacidad*=compu.getPesoUmbralPolitica("PESO0_MICRO");
    
    capacidad+=compu.getMemCant()/
        ((compu.getPesoUmbralPolitica("PARM0_MEM_BASE")))*
        ((compu.getPesoUmbralPolitica("PESO0_RAM")));
    
    capacidad+=compu.getDiscoCant()/
        ((compu.getPesoUmbralPolitica("PARM0_DISCO_BASE")))*
        ((compu.getPesoUmbralPolitica("PESO0_DISCO")));
    
    capacidad+=(compu.getVecinosCant()-1)/
        ((compu.getPesoUmbralPolitica("PARM0_VECINAS_BASE")))*
        ((compu.getPesoUmbralPolitica("PESO0_VECINAS")));
    return capacidad;
  }
  /**
   * Indica si se encuentran todos los valores para poder calcular
   * la capacidad de la computadora.
   * @return Si se puede calcular la capacidad de la computadora.
   */
  static private boolean siCalculoCapacidadHabilitado(Computadora compu) {
    try {
      if(((compu.getPesoUmbralPolitica("PARM0_DISCO_BASE"))==0) ||
        ((compu.getPesoUmbralPolitica("PARM0_FACTOR_ARQ"))==0) ||
        ((compu.getPesoUmbralPolitica("PARM0_MEM_BASE"))==0) ||
        ((compu.getPesoUmbralPolitica("PARM0_RELOJ_BASE"))==0) ||
        ((compu.getPesoUmbralPolitica("PARM0_VECINAS_BASE"))==0) ||
        ((compu.getPesoUmbralPolitica("PESO0_DISCO"))==0) ||
        ((compu.getPesoUmbralPolitica("PESO0_MICRO"))==0) ||
        ((compu.getPesoUmbralPolitica("PESO0_RAM"))==0) ||
        ((compu.getPesoUmbralPolitica("PESO0_VECINAS"))==0)){
        return false;
      }
    } catch (ADMINGLOExcepcion e) {
      return false;
    }    
    return true;
  }
  
  /**
   * Indica si se encuentran todos los valores para poder calcular
   * la carga funcional de la computadora.
   * @return Si se puede calcular la carga funcional de la computadora.
   */
  static private boolean siCalculoCargaFHabilitado(Computadora compu) {
    try {
      if(((compu.getPesoUmbralPolitica("PARM1_DISCO_LIBRE_MIN"))==0) ||
      ((compu.getPesoUmbralPolitica("PARM1_MEM_LIBRE_MIN"))==0) ||
      ((compu.getPesoUmbralPolitica("PARM1_PROM_CARGA_MAX"))==0) ||
      ((compu.getPesoUmbralPolitica("PESO1_CARGA"))==0) ||
      ((compu.getPesoUmbralPolitica("PESO1_DISCO_LIBRE"))==0) ||
      ((compu.getPesoUmbralPolitica("PESO1_MEM_LIBRE"))==0) ||
      ((compu.getPesoUmbralPolitica("PESO1_VECINAS_USADAS"))==0)){
        return false;
      }
    } catch (ADMINGLOExcepcion e) {
      return false;
    }    
    return true;
  }
  /**
   * Indica si se encuentran todos los valores para poder calcular
   * la carga de aplicación de la computadora.
   * @return Si se puede calcular la carga de aplicación de la computadora.
   */
  static private boolean siCalculoCargaAHabilitado(Computadora compu) {
    try {
      if(((compu.getPesoUmbralPolitica("PARM2_AJUSTE"))==0) ||
      ((compu.getPesoUmbralPolitica("PESO2_MAYOR"))==0) ||
      ((compu.getPesoUmbralPolitica("PESO2_MENOR"))==0)){
        return false;
      }
    } catch (ADMINGLOExcepcion e) {
      return false;
    }    
    return true;
  }  
  /**
   * Calcula la carga de aplicación de la computadora.
   * @return La carga de aplicación de la computadora.
   */
  static double calcCargaAplicacion(Computadora compu) throws ADMINGLOExcepcion {
    double carga_aplicacion=0.0;
    if(!siCalculoCargaAHabilitado(compu)){
      throw new ADMINGLOExcepcion("Faltan valores para calcular la " +
          "carga de aplicación de la computadora.");
    }
    carga_aplicacion=(compu.getLocalActiva()>compu.getLocalEspera())?
        compu.getLocalActiva()*(compu.getPesoUmbralPolitica("PESO2_MAYOR"))+
            compu.getLocalEspera()*(compu.getPesoUmbralPolitica("PESO2_MENOR")):
            compu.getLocalActiva()*(compu.getPesoUmbralPolitica("PESO2_MENOR"))+
            compu.getLocalEspera()*(compu.getPesoUmbralPolitica("PESO2_MAYOR"));
    carga_aplicacion/=(compu.getPesoUmbralPolitica("PARM2_AJUSTE"));
    return carga_aplicacion;
  }
  /**
   * Calcula la carga funcional de la computadora.
   * @return La carga funcional de la computadora.
   */
  static double calcCargaFuncional(Computadora compu) throws ADMINGLOExcepcion {
    double carga_funcional=0.0;
    if(!siCalculoCargaFHabilitado(compu)){
      throw new ADMINGLOExcepcion("Faltan valores para calcular la " +
          "carga funcional de la computadora.");
    }
    /*
     * evita división entre cero y realiza
     * alternativas en pro del cálculo.
     */
    if(compu.getVecinosCant()!=0){
      carga_funcional=((1-compu.getMicroLibre())/
          compu.getPesoUmbralPolitica("PARM1_PROM_CARGA_MAX"))*
          (compu.getPesoUmbralPolitica("PESO1_CARGA"));
      carga_funcional+=(1+compu.getVecinasUsadas())/
          (compu.getVecinosCant())*
          (compu.getPesoUmbralPolitica("PESO1_VECINAS_USADAS"));
      carga_funcional+=(1-compu.getMemLibre())/
          (1-(compu.getPesoUmbralPolitica("PARM1_MEM_LIBRE_MIN")))*
          (compu.getPesoUmbralPolitica("PESO1_MEM_LIBRE"));
      carga_funcional+=(1-compu.getDiscoLibre())/
          (1-(compu.getPesoUmbralPolitica("PARM1_DISCO_LIBRE_MIN")))*
          (compu.getPesoUmbralPolitica("PESO1_DISCO_LIBRE"));
    }else{
      /*
       * debido a que vecinas_cantidad==0 entonces se reparte el 
       * peso entre los demás pesos disponibles.
       */
      carga_funcional=(1-compu.getMicroLibre())/
          (compu.getPesoUmbralPolitica("PARM1_PROM_CARGA_MAX"))*
          ((compu.getPesoUmbralPolitica("PESO1_CARGA"))+
          (compu.getPesoUmbralPolitica("PESO1_VECINAS_USADAS"))/3);
      
      carga_funcional+=(1-compu.getMemLibre())/(1-(compu.getPesoUmbralPolitica("PARM1_MEM_LIBRE_MIN")))*
          ((compu.getPesoUmbralPolitica("PESO1_MEM_LIBRE"))+
          (compu.getPesoUmbralPolitica("PESO1_VECINAS_USADAS"))/3);
      
      carga_funcional+=(1-compu.getDiscoLibre())/
          (1-(compu.getPesoUmbralPolitica("PARM1_DISCO_LIBRE_MIN")))*
          ((compu.getPesoUmbralPolitica("PESO1_DISCO_LIBRE"))+
          (compu.getPesoUmbralPolitica("PESO1_VECINAS_USADAS"))/3);
    }
    return carga_funcional;
  }
  /**
   * Consulta si después de ejecutar un subtrabajo del peso dado se puede
   * ejecutar otro subtrabajo.
   * <li>Realiza la decisión según el estado de la computadora,
   * particularmente la carga de aplicación y la carga funcional de
   * ésta.</li>
   * <li>Se pone desde un punto de vista donde se tiene un subtrabajo
   * que procesar de manera irremediable: un subtrabajo solicitado con
   * mucha anticipación y que no ha dado respuesta.</li>
   * @param nPeso Peso del subtrabajo que se va a ejecutar.
   * @param compu Información sobre la computadora.
   * @return Si se debe procesar un nuevo subtrabajo o no.
   */
  static boolean calcSiProcesarSubtrabajo(double nPeso,Computadora compu){
    return false;
  }
}
