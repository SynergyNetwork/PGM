package tc.oc.pgm.regions;

import com.google.common.collect.ArrayListMultimap;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.util.Vector;

public class RFAContext {
  protected final ArrayListMultimap<RFAScope, RegionFilterApplication> rfas =
      ArrayListMultimap.create();
  protected final List<RegionFilterApplication> byPriority = new ArrayList<>();

  public RFAContext() {}

  /** Append the given RFA, giving it the lowest priority */
  public void add(RegionFilterApplication rfa) {
    this.rfas.put(rfa.scope, rfa);
    this.byPriority.add(rfa);
  }

  /** Prepend the given RFA, giving it the highest priority */
  public void prepend(RegionFilterApplication rfa) {
    this.rfas.get(rfa.scope).add(0, rfa);
    this.byPriority.add(0, rfa);
  }

  /** Return all RFAs in the given scope, in priority order */
  public Iterable<RegionFilterApplication> get(RFAScope scope) {
    return this.rfas.get(scope);
  }

  /** Return all RFAs in priority order */
  public Iterable<RegionFilterApplication> getAll() {
    return this.byPriority;
  }

  public RegionFilterApplication getNearest(Vector pos) {
    RegionFilterApplication nearest = null;
    double distance = Double.POSITIVE_INFINITY;

    for (RegionFilterApplication rfa : byPriority) {
      double d = pos.distanceSquared(rfa.region.getBounds().getCenterPoint());
      if (d < distance) {
        nearest = rfa;
        distance = d;
      }
    }

    return nearest;
  }
}
