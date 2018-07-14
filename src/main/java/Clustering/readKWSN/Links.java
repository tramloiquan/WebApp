package Clustering.readKWSN;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Links")
@XmlAccessorType(XmlAccessType.FIELD)
public class Links {
	@XmlElement(name = "Link")
	public List<Link> listLink = new ArrayList<>();

	public List<Link> getListLink() {
		return listLink;
	}

	public void setListLink(List<Link> listLink) {
		this.listLink = listLink;
	}
}
