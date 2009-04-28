/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * A tree that allows a user to start a mouse drag in an empty area to make a rectangular selection. Note that the shift and control modifiers modify the selection behavior.
 * @author Christopher Deckers
 */
public class JSelectableTree extends JTree {

  private static class SelectionData {

    private Point pressedLocation;
    private int modifiers;
    private int[] selectedRows;

    public SelectionData(Point pressedLocation, int modifiers, int[] selectedRows) {
      this.pressedLocation = pressedLocation;
      this.modifiers = modifiers;
      this.selectedRows = selectedRows;
    }

    private Point currentLocation;

    public void setCurrentLocation(Point currentLocation) {
      this.currentLocation = currentLocation;
    }

    public Point getCurrentLocation() {
      return currentLocation;
    }

    public Point getPressedLocation() {
      return pressedLocation;
    }

    public int getModifiers() {
      return modifiers;
    }

    public int[] getSelectedRows() {
      return selectedRows;
    }

  }

  public JSelectableTree() {
    init();
  }

  public JSelectableTree(Hashtable<?, ?> value) {
    super(value);
    init();
  }

  public JSelectableTree(Object[] value) {
    super(value);
    init();
  }

  public JSelectableTree(TreeModel newModel) {
    super(newModel);
    init();
  }

  public JSelectableTree(TreeNode root) {
    super(root);
    init();
  }

  public JSelectableTree(TreeNode root, boolean asksAllowsChildren) {
    super(root, asksAllowsChildren);
    init();
  }

  public JSelectableTree(Vector<?> value)  {
    super(value);
    init();
  }

  private SelectionData selectionData;

  private void init() {
    MouseInputAdapter mouseInputListener = new MouseInputAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if(getSelectionModel().getSelectionMode() == TreeSelectionModel.SINGLE_TREE_SELECTION) {
          return;
        }
        if(e.getButton() == MouseEvent.BUTTON1 && getRowForLocation(e.getX(), e.getY()) < 0) {
          int[] selectedRows = getSelectionRows();
          if(selectedRows == null) {
            selectedRows = new int[0];
          }
          selectionData = new SelectionData(e.getPoint(), e.getModifiersEx(), selectedRows);
        }
      }
      @Override
      public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
          selectionData = null;
          repaint();
        }
      }
      @Override
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 1 && !e.isControlDown() && !e.isShiftDown()) {
          int closestRowForLocation = getClosestRowForLocation(e.getX(), e.getY());
          boolean isClearing = true;
          if(closestRowForLocation >= 0) {
            Rectangle rowBounds = getRowBounds(closestRowForLocation);
            if(getComponentOrientation().isLeftToRight()) {
              rowBounds.width += rowBounds.x;
              rowBounds.x = 0;
            } else {
              rowBounds.width += getWidth() - rowBounds.x;
            }
            if(rowBounds.contains(e.getPoint())) {
              isClearing = false;
            }
          }
          if(isClearing) {
            clearSelection();
          }
        }
      }
      @Override
      public void mouseDragged(MouseEvent e) {
        if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0 && selectionData != null) {
          Point currentLocation = e.getPoint();
          selectionData.setCurrentLocation(currentLocation);
          Point pressedLocation = selectionData.getPressedLocation();
          int x1 = Math.max(0, Math.min(pressedLocation.x, currentLocation.x));
          int x2 = Math.max(pressedLocation.x, currentLocation.x);
          int y1 = Math.max(0, Math.min(pressedLocation.y, currentLocation.y));
          int y2 = Math.max(pressedLocation.y, currentLocation.y);
          int row1 = Math.max(0, getClosestRowForLocation(x1, y1));
          int row2 = Math.max(0, getClosestRowForLocation(x2, y2));
          Set<Integer> newRowSet = new HashSet<Integer>();
          int modifiers = selectionData.getModifiers();
          boolean isControl = (modifiers & MouseEvent.CTRL_DOWN_MASK) != 0;
          boolean isShift = (modifiers & MouseEvent.SHIFT_DOWN_MASK) != 0;
          if(getSelectionModel().getSelectionMode() == TreeSelectionModel.CONTIGUOUS_TREE_SELECTION) {
            isControl = false;
            isShift = false;
          }
          int[] originalSelectedRows = selectionData.getSelectedRows();
          if(isControl || isShift) {
            for(int row: originalSelectedRows) {
              newRowSet.add(row);
            }
          }
          Rectangle rectangle = new Rectangle(x1, y1, x2 - x1, y2 - y1);
          int[] newRows = isControl || isShift? originalSelectedRows: new int[0];
          boolean isSelecting = false;
          for(int i=row1; i<=row2; i++) {
            if(!isSelecting && getRowBounds(i).intersects(rectangle)) {
              isSelecting = true;
            }
            if(isControl && newRowSet.contains(i)) {
              newRowSet.remove(i);
            } else {
              newRowSet.add(i);
            }
          }
          if(isSelecting) {
            Integer[] newRowObjects = newRowSet.toArray(new Integer[0]);
            newRows = new int[newRowObjects.length];
            for(int i=0; i<newRows.length; i++) {
              newRows[i] = newRowObjects[i];
            }
            Arrays.sort(newRows);
          }
          int[] rows = getSelectionRows();
          if(rows != newRows && !Arrays.equals(rows, newRows)) {
            setSelectionRows(newRows);
          }
          repaint();
          final int x = e.getX();
          final int y = e.getY();
          final int closestRowForLocation = getClosestRowForLocation(x, y);
          if(closestRowForLocation >= 0) {
            new Thread() {
              @Override
              public void run() {
                try {
                  sleep(50);
                } catch(Exception e) {
                }
                SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                    Rectangle bounds = getRowBounds(closestRowForLocation);
                    if(bounds != null) {
                      bounds.x = x;
                      if(getVisibleRect().x + 10 > x) {
                        bounds.x -= 15;
                      }
                      bounds.width = 1;
                      scrollRectToVisible(bounds);
                    }
                  }
                });
              }
            }.start();
          }
        }
      }
    };
    addMouseListener(mouseInputListener);
    addMouseMotionListener(mouseInputListener);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if(selectionData == null) {
      return;
    }
    Point pressedLocation = selectionData.getPressedLocation();
    Point currentLocation = selectionData.getCurrentLocation();
    if(pressedLocation != null && currentLocation != null && !pressedLocation.equals(currentLocation)) {
      int x1 = Math.min(pressedLocation.x, currentLocation.x);
      int x2 = Math.max(pressedLocation.x, currentLocation.x);
      int y1 = Math.min(pressedLocation.y, currentLocation.y);
      int y2 = Math.max(pressedLocation.y, currentLocation.y);
      Color color = UIManager.getColor("Tree.selectionBackground");
      if(color == null) {
        color = Color.BLUE;
      }
      if(color.getRed() > 128 && color.getGreen() > 128 && color.getBlue() > 128) {
        color = color.darker();
      }
      g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 70));
      g.fillRect(x1, y1, x2 - x1, y2 - y1);
      g.setColor(color);
      g.drawRect(x1, y1, x2 - x1, y2 - y1);
    }
  }

}
