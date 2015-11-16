package admin;
import org.w3c.dom.*;
/**
 * <p>Title: Admin</p>
 * <p>Description: Administración de procesos</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero [alesscor@ieee.org]
 * @version 1.0
 */

public class zzADMINEstadoProceso {
  //////////////////////////////////////////////////////////////////////
  /**
   * Descriptor del proceso.<br>
   * Identificación del proceso. Compuesta por
   * <code>alias_nodo + número_proceso_nodo</code>.
   * Identificación del proceso padre. Compuesta por
   * <code>id</code> del proceso padre.<br>
   * Identificación del grupo de trabajo del proceso. Compuesta por
   * <code>alias_nodo + número_grupo</code>.<br>
   * <li> Esta identificación es copiada por todos los procesos del
   * mismo grupo.</li>
   * <li> Sirve para hacer listas de estados de procesos con esta
   * información:</li>
   * <pre>
   +-------------+
   | pd:         |
   |+---------+  |
   || id      |  |
   || idPadre |  |
   || idGrupo |  |
   |+---------+  |
   +-------------+
   | solicitud:  |
   |+-----------+|
   || alias     ||
   || ambiente[]||
   || param[]   ||
   || dirTrab   ||
   || esperar   ||
   |+-----------+|
   +-------------+
   | gestor      |
   +-------------+
   | horainicio  |
   +-------------+
   | progreso    |
   +-------------+
   </pre>
   */
  protected zzADMINDescriptorRevisar pd;
  /**
   * Hora en la que se inició la ejecución del proceso, son los
   * milisegundos después del 19700101 a las 00:00:00.
   */
  protected String horaInicio;
  /**
   * Valor del avance del cumplimiento del trabajo del proceso.
   */
  protected int progreso;
  /**
   * Objeto que contiene la solicitud originadora del proceso.
   */
  protected zzLEESolicitudProceso solicitud;
  /**
   * Objeto de carga y descarga de y a código XML.
   */
  private xmlestadoproceso xml;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public final zzLEESolicitudProceso getSolicitud(){
    return solicitud;
  }
  public final zzADMINDescriptorRevisar getDescriptor(){
    return this.pd;
  }
  public final String getHoraInicio(){
    return horaInicio;
  }
  public final int getProgreso(){
    return progreso;
  }
  public void setXMLParser(){
    if(xml==null){
      xml = new xmlestadoproceso(this);
    }
  }
  public void loadFromXMLNode(Node nodo) throws ADMINGLOExcepcion{
    try{
      xml.setFromXMLNode(nodo);
    }catch(Exception ex){
      throw new ADMINGLOExcepcion(ex.getMessage(),ex);
    }
  }
  public String storeToXMLNode() throws ADMINGLOExcepcion{
    String res="";
    setXMLParser();
    try{
      res = xml.getXMLElem(null);
    }catch(Exception ex){
      throw new ADMINGLOExcepcion(ex.getMessage(),ex);
    }
    return res;
  }
  //////////////////////////////////////////////////////////////////////
  /***
   * Clase dedicada a cargar y descargar el objeto ADMINDescriptorRevisar en un
   * XML.
   */
  private class xmlestadoproceso extends mens.MENSMensaje{
    //////////////////////////////////////////////////////////////////////
    private zzADMINEstadoProceso pd;
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    public xmlestadoproceso(zzADMINEstadoProceso pd0){
      pd=pd0;
    }
    protected String getXMLContainedElements() {
      String xml="";
      xml+="<estado>\n";
      try{
        xml += pd.getDescriptor().storeToXMLNode();
      }catch(ADMINGLOExcepcion ex){
        // si hay error no va descriptor
      }
      try{
        xml += "\n"+ pd.getSolicitud().storeToXMLNode();
      }catch(ADMINGLOExcepcion ex){
        // si hay error no va solicitud
      }

      xml+="\n\t<horainicio>";
      xml+=pd.horaInicio;
      xml+="</horainicio>\n";
      xml+="\t<progreso>";
      xml+=pd.progreso;
      xml+="</progreso>\n";
      xml+="</estado>";
      return xml;
    }
    protected void setContentFromDoc(Node nodo,int[]err,String[]merr){
      pd.horaInicio=getElementText(nodo,"horainicio").trim();
      pd.progreso=Integer.parseInt(getElementText(nodo,"progreso"));
      try{
        pd.getDescriptor().loadFromXMLNode(nodo);
        pd.getSolicitud().loadFromXMLNode(nodo);
      }catch(Exception ex){

      }
    }
    protected void toleraXML(int[]err,String[]merr){
      if(pd.horaInicio=="" || pd.solicitud ==null || pd.getDescriptor() ==null){
        // cero tolerancia
        err[0]=3;
        merr[0]="No se encuentra un elemento importante para " +
                "cargar el estado de un proceso.";
      }
    }
    public boolean isVacio(){
      return (pd.solicitud==null);
    }
  }
  //////////////////////////////////////////////////////////////////////
}
