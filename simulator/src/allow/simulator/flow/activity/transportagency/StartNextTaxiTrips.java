package allow.simulator.flow.activity.transportagency;

import java.util.List;

import allow.simulator.entity.Taxi;
import allow.simulator.entity.TaxiAgency;
import allow.simulator.flow.activity.Activity;
import allow.simulator.flow.activity.taxi.PrepareTaxiTrip;
import allow.simulator.mobility.data.TaxiTrip;

public class StartNextTaxiTrips extends Activity {
	
	public StartNextTaxiTrips(TaxiAgency agency) {
		super(Activity.Type.SCHEDULE_NEXT_TAXI_TRIPS, agency);
	}

	@Override
	public double execute(double deltaT) {
		// Agency entity.
		TaxiAgency agency = (TaxiAgency) entity;
		
		// Get next trips from agency.
		List<TaxiTrip> nextTrips = agency.getTripsToSchedule();

		// Schedule a new bus for each trip.
		for (TaxiTrip trip : nextTrips) {
			// Get next free transportation vehicle.
			Taxi t = agency.scheduleTrip(trip);
			
			// Assign new trip to vehicle.
			t.getFlow().addActivity(new PrepareTaxiTrip(t, trip));
		}
		// Activity is never finished.
		return deltaT;
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
