package Clustering.CPN;

import java.util.ArrayList;

public class Transition {
  private String type;
  private int id;
  private Place in;
  private Place out;

  public Transition(String type, int id, Place in, Place out) {
    this.type = type;
    this.id = id;
    this.in = in;
    this.out = out;
  }

  public String getType() {
    return this.type;
  }

  public int getId() {
    return this.id;
  }

  public Place getIn() {
    return this.in;
  }

  public Place getOut() {
    return this.out;
  }

  public boolean isEnabled() {
    if (this.in.getToken() > 0) {
      return true;
    }
    return false;
  }
}
