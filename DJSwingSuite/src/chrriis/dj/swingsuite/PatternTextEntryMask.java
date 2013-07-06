/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Deckers
 */
public class PatternTextEntryMask extends TextEntryMask {

  private enum CharType {
    DIGIT,
    LETTER_TO_UPPERCASE,
    LETTER_TO_LOWERCASE,
    LETTER_OR_DIGIT,
    HEX_CHAR,
    ANY_LETTER,
    ANY_CHAR,
    PREDEFINED_CHAR,
  }

  private CharType[] charTypes;
  private int[] chars;
  private int defaultChar;

  public PatternTextEntryMask(String pattern) {
    this(pattern, '_');
  }
  
  public PatternTextEntryMask(String pattern, int defaultChar) {
    if(pattern == null || pattern.length() == 0) {
      throw new IllegalArgumentException("The mask cannot be empty!");
    }
    this.defaultChar = defaultChar;
    List<CharType> charTypeList = new ArrayList<CharType>();
    List<Integer> charList = new ArrayList<Integer>();
    boolean isLastQuote = false;
    int predefinedCount = 0;
    for(int i=0; i<pattern.length(); i++) {
      int c = pattern.codePointAt(i);
      if(isLastQuote) {
        isLastQuote = false;
        charTypeList.add(CharType.PREDEFINED_CHAR);
        charList.add(c);
        predefinedCount++;
      } else {
        CharType type = null;
        switch(c) {
          case '#': type = CharType.DIGIT; break;
          case 'U': type = CharType.LETTER_TO_UPPERCASE; break;
          case 'L': type = CharType.LETTER_TO_LOWERCASE; break;
          case 'A': type = CharType.LETTER_OR_DIGIT; break;
          case 'H': type = CharType.HEX_CHAR; break;
          case '?': type = CharType.ANY_LETTER; break;
          case '*': type = CharType.ANY_CHAR; break;
          case '\'':
            isLastQuote = true;
            break;
          default:
            charTypeList.add(CharType.PREDEFINED_CHAR);
            charList.add(c);
            predefinedCount++;
            break;
        }
        if(type != null) {
          charTypeList.add(type);
          charList.add(defaultChar);
        }
      }
    }
    if(predefinedCount == charList.size()) {
      throw new IllegalArgumentException("The mask \"" + pattern + "\" must contain some editable characters!");
    }
    if(isLastQuote) {
      throw new IllegalArgumentException("The mask \"" + pattern + "\" contains a dangling quote!");
    }
    charTypes = charTypeList.toArray(new CharType[0]);
    chars = new int[charTypes.length];
    for(int i=0; i<chars.length; i++) {
      chars[i] = charList.get(i);
    }
  }

  @Override
  protected int getDefaultCodePoint(int position) {
    return chars[position];
  }

  @Override
  protected Integer getCodePoint(String text, int codePoint, int position) {
    CharType charType = charTypes[position];
    if(codePoint == defaultChar && charType != CharType.PREDEFINED_CHAR) {
      return codePoint;
    }
    switch(charTypes[position]) {
      case DIGIT:
        if(!Character.isDigit(codePoint)) {
          return null;
        }
        return codePoint;
      case LETTER_TO_UPPERCASE:
        if(!Character.isLetter(codePoint)) {
          return null;
        }
        return Character.toUpperCase(codePoint);
      case LETTER_TO_LOWERCASE:
        if(!Character.isLetter(codePoint)) {
          return null;
        }
        return Character.toLowerCase(codePoint);
      case LETTER_OR_DIGIT:
        if(!Character.isLetter(codePoint) && !Character.isDigit(codePoint)) {
          return null;
        }
        return codePoint;
      case HEX_CHAR:
        if(codePoint >= '0' && codePoint <= '9' || codePoint >= 'a' && codePoint <= 'f' || codePoint >= 'A' && codePoint <= 'F') {
          return codePoint;
        }
        return null;
      case ANY_LETTER:
        if(!Character.isLetter(codePoint)) {
          return null;
        }
        return codePoint;
      case ANY_CHAR:
        return codePoint;
      case PREDEFINED_CHAR:
        if(codePoint != chars[position]) {
          return null;
        }
        return codePoint;
    }
    // Impossible case
    return null;
  }

  @Override
  protected int getLength() {
    return chars.length;
  }

  @Override
  protected int getNextValidInputPosition(int position) {
    for(int i=position; i<charTypes.length; i++) {
      if(charTypes[i] != CharType.PREDEFINED_CHAR) {
        return i;
      }
    }
    return chars.length;
  }

}
