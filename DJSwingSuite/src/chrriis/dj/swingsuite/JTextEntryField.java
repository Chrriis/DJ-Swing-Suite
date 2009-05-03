/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * A text entry field, which supports validation, formatting and masks.
 * @author Christopher Deckers
 */
public class JTextEntryField extends JTextField {

  private class ValidationDocument extends PlainDocument {

    private boolean isInserting;
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      if(isInserting) {
        super.insertString(offs, str, a);
        return;
      }
      isInserting = true;
      try {
        boolean isInsertingInMask = false;
        if(textMask != null) {
          String text = getText_();
          if(text.length() > 0) {
            int caretPosition = getCaretPosition();
            if(offs == caretPosition) {
              offs = textMask.getNextValidInputPosition(caretPosition);
            }
            if(textMask.insertText(text, str, offs) == null) {
              setCaretPosition(textMask.getNextValidInputPosition(getCaretPosition()));
              return;
            }
            isInsertingInMask = true;
          }
        }
        String oldText = getText_();
        String result = oldText.substring(0, offs) + str + oldText.substring(offs);
        if(!isTextAllowed(result)) {
          return;
        }
        if(maximumLength < 0 || isValueForDisplayShown) {
          super.insertString(offs, str, a);
          if(isInsertingInMask) {
            int addedLength = str.length();
            String text = getText_();
            text = text.substring(0, offs + addedLength) + text.substring(offs + addedLength * 2);
            text = textMask.insertText(text, text, 0);
            setText_(text);
            setCaretPosition(textMask.getNextValidInputPosition(offs + addedLength));
          }
          return;
        }
        int oldCaretPosition = getCaretPosition();
        super.insertString(offs, str, a);
        String text = getText_();
        if(text.length() > maximumLength) {
          setText_(oldText);
          setCaretPosition(oldCaretPosition);
          showPopup(true);
          return;
        }
      } finally {
        isInserting = false;
      }
    }
    @Override
    public void remove(int offs, int len) throws BadLocationException {
      if(isInserting) {
        super.remove(offs, len);
        return;
      }
      String text = getText_();
      boolean isRemovingInMask = false;
      if(textMask != null) {
        text = textMask.removeText(text, offs, len);
        if(text == null) {
          return;
        }
        isRemovingInMask = true;
      }
      if(isRemovingInMask) {
        isInserting = true;
        try {
          setText_(text);
        } finally {
          isInserting = false;
        }
        setCaretPosition(offs);
      } else {
        super.remove(offs, len);
      }
    }
    @Override
    public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
      if(!isInserting) {
        String oldText = getText_();
        if(length > 0) {
          String newText = oldText.substring(0, offset) + text + oldText.substring(Math.min(oldText.length(), offset + text.length()));
          if(!isTextAllowed(newText)) {
            return;
          }
          if(oldText.length() > 0 && textMask != null) {
            if(!textMask.isTextValid(newText)) {
              return;
            }
          }
        }
      }
      super.replace(offset, length, text, attrs);
    }
  }

  private JPopupMenu popup;
  private String lastValidText = "";
  private int maximumLength = -1;
  private boolean isFocused;

  /**
   * Construct a text entry field.
   * @param text the default text.
   */
  public JTextEntryField(String text) {
    this(text, 0);
  }

  /**
   * Construct a text entry field.
   * @param text the default text.
   * @param columns The number of columns used to calculate the preferred width, or zero for the default size calculation.
   */
  public JTextEntryField(String text, int columns) {
    this(text, columns, -1);
  }

  /**
   * Construct a text entry field.
   * @param text the default text.
   * @param columns The number of columns used to calculate the preferred width, or zero for the default size calculation.
   * @param maxLength the maximum length of the text.
   */
  public JTextEntryField(String text, int columns, int maxLength) {
    this(columns);
    setText(text);
    setMaximumLength(maxLength);
  }

  /**
   * Construct a text entry field.
   * @param text the default text.
   * @param mask the mask to apply when entering the text.
   */
  public JTextEntryField(String text, String mask) {
    this(text, 0, mask);
  }

  /**
   * Construct a text entry field with input restricted by a pattern-based mask. The pattern uses these special tokens:<br>
   * # = Character.isDigit.<br>
   * U = Character.isLetter mapped to uppercase.<br>
   * L = Character.isLetter mapped to lowercase.<br>
   * A = Character.isLetter  or Character.isDigit.<br>
   * ? = Character.isLetter.<br>
   * * = Any character.<br>
   * H = Any hex character (0-9, a-f or A-F).<br>
   * ' = Escape any of the special formatting tokens.
   * @param text the default text.
   * @param columns The number of columns used to calculate the preferred width, or zero for the default size calculation.
   * @param pattern the pattern of the mask to apply when entering the text.
   */
  public JTextEntryField(String text, int columns, String pattern) {
    this(columns, new PatternTextEntryMask(pattern));
    setText(text);
  }

  /**
   * Construct a text entry field.
   * @param text the default text.
   * @param columns The number of columns used to calculate the preferred width, or zero for the default size calculation.
   * @param mask the mask to apply when entering the text.
   */
  public JTextEntryField(String text, int columns, TextEntryMask mask) {
    this(columns, mask);
    setText(text);
  }

  /**
   * Construct a text entry field.
   * @param columns The number of columns used to calculate the preferred width, or zero for the default size calculation.
   */
  protected JTextEntryField(int columns) {
    init(columns);
  }

  private TextEntryMask textMask;

  /**
   * Construct a text entry field with a mask.
   * @param columns The number of columns used to calculate the preferred width, or zero for the default size calculation.
   */
  protected JTextEntryField(int columns, TextEntryMask textMask) {
    if(textMask != null) {
      this.textMask = textMask;
      setText_(textMask.getDefaultText());
    }
    init(columns);
  }

  private void adjustFont() {
    if(textMask != null) {
      Font font = UIManager.getFont("FormattedTextField.font");
      if(font == null) {
        font = new JFormattedTextField().getFont();
      }
      setFont(font);
      return;
    }
    Font font = UIManager.getFont("TextField.font");
    if(font == null) {
      font = new JTextField().getFont();
    }
    setFont(font);
  }

  @Override
  public void updateUI() {
    super.updateUI();
    adjustFont();
  }

  private void init(int columns) {
    adjustFont();
    setColumns(columns);
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          boolean isPopupVisible = isPopupVisible();
          boolean isConsuming = isPopupVisible || !getText_().equals(lastValidText);
          hidePopup();
          boolean isTextValid = isTextValid(getText_());
          if(!isPopupVisible || !isTextValid) {
            setText(lastValidText);
          }
          if(isConsuming) {
            e.consume();
          }
          selectAll();
          if(!isPopupVisible && isTextValid) {
            exit();
          }
        }
      }
    });
    addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        isFocused = true;
        addFocusHandler();
        if(!e.isTemporary()) {
          if(isValueForDisplayShown) {
            adjustValueForDisplay();
          }
          if(!isTextValid(getText_())) {
            showPopup(false);
          }
        }
      }
      public void focusLost(FocusEvent e) {
        isFocused = false;
        if(!e.isTemporary()) {
          boolean isTextValid = isTextValid(getText_());
          if(isFocusTrappedOnInvalidText && !isTextValid) {
            requestFocus();
            return;
          }
          if(!isTextValid) {
            setText(lastValidText);
          }
          hidePopup();
          validateText();
          adjustValueForDisplay();
        } else {
          isSelecting = false;
          requestFocus();
        }
      }
    });
    setDocument(new ValidationDocument());
    getDocument().addDocumentListener(new DocumentListener() {
      /** Gives notification that an attribute or set of attributes changed. */
      public void changedUpdate(DocumentEvent e) {}
      /** Gives notification that there was an insert into the document. */
      public void insertUpdate(DocumentEvent e) {
        adjustPopupVisibility();
      }
      /** Gives notification that a portion of the document has been removed. */
      public void removeUpdate(DocumentEvent e)  {
        adjustPopupVisibility();
      }
    });
    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exit();
      }
    });
    setSelectingAllOnFocus(true);
  }

  private boolean isFocusTrappedOnInvalidText;

  /**
   * Set whether the focus is trapped or if it can escape the field when the text is invalid.
   * @param isFocusTrappedOnInvalidText true if the focus is trapped, false otherwise.
   */
  public void setFocusTrappedOnInvalidText(boolean isFocusTrappedOnInvalidText) {
    this.isFocusTrappedOnInvalidText = isFocusTrappedOnInvalidText;
  }

  /**
   * Indicate wether the focus is trapped or if it can escape the field when the text is invalid.
   * @return trus if the focus is trapped, false otherwise.
   */
  public boolean isFocusTrappedOnInvalidText() {
    return isFocusTrappedOnInvalidText;
  }

  private void addFocusHandler() {
    try {
      Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
        public void eventDispatched(AWTEvent e) {
          if(e.getID() == FocusEvent.FOCUS_LOST) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
            return;
          }
          if(isFocusTrappedOnInvalidText) {
            return;
          }
          if(e.getID() == MouseEvent.MOUSE_PRESSED) {
            MouseEvent me = (MouseEvent)e;
            if(me.getComponent() != JTextEntryField.this && hasFocus()) {
              exit();
            }
          }
        }
      }, FocusEvent.FOCUS_EVENT_MASK | MouseEvent.MOUSE_EVENT_MASK);
    } catch(Exception e) {
      // We swallow potential security exceptions.
    }
  }

  private class FocusPanel extends JPanel {
    public FocusPanel() {
      setSize(0, 0);
      setFocusable(true);
      setOpaque(false);
      enableEvents(KeyEvent.KEY_EVENT_MASK);
      addFocusListener(new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
          if(!e.isTemporary()) {
            destroy();
          }
        }
      });
    }
    @Override
    protected void processKeyEvent(KeyEvent e) {
      if(e.getID() != KeyEvent.KEY_PRESSED || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
        return;
      }
      if(e.getKeyCode() == KeyEvent.VK_F2 || e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
        JTextEntryField.this.requestFocus();
      }
    }
    @Override
    public void transferFocus() {
      destroy();
      JTextEntryField.this.transferFocus();
    }
    @Override
    public void transferFocusBackward() {
      destroy();
      JTextEntryField.this.transferFocusBackward();
    }
    private void destroy() {
      JTextEntryField.this.remove(this);
      JTextEntryField.this.revalidate();
      JTextEntryField.this.repaint();
    }
  }

  private void exit() {
    String textForDisplay = getTextForDisplay();
    if(textForDisplay != null && !getValidText().equals(textForDisplay)) {
      JPanel p = new FocusPanel();
      add(p);
      revalidate();
      repaint();
      p.requestFocus();
    }
    validateText();
  }

  @Override
  public void setDocument(Document doc) {
    if(getDocument() instanceof ValidationDocument) {
      throw new IllegalStateException("The document cannot be set on a text entry field!");
    }
    super.setDocument(doc);
  }

  private String getText_() {
    return getText();
  }

  private void adjustPopupVisibility() {
    String text = getText_();
    if(isTextValid(text)) {
      hidePopup();
    } else {
      showPopup(maximumLength > 0 && text.length() > maximumLength);
    }
  }

  private boolean isPopupVisible() {
    return popup != null && popup.isVisible();
  }

  private void hidePopup() {
    if(lastDisplayedMessage != null && isTextValid(getText_())) {
      lastDisplayedMessage = null;
      fireErrorMessageChangedEvent(null);
    }
    lastMessage = null;
    if(popup != null) {
      if(popup.isVisible()) {
        popup.setVisible(false);
      }
      popup = null;
    }
  }

  private String lastDisplayedMessage;
  private String lastMessage;
  private boolean preventPopupRespawn;

  private void showPopup(final boolean isMaxLengthPopup) {
    preventPopupRespawn = false;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if(isShowing()) {
          if(!hasFocus()) {
            return;
          }
          String text = getText_();
          boolean isTextValid = isTextValid(text);
          String message = !isTextValid? getInvalidTextErrorMessage(text): getMaximumLengthValidationErrorMessage();
          if(!isTextValid || isMaxLengthPopup) {
            if(!message.equals(lastDisplayedMessage)) {
              lastDisplayedMessage = message;
              fireErrorMessageChangedEvent(lastDisplayedMessage);
            }
          }
          if(!isTipDisplayedOnError() || message.equals(lastMessage)) {
            return;
          }
          hidePopup();
          if(isTextValid && !isMaxLengthPopup) {
            return;
          }
          lastMessage = message;
          final AWTEventListener mouseListener = new AWTEventListener() {
            public void eventDispatched(AWTEvent e) {
              if(e.getID() == MouseEvent.MOUSE_PRESSED) {
                if(((MouseEvent)e).getComponent() == JTextEntryField.this) {
                  preventPopupRespawn = true;
                  hidePopup();
                  requestFocus();
                }
              }
            }
          };
          popup = new JPopupMenu() {
            @Override
            public void setVisible(boolean isVisible) {
              super.setVisible(isVisible);
              if(!isVisible) {
                try {
                  Toolkit.getDefaultToolkit().removeAWTEventListener(mouseListener);
                } catch(Exception e) {
                }
                if(popup == this) {
                  hidePopup();
                }
                SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                    if(popup == null && !isTextValid(getText_())) {
                      if(JTextEntryField.this.hasFocus() && isFocusTrappedOnInvalidText) {
                        if(!preventPopupRespawn) {
                          showPopup(false);
                        }
                      } else {
                        setText(lastValidText);
                        selectAll();
                      }
                    }
                  }
                });
              }
            }
          };
          try {
            Toolkit.getDefaultToolkit().addAWTEventListener(mouseListener, MouseEvent.MOUSE_EVENT_MASK);
          } catch(Exception e) {
            preventPopupRespawn = true;
          }
          popup.setFocusable(false);
          popup.setLayout(new BoxLayout(popup, BoxLayout.Y_AXIS));
          String[] lines = message.split("\n");
          for(String line: lines) {
            JLabel popupLabel = new JLabel(" " + line + " ");
            popupLabel.setForeground(Color.RED);
            popup.add(popupLabel);
          }
          boolean isLeftToRight = getComponentOrientation().isLeftToRight();
          boolean isLeft = true;
          switch(getHorizontalAlignment()) {
            case RIGHT:
              isLeft = false;
              break;
            case LEADING:
              if(!isLeftToRight) {
                isLeft = false;
              }
              break;
            case TRAILING:
              if(isLeftToRight) {
                isLeft = false;
              }
              break;
          }
          int sizeDiff = getWidth() - popup.getPreferredSize().width;
          if(sizeDiff < 0) {
            isLeft = !isLeft;
          }
          int x;
          if(isLeft) {
            x = 0;
          } else {
            x = Math.max(0, sizeDiff);
          }
          popup.show(JTextEntryField.this, x, getHeight());
        }
      }
    });
  }

  /**
   * Set the maximum text length.
   * @param maximumLength The maximum length, or a negative value if there is no limit.
   */
  public void setMaximumLength(int maximumLength) {
    this.maximumLength = maximumLength;
    revalidateText();
  }

  /**
   * Try to set a valid text in the field.
   */
  protected void revalidateText() {
    setText(getValidText());
  }

  /**
   * Get the maximum length, or a negative value if there is no maximum.
   * @return the maximum length, or a negative value.
   */
  public int getMaximumLength() {
    return maximumLength;
  }

  /**
   * Get a default text that is valid within the current set of validation constraints.<br>
   * This method is invoked by the validation mechanism so it needs to be properly implemented by custom classes that add custom validation behaviors.
   * @return A valid default text.
   */
  private String getDefaultValidText() {
    String text = validator == null? "": validator.getDefaultValidText(this);
    if(textMask != null && !textMask.isTextValid(text)) {
      return textMask.getDefaultText();
    }
    return text;
  }

  private TextEntryValidator validator;

  /**
   * Get the validator to use when data is to be validated.
   * @return the text validator.
   */
  public TextEntryValidator getValidator() {
    return validator;
  }

  /**
   * Set the text validator.
   * @param validator The text validator, or null if there is no validation to perform.
   */
  public void setValidator(TextEntryValidator validator) {
    this.validator = validator;
    revalidateText();
  }

  private String getInvalidTextErrorMessage(String invalidText) {
    if(validator != null) {
      String message = validator.getInvalidTextErrorMessage(this, invalidText);
      if(message != null) {
        return message;
      }
    }
    return SwingSuiteUtilities.getUIManagerMessage("TextEntryField.invalidInputMessage", "Invalid input");
  }

  /**
   * Get the message to show when the maximum length is reached.
   * This can be overriden by subclasses to replace the default message.
   * @return the message.
   */
  private String getMaximumLengthValidationErrorMessage() {
    return SwingSuiteUtilities.getUIManagerMessage("TextEntryField.maxTextLengthMessage", "Max length: {0}", maximumLength);
  }

  private TextEntryFormatter formatter;

  /**
   * Set the formatter for the text when the component does not have the focus.
   * @param formatter The new formatter, or null to remove any formatting.
   */
  public void setFormatter(TextEntryFormatter formatter) {
    this.formatter = formatter;
    revalidateText();
  }

  /**
   * Get the formatter for the text when the component does not have the focus.
   * @return The formatter, or null if there is no formatting.
   */
  public TextEntryFormatter getDisplayFormatter() {
    return formatter;
  }

  private String getTextForDisplay() {
    return formatter == null? null: formatter.getTextForDisplay(this, getText_());
  }

  private boolean isTipDisplayedOnError = true;

  /**
   * Set if the tip is displayed on error.
   * @param isTipDisplayedOnError  indicate if the tip must be displayed on error
   */
  public void setTipDisplayedOnError(boolean isTipDisplayedOnError) {
    this.isTipDisplayedOnError = isTipDisplayedOnError;
    if(!isTipDisplayedOnError) {
      hidePopup();
    }
  }

  /**
   * Indicate if the tip is displayed on error.
   * @return True if the tip is displayed on error
   */
  public boolean isTipDisplayedOnError() {
    return isTipDisplayedOnError;
  }

  private void validateText() {
    if(isValueForDisplayShown) {
      return;
    }
    String text = getText_();
    if(!isTextValid(text)) {
      return;
    }
    if(!lastValidText.equals(text)) {
      lastValidText = text;
      fireTextCommittedEvent();
    }
  }

  /**
   * Get a valid text, which is the current text if it is valid or the last valid one.
   * @return a valid text.
   */
  public String getValidText() {
    if(isValueForDisplayShown) {
      return lastValidText;
    }
    String text = getText_();
    if(isTextValid(text)) {
      return text;
    }
    return lastValidText;
  }

  private boolean isTextAllowed(String text) {
    if(isValueForDisplayShown) {
      return true;
    }
    if(validator != null) {
      return validator.isTextAllowed(this, text);
    }
    return true;
  }

  private boolean isTextValid(String text) {
    if(maximumLength >= 0 && text.length() > maximumLength) {
      return false;
    }
    if(textMask != null && !textMask.isTextValid(text)) {
      return false;
    }
    if(validator != null) {
      return validator.isTextValid(this, text);
    }
    return true;
  }

  private boolean isValueForDisplayShown;

  private void adjustValueForDisplay() {
    boolean isValueForDisplayShown = !isFocused;
    if(this.isValueForDisplayShown == isValueForDisplayShown) {
      return;
    }
    String textToDisplay;
    if(isValueForDisplayShown) {
      String valueForDisplay = getTextForDisplay();
      if(valueForDisplay == null) {
        return;
      }
      textToDisplay = valueForDisplay;
    } else {
      textToDisplay = lastValidText;
    }
    if(isValueForDisplayShown) {
      this.isValueForDisplayShown = isValueForDisplayShown;
    }
    if(getText_().equals(textToDisplay)) {
      this.isValueForDisplayShown = isValueForDisplayShown;
      return;
    }
    setText_(textToDisplay);
    if(!isValueForDisplayShown) {
      this.isValueForDisplayShown = isValueForDisplayShown;
    }
  }

  private void setText_(String text) {
    super.setText(text);
  }

  /**
   * Set some text, which only works when the text to set is valid.
   * @param text The text to set.
   */
  @Override
  public void setText(String text) {
    String validText;
    if(isTextValid(text)) {
      validText = text;
    } else if(isTextValid(lastValidText)) {
      validText = lastValidText;
    } else {
      validText = getDefaultValidText();
    }
    setText_(validText);
    isValueForDisplayShown = false;
    validateText();
    adjustValueForDisplay();
  }

  private boolean isSelecting = true;

  @Override
  public void selectAll() {
    if(isSelecting) {
      super.selectAll();
    }
    isSelecting = true;
  }

  private boolean isSelectingAllOnFocus;

  /**
   * Set whether this field selects all of its text when it acquires the focus.
   * @param isSelectingAllOnFocus true if the text should be selected when focus is acquired, false otherwise.
   */
  public void setSelectingAllOnFocus(boolean isSelectingAllOnFocus) {
    this.isSelectingAllOnFocus = isSelectingAllOnFocus;
    SwingSuiteUtilities.setSelectingAllOnFocus(this, isSelectingAllOnFocus);
  }

  /**
   * Indicate whether this field selects all of its text when it acquires the focus.
   * @return true if the text should be selected when focus is acquired, false otherwise.
   */
  public boolean isSelectingAllOnFocus() {
    return isSelectingAllOnFocus;
  }

  private void fireTextCommittedEvent() {
    for(TextEntryFieldListener listener: getTextEntryFieldListeners()) {
      listener.textCommitted(this);
    }
  }

  private void fireErrorMessageChangedEvent(String errorMessage) {
    for(TextEntryFieldListener listener: getTextEntryFieldListeners()) {
      listener.errorMessageChanged(this, errorMessage);
    }
  }

  /**
   * Add a text entry listener.
   * @param textEntryFieldListener the listener to add.
   */
  public void addTextEntryFieldListener(TextEntryFieldListener textEntryFieldListener) {
    listenerList.add(TextEntryFieldListener.class, textEntryFieldListener);
  }

  /**
   * Remove a text entry field listener.
   * @param textEntryFieldListener the listener to remove.
   */
  public void removeTextEntryFieldListener(TextEntryFieldListener textEntryFieldListener) {
    listenerList.remove(TextEntryFieldListener.class, textEntryFieldListener);
  }

  /**
   * Get the text entry field listeners that are registered.
   * @return the registered text entry field listeners.
   */
  public TextEntryFieldListener[] getTextEntryFieldListeners() {
    return listenerList.getListeners(TextEntryFieldListener.class);
  }

}
