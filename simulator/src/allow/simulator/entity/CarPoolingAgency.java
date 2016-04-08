package allow.simulator.entity;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.entity.utility.Utility;

public class CarPoolingAgency extends TransportAgency {

	public CarPoolingAgency(long id, Utility utility, Preferences prefs, Context context) {
		super(id, Entity.Type.CARPOOLINGAGENCY, utility, prefs, context);
	}

	@Override
	public boolean isActive() {
		return false;
	}

	public String toString() {
		return "[CarPoolingAgency" + id + "]";
	}
}
