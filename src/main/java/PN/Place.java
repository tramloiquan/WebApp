package PN;

import CPN.*;
import java.util.ArrayList;

public class Place {
  private String type;
  private int id;
  private int token;

  public Place(String type, int id, int token) {
    this.type = type;
    this.id = id;
    this.token = token;
  }

  public String getType() {
    return this.type;
  }

  public int getId() {
    return this.id;
  }

  public int getToken() {
    return this.token;
  }

  public void setToken(int token) {
    this.token = token;
  }

  public String export() {
    StringBuilder sb = new StringBuilder();
    sb.append("<place ").append("id=\"").append(id).append("\">").append("<label>")
      .append(type).append(id).append("</label>")
      .append("<token>").append(token).append("</token>")
      .append("</place>").append("\n");
    return sb.toString();
  }
}
