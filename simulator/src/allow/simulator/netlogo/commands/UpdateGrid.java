package allow.simulator.netlogo.commands;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;

import allow.simulator.core.Simulator;
import allow.simulator.world.NetLogoWorld;

public class UpdateGrid extends DefaultCommand {

	@Override
	public void perform(Argument[] arg0, Context arg1) throws ExtensionException, LogoException {
		NetLogoWorld world = (NetLogoWorld) Simulator.Instance().getWorld();
		world.updateGrid();
	}

}
