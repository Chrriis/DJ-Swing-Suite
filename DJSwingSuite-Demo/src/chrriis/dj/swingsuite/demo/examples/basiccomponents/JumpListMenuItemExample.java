/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite.demo.examples.basiccomponents;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import chrriis.dj.swingsuite.JJumpListMenuItem;
import chrriis.dj.swingsuite.SwingSuiteUtilities;

/**
 * @author Christopher Deckers
 */
public class JumpListMenuItemExample extends JPanel {

  public JumpListMenuItemExample() {
    super(new BorderLayout(0, 0));
    JPanel centerPane = new JPanel(new GridBagLayout());
    centerPane.add(new JLabel("A pane with a popup menu"));
    centerPane.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        processMouseEvent(e);
      }
      @Override
      public void mouseReleased(MouseEvent e) {
        processMouseEvent(e);
      }
      private void processMouseEvent(MouseEvent e) {
        if(e.isPopupTrigger()) {
          JPopupMenu popupMenu = new JPopupMenu();
          popupMenu.add(new JMenuItem("A regular item"));
          JJumpListMenuItem jumpListMenuItem = new JJumpListMenuItem("An item with a jump list");
          jumpListMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              JOptionPane.showMessageDialog(JumpListMenuItemExample.this, "Main item selected.");
            }
          });
          JMenu jumpListMenu = new JMenu();
          JMenuItem jumpListItem1 = new JMenuItem("Jump item 1");
          jumpListItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              JOptionPane.showMessageDialog(JumpListMenuItemExample.this, "Jump list item item 1 selected.");
            }
          });
          jumpListMenu.add(jumpListItem1);
          JMenuItem jumpListItem2 = new JMenuItem("Jump item 2");
          jumpListItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              JOptionPane.showMessageDialog(JumpListMenuItemExample.this, "Jump list item item 2 selected.");
            }
          });
          jumpListMenu.add(jumpListItem2);
          jumpListMenuItem.setJumpListMenu(jumpListMenu);
          popupMenu.add(jumpListMenuItem);
          popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });
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
        frame.getContentPane().add(new JumpListMenuItemExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
