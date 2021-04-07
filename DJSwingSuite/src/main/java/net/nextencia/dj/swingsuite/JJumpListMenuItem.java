/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.dj.swingsuite;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.UIManager;
import javax.swing.plaf.MenuItemUI;

/**
 * A menu item that can present sub-items (a.k.a jump list) in addition to having a default action.
 * @author Christopher Deckers
 */
public class JJumpListMenuItem extends JMenuItem {

  private static final Icon arrowIcon;
  
  static {
    arrowIcon = UIManager.getIcon("SwingSuiteJumpListMenuItem.arrowIcon");
  }
  
  /**
   * Construct a jump list menu item.
   */
  public JJumpListMenuItem() {
    super();
    init();
  }

  /**
   * Construct a jump list menu item with some text.
   * @param text the text to use.
   */
  public JJumpListMenuItem(String text) {
    super(text);
    init();
  }

  /**
   * Construct a jump list menu item with some text and a keayboard mnemonic.
   * @param text the text to use.
   * @param mnemonic the mnemonic to use.
   */
  public JJumpListMenuItem(String text, int mnemonic) {
    super(text, mnemonic);
    init();
  }

  /**
   * Construct a jump list menu item with an icon and some text.
   * @param text the text to use.
   * @param icon the icon to use.
   */
  public JJumpListMenuItem(String text, Icon icon) {
    super(text, icon);
    init();
  }

  /**
   * Construct a jump list menu item with an action.
   * @param action the action to use.
   */
  public JJumpListMenuItem(Action action) {
    super(action);
    init();
  }

  /**
   * Construct a jump list menu item with an icon.
   * @param icon the icon to use.
   */
  public JJumpListMenuItem(Icon icon) {
    super(icon);
    init();
  }

  private int arrowWidth;
  private int arrowSpaceWidth;

  private void init() {
    enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    if(arrowIcon != null) {
      arrowWidth = arrowIcon.getIconWidth();
    } else {
      arrowWidth = getPreferredSize().height / 4;
      arrowWidth -= (arrowWidth + 1) % 2;
    }
    arrowSpaceWidth = arrowWidth + 4;
    setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
  }

  private boolean isArrowMouseOver;

  @Override
  protected void processMouseEvent(MouseEvent e) {
    switch(e.getID()) {
      case MouseEvent.MOUSE_PRESSED:
      case MouseEvent.MOUSE_RELEASED:
      case MouseEvent.MOUSE_CLICKED:
        if(isArrowMouseOver) {
          e.consume();
          return;
        }
    }
    if(e.getID() != MouseEvent.MOUSE_RELEASED) {
      adjustPopupVisibility(e);
    }
    super.processMouseEvent(e);
  }

  @Override
  protected void processMouseMotionEvent(MouseEvent e) {
    adjustPopupVisibility(e);
    super.processMouseMotionEvent(e);
  }

  private void adjustPopupVisibility(MouseEvent e) {
    if(jumpListMenu == null || !isEnabled()) {
      return;
    }
    boolean isShowing = jumpListMenu.getPopupMenu().isShowing();
    boolean oldIsArrowMouseOver = isArrowMouseOver;
    isArrowMouseOver = isArrowMouseOver(e);
    if(oldIsArrowMouseOver != isArrowMouseOver) {
      repaint();
    }
    if(isArrowMouseOver) {
      MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
      MenuElement path[] = defaultManager.getSelectedPath();
      if(path.length > 0 && path[path.length-1] == this) {
        MenuElement newPath[] = new MenuElement[path.length + 2];
        System.arraycopy(path, 0, newPath, 0, path.length);
        newPath[path.length] = jumpListMenu;
        newPath[path.length + 1] = jumpListMenu.getPopupMenu();
        MenuSelectionManager.defaultManager().setSelectedPath(newPath);
      }
      if(isShowing) {
        return;
      }
      Point popupMenuOrigin = getPopupMenuOrigin();
      jumpListMenu.getPopupMenu().show(jumpListMenu, popupMenuOrigin.x, popupMenuOrigin.y);
    } else {
      if(isShowing) {
        jumpListMenu.getPopupMenu().setVisible(false);
      }
    }
  }

  private boolean isArrowMouseOver(MouseEvent e) {
    if(jumpListMenu == null) {
      return false;
    }
    boolean isArrowMouseOver = false;
    if(getComponentOrientation().isLeftToRight()) {
      int right = getBorder().getBorderInsets(e.getComponent()).right + arrowSpaceWidth + 3;
      isArrowMouseOver = e.getX() > getWidth() - right;
    } else {
      int left = getBorder().getBorderInsets(e.getComponent()).left + arrowSpaceWidth + 3;
      isArrowMouseOver = e.getX() < left;
    }
    return isArrowMouseOver;
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension preferredSize = super.getPreferredSize();
    if(jumpListMenu != null && !isPreferredSizeSet()) {
      preferredSize.width += arrowSpaceWidth;
    }
    return preferredSize;
  }

  private boolean isMenuIndicationAlwaysVisible = true;
  
  /**
   * Set whether the menu is always shown or only on hover.
   * @param isMenuIndicationAlwaysVisible true if always visible, false if only visible on hover.
   */
  public void setMenuIndicationAlwaysVisible(boolean isMenuIndicationAlwaysVisible) {
    this.isMenuIndicationAlwaysVisible = isMenuIndicationAlwaysVisible;
  }
  
  /**
   * @return true if the menu indication is always visible or false if only shown on hover.
   */
  public boolean isMenuIndicationAlwaysVisible() {
    return isMenuIndicationAlwaysVisible;
  }

  private boolean isModernWindowsLaF;
  private boolean isFlatLaf;
  
  @Override
  public void setUI(MenuItemUI ui) {
    LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
    isModernWindowsLaF = lookAndFeel.isNativeLookAndFeel() && System.getProperty("os.name").startsWith("Windows") && !Boolean.parseBoolean(System.getProperty("swing.noxp")) && !lookAndFeel.getClass().getName().endsWith("WindowsClassicLookAndFeel");
    try {
      isFlatLaf = Class.forName("com.formdev.flatlaf.FlatLaf").isInstance(UIManager.getLookAndFeel());
    } catch(Exception e) {
      isFlatLaf = false;
    }
    super.setUI(ui);
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    boolean isArmed = isArmed();
    if(jumpListMenu == null || !isMenuIndicationAlwaysVisible && !isArmed) {
      return;
    }
    boolean isEnabled = isEnabled();
    int w = getWidth();
    int h = getHeight();
    int size = (arrowWidth + 1) / 2;
    Insets borderInsets = getBorder().getBorderInsets(this);
    int y = h / 2;
    int x;
    boolean isLeftToRight = getComponentOrientation().isLeftToRight();
    Color foregroundColor = null;
    boolean isFlatLafUnderline = isFlatLaf && "underline".equals(UIManager.getString("MenuItem.selectionType"));
    if(isArmed && !isModernWindowsLaF && !isFlatLafUnderline) {
      // With certain look and feels, namely windows with XP style, we must use the foreground and not the selectionForeground.
      foregroundColor = UIManager.getColor("Menu.selectionForeground");
    }
    if(foregroundColor == null) {
      foregroundColor = getForeground();
    }
    if(isLeftToRight) {
      x = w - borderInsets.right - arrowSpaceWidth - 2;
      if(arrowIcon != null) {
        Graphics g2 = g.create();
        arrowIcon.paintIcon(this, g2, x + ((arrowSpaceWidth - arrowIcon.getIconWidth()) / 2) + 1, (getHeight() - arrowIcon.getIconHeight()) / 2);
        g2.dispose();
      } else {
//        x = w - borderInsets.right - arrowWidth - arrowSpaceWidth;
        paintTriangle(g, x + arrowSpaceWidth / 2 + 1, y, size, isEnabled, isLeftToRight, foregroundColor);
      }
    } else {
      x = borderInsets.left + arrowSpaceWidth - 2;
      if(arrowIcon != null) {
        Graphics g2 = g.create();
        arrowIcon.paintIcon(this, g2, x - ((arrowSpaceWidth - arrowIcon.getIconWidth()) / 2) - 1, (getHeight() - arrowIcon.getIconHeight()) / 2);
        g2.dispose();
      } else {
        paintTriangle(g, x - arrowSpaceWidth / 2 - 1, y, size, isEnabled, isLeftToRight, foregroundColor);
      }
    }
    if(!isArmed) {
      return;
    }
    int y1 = borderInsets.top;
    int y2 = h - borderInsets.bottom;
    int gradientHeight = Math.max((y2 - y1 + 1) / 5, 1);
    float gradientIncrement = (isEnabled? 100f: 50f) / gradientHeight;
    for(int i=0; i<gradientHeight; i++) {
      g.setColor(new Color(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue(), (int)(gradientIncrement * (i + 1))));
      g.drawLine(x, y1 + i, x, y1 + i);
      g.drawLine(x, y2 - i, x, y2 - i);
    }
    Color dividerColor = new Color(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue(), isEnabled? 100: 50);
    g.setColor(dividerColor);
    g.drawLine(x, y1 + gradientHeight, x, y2 - gradientHeight);
  }

  private void paintTriangle(java.awt.Graphics g, int x, int y, int size, boolean isEnabled, boolean isLeftToRight, Color foregroundColor) {
    java.awt.Color oldColor = g.getColor();
    size = Math.max(size, 2);
    int mid = (size / 2) - 1;
    g.translate(x, y);
    if (isEnabled) {
      g.setColor(foregroundColor);
    } else {
      g.setColor(new Color(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue(), 100));
    }
    int j=0;
    for (int i = size - 1; i >= 0; i--) {
      g.drawLine(j, mid - i, j, mid + i);
      if(isLeftToRight) {
        j++;
      } else {
        j--;
      }
    }
    g.translate(-x, -y);
    g.setColor(oldColor);
  }

  private JMenu jumpListMenu;

  /**
   * Set the menu that holds jump list actions.
   * @param jumpListMenu the menu to set.
   */
  public void setJumpListMenu(JMenu jumpListMenu) {
    if(this.jumpListMenu != null) {
      remove(this.jumpListMenu);
    }
    this.jumpListMenu = jumpListMenu;
    jumpListMenu.setBorder(BorderFactory.createEmptyBorder());
    jumpListMenu.setPreferredSize(new Dimension(0, 0));
    add(jumpListMenu);
  }

  /**
   * Get the menu that holds the jump list actions.
   * @return the menu, or null if no menu is set.
   */
  public JMenu getJumpListMenu() {
    return jumpListMenu;
  }

  private Point getPopupMenuOrigin() {
    JPopupMenu popupMenu = jumpListMenu.getPopupMenu();
    Point locationOnScreen = getLocationOnScreen();
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    GraphicsConfiguration gc = getGraphicsConfiguration();
    for(GraphicsDevice gd: GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
      if(gd.getType() == GraphicsDevice.TYPE_RASTER_SCREEN) {
        GraphicsConfiguration dgc = gd.getDefaultConfiguration();
        if(dgc.getBounds().contains(locationOnScreen)) {
          gc = dgc;
          break;
        }
      }
    }
    Rectangle screenBounds;
    if (gc == null) {
      screenBounds = new Rectangle(toolkit.getScreenSize());
    } else {
      screenBounds = gc.getBounds();
      Insets screenInsets = toolkit.getScreenInsets(gc);
      screenBounds.width -= Math.abs(screenInsets.left + screenInsets.right);
      screenBounds.height -= Math.abs(screenInsets.top + screenInsets.bottom);
      locationOnScreen.x -= Math.abs(screenInsets.left);
      locationOnScreen.y -= Math.abs(screenInsets.top);
    }
    int x;
    int width = getWidth();
    int height = getHeight();
    Dimension popupMenuSize = popupMenu.getSize();
    if (popupMenuSize.width == 0) {
      popupMenuSize = popupMenu.getPreferredSize();
    }
    int xOffset = UIManager.getInt("Menu.submenuPopupOffsetX");
    int yOffset = UIManager.getInt("Menu.submenuPopupOffsetY");
    if(getComponentOrientation().isLeftToRight()) {
      x = width + xOffset;
      if (locationOnScreen.x + x + popupMenuSize.width >= screenBounds.width + screenBounds.x && screenBounds.width - width < 2 * (locationOnScreen.x - screenBounds.x)) {
        x = 0 - xOffset - popupMenuSize.width;
      }
    } else {
      x = 0 - xOffset - popupMenuSize.width;
      if (locationOnScreen.x + x < screenBounds.x && screenBounds.width - width > 2*(locationOnScreen.x - screenBounds.x)) {
        x = width + xOffset;
      }
    }
    int y = yOffset;
    if (locationOnScreen.y + y + popupMenuSize.height >= screenBounds.height + screenBounds.y && screenBounds.height - height < 2 * (locationOnScreen.y - screenBounds.y)) {
      y = height - yOffset - popupMenuSize.height;
    }
    return new Point(x, y);
  }

}
