package allow.simulator.flow.activity.publictransportation;

import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;

import allow.simulator.ensemble.Ensemble;
import allow.simulator.ensemble.EnsembleManager;
import allow.simulator.entity.PublicTransportation;
import allow.simulator.flow.activity.Activity;
import allow.simulator.flow.activity.Learn;
import allow.simulator.mobility.data.PublicTransportationStop;
import allow.simulator.mobility.data.PublicTransportationTrip;
import allow.simulator.world.Street;

/**
 * Creates a flow of activities to execute a given trip of a means of public
 * transportation.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class PrepareTrip extends Activity {
	// Trip to execute.
	private PublicTransportationTrip trip;
	
	/**
	 * Constructor.
	 * Creates a new instance of the activity specifying the entity to execute
	 * the trip and the trip to execute.
	 * 
	 * @param entity Public transportation entity to execute the trip.
	 * @param trip Trip to execute.
	 */
	public PrepareTrip(PublicTransportation entity, PublicTransportationTrip trip) {
		super(Activity.Type.PREPARE_TRIP, entity);
		this.trip = trip;
	}

	/**
	 * Creates a sequence of activities to execute the given trip and adds
	 * it to the flow of the given transportation entity.
	 * 
	 * @param deltaT Time interval
	 */
	@Override
	public double execute(double deltaT) {
		// Get entity.
		PublicTransportation p = (PublicTransportation) entity;
		
		// Check trip.
		List<PublicTransportationStop> tripStops = trip.getStops();
		List<LocalTime> tripStopTimes = trip.getStopTimes();
		
		if ((tripStops.size() != tripStopTimes.size()) || tripStops.size() == 0
				|| (trip.getTraces().size() != (tripStops.size() - 1))) {
			throw new IllegalStateException("Error: Trip is inconsistent. Number of stops: " 
				+ tripStops.size() + ", number of times: " 
				+ tripStopTimes.size() + ", number of traces: "
				+ trip.getTraces().size());
		}
		// Prepare trip by creating a PickUpAndWait activity for each stop and
		// a DriveToNextStop for each trace, and finally set transport trip.
		Iterator<PublicTransportationStop> stopIterator = trip.getStops().iterator();
		Iterator<LocalTime> timesIterator = trip.getStopTimes().iterator();
		Iterator<List<Street>> tracesIterator = trip.getTraces().iterator();

		// Set transportation to first stop.
		p.getFlow().addActivity(new PickUpAndWait(p, stopIterator.next(), timesIterator.next()));
		
		while (stopIterator.hasNext()) {
			// Add activity to drive to next stop.
			p.getFlow().addActivity(new DriveToNextStop(p, tracesIterator.next()));
			
			// Add activity to wait and pick up passengers at next stop.
			p.getFlow().addActivity(new PickUpAndWait(p, stopIterator.next(), timesIterator.next()));
		}
		// Add return activity.
		p.getFlow().addActivity(new ReturnToAgency(p));
		p.getFlow().addActivity(new Learn(p));
		
		// Set trip.
		p.setCurrentTrip(trip);
		p.setCurrentDelay(0);
		
		// Prepare ensemble structure.
		EnsembleManager ensembles = p.getContext().getEnsembleManager();
		Ensemble transport = ensembles.getEnsemble("TransportAgency" + p.getTransportationAgency().getAgencyId() + "Ensemble");
		transport.join(p);
		ensembles.createEnsemble(trip.getTripId(), p);
		
		setFinished();
		return 0;
	}
}
