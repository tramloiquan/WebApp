package PN;

import CPN.*;

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

  public String getName() {
    return type+id;
  }

  public boolean isEnabled() {
    if (this.in.getToken() > 0) {
      return true;
    }
    return false;
  }

  public String export() {
    StringBuilder sb = new StringBuilder();
    sb.append("<transition ").append("id=\"").append(id).append("\">").append("<label>")
      .append(type).append(id).append("</label>")
      .append("</transition>").append("\n");
    sb.append("\t<arc>")
      .append("<transition>").append(id).append("</transition>")
      .append("<place>").append(in.getId()).append("</place>")
      .append("<direction>").append("PLACE_TO_TRANSITION").append("</direction>")
      .append("</arc>").append("\n");
    sb.append("\t<arc>")
      .append("<transition>").append(id).append("</transition>")
      .append("<place>").append(out.getId()).append("</place>")
      .append("<direction>").append("TRANSITION_TO_PLACE").append("</direction>")
      .append("</arc>").append("\n");
    return sb.toString();
  }
}
