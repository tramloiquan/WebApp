package Clustering.readKWSN;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Link")
@XmlAccessorType(XmlAccessType.FIELD)
public class Link {
	@XmlAttribute(name = "id")
	public String id;
	@XmlAttribute(name = "MaxSendingRate")
	public String MaxSendingRate;
	@XmlElement(name = "From")
	public String From;
	@XmlElement(name = "To")
	public String To;
	public boolean IsConverted;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMaxSendingRate() {
		return MaxSendingRate;
	}
	public void setMaxSendingRate(String maxSendingRate) {
		MaxSendingRate = maxSendingRate;
	}
	public String getFrom() {
		return From;
	}
	public void setFrom(String from) {
		From = from;
	}
	public String getTo() {
		return To;
	}
	public void setTo(String to) {
		To = to;
	}
	public boolean isIsConverted() {
		return IsConverted;
	}
	public void setIsConverted(boolean isConverted) {
		IsConverted = isConverted;
	}
}
