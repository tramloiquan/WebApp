package Clustering.readKWSN;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "Process")
@XmlAccessorType(XmlAccessType.FIELD)
public class Process {
	@XmlElement(name = "Sensors")
	public Sensors sensors;
	@XmlElement(name = "Links")
	public Links links;
	
	public Sensors getSensors() {
		return sensors;
	}
	public void setSensors(Sensors sensors) {
		this.sensors = sensors;
	}
	public Links getLinks() {
		return links;
	}
	public void setLinks(Links links) {
		this.links = links;
	}
}
