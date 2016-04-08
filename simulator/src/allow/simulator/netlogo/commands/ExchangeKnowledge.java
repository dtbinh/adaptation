package allow.simulator.netlogo.commands;

import org.nlogo.agent.Agent;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;

import allow.simulator.flow.activity.Activity;
import allow.simulator.flow.activity.Activity.Type;
import allow.simulator.netlogo.agent.IAgent;

/**
 * NetLogo command to initiate knowledge exchange.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class ExchangeKnowledge extends DefaultCommand {

	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException, LogoException {
		Agent a = (Agent) context.getAgent();
		
		if (a instanceof IAgent) {
			IAgent p = (IAgent) a;
			
			if (!p.getEntity().getFlow().isIdle()) {
				Activity.Type type = p.getEntity().getFlow().getCurrentActivity().getType();
				
				if (type == Type.DRIVE || type == Type.CYCLE || type == Type.WALK || type == Type.USE_PUBLIC_TRANSPORT) {
					p.exchangeKnowledge();
				}
			}
			
		} else {
			throw new ExtensionException("Error: Calling agent must be a valid Allow entity.");
		}
		
	}

}