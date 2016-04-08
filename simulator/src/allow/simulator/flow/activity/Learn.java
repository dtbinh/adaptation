package allow.simulator.flow.activity;

import allow.simulator.entity.Entity;

/**
 * Represents an activity to execute learning.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class Learn extends Activity {
	
	public Learn(Entity entity) {
		super(Activity.Type.LEARN, entity);
	}

	@Override
	public double execute(double deltaT) {
		entity.getKnowledge().learn();
		setFinished();
		return 0.0;
	}
}
