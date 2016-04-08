package allow.simulator.netlogo.commands;

import org.nlogo.agent.Agent;
import org.nlogo.api.AgentException;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;

import allow.simulator.netlogo.agent.TransportAgencyAgent;

/**
 * NetLogo command to execute transport agency agents.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class ExecuteTransportAgencies extends DefaultCommand {

	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException, LogoException {
		Agent a = (Agent) context.getAgent();
		
		if (a instanceof TransportAgencyAgent) {
			TransportAgencyAgent t = (TransportAgencyAgent) a;
			
			try {
				t.execute();
				
			} catch (AgentException e) {
				e.printStackTrace();
			}
			
		} else {
			throw new ExtensionException("Error: Calling agent must be of breed TransportAgency.");
		}
		
	}

}
