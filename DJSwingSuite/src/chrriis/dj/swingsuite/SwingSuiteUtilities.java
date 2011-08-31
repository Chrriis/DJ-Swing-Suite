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
import java.awt.Point;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
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
    private boolean isSettingFocus;
    private Point mouseLocation = null;
    public void focusGained(FocusEvent e) {
      isSettingFocus = true;
      ((JTextComponent)e.getComponent()).selectAll();
    }
    public void focusLost(FocusEvent e) {
      isSettingFocus = false;
    }
    @Override
    public void mousePressed(MouseEvent e) {
      if(e.getButton() == MouseEvent.BUTTON1) {
        mouseLocation = e.getPoint();
        isSettingFocus = false;
      }
    }
    @Override
    public void mouseClicked(MouseEvent e) {
      if(e.getButton() == MouseEvent.BUTTON1) {
        if(isSettingFocus) {
          e.consume();
        }
        isSettingFocus = false;
      }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
      if(e.getButton() == MouseEvent.BUTTON1) {
        mouseLocation = null;
      }
    }
    @Override
    public void mouseMoved(MouseEvent e) {
    }
    @Override
    public void mouseDragged(MouseEvent e) {
      if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0 && mouseLocation != null) {
        if(isSettingFocus && Math.abs(mouseLocation.x - e.getX()) < 5) {
          e.consume();
        } else {
          ((JTextComponent)e.getComponent()).setCaretPosition(((JTextComponent)e.getComponent()).viewToModel(mouseLocation));
          mouseLocation = null;
        }
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
  public static void setSelectAllOnFocus(JTextComponent component, boolean isSelectingAllOnFocus) {
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
    LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
    if(lookAndFeel.isNativeLookAndFeel() && System.getProperty("os.name").startsWith("Windows") && !Boolean.parseBoolean(System.getProperty("swing.noxp")) && !lookAndFeel.getClass().getName().endsWith("WindowsClassicLookAndFeel")) {
      toolBarButton.setFocusPainted(false);
    }
  }

  /**
   * Set whether the auto-scroll feature is enabled. Auto-scoll is triggered when the user clicks with the middle mouse button and the component is a scroll pane or has a scroll pane ancestor.<br>
   * Note that clicking on a descendant component activates the auto-scroll feature only if it has not registered a mouse listener. Otherwise, you need to activate auto scroll on these components as well.
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
   * @param columnIndex the index of the column to auto fit, in view index.
   * @param maxWidth the maximum width that a column can take (like Integer.MAX_WIDTH).
   */
  public static void autoFitTableColumn(JTable table, int columnIndex, int maxWidth) {
    TableModel model = table.getModel();
    TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
    int rowCount = table.getRowCount();
    int rowMargin = table.getRowMargin();
    TableColumnModel columnModel = table.getColumnModel();
    for (int viewCol=columnIndex>=0? columnIndex: model.getColumnCount()-1; viewCol>=0; viewCol--) {
      TableColumn tableColumn = columnModel.getColumn(viewCol);
      int headerWidth = headerRenderer.getTableCellRendererComponent(table, tableColumn.getHeaderValue(), false, false, 0, viewCol).getPreferredSize().width;
      int cellWidth = 0;
      for(int viewRow=0; viewRow<rowCount; viewRow++) {
        Component comp = table.getCellRenderer(viewRow, viewCol).getTableCellRendererComponent(table, table.getValueAt(viewRow, viewCol), false, false, viewRow, viewCol);
        int preferredWidth = comp.getPreferredSize().width;
        // Artificial space to look nicer.
        preferredWidth += 10;
        cellWidth = Math.max(cellWidth, preferredWidth);
        if(Math.max(headerWidth, cellWidth) + rowMargin >= maxWidth) {
          break;
        }
      }
      // Artificial space for the sort icon.
      headerWidth += 20;
      tableColumn.setPreferredWidth(Math.min(Math.max(headerWidth, cellWidth) + rowMargin, maxWidth));
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
   * A "sed -e" like reg exp, of the form:<br/>
   * - /regexp/flags: find and output the matches.<br/>
   * - /regexp/replacement/flags: replace the matches and output the resulting string.<br/>
   * Flags can be left empty or any combinations of the characters 'gidmsux' (g perfoms a replace all instead of just the first match. For other flags, refer to the Javadoc of Pattern).
   * It is also possible to chain the output using ';' to perform multiple replacements.<br/>
   * If the regexp contains capturing groups, a find operation would only retain those; for a replace operation, the replacement string can refer to capturing groups with a syntax like '$1'.
   */
  public static String applySedRegularExpression(String text, String regex) {
    String originalRegEx = regex;
    if(!regex.startsWith("/")) {
      throw new IllegalArgumentException("Invalid expression format: " + originalRegEx);
    }
    regex = regex.substring(1);
    StringBuilder sb = new StringBuilder();
    char[] chars = regex.toCharArray();
    int index1 = -1;
    int index2 = -1;
    for(int i=0; i<chars.length; i++) {
      char c = chars[i];
      switch(c) {
        case ';':
          text = applySedRegularExpression(sb.toString(), text, originalRegEx, index1, index2);
          index1 = -1;
          index2 = -1;
          sb = new StringBuilder();
          i++;
          if(i >= chars.length || chars[i] != '/') {
            throw new IllegalArgumentException("Invalid expression format: " + originalRegEx);
          }
          break;
        case '\\':
          i++;
          if(i >= chars.length) {
            throw new IllegalArgumentException("Invalid expression format: " + originalRegEx);
          }
          switch(chars[i]) {
            case '/': sb.append('/'); break;
            case ';': sb.append(';'); break;
            default: sb.append('\\').append(chars[i]); break;
          }
          break;
        case '/':
          if(index1 == -1) {
            index1 = sb.length();
          } else if(index2 == -1) {
            index2 = sb.length();
          } else {
            throw new IllegalArgumentException("Invalid expression format: " + originalRegEx);
          }
          break;
        default: sb.append(c); break;
      }
    }
    if(index1 == -1) {
      throw new IllegalArgumentException("Invalid expression format: " + originalRegEx);
    }
    return applySedRegularExpression(sb.toString(), text, originalRegEx, index1, index2);
  }

  private static String applySedRegularExpression(String s, String text, String originalRegEx, int index1, int index2) {
    StringBuilder sb;
    String toFind = s.substring(0, index1);
    String replacement = index2 == -1? null: s.substring(index1, index2);
    String modifiers = index2 == -1? s.substring(index1): s.substring(index2);
    boolean isGlobal = false;
    int flags = 0;
    for(int i=0; i<modifiers.length(); i++) {
      char c = modifiers.charAt(i);
      switch(c) {
        case 'g': isGlobal = true; break;
        case 'i': flags |= Pattern.CASE_INSENSITIVE; break;
        case 'd': flags |= Pattern.UNIX_LINES; break;
        case 'm': flags |= Pattern.MULTILINE; break;
        case 's': flags |= Pattern.DOTALL; break;
        case 'u': flags |= Pattern.UNICODE_CASE; break;
        case 'x': flags |= Pattern.COMMENTS; break;
        default:
          throw new IllegalArgumentException("Invalid expression format: " + originalRegEx);
      }
    }
    Matcher matcher = Pattern.compile(toFind, flags).matcher(text);
    if(replacement == null) {
      // Just returning the matches, no replacement
      int groupCount = matcher.groupCount();
      sb = new StringBuilder();
      while(matcher.find()) {
        if(groupCount > 0) {
          for(int i=0; i<groupCount; i++) {
            String group = matcher.group(i + 1);
            if(group != null) {
              sb.append(group);
            }
          }
        } else {
          String group = matcher.group();
          if(group != null) {
            sb.append(group);
          }
        }
        if(!isGlobal) {
          break;
        }
      }
      return sb.toString();
    }
    if(isGlobal) {
      return matcher.replaceAll(replacement);
    }
    return matcher.replaceFirst(replacement);
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
