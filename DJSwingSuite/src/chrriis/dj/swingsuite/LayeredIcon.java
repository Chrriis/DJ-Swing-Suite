/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class LayeredIcon extends ImageIcon {

  private List<Icon> iconList = new ArrayList<Icon>(3);
  private List<Point> iconLocationList = new ArrayList<Point>(3);

  private int width;
  private int height;

  public LayeredIcon(int width, int height) {
    this.width = width;
    this.height = height;
  }

  @Override
  public int getIconWidth() {
    return width;
  }

  @Override
  public int getIconHeight() {
    return height;
  }

  private Component component;

  @Override
  public void paintIcon(Component component, Graphics g, int x, int y) {
    this.component = component;
    for(int i=0; i<iconList.size(); i++) {
      Point location = iconLocationList.get(i);
      iconList.get(i).paintIcon(component, g, x + location.x, y + location.y);
    }
  }

  /**
   * Get all the icons.
   * @return the icons.
   */
  public Icon[] getIcons() {
    return iconList.toArray(new Icon[0]);
  }

  /**
   * Get all the icon locations.
   * @return the locations of all the icons.
   */
  public Point[] getIconLocations() {
    return iconLocationList.toArray(new Point[0]);
  }

  /**
   * Get the number of icons.
   * @return the number of icons.
   */
  public int getIconCount() {
    return iconList.size();
  }

  /**
   * Remove an icon.
   * @param icon the icon to remove.
   */
  public void removeIcon(Icon icon) {
    int index = iconList.indexOf(icon);
    if(index >= 0) {
      iconList.remove(index);
      iconLocationList.remove(index);
    }
  }

  /**
   * Remove all icons.
   */
  public void removeAllIcons() {
    iconList.clear();
    iconLocationList.clear();
  }

  /**
   * Add an icon starting at the top left corner.
   * @param icon the icon to add.
   */
  public void addIcon(Icon icon) {
    addIcon(icon, new Point(0, 0));
  }

  /**
   * Add an icon starting at a specific location.
   * @param icon the icon to add.
   * @param x the x coordinate.
   * @param y the y coordinate.
   */
  public void addIcon(Icon icon, int x, int y) {
    addIcon(icon, new Point(x, y));
  }

  private ImageObserver observer;
  private Image currentImage;

  /**
   * Add an icon starting at a specific location.
   * @param icon the icon to add.
   * @param location the location.
   */
  public void addIcon(Icon icon, Point location) {
    addIcon(icon, location, getIconCount());
  }

  /**
   * Add an icon starting at a specific location.
   * @param icon the icon to add.
   * @param location the location.
   * @param zOrder the zOrder of this icon, where 0 means at the back.
   */
  public void addIcon(Icon icon, Point location, int zOrder) {
    iconList.add(zOrder, icon);
    iconLocationList.add(zOrder, location);
    if(icon instanceof ImageIcon) {
      if(observer == null) {
        observer = new ImageObserver() {
          public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            if(component != null) {
              currentImage = img;
              boolean result = component.imageUpdate(img, infoflags, x, y, width, height);
              currentImage = null;
              return result;
            }
            return false;
          }
        };
      }
      ((ImageIcon)icon).setImageObserver(observer);
    }
  }

  @Override
  public Image getImage() {
    if(currentImage == null) {
      // If an external API wants to get the image, we have to provide it on the fly.
      BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      paintIcon(component, img.getGraphics(), 0, 0);
      return img;
    }
    return currentImage;
  }

}
