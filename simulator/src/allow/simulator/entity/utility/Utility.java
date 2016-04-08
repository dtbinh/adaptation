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
public class Utility implements IUtility {

	private static Comparator<Itinerary> descUtility = new Comparator<Itinerary>() {
        @Override
        public int compare(Itinerary rt1, Itinerary rt2){
            return (int) ((rt2.utility - rt1.utility) * 100);
        }
	};

	@JsonCreator
	public Utility() {
		
	}
	
	@Override
	public List<Itinerary> rankAlternatives(Preferences prefs, List<Itinerary> itineraries) {
		
		for (Itinerary it : itineraries) {
			it.utility = computeUtility((it.duration + it.waitingTime), it.costs, it.walkDistance, it.maxFillingLevel, it.transfers, prefs);
		
			double pref = 0.5;
			
			if (it.itineraryType == 0) {
				pref = prefs.getCarPreference();
				
			} else if (it.itineraryType == 1) {
				pref = prefs.getBusPreference();	
			}	
			pref = 1 + (pref - 0.5);
			it.utility = it.utility * pref;
		}
		itineraries.sort(descUtility);
		return itineraries;
	}
	
	public static double computeUtility2(Preferences prefs, 
			double travelTime,
			double costs,
			int itineraryType) {
		long diff = (long) (prefs.getTmax() - travelTime);
		double u = prefs.getTTweight() * (Math.exp(diff - Math.abs(diff)));
		u += prefs.getCweight() * (Math.exp(-costs / prefs.getCmax()));
		double pref = 0.0;
		
		if (itineraryType == 0) {
			pref = prefs.getCarPreference();
			
		} else if (itineraryType == 1) {
			pref = prefs.getBusPreference();
			
		} else {
			pref = 0;
		}
		pref = 1 + (pref - 0.5);
		u = u * pref;
		return Math.max(u, 0);
	}
	
	public double computeUtility(double travelTime, double costs, double walkDistance,
			double busFillingLevel, int transfers, Preferences prefs) {
		long diff = (long) (prefs.getTmax() - travelTime);
		double u = prefs.getTTweight() * (Math.exp(diff - Math.abs(diff)));
		u += prefs.getCweight() * (Math.exp(-costs / prefs.getCmax()));
		u += prefs.getWDweight() * (-walkDistance / prefs.getWmax() + 1);
				
		if (transfers == 1)
			u += prefs.getNCweight();
		else if (transfers == 2)
			u += 0.8 * prefs.getNCweight();
		else if (transfers == 3)
			u += 0.6 * prefs.getNCweight();
		else if (transfers == 4)
			u += 0.4 * prefs.getNCweight();
		else
			u += 0.2 * prefs.getNCweight();
		return Math.max(u, 0);
	}
}
