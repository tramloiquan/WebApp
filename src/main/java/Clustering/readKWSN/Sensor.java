package Clustering.readKWSN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Sensor")
@XmlAccessorType(XmlAccessType.FIELD)
public class Sensor {

	public String MinSendingRate;
	public String MinProcessingRate;
	@XmlElement(name = "Position")
	public Position position;
	@XmlAttribute(name = "MaxSendingRate")
	public String MaxSendingRate;
	@XmlAttribute(name = "MaxProcessingRate")
	public String MaxProcessingRate;
	@XmlAttribute(name = "id")
	public String Id;
	@XmlAttribute(name = "Name")
	public String Name;
	@XmlAttribute(name = "Init")
	public String Init;
	@XmlAttribute(name = "SType")
	public int Type;
	@XmlAttribute(name = "energy")
	public float energy = 100;
	public int flag = 0;
	public String getMinSendingRate() {
		return MinSendingRate;
	}
	public void setMinSendingRate(String minSendingRate) {
		MinSendingRate = minSendingRate;
	}
	public String getMinProcessingRate() {
		return MinProcessingRate;
	}
	public void setMinProcessingRate(String minProcessingRate) {
		MinProcessingRate = minProcessingRate;
	}
	public String getMaxSendingRate() {
		return MaxSendingRate;
	}
	public void setMaxSendingRate(String maxSendingRate) {
		MaxSendingRate = maxSendingRate;
	}
	public String getMaxProcessingRate() {
		return MaxProcessingRate;
	}
	public void setMaxProcessingRate(String maxProcessingRate) {
		MaxProcessingRate = maxProcessingRate;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getInit() {
		return Init;
	}
	public void setInit(String init) {
		Init = init;
	}
	public int getType() {
		return Type;
	}
	public void setType(int type) {
		Type = type;
	}	
		
}
