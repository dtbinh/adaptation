package allow.simulator.flow.activity.person;

import allow.simulator.entity.Person;
import allow.simulator.flow.activity.Activity;

public class Wait extends Activity {

	private double waitingTime;
	
	public Wait(Person person, double waitingTime) {
		super(Activity.Type.WAIT, person);
		this.waitingTime = waitingTime;
	}

	@Override
	public double execute(double deltaT) {
	    double diff = waitingTime - deltaT;
	    
	    if (diff > 0) {
	    	waitingTime -= diff;
	    	return deltaT;
	    } else {
	    	setFinished();
	    	return Math.abs(diff);
	    }
	}
}
