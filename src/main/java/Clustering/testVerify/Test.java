package Clustering.testVerify;

import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;

import Clustering.readKWSN.Sensor;
import PN.*;
import org.javatuples.Pair;
import org.javatuples.*;

import CPN.Channel;
import Clustering.full_verify.Connect_Sensors;
import Clustering.intra_interClu.Intercluster_Verify;
import Clustering.intra_interClu.Intracluster_Verify;
import Clustering.readKWSN.Link;
import PN.Verification;

public class Test {

	public Link initLink(int sensor1, int sensor2 , int sendrate) {
		Link temp_link = new Link();
		temp_link.From = "Sensor " + String.valueOf(sensor1);
		temp_link.id = sensor1 + "_" + String.valueOf(sensor2);
		temp_link.IsConverted = false;
		temp_link.MaxSendingRate = String.valueOf(sendrate);
		temp_link.To = "Sensor " + sensor2;
		return temp_link;
	}
	
	public static Quartet<ArrayList<Clustering.readKWSN.Sensor>,ArrayList<Link>, Integer, Node> main(ArrayList<ArrayList<Pair<ArrayList<Clustering.readKWSN.Sensor>, ArrayList<Link>>>> topologyClu,ArrayList<Clustering.readKWSN.Sensor> sensorT, ArrayList<Link> linkT, ArrayList<CPN.Sensor> sensors, ArrayList<Channel> channels, int maxB) throws Exception {
		//		System.out.print("Done");
		//		readKwsn("WSN//file-kwsn//mod_Cluster8.kwsn");
		//	    Boolean check1 = check_congestion();
		//

		for (Clustering.readKWSN.Link l : linkT) {
			l.From = "Sensor "+ l.From;
			l.To = "Sensor " + l.To;
		}

		ArrayList<Clustering.readKWSN.Sensor> After_sensors = new ArrayList<Clustering.readKWSN.Sensor>();
		ArrayList<Link> After_links = new ArrayList<Link>(); 
		//bien dung de kiem tra xem co verify tiep hay khong	
		int continueClu = 0;
		//verify intracluster
		Intracluster_Verify intra_verify = new Intracluster_Verify();

		//verify intercluster
		Intercluster_Verify inter_verify = new Intercluster_Verify();
		
		//verify
		Verification verify = new Verification();
		Node check = null;
		
		//convert KWSN - CPN
		Clustering.CPN.convKC conv = new Clustering.CPN.convKC();
		
		//take each file
		int dList = topologyClu.get(0).size() ;
		int iList = topologyClu.get(1).size() ;
	
		int i = 0;
		int total = dList + iList;
		int dCount = 0;
		int iCount = 0;
		// Run each file
		while( i < total ) {

			if ( dCount < dList )
			{	 

				// Chuan hoa file truoc khi dua vao ham doc. Neu Cluster's sensor > 7 thi khong can phai verify
				Triplet<ArrayList<Clustering.readKWSN.Sensor>,ArrayList<Link>, Integer> checkIntra = intra_verify.main(topologyClu.get(0).get(dCount), sensorT, linkT);
				// tien xu ly truoc khi verify. chi verify khi topology day du va so sensor < 7
				if ( checkIntra.getValue2() == 2) {
					continueClu++;
					
					//khong verify
				}else if (checkIntra.getValue2() == 3) {
					//convert to CPN and verify
					Pair<ArrayList<CPN.Sensor>, ArrayList<Channel>> cpn  = conv.KWSNtoCPN(checkIntra.getValue0(), checkIntra.getValue1(), sensors, channels, maxB);
					continueClu++;
					Topology topology = new Topology(cpn.getValue0(),cpn.getValue1());
					check = verify.verify(topology);
					if(check != null) {
						break;
					}

				}
				//Return sensor and link
				Pair<List<Clustering.readKWSN.Sensor>, List<Link>> temp_dense =  inter_verify.main(topologyClu.get(0).get(dCount), sensorT, linkT);
				After_sensors.addAll(temp_dense.getValue0());
				After_links.addAll(temp_dense.getValue1());
				i++;
				dCount++;
			}

			if (iCount < iList)
			{
				// Chuan hoa file truoc khi dua vao ham doc
				Triplet<ArrayList<Clustering.readKWSN.Sensor>,ArrayList<Link>, Integer> checkIntra = intra_verify.main(topologyClu.get(1).get(iCount), sensorT, linkT);
				// tien xu ly truoc khi verify. chi verify khi topology day du va so sensor < 7
				if (checkIntra.getValue2() == 2) {
					continueClu++;
					//khong verify
				}else if (checkIntra.getValue2() == 3) {
					// verify
					Pair<ArrayList<CPN.Sensor>, ArrayList<Channel>> cpn  = conv.KWSNtoCPN(checkIntra.getValue0(), checkIntra.getValue1(), sensors, channels, maxB);
					continueClu++;
					Topology topology = new Topology(cpn.getValue0(),cpn.getValue1());
					check = verify.verify(topology);
					if(check != null) {
						break;
					}
				}
				//Return sensor and link
				Pair<List<Clustering.readKWSN.Sensor>, List<Link>> temp_imba = inter_verify.main(topologyClu.get(1).get(iCount), sensorT, linkT);
				After_sensors.addAll(temp_imba.getValue0());
				After_links.addAll(temp_imba.getValue1());
				i++;
				iCount++;
			}
		}
		if (check == null) {
			System.out.println("Congestion in cluster is not founded");
			// Get information about sensor abandoned
			// kiem tra neu khong ton tai sensor nao bi le
			ArrayList<Clustering.readKWSN.Sensor> abandoneS = topologyClu.get(2).get(0).getValue0();
			After_sensors.addAll(abandoneS);
			ArrayList<Link> abandoneL = topologyClu.get(2).get(0).getValue1();

			//Dua vao sensors tim cac link co lien quan de add vao va tra ve full_link
			Connect_Sensors connect = new Connect_Sensors();
			ArrayList<Link> full_link = connect.connect(After_sensors, abandoneL ,After_links);

			//Model have complete sensors and links
			//return Pair<ArrayList<Sensor>,ArrayList<Link>>
			Quartet<ArrayList<Sensor>,ArrayList<Link>, Integer, Node> AfterTopo = new Quartet<ArrayList<Clustering.readKWSN.Sensor>,ArrayList<Link>, Integer, Node>(After_sensors, full_link, continueClu, null);
			return AfterTopo;
		}else{

			Quartet<ArrayList<Sensor>,ArrayList<Link>, Integer, Node> AfterTopo = new Quartet<ArrayList<Clustering.readKWSN.Sensor>,ArrayList<Link>, Integer, Node>(null, null, null, check);
			return AfterTopo;
		}
	}
}
