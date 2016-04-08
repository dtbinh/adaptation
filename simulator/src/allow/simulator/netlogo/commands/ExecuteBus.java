package allow.simulator.netlogo.commands;

import org.nlogo.agent.Agent;
import org.nlogo.api.AgentException;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;

import allow.simulator.netlogo.agent.BusAgent;

/**
 * NetLogo command to execute one step of a bus.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class ExecuteBus extends DefaultCommand {

	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException, LogoException {
		Agent a = (Agent) context.getAgent();
		
		if (a instanceof BusAgent) {
			BusAgent p = (BusAgent) a;
			
			try {
				p.execute();
			} catch (AgentException e) {
				throw new ExtensionException(e);
			}
			
		} else {
			throw new ExtensionException("Error: Calling agent must be of breed Bus");
		}
		
	}

}
