package allow.simulator.netlogo.commands;

import org.nlogo.agent.AgentSet;
import org.nlogo.agent.AgentSet.Iterator;
import org.nlogo.agent.World;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

import allow.simulator.entity.Person;
import allow.simulator.netlogo.agent.PersonAgent;
import allow.simulator.util.Geometry;

public class ShowNotAtHome extends DefaultCommand {
	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException, LogoException {
		// The number of agents to create.
		World w = (World) context.getAgent().world();
		AgentSet s = w.getBreed("PERSONS");
		
		for (Iterator it = s.iterator(); it.hasNext(); ) {
			PersonAgent p = (PersonAgent) it.next();
			Person pers = (Person) p.getEntity();
			
			if (!pers.getPosition().equals(pers.getHome()))
				System.out.println(pers + " " + pers.getPosition() + " " + pers.getHome() + " " + Geometry.haversine(pers.getPosition(), pers.getHome()));
		}
		
	}
	
	public Syntax getSyntax() {
		int right[] = new int[] { Syntax.AgentsetType() };
		return Syntax.commandSyntax(right);
	}
}
