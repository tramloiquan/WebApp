package PN;

import CPN.*;
import java.util.ArrayList;

public class Node {
  private int id;
  private Marking marking;
  private Variable variable;
  private ArrayList<Arc> outArcs;
  private ArrayList<Arc> inArcs;
  private boolean congestion = false;
  private boolean visited = false;

  public Node(int id, Marking marking, Variable variable) {
    this.id = id;
    this.marking = marking;
    this.variable = variable;
    this.outArcs = new ArrayList<Arc>();
    this.inArcs = new ArrayList<Arc>();
  }

  public Marking getMarking() {
    return this.marking;
  }

  public Variable getVariable() {
    return this.variable;
  }

  public void setCongestion(boolean congestion) {
    this.congestion = congestion;
  }

  public void addChild(Arc arc) {
    this.outArcs.add(arc);
  }

  public void addParent(Arc arc) {
    this.inArcs.add(arc);
  }

  public boolean isCongesting() {
    if (Constant.MODE == 1) {
      return this.congestion;
    }
    float[] t = variable.getEnergy();
    int c = 0;
    for (float f: t) {
      if (f < 1.5) return true;
    }
    return this.congestion;
  }

  public boolean isOut() {
    float[] t = variable.getEnergy();
    int c = 0;
    for (float f: t) {
      if (f < 1.5) return true;
    }
    return false;
  }

  public ArrayList<Arc> getChildren() {
    return this.outArcs;
  }

  public ArrayList<Arc> getParents() {
    return this.inArcs;
  }

  public void markVisited() {
    this.visited = true;
  }

  public void unmarkVisited() {
    this.visited = false;
  }

  public boolean getVisited() {
    return this.visited;
  }

  public String getCode() {
    return marking.getCode() + variable.getCode();
  }

  public String getMarkingCode() {
    return this.marking.getCode();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    int ns = variable.getBuffer().length;
    int nc = variable.getCbuffer().length;
    for (int i = 0; i < ns; i++) {
      sb.append("Sensor ").append(i).append(": ");
      sb.append(variable.getBuffer()[i]).append(", ");
      sb.append(variable.getQueue()[i]).append(", ");
      sb.append(variable.getEnergy()[i]).append(";\n");
    }
    sb.append("---\n");
    for (int i = 0; i < nc; i++) {
      sb.append("Channel ").append(i).append(": ");
      sb.append(variable.getCbuffer()[i]).append(";\n");
    }
    return sb.toString();
  }
}
