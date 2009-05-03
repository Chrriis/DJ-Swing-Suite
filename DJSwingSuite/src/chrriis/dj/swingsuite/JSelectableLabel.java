/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;

/**
 * A label that allows text selection. In practice, this is in fact a non editable text field that looks like a label.
 * @author Christopher Deckers
 */
public class JSelectableLabel extends JTextField {

  public JSelectableLabel(String text) {
    super(text);
    adjustLook();
  }

  public JSelectableLabel(String text, int horizontalAlignment) {
    super(text, horizontalAlignment);
    adjustLook();
  }

  private void adjustLook() {
    setEditable(false);
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    JLabel label = new JLabel("M");
    setAlignmentX(label.getAlignmentX());
    setAlignmentY(label.getAlignmentY());
    setOpaque(label.isOpaque());
    setBorder(label.getBorder());
    setFont(label.getFont());
    setForeground(label.getForeground());
    setBackground(label.getBackground());
  }

  private static class LabelGraphics2D extends Graphics2D {

    private Graphics2D wrapped;

    public LabelGraphics2D(Graphics2D wrapped) {
      this.wrapped = wrapped;
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
      wrapped.addRenderingHints(hints);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
//      wrapped.clearRect(x, y, width, height);
    }

    @Override
    public void clip(Shape s) {
      wrapped.clip(s);
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
      wrapped.clipRect(x, y, width, height);
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
      wrapped.copyArea(x, y, width, height, dx, dy);
    }

    @Override
    public Graphics create() {
      return new LabelGraphics2D((Graphics2D)wrapped.create());
    }

    @Override
    public void dispose() {
      wrapped.dispose();
    }

    @Override
    public void draw(Shape s) {
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
      wrapped.drawGlyphVector(g, x, y);
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
      return true;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
      return true;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
      return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
      return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
      return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
      return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
      return true;
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
    }

    @Override
    public void drawPolygon(int[] points, int[] points2, int points3) {
    }

    @Override
    public void drawPolyline(int[] points, int[] points2, int points3) {
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
      wrapped.drawString(iterator, x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
      wrapped.drawString(iterator, x, y);
    }

    @Override
    public void drawString(String str, float x, float y) {
      wrapped.drawString(str, x, y);
    }

    @Override
    public void drawString(String str, int x, int y) {
      wrapped.drawString(str, x, y);
    }

    @Override
    public void fill(Shape s) {
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
    }

    @Override
    public void fillPolygon(int[] points, int[] points2, int points3) {
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
      wrapped.fillRect(x, y, width, height);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    }

    @Override
    public Color getBackground() {
      return wrapped.getBackground();
    }

    @Override
    public Shape getClip() {
      return wrapped.getClip();
    }

    @Override
    public Rectangle getClipBounds() {
      return wrapped.getClipBounds();
    }

    @Override
    public Color getColor() {
      return wrapped.getColor();
    }

    @Override
    public Composite getComposite() {
      return wrapped.getComposite();
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
      return wrapped.getDeviceConfiguration();
    }

    @Override
    public Font getFont() {
      return wrapped.getFont();
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
      return wrapped.getFontMetrics(f);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
      return wrapped.getFontRenderContext();
    }

    @Override
    public Paint getPaint() {
      return wrapped.getPaint();
    }

    @Override
    public Object getRenderingHint(Key hintKey) {
      return wrapped.getRenderingHint(hintKey);
    }

    @Override
    public RenderingHints getRenderingHints() {
      return wrapped.getRenderingHints();
    }

    @Override
    public Stroke getStroke() {
      return wrapped.getStroke();
    }

    @Override
    public AffineTransform getTransform() {
      return wrapped.getTransform();
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
      return wrapped.hit(rect, s, onStroke);
    }

    @Override
    public void rotate(double theta) {
      wrapped.rotate(theta);
    }

    @Override
    public void rotate(double theta, double x, double y) {
      wrapped.rotate(theta, x, y);
    }

    @Override
    public void scale(double sx, double sy) {
      wrapped.scale(sx, sy);
    }

    @Override
    public void setBackground(Color color) {
      wrapped.setBackground(color);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
      wrapped.setClip(x, y, width, height);
    }

    @Override
    public void setClip(Shape clip) {
      wrapped.setClip(clip);
    }

    @Override
    public void setColor(Color c) {
      wrapped.setColor(c);
    }

    @Override
    public void setComposite(Composite comp) {
      wrapped.setComposite(comp);
    }

    @Override
    public void setFont(Font font) {
      wrapped.setFont(font);
    }

    @Override
    public void setPaint(Paint paint) {
      wrapped.setPaint(paint);
    }

    @Override
    public void setPaintMode() {
      wrapped.setPaintMode();
    }

    @Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
      wrapped.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
      wrapped.setRenderingHints(hints);
    }

    @Override
    public void setStroke(Stroke s) {
      wrapped.setStroke(s);
    }

    @Override
    public void setTransform(AffineTransform Tx) {
      wrapped.setTransform(Tx);
    }

    @Override
    public void setXORMode(Color c1) {
      wrapped.setXORMode(c1);
    }

    @Override
    public void shear(double shx, double shy) {
      wrapped.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform Tx) {
      wrapped.transform(Tx);
    }

    @Override
    public void translate(double tx, double ty) {
      wrapped.translate(tx, ty);
    }

    @Override
    public void translate(int x, int y) {
      wrapped.translate(x, y);
    }

  }

  @Override
  public void paintComponent(Graphics g) {
    if(UIManager.getLookAndFeel() instanceof SynthLookAndFeel) {
      super.paintComponent(new LabelGraphics2D((Graphics2D)g));
    } else {
      super.paintComponent(g);
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
  public void updateUI() {
    super.updateUI();
    adjustLook();
  }

  @Override
  public void setText(String text) {
    super.setText(text);
    setCaretPosition(0);
  }

}
