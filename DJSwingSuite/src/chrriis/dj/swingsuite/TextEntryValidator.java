/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

/**
 * @author Christopher Deckers
 */
public abstract class TextEntryValidator {

  /**
   * Indicate whether the text is allowed.
   * @param textEntryField The text entry field for which to test the value.
   * @return true if the text is allowed, false otherwise.
   */
  public boolean isTextAllowed(JTextEntryField textEntryField, String text) {
    return true;
  }

  /**
   * Indicate whether the text is valid.
   * @param textEntryField The text entry field for which to test the value.
   * @return true if the text is valid, false otherwise.
   */
  public boolean isTextValid(JTextEntryField textEntryField, String text) {
    return true;
  }

  /**
   * Get the message to show when the current text is invalid, or null for the default.
   * @param textEntryField The text entry field for which to test the value.
   * @param invalidText the text which is invalid.
   * @return the message.
   */
  public String getInvalidTextErrorMessage(JTextEntryField textEntryField, String invalidText) {
    return null;
  }

  /**
   * Get a default text that is valid within the current set of validation constraints.<br>
   * This method is invoked by the validation mechanism so it needs to be properly implemented..
   * @param textEntryField The text entry field for which to test the value.
   * @return A valid default text.
   */
  public String getDefaultValidText(JTextEntryField textEntryField) {
    return "";
  }

}
