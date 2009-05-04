/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite.demo.examples.utilities;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import chrriis.dj.swingsuite.SwingSuiteUtilities;

/**
 * @author Christopher Deckers
 */
public class WildcardsConversionExample extends JPanel {

  public WildcardsConversionExample() {
    super(new GridBagLayout());
    JPanel contentPane = new JPanel(new BorderLayout());
    final JTextField filterTextField = new JTextField("*.*", 14);
    SwingSuiteUtilities.setSelectAllOnFocus(filterTextField, true);
    contentPane.add(filterTextField, BorderLayout.NORTH);
    final String[] fileJListItems = new String[] {
        "designpatternscard.pdf",
        "fckeditor.html",
        "fckeditor2.html",
        "fckeditor3.html",
        "Feed-icon.svg",
        "FileZilla_3.1.0.1_win32-setup.exe",
        "fireshot-0.25-fx-win.xpi",
        "flex_sdk_3.zip",
        "MozillaInterfaces.jar",
        "Tortoise.jpg",
    };
    final JList fileJList = new JList(fileJListItems);
    contentPane.add(new JScrollPane(fileJList), BorderLayout.CENTER);
    add(contentPane);
    // Add the listener that updates the list.
    filterTextField.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        updateFileJList();
      }
      public void insertUpdate(DocumentEvent e) {
        updateFileJList();
      }
      public void removeUpdate(DocumentEvent e) {
        updateFileJList();
      }
      private void updateFileJList() {
        String text = filterTextField.getText();
        if(text.length() == 0) {
          text = "*.*";
        }
        String regExp = SwingSuiteUtilities.convertWildcardsToRegExp(text);
        Pattern pattern = Pattern.compile(regExp);
        DefaultListModel defaultListModel = new DefaultListModel();
        for(String s: fileJListItems) {
          if(pattern.matcher(s).matches()) {
            defaultListModel.addElement(s);
          }
        }
        fileJList.setModel(defaultListModel);
      }
    });
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    SwingSuiteUtilities.setPreferredLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DJ Swing Suite Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new WildcardsConversionExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
