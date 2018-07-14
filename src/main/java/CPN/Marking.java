package CPN;

import org.apache.commons.math3.distribution.UniformRealDistribution;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "net")
@XmlAccessorType(XmlAccessType.NONE)
public class Marking{
	@XmlElementWrapper(name = "channels")
	@XmlElement(name = "channel")
	private ArrayList<Channel> lc;
	@XmlElementWrapper(name = "sensors")
	@XmlElement(name = "sensor")
	private ArrayList<Sensor>  ls;
	@XmlElement(name = "general_setting")
	private General g;
	private double prob;
	private int pmax;

	public Marking(){}
	public Marking(ArrayList<Channel> lc, ArrayList<Sensor> ls, double p, int pmax){
		this.lc = lc;
		this.ls = ls;
		this.prob = p;
		this.pmax = pmax;
	}

	public Marking clone(){
		ArrayList<Channel> lnc = new ArrayList<Channel>();
		for(Channel c: this.lc) lnc.add(c.clone());
		ArrayList<Sensor> lns = new ArrayList<Sensor>();
		for(Sensor s: this.ls) lns.add(s.clone());
		return new Marking(lnc, lns, this.prob, this.pmax);
	}

	public boolean equals(Marking m){
	    if(this.lc.size() != m.getLc().size()) return false;
	    if(this.ls.size() != m.getLs().size()) return false;
	    for(int i=0; i<this.lc.size(); i++){
	        if(!this.lc.get(i).equals(m.getLc().get(i))) return false;
        }
        for(int i = 0; i<this.ls.size(); i++){
	        if(!this.ls.get(i).equals(m.getLs().get(i))) return false;
        }
        return this.prob == m.getProb() && this.pmax == m.getPmax();
    }

	public double getProb() {return this.prob;}
	public void setProb(double p){this.prob = p;}
    public ArrayList<Channel> getLc() {
        return lc;
    }
    public ArrayList<Sensor> getLs() {
        return ls;
    }
    public void setG(General g){this.g = g;}
    public int getPmax() {
        return pmax;
    }
    //	public void setChild(ArrayList<Marking> lm) {this.lm = lm;}

	public void infoMappingfun(){
		for(int i=0; i<this.ls.size(); i++){
			ArrayList<Integer> lindex = new ArrayList<Integer>();
			for(int j = 0; j < this.lc.size(); j++)
				if(this.ls.get(i).getId() == this.lc.get(j).getFrom()) lindex.add(j);
			General.infoMaping.put(i, lindex);
		}
	}

	public boolean isCongest(){
		for(Channel c: this.lc){
			if(c.getCb() > c.getCbmax()) return true;
		}
		for(Sensor s: this.ls){
			if(calLambdaB(s.getEnergy(), s.getOriginalEnergy()) == 0) return true;
			if((s.getQ() > s.getSqmax() && s.getType() != 2) || (s.getType() != 0 && s.getB() > s.getSbmax())) return true;
		}
		return false;
	}
	private ArrayList<String> whichSend(){
		ArrayList<String> li = new ArrayList<String>();
		for(int i=0; i<this.ls.size(); i++)
			if(this.ls.get(i).getQ() > 0 && this.ls.get(i).getType() != 2) li.add(i +"_send");
		return li;
	}

	private ArrayList<String> whichTransmit(){
		ArrayList<String> li = new ArrayList<String>();
		for(int i = 0; i< this.lc.size(); i++)
			if(this.lc.get(i).getCb() > 0) li.add(i+"_trans");
		return li;
	}

	private ArrayList<String> whichGenerate(){
		ArrayList<String> li = new ArrayList<String>();
		Sensor source = null;
		int sindex = -1;
		for(int i = 0; i<this.ls.size(); i++) {
			if (this.ls.get(i).getType() == 0) {
				source = this.ls.get(i);
				sindex = i;
			}
		}
		if(this.pmax > 0 && source.getQ() < source.getSqmax()) li.add(sindex+"_gen");
		return li;
	}

	private ArrayList<String> whichProcess(){
		ArrayList<String> li = new ArrayList<String>();
		for(int i=0; i<this.ls.size(); i++)
			if(this.ls.get(i).getType() != 0 && this.ls.get(i).getB() > 0 && this.ls.get(i).getB() <= this.ls.get(i).getSbmax())
				li.add(i+"_proc");
		return li;
	}

	private double calLambdaB(double e, double oe){
		double re = e/oe;
		if(re >= 0.81) return 1.0;
		else if(re < 0.81 && re > 0.53) return (re-0.53)/(0.81-0.53);
		else return 0.0;
	}

	private double calPRR(){
		if(General.snr > 8) return 1.0;
		else if(General.snr <= 8 && General.snr >=6) {
			double p = new UniformRealDistribution(0.85, 1.0).sample();
			return p;
		}
		else if(General.snr <= 6 && General.snr >=2){
			double p =  new UniformRealDistribution(0.25, 0.85).sample();
			return p;
		}
		else{
			double p = new UniformRealDistribution(0.0,0.25).sample();
			if(p < 0.0) p = 0;
			else if(p > 0.25) p = 0.25;
			return p;
		}
	}

	public void fireAll(List<String> allEnable){
		for(String e: allEnable){
			if(e.contains("gen")){
				int id = Integer.parseInt(e.split("_")[0]);
				Sensor ps = this.ls.get(id);
				int ranpackage = (int) new UniformRealDistribution(ps.getmu_p()-2*ps.getsigma_p(), ps.getmu_p()+2*ps.getsigma_p()).sample();

				int numpackage = Math.min(Math.min(this.pmax, ranpackage), ps.getSqmax()-ps.getQ());
				double eg = new UniformRealDistribution(0.2, 0.3).sample();

				double lambda_B = calLambdaB(ps.getEnergy(), ps.getOriginalEnergy());

				double tp = General.lambda_s0 *lambda_B;

				General.params.put("gen_e"+id, eg);
				General.params.put("gen_p"+id, numpackage);
				General.params.put("gen_prob"+id, tp);
			}
			else if(e.contains("send")){
				int id = Integer.parseInt(e.split("_")[0]);
				Sensor ps = this.ls.get(id);

				int ranpackage = (int) new UniformRealDistribution(ps.getmu_s()-2*ps.getsigma_s(), ps.getmu_s()+2*ps.getsigma_s()).sample();

				int numpackage = Math.min(ps.getQ(), ranpackage);
				double eg = new UniformRealDistribution(0.1, 0.2).sample();
				if(eg < 0.1) eg = 0.1;
				else if(eg > 0.2) eg = 0.2;

				double lambda_B = calLambdaB(ps.getEnergy(), ps.getOriginalEnergy());

				double tp = lambda_B* General.lambda_s0;

				General.params.put("send_e"+id, eg);
				General.params.put("send_p"+id, numpackage);
				General.params.put("send_prob"+id, tp);
			}
			else if(e.contains("trans")){
				Channel pc = null;
				int id = Integer.parseInt(e.split("_")[0]);
				pc = this.lc.get(id);
				int ranpackage = (int) new UniformRealDistribution(pc.getmu_t()-2*pc.getsigma_t(), pc.getmu_t()+2*pc.getsigma_t()).sample();

				int numpackage = Math.min(ranpackage, pc.getCb());

				double tp = calPRR()* General.lambda_c0;

				General.params.put("trans_p"+id, numpackage);
				General.params.put("trans_prob"+id, tp);
			}
			else if(e.contains("proc")){
				int id =  Integer.parseInt(e.split("_")[0]);
				Sensor ps = this.ls.get(id);

				int ranpackage = (int) new UniformRealDistribution(ps.getmu_p()-2*ps.getsigma_p(), ps.getmu_p()+2*ps.getsigma_p()).sample();
				int numpackage = 0;
				if(ps.getType() == 2){
					numpackage = Math.min(ps.getB(), ranpackage);
				}
				else numpackage = Math.min(Math.min(ps.getB(), ranpackage), ps.getSqmax()-ps.getQ());
				double eg = new UniformRealDistribution(0.1, 0.2).sample();

				double lambda_B = calLambdaB(ps.getEnergy(), ps.getOriginalEnergy());

				double tp = General.lambda_s0 *lambda_B;

				General.params.put("proc_e"+id, eg);
				General.params.put("proc_p"+id, numpackage);
				General.params.put("proc_prob"+id, tp);
			}
		}
	}

	public ArrayList<Marking> firing(ArrayList<String> enable, ArrayList<String> allEnable){
		ArrayList<ArrayList> ls2c = new ArrayList<ArrayList>();

		for(String e: enable){
			if(e.contains("gen")){
				int id = Integer.parseInt(e.split("_")[0]);
				Sensor ps = this.ls.get(id);
				int numpackage = (Integer) General.params.get("gen_p"+id);
				double eg = (Double) General.params.get("gen_e"+id);

				this.pmax -= numpackage;
				ps.setQ(ps.getQ()+numpackage);
				ps.setEnergy((float)(ps.getEnergy()-eg));

				this.prob = this.prob*((Double) General.params.get("gen_prob"+id));
			}

			else if(e.contains("send")){
				int id = Integer.parseInt(e.split("_")[0]);
				Sensor ps = this.ls.get(id);

				int numpackage = (Integer) General.params.get("send_p"+id);
				double eg = (Double) General.params.get("send_e"+id);

				ps.setQ(ps.getQ()-numpackage);
				ps.setEnergy((float)(ps.getEnergy()-eg));

				ArrayList<Channel> s2c = new ArrayList<Channel>();
				int numChannel = General.infoMaping.get(id).size();
				for(int i: General.infoMaping.get(id)) {
					Channel pc = this.lc.get(i).clone();
					pc.setId(i);
					pc.setCb(pc.getCb() + numpackage);
					this.prob = this.prob * ((Double) General.params.get("send_prob"+id)) * (1.0 / numChannel);
					s2c.add(pc);
				}
				ls2c.add(s2c);
			}
			else if(e.contains("trans")){

				int id = Integer.parseInt(e.split("_")[0]);
				Channel pc = this.lc.get(id);
				Sensor ps = null;
				for (Sensor s : this.ls) {
					if (s.getId() == pc.getTo()) ps = s;
				}

				int numpackage = (Integer) General.params.get("trans_p"+id);
				pc.setCb(pc.getCb()-numpackage);
				ps.setB(ps.getB()+numpackage);

				this.prob = this.prob*((Double) General.params.get("trans_prob"+id));
			}
			else if(e.contains("proc")){
				int id =  Integer.parseInt(e.split("_")[0]);
				Sensor ps = this.ls.get(id);

				int numpackage = Math.min((Integer) General.params.get("proc_p"+id), ps.getSqmax() - ps.getQ());
				double eg = (Double) General.params.get("proc_e"+id);

				ps.setQ(ps.getQ()+numpackage);
				ps.setB(ps.getB()-numpackage);

				ps.setEnergy((float)(ps.getEnergy()-eg));

				this.prob = this.prob*((Double) General.params.get("proc_prob"+id));
			}
		}
		// them xs khong fire
		ArrayList<String> tEn = (ArrayList<String>) allEnable.clone();
		tEn.removeAll(enable);
		for(String e: tEn) {
            if (e.contains("gen")) {
                this.prob = this.prob * (1 - ((Double) General.params.get("gen_prob" + e.split("_")[0])));
            } else if (e.contains("send")) {
                this.prob = this.prob * (1 - ((Double) General.params.get("send_prob" + e.split("_")[0])));
            } else if (e.contains("trans")) {
                this.prob = this.prob * (1 - ((Double) General.params.get("trans_prob" + e.split("_")[0])));
            } else if (e.contains("proc")) {
                this.prob = this.prob * (1 - ((Double) General.params.get("proc_prob" + e.split("_")[0])));
            }
        }
		// gen all possibilities
		ArrayList<Marking> res = new ArrayList<Marking>();
		if(ls2c.size() != 0){
			ArrayList<ArrayList<Channel>> allpos = new ArrayList<ArrayList<Channel>>();
			ArrayList<Channel> t = ls2c.remove(0);
			for(Channel c: t){
				ArrayList<Channel> tc = new ArrayList<Channel>();
				tc.add(c);
				allpos.add(tc);
			}
			while(ls2c.size() != 0){
				t = ls2c.remove(0);
				int tsize = allpos.size();
				for(int i = 0; i<tsize; i++){
					ArrayList<Channel> t1 = allpos.remove(0);
					for(Channel c: t){
						ArrayList<Channel> t2 = (ArrayList) t1.clone();
						t2.add(c);
						allpos.add(t2);
					}
				}
			}

			for(int i = 1; i< allpos.size(); i++){
				ArrayList<Channel> tc = (ArrayList) this.lc.clone();
				for(Channel c: allpos.get(i)) tc.set(c.getId(), c);
				Marking m = new Marking(tc, this.ls, this.prob, this.pmax);
				res.add(m);
			}
			for(Channel c: allpos.get(0)) this.lc.set(c.getId(), c);
			res.add(this);
		}
		else res.add(this);
		return res;
	}

	public ArrayList<String> enableBinding(){
		ArrayList<String> allEnable = new ArrayList<String>();
		allEnable.addAll(whichGenerate());
		allEnable.addAll(whichProcess());
		allEnable.addAll(whichTransmit());
		allEnable.addAll(whichSend());
		return allEnable;
	}

//	private void subset(ArrayList<String> allEnable, ArrayList<String> sb, ArrayList<ArrayList> allSub, int k){
//		if(k==0){
//			allSub.add((ArrayList)sb.clone());
//			return;
//		}
//		for(int i = 0; i < allEnable.size()-k+1; i++){
//			sb.add(allEnable.get(i));
//			ArrayList<String> t = new ArrayList<String>(allEnable.subList(i, allEnable.size()));
//			t.remove(0);
//			subset(t, sb, allSub, k-1);
//			sb.remove(allEnable.get(i));
//		}
//	}

//	public ArrayList<Marking> nextMarking(ArrayList<String> allEnable, int k){
//		ArrayList<String> sb = new ArrayList<String>();
//		ArrayList<ArrayList> allSub = new ArrayList<ArrayList>();
//		ArrayList<Marking> lm = new ArrayList<Marking>();
//
//		subset(allEnable, sb, allSub, k);
//
//		for(ArrayList enable: allSub){
//			lm.addAll(this.clone().firing(enable, allEnable));
//		}
//		return lm;
//	}

	@Override
	public String toString() {
		String sm = "{\n";
		sm+="Sensor:\n";
		for(Sensor s: this.ls) sm += s.toString();
		sm+="Channel:\n";
		for(Channel c: this.lc) sm+=c.toString();
		sm+="Prob: "+this.prob+"\n";
		sm+="}\n";
		return sm;
	}
}