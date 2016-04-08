package allow.simulator.flow.activity.person;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import allow.simulator.entity.Person;
import allow.simulator.flow.activity.Activity;
import allow.simulator.util.Coordinate;

public final class RegisterToFlexiBus extends Activity {
	// Starting point of the journey.
	private Coordinate start;
		
	// Destination of the journey.
	private Coordinate destination;
		
	// Earliest time a person is willing to start her journey. 
	private LocalTime earliestStartingTime;
	
	/**
	 * Creates new instance of an Activity to register to the UMS journey
	 * planning services.
	 * 
	 * @param person Person to register.
	 */
	public RegisterToFlexiBus(Person entity, Coordinate start, Coordinate dest, LocalTime earliestStartingTime) {
		super(Activity.Type.REGISTER_TO_FLEXIBUS, entity);
		this.start = start;
		this.destination = dest;
		this.earliestStartingTime = earliestStartingTime;
	}

	@Override
	public double execute(double deltaT) {
		Person p = (Person) entity;
		LocalDate d = p.getContext().getTime().getCurrentDateTime().toLocalDate();
		p.getContext().getWorld().getUrbanMobilitySystem().register(p, start, destination, LocalDateTime.of(d, earliestStartingTime));
		setFinished();
		return deltaT;
	}
	
}
