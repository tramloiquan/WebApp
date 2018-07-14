package Clustering.CPN;
import org.javatuples.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import Clustering.readKWSN.*;
public class convKC {

	void initSensor(Clustering.readKWSN.Sensor x) {

	}

	public Pair<ArrayList<CPN.Sensor>, ArrayList<CPN.Channel>> KWSNtoCPN (ArrayList<Clustering.readKWSN.Sensor> listS, ArrayList<Clustering.readKWSN.Link> listL, ArrayList<CPN.Sensor> CPNss, ArrayList<CPN.Channel> CPNcc, int maxB){
		//chi giu lai nhung sensor can thiet
		for (Clustering.readKWSN.Link l : listL) {
			String[] part = l.From.split(" ");
			if( part.length == 1){
				break;
			}else {
				l.From = part[1];
				part = l.To.split(" ");
				l.To = part[1];
			}
		}

		ArrayList<CPN.Sensor> CPNs = new ArrayList<CPN.Sensor>();
		for (CPN.Sensor x : CPNss) {
			CPN.Sensor temp = x.clone();
			CPNs.add(temp);
		}

		ArrayList<CPN.Channel> CPNc = new ArrayList<CPN.Channel>();
		for(CPN.Channel y : CPNcc) {
			CPN.Channel temp = y.clone();
			CPNc.add(temp);
		}


		ArrayList<CPN.Sensor> listCPNs = new ArrayList<CPN.Sensor>();
		for (Clustering.readKWSN.Sensor x : listS) {
			for(CPN.Sensor y : CPNs) {
				if(x.Id.equals(String.valueOf(y.getId()))) {
					listCPNs.add(y);
					break;
				}
			}
		}

		for (Clustering.readKWSN.Sensor x : listS) {
			if (x.Name.equals("Sensor 222")) {
				CPN.Sensor g = new CPN.Sensor(Float.parseFloat(x.position.x),Float.parseFloat(x.position.y), 222,2,0,0,5,5,10f,10.0f,4.5f,0.25f,4.5f,0.25f);
				listCPNs.add(g);
				break;
			}
		}



		//Dua cac link vao lai
		ArrayList<CPN.Channel> listCPNc = new ArrayList<CPN.Channel>();
		int idc = 0;
		for (Clustering.readKWSN.Link y : listL) {
			//Channel(int from, int to, int id, int cb, int cbmax, double mu_t, double sigma_t)
			CPN.Channel c = new CPN.Channel(Integer.parseInt(y.From),Integer.parseInt(y.To),idc,0,maxB,Float.parseFloat(y.MaxSendingRate),0.5f);
			idc++;
			listCPNc.add(c);
		}
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		int ids = 0;
		for(CPN.Sensor x : listCPNs) {
			map.put(x.getId(),ids);
			x.setId(ids);
			ids++;
		}
		//chuan hoa lai topology

			for(CPN.Channel y : listCPNc) {
				y.setFrom(map.get(y.getFrom()));
				y.setTo(map.get(y.getTo()));
			}
		Pair<ArrayList<CPN.Sensor>, ArrayList<CPN.Channel>> pair = new Pair<ArrayList<CPN.Sensor>, ArrayList<CPN.Channel>>(listCPNs, listCPNc);
		return pair;
	}

	public Pair<ArrayList<Clustering.readKWSN.Sensor>, ArrayList<Clustering.readKWSN.Link>> CPNtoKWSN(ArrayList<CPN.Sensor> CPNs, ArrayList<CPN.Channel> CPNc) {

		//chuan hoa sensor va channel ve sensor va link cua minh
		ArrayList<Clustering.readKWSN.Sensor> listS = new ArrayList<Clustering.readKWSN.Sensor>();
		ArrayList<Clustering.readKWSN.Link> listL = new ArrayList<Clustering.readKWSN.Link>();

		for (CPN.Sensor x : CPNs) {
			Clustering.readKWSN.Sensor s = new Clustering.readKWSN.Sensor();
			s.Id = String.valueOf(x.getId());
			s.Init = "false" ;

			double Minp =  x.getmu_p() - 2*x.getsigma_p();
			double Maxp = x.getmu_p() + 2*x.getsigma_p();
			double proR = ThreadLocalRandom.current().nextDouble(Minp, Maxp + 1.0);
			s.MaxProcessingRate = String.valueOf(Math.round(proR));

			double Mins =  x.getmu_s() - 2*x.getsigma_s();
			double Maxs = x.getmu_s() + 2*x.getsigma_s();
			double proS = ThreadLocalRandom.current().nextDouble(Mins, Maxs + 1.0);
			s.MaxSendingRate = String.valueOf(Math.round(proS));

			s.Name = "Sensor " + String.valueOf(x.getId());
			Clustering.readKWSN.Position z = new Clustering.readKWSN.Position();

			z.x = String.valueOf(x.getX());

			z.y = String.valueOf(x.getY());
			s.position = z;
			s.Type = x.getType();
			listS.add(s);
		}
		for (CPN.Channel y : CPNc) {
			Clustering.readKWSN.Link l = new Clustering.readKWSN.Link();
			l.From = "Sensor " + String.valueOf(y.getFrom());
			l.To = "Sensor " + String.valueOf(y.getTo());
			l.id = String.valueOf(y.getFrom())+"_"+String.valueOf(y.getTo());
			double Mint =  y.getmu_t() - 2*y.getsigma_t();
			double Maxt = y.getmu_t() + 2*y.getsigma_t();
			double proT = ThreadLocalRandom.current().nextDouble(Mint, Maxt + 1.0);
			l.MaxSendingRate = String.valueOf(Math.round(proT));
			l.IsConverted = false;	
			listL.add(l);
		}
		Pair<ArrayList<Clustering.readKWSN.Sensor>, ArrayList<Clustering.readKWSN.Link>> KWSN = new Pair<ArrayList<Clustering.readKWSN.Sensor>, ArrayList<Clustering.readKWSN.Link>>(listS,listL);
		return KWSN;
	}
}
	// cach lay gia tri cua pair
	// Pair<List<Sensor>, List<Channel>> topology = pre_process(args, listSensor, ListChannel, numberOfpacket)
	// List<Sensor> list1 = topology.getValue0();
	// List<Channel> list2 = topology.getValue1();
	// Vay la co 2 list roi do

