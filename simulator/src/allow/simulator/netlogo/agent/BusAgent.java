package allow.simulator.netlogo.agent;

import java.util.EnumMap;
import java.util.Observable;
import java.util.Observer;

import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.api.AgentException;

import allow.simulator.entity.Bus;
import allow.simulator.entity.Entity;
import allow.simulator.flow.activity.Activity;
import allow.simulator.flow.activity.Activity.Type;
import allow.simulator.util.Coordinate;

/**
 * Wrapper class to add bus state information to corresponding NetLogo agents.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class BusAgent extends Turtle implements Observer, IAgent {
	// Actual bus agent.
	private Bus bImpl;
	
	// Buffer for coordinate transformation.
	private Coordinate temp;
	
	// Shape lookup.
	private static final EnumMap<Activity.Type, String> shapes;
	
	static {
		shapes = new EnumMap<Activity.Type, String>(Activity.Type.class);
		shapes.put(Type.PREPARE_TRIP, "bus");
		shapes.put(Type.DRIVE_TO_NEXT_STOP, "bus");
		shapes.put(Type.PICKUP_AND_WAIT, "bus");
		shapes.put(Type.RETURN_TO_AGENCY, "bus");
		shapes.put(Type.LEARN, "person");
	}
	
	public BusAgent(World world, Bus b) throws AgentException {
		super(world, world.getBreed("BUSSES"), 0.0, 0.0);
		temp = new Coordinate();
		bImpl = b;
		bImpl.addObserver(this);
		hidden(true);
		shape("bus");
		size(1.0);
		
		Coordinate netlogo = bImpl.getContext().getWorld().getTransformation().GISToNetLogo(bImpl.getPosition());
				
		if ((netlogo.x > world().minPxcor()) && (netlogo.x < world().maxPxcor()) && (netlogo.y > world().minPycor() && (netlogo.y < world().maxPycor()))) {
			xandycor(netlogo.x, netlogo.y);
		}
	}
	
	@Override
	public boolean execute() throws AgentException {
		
		if (bImpl.getFlow().isIdle()) {
			hidden(true);
			return false;
		}
		Activity.Type executedActivity = bImpl.getFlow().getCurrentActivity().getType();
		String s = shapes.get(executedActivity);
		bImpl.getFlow().executeActivity(bImpl.getContext().getTime().getDeltaT());
		
		// Update shape if necessary.
		if (!s.equals(shape())) shape(s);
		if (hidden()) hidden(false);
		return true;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		Bus b = (Bus) o;
		
		// Update x and y coordinates.
		b.getContext().getWorld().getTransformation().GISToNetLogo(b.getPosition(), temp);
		
		if ((temp.x > world().minPxcor()) && (temp.x < world().maxPxcor()) && (temp.y > world().minPycor() && (temp.y < world().maxPycor()))) {
			try {
				xandycor(temp.x, temp.y);
			} catch (AgentException e) {
				e.printStackTrace();
			}
			hidden(false);
		} else {
			hidden(true);
		}
	}

	@Override
	public Entity getEntity() {
		return bImpl;
	}

	@Override
	public void exchangeKnowledge() {
		bImpl.exchangeKnowledge();
	}
}
