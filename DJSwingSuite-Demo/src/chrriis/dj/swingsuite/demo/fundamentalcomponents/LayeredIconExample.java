/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite.demo.fundamentalcomponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import chrriis.dj.swingsuite.LayeredIcon;
import chrriis.dj.swingsuite.SwingSuiteUtilities;

/**
 * @author Christopher Deckers
 */
public class LayeredIconExample extends JPanel {

  public LayeredIconExample() {
    super(new BorderLayout(0, 0));
    JPanel centerPane = new JPanel(new GridBagLayout());
    ImageIcon icon = new ImageIcon(getClass().getResource("resource/package16.png"));
    Icon loadingBackgroundIcon = new Icon() {
      public int getIconWidth() {
        return 12;
      }
      public int getIconHeight() {
        return 8;
      }
      public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, getIconWidth(), getIconHeight());
      }
    };
    ImageIcon loadingIcon = new ImageIcon(getClass().getResource("resource/loading9.gif"));
    LayeredIcon layeredIcon = new LayeredIcon(20, 16);
    layeredIcon.addIcon(icon);
    layeredIcon.addIcon(loadingBackgroundIcon, 6, 6);
    layeredIcon.addIcon(loadingIcon, 12, 8);
    centerPane.add(new JLabel("layered icon with an image, a custom icon and an animated image", layeredIcon, JLabel.LEFT));
    add(centerPane, BorderLayout.CENTER);
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    SwingSuiteUtilities.setPreferredLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DJ Swing Suite Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new LayeredIconExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
