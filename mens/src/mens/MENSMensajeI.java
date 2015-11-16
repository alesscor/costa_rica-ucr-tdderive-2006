package mens;
/**
 * Interfaz para construir clases de mensajes a partir de
 * fuentes de datos dados en formato XML.
 */
public interface MENSMensajeI extends java.io.Serializable {
  public String getInfoVerificacion(String infoVerificacion);
//  public String getAlias();
//  public int getIndice();
//  public String getNodo();
//  public char getTipoMensaje();
//  public String getVerificaGeneral();
//  public int getVencimiento();
//  public String getNombreObjetoEmisor();
  public String[] getMensaje();
//  public void setInfoVerificacion(String infoVerificacion);
//  public void setVencimiento(int vencimiento);
  public void setMensaje(String mensaje[]);
//  public void setField(String fieldName,Object fieldValue)throws MENSException;
//  public void setField(String fieldName,boolean fieldValue)throws MENSException;
//  public void setField(String fieldName,byte fieldValue)throws MENSException;
//  public void setField(String fieldName,char fieldValue)throws MENSException;
//  public void setField(String fieldName,double fieldValue)throws MENSException;
//  public void setField(String fieldName,float fieldValue)throws MENSException;
//  public void setField(String fieldName,int fieldValue)throws MENSException;
//  public void setField(String fieldName,long fieldValue)throws MENSException;
//  public void setField(String fieldName,short fieldValue)throws MENSException;
//  public void setField(String fieldName,String fieldValue);
//  public String getField(String fieldName);
  public boolean isVacio();
  public void setFromXMLURI(String URI)throws MENSException;
  public void setFromXMLSource(java.io.InputStream source)throws MENSException;
  public void setFromXMLSource(String source)throws MENSException;
  public String getXMLRootElem(String nombre);
}
