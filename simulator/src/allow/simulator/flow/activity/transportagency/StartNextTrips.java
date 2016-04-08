package allow.simulator.flow.activity.transportagency;

import java.util.List;

import allow.simulator.entity.PublicTransportation;
import allow.simulator.entity.TransportAgency;
import allow.simulator.flow.activity.Activity;
import allow.simulator.flow.activity.publictransportation.PrepareTrip;
import allow.simulator.mobility.data.Trip;

public class StartNextTrips extends Activity {

	public StartNextTrips(TransportAgency agency) {
		super(Activity.Type.SCHEDULE_NEXT_TRIPS, agency);
	}

	@Override
	public double execute(double deltaT) {
		// Agency entity.
		TransportAgency agency = (TransportAgency) entity;
		
		// Get next trips from agency.
		List<Trip> nextTrips = agency.getTripsToSchedule(agency.getContext().getTime().getCurrentDateTime());

		// Schedule a new bus for each trip.
		for (Trip t : nextTrips) {
			// Get next free transportation vehicle.
			PublicTransportation b = agency.scheduleTrip(t);
			
			// Assign new trip to vehicle.
			b.getFlow().addActivity(new PrepareTrip(b, t));
		}
		// Activity is never finished.
		return deltaT;
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}