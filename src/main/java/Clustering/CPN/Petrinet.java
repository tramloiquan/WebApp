package Clustering.CPN;

import java.util.ArrayList;

public class Petrinet {
  private ArrayList<Place> places;
  private ArrayList<Transition> transitions;
  private ArrayList<Sensor> sensors;
  private ArrayList<Channel> channels;
  private int THRESHOLD = 100000;

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

  public void operate() {
    int placeSize = places.size();
    int transitionSize = transitions.size();
    // get initial Marking
    int[] _marking = new int[placeSize];
    int i = 0;
    for (Place p: this.places) {
      _marking[i++] = p.getToken();
    }
    Marking marking = new Marking(_marking);
    // get initial Variable
    int sensorSize = this.sensors.size();
    int channelSize = this.channels.size();
    int[] _buffer = new int[sensorSize];
    int[] _queue = new int[sensorSize];
    float[] _energy = new float[sensorSize];
    int[] _cbuffer = new int[channelSize];
    i = 0;
    for (Sensor s: sensors) {
      _buffer[i] = s.getB();
      _queue[i] = s.getQ();
      //_energy[i] = s.getEnergy();
      i++;
    }
    i = 0;
    for (Channel c: channels) {
      _cbuffer[i++] = c.getCb();
    }
    Variable variable  = new Variable(_buffer, _queue, _energy, _cbuffer);
    // get initial Node
    int count = 0;
    Node init = new Node(count++, marking, variable);
    Graph graph = new Graph(init);




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

  private ArrayList<Transition> getEnabledTransitons() {
		ArrayList<Transition> result = new ArrayList<Transition>();
		for (Transition t: this.transitions) {
			if (t.isEnabled()) {
				result.add(t);
			}
		}
		return result;
	}

  private void fire(Transition t) {
//    String type = t.getType();
//    if (type == Constant.GEN || type == Constant.PROC) {
//      int id = t.getId();
//      Sensor s = this.sensors[id];
//      //int pack = randomBuffer(s);
//      //s.b2q(pack);
//      //if (s.isEmptyBuffer()) {
//        t.getIn().setToken(0);
//      }
//      t.getOut().setToken(1);
//    } else if (type == Constant.SEND) {
//      Sensor s = this.sensors[t.getIn().getId()];
//      Channel c = this.channels[t.getId()];
//      int pack = randomQueue(s);
//      //q2cb(s, c, pack);
//      //if (s.isEmptyBuffer()) {
//        t.getIn().setToken(0);
//      }
//      t.getOut().setToken(1);
//    } else if (type == Constant.TRAN) {
//      Sensor s = this.sensors[t.getOut().getId()];
//      Channel c = this.channels[t.getId()];
//      int pack = randomCbuffer(c);
//      cb2b(c, s, pack);
//      if (s.isEmptyBuffer()) {
//        t.getIn().setToken(0);
//      }
//      t.getOut().setToken(1);
//    }
  }
}
