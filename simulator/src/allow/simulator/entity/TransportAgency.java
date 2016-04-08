package allow.simulator.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.entity.utility.Utility;
import allow.simulator.mobility.data.Route;
import allow.simulator.mobility.data.Trip;

/**
 * Represents a transport agency entity managing a set of trips and vehicles
 * to execute them. 
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public abstract class TransportAgency extends Entity {
	// Id of the agency.
	protected String agencyId;
	
	// The routes of this transport agency.
	protected Map<String, Route> routes;
	
	// Buffer for next trips to schedule.
	protected List<Trip> nextTrips;
	
	// List of transport entities used by this agency.
	protected Queue<PublicTransportation> vehicles;
	
	// "Live" information about current trips and vehicles executing trips.
	protected Map<String, PublicTransportation> currentlyUsedVehicles;
	
	/**
	 * Constructor.
	 * Creates new instance of a transport agency.
	 * 
	 * @param id Id of this transport agency.
	 * @param utility Utility function.
	 * @param context Context of this transport agency.
	 */
	protected TransportAgency(long id, Type type, Utility utility, Preferences prefs, Context context) {
		super(id, type, utility, prefs, context);
		agencyId = "";
		routes = new HashMap<String, Route>();
		nextTrips = new LinkedList<Trip>();
		
		// Create bus repository of this agency.
		vehicles = new ConcurrentLinkedQueue<PublicTransportation>();
		
		// "Live" information about trips and vehicles executing trips.
		currentlyUsedVehicles = new HashMap<String, PublicTransportation>();
	}
	
	/**
	 * Adds a new route to the agency.
	 * 
	 * @param newRoute New route to be served by the agency.
	 */
	public void addRoute(Route newRoute) {
		routes.put(newRoute.getRouteId(), newRoute);
	}
	
	/**
	 * Returns Id of this agency.
	 * 
	 * @return Id of this agency.
	 */
	public String getAgencyId() {
		return agencyId;
	}
	
	/**
	 * Sets the Id of this agency.
	 * 
	 * @param agencyId Id to set.
	 */
	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}
	
	/**
	 * Returns the route having the specified Id.
	 * 
	 * @param routeId Id of the route.
	 * @return Route having the Id routeId or null of there is no such route.
	 */
	public Route getRoute(String routeId) {
		return routes.get(routeId);
	}
	
	public List<Trip> getTripsToSchedule(LocalDateTime currentTime) {
		// Clear list of next trips.
		nextTrips.clear();

		// Create list of next trips to start.
		for (Route route : routes.values()) {
			// Get next trip for every route.
			List<Trip> t = route.getNextTrip(currentTime);
			
			// If trip is not null (i.e. route has a trip starting at current time), add it to the list.
			if (t != null) {
				nextTrips.addAll(t);
			}
		}
		return nextTrips;
	}
	
	public PublicTransportation scheduleTrip(Trip trip) {
		// Poll next vehicle.
		PublicTransportation vehicle = vehicles.poll();
		
		if (vehicle == null) throw new IllegalStateException("Error: No vehicle left to schedule trip " + trip.getTripId());
		currentlyUsedVehicles.put(trip.getTripId(), vehicle);
		return vehicle;
	}
	
	public void finishTrip(Trip trip, PublicTransportation vehicle) {
		currentlyUsedVehicles.remove(trip.getTripId());
		vehicles.add(vehicle);
	}
	
	public PublicTransportation getVehicleOfTrip(String tripId) {
		return currentlyUsedVehicles.get(tripId);
	}
	
	public void addVehicle(PublicTransportation b) {
		vehicles.add(b);
	}
	
	@Override
	public void exchangeKnowledge() { }
	
	public boolean isActive() {
		return false;
	}
	
	public String toString() {
		return "[TransportAgency" + id + "]";
	}
}
