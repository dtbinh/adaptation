package allow.simulator.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.IUtility;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.flow.activity.transportagency.StartNextTaxiTrips;
import allow.simulator.mobility.data.TaxiTrip;
import allow.simulator.mobility.planner.TaxiPlanner;
import allow.simulator.util.Coordinate;

public final class TaxiAgency extends TransportationAgency {
	// Trips which have been requested by persons and need to be scheduled
	private final List<TaxiTrip> tripsToSchedule;
	
	// Collection of trips which have been requested and are being executed
	private final Map<String, TaxiTrip> currentTrips;
	
	public TaxiAgency(long id, IUtility utility, Preferences prefs, Context context) {
		super(id, Type.TAXIAGENCY, utility, prefs, context);
		position = new Coordinate(11.119714, 46.071988);
		tripsToSchedule = new ArrayList<TaxiTrip>();
		currentTrips = new HashMap<String, TaxiTrip>();
		
		// Start scheduling next trips
		flow.addActivity(new StartNextTaxiTrips(this));
	}

	@Override
	public boolean isActive() {
		return false;
	}

	public List<TaxiTrip> getTripsToSchedule() {
		List<TaxiTrip> ret = new ArrayList<TaxiTrip>(tripsToSchedule);
		tripsToSchedule.clear();
		return ret;
	}
	
	public Taxi scheduleTrip(TaxiTrip taxiTrip) {
		// Poll next free transportation entity
		Taxi taxi = (Taxi) vehicles.poll();
		
		if (taxi == null)
			throw new IllegalStateException("Error: No taxi left to schedule trip " + taxiTrip.getTripId());
		currentlyUsedVehicles.put(taxiTrip.getTripId(), taxi);
		return taxi;
	}
	
	public void finishTrip(String tripId, Taxi taxi) {
		currentlyUsedVehicles.remove(tripId);
		currentTrips.remove(tripId);
		vehicles.add(taxi);
	}
	
	public void call(String tripId) {
		TaxiPlanner service = context.getTaxiPlannerService();
		TaxiTrip trip = service.getTaxiTrip(tripId);
		tripsToSchedule.add(trip);
		currentTrips.put(tripId, trip);
	}
	
	public void cancel(String tripId) {
		TaxiPlanner service = context.getTaxiPlannerService();
		service.getTaxiTrip(tripId);
	}
	
	public TaxiTrip getTripInformation(String tripId) {
		return currentTrips.get(tripId);
	}
	
	/**
	 * Adds a taxi entity to the agency
	 * 
	 * @param transportation Taxi entity to be added to the agency
	 */
	public void addTaxi(Taxi transportation) {
		vehicles.add(transportation);
	}
	
	@Override
	public String toString() {
		return "[Taxi" + id + "]";
	}

	
}
