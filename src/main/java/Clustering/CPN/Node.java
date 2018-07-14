package Clustering.CPN;

import java.util.ArrayList;

public class Node {
  private int id;
  private Marking marking;
  private Variable variable;
  private ArrayList<Arc> outArcs;
  private ArrayList<Arc> inArcs;

  public Node(int id, Marking marking, Variable variable) {
    this.id = id;
    this.marking = marking;
    this.variable = variable;
    this.outArcs = new ArrayList<Arc>();
    this.inArcs = new ArrayList<Arc>();
  }
}
