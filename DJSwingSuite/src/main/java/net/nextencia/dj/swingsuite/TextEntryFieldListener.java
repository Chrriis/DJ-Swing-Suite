/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.dj.swingsuite;

import java.util.EventListener;

/**
 * The listener interface for receiving events.
 * @author Christopher Deckers
 */
public interface TextEntryFieldListener extends EventListener {

  /**
   * Invoked when an error message is set or cleared.
   */
  public void errorMessageChanged(JTextEntryField validationField, String errorMessage);

  /**
   * Invoked when a valid text of an entry field is committed.
   */
  public void textCommitted(JTextEntryField validationField);

}
