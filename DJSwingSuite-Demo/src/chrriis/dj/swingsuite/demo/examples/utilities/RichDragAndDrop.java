/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite.demo.examples.utilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import chrriis.dj.swingsuite.RichDnDManager;
import chrriis.dj.swingsuite.SwingSuiteUtilities;
import chrriis.dj.swingsuite.RichDnDManager.RichDnDImageProvider;

/**
 * @author Christopher Deckers
 */
public class RichDragAndDrop extends JPanel {

  public RichDragAndDrop() {
    super(new BorderLayout());
    JPanel centerPanel = new JPanel(new GridLayout(1, 2));
    // A simple tree
    JTree tree = new JTree();
    RichDnDManager.register(tree);
    tree.setDragEnabled(true);
    centerPanel.add(new JScrollPane(tree));
    // A simple list
    JList list = new JList(new Object[] {"Choice 1", "Choice 2", "Choice 3", "Choice 4", "Choice 5", "Choice 6", "Choice 7", "Choice 8", "Choice 9", "Choice 10"});
    RichDnDManager.register(list);
    list.setDragEnabled(true);
    centerPanel.add(new JScrollPane(list));
    // A custom DnD gesture listener which we are going to use on our 2 sample labels
    DragGestureListener labelGestureListener = new DragGestureListener() {
      public void dragGestureRecognized(DragGestureEvent dge) {
        dge.startDrag(null, new StringSelection(((JLabel)dge.getComponent()).getText()));
      }
    };
    add(centerPanel, BorderLayout.CENTER);
    JPanel northPanel = new JPanel(new GridLayout(2, 1));
    // A label with default image
    final JLabel labelWithDefaultImage = new JLabel("A label that we can drag and drop (uses the component's image)", JLabel.CENTER);
    RichDnDManager.register(labelWithDefaultImage);
    DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(labelWithDefaultImage, DnDConstants.ACTION_COPY, labelGestureListener);
    northPanel.add(labelWithDefaultImage);
    // A label with custom image
    final JLabel labelWithCustomImage = new JLabel("A label that we can drag and drop (uses a custom image)", JLabel.CENTER);
    RichDnDManager.register(labelWithCustomImage, new RichDnDImageProvider() {
      public Icon getDnDImage(Component c, int x, int y, int width, int height, Point mouseLocation) {
        JLabel jLabel = new JLabel("That's a custom image!", new ImageIcon(getClass().getResource("resource/package16.png")), JLabel.CENTER);
        Dimension size = jLabel.getPreferredSize();
        jLabel.setSize(size);
        BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        jLabel.paint(g);
        g.dispose();
        // We place the image above the mouse cursor and centered horizontally
        mouseLocation.x = size.width / 2;
        mouseLocation.y = 20;
        return new ImageIcon(img);
      }
    });
    DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(labelWithCustomImage, DnDConstants.ACTION_COPY, labelGestureListener);
    northPanel.add(labelWithCustomImage);
    add(northPanel, BorderLayout.NORTH);
    // A panel that allows to view the content of the drag
    JPanel dropPanel = new JPanel(new BorderLayout());
    dropPanel.setBorder(BorderFactory.createTitledBorder("Drop area"));
    final JLabel dropContentLabel = new JLabel("Drag content: -");
    dropPanel.add(dropContentLabel, BorderLayout.CENTER);
    dropPanel.setTransferHandler(new TransferHandler() {
      @Override
      public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        for(DataFlavor dataFlavor: transferFlavors) {
          if(dataFlavor.equals(DataFlavor.stringFlavor)) {
            return true;
          }
        }
        return false;
      }
      @Override
      public boolean importData(JComponent comp, Transferable t) {
        DataFlavor[] transferFlavors = t.getTransferDataFlavors();
        for(DataFlavor dataFlavor: transferFlavors) {
          if(dataFlavor.equals(DataFlavor.stringFlavor)) {
            try {
              String text = (String)t.getTransferData(dataFlavor);
              if(text != null) {
                text = text.replace('\n', ' ').replace('\r', ' ');
              }
              dropContentLabel.setText("Drag content: " + text);
            } catch(Exception e) {
              e.printStackTrace();
            }
          }
        }
        return false;
      }
    });
    add(dropPanel, BorderLayout.SOUTH);
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    SwingSuiteUtilities.setPreferredLookAndFeel();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame frame = new JFrame("DJ Swing Suite Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new RichDragAndDrop(), BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }

}
