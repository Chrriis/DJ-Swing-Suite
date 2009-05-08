/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

/**
 * A label that allows text selection and multi line. In practice, this is in fact a non editable text component that looks like a label.
 * @author Christopher Deckers
 */
public class JExtendedLabel extends JComponent implements SwingConstants {

  private class TextComponentX extends JTextPane {

    public TextComponentX(String text) {
      setText(text);
    }

    @Override
    public void setText(String text) {
      super.setText(text);
      setCaretPosition(0);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
      // Should prevent the text to wrap.
      return false;
    }

  }

  private JScrollPane scrollPane;
  private TextComponentX textComponent;

  public JExtendedLabel(String text) {
    this(text, LEADING, true);
  }

  public JExtendedLabel(String text, boolean isSelectable) {
    this(text, LEADING, isSelectable);
  }

  public JExtendedLabel(String text, int horizontalAlignment) {
    this(text, horizontalAlignment, true);
  }

  public JExtendedLabel(String text, int horizontalAlignment, boolean isSelectable) {
    setLayout(new BorderLayout());
    textComponent = new TextComponentX(text);
    scrollPane = new JScrollPane(textComponent) {
      @Override
      public void paint(Graphics g) {
        paintComponents(g);
      }
      @Override
      public boolean isShowing() {
        return true;
      }
    };
    scrollPane.setViewportView(textComponent);
    add(scrollPane, BorderLayout.CENTER);
    setHorizontalAlignment(horizontalAlignment);
    adjustLook();
    setSelectable(isSelectable);
  }

  private void adjustLook() {
    JLabel label = new JLabel("M");
    setOpaque(label.isOpaque());
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    textComponent.setOpaque(false);
    if(textComponent == null) {
      return;
    }
    textComponent.setEditable(false);
    textComponent.setAlignmentX(label.getAlignmentX());
    textComponent.setAlignmentY(label.getAlignmentY());
    textComponent.setOpaque(false);
    textComponent.setBorder(label.getBorder());
    textComponent.setFont(label.getFont());
    textComponent.setForeground(label.getForeground());
    Insets margin = textComponent.getMargin();
    margin.left = 0;
    margin.right = 0;
    textComponent.setMargin(margin);
    if(label.isOpaque()) {
      textComponent.setBackground(label.getBackground());
    } else {
      textComponent.setBackground(new Color(0, 0, 0, 0));
    }
    label.setEnabled(false);
    textComponent.setDisabledTextColor(label.getForeground());
    textComponent.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
  }

  public void setText(String text) {
    textComponent.setText(text);
  }

  public String getText() {
    return textComponent.getText();
  }

  @Override
  public void setFont(Font font) {
    textComponent.setFont(font);
  }

  @Override
  public Font getFont() {
    return textComponent.getFont();
  }

  /**
   * Sets the alignment of the label's contents along the X axis.
   * @param horizontalAlignment One of the following constants defined in SwingConstants: LEFT, RIGHT, LEADING or TRAILING.
   */
  public void setHorizontalAlignment(int horizontalAlignment) {
    switch(horizontalAlignment) {
      case LEFT:
        applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        break;
      case RIGHT:
        applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        break;
      case TRAILING:
        applyComponentOrientation(getComponentOrientation().isLeftToRight()? ComponentOrientation.RIGHT_TO_LEFT: ComponentOrientation.LEFT_TO_RIGHT);
        break;
    }
  }

  @Override
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  @Override
  public Dimension getMaximumSize() {
    return getPreferredSize();
  }

  @Override
  public Dimension getPreferredSize() {
    if(isPreferredSizeSet()) {
      return super.getPreferredSize();
    }
    return textComponent.getPreferredSize();
  }

  @Override
  public void updateUI() {
    super.updateUI();
    adjustLook();
  }

  public void setSelectable(boolean isSelectable) {
    scrollPane.setVisible(isSelectable);
  }

  public boolean isSelectable() {
    return scrollPane.isVisible();
  }

  @Override
  public void setEnabled(boolean isEnabled) {
    textComponent.setEnabled(isEnabled);
  }

  @Override
  public boolean isEnabled() {
    return textComponent.isEnabled();
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    if(!scrollPane.isVisible()) {
      textComponent.setSize(getWidth(), getHeight());
      textComponent.paint(g);
    }
  }

}
