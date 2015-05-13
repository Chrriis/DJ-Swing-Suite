/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.RowFilter;
import javax.swing.Scrollable;
import javax.swing.UIManager;
import javax.swing.table.TableModel;

import chrriis.dj.swingsuite.FilterableTableHeader.TableHeaderFilter;
import chrriis.dj.swingsuite.JTriStateCheckBox.CheckState;

/**
 * @author Christopher Deckers
 */
public class DefaultTableHeaderFilter implements TableHeaderFilter {

  private static class ScrollablePanel extends JPanel implements Scrollable {

    private int unit;

    public ScrollablePanel(LayoutManager layoutManager) {
      super(layoutManager);
      JCheckBox checkBox = new JCheckBox();
      checkBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      unit = checkBox.getPreferredSize().height + 2;
    }

    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
      return unit;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
      return unit * 3;
    }

    public boolean getScrollableTracksViewportHeight() {
      return getParent().getHeight() > getPreferredSize().height;
    }

    public boolean getScrollableTracksViewportWidth() {
      return getParent().getWidth() > getPreferredSize().width;
    }

  }

  private class FilterEditor extends JPanel {
    private boolean isAdjusting;
    private JTriStateCheckBox selectAllCheckBox;
    private List<JCheckBox> checkBoxList;
    private Set<Object> newAcceptedValueSet = new HashSet<Object>();
    public FilterEditor(final int column, final FilterableTableHeader filterableTableHeader, final DefaultTableHeaderFilter headerFilter, final JPopupMenu popupMenu, Object[] values, Map<Object, String> valueToTextMap) {
      super(new BorderLayout());
      setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      JPanel centerPane = new JPanel(new BorderLayout());
      JPanel checkBoxPane = new ScrollablePanel(new GridBagLayout());
      checkBoxPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      checkBoxPane.setBackground(UIManager.getColor("TextField.background"));
      checkBoxList = new ArrayList<JCheckBox>();
      Insets emptyInsets = new Insets(0, 0, 0, 0);
      int y = 0;
      selectAllCheckBox = new JTriStateCheckBox("(Select all)");
      selectAllCheckBox.setRollingStates(CheckState.SELECTED, CheckState.NOT_SELECTED);
      selectAllCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      selectAllCheckBox.setOpaque(false);
      checkBoxPane.add(selectAllCheckBox, new GridBagConstraints(0, y++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));
      Set<Object> acceptedValueSet = getAcceptedValueSet(column);
      boolean isLimiting = false;
      for (int i = 0; i < values.length; i++) {
        final Object value = values[i];
        if(i == 2000) {
          isLimiting = true;
          break;
        }
        String text = valueToTextMap.get(value);
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkBox.setOpaque(false);
        boolean isSelected = acceptedValueSet == null || acceptedValueSet.contains(value);
        checkBox.setSelected(isSelected);
        if(isSelected) {
          newAcceptedValueSet.add(value);
        }
        checkBox.addItemListener(new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            boolean isSelected = e.getStateChange() == ItemEvent.SELECTED;
            if(isSelected) {
              newAcceptedValueSet.add(value);
            } else {
              newAcceptedValueSet.remove(value);
            }
            if(isAdjusting) {
              return;
            }
            adjustSelectAllCheckBoxState();
          }
        });
        checkBoxList.add(checkBox);
        checkBoxPane.add(checkBox, new GridBagConstraints(0, y++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));
      }
      checkBoxPane.add(Box.createVerticalGlue(), new GridBagConstraints(0, y++, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));
      adjustSelectAllCheckBoxState();
      final JButton okButton = new JButton("OK");
      okButton.setEnabled(selectAllCheckBox.getState() != CheckState.NOT_SELECTED);
      selectAllCheckBox.addTriStateCheckBoxListener(new TriStateCheckBoxListener() {
        public void stateChanged(JTriStateCheckBox triStateCheckBox, CheckState checkState) {
          okButton.setEnabled(checkState != CheckState.NOT_SELECTED);
          if(isAdjusting) {
            return;
          }
          isAdjusting = true;
          for(JCheckBox checkBox: checkBoxList) {
            checkBox.setSelected(checkState == CheckState.SELECTED);
          }
          isAdjusting = false;
        }
      });
      JScrollPane scrollPane = new JScrollPane(checkBoxPane);
      Dimension preferredSize = scrollPane.getPreferredSize();
      preferredSize.width = 200;
      preferredSize.height = 200;
      scrollPane.setPreferredSize(preferredSize);
      centerPane.add(scrollPane, BorderLayout.CENTER);
      if(isLimiting) {
        centerPane.add(new JLabel("Note: only displaying the first 2000 unique items."), BorderLayout.SOUTH);
      }
      add(centerPane, BorderLayout.CENTER);
      JPanel southPane = new JPanel(new BorderLayout());
      southPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
      JPanel buttonPane = new JPanel(new GridLayout(1, 2));
      okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          switch(selectAllCheckBox.getState()) {
            case SELECTED:
              setAcceptedValueSet(column, null);
              break;
            case NOT_SELECTED:
              setAcceptedValueSet(column, new HashSet<Object>());
              break;
            case INDETERMINATE:
              setAcceptedValueSet(column, newAcceptedValueSet);
              break;
          }
          popupMenu.setVisible(false);
          filterableTableHeader.notifyFilterChanged(column, headerFilter);
        }
      });
      buttonPane.add(okButton);
      JButton cancelButton = new JButton("Cancel");
      cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          popupMenu.setVisible(false);
        }
      });
      buttonPane.add(cancelButton);
      southPane.add(buttonPane, BorderLayout.EAST);
      add(southPane, BorderLayout.SOUTH);
    }
    private void adjustSelectAllCheckBoxState() {
      isAdjusting = true;
      boolean hasSelected = false;
      boolean hasDeselected = false;
      for(JCheckBox checkBox: checkBoxList) {
        if(checkBox.isSelected()) {
          hasSelected = true;
        } else {
          hasDeselected = true;
        }
        if(hasSelected && hasDeselected) {
          break;
        }
      }
      selectAllCheckBox.setState(hasSelected? hasDeselected? CheckState.INDETERMINATE: CheckState.SELECTED: CheckState.NOT_SELECTED);
      isAdjusting = false;
    }
  }

  public boolean isFilterActive(int column) {
    return columnToAcceptedValueSetMap.containsKey(column);
  }

  private Map<Integer, Set<Object>> columnToAcceptedValueSetMap = new HashMap<Integer, Set<Object>>();

  private Set<Object> getAcceptedValueSet(int column) {
    return columnToAcceptedValueSetMap.get(column);
  }

  private void setAcceptedValueSet(int column, Set<Object> acceptedValueSet) {
    if(acceptedValueSet == null) {
      columnToAcceptedValueSetMap.remove(column);
    } else {
      columnToAcceptedValueSetMap.put(column, acceptedValueSet);
    }
  }

  public JPopupMenu getFilterEditor(FilterableTableHeader filterableTableHeader, TableModel tableModel, int column, int[] rows, Comparator<Object> valueComparator) {
    Map<Object, String> valueToTextMap = new HashMap<Object, String>();
    for(int i=0; i<rows.length; i++) {
      int row = rows[i];
      Object o = tableModel.getValueAt(row, column);
      valueToTextMap.put(o, convertToString(o, tableModel, row, column));
    }
    Object[] values = valueToTextMap.keySet().toArray();
    Arrays.sort(values, valueComparator);
    JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.setLayout(new BorderLayout());
    FilterEditor filterEditor = new FilterEditor(column, filterableTableHeader, this, popupMenu, values, valueToTextMap);
    popupMenu.add(filterEditor, BorderLayout.CENTER);
    return popupMenu;
  }

  protected String convertToString(Object value, TableModel tableModel, int row, int column) {
    if(value == null) {
      return "(empty)";
    }
    if(value instanceof Integer || value instanceof Long) {
      return getIntegerFormat().format(value);
    }
    if(value instanceof Number) {
      return getDoubleFormat().format(value);
    }
    if(value instanceof Date) {
      return getDateFormat().format(value);
    }
    return String.valueOf(value);
  }

  private NumberFormat integerFormat;

  private NumberFormat getIntegerFormat() {
    if(integerFormat == null) {
      integerFormat = DecimalFormat.getIntegerInstance();
    }
    return integerFormat;
  }

  private NumberFormat doubleFormat;

  private NumberFormat getDoubleFormat() {
    if(doubleFormat == null) {
      doubleFormat = DecimalFormat.getInstance();
    }
    return doubleFormat;
  }

  private DateFormat dateFormat;

  private DateFormat getDateFormat() {
    if(dateFormat == null) {
      dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
    }
    return dateFormat;
  }

  public boolean include(RowFilter.Entry<? extends TableModel, ? extends Integer> entry, int column) {
    Set<Object> acceptedValueSet = getAcceptedValueSet(column);
    return acceptedValueSet == null || acceptedValueSet.contains(entry.getValue(column));
  }

}
