/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.dj.swingsuite;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JTextField;

/**
 * An entry field for number types (Byte, Short, Integer, Long, BigInteger, Float, Double, BigDecimal).
 * @author Christopher Deckers
 */
public class JNumberEntryField<T extends Number & Comparable<T>> extends JTextEntryField {

  private class NumberEntryFieldValueValidator extends TextEntryValidator {

    @Override
    public boolean isTextAllowed(JTextEntryField validationField, String text) {
      if(isNullAllowed && (text == null || "".equals(text))) {
        return true;
      }
      for(int i=text.length()-1; i>=0; i--) {
        int codePoint = text.codePointAt(i);
        if((codePoint != DECIMAL_SEPARATOR && codePoint != '.' || !numberEntryFieldType.hasDecimals()) && !Character.isDigit(codePoint) && (codePoint != '-' || i > 0)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public boolean isTextValid(JTextEntryField validationField, String text) {
      if(isNullAllowed && (text == null || "".equals(text))) {
        return true;
      }
      return getNumber(text) != null;
    }

    @Override
    public String getInvalidTextErrorMessage(JTextEntryField validationField, String invalidText) {
      if(isNullAllowed && (invalidText == null || "".equals(invalidText))) {
        return null;
      }
      T number = parseNumber(invalidText);
      if(number == null) {
        int decimalCount = 0;
        int length = invalidText.length();
        boolean isInvalid = length == 0;
        boolean hasDigit = false;
        for(int i=0; i<length; i++) {
          char c = invalidText.charAt(i);
          if(Character.isDigit(c)) {
            hasDigit = true;
          } else if(c != '-' || i != 0 || length == 1) {
            if(c != DECIMAL_SEPARATOR && c != '.') {
              isInvalid = true;
              break;
            }
            decimalCount++;
            if(!numberEntryFieldType.hasDecimals() || decimalCount > 1) {
              isInvalid = true;
              break;
            }
          }
        }
        if(isInvalid || !hasDigit) {
          return SwingSuiteUtilities.getUIManagerMessage("NumberEntryField.invalidFormatMessage", "Invalid format");
        }
      } else if(!isDecimalCountValid(invalidText)) {
        return SwingSuiteUtilities.getUIManagerMessage("NumberEntryField.maxDecimalsMessage", "Max decimals: {0}", decimalCount);
      }
      return SwingSuiteUtilities.getUIManagerMessage("NumberEntryField.rangeMessage", "Range: {0} .. {1}", rangeMin == null? "-\u221E": rangeMin, rangeMax == null? "+\u221E": rangeMax);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getDefaultValidText(JTextEntryField validationField) {
      Number zero = parseNumber("0");
      String s;
      if((rangeMin == null || ((Comparable)rangeMin).compareTo(zero) <= 0) && (rangeMax == null || ((Comparable)rangeMax).compareTo(zero) >= 0)) {
        s = "0";
      } else {
        s = rangeMin != null && ((Comparable)rangeMin).compareTo(zero) > 0? rangeMin.toString(): rangeMax.toString();
      }
      return s.replace('.', DECIMAL_SEPARATOR);
    }

  }

  private static enum NumberEntryFieldType {

    BYTE(Byte.class, false, Byte.MIN_VALUE, Byte.MAX_VALUE),
    SHORT(Short.class, false, Short.MIN_VALUE, Short.MAX_VALUE),
    INTEGER(Integer.class, false, Integer.MIN_VALUE, Integer.MAX_VALUE),
    BIG_INTEGER(BigInteger.class, false, null, null),
    LONG(Long.class, false, Long.MIN_VALUE, Long.MAX_VALUE),
    FLOAT(Float.class, true, null, null),
    DOUBLE(Double.class, true, null, null),
    BIG_DECIMAL(BigDecimal.class, true, null, null),

    ;

    private Class<? extends Number> numberClass;
    private boolean hasDecimals;
    private Number rangeMin;
    private Number rangeMax;

    private <T extends Number & Comparable<T>> NumberEntryFieldType(Class<T> numberClass, boolean hasDecimals, T rangeMin, T rangeMax) {
      this.numberClass = numberClass;
      this.hasDecimals = hasDecimals;
      this.rangeMin = rangeMin;
      this.rangeMax = rangeMax;
    }

    public Class<? extends Number> getNumberClass() {
      return numberClass;
    }

    public boolean hasDecimals() {
      return hasDecimals;
    }

    public Number getRangeMin() {
      return rangeMin;
    }

    public Number getRangeMax() {
      return rangeMax;
    }

    @Override
    public String toString() {
      return numberClass.getName();
    }

    public Number parseNumber(String text) {
      if(text == null || "".equals(text)) {
        return null;
      }
      try {
        switch(this) {
          case BYTE: return Byte.valueOf(text);
          case SHORT: return Short.valueOf(text);
          case INTEGER: return Integer.valueOf(text);
          case BIG_INTEGER: return new BigInteger(text);
          case LONG: return Long.valueOf(text);
          case FLOAT: return Float.valueOf(text);
          case DOUBLE: return Double.valueOf(text);
          case BIG_DECIMAL: return new BigDecimal(text);
        }
      } catch(Exception e) {
      }
      return null;
    }

    public String formatNumber(Number number) {
      String s = number.toString();
      boolean isNegative = s.startsWith("-");
      if(isNegative) {
        s = s.substring(1);
      }
      int index = s.indexOf('E');
      if(index >= 0) {
        int exponent = Integer.parseInt(s.substring(index + 1));
        String root = s.substring(0, 1) + s.substring(2, index);
        // Adjust dot position
        if(exponent >= 0) {
          int length = root.length();
          if(exponent < length - 1) {
            s = root.substring(0, exponent + 1) + '.' + root.substring(exponent + 1);
          } else if(exponent >= length) {
            char[] zeros = new char[exponent + 1 - length];
            Arrays.fill(zeros, '0');
            s = root + new String(zeros);
          } else {
            s = root;
          }
        } else {
          char[] zeros = new char[-exponent - 1];
          Arrays.fill(zeros, '0');
          s = "0." + new String(zeros) + root;
          if(s.endsWith("0")) {
            s = s.substring(0, s.length() - 1);
          }
        }
      }
      if(s.endsWith(".0")) {
        s = s.substring(0, s.length() - 2);
      }
      String value = s.replace('.', DECIMAL_SEPARATOR);
      if(isNegative) {
        value = "-" + value;
      }
      return value;
    }

  }

  private boolean isNullAllowed;
  private T rangeMin;
  private T rangeMax;
  private NumberEntryFieldType numberEntryFieldType;
  private int decimalCount = -1;

  private static final char DECIMAL_SEPARATOR;
  
  static {
    String s = System.getProperty("swingsuite.decimalSeparator");
    if(s != null && s.length() == 1) {
      DECIMAL_SEPARATOR = s.charAt(0);
    } else {
      DECIMAL_SEPARATOR = new DecimalFormatSymbols(Locale.getDefault()).getDecimalSeparator();
    }
  }

  /**
   * Check that the number is OK and returns the number.
   * @param text  the current text
   * @return The number corresponding to the text, or null if not valid or if null is allowed.
   */
  private T getNumber(String text) {
    T number = parseNumber(text);
    if(number == null) {
      return null;
    }
    if(!isRangeValid(number)) {
      return null;
    }
    if(!isDecimalCountValid(text)) {
      return null;
    }
    return number;
  }

  @SuppressWarnings("unchecked")
  private T parseNumber(String text) {
    return (T)numberEntryFieldType.parseNumber(text.replace(DECIMAL_SEPARATOR, '.'));
  }

  public void replaceSelection(String content) {
    // This method is invoked from clipboard paste, and clipboard data may contain trailing spaces or new lines which we have to ignore to permit pasting.
    if(content != null) {
      content = content.trim();
    }
    super.replaceSelection(content);
  }
  
  private boolean isRangeValid(T number) {
    return (rangeMin == null || number.compareTo(rangeMin) >= 0) && (rangeMax == null || number.compareTo(rangeMax) <= 0);
  }

  private boolean isDecimalCountValid(String text) {
    if(decimalCount >= 0) {
      int separatorPosition = text.indexOf(DECIMAL_SEPARATOR);
      if(separatorPosition < 0) {
        separatorPosition = text.indexOf('.');
      }
      if(separatorPosition >= 0 && text.length() - separatorPosition - 1 > decimalCount) {
        return false;
      }
    }
    return true;
  }

  /**
   * Construct a number entry field.
   * @param number the default number.
   */
  public JNumberEntryField(T number) {
    this(number, 0);
  }

  /**
   * Construct a number entry field.
   * @param number the default number.
   * @param rangeMin the minimum number authorized by the range.
   * @param rangeMax the maximum number authorized by the range.
   */
  public JNumberEntryField(T number, T rangeMin, T rangeMax) {
    this(number, rangeMin, rangeMax, false);
  }
  
  /**
   * Construct a number entry field.
   * @param number the default number.
   * @param rangeMin the minimum number authorized by the range.
   * @param rangeMax the maximum number authorized by the range.
   * @param isNullAllowed true if null is allowed, false otherwise.
   */
  public JNumberEntryField(T number, T rangeMin, T rangeMax, boolean isNullAllowed) {
    this(number, 0, rangeMin, rangeMax, isNullAllowed);
  }

  /**
   * Construct a number entry field.
   * @param number the default number.
   * @param columns The number of columns used to calculate the preferred width, or zero for the default size calculation.
   */
  public JNumberEntryField(T number, int columns) {
    this(number, columns, -1);
  }

  /**
   * Construct a number entry field.
   * @param number the default number.
   * @param columns The number of columns used to calculate the preferred width, or zero for the default size calculation.
   * @param decimalCount the number of decimals allowed.
   */
  public JNumberEntryField(T number, int columns, int decimalCount) {
    this(number, columns, decimalCount, null, null);
  }

  /**
   * Construct a number entry field.
   * @param number the default number.
   * @param columns The number of columns used to calculate the preferred width, or zero for the default size calculation.
   * @param rangeMin the minimum number authorized by the range.
   * @param rangeMax the maximum number authorized by the range.
   */
  public JNumberEntryField(T number, int columns, T rangeMin, T rangeMax) {
    this(number, columns, rangeMin, rangeMax, false);
  }
  
  /**
   * Construct a number entry field.
   * @param number the default number.
   * @param columns The number of columns used to calculate the preferred width, or zero for the default size calculation.
   * @param rangeMin the minimum number authorized by the range.
   * @param rangeMax the maximum number authorized by the range.
   * @param isNullAllowed true if null is allowed, false otherwise.
   */
  public JNumberEntryField(T number, int columns, T rangeMin, T rangeMax, boolean isNullAllowed) {
    this(number, columns, -1, rangeMin, rangeMax, isNullAllowed);
  }

  /**
   * Construct a number entry field.
   * @param number the default number.
   * @param columns The number of columns used to calculate the preferred width, or zero for the default size calculation.
   * @param decimalCount the number of decimals allowed.
   * @param rangeMin the minimum number authorized by the range.
   * @param rangeMax the maximum number authorized by the range.
   */
  public JNumberEntryField(T number, int columns, int decimalCount, T rangeMin, T rangeMax) {
    this(number, columns, decimalCount, rangeMin, rangeMax, false);
  }
  
  /**
   * Construct a number entry field.
   * @param number the default number.
   * @param columns The number of columns used to calculate the preferred width, or zero for the default size calculation.
   * @param decimalCount the number of decimals allowed.
   * @param rangeMin the minimum number authorized by the range.
   * @param rangeMax the maximum number authorized by the range.
   * @param isNullAllowed true if null is allowed, false otherwise.
   */
  public JNumberEntryField(T number, int columns, int decimalCount, T rangeMin, T rangeMax, boolean isNullAllowed) {
    super(columns);
    setHorizontalAlignment(JTextField.TRAILING);
    setFieldType(number);
    setText("0");
    setValidator(null);
    setDecimalCount(decimalCount);
    setRange(rangeMin, rangeMax);
    setNullAllowed(isNullAllowed);
    setNumber(number);
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        processKey(e);
      }
      @Override
      public void keyReleased(KeyEvent e) {
        processKey(e);
      }
      @Override
      public void keyTyped(KeyEvent e) {
        processKey(e);
      }
      private void processKey(KeyEvent e) {
        if(!isEditable() || !isEnabled()) {
          return;
        }
        Integer factor = null;
        boolean isDivision = false;
        switch(e.getKeyChar()) {
          case 'k': factor = 1000; break;
          case 'K': factor = 1000; isDivision = true; break;
          case 'm': factor = 1000000; break;
          case 'M': factor = 1000000; isDivision = true; break;
        }
        if(factor != null) {
          if(e.getID() == KeyEvent.KEY_PRESSED) {
            T number = parseNumber(getText());
            if(number != null) {
              double d = number.doubleValue();
              if(!Double.isInfinite(d) && !Double.isNaN(d)) {
                if(isDivision) {
                  d = d / factor;
                } else {
                  d = d * factor;
                }
                String formattedNumber = numberEntryFieldType.formatNumber(d);
                setText(formattedNumber);
              }
            }
          }
          e.consume();
          setCaretPosition(getText().length());
        }
      }
    });
  }

  /**
   * Set the text validator.
   * @param validator The new number validator, or null to install the default number validator.
   */
  @Override
  public void setValidator(TextEntryValidator validator) {
    if(validator == null) {
      validator = new NumberEntryFieldValueValidator();
    }
    super.setValidator(validator);
  }

  private void setFieldType(T number) {
    if(number == null) {
      throw new IllegalArgumentException("The number cannot be null!");
    }
    for(NumberEntryFieldType type: NumberEntryFieldType.values()) {
      Class<? extends Number> numberClass = number.getClass();
      if(type.getNumberClass().isAssignableFrom(numberClass)) {
        numberEntryFieldType = type;
        return;
      }
    }
    throw new IllegalArgumentException("The type \"" + number.getClass().getName() + "\" is not supported! It must be one of " + Arrays.asList(NumberEntryFieldType.values()));
  }

  /**
   * Set the range of this field.
   * @param rangeMin the minimum number authorized by the range.
   * @param rangeMax the maximum number authorized by the range.
   */
  @SuppressWarnings("unchecked")
  public void setRange(T rangeMin, T rangeMax) {
    this.rangeMin = rangeMin == null? (T)numberEntryFieldType.getRangeMin(): rangeMin;
    this.rangeMax = rangeMin == null? (T)numberEntryFieldType.getRangeMax(): rangeMax;
    if(rangeMin != null && rangeMax != null && this.rangeMin.compareTo(this.rangeMax) > 0) {
      throw new IllegalArgumentException("Maximum range value must be greater than minimum value.");
    }
    revalidateText();
  }

  /**
   * Set whether this range accepts null.
   * @param isNullAllowed true if null is allowed, false otherwise.
   */
  @SuppressWarnings("unchecked")
  public void setNullAllowed(boolean isNullAllowed) {
    this.isNullAllowed = isNullAllowed;
    revalidateText();
  }
  
  public boolean isNullAllowed() {
    return isNullAllowed;
  }
  
  /**
   * Set the number of decimals, which has an effect only if the number type accepts decimals.
   * @param decimalCount the number of decimals allowed, or a negative value to remove any limitation.
   */
  public void setDecimalCount(int decimalCount) {
    this.decimalCount = decimalCount;
    revalidateText();
  }

  /**
   * Get the number of decimals.
   * @return the number of decimals, or a negative value if it is not set.
   */
  public int getDecimalCount() {
    return decimalCount;
  }

  /**
   * Get a valid number, which is the current number if it is valid or the last valid one.
   * @return a valid number.
   */
  public T getNumber() {
    return getNumber(getValidText());
  }

  /**
   * Set some number, which only works when the number to set is valid.
   * @param number The number to set.
   */
  public void setNumber(T number) {
    if(isNullAllowed && number == null) {
      setText("");
    } else {
      setText(numberEntryFieldType.formatNumber(number));
    }
  }

}
