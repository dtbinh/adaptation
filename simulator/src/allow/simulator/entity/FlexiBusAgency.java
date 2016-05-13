package allow.simulator.entity;

import allow.simulator.core.Context;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.entity.utility.Utility;
import allow.simulator.flow.activity.flexibusagency.StartNextTrips;
import allow.simulator.mobility.planner.FlexiBusPlanner;

public class FlexiBusAgency extends TransportationAgency {
	// Planner instance is required to poll trips to schedule. 
	private FlexiBusPlanner planner;
	
	public FlexiBusAgency(long id, Utility utility, Preferences prefs, Context context) {
		super(id, Type.FLEXIBUSAGENCY, utility, prefs, context);
		planner = (FlexiBusPlanner) context.getFlexiBusPlannerService();
		flow.addActivity(new StartNextTrips(this));
	}

	public FlexiBusPlanner getPlannerInstance() {
		return planner;
	}
	
	public void setPlannerInstance(FlexiBusPlanner planner) {
		this.planner = planner;
	}
	
	@Override
	public boolean isActive() {
		return false;
	}
	
	@Override
	public String toString() {
		return "[FlexiBusAgency" + id + "]";
	}
}
