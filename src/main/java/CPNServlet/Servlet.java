package CPNServlet;


import CPN.*;
import CPN.Graph;
import CPN.Marking;
import PN.*;
import com.google.gson.Gson;
import org.javatuples.Triplet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "Servlet", urlPatterns = "cal")
public class Servlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String re = request.getReader().readLine();
        try {
            JSONObject res = new JSONObject();

            JSONObject json = (JSONObject) new JSONParser().parse(re);
            String mode = json.get("mode").toString();
            System.out.println(mode);
            JSONArray sjson = (JSONArray) json.get("sensors");
            JSONArray cjson = (JSONArray) json.get("channels");

            JSONObject gjson = (JSONObject) json.get("general");
            General.mmax = Integer.parseInt(gjson.get("mmax").toString());
            General.snr = Float.parseFloat(gjson.get("snr").toString());
            General.lambda_s0 = Float.parseFloat(gjson.get("lambda_s0").toString());
            General.lambda_c0 = Float.parseFloat(gjson.get("lambda_c0").toString());

            ArrayList<Sensor> ls = new ArrayList<Sensor>();
            for (int i = 0; i < sjson.size(); i++) {
                Sensor s = new Gson().fromJson(sjson.get(i).toString(), Sensor.class);
                if(s.getType() == 0) s.setB(Integer.parseInt(gjson.get("pmax").toString()));
                ls.add(s);
            }

            ArrayList<Channel> lc = new ArrayList<Channel>();
            for (int i = 0; i < cjson.size(); i++) {
                Channel c = new Gson().fromJson(cjson.get(i).toString(), Channel.class);
                lc.add(c);
            }
            if (mode.equalsIgnoreCase("cpn")) {
                Marking init = new Marking(lc, ls, 1.0, Integer.parseInt(gjson.get("pmax").toString()));
                init.infoMappingfun();

                Graph g = new Graph(init);
                try {
                    System.out.println("Caculating.....");
//                    XMLExporter export = new XMLExporter();
//                    export.marshall(ls, lc, new General());
                    g.generateGraph();
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                } finally {
                    res.put("code", "0");
                    res.put("marking", g.getTotal());
                    res.put("pc", g.getpc());
                    res.put("pnc", g.getpnc());
                    response.setContentType("text/html");
                    response.getWriter().println(res.toJSONString());
                    System.out.println(g.getpc());
                    System.out.println(g.getpnc());
                    System.out.println(g.getTotal());
                }
            }
            else if(mode.equalsIgnoreCase("pn")){
                Topology topo = new Topology(ls, lc);
                String n = Verification.verifyHeuristic(topo);
                Petrinet petrinet = Converter.convert(topo);
//                FileWriter f = new FileWriter(new File("src\\main\\resoures\\webapp\\pnml.xml"));
//                f.write(petrinet.export());
//                f.flush();
//                f.close();

                res.put("code", "1");
                res.put("node", n);
                res.put("file", petrinet.export());
                response.setContentType("text/html");
                response.getWriter().println(res.toJSONString());
            }
            else if(mode.equalsIgnoreCase("clustering")){
                try {
                    Triplet topology = Clustering.full_verify.Main.cluster(ls, lc, 10,Double.parseDouble(gjson.get("sr").toString()),Double.parseDouble(gjson.get("tr").toString()),Integer.parseInt(gjson.get("noc").toString()));
                    if (topology.getValue2() != null) {
                        res.put("code", "2");
                        res.put("node", topology.getValue2().toString());
                        response.setContentType("text/html");
                        response.getWriter().println(res.toJSONString());
                    }else {
                        String channeljson = new Gson().toJson(topology.getValue1());
                        String sensorjson = new Gson().toJson(topology.getValue0());
                        res.put("code", "3");
                        res.put("links", channeljson);
                        res.put("nodes", sensorjson);
                        response.setContentType("text/html");
                        response.getWriter().println(res.toJSONString());
                    }
                }catch (Exception e) {
                    System.out.println("Fail");
                }
            }
            else{
                res.put("code", "2000");
                response.setContentType("text/html");
                response.getWriter().println(res.toJSONString());
            }
        } catch (ParseException e) {
            System.out.println("lskjdfl");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("get");
        response.setContentType("text/html");
        response.getWriter().println("HELLO");
    }
}
