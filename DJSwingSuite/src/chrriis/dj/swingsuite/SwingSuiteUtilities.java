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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

/**
 * @author Christopher Deckers
 */
public class SwingSuiteUtilities {

  private SwingSuiteUtilities() {
  }

  public static final boolean IS_JAVA_6_OR_GREATER = System.getProperty("java.version").compareTo("1.6") >= 0;

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

  /**
   * Set whether the auto-scroll feature is enabled. Auto-scoll is triggered when the user clicks with the middle mouse button and the component is a scroll pane or has a scroll pane ancestor.<br>
   * Note that clicking on a descendant component activates the auto-scroll feature only if it has not register a mouse listener. Otherwise, you need to activate the auto scroll on these components as well.
   * @param component The component for which to enable or disable the feature.
   * @param isEnabled true if the feature is to be enabled, false otherwise.
   */
  public static void setAutoScrollEnabled(JComponent component, boolean isEnabled) {
    AutoScrollActivator.setAutoScrollEnabled(component, isEnabled);
  }

  /**
   * Auto fit the columns of a table.
   * @param table the table for which to auto fit the columns.
   * @param maxWidth the maximum width that a column can take (like Integer.MAX_WIDTH).
   */
  public static void autoFitTableColumns(JTable table, int maxWidth) {
    autoFitTableColumn(table, -1, maxWidth);
  }

  /**
   * Auto fit the column of a table.
   * @param table the table for which to auto fit the columns.
   * @param columnIndex the index of the column to auto fit.
   * @param maxWidth the maximum width that a column can take (like Integer.MAX_WIDTH).
   */
  public static void autoFitTableColumn(JTable table, int columnIndex, int maxWidth) {
    TableModel model = table.getModel();
    TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
    int rowCount = table.getRowCount();
    for (int i=columnIndex>=0? columnIndex: model.getColumnCount()-1; i>=0; i--) {
      TableColumn column = table.getColumnModel().getColumn(i);
      int headerWidth = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0).getPreferredSize().width;
      int cellWidth = 0;
      for(int j=0; j<rowCount; j++) {
        Component comp = table.getDefaultRenderer(model.getColumnClass(i)).getTableCellRendererComponent(table, table.getValueAt(j, i), false, false, 0, i);
        int preferredWidth = comp.getPreferredSize().width;
        // Artificial space to look nicer.
        preferredWidth += 10;
        cellWidth = Math.max(cellWidth, preferredWidth);
      }
      // Artificial space for the sort icon.
      headerWidth += 20;
      column.setPreferredWidth(Math.min(Math.max(headerWidth, cellWidth) + table.getRowMargin(), maxWidth));
      if(columnIndex >= 0) {
        break;
      }
    }
  }

  /**
   * Decode some text that was URL encoded.
   * @param s the string to decode.
   * @return the string once decoded.
   */
  public static String decodeURL(String s) {
    try {
      return URLDecoder.decode(s, "UTF-8");
    } catch(UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Encode some text to be used in a URL.
   * @param s the string to encode.
   * @return the string once encoded.
   */
  @SuppressWarnings("deprecation")
  public static String encodeURL(String s) {
    String encodedString;
    try {
      encodedString = URLEncoder.encode(s, "UTF-8");
    } catch(Exception e) {
      encodedString = URLEncoder.encode(s);
    }
    return encodedString.replaceAll("\\+", "%20");
  }

  /**
   * Escape a string to be used in XML.
   * @param s the string to escape.
   * @return the string after having been escaped.
   */
  public static String escapeXML(String s) {
    if(s == null || s.length() == 0) {
      return s;
    }
    StringBuffer sb = new StringBuffer((int)(s.length() * 1.1));
    for(int i=0; i<s.length(); i++) {
      char c = s.charAt(i);
      switch(c) {
        case '<':
          sb.append("&lt;");
          break;
        case '>':
          sb.append("&gt;");
          break;
        case '&':
          sb.append("&amp;");
          break;
        case '\'':
          sb.append("&apos;");
          break;
        case '\"':
          sb.append("&quot;");
          break;
        default:
          sb.append(c);
        break;
      }
    }
    return sb.toString();
  }

  /**
   * Replace a string that contains wildcards (* and ?) to its regular expression equivalent.
   * @param wildcardString the string to convert.
   * @return the regular expression equivalent to the wildcard string.
   */
  public static String convertWildcardsToRegExp(String wildcardString) {
    return "\\Q" + wildcardString.replace("\\E", "\\\\E").replace("\\Q", "\\\\Q").replace("?", "\\E.\\Q").replace("*", "\\E.*\\Q");
  }

  /**
   * Set the look and feel that users tend to prefer for the current platform.
   */
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
