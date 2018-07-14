package Clustering.CPN;

import java.util.ArrayList;

public class Converter {
  public Petrinet convert(Topology topo) {
    ArrayList<Sensor> sensors = topo.getSensors();
    ArrayList<Channel> channels = topo.getChannels();

    ArrayList<Place> places = new ArrayList<>();
    ArrayList<Transition> transitions = new ArrayList<>();

    for (Sensor s: sensors) {
      int sType = s.getType();
      int sId = s.getId();
      if (sType == 0) {
        Place in = new Place(Constant.IN, sId, 1);
        Place inter = new Place(Constant.INT, sId, 0);
        places.add(in);
        places.add(inter);
        transitions.add(new Transition(Constant.GEN, sId, in, inter));
      } else if (sType == 1) {
        Place in = new Place(Constant.IN, sId, 0);
        Place inter = new Place(Constant.INT, sId, 0);
        places.add(in);
        places.add(inter);
        transitions.add(new Transition(Constant.PROC, sId, in, inter));
      } else if (sType == 2) {
        Place in = new Place(Constant.IN, sId, 0);
        Place out = new Place(Constant.OUT, sId, 0);
        places.add(in);
        places.add(out);
        transitions.add(new Transition(Constant.PROC, sId, in, out));
      }
    }

    for (Channel c: channels) {
      Place in = null;
      Place out = null;
      int cId = c.getId();
      for (Place p: places) {
        if (p.getId() == c.getFrom() && p.getType() == Constant.INT) {
          in = p;
        }
        if (p.getId() == c.getTo() && p.getType() == Constant.IN) {
          out = p;
        }
      }
      Place inter = new Place(Constant.OUT, cId, 0);
      transitions.add(new Transition(Constant.SEND, cId, in, inter));
      transitions.add(new Transition(Constant.TRAN, cId, inter, out));
    }

    return new Petrinet(places, transitions, topo);
  }

}
