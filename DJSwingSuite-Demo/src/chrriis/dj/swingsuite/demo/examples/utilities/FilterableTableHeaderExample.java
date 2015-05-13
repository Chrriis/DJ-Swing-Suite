/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite.demo.examples.utilities;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import chrriis.dj.swingsuite.DefaultTableHeaderFilter;
import chrriis.dj.swingsuite.FilterableTableHeader;
import chrriis.dj.swingsuite.FilterableTableHeader.TableHeaderFilterChangeListener;
import chrriis.dj.swingsuite.SwingSuiteUtilities;

/**
 * @author Christopher Deckers
 */
public class FilterableTableHeaderExample extends JPanel {

  public FilterableTableHeaderExample() {
    super(new BorderLayout());
    final Object[][] rowData = new Object[][] {
        {"Bruce", "Pink", 48},
        {"John", "Blue", 12},
        {"Adam", "Yellow", 56},
        {"John", "Black", 18},
        {"Alfred", "Black", 52},
        {"Adam", "Green", 40},
        {"Michel", "Red", 48},
        {"Aline", "Blue", 10},
    };
    final JTable table = new JTable(new AbstractTableModel() {
      @Override
      public String getColumnName(int column) {
        switch(column) {
          case 0: return "Name";
          case 1: return "Color";
          case 2: return "Age";
        }
        return null;
      }
      public int getRowCount() {
        return rowData.length;
      }
      public int getColumnCount() {
        return 3;
      }
      public Object getValueAt(int row, int col) {
        return rowData[row][col];
      }
      @Override
      public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
          case 2: return Integer.class;
          default:return String.class;
        }
      }
    });
    // The table must have a row sorter for the filter logic to work.
    table.setAutoCreateRowSorter(true);
    // Now create the filterable header.
    final FilterableTableHeader filterableTableHeader = new FilterableTableHeader(table);
    DefaultTableHeaderFilter headerFilter = new DefaultTableHeaderFilter();
    filterableTableHeader.setHeaderFilter(0, headerFilter);
    filterableTableHeader.setHeaderFilter(1, headerFilter);
    filterableTableHeader.setHeaderFilter(2, headerFilter);
    filterableTableHeader.addFilterChangeListener(new TableHeaderFilterChangeListener() {
      public void processFilterModification(int column) {
        // Set the row filter, which triggers recomputation.
        ((TableRowSorter<? extends TableModel>)table.getRowSorter()).setRowFilter(filterableTableHeader.getRowFilter());
      }
    });
    table.setTableHeader(filterableTableHeader);
    add(new JScrollPane(table), BorderLayout.CENTER);
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    SwingSuiteUtilities.setPreferredLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DJ Swing Suite Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new FilterableTableHeaderExample(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
