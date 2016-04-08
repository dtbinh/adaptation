package allow.simulator.netlogo.commands;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.api.Syntax;

import allow.simulator.core.Simulator;
import allow.simulator.statistics.Statistics;

public class Tick extends DefaultReporter {

	@Override
	public Object report(Argument[] args, Context context) throws ExtensionException, LogoException {
		// Get deltaT.
		int deltaT = args[0].getIntValue();
		
		// Smart Planner execution.
		Simulator.Instance().getContext().getWorld().getUrbanMobilitySystem().getFlow().executeActivity(Simulator.Instance().getContext().getTime().getDeltaT());
		
		// Update simulator.
		Simulator.Instance().tick(deltaT);
		
		// Return context and statistics.
		LogoListBuilder listBuilder = new LogoListBuilder();
		allow.simulator.core.Context ctx = Simulator.Instance().getContext();
		ctx.getStatistics().updateGlobalStatistics(ctx);
		
		listBuilder.add(ctx.getTime().toString());
		listBuilder.add(ctx.getWeather().getCurrentState().getDescription());
		
		Statistics s = ctx.getStatistics();
		listBuilder.add(s.getMeanPriorCarTravelTime());
		listBuilder.add(s.getMeanPosteriorCarTravelTime());
		listBuilder.add(s.getMeanPriorBusTravelTime());
		listBuilder.add(s.getMeanPosteriorBusTravelTime());
		listBuilder.add(s.getMeanBusPreference());
		listBuilder.add(s.getMeanCarPreference());
		listBuilder.add(s.getCarJourneyRatio());
		listBuilder.add(s.getTransitJourneyRatio());
		listBuilder.add(s.getBikeJourneyRatio());
		listBuilder.add(s.getWalkJourneyRatio());
		listBuilder.add(s.getMeanBusFillingLevel());
		listBuilder.add(s.getMeanPriorUtilityCar());
		listBuilder.add(s.getMeanPosteriorUtilityCar());
		listBuilder.add(s.getMeanPriorUtilityBus());
		listBuilder.add(s.getMeanPosteriorUtilityBus());
		listBuilder.add(s.getTaxiJourneyRatio());
		listBuilder.add(s.getMeanReplaningWaitingTime());
		listBuilder.add((double) s.getNumberOfCongestedStreets());
		ctx.getStatistics().resetCongestedStreets();

		return listBuilder.toLogoList();
	}

	public Syntax getSyntax() {
		int right[] = { Syntax.NumberType() };
		return Syntax.reporterSyntax(right, Syntax.ListType());
	}
}
