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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import chrriis.dj.swingsuite.JNumberEntryField;
import chrriis.dj.swingsuite.JTextEntryField;
import chrriis.dj.swingsuite.JTitledSeparator;
import chrriis.dj.swingsuite.SwingSuiteUtils;
import chrriis.dj.swingsuite.TextEntryFieldListener;

/**
 * @author Christopher Deckers
 */
public class TextAndNumberFieldsExample extends JPanel {

  public TextAndNumberFieldsExample() {
    super(new BorderLayout(0, 0));
    JPanel centerPane = new JPanel(new GridBagLayout());
    JLabel updateLabel;
    int y = 0;
    updateLabel = new JLabel("Committed value");
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
    Insets insets = new Insets(2, 5, 0, 5);
    Insets sectionInsets = new Insets(10, 5, 0, 5);
    // New section: Text
    centerPane.add(new JTitledSeparator("Text"), new GridBagConstraints(0, y++, 3, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
    // A default text field.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Text field:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createDefaultTextEntryField(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // A text field with a maximum length of 4 chars.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Text field, max 4 characters:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createTextEntryFieldWithMaxLength(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // New section: Numbers
    centerPane.add(new JTitledSeparator("Numbers (Byte, Short, Integer, Long, BigInteger, Float, Double, BigDecimal)"), new GridBagConstraints(0, y++, 3, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, sectionInsets, 0, 0));
    // A default integer field.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Integer field:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createDefaultIntegerField(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // An integer field with a range [-20;50].
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Integer field, [-20;50]:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createIntegerFieldWithRange(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // A default double field.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Double field:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createDefaultDoubleField(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // A double field with maximum 3 decimals.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Double field, max 3 decimals:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createDoubleFieldWithDecimals(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // A big decimal field with maximum 3 decimals.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("BigDecimal field, max 3 decimals:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createBigDecimalFieldWithDecimals(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // A big decimal field with a range [0; +infinity].
    updateLabel = new JLabel();
    centerPane.add(new JLabel("BigInteger field, [0;+\u221E]:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createBigDecimalFieldWithRange(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // New section: miscellaneous
    centerPane.add(new JTitledSeparator("Miscellaneous"), new GridBagConstraints(0, y++, 3, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, sectionInsets, 0, 0));
    // A double field that does not show the tip when input is invalid.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Double field, no tip on error:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createDoubleFieldWithoutTip(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // A String field with no selection on focus.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("String field, no selection on focus:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createTextEntryFieldWithNoSelectionOnFocus(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // A String field with a validator and trapping focus.
    updateLabel = new JLabel();
    centerPane.add(new JLabel("Integer field, trap focus when invalid:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(connectUpdateLabel(createIntegerEntryFieldWithFocusTrappedOnError(), updateLabel), new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(updateLabel, new GridBagConstraints(2, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    // Rest of the initialisation
    add(new JScrollPane(centerPane), BorderLayout.CENTER);
  }

  private JTextEntryField createDefaultTextEntryField() {
    return new JTextEntryField("Some string", 14);
  }

  private JTextEntryField createTextEntryFieldWithMaxLength() {
    return new JTextEntryField("abc", 14, 4);
  }

  private JNumberEntryField<Integer> createDefaultIntegerField() {
    return new JNumberEntryField<Integer>(486, 14);
  }

  private JNumberEntryField<Integer> createIntegerFieldWithRange() {
    return new JNumberEntryField<Integer>(0, 14, -20, 50);
  }

  private JNumberEntryField<Double> createDefaultDoubleField() {
    return new JNumberEntryField<Double>(1234567890123.123d, 14);
  }

  private JNumberEntryField<Double> createDoubleFieldWithDecimals() {
    return new JNumberEntryField<Double>(0.0, 14, 3);
  }

  private JNumberEntryField<Double> createDoubleFieldWithoutTip() {
    JNumberEntryField<Double> numberEntryField = new JNumberEntryField<Double>(0.0, 14, 3, null, null);
    numberEntryField.setTipDisplayedOnError(false);
    return numberEntryField;
  }

  private JNumberEntryField<BigDecimal> createBigDecimalFieldWithDecimals() {
    return new JNumberEntryField<BigDecimal>(BigDecimal.ZERO, 14, 3);
  }

  private JNumberEntryField<BigInteger> createBigDecimalFieldWithRange() {
    return new JNumberEntryField<BigInteger>(BigInteger.ZERO, 14, BigInteger.ZERO, null);
  }

  private JTextEntryField createTextEntryFieldWithNoSelectionOnFocus() {
    JTextEntryField textEntryField = new JTextEntryField("ab", 14);
    textEntryField.setSelectingAllOnFocus(false);
    return textEntryField;
  }

  private JTextEntryField createIntegerEntryFieldWithFocusTrappedOnError() {
    JNumberEntryField<Integer> numberEntryField = new JNumberEntryField<Integer>(0, 14, 0, 9);
    numberEntryField.setFocusTrappedOnInvalidText(true);
    return numberEntryField;
  }

  private JTextEntryField connectUpdateLabel(JTextEntryField field, final JLabel updateLabel) {
    updateLabel.setPreferredSize(new Dimension(150, 0));
    updateLabel.setText(field.getValidText());
    field.addTextEntryFieldListener(new TextEntryFieldListener() {
      public void textCommitted(JTextEntryField textEntryField) {
        updateLabel.setText(textEntryField.getValidText());
      }
    });
    return field;
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    SwingSuiteUtils.setPreferredLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DJ Swing Suite Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new TextAndNumberFieldsExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
