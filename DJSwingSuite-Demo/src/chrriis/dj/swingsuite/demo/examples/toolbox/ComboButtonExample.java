/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite.demo.examples.toolbox;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import chrriis.dj.swingsuite.JComboButton;
import chrriis.dj.swingsuite.SwingSuiteUtilities;

/**
 * @author Christopher Deckers
 */
public class ComboButtonExample extends JPanel {

  public ComboButtonExample() {
    super(new BorderLayout(0, 0));
    JPanel centerPane = new JPanel(new GridBagLayout());
    int y = 0;
    Insets insets = new Insets(15, 5, 0, 5);
    JToolBar toolBar;
    // A sample icon used by combo buttons
    ImageIcon icon = new ImageIcon(getClass().getResource("resource/package16.png"));
    // A sample popup menu used by combo buttons
    JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.add(new JMenuItem("Item 1"));
    popupMenu.add(new JMenuItem("Item 2"));
    // Create a toolbar for the first row.
    toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.setRollover(true);
    // A combo button with an icon.
    JComboButton comboButtonIcon = new JComboButton(icon, false);
    comboButtonIcon.setArrowPopupMenu(popupMenu);
    SwingSuiteUtilities.adjustToolbarButtonFocus(comboButtonIcon);
    toolBar.add(comboButtonIcon);
    // A combo button with an icon and some text.
    JComboButton comboButtonIconAndText = new JComboButton("Text", icon, false);
    comboButtonIconAndText.setArrowPopupMenu(popupMenu);
    SwingSuiteUtilities.adjustToolbarButtonFocus(comboButtonIconAndText);
    toolBar.add(comboButtonIconAndText);
    // A combo button with some text.
    JComboButton comboButtonText = new JComboButton("Text", false);
    comboButtonText.setArrowPopupMenu(popupMenu);
    SwingSuiteUtilities.adjustToolbarButtonFocus(comboButtonText);
    toolBar.add(comboButtonText);
    // A combo button with an icon and some text, disabled.
    JComboButton comboButtonIconAndTextDisabled = new JComboButton("Text", icon, false);
    comboButtonIconAndTextDisabled.setEnabled(false);
    SwingSuiteUtilities.adjustToolbarButtonFocus(comboButtonIconAndTextDisabled);
    toolBar.add(comboButtonIconAndTextDisabled);
    // A combo button without popups, but which shows the events.
    JComboButton comboButtonIconNoPopup = new JComboButton(icon, false);
    comboButtonIconNoPopup.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(ComboButtonExample.this, "ActionEvent received. On arrow=" + JComboButton.isArrowEvent(e));
      }
    });
    SwingSuiteUtilities.adjustToolbarButtonFocus(comboButtonIconNoPopup);
    toolBar.add(comboButtonIconNoPopup);
    centerPane.add(new JLabel("Integral Combo buttons:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(toolBar, new GridBagConstraints(1, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    // Create a toolbar for the second row.
    toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.setRollover(true);
    // A divided combo button with an icon.
    JComboButton comboButtonIconDivided = new JComboButton(icon, true);
    comboButtonIconDivided.setArrowPopupMenu(popupMenu);
    SwingSuiteUtilities.adjustToolbarButtonFocus(comboButtonIconDivided);
    toolBar.add(comboButtonIconDivided);
    // A combo button with an icon and some text.
    JComboButton comboButtonIconAndTextDivided = new JComboButton("Text", icon, true);
    comboButtonIconAndTextDivided.setArrowPopupMenu(popupMenu);
    SwingSuiteUtilities.adjustToolbarButtonFocus(comboButtonIconAndTextDivided);
    toolBar.add(comboButtonIconAndTextDivided);
    // A combo button with some text.
    JComboButton comboButtonTextDivided = new JComboButton("Text", true);
    comboButtonTextDivided.setArrowPopupMenu(popupMenu);
    SwingSuiteUtilities.adjustToolbarButtonFocus(comboButtonTextDivided);
    toolBar.add(comboButtonTextDivided);
    // A combo button with an icon and some text, disabled.
    JComboButton comboButtonIconAndTextDividedDisabled = new JComboButton("Text", icon, true);
    comboButtonIconAndTextDividedDisabled.setEnabled(false);
    SwingSuiteUtilities.adjustToolbarButtonFocus(comboButtonIconAndTextDividedDisabled);
    toolBar.add(comboButtonIconAndTextDividedDisabled);
    // A divided combo button without popups, but which shows the events.
    JComboButton comboButtonIconDividedNoPopup = new JComboButton(icon, true);
    comboButtonIconDividedNoPopup.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(ComboButtonExample.this, "ActionEvent received. On arrow=" + JComboButton.isArrowEvent(e));
      }
    });
    SwingSuiteUtilities.adjustToolbarButtonFocus(comboButtonIconDividedNoPopup);
    toolBar.add(comboButtonIconDividedNoPopup);
    centerPane.add(new JLabel("Divided Combo buttons:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(toolBar, new GridBagConstraints(1, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
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
        frame.getContentPane().add(new ComboButtonExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
