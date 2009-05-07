/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite.demo.examples.basiccomponents;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import chrriis.dj.swingsuite.JTitledSeparator;
import chrriis.dj.swingsuite.SwingSuiteUtilities;

/**
 * @author Christopher Deckers
 */
public class TitledSeparatorExample extends JPanel {

  public TitledSeparatorExample() {
    super(new BorderLayout(0, 0));
    JPanel centerPane = new JPanel(new GridBagLayout());
    int y = 0;
    centerPane.add(new JTitledSeparator("Section 1"), new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    centerPane.add(new JLabel("The quick brown fox jumps over the lazy dog 1"), new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 25, 0, 25), 0, 0));
    centerPane.add(new JLabel("The quick brown fox jumps over the lazy dog 2"), new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 25, 0, 25), 0, 0));
    centerPane.add(new JTitledSeparator("Section 2"), new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    centerPane.add(new JLabel("The quick brown fox jumps over the lazy dog 1"), new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 25, 0, 25), 0, 0));
    centerPane.add(new JLabel("The quick brown fox jumps over the lazy dog 2"), new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 25, 0, 25), 0, 0));
    // Rest of the initialisation
    add(centerPane, BorderLayout.CENTER);
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    SwingSuiteUtilities.setPreferredLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DJ Swing Suite Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new TitledSeparatorExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
