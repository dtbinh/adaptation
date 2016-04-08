package allow.simulator.netlogo.commands;

import java.util.Collection;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

import allow.simulator.core.Simulator;
import allow.simulator.world.StreetSegment;

public class ShowSegments extends DefaultCommand {
	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException, LogoException {
		Collection<StreetSegment> s = Simulator.Instance().getContext().getWorld().getStreetMap().getStreetSegments();
	
		for (StreetSegment seg : s) {
			if (seg.getNumberOfVehicles() > 0) System.out.println(seg.getId() + " " + seg.getNumberOfVehicles());
		}
	}
	
	public Syntax getSyntax() {
		return Syntax.commandSyntax();
	}
}
