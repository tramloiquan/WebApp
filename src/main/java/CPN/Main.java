package CPN;

import CPNServlet.Servlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main{
//    static int numsensors = 40;
//	public static void main(String[] args){
//		System.out.println(Runtime.getRuntime().maxMemory());
//		int sbmax = 7;
//		int sqmax = 7;
//		int cbmax = 5;
//		double mu_p = 6.5;
//		double sigma_p = 0.25;
//		double mu_s = 6.5;
//		double sigma_s = 0.25;
//		double mu_t = 6.5;
//		double sigma_t = 0.25;
//
//		ArrayList<Sensor> ls = new ArrayList<Sensor>();
////		for(int i = 1; i<= numsensors; i++){
////			Sensor s = new Sensor(i, 1,0, 0, sbmax, sqmax, 10.0, mu_p, sigma_p, mu_s, sigma_s);
////			ls.add(s);
////		}
//		ls.get(numsensors-1).setSqmax(10000);
//		ls.get(0).setB(10);
//
//		ArrayList<Channel> lc = new ArrayList<Channel>();
//		for(int i = 1; i<= numsensors; i++){
//			Channel c = new Channel(0 , 0, i, 0, cbmax, mu_t, sigma_t);
//			lc.add(c);
//		}
//		lc.get(0).setCbmax(6);
//		lc.get(1).setCbmax(6);
//		Marking init = new Marking(lc, ls, 1.0, 10);
//
//		Graph g = new Graph(init);
//		try{
//			g.generateGraph();
//			System.out.println(g.getpc());
//			System.out.println(g.getpnc());
//			System.out.println(g.getTotal());
//		}catch (OutOfMemoryError e){
//			e.printStackTrace();
//			System.out.println(g.getpc());
//			System.out.println(g.getpnc());
//			System.out.println(g.getTotal());
//		}
//	}
	public static void main(String[] args) throws Exception{

		Server server = new Server(8080);

		ServletContextHandler myContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		myContext.setContextPath("/");
		myContext.addServlet(new ServletHolder(new Servlet()), "/*");

		ResourceHandler rh = new ResourceHandler();
		rh.setResourceBase(Main.class.getClassLoader().getResource("webapp").toString());

		HandlerList hl = new HandlerList();
		hl.setHandlers(new Handler[]{rh, myContext});

		server.setHandler(hl);

		server.start();
		server.join();
	}
}