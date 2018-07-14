package Clustering.readKWSN;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Sensors")
@XmlAccessorType(XmlAccessType.FIELD)
public class Sensors {
	@XmlElement(name = "Sensor")
	public List<Sensor> listSensor = new ArrayList<>();

	public List<Sensor> getListSensor() {
		return listSensor;
	}

	public void setListSensor(List<Sensor> listSensor) {
		this.listSensor = listSensor;
	}
}
