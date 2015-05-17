/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EventListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * @author Christopher Deckers
 */
public class FilterableTableHeader extends JTableHeader {

  public interface TableHeaderFilter {
    public JPopupMenu getFilterEditor(FilterableTableHeader filterableTableHeader, TableModel tableModel, int column, int[] rows, Comparator<Object> valueComparator);
    public boolean isFilterActive(int column);
    public boolean include(RowFilter.Entry<? extends TableModel, ? extends Integer> entry, int column);
    public void clearFilter(int column);
  }

  private static final ImageIcon FILTER_ICON = new ImageIcon(FilterableTableHeader.class.getResource("resource/TableColumnFilter16x12.png"));
  private static final ImageIcon FILTER_ACTIVE_ICON = new ImageIcon(FilterableTableHeader.class.getResource("resource/TableColumnFilterOn16x12.png"));

  private static final int ICON_WIDTH = 16;
  private static final int ICON_HEIGHT = 12;

  private static final int BOTTOM_OFFSET = 4;
  private static final int RIGHT_OFFSET = 4;

  public FilterableTableHeader(JTable table) {
    super(table.getColumnModel());
    if(table.getRowSorter() == null) {
      throw new IllegalStateException("The table does not have a row sorter, which is mandatory to have a filtering header!");
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if(activeFilterIndexes.length > 0 || location != null || columnWithPopupVisible >= 0) {
      TableColumnModel columnModel = getColumnModel();
      int height = getHeight();
      int offset = 0;
      int columnCount = columnModel.getColumnCount();
      for(int i=0; i<columnCount; i++) {
        int width = columnModel.getColumn(i).getWidth();
        int modelColumn = table.convertColumnIndexToModel(i);
        if(modelColumn < headerFilters.length && headerFilters[modelColumn] != null) {
          if(columnWithPopupVisible == i) {
            g.drawImage(FILTER_ICON.getImage(), offset + width - RIGHT_OFFSET - ICON_WIDTH, height - BOTTOM_OFFSET - ICON_HEIGHT, null);
            if(activeFilterIndexes.length == 0) {
              break;
            }
          } else if(hoveredColumn == i) {
            ImageIcon icon;
            if(!isOnHitZone) {
              if(headerFilters[modelColumn].isFilterActive(modelColumn)) {
                icon = FILTER_ACTIVE_ICON;
              } else {
                ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
                icon = FILTER_ICON;
              }
            } else {
              icon = FILTER_ICON;
            }
            g.drawImage(icon.getImage(), offset + width - RIGHT_OFFSET - ICON_WIDTH, height - BOTTOM_OFFSET - ICON_HEIGHT, null);
            ((Graphics2D)g).setPaintMode();
            if(activeFilterIndexes.length == 0) {
              break;
            }
          } else if(headerFilters[modelColumn].isFilterActive(modelColumn)) {
            ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
            g.drawImage(FILTER_ACTIVE_ICON.getImage(), offset + width - RIGHT_OFFSET - ICON_WIDTH, height - BOTTOM_OFFSET - ICON_HEIGHT, null);
            ((Graphics2D)g).setPaintMode();
          }
        }
        offset += width;
      }
    }
  }

  private int columnWithPopupVisible = -1;
  private Point location;

  private int hoveredColumn = -1;
  private boolean isOnHitZone;

  private void adjustZones() {
    TableColumnModel columnModel = getColumnModel();
    int height = getHeight();
    int offset = 0;
    int columnCount = columnModel.getColumnCount();
    int hoveredColumn = -1;
    boolean isOnHitZone = false;
    if(location != null) {
      for(int i=0; i<columnCount; i++) {
        int width = columnModel.getColumn(i).getWidth();
        int modelIndex = table.convertColumnIndexToModel(i);
        if(modelIndex < headerFilters.length && headerFilters[modelIndex] != null && location.x > offset && location.x <= offset + width) {
          hoveredColumn = i;
          isOnHitZone = location.x >= offset + width - RIGHT_OFFSET - ICON_WIDTH && location.x < offset + width - RIGHT_OFFSET && location.y >= height - BOTTOM_OFFSET - ICON_HEIGHT && location.y < height - BOTTOM_OFFSET;
          break;
        }
        offset += width;
      }
    }
    if(this.hoveredColumn != hoveredColumn || this.isOnHitZone != isOnHitZone) {
      this.hoveredColumn = hoveredColumn;
      this.isOnHitZone = isOnHitZone;
    }
  }

  @Override
  protected void processMouseEvent(MouseEvent e) {
    switch(e.getID()) {
      case MouseEvent.MOUSE_PRESSED:
        if(isOnHitZone) {
          if(e.getButton() == MouseEvent.BUTTON1) {
            showFilterPopup(e);
          } else if(e.isPopupTrigger()) {
            showPopup(e);
          }
          return;
        }
        break;
      case MouseEvent.MOUSE_RELEASED:
        if(isOnHitZone && e.isPopupTrigger()) {
          showPopup(e);
          return;
        }
        break;
      case MouseEvent.MOUSE_CLICKED:
        if(isOnHitZone) {
          if(e.isPopupTrigger()) {
            showPopup(e);
          }
          return;
        }
        break;
    }
    super.processMouseEvent(e);
    processMouseEvent_(e);
  }
  
  private void showPopup(MouseEvent e) {
    columnWithPopupVisible = hoveredColumn;
    repaint();
    final int modelColumn = table.convertColumnIndexToModel(hoveredColumn);
    final TableHeaderFilter headerFilter = headerFilters[modelColumn];
    boolean hasOtherFilters = false;
    for(int i=0; i<activeFilterIndexes.length; i++) {
      if(activeFilterIndexes[i] != modelColumn) {
        hasOtherFilters = true;
        break;
      }
    }
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem clearFilterMenuItem = new JMenuItem(getClearFilterLabelText());
    clearFilterMenuItem.setEnabled(headerFilter.isFilterActive(modelColumn));
    clearFilterMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        headerFilter.clearFilter(modelColumn);
        notifyFilterChanged(new int[] {modelColumn}, new TableHeaderFilter[] {headerFilter});
        repaint();
      }
    });
    popupMenu.add(clearFilterMenuItem);
    JMenuItem clearAllFiltersMenuItem = new JMenuItem(getClearAllFiltersLabelText());
    clearAllFiltersMenuItem.setEnabled(hasOtherFilters);
    clearAllFiltersMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int[] columns = activeFilterIndexes;
        TableHeaderFilter[] clearedHeaderFilters = new TableHeaderFilter[activeFilterIndexes.length];
        for(int i=activeFilterIndexes.length-1; i>=0; i--) {
          int column = activeFilterIndexes[i];
          TableHeaderFilter headerFilter = headerFilters[column];
          headerFilter.clearFilter(column);
          clearedHeaderFilters[i] = headerFilter;
        }
        activeFilterIndexes = new int[0];
        notifyFilterChanged(columns, clearedHeaderFilters);
        repaint();
      }
    });
    popupMenu.add(clearAllFiltersMenuItem);
    popupMenu.show(e.getComponent(), e.getX(), e.getY());
    popupMenu.addPopupMenuListener(new PopupMenuListener() {
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
      }
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        columnWithPopupVisible = -1;
        location = null;
        adjustZones();
        repaint();
      }
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    });
  }
  
  protected String getClearFilterLabelText() {
    return "Clear filter";
  }

  protected String getClearAllFiltersLabelText() {
    return "Clear all filters";
  }
  
  @Override
  protected void processMouseMotionEvent(MouseEvent e) {
    super.processMouseMotionEvent(e);
    processMouseEvent_(e);
  }

  private void processMouseEvent_(MouseEvent e) {
    Point newLocation = null;
    switch(e.getID()) {
      case MouseEvent.MOUSE_ENTERED:
      case MouseEvent.MOUSE_MOVED:
      case MouseEvent.MOUSE_RELEASED:
      case MouseEvent.MOUSE_CLICKED:
        newLocation = e.getPoint();
        break;
    }
    if(newLocation != null && !newLocation.equals(location) || newLocation == null && location != null) {
      location = newLocation;
      adjustZones();
      repaint();
    }
  }

  private class FilterEntry extends RowFilter.Entry<TableModel, Integer> {
    private int modelIndex;
    public void setModelIndex(int modelIndex) {
      this.modelIndex = modelIndex;
    }
    @Override
    public TableModel getModel() {
      return ((TableRowSorter<TableModel>)table.getRowSorter()).getModel();
    }
    @Override
    public int getValueCount() {
      return getModel().getColumnCount();
    }
    @Override
    public Object getValue(int index) {
      return getModel().getValueAt(modelIndex, index);
    }
    @Override
    public String getStringValue(int index) {
      return ((TableRowSorter<TableModel>)table.getRowSorter()).getStringConverter().toString(getModel(), modelIndex, index);
    }
    @Override
    public Integer getIdentifier() {
      return modelIndex;
    }
  }

  private void showFilterPopup(MouseEvent e) {
    int modelColumn = table.convertColumnIndexToModel(hoveredColumn);
    TableHeaderFilter headerFilter = headerFilters[modelColumn];
    TableRowSorter<TableModel> rowSorter = (TableRowSorter<TableModel>)table.getRowSorter();
    TableModel model = rowSorter.getModel();
    int otherFilterCount = 0;
    for(int i=0; i<activeFilterIndexes.length; i++) {
      if(activeFilterIndexes[i] != modelColumn) {
        otherFilterCount++;
      }
    }
    int[] rows;
    if(otherFilterCount == 0) {
      rows = new int[model.getRowCount()];
      for(int i=0; i<rows.length; i++) {
        rows[i] = i;
      }
    } else {
      List<Integer> rowList = new ArrayList<Integer>();
      FilterEntry filterEntry = new FilterEntry();
      for(int i=model.getRowCount()-1; i>=0; i--) {
        filterEntry.setModelIndex(i);
        boolean isIncluded = true;
        for(int j=0; j<activeFilterIndexes.length; j++) {
          int filterColumn = activeFilterIndexes[j];
          if(activeFilterIndexes[j] != modelColumn) {
            if(!headerFilters[filterColumn].include(filterEntry, filterColumn)) {
              isIncluded = false;
              break;
            }
          }
        }
        if(isIncluded) {
          rowList.add(i);
        }
      }
      rows = new int[rowList.size()];
      for(int i=0; i<rows.length; i++) {
        rows[i] = rowList.get(i);
      }
    }
    JPopupMenu popupMenu = headerFilter.getFilterEditor(this, model, modelColumn, rows, (Comparator<Object>)rowSorter.getComparator(modelColumn));
    int offset = 0;
    int columnCount = columnModel.getColumnCount();
    int x = 0;
    for(int i=0; i<columnCount; i++) {
      int width = columnModel.getColumn(i).getWidth();
      if(hoveredColumn == i) {
        x = offset + width;
        break;
      }
      offset += width;
    }
    Dimension preferredSize = popupMenu.getPreferredSize();
    popupMenu.addNotify();
    popupMenu.show(e.getComponent(), x - preferredSize.width - RIGHT_OFFSET, getHeight() - BOTTOM_OFFSET);
    columnWithPopupVisible = hoveredColumn;
    repaint();
    popupMenu.addPopupMenuListener(new PopupMenuListener() {
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
      }
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        columnWithPopupVisible = -1;
        location = null;
        adjustZones();
        repaint();
      }
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    });
  }

  public void notifyFilterChanged(int[] columns, TableHeaderFilter[] headerFilters) {
    for(int i=0; i<columns.length; i++) {
      adjustFilterActiveIndexes(columns[i], headerFilters[i]);
    }
    for(TableHeaderFilterChangeListener listener: getFilterChangeListeners()) {
      listener.processFilterModification(columns);
    }
  }

  private TableHeaderFilter[] headerFilters = new TableHeaderFilter[0];

  public void setHeaderFilter(int column, TableHeaderFilter headerFilter) {
    if(headerFilters.length <= column) {
      TableHeaderFilter[] newHeaderFilters = new TableHeaderFilter[column + 1];
      System.arraycopy(headerFilters, 0, newHeaderFilters, 0, headerFilters.length);
      headerFilters = newHeaderFilters;
    }
    headerFilters[column] = headerFilter;
    if(headerFilter == null) {
      int newLength = headerFilters.length;
      for(; newLength>0; newLength--) {
        if(headerFilters[newLength - 1] != null) {
          break;
        }
      }
      if(newLength != headerFilters.length) {
        TableHeaderFilter[] newHeaderFilters = new TableHeaderFilter[newLength];
        System.arraycopy(headerFilters, 0, newHeaderFilters, 0, newLength);
        headerFilters = newHeaderFilters;
      }
    }
    adjustFilterActiveIndexes(column, headerFilter);
  }

  private void adjustFilterActiveIndexes(int column, TableHeaderFilter headerFilter) {
    for(int i=0; i<activeFilterIndexes.length; i++) {
      if(activeFilterIndexes[i] == column) {
        if(headerFilter != null && headerFilter.isFilterActive(column)) {
          return;
        }
        int[] newActiveFilterIndexes = new int[activeFilterIndexes.length - 1];
        System.arraycopy(activeFilterIndexes, 0, newActiveFilterIndexes, 0, i);
        System.arraycopy(activeFilterIndexes, i + 1, newActiveFilterIndexes, i, newActiveFilterIndexes.length - i);
        activeFilterIndexes = newActiveFilterIndexes;
        break;
      }
    }
    if(headerFilter != null && headerFilter.isFilterActive(column)) {
      int[] newActiveFilterIndexes = new int[activeFilterIndexes.length + 1];
      System.arraycopy(activeFilterIndexes, 0, newActiveFilterIndexes, 0, activeFilterIndexes.length);
      newActiveFilterIndexes[activeFilterIndexes.length] = column;
      activeFilterIndexes = newActiveFilterIndexes;
    }
  }

  private int[] activeFilterIndexes = new int[0];

  private RowFilter<TableModel, Integer> rowFilter = new RowFilter<TableModel, Integer>() {
    @Override
    public boolean include(RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
      if(activeFilterIndexes.length == 0) {
        return true;
      }
      for(int i=0; i<activeFilterIndexes.length; i++) {
        int column = activeFilterIndexes[i];
        TableHeaderFilter headerFilter = headerFilters[column];
        if(headerFilter != null && headerFilter.isFilterActive(column)) {
          if(!headerFilter.include(entry, column)) {
            return false;
          }
        }
      }
      return true;
    }
  };

  public RowFilter<TableModel, Integer> getRowFilter() {
    return rowFilter;
  }

  public static interface TableHeaderFilterChangeListener extends EventListener {
    public void processFilterModification(int[] columns);
  }

  public void addFilterChangeListener(TableHeaderFilterChangeListener listener) {
    listenerList.add(TableHeaderFilterChangeListener.class, listener);
  }

  public void removeFilterChangeListener(TableHeaderFilterChangeListener listener) {
    listenerList.remove(TableHeaderFilterChangeListener.class, listener);
  }

  public TableHeaderFilterChangeListener[] getFilterChangeListeners() {
    return listenerList.getListeners(TableHeaderFilterChangeListener.class);
  }

}
