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
 * A combo box which allows a wide popup when the combo size is smaller than long items it contains.
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

  private Integer preferredWidth;

  /**
   * Set the preferred width of the combo, which is taken into account in the preferred size computations.
   * @param preferredWidth the preferred width to set, or null to clear it.
   */
  public void setPreferredWidth(Integer preferredWidth) {
    this.preferredWidth = preferredWidth;
    preferredSize = null;
  }

  /**
   * Get the preferred width of the combo.
   * @return the preferred width, or null if it is not set.
   */
  public Integer getPreferredWidth() {
    return preferredWidth;
  }

  private Dimension preferredSize;

  @Override
  public void setPreferredSize(Dimension preferredSize) {
    preferredWidth = null;
    this.preferredSize = preferredSize;
  }

  @Override
  public Dimension getPreferredSize() {
    if(preferredSize != null) {
      return preferredSize;
    }
    Dimension preferredSize = super.getPreferredSize();
    if(preferredWidth != null) {
      preferredSize.width = preferredWidth;
    }
    return preferredSize;
  }

}
