package Clustering.CPN;
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

}
