/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite.demo.examples.utilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import chrriis.dj.swingsuite.SwingSuiteUtilities;

/**
 * @author Christopher Deckers
 */
public class TableColumnAutoFitExample extends JPanel {

  public TableColumnAutoFitExample() {
    super(new BorderLayout());
    JPanel tableContainer = new JPanel(new BorderLayout());
    tableContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
    final JTable table = new JTable(new String[][] {
        {"Some data for C1 with a long description", "Some data for C2", "C3"},
        {"Some data for C1", "Some data for C2", "C3"},
        {"Some data for C1", "Some data for C2", "C3"},
    }, new String[] {"Column 1", "Column 2 with a long title", "C3"});
    if(SwingSuiteUtilities.IS_JAVA_6_OR_GREATER) {
      table.setAutoCreateRowSorter(true);
      table.setFillsViewportHeight(true);
    }
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.getViewport().setBackground(Color.WHITE);
    tableContainer.add(scrollPane, BorderLayout.CENTER);
    tableContainer.add(new JLabel("Note: cells are editable to test with different content."), BorderLayout.SOUTH);
    add(tableContainer, BorderLayout.CENTER);
    JPanel buttonPane = new JPanel(new FlowLayout());
    JButton autoFitColumnButton = new JButton("Auto-fit second column");
    autoFitColumnButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SwingSuiteUtilities.autoFitTableColumn(table, 1, Integer.MAX_VALUE);
      }
    });
    buttonPane.add(autoFitColumnButton);
    JButton autoFitAllButton = new JButton("Auto-fit all Columns");
    autoFitAllButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SwingSuiteUtilities.autoFitTableColumns(table, Integer.MAX_VALUE);
      }
    });
    buttonPane.add(autoFitAllButton);
    JButton autoFitAllWithMaxButton = new JButton("Auto-fit all with max 50");
    autoFitAllWithMaxButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SwingSuiteUtilities.autoFitTableColumns(table, 50);
      }
    });
    buttonPane.add(autoFitAllWithMaxButton);
    add(buttonPane, BorderLayout.SOUTH);
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    SwingSuiteUtilities.setPreferredLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DJ Swing Suite Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new TableColumnAutoFitExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
