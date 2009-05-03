/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite.demo.toolbox;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import chrriis.dj.swingsuite.SwingSuiteUtilities;

/**
 * @author Christopher Deckers
 */
public class AutoScrollExample extends JPanel {

  public AutoScrollExample() {
    super(new BorderLayout());
    JPanel panel = new JPanel(new GridLayout(10, 10, 100, 100));
    for(int i=0; i<10; i++) {
      for(int j=0; j<10; j++) {
        JTextField textField = new JTextField("Text " + i + "x" + j);
        textField.setEnabled(i % 2 == 0);
        // We enable auto scroll on the text fields as well because it captures mouse inputs:
        // the auto scroll at the scroll pane level cannot be triggered.
        SwingSuiteUtilities.setAutoScrollEnabled(textField, true);
        panel.add(textField);
      }
    }
    JScrollPane scrollPane = new JScrollPane(panel);
    SwingSuiteUtilities.setAutoScrollEnabled(scrollPane, true);
    add(scrollPane, BorderLayout.CENTER);
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    SwingSuiteUtilities.setPreferredLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DJ Swing Suite Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new AutoScrollExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
