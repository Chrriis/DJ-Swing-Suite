/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.dj.swingsuite;

/**
 * A formatter for a text entry field when it does not have the focus.
 * @author Christopher Deckers
 */
public interface TextEntryFormatter {

  /**
   * Get the text to show when the field does not have the focus.
   * @param textEntryField The text entry field for which to display the value.
   * @param validText The text for which to show a representation.
   * @return a specific value or null for the default.
   */
  public String getTextForDisplay(JTextEntryField textEntryField, String validText);

}
