package allow.simulator.netlogo.commands;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

import allow.simulator.core.Simulator;

public class ShutDownSimulator extends DefaultCommand {
		
	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException, LogoException {
		Simulator.Instance().finish();
	}

	@Override
	public Syntax getSyntax() {
		return Syntax.commandSyntax();
	}
}
