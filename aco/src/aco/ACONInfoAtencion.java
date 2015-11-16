package aco;

/**
 * Genera información sobre la atención a una solicitud.
 */
public final class ACONInfoAtencion {
  private java.util.Date inicio=null;
  private java.util.Date termino=null;
  private ACONGestor gestor=null;
  public ACONInfoAtencion(ACONGestor gestor0) {
    inicio=new java.util.GregorianCalendar().getTime();
    gestor=gestor0;
  }
  public void setTermino(){
    termino=new java.util.GregorianCalendar().getTime();
  }
  public ACONGestor getGestor(){
    return gestor;
  }
  public java.util.Date getInicio(){
    return inicio;
  }
  public java.util.Date getTermino(){
    return termino;
  }
}