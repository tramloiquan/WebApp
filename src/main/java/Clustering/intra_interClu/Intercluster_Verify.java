package Clustering.intra_interClu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.javatuples.Pair;

import Clustering.Dijkstra_interClus.TestDijkstraAlgorithm;
import Clustering.readKWSN.Link;
import Clustering.readKWSN.Sensor;

public class Intercluster_Verify  {

	public Link initLink(int sensor1, int sensor2 , int sendrate) {
		Link temp_link = new Link();
		temp_link.From = "Sensor " + String.valueOf(sensor1);
		temp_link.id = sensor1 + "_" + String.valueOf(sensor2);
		temp_link.IsConverted = false;
		temp_link.MaxSendingRate = String.valueOf(sendrate);
		temp_link.To = "Sensor " + sensor2;
		return temp_link;
	}
	public Sensor initSensor(Sensor x) {
		Sensor temp = new Sensor();
		temp.energy = x.energy;
		temp.Id = x.Id;
		temp.Init = x.Init;
		temp.MaxProcessingRate = x.MaxProcessingRate;
		temp.MaxSendingRate = x.MaxSendingRate;
		temp.Name = x.Name;
		temp.setType(x.Type);
		temp.position = x.position;
		return temp;
	}

	public ArrayList<Pair<ArrayList<String>, ArrayList<String>>> get_Tlinks_info(ArrayList<Sensor> sensorT, ArrayList<Link> linkT) throws JAXBException  {
		ArrayList<Sensor> sensors = new ArrayList<Sensor>();
		ArrayList<Link> links = new ArrayList<Link>();
		for (Sensor x : sensorT) {
			sensors.add(x);
		}
		for (Link y : linkT) {
			links.add(y);
		}

		ArrayList<Pair<ArrayList<String>, ArrayList<String>>> topology = new ArrayList<Pair<ArrayList<String>, ArrayList<String>>>();

		int max = 0;
		for( Sensor x : sensors) {
			if( Integer.parseInt(x.Id) > max ) {
				max = Integer.parseInt(x.Id);
			}
		}

		for(int i = 0; i <= max; i++)
		{
			ArrayList<String> l1 =new ArrayList<String>();
			ArrayList<String> l2 =new ArrayList<String>();
			Pair<ArrayList<String>, ArrayList<String>> pair = new Pair<ArrayList<String>, ArrayList<String>>(l1, l2);
			topology.add(pair);
		}
		Iterator<Link> iterator = links.iterator();
		while (iterator.hasNext()){
			String[] parts = iterator.next().id.split("_");
			int index1 = Integer.parseInt(parts[0]);
			int index2 = Integer.parseInt(parts[1]);
			topology.get(index1).getValue0().add(parts[1]);
			topology.get(index2).getValue1().add(parts[0]);
		}
		return topology;
	}

	public ArrayList<Sensor> get_Tsensors_info(ArrayList<Sensor> sensorT) throws JAXBException {
		ArrayList<Sensor> sensors = sensorT;

		ArrayList<Sensor> abc = new ArrayList<Sensor>();
		for ( Sensor x : sensors) {
			abc.add(x);
		}
		return abc;
	}

	public Pair<List<Sensor> , List<Link>> process_T(ArrayList<Sensor> sensorCLu, ArrayList<Link> linkClu, ArrayList<Pair<ArrayList<String>, ArrayList<String>>> topology, ArrayList<Sensor> tSensor) throws JAXBException {
		for (Clustering.readKWSN.Link l : linkClu) {
			l.From = "Sensor "+ l.From;
			l.To = "Sensor " + l.To;
		}

		ArrayList<Sensor> sensors = new ArrayList<Sensor>();
		ArrayList<Link> links = new ArrayList<Link>();

		for(Sensor x : sensorCLu) {
			sensors.add(x);
		}
		for(Link y : linkClu) {
			links.add(y);
		}

		// -> form of Dij
		ArrayList<Integer> dij_ver = new ArrayList<Integer>();
		ArrayList<Integer> dij_edge = new ArrayList<Integer>();
		for (Sensor s : sensors) {
			dij_ver.add(Integer.parseInt(s.Id));
		}
		for (Link l : links ) {
			String[] parts = l.id.split("_");
			String part1 = parts[0];
			String part2 = parts[1];
			dij_edge.add(Integer.parseInt(part1));
			dij_edge.add(Integer.parseInt(part2));
			float delay_time = 0;
			dij_edge.add(Integer.parseInt(l.MaxSendingRate));
			for (Sensor s : sensors) {
				if (s.Id.equals(part1)) {
					dij_edge.add(Integer.parseInt(s.MaxProcessingRate));
					dij_edge.add(Integer.parseInt(s.MaxSendingRate));
					break;
				}
			}
		}

		//
		ArrayList<String> notation_sensor =  new ArrayList<String>();
		for (Sensor x : sensors) {
			notation_sensor.add(x.Id);
		}
		ArrayList<Sensor> rSensor =  new ArrayList<Sensor>();
		ArrayList<Link> rLink = new ArrayList<Link>();
		for (Sensor x : sensors) {
			//
			int index = Integer.parseInt(x.Id) ;
			Sensor Rsource = new Sensor();
			ArrayList<String> temp1 = new ArrayList<String>();
			temp1.addAll(topology.get(index).getValue0());
			temp1.addAll(topology.get(index).getValue1());
			temp1.removeAll(notation_sensor);
			// Check source sink if have
			if ((temp1.size() == 0) && (x.Type != 0) && (x.Type != 2)) {
				//mark sensor as to be delete
				rSensor.add(x);
				// Loáº¡i link
				String from = "Sensor " + x.Id;
				String to = "Sensor " + x.Id;
				for (Link y : links) {
					if (y.From.equals(from)) {
						//mark link as to be delete
						rLink.add(y);
					}
					else if (y.To.equals(to)) {
						//mark link as to be delete
						rLink.add(y);

					}
				}
			}
		}
		// remove sensor not neccesary
		sensors.removeAll(rSensor);
		links.removeAll(rLink);

		//Run Dijkstra to build model
		TestDijkstraAlgorithm a = new TestDijkstraAlgorithm();

		//
		for ( int i = 0; i < sensors.size(); i++) {
			for (int j = i+1; j<sensors.size(); j++) {
				Boolean check = false;
				// 
				for (Link ltemp : links) {
					String[] parts = ltemp.id.split("_");
					String part1 = parts[0];
					String part2 = parts[1];
					if((Integer.parseInt(part1)== Integer.parseInt(sensors.get(i).Id)) &
							(Integer.parseInt(part2) == Integer.parseInt(sensors.get(j).Id)))
					{
						check = true;
					}
				}
				//If not, run Dij
				if (check == false)
				{
					int wei = a.testExcute(dij_ver,dij_edge,Integer.parseInt(sensors.get(i).Id),Integer.parseInt(sensors.get(j).Id));
					//
					if (wei > 0) {
						Link lNew = new Link();
						lNew = initLink(Integer.parseInt(sensors.get(i).Id),Integer.parseInt(sensors.get(j).Id),wei);
						links.add(lNew);
					}
				}
			}
		}
		Pair<List<Sensor> , List<Link>> After_cluster = new Pair<List<Sensor> , List<Link>>(sensors, links);
		return After_cluster;
	}

	public  Pair<List<Sensor>, List<Link>> main( Pair<ArrayList<Sensor>,ArrayList<Link>> topologyClu, ArrayList<Sensor> sensorT, ArrayList<Link> linkT) throws Exception {

		///////////// Verify Intra-Cluster
		Intercluster_Verify xyz = new Intercluster_Verify();

		// get all links between every sensor .
		ArrayList<Pair<ArrayList<String>, ArrayList<String>>> Topology = xyz.get_Tlinks_info(sensorT, linkT);

		// get all sensors
		ArrayList<Sensor> tSensor = xyz.get_Tsensors_info(sensorT);

		//

		Pair<List<Sensor>, List<Link>> After_cluster = xyz.process_T(topologyClu.getValue0(), topologyClu.getValue1(), Topology, tSensor);

		//
		return After_cluster;
	}

}