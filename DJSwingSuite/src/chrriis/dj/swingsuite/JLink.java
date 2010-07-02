/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * A simple link class. With Java 6, the default handler supports String, URL, URI and File objects, in which case the content can be processed as a file, a directory, an e-mail or a web site.
 * @author Christopher Deckers
 */
public class JLink<T> extends JLabel {

  /**
   * Construct a link, with a given text and a target.
   * @param text the text of the link.
   * @param target the target of the link.
   */
  public JLink(String text, T target) {
    this(text, target, null);
  }

  /**
   * Construct a link, with a given text and a target and a custom tool tip.
   * @param text the text of the link.
   * @param target the target of the link.
   * @param toolTip the tool tip, or null for the default one.
   */
  public JLink(String text, T target, String toolTip) {
    super(text);
    this.isDefaultToolTipShown = toolTip == null;
    setToolTipText(toolTip);
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    setForeground(Color.BLUE);
    Map<TextAttribute, Object> attributeMap = new HashMap<TextAttribute, Object>();
    attributeMap.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
    setFont(getFont().deriveFont(attributeMap));
    setTarget(target);
    setFocusable(true);
    addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        repaint();
      }
      public void focusLost(FocusEvent e) {
        repaint();
      }
    });
    MouseInputAdapter mouseListener = new MouseInputAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if(!isEnabled()) {
          return;
        }
        setForeground(Color.RED);
        requestFocus();
        repaint();
      }
      @Override
      public void mouseReleased(MouseEvent e) {
        setForeground(Color.BLUE);
        repaint();
        if(!isEnabled()) {
          return;
        }
        Point location = e.getPoint();
        if(e.getButton() == MouseEvent.BUTTON1 && location.x >= 0 && location.x < getWidth() && location.y >= 0 && location.y < getHeight()) {
          fireLinkActivated();
        }
      }
    };
    setFocusTraversalKeysEnabled(true);
    addMouseListener(mouseListener);
    addMouseMotionListener(mouseListener);
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
          fireLinkActivated();
        }
      }
    });
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if(!SwingSuiteUtilities.IS_JAVA_6_OR_GREATER) {
      FontMetrics fm = g.getFontMetrics();
      int y = fm.getHeight() - fm.getDescent() + 1;
      g.drawLine(0, y, getWidth(), y);
    }
    if(hasFocus()) {
      BasicGraphicsUtils.drawDashedRect(g, 0, 0, getWidth(), getHeight());
    }
  }

  private void fireLinkActivated() {
    for(LinkListener<T> linkListener: getLinkListeners()) {
      if(!linkListener.linkActivated(this, target)) {
        return;
      }
    }
    try {
      if(target instanceof String) {
        String s = (String)target;
        if(s.startsWith("file:")) {
          Desktop.getDesktop().open(new File(new URI(s)));
          return;
        }
        if(s.startsWith("mailto:")) {
          Desktop.getDesktop().mail(new URI(s));
          return;
        }
        Desktop.getDesktop().browse(new URI(s));
        return;
      }
      if(target instanceof File) {
        Desktop.getDesktop().open((File)target);
        return;
      }
      if(target instanceof URL) {
        URL url = ((URL)target);
        String protocol = url.getProtocol();
        if(protocol.startsWith("file:")) {
          Desktop.getDesktop().open(new File(url.toURI()));
          return;
        }
        if(protocol.startsWith("mailto:")) {
          Desktop.getDesktop().mail(url.toURI());
          return;
        }
        Desktop.getDesktop().browse(url.toURI());
        return;
      }
      if(target instanceof URI) {
        URI uri = ((URI)target);
        String scheme = uri.getScheme();
        if(scheme.startsWith("file:")) {
          Desktop.getDesktop().open(new File(uri));
          return;
        }
        if(scheme.startsWith("mailto:")) {
          Desktop.getDesktop().mail(uri);
          return;
        }
        Desktop.getDesktop().browse(uri);
        return;
      }
    } catch(Throwable ex) {
      JOptionPane.showMessageDialog(JLink.this, SwingSuiteUtilities.getUIManagerMessage("Link.linkActivationErrorMessage", "Failed to open the link \"{0}\".", target), SwingSuiteUtilities.getUIManagerMessage("Link.linkActivationErrorTitle", "Link error"), JOptionPane.ERROR_MESSAGE);
    }
  }

  private boolean isDefaultToolTipShown;

  private T target;

  @Override
  public void setToolTipText(String text) {
    isDefaultToolTipShown = text == null;
    if(text == null) {
      if(target != null) {
        text = target.toString();
      }
    }
    if(text != null && text.length() == 0) {
      text = null;
    }
    super.setToolTipText(text);
  }

  /**
   * Set the link target.
   * @param target the target of the link.
   */
  public void setTarget(T target) {
    this.target = target;
    if(isDefaultToolTipShown) {
      setToolTipText(null);
    }
  }

  public T getTarget() {
    return target;
  }

  /**
   * Add a listener that will be invoked when the link is activated.
   * @param linkListener the listener to register.
   */
  public void addLinkListener(LinkListener<T> linkListener) {
    listenerList.add(LinkListener.class, linkListener);
  }

  /**
   * Remove a listener from the list of listeners that are invoked when the link is activated.
   * @param linkListener the listener to unregister.
   */
  public void removeLinkListener(LinkListener<T> linkListener) {
    listenerList.remove(LinkListener.class, linkListener);
  }

  /**
   * Get all the listeners that are invoked when a link is activated.
   * @return the link listeners.
   */
  @SuppressWarnings("unchecked")
  public LinkListener<T>[] getLinkListeners() {
    return listenerList.getListeners(LinkListener.class);
  }

}
