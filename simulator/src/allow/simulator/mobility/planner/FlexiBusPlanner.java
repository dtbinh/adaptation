package allow.simulator.mobility.planner;

import java.time.LocalDateTime;
import java.util.List;

import allow.simulator.entity.Person;
import allow.simulator.mobility.data.Trip;
import allow.simulator.util.Coordinate;

public class FlexiBusPlanner implements IPlannerService {

	
//	@Override
//	public List<Itinerary> requestSingleJourney(JourneyRequest request) {
//		return null;
//	}
	
	@Override
	public boolean requestSingleJourney(JourneyRequest request, List<Itinerary> itineraries) {
		return false;
	}
	
	/**
	 * Registers a person to the FlexiBus planner.
	 * 
	 * @param p Person registering to the FlexiBus planner.
	 * @param request Journey request of person p.
	 */
	public void register(Person p, Coordinate start, Coordinate dest, LocalDateTime startingTime) {
		// Here the persons can be registered to the planner.
	}
	
	/**
	 * Unregisters a person from the FlexiBus planner.
	 * 
	 * @param p Person to unregister from the planner.
	 */
	public void unregister(Person p) {
		// Persons are unregistered from the planner 
	}
	
	
	public void reschedule() {
		// Leads to a rescheduling of possible trips.
		// Called during every time step or whenever a person registers?
	}
	
	/**
	 * Polls the trips which are ready to depart.
	 * 
	 * @return List of trips which are ready to depart. List can be empty.
	 */
	public List<Trip> getTripsToDepart() {
		return null;
	}
}
