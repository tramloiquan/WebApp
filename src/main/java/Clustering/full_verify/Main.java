package Clustering.full_verify;
import java.io.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import Clustering.readKWSN.Sensor;
import PN.Node;
import PN.Topology;
import PN.Verification;
import org.javatuples.*;
import org.xml.sax.SAXException;

import Clustering.readKWSN.*;
import Clustering.testVerify.*;

import java.util.*;
import CPN.*;
public class Main {
	
	public static Triplet<ArrayList<CPN.Sensor>, ArrayList<Channel>, Node> cluster (ArrayList<CPN.Sensor> sensors, ArrayList<Channel> channels, int maxB, double s, double t, int k) throws  Exception {
		// Pair<ArrayList<CPN.Sensor>,ArrayList<Channel>>
		//COCA algorithm
		Pair<ArrayList<CPN.Sensor>, ArrayList<Channel>> cpn = new Pair<ArrayList<CPN.Sensor>, ArrayList<Channel>>(null,null);
		final long startTime = System.nanoTime();
		Clustering.readKWSN.Cluster clu = new Clustering.readKWSN.Cluster();
		//CPN -> KWSN for converter
		Clustering.CPN.convKC conv = new Clustering.CPN.convKC();
		Pair<ArrayList<Clustering.readKWSN.Sensor>, ArrayList<Clustering.readKWSN.Link>> kwsn  = conv.CPNtoKWSN(sensors,channels);
		ArrayList<ArrayList<Pair<ArrayList<Clustering.readKWSN.Sensor>, ArrayList<Link>>>> topologyClu = clu.cluster(kwsn.getValue0(), kwsn.getValue1(), s, t, k);
		
		// check so luong file dau tien
		int dList = topologyClu.get(0).size() ;
		int iList = topologyClu.get(1).size() ;
		//Full verify
		Test test_clus = new Test();
		
		Quartet<ArrayList<Sensor>,ArrayList<Link>, Integer, Node> checkT =  test_clus.main(topologyClu, kwsn.getValue0(), kwsn.getValue1(),sensors, channels, maxB);
		// chua dung lai cho den khi tat ca cluster's sensor deu <= 10. Chi tinh dense, chua xet den imbalance
		// kiem tra dieu kien ben tren. Neu after bang 2*begin thi ok. vi chi verify cac cum <10
		Main cf = new Main();
		if (checkT.getValue3() == null) {
			int continueClu = checkT.getValue2();
			boolean check_sensor = false;
			if(continueClu ==  (dList+iList)) {
				check_sensor = true;
			}
			while (check_sensor  == false && checkT.getValue3() == null) {
				System.out.println("------------------------");
				// chay giai thuat gom cum
				s = s/2;
				topologyClu = clu.cluster(checkT.getValue0(), checkT.getValue1(), s, t, k);
				dList = topologyClu.get(0).size() ;
				iList = topologyClu.get(1).size() ;

				//chay giai thuat verify
				checkT =  test_clus.main(topologyClu, kwsn.getValue0(), kwsn.getValue1(),sensors, channels, maxB);
				if(continueClu ==  (dList+iList)) {
					check_sensor = true;
				}
			}
			if (checkT.getValue3() == null) {
				// tat ca cac cum dem < 10 sensors
				//check neu so sensor > 10 bao bung no trang thai. con khong thi moi verify
				if (checkT.getValue0().size() > 10) {
					System.out.println("Cannot verify because of the explosion of state");
				} else {
					//Veryfy the last model - After-Cluster
					cpn = conv.KWSNtoCPN(checkT.getValue0(), checkT.getValue1(), sensors, channels, maxB);
					//verify
					Verification verify = new Verification();
					Topology topology = new Topology(cpn.getValue0(), cpn.getValue1());
//					Node check = verify.verify(topology);
//					if(check == false) {
//                        System.out.println("Congestion is not founded");
//                    }

				}
			}
			cpn = conv.KWSNtoCPN(checkT.getValue0(), checkT.getValue1(), sensors, channels, maxB);
			Triplet<ArrayList<CPN.Sensor>, ArrayList<Channel>, Node> topology = new Triplet<ArrayList<CPN.Sensor>, ArrayList<Channel>, Node>(cpn.getValue0(),cpn.getValue1(),null);
			return topology;
		} else {
			Triplet<ArrayList<CPN.Sensor>, ArrayList<Channel>, Node> topology = new Triplet<ArrayList<CPN.Sensor>, ArrayList<Channel>, Node>(null,null,checkT.getValue3());
			return topology;
		}
	}
}