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
 * @author Christopher Deckers
 */
public class JLink<T> extends JLabel {

  public JLink(String text, T target) {
    this(text, target, null);
  }

  public JLink(String text, T target, String toolTip) {
    super(text);
    this.isDefaultToolTipShown = toolTip == null;
    setToolTipText(toolTip);
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    setForeground(Color.BLUE);
    Map<TextAttribute, Object> attributeMap = new HashMap<TextAttribute, Object>();
    attributeMap.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
    setFont(getFont().deriveFont(attributeMap));
    setTaget(target);
    setFocusable(true);
    addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        repaint();
      }
      public void focusLost(FocusEvent e) {
        setForeground(Color.BLUE);
        repaint();
      }
    });
    MouseInputAdapter mouseListener = new MouseInputAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        setForeground(Color.RED);
        requestFocus();
      }
      @Override
      public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
          fireLinkActivated();
        }
      }
    };
    setFocusTraversalKeysEnabled(true);
    addMouseListener(mouseListener);
    addMouseMotionListener(mouseListener);
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
          fireLinkActivated();
        }
      }
    });
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if(!SwingSuiteUtils.IS_JAVA_6_OR_GREATER) {
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
      JOptionPane.showMessageDialog(JLink.this, SwingSuiteUtils.getUIManagerMessage("Link.linkActivationErrorMessage", "Failed to open the link \"{0}\".", target), SwingSuiteUtils.getUIManagerMessage("Link.linkActivationErrorTitle", "Link error"), JOptionPane.ERROR_MESSAGE);
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
    super.setToolTipText(text);
  }

  public void setTaget(T target) {
    this.target = target;
    if(isDefaultToolTipShown) {
      setToolTipText(null);
    }
  }

  public T getTarget() {
    return target;
  }

  public void addLinkListener(LinkListener<T> linkListener) {
    listenerList.add(LinkListener.class, linkListener);
  }

  public void removeLinkListener(LinkListener<T> linkListener) {
    listenerList.remove(LinkListener.class, linkListener);
  }

  @SuppressWarnings("unchecked")
  public LinkListener<T>[] getLinkListeners() {
    return listenerList.getListeners(LinkListener.class);
  }

}
