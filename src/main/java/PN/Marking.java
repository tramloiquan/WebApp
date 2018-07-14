package PN;

import CPN.*;
import java.util.ArrayList;

public class Marking {
  private int[] marking;

  public Marking(int[] marking) {
    this.marking = marking;
  }

  public int[] getMarking() {
    return this.marking;
  }

  public String getCode() {
    StringBuilder res = new StringBuilder();
    for (int i: marking) {
      res.append(i);
    }
    return res.toString();
  }
}
