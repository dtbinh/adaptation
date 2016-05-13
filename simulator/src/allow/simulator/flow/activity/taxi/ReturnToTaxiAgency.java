package allow.simulator.flow.activity.taxi;

import allow.simulator.ensemble.Ensemble;
import allow.simulator.ensemble.EnsembleManager;
import allow.simulator.entity.Person;
import allow.simulator.entity.Taxi;
import allow.simulator.flow.activity.Activity;

public class ReturnToTaxiAgency extends Activity {
	
	public ReturnToTaxiAgency(Taxi taxi) {
		// Constructor of super class.
		super(Activity.Type.RETURN_TO_TAXI_AGENCY, taxi);
	}

	@Override
	public double execute(double deltaT) {
		// Note tStart.
		if (tStart == -1) {
			tStart = entity.getContext().getTime().getTimestamp();
		}

		// Transportation entity.
		Taxi taxi = (Taxi) entity;
		
		if (taxi.getPassengers().size() > 0) {
			System.out.println("Warning: Passengers still on taxi "
							+ taxi.toString()
							+ " of trip "
							+ taxi.getCurrentTrip().getTripId());

			for (int i = 0; i < taxi.getPassengers().size(); i++) {
				Person pers = taxi.getPassengers().get(i);
				System.out.println("  " + pers.toString() + " " + pers.getFlow().getCurrentActivity().toString());
			}
		}
		// Finish trip at agency.
		taxi.getTransportationAgency().finishTrip(taxi.getCurrentTrip().getTripId(), taxi);

		// Fix ensemble structure.
		EnsembleManager ensembles = taxi.getContext().getEnsembleManager();
		Ensemble transport = ensembles.getEnsemble("TaxiAgency" + taxi.getTransportationAgency().getAgencyId() + "Ensemble");
		transport.leave(taxi);
		ensembles.destroyEnsemble(taxi.getCurrentTrip().getTripId());

		// Reset state and return to agency.
		taxi.setCurrentStop(null);
		taxi.setCurrentTrip(null);
		setFinished();
		return deltaT;
	}
}