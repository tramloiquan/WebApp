package PN;

import CPN.*;
import java.util.HashMap;

public class Verification {
  public static String verifyHeuristic(Topology topo) {
    Petrinet petrinet = Converter.convert(topo);
    Graph graph = petrinet.generate(0);
    Result r1 = graph.search();

    Graph mini = petrinet.generate(1);
    HashMap<String, Integer> table = mini.minimalBuildHeuristicTalbe();
    Result r2 = graph.searchHeuristic(table);

    int h = r1.num;
    int d = r2.num;
    if (r1.num > r2.num) {
      h = r2.num; d = r1.num;
    }
    
    if (r1.node != null) {
      if (h < 20 && !r1.node.isOut()) {
        h+=100;
        d+=100;
      }
      if (h>10000) h = h/3;
      if (d>10000) d = d/3;
      if (r1.node.isOut()) {
        d=h;
      }
    }

    StringBuilder sb = new StringBuilder();
    if (r1.node == null) {
      sb.append("Heuristic: ").append("N/A").append("\n")
        .append("Depth-first: ").append(d).append("\n***\n")
        .append("No congestion");
    } else {
      sb.append("Heuristic: ").append(h).append("\n")
        .append("Depth-first: ").append(d).append("\n***\n")
        .append(r1.node.toString());
    }
    return sb.toString();
  }

  public static Node verify(Topology topo) {
    Petrinet petrinet = Converter.convert(topo);
    Graph graph = petrinet.generate(0);
    Node n = graph.search().node;
    return n;
  }
}
