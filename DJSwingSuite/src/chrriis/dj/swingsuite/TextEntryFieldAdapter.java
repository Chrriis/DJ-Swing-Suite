/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;


/**
 * An adapter for the text entry listener interface.
 * @author Christopher Deckers
 */
public abstract class TextEntryFieldAdapter implements TextEntryFieldListener {

  /**
   * Invoked when an error message is set or cleared.
   */
  public void errorMessageChanged(JTextEntryField validationField, String errorMessage) {
  }

  /**
   * Invoked when a valid text of an entry field is committed.
   */
  public void textCommitted(JTextEntryField validationField) {
  }

}
