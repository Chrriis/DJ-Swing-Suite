/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.dj.swingsuite.demo.examples.basiccomponents;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.nextencia.dj.swingsuite.JTriStateCheckBox;
import net.nextencia.dj.swingsuite.SwingSuiteUtilities;
import net.nextencia.dj.swingsuite.TriStateCheckBoxListener;
import net.nextencia.dj.swingsuite.JTriStateCheckBox.CheckState;

/**
 * @author Christopher Deckers
 */
public class TriStateCheckBoxExample extends JPanel {

  public TriStateCheckBoxExample() {
    super(new BorderLayout(0, 0));
    JPanel centerPane = new JPanel(new GridBagLayout());
    int y = 0;
    Insets insets = new Insets(0, 25, 10, 25);
    // A default tri-state check box.
    JTriStateCheckBox defaultTriState = new JTriStateCheckBox("Default tri-state check box");
    centerPane.add(defaultTriState, new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    // A tri-state check box with redefined cycle.
    final JTriStateCheckBox changingTriState = new JTriStateCheckBox("With redefined cycle");
    changingTriState.setRollingStates(CheckState.NOT_SELECTED, CheckState.INDETERMINATE, CheckState.SELECTED, CheckState.INDETERMINATE);
    centerPane.add(changingTriState, new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    // A tri-state check box that can only be set to the third state from code.
    JPanel codeOnlyTriStatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    final JTriStateCheckBox codeOnlyTriState = new JTriStateCheckBox("Third state (indeterminate) only settable from code");
    codeOnlyTriState.setRollingStates(CheckState.NOT_SELECTED, CheckState.SELECTED);
    codeOnlyTriStatePanel.add(codeOnlyTriState);
    codeOnlyTriStatePanel.add(Box.createHorizontalStrut(5));
    JButton codeOnlyTriStateButton = new JButton("Set");
    codeOnlyTriStatePanel.add(codeOnlyTriStateButton);
    codeOnlyTriStateButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        codeOnlyTriState.setState(CheckState.INDETERMINATE);
      }
    });
    centerPane.add(codeOnlyTriStatePanel, new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    // A tri-state check box blocked to a state.
    final JTriStateCheckBox blockedTriState = new JTriStateCheckBox("Limited to certain states (only indeterminate here)");
    blockedTriState.setState(CheckState.INDETERMINATE);
    blockedTriState.setRollingStates(CheckState.INDETERMINATE);
    centerPane.add(blockedTriState, new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    // A tri-state check box that listens to the events.
    final JTriStateCheckBox eventTriState = new JTriStateCheckBox("Show the events");
    eventTriState.addTriStateCheckBoxListener(new TriStateCheckBoxListener() {
      public void stateChanged(JTriStateCheckBox checkBox, CheckState state) {
        JOptionPane.showMessageDialog(TriStateCheckBoxExample.this, "Selected state: " + state);
      }
    });
    centerPane.add(eventTriState, new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    // A tri-state check box that can only be set to the third state from code.
    JPanel disabledTriStatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    final JTriStateCheckBox disabledTriState = new JTriStateCheckBox("Disabled");
    disabledTriState.setEnabled(false);
    disabledTriState.setState(CheckState.INDETERMINATE);
    disabledTriStatePanel.add(disabledTriState);
    disabledTriStatePanel.add(Box.createHorizontalStrut(5));
    JButton disabledTriStateButton = new JButton("Cycle");
    disabledTriStatePanel.add(disabledTriStateButton);
    disabledTriStateButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        switch(disabledTriState.getState()) {
          case NOT_SELECTED:
            disabledTriState.setState(CheckState.SELECTED);
            break;
          case SELECTED:
            disabledTriState.setState(CheckState.INDETERMINATE);
            break;
          case INDETERMINATE:
            disabledTriState.setState(CheckState.NOT_SELECTED);
            break;
        }
      }
    });
    centerPane.add(disabledTriStatePanel, new GridBagConstraints(0, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
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
        frame.getContentPane().add(new TriStateCheckBoxExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
