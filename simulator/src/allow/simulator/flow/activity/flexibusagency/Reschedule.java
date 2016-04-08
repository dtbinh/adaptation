package allow.simulator.flow.activity.flexibusagency;

import allow.simulator.entity.FlexiBusAgency;
import allow.simulator.flow.activity.Activity;

public class Reschedule extends Activity {

	public Reschedule(FlexiBusAgency agency) {
		super(Activity.Type.SCHEDULE_NEXT_FLEXIBUS_TRIPS, agency);
	}

	@Override
	public double execute(double deltaT) {
		
		return deltaT;
	}

}
