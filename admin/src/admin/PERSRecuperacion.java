package admin;
import orgainfo.*;
/**
 * <p>Title: Administración de recursos</p>
 * <p>Description: Administrador de recursos para tdderive</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ECCI - UCR</p>
 * @author Alessandro Cordero
 * @version 1.0
 */
/**
 * Anida clases para la persistencia de datos sobre la
 * recuperación de <tt>tdderive</tt>.
 */

class PERSRecuperacion {
  static class Estado_tablas extends OIEstado_tablas{
    boolean getSiTerminada(){return si_terminada;}
    Estado_tablas(){
      super();
    }
    Estado_tablas(ADMINGLOInfo info0){
      super(info0);
    }
  }
  static class Estado_instancia extends OIEstado_instancia{
    boolean getSiIniciada(){return si_iniciada;}
    boolean getSiTerminada(){return si_terminada;}
    Estado_instancia(){
      super();
    }
    Estado_instancia(ADMINGLOInfo info0){
      super(info0);
    }
  }
}