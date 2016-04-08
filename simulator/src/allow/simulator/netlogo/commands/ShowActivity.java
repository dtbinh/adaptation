package allow.simulator.netlogo.commands;

import org.nlogo.agent.Agent;
import org.nlogo.agent.AgentSet;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

import allow.simulator.netlogo.agent.PersonAgent;

public class ShowActivity extends DefaultCommand {
	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException, LogoException {
		// The number of agents to create.
		AgentSet set = (AgentSet) args[0].getAgentSet();
		
		for (AgentSet.Iterator it = set.iterator(); it.hasNext(); ) {
			
			Agent a = it.next();
			
			if (a instanceof PersonAgent) {
				PersonAgent p = (PersonAgent) a;
				String c = (p.getEntity().getFlow().getCurrentActivity() != null) ? p.getEntity().getFlow().getCurrentActivity().toString() : null;
				System.out.println(c);
			}
		}
	}
	
	public Syntax getSyntax() {
		int right[] = new int[] { Syntax.AgentsetType() };
		return Syntax.commandSyntax(right);
	}
}
