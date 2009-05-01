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
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class LayeredIcon extends ImageIcon {

  protected List<Icon> iconList = new ArrayList<Icon>();
  protected List<Point> positionsList = new ArrayList<Point>();

  protected int width;
  protected int height;

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

  protected Component component;

  @Override
  public void paintIcon(Component component, Graphics g, int x, int y) {
    this.component = component;
    for(int i=0; i<iconList.size(); i++) {
      Point position = positionsList.get(i);
      iconList.get(i).paintIcon(component, g, x + position.x, y + position.y);
    }
  }

  public void addIcon(Icon icon) {
    addIcon(icon, new Point(0, 0));
  }

  protected ImageObserver observer;

  protected Image currentImage;

  public void addIcon(Icon icon, int x, int y) {
    addIcon(icon, new Point(x, y));
  }

  public void addIcon(Icon icon, Point position) {
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
    iconList.add(icon);
    positionsList.add(position);
//    computeAttributes();
  }

  @Override
  public Image getImage() {
    return currentImage;
  }

}
