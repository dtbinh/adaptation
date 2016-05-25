package allow.simulator.flow.activity.person;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import allow.simulator.entity.Person;
import allow.simulator.entity.PublicTransportationAgency;
import allow.simulator.entity.TaxiAgency;
import allow.simulator.flow.activity.Activity;
import allow.simulator.flow.activity.Learn;
import allow.simulator.mobility.data.PublicTransportationStop;
import allow.simulator.mobility.data.Route;
import allow.simulator.mobility.data.Trip;
import allow.simulator.mobility.planner.Itinerary;
import allow.simulator.mobility.planner.Leg;
import allow.simulator.world.Street;
import allow.simulator.world.StreetSegment;

/**
 * Class representing an activity to prepare a journey, i.e. given an itinerary
 * from the planner, the activity creates travel activities for the individual
 * itinerary legs.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public final class PrepareJourney extends Activity {
	// The journey to execute.
	private Itinerary journey;
	
	/**
	 * Creates a new journey planning (i.e. transform a requested journey
	 * into Activities and add them to workflow of person) Activity.
	 * 
	 * @param person The person planning the journey.
	 * @param journey The journey.
	 */
	public PrepareJourney(Person entity, Itinerary journey) {
		super(Activity.Type.PREPARE_JOURNEY, entity);
		this.journey = journey;
	}
	
	@Override
	public double execute(double deltaT) {
		// Person entity.
		Person person = (Person) entity;
		person.setCurrentItinerary(journey);
		
		if (journey.initialWaitingTime > 0) {
			person.getFlow().addActivity(new Wait(person, journey.initialWaitingTime));

			if (person.isReplanning()) {
	    		person.getContext().getStatistics().reportReplaningWaitingTime(journey.initialWaitingTime);
	    	}
		}
		person.setReplanning(false);

		// Get parts journey is composed of.
		List<Leg> legs = journey.legs;
		
		// Create a new Activity for every leg
		for (int i = 0; i < legs.size(); i++) {
			Leg l = legs.get(i);
			List<StreetSegment> segs = new ArrayList<StreetSegment>();
			
			for (Street s : l.streets) {
				segs.addAll(s.getSubSegments());
			}
			
			switch (l.mode) {
			
			case BICYCLE:
				if (l.streets.size() == 0)
					continue;

				Activity cycle = new Cycle(person, l.streets);
				entity.getFlow().addActivity(cycle);
				break;
				
			case BUS:
			case RAIL:
			case CABLE_CAR:
			case TRANSIT:
				PublicTransportationAgency ta = person.getContext().getWorld().getUrbanMobilitySystem()
					.getTransportationRepository().getGTFSTransportAgency(l.agencyId);
				Route route = ta.getRoute(l.routeId);		
				if (route == null) throw new IllegalStateException("Error: Transport " + l.routeId + " of " + l.agencyId + " is unknown.");

				Trip trip = route.getTripInformation(l.tripId);
				if (trip == null) throw new IllegalStateException("Error: Trip " + l.tripId + " of " + l.agencyId + " is unknown.");

				// Get start and destination stop.
				PublicTransportationStop in = route.getStop(l.stopIdFrom);
				if (in == null) throw new IllegalStateException("Error: Stop "+ l.stopIdFrom + " of route " + l.routeId + " is unknown.");

				PublicTransportationStop out = route.getStop(l.stopIdTo);
				if (out == null) throw new IllegalStateException("Error: Stop "+ l.stopIdTo + " of route " + l.routeId + " is unknown.");
				
				entity.getFlow().addActivity(new UsePublicTransport(person,
						in, out, l.agencyId, trip, LocalDateTime.ofInstant(Instant.ofEpochMilli(l.startTime), ZoneId.of("UTC+2")).toLocalTime()));
				break;
				
			case CAR:
				if (l.streets.size() == 0)
					continue;
				
				Activity drive = new Drive(person, l.streets, journey.isTaxiItinerary);
				entity.getFlow().addActivity(drive);
				break;
				
			case WALK:
				if (l.streets.size() == 0)
					continue;
				
				Activity walk = new Walk(person, l.streets);
				entity.getFlow().addActivity(walk);
				break;
			
			case TAXI:
				TaxiAgency ta2 = person.getContext().getWorld().getUrbanMobilitySystem()
				.getTransportationRepository().getTaxiAgency();
				
				break;
				
			default:
				throw new IllegalArgumentException("Error: Activity " + l.mode + " is not supported.");
			}
		}
		
		if (journey.itineraryType == 0 && !journey.isTaxiItinerary) {
			person.setUsedCar(true);
		}
		
		switch (journey.itineraryType) {
			case 0:
				
				if (journey.isTaxiItinerary) {
					person.getContext().getStatistics().reportTaxiJourney();
					
				} else {
					person.getContext().getStatistics().reportCarJourney();
				}
				break;
			
			case 1:
				person.getContext().getStatistics().reportTransitJourney();
				break;
				
			case 2:
				person.getContext().getStatistics().reportBikeJourney();
				break;
				
			case 3:
				person.getContext().getStatistics().reportWalkJourney();
				break;
		}
		entity.getFlow().addActivity(new CorrectPosition(person, journey.to));
		entity.getFlow().addActivity(new Learn(person));
		setFinished();
		return 0;
	}
	
	public String toString() {
		return "PrepareJourney " + entity;
	}
}
