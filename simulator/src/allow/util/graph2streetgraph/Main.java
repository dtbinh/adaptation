package allow.util.graph2streetgraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.opentripplanner.routing.edgetype.StreetEdge;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Graph.LoadLevel;
import org.opentripplanner.routing.graph.Vertex;

import allow.simulator.world.StreetNode;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

public class Main {

	public static void main(String args[]) throws ClassNotFoundException, IOException {
		//Graph g = Graph.load(new File("/Users/Andi/otprepos/Graph/Graph.obj"), LoadLevel.FULL);
		Graph g = Graph.load(new File("/Users/Andi/Documents/DFKI/Allow Ensembles/Repository/repos/Software/DFKI Simulator/OpentripPlanner/Graph.obj"), LoadLevel.FULL);
		Map<String, StreetNode> nodes = new HashMap<String, StreetNode>();
		Map<String, Segment> segments = new HashMap<String, Segment>();
		
		int id = 0;
		
		System.out.println("Number of nodes: " + g.getVertices().size());
		System.out.println("Number of edges: " + g.getEdges().size());
		
		for (Edge e : g.getEdges()) {

			if (e instanceof StreetEdge) {
				StreetEdge link = (StreetEdge) e;
				StreetTraversalPermission p = link.getPermission();
				if ((p == StreetTraversalPermission.ALL)
						// || (p == StreetTraversalPermission.BICYCLE)
						// || (p == StreetTraversalPermission.BICYCLE_AND_CAR)
						// || (p == StreetTraversalPermission.CAR)
						|| (p == StreetTraversalPermission.PEDESTRIAN)
						|| (p == StreetTraversalPermission.PEDESTRIAN_AND_BICYCLE)
						|| (p == StreetTraversalPermission.PEDESTRIAN_AND_BICYCLE_AND_CAR)
						|| (p == StreetTraversalPermission.PEDESTRIAN_AND_CAR)
						//|| (p == StreetTraversalPermission.PEDESTRIAN_AND_BICYCLE)
						) {
					
					Vertex v1 = link.getFromVertex();
					StreetNode n1;
					
					if (!nodes.containsKey(v1.getLabel())) {
						n1 = new StreetNode(id++, v1.getLabel().replaceAll("\\s",""), new allow.simulator.util.Coordinate(v1.getLon(), v1.getLat()));
						nodes.put(v1.getLabel(), n1);
					} else {
						n1 = nodes.get(v1.getLabel());
					}
				
					Vertex v2 = link.getToVertex();
					StreetNode n2;
				
					if (!nodes.containsKey(v2.getLabel())) {
						n2 = new StreetNode(id++, v2.getLabel().replaceAll("\\s",""), new allow.simulator.util.Coordinate(v2.getLon(), v2.getLat()));
						nodes.put(v2.getLabel(), n2);
					} else {
						n2 = nodes.get(v2.getLabel());
					}
				
					Segment s = new Segment();
					s.id = n1.getLabel() + "->" + n2.getLabel();
					s.fromLabel = n1.getLabel();
					s.tolabel = n2.getLabel();
					LineString segs = link.getGeometry();
					Coordinate coord[] = segs.getCoordinates();
					s.segs = new String[coord.length];
					s.segs[0] = n1.getLabel();
					
					for (int i = 1; i < coord.length - 1; i++) {
						StreetNode temp = new StreetNode(id++, n1.getLabel() + "->" + n2.getLabel() + "->" + i, new allow.simulator.util.Coordinate(coord[i].x, coord[i].y));
						nodes.put(temp.getLabel(), temp);
						s.segs[i] = temp.getLabel();
					}
					s.segs[coord.length - 1] = n2.getLabel();
					s.name = link.getName();
					s.speed = link.getCarSpeed();
					s.length = link.getDistance();
					segments.put(n1.getLabel() + "->" + n2.getLabel(), s);
				}
			}
		}

		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("matching_graph_walk")));
		
		wr.write("NODES " + nodes.size() + "\n");
		
		for (StreetNode node : nodes.values()) {
			wr.write(node.getLabel() + ";;" + node.getPosition().x + ";;" + node.getPosition().y + "\n");
		}
		wr.write("\n");
		
		wr.write("LINKS " + segments.size() + "\n");
		
		for (Segment s : segments.values()) {
			wr.write(s.id + ";;");
			wr.write(s.fromLabel + ";;");
			wr.write(s.tolabel + ";;");
			wr.write(s.name + ";;");
			wr.write(s.speed + ";;");
			
			for (int i = 0; i < s.segs.length - 1; i++) {
				wr.write(s.segs[i] + " ");
			}
			wr.write(s.segs[s.segs.length - 1] + "\n");
		}
		wr.close();
		/*Graph g = Graph.load(new File("/var/otp/graphs/Graph.obj"), LoadLevel.FULL);
		Map<String, StreetNode> nodes = new HashMap<String, StreetNode>();
		Map<Long, Segment> segments = new HashMap<Long, Segment>();
		
		double minX = 10.9824643;
		double maxX = 11.2052265;
		double minY = 45.9434744;
		double maxY = 46.1721383;
		
		int id = 0;
		int segId = 0;
		
		System.out.println("Number of nodes: " + g.getVertices().size());
		System.out.println("Number of edges: " + g.getEdges().size());
		
		for (Edge e : g.getEdges()) {

			if (e instanceof StreetEdge) {
				StreetEdge link = (StreetEdge) e;
				StreetTraversalPermission p = link.getPermission();
				
				if ((p == StreetTraversalPermission.ALL)
						|| (p == StreetTraversalPermission.ALL_DRIVING)
						|| (p == StreetTraversalPermission.BICYCLE_AND_DRIVING)
						|| (p == StreetTraversalPermission.BICYCLE_AND_CAR)
						|| (p == StreetTraversalPermission.CAR)
						|| (p == StreetTraversalPermission.PEDESTRIAN_AND_BICYCLE_AND_CAR)
						|| (p == StreetTraversalPermission.PEDESTRIAN_AND_CAR)
						|| (p == StreetTraversalPermission.PEDESTRIAN_AND_DRIVING)) {
					
					Vertex v1 = link.getFromVertex();
					StreetNode n1;
					
					if (!nodes.containsKey(v1.getLabel())) {
						n1 = new StreetNode(id++, v1.getLabel(), new allow.simulator.util.Coordinate(v1.getLon(), v1.getLat()));
						nodes.put(v1.getLabel(), n1);
					} else {
						n1 = nodes.get(v1.getLabel());
					}
				
					Vertex v2 = link.getToVertex();
					StreetNode n2;
				
					if (!nodes.containsKey(v2.getLabel())) {
						n2 = new StreetNode(id++, v2.getLabel(), new allow.simulator.util.Coordinate(v2.getLon(), v2.getLat()));
						nodes.put(v2.getLabel(), n2);
					} else {
						n2 = nodes.get(v2.getLabel());
					}
				
					Segment s = new Segment();
					s.id = segId++;
					s.fromId = n1.getId();
					s.fromLabel = n1.getLabel();
					s.toId = n2.getId();
					s.tolabel = n2.getLabel();
					LineString segs = link.getGeometry();
					Coordinate coord[] = segs.getCoordinates();
					s.segs = coord;
					s.name = link.getName();
					s.speed = link.getCarSpeed();
					s.length = link.getLength();
					segments.put(s.id, s);
				}
			}
		}

		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("matching_graph")));
		wr.write("ENVELOPE " + minX + " " + maxX + " " + minY + " " + maxY + "\n\n");
		
		wr.write("NODES " + nodes.size() + "\n");
		
		for (StreetNode node : nodes.values()) {
			wr.write(node.getId() + ";;" + node.getLabel() + ";;" + node.getPosition().x + ";;" + node.getPosition().y + "\n");
		}
		wr.write("\n");
		
		wr.write("LINKS " + segments.size() + "\n");
		
		for (Segment s : segments.values()) {
			wr.write(s.id + ";;");
			wr.write(s.fromId + ";;");
			wr.write(s.toId + ";;");
			wr.write(s.name + ";;");
			wr.write(s.speed + ";;");
			
			for (int i = 0; i < s.segs.length - 1; i++) {
				wr.write(s.segs[i].x + " " + s.segs[i].y + " ");
			}
			wr.write(s.segs[s.segs.length - 1].x + " " + s.segs[s.segs.length - 1].y + "\n");
		}
		wr.close();*/
	}

}
