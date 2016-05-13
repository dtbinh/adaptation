package allow.simulator.flow.activity.person;

import java.time.LocalTime;

import allow.simulator.entity.Entity;
import allow.simulator.entity.PublicTransportation;
import allow.simulator.flow.activity.Activity;
import allow.simulator.mobility.data.PublicTransportationStop;
import allow.simulator.mobility.data.Trip;

public class UseFlexiBus extends Activity {
	// The stops to get in and out.
	private PublicTransportationStop in;
	private PublicTransportationStop out;
	private Trip trip;
		
	// The bus a person entered.
	private PublicTransportation b;
		
	// Earliest starting time of the activity.
	private LocalTime earliestStartingTime;
		
	public UseFlexiBus(Entity entity, PublicTransportationStop start, PublicTransportationStop dest, Trip trip, LocalTime departure) {
		super(Type.USE_FLEXIBUS, entity);
		this.in = start;
		this.out = dest;
		this.trip = trip;
		earliestStartingTime = departure;
	}

	@Override
	public double execute(double deltaT) {
		return 0;
	}

}
