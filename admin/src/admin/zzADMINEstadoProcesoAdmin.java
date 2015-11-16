package admin;
/**
 * <p>Title: Admin</p>
 * <p>Description: Administración de procesos</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero [alesscor@ieee.org]
 * @version 1.0
 */

public final class zzADMINEstadoProcesoAdmin extends zzADMINEstadoProceso {
  //////////////////////////////////////////////////////////////////////
  /**
   * Objeto capaz de detener al proceso en tiempo de ejecución.
   */
  private ADMINPOLEscoltas gestor;
  /**
   * Información sobre el comando a ejecutar por este proceso.
   */
//  private ADMAliasComando comando;
  /**
   * Nodo actual, en donde se ejecuta el proceso.
   */
//  private ADMBDAlias aliases;
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////
  public zzADMINEstadoProcesoAdmin(String id0,String idPadre0,String idGrupo0,
                          zzLEESolicitudProceso solicitud0) {
    this.pd=new zzADMINDescriptorRevisar(id0,idPadre0,idGrupo0,solicitud0);
    setSolicitud(solicitud0);
    horaInicio=null;
  }
  public void setGestor(ADMINPOLEscoltas gestor0){
    gestor=gestor0;
  }
  public void setHoraInicio(){
    if(horaInicio==null){
      // obtiene segundos desde 19700101 a las 00:00:00
      horaInicio=""+((new java.util.Date()).getTime()/1000);
    }
  }
  public void setSolicitud(zzLEESolicitudProceso sol){
    this.solicitud=sol;
//    if((solicitud!=null) && (aliases!=null)){
//      comando = aliases.getByAlias(solicitud.getAlias());
//    }else{
//      comando=null;
//    }
  }
  public void setComando()throws ADMINGLOExcepcion{
//    if((solicitud!=null) && (aliases!=null)){
//      comando = aliases.getByAlias(solicitud.getAlias());
//      solicitud.setComando(comando.getComando());
//    }else{
//      comando=null;
//      if(solicitud!=null){
//        throw new ADMINExcepcion("No se encuentra el comando del alias \"" +
//                               this.solicitud.getAlias() + "\"");
//      }else{
//        throw new ADMINExcepcion("La solicitud es nula");
//      }
//    }
  }
  public ADMINPOLEscoltas getGestor(){
    return gestor;
  }
  //////////////////////////////////////////////////////////////////////
}
