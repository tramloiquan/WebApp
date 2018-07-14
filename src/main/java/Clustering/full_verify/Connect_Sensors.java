package Clustering.full_verify;
import Clustering.readKWSN.*;
import java.util.*;

public class Connect_Sensors {

	public ArrayList<Link> connect( List<Sensor> sensors, List<Link> linkt, ArrayList<Link> links) {
		//trong tap sensor sau khi inter, lay ra het id sensor cho vao 1 list A
		//Da co list tat ca cac link, chi giu lai nhung link nao co from chua id trong A va to cung chua ID trong A
		// khoi tao list int sensors id
		List<String> sensors_ID = new ArrayList<>();
		List<Link> link_re = new ArrayList<>();
		for (Sensor x : sensors) {
			sensors_ID.add("Sensor "+x.Id);
		}
		int i = 1; // bien check link trung
		// giu lai nhung link nao co from va to chuaw ID trong sensors_ID
		for (Link y : linkt) {
			for (Link y1 : links) {
				if ( (y.From.equals(y1.From) && y.To.equals(y1.To)) ) {
					i = 0;
					break;
				}
			}
			if ((sensors_ID.contains(y.From) && sensors_ID.contains(y.To)) && i == 1) {
				link_re.add(y);	
			}
			i = 1;
		}
		links.addAll(link_re);
		return links;
	} 
	
}
