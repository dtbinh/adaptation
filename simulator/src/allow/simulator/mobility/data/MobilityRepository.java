package allow.simulator.mobility.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import allow.simulator.mobility.data.gtfs.GTFSAgency;
import allow.simulator.mobility.data.gtfs.GTFSRoute;
import allow.simulator.mobility.data.gtfs.GTFSService;
import allow.simulator.mobility.data.gtfs.GTFSServiceException;
import allow.simulator.mobility.data.gtfs.GTFSStop;
import allow.simulator.mobility.data.gtfs.GTFSStopTimes;
import allow.simulator.mobility.data.gtfs.GTFSTrip;
import allow.simulator.util.Coordinate;
import allow.simulator.util.Geometry;
import allow.simulator.world.Street;
import allow.simulator.world.StreetMap;
import allow.simulator.world.StreetNode;
import allow.simulator.world.StreetSegment;

/**
 * Data repository for offline data service realization.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class MobilityRepository {
	
	// Mapping of agencyId to GTFSAgency.
	private Map<String, GTFSAgency> agencies;
	
	// Mapping of agencyId to list of GTFSRoutes operated by that agency.
	private Map<String, List<GTFSRoute>> routes;
	
	// Mapping of serviceId to GTFSService.
	private Map<String, GTFSService> service;
	
	// Mapping of serviceId to GTFSServiceExceptions.
	private Map<String, List<GTFSServiceException>> exceptions;
	
	// Mapping of stopId to GTFSStop.
	private Map<String, GTFSStop> stops;
	
	// Mapping of tripId to GTFSStopTimes.
	private Map<String, GTFSStopTimes> stoptimes;
	
	// Mapping of routeId to GTFSTrips.
	private Map<String, List<GTFSTrip>> trips;
	
	// Mapping of identifier (stop1.id,stop2.id) to list of segments.
	private Map<String, List<StreetSegment>> routing;
	private Map<String, List<Street>> routingStreet;
	
	/**
	 * Constructor.
	 * Creates a new instance of a mobility repository from a street map and 
	 * a GTFS data set.
	 * 
	 * @param path Path to folder containing GTFS data set.
	 * @param map Street map used for routing.
	 * @throws IOException
	 */
	public MobilityRepository(Path path, StreetMap map) throws IOException {
		agencies = loadAgencies(path.resolve("agency.txt"));
		routes = loadRoutes(path.resolve("routes.txt"));
		service = loadServices(path.resolve("calendar.txt"));
		exceptions = loadServiceExceptions(path.resolve("calendar_dates.txt"));
		stops = loadStops(path.resolve("stops.txt"));
		stoptimes = loadStopTimes(path.resolve("stop_times.txt"));
		trips = loadTrips(path.resolve("trips.txt"));
		routing = createRouting(path, map);
		routingStreet = createRoutingStreet(path, map);
	}
	
	private Map<String, GTFSAgency> loadAgencies(Path path) throws IOException {
		// Read file.
		List<String> content = Files.readAllLines(path);

		// Initialize map to return.
		Map<String, GTFSAgency> ret = new HashMap<String, GTFSAgency>(content.size() - 1);
		
		// Parse agencies from lines.
		for (int i = 1; i < content.size(); i++) {
			GTFSAgency newAgency = GTFSAgency.fromGTFS(content.get(i));
			ret.put(newAgency.getId(), newAgency);
		}
		return ret;
	}
	
	private Map<String, List<GTFSRoute>> loadRoutes(Path path) throws IOException {
		// Read file.
		List<String> content = Files.readAllLines(path);

		// Initialize map to return.
		Map<String, List<GTFSRoute>> ret = new HashMap<String, List<GTFSRoute>>(content.size() - 1);
		
		// Parse route from lines.
		for (int i = 1; i < content.size(); i++) {
			GTFSRoute newRoute = GTFSRoute.fromGTFS(content.get(i));
			List<GTFSRoute> temp = ret.get(newRoute.getAgencyId());
			
			if (temp != null) {
				temp.add(newRoute);
			} else {
				List<GTFSRoute> r = new ArrayList<GTFSRoute>();
				r.add(newRoute);
				ret.put(newRoute.getAgencyId(), r);
			}
		}
		return ret;
	}
	
	private Map<String, GTFSService> loadServices(Path path) throws IOException {
		// Read file.
		List<String> content = Files.readAllLines(path);

		// Initialize map to return.
		Map<String, GTFSService> ret = new HashMap<String, GTFSService>(content.size() - 1);
		
		// Parse route from lines.
		for (int i = 1; i < content.size(); i++) {
			GTFSService newService;
			try {
				newService = GTFSService.fromGTFS(content.get(i));
			} catch (ParseException e) {
				throw new IOException(e);
			}
			ret.put(newService.getServiceId(), newService);
		}
		return ret;
	}
	
	private Map<String, List<GTFSServiceException>> loadServiceExceptions(Path path) throws IOException {
		// Read file.
		List<String> content = Files.readAllLines(path);

		// Initialize map to return.
		Map<String, List<GTFSServiceException>> ret = new HashMap<String, List<GTFSServiceException>>(content.size() - 1);
		
		// Parse route from lines.
		for (int i = 1; i < content.size(); i++) {
			GTFSServiceException newServiceException;
			try {
				newServiceException = GTFSServiceException.fromGTFS(content.get(i));
			} catch (ParseException e) {
				throw new IOException(e);
			}
			List<GTFSServiceException> exceptions = ret.get(newServiceException.getServiceId());
			
			if (exceptions == null) {
				List<GTFSServiceException> ex = new ArrayList<GTFSServiceException>();
				ex.add(newServiceException);
				ret.put(newServiceException.getServiceId(), ex);
				
			} else {
				exceptions.add(newServiceException);
			}
		}
		return ret;
	}
	
	private Map<String, GTFSStop> loadStops(Path path) throws IOException {
		// Read file.
		List<String> content = Files.readAllLines(path);

		// Initialize map to return.
		Map<String, GTFSStop> ret = new HashMap<String, GTFSStop>(content.size() - 1);
		
		// Parse stops from lines.
		for (int i = 1; i < content.size(); i++) {
			GTFSStop newStop = GTFSStop.fromGTFS(content.get(i));
			ret.put(newStop.getId(), newStop);
		}
		return ret;
	}
	
	private Map<String, GTFSStopTimes> loadStopTimes(Path path) throws IOException {
		// Read file.
		List<String> content = Files.readAllLines(path);

		// Initialize map to return.
		Map<String, GTFSStopTimes> ret = new HashMap<String, GTFSStopTimes>();
		
		// Parse stop times from corresponding lines.
		int startIdx = 1;
		int endIdx = 1;
		int seq = 0;
		String previousTripId = "";
		
		for (int i = 1; i < content.size(); i++) {
			String line = content.get(i);
			String tokens[] = line.split(",");
			int seqNumber = Integer.parseInt(tokens[4]);
			String tripId = tokens[0];
			
			if (previousTripId.equals("")) {
				previousTripId = tokens[0];
			}
			
			if (i == content.size() - 1) {
				GTFSStopTimes newStopTime = GTFSStopTimes.fromGTFS(content.subList(startIdx, endIdx));
				ret.put(newStopTime.getTripId(), newStopTime);
				
			} else if (seqNumber > seq && tripId.equals(previousTripId)) {
				endIdx++;
				seq = seqNumber;
				
			} else {
				GTFSStopTimes newStopTime = GTFSStopTimes.fromGTFS(content.subList(startIdx, endIdx));
				ret.put(newStopTime.getTripId(), newStopTime);
				startIdx = endIdx;
				endIdx++;
				seq = seqNumber;
				previousTripId = tripId;
			}	
		}
		return ret;
	}
	
	private Map<String, List<GTFSTrip>> loadTrips(Path path) throws IOException {
		// Read file.
		List<String> content = Files.readAllLines(path);

		// Initialize map to return.
		Map<String, List<GTFSTrip>> ret = new HashMap<String, List<GTFSTrip>>();
		
		// Parse trips from lines.
		for (int i = 1; i < content.size(); i++) {
			GTFSTrip newTrip = GTFSTrip.fromGTFS(content.get(i));
			List<GTFSTrip> temp = ret.get(newTrip.getRouteId());
			
			if (temp != null) {
				temp.add(newTrip);
			} else {
				List<GTFSTrip> n = new ArrayList<GTFSTrip>();
				n.add(newTrip);
				ret.put(newTrip.getRouteId(), n);
			}
		}
		return ret;
	}
	
	private Map<String, List<Coordinate>> loadShapes(Path path) throws IOException {
		Map<String, List<Coordinate>> ret = new HashMap<String, List<Coordinate>>();
		
		// Read file.
		List<String> content = Files.readAllLines(path);
						
		// Read first data line.
		List<Coordinate> currentSequence = null;
		String currentId = "";
		
		for (int i = 1; i < content.size(); i++) {
			// Split current line.
			String tokens[] = content.get(i).split(",");
			
			if (!tokens[0].equals(currentId)) {
				currentId = tokens[0];
				currentSequence = new ArrayList<Coordinate>(256);
				ret.put(currentId, currentSequence);
			}
			currentSequence.add(new Coordinate(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[1])));
		}
		ret.put(currentId, currentSequence);
		return ret;
	}
	
	private Map<String, List<StreetSegment>> parseRoutingFromFile(Path path, StreetMap map) throws IOException {
		Map<String, List<StreetSegment>> ret = new HashMap<String, List<StreetSegment>>();
		List<String> lines = Files.readAllLines(path);
		
		for (int i = 0; i < lines.size(); i += 2) {
			String key = lines.get(i);
			String nodes = lines.get(i + 1);
			String tokens[] = nodes.split(" ");
			List<StreetSegment> segs = new ArrayList<StreetSegment>();
			
			for (int j = 0; j < tokens.length - 1; j++) {
				// Get source node.
				StreetNode n1 = map.getStreetNode(tokens[j]);
				
				if (n1 == null) {
					throw new IllegalStateException("Error: Node " + tokens[j] + " unknown.");
				}
				// Get destination node.
				StreetNode n2 = map.getStreetNode(tokens[j + 1]);
				
				if (n2 == null) {
					throw new IllegalStateException("Error: Node " + tokens[j + 1] + " unknown.");
				}
				// Get segment.
				StreetSegment seg = map.getStreetSegment(n1, n2);
				
				if (seg == null) {
					throw new IllegalStateException("Error: Segment between " + n1.getLabel() + " and " + n2.getLabel() + " unknown.");
				}
				segs.add(seg);
			}
			ret.put(key, segs);
		}
		return ret;
	}
	
	private Map<String, List<Street>> parseStreetRoutingFromFile(Path path, StreetMap map) throws IOException {
		Map<String, List<Street>> ret = new HashMap<String, List<Street>>();
		List<String> lines = Files.readAllLines(path);
		
		for (int i = 0; i < lines.size(); i += 2) {
			String key = lines.get(i);
			String nodes = lines.get(i + 1);
			String tokens[] = nodes.split(" ");
			List<Street> streets = new ArrayList<Street>();
			
			//for (int j = 0; j < tokens.length - 1; j++) {
				StreetNode n1 = null, n2 = null;
				int k = 0;

				while ((k < tokens.length) && (n1 == null)) {
					n1 = map.getStreetNodeReduced(tokens[k]);
					k++;
				}

				while (k < tokens.length) {
					n2 = null;
					
					while ((k < tokens.length) && (n2 == null)) {
						n2 = map.getStreetNodeReduced(tokens[k]);
						k++;
					}
					
					if (k == tokens.length)
						break;
					Street street = map.getStreet(n1.getLabel(), n2.getLabel());
					
					if (street == null)
						throw new IllegalStateException("Error: Could not find street.");
					n1 = n2;
					streets.add(street);
				}
			//}
			ret.put(key, streets);
		}
		return ret;
	}
	
	private Map<String, List<StreetSegment>> createRouting(Path path, StreetMap map) throws IOException {
		// Mapping to return.
		Map<String, List<StreetSegment>> ret = new HashMap<String, List<StreetSegment>>();
		List<String> processedShapes = new ArrayList<String>();
		
		// Load shapes.
		Map<String, List<Coordinate>> shapes = loadShapes(path.resolve("shapes.txt"));
		
		for (Iterator<String> ai = agencies.keySet().iterator(); ai.hasNext(); ) {
			String agencyId = ai.next();
			List<GTFSRoute> rts = routes.get(agencyId);
			
			for (Iterator<GTFSRoute> ri = rts.iterator(); ri.hasNext(); ) {
				GTFSRoute route = ri.next();
				List<GTFSTrip> tps = trips.get(route.getId());
				
				for (Iterator<GTFSTrip> ti = tps.iterator(); ti.hasNext(); ) {
					GTFSTrip trip = ti.next();
					String[] stopIds = stoptimes.get(trip.getTripId()).getStopIds();
					
					// Check if shape has already been processed.
					if (processedShapes.contains(trip.getShapeId())) {
						continue;
					}
					
					// Get trace of shape and continue, if no trace is available.
					List<StreetSegment> polyTrace = createPolyTrace(shapes.get(trip.getShapeId()));
					if (polyTrace == null || polyTrace.size() == 0) {
						continue;
					}
					
					// Check if shape has map matched traces.
					Path mappingPath = path.resolve("mapping/" + trip.getShapeId());
					
					if (Files.exists(mappingPath)) {
						Map<String, List<StreetSegment>> mapping = parseRoutingFromFile(mappingPath, map);
						ret.putAll(mapping);
						processedShapes.add(trip.getShapeId());
						continue;
					}
					System.out.println("Warning: No routing found for " + trip.getShapeId() + ". Using non matched GPS traces instead.");

					// Otherwise split trace along stops and create "virtual segments".
					List<List<Coordinate>> newShape = new ArrayList<List<Coordinate>>(stopIds.length);
					int lastIndex = 0;
					Coordinate lastCoordinate = polyTrace.get(0).getStartingPoint();
					
					for (int i = 0; i < stopIds.length; i++) {
						// Get position of next stop.
						GTFSStop stop = stops.get(stopIds[i]);
						Coordinate stopPos = new Coordinate(stop.getLon(), stop.getLat());
						List<Coordinate> s = new ArrayList<Coordinate>();
						
						// Find closest segment by projecting stop position to segment and measure distance.
						int minIndex = 0;
						double minDist = Geometry.haversine(stopPos, projectPointToSegment(stopPos, polyTrace.get(0)));
						Coordinate minCoord = projectPointToSegment(stopPos, polyTrace.get(0));
						
						for (int j = 0; j < polyTrace.size(); j++) {
							Coordinate d = projectPointToSegment(stopPos, polyTrace.get(j));
							double dist = Geometry.haversine(stopPos, d);
							
							if (dist < minDist) {
								minDist = dist;
								minIndex = j;
								minCoord = d;
							}
						}
						
						if (minIndex >= lastIndex) {
							StreetSegment seg = polyTrace.get(lastIndex);
							Coordinate start = lastCoordinate;
							s.add(start);
							
							for (int k = lastIndex; k < minIndex; k++) {
								seg = polyTrace.get(k);
								s.add(seg.getEndPoint());
							}
							s.add(minCoord);
							
						} else {
							StreetSegment seg = polyTrace.get(lastIndex);
							Coordinate start = lastCoordinate;
							s.add(start);
							
							// If point is closer to one of previous segments, iterate 
							for (int k = lastIndex; k < polyTrace.size(); k++) {
								seg = polyTrace.get(k);
								s.add(seg.getEndPoint());
							}
							
							for (int k = 0; k < minIndex; k++) {
								seg = polyTrace.get(k);
								s.add(seg.getEndPoint());
							}
							s.add(minCoord);

						}
						lastIndex = minIndex;
						lastCoordinate = minCoord;
						newShape.add(s);
					}
					
					for (int i = 0; i < stopIds.length - 1; i++) {
						ret.put(stopIds[i] + "," + stopIds[i + 1], createPolyTrace(newShape.get(i + 1)));
					}
					processedShapes.add(trip.getShapeId());
				}
			}
		}
		return ret;
	}
	
	private Map<String, List<Street>> createRoutingStreet(Path path, StreetMap map) throws IOException {
		// Mapping to return.
		Map<String, List<Street>> ret = new HashMap<String, List<Street>>();
		List<String> processedShapes = new ArrayList<String>();
		
		// Load shapes.
		Map<String, List<Coordinate>> shapes = loadShapes(path.resolve("shapes.txt"));
		
		for (Iterator<String> ai = agencies.keySet().iterator(); ai.hasNext(); ) {
			String agencyId = ai.next();
			List<GTFSRoute> rts = routes.get(agencyId);
			
			for (Iterator<GTFSRoute> ri = rts.iterator(); ri.hasNext(); ) {
				GTFSRoute route = ri.next();
				List<GTFSTrip> tps = trips.get(route.getId());
				
				for (Iterator<GTFSTrip> ti = tps.iterator(); ti.hasNext(); ) {
					GTFSTrip trip = ti.next();
					String[] stopIds = stoptimes.get(trip.getTripId()).getStopIds();
					
					// Check if shape has already been processed.
					if (processedShapes.contains(trip.getShapeId())) {
						continue;
					}
					
					// Get trace of shape and continue, if no trace is available.
					List<Street> polyTrace = createPolyTraceStreet(shapes.get(trip.getShapeId()));
					if (polyTrace == null || polyTrace.size() == 0) {
						continue;
					}
					
					// Check if shape has map matched traces.
					Path mappingPath = path.resolve("mapping/" + trip.getShapeId());
					
					if (Files.exists(mappingPath)) {
						Map<String, List<Street>> mapping = parseStreetRoutingFromFile(mappingPath, map);
						ret.putAll(mapping);
						processedShapes.add(trip.getShapeId());
						continue;
					}
					System.out.println("Warning: No routing found for " + trip.getShapeId() + ". Using non matched GPS traces instead.");

					// Otherwise split trace along stops and create "virtual segments".
					List<List<Coordinate>> newShape = new ArrayList<List<Coordinate>>(stopIds.length);
					int lastIndex = 0;
					Coordinate lastCoordinate = polyTrace.get(0).getStartingNode().getPosition();
					
					for (int i = 0; i < stopIds.length; i++) {
						// Get position of next stop.
						GTFSStop stop = stops.get(stopIds[i]);
						Coordinate stopPos = new Coordinate(stop.getLon(), stop.getLat());
						List<Coordinate> s = new ArrayList<Coordinate>();
						
						// Find closest segment by projecting stop position to segment and measure distance.
						int minIndex = 0;
						double minDist = Geometry.haversine(stopPos, projectPointToVirtualStreet(stopPos, polyTrace.get(0)));
						Coordinate minCoord = projectPointToVirtualStreet(stopPos, polyTrace.get(0));
						
						for (int j = 0; j < polyTrace.size(); j++) {
							Coordinate d = projectPointToVirtualStreet(stopPos, polyTrace.get(j));
							double dist = Geometry.haversine(stopPos, d);
							
							if (dist < minDist) {
								minDist = dist;
								minIndex = j;
								minCoord = d;
							}
						}
						
						if (minIndex >= lastIndex) {
							Street seg = polyTrace.get(lastIndex);
							Coordinate start = lastCoordinate;
							s.add(start);
							
							for (int k = lastIndex; k < minIndex; k++) {
								seg = polyTrace.get(k);
								s.add(seg.getEndNode().getPosition());
							}
							s.add(minCoord);
							
						} else {
							Street seg = polyTrace.get(lastIndex);
							Coordinate start = lastCoordinate;
							s.add(start);
							
							// If point is closer to one of previous segments, iterate 
							for (int k = lastIndex; k < polyTrace.size(); k++) {
								seg = polyTrace.get(k);
								s.add(seg.getEndNode().getPosition());
							}
							
							for (int k = 0; k < minIndex; k++) {
								seg = polyTrace.get(k);
								s.add(seg.getEndNode().getPosition());
							}
							s.add(minCoord);

						}
						lastIndex = minIndex;
						lastCoordinate = minCoord;
						newShape.add(s);
					}
					
					for (int i = 0; i < stopIds.length - 1; i++) {
						ret.put(stopIds[i] + "," + stopIds[i + 1], createPolyTraceStreet(newShape.get(i + 1)));
					}
					processedShapes.add(trip.getShapeId());
				}
			}
		}
		return ret;
	}
	
	private static long virtualSegId = 1;
	private static long virtualNodeId = 0;
	
	private static List<StreetSegment> createPolyTrace(List<Coordinate> coordinates) {
		if (coordinates == null) {
			return null;
		}
		
		if (coordinates.size() == 0) {
			return new ArrayList<StreetSegment>(0);
		}
		List<StreetSegment> ret = new ArrayList<StreetSegment>(coordinates.size() - 1);
		
		for (int i = 0; i < coordinates.size() - 1; i++) {
			
			if (!coordinates.get(i).equals(coordinates.get(i + 1))) {
				ret.add(new StreetSegment(-(virtualSegId++), 
						new StreetNode(-(virtualNodeId++), "", coordinates.get(i)),
						new StreetNode(-(virtualNodeId++), "", coordinates.get(i + 1)),
						11.1,
						Geometry.haversine(coordinates.get(i), coordinates.get(i + 1))));
			}
		}
		return ret;
	}
	
	private static List<Street> createPolyTraceStreet(List<Coordinate> coordinates) {
		if (coordinates == null) {
			return null;
		}
		
		if (coordinates.size() == 0) {
			return new ArrayList<Street>(0);
		}
		List<Street> ret = new ArrayList<Street>(coordinates.size() - 1);
		
		for (int i = 0; i < coordinates.size() - 1; i++) {
			
			if (!coordinates.get(i).equals(coordinates.get(i + 1))) {
				List<StreetSegment> segs = new ArrayList<StreetSegment>(1);
				segs.add(new StreetSegment(-(virtualSegId++), 
						new StreetNode(-(virtualNodeId++), "", coordinates.get(i)),
						new StreetNode(-(virtualNodeId++), "", coordinates.get(i + 1)),
						11.1,
						Geometry.haversine(coordinates.get(i), coordinates.get(i + 1))));
				ret.add(new Street(-(virtualSegId++), "", segs));
			}
		}
		return ret;
	}
	
	private static Coordinate projectPointToVirtualStreet(Coordinate c, Street s) {
		Coordinate start = s.getStartingNode().getPosition();
		Coordinate end = s.getEndNode().getPosition();
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
	
	/**
	 * Returns mapping (agencyId, agency) of all agencies of urban mobility
	 * system.
	 * 
	 * @return All agencies of urban mobility system.
	 */
	public Map<String, GTFSAgency> getAgencies() {
		return agencies;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public Map<String, List<GTFSRoute>> getRoutes() {
		return routes;
	}
	
	public Map<String, GTFSService> getServices() {
		return service;
	}
	
	public Map<String, List<GTFSServiceException>> getServiceExceptions() {
		return exceptions;
	}
	
	public Map<String, GTFSStop> getStops() {
		return stops;
	}
	
	public Map<String, GTFSStopTimes> getStopTimes() {
		return stoptimes;
	}
	
	public Map<String, List<GTFSTrip>> getTrips() {
		return trips;
	}
	
	public List<StreetSegment> getRouting(String stop1, String stop2) {
		return routing.get(stop1 + "," + stop2);
	}
	
	public List<Street> getRoutingStreet(String stop1, String stop2) {
		return routingStreet.get(stop1 + "," + stop2);
	}
	
	public Map<String, List<StreetSegment>> getAllRoutings() {
		return routing;
	}
}
