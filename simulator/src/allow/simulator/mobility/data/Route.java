package allow.simulator.mobility.data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import allow.simulator.core.Simulator;
import allow.simulator.mobility.data.TimeTable.Day;
import allow.simulator.mobility.data.gtfs.GTFSService;
import allow.simulator.mobility.data.gtfs.GTFSServiceException;
import allow.simulator.mobility.data.gtfs.GTFSStopTimes;
import allow.simulator.world.Street;

public class Route {
	// Id of this route.
	private String routeId;
	
	// Stops of this route.
	private Map<String, PublicTransportationStop> stops;
	
	// Trips of this route ordered chronological by day.
	private List<List<PublicTransportationTrip>> trips;
	private Map<String, PublicTransportationTrip> tripInfo;

	// Buffer to return.
	private List<PublicTransportationTrip> tripsToReturn;
	
	/**
	 * Constructor.
	 * Creates a new route with given Id and time table.
	 * 
	 * @param routeId Id of this route.
	 * @param timeTable Time table of this route.
	 */
	public Route(String routeId, TimeTable timeTable, Map<String, PublicTransportationStop> stops) {
		this.routeId = routeId;
		this.stops = stops;
		timeTableToTrips(timeTable);
		tripsToReturn = new ArrayList<PublicTransportationTrip>(16);
	}
	
	private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("kk:mm:ss");
	
	private void timeTableToTrips(TimeTable tt) {
		// For routing.
		IDataService service = Simulator.Instance().getDataService().get(0);

		// Allocate trips structure.
		trips = new ArrayList<List<PublicTransportationTrip>>(7);
		tripInfo = new HashMap<String, PublicTransportationTrip>();
		
		for (int i = 0; i < 7; i++) {
			List<GTFSStopTimes> stoptimes = tt.getTripsOfDay(Day.values()[i]);
			LinkedList<PublicTransportationTrip> toAdd = new LinkedList<PublicTransportationTrip>();
			
			for (int j = 0; j < stoptimes.size(); j++) {
				// Current stop info.
				GTFSStopTimes info = stoptimes.get(j);

				// Allocate lists for times and stops and copy them.
				List<LocalTime> tripTimes = new ArrayList<LocalTime>(info.getStopIds().length);
				List<PublicTransportationStop> tripStops = new ArrayList<PublicTransportationStop>(info.getStopIds().length);
				
				for (int k = 0; k < info.getStopIds().length; k++) {
					tripStops.add(stops.get(info.getStopIds()[k]));
					tripTimes.add(LocalTime.parse(info.getDepartureTimes()[k], format));
				}
				
				// Allocate lists for traces and generate them.
				List<List<Street>> traces = new ArrayList<List<Street>>(tripStops.size() - 1);

				for (int l = 0; l < tripStops.size() - 1; l++) {
					PublicTransportationStop curr = tripStops.get(l);
					PublicTransportationStop next = tripStops.get(l + 1);
					List<Street> routing = service.getBusstopRoutingStreet(curr.getStopId(), next.getStopId());
					
					if (routing == null) routing = new ArrayList<Street>(0);
					traces.add(routing);
				}
				GTFSService serviceId = service.getServiceId(routeId, info.getTripId());
				List<GTFSServiceException> exceptions = service.getServiceExceptions(serviceId.getServiceId());
				PublicTransportationTrip t = new PublicTransportationTrip(info.getTripId(), serviceId.startDate(), serviceId.endDate(), exceptions, tripStops, tripTimes, traces);
				toAdd.addLast(t);
				
				if (!tripInfo.containsKey(t.getTripId())) tripInfo.put(t.getTripId(), t);
			}
			trips.add(toAdd);
		}
	}
	
	public String getRouteId() {
		return routeId;
	}
	
	public List<PublicTransportationTrip> getNextTrip(LocalDateTime currentTime) {
		LinkedList<PublicTransportationTrip> dayTrips = null;
		tripsToReturn.clear();
		
		switch (currentTime.getDayOfWeek()) {
			case MONDAY:
				dayTrips = (LinkedList<PublicTransportationTrip>) trips.get(0);
				break;
			case TUESDAY:
				dayTrips = (LinkedList<PublicTransportationTrip>) trips.get(1);
				break;
			case WEDNESDAY:
				dayTrips = (LinkedList<PublicTransportationTrip>) trips.get(2);
				break;
			case THURSDAY:
				dayTrips = (LinkedList<PublicTransportationTrip>) trips.get(3);
				break;
			case FRIDAY:
				dayTrips = (LinkedList<PublicTransportationTrip>) trips.get(4);
				break;
			case SATURDAY:
				dayTrips = (LinkedList<PublicTransportationTrip>) trips.get(5);
				break;
			case SUNDAY:
				dayTrips = (LinkedList<PublicTransportationTrip>) trips.get(6);
				break;
		}
		
		if (dayTrips.size() == 0) {
			return tripsToReturn;
		}

		// Get starting time of next trip.
		PublicTransportationTrip nextTrip = dayTrips.peekFirst();
		int c = 0;
		while ((nextTrip != null) && (c < dayTrips.size()) && (nextTrip.getStartingTime().getHour() == currentTime.getHour()) 
				&& (nextTrip.getStartingTime().getMinute() == currentTime.getMinute())) {
			
			if (nextTrip.isValidThisDay(currentTime.toLocalDate())) {
				tripsToReturn.add(nextTrip);
			}
			dayTrips.pollFirst();
			nextTrip = dayTrips.peekFirst();
			dayTrips.addLast(nextTrip);
			c++;
		}
		return tripsToReturn;
	}
	
	public PublicTransportationStop getStop(String stopId) {
		return stops.get(stopId);
	}
	
	public Trip getTripInformation(String tripId) {
		return tripInfo.get(tripId);
	}
}
