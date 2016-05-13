package allow.simulator.flow.activity.person;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import allow.simulator.entity.Person;
import allow.simulator.entity.Person.Profile;
import allow.simulator.flow.activity.Activity;
import allow.simulator.mobility.data.TType;
import allow.simulator.mobility.planner.Itinerary;
import allow.simulator.mobility.planner.JourneyRequest;
import allow.simulator.mobility.planner.RequestId;
import allow.simulator.util.Coordinate;
import allow.simulator.util.Geometry;

/**
 * Class representing an Activity to request a journey.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public final class PlanJourney extends Activity {
	// Predefined set of means of transportation to be used in a journey.	
	private static final TType transitJourney[] = new TType[] { TType.TRANSIT, TType.WALK };
	private static final TType walkJourney[] = new TType[] { TType.WALK };
	private static final TType carJourney[] = new TType[] { TType.CAR, TType.WALK };

	// The start coordinate of the journey.
	private Coordinate start;
	
	// The destination of the journey.
	private Coordinate destination;
	private boolean requestSent;
	
	/**
	 * Creates a new Activity to request a journey.
	 * 
	 * @param person The person executing the journey request.
	 */
	public PlanJourney(Person person, Coordinate start, Coordinate destination) {
		super(Activity.Type.PLAN_JOURNEY, person);
		this.start = start;
		this.destination = destination;
		requestSent = false;
	}
			
	@Override
	public double execute(double deltaT) {
		// Register for knowledge exchange.
		// entity.getRelations().addToUpdate(Relation.Type.DISTANCE);
				
		// Person entity.
		Person person = (Person) entity;
		
		// Update preferences.
		double dist = Geometry.haversine(start, destination);
		person.getPreferences().setTmax(1500);
		person.getPreferences().setCmax(2.5);
		person.getPreferences().setWmax(Math.min(dist, 1000));
		
		if (!requestSent) {
			RequestId reqId = new RequestId();
			List<JourneyRequest> requests = new ArrayList<JourneyRequest>(4);
			LocalDateTime date = person.getContext().getTime().getCurrentDateTime();
			
			// Car requests are now sent out in any case. If a person does not
			// own a private car or person left the car at home, a taxi request
			// is emulated.
			if (person.getProfile() != Profile.CHILD) {
				requests.add(JourneyRequest.createRequest(start, destination, date, false, carJourney, person, reqId));
			}
			
			if (!person.hasUsedCar()) {
				requests.add(JourneyRequest.createRequest(start, destination, date, false, transitJourney, person, reqId));
				requests.add(JourneyRequest.createRequest(start, destination, date, false, walkJourney, person, reqId));
			
				// if (person.hasBike())
				//	requests.add(createRequest(start, destination, date, time, bikeJourney, person, reqId, reqNumber++));
			
				// if (person.useFlexiBus())
				//	requests.add(createRequest(start, destination, date, time, flexiBusJourney, person, reqId, reqNumber++));
			}
			person.getContext().getWorld().getUrbanMobilitySystem().addRequests(requests, person.getRequestBuffer());
			requestSent = true;
			return deltaT;
			
		} else if (!person.getRequestBuffer().processed) {
			return deltaT;
				
		} else if (person.getRequestBuffer().buffer.size() == 0) {
			// In case no trips were found, reset buffer.
			setFinished();
			person.setPosition(destination);
			return 0.0;
				
		} else {
			// In case FlexiBus was queried, unregister now.
			if (person.useFlexiBus()) {
				person.getContext().getWorld().getUrbanMobilitySystem().unregister(person);
			}
			
			// In case response was received, rank alternatives.
			person.getFlow().addActivity(new FilterAlternatives(person, new ArrayList<Itinerary>(person.getRequestBuffer().buffer)));
			person.getRequestBuffer().buffer.clear();
			setFinished();
			return 0.0;
		}
	}
	
	@Override
	public String toString() {
		return "[PlanJourney " + entity + "]";
	}
}