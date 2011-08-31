/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Christopher Deckers
 */
public class JSedRegExBuilder extends JPanel {

  public JSedRegExBuilder(String patternSummary, String sampleText) {
    setLayout(new GridBagLayout());
    JPanel summaryPanel = new JPanel(new GridBagLayout());
    summaryPanel.add(new JLabel("Pattern summary: "), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    final JTextField summaryTextField = new JTextField(7);
    final JTextArea sampleTextTextArea = new JTextArea(sampleText);
    final JTextArea outputTextArea = new JTextArea();
    outputTextArea.setEditable(false);
    DocumentListener documentListener = new DocumentListener() {
      public void removeUpdate(DocumentEvent e) {
        update();
      }
      public void insertUpdate(DocumentEvent e) {
        update();
      }
      public void changedUpdate(DocumentEvent e) {
        update();
      }
      private void update() {
        try {
          outputTextArea.setText(SwingSuiteUtilities.applySedRegularExpression(sampleTextTextArea.getText(), summaryTextField.getText()));
        } catch(Exception e) {
          outputTextArea.setText(e.getMessage());
          outputTextArea.setCaretPosition(0);
        }
      }
    };
    summaryTextField.getDocument().addDocumentListener(documentListener);
    summaryPanel.add(summaryTextField, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    add(summaryPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    add(new JLabel("Sample text:"), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
    add(new JScrollPane(sampleTextTextArea), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    add(new JLabel("Output:"), new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
    add(new JScrollPane(outputTextArea), new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    summaryTextField.setText(patternSummary);
    sampleTextTextArea.getDocument().addDocumentListener(documentListener);
  }

}
