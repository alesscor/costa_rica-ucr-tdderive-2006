package mineria;
import java.io.File;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

/*
 * Created on 19/05/2004
 */
/**
 */
public class MINERIASelector extends AbstractSelector {
  /**
   * @param provider
   */
  public MINERIASelector(SelectorProvider provider) {
    super(provider);
  }
  /**
   * @see java.nio.channels.spi.AbstractSelector#implCloseSelector()
   */
  protected void implCloseSelector() throws IOException {
  }
  /**
   * @see java.nio.channels.spi.AbstractSelector#register(java.nio.channels.spi.AbstractSelectableChannel, int, java.lang.Object)
   */
  protected SelectionKey register(
    AbstractSelectableChannel ch,
    int ops,
    Object att) {
    return null;
  }
  /**
   * @see java.nio.channels.Selector#keys()
   */
  public Set keys() {
    return null;
  }
  /**
   * @see java.nio.channels.Selector#selectedKeys()
   */
  public Set selectedKeys() {
    return null;
  }
  /**
   * @see java.nio.channels.Selector#selectNow()
   */
  public int selectNow() throws IOException {
    return 0;
  }
  /**
   * @see java.nio.channels.Selector#select(long)
   */
  public int select(long timeout) throws IOException {
    return 0;
  }
  /**
   * @see java.nio.channels.Selector#select()
   */
  public int select() throws IOException {
    return 0;
  }
  /**
   * @see java.nio.channels.Selector#wakeup()
   */
  public Selector wakeup() {
    return null;
  }
  public boolean existeArchivo(String nombre){
    boolean siExiste=false;
    File fArchivo=null;
    try{
      begin();
      fArchivo=new File(nombre);
      siExiste=fArchivo.exists();
    }finally{
      end();
    }
    return siExiste;
  }
}
