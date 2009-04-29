/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * @author Christopher Deckers
 */
public class JWidePopupComboBox extends JComboBox {

  public JWidePopupComboBox() {
  }

  public JWidePopupComboBox(final Object items[]) {
    super(items);
  }

  public JWidePopupComboBox(Vector<?> items) {
    super(items);
  }

  public JWidePopupComboBox(ComboBoxModel aModel) {
    super(aModel);
  }

  private boolean layingOut;

  @Override
  public void doLayout() {
    try {
      layingOut = true;
      super.doLayout();
    } finally {
      layingOut = false;
    }
  }

  @Override
  public Dimension getSize() {
    Dimension size = super.getSize();
    if(!layingOut) {
      size.width = Math.max(size.width, super.getPreferredSize().width);
    }
    return size;
  }

  private Integer maxWidth;

  public void setMaximumWidth(Integer maxWidth) {
    this.maxWidth = maxWidth;
    preferredSize = null;
  }

  private Dimension preferredSize;

  @Override
  public void setPreferredSize(Dimension preferredSize) {
    maxWidth = null;
    this.preferredSize = preferredSize;
  }

  @Override
  public Dimension getPreferredSize() {
    if(preferredSize != null) {
      return preferredSize;
    }
    Dimension preferredSize = super.getPreferredSize();
    if(maxWidth != null) {
      preferredSize.width = maxWidth;
    }
    return preferredSize;
  }

}
