package Clustering.Dijkstra_interClus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Clustering.Dijkstra_interClus.Edge;
import Clustering.Dijkstra_interClus.Vertex;
import Clustering.Dijkstra_interClus.Graph;
import Clustering.Dijkstra_interClus.DijkstraAlgorithm;

public class TestDijkstraAlgorithm {

	private List<Vertex> nodes;
    private List<Edge> edges;
    // x lÃ  vá»‹ trÃ­ Ä‘áº§u, y lÃ  vá»‹ trÃ­ cuá»‘i
    
    public int testExcute(ArrayList<Integer> Ver, ArrayList<Integer> Edge, int x, int y) {
        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        
        for (int i = 0; i < Ver.size(); i++) {
            Vertex location = new Vertex("Node_" + Ver.get(i), "Node_" + Ver.get(i));
            nodes.add(location);
        }
        int counte = 0;
        for (int i = 0; i <= Edge.size() - 5; i = i + 5) {
        	Float weigh = 1/(float)(Edge.get(i+2)) + 1/(float)(Edge.get(i+3)) + 1/(float)(Edge.get(i+4));
        	addLane("Edge_" + Integer.toString(counte) ,Edge.get(i), Edge.get(i+1), weigh);
        	counte++; 
        }

        // Lets check from location Loc_1 to Loc_10
        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        // tÃ¬m index tÆ°Æ¡ng á»©ng vá»›i node
        int position = 0;
        int input = 0; 
        int output = 0;
        for (Vertex v : nodes) {
    		if (v.getId().equals("Node_"+ String.valueOf(x))) {
    			 input = position; 
    		}
    		if (v.getId().equals("Node_"+ String.valueOf(y))) {
    			 output = position;
    		}
    	position++;
    	}
        
        dijkstra.execute(nodes.get(input));
        LinkedList<Vertex> path = dijkstra.getPath(nodes.get(output));
        
        int Sum = 0;
        
        //láº­p má»™t arraylist cá»§a Ä‘Æ°á»�ng Ä‘i (cÃ¡c cáº·p Ä‘á»‰nh Ä‘áº§u vÃ  Ä‘á»‰nh cuá»‘i)
        ArrayList<Integer> path1 = new ArrayList<Integer>(); 
        if(path != null) {	
        	for (int i = 0; i < path.size() - 1; i++) {
        		String[] parts = path.get(i).getId().split("_");
            	String part1 = parts[1];
            	String[] partss = path.get(i+1).getId().split("_");
            	String part2 = partss[1];
        		path1.add(Integer.parseInt(part1));
        		path1.add(Integer.parseInt(part2));
        	}
        	// tÃ­nh transfer rate
        	int count1 = 0;
        	for (int i = 0; i <= Edge.size() - 5; i = i + 5) {
        		//so sÃ¡nh 2 Ä‘iá»ƒm Ä‘áº§u vÃ  Ä‘iá»ƒm cuá»‘i trong máº£ng
        			if (Edge.get(i).equals(path1.get(count1)) && Edge.get(i+1).equals(path1.get(count1 + 1))) {
        				Sum = Sum + Edge.get(i+2);
        				count1 = count1 + 2;
        			}
        			if( count1 == path1.size()) {
        				break;
        			}
            }
        }
       return Sum;
    }

    private void addLane(String laneId, int sourceLocNo, int destLocNo,
            float duration) {
    	int position = 0;
    	int source = 0;
    	int dest = 0;
    	for (Vertex x : nodes) {
    		if (x.getId().equals("Node_"+ String.valueOf(sourceLocNo))) {
    			source = position; 
    		}
    		if (x.getId().equals("Node_"+ String.valueOf(destLocNo))) {
    			dest = position;
    		}
    	position++;
    	}
        Edge lane = new Edge(laneId,nodes.get(source), nodes.get(dest), duration );
        edges.add(lane);
    }
//    public static void main(String[] args) {
//    	
//    	TestDijkstraAlgorithm q = new TestDijkstraAlgorithm();
//    	ArrayList<Integer> a = new ArrayList<Integer>();
//    	a.add(1);a.add(2);a.add(3);a.add(4);a.add(5);a.add(6);a.add(7);
//    	
//    	ArrayList<Integer> b = new ArrayList<Integer>();
//    	b.add(1);b.add(2);b.add(1);b.add(1);b.add(1);
//    	b.add(1);b.add(3);b.add(1);b.add(3);b.add(5);
//    	b.add(2);b.add(4);b.add(2);b.add(1);b.add(4);
//    	b.add(3);b.add(5);b.add(6);b.add(1);b.add(9);
//    	b.add(3);b.add(6);b.add(3);b.add(5);b.add(1);
//    	b.add(4);b.add(5);b.add(4);b.add(3);b.add(2);
//    	b.add(4);b.add(7);b.add(7);b.add(3);b.add(8);
//    	b.add(5);b.add(7);b.add(9);b.add(9);b.add(2);
//    	b.add(6);b.add(5);b.add(1);b.add(5);b.add(7);
//
//    	q.testExcute(a, b, 1, 7);
//    }
}
