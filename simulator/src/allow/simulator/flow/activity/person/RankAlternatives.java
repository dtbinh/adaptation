package allow.simulator.flow.activity.person;

import java.util.List;

import allow.simulator.entity.Person;
import allow.simulator.entity.knowledge.EvoKnowledge;
import allow.simulator.entity.utility.Preferences;
import allow.simulator.flow.activity.Activity;
import allow.simulator.mobility.planner.Itinerary;

public class RankAlternatives extends Activity {

	private List<Itinerary> toRank;
		
	public RankAlternatives(Person entity, List<Itinerary> it) {
		super(Activity.Type.RANK_ALTERNATIVES, entity);
		toRank = it;
	}

	@Override
	public double execute(double deltaT) {

		if (tStart == -1) {
			tStart = 1;
			updatePreferences();
			updateItineraryParameters();
			return deltaT;
		}
		toRank = entity.getUtility().rankAlternatives(entity.getPreferences(), toRank);
		entity.getFlow().addActivity(new PrepareJourney((Person) entity, toRank.get(0)));
		setFinished();
		return 0.0;
	}
	
	private void updateItineraryParameters() {
		// Correct journey parameters before calling utility function.
		EvoKnowledge evo = entity.getKnowledge();
		evo.predict(toRank);
	}
	
	private void updatePreferences() {
		long minTTime = Long.MAX_VALUE;
		double minCosts = Double.MAX_VALUE;
		double minWalking = Double.MAX_VALUE;
		
		for (Itinerary it : toRank) {
			// if (it.itineraryType == 2 || it.itineraryType == 3) continue;
			
			if (it.duration < minTTime) minTTime = it.duration;
			if (it.costs > 0 && it.costs < minCosts) minCosts = it.costs;
			if (it.walkDistance > 0 && it.walkDistance < minWalking) minWalking = it.walkDistance;
		}
		Preferences prefs = entity.getPreferences();
		prefs.setTmax((long) (minTTime * 1.2));
		prefs.setCmax(minCosts * 1.2);
		prefs.setWmax(minWalking * 1.2);
		
	}
	
	public String toString() {
		return "RankAlternatives " + entity;
	}
}
