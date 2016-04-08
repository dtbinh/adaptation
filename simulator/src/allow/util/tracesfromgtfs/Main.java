package allow.util.tracesfromgtfs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import allow.simulator.mobility.data.gtfs.GTFSStop;
import allow.simulator.util.Coordinate;
import allow.simulator.util.Geometry;
import allow.simulator.world.StreetMap;
import allow.simulator.world.StreetNode;
import allow.simulator.world.StreetSegment;

public class Main {
	
	private static final Map<String, MapMatching> matchings;
	
	static {
		matchings = new HashMap<String, MapMatching>();
		try {
			MapMatching carMatching = new MapMatching(new StreetMap(Paths.get("/Users/Andi/Documents/DFKI/Allow Ensembles/Simulator/Trento/data/trento/matching_graph_car")));
			MapMatching walkMatching = new MapMatching(new StreetMap(Paths.get("/Users/Andi/Documents/DFKI/Allow Ensembles/Simulator/Trento/data/trento/matching_graph_walk")));
			MapMatching bikeMatching = new MapMatching(new StreetMap(Paths.get("/Users/Andi/Documents/DFKI/Allow Ensembles/Simulator/Trento/data/trento/matching_graph_bike")));
			MapMatching busMatching = new MapMatching(new StreetMap(Paths.get("/Users/Andi/Documents/DFKI/Allow Ensembles/Simulator/Trento/data/trento/matching_graph_car")));

			matchings.put("bicycle", bikeMatching);
			matchings.put("car", carMatching);
			matchings.put("walk", walkMatching);
			matchings.put("bus", busMatching);
			matchings.put("carsharing", carMatching);
			matchings.put("train", busMatching);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) throws IOException {		
		// Load street map.
		// StreetMap map = new StreetMap(Paths.get("/Users/Andi/Documents/DFKI/Allow Ensembles/Simulator/Trento/data/trento/matching_graph_bike"));
	
		List<String> traces = Files.readAllLines(Paths.get("/Users/Andi/Desktop/traces"));
		// MapMatching m = new MapMatching(map);

		for (int i = 0; i < traces.size(); i++) {
			String tokens[] = traces.get(i).split(";;");
			System.out.println("Matching alternative " + tokens[0] + ", leg " + tokens[1]); 
			List<Coordinate> trace = Geometry.decodePolyline(tokens[3]);
			BufferedWriter wr = Files.newBufferedWriter(Paths.get("/Users/Andi/Desktop/mapping/trace " + tokens[0] + "," + tokens[1]), Charset.defaultCharset());
			
			wr.write("lat,lon\n");
			for (Coordinate c : trace) {
				wr.write(c.y + "," + c.x + "\n");
			}
			wr.write("\n");
			MapMatching m = matchings.get(tokens[2].toLowerCase());
			ScoredPath p = m.mapMatch(trace);
			System.out.println(p);
			if (p != null) {
				wr.write("lat,lon\n");
				
				for (StreetSegment s : p.getPath()) {
					//wr.write(s.getStartingNode().getPosition().y + "," + s.getStartingNode().getPosition().x + "\n");
					wr.write(s.getStartingNode().getLabel() + ";;" + s.getEndingNode().getLabel() + "\n");
				}
				Coordinate end = p.getPath().get(p.getPath().size() - 1).getEndPoint();
				wr.write(end.y + "," + end.x + "\n");
			}
			wr.close();
		}
		
		// Load mobility repository.
		/*MobilityRepository repos = new MobilityRepository(Paths.get("/Users/Andi/Documents/DFKI/Allow Ensembles/Simulator/Trento/data/trento/trento-gtfs/"), map);
		
		// Traces from shapes.txt
		Map<String, List<StreetSegment>> traces = readShapesFromFile("/Users/Andi/Documents/DFKI/Allow Ensembles/Simulator/Trento/data/trento/trento-gtfs/shapes.txt");

		// Get agency.
		Map<String, GTFSAgency> agencies = repos.getAgencies();
		
		Map<String, List<List<Coordinate>>> finalShapes = new HashMap<String, List<List<Coordinate>>>();
		Map<String, List<GTFSStop>> stopPositions = new HashMap<String, List<GTFSStop>>();
		
		for (Iterator<String> ai = agencies.keySet().iterator(); ai.hasNext(); ) {
			String agencyId = ai.next();
			List<GTFSRoute> routes = repos.getRoutes().get(agencyId);
			
			for (Iterator<GTFSRoute> ri = routes.iterator(); ri.hasNext(); ) {
				GTFSRoute route = ri.next();
				List<GTFSTrip> trips = repos.getTrips().get(route.getId());
				
				for (Iterator<GTFSTrip> ti = trips.iterator(); ti.hasNext(); ) {
					GTFSTrip trip = ti.next();
					String[] stopIds = repos.getStopTimes().get(trip.getTripId()).getStopIds();
					
					// Check if shape has already been processed.
					if (finalShapes.containsKey(trip.getShapeId())) {
						continue;
					}
					
					// Get trace of shape and continue, if no trace is available.
					List<StreetSegment> trace = traces.get(trip.getShapeId());
					if (trace == null || trace.size() == 0) {
						continue;
					}
					
					// Otherwise split trace along stops.
					List<List<Coordinate>> newShape = new ArrayList<List<Coordinate>>(stopIds.length);
					List<GTFSStop> stops = new ArrayList<GTFSStop>(stopIds.length);
					int lastIndex = 0;
					Coordinate lastCoordinate = trace.get(0).getStartingPoint();
					
					for (int i = 0; i < stopIds.length; i++) {
						// Get position of next stop.
						GTFSStop stop = repos.getStops().get(stopIds[i]);
						Coordinate stopPos = new Coordinate(stop.getLon(), stop.getLat());
						stops.add(stop);
						
						List<Coordinate> s = new ArrayList<Coordinate>();
						
						// Find closest segment by projecting stop position to segment and measure distance.
						int minIndex = 0;
						double minDist = Geometry.haversine(stopPos, projectPointToSegment(stopPos, trace.get(0)));
						Coordinate minCoord = projectPointToSegment(stopPos, trace.get(0));
						
						for (int j = 0; j < trace.size(); j++) {
							Coordinate d = projectPointToSegment(stopPos, trace.get(j));
							double dist = Geometry.haversine(stopPos, d);
							
							if (dist < minDist) {
								minDist = dist;
								minIndex = j;
								minCoord = d;
							}
						}
						
						if (minIndex >= lastIndex) {
							StreetSegment seg = trace.get(lastIndex);
							Coordinate start = lastCoordinate;
							s.add(start);
							
							for (int k = lastIndex; k < minIndex; k++) {
								seg = trace.get(k);
								s.add(seg.getEndPoint());
							}
							s.add(minCoord);
							
						} else {
							StreetSegment seg = trace.get(lastIndex);
							Coordinate start = lastCoordinate;
							s.add(start);

							// If point is closer to one of previous segments, iterate 
							for (int k = lastIndex; k < trace.size(); k++) {
								seg = trace.get(k);
								s.add(seg.getEndPoint());
							}
							
							for (int k = 0; k < minIndex; k++) {
								seg = trace.get(k);
								s.add(seg.getEndPoint());
							}
							s.add(minCoord);

						}
						lastIndex = minIndex;
						lastCoordinate = minCoord;
						newShape.add(s);
					}
					
					if (trip.getShapeId().equals("D587_T0863a_Andata_sub1")
							|| trip.getShapeId().equals("D173_F1212_Ritorno_sub2")
							|| trip.getShapeId().equals("D640_T1222a_Ritorno_sub1")
							|| trip.getShapeId().equals("D639_T1212_Ritorno_sub2")
							|| trip.getShapeId().equals("D566_T0762a_Ritorno_sub1")
							|| trip.getShapeId().equals("D136_F0402_Ritorno_sub1")
							|| trip.getShapeId().equals("D679_T3201a_Circolare_giro_lungo_PRINCIPALE_sub1")) {
						System.out.println(trip.getShapeId());
						getDistanceMatrix(stops, trace);
						System.out.println();
					}
					stopPositions.put(trip.getShapeId(), stops);
					finalShapes.put(trip.getShapeId(), newShape);
				}
			}
		}
		
		// stopPositions contains the list of stop positions for each shape.
		// finalShapes contains the traces to get to each stop.
		
		// 1. Write the split traces.
		for (Iterator<String> stopKeys = stopPositions.keySet().iterator(); stopKeys.hasNext(); ) {
			String key = stopKeys.next();
			List<GTFSStop> c = stopPositions.get(key);
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/Andi/Documents/GPS/split traces/" + key + "_stops")));
			wr.write("lat,lon,seq\n");
			for (int i = 0; i < c.size(); i++) {
				wr.write(c.get(i).getLat() + "," + c.get(i).getLon() + "," + i + "\n");
			}
			
			List<List<Coordinate>> b = finalShapes.get(key);
			for (int i = 0; i < b.size(); i++) {
				List<Coordinate> t = b.get(i);
				wr.write("lat,lon,seq\n");
				for (int j = 0; j < t.size(); j++) {
					wr.write(t.get(j).y + "," + t.get(j).x + "," + i + "\n");
				}
			}
			wr.close();
		}
		
		// Now the actual map matching.
		MapMatching matching = new MapMatching(map);
		List<String> failedMatching = new ArrayList<String>();
		String shapeIds[] = new String[stopPositions.keySet().size()];
		stopPositions.keySet().toArray(shapeIds);
		
		for (int k = 0; k < shapeIds.length; k++) {
			// Print status.
			String shapeId = shapeIds[k];
			System.out.println("Matching traces of shape " + shapeId);
			
			// Get list of coordinates between stops (split trace).
			List<List<Coordinate>> b = finalShapes.get(shapeId);
			List<ScoredPath> result = new ArrayList<ScoredPath>(b.size());
			
			// Now match first trace (from stop 0 to stop 1).
			ScoredPath first = matching.mapMatch(b.get(1));
			
			if (first == null || first.getPath().size() == 0) {
				System.out.println(" No matching for segment 1");
				failedMatching.add(shapeId);
				continue;
			} 
			result.add(first);
			
			for (int i = 2; i < b.size(); i++) {
				List<StreetSegment> last = result.get(result.size() - 1).getPath();
				List<StreetSegment> tt = new ArrayList<StreetSegment>();
				tt.add(last.get(last.size() - 1));
				
				if (last.size() >= 2) {
					tt.add(last.get(last.size() - 2));
				}
				
				if (last.size() >= 3) {
					tt.add(last.get(last.size() - 3));
				}
				ScoredPath next = matching.mapMatch(b.get(i), tt);
				
				if (next == null || next.getPath().size() == 0) {
					System.out.println(" No matching for segment " + i);
					failedMatching.add(shapeId);
					break;
				}
				result.add(next);
			}
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/Andi/Documents/GPS/" + shapeId + "_trace")));
			wr.write("lat,lon,seq\n");

			for (int i = 0; i < b.size(); i++) {
				List<Coordinate> temp = b.get(i);
				
				for (int j = 0; j < temp.size(); j++) {
					wr.write(temp.get(j).y + "," + temp.get(j).x + "," + i + "\n");
				}
			}
			wr.close();

			if (!failedMatching.contains(shapeId)) {
				BufferedWriter wr2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/Users/Andi/Documents/GPS/mapping/" + shapeId)));
				List<GTFSStop> traceStops = stopPositions.get(shapeId);
				
				for (int i = 0; i < traceStops.size() - 1; i++) {
					wr2.write(traceStops.get(i).getId() + "," + traceStops.get(i + 1).getId() + "\n");
					List<StreetSegment> trace = result.get(i).getPath();
					
					for (int j = 0; j < trace.size(); j++) {
						StreetSegment segToWrite = trace.get(j);
						wr2.write(segToWrite.getStartingNode().getLabel() + " ");
					}
					wr2.write(trace.get(trace.size() - 1).getEndingNode().getLabel() + "\n");
				}
				wr2.close();
			}
		}
		
		// Print ids of shapes with failed matching.
		System.out.println("Errors: ");
		
		for (int i = 0; i < failedMatching.size(); i++) {
			System.out.println(" " + failedMatching.get(i));
		}*/
	}
	
	private static Map<String, List<StreetSegment>> readShapesFromFile(String file) throws IOException {
		// Map to return.
		Map<String, List<StreetSegment>> ret = new HashMap<String, List<StreetSegment>>();
		
		// Read from files.
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		// Read first commentary line.
		reader.readLine();
				
		// Read first data line.
		String line = reader.readLine();
		List<Coordinate> currentSequence = null;
		String currentId = "";
		
		while (line != null) {
			// Split current line.
			String tokens[] = line.split(",");
			
			if (!tokens[0].equals(currentId)) {
				
				if (currentSequence != null && !currentId.equals("")) {
					List<StreetSegment> temp =  new ArrayList<StreetSegment>(currentSequence.size());
					
					for (int i = 0; i < currentSequence.size() - 1; i++) {
						temp.add(new StreetSegment(0, new StreetNode(-1, "", currentSequence.get(i)), new StreetNode(-1, "", currentSequence.get(i + 1)), 0, 0));
					}
					ret.put(currentId, temp);
				}
				currentId = tokens[0];
				currentSequence = new ArrayList<Coordinate>(256);
			}
			currentSequence.add(new Coordinate(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[1])));
			line = reader.readLine();
		}
		List<StreetSegment> temp =  new ArrayList<StreetSegment>(currentSequence.size());
		
		for (int i = 0; i < currentSequence.size() - 1; i++) {
			temp.add(new StreetSegment(0, new StreetNode(-1, "", currentSequence.get(i)), new StreetNode(-1, "", currentSequence.get(i + 1)), 0, 0));
		}
		ret.put(currentId, temp);
		reader.close();
		return ret;
	}
	
	private static Coordinate projectPointToSegment(Coordinate c, StreetSegment s) {
		Coordinate start = s.getStartingPoint();
		Coordinate end = s.getEndPoint();
		Coordinate a = new Coordinate(end.x - start.x, end.y - start.y);
		Coordinate b = new Coordinate(c.x - start.x, c.y - start.y);
		
		double norm_a_square = a.x * a.x + a.y * a.y;
		double r = (a.x * b.x + a.y * b.y) / norm_a_square;
		
		if (r < 0.0) {
			return start;
			
		} else if (r >= 0.0 && r < 1.0) {
			return new Coordinate(start.x + r * a.x, start.y + r * a.y);
		
		} else {
			return end;
		}
	}
	
	private static double[][] getDistanceMatrix(List<GTFSStop> stops, List<StreetSegment> polyTrace) {
		List<Coordinate> stopPositions = new ArrayList<Coordinate>(stops.size());
		
		for (int i = 0; i < stops.size(); i++) {
			stopPositions.add(new Coordinate(stops.get(i).getLon(), stops.get(i).getLat()));
		}
		double mat[][] = new double[stopPositions.size()][polyTrace.size()];
		
		for (int i = 0; i < stopPositions.size(); i++) {
			
			for (int j = 0; j < polyTrace.size(); j++) {
				mat[i][j] = Geometry.haversine(stopPositions.get(i), projectPointToSegment(stopPositions.get(i), polyTrace.get(j)));
			}
		}
		System.out.println(mat.length * mat[0].length);
		return mat;
	}
	
	/*private static List<Integer> splitTraceAlongStops(List<StreetSegment> polyTrace, List<GTFSStop> stops) {
		double distanceMatrix[][] = getDistanceMatrix(stops, polyTrace);
		boolean trackingMatix[][] = new boolean[distanceMatrix.length][distanceMatrix[0].length];
		
		boolean foundSplitting = false;
		
		
		while (!foundSplitting) {
			List<Integer> indices = new ArrayList<Integer>(stops.size());
			
			double cumulatedError = findSplitting(distanceMatrix, trackingMatrix, indices);
			
			if (cumulatedError != -1) {
				return indices;
			}
			
			
		}
		
		
		return null;
	}

	private static int nextIndex(double distances[][], boolean error[][], List<Integer> indices) {
		int firstNonErrorIndex = indices.size() == 0 ? 0 :-1;
		int offset = indices.size() == 0 ? 0 : indices.get(indices.size() - 1);
		
		double currentDistances[] = distances[]
		for (int i = offset; i < error.length; i++) {
			
			if (!error[i]) {
				firstNonErrorIndex = i;
				break;
			}
		}
		
		if (firstNonErrorIndex == -1) {
			return -1;
		}
		
		int minIndex = firstNonErrorIndex;
		
		for (int i = firstNonErrorIndex + 1; i < distances.length; i++) {
			
			if (!error[i] && distances[i] < distances[minIndex]) {
				minIndex = i;
			}
		}
		return minIndex;
	}*/
	/*private static List<StreetSegment> createPolyTrace(List<Coordinate> coordinates) {
		if (coordinates == null) {
			return null;
		}
		
		if (coordinates.size() == 0) {
			return new ArrayList<StreetSegment>(0);
		}
		List<StreetSegment> ret = new ArrayList<StreetSegment>(coordinates.size() - 1);
		
		for (int i = 0; i < coordinates.size() - 1; i++) {
			ret.add(new StreetSegment(-i, 
					new StreetNode(i, "", coordinates.get(i)),
					new StreetNode(i + 1, "", coordinates.get(i + 1)),
					9.0,
					PolylinesUtil.haversine(coordinates.get(i), coordinates.get(i + 1))));
		}
		return ret;
	}*/
}
