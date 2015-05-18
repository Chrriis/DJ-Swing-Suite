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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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

  private static enum Operator {
    EQUAL {
      @Override
      public String toString() {
        return "=";
      }
    },
    DIFFERENT {
      @Override
      public String toString() {
        return "\u2260";
      }
    },
    GREATER_THAN {
      @Override
      public String toString() {
        return "\u2265";
      }
    },
    STRICTLY_GREATER_THAN {
      @Override
      public String toString() {
        return ">";
      }
    },
    LESS_THAN {
      @Override
      public String toString() {
        return "\u2264";
      }
    },
    STRICTLY_LESS_THAN {
      @Override
      public String toString() {
        return "<";
      }
    },
  }
  
  private static class NumberCustomFilter {
    private Operator operator1;
    public Operator getOperator1() {
      return operator1;
    }
    private Double value1;
    public Double getValue1() {
      return value1;
    }
    public void setFilter1(Operator operator1, Double value1) {
      this.operator1 = operator1;
      this.value1 = value1;
    }
    private boolean isOr;
    public boolean isOr() {
      return isOr;
    }
    private Operator operator2;
    public Operator getOperator2() {
      return operator2;
    }
    private Double value2;
    public Double getValue2() {
      return value2;
    }
    public void setFilter2(Operator operator1, Double value2, boolean isOr) {
      this.operator2 = operator1;
      this.value2 = value2;
      this.isOr = isOr;
    }
    public boolean include(RowFilter.Entry<? extends TableModel, ? extends Integer> entry, int column) {
      Object refValue = entry.getValue(column);
      boolean isIncluded1 = isIncluded(refValue, operator1, value1);
      if(isIncluded1 && isOr || operator2 == null || !isIncluded1 && !isOr) {
        return isIncluded1;
      }
      boolean isIncluded2 = isIncluded(refValue, operator2, value2);
      return isIncluded2;
    }
    private boolean isIncluded(Object refValue, Operator operator, Double value) {
      boolean isIncluded = false;
      switch(operator) {
        case EQUAL: isIncluded = refValue == null? value == null: refValue instanceof Number && value != null && ((Number)refValue).doubleValue() == value; break;
        case DIFFERENT: isIncluded = refValue == null? value != null: refValue instanceof Number && (value == null || ((Number)refValue).doubleValue() != value); break;
        case GREATER_THAN: isIncluded = refValue instanceof Number && value != null && ((Number)refValue).doubleValue() >= value.doubleValue(); break;
        case STRICTLY_GREATER_THAN: isIncluded = refValue instanceof Number && value != null && ((Number)refValue).doubleValue() > value.doubleValue(); break;
        case LESS_THAN: isIncluded = refValue instanceof Number && value != null && ((Number)refValue).doubleValue() <= value.doubleValue(); break;
        case STRICTLY_LESS_THAN: isIncluded = refValue instanceof Number && value != null && ((Number)refValue).doubleValue() < value.doubleValue(); break;
      }
      return isIncluded;
    }
  }
  
  private static class FilterData {
    private Set<Object> acceptedValueSet;
    public void setAcceptedValueSet(Set<Object> acceptedValueSet) {
      this.acceptedValueSet = acceptedValueSet;
    }
    public Set<Object> getAcceptedValueSet() {
      return acceptedValueSet;
    }
    private NumberCustomFilter numberFilter;
    public void setNumberFilter(NumberCustomFilter numberFilter) {
      this.numberFilter = numberFilter;
    }
    public NumberCustomFilter getNumberFilter() {
      return numberFilter;
    }
    public boolean include(RowFilter.Entry<? extends TableModel, ? extends Integer> entry, int column) {
      if(numberFilter != null) {
        return numberFilter.include(entry, column);
      }
      return acceptedValueSet.contains(entry.getValue(column));
    }
  }
  
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
    private List<JComponent> mainFilterComponentList;
    private JTriStateCheckBox selectAllCheckBox;
    private List<JCheckBox> checkBoxList;
    private Set<Object> newAcceptedValueSet;
    private JButton okButton;
    private JTextField filterTextField;
    private JPanel contentPane;
    private FilterData filterData;
    private Object[] values;
    private Map<Object, String> valueToTextMap;
    private boolean isAddingToCurrentFilter;
    private int column;
    private FilterableTableHeader filterableTableHeader;
    private DefaultTableHeaderFilter headerFilter;
    private boolean isCustomFilterActive;
    private JPopupMenu popupMenu;
    public FilterEditor(final int column, final FilterableTableHeader filterableTableHeader, final DefaultTableHeaderFilter headerFilter, final JPopupMenu popupMenu, Object[] values, Map<Object, String> valueToTextMap) {
      super(new BorderLayout());
      this.popupMenu = popupMenu;
      setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.column = column;
      this.filterableTableHeader = filterableTableHeader;
      this.headerFilter = headerFilter;
      filterData = getFilterData(column);
      if(filterData == null) {
        filterData = new FilterData();
      }
      isCustomFilterActive = filterData.getNumberFilter() != null;
      this.values = values;
      this.valueToTextMap = valueToTextMap;
      okButton = new JButton("OK");
      JPanel northPane = new JPanel(new BorderLayout());
      northPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0));
      filterTextField = new JTextField();
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
                  thread = null;
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
          if(!isCustomFilterActive) {
            switch(selectAllCheckBox.getState()) {
              case SELECTED:
                if(filter == null) {
                  setFilterData(column, null);
                } else {
                  if(isAddingToCurrentFilter) {
                    newAcceptedValueSet.addAll(filterData.getAcceptedValueSet());
                  }
                  filterData.setAcceptedValueSet(newAcceptedValueSet);
                  setFilterData(column, filterData);
                }
                break;
              case NOT_SELECTED:
                if(!isAddingToCurrentFilter) {
                  filterData.setAcceptedValueSet(new HashSet<Object>());
                  setFilterData(column, filterData);
                }
                break;
              case INDETERMINATE:
                if(isAddingToCurrentFilter) {
                  newAcceptedValueSet.addAll(filterData.getAcceptedValueSet());
                }
                filterData.setAcceptedValueSet(newAcceptedValueSet);
                setFilterData(column, filterData);
                break;
            }
          }
          popupMenu.setVisible(false);
          filterableTableHeader.notifyFilterChanged(new int[] {column}, new TableHeaderFilter[] {headerFilter});
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
      JPanel mainPane = new JPanel(new BorderLayout());
      JPanel checkBoxPane = new ScrollablePanel(new GridBagLayout());
      checkBoxPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      checkBoxPane.setBackground(UIManager.getColor("TextField.background"));
      checkBoxList = new ArrayList<JCheckBox>();
      Insets emptyInsets = new Insets(0, 0, 0, 0);
      int y = 0;
      newAcceptedValueSet = new HashSet<Object>();
      mainFilterComponentList = new ArrayList<JComponent>();
      selectAllCheckBox = null;
      boolean isLimiting = false;
      String filterLC = filter;
      if(filterLC != null) {
        filterLC = filterLC.toLowerCase();
      }
      isAddingToCurrentFilter = false;
      int itemCount = 0;
      int maxItemCount = 2000;
      Set<Object> acceptedValueSet = filterData.getAcceptedValueSet();
      final NumberCustomFilter numberFilter = filterData.getNumberFilter();
      boolean hasNumbers = false;
      for (int i = 0; i < values.length; i++) {
        final Object value = values[i];
        if(itemCount >= maxItemCount) {
          isLimiting = true;
        }
        hasNumbers = value instanceof Number;
        if(!isLimiting) {
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
              mainFilterComponentList.add(selectAllCheckBox);
              checkBoxPane.add(selectAllCheckBox, new GridBagConstraints(0, y++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));
              if(filterLC != null && acceptedValueSet != null) {
                JCheckBox addSelectionCheckBox = new JCheckBox("Add selection to current filter");
                addSelectionCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                addSelectionCheckBox.setOpaque(false);
                mainFilterComponentList.add(addSelectionCheckBox);
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
            mainFilterComponentList.add(checkBox);
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
      mainPane.add(scrollPane, BorderLayout.CENTER);
      if(isLimiting) {
        mainPane.add(new JLabel("Only the first " + maxItemCount + " unique items are displayed.", new ImageIcon(DefaultTableHeaderFilter.class.getResource("resource/warning_obj.png")), JLabel.LEADING), BorderLayout.SOUTH);
      }
      centerPane.add(mainPane, BorderLayout.CENTER);
      if(hasNumbers || numberFilter != null) {
        JPanel numberFilterPane = new JPanel(new GridBagLayout());
        JCheckBox numberFilterCheckBox = new JCheckBox("Number filter");
        final Runnable showDialogRunnable = new Runnable() {
          public void run() {
            JPanel contentPane = new JPanel(new BorderLayout());
            contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            JPanel numberFilterContentPane = new JPanel(new GridBagLayout());
            final JComboBox operator1ComboBox = new JComboBox(Operator.values());
            numberFilterContentPane.add(operator1ComboBox, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 0, 0));
            final JNumberEntryField<Double> value1NumberField = new JNumberEntryField<Double>(0.0, 14, null, null, true);
            value1NumberField.setNumber(null);
            numberFilterContentPane.add(value1NumberField, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 0, 0));
            final JComboBox andComboBox = new JComboBox(new String[] {" ", "and", "or"});
            numberFilterContentPane.add(andComboBox, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 0, 0), 0, 0));
            final JComboBox operator2ComboBox = new JComboBox(Operator.values());
            operator2ComboBox.setEnabled(false);
            numberFilterContentPane.add(operator2ComboBox, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 0, 0), 0, 0));
            final JNumberEntryField<Double> value2NumberField = new JNumberEntryField<Double>(0.0, 14, null, null, true);
            value2NumberField.setNumber(null);
            value2NumberField.setEnabled(false);
            andComboBox.addItemListener(new ItemListener() {
              public void itemStateChanged(ItemEvent e) {
                boolean isEnabled = andComboBox.getSelectedIndex() > 0;
                operator2ComboBox.setEnabled(isEnabled);
                value2NumberField.setEnabled(isEnabled);
              }
            });
            numberFilterContentPane.add(value2NumberField, new GridBagConstraints(2, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 0), 0, 0));
            if(numberFilter != null) {
              operator1ComboBox.setSelectedItem(numberFilter.getOperator1());
              value1NumberField.setNumber(numberFilter.getValue1());
              Operator operator2 = numberFilter.getOperator2();
              if(operator2 != null) {
                andComboBox.setSelectedIndex(numberFilter.isOr()? 2: 1);
                operator2ComboBox.setSelectedItem(operator2);
                value2NumberField.setNumber(numberFilter.getValue2());
              }
            }
            contentPane.add(numberFilterContentPane, BorderLayout.CENTER);
            JPanel southPane = new JPanel(new BorderLayout());
            southPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            JPanel buttonPane = new JPanel(new GridLayout(1, 2));
            JButton okButton = new JButton("OK");
            buttonPane.add(okButton);
            JButton cancelButton = new JButton("Cancel");
            buttonPane.add(cancelButton);
            southPane.add(buttonPane, BorderLayout.EAST);
            contentPane.add(southPane, BorderLayout.SOUTH);
            popupMenu.setVisible(false);
            final FilterDialog dialog = getModalDialog(filterableTableHeader.getTable(), contentPane, "Number filter");
            okButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                NumberCustomFilter newNumberFilter = new NumberCustomFilter();
                newNumberFilter.setFilter1((Operator)operator1ComboBox.getSelectedItem(), value1NumberField.getNumber());
                switch(andComboBox.getSelectedIndex()) {
                  case 0: newNumberFilter.setFilter2(null, null, false); break;
                  case 1: newNumberFilter.setFilter2((Operator)operator2ComboBox.getSelectedItem(), value2NumberField.getNumber(), false); break;
                  case 2: newNumberFilter.setFilter2((Operator)operator2ComboBox.getSelectedItem(), value2NumberField.getNumber(), true); break;
                }
                filterData.setNumberFilter(newNumberFilter);
                setFilterData(column, filterData);
                dialog.disposeDialog();
                filterableTableHeader.notifyFilterChanged(new int[] {column}, new TableHeaderFilter[] {headerFilter});
              }
            });
            cancelButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                dialog.disposeDialog();
              }
            });
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                value1NumberField.requestFocusInWindow();
              }
            });
            dialog.openDialog();
          }
        };
        numberFilterPane.add(numberFilterCheckBox, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, emptyInsets, 0, 0));
        final JLink<Void> configureLink = new JLink<Void>("(edit)", null);
        configureLink.setVisible(false);
        configureLink.addLinkListener(new LinkListener<Void>() {
          public boolean linkActivated(JLink<Void> link, Void target) {
            showDialogRunnable.run();
            return false;
          }
        });
        numberFilterPane.add(configureLink, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, emptyInsets, 0, 0));
        numberFilterCheckBox.addItemListener(new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            boolean isWithFilter = e.getStateChange() == ItemEvent.SELECTED;
            configureLink.setVisible(isWithFilter);
            if(numberFilter == null && isWithFilter) {
              showDialogRunnable.run();
            }
            filterTextField.setEnabled(!isWithFilter);
            for(JComponent component: mainFilterComponentList) {
              component.setEnabled(!isWithFilter);
            }
            isCustomFilterActive = isWithFilter;
          }
        });
        numberFilterCheckBox.setSelected(isCustomFilterActive);
        numberFilterPane.add(Box.createHorizontalGlue(), new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, emptyInsets, 0, 0));
        centerPane.add(numberFilterPane, BorderLayout.SOUTH);
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

  protected static interface FilterDialog {
    public void openDialog();
    public void disposeDialog();
  }
  
  private static class DefaultFilterDialog extends JDialog implements FilterDialog {
    public void openDialog() {
      setVisible(true);
    }
    public void disposeDialog() {
      dispose();
    }
  }
  
  protected FilterDialog getModalDialog(JTable table, JComponent content, String title) {
    DefaultFilterDialog dialog = new DefaultFilterDialog();
    dialog.setModal(true);
    dialog.setTitle(title);
    dialog.getContentPane().add(content, BorderLayout.CENTER);
    dialog.pack();
    dialog.setMinimumSize(dialog.getPreferredSize());
    dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(table));
    return dialog;
  }
  
  public boolean isFilterActive(int column) {
    return columnToFilterDataMap.containsKey(column);
  }

  private Map<Integer, FilterData> columnToFilterDataMap = new HashMap<Integer, FilterData>();

  private FilterData getFilterData(int column) {
    return columnToFilterDataMap.get(column);
  }

  private void setFilterData(int column, FilterData filterData) {
    if(filterData == null) {
      columnToFilterDataMap.remove(column);
    } else {
      columnToFilterDataMap.put(column, filterData);
    }
  }

  public JPopupMenu getFilterEditor(FilterableTableHeader filterableTableHeader, TableModel tableModel, int column, int[] rows, final Comparator<Object> valueComparator) {
    Map<Object, String> valueToTextMap = new HashMap<Object, String>();
    for(int i=0; i<rows.length; i++) {
      int row = rows[i];
      Object o = tableModel.getValueAt(row, column);
      valueToTextMap.put(o, convertToString(o, tableModel, row, column));
    }
    Object[] values = valueToTextMap.keySet().toArray();
    Arrays.sort(values, new Comparator<Object>() {
      public int compare(Object o1, Object o2) {
        return o1 == null? o2 == null? 0: -1: o2 == null? -1: valueComparator.compare(o1, o2);
      }
    });
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
    FilterData filterData = getFilterData(column);
    return filterData == null || filterData.include(entry, column);
  }
  
  public void clearFilter(int column) {
    setFilterData(column, null);
  }

}
