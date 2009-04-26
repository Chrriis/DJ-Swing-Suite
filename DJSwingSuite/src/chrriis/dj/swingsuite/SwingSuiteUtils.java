/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.MessageFormat;

import javax.swing.AbstractButton;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

/**
 * @author Christopher Deckers
 */
public class SwingSuiteUtils {

  private static class SelectAllOnFocusListener extends MouseAdapter implements MouseMotionListener, FocusListener {
    private boolean isSettingFocus = false;
    public void focusGained(FocusEvent e) {
      isSettingFocus = true;
      ((JTextComponent)e.getComponent()).selectAll();
    }
    public void focusLost(FocusEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {
      isSettingFocus = false;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
      if(isSettingFocus) {
        e.consume();
      }
      isSettingFocus = false;
    }
    @Override
    public void mouseMoved(MouseEvent e) {
    }
    @Override
    public void mouseDragged(MouseEvent e) {
      if(isSettingFocus) {
        e.consume();
      }
    }
  }

  private static final SelectAllOnFocusListener SELECT_ALL_ON_FOCUS_LISTENER = new SelectAllOnFocusListener();

  /**
   * Enable/disable a hierarchy of swing components.
   * @param component the top level component to consider.
   * @param isEnabled the boolean value that indicates whether the component tree is enabled.
   */
  static void setComponentTreeEnabled(Component component, boolean isEnabled) {
    component.setEnabled(isEnabled);
    if(component instanceof Container) {
      int componentCount = ((Container)component).getComponentCount();
      for(int i=0; i<componentCount; i++) {
        setComponentTreeEnabled(((Container)component).getComponent(i), isEnabled);
      }
    }
  }

  /**
   * Set whether a text component selects all of its text when it acquires the focus.
   * @param component The component to set the select all status to.
   * @param isSelectingAllOnFocus true if the text should be selected when focus is acquired, false otherwise.
   */
  public static void setSelectingAllOnFocus(JTextComponent component, boolean isSelectingAllOnFocus) {
    component.removeFocusListener(SELECT_ALL_ON_FOCUS_LISTENER);
    component.removeMouseListener(SELECT_ALL_ON_FOCUS_LISTENER);
    component.removeMouseMotionListener(SELECT_ALL_ON_FOCUS_LISTENER);
    if(isSelectingAllOnFocus) {
      MouseListener[] mouseListeners = component.getMouseListeners();
      for(int i=0; i<mouseListeners.length; i++) {
        component.removeMouseListener(mouseListeners[i]);
      }
      component.addMouseListener(SELECT_ALL_ON_FOCUS_LISTENER);
      for(int i=0; i<mouseListeners.length; i++) {
        component.addMouseListener(mouseListeners[i]);
      }
      MouseMotionListener[] mouseMotionListeners = component.getMouseMotionListeners();
      for(int i=0; i<mouseMotionListeners.length; i++) {
        component.removeMouseMotionListener(mouseMotionListeners[i]);
      }
      component.addMouseMotionListener(SELECT_ALL_ON_FOCUS_LISTENER);
      for(int i=0; i<mouseMotionListeners.length; i++) {
        component.addMouseMotionListener(mouseMotionListeners[i]);
      }
      component.addFocusListener(SELECT_ALL_ON_FOCUS_LISTENER);
    }
  }

  /**
   * Indicate whether a text component selects all its text when it receives the focus.
   * @param textComponent The text component for which to get the select all state.
   * @return true if the text component selects all its text when gaining focus, false otherwise.
   */
  public static boolean isSelectingAllOnFocus(JTextComponent textComponent) {
    for(FocusListener focusListener: textComponent.getFocusListeners()) {
      if(SELECT_ALL_ON_FOCUS_LISTENER.equals(focusListener)) {
        return true;
      }
    }
    return false;
  }

  static String getUIManagerMessage(String managerKey, String defaultMessage, Object... params) {
    String message = UIManager.getString(managerKey);
    return MessageFormat.format(message != null? message: defaultMessage, params);
  }

  /**
   * With certain look and feels, namely windows with XP style, the focus of a toolbar button is already indicated (border changes) and the focus indicator should not be drawn: this fixes the visual rendering.
   * @param toolBarButton the tool bar button for which to adjust the focus state.
   */
  public static void adjustToolbarButtonFocus(AbstractButton toolBarButton) {
    if(UIManager.getLookAndFeel().isNativeLookAndFeel() && System.getProperty("os.name").startsWith("Windows") && !Boolean.parseBoolean(System.getProperty("swing.noxp"))) {
      toolBarButton.setFocusPainted(false);
    }
  }

  public static void setPreferredLookAndFeel() {
    try {
      String systemLookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
      if(!"com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(systemLookAndFeelClassName)) {
        UIManager.setLookAndFeel(systemLookAndFeelClassName);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

}
