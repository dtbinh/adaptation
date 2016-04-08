package allow.simulator.netlogo.agent;

import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.api.AgentException;

import allow.simulator.entity.Entity;
import allow.simulator.entity.TransportAgency;

/**
 * Wrapper for 
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class TransportAgencyAgent extends Turtle implements IAgent {
	// Implementation of an agency.
	private TransportAgency aImpl;
	
	public TransportAgencyAgent(World world, TransportAgency a) {
		super(world, world.getBreed("TRANSPORTAGENCIES"), 0.0, 0.0);
		hidden(true);
		aImpl = a;
	}
	
	@Override
	public boolean execute() throws AgentException {
		
		if (aImpl.getFlow().getCurrentActivity() == null) {
			// If there is no current activity return finished signal.
			return true;
		}
		// Otherwise execute current activity and return not finished.
		aImpl.getFlow().executeActivity(aImpl.getContext().getTime().getDeltaT());
		return false;
	}

	@Override
	public Entity getEntity() {
		return aImpl;
	}

	@Override
	public void exchangeKnowledge() {
		aImpl.exchangeKnowledge();
	}

}
