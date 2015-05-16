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
import java.awt.Font;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    private Set<Object> newAcceptedValueSet;
    private JButton okButton;
    private JPanel contentPane;
    private Set<Object> acceptedValueSet;
    private Object[] values;
    private Map<Object, String> valueToTextMap;
    private boolean isAddingToCurrentFilter;
    public FilterEditor(final int column, final FilterableTableHeader filterableTableHeader, final DefaultTableHeaderFilter headerFilter, final JPopupMenu popupMenu, Object[] values, Map<Object, String> valueToTextMap) {
      super(new BorderLayout());
      setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      acceptedValueSet = getAcceptedValueSet(column);
      this.values = values;
      this.valueToTextMap = valueToTextMap;
      okButton = new JButton("OK");
      JPanel northPane = new JPanel(new BorderLayout());
      northPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0));
      final JTextField filterTextField = new JTextField();
      filterTextField.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton.doClick();
        }
      });
      filterTextField.getDocument().addDocumentListener(new DocumentListener() {
        public void removeUpdate(DocumentEvent e) {
          adjustFilter();
        }
        public void insertUpdate(DocumentEvent e) {
          adjustFilter();
        }
        public void changedUpdate(DocumentEvent e) {
          adjustFilter();
        }
        private volatile Thread thread;
        private void adjustFilter() {
          thread = new Thread("Table filter delay") {
            @Override
            public void run() {
              try {
                Thread.sleep(200);
              } catch (InterruptedException e) {
              }
              final Thread current = this;
              if(thread != current) {
                return;
              }
              SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  if(thread != current) {
                    return;
                  }
                  String filter = filterTextField.getText();
                  if(filter.length() == 0) {
                    filter = null;
                  }
                  setFilter(filter);
                }
              });
            }
          };
          thread.setDaemon(true);
          thread.start();
        }
      });
      northPane.add(filterTextField, BorderLayout.CENTER);
      add(northPane, BorderLayout.NORTH);
      contentPane = new JPanel(new BorderLayout());
      populateContentPane();
      add(contentPane, BorderLayout.CENTER);
      JPanel southPane = new JPanel(new BorderLayout());
      southPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
      JPanel buttonPane = new JPanel(new GridLayout(1, 2));
      okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          switch(selectAllCheckBox.getState()) {
            case SELECTED:
              if(filter == null) {
                setAcceptedValueSet(column, null);
              } else {
                if(isAddingToCurrentFilter) {
                  newAcceptedValueSet.addAll(acceptedValueSet);
                }
                setAcceptedValueSet(column, newAcceptedValueSet);
              }
              break;
            case NOT_SELECTED:
              if(!isAddingToCurrentFilter) {
                setAcceptedValueSet(column, new HashSet<Object>());
              }
              break;
            case INDETERMINATE:
              if(isAddingToCurrentFilter) {
                newAcceptedValueSet.addAll(acceptedValueSet);
              }
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
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          filterTextField.requestFocusInWindow();
        }
      });
    }
    private void populateContentPane() {
      contentPane.removeAll();
      JPanel centerPane = new JPanel(new BorderLayout());
      JPanel checkBoxPane = new ScrollablePanel(new GridBagLayout());
      checkBoxPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      checkBoxPane.setBackground(UIManager.getColor("TextField.background"));
      checkBoxList = new ArrayList<JCheckBox>();
      Insets emptyInsets = new Insets(0, 0, 0, 0);
      int y = 0;
      newAcceptedValueSet = new HashSet<Object>();
      selectAllCheckBox = null;
      boolean isLimiting = false;
      String filterLC = filter;
      if(filterLC != null) {
        filterLC = filterLC.toLowerCase();
      }
      isAddingToCurrentFilter = false;
      int itemCount = 0;
      int maxItemCount = 2000;
      for (int i = 0; i < values.length; i++) {
        final Object value = values[i];
        if(itemCount == maxItemCount) {
          isLimiting = true;
          break;
        }
        String text = valueToTextMap.get(value);
        boolean isRetained = true;
        if(filterLC != null) {
          isRetained = text.toLowerCase().contains(filterLC);
        }
        if(isRetained) {
          if(itemCount == 0) {
            selectAllCheckBox = new JTriStateCheckBox("(Select all)");
            selectAllCheckBox.setRollingStates(CheckState.SELECTED, CheckState.NOT_SELECTED);
            selectAllCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            selectAllCheckBox.setOpaque(false);
            checkBoxPane.add(selectAllCheckBox, new GridBagConstraints(0, y++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));
            if(filterLC != null && acceptedValueSet != null) {
              JCheckBox addSelectionCheckBox = new JCheckBox("Add selection to current filter");
              addSelectionCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
              addSelectionCheckBox.setOpaque(false);
              addSelectionCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                  isAddingToCurrentFilter = e.getStateChange() == ItemEvent.SELECTED;
                }
              });
              checkBoxPane.add(addSelectionCheckBox, new GridBagConstraints(0, y++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));
            }
          }
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
          itemCount++;
        }
      }
      if(itemCount == 0) {
        JLabel noItemsLabel = new JLabel("No corresponding items.");
        noItemsLabel.setFont(noItemsLabel.getFont().deriveFont(Font.ITALIC));
        checkBoxPane.add(noItemsLabel, new GridBagConstraints(0, y++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));
      }
      checkBoxPane.add(Box.createVerticalGlue(), new GridBagConstraints(0, y++, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));
      adjustSelectAllCheckBoxState();
      okButton.setEnabled(selectAllCheckBox != null && selectAllCheckBox.getState() != CheckState.NOT_SELECTED);
      if(selectAllCheckBox != null) {
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
        if(filterLC != null) {
          selectAllCheckBox.setSelected(true);
        }
      }
      JScrollPane scrollPane = new JScrollPane(checkBoxPane);
      Dimension preferredSize = scrollPane.getPreferredSize();
      preferredSize.width = 200;
      preferredSize.height = 200;
      scrollPane.setPreferredSize(preferredSize);
      centerPane.add(scrollPane, BorderLayout.CENTER);
      if(isLimiting) {
        centerPane.add(new JLabel("Only the first " + maxItemCount + " unique items are displayed.", new ImageIcon(DefaultTableHeaderFilter.class.getResource("resource/warning_obj.png")), JLabel.LEADING), BorderLayout.SOUTH);
      }
      contentPane.add(centerPane, BorderLayout.CENTER);
      contentPane.revalidate();
      contentPane.repaint();
    }
    private String filter;
    private void setFilter(String filter) {
      if(filter != null && filter.equals(this.filter) || filter == this.filter) {
        return;
      }
      this.filter = filter;
      populateContentPane();
    }
    private void adjustSelectAllCheckBoxState() {
      if(selectAllCheckBox == null) {
        return;
      }
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
