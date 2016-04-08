package allow.simulator.entity.utility;

import java.util.List;

import allow.simulator.mobility.planner.Itinerary;

public interface IUtility {

	List<Itinerary> rankAlternatives(Preferences prefs, List<Itinerary> itineraries);

	double computeUtility(double traveltime, double costs, double walkingDistance,
			double busFillingLevel, int transfers, Preferences prefs);
}
