/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.util.EventListener;

import chrriis.dj.swingsuite.JTriStateCheckBox.CheckState;

/**
 * A listener for tri state check boxes.
 * @author Christopher Deckers
 */
public interface TriStateCheckBoxListener extends EventListener {

  /**
   * Invoked when the tri state check box changes of state.
   * @param checkBox the check box that has changed state.
   * @param state the new state.
   */
  public void stateChanged(JTriStateCheckBox checkBox, CheckState state);

}
