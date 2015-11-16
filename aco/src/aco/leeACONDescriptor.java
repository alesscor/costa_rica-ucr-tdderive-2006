package aco;

import mens.*;
import org.w3c.dom.*;


/**
 * Objeto dedicado a leer descriptores guardados
 * en algún almacenamiento.
 */
public final class leeACONDescriptor extends MENSMensaje {
  //////////////////////////////////////////////////////////////////////
  ACONDescriptorAdmin instancia;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public leeACONDescriptor() {
    instancia=null;
  }
  public void setobj(ACONDescriptorAdmin obj){
    instancia=obj;
  }
  public ACONDescriptorAdmin getobj(){
    return instancia;
  }
  public boolean isVacio() {
    /**@todo Implement this mens.MENSMensajeI abstract method*/
    return (instancia.id==null)||(instancia.id=="");
  }
  public static boolean vacio(String str){
    return (str==null) || (str=="");
  }
  protected String getXMLContainedElements() {
    String xml="";
    String str="";
    xml+="<servicio>\n";
    xml+="\t<id>"+instancia.id+"</id>\n";
    if(!vacio(instancia.localhost)){
       xml += "\t<localhost>" + instancia.localhost + "</localhost>\n";
    }
    if(instancia.localport>0){
      xml += "\t<localport>" + instancia.localport + "</localport>\n";
    }
    if(!vacio(instancia.protocol)){
      xml += "\t<protocol>" + instancia.protocol + "</protocol>\n";
    }
    if(!vacio(instancia.remotehost)){
      xml += "\t<remotehost>" + instancia.remotehost + "</remotehost>\n";
    }
    if(instancia.remoteport>0){
      xml += "\t<remoteport>" + instancia.remoteport + "</remoteport>\n";
    }
    if(!vacio(instancia.server)){
      xml += "\t<server>" + instancia.server + "</server>\n";
    }
    if(instancia.socket_type>0){
      switch(instancia.socket_type){
        case ACONDescriptor.DGRAM:
          str="DGRAM";
          break;
        case ACONDescriptor.STREAM:
          str="STREAM";
          break;
        case ACONDescriptor.SEQPACKET:
          str="SEQPACKET";
          break;
      }
      xml += "\t<socket_type>" + str + "</socket_type>\n";
    }
    if(instancia.tamannoMaximo>0){
      xml += "\t<tamannoMaximo>" + instancia.tamannoMaximo +
          "</tamannoMaximo>\n";
    }
    if(!vacio(instancia.user)){
      xml += "\t<user>" + instancia.user + "</user>\n";
    }
    if(instancia.wait){
      xml += "\t<wait>true</wait>\n";
    }else{
      // xml += "\t<wait>false</wait>\n";
    }
    xml+="</servicio>";
    return xml;
  }
  protected void setContentFromDoc(Node node, int[] nerr, String[] merr) {
    String valor="";
    if(instancia==null){
      instancia=new ACONDescriptorAdmin();
    }
    instancia.id=getElementText(node,"id");
    instancia.localhost=getElementText(node,"localhost");
    try{
      instancia.localport = Integer.parseInt(getElementText(node, "localport"));
    }catch(Exception ex){
      instancia.localport = 0;
    }
    instancia.protocol=getElementText(node,"protocol");
    instancia.remotehost=getElementText(node,"remotehost");
    try{
      instancia.remoteport = Integer.parseInt(getElementText(node, "remoteport"));
    }catch(Exception ex){
      instancia.remoteport = 0;
    }
    instancia.server=getElementText(node,"server");
    valor=getElementText(node,"socket_type");
    if(valor.compareToIgnoreCase("DGRAM")==0){
      instancia.socket_type=ACONDescriptor.DGRAM;
    }
    if(valor.compareToIgnoreCase("STREAM")==0){
      instancia.socket_type=ACONDescriptor.STREAM;
    }
    if(valor.compareToIgnoreCase("SEQPACKET")==0){
      instancia.socket_type=ACONDescriptor.SEQPACKET;
    }
    try{
      instancia.tamannoMaximo = Integer.parseInt(getElementText(node,
          "tamannoMaximo"));
    }catch(Exception ex){
      instancia.tamannoMaximo = 0;
    }
    instancia.user=getElementText(node,"user");
    valor=getElementText(node,"wait");
    if(valor.compareToIgnoreCase("true")==0){
      instancia.wait=true;
    }else{
      instancia.wait=false;
    }
  }
  protected void toleraXML(int[] nerr, String[] merr) {
    if(vacio(instancia.id)){
      nerr[0]=3;
      merr[0]="No se tiene identificación para el objeto";
    }
  }
  //////////////////////////////////////////////////////////////////////
}