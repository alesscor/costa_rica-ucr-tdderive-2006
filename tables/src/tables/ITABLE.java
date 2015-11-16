package tables;
/**
 * Interfaz de contenedores.
 */
public interface ITABLE extends java.rmi.Remote {
  public boolean GetNodeInfoByIP(String cIP,String cFields,String[] aValues)
    throws java.rmi.RemoteException;
  public boolean GetNodeInfoByName(String cName,String cFields,String[] aValues)
    throws java.rmi.RemoteException;
  public void DeleteNode(int nNode)
    throws java.rmi.RemoteException;
  public int UpdateNode(int nNode,String fields,String values)
    throws java.rmi.RemoteException;
  public int Get_NodeInfo(int nNode,String fields,String values,
  String filter)
    throws java.rmi.RemoteException;
  public int Close()
    throws java.rmi.RemoteException;
}