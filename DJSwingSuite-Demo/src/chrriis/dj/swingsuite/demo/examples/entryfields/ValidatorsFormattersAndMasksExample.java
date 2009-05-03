/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite.demo.examples.entryfields;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import chrriis.dj.swingsuite.JNumberEntryField;
import chrriis.dj.swingsuite.JTextEntryField;
import chrriis.dj.swingsuite.JTitledSeparator;
import chrriis.dj.swingsuite.SwingSuiteUtilities;
import chrriis.dj.swingsuite.TextEntryFieldAdapter;
import chrriis.dj.swingsuite.TextEntryFormatter;
import chrriis.dj.swingsuite.TextEntryMask;
import chrriis.dj.swingsuite.TextEntryValidator;

/**
 * @author Christopher Deckers
 */
public class ValidatorsFormattersAndMasksExample extends JPanel {

  public ValidatorsFormattersAndMasksExample() {
    super(new BorderLayout(0, 0));
    JPanel centerPane = new JPanel(new GridBagLayout());
    JLabel updateLabel;
    int y = 0;
    updateLabel = new JLabel("Committed value");
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
    Insets insets = new Insets(2, 5, 0, 5);
    Insets sectionInsets = new Insets(10, 5, 0, 5);
    // New section: validators.
    centerPane.add(new JTitledSeparator("Validators"), new GridBagConstraints(0, y++, 3, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
    // New section: formatters
    // A text field with a custom validator that requires the letters 'a' and 'b'.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Text field, custom validator (try 'm'):"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createTextEntryFieldWithCustomValidator(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // A text field with forbidden characters.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Text field, vowels forbidden:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createTextEntryFieldWithForbiddenChars(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // New section: formatters
    centerPane.add(new JTitledSeparator("Formatters"), new GridBagConstraints(0, y++, 3, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, sectionInsets, 0, 0));
    // A double field with maximum 2 decimals and a formatter that shows a nice number with a dollar sign in front.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Double field, 2 decimals, formatted:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createDoubleFieldWithDecimalsAndCustomFormatter(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // New section: masks
    centerPane.add(new JTitledSeparator("Masks"), new GridBagConstraints(0, y++, 3, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, sectionInsets, 0, 0));
    // A String field with a mask.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Pattern-based mask (#A-UL-?* '# H):"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createTextEntryFieldWithMask(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // A String field with a mask, and custom validator.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Mask (digits) and validator:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createTextEntryFieldWithMaskAndCustomValidator(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // A String field with a custom mask.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Mask ([123]-[456]-[789]):"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createTextEntryFieldWithCustomMask(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // New section: custom error display
    centerPane.add(new JTitledSeparator("Custom error display"), new GridBagConstraints(0, y++, 3, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, sectionInsets, 0, 0));
    // A text field with custom error display.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Number field, custom error display:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    final JLabel customErrorMessageDisplayLabel = new JLabel();
    centerPane.add(connectUpdateLabel(createNumberEntryFieldWithCustomErrorDisplay(customErrorMessageDisplayLabel), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    centerPane.add(customErrorMessageDisplayLabel, new GridBagConstraints(1, y++, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // Rest of the initialisation.
    add(new JScrollPane(centerPane), BorderLayout.CENTER);
  }

  private JTextEntryField createTextEntryFieldWithCustomValidator() {
    JTextEntryField textEntryField = new JTextEntryField("ab", 14);
    textEntryField.setValidator(new TextEntryValidator() {
      @Override
      public boolean isTextValid(JTextEntryField textEntryField, String text) {
        return text.indexOf('m') < 0 && text.indexOf('a') >= 0 && text.indexOf('b') >= 0;
      }
      @Override
      public String getDefaultValidText(JTextEntryField textEntryField) {
        return "ab";
      }
      @Override
      public String getInvalidTextErrorMessage(JTextEntryField textEntryField, String invalidText) {
        if(invalidText.indexOf('m') >= 0) {
          return "'m' not allowed.\nNote: this description is multiline!";
        }
        boolean isAMissing = invalidText.indexOf('a') < 0;
        boolean isBMissing = invalidText.indexOf('b') < 0;
        if(isAMissing && isBMissing) {
          return "Missing letters: 'a' and 'b'";
        }
        if(isAMissing) {
          return "Missing letter: 'a'";
        }
        if(isBMissing) {
          return "Missing letter: 'b'";
        }
        return null;
      }
    });
    return textEntryField;
  }

  private JTextEntryField createTextEntryFieldWithForbiddenChars() {
    JTextEntryField textEntryField = new JTextEntryField("qwrtp", 14);
    textEntryField.setValidator(new TextEntryValidator() {
      @Override
      public boolean isTextAllowed(JTextEntryField textEntryField, String text) {
        for(int i=text.length()-1; i>=0; i--) {
          switch(text.codePointAt(i)) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
            case 'y':
            case 'A':
            case 'E':
            case 'I':
            case 'O':
            case 'U':
            case 'Y':
              return false;
          }
        }
        return true;
      }
    });
    return textEntryField;
  }

  private JNumberEntryField<Double> createDoubleFieldWithDecimalsAndCustomFormatter() {
    JNumberEntryField<Double> formattedField = new JNumberEntryField<Double>(1000000000.0d, 14, 2);
    formattedField.setFormatter(new TextEntryFormatter() {
      public String getTextForDisplay(JTextEntryField textEntryField, String validText) {
        return "$ " + NumberFormat.getNumberInstance().format(((JNumberEntryField<?>)textEntryField).getNumber());
      }
    });
    return formattedField;
  }

  private JTextEntryField createTextEntryFieldWithMask() {
    return new JTextEntryField("", 14, "#A-UL-?* '# H");
  }

  private JTextEntryField createTextEntryFieldWithMaskAndCustomValidator() {
    JTextEntryField maskField = new JTextEntryField("13-57-93", 14, "##-##-##");
    maskField.setValidator(new TextEntryValidator() {
      @Override
      public boolean isTextValid(JTextEntryField textEntryField, String text) {
        for(int i=text.length()-1; i>=0; i--) {
          char c = text.charAt(i);
          if(c != '-' && c != '_' && ((c - '0') % 2 == 0)) {
            return false;
          }
        }
        return true;
      }
      @Override
      public String getInvalidTextErrorMessage(JTextEntryField textEntryField, String invalidText) {
        return "Digits must be odd";
      }
    });
    return maskField;
  }

  private JTextEntryField createTextEntryFieldWithCustomMask() {
    JTextEntryField maskField = new JTextEntryField("12-45-78", 14, new TextEntryMask() {
      @Override
      protected int getLength() {
        return 8;
      }
      @Override
      protected int getDefaultCodePoint(int position) {
        switch(position) {
          case 2:
          case 5:
            return '-';
        }
        return '?';
      }
      @Override
      protected Integer getCodePoint(String text, int codePoint, int position) {
        switch(position) {
          case 0:
          case 1:
            if(codePoint == '1' || codePoint == '2' || codePoint == '3' || codePoint == '?') {
              return codePoint;
            }
            return null;
          case 3:
          case 4:
            if(codePoint == '4' || codePoint == '5' || codePoint == '6' || codePoint == '?') {
              return codePoint;
            }
            return null;
          case 6:
          case 7:
            if(codePoint == '7' || codePoint == '8' || codePoint == '9' || codePoint == '?') {
              return codePoint;
            }
            return null;
        }
        return codePoint == '-'? codePoint: null;
      }
      @Override
      protected int getNextValidInputPosition(int position) {
        switch(position) {
          case 2: return 3;
          case 5: return 6;
        }
        return position;
      }
    });
    return maskField;
  }

  private JNumberEntryField<Double> createNumberEntryFieldWithCustomErrorDisplay(final JLabel customErrorMessageDisplayLabel) {
    customErrorMessageDisplayLabel.setText(" ");
    customErrorMessageDisplayLabel.setFont(customErrorMessageDisplayLabel.getFont().deriveFont(Font.BOLD));
    JNumberEntryField<Double> numberEntryField = new JNumberEntryField<Double>(0.12, 14, 2, -20.0, 50.0);
    numberEntryField.setTipDisplayedOnError(false);
    numberEntryField.addTextEntryFieldListener(new TextEntryFieldAdapter() {
      @Override
      public void errorMessageChanged(JTextEntryField validationField, String errorMessage) {
        if(errorMessage == null) {
          errorMessage = " ";
        } else {
          errorMessage = "ERROR -> " + errorMessage;
        }
        customErrorMessageDisplayLabel.setText(errorMessage);
      }
    });
    return numberEntryField;
  }

  private JTextEntryField connectUpdateLabel(JTextEntryField field, final JLabel updateLabel) {
    updateLabel.setPreferredSize(new Dimension(150, 0));
    updateLabel.setText(field.getValidText());
    field.addTextEntryFieldListener(new TextEntryFieldAdapter() {
      @Override
      public void textCommitted(JTextEntryField textEntryField) {
        updateLabel.setText(textEntryField.getValidText());
      }
    });
    return field;
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    SwingSuiteUtilities.setPreferredLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DJ Swing Suite Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ValidatorsFormattersAndMasksExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
