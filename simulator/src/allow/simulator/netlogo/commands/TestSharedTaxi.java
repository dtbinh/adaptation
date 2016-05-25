package allow.simulator.netlogo.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nlogo.agent.Agent;
import org.nlogo.api.AgentSet;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.Syntax;

import allow.simulator.entity.Person;
import allow.simulator.entity.TravelEvent;
import allow.simulator.mobility.data.TType;
import allow.simulator.mobility.planner.IPlannerService;
import allow.simulator.mobility.planner.Itinerary;
import allow.simulator.mobility.planner.JourneyRequest;
import allow.simulator.mobility.planner.RequestId;
import allow.simulator.netlogo.agent.PersonAgent;
import allow.simulator.util.Coordinate;

public class TestSharedTaxi extends DefaultCommand {

	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException, LogoException {
		Agent a = (Agent) context.getAgent();
		
		if (a instanceof PersonAgent) {
			PersonAgent p = (PersonAgent) a;
			Person person = (Person) p.getEntity();
			List<Coordinate> to = new ArrayList<Coordinate>();

			List<TravelEvent> t = person.getDailyRoutine().getDailyRoutine(1);
			to.add(t.get(0).getDestination());
			AgentSet otherAgents = (AgentSet) args[0].getAgentSet();

			for (Iterator<org.nlogo.api.Agent> it = otherAgents.agents().iterator(); it.hasNext(); ) {
				org.nlogo.api.Agent temp = it.next();
				
				if (!(temp instanceof PersonAgent))
					continue;
				
				Person other = (Person) ((PersonAgent) temp).getEntity();
				List<TravelEvent> t2 = other.getDailyRoutine().getDailyRoutine(1);
				to.add(t2.get(0).getDestination());
			}
			IPlannerService taxiPlanner = person.getContext().getTaxiPlannerService();
			JourneyRequest req = JourneyRequest.createRequest(t.get(0).getStartingPoint(), to, person.getContext().getTime().getCurrentDateTime(),
					false, new TType[] { TType.SHARED_TAXI }, person, new RequestId());
			long t1 = System.currentTimeMillis();
			List<Itinerary> temp = new ArrayList<Itinerary>();
			taxiPlanner.requestSingleJourney(req, temp);
			long t2 = System.currentTimeMillis();
			System.out.println("Took " + (double) ((t2 - t1) / 1000.0) + " s");
			
		} else {
			throw new ExtensionException("Error: Calling agent must be of breed Person");
		}
		
	}
	
	@Override
	public Syntax getSyntax() {
		return Syntax.commandSyntax(new int[] { Syntax.AgentsetType() });
	}
}
