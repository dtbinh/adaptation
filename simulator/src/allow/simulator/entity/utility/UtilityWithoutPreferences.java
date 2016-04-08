package allow.simulator.entity.utility;

import java.util.Comparator;
import java.util.List;

import allow.simulator.mobility.planner.Itinerary;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 
 * @author UOC
 *
 */
public class UtilityWithoutPreferences implements IUtility {

	private static Comparator<Itinerary> ascUtility = new Comparator<Itinerary>() {
        @Override
        public int compare(Itinerary rt1, Itinerary rt2) {
        	double diff = rt1.utility - rt2.utility;
        	
        	if (diff < 0) {
        		return -1;
        		
        	} else if (diff == 0) {
        		return 0;
        		
        	} else {
        		return 1;
        	}
        }
	};

	@JsonCreator
	public UtilityWithoutPreferences() {
		
	}
	
	@Override
	public List<Itinerary> rankAlternatives(Preferences prefs, List<Itinerary> itineraries) {
		
		for (Itinerary it : itineraries) {
			it.utility = computeUtility(it.duration + it.initialWaitingTime, it.costs, it.walkDistance, it.maxFillingLevel, it.transfers, prefs);
			double pref = 0.5;
			
			if (it.itineraryType == 0) {
				pref = prefs.getCarPreference();
				
			} else if (it.itineraryType == 1) {
				pref = prefs.getBusPreference();	
			}
			pref = 1 - (pref - 0.5);
			it.utility = it.utility * pref;
		}
		itineraries.sort(ascUtility);
		//System.out.println(itineraries);
		return itineraries;
	}
	
	private static final double TFACTOR = 1500;
	private static final double CFACTOR = 2.5;
	private static final double FFACTOR = 1.0;
	
	public double computeUtility(double travelTime, double costs, double walkingDistance, 
			double fLevel, int transfers, Preferences prefs) {
		return travelTime / TFACTOR + costs / CFACTOR + ((fLevel < 0.7) ? 0.0 : fLevel * FFACTOR);
	}
}
