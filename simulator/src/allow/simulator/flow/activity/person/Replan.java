package allow.simulator.flow.activity.person;

import allow.simulator.entity.Person;
import allow.simulator.flow.activity.Activity;

public class Replan extends Activity {

	public Replan(Person p) {
		super(Activity.Type.REPLAN, p);
	}

	@Override
	public double execute(double deltaT) {
		Person p = (Person) entity;
		
		// Clear flow of activities and knowledge.
		p.getFlow().clear();
		p.getKnowledge().clear();
		
		// Add new planning activity.
		p.getFlow().addActivity(new PlanJourney(p, p.getPosition(), p.getCurrentItinerary().to));
		p.setCurrentItinerary(null);
		p.setReplanning(true);
		setFinished();
		return 0.0;
	}
}
