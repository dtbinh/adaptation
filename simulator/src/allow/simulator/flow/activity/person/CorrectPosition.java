package allow.simulator.flow.activity.person;

import allow.simulator.entity.Person;
import allow.simulator.flow.activity.Activity;
import allow.simulator.util.Coordinate;

public class CorrectPosition extends Activity {

	private Coordinate destination;
	
	public CorrectPosition(Person entity, Coordinate destination) {
		super(Activity.Type.CORRECT_POSITION, entity);
		this.destination = destination;
	}

	@Override
	public double execute(double deltaT) {
		Person person = (Person) entity;
		person.setPosition(destination);

		if (person.hasUsedCar() && person.isAtHome())
			person.setUsedCar(false);
		
		setFinished();
		return 0.0;
	}
}
