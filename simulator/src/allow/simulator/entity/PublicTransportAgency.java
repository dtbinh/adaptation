package allow.simulator.entity;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.entity.utility.Utility;
import allow.simulator.flow.activity.transportagency.StartNextTrips;

public class PublicTransportAgency extends TransportAgency {

	public PublicTransportAgency(long id, Utility utility, Preferences prefs, Context context) {
		super(id, Type.PUBLICTRANSPORTAGENCY, utility, prefs, context);
		
		// Start scheduling next trips.
		flow.addActivity(new StartNextTrips(this));
	}

}
