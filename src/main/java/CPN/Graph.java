package CPN;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private Marking initMarking;
    private int total;
    private double pc;
    private double pnc;

    public Graph(Marking init) {
        this.initMarking = init;
        this.total = 1;
        this.pc = 0.0;
        this.pnc = 0.0;
    }

    public void generateGraph() {
        List<Marking> lm = new ArrayList<Marking>();
        lm.add(this.initMarking);
        long start = System.currentTimeMillis();
        while (true) {
            System.out.println(this.total);
            if (lm.size() == 0) break;

            Marking t = lm.remove(0);
            if (t.isCongest() || t.getProb() == 0) {
                this.pc += t.getProb();
                continue;
            }
            if (this.total < General.mmax) {
                ArrayList<String> allEnable = t.enableBinding();
                int numBinding = allEnable.size();
                if (numBinding != 0) {
                    General.params.clear();

                    ArrayList<Marking> child = new ArrayList<Marking>();
                    t.fireAll(allEnable);
                    for (int k = numBinding; k > 0; k--) {
                        int p = k-1;
                        int indexsubset[] = new int[numBinding];
                        for (int i = 0; i < k; i++) indexsubset[i]=i;
                        while (p >= 0) {
                            ArrayList<String> subset = new ArrayList<String>();
                            for(int i = 0; i < k; i++) subset.add(allEnable.get(indexsubset[i]));
                            child.addAll(t.clone().firing(subset, allEnable));
                            if (p == numBinding-1) break;
                            if(indexsubset[k-1] == numBinding-1) p--;
                            else p = k-1;
                            if (p >= 0) for (int i = k-1; i >= p; i--) indexsubset[i] = indexsubset[p]+i-p+1;
                        }
//                        child.addAll(t.nextMarking(allEnable, k));
                    }
                    double tprob = 0.0;
                    ArrayList<Marking> tm = new ArrayList<Marking>();
                    for (Marking m : child) {
                        tprob += m.getProb();
                        for(Marking m1: lm){
                            if(m1.equals(m)) tm.add(m);
                        }
                    }
                    child.removeAll(tm);
//                    this.pnc += t.getProb() - tprob;
                    this.total += child.size();
                    for(Marking m: child){
                        m.setProb(m.getProb()+(t.getProb()-tprob)/child.size());
                    }
//          t.setChild(child);
                    lm.addAll(child);
                } else {
                    this.pnc += t.getProb();
                }
            } else {
                this.pnc += t.getProb();
            }
        }
        System.out.println("TOOK: " + (System.currentTimeMillis() - start));
    }

    public double getpc() {
        return this.pc;
    }

    public double getpnc() {
        return this.pnc;
    }

    public int getTotal() {
        return this.total;
    }
}