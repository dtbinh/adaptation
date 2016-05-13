package allow.simulator.flow.activity.person;

import java.time.LocalTime;
import java.util.List;

import allow.simulator.entity.Person;
import allow.simulator.entity.PublicTransportation;
import allow.simulator.entity.relation.Relation;
import allow.simulator.flow.activity.Activity;
import allow.simulator.mobility.data.PublicTransportationStop;
import allow.simulator.mobility.data.TransportationRepository;
import allow.simulator.mobility.data.Trip;

/**
 * Represents an activity to use public transport (bus).
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class UsePublicTransport extends Activity {
	// The stops to get in and out.
	private PublicTransportationStop in;
	private PublicTransportationStop out;
	private String agencyId;
	private Trip trip;
	
	// The bus a person entered.
	private PublicTransportation b;
	
	// Earliest starting time of the activity.
	private LocalTime earliestStartingTime;
	
	// Utility state variables.
	private boolean reachedStop;
	private boolean enteredBus;
	private boolean leftBus;
	
	/**
	 * Constructor.
	 * Creates new activity to use a bus given start and end stop.
	 * 
	 * @param person Person to execute the activity. 
	 * @param start Starting stop.
	 * @param dest Destination stop.
	 * @param departure Time when public transportation departs from stop
	 *        according to schedule. 
	 */
	public UsePublicTransport(Person person, PublicTransportationStop start, PublicTransportationStop dest, String agencyId, Trip trip, LocalTime departure) {
		super(Activity.Type.USE_PUBLIC_TRANSPORT, person);
		earliestStartingTime = departure;
		reachedStop = false;
		enteredBus = false;
		this.agencyId = agencyId;
		leftBus = false;
		this.trip = trip;
		in = start;
		out = dest;
	}

	/**
	 * Executes one step of this activity.
	 */
	@Override
	public double execute(double deltaT) {
		// Register relations update.
		entity.getRelations().addToUpdate(Relation.Type.DISTANCE);

		// Get entity.
		Person person = (Person) entity;
		
		if (!reachedStop) {
			// If person has not reached stop yet, set position, add person to waiting passengers, and set flag.
			person.setPosition(in.getPosition());
			in.addWaitingPerson(person);
			reachedStop = true;
			return 0.0;
			
		} else if (!enteredBus) {
			// Try to get transportation mean.
			if (b == null) {
				b = TransportationRepository.Instance().getGTFSTransportAgency(agencyId).getVehicleOfTrip(trip.getTripId());
			}
			
			// Reaching a stop needs zero time.
			if (b != null && person.getContext().getTime().getCurrentTime().isAfter(earliestStartingTime.plusSeconds(b.getCurrentDelay() + 300))) {
				person.getFlow().clear();
				person.getFlow().addActivity(new Replan(person));
				setFinished();
				return 0.0;
				
			} else if (person.getContext().getTime().getCurrentTime().isAfter(earliestStartingTime.plusSeconds(1800))) {
				// Dirty error handling for missed busses
				person.getFlow().clear();
				person.getKnowledge().clear();
				person.setPosition(person.getCurrentItinerary().to);
				person.setCurrentItinerary(null);
				
				/*person.getFlow().clear();
				person.getFlow().addActivity(new Replan(person));*/
				setFinished();
				return 0.0;
				
			}
			
			// If person has not entered the correct means yet, check in stop for waiting vehicles.
			if (in.hasWaitingPublicTransportationEntities()) {
				List<PublicTransportation> waiting = in.getPublicTransportationEntities();

				for (int i = 0; i < waiting.size(); i++) {
					PublicTransportation transport = waiting.get(i);
					
					if (transport.getCurrentTrip().getTripId().equals(trip.getTripId())) {
						boolean success = transport.addPassenger(person);
						
						if (success) {
							in.removeWaitingPerson(person);
							enteredBus = true;
							b = transport;
							break;
							
						} else {
							person.getFlow().clear();
							person.getFlow().addActivity(new Replan(person));
						}
					}
				}
			}
			return deltaT;

		} else if (enteredBus && !leftBus){
			// If on bus, trigger knowledge exchange.
			//entity.getRelations().addToUpdate(Relation.Type.BUS);
			
			// If person entered bus, update position.
			if ((b.getCurrentStop() != null)) {
				if (b.getCurrentStop().getStopId().equals(out.getStopId())) {
					b.removePassenger(person);
					leftBus = true;
					setFinished();
				}
			}
		}
		return deltaT;
	}

	public PublicTransportation getMeansOfTransportation() {
		return b;
	}
	
	public String toString() {
		return "UsePublicTransport " + entity.toString() + " from " + in.getStopId() + " to " + out.getStopId() + " trip " + trip.getTripId() + " " + enteredBus + " " + leftBus + "earliest starting: " + earliestStartingTime.toString(); 
	}
}
