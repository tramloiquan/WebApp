package PN;

import CPN.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.LinkedList;
import java.util.Queue;

public class Petrinet {
  private ArrayList<Place> places;
  private ArrayList<Transition> transitions;
  private ArrayList<Sensor> sensors;
  private ArrayList<Channel> channels;
  private int THRESHOLD = 1000000;
  private Random rand = new Random();
  private int mode = 0;

  public Petrinet(ArrayList<Place> places, ArrayList<Transition> transitions, Topology topo) {
    this.places = places;
    this.transitions = transitions;
    this.sensors = topo.getSensors();
    this.channels = topo.getChannels();
  }

  public ArrayList<Place> getPlaces() {
    return this.places;
  }

  public ArrayList<Transition> getTransitions() {
    return this.transitions;
  }

  public Graph generate(int m) {
    mode = m;
    Constant.MODE = m;
    Marking marking = getCurrentMarking();
    Variable variable  = getCurrentVariable();
    // get initial Node
    int count = 0;
    Node init = new Node(count++, marking, variable);
    Graph graph = new Graph(init);
    Queue<Node> queue = new LinkedList<Node>();
    queue.add(init);
    while (!queue.isEmpty() && count < THRESHOLD) {
      Node current = (Node)queue.poll();
      applyMarking(current.getMarking());
      ArrayList<Transition> enabledTransitions = getEnabledTransitons();
      for (Transition t: enabledTransitions) {
        applyMarking(current.getMarking());
        applyVariable(current.getVariable());
        boolean congest = fire(t);
        Node newNode = new Node(count++, getCurrentMarking(), getCurrentVariable());
        newNode.setCongestion(congest);
        String code = newNode.getCode();
        Node findNode = graph.find(code);
        if (findNode == null) {
          current.addChild(new Arc(t.getName(), newNode));
          newNode.addParent(new Arc(t.getName(), current));
          graph.add(code, newNode);
          if (!congest) {
            queue.add(newNode);
          } else {
//            System.out.println(congest);
          }
        } else {
          current.addChild(new Arc(t.getName(), findNode));
          findNode.addParent(new Arc(t.getName(), current));
        }
      }
    }
    applyMarking(marking);
    applyVariable(variable);
    return graph;
  }

  private void applyMarking(Marking marking) {
    int[] _marking = marking.getMarking();
    int i = 0;
    for (Place p: this.places) {
      p.setToken(_marking[i++]);
    }
  }

  private void applyVariable(Variable variable) {
    int[] _buffer = variable.getBuffer();
    int[] _queue = variable.getQueue();
    float[] _energy = variable.getEnergy();
    int[] _cbuffer = variable.getCbuffer();
    int i = 0;
    for (Sensor s: this.sensors) {
      s.setB(_buffer[i]);
      s.setQ(_queue[i]);
      s.setEnergy(_energy[i]);
      i++;
    }
    i = 0;
    for (Channel c: channels) {
      c.setCb(_cbuffer[i++]);
    }
  }

  private Marking getCurrentMarking() {
    int[] _marking = new int[places.size()];
    int i = 0;
    for (Place p: this.places) {
      _marking[i++] = p.getToken();
    }
    Marking marking = new Marking(_marking);
    return marking;
  }

  private Variable getCurrentVariable() {
    int sensorSize = this.sensors.size();
    int channelSize = this.channels.size();
    int[] _buffer = new int[sensorSize];
    int[] _queue = new int[sensorSize];
    float[] _energy = new float[sensorSize];
    int[] _cbuffer = new int[channelSize];
    int i = 0;
    for (Sensor s: sensors) {
      _buffer[i] = s.getB();
      _queue[i] = s.getQ();
      _energy[i] = (float) s.getEnergy();
      i++;
    }
    i = 0;
    for (Channel c: channels) {
      _cbuffer[i++] = c.getCb();
    }
    Variable variable  = new Variable(_buffer, _queue, _energy, _cbuffer);
    return variable;
  }

  private ArrayList<Transition> getEnabledTransitons() {
		ArrayList<Transition> result = new ArrayList<Transition>();
		for (Transition t: this.transitions) {
			if (t.isEnabled()) {
        if (t.getType() == Constant.GEN || t.getType() == Constant.PROC) {
          Sensor s = this.sensors.get(t.getId());
          if (s.getQ() >= s.getSqmax()) {
            continue;
          }
        }
				result.add(t);
			}
		}
		return result;
	}

  private boolean fire(Transition t) {
    String type = t.getType();
    if (type == Constant.GEN || type == Constant.PROC) {
      int id = t.getId();
      Sensor s = this.sensors.get(id);
      int pack = randomProcess(s);
      buffer2queue(s, pack);
      if (s.getB() <= 0) {
        t.getIn().setToken(0);
      }
      if (s.getQ() > 0) {
        t.getOut().setToken(1);
      }
      decreaseEnergy(s, type);
      return false;
    } else if (type == Constant.SEND) {
      Sensor s = this.sensors.get(t.getIn().getId());
      Channel c = this.channels.get(t.getId());
      int pack = randomSend(s);
      queue2cbuffer(s, c, pack);
      if (s.getQ() <= 0) {
        t.getIn().setToken(0);
      }
      if (c.getCb() > 0) {
        t.getOut().setToken(1);
      }
      decreaseEnergy(s, type);
      if (c.getCb() > c.getCbmax()) {
        return true;
      }
      return false;
    } else if (type == Constant.TRAN) {
      Sensor s = this.sensors.get(t.getOut().getId());
      Channel c = this.channels.get(t.getId());
      int pack = randomTransmit(c);
      cbuffer2queue(c, s, pack);
      if (c.getCb() <= 0) {
        t.getIn().setToken(0);
      }
      if (s.getB() > 0) {
        t.getOut().setToken(1);
      }
      if (s.getB() > s.getSbmax()) {
        return true;
      }
      return false;
    }
    return false;
  }

  private int randomProcess(Sensor s) {
    float pack = (float)rand.nextGaussian()*s.getsigma_p()+s.getmu_p();
    if (mode == 0) {
      if (pack < s.getmu_p()-2*s.getsigma_p()) {
        pack = s.getmu_p()-2*s.getsigma_p();
      } else if (pack > s.getmu_p()+2*s.getsigma_p()) {
        pack = s.getmu_p()+2*s.getsigma_p();
      }
      if (pack <= 0) {
        pack = 1;
      }
    } else {
      pack = s.getmu_p()+2*s.getsigma_p();
    }

    return Math.round(pack);
  }

  private int randomSend(Sensor s) {
    float pack = (float)rand.nextGaussian()*s.getsigma_s()+s.getmu_s();
    if (mode == 0) {
      if (pack < s.getmu_s()-2*s.getsigma_s()) {
        pack = s.getmu_s()-2*s.getsigma_s();
      } else if (pack > s.getmu_s()+2*s.getsigma_s()) {
        pack = s.getmu_s()+2*s.getsigma_s();
      }
      if (pack <= 0) {
        pack = 1;
      }
    } else {
      pack = s.getmu_s()+2*s.getsigma_s();
    }
    return Math.round(pack);
  }

  private int randomTransmit(Channel c) {
    float pack = (float)rand.nextGaussian()*c.getsigma_t()+c.getmu_t();
    if (mode == 0) {
      if (pack < c.getmu_t()-2*c.getsigma_t()) {
        pack = c.getmu_t()-2*c.getsigma_t();
      } else if (pack > c.getmu_t()+2*c.getsigma_t()) {
        pack = c.getmu_t()+2*c.getsigma_t();
      }
      if (pack <= 0) {
        pack = 1;
      }
    } else {
      pack = c.getmu_t()+2*c.getsigma_t();
    }
    return Math.round(pack);
  }

  private void buffer2queue(Sensor s, int pack) {
    if (pack > s.getSqmax() - s.getQ() || pack > s.getB()) {
      pack = Math.min(s.getSqmax() - s.getQ(), s.getB());
    }
    s.setB(s.getB()-pack);
    s.setQ(s.getQ()+pack);
  }

  private void queue2cbuffer(Sensor s, Channel c, int pack) {
    if (pack > s.getQ()) {
      pack = s.getQ();
    }
    s.setQ(s.getQ()-pack);
    c.setCb(c.getCb()+pack);
  }

  private void cbuffer2queue(Channel c, Sensor s, int pack) {
    if (pack > c.getCb()) {
      pack = c.getCb();
    }
    c.setCb(c.getCb()-pack);
    s.setB(s.getB()+pack);
  }

  private void decreaseEnergy(Sensor s, String type) {
    if (mode == 1) return;
    if (type == Constant.GEN) {
      float e = s.getEnergy() - (float)(rand.nextInt(2)+2)/10.0f;
      float ei = Math.round(e*10)/10.0f;
      s.setEnergy(ei);
    } else if (type == Constant.SEND || type == Constant.PROC) {
      float e = s.getEnergy() - (float)(rand.nextInt(2)+1)/10.0f;
      float ei = Math.round(e*10)/10.0f;
      s.setEnergy(ei);
    }
  }

  public String export() {
    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
    sb.append("<pnml>\n\t<net>\n");
    for (Place p: places) {
      sb.append("\t").append(p.export());
    }
    for (Transition t: transitions) {
      sb.append("\t").append(t.export());
    }
    sb.append("\t</net>\n</pnml>");
    return sb.toString();
  }
}
