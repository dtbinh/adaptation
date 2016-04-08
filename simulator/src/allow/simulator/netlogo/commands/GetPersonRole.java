package allow.simulator.netlogo.commands;

import org.nlogo.agent.Agent;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

import allow.simulator.entity.Person;
import allow.simulator.netlogo.agent.PersonAgent;

public class GetPersonRole extends DefaultReporter
{
	@Override
	public Object report(Argument[] args, Context context) throws ExtensionException, LogoException {
		Agent a = (Agent) context.getAgent();
		
		if (!(a instanceof PersonAgent))
			throw new ExtensionException("Error: Calling agent must be of type person.");
		
		PersonAgent temp = (PersonAgent) a;
		Person p = (Person) temp.getEntity();
		return p.getProfile().toString();
	}
	
	public Syntax getSyntax() {
		return Syntax.reporterSyntax(Syntax.StringType());
	}
}
