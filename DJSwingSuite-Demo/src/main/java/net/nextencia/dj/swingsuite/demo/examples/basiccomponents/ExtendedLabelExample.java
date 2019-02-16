/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.dj.swingsuite.demo.examples.basiccomponents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.nextencia.dj.swingsuite.JExtendedLabel;
import net.nextencia.dj.swingsuite.SwingSuiteUtilities;

/**
 * @author Christopher Deckers
 */
public class ExtendedLabelExample extends JPanel {

  public ExtendedLabelExample() {
    super(new BorderLayout(0, 0));
    JPanel centerPane = new JPanel(new GridBagLayout());
    int y = 0;
    Insets insets = new Insets(0, 25, 10, 25);
    // Standard label.
    centerPane.add(new JLabel("This is a normal JLabel."), new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    // Selectable label.
    centerPane.add(new JExtendedLabel("This is a selectable label."), new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    // Long selectable label (demonstrate scrolling).
    JExtendedLabel veryLongLabel = new JExtendedLabel("This is a very long selectable label that does not fit so that we can show what happens when scrolling is needed.");
    veryLongLabel.setPreferredSize(new Dimension(200, veryLongLabel.getPreferredSize().height));
    centerPane.add(veryLongLabel, new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    // Multiline label.
    centerPane.add(new JExtendedLabel("This is a multiline\nselectable label."), new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    // Multiline right-aligned label.
    centerPane.add(new JExtendedLabel("This is a multiline\nright-aligned selectable label.", JExtendedLabel.RIGHT), new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    // Multiline non-selectable label.
    centerPane.add(new JExtendedLabel("This is a multiline\nnon-selectable label.", false), new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    add(centerPane, BorderLayout.CENTER);
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    SwingSuiteUtilities.setPreferredLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DJ Swing Suite Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ExtendedLabelExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
