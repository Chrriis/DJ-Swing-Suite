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
import chrriis.dj.swingsuite.demo.fundamentalcomponents.ComboButtonExample;
import chrriis.dj.swingsuite.demo.fundamentalcomponents.LinkExample;
import chrriis.dj.swingsuite.demo.fundamentalcomponents.SelectableTreeExample;
import chrriis.dj.swingsuite.demo.fundamentalcomponents.TitledSeparatorExample;

/**
 * @author Christopher Deckers
 */
public class DemoExampleDefinitionLoader {

  public static List<ExampleGroup> getExampleGroupList() {
    List<ExampleGroup> exampleGroupList = new ArrayList<ExampleGroup>();
    exampleGroupList.add(new ExampleGroup("Introduction", new Example[] {
        new Example("Swing Suite", SwingSuite.class, "The motivations behind this project.", false),
    }));
    exampleGroupList.add(new ExampleGroup("Entry Fields", new Example[] {
        new Example("Text and Numbers", TextAndNumberFieldsExample.class, "This is a simple example that shows text and number fields.", true),
        new Example("Validators, Formatters, Masks", ValidatorsFormattersAndMasksExample.class, "How to use validators, formatters and masks to control inputs and improve usability.", true),
    }));
    exampleGroupList.add(new ExampleGroup("Toolbox", new Example[] {
        new Example("Combo Buttons", ComboButtonExample.class, "Combo buttons are generally found in tool bars, often to offer a list of choices and optionally a default action.", true),
        new Example("Links", LinkExample.class, "Users are generally familiar with links: they are simple, intuitive and generally provide some information through tool tips.", true),
        new Example("Selectable Tree", SelectableTreeExample.class, "A tree where the user can click in the empty area, and drag the mouse over some nodes to select them. Modifiers (control and shift) can add or exclude to the current selection.", true),
        new Example("Titled Separator", TitledSeparatorExample.class, "Titled separators are generally more elegant than titled borders and do not require nesting panels.", true),
    }));
    return exampleGroupList;
  }

}
