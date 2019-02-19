/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.dj.swingsuite;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * A combo box which allows a wide popup when the combo size is smaller than long items it contains.
 * @author Christopher Deckers
 */
public class JWidePopupComboBox<E> extends JComboBox<E> {

  public JWidePopupComboBox() {
  }

  public JWidePopupComboBox(final E items[]) {
    super(items);
  }

  public JWidePopupComboBox(Vector<E> items) {
    super(items);
  }

  public JWidePopupComboBox(ComboBoxModel<E> aModel) {
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
  private boolean isSmallerDefaultPreferredSizeAllowed;

  /**
   * Set the preferred width of the combo, which is taken into account in the preferred size computations.
   * @param preferredWidth the preferred width to set, or null to clear it.
   * @param isSmallerDefaultPreferredSizeAllowed true to let getPreferredSize() return the default preferred size if it is smaller than this preferred width.
   */
  public void setPreferredWidth(Integer preferredWidth, boolean isSmallerDefaultPreferredSizeAllowed) {
    this.preferredWidth = preferredWidth;
    this.isSmallerDefaultPreferredSizeAllowed = isSmallerDefaultPreferredSizeAllowed;
    super.setPreferredSize(null);
  }

  /**
   * Get the preferred width of the combo.
   * @return the preferred width, or null if it is not set.
   */
  public Integer getPreferredWidth() {
    return preferredWidth;
  }

  public boolean isSmallerDefaultPreferredSizeAllowed() {
    return isSmallerDefaultPreferredSizeAllowed;
  }

  @Override
  public void setPreferredSize(Dimension preferredSize) {
    preferredWidth = null;
    super.setPreferredSize(preferredSize);
  }

  private Integer maximumWidth;

  /**
   * Set the maximum width of the combo, which is taken into account in the maximum size computations.
   * @param maximumWidth the maximum width to set, or null to clear it.
   */
  public void setMaximumWidth(Integer maximumWidth) {
    this.maximumWidth = maximumWidth;
    super.setMaximumSize(null);
  }

  /**
   * Get the maximum width of the combo.
   * @return the maximum width, or null if it is not set.
   */
  public Integer getMaximumWidth() {
    return maximumWidth;
  }

  @Override
  public void setMaximumSize(Dimension maximumSize) {
    maximumWidth = null;
    super.setMaximumSize(maximumSize);
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension preferredSize = super.getPreferredSize();
    if(isPreferredSizeSet()) {
      return preferredSize;
    }
    if(preferredWidth != null && (!isSmallerDefaultPreferredSizeAllowed || preferredSize.width > preferredWidth)) {
      preferredSize.width = preferredWidth;
    }
    return preferredSize;
  }

  @Override
  public Dimension getMaximumSize() {
      Dimension maximumSize = super.getMaximumSize();
      if(isMaximumSizeSet()) {
          return maximumSize;
      }
      if(maximumWidth != null) {
          maximumSize.width = maximumWidth;
      }
      return maximumSize;
  }

  @Override
  public Dimension getMinimumSize() {
    Dimension minimumSize = super.getMinimumSize();
    if(isMinimumSizeSet()) {
      return minimumSize;
    }
    if(preferredWidth != null && preferredWidth < minimumSize.width) {
      minimumSize.width = preferredWidth;
    }
    return minimumSize;
  }

}
