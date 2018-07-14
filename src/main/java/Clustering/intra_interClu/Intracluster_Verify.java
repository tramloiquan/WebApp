package Clustering.intra_interClu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.xml.sax.SAXException;

import Clustering.readKWSN.Link;
import Clustering.readKWSN.Sensor;


public class Intracluster_Verify  {

	public Link initLink(String sensor1, String sensor2) {
		Link temp_link = new Link();
		temp_link.From = "Sensor " + sensor1;
		temp_link.id = sensor1 + "_" + sensor2;
		temp_link.IsConverted = false;
		temp_link.MaxSendingRate = "10"; // max cua topology
		temp_link.To = "Sensor " + sensor2;
		return temp_link;
	}
	public Link initL (Link l) {
		Link temp = new Link();
		temp.From = l.From;
		temp.To = l.To;
		temp.id = l.id;
		temp.MaxSendingRate = l.MaxSendingRate;
		temp.IsConverted = l.IsConverted;
		return temp;
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

	public ArrayList<Pair<ArrayList<String>, ArrayList<String>>> get_Tlinks_info(ArrayList<Sensor> sensorT, ArrayList<Link> linkT) throws JAXBException {


		ArrayList<Sensor> sensors = sensorT;
		ArrayList<Link> links = linkT;

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
			int index1 = Integer.parseInt(parts[0]) ;
			int index2 = Integer.parseInt(parts[1]) ;
			topology.get(index1).getValue0().add(parts[1]);
			topology.get(index2).getValue1().add(parts[0]);	
		}
		return topology;
	}

	public ArrayList<Sensor> get_Tsensors_info( ArrayList<Sensor> sensorT) throws JAXBException {
		ArrayList<Sensor> sensors = sensorT; 
		ArrayList<Sensor> abc = new ArrayList<Sensor>();
		for ( Sensor x : sensors) {
			abc.add(x);
		}
		return abc;
	}

	public Triplet<ArrayList<Sensor>,ArrayList<Link>, Integer> process_T(ArrayList<Sensor> sensorCLu, ArrayList<Link> linkClu,  ArrayList<Pair<ArrayList<String>, ArrayList<String>>> topology, ArrayList<Sensor> tSensor) throws  JAXBException, ParserConfigurationException, SAXException, IOException, TransformerException {
		// Tra ve 1 => sensor's cluster > 7 , ko process
		// Tra ve 2 => missing source or sink, process nhung khong verify
		// Tra ve 3 => verify
		
		ArrayList<Sensor> sensors = new ArrayList<Sensor>();
		ArrayList<Link> links = new ArrayList<Link>();

		for(Sensor x : sensorCLu) {
			sensors.add(x);
		}
		for(Link y : linkClu) {
			links.add(y);
		}


		// One cluster just have under 7 sensors
		if (sensors.size() > 7) {
			
			System.out.println("Move to another cluster");
			Triplet<ArrayList<Sensor>,ArrayList<Link>, Integer> triplet = new Triplet<ArrayList<Sensor>,ArrayList<Link>, Integer>(null,null,1);
			return triplet;
		} // If Cluster's sensor <= 7
		else {
			// ~min(buffer, queue, processing rate) - max(sending rate) => When optimize

			ArrayList<String> notation_sensor =  new ArrayList<String>();
			// Check if topology already have sink or source
			Boolean source = false;
			Boolean sink = false;
			Boolean flag_source = false;
			Boolean flag_sink = false;
			// haven't optimizied yet
			for (Sensor x : sensors) {
				if (x.Type == 0) {
					source = true;
					flag_source = true;
				}
				if (x.Type == 2) {
					sink = true;
					flag_sink = true;
				}
				notation_sensor.add(x.Id);
			}

			////////////////// Get temp source

			if(source == false ) {
				for (Sensor x : sensors) {
					int index = Integer.parseInt(x.Id);
					Sensor Rsource = new Sensor();
					ArrayList<String> so = topology.get(index).getValue1();
					so.removeAll(notation_sensor);
					if (so.size() != 0) {
						int index1 = Integer.parseInt(so.get(0));
						for (Sensor y : tSensor) {
							if (Integer.parseInt(y.Id) == index1) {
								Rsource = initSensor(y); 			
								Rsource.setType(0);
								flag_source = true;
								break;
							}		
						}
						sensors.add(Rsource);
						Link temp_link = initLink(Rsource.getId(), x.Id);
						//Link temp_link = initLink(so.get(0),x.Id);
						links.add(temp_link);
						break;
					}
				}
			}
			/////////////////// Get temp sink

			if(sink == false) {
				for (Sensor x : sensors) {
					int index = Integer.parseInt(x.Id);
					Sensor Rsink = new Sensor();
					ArrayList<String> so = topology.get(index).getValue0();
					so.removeAll(notation_sensor);
					if (so.size() != 0) {
						//
						int index1 = Integer.parseInt(so.get(0));
						for (Sensor y : tSensor) {
							if (Integer.parseInt(y.Id) == index1  ) {
								Rsink = initSensor(y);
								Rsink.setId("222");
								Rsink.setName("Sensor 222");
								Rsink.setType(2);
								flag_sink = true;
								break;
							}		
						}	
						sensors.add(Rsink);
						Link temp_link = initLink(x.Id,Rsink.getId());
						//Link temp_link = initLink(x.Id,so.get(0));
						links.add(temp_link);
						break;
					}
				}
			} 
			if(flag_source == false | flag_sink == false ) {
				
				System.out.println("Missing source or sink");
				Triplet<ArrayList<Sensor>,ArrayList<Link>, Integer> triplet = new Triplet<ArrayList<Sensor>,ArrayList<Link>, Integer>(null,null,2);
				return triplet;
				
			}
			else {
				Triplet<ArrayList<Sensor>,ArrayList<Link>, Integer> triplet = new Triplet<ArrayList<Sensor>,ArrayList<Link>, Integer>(sensors,links,3);
				return triplet;
			}
		}       
	}	 
	//Triplet<ArrayList<readKWSN.Sensor>,ArrayList<Link>, Integer> tra ve theo format nay
	public Triplet<ArrayList<Sensor>,ArrayList<Link>, Integer> main(Pair<ArrayList<Sensor>,ArrayList<Link>> topologyClu, ArrayList<Sensor> sensorT, ArrayList<Link> linkT) throws Exception {
		////////////////// Verify Intra-Cluster with just 1 file. Need to be done with list of file
		Intracluster_Verify abc = new Intracluster_Verify();

		// get all links between every sensor // maybe all sensor.
		ArrayList<Pair<ArrayList<String>, ArrayList<String>>> Topology = abc.get_Tlinks_info(sensorT, linkT);

		// get all sensors
		ArrayList<Sensor> tSensor = abc.get_Tsensors_info(sensorT);

		// process info to verify
		Triplet<ArrayList<Sensor>,ArrayList<Link>, Integer> check = abc.process_T(topologyClu.getValue0(), topologyClu.getValue1(), Topology, tSensor);
		

		return check;

	}
}