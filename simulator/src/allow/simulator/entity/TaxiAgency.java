package allow.simulator.entity;

import java.util.ArrayList;
import java.util.List;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.IUtility;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.mobility.data.TaxiTrip;
import allow.simulator.mobility.planner.TaxiPlanner;
import allow.simulator.util.Coordinate;

public final class TaxiAgency extends TransportationAgency {

	private List<TaxiTrip> tripsToSchedule;
	
	public TaxiAgency(long id, IUtility utility, Preferences prefs, Context context) {
		super(id, Type.TAXIAGENCY, utility, prefs, context);
		position = new Coordinate(11.119714, 46.071988);
		tripsToSchedule = new ArrayList<TaxiTrip>();
	}

	@Override
	public boolean isActive() {
		return false;
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
		vehicles.add(taxi);
	}
	
	public List<TaxiTrip> getTripsToSchedule() {
		List<TaxiTrip> ret = new ArrayList<TaxiTrip>(tripsToSchedule);
		tripsToSchedule.clear();
		return ret;
	}
	
	public void call(String tripId) {
		TaxiPlanner service = context.getTaxiPlannerService();
		TaxiTrip trip = service.getTaxiTrip(tripId);
		tripsToSchedule.add(trip);
	}
	
	public void cancel(String tripId) {
		TaxiPlanner service = context.getTaxiPlannerService();
		service.getTaxiTrip(tripId);
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
