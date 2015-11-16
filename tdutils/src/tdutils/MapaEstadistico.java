package tdutils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/*
 * Created on 18/08/2004
 */

/**
 * <br>Contiene objetos de la misma clase llevando estadísticas
 * de sus miembros de tipo numérico <tt>double</tt>.</br>
 */

public class MapaEstadistico{
  /**
   * Indica cuál es la clase de los objetos contenidos.
   */
  Class _claseContenido;
  Map _contenidos;
  /**
   * Registra el índice de los campos que se están utilizando.
   */
  int _indices[];
  /**
   * Contiene información sobre los campos y el índice que se les asigna.
   */
  Map _infoAtributos;
  /**
   * Indica cuál es la clase de los objetos llave.
   */
  Comparator _objetoComparador;
  /**
   * Registra el total de cada campo de tipo <tt>double</tt>.
   */
  double _totales[];
  public MapaEstadistico(Comparator comparador,Class claseContenido0){
    _contenidos=new TreeMap(comparador);
    _infoAtributos=new TreeMap(String.CASE_INSENSITIVE_ORDER);
    _claseContenido=claseContenido0;
    _objetoComparador=comparador;
    _totales=new double[_claseContenido.getFields().length];
    _indices=new int[_totales.length];
    this.examinaCampos(claseContenido0);
  }
  /**
   * Agrega un nuevo elemento al mapa estadístico.
   * @param llave Objeto con el cual se puede buscar al objeto dado.
   * @param objeto Objeto a registrar y a evaluar en el mapa estadístico.
   * @return El objeto anterior que estaba relacionado con la llave.
   * @throws Exception Si hay un error.
   */
  public Object agrega(Object llave,Object objeto)throws Exception{
    Object retorno=null;
    try{
      _objetoComparador.compare(llave, llave);
    }catch(Exception ex){
      throw new Exception("La llave no corresponde como " +
          "instancia de "+_objetoComparador.getClass().getName()+".");
    }
    if (!_claseContenido.isInstance(objeto)){
      throw new Exception("El objeto no corresponde como " +
          "instancia de "+_claseContenido.getName()+".");
    }
    retorno=_contenidos.put(llave, objeto);
    recolecta(objeto,true);
    return retorno;
  }
  public Object deme(Object llave){
    Object retorno=null;
    retorno=_contenidos.get(llave);
    return retorno;
  }
  /**
   * Devuelve la media del atributo dado en los
   * objetos de la lista.
   * <li>Se retorna negativo cuando el atributo no existe o no está
   * disponible.</li>
   * @param atributo El nombre del atributo de los objetos contenidos.
   * @return La media de los atributos de los objetos dados.
   */
  public double demeMedia(String atributo){
    int indiceCampo;
    double res=-1.0;
    InfoAtributos info;
    info=(InfoAtributos)this._infoAtributos.get(atributo);
    if(info==null){
      return res;
    }else{
      res=_totales[info.indiceAtributo]/this._contenidos.size();
      return res;
    }
  }
  /**
   * Retorna el objeto de la mediana para el atributo dado.
   * @param atributo El atributo con el cual se busca el objeto que está
   * en la mediana.
   * @return El objeto que está en la mediana o nulo, si no hubo objetos.
   */
  public Object demeMediana(String atributo){
    int nLugarMediana=0;
    Object objeto=null;
    Collection col=this.valores(atributo);
    if(col.size()<=0){
      return null;
    }
    nLugarMediana=col.size()/2;
    objeto=col.toArray()[nLugarMediana];
    return objeto;
  }
  /**
   * Devuelve los objetos ordenados por un atributo dado.
   * @param atributo El atributo con el cual se ordenan los objetos.
   * @return La coleccion de objetos ordenada. Nunca es null.
   */
  public Collection valores(String atributo){
    int nLugarAtributo;
    InfoAtributos info;
    TreeMap conjunto=new TreeMap();
    Iterator itr;
    Object objI=null;
    Double valor=new Double(0);
    info=(InfoAtributos)this._infoAtributos.get(atributo);
    if(info==null){
      return conjunto.values();
    }
    nLugarAtributo=info.getLugarAtributo();
    itr=this._contenidos.values().iterator();
    while(itr.hasNext()){
      objI=itr.next();
      try {
        valor=new Double(this._claseContenido.getFields()[nLugarAtributo].getDouble(objI));
        while(conjunto.get(valor)!=null){
          valor=new Double(valor.doubleValue()+ 0.0000000001);
        }
        conjunto.put(valor,objI);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return conjunto.values();
  }
  /**
   * Devuelve el total para el atributo dado en los
   * objetos de la lista.
   * <li>Se retorna negativo cuando el atributo no existe o no está
   * disponible.</li>
   * @param atributo El nombre del atributo de los objetos contenidos.
   * @return La suma de los atributos de los objetos dados.
   */
  public double demeTotal(String atributo){
    int indiceCampo;
    double res=-1.0;
    InfoAtributos info;
    info=(InfoAtributos)this._infoAtributos.get(atributo);
    if(info==null){
      return res;
    }else{
      res=_totales[info.indiceAtributo];
      return res;
    }
  }
  public Object remueve(Object llave){
    Object retorno=null;
    retorno=_contenidos.remove(llave);
    recolecta(llave,true);
    return retorno;
  }
  public Collection valores(){
    return this._contenidos.values();
  }
  private void examinaCampos(Class clase){
    int i,j;
    Thread th;
    Object conDouble=new Object(){
      public double campo1;
    };
    j=0;
    for(i=0;i<_indices.length;i++){
      try{
        if(((Field)_claseContenido.getFields()[i]).getType().isAssignableFrom(
            conDouble.getClass().getFields()[0].getType())){
          _indices[j]=i;
          this._infoAtributos.put(
              ((Field)_claseContenido.getFields()[i]).getName(),
              new InfoAtributos(j,
                  ((Field)_claseContenido.getFields()[i]).getName(),i));
          j++;
        }
      }catch(Exception ex){
      }
    }
    for(;j<_indices.length;j++){
      _indices[j]=-1;
    }
  }
  /**
   * Recolecta los valores reales de los atributos de los
   * objetos y actualiza la suma total.
   * @param objeto El objeto del cual se recolecta información
   * de los atributos reales.
   */
  private void recolecta(Object objeto,boolean siAgregar) {
    int i;
    i=0;
    if(_indices.length>0){
        while(i<_indices.length && _indices[i]>=0){
          try {
            if(siAgregar){
              _totales[i]=_totales[i]+_claseContenido.getFields()[_indices[i]]
                                                    .getDouble(objeto);
            }else{
              _totales[i]=_totales[i]-_claseContenido.getFields()[_indices[i]]
                                                    .getDouble(objeto);
            }
          } catch (IllegalArgumentException e) {
          } catch (SecurityException e) {
          } catch (IllegalAccessException e) {
          }
          i++;
        }
      }
    }
  static class InfoAtributos{
    private int indiceAtributo;
    private int lugarAtributo;
    private String nombreAtributo;
    InfoAtributos(int nIndice,String sNombre,int nLugar){
      indiceAtributo=nIndice;
      nombreAtributo=sNombre;
      lugarAtributo=nLugar;
    }
    final int getIndiceAtributo() {
      return indiceAtributo;
    }
    final int getLugarAtributo() {
      return lugarAtributo;
    }
    final String getNombreAtributo() {
      return nombreAtributo;
    }
    final void setIndiceAtributo(int indiceAtributo) {
      this.indiceAtributo = indiceAtributo;
    }
    final void setLugarAtributo(int lugarAtributo) {
      this.lugarAtributo = lugarAtributo;
    }
    final void setNombreAtributo(String nombreAtributo) {
      this.nombreAtributo = nombreAtributo;
    }
  }
}