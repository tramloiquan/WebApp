package PN;

import CPN.*;
import java.util.ArrayList;

public class Variable {
  private int[] buffer;
  private int[] queue;
  private float[] energy;
  private int[] cbuffer;

  public Variable(int[] buffer, int[] queue, float[] energy, int[] cbuffer) {
    this.buffer = buffer;
    this.queue = queue;
    this.energy = energy;
    this.cbuffer = cbuffer;
  }

  public int[] getBuffer() {
    return this.buffer;
  }

  public int[] getQueue() {
    return this.queue;
  }

  public float[] getEnergy() {
    return this.energy;
  }

  public int[] getCbuffer() {
    return this.cbuffer;
  }

  public String getCode() {
    StringBuilder res = new StringBuilder();
    for (int i: buffer) {
      res.append(i);
    }
    for (int i: queue) {
      res.append(i);
    }
    for (int i: cbuffer) {
      res.append(i);
    }
    for (float i: energy) {
      res.append(i);
    }
    return res.toString();
  }

}
