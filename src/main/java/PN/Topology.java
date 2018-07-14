package PN;

import CPN.*;
import java.util.ArrayList;

public class Topology {
  private ArrayList<Sensor> sensors;
  private ArrayList<Channel> channels;

  public Topology(ArrayList<Sensor> sensors, ArrayList<Channel> channels) {
    this.sensors = sensors;
    this.channels = channels;
  }

  public ArrayList<Sensor> getSensors() {
    return this.sensors;
  }

  public ArrayList<Channel> getChannels() {
    return this.channels;
  }
}
