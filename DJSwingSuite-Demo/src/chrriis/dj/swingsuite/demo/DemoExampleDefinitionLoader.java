/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.swingsuite.demo;

import java.util.ArrayList;
import java.util.List;

import chrriis.dj.swingsuite.demo.examples.entryfields.TextAndNumberFieldsExample;
import chrriis.dj.swingsuite.demo.examples.entryfields.ValidatorsFormattersAndMasksExample;
import chrriis.dj.swingsuite.demo.examples.introduction.SwingSuite;
import chrriis.dj.swingsuite.demo.examples.toolbox.ComboButtonExample;
import chrriis.dj.swingsuite.demo.examples.toolbox.DragSelectTreeExample;
import chrriis.dj.swingsuite.demo.examples.toolbox.LinkExample;
import chrriis.dj.swingsuite.demo.examples.toolbox.SelectableLabelExample;
import chrriis.dj.swingsuite.demo.examples.toolbox.TitledSeparatorExample;
import chrriis.dj.swingsuite.demo.examples.toolbox.TriStateCheckBoxExample;
import chrriis.dj.swingsuite.demo.examples.toolbox.WidePopupComboBoxExample;
import chrriis.dj.swingsuite.demo.examples.utilities.AutoScrollExample;
import chrriis.dj.swingsuite.demo.examples.utilities.LayeredIconExample;
import chrriis.dj.swingsuite.demo.examples.utilities.SelectAllOnFocusExample;
import chrriis.dj.swingsuite.demo.examples.utilities.TableColumnAutoFitExample;
import chrriis.dj.swingsuite.demo.examples.utilities.WildcardsConversionExample;

/**
 * @author Christopher Deckers
 */
public class DemoExampleDefinitionLoader {

  public static List<ExampleGroup> getExampleGroupList() {
    List<ExampleGroup> exampleGroupList = new ArrayList<ExampleGroup>();
    exampleGroupList.add(new ExampleGroup("Introduction", new Example[] {
        new Example("Swing Suite", SwingSuite.class, "The motivations behind this project.", false),
    }));
    exampleGroupList.add(new ExampleGroup("Toolbox", new Example[] {
        new Example("Combo button", ComboButtonExample.class, "Combo buttons are generally found in tool bars, often to offer a list of choices and optionally a default action.", true),
        new Example("Tri-state check box", TriStateCheckBoxExample.class, "Tri-state check boxes, with advanced capabilities like rolling cycle definition.", true),
        new Example("Link", LinkExample.class, "Users are generally familiar with links: they are simple, intuitive and generally provide some information through tool tips.", true),
        new Example("Selectable label", SelectableLabelExample.class, "A label that allows text selection.", true),
        new Example("Wide popup combo box", WidePopupComboBoxExample.class, "Wide popup combo boxes are a solution to the problem of having content that is too long. Such content generally messes up the user interface layout.", true),
        new Example("Drag-select tree", DragSelectTreeExample.class, "A tree where the user can click in the empty area, and drag the mouse over some nodes to select them. Modifiers (control and shift) can add or exclude to the current selection.", true),
        new Example("Titled separator", TitledSeparatorExample.class, "Titled separators are generally more elegant than titled borders and do not require nesting panels.", true),
    }));
    exampleGroupList.add(new ExampleGroup("Entry Fields", new Example[] {
        new Example("Text and Numbers", TextAndNumberFieldsExample.class, "This is a simple example that shows text and number fields.", true),
        new Example("Validators, Formatters, Masks", ValidatorsFormattersAndMasksExample.class, "How to use validators, formatters and masks to control inputs and improve usability.", true),
    }));
    exampleGroupList.add(new ExampleGroup("Utilities", new Example[] {
        new Example("Layered icon", LayeredIconExample.class, "A layered icon is an icon that aggregates icons, which can be at any location within their parent icon. Layered icons even support animated images in their composition.", true),
        new Example("Auto scroll", AutoScrollExample.class, "Enable the auto-scroll feature to simplify scrolling: a click with the middle mouse button then moving the mouse directs scrolling (as seen in web browsers).", true),
        new Example("Table column auto fit", TableColumnAutoFitExample.class, "Auto fit one or all the columns of a table.", true),
        new Example("Select all on focus", SelectAllOnFocusExample.class, "Make a text component select its entire text when it receives the focus. This utility takes care of proper selection in certain cases such as inaccurate click (mouse moves during the click).", true),
        new Example("Wildcards conversion", WildcardsConversionExample.class, "Convert a simple text containing wildcards (generally entered by the user) to a regular expression that can be used to filter/search.", true),
    }));
    return exampleGroupList;
  }

}
