package Clustering.CPN;

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
}
