package allow.simulator.netlogo.agent;

import org.nlogo.api.AgentException;

import allow.simulator.entity.Entity;

/**
 * Interface connecting simulation entities and NetLogo agents.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public interface IAgent {
	
	/**
	 * Returns the underlying entity.
	 * 
	 * @return Implementation of the entity.
	 */
	public Entity getEntity();
	
	/**
	 * Calls the behavioural logic of the underlying entity.
	 * 
	 * @return True, if entity has actually performed an action (e.g. executed
	 * 		   an activity), false otherwise.
	 * @throws AgentException
	 */
	public boolean execute() throws AgentException;
	
	/**
	 * Initiates knowledge exchange.
	 */
	public void exchangeKnowledge();
}
