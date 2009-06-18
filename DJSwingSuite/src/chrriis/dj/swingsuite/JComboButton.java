/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ButtonUI;

/**
 * A button primarily targeted for toolbars which features a sub-section containing an arrow.
 * @author Christopher Deckers
 */
public class JComboButton extends JButton {

  private static final String ARROW_EVENT_SUFFIX = "[Arrow]";

  private MouseInputAdapter mouseHandler = new MouseInputAdapter() {
    @Override
    public void mouseExited(MouseEvent e) {
      setActionCommand(originalActionCommand);
      originalActionCommand = null;
      isMouseOver = false;
      isArrowMouseOver = false;
      repaint();
    }
    @Override
    public void mouseEntered(MouseEvent e) {
      originalActionCommand = getActionCommand();
      isMouseOver = true;
      processMouseEvent(e);
      repaint();
    }
    @Override
    public void mouseMoved(MouseEvent e) {
      processMouseEvent(e);
    }
    @Override
    public void mouseReleased(MouseEvent e) {
      processMouseEvent(e);
    }
    protected void processMouseEvent(MouseEvent e) {
      if(getComponentOrientation().isLeftToRight()) {
        int right = originalBorder.getBorderInsets(e.getComponent()).right + arrowSpaceWidth;
        isArrowMouseOver = e.getX() > getWidth() - right;
      } else {
        int left = originalBorder.getBorderInsets(e.getComponent()).left + arrowSpaceWidth;
        isArrowMouseOver = e.getX() < left;
      }
      if(isArrowMouseOver && isDivided) {
        setActionCommand(originalActionCommand + ARROW_EVENT_SUFFIX);
      } else {
        setActionCommand(originalActionCommand);
      }
    }
  };

  private String originalActionCommand;
  private Border originalBorder;
  private boolean isMouseOver;
  private boolean isArrowMouseOver;

  private int arrowWidth;
  private int arrowSpaceWidth;

  /**
   * Construct a combo button with an action.
   * @param action the action to use.
   * @param isDivided true if the button is to be devided in two sections (button area, arrow area), false otherwise.
   */
  public JComboButton(Action action, boolean isDivided) {
    super("M");
    init(isDivided);
    setAction(action);
  }

  /**
   * Construct a combo button.
   * @param isDivided true if the button is to be devided in two sections (button area, arrow area), false otherwise.
   */
  public JComboButton(boolean isDivided) {
    this(null, null, isDivided);
  }

  /**
   * Construct a combo button with an icon.
   * @param icon the icon to use.
   * @param isDivided true if the button is to be devided in two sections (button area, arrow area), false otherwise.
   */
  public JComboButton(Icon icon, boolean isDivided) {
    this(null, icon, isDivided);
  }

  /**
   * Construct a combo button with some text.
   * @param text the text to use.
   * @param isDivided true if the button is to be devided in two sections (button area, arrow area), false otherwise.
   */
  public JComboButton(String text, boolean isDivided) {
    this(text, null, isDivided);
  }

  /**
   * Construct a combo button with an icon and some text.
   * @param text the text to use.
   * @param icon the icon to use.
   * @param isDivided true if the button is to be devided in two sections (button area, arrow area), false otherwise.
   */
  public JComboButton(String text, Icon icon, boolean isDivided) {
    super("M");
    init(isDivided);
    setText(text);
    setIcon(icon);
  }

  private void init(boolean isDivided) {
    arrowWidth = getPreferredSize().height / 4;
    arrowWidth -= (arrowWidth + 1) % 2;
    arrowSpaceWidth = arrowWidth + 7;
    setText(null);
    setDivided(isDivided);
    addHierarchyListener(new HierarchyListener() {
      public void hierarchyChanged(HierarchyEvent e) {
        long changeFlags = e.getChangeFlags();
        if((changeFlags & HierarchyEvent.PARENT_CHANGED) != 0) {
          if(e.getChanged() == JComboButton.this) {
            adjustLook();
          }
        }
      }
    });
    setModel(new DefaultButtonModel() {
      @Override
      public boolean isPressed() {
        return super.isPressed() && JComboButton.this.isDivided && (!isArrowMouseOver || isKeyEvent);
      }
    });
    addMouseListener(mouseHandler);
    addMouseMotionListener(mouseHandler);
    enableEvents(KeyEvent.KEY_EVENT_MASK);
  }

  private void adjustLook() {
    Container parent = getParent();
    if(parent != null) {
      if(originalBorder == null) {
        originalBorder = getBorder();
        if(getComponentOrientation().isLeftToRight()) {
          setBorder(BorderFactory.createCompoundBorder(originalBorder, BorderFactory.createEmptyBorder(0, 0, 0, arrowSpaceWidth + 1)));
        } else {
          setBorder(BorderFactory.createCompoundBorder(originalBorder, BorderFactory.createEmptyBorder(0, arrowSpaceWidth + 1, 0, 0)));
        }
      }
    } else {
      setBorder(originalBorder);
      originalBorder = null;
    }
  }

  @Override
  public void setUI(ButtonUI ui) {
    super.setUI(ui);
    if(getParent() != null) {
      originalBorder = null;
      adjustLook();
    }
  }

  private boolean showPopup(ActionEvent e) {
    boolean oldIsArrowMouseOver = isArrowMouseOver;
    isArrowMouseOver = false;
    getModel().setPressed(false);
    isArrowMouseOver = oldIsArrowMouseOver;
    if(isArrowEvent(e)) {
      requestFocus();
      if(arrowPopupMenu != null) {
        arrowPopupMenu.show(JComboButton.this, getComponentOrientation().isLeftToRight()? 0: getWidth() - arrowPopupMenu.getPreferredSize().width, getHeight());
        return true;
      }
    }
    return false;
  }

  private boolean isKeyEvent;

  @Override
  protected void processKeyEvent(KeyEvent e) {
    try {
      isKeyEvent = true;
      if(e.getKeyCode() == KeyEvent.VK_DOWN) {
        if(e.getID() == KeyEvent.KEY_PRESSED) {
          fireActionPerformed_(new ActionEvent(JComboButton.this, ActionEvent.ACTION_PERFORMED, getOriginalActionEvent() + ARROW_EVENT_SUFFIX, e.getModifiers()));
        }
        e.consume();
        return;
      }
      super.processKeyEvent(e);
    } finally {
      isKeyEvent = false;
    }
  }

  /**
   * Indicate whether the event originates from the arrow of a combo button.
   * @param e the event to test.
   * @return true if the event comes from the arrow, false otherwise.
   */
  public static boolean isArrowEvent(ActionEvent e) {
    String actionCommand = e.getActionCommand();
    return actionCommand != null && actionCommand.endsWith(ARROW_EVENT_SUFFIX);
  }

  private String getOriginalActionEvent() {
    String command = getActionCommand();
    if(command == null) {
      return "";
    }
    if(command.endsWith(ARROW_EVENT_SUFFIX)) {
      return command.substring(0, command.length() - ARROW_EVENT_SUFFIX.length());
    }
    return command;
  }

  @Override
  protected void fireActionPerformed(ActionEvent e) {
    boolean isArrowEvent = isArrowEvent(e);
    if(!isDivided || isArrowMouseOver == isArrowEvent) {
      if(isKeyEvent && isArrowEvent) {
        e = new ActionEvent(JComboButton.this, ActionEvent.ACTION_PERFORMED, getOriginalActionEvent(), e.getWhen(), e.getModifiers());
      } else {
        if(!isDivided && !isArrowEvent) {
          e = new ActionEvent(JComboButton.this, ActionEvent.ACTION_PERFORMED, getOriginalActionEvent() + ARROW_EVENT_SUFFIX, e.getWhen(), e.getModifiers());
        }
      }
      fireActionPerformed_(e);
    }
  }

  private void fireActionPerformed_(ActionEvent e) {
    if(!showPopup(e)) {
      super.fireActionPerformed(e);
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Color origColor;
    origColor = g.getColor();
    int w = getWidth();
    int h = getHeight();
    Insets borderInsets = originalBorder.getBorderInsets(this);
    boolean isEnabled = isEnabled();
    int x;
    int y1 = borderInsets.top;
    int y2 = h - borderInsets.bottom;
    if(getComponentOrientation().isLeftToRight()) {
      x = w - arrowSpaceWidth - borderInsets.right + 1;
      int size = (arrowWidth + 1) / 2;
      paintTriangle(g, x + arrowSpaceWidth / 2, (h - size) / 2, size, isEnabled);
    } else {
      x = arrowSpaceWidth + borderInsets.left - 1;
      int size = (arrowWidth + 1) / 2;
      paintTriangle(g, x - 1 - arrowWidth, (h - size) / 2, size, isEnabled);
    }
    if(isDivided && (isMouseOver || hasFocus()) && isEnabled) {
      int gradientHeight = Math.max((y2 - y1 + 1) / 5, 1);
      float gradientIncrement = 100f / gradientHeight;
      Color foregroundColor = getForeground();
      for(int i=0; i<gradientHeight; i++) {
        g.setColor(new Color(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue(), (int)(gradientIncrement * (i + 1))));
        g.drawLine(x, y1 + i, x, y1 + i);
        g.drawLine(x, y2 - i, x, y2 - i);
      }
      Color dividerColor = new Color(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue(), 100);
      g.setColor(dividerColor);
      g.drawLine(x, y1 + gradientHeight, x, y2 - gradientHeight);
    }
    g.setColor(origColor);
  }

  private boolean isDivided;

  /**
   * Set whether the combo button is divided in two different parts each with their own events: the button and the arrow.
   * @param isDivided true if the button should be divided, false otherwise.
   */
  public void setDivided(boolean isDivided) {
    this.isDivided = isDivided;
  }

  /**
   * Indicate whether the button is divided in two different parts.
   * @return true if the button is divided, false otherwise.
   */
  public boolean isDivided() {
    return isDivided;
  }

  private void paintTriangle(java.awt.Graphics g, int x, int y, int size, boolean isEnabled) {
    java.awt.Color oldColor = g.getColor();
    size = Math.max(size, 2);
    int mid = (size / 2) - 1;
    g.translate(x, y);
    Color foregroundColor = getForeground();
    if (isEnabled) {
      g.setColor(foregroundColor);
    } else if (!isEnabled) {
      g.setColor(new Color(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue(), 100));
    }
    int j=0;
    for (int i = size - 1; i >= 0; i--) {
      g.drawLine(mid - i, j, mid + i, j);
      j++;
    }
    g.translate(-x, -y);
    g.setColor(oldColor);
  }

  private JPopupMenu arrowPopupMenu;

  /**
   * Set a popup menu that is automatically shown when the arrow is pressed.<br>
   * Note that setting an automatically managed popup menu prevents the firing of action events from the arrow.
   * @param arrowPopupMenu the popup menu to show, or null to remove the popup menu.
   */
  public void setArrowPopupMenu(JPopupMenu arrowPopupMenu) {
    this.arrowPopupMenu = arrowPopupMenu;
  }

  /**
   * Get the popup menu that is currently associated to the arrow.
   * @return the popup menu, or null of no popup menu is set for the arrow.
   */
  public JPopupMenu getArrowPopupMenu() {
    return arrowPopupMenu;
  }

}
