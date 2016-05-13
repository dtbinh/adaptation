package allow.simulator.flow.activity.taxi;

import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;

import allow.simulator.ensemble.Ensemble;
import allow.simulator.ensemble.EnsembleManager;
import allow.simulator.entity.Taxi;
import allow.simulator.flow.activity.Activity;
import allow.simulator.flow.activity.Learn;
import allow.simulator.mobility.data.TaxiStop;
import allow.simulator.mobility.data.TaxiTrip;
import allow.simulator.world.Street;

public final class PrepareTaxiTrip extends Activity {

	// Trip to execute.
	private TaxiTrip trip;

	/**
	 * Creates a new instance of the activity specifying the entity
	 * to execute the trip and the trip to execute.
	 * 
	 * @param entity Taxi entity to execute the trip
	 * @param trip Trip to execute.
	 */
	public PrepareTaxiTrip(Taxi entity, TaxiTrip trip) {
		super(Activity.Type.PREPARE_TAXI_TRIP, entity);
		this.trip = trip;
	}

	/**
	 * Creates a sequence of activities to execute the given trip and adds it to
	 * the flow of the given taxi entity.
	 * 
	 * @param deltaT Time interval
	 */
	@Override
	public double execute(double deltaT) {
		// Get entity.
		Taxi taxi = (Taxi) entity;

		// Check trip.
		List<TaxiStop> tripStops = trip.getTaxiStops();
		List<LocalTime> tripStopTimes = trip.getStopTimes();

		if ((tripStops.size() != tripStopTimes.size()) || tripStops.size() == 0
				|| (trip.getTraces().size() != (tripStops.size()))) {
			throw new IllegalStateException("Error: Trip is inconsistent. Number of stops: "
							+ tripStops.size() + ", number of times: "
							+ tripStopTimes.size() + ", number of traces: "
							+ trip.getTraces().size());
		}
		// Prepare trip by creating a PickUpAndWait activity for each stop and
		// a DriveToNextStop for each trace, and finally set transport trip.
		Iterator<TaxiStop> stopIterator = trip.getTaxiStops().iterator();
		Iterator<LocalTime> timesIterator = trip.getStopTimes().iterator();
		Iterator<List<Street>> tracesIterator = trip.getTraces().iterator();

		while (stopIterator.hasNext()) {
			// Add activity to drive to next stop.
			taxi.getFlow().addActivity(new DriveToNextDestination(taxi, tracesIterator.next()));

			// Add activity to wait and pick up passengers at next stop.
			taxi.getFlow().addActivity(new PickupOrDrop(taxi, stopIterator.next(), timesIterator.next()));
		}
		// Add return activity.
		taxi.getFlow().addActivity(new ReturnToTaxiAgency(taxi));
		taxi.getFlow().addActivity(new Learn(taxi));

		// Set trip.
		taxi.setCurrentTrip(trip);
		taxi.setCurrentDelay(0);

		// Prepare ensemble structure.
		EnsembleManager ensembles = taxi.getContext().getEnsembleManager();
		Ensemble transport = ensembles.getEnsemble("TaxiAgency" + taxi.getTransportationAgency().getAgencyId() + "Ensemble");
		transport.join(taxi);
		ensembles.createEnsemble(trip.getTripId(), taxi);
		setFinished();
		return 0;
	}

}
