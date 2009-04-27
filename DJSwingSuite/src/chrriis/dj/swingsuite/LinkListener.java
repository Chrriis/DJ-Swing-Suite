/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.util.EventListener;

/**
 * @author Christopher Deckers
 */
public interface LinkListener<T> extends EventListener {

  /**
   * Invoked when a link is activated, it returns true if the other listeners and potentially the default handler should be invoked.<br>
   * Note that the default handler is only invoked if the object is of a recognized type.
   * @param link the link which was activated.
   * @param target the target of the link.
   * @return true if the other listeners and the default handler should be invoked, false otherwise.
   */
  public boolean linkActivated(JLink<T> link, T target);

}
