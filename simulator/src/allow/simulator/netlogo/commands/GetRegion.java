package allow.simulator.netlogo.commands;

import java.util.List;

import org.nlogo.agent.Agent;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.api.Syntax;

import allow.simulator.netlogo.agent.BusAgent;
import allow.simulator.netlogo.agent.PersonAgent;
import allow.simulator.world.layer.Area;
import allow.simulator.world.layer.DistrictArea;
import allow.simulator.world.layer.Layer;
import allow.simulator.world.layer.Layer.Type;

public class GetRegion extends DefaultReporter
{
	@Override
	public Object report(Argument[] args, Context context) throws ExtensionException, LogoException {
		Agent a = (Agent) context.getAgent();
		
		if (a instanceof PersonAgent) {
			PersonAgent temp = (PersonAgent) a;
			Layer l = temp.getEntity().getContext().getWorld().getStreetMap().getLayer(Type.DISTRICTS);
			List<Area> areas = l.getAreasContainingPoint(temp.getEntity().getPosition());
			
			LogoListBuilder bldr = new LogoListBuilder();
			boolean added = false;
			
			for (Area area : areas) {
				if (area.getName().equals("default")) {
					continue;
				}
				DistrictArea t = (DistrictArea) area;
				bldr.add(t.getType().toString());
				added = true;
				break;
			}
			
			if (!added) {
				bldr.add("UNKNOWN");
			}
			return bldr.toLogoList();
			
		} else if (a instanceof BusAgent) {
			BusAgent temp = (BusAgent) a;
			Layer l = temp.getEntity().getContext().getWorld().getStreetMap().getLayer(Type.DISTRICTS);
			List<Area> areas = l.getAreasContainingPoint(temp.getEntity().getPosition());
			
			LogoListBuilder bldr = new LogoListBuilder();
			
			for (Area area : areas) {
				
				if (area.getName().equals("default")) {
					continue;
				}
				DistrictArea t = (DistrictArea) area;
				bldr.add(t.getType().toString());
			}
			return bldr.toLogoList();
			
		} else {
			throw new ExtensionException("Error: Calling agent must be of type person or bus.");
		}
	}
	
	public Syntax getSyntax() {
		return Syntax.reporterSyntax(Syntax.StringType());
	}
}
