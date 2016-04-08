package allow.simulator.netlogo.commands;

import org.nlogo.agent.Agent;
import org.nlogo.api.AgentException;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

import allow.simulator.netlogo.agent.PersonAgent;

/**
 * NetLogo command to execute one step of a person.
 * 
 * @author Andreas Poxrucker (DFKI)
 *
 */
public class ExecutePerson extends DefaultReporter {
	
	@Override
	public Object report(Argument[] args, Context context) throws ExtensionException, LogoException {
		Agent a = (Agent) context.getAgent();
		
		if (a instanceof PersonAgent) {
			PersonAgent p = (PersonAgent) a;
			
			try {
				return p.execute();
			} catch (AgentException e) {
				throw new ExtensionException(e);
			}
		} else {
			throw new ExtensionException("Error: Calling agent must be of breed Person");
		}	
	}
	
	public Syntax getSyntax() {
		return Syntax.reporterSyntax(Syntax.BooleanType());
	}
}
