package Clustering.readKWSN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "Network")
@XmlAccessorType(XmlAccessType.FIELD)
public class Network {
	@XmlAttribute(name = "SensorMaxBufferSize")
	public String SensorMaxBufferSize;
	@XmlAttribute(name = "SensorMaxQueueSize")
	public String SensorMaxQueueSize;
	@XmlAttribute(name = "ChannelMaxBufferSize")
	public String ChannelMaxBufferSize;
	@XmlElement(name = "Process")
	public Process processes;
	@XmlAttribute(name = "NumberOfPackets")
	public String NumberOfPacket;
	
	public String getSensorMaxBufferSize() {
		return SensorMaxBufferSize;
	}
	public void setSensorMaxBufferSize(String sensorMaxBufferSize) {
		SensorMaxBufferSize = sensorMaxBufferSize;
	}
	public String getSensorMaxQueueSize() {
		return SensorMaxQueueSize;
	}
	public void setSensorMaxQueueSize(String sensorMaxQueueSize) {
		SensorMaxQueueSize = sensorMaxQueueSize;
	}
	public String getChannelMaxBufferSize() {
		return ChannelMaxBufferSize;
	}
	public void setChannelMaxBufferSize(String channelMaxBufferSize) {
		ChannelMaxBufferSize = channelMaxBufferSize;
	}
	public Process getProcesses() {
		return processes;
	}
	public void setProcesses(Process processes) {
		this.processes = processes;
	}
	public String getNumberOfPacket() {
		return NumberOfPacket;
	}
	public void setNumberOfPacket(String numberOfPacket) {
		NumberOfPacket = numberOfPacket;
	}
		 
}
