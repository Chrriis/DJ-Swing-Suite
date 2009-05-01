/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite.demo.toolbox;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import chrriis.dj.swingsuite.JWidePopupComboBox;
import chrriis.dj.swingsuite.SwingSuiteUtilities;

/**
 * @author Christopher Deckers
 */
public class WidePopupComboBoxExample extends JPanel {

  public WidePopupComboBoxExample() {
    super(new BorderLayout(0, 0));
    JPanel centerPane = new JPanel(new GridBagLayout());
    int y = 0;
    Insets insets = new Insets(0, 5, 10, 5);
    // A wide popup combo box without a size constraint.
    Object[] comboItems = new Object[] {"Item 1", "Item 2", "an item that is very long and messes up UIs when the combo box is the standard one"};
    centerPane.add(new JLabel("Without width constraint: "), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(new JWidePopupComboBox(comboItems), new GridBagConstraints(1, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    // A wide popup combo box with a width constraint.
    centerPane.add(new JLabel("With width constraint: "), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    JWidePopupComboBox constrainedWidthComboBox = new JWidePopupComboBox(comboItems);
    constrainedWidthComboBox.setPreferredWidth(150);
    centerPane.add(constrainedWidthComboBox, new GridBagConstraints(1, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    // A wide popup combo box with a preferred size.
    centerPane.add(new JLabel("With preferred size: "), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    JWidePopupComboBox preferredSizeComboBox = new JWidePopupComboBox(comboItems);
    preferredSizeComboBox.setPreferredSize(new Dimension(150, preferredSizeComboBox.getPreferredSize().height));
    centerPane.add(preferredSizeComboBox, new GridBagConstraints(1, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    // An editable wide popup combo box with a width constraint.
    centerPane.add(new JLabel("With width constraint, editable: "), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    JWidePopupComboBox constrainedWidthEditableComboBox = new JWidePopupComboBox(comboItems);
    constrainedWidthEditableComboBox.setPreferredWidth(150);
    constrainedWidthEditableComboBox.setEditable(true);
    centerPane.add(constrainedWidthEditableComboBox, new GridBagConstraints(1, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    // Rest of the initialisation
    add(new JScrollPane(centerPane), BorderLayout.CENTER);
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    SwingSuiteUtilities.setPreferredLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DJ Swing Suite Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new WidePopupComboBoxExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
