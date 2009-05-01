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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URL;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import chrriis.dj.swingsuite.JLink;
import chrriis.dj.swingsuite.JTitledSeparator;
import chrriis.dj.swingsuite.LinkListener;
import chrriis.dj.swingsuite.SwingSuiteUtilities;

/**
 * @author Christopher Deckers
 */
public class LinkExample extends JPanel {

  public LinkExample() {
    super(new BorderLayout(0, 0));
    JPanel centerPane = new JPanel(new GridBagLayout());
    int y = 0;
    Insets insets = new Insets(0, 25, 10, 25);
    // New section: default handler (Java 6+ only).
    centerPane.add(new JTitledSeparator("Default Handler (Java 6+ only)"), new GridBagConstraints(0, y++, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    // A web link (String-based).
    centerPane.add(new JLabel("Web link (String-based):"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    centerPane.add(new JLink<String>("Google", "http://www.google.com"), new GridBagConstraints(1, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    // A local link (URL-based).
    try {
      centerPane.add(new JLabel("Local file system (URL-based):"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
      centerPane.add(new JLink<URL>("Root folder", new URL("file:///")), new GridBagConstraints(1, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    } catch(Exception e) {
      e.printStackTrace();
    }
    // An e-mail (URI-based).
    try {
      centerPane.add(new JLabel("E-mail (URI-based):"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
      centerPane.add(new JLink<URI>("chrriis@nextencia.net", new URI("mailto:chrriis@nextencia.net")), new GridBagConstraints(1, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    } catch(Exception e) {
      e.printStackTrace();
    }
    // A local file (File-based).
    try {
      centerPane.add(new JLabel("Local file (File-based):"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
      JPanel localFileLinkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      final JLink<File> fileLink = new JLink<File>("<select a target>", null, "First, select a file from your system");
      localFileLinkPanel.add(fileLink);
      localFileLinkPanel.add(Box.createHorizontalStrut(5));
      JButton fileSelectionButton = new JButton("...");
      localFileLinkPanel.add(fileSelectionButton);
      fileSelectionButton.addActionListener(new ActionListener() {
        private  JFileChooser fileChooser = new JFileChooser();
        public void actionPerformed(ActionEvent e) {
          if(fileChooser.showOpenDialog(LinkExample.this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileLink.setText(selectedFile.getName());
            fileLink.setToolTipText(selectedFile.getAbsolutePath());
            fileLink.setTarget(selectedFile);
          }
        }
      });
      centerPane.add(localFileLinkPanel, new GridBagConstraints(1, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    } catch(Exception e) {
      e.printStackTrace();
    }
    // New section: default handler (Java 6+ only).
    centerPane.add(new JTitledSeparator("Custom Handler"), new GridBagConstraints(0, y++, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));
    // A custom link with a custom tool tip.
    centerPane.add(new JLabel("Custom handler and tool tip:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    JLink<Color> colorLink = new JLink<Color>("A color", Color.RED, "A link with a color");
    colorLink.addLinkListener(new LinkListener<Color>() {
      public boolean linkActivated(JLink<Color> link, Color target) {
        JOptionPane.showMessageDialog(LinkExample.this, "Link was clicked with a color object (" + target + ")");
        return false;
      }
    });
    centerPane.add(colorLink, new GridBagConstraints(1, y++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
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
        frame.getContentPane().add(new LinkExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
