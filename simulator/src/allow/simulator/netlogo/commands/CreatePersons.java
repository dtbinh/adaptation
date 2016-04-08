package allow.simulator.netlogo.commands;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

import allow.simulator.core.Simulator;
import allow.simulator.entity.Entity;

public class CreatePersons extends DefaultCommand {

	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException, LogoException {
		// The number of agents to create.
		int numberOfAgents = args[0].getIntValue();
		
		for (int i = 0; i < numberOfAgents; i++) {
			Simulator.Instance().addEntity(Entity.Type.PERSON);
		}
	}
	
	public Syntax getSyntax() {
		int right[] = new int[] { Syntax.NumberType() };
		return Syntax.reporterSyntax(right, Syntax.TurtlesetType());
	}
}
