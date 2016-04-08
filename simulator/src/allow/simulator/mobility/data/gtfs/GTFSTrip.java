package allow.simulator.mobility.data.gtfs;

public class GTFSTrip {

	private String routeId;
	private String serviceId;
	private String tripId;
	private String tripHeadsign;
	private String directionId;
	private String shapeId;
	
	private static int TRIP_ROUTE_ID = 0;
	private static int TRIP_SERIVCE_ID = 1;
	private static int TRIP_ID = 2;
	private static int TRIP_HEADSIGN = 3;
	private static int TRIP_DIRECTION_ID = 4;
	private static int TRIP_SHAPE_ID = 5;

	public String getRouteId() {
		return routeId;
	}
	
	public String getServiceId() {
		return serviceId;
	}
	
	public String getTripId() {
		return tripId;
	}
	
	public String getTripHeadsign() {
		return tripHeadsign;
	}
	
	public String getDirectionsId() {
		return directionId;
	}
	
	public String getShapeId() {
		return shapeId;
	}
	
	public static GTFSTrip fromGTFS(String line) {
		GTFSTrip t = new GTFSTrip();
		String tokens[] = line.split(",");
		t.routeId = tokens[TRIP_ROUTE_ID];
		t.serviceId = tokens[TRIP_SERIVCE_ID];
		t.tripId = tokens[TRIP_ID];
		t.tripHeadsign = tokens[TRIP_HEADSIGN];
		t.directionId = tokens[TRIP_DIRECTION_ID];
		t.shapeId = (tokens.length == 6) ? tokens[TRIP_SHAPE_ID] : "";
		return t;
	}
}
