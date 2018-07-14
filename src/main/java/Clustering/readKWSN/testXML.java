package Clustering.readKWSN;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class testXML {
	// path là file đã chỉnh sửa có _mod nếu ghi file trong lúc veryfy
	
	public void writeToKwsn(List<Sensor> sensors, List<Link> links, Network network, String path) throws JAXBException {
		try{

		    //getting the xml file to read

//		    File file = new File("D://Test_XML.kwsn");

		    JAXBContext jContext = JAXBContext.newInstance(Wsn.class);

//		    Unmarshaller unmarshallerObj = jContext.createUnmarshaller();
//		    Wsn topology =( Wsn) unmarshallerObj.unmarshal(file);
//		    List<Sensor> sensors = topology.getNetwork().getProcesses().getSensors().getListSensor();
//	        List<Link> links = topology.getNetwork().getProcesses().getLinks().getListLink();
	        	        
		    Marshaller marshallObj = jContext.createMarshaller();

		    marshallObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    
		    Sensors Ss = new Sensors();
		    Ss.setListSensor(sensors);
		    
		    Links Ls = new Links();
		    Ls.setListLink(links);
		    
		    Process process = new Process();
		    
		    process.setLinks(Ls);
		    process.setSensors(Ss);
		    
		    network.setProcesses(process);
//		    
//		    // Set các thông số của process 
//		    network.setChannelMaxBufferSize(p));
//		    network.setNumberOfPacket("2");
//		    network.setSensorMaxBufferSize("10");
//		    network.setSensorMaxQueueSize("10");
		    
		    Wsn topology1 = new Wsn();
		    topology1.setNetwork(network);
		    OutputStream os = new FileOutputStream( path );
		    // Sửa lại tên
		    marshallObj.marshal(topology1, os);
		    os.close();
			}
		catch(Exception e) {
		    e.printStackTrace();
		}
	}
	// path la file chưa chỉnh sửa
	public Wsn readKwsn(String path) throws JAXBException {
		Wsn topology = new Wsn();
		try{
			  //getting the xml file to read

		    File file = new File(path);

		    JAXBContext jContext = JAXBContext.newInstance(Wsn.class);

		    Unmarshaller unmarshallerObj = jContext.createUnmarshaller();
		    topology =( Wsn) unmarshallerObj.unmarshal(file);
		    
//		    List<Sensor> sensors = topology.getNetwork().getProcesses().getSensors().getListSensor();
//	        List<Link> links = topology.getNetwork().getProcesses().getLinks().getListLink();
		}
		catch(Exception e) {
		    e.printStackTrace();
		}
		return topology;
	}
}