package allow.simulator.netlogo.agent;

import java.time.LocalTime;
import java.util.EnumMap;
import java.util.Observable;
import java.util.Observer;

import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.api.AgentException;

import allow.simulator.entity.Entity;
import allow.simulator.entity.Person;
import allow.simulator.entity.PlanGenerator;
import allow.simulator.flow.activity.Activity;
import allow.simulator.flow.activity.Activity.Type;
import allow.simulator.util.Coordinate;
import allow.simulator.util.Pair;

/**
 * Wrapper class to add person state information to NetLogo Person agents.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class PersonAgent extends Turtle implements Observer, IAgent {
	// Actual person entity.
	private Person pImpl;
	
	// Reused buffer to convert NetLogo to GIS coordinates. 
	private Coordinate temp;

	// Shape lookup.
	private static final EnumMap<Activity.Type, String> shapes;
	
	static {
		shapes = new EnumMap<Activity.Type, String>(Activity.Type.class);
		shapes.put(Type.CORRECT_POSITION, "person");
		shapes.put(Type.PREPARE_JOURNEY, "person");
		shapes.put(Type.PLAN_JOURNEY, "person");
		shapes.put(Type.CYCLE, "bike");
		shapes.put(Type.DRIVE, "car side");
		shapes.put(Type.FILTER_ALTERNATIVES, "person");
		shapes.put(Type.PREPARE_JOURNEY, "person");
		shapes.put(Type.USE_PUBLIC_TRANSPORT, "person");
		shapes.put(Type.WALK, "person");
		shapes.put(Type.RANK_ALTERNATIVES, "person");
		shapes.put(Type.LEARN, "person");
		shapes.put(Type.REGISTER_TO_FLEXIBUS, "person");
		shapes.put(Type.USE_FLEXIBUS, "person");
		shapes.put(Type.REPLAN, "person");
		shapes.put(Type.WAIT, "person");
	}
	
	/**
	 * Constructor.
	 * Create new instance of NetLogo agent modeling a person traveling in the simulator.
	 * 
	 * @param world The NetLogo world to add the agent.
	 * @param start The starting position 
	 * @throws AgentException
	 */
	public PersonAgent(World world, Person p) throws AgentException {
		super(world, world.getBreed("PERSONS"), 0.0, 0.0);
		temp = new Coordinate();
		pImpl = p;
		pImpl.addObserver(this);
		shape("person");
		size(1.0);
		hidden(true);
		colorDouble((double) Math.random() * 149.0);
				
		// Define appearance and shape.
		Coordinate netlogo = pImpl.getContext().getWorld().getTransformation().GISToNetLogo(pImpl.getPosition());
				
		if ((netlogo.x > world().minPxcor()) && (netlogo.x < world().maxPxcor()) && (netlogo.y > world().minPycor() && (netlogo.y < world().maxPycor()))) {
			xandycor(netlogo.x, netlogo.y);
		}
	}
	
	public void createDailySchedule() {
		PlanGenerator.generateDayPlan(pImpl);
	}
	
	public boolean execute() throws AgentException {
		Pair<LocalTime, Activity> next = pImpl.getScheduleQueue().peek();
		
		if (pImpl.getFlow().isIdle() && (next != null)) {
			LocalTime c = pImpl.getContext().getTime().getCurrentTime();

			if (next.first.compareTo(c) <= 0) {
				pImpl.getFlow().addActivity(next.second);
				pImpl.getScheduleQueue().poll();
			}
		}
		return executeActivity();
	}
	
	public boolean executeActivity() throws AgentException {
		
		if (pImpl.getFlow().isIdle()) {
			hidden(true);
			return false;
		}
		Activity.Type executedActivity = pImpl.getFlow().getCurrentActivity().getType();
		String s = shapes.get(executedActivity);
		pImpl.getFlow().executeActivity(pImpl.getContext().getTime().getDeltaT());

		// Update shape if necessary.
		if (!s.equals(shape())) shape(s);	
		if (hidden()) hidden(false);
		return true;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		Person p = (Person) arg0;
		p.getContext().getWorld().getTransformation().GISToNetLogo(p.getPosition(), temp);
				
		try {
			xandycor(temp.x, temp.y);
		} catch (AgentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Entity getEntity() {
		return pImpl;
	}

	@Override
	public void exchangeKnowledge() {
		pImpl.exchangeKnowledge();
	}
}
