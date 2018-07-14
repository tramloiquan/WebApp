package Clustering.readKWSN;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.javatuples.Pair;
import org.json.simple.JSONArray;

public class Cluster {

    public double calDis(String a, String b, String c, String d) {
        return Math.sqrt(Math.pow(Double.parseDouble(a) - Double.parseDouble(b), 2) + Math.pow(Double.parseDouble(c) - Double.parseDouble(d), 2));
    }

    public boolean checkL(String a, String b, ArrayList<Link> link) {
        boolean check = false;
        for (Link y : link) {
            if (((y.From.equals(a)) && (y.To.equals(b))) | ((y.From.equals(b)) && (y.To.equals(a)))) {
                check = true;
            }
        }
        return check;
    }

    public boolean checkF(String a, String b, ArrayList<Link> link) {
        boolean check = false;
        for (Link y : link) {
            if (((y.From.equals(a)) && (y.To.equals(b)))) {
                check = true;
            }
        }
        return check;
    }

    public boolean checkT(String a, String b, ArrayList<Link> link) {
        boolean check = false;
        for (Link y : link) {
            if (((y.From.equals(b)) && (y.To.equals(a)))) {
                check = true;
            }
        }
        return check;
    }

    public ArrayList<ArrayList<Pair<ArrayList<Sensor>, ArrayList<Link>>>> cluster(ArrayList<Sensor> sensor, ArrayList<Link> link, double s1, double t, int k) {
        double r = (2 * s1) / (t + 1);
        DecimalFormat df = new DecimalFormat("0.00");
        System.out.print("Radius: ");
        System.out.println(df.format(((double) Math.round(r * 100) / 100)));
        stack4clu<Sensor> neighborD = new stack4clu<Sensor>();
        stack4clu<Sensor> neighborL = new stack4clu<Sensor>();
        int indexD = 1;
        boolean checkC = false;
        Cluster clu = new Cluster();

        for (Clustering.readKWSN.Link l : link) {
            String[] part = l.From.split(" ");
            l.From = part[1];
            part = l.To.split(" ");
            l.To = part[1];
        }
        //-----check dense cluster-----
        //gom cum xung quanh not hien tai
        for (Sensor x : sensor) {
            if (x.flag == 0) {
                for (Sensor x1 : sensor) {
                    if (!x1.Id.equals(x.Id) && clu.checkL(x.Id, x1.Id, link)) {
                        double d = clu.calDis(x.position.x, x1.position.x, x.position.y, x1.position.y);
                        if (d < r) {
                            x.flag = indexD;
                            x1.flag = indexD;
                            neighborD.push(x1);
                        }
                    }
                }
                // gom cum cac node hang xom xung quanh node hang xom
                while (!neighborD.isEmpty()) {
                    Sensor z = neighborD.pop();
                    for (Sensor z1 : sensor) {
                        if (!z1.Id.equals(z.Id) && clu.checkL(z.Id, z1.Id, link) && !(z1.flag == indexD)) {
                            double d = clu.calDis(z.position.x, z1.position.x, z.position.y, z1.position.y);
                            if (d < r) {
                                z1.flag = indexD;
                                neighborD.push(z1);
                            }
                        }
                    }
                }
                indexD++;
            }
        }
        ArrayList<Pair<ArrayList<Sensor>, ArrayList<Link>>> topologyD = new ArrayList<Pair<ArrayList<Sensor>, ArrayList<Link>>>();
        // Gom cac sensor lai dua vao flag
        int count = 1;
        boolean addLink = false;
        while (count < indexD) {
            ArrayList<String> delLink = new ArrayList<String>();
            ArrayList<Sensor> temps = new ArrayList<Sensor>();
            ArrayList<Link> templ = new ArrayList<Link>();
            ArrayList<Link> templ1 = new ArrayList<Link>();
            for (Sensor x : sensor) {
                // add link va sensor vao cluster
                if (x.flag == count) {
                    temps.add(x);
                    delLink.add(x.Id);
                    Clustering.readKWSN.Link temp = new Clustering.readKWSN.Link();
                    for (Link l : link) {
                        if (l.From.equals(x.Id) || l.To.equals(x.Id)) {
                            temp = l;
                            addLink = clu.checkL(temp.From, temp.To, templ1);
                            if (addLink == false) {
                                templ1.add(temp);
                            }
                        }
                    }
                }
            }
            for (Link l : templ1) {
                if (delLink.contains(l.From) && delLink.contains(l.To)) {
                    templ.add(l);
                }
            }
            if (temps.size() < k) {
                templ.clear();
                for (Sensor s : temps) {
                    s.flag = 0;
                }
            } else {
                Pair<ArrayList<Sensor>, ArrayList<Link>> tempClu = new Pair<ArrayList<Sensor>, ArrayList<Link>>(temps, templ);
                topologyD.add(tempClu);
            }
            count++;
        }

        //-- cluster imbalance cluster
        //gom cum xung quanh not hien tai
        ArrayList<Pair<ArrayList<Sensor>, ArrayList<Link>>> topologyI = new ArrayList<Pair<ArrayList<Sensor>, ArrayList<Link>>>();
        int indexI = count;
        for (Sensor x : sensor) {
            int sumSend = 1;
            int sumRei = 0;
            if (x.flag == 0 && !(x.getType() == 0) && !(x.getType() == 2)) {
                ArrayList<String> delLink = new ArrayList<String>();
                ArrayList<Sensor> tempS = new ArrayList<Sensor>();
                for (Sensor x1 : sensor) {
                    // tim nhung sensor nao co send packet den x
                    if (!x1.Id.equals(x.Id) && x1.flag == 0 && clu.checkF(x1.Id, x.Id, link)) {
                        tempS.add(x1);
                        delLink.add(x1.Id);
                        sumRei += Integer.parseInt(x1.MaxSendingRate);
                    }
                    // tim nhung sensor nao x send packet den
                    if (!x1.Id.equals(x.Id) && x1.flag == 0 && clu.checkT(x1.Id, x.Id, link)) {
                        tempS.add(x1);
                        delLink.add(x1.Id);
                    }
                }
                // neu thoa dieu kien thi dua vao cluster
                if (sumRei > Integer.parseInt(x.MaxSendingRate) && tempS.size() > 2) {
                    ArrayList<Link> templ = new ArrayList<Link>();
                    ArrayList<Link> templ1 = new ArrayList<Link>();
                    tempS.add(x);
                    delLink.add(x.Id);
                    for (Sensor z : tempS) {
                        // add link va sensor vao cluster
                        z.flag = indexI;
                        Clustering.readKWSN.Link temp = new Clustering.readKWSN.Link();
                        for (Link l : link) {
                            if (l.From.equals(z.Id) || l.To.equals(z.Id)) {
                                temp = l;
                                addLink = clu.checkL(temp.From, temp.To, templ1);
                                if (addLink == false) {
                                    templ1.add(temp);
                                }
                            }
                        }
                    }

                    for (Link l : templ1) {
                        if (delLink.contains(l.From) & delLink.contains(l.To)) {
                            templ.add(l);
                        }
                    }
                    Pair<ArrayList<Sensor>, ArrayList<Link>> tempClu = new Pair<ArrayList<Sensor>, ArrayList<Link>>(tempS, templ);
                    topologyI.add(tempClu);

                }
            }
            indexI++;
        }
        // Cac senssor con lai dua vao abandone sensor
        ArrayList<Sensor> tempS = new ArrayList<Sensor>();
        ArrayList<Link> templ = new ArrayList<Link>();
        for (Sensor x : sensor) {
            if (x.flag == 0) {
                tempS.add(x);
                Clustering.readKWSN.Link temp = new Clustering.readKWSN.Link();
                for (Link l : link) {
                    if (l.From.equals(x.Id) || l.To.equals(x.Id)) {
                        temp = l;
                        addLink = clu.checkL(temp.From, temp.To, templ);
                        if (addLink == false) {
                            templ.add(temp);
                        }
                    }
                }
            }
        }
        ArrayList<Pair<ArrayList<Sensor>, ArrayList<Link>>> topologyA = new ArrayList<Pair<ArrayList<Sensor>, ArrayList<Link>>>();
        Pair<ArrayList<Sensor>, ArrayList<Link>> tempClu = new Pair<ArrayList<Sensor>, ArrayList<Link>>(tempS, templ);
        topologyA.add(tempClu);
        // tra ve ArrayList voi cau truc: 0.DenseCluster 1.ImbalanceCluster 2.AbandonCluster
        ArrayList<ArrayList<Pair<ArrayList<Sensor>, ArrayList<Link>>>> topology = new ArrayList<ArrayList<Pair<ArrayList<Sensor>, ArrayList<Link>>>>();
        topology.add(topologyD);
        topology.add(topologyI);
        topology.add(topologyA);
        return topology;
    }
}
