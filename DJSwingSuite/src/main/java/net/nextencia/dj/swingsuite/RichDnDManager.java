/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.dj.swingsuite;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.JWindow;
import javax.swing.ListCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

import com.sun.jna.Native;

import net.nextencia.dj.swingsuite.jna.platform.WindowUtils;

/**
 * A class that provides overlay image during drag and drop operations.
 * @author Christopher Deckers
 */
public class RichDnDManager {

  /**
   * A provider that defines the image to use during a drag and drop operation.
   * @author Christopher Deckers
   */
  public static interface RichDnDImageProvider {
    /**
     * Get the image to use during a drag and drop.
     * @param c the component that is being dragged.
     * @param x the visible x coordinate of the component.
     * @param y the visible y coordinate of the component.
     * @param width the visible width of the component.
     * @param height the visible height of the component.
     * @param mouseLocation The location of the mouse within the returned image, which can be altered when the image is returned.
     * @return the image to use.
     */
    public Icon getDnDImage(Component c, int x, int y, int width, int height, Point mouseLocation);
  }

  private static final RichDnDImageProvider DEFAULT_IMAGE_PROVIDER = new RichDnDImageProvider() {
    public Icon getDnDImage(Component c, int x, int y, int width, int height, Point mouseLocation) {
      BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics g = img.getGraphics();
      g.translate(-x, -y);
      c.paint(g);
      g.dispose();
      return new ImageIcon(img);
    }
  };

  private static final RichDnDImageProvider TREE_IMAGE_PROVIDER = new RichDnDImageProvider() {
    public Icon getDnDImage(Component c, int x, int y, int width, int height, Point mouseLocation) {
      BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics g = img.getGraphics();
      g.translate(-x, -y);
      JTree tree = (JTree)c;
      int leadIndex;
      if(tree.hasFocus()) {
        leadIndex = tree.getLeadSelectionRow();
      } else {
        leadIndex = -1;
      }
      TreeCellRenderer cellRenderer = tree.getCellRenderer();
      TreeModel treeModel = tree.getModel();
      int[] selectionRows = tree.getSelectionRows();
      for(int row: selectionRows == null? new int[0]: selectionRows) {
        Rectangle rowBounds = tree.getRowBounds(row);
        g.translate(rowBounds.x, rowBounds.y);
        Object lastPathComponent = tree.getPathForRow(row).getLastPathComponent();
        Component rendererComponent = cellRenderer.getTreeCellRendererComponent(tree, lastPathComponent, true, tree.isExpanded(row), treeModel.isLeaf(lastPathComponent), row, leadIndex == row);
        rendererComponent.setSize(rowBounds.width, rowBounds.height);
        rendererComponent.invalidate();
        rendererComponent.validate();
        rendererComponent.paint(g);
        g.translate(-rowBounds.x, -rowBounds.y);
      }
      g.dispose();
      return new ImageIcon(img);
    }
  };

  private static final RichDnDImageProvider LIST_IMAGE_PROVIDER = new RichDnDImageProvider() {
    public Icon getDnDImage(Component c, int x, int y, int width, int height, Point mouseLocation) {
      BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics g = img.getGraphics();
      g.translate(-x, -y);
      JList list = (JList)c;
      int leadIndex;
      if(list.hasFocus()) {
        leadIndex = list.getLeadSelectionIndex();
      } else {
        leadIndex = -1;
      }
      ListCellRenderer cellRenderer = list.getCellRenderer();
      int[] selectedIndices = list.getSelectedIndices();
      for(int index: selectedIndices == null? new int[0]: selectedIndices) {
        Rectangle rowBounds = list.getCellBounds(index, index);
        g.translate(rowBounds.x, rowBounds.y);
        Component rendererComponent = cellRenderer.getListCellRendererComponent(list, list.getModel().getElementAt(index), index, true, leadIndex == index);
        rendererComponent.setSize(rowBounds.width, rowBounds.height);
        rendererComponent.invalidate();
        rendererComponent.validate();
        rendererComponent.paint(g);
        g.translate(-rowBounds.x, -rowBounds.y);
      }
      g.dispose();
      return new ImageIcon(img);
    }
  };

  private static String IMAGE_PROVIDER_PROPERTY = "richDnDImageProvider";

  private static boolean isValid;
  private static boolean isActive;
  private static DragSource dragSource;
  private static RichDragSourceListener richDragSourceListener;
  private static Method setWindowOpaqueMethod;

  public static void unregister(JComponent c) {
    c.putClientProperty(IMAGE_PROVIDER_PROPERTY, null);
  }

  public static void register(JComponent c) {
    RichDnDImageProvider imageProvider = DEFAULT_IMAGE_PROVIDER;
    if(c instanceof RichDnDImageProvider) {
      imageProvider = (RichDnDImageProvider)c;
    } else if(c instanceof JTree) {
      imageProvider = TREE_IMAGE_PROVIDER;
    } else if(c instanceof JList) {
      imageProvider = LIST_IMAGE_PROVIDER;
    }
    register(c, imageProvider);
  }

  public static void register(JComponent c, RichDnDImageProvider imageProvider) {
    if(!isActive) {
      setActive(true);
    }
    c.putClientProperty(IMAGE_PROVIDER_PROPERTY, imageProvider);
  }

  private RichDnDManager() {}

  private static class RichDragSourceListener implements DragSourceListener, DragSourceMotionListener {

    private boolean isStarted;
    private static JWindow dragWindow;
    private Rectangle clip;
    private Point dragOrigin;

    private void startDnD(DragSourceDragEvent dsde) {
      if(!isValid) {
        return;
      }
      if(isStarted) {
        adjusteWindowLocation(dsde);
      } else {
        isStarted = true;
        DragGestureEvent trigger = dsde.getDragSourceContext().getTrigger();
        Component c = trigger.getComponent();
        if(!(c instanceof JComponent)) {
          return;
        }
        RichDnDImageProvider imageProvider = (RichDnDImageProvider)((JComponent)c).getClientProperty(IMAGE_PROVIDER_PROPERTY);
        if(imageProvider == null) {
          return;
        }
        dragOrigin = trigger.getDragOrigin();
        clip = getClip(c);
        Icon icon = imageProvider.getDnDImage(c, clip.x, clip.y, clip.width, clip.height, dragOrigin);
        if(icon != null) {
          dragWindow = new JWindow() {
            @Override
            public boolean contains(int x, int y) {
              return false;
            }
            @Override
            public void paint(Graphics g) {
              if(!(g instanceof Graphics2D)) {
                dispose();
              }
              Graphics2D g2d = (Graphics2D)g;
              g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.4f));
              super.paint(g);
              g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
              int x = dragOrigin.x - clip.x;
              int y = dragOrigin.y - clip.y;
              // Not needed when we have click through using JNA (Windows only).
              g.drawLine(x, y, x, y);
            }
          };
          dragWindow.getContentPane().add(new JLabel(icon));
          dragWindow.pack();
          adjusteWindowLocation(dsde);
          try {
            if(setWindowOpaqueMethod == null) {
              dragWindow.setBackground(new Color(0, 0, 0, 0));
            } else {
              setWindowOpaqueMethod.invoke(null, dragWindow, false);
            }
          } catch(Throwable t) {
            t.printStackTrace();
            dispose();
            return;
          }
          if(System.getProperty("os.name").startsWith("Windows")) {
            try {
              Class.forName("com.sun.jna.Native");
              Class.forName("com.sun.jna.examples.WindowUtils");
              WindowUtils.setWindowClickThrough(dragWindow, true);
            } catch (ClassNotFoundException e) {
            }
          }
          dragWindow.setVisible(true);
        } else {
          dispose();
        }
      }
    }
    
    private void stopDnD() {
      if(isStarted) {
        isStarted = false;
        dispose();
      }
    }

    private void dispose() {
      if(dragWindow != null) {
        dragWindow.dispose();
        dragWindow = null;
      }
      clip = null;
      dragOrigin = null;
    }

    private void adjusteWindowLocation(DragSourceDragEvent dsde) {
      if(dragWindow != null) {
        Point location = dsde.getLocation();
        dragWindow.setLocation(location.x - dragOrigin.x + clip.x, location.y - dragOrigin.y + clip.y);
      }
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
      stopDnD();
    }

    public void dragMouseMoved(DragSourceDragEvent dsde) {
      startDnD(dsde);
    }

    public void dragEnter(DragSourceDragEvent dsde) {
    }

    public void dragExit(DragSourceEvent dse) {
    }

    public void dragOver(DragSourceDragEvent dsde) {
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

  }

  private static Rectangle getClip(Component c) {
    Container parent = c.getParent();
    if(parent != null) {
      Rectangle parentClip = getClip(parent);
      Point location = c.getLocation();
      parentClip.x -= location.x;
      parentClip.y -= location.y;
      return parentClip.intersection(new Rectangle(c.getWidth(), c.getHeight()));
    }
    return new Rectangle(c.getWidth(), c.getHeight());
  }

  private static void setActive(boolean isActive) {
    if(RichDnDManager.isActive == isActive) {
      return;
    }
    if(isActive) {
      RichDnDManager.isValid = false;
      try {
        Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
        try {
          Class<?> awtUtilitiesTranslucencyClass = Class.forName("com.sun.awt.AWTUtilities$Translucency");
          if(!(Boolean)awtUtilitiesClass.getDeclaredMethod("isTranslucencySupported", awtUtilitiesTranslucencyClass).invoke(null, awtUtilitiesTranslucencyClass.getDeclaredField("PERPIXEL_TRANSLUCENT").get(null))) {
            return;
          }
          Method isTranslucencyCapableMethod = awtUtilitiesClass.getDeclaredMethod("isTranslucencyCapable", GraphicsConfiguration.class);
          for(GraphicsDevice device: GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            for(GraphicsConfiguration config: device.getConfigurations()) {
              boolean isTranslucencyCapable = (Boolean)isTranslucencyCapableMethod.invoke(null, config);
              if(!isTranslucencyCapable) {
                return;
              }
            }
          }
          setWindowOpaqueMethod = awtUtilitiesClass.getDeclaredMethod("setWindowOpaque", Window.class, boolean.class);
        } catch(Throwable t) {
          return;
        }
      } catch(Throwable t) {
        // New official API (Java 7)
        try {
          Class<?> windowTranslucencyClass = Class.forName("java.awt.GraphicsDevice$WindowTranslucency");
          Object perPixelTranslucent = windowTranslucencyClass.getDeclaredField("PERPIXEL_TRANSLUCENT").get(null);
          Method isTranslucencyCapableMethod = GraphicsConfiguration.class.getDeclaredMethod("isTranslucencyCapable");
          for(GraphicsDevice device: GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            if(!(Boolean)GraphicsDevice.class.getDeclaredMethod("isWindowTranslucencySupported", windowTranslucencyClass).invoke(device, perPixelTranslucent)) {
              return;
            }
            for(GraphicsConfiguration config: device.getConfigurations()) {
              if(!(Boolean)isTranslucencyCapableMethod.invoke(config)) {
                return;
              }
            }
          }
        } catch(Throwable th) {
          return;
        }
      }
      RichDnDManager.isValid = true;
      dragSource = DragSource.getDefaultDragSource();
      richDragSourceListener = new RichDragSourceListener();
      dragSource.addDragSourceListener(richDragSourceListener);
      dragSource.addDragSourceMotionListener(richDragSourceListener);
    } else {
      dragSource.removeDragSourceListener(richDragSourceListener);
      dragSource.removeDragSourceMotionListener(richDragSourceListener);
    }
    RichDnDManager.isActive = isActive;
  }

}
