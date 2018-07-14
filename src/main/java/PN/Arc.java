package PN;

import CPN.*;
import java.util.ArrayList;

public class Arc {
  private String name;
  private Node node;

  public Arc(String name, Node node) {
    this.name = name;
    this.node = node;
  }

  public String getName() {
    return this.name;
  }

  public Node getNode() {
    return this.node;
  }
}
