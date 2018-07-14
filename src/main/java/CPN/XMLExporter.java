package CPN;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by traml on 6/27/2018.
 */
public class XMLExporter {
    public void marshall(ArrayList<Sensor> ls, ArrayList<Channel> lc, General g){
        try {
            Marking net = new Marking(lc, ls, 0, 0);
            net.setG(g);
            JAXBContext xmlnet = JAXBContext.newInstance(Marking.class);
            Marshaller ms = xmlnet.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            System.out.println(XMLExporter.class.getClassLoader().getResource("webapp/cpnml.xml").getFile());
            OutputStream f = new FileOutputStream("src\\main\\resoures\\webapp\\cpnml.xml");
//            OutputStream f = new FileOutputStream("C:\\Users\\traml\\IdeaProjects\\WebApp\\src\\main\\resources\\webapp\\cpnml.xml");
            ms.marshal(net, f);
            f.close();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
