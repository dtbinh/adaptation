package allow.simulator.netlogo.agent;

import java.util.EnumMap;
import java.util.Observable;
import java.util.Observer;

import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.api.AgentException;

import allow.simulator.entity.Entity;
import allow.simulator.entity.PublicTransportation;
import allow.simulator.entity.Taxi;
import allow.simulator.flow.activity.Activity;
import allow.simulator.flow.activity.Activity.Type;
import allow.simulator.util.Coordinate;

/**
 * Wrapper class to add taxi state information to corresponding NetLogo agents.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class TaxiAgent extends Turtle implements Observer, IAgent {
	// Actual taxi agent
	private Taxi tImpl;
	
	// Buffer for coordinate transformation
	private Coordinate temp;
	
	// Shape lookup
	private static final EnumMap<Activity.Type, String> shapes;
	
	static {
		shapes = new EnumMap<Activity.Type, String>(Activity.Type.class);
		shapes.put(Type.PREPARE_TAXI_TRIP, "car side");
		shapes.put(Type.PICK_UP_OR_DROP, "car side");
		shapes.put(Type.DRIVE_TO_NEXT_DESTINATION, "car side");
		shapes.put(Type.RETURN_TO_TAXI_AGENCY, "car side");
		shapes.put(Type.LEARN, "car side");
	}
	
	public TaxiAgent(World world, Taxi t) throws AgentException {
		super(world, world.getBreed("TAXIS"), 0.0, 0.0);
		temp = new Coordinate();
		tImpl = t;
		tImpl.addObserver(this);
		hidden(true);
		shape("car side");
		colorDouble(45.0);
		size(1.0);
		
		Coordinate netlogo = tImpl.getContext().getWorld().getTransformation().GISToNetLogo(tImpl.getPosition());
				
		if ((netlogo.x > world().minPxcor()) && (netlogo.x < world().maxPxcor()) && (netlogo.y > world().minPycor() && (netlogo.y < world().maxPycor()))) {
			xandycor(netlogo.x, netlogo.y);
		}
	}
	
	@Override
	public boolean execute() throws AgentException {
		
		if (tImpl.getFlow().isIdle()) {
			hidden(true);
			return false;
		}
		Activity.Type executedActivity = tImpl.getFlow().getCurrentActivity().getType();
		String s = shapes.get(executedActivity);
		tImpl.getFlow().executeActivity(tImpl.getContext().getTime().getDeltaT());
		
		// Update shape if necessary.
		if (!s.equals(shape())) shape(s);
		if (hidden()) hidden(false);
		return true;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		PublicTransportation b = (PublicTransportation) o;
		
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
		return tImpl;
	}

	@Override
	public void exchangeKnowledge() {
		tImpl.exchangeKnowledge();
	}
}

