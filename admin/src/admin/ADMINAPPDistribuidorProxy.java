package admin;

import oact.OACTDistribuidorBase;
import org.w3c.dom.*;
import mens.*;
import aco.*;


/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Intermediario de un despachador.
 */
final class ADMINAPPDistribuidorProxy extends OACTDistribuidorBase {
  public final static String DESTINO_OMISION="localhost";
  private String sDestinoOmision;
  private boolean si_espera;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public ADMINAPPDistribuidorProxy(String archivo) {
    super(archivo,"iniciador");
    _inicio();    
  }
  public ADMINAPPDistribuidorProxy(Node nodo) {
    super(nodo,"iniciador");    
    _inicio();    
  }
  private void _inicio(){
    si_espera=false;
    if(sDestinoOmision==null || sDestinoOmision == ""){
      sDestinoOmision=DESTINO_OMISION;
    }
  }
  public String getDestinoOmision(){
    return this.sDestinoOmision;
  }
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public void setFromXMLConcreto(Node nodo){
    String value="";
    sDestinoOmision=MENSMensaje.getElementText(nodo,"destino_omision");
    value=MENSMensaje.getElementText(nodo,"si_espera");
    if(value!=""){
      si_espera=(value.compareTo("verdadero")!=0);
      if(!si_espera){
        si_espera=(value.compareTo("true")!=0);
      }
    }
    if(sDestinoOmision==""){
      sDestinoOmision=DESTINO_OMISION;
    }

  }
  public String getXMLConcreto(){
    String xml="";
    xml+="<destino_omision>"+sDestinoOmision+"</destino_omision>";
    xml+="<si_espera>"+si_espera+"</si_espera>";
    return xml;
  }
  /**
   * Crea un descriptor con información de configuración.
   * <li>Se asigna como destino el considerado por omision.</li>
   * @see oact.OACTDistribuidorBase#getDescripcion()
   */
  public ACONDescriptor getDescripcion(){
    ACONDescriptor desc=new ACONDescriptor();
    desc.localhost="localhost";
    desc.localport=0;
    desc.remotehost=sDestinoOmision;
    desc.remoteport=this.puerto;
    desc.socket_type=ACONDescriptor.STREAM;
    desc.wait=si_espera;
    desc.aoNavegables=new Object[]{this};
    desc.tiempoEspera=this.intervalo_stream;
    return desc;
  }  
}