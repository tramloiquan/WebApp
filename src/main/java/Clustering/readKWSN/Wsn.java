package Clustering.readKWSN;
import javax.xml.bind.annotation.*;


@XmlRootElement(name = "WSN")
@XmlAccessorType(XmlAccessType.FIELD)
public class Wsn {
	@XmlElement(name = "Network")
	public Network network;

	public Network getNetwork() {
		return network;
	}

	public void setNetwork(Network network) {
		this.network = network;
	}
	
}
