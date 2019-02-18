/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.dj.swingsuite;


/**
 * The superclass of all masks.
 * @author Christopher Deckers
 */
public abstract class TextEntryMask {

  /**
   * Considering some text and a code point that is being inserted at a given position, return the code point to actually insert or null if the insertion is not allowed.
   *  @param text the text in which the insertion will occur.
   *  @param codePoint the code point that is being inserted.
   *  @param position the position at which the code point is inserted.
   *  @return the actual code point to insert, or null if this insertion is not allowed.
   */
  protected abstract Integer getCodePoint(String text, int codePoint, int position);

  /**
   * Get the length of this mask.
   * @return the length of this mask.
   */
  protected abstract int getLength();

  /**
   * Get the code point to use at a specific position when there is no user input.
   * @param position the position for which to get the code point.
   * @return the default code point.
   */
  protected abstract int getDefaultCodePoint(int position);

  /**
   * Get the next valid input position that is the same or after the specified postion.
   * @param position the reference position to get the next valid one.
   * @return the next valid input position.
   */
  protected abstract int getNextValidInputPosition(int position);

  boolean isTextValid(String text) {
    if(text.length() != getLength()) {
      return false;
    }
    for(int i=text.length()-1; i>=0; i--) {
      int codePoint = text.codePointAt(i);
      if(getCodePoint(text, codePoint, i) == null) {
        return false;
      }
    }
    return true;
  }

  String getDefaultText() {
    int[] chars = new int[getLength()];
    for(int i=0; i<chars.length; i++) {
      chars[i] = getDefaultCodePoint(i);
    }
    return new String(chars, 0, chars.length);
  }

  private static int[] getCodePoints(String s) {
    int[] codePoints = new int[s.length()];
    for(int i=0; i<codePoints.length; i++) {
      codePoints[i] = s.codePointAt(i);
    }
    return codePoints;
  }

  String insertText(String text, String textToInsert, int position) {
    int length = getLength();
    if(text.length() != length) {
      return null;
    }
    if(position + textToInsert.length() > length) {
      return null;
    }
    int[] codePoints = getCodePoints(text);
    for(int i=textToInsert.length()-1; i>=0; i--) {
      Integer codePointToInsert = getCodePoint(text, textToInsert.codePointAt(i), position + i);
      if(codePointToInsert == null) {
        return null;
      }
      codePoints[position + i] = codePointToInsert;
    }
    return new String(codePoints, 0, codePoints.length);
  }

  String removeText(String text, int position, int length) {
    int[] codePoints = getCodePoints(text);
    for(int i=position+length-1; i>=position; i--) {
      codePoints[i] = getDefaultCodePoint(i);
    }
    return new String(codePoints, 0, codePoints.length);
  }

}
