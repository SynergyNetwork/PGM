package tc.oc.tablist;

import tc.oc.component.types.PersonalizedText;
import tc.oc.util.collection.DefaultProvider;

public class BlankTabEntry extends StaticTabEntry {

  public static class Factory implements DefaultProvider<Integer, TabEntry> {
    @Override
    public TabEntry get(Integer key) {
      return new BlankTabEntry();
    }
  }

  private static final PersonalizedText BLANK_COMPONENT = new PersonalizedText("");

  public BlankTabEntry() {
    super(BLANK_COMPONENT);
  }
}
