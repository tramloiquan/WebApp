package Clustering.readKWSN;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Position")
@XmlAccessorType(XmlAccessType.FIELD)
public class Position {
	@XmlAttribute(name = "X")
	public String x;
	@XmlAttribute(name = "Y")
	public String y;
	
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	
	
}


