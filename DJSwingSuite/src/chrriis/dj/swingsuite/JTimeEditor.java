/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A simple time editor.
 * @author Christopher Deckers
 */
public class JTimeEditor extends JPanel {

  public static final int MINUTE_PRECISION = 1;
  public static final int SECOND_PRECISION = 2;
  public static final int MILLISECOND_PRECISION = 3;

  public JTimeEditor(int precision) {
    this(null, precision);
  }

  private TimeEntryField hourEntryField;
  private TimeEntryField minuteEntryField;
  private TimeEntryField secondEntryField;
  private TimeEntryField millisecondEntryField;

  public JTimeEditor(Calendar calendar, int precision) {
    super(new BorderLayout());
    JPanel editorPane = new JPanel(new GridBagLayout());
    // Hours
    hourEntryField = createTimeEntryField(2, "00", 24);
    hourEntryField.setBorder(BorderFactory.createEmptyBorder());
    editorPane.setBackground(hourEntryField.getBackground());
    int x = 0;
    editorPane.add(hourEntryField, new GridBagConstraints(x++, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 1, 1));
    // Minutes
    editorPane.add(new JLabel(":"), new GridBagConstraints(x++, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 1, 1));
    minuteEntryField = createTimeEntryField(2, "00", 60);
    minuteEntryField.setBorder(BorderFactory.createEmptyBorder());
    editorPane.add(minuteEntryField, new GridBagConstraints(x++, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 1, 1));
    addNavigationListener(hourEntryField, minuteEntryField);
    if(precision > MINUTE_PRECISION) {
      // Seconds
      editorPane.add(new JLabel(":"), new GridBagConstraints(x++, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 1, 1));
      secondEntryField = createTimeEntryField(2, "00", 60);
      secondEntryField.setBorder(BorderFactory.createEmptyBorder());
      editorPane.add(secondEntryField, new GridBagConstraints(x++, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 1, 1));
      addNavigationListener(minuteEntryField, secondEntryField);
      if(precision > SECOND_PRECISION) {
        // Milliseconds
        editorPane.add(new JLabel("."), new GridBagConstraints(x++, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 1, 1));
        millisecondEntryField = createTimeEntryField(3, "000", 1000);
        millisecondEntryField.setBorder(BorderFactory.createEmptyBorder());
        editorPane.add(millisecondEntryField, new GridBagConstraints(x++, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 1, 1));
        addNavigationListener(secondEntryField, millisecondEntryField);
      }
    }
    editorPane.setMinimumSize(editorPane.getPreferredSize());
    editorPane.setPreferredSize(editorPane.getPreferredSize());
    // Spinner
    final JSpinner spinner = new JSpinner();
    // Spinner border is very big. Text field border seems more appropriate but we need to add a small insets.
    spinner.setBorder(BorderFactory.createCompoundBorder(UIManager.getBorder("TextField.border"), BorderFactory.createEmptyBorder(1, 0, 1, 1)));
    spinner.setEditor(editorPane);
    spinner.getModel().addChangeListener(new ChangeListener() {
      private int startValue = (Integer)spinner.getModel().getValue();
      public void stateChanged(ChangeEvent e) {
        int newValue = (Integer)spinner.getModel().getValue();
        if(focusedField == null) {
          focusedField = hourEntryField;
        }
        if(newValue > startValue) {
          focusedField.adjust(1);
        } else if(newValue < startValue) {
          focusedField.adjust(-1);
        }
        startValue = newValue;
      }
    });
    add(spinner, BorderLayout.CENTER);
    setTime(calendar);
  }
  
  private class FieldListener extends KeyAdapter implements FocusListener {
    private TimeEntryField mainField;
    private TimeEntryField nextField;
    public FieldListener(TimeEntryField mainField, TimeEntryField nextField) {
      this.mainField = mainField;
      this.nextField = nextField;
    }
    private int downCount = 0;
    @Override
    public void keyPressed(KeyEvent e) {
      downCount++;
      switch(e.getKeyCode()) {
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_KP_RIGHT:
          if(mainField.getCaretPosition() == mainField.getText().length()) {
            nextField.requestFocus();
          }
          break;
      }
    }
    @Override
    public void keyReleased(KeyEvent e) {
      downCount = Math.max(0, downCount - 1);
      if(e.getModifiers() != 0 || downCount > 0) {
        return;
      }
      int keyChar = e.getKeyChar();
      if(keyChar >= '0' && keyChar <= '9') {
        int timeValue = mainField.getTimeValue();
        if(timeValue * 10 >= mainField.getMaxBound()) {
          nextField.requestFocus();
        }
      }
    }
    public void focusGained(FocusEvent e) {
      downCount = 0;
    }
    public void focusLost(FocusEvent e) {
      downCount = 0;
    }
  }

  private void addNavigationListener(final TimeEntryField mainField, final TimeEntryField nextField) {
    FieldListener fieldListener = new FieldListener(mainField, nextField);
    mainField.addFocusListener(fieldListener);
    mainField.addKeyListener(fieldListener);
    nextField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
          case KeyEvent.VK_LEFT:
          case KeyEvent.VK_KP_LEFT:
            if(nextField.getSelectionStart() == 0) {
              mainField.requestFocus();
            }
            break;
        }
      }
    });
  }
  
  private Calendar calendar;

  /**
   * Set the time, using the appropriate fields from this calendar.
   */
  public void setTime(Calendar calendar) {
    if(calendar == null) {
      calendar = Calendar.getInstance();
    }
    this.calendar = (Calendar)calendar.clone();
    hourEntryField.setTimeValue(calendar.get(Calendar.HOUR_OF_DAY));
    minuteEntryField.setTimeValue(calendar.get(Calendar.MINUTE));
    if(secondEntryField != null) {
      secondEntryField.setTimeValue(calendar.get(Calendar.SECOND));
      if(millisecondEntryField != null) {
        millisecondEntryField.setTimeValue(calendar.get(Calendar.MILLISECOND));
      }
    }
  }

  /**
   * Get the time, using the calendar that was used last to populate the field with setting the relevant fields.
   * @return the time.
   */
  public Calendar getTime() {
    calendar.set(Calendar.HOUR_OF_DAY, hourEntryField.getTimeValue());
    calendar.set(Calendar.MINUTE, minuteEntryField.getTimeValue());
    if(secondEntryField != null) {
      calendar.set(Calendar.SECOND, secondEntryField.getTimeValue());
      if(millisecondEntryField != null) {
        calendar.set(Calendar.MILLISECOND, millisecondEntryField.getTimeValue());
      }
    }
    return (Calendar)calendar.clone();
  }

  private TimeEntryField focusedField;

  private static class TimeEntryField extends JTextEntryField {

    private int maxBound;

    public TimeEntryField(final int fieldLength, String defaultString, final int maxBound) {
      super(defaultString, fieldLength);
      this.maxBound = maxBound;
      setHorizontalAlignment(JTextField.RIGHT);
      setValidator(new TextEntryValidator() {
        @Override
        public boolean isTextAllowed(JTextEntryField textEntryField, String text) {
          if(text.length() > fieldLength) {
            return false;
          }
          if(text.length() == 0) {
            return true;
          }
          int hour;
          try {
            hour = Integer.parseInt(text);
          } catch(NumberFormatException e) {
            return false;
          }
          return hour >= 0 && hour < maxBound;
        }
      });
    }

    public int getMaxBound() {
      return maxBound;
    }
    
    public void adjust(int count) {
      int value;
      try {
        value = Integer.parseInt(getText());
      } catch(NumberFormatException e) {
        value = 0;
      }
      value += count;
      setTimeValue(value);
      selectAll();
    }

    public int getTimeValue() {
      try {
        return Integer.parseInt(getText());
      } catch(NumberFormatException e) {
        return 0;
      }
    }

    public void setTimeValue(int value) {
      value = ((value % maxBound) + maxBound) % maxBound;
      int columns = getColumns();
      String sValue = String.valueOf(value);
      char[] chars = new char[columns - sValue.length()];
      Arrays.fill(chars, '0');
      setText(new String(chars) + sValue);
    }

  }

  private TimeEntryField createTimeEntryField(final int fieldLength, String defaultString, final int maxBound) {
    final TimeEntryField timeEntryField = new TimeEntryField(fieldLength, defaultString, maxBound);
    if(focusedField == null) {
      focusedField = timeEntryField;
    }
    timeEntryField.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        focusedField = timeEntryField;
      }
      @Override
      public void focusLost(FocusEvent e) {
        timeEntryField.setTimeValue(timeEntryField.getTimeValue());
      }
    });
    return timeEntryField;
  }

}
