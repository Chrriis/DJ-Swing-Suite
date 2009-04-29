package chrriis.dj.swingsuite;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.basic.BasicRadioButtonUI;

public class JTriStateCheckBox extends JCheckBox {

  public static enum CheckState {
    SELECTED,
    NOT_SELECTED,
    INDETERMINATE,
  }

  private class TriStateModel extends ToggleButtonModel {

    private boolean isIndeterminate;

    public boolean isIndeterminate() {
      return isIndeterminate;
    }

    private void setIndeterminate(boolean isIndeterminate) {
      this.isIndeterminate = isIndeterminate;
    }

    private void setState(CheckState state) {
      if(getState() == state) {
        return;
      }
      if (state == CheckState.NOT_SELECTED) {
        setIndeterminate(false);
        setSelected(false);
      } else if (state == CheckState.SELECTED) {
        setIndeterminate(false);
        setSelected(true);
      } else {
        setIndeterminate(true);
        setSelected(false);
      }
      repaint();
      for(TriStateCheckBoxListener listener: getTriStateCheckBoxListeners()) {
        listener.stateChanged(JTriStateCheckBox.this, state);
      }
    }

    private CheckState getState() {
      if (isIndeterminate) {
        return CheckState.INDETERMINATE;
      }
      if (isSelected()) {
        return CheckState.SELECTED;
      }
      return CheckState.NOT_SELECTED;
    }

    private CheckState[] rollingStates = new CheckState[] {CheckState.NOT_SELECTED, CheckState.SELECTED, CheckState.INDETERMINATE};
    private int rollingIndex;

    public void setRollingStates(CheckState... states) {
      rollingStates = states;
      adjustRollingState(getState());
    }

    public void adjustRollingState(CheckState state) {
      rollingIndex = -1;
      for(int i=0; i<rollingStates.length; i++) {
        if(rollingStates[i] == state) {
          rollingIndex = i;
          break;
        }
      }
    }

    private void nextState() {
      setState(rollingStates[++rollingIndex % rollingStates.length]);
    }

    @Override
    public void setEnabled(boolean b) {
      setFocusable(b);
      super.setEnabled(b);
    }

    @Override
    public void setSelected(boolean b) {
      if(!isUIAdjustement) {
        isUIAdjustement = true;
        nextState();
        isUIAdjustement = false;
        return;
      }
      super.setSelected(b);
    }

  }

  public JTriStateCheckBox() {
    init();
  }

  public JTriStateCheckBox(Action a) {
    super(a);
    init();
  }

  public JTriStateCheckBox(Icon icon) {
    super(icon);
    init();
  }

  public JTriStateCheckBox(Icon icon, boolean selected) {
    super(icon, selected);
    init();
  }

  public JTriStateCheckBox(String text) {
    super(text);
    init();
  }

  public JTriStateCheckBox(String text, boolean selected) {
    super(text, selected);
    init();
  }

  public JTriStateCheckBox(String text, Icon icon) {
    super(text, icon);
    init();
  }

  public JTriStateCheckBox(String text, Icon icon, boolean selected) {
    super(text, icon, selected);
    init();
  }

  private TriStateModel model;

  private void init() {
    model = new TriStateModel();
    setModel(model);
    ActionMap map = new ActionMapUIResource();
    map.put("pressed", new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        grabFocus();
        isUIAdjustement = true;
        model.nextState();
        isUIAdjustement = false;
        repaint();
      }
    });
    map.put("released", null);
    SwingUtilities.replaceUIActionMap(this, map);
  }

  @Override
  public void setSelected(boolean b) {
    if (b) {
      setState(CheckState.SELECTED);
    } else {
      setState(CheckState.NOT_SELECTED);
    }
  }

  private boolean isUIAdjustement;

  public void setState(CheckState state) {
    isUIAdjustement = true;
    model.setState(state);
    isUIAdjustement = false;
    model.adjustRollingState(state);
  }

  public CheckState getState() {
    return model.getState();
  }

  private static class IndeterminateIcon implements Icon {
    private Icon icon;
    public IndeterminateIcon(Icon icon) {
      this.icon = icon;
    }
    public int getIconWidth() {
      return icon.getIconWidth();
    }
    public int getIconHeight() {
      return icon.getIconHeight();
    }
    public void paintIcon(Component c, Graphics g, int x, int y) {
      icon.paintIcon(c, g, x, y);
      if((((JTriStateCheckBox)c).getState() == CheckState.INDETERMINATE)) {
        g.setColor(indeterminateColor);
        g.fillRect(x + 3, y + 3, icon.getIconWidth() - 6, icon.getIconHeight() - 6);
      }
    }

  }

  private static Color indeterminateColor;

  @Override
  public void updateUI() {
    super.updateUI();
    if(indeterminateColor == null) {
      JCheckBox checkBox = new JCheckBox();
      checkBox.setSize(checkBox.getPreferredSize());
      int imgWidth = checkBox.getWidth() - 4;
      int imgHeight = checkBox.getHeight() - 4;
      BufferedImage img1 = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
      Graphics graphics = img1.getGraphics();
      graphics.translate(-2, -2);
      checkBox.print(graphics);
      graphics.dispose();
      checkBox.setSelected(true);
      BufferedImage img2 = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
      graphics = img2.getGraphics();
      graphics.translate(-2, -2);
      checkBox.print(graphics);
      graphics.dispose();
      int diff = 0;
      int rgb = 0;
      // Try to find a color in the selected box that has the highest difference with the color at the same position before selection.
      for(int i=0; i<imgWidth; i++) {
        for(int j=0; j<imgHeight; j++) {
          int rgb1 = img1.getRGB(i, j);
          int rgb2 = img2.getRGB(i, j);
          int rDiff = Math.abs(rgb1 & 0xff - rgb2 & 0xff) + Math.abs(rgb1 >> 8 & 0xff - rgb2 >> 8 & 0xff) + Math.abs(rgb1 >> 16 & 0xff - rgb2 >> 16 & 0xff);
          if(rDiff > diff) {
            diff = rDiff;
            rgb = rgb2;
          }
        }
      }
      indeterminateColor = new Color(rgb);
    }
    Icon icon = ((BasicRadioButtonUI)getUI()).getDefaultIcon();
    if(icon instanceof IndeterminateIcon) {
      return;
    }
    setIcon(new IndeterminateIcon(icon));
  }

  public void setRollingStates(CheckState... states) {
    model.setRollingStates(states);
  }

  /**
   * Add a listener that will be invoked when the state changes.
   * @param listener the listener to register.
   */
  public void addTriStateCheckBoxListener(TriStateCheckBoxListener listener) {
    listenerList.add(TriStateCheckBoxListener.class, listener);
  }

  /**
   * Remove a listener from the list of listeners that are invoked when the state changes.
   * @param listener the listener to unregister.
   */
  public void removeLinkListener(TriStateCheckBoxListener listener) {
    listenerList.remove(TriStateCheckBoxListener.class, listener);
  }

  /**
   * Get all the listeners that are invoked when the state changes.
   * @return the listeners.
   */
  public TriStateCheckBoxListener[] getTriStateCheckBoxListeners() {
    return listenerList.getListeners(TriStateCheckBoxListener.class);
  }

}
