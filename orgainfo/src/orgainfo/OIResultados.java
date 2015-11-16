package orgainfo;

/**
 * Personifican los resultados que un subtrabajo o tarea puede tener en 
 * <tt>tdderive</tt>.
 */
public class OIResultados extends OIPersistente implements OIActualiza{
  protected String id_tarea;
  protected String id_subtrabajo;
  protected long hora_fin;
  protected String rutas;
  protected boolean si_completado;
  protected OIResultados(OIDescriptor info0,boolean sivacio){
    super(info0,sivacio);
  }
  public void open(){
  }
  public void write(){
  }
  public void delete(){
  }
  public void close(){
  }
  public void creaVacio(){
  }
  public void creaUltimo(){
  }
}
