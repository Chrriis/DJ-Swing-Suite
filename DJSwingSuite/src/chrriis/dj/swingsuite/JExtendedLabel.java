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
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

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
      Dimension size1 = textComponent.getSize();
      Dimension size2 = JExtendedLabel.this.getSize();
      return size1.width <= size2.width && size1.height <= size2.height;
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
    int alignment = StyleConstants.ALIGN_LEFT;
    switch(horizontalAlignment) {
      case RIGHT:
        alignment = StyleConstants.ALIGN_RIGHT;
        break;
      case CENTER:
        alignment = StyleConstants.ALIGN_CENTER;
        break;
      case TRAILING:
        alignment = getComponentOrientation().isLeftToRight()? StyleConstants.ALIGN_RIGHT: StyleConstants.ALIGN_LEFT;
        break;
    }
    AttributeSet paragraphAttributes = textComponent.getParagraphAttributes();
    if(StyleConstants.getAlignment(paragraphAttributes) == alignment) {
      return;
    }
    int selectionStart = textComponent.getSelectionStart();
    int selectionEnd = textComponent.getSelectionEnd();
    textComponent.selectAll();
    SimpleAttributeSet attributeSet = new SimpleAttributeSet();
    attributeSet.addAttributes(paragraphAttributes);
    StyleConstants.setAlignment(attributeSet, alignment);
    textComponent.setParagraphAttributes(attributeSet, false);
    textComponent.select(selectionStart, selectionEnd);
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
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if(isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
    }
    if(!scrollPane.isVisible()) {
      textComponent.setSize(getWidth(), getHeight());
      textComponent.paint(g);
    }
  }

}
